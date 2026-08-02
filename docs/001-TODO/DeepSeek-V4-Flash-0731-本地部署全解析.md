---
title: DeepSeek V4 Flash 0731 — 284B MoE 本地部署与基准质疑全解析
aliases: [DeepSeek V4 Flash, DS V4 Flash 0731, DeepSeek 本地部署]
tags:
  - llm
  - local-inference
  - deepseek
  - quantization
  - benchmark-audit
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=MwkZ7DwhRXk"
  - "https://unsloth.ai/docs/models/deepseek-v4"
  - "https://huggingface.co/unsloth/DeepSeek-V4-Flash-0731-GGUF"
author: Cloud Codes（频道）；DeepSeek（模型）；Unsloth（量化）
created: 2026-08-02
updated: 2026-08-02
description: DeepSeek V4 Flash 0731 的 MoE 架构、基准测试真伪、Unsloth 量化阶梯与本地部署硬件门槛深度解析
level: intermediate
stars: 4
---

# DeepSeek V4 Flash 0731 — 284B MoE 本地部署与基准质疑全解析

> 2026-07-31，DeepSeek 发布 V4 Flash 0731（284B 总参数 / 13B 激活），Unsloth 在 4 小时 54 分钟后推出可本地运行的 GGUF 量化版。本文拆解其 MoE 架构、质疑官方基准数据、并实测本地部署的硬件门槛——结论是：模型是真的，但厂商自测分数不可信，而真正阻挡你本地跑它的不是许可证，是 DRAM 价格。

