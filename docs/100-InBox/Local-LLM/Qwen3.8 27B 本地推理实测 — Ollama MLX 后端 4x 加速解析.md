---
title: Qwen3.8 27B 本地推理实测 — Ollama MLX 后端 4x 加速解析
aliases: [Qwen 3.8 Ollama 加速, MTPLX vs oMLX vs Ollama]
tags:
  - local-llm
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=hcNa6udDmzY"
  - "https://ollama.com/blog/mlx"
  - "https://github.com/youssofal/MTPLX"
author: Execute Automation (Karthik KK)
created: 2026-08-21
updated: 2026-08-21
description: 在 Apple M5 Max 上实测 Qwen3.8 27B（4-bit MLX）于 LM Studio / oMLX / MTPLX / Ollama 四框架的推理速度，Ollama 新 MLX 后端零配置达到最高速度，附投机解码与原生 MTP 原理分析
level: intermediate
stars: 3
---

# Qwen3.8 27B 本地推理实测 — Ollama MLX 后端 4x 加速解析

> Execute Automation 频道在 Apple M5 Max 上对比四个本地推理框架跑 Qwen3.8 27B（4-bit MLX 格式）的 tokens/sec 表现：LM Studio 约 27-28，oMLX+DFlash2 约 33，MTPLX 约 54 但质量下降，Ollama 新 MLX 后端零配置 47-70+。核心结论：**框架对底层硬件与模型特性的调度整合度，比手动加投机解码配置更能决定速度**。

## 目录

