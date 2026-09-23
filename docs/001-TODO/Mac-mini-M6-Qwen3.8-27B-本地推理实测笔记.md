---
title: Mac mini M6 本地推理实测：Qwen3.8-27B 从 8.3 到 52.6 TPS
aliases: ["Mac mini M6 First Look", "M6 本地推理 53 TPS", "Splash Engine 实测"]
tags:
  - local-llm
  - apple-silicon
  - qwen
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=7ugFOo8e8H4"
  - "https://www.apple.com/newsroom/2026/08/apple-unveils-a-more-powerful-mac-mini-featuring-the-all-new-m6-and-m5-pro/"
  - "https://www.apple.com/mac-mini/specs/"
  - "https://lmstudio.ai/blog/splash-engine"
  - "https://localmodelwatch.tsuchitsuchi.com/en/2026/09/20/inco-ai-releases-splash-engine-for-mac"
  - "https://github.com/jundot/omlx"
  - "https://rentamac.io/mac-mini-m6-statistics/"
author: Execute Automation（Karthik K）
created: 2026-09-23
updated: 2026-09-23
description: Mac mini M6（32GB）首发日实测 Qwen3.8-27B 4-bit：MLX 原生 8.9 → OMLX Lightning MTP 17.8 → Splash 引擎 52.6 tok/s，带宽决定 decode 上限的第一性原理拆解与采购决策，附 Apple 官方口径逐条验证。
level: intermediate
stars: 3
---

# Mac mini M6 本地推理实测：Qwen3.8-27B 从 8.3 到 52.6 TPS

> Execute Automation 频道在 M6 Mac mini 全球发售当天（2026-09-22）做的开箱 + 本地 LLM 首测：同一颗 Qwen3.8-27B（4-bit 量化）在三种推理引擎下从 8.9 冲到 52.58 tok/s。本文基于视频字幕全文，用 Apple 官方规格、LM Studio / Inco AI 官方博客、OMLX 仓库做了逐条交叉验证——重点是拆解清楚：这 6 倍速度里哪些来自硬件，哪些来自引擎。

> [!info] 基本信息
> - **视频**: Mac mini M6 First Look — Qwen3.8-27B Hits 53+ TPS Locally（12:37）
> - **频道**: Execute Automation（Karthik K，测试自动化课程作者）
> - **发布**: 2026-09-22（M6 mini 发售当天），测试机为自购 32GB 版
> - **素材**: webReader 抓取完整英文字幕（Tier 0 被 IP 封锁）

## 目录

