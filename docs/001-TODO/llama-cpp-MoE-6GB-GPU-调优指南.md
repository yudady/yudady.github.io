---
title: 在 6GB 显卡上跑 22GB AI 模型 - llama.cpp MoE 调优指南
aliases: [llama.cpp MoE offloading, n-cpu-moe 调优, Qwen3.6-35B 6GB GPU]
tags:
  - llama-cpp
  - local-llm
  - moe
  - gpu-optimization
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=7AExwNFlXU4"
author: Cloud Codes
created: 2026-08-01
updated: 2026-08-01
description: 用 llama.cpp 的 5 个关键参数，在 GTX 1060 6GB 上运行 Qwen3.6-35B-A3B MoE 模型，实现 3→17 tokens/s 的调优全流程
level: intermediate
stars: 5
---

# 在 6GB 显卡上跑 22GB AI 模型 - llama.cpp MoE 调优指南

> 22GB 的模型塞进 6GB 显卡，还能 17 tokens/s 流畅生成。核心不是魔法，是 MoE（Mixture of Experts）架构的稀疏性 + llama.cpp 的细粒度权重分工。这篇笔记拆解从 3 t/s 到 17 t/s 的 5 个关键 flag，以及 3 个调优会撞墙的瓶颈。

---

## 目录

1. [MoE 架构原理：为什么 22GB 模型能塞进 6GB](#一moe-架构原理为什么-22gb-模型能塞进-6gb)
2. [5 个关键调优参数（3→17 t/s）](#二5-个关键调优参数317-ts)
3. [Prompt 处理与 KV Cache 优化](#三prompt-处理与-kv-cache-优化)
4. [技术陷阱与极限瓶颈](#四技术陷阱与极限瓶颈)
5. [4-bit 量化质量评估](#五4-bit-量化质量评估)
6. [总结与落地建议](#六总结与落地建议)

---

## 一、MoE 架构原理：为什么 22GB 模型能塞进 6GB

### 稀疏激活：35B 参数，每个 token 只用 3B

Qwen3.6-35B-A3B（阿里巴巴，2026-04-15 发布，Apache 2.0）总参数 350 亿，但生成每个 token 时仅有 **30 亿参数（3B）** 被实际激活。

模型内部有 **256 个路由专家（Routed Experts）**，每个 token 由 Router 唤醒 **8 个专家 + 1 个共享专家**，其余 248 个保持静止。

这意味着 MoE 把问题从「模型塞不塞得进显存」变成了「哪些权重需要快速访问」。

```
┌─────────────────────────────────────────────────────────┐
│              Dense vs MoE 记忆体流量对比                  │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Dense 35B（密集型）                                     │
│  每 token 读取全部 35B 权重 → ~20 GB 记忆体流量         │
│  ████████████████████████████████ 20 GB/token           │
│                                                         │
│  MoE 35B-A3B（稀疏型）                                  │
│  每 token 仅读取 3B 激活权重 → <1 GB 记忆体流量         │
│  █ <1 GB/token                                          │
│                                                         │
│  同等知识量，记忆体移动量降至 1/20                       │
└─────────────────────────────────────────────────────────┘
```

### 记忆体层级分工

关键洞察：不是所有权重都需要同等速度。

| 权重类型 | 每 token 是否触发 | 应放在哪里 | 原因 |
|----------|-------------------|------------|------|
| Attention（注意力） | ✅ 每个 token | GPU VRAM | 高频访问，需高带宽 |
| Embedding（嵌入层） | ✅ 每个 token | GPU VRAM | 高频访问 |
| Shared Expert（共享专家） | ✅ 每个 token | GPU VRAM | 恒定激活 |
| KV Cache | ✅ 每个 token | GPU VRAM | 随上下文增长 |
| Routed Experts（路由专家） | ❌ 大多数闲置 | 系统 RAM | 248/256 闲置，可慢速访问 |

核心思路：把高频权重放 GPU，低频权重放 RAM，用 PCI Express 按需搬运。

> 在 2025 年 7 月前，要实现专家权重卸载只能用手写正则表达式：`-ot "\.ffn_.*_exps=CPU"`，从论坛帖子复制并祈祷粘贴正确。现在有了专用 flag。

---

## 二、5 个关键调优参数（3→17 t/s）

基准平台：GTX 1060 6GB + 32GB DDR4 RAM。

| Flag | 作用 | 速度影响 | 难度 |
|------|------|----------|------|
| `--n-cpu-moe N` | N 层专家权重留在 CPU | 3→10 t/s | ⭐⭐ |
| `--load-mode` | mmap/mlock 模式选择 | 10→13.5 t/s | ⭐⭐⭐ |
| `-ngl N` | 图层回推 GPU | 13.5→17 t/s | ⭐⭐ |
| `--mlock` | 锁定记忆体页面 | 稳定性提升 | ⭐ |
| microbatch 调优 | Prompt 处理加速 | 22→345 t/s | ⭐⭐⭐ |

### Flag 1：`--n-cpu-moe`（核心参数）

```
# 将所有专家层放 CPU（最保守起点）
llama-server -m model.gguf --cpu-moe

# 将前 N 层专家放 CPU，逐步调降
llama-server -m model.gguf --n-cpu-moe 30
```

**原理**：将指定层数的 MoE 专家权重强制保留在系统 RAM 中，腾出 VRAM 给高频权重。

**合并历史**：
- 2025-07-31：`--cpu-moe` 合并进主线（PR 仅审核 80 分钟）
- 2025-08-04：`--n-cpu-moe`（可指定层数版本）跟进

**调优方法（渐进测试法）**：

```
┌──────────────────────────────────────────┐
│         N 值调优流程                      │
├──────────────────────────────────────────┤
│                                          │
│  --n-cpu-moe ALL  ← 起点最保守           │
│       ↓                                  │
│  --n-cpu-moe 28   ← 逐步降低             │
│       ↓                                  │
│  --n-cpu-moe 26                           │
│       ↓                                  │
│  --n-cpu-moe 24                           │
│       ↓                                  │
│  ❌ VRAM 不足！                           │
│       ↑ 退回一步                          │
│  ✅ --n-cpu-moe 24 = 正确值              │
│     （最后一个能装下的值）                 │
└──────────────────────────────────────────┘
```

**工具限制**：

| 工具 | 是否支持 `--n-cpu-moe` | 状态 |
|------|------------------------|------|
| llama.cpp CLI | ✅ 完整支持 | 2025-08 起可用 |
| Ollama | ❌ 不支持 | Feature request 自 2025-08 仍 open；自动分配（实测 17% CPU / 83% GPU，无法干预） |
| LM Studio | ⚠️ 仅支持 all/none | 有 checkbox 但无法指定具体层数，ticket 仍 open |

> 视频明确建议：使用 llama.cpp 原生 CLI，暂勿用 Ollama / LM Studio 的 GUI。

### Flag 2：`--load-mode`（记忆体映射模式）

llama.cpp 在 2025 年 3 月将旧参数（`--no-mmap`、`--mmap`、`--mlock`）整合为统一的 `--load-mode`：

| 模式 | 行为 | 适用场景 |
|------|------|----------|
| `none` | 不使用 mmap，全量载入 RAM | 模型 < 系统 RAM 时 |
| `mmap` | 记忆体映射，由 kernel 决定驻留 | 默认值；模型 > 系统 RAM 时更优 |
| `mlock` | 锁定页面，防止 swap | 长时间运行的服务 |
| `deo` | — | 特定优化路径 |

**关键陷阱**：如果模型大于系统 RAM（MoE 模型常达系统记忆体的 100%-150%），保留 `mmap` 反而更快——因为 OS Page Cache 在做实际工作。强制全量载入会导致频繁 swap，性能下降。

> 很多在线教程仍在教 `--no-mmap`，但该参数已打印 deprecation warning，现在统一用 `--load-mode`。

### Flag 3：`-ngl N`（GPU 图层回推）

```bash
# 专家移到 CPU 后，VRAM 空出来了，把 Transformer 常规图层推回 GPU
llama-server -m model.gguf --n-cpu-moe 24 -ngl 99
```

**原理**：专家权重移出后，6GB VRAM 大部分闲置。此时用 `-ngl`（`--n-gpu-layers`）将常态 Transformer 图层回推 GPU 处理，进一步加速解码。

调到 VRAM 填满为止。本案例中这一步让速度从 13.5 → 17 t/s。

### Flag 4：`--mlock`（锁定记忆体页面）

仅在长期运行 server 时重要。Linux kernel 会在闲置数小时后将专家权重 page out 到 swap，导致「午餐后第一个 token 卡 4 秒」。`--mlock` 阻止此行为。

### 完整启动命令示例

```bash
llama-server \
  -m qwen3.6-35b-a3b-Q4_K_M.gguf \
  --n-cpu-moe 24 \
  --load-mode mlock \
  -ngl 99 \
  -c 8192 \
  -fa \
  -tb 2048 \
  -t 8
```

> `-tb` = microbatch size（见下一节），`-fa` = flash attention，`-t 8` = CPU 线程数。

---

## 三、Prompt 处理与 KV Cache 优化

### Prompt Processing（Prefill）是另一套机制

生成和读取 prompt 是两个完全不同的工作，llama.cpp 内部用不同路径处理。

CUDA 后端有一个常量 `op_offload_min_batch_size`（默认 32），它决定矩阵乘法由 CPU 还是 GPU 执行：

```
┌─────────────────────────────────────────────────────────────┐
│          生成 vs Prompt 处理的分水岭                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  生成阶段（Generation / Decode）                             │
│  Batch = 1（逐 token 生成）                                  │
│  1 < 32 → CPU 执行，从 RAM @ 51 GB/s 读取                   │
│  → 这就是你的 17 t/s                                        │
│                                                             │
│  Prompt 处理阶段（Prefill）                                  │
│  Batch = 数百 tokens 同时处理                                │
│  数百 > 32 → llama.cpp 把专家权重经 PCIe 复制到 VRAM        │
│  → 交给 GPU 计算 @ 192 GB/s                                 │
│  → 同模型、同 flag，底层完全是另一台机器                     │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**优化手段**：提高 microbatch 尺寸。默认 512 tokens，提升到 2048 后效果惊人：

| Microbatch | Prompt 处理速度 |
|------------|----------------|
| 128 | 22 t/s |
| 512（默认） | ~100 t/s |
| 2048 | **345 t/s**（15x 提升） |

2025 年 8 月的一个 patch 进一步优化：标题就叫「Copy only the used experts when offloading prompt processing」——之前所有 256 个专家都会被复制到 VRAM，现在只复制实际被激活的。

### 混合注意力架构大幅节省 KV Cache

Qwen3.6-35B-A3B 的 40 层结构：

| 层类型 | 层数 | 注意力机制 | KV Cache 行为 |
|--------|------|------------|---------------|
| Full Attention | 10 层（25%） | 传统注意力 | Cache 随 token 增长 |
| Gated DeltaNet（线性注意力） | 30 层（75%） | 固定大小状态 | **无增长**，固定 state |

```
KV Cache 记忆体需求对比（每 token 每序列）：

  Qwen3.6-35B-A3B（混合注意力）
  20,480 bytes/token → 262K 上下文 ≈ 5 GB

  传统全注意力 MoE（同级别）
  ~98,000 bytes/token → 262K 上下文 ≈ 24 GB

  差距：4.8x
  「最便宜的 context，是架构本身从不分配的那些。」
```

### TurboQuant：3-bit KV Cache 压缩（实验性）

TurboQuant（Google Research + NYU，arXiv 2025-04，ICLR 2026）通过旋转变换 + 单一 codebook 量化实现 3-bit KV Cache：

- 效果：KV 记忆体减少 6 倍，benchmark 分数不变
- 实测：6GB 卡上下文从 64K → 256K tokens

**但目前在 llama.cpp 中不可用**：

| 状态 | 详情 |
|------|------|
| 主线合并 | ❌ 所有 PR 均 closed/unmerged（共 75 个相关 PR/issue） |
| Fork 需求 | 需构建落后主线 300+ commits 的 fork |
| AI 政策标记 | 其中 2 个 PR 被标记「AI policy violation」 |
| 速度倒退 | 主线重写 MoE attention kernel 后，plain 16-bit 快了 4x，TurboQuant 路径未跟进；Turbo 2 解码速度仅剩 F16 的 45% |

> 结论：TurboQuant 是「省显存但牺牲速度」的取舍，当前阶段不建议追。

---

## 四、技术陷阱与极限瓶颈

### 瓶颈 1：记忆体频宽是最终天花板

```
┌──────────────────────────────────────────────┐
│        记忆体频宽对比（决定速度上限）         │
├──────────────────────────────────────────────┤
│                                              │
│  GPU VRAM（GTX 1060）  192 GB/s  ████████    │
│  系统 RAM（DDR4-3200）   51 GB/s  ██         │
│                                              │
│  专家权重一旦移到 RAM，速度由 RAM 频宽决定    │
│  51/192 ≈ 1/4，这正是速度差距的来源          │
│                                              │
│  → 这个瓶颈没有任何 flag 能突破              │
└──────────────────────────────────────────────┘
```

不同硬件的速度上限参考：

| 硬件 | VRAM | 速度 | 备注 |
|------|------|------|------|
| GTX 1060 | 6GB | 17 t/s | 专家在 RAM |
| GTX 1070 | 8GB | ~18 t/s | @ 132K 上下文 |
| RTX 3090 | 24GB | 105 t/s | 全部塞进 VRAM |
| RTX 5090 | 32GB | 183 t/s | 全部塞进 VRAM |

### 瓶颈 2：推测解码（Speculative Decoding）在 MoE 上失效

推测解码的原理：用小模型（Draft Model）一次预测多个 token，大模型一次性验证。在 Dense 模型上接近免费加速。

**在 MoE 上彻底失败**，原因很优雅但令人恼火：

```
┌────────────────────────────────────────────────────┐
│      为什么 Speculative Decoding 在 MoE 失效        │
├────────────────────────────────────────────────────┤
│                                                    │
│  正常生成 1 个 token：                              │
│    唤醒 8 个专家 → 8 次记忆体读取                   │
│                                                    │
│  Speculative 预测 4 个 token：                      │
│    唤醒的专家数 ≈ 大部分（非 8x4=32，而是去重后    │
│    接近全量，因为不同 token 路由到不同专家）        │
│                                                    │
│  Mixtral 实测（2025 论文）：                        │
│    1 token  → 2 个专家                             │
│    8 tokens → 7+ 个专家（3.5x 数据搬运）           │
│                                                    │
│  专家在 CPU → 每个额外专家 = 多走一次记忆体汇流排   │
│  验证阶段速度下降 2-3x                             │
│  整体比不开 Speculative 还慢 1.5x                   │
│                                                    │
└────────────────────────────────────────────────────┘
```

**替代方案**：多 token 预测（Multi-Token Prediction），将 draft head 训练进模型本身，共享 context 和 cache：

| 模型类型 | 加速比 |
|----------|--------|
| Dense 模型 | 1.4-2.0x |
| MoE 模型 | 1.15-1.25x（聊胜于无）|

> 稀疏性一手给、一手收——MoE 省了推理带宽，但也让投机解码失去了舞台。

---

## 五、4-bit 量化质量评估

22GB 的 4-bit 版本是否还是那个 SWE-bench 73.4 分的模型？

| 指标 | 4-bit | 8-bit | 说明 |
|------|-------|-------|------|
| KL Divergence | 0.0137 | 0.0026 | 4-bit 漂移是 8-bit 的 5 倍 |
| Perplexity 变化 | <1% | 更小 | 量化影响有限 |

Unsloth 自己的警告值得记住：Perplexity 和 KL Divergence 都受校准数据影响，**都不能可靠预测模型能否完成你的具体任务**。

> 最终的 benchmark 是在你自己的代码仓库上跑一天。

---

## 六、总结与落地建议

### 核心洞察

过去两年的规则是：VRAM 是一堵墙，模型要么装得进要么装不进，唯一的解法是买更大的卡。稀疏模型把这堵墙变成了预算。

```
┌──────────────────────────────────────────────────────────┐
│                    范式转变                               │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  旧范式：VRAM = 容量墙                                   │
│  "模型 fit 或不 fit，不 fit 就买卡"                       │
│                                                          │
│  新范式：VRAM = 预算分配                                 │
│  "哪些权重值得放进快速记忆体？"                          │
│   → 高频权重 → GPU VRAM                                  │
│   → 低频权重 → 系统 RAM                                  │
│                                                          │
│  6GB + 2016 年的卡 + 350 亿参数 + 实时响应               │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### 适用 vs 不适用场景

```
        ┌─ 17 t/s > 人类阅读速度 ──→ ✅ 个人互动助手
        │                         ✅ 代码阅读 / 辅助工具
        │                         ✅ 桌面级日常对话
适用？──┤
        │                         ❌ 全自动 Agent 任务
        └─ 17 t/s 对无人值守太慢 ─→ ❌ 大规模异步批处理
                                  ❌ 需要 API 级速度的场景
```

### 实践步骤

1. 下载 4-bit MoE 模型（如 Q4_K_M），Qwen3.6-35B-A3B 的 Unsloth 构建为 22.36 GB
2. 使用 llama.cpp 原生 CLI（不用 Ollama / LM Studio GUI）
3. 用 `--n-cpu-moe` 压低专家层，渐进调到 VRAM 填满前临界值
4. 配合 `-ngl` 把常规图层推回 GPU
5. 提高 microbatch（`-tb`）兼顾 Prompt 读取速度
6. 长期运行加 `--mlock` 防止 page out

### 决策树：选哪种方案

```
你的 VRAM 够装下整个模型吗？
├─ 是 → 直接 -ngl 99，全部 GPU，不需要 --n-cpu-moe
└─ 否 → 模型是 MoE 架构吗？
    ├─ 否 → 降量化精度（Q3/Q2）或换更小模型
    └─ 是 → 使用 --n-cpu-moe + -ngl 组合
           ├─ 模型 < 系统 RAM → --load-mode mlock（全量载入）
           └─ 模型 > 系统 RAM → --load-mode mmap（保留映射）
```

---

## 参考资料

- [Running a 22GB AI Model on a 6GB GPU, FAST (llama.cpp Guide) — Cloud Codes](https://www.youtube.com/watch?v=7AExwNFlXU4)
- [New llama.cpp options make MoE offloading trivial: `--n-cpu-moe` — r/LocalLLaMA](https://www.reddit.com/r/LocalLLaMA/comments/1mi7bem/new_llamacpp_options_make_moe_offloading_trivial)
- [Guide to optimizing inference performance of large MoE models — GitHub Gist](https://gist.github.com/DocShotgun/a02a4c0c0a57e43ff4f038b46ca66ae0)
- [Hybrid CPU/GPU inference — ik_llama.cpp docs](https://ikawrakow-ik_llama-cpp.mintlify.app/inference/hybrid-cpu-gpu)
- [Running MoE models with llama.cpp — ダフネ](https://note.com/daphne_none/n/na79c985f2aa3)

## 相关笔记

- [[llama-cpp 本地 GGUF 推理]]
- [[本地 LLM 部署优化]]

---

*文档生成时间：2026-08-01*
*基于 Cloud Codes 视频 + llama.cpp `--n-cpu-moe` 主线（2025-08 合并）*