- [[#一、测试环境与模型规格]]
- [[#二、四框架实测数据总览]]
- [[#三、Coding Agent 实战压测]]
- [[#四、技术原理：投机解码 vs 原生 MTP]]
- [[#五、Ollama 为什么快：MLX 后端机制]]
- [[#六、框架选型建议]]
- [[#七、质疑与注意事项]]
- [[#参考资料]]

---

## 一、测试环境与模型规格

### 硬件与模型

| 项目 | 配置 |
|------|------|
| 芯片 | Apple M5 Max |
| 模型 | Qwen3.8 27B（Qwen 3.8 27 billion parameter） |
| 量化 | 4-bit（NVFP4，MLX 格式） |
| 为什么选 4-bit | 消费级硬件能装下的最大精度档位，最贴近一般用户场景 |
| 格式 | MLX（非 GGUF） |

### MLX vs GGUF 格式选择

| 维度 | MLX | GGUF |
|------|-----|------|
| 适配对象 | Apple Silicon 原生 | 跨平台通用（llama.cpp 系） |
| 统一内存利用 | 原生优化，直接受益于 Unified Memory | 通过 llama.cpp 间接映射 |
| Apple 芯片性能 | 同模型同量化下更优 | 略逊 |
| 生态 | MLX / oMLX / MTPLX / 新版 Ollama | LM Studio / 旧版 Ollama / llama.cpp |

> 结论：Apple Silicon 用户跑本地模型，优先选 MLX 格式权重；GGUF 是跨平台兼容的选择，不是 Mac 上的性能选择。

### 代码示例：复现测试环境

```bash
# Ollama（0.19+，MLX 后端预览版）
ollama run <qwen3.8-27b-mlx-model-tag>

# verbose 模式看真实 tokens/sec（视频中用此方法取证）
ollama run <model-tag> --verbose

# pi coding agent 通过 Ollama 启动
ollama launch pi --model <qwen3.8-27b-mlx-model-tag>
```

---

## 二、四框架实测数据总览

### 核心对比表（简单对话生成）

| 框架 | 加速机制 | tokens/sec | 相对 LM Studio | 配置成本 | 输出质量 |
|------|----------|-----------|---------------|---------|---------|
| LM Studio | 无（基线） | 27.5 - 28 | 1.0x | 零 | 基线 |
| oMLX + DFlash2 | 外挂 Drafter Model 投机解码 | 32.9 - 33 | ~1.2x | 需手动指定 drafter | 正常 |
| MTPLX | 调用模型自带 MTP Head | ~54 | ~1.9x | 低 | **实测下降**（语义完整度、稳定度问题） |
| Ollama（GUI 对话） | MLX 后端自动调度 | ~70 | ~2.5x | **零** | 正常 |
| Ollama（CLI verbose） | 同上 | 47 - 50+（首轮 31，预热后 50） | ~1.7-1.8x | **零** | 正常 |

### 数据可视化

```
tokens/sec（M5 Max · Qwen3.8 27B · 4-bit MLX · 简单对话）

LM Studio      ████████████████▏               28
oMLX+DFlash2   ███████████████████▎            33
MTPLX          ██████████████████████████████▌ 54  ← 质量下降
Ollama (CLI)   ████████████████████████████    50
Ollama (GUI)   ████████████████████████████████████▋ 70
               └────────── 每格 ≈ 2 tok/s ──────────┘
```

### 逐框架细节

**LM Studio [02:25]**
- 未启用任何加速，稳定 27.47 → 28 tok/s
- "从发布第一天起就是这个速度"——基线参照

**oMLX + DFlash2 [03:38]**
- DFlash2 是 Qwen3.8 27B 专属的 Drafter Model（投机解码辅助小模型）
- 需在 oMLX 设置中手动指定：Qwen 3.8 27B DFlash2
- 27-28 → 32-33 tok/s，提升约 18%，幅度有限
- 代价：显存/内存中常驻第二个模型

**MTPLX [05:08]**
- 官方定位：`mtplx.com`，"drafter is the target's own MTP head"——不加载第二个模型
- 实测 54 tok/s，约为 LM Studio 两倍
- **但输出质量不佳** [06:05]：视频作者明确说 "the quality of the output for this particular model was not that amazing"，不适合他的用例
- 交叉验证：Andrew Zhu 在 M4 Max 实测 MTPLX 比 MLX 快 2.04x，与视频数据吻合

**Ollama [07:13]**
- GUI 对话约 70 tok/s；CLI verbose 模式下首轮（冷加载）31 → 预热后 50 tok/s
- 写 Selenium C# 代码任务 47 tok/s（随负载波动）
- **全程零配置**：未启用 drafter、未开启任何 draft token / MTP 开关

---

## 三、Coding Agent 实战压测

真实 Agent 工作负载（pi coding agent + "What is this code repo" 代码库解析任务）才是拉开差距的地方——长上下文、多轮对话、高 prefill 压力。

### oMLX vs Ollama（pi coding agent，同一模型）

| 场景 | oMLX 后端 | Ollama 后端 | 差距 |
|------|----------|------------|------|
| 首轮（模型加载） | 24 tok/s | ~60 tok/s | 2.5x |
| "What is this code repo" 代码库解析 | **17 tok/s** | **46 tok/s**（瞬时 100+） | **2.7x** |

### 为什么 Agent 负载下差距更大

```
简单对话：                          Agent 任务（代码库解析）：
prompt 短，decode 占主导            prompt 数万 token，prefill 占主导
                                    ┌──────────────────────────┐
LM Studio  28 ──► 差距小 ◄── 33     │ 长上下文 KV cache 管理     │
                                    │ 多轮分支的 cache 复用      │
                                    │ 分支切换时的 cache 重建     │
                                    └──────────────────────────┘
oMLX:      17 tok/s（跌 50%）       Ollama: 46-60 tok/s（几乎不跌）
```

关键观察：oMLX 在 Agent 负载下从 33 跌到 17（-50%），Ollama 从 50+ 只降到 46-60（基本持平）。**Agent 场景的性能瓶颈在 prefill/cache 管理，不在 decode**——这正是 Ollama 0.19 缓存升级针对的地方（见第五节）。

> 视频标题 "4x FASTER" 的来源：17 → 60+（首轮）确实接近 4x；稳定态对比是 17 → 46 约 2.7x。标题取的是最佳情况。

---

## 四、技术原理：投机解码 vs 原生 MTP

### 自回归解码（为什么 LLM 慢）

LLM 默认逐 token 生成：预测 1 个 → 喂回去 → 再预测 1 个。每步都要跑一遍完整前向计算，decode 速度受内存带宽限制。

### 两条加速路线

```
路线 A：外挂式投机解码（Speculative Decoding，oMLX + DFlash2）
┌─────────────┐  草拟 K 个 token  ┌─────────────────┐
│ Drafter 小模型│ ───────────────► │ 目标大模型        │
│ (DFlash2)    │                  │ (Qwen3.8 27B)    │
└─────────────┘                  └────────┬────────┘
                                          │ 一次性批量验证 K 个
     代价：内存常驻第二个模型                ▼
     收益：验证比逐个生成快              接受/拒绝 → 输出
     （命中率低时反而更慢）

路线 B：原生 MTP（Multi-Token Prediction，MTPLX / Ollama 调度）
┌───────────────────────────────┐
│ 目标大模型 (Qwen3.8 27B)        │
│  ┌──────────┐  ┌────────────┐ │   模型自带 MTP Head（训练时内置）
│  │ 主干网络   │─►│ MTP Head   │─┼──► 一次草拟 + 批量验证 K 个 token
│  └──────────┘  └────────────┘ │    无需第二个模型、不占额外内存
└───────────────────────────────┘
```

### 三种解码方式对比

| 维度 | 纯自回归 | 外挂投机解码 | 原生 MTP |
|------|---------|-------------|---------|
| 额外内存 | 无 | 第二个模型常驻 | **无**（head 内置） |
| 需要配置 | 无 | 手动指定 drafter | 取决于框架调度 |
| 理论加速 | 1x | 1.2-2x（受命中率制约） | 最高 2-2.25x |
| 输出质量 | 基线 | 与基线一致（验证保证） | 取决于 MTP head 训练质量 |
| Qwen3.8 支持情况 | 所有框架 | oMLX + DFlash2 | MTPLX（质量不稳）/ Ollama（自动） |

> 注意：投机解码的验证机制理论上保证输出分布与基线一致；MTPLX 的质量问题说明**原生 MTP 路线中 head 的草拟质量直接暴露在输出里**（视频实测如此，官方宣称 "preserving the quality"，两者矛盾——以实测为准，等待版本迭代）。

---

## 五、Ollama 为什么快：MLX 后端机制

视频作者的归因是猜测（"I think it's all running within the MTP enabled inside the same exact model"）。官方博客（2026-03-30）给出了确切答案，加速来自三个层面的整合：

### Ollama 0.19 MLX 后端（Apple Silicon preview）

1. **MLX 引擎替换 llama.cpp**：Apple Silicon 上直接构建于 Apple 的 MLX 框架，原生利用统一内存架构
2. **M5 系 GPU Neural Accelerators**：在 M5 / M5 Pro / M5 Max 上利用新硬件加速器，同时提升 TTFT（Time To First Token）和生成速度
3. **NVFP4 量化格式**：NVIDIA 的 4-bit 浮点格式，减少内存带宽占用，保持模型精度（生产环境同格式，结果可对齐）
4. **缓存系统重构**（对 Agent 场景最关键）：
   - 跨对话复用 cache → 共享 system prompt 时（Claude Code / pi 这类 agent 都是）更多 cache 命中、更低内存占用
   - 智能检查点（intelligent checkpoints）→ prompt 中自动存快照，减少重复 prompt 处理
   - 更聪明的淘汰策略 → 共享前缀存活更久

```
为什么"0 Config Changes"也能快：

用户视角          Ollama 0.19 内部（自动发生）
─────────        ─────────────────────────────
ollama run x  ─► MLX 引擎（非 llama.cpp）
  (什么都没配)      ├─► NVFP4 权重 → 低带宽占用
                   ├─► M5 Max 神经加速器 → TTFT + decode
                   └─► Agent cache 复用 → prefill 大幅减免
                        
官方数据（Qwen3.5-35B-A3B · NVFP4 · M5）：
  prefill 1851 tok/s · decode 134 tok/s
```

> 视频实测 Ollama 47-70 tok/s（27B 模型）与官方 134 tok/s（35B-A3B MoE 模型，激活参数少）不矛盾——不同模型架构、不同量化、不同任务负载。

---

## 六、框架选型建议

### 判断决策树

```
你在 Apple Silicon 上跑 Qwen3.8 / MTP 架构模型：

需要 Coding Agent / 长上下文多轮任务？
├─ 是 ─► Ollama（0.19+ MLX 后端）
│        零配置、Agent 负载下几乎不降速
│        └─ pi agent: ollama launch pi --model <tag>
│
└─ 只做单轮对话 / 批处理？
   ├─ 要最大速度且能接受质量风险 ─► MTPLX（等版本修复质量后转生产）
   └─ 要稳定质量 ─► Ollama（GUI/CLI）或 LM Studio（不追求速度时）
```

### 最佳实践清单

- ✅ Apple Silicon 优先下载 MLX 格式权重，不用 GGUF
- ✅ 用 `ollama run <model> --verbose` 取证真实 tokens/sec（GUI 看不到细节）
- ✅ Agent 后端选 Ollama，直接吃 cache 复用红利
- ✅ 统一内存 32GB+ 才考虑 27B 级别模型（官方对 35B 的建议，27B 4-bit 略低）
- ❌ 不要同时启用 DFlash 和原生 MTP（两者是互斥的投机解码引擎）
- ❌ MTPLX 的速度数字好看，但质量未稳前别用于生产
- ❌ 别拿 GUI 显示的峰值 tok/s 当基准——冷加载、预热、负载类型都会大幅影响数字

---

## 七、质疑与注意事项

| 视频说法 | 交叉验证结果 |
|---------|-------------|
| "4x FASTER" | 稳定态 2.7x（17→46），首轮接近 4x（17→60）。标题取最佳情况 |
| "I think MTP is enabled inside the model"（作者自述是猜测） | 官方博客未提及自动 MTP；加速来自 MLX 引擎 + NVFP4 + M5 加速器 + cache 重构。作者本人也不确定 |
| Ollama 零配置即最快 | 属实，且官方明确针对 coding agent（Pi、Claude Code、OpenClaw）优化 |
| MTPLX 速度翻倍但质量下降 | 与 Andrew Zhu M4 Max 实测（2.04x）速度吻合；质量问题是独立发现 |
| 单机样本 | 仅 M5 Max 一台机器、无多次取样、无标准 benchmark 协议。数字看趋势，别当精确基准 |

**pi coding agent 补充**：视频中的 "pie coding agent" 是 Mario Zechner（badlogic，libGDX 作者）的 pi（pi.dev，现归 earendil-works），极简终端 coding agent，Ollama 官方博客点名适配（`ollama launch pi`）。

---

## 参考资料

- [QWEN3.8 is 4x FASTER with 0 Config Changes ⚡️ — Execute Automation](https://www.youtube.com/watch?v=hcNa6udDmzY)
- [Ollama is now powered by MLX on Apple Silicon in preview — Ollama Blog](https://ollama.com/blog/mlx)
- [youssofal/MTPLX — GitHub](https://github.com/youssofal/MTPLX)
- [MTPLX Is 2.04× Faster Than MLX — But Is It Really Usable? — Andrew Zhu](https://blog.gopenai.com/mtplx-is-2-04-faster-than-mlx-but-is-it-really-usable-519621f718fd)
- [MLX vs oMLX vs MTPLX: Apple Silicon's LLM Stack Explained — Towards AI](https://pub.towardsai.net/mlx-vs-omlx-vs-mtplx-apple-silicons-llm-stack-explained-6d1fec6c73c3)
- [pi coding agent — Mario Zechner](https://mariozechner.at/posts/2025-11-30-pi-coding-agent/)

## 相关笔记

- [[本地 AI 模型 - GPU 显存分级选型指南]]
- [[TurboFieldfare - 8GB Mac 跑通 26B 大模型的端侧 AI 革命]]
- [[本周热门开发工具项目汇总 - AI Agent 与基础设施]]

---

*笔记生成时间：2026-08-21，基于 Execute Automation 2026-08-19 视频 + 官方博客交叉验证*