- [[#视频与硬件背景]]
- [[#M6 规格解析：带宽才是本地推理的命门]]
- [[#实测数据链：同一模型，三种引擎]]
- [[#第一性原理拆解：52 TPS 为什么不可能是纯硬件]]
- [[#引擎技术解构：三条推测解码路线]]
- [[#内存占用与选购决策]]
- [[#真实场景：C# Selenium 代码生成]]
- [[#验证与勘误记录]]
- [[#参考资料]]
- [[#相关笔记]]

---

## 视频与硬件背景

频道主 Karthik 是 Selenium/C# 测试自动化方向的课程作者，这是他工作室的第三台 Mac mini（前两台：M4 16GB、M4 24GB），本台 M6 32GB 为发售日首发自购。视频结构：

```
开箱/规格速览 (00:00-04:00)
  │
  ├─ LM Studio 社区版 MLX 模型，无 MTP ──→ 8.3-8.9 tok/s（基线）
  ├─ OMLX + Lightning MTP 模型变体 ────→ 16.2-19.1 tok/s（2x）
  └─ LM Studio Splash 引擎（Inco AI）──→ 52.58 / 33-34 tok/s（4-6x）
       └─ C# Selenium DI+POM 代码生成实测（33.4 tok/s）
```

三个值得注意的测试纪律：全部用 4-bit 量化（控制变量）、同一颗 Qwen3.8-27B dense、每次切换引擎前先 unload 释放统一内存。这是"first look"类视频里少见的对照意识。

---

## M6 规格解析：带宽才是本地推理的命门

### 机器定位演进

| 维度 | M4 mini（2024-10） | M6 mini（2026-09 发售） | M5 Pro mini（同场发布） |
|------|------|------|------|
| CPU | 10 核（4P+6E） | 12 核（2 super + 4P + 6E） | 15-18 核 |
| GPU | 10 核 | 12 核 + Neural Accelerators | 16-20 核 + Neural Accelerators |
| Neural Engine | 16 核 | **Dual 16 核**（较上代 2x） | 16 核 |
| 统一内存 | 16GB 起，可选 24/32GB | 16GB 起，可选 24/32GB | 最高 64GB |
| 内存带宽 | 120GB/s | **24/32GB 档 170GB/s**（16GB 档仅 153GB/s） | 307GB/s |
| 起售价（美元） | $599（发布时） | $899 | 更高一档 |

### 带宽代际演进（decode 速度的物理天花板）

```
M2 mini   M4 mini   M6(16GB)  M6(24/32GB)  M4 Pro    M5 Pro
100  →    120   →   153   →   170      →   273   →   307    GB/s
 │        │                             │
 └─ M6 标准档只比 M4 高 42% ─────────────┘ M4 Pro(2024) 带宽已高于 M6 标准档
```

关键认知：**Token 生成（decode）阶段通常是带宽瓶颈而非算力瓶颈**——每生成一个 token 都要把全部权重从统一内存读一遍。Apple 官方宣传的「up to 4x faster AI performance」是综合口径，其中 LLM prompt processing（prefill，计算型）4.8x vs M4 属实，但这不代表 decode 也 4x——decode 端带宽只提升了 1.42x。这个区分是理解后面实测数据 的钥匙。

另一个视频没提的细节（Apple 官网 specs 页）：M6 的 170GB/s 只属于 24/32GB 内存档，16GB 基础档是 153GB/s——买 16GB 版等于同时损失内存容量和带宽。

---

## 实测数据链：同一模型，三种引擎

### 速度对比总表（均为 Qwen3.8-27B dense，4-bit）

| 引擎/技术 | 模型变体 | 场景 | 实测 TPS | 相对基线 |
|-----------|---------|------|---------|---------|
| LM Studio MLX 原生（无 MTP） | 社区版 MLX 4-bit | 对话 | 8.3 - 8.9 | 1.0x |
| OMLX + Lightning MTP | OQ4 MTP 变体（OMLX 作者发布） | 对话 | 16.2 - 17.8 | ~2x |
| OMLX + Lightning MTP | 同上 | Selenium C# 代码 | 19.1 | ~2.2x |
| Splash 引擎（Inco AI） | incoai/Qwen3.8-27B-Splash | 首次对话 | **52.58** | ~6x |
| Splash 引擎 | 同上 | 后续多轮对话 | 33 - 34 | ~4x |
| Splash 引擎 | 同上 | 代码生成（含思考 10.8s） | 33.4 | ~4x |

### 速度阶梯可视化

```
tok/s
 56 ┤
 52 ┤ ████████████████████  Splash 首响 52.58
 48 ┤
 40 ┤
 34 ┤ █████████████        Splash 多轮/代码 33-34
 28 ┤
 20 ┤ ██████               OMLX+MTP 代码 19.1
 17 ┤ ██████               OMLX+MTP 对话 16.2-17.8
 12 ┤
  9 ┤ ███                  MLX 原生 8.3-8.9
  4 ┤
  0 └────────────────────────────────────────────
      基线      MTP 推测解码      特化引擎+draft model
```

两个视频内的细节值得记录：

- MTP 开启后 prefill（prompt 处理）速度也明显上升（字幕：「The prefill speed is also increasing」）——推测解码不只加速 decode。
- 首响 52.58 与多轮 33-34 的落差：首条 "hi" prompt 极短、KV cache 冷启动小；上下文变长后 decode 仍受带宽与 KV 管理约束，33-34 更接近持续输出真实值。Inco 官方自测（48GB M5 Pro）同样是短 prompt 74 TPS、32K 上下文降到 54 TPS 的曲线，模式一致。

### 对照：官方与第三方参考值

| 数据点 | 数值 | 来源 |
|--------|------|------|
| Apple 官方：LM Studio LLM prompt processing | 4.8x vs M4（13.5x vs M1） | Apple Newsroom |
| Inco AI 自测 Splash @ Qwen3.8-27B（48GB M5 Pro） | 短 prompt 74 TPS / 32K 上下文 54 TPS | LM Studio 官方博客 |
| Splash 4 并发合计吞吐 | 170 TPS（次快引擎的 3.9x） | 同上 |
| M4 mini 跑同模型 | 无法加载（24GB 版也吃紧） | 视频口播 |

---

## 第一性原理拆解：52 TPS 为什么不可能是纯硬件

这是本笔记的核心分析（视频没有展开，但对理解"该买什么"至关重要）。

### 带宽数学

27B dense 模型 4-bit 量化 ≈ 每 param 约 4.5 bit → **权重约 15GB**。单流自回归 decode 时每 token 读一遍全部权重：

```
理论 decode 上限 = 带宽 ÷ 权重大小
               = 170 GB/s ÷ 15 GB
               ≈ 11.3 tok/s          ← M6 单 token/pass 的物理天花板
```

对照实测：

| 实测档位 | TPS | 达到带宽上限的几倍 | 需要的机制 |
|---------|-----|------------------|-----------|
| MLX 原生 | 8.3-8.9 | 0.74-0.79x | 无（正常低效实现） |
| OMLX + MTP | 16.2-19.1 | ~1.5x | 每 pass 平均接受 ~1.7 token |
| Splash | 33.4 | ~3.0x | 每 pass 平均接受 ~3 token |
| Splash 首响 | 52.58 | ~4.7x | 短 prompt + 高接受率 |

结论一句话：**超过 11 tok/s 的每一分速度都来自推测解码（speculative decoding）让一次权重读取产出多个 token**，而不是 M6 芯片本身"变快了 6 倍"。M6 的功劳是把带宽底座从 120 提到 170GB/s（+42%）并让 32GB 容量档能装下 27B 模型；剩下 3-4 倍是引擎侧的军备竞赛。

### 选购启示（由数学直接推出）

- ✅ 买 M6 跑 27B 的正确姿势：32GB + 特化引擎（Splash / MTP 变体），缺一不可
- ❌ 买 16GB 版（153GB/s + 装不下）或 24GB 版（装下但 KV cache 一深就 swap）跑 27B dense
- ❌ 期待 M6「4x AI 性能」自动体现在 decode——那是 prefill 口径

---

## 引擎技术解构：三条推测解码路线

视频里 8.9 → 52.58 的三级跳，本质是三代推测解码技术路线的同台：

```
            传统自回归                推测解码（speculative decoding）
        ┌──────────────────┐    ┌─────────────────────────────────────┐
        │ 每步：读全部权重  │    │ draft 模型先草拟 k 个 token（便宜）  │
        │   → 产出 1 token │    │ 主模型一次前向批量验证 k 个（读一遍）│
        │                  │    │   → 接受 m 个（m ≤ k）              │
        │ 8-9 tok/s @ M6   │    │ 17-52 tok/s，接受率越高越快         │
        └──────────────────┘    └─────────────────────────────────────┘
                    三条实现路线：
                    ① Lightning MTP：模型自带 MTP 头（OMLX 激活）
                    ② Splash/DFlash 2：外挂专用训练 draft 模型 + 特化 kernel
                    ③ MTPLX：模型原生 MTP 头独立运行时（社区另一分支）
```

### 三路线对比

| 维度 | Lightning MTP（OMLX） | Splash Engine（Inco AI） | MTPLX |
|------|----------------------|--------------------------|-------|
| 归属 | jundot/omlx 开源项目 | Inco AI 公司开源 | 社区独立运行时 |
| draft 来源 | 模型自带 MTP 头 | 专用训练的 DFlash 2 draft 模型 | 模型原生 MTP 头 |
| 特化程度 | 通用 MLX 生态 | per-model kernel + memory plan | 原生 MTP 批量验证 |
| 支持模型 | MLX 格式 MTP 变体 | 仅 Qwen3.6-35B-A3B / Qwen3.8-27B 两款 | 支持原生 MTP 头的模型 |
| 获取方式 | OMLX app + MTP 变体量化 | LM Studio Bionic → Settings > Runtime > Experimental > Splash (Metal) | 独立 app/CLI |
| 实测表现（本视频） | 16-19 tok/s | 33-52 tok/s | 未测（第三方称 2.24x TPS） |

几个经官方资料坐实的细节：

- Splash **不是通用运行时**——不能随意加载任意 GGUF/MLX 模型，只服务两款特化模型，换来 per-model 的 GPU kernel 与内存布局优化（这是它快的原因，也是它的边界）。
- DFlash 系列 draft 模型同时在 SGLang、vLLM、TensorRT-LLM、llama.cpp 可用，累计下载 600 万+——Splash 的加速思路在 NVIDIA 生态有同源实现。
- OMLX v0.6.1（2026-09）release note 明确写了「focuses on Qwen3.8 performance」与「Lightning MTP generation +34.0% decode throughput」——视频测到的 2x 是该版本发行前的成绩，引擎仍在快速迭代。
- 视频里 LM Studio 社区版 MLX 模型「不是 MTP 模型所以 Lightning MTP 加载不了」——用 Splash/MTP 路线必须下对模型变体（OMLX 作者的 OQ4 MTP 量化或 incoai 的 -Splash 版），拿社区普通 MLX 量化只有 8-9 tok/s。

### 工程最佳实践

- ✅ 换引擎前先 unload 旧模型（视频每轮都做，避免内存压力污染下一组数据）
- ✅ 对照测试固定量化位宽（4-bit 全程一致），速度差异才可归因于引擎
- ✅ 记录首响与多轮两个数——首响好看，多轮才代表真实使用
- ❌ 把单一 benchmark TPS 当采购依据——代码/结构化输出（MTP 接受率高）和自由对话的加速比不同

---

## 内存占用与选购决策

### 32GB 档的实测占用

| 状态 | 占用 | 压力区颜色（活动监视器） |
|------|------|------------------------|
| 模型加载后（MLX 原生） | ~25GB | 绿 |
| OMLX + MTP 推理中 | 不冲黄 | 绿 |
| Splash 推理中 | 峰值 27-28GB，未触顶 | 绿 |

15GB 权重 + macOS + LM Studio/OMLX 双进程 + KV cache ≈ 25-28GB。32GB 能跑但余量薄（~4-7GB）；上下文开长（32K+）后 KV cache 增长，最先见红的就是这个档位。

### 选购决策树

```
你的主力模型是什么量级？
│
├─ ≤14B（Phi-4 级）
│    └─ 16GB M6（153GB/s）够用 ──→ 但注意 16GB 档带宽比 24/32GB 档低 10%
│
├─ 27B dense（Qwen3.8-27B / Qwen3.6-27B）
│    ├─ 要稳定长上下文 ──→ M5 Pro 64GB + 307GB/s（带宽 1.8x，代价更高）
│    └─ 中短上下文日常 ──→ M6 32GB + Splash/MTP 引擎（本视频方案）✅
│         └─ 24GB 版直接出局：权重 15GB + KV + 系统 → swap 崩溃
│
└─ 35B+ MoE / 更大 dense
     └─ Mac mini 全线不合适，看 Mac Studio（M5 Ultra / 更大带宽与内存）
```

### 关于价格

视频口播「记忆体涨价导致这台涨了 $200」与可查基准对不上（见勘误表）。可坐实的是：M6 mini $899 起，比 M4 发布价 $599 高 50%，比 M6 发布前 M4 实售价 $799 高 $100；2026 年存储涨价导致 Mac 全线提价 15-20%。买 32GB 配置还要在 $899 基础上加内存与存储选配价。

---

## 真实场景：C# Selenium 代码生成

### 测试任务

> 「写一个 Selenium C# .NET 的 google.com 搜索页测试，要求 DI（依赖注入）+ POM（Page Object Model）+ 关注点分离」

这是频道主的本行领域，输出质量他有判断力（字幕：「look at how much quality of the code is being written」）。

### Splash 引擎下的执行时间线

```
t=0        prompt 提交
 │
 ├─ 思考阶段（thinking）────────── 10.8 秒
 │
 ├─ 开始流式输出代码 ───────────── 33.4 tok/s 持续
 │    （频道主：流畅度接近他的 M5 Max 工作站）
 │
 └─ 输出完成：完整 DI + POM 结构代码
```

### 三代硬件同任务对照

| 机器 | 结果 |
|------|------|
| M4 mini 24GB | 同模型无法加载/无法完成 |
| M6 mini 32GB + Splash | 思考 10.8s + 33.4 tok/s 输出完整框架 |
| MacBook Pro M5 Max（频道主参照） | 主观体感同级 |

代码生成是推测解码的甜点场景：结构化代码的 draft 接受率高（NVIDIA 侧 NInfer 的数据是代码任务 MTP 接受率 ~81% vs 混合语料 ~49%），同一引擎写代码比写散文更快——视频里 OMLX 档代码 19.1 vs 对话 16.2-17.8 也印证了这一点。

---

## 验证与勘误记录

以下逐条核对视频口播声称与外部权威来源（2026-09-23 查证）：

| 视频声称 | 验证结果 | 依据 |
|---------|---------|------|
| M6 配备双神经引擎（dual neural engine） | ✅ Apple 官方：Dual 16-core Neural Engine，较上代最高 2x | Apple Newsroom / 官网 specs |
| 12 核 CPU / 12 核 GPU | ✅ 官方规格完全一致（2 super + 4P + 6E） | Apple 官网 specs |
| 内存带宽 170GB/s（M4 为 120GB/s） | ✅ 但有档位细节：170GB/s 仅 24/32GB 档，16GB 基础档为 153GB/s | Apple 官网 specs 页 |
| Apple 称比 M4 mini 快 4 倍 | ✅ 官方口径为「up to 4x faster AI performance」；LLM prompt processing 专项是 4.8x vs M4（prefill 口径，非 decode） | Apple Newsroom |
| M4 mini「现在只能买到 24GB 版」 | ⚠️ 未能独立验证 M4 在售配置收缩情况；M4 原生支持 16/24/32GB 选配 | Apple 官网（specs 页已只列 M6/M5 Pro） |
| 记忆体涨价导致涨价 $200 | ⚠️ 与基准不符：M6 $899 起 vs M4 发布价 $599（+$300）vs M6 前夕 M4 实售价 $799（+$100）；「涨 $200」两个口径都对不上，可能指其 32GB 具体配置的差价 | rentamac.io 统计（2026-09） |
| Qwen3.8-27B dense 模型存在且流行 | ✅ HuggingFace 官方 Qwen/Qwen3.8-27B（2026-08-14 发布，Apache 2.0） | HF 模型卡 |
| OMLX 引擎与 Lightning MTP | ✅ jundot/omlx 真实存在；v0.6.1 专注 Qwen3.8 性能，Lightning MTP decode +34% | GitHub releases |
| Splash 是独立于 MLX 的引擎 | ✅ Inco AI 开源引擎，集成于 LM Studio Bionic（Experimental backends）；per-model kernel + memory plan；仅支持两款模型 | LM Studio 官方博客 |
| Splash 首响 52.58 TPS / 多轮 33-34 / 代码 33.4 TPS | ✅ 视频一手实测（字幕确认），量级与 Inco 官方自测一致（短 prompt 74 TPS @ M5 Pro 48GB） | 视频字幕 + LM Studio 博客 |
| 结尾口播「56 tokens per second」 | ⚠️ 与片中实测 52.58 不一致，疑为口播四舍五入失误，以 52.58 为准 | 视频字幕前后文 |
| M4 mini 跑 Qwen3.8-27B「无法加载」 | ⚠️ 一手体验陈述无法复现验证；从 24GB 内存 vs 25GB+ 占用推算，确实装不下，说法合理 | 内存占用数学 |

补记：视频发布当天（2026-09-22）恰逢 Splash 引擎 + LM Studio Bionic 集成发布两天后（2026-09-20 localmodelwatch 报道）——这台机器的首测成绩本身就是「新硬件 × 新引擎」同周共振的产物，数字时效性强，引擎后续版本大概率还会变。

---

## 参考资料

- [Mac mini M6 First Look — Qwen3.8-27B Hits 53+ TPS Locally（Execute Automation）](https://www.youtube.com/watch?v=7ugFOo8e8H4)
- [Apple unveils a more powerful Mac mini featuring the all-new M6 and M5 Pro（Apple Newsroom）](https://www.apple.com/newsroom/2026/08/apple-unveils-a-more-powerful-mac-mini-featuring-the-all-new-m6-and-m5-pro/)
- [Mac mini 技术规格（Apple 官网，含 153/170GB/s 分档）](https://www.apple.com/mac-mini/specs/)
- [Splash Engine — the fastest local Qwen3.8 on Apple Silicon（LM Studio 官方博客）](https://lmstudio.ai/blog/splash-engine)
- [Inco AI Releases Splash Engine for Mac（Local Model Watch）](https://localmodelwatch.tsuchitsuchi.com/en/2026/09/20/inco-ai-releases-splash-engine-for-mac)
- [jundot/omlx（GitHub，Lightning MTP）](https://github.com/jundot/omlx)
- [Mac Mini M6 Statistics: M1 to M6 Compared（rentamac.io）](https://rentamac.io/mac-mini-m6-statistics/)

## 相关笔记

- [[NInfer-Qwen3.8-27B-单卡4090-256K上下文-1.5倍速实战笔记]] — 同一模型在 NVIDIA 单卡侧（RTX 4090、256K 上下文、100-130 tok/s）的镜像篇：两篇合看即 Qwen3.8-27B 在两类硬件 + 两代推理引擎的完整加速路线图（本篇补 Apple Silicon 端侧视角）
- [[Qwen3.6-27B-MacBook-M4-本地部署指南]] — M4 时代 Mac 本地部署基线，可与本篇 M6 实测对照看代际提升
- [[Qwen3.8-27B 模型评测与 SGLang 高速推理部署]] — 模型本体评测与服务器侧部署

---

*文档生成时间：2026-09-23，基于视频字幕全文 + Apple/LM Studio/Inco AI 官方来源交叉验证*
