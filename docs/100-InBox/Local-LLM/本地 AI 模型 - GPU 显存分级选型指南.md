---
title: 本地 AI 模型 - GPU 显存分级选型指南
aliases: [Best Local AI Models for Every GPU, Code Bear 本地模型推荐, VRAM 分级模型选择]
tags:
  - local-llm
  - model-selection
  - quantization
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=T7WF9okSfrw"
author: Code Bear
created: 2026-08-01
updated: 2026-08-01
description: 按 GPU 显存容量（4GB-128GB）分级推荐本地 AI 模型，涵盖工作记忆计算、量化策略与兼容性评估方法
level: intermediate
stars: 4
---

# 本地 AI 模型 - GPU 显存分级选型指南

> Code Bear 频道的系统性评测，按 VRAM 容量从 4GB 到 128GB 分级推荐本地模型。核心论点：最佳模型不是「刚好塞满显存的最大模型」，而是「留有足够上下文空间、能稳定运行的最强模型」。

---

## 目录

- [一、核心概念：工作记忆 ≠ 档案大小](#一核心概念工作记忆--档案大小)
- [二、显存分级模型推荐](#二显存分级模型推荐)
- [三、极限压缩技术评估](#三极限压缩技术评估)
- [四、选型方法论](#四选型方法论)
- [五、真实归属验证](#五真实归属验证)
- [参考资料](#参考资料)

---

## 一、核心概念：工作记忆 ≠ 档案大小

### 档案大小 vs 运行显存

HuggingFace 上显示的下载大小只是模型权重（Weights）。实际运行时还需要额外显存：

```
┌───────────────────────────────────────────────────┐
│             实际 VRAM 占用 = 模型权重              │
│                          + KV Cache（上下文缓存） │
│                          + 推论引擎开销           │
│                          + 系统开销（OS 等）      │
└───────────────────────────────────────────────────┘
```

**案例 — Bonsai 27B 1-bit：**

| 场景 | 显存占用 |
|------|----------|
| HuggingFace 下载大小 | ~3.9 GB |
| 4K 上下文运行 | ~5.2 GB |
| 100K 上下文运行 | >11 GB（4GB GPU 直接崩溃） |

看似适合 4GB GPU，实际短上下文就超了。

### 记忆体容量 vs 记忆体频宽

这是两个独立维度，新手最常混淆：

| 维度 | 含义 | 决定什么 |
|------|------|----------|
| **容量（Capacity）** | VRAM 总大小 | 模型**能不能装得下** |
| **频宽（Bandwidth）** | 记忆体资料传输速率 | 生成速度**有多快（tokens/s）** |

> [!warning] DGX Spark 陷阱
> Nvidia DGX Spark 拥有 128GB Unified Memory，能塞入大模型，但记忆体频宽远低于高阶独立显卡（如 RTX 5090）。结果是：**模型能载入，但生成速度极慢**。容量回答「能不能跑」，频宽回答「跑多快」。

---

## 二、显存分级模型推荐

### 总览表

| 显存级别 | 稳定推荐 | 实验/极限推荐 | 定位 |
|----------|----------|--------------|------|
| **4-8 GB** | Gemma 4 E4B | Nanbeige 4.2-3B / Bonsai 27B (1-bit) | 轻量多模态 |
| **12-16 GB** | Gemma 4 12B (Q4) | Ternary Bonsai 27B | 进阶日用 |
| **24 GB** | Qwen 3.6-27B (Q4) | Thinking Cap | **消费级甜点** |
| **32-64 GB** | Gemma 4 31B / Qwen 3.6-27B (Full) | Gemma 4 26B A4B (MoE) | 高精度+多模态 |
| **128 GB** | Laguna S 2.1 (量化版) | — | Agent 写程式 |
| **>192 GB** | Motif 3 (314B MoE) | — | 伺服器级（非个人） |

### 4-8 GB：轻量入门

**稳定推荐：Gemma 4 E4B**
- 支援文字/图片/语音多模态
- 内建推理（Reasoning）、函数调用（Function Calling）
- 最大上下文 128K tokens（但小 GPU 实际跑不了这么多）
- 生态成熟：Ollama、LM Studio、llama.cpp 原生支援

**实验选项：**

| 模型 | 特点 | 风险 |
|------|------|------|
| Nanbeige 4.2-3B | Looped Transformer 架构，3B 参数复用层叠达到更强算力 | 官方自称 SWE-Bench 63.6%，但来自自家团队测试，需自行验证 |
| Bonsai 27B (1-bit) | 27B 模型压缩到 3.9GB | 仅保留 89.5% 基准效能；长链 Agent 编码能力弱 |

> [!tip] 选型原则
> 稍弱但能在 Ollama/LM Studio 稳定运行的模型，比需要自定义 Runtime 的跑分冠军更实用。

### 12-16 GB：进阶日用

**稳定推荐：Gemma 4 12B (Q4)**
- 比 E4B 更强的推理和编码能力
- Q4 量化后仍有足够空间留给上下文
- 留有 headroom 是关键：稍小的模型 + 剩余空间 > 塞满的大模型 + 频繁崩溃

**实验选项：Ternary Bonsai 27B**
- 在 1-bit（{-1, +1}）基础上加入 0 作为第三态（{-1, 0, +1}）
- 理论打包大小 ~5.9GB，实际部署约 7.2GB
- 短上下文峰值约 8.4GB
- 保留原始模型 94.6% 基准效能
- 依赖较新的 kernel 和专用 Runtime

### 24 GB：消费级甜点

**稳定推荐：Qwen 3.6-27B (Q4)**
- 满精度约 55GB，Q4 后约 17-18GB
- 24GB GPU 跑 Q4 后还有中等上下文空间
- 支援：编码、推理、文档分析、工具调用
- 推理框架支援度最广

**实验选项：Thinking Cap**
- Qwen 3.6-27B 的微调版
- 减少约 50% 思考 Token（某些任务降幅更大）
- 非全面升级：部分评估提升、部分略降
- 适合标准 Qwen 经常「过度思考」简单问题时使用

```
何时选 Thinking Cap？
┌──────────────────────────┐    ┌──────────────────────────┐
│ Qwen 经常用数千 Token    │───▶│ 试 Thinking Cap          │
│ 「过度思考」简单问题      │    │ （更快、更省）           │
└──────────────────────────┘    └──────────────────────────┘
┌──────────────────────────┐    ┌──────────────────────────┐
│ 需要最高可靠性            │───▶│ 用原始 Qwen 3.6-27B      │
└──────────────────────────┘    └──────────────────────────┘
```

### 32-64 GB：高精度 + 多模态

此级别核心问题从「能不能跑」变成「还需要多少量化」。

| 模型 | 适用场景 | 备注 |
|------|----------|------|
| Gemma 4 31B | 强通用多模态助手 | 文字/图片/语音，成熟生态 |
| Qwen 3.6-27B (Full) | 编码/推理/Agent 优先 | 高精度移除量化损失 |
| Gemma 4 26B A4B (MoE) | 生成速度优先 | 总 25B / 激活 3.8B，速度快但仍需全部显存储存所有专家 |

> [!important] MoE 记忆体机制
> - **总参数** → 决定显存占用（所有专家都需储存）
> - **激活参数** → 决定每 Token 计算量和速度
>
> MoE = 大模型的知识量 + 小模型的速度，但**记忆体占用仍按总参数算**。

### 128 GB：Agent 写程式级别

**推荐：Laguna S 2.1（量化版）**

| 属性 | 数值 |
|------|------|
| 总参数 | 118B（MoE） |
| 激活参数 | 8B / token |
| 上下文 | >100 万 tokens |
| FP8 原始大小 | ~121 GB |
| 定位 | Agent 编码 + 长文本任务 |

> [!danger] FP8 陷阱
> Laguna FP8 原始权重 121GB，看似完美适配 128GB 机器，但**几乎不留空间给 OS、推论引擎和上下文**。128GB 工作站务必用量化版本。

### >192 GB：伺服器级

**Motif 3（314B MoE，13B active）**
- 目前仅 Beta 版本
- 官方仅在资料中心 GPU（H200、B200）测试
- 授权：个人/教育/非商业研究免费，商业需许可
- **不适合个人工作站拼接**，硬件成本和功耗远超模型本身价值

---

## 三、极限压缩技术评估

### 1-bit / Ternary 量化（Bonsai 系列）

```
原始 Qwen 3.6-27B（FP16）  ≈ 54 GB
        │
        ▼  1-bit 压缩（权重 = {-1, +1}）
   Bonsai 27B 1-bit         ≈ 3.9 GB   （保留 89.5% 效能）
        │
        ▼  Ternary 压缩（权重 = {-1, 0, +1}）
   Bonsai 27B Ternary       ≈ 7.2 GB   （保留 94.6% 效能）
```

| 面向 | 评价 |
|------|------|
| 压缩率 | 极高，27B → 3.9GB 是业界突破 |
| 基准保留 | 89.5%-94.6%，表面数字不错 |
| 实际体验 | Reddit 实测反馈偏负面：1-bit 无法产生 benchmark 分数，ternary SWE-bench 仅 7.9% |
| 长链 Agent 编码 | 创作者承认非强项 |
| Runtime 兼容 | 需要专用 kernel 和编译器分支 |

> [!note] 压缩不是免费的
> 基准保留百分比 ≠ 实际任务表现。Reddit r/LocalLLaMA 社区实测显示，Bonsai 在真实编码任务中表现远低于官方数字。**「 fascinating for research, not your everyday model」**。

### MoE 记忆体机制图解

```
              ┌─────────────────────────┐
              │    MoE 模型总参数        │
              │  （全部需要载入 VRAM）   │
              │  ┌───┐ ┌───┐ ┌───┐     │
              │  │E1 │ │E2 │ │E3 │ ... │  ← 所有专家 (Expert)
              │  └───┘ └───┘ └───┘     │
              │       Router            │
              │         │               │
              │     激活 1 个专家        │  ← 每 Token 只用一小部分
              └─────────────────────────┘

  显存占用 ∝ 总参数     计算量 ∝ 激活参数
```

---

## 四、选型方法论

### 三步选型法

```
Step 1: 确认工作流
  ├─ 轻量对话/摘要/多模态  → Gemma 4 系列
  ├─ 编码/推理/Agent       → Qwen 3.6-27B
  ├─ 极限压缩实验           → Bonsai（仅供实验）
  └─ 128GB Agent 编码       → Laguna S 2.1（量化版）
        │
        ▼
Step 2: 检查推论引擎支援
  ├─ Ollama
  ├─ llama.cpp
  ├─ MLX (Apple Silicon)
  ├─ LM Studio
  ├─ vLLM
  └─ SGLang
  → 确认目标模型在引擎中有原生支援，再下载
        │
        ▼
Step 3: 用真实任务测试
  ├─ 丢一段自己 codebase 的 Bug
  ├─ 丢一份长文档做摘要
  ├─ 测试一次工具调用
  ├─ 测试一次图片输入
  └─ 测试一次复杂推理
  → 榜单只能帮你筛出候选，只有自己的任务能告诉你哪个最好
```

### 三条核心原则

| 原则 | 说明 |
|------|------|
| **算运转记忆，不算档案大小** | 预留 20-30% 显存给上下文和系统 |
| **留空间给上下文** | 4K token 够用不代表 100K 够用；模型标注 256K 不代表你的硬件跑得了 |
| **选稳定可靠的，不选数字最大的** | 能在成熟引擎稳定运行的模型 > 需要自己编译的跑分王 |

### Benchmark 警示清单

```
✅ 可以参考的 Benchmark
  └─ 社区独立测试（Reddit r/LocalLLaMA 等）

❌ 需要警惕的 Benchmark
  ├─ 模型自家团队发布的数字（如 Nanbeige、Laguna）
  ├─ 使用 Custom Scaffolds（专用提示词框架）跑出的分数
  └─ 没有第三方复现的 SWE-Bench / Terminal-Bench 成绩
```

---

## 五、真实归属验证

视频中提及的模型，经搜索交叉验证后的真实归属和社区评价：

| 视频中的名称 | 真实开发方 | 社区评价 |
|-------------|-----------|----------|
| Bonsai 27B | **PrismML**（prismml.com），非大厂 | Reddit 实测 1-bit 无法产生分数、ternary SWE-bench 仅 7.9%，远低于官方数字 |
| Laguna S 2.1 | **Poolside**（poolside.ai），agentic coding 专精公司 | 开源权重，另有更小的 Laguna XS 2.1 (33B/3B) |
| Nanbeige 4.2-3B | **Nanbeige** 独立团队，looped transformer 架构 | Reddit r/LocalLLaMA 有帖「I'm not impressed」，实际体验与官方声称有落差 |
| Motif 3 | Beta 版本，资料中心级 | 个人工作站不适用 |

> [!note] 验证结论
> 视频介绍的所有模型都是独立团队/新创公司项目，非 Google/Nvidia 等大厂产品。视频本身立场中立，反复强调「需自行测试验证」。社区实测普遍认为实验性模型的官方 Benchmark 含有水分。

---

## 参考资料

- [Best Local AI Models for Every GPU — YouTube](https://www.youtube.com/watch?v=T7WF9okSfrw)（Code Bear，2026-07-27）
- [PrismML 官网 — Bonsai 27B 开发方](https://prismml.com/)
- [PrismML Bonsai-27B-mlx-1bit — HuggingFace](https://huggingface.co/prism-ml/Bonsai-27B-mlx-1bit)
- [Bonsai 27B Review — Kaitchup](https://kaitchup.substack.com/p/bonsai-27b-review-can-a-39-gb-1-bit)
- [Reddit r/LocalLLaMA — Ternary-Bonsai 实测](https://www.reddit.com/r/LocalLLaMA/comments/1v1ya97/i_ran_ternarybonsai27b_2bit_and_bonsai27b_1bit_on/)
- [Poolside — Laguna S 2.1 介绍](https://poolside.ai/blog/introducing-laguna-s-2-1)
- [poolside/Laguna-S-2.1 — HuggingFace](https://huggingface.co/poolside/Laguna-S-2.1)
- [Nanbeige4.2-3B — HuggingFace](https://huggingface.co/Nanbeige/Nanbeige4.2-3B)
- [Reddit r/LocalLLaMA — Nanbeige 4.2-3B 实测讨论](https://www.reddit.com/r/LocalLLaMA/comments/1vayzwm/nanbeige423b_im_not_impressed/)
- [Nanbeige4-3B Technical Report — arXiv](https://arxiv.org/html/2512.06266v1)