> [!info] 基本信息
> - **视频**: [Run "DeepSeek V4 Flash 0731" on Laptop Locally](https://www.youtube.com/watch?v=MwkZ7DwhRXk)
> - **频道**: Cloud Codes（英文技术深度解读）
> - **发布**: 2026-08-01｜**时长**: 15:28｜**观看**: 22.4K
> - **模型授权**: MIT（DeepSeek 权重 + Unsloth 转换，均可商用）
> - **关键日期**: 权重推送 07-31 07:30 UTC；Unsloth GGUF 07-31 12:24 UTC

---

## 目录

1. [核心架构：MoE + Hashed Routing + MXFP4](#1-核心架构moehashed-routingmxfp4)
2. [基准测试真伪：厂商自测 vs 独立审计](#2-基准测试真伪厂商自测-vs-独立审计)
3. [Unsloth 量化阶梯：8-bit 的秘密](#3-unsloth-量化阶梯8bit-的秘密)
4. [硬件实测与 KV Cache 瓶颈](#4-硬件实测与-kv-cache-瓶颈)
5. [工具链对接：llama.cpp → Claude Code / Codex](#5-工具链对接llamacpp--claude-code--codex)
6. [DRAM 经济学与最终决策](#6-dram-经济学与最终决策)

---

## 1. 核心架构：MoE + Hashed Routing + MXFP4

### 一句话定位

DeepSeek V4 Flash 是一个混合专家（Mixture-of-Experts, MoE）模型：284B 总参数躺在内存里，但每个 Token 只激活 13B —— 这就是它能用"小模型的速度"跑"大模型智力"的根本原因。

### 架构关键数字

| 维度 | V4 Flash 0731 | V4 Pro（旗舰） | 备注 |
|------|---------------|----------------|------|
| 总参数 | 284B | 1.6T | Flash 是 Pro 的 ~1/6 |
| 每 Token 激活参数 | 13B | ~49B | MoE 的核心优势 |
| 共享专家数 | 1 | 1 | 每个 Token 必经 |
| 路由专家数 | 256 | 256 | 每 Token 选 6 个 |
| 单专家中间维度 | 2048 | — | 专家本身很小 |
| 上下文窗口 | 1,048,000 Token | 同 | 压缩稀疏注意力实现 |
| 最大输出 | 384,000 Token | 同 | 超长生成 |
| 训练精度 | MXFP4（4-bit） | — | 原生低精度，非后期压缩 |
| 预训练数据量 | 32T Token | — | 异常庞大 |

### Hashed Routing：前三层的特殊设计

大多数 MoE 模型每层都用可学习的路由器（Router）决定 Token 去哪个专家。V4 Flash 在**前三层做了不同的事**——固定 Hash 赋值，没有路由器可训练。

```
Token 进入
   │
   ├─ Layer 1-3: 固定 Hash 路由（无可训练参数，无路由损耗）
   │
   └─ Layer 4+: 可学习路由器 → 从 256 专家选 6 个
                      │
                      ├─ 6 个路由专家（各 intermediate=2048）
                      └─ 1 个共享专家（每个 Token 必经）
                              │
                              └─ + Attention 层
                                     │
                          合计约 13B 参数被激活
```

这减少了训练负担和路由不稳定性，代价是前三层的专家分配不可学习。

### 注意力与训练创新

- **混合注意力（Hybrid Attention）**：压缩稀疏注意力（Compressed Sparse Attention）叠加重度压缩注意力（Heavily Compressed Attention），这是 1M 上下文窗口得以实现的工程基础
- **残差连接替换**：用 MHC 替代传统残差连接
- **优化器**：Muon（非 AdamW）
- **预训练规模**：32 万亿 Token

### DSpark 草稿模块（Speculative Decoding）

这是多数解读漏掉的"第二个模型"。DeepSeek Spark 是一个 ~20B 参数的草稿模块，做投机解码（Speculative Decoding）：

```
[DSpark 草稿模块] 一次性提议一整块 Token
        │
        ▼
[V4 Flash 主模型] 一遍过验证整块
        │
   ├─ 验证通过 → 接受（一次前向生成多 Token）
   └─ 验证失败 → 逐 Token 回退到标准生成
```

**两个容易混淆的加速数字**（视频特意区分）：

| 指标 | 加速幅度 | 谁关心 |
|------|----------|--------|
| 单用户生成速度 | 60%~85% | 你（实际体感） |
| 整机总吞吐 | ~51% | 数据中心（成本核算） |

> [!warning] Hugging Face 页面显示 304B 参数，技术报告写 284B
> 差值 ~20B 就是 DSpark 草稿模块。它跟主模型在同一个 repo 里一起发布，所以 HF 总数偏高。真正的模型是 284B。

### API 定价极具攻击性

| 模型 | 输入（/M Token） | 输出（/M Token） | 缓存命中输入 | 并发限制 |
|------|------------------|------------------|-------------|----------|
| **V4 Flash** | $0.14 | $0.28 | $0.002（降 50 倍） | 高 |
| V4 Pro | $0.435 | $0.87 | — | Flash 的 1/5 |

Flash 比 Pro 便宜约 2/3，且并发限制宽松 5 倍。

---

## 2. 基准测试真伪：厂商自测 vs 独立审计

### 核心问题：所有 9 个 Agent 分数都来自 DeepSeek 自己

DeepSeek Model Card 公布了 9 个 Agent 基准分数（Terminal Bench、Cyber Gym、DeepSWE 等），**全部在 DeepSeek 内部测试环境（In-house Harness）运行**，该 Harness 在脚注中被描述为"即将公开（forthcoming）"。

第三方至今无法复现其中任何一个。

### Terminal Bench 2.1 榜单对照

Terminal Bench 是公开的终端 Agent 排行榜：89 个真实任务（软件工程、系统管理、数据处理、模型训练、安全），沙箱内运行，测试脚本过即得分，不过即 0 分（无部分分）。

| 排名 | 配置 | 分数 | 测试方 |
|------|------|------|--------|
| 1 | Claude Code + Fable 5 | 83.8 | 第三方（公开榜） |
| 2 | Codex + GPT-5.5 | 83.1 | 第三方（公开榜） |
| **3** | **DeepSeek V4 Flash（自称）** | **82.7** | **DeepSeek 自测（未上榜）** |
| — | DeepSeek V4 Flash | — | **Artificial Analysis 独立板：未上榜** |

> [!danger] DeepSeek 自称第三，但它根本没出现在任何公开的 Terminal Bench 2.1 榜单上
> Artificial Analysis 自己运营的 Terminal Bench 2.1 板跟踪 182 个模型中的 27 个，DeepSeek V4 不在其中。

### DataCurve 的 DeepSWE 审计：80.6 分 → 8% 的崩塌

这是整个基准质疑最有说服力的证据。DataCurve（四人审计团队）2026 年 5 月发布 DeepSWE，专门针对"刷题/记忆（Memorization）"问题：

**DeepSWE 的设计**：
- 任务全部从零编写，覆盖 91 个仓库、5 种语言
- 不从旧 PR 抓取任何内容（防数据污染）
- Verifier 误报率：每 1000 次运行仅 3 次

**审计结果（独立测试）**：

| 模型 | 自家榜分数 | DeepSWE 独立分 | 落差 |
|------|-----------|----------------|------|
| GPT-5.5 | — | 70% | — |
| Opus 4.7 | — | 54% | — |
| Gemini 3.5 Flash | — | 28% | — |
| **DeepSeek V4 Pro** | **80.6**（SWE-bench Pro） | **8%** | **-72.6 分** |

```
80.6 ─────────────██████████████████████  DeepSeek 自报（SWE-bench Pro）
                   │
                   │  巨大落差
                   │
 8.0 ────█─────────┘  DeepSWE 独立审计
```

8% 不是 80.6 的四舍五入误差。两种解释：要么独立测试坏了，要么自报分数测的是模型已经"背过"的题。DataCurve 的论证指向后者。

> [!note] 我不是说新分数是假的
> 仓库流式读取（Repo Streaming）确实能提升 Agent 表现，这是有文献支持的。问题是：你手上只有卖模型那家公司给的一个数字，其他人给的数字是零。

### 唯一的独立分数：Artificial Analysis

Artificial Analysis 跑了 Intelligence Index（推理与知识，非 Agent 任务完成）：

- **得分 50**，同类 101 个模型中排第 3，中位数仅 25
- 证明模型推理能力真实且强
- **但**：这个指数测的是推理/知识，**不能**直接证明终端 Agent 任务完成率——而那才是 DeepSeek Model Card 的核心声明

副作用：该评测消耗了 **2.1 亿输出 Token**（中位数是 1 亿）。

### 评测可信度阶梯

```
┌─────────────────────────────────────────────┐
│  最可信：你自己仓库的 10 个已知任务          │  ← 唯一可复现
├─────────────────────────────────────────────┤
│  次可信：独立第三方公开榜（Artificial Analysis）│
├─────────────────────────────────────────────┤
│  存疑：独立审计但方法论有争议（DeepSWE）      │
├─────────────────────────────────────────────┤
│  最不可信：厂商自测未公开 Harness（Model Card）│  ← 当前 DeepSeek 全部 9 个分数
└─────────────────────────────────────────────┘
```

---

## 3. Unsloth 量化阶梯：8-bit 的秘密

### 13 个量化版本

Unsloth 发布日当天推出 13 个 GGUF 量化版本（发布时宣称"更小的当天就来"，确实来了）：

| 量化 | 文件大小 | 最低 RAM | 适用场景 |
|------|----------|----------|----------|
| 1-bit | 82.5 GB | ~90 GB | 极限压缩（4 月时被认为不可能） |
| 2-bit | 91 GB | ~96.5 GB | 128GB 机器勉强可用 |
| 3-bit | 103~110 GB | 110 GB | 多数高端桌面可达 |
| 4-bit（Q4） | 155 GB | ~162 GB | 平衡点 |
| **8-bit（Q8）** | **162 GB** | **~169 GB** | **唯一带无损声明** |

### 8-bit 反直觉的真相

正常情况下，8-bit 文件应该是 4-bit 的两倍大。这里 8-bit 只比 4-bit 大 7 GB——因为**这个模型本来就是 4-bit 训练的**。

**Hugging Face 的张量分布（无需下载即可自查）**：

| 精度 | 参数量 | 占比 |
|------|--------|------|
| MXFP4（4-bit 打包） | 296B | **97%** |
| FP8 | 6B | ~2% |
| BF16 | 1.5B | <1% |

所以"8-bit 量化"做的事其实是：
- 4-bit 专家权重**逐位重打包**（不压缩，无损）
- FP8 部分展开为 BF16（精确无损）
- 1328 个张量全部与 DeepSeek 原始一致

```
原始权重（已是 MXFP4）          Unsloth Q8
┌──────────────────┐           ┌──────────────────┐
│ 专家：MXFP4 (97%) │ ──逐位重打包──→ │ 专家：重打包格式   │  ← 无损
│ FP8   (2%)       │ ──展开────→  │ BF16             │  ← 精确
│ BF16  (<1%)      │ ──保留────→  │ BF16             │  ← 原样
└──────────────────┘           └──────────────────┘
```

> [!warning] "无损"是 Unsloth 自己的声明，无第三方验证
> Unsloth 声称全部 1328 个张量与 DeepSeek 原始一致，但目前没有外部实验室复核过。

### 选哪个量化？

| 你的情况 | 推荐 | 理由 |
|----------|------|------|
| 内存充裕（≥169 GB） | Q8（8-bit） | 唯一带无损声明 |
| 多数高端桌面（~110 GB） | 3-bit | 性价比最佳，多数桌面可达 |
| 128GB Mac（紧巴巴） | 2-bit | 留约 30 GB 给 KV Cache + OS |
| 想验证"1-bit 能用" | 1-bit | 4 月时被认为不可能，现可用 |

**4 月到 7 月的方法跃迁**：4 月时业界共识是这个模型"下不去 4-bit"（专家已是 FP4，无压缩空间，输出会乱）。Unsloth 的突破是**按张量敏感度选位宽**——基于 150 个基准的敏感度研究，针对长上下文编码和工具调用（而非百科文本）校准。这让 1-bit 成为可能。

---

## 4. 硬件实测与 KV Cache 瓶颈

### 推理速度实测（首日单次运行，非基准）

> [!warning] 以下为单引擎、单次运行、贪心解码，发布当天公布
> 无人复现过。把它们当"第一眼"，不是"基准"。这也正是本视频的核心论点——自己跑。

| 硬件 | 量化 | 生成速度（t/s） | 预填速度（t/s） | 备注 |
|------|------|-----------------|------------------|------|
| MacBook Pro M5 Max（128 GB） | 2-bit | 34 | 87 | 128GB 机器的实际天花板 |
| MacBook Pro M3 Max | 2-bit | 26.5 | — | 上一代芯片 |
| Mac Studio（512 GB） | 4-bit | 35.5 | — | 可跑无损级量化 |
| NVIDIA DGX Spark（$4,700） | — | 13.75 | **343** | 读代码极快，回答慢 |
| 2× 96GB Blackwell（4 月独立测试） | — | ~36 | — | 卡占用仅 30-40% |

**关键观察**：
- 30+ t/s 超过人类阅读速度，日常可用
- **预填速度才是本地推理的痛点**：M5 Max 上 11,700 Token 上下文把生成速度从 34 拉到 26。你的代码库远大于 11K Token
- DGX Spark 是异类：预填 343（读代码极快），但生成仅 13.75（回答慢）
- 2× Blackwell 与 M5 Max 同档位，但价格高数倍

### KV Cache：真正的内存杀手

文件大小不等于实际内存需求。你需要：**权重 + KV Cache + 操作系统**。

```
[128GB Mac 跑 2-bit]
┌──────────────────────────────────────┐
│ 权重：96.5 GB                         │
│ ├─ 剩余：~30 GB                       │
│ │  ├─ KV Cache（随上下文线性增长）      │  ← 长上下文的瓶颈
│ │  └─ macOS + 其他进程                 │
│ └─ 够用工作上下文，不够 1M Token       │
└──────────────────────────────────────┘
```

**救命技术：Quantized KV Cache**（2026-07-07 合入 llama.cpp）
- KV Cache 随对话增长，量化 KV Cache 压缩它
- 这是长上下文在小内存机器上能存活的关键
- 没有它，128GB 机器根本跑不动超长上下文

### Max Reasoning Effort 的陷阱

模型支持 Max reasoning effort，但它需要 **384,000 Token 上下文**才能"值回票价"。在 128GB 机器上，这会直接撑爆内存预算。对多数本地部署，Max 是一个你够不到的设置——开启前先算账。

---

## 5. 工具链对接：llama.cpp → Claude Code / Codex

### llama.cpp 的关键 PR

PR 24162 为本架构添加了：
- 完整的压缩注意力路径
- 新张量操作 **Lightning Indexer**（手写 kernel）
- 同时支持两套 API 协议

### 一个本地进程，同时服务两个 Agent

```
              ┌─────────────────────────────┐
              │   llama.cpp 本地服务         │
              │   （localhost:port）         │
              │                             │
   /v1/messages   ←──── Anthropic 协议       │
   (Anthropic)    ────→ Claude Code          │
              │                             │
   /v1/responses ←──── OpenAI 协议           │
   (OpenAI)      ────→ Codex                 │
              └─────────────────────────────┘
```

### Claude Code 配置

把 `anthropic_base_url` 指向你的 localhost 即可。这就是全部集成。

### Codex 配置

在 `config.toml` 添加 provider block：
- base URL 指向 llama.cpp 端口
- `wire_api = "responses"`（chat 已废弃）

启动 Codex 时用 `off` profile（离线模式）。

### 关键推理参数

| 参数 | 推荐值 | 注意 |
|------|--------|------|
| temperature | 1.0 | — |
| top_p | 1.0（chat） | **不要用 0.95 做 Agent 工作** |
| reasoning_effort | high（默认） | Max 需 384K 上下文，本地慎用 |

### 排错：第三轮开始输出乱码？

V4 不用常规的 Jinja chat template，改用一个独立文件夹里的 Python encoder。Unsloth 重建了模板并测试了 4000 段对话。

**排查顺序**（别一上来就怪模型）：
1. 先更新 llama.cpp
2. 再重新拉取 GGUF
3. 最后才怀疑模型本身

---

## 6. DRAM 经济学与最终决策

### 162GB 内存"今年不便宜"

这是发布帖不会告诉你的部分。

**Apple 的时机**：
- 2026 年 3 月，Apple 砍掉 512GB Mac Studio 选项
- 256GB 升级价推高至 $2,000
- 4 个月后，一个需要 162GB 内存的模型以"下载"形式发布
- 那台能舒服装下它的台式机，在它诞生前就停止接单了

**DRAM 涨价的结构性原因**：

```
AI 加速器需求 ↑
      │
      ▼
三大 DRAM 厂（占全球 95%+ 产能）转产 HBM
      │
      ├─→ 消费级 DDR5 供应 ↓ → 价格较 2025 年底翻倍
      ├─→ Gartner 预测：2026 年 DRAM+SSD 综合 +130%
      ├─→ PC 价格 +17%，出货 -10.4%
      └─→ Micron 2 月砍掉消费级 Crucial 品牌
          Framework 笔记本内存涨价约 50%
```

- 全球约 1/5 的 DRAM 产能转向 AI 加速器
- 新晶圆厂是 2027 年的事
- 缓解时点：2027 年底

**讽刺的现实**：模型终于在 2026 年小到能在桌面跑，而跑它的内存在同一年变贵了。18 个月前 162GB 内存是一次"普通的购物"，现在是一笔"真正的采购"。

### 本地 vs API 决策树

```
你要跑 DeepSeek V4 Flash 0731？
        │
        ├─ 已有 ≥128GB 内存的 Mac？
        │     │
        │     ├─ 是 → 拉 Unsloth GGUF（3-bit 或 2-bit）
        │     │        + 更新 llama.cpp
        │     │        + 接 Claude Code / Codex
        │     │        → 免 API 费、高隐私、可自测
        │     │
        │     └─ 否 → 用 API（$0.14/M 输入）
        │              → 代码发往中国服务器、接受厂商基准
        │
        └─ 无论哪条路：
              停止把 Model Card 当证据。
              用你自己仓库里 10 个已知答案的任务测它。
              这是今天唯一任何人都能复现的 Harness。
```

### 两种路径的取舍

| 维度 | 本地（自有内存） | 官方 API |
|------|-----------------|----------|
| 单次成本 | 一次内存采购 | $0.14/M 输入 |
| 数据隐私 | 完全本地 | 服务器在中国，受中国法律管辖（对很多代码库，讨论到此结束） |
| 速率限制 | 无（你拥有硬件） | 有 |
| 模型可用性 | 不会被下架 | 可能被弃用 |
| 速度 | ~34 t/s（M5 Max 2-bit） | 服务端更快 |
| 基准可信 | 可自测复现 | 只能信任厂商 |

---

## 核心结论

1. **模型是真的，推理能力强**：Artificial Analysis Intelligence Index 50 分（同类第三），架构创新扎实
2. **Agent 分数不可信**：9 个分数全来自厂商未公开的 Harness，前代旗舰在独立审计中从 80.6 暴跌到 8%
3. **本地部署的瓶颈是内存，不是许可证或模型大小**：MIT 授权、Unsloth 量化让它技术上可跑，但 162GB DRAM 在 2026 年是硬门槛
4. **唯一可信的评测是你自己跑**：用你仓库里 10 个已知答案的任务，这是今天唯一可复现的 Harness

---

## 参考资料

- [视频：Run "DeepSeek V4 Flash 0731" on Laptop Locally (Cloud Codes)](https://www.youtube.com/watch?v=MwkZ7DwhRXk)
- [Unsloth DeepSeek V4 本地运行指南](https://unsloth.ai/docs/models/deepseek-v4)
- [Unsloth GGUF 仓库（0731）](https://huggingface.co/unsloth/DeepSeek-V4-Flash-0731-GGUF)
- [DeepSeek 官方 Hugging Face](https://huggingface.co/deepseek-ai)
- [llama.cpp](https://github.com/ggerganov/llama.cpp)
- [Artificial Analysis 独立排行榜](https://artificialanalysis.ai/)
- [Datacurve / DeepSWE 基准审计](https://datacurve.ai/)
- [Claude Code 文档](https://docs.anthropic.com/en/docs/agents-and-tools/claude-code/overview)
- [OpenAI API / Codex](https://platform.openai.com/docs/)
- [DeepSWE 基准审计解读（yage.ai）](https://yage.ai/share/deepswe-benchmark-audit-en-20260528.html)
- [Reddit: DeepSWE 显示 V4 Pro 仅 8% 解题率讨论](https://www.reddit.com/r/LocalLLaMA/comments/1tsse9i/deepswe_benchmarks_indicate_that_deepseek_v4_pro/)

## 相关笔记

- [[本地 LLM 部署]]
- [[MoE 架构]]
- [[模型量化]]
- [[AI 基准测试]]
