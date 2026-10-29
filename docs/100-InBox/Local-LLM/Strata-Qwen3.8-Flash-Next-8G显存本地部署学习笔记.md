---
title: Strata — 8~12GB 显存跑 125B 大模型的本地推理引擎
aliases: ["Niko1221/Strata", "Strata 推理引擎", "Strata 本地部署", "Strata 开源神器"]
tags:
  - local-llm
  - strata
  - qwen
  - moe
  - inference-engine
  - gguf
  - agent
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=aa-t-Jpvx70"
  - "https://github.com/Niko1221/Strata"
  - "https://www.freedidi.com/25831.html"
  - "https://huggingface.co/Qwen/Qwen3.8-Flash-Next"
  - "https://news.ycombinator.com/item?id=49953495"
author: 零度解说（频道）/ Strata by Niko1221
created: 2026-10-28
updated: 2026-10-28
description: |
  Strata（Niko1221/Strata，MIT）把 Qwen3.8-Flash-Next 主模型 125B 拆片塞进「显存 + RAM + SSD」三级存储，消费级显卡一键部署 + Agent 客户端一体化。本笔记基于零度解说视频演示 + 官方 README/docs 逐条交叉验证。
level: beginner
stars: 4
note: Tier 0 字幕被 YouTube IP 封锁，NotebookLM 认证过期未用；素材 = 用户 Content Insights（骨架）+ web_extract 直出的完整 zh-Hant 字幕（一手主体）+ freedidi 配套文章 + Strata 官方仓库 README/MODELS/HOW_IT_WORKS/TERMINAL_LAUNCH 交叉验证。视频 2026-10-26 发布，Strata v0.2.7 于次日 2026-10-27 发布——部分 UI 细节可能随版本演进。
---

# Strata — 8~12GB 显存跑 125B 大模型的本地推理引擎

> 零度解说 2026-10-26 视频介绍的开源项目 **Strata**：把 Qwen3.8-Flash-Next（主模型 125B）的权重分层分片，核心层进显存、专家层进 RAM、查找表留 SSD，让消费级电脑也能跑超大 MoE 模型，并内置 Agent 工作台、OpenAI/Anthropic 兼容 API 与 Cloudflare 隧道。本笔记以视频演示为骨架，用官方仓库逐条核验，适合想在自己旧电脑/游戏本上尝试本地大模型的读者。

## 目录

- [[#1 项目速览]]
- [[#2 核心原理：显存 + RAM + SSD 三级分片调度]]
- [[#3 「比 llama.cpp 快 4 倍」怎么看]]
- [[#4 安装与部署流程]]
- [[#5 模型生态与量化阶梯]]
- [[#6 Agent 能力与监控面板]]
- [[#7 API 串接与远程访问]]
- [[#8 硬件真实门槛：显存不是瓶颈，RAM 才是]]
- [[#9 验证与勘误记录]]
- [[#10 与既有笔记的互补]]
- [[#参考资料]]
- [[#相关笔记]]

---

## 1 项目速览

> [!info] 基本信息（2026-10-28 查询快照）
> - **仓库**：https://github.com/Niko1221/Strata
> - **Stars / Forks / Open Issues**：34,638 / 3,711 / 968
> - **创建时间**：2026-09-24（一个月冲到 34K+ stars）
> - **语言 / 协议**：C++ / MIT
> - **最新版本**：v0.2.7（2026-10-27，视频发布次日）
> - **社区热度**：HN 帖《Run Qwen 3.8 Flash Next (125B) on consumer hardware (RTX 4090) at 100T/s》940 points / 420 comments（2026-10-04）
> - **支持平台**：Windows / Linux / macOS（Apple Silicon M1+）
> - **支持显卡**：NVIDIA RTX 20/30/40/50、AMD RX 7700/7800/7900 + RX 9060/9070 + AI PRO R9700（RX 6800/6900 实验性）、Intel Arc（仅 Linux）、AMD Strix Halo

一句话定位：**llama.cpp 生态的分片调度壳 + 一键安装客户端**。它不是从零造的推理引擎——官方 HOW_IT_WORKS 明说「Built with parts of llama.cpp / ggml (MIT)」——而是在 llama.cpp/ggml 之上做了 MoE 专家分片、MTP 投机解码和 KV cache 流式化的调度层，再包了一个桌面 App。

与同类工具的定位对比：

| 维度 | Strata | 原生 llama.cpp | Ollama/LM Studio |
|------|--------|----------------|------------------|
| 核心卖点 | MoE 分片调度，小显存跑超大模型 | 通用 GGUF 推理基座 | 易用性 + 模型管理 |
| 超大 MoE（125B 级） | 显存+RAM+SSD 三级协同，官方给出各档内存表 | 全量载入为主，mmap 溢出慢一个量级 | 通常建议小模型 |
| 投机解码 | MTP 草稿层默认开启 + dflash2 drafter | 需手动配置 speculative | 少数支持 |
| 内置 Agent 工作台 | ✅ 文件/终端/联网 + 子代理 | ❌ | 部分（LM Studio 无） |
| API | OpenAI + Anthropic 兼容，localhost | OpenAI 兼容 server | OpenAI 兼容 |
| 部署形态 | 安装包 + `strata launch` CLI | 源码/预编译 | 安装包 |

---

## 2 核心原理：显存 + RAM + SSD 三级分片调度

传统跑 125B 级模型要么全塞显存（企业级卡，几十万元），要么 mmap 溢出到硬盘（每 token 都要算的权重跟不上，龟速）。Strata 的思路是按「数据被访问的频率」分级摆放（官方 HOW_IT_WORKS + MODELS.md）：

```
                 ┌─────────────────────────────────────────┐
                 │          Qwen3.8-Flash-Next 全量 180B      │
                 │   主模型 125B + 51B n-gram + 4B MTP 草稿   │
                 └─────────────────────────────────────────┘
                                    │ 分片
        ┌───────────────────────────┼──────────────────────────┐
        ▼                           ▼                          ▼
┌───────────────┐          ┌────────────────┐         ┌────────────────┐
│    GPU 显存    │          │    系统 RAM     │         │   NVMe SSD     │
│               │          │                │         │                │
│ · 注意力等核心层│          │ · shard1 专家层  │         │ · shard2 = 29GB │
│ · KV cache     │          │   （按量化档 38- │         │   n-gram 查找表 │
│   最常读部分    │          │    55GB）       │         │   （Engram）    │
│ · MTP 草稿层   │          │ · KV cache 溢出  │         │ · RAM 不足时     │
│   （约 6GB）   │          │   部分（64K+）   │         │   专家层兜底流式  │
└───────────────┘          └────────────────┘         └────────────────┘
        └─────────────── 每步推理按需取用 ───────────────────────┘
```

官方口径的三个关键机制：

1. **专家层进 RAM，查找表留 SSD**：MoE 每个 token 只激活少数专家，所以「放不进显存的专家矩阵」放 RAM 是划算的；而 n-gram 查找表（shard2，29GB）不做矩阵乘法，放 SSD 慢速读也能忍——这正是 [[Qwen3.8-Flash-Next vs GLM-5.3-Flash 64GB Mac 溢出实测]] 拆解过的「溢出什么决定快慢」原理。
2. **MTP 投机解码**：模型的 MTP（Multi-Token Prediction）草稿层一次起草最多 3 个 token，48 层主体一次前向验证，官方默认开启（`--mtp off` 可关，省约 1GB 显存但更慢）；Flash-Next 专属的 dflash2 drafter 另需下载 1.2GB。
3. **KV cache 流式化**：上下文从 64K 起，只有最常读的部分驻留 RAM，其余流式存取。

内存拟合公式（MODELS.md「Will it fit」）：**RAM ≥ 专家层体积 + ~10GB（留给 OS）**。装不下也能跑——专家层从 SSD 流式读，「works and is much slower」。

---

## 3 「比 llama.cpp 快 4 倍」怎么看

视频开场的对比演示：相同模型、相同提示词，llama.cpp 约 104 秒 vs Strata 28.8 秒，快近 4 倍。

**验证结论：⚠️ 创作者自测快照，方向可信但数字无独立复现。**

- 该对比只出现在零度视频与其配套博客（freedidi），Strata 官方文档没有这个 104s/28.8s 基准；
- 有意思的是 Strata 引擎本身基于 llama.cpp/ggml——所谓 4 倍差距来自「全量载入路径 vs 分片 + MTP 路径」，不是两套引擎的泛化差距；
- 社区侧旁证速度量级：HN 热帖标题即为「RTX 4090 at 100T/s」（940 points）；官方 DeepSeek V4.1-Flash 表格给出 RTX 3060 12GB + 46GB RAM 跑 239-274GB 模型仍有 9-11 tok/s 输出；
- 视频自己演示的实时速度：IQ3_S 档生成约 26 tok/s（24GB 显存、GPU 100%、显存占 21GB）。

判断这类「X 倍快」宣传时的检查清单：

- ✅ 看**相同量化档、相同提示词、相同上下文**的对照（视频确实做了）
- ✅ 找**官方或第三方基准表**交叉（官方 COMMUNITY_BENCHMARKS.md 有上报体系，带模型 revision 字段）
- ❌ 把「vs 原生 llama.cpp」当成「vs 所有推理引擎」——vLLM/SGLang 服务端场景是另一赛道
- ❌ 忽略 RAM/SSD 配置差异——同一引擎，RAM 差一档速度可能差数倍

---

## 4 安装与部署流程

视频演示的完整流程（桌面 App 形态）：

```
下载安装包 → 安装（自动拉取显卡对应引擎包）→ 硬件检测（汇总 RAM+VRAM）
    → 推荐量化档（第一项默认最适配）→ 选模型存放路径 → 下载分片
    → [Picture Reader 视觉层] + [MTP 压缩加速器] + [推理引擎安装]
    → 选性能配比（保留 20% 给日常软件 / 100% 全力推理）
    → 快速调优（自动试几组引擎参数，保留最快的一组）→ 进入使用
```

体积勘误（Insights 与实际不符，见 [[#9 验证与勘误记录]]）：

| 项目 | 数值 | 来源 |
|------|------|------|
| Windows 安装包 | 107.3 MB | GitHub release 资产 + 视频口播「100多M」 |
| Linux AppImage / macOS dmg | 278.0 MB / 160.2 MB | 同上 |
| 安装后占用 | 约 560 MB | 视频口播「560多M空間」 |
| 引擎包 | 每显卡厂商一份（CUDA 12/13、HIP rdna2/rdna3/rx9070/rx6000/halo、SYCL、Metal），单包 5MB-890MB | release 资产清单 |

桌面 App 之外还有终端形态（官方 README 一等公民）：

```bash
# Linux / macOS 安装
curl -fsSL https://github.com/Niko1221/Strata/releases/latest/download/install.sh -o install.sh && sh install.sh

# Windows PowerShell
irm https://github.com/Niko1221/Strata/releases/latest/download/install.ps1 | iex

# 交互式启动（引导下载 + 选模型）
strata launch

# 典型组合：指定量化档 / 128K 上下文 / 4 并发 / 关视觉 / 快速调优
strata launch --family qwen --model iq3_xxs --context 128k --parallel 4 --vision off --tune quick

# Swift 1.5（思考更短的微调版）
strata launch --family swift --model IQ2_XS

# DeepSeek V4.1-Flash（独立引擎）
strata launch --family deepseek41 --model ist-das-2.00bpw --plan
```

最佳实践：

- ✅ 首次用 `strata launch` 交互模式，让硬件检测自己选档，再抄下它给的参数固化成脚本
- ✅ 模型默认装 C 盘，83.6GB 的 IQ3_S 建议手动改到大盘（视频也提醒了）
- ✅ 日常挂机选「保留 20% 性能」，跑长任务再切 100%
- ❌ 不要把模型放机械硬盘——专家层流式读 NVMe 是「能跑」，HDD 是「跑不动」
- ❌ 载入引擎阶段长时间等待不是卡死（视频原话「不要以为是卡住了」）

---

## 5 模型生态与量化阶梯

Strata 不只一个模型。官方当前文档化的三大家族 + 社区变体：

| 模型 | 定位 | 最低档内存需求 | 备注 |
|------|------|----------------|------|
| **Qwen3.8-Flash-Next** | 主推，125B 级 MoE + 视觉 | Q2_0 档 RAM+VRAM 37.6GB | 官方 HuggingFace 权重，架构 `qwen4_exp` |
| **Qwen3.6-35B-A3B** | 小一档 | 32-52GB RAM | Unsloth UD-Q4_K_XL / Q6 / Q8 |
| **DeepSeek V4.1-Flash-1017** | 超大档 | 12GB 显存 + 46GB RAM + 257GB 磁盘 | 独立引擎，239-274GB 权重集 |
| Swift 1.5（UkisAI 微调） | 思考更短、速度同档 | 同原版同尺寸 | `--family swift` |
| Coder（ISTA-DASLab 砍半专家） | 纯编码 | ~32GB RAM（IQ1_M） | 保留 512 专家中的 256 个，作者自报 91% SWE-bench Verified / 99% LiveCodeBench |

Flash-Next 官方「Pick by memory」表（ISTA-DASLab 原生包，数字 = 权重占 RAM+VRAM）：

| 量化档 | RAM+VRAM | 速度 | 质量 |
|--------|----------|------|------|
| Q2_0 | 37.6 GB | 最快 | good |
| IQ2_XS | 39.2 GB | fast | better |
| IQ3_XXS | 47.0 GB | slower | great |
| **IQ3_S** | **54.8 GB** | 最慢 | best；官方称公开测试上追平全精度 top token |
| （参考）IQ3_S vs Q8_0 | — | — | top token 命中率 87.7% |

内存充足的升级路线（Unsloth UD 系列，专家全进 RAM）：

```
UD-IQ4_XS  下载 94GB / 专家 55GiB / RAM ~72GB / 88.9% ← 装机默认首推
UD-Q4_K_XL 111GB / 72GiB / ~88GB / 93.1%
UD-Q5_K_XL 158GB / 92GiB / ~108GB / 94.6%
UD-Q6_K_XL 169GB / 102GiB / ~118GB / 94.6%
Q8_0       188GB / 120GiB / ~136GB / 100%（基准）
```

选型决策树（按你的机器）：

```
你的 RAM+VRAM 总量？
├─ < 40GB ──────→ 别硬上 Flash-Next，选 Qwen3.6-35B-A3B（32GB 起）
├─ 40-48GB ─────→ IQ2_XS / Q2_0，或 Coder IQ1_M（纯写码，~32GB RAM）
├─ 48-56GB ─────→ IQ3_XXS～IQ3_S（视频主角档：下载 83.6GB、运行 54.8GB）
├─ 56-72GB ─────→ IQ3_S 舒适区 + 开视觉 + 128K 上下文
└─ 72GB+ ───────→ Unsloth UD-IQ4_XS 起步，追质量上 Q4_K_XL/Q6

RAM 略低于所选档位？→ 官方允许专家层落 SSD 流式（慢得多），仅建议应急
```

⚠️ 视频说「IQ3_S 83.6GB」是**下载体积**；「装不装得下」要看 **RAM+VRAM ≥ 54.8GB** 这一行。视频作者的 64GB RAM + 24GB 显存 = 88GB 总量，属于 IQ3_S 舒适区，但不能反推「83GB 文件需要 88GB 内存」。

---

## 6 Agent 能力与监控面板

视频下半场演示了桌面 App 内置的 Agent 工作台（⚠️ 此部分以视频 UI 演示为准，官方 README 只到「Chat, files and a live preview in one window」的程度，未文档化子代理机制）：

- **系统能力**：自我介绍即声明可操作文件系统、执行终端命令、联网查询（模型本身的行为，在完全授权下）
- **权限三档**：手动（写操作逐次确认）/ 计划 / 完全授权——视频为省时间直接开满
- **多智能体**：实测提示词「编写雪地 3D FPS 射击游戏，枪声爆炸声要真实，枪支必须真实贴图」，主 Agent 自主派生一个子 Agent 专门画 4 把枪的贴图，自己同时写游戏主体；约 40 分钟完成，自报耗 300 万 token（全免费、本地）、35% 上下文
- **成品验收**：视频里试玩雪地场景 FPS——有枪声/爆炸声/换弹/切枪（含霰弹枪）/投掷手榴弹，一次生成直接可玩
- **实时监控面板**：生成速度（视频实测约 26 tok/s）、GPU 负载 100%、显存占用约 21GB、温度、磁盘读取、CPU、PCIe 读写

最佳实践（含视频作者自己的建议）：

- ✅ 首次跑复杂任务用手动档，确认操作模式无误后再开完全授权——大模型拿到终端 + 文件 + 网络的完全权限，翻车成本是真实世界的
- ✅ 低配机器把「思考预算」（上下文长度）从默认不限制改小
- ✅ 看监控面板的显存水位决定要不要降一档量化
- ❌ 不要用完全授权模式跑来路不明的提示词

---

## 7 API 串接与远程访问

Strata 在 localhost 同时开 **OpenAI 兼容 + Anthropic 兼容** API，可设 API key。官方 `strata connect` 直接打印各工具的接法，README 点名支持：

```
Claude Code · Codex CLI · OpenCode · oh-my-pi · pi.dev · Hermes Agent · OpenClaw
```

（视频口播的「对接 Claude Code 或者 DeepSeek Harness」即此能力；对 Hermes Agent 用户来说，把本地 Strata 端点指过去即可当免费后端。）

访问范围四档（`--access` 参数，视频只演示了 Cloudflare 一种）：

| 档位 | 范围 | 备注 |
|------|------|------|
| `local` | 仅 127.0.0.1 | 默认，最安全 |
| `lan` | 局域网 + key | 家里其他设备可用 |
| `cloudflare` | 公网，Cloudflare quick tunnel | **需自行安装 cloudflared**，视频未提此前置 |
| `tailscale` | 走 Tailscale 网络 | 视频完全没提，其实是更省心的外网方案 |

```bash
# 一条命令把本地模型变成有公网入口的 API（前提：已装 cloudflared）
strata launch --family qwen --model iq3_xxs --access cloudflare
```

- ✅ 只在本机用就保持 `local`，API key 照设
- ✅ 外网访问优先考虑 Tailscale（组网即私有，不暴露公网入口）
- ❌ Cloudflare quick tunnel 模式下关掉 key（`--no-api-key`）官方会加强警告——等于把模型裸奔到公网

---

## 8 硬件真实门槛：显存不是瓶颈，RAM 才是

视频标题主打「8~12G 显存」，官方与社区口径可以对上，但要完整理解「能跑」的构成：

| 硬件项 | Flash-Next IQ3_S 档实际需求 | 说明 |
|--------|-----------------------------|------|
| 显存 | 8GB 可跑（HN/仓库描述口径），视频机 24GB | 视频实测 IQ3_S 运行时显存占 ~21GB——8GB 机器上是核心层+KV 的精简布局 |
| RAM | **≥ 54.8GB（含显存摊派）**，即 48-64GB 物理内存 | 官方公式：RAM ≥ 专家层 + ~10GB |
| 硬盘 | 下载 83.6GB + 余量，NVMe 必要 | 机械盘专家流式读不可用 |
| CPU | 无特殊门槛 | 推理主力在 GPU+RAM 通路 |

官方 DeepSeek V4.1-Flash 实测表（同一引擎家族的速度参照，官方数字）：

| 硬件 | 输出速度 | Prompt 处理 |
|------|----------|-------------|
| RTX 3060 12GB + 46GB RAM | 9-11 tok/s | ~155 tok/s (4K) |
| RTX 5070 12GB + 64GB RAM | 4-6 tok/s | ~110 tok/s (4K) |
| Ryzen AI Max+ 395（128GB 统一内存） | 19-21 tok/s（码）/ 11（聊） | 87-125 tok/s |
| 4x Radeon AI PRO R9700 | 74-77 tok/s | 400-450 tok/s (8K) |

结论：**「老电脑起飞」有前提**——显存 8GB 只是入场券，48GB+ RAM 和 NVMe 才是主体门槛。视频里「几千元老旧电脑」的说法要打折听：64GB RAM + 24GB 显存（大概率 3090/4090 级）在二手市场远不止几千元。

---

## 9 验证与勘误记录

| 视频声称 | 验证结果 | 来源 |
|----------|----------|------|
| 「Strata 开源神器」 | ✅ MIT 协议 | GitHub API license 字段 |
| 「125B 大模型」 | ✅ 主模型口径成立：125B 主干 + 51B n-gram(Engram) + 4B MTP = 180B 全量，HF safetensors 计 179,999,981,459 完全吻合 | HF API + vault 既有笔记（vLLM recipes/unsloth 多源） |
| 「最新发布的千问 3.8 Flash Next」 | ✅ `Qwen/Qwen3.8-Flash-Next` 官方 org 权重，335 万下载；架构 `Qwen4ExpForConditionalGeneration`（qwen4_exp） | HF API + config.json |
| 「llama.cpp 104s vs Strata 28.8s，快近 4 倍」 | ⚠️ 创作者自测快照（视频+博客自报），官方文档无此基准；且 Strata 引擎本身基于 llama.cpp/ggml，对比对象是原生全量载入路径 | freedidi 博客 + HOW_IT_WORKS.md |
| Content Insights 称「安装档约 500 多 MB」 | ❌ 混淆：安装包 107.3MB（口播「100多M」），安装后占用约 560MB（口播「560多M空間」） | GitHub release 资产 + 字幕 |
| 「IQ3_S 量化版 83.6G」 | ✅ 下载体积属实（ISTA-DASLab 原生包）；运行门槛另计：RAM+VRAM 54.8GB | MODELS.md Pick by memory |
| 「Swift 1.5，2-bit 压缩微调，思考更快、质量略低」 | ✅ 定位属实：UkisAI 微调（GSQ-RCO 格式、IQ2_XS 档）、官方称「thinks much shorter, about the same quality」；「68GB」具体数字官方未写，按档位推算吻合 | MODELS.md §Swift 1.5 |
| 「支持 N 卡、A 卡、I 卡 + Mac」 | ✅ 但 Intel Arc 仅 Linux 且视觉默认受限制（B580/B70 需手动开）；macOS 走 Metal（Apple Silicon M1+） | README Hardware 表 |
| 「MTP 压缩 = 模型加速器」 | ✅ `--mtp` 默认开启，草稿层约 6GB，一次起草 3 token；另有 Flash-Next 专属 dflash2 drafter（1.2GB，视频未提） | HOW_IT_WORKS + TERMINAL_LAUNCH |
| 「Picture Reader = 视觉模型」 | ✅ mmproj-BF16.gguf 约 1GB，默认开启，`--vision off` 可关 | MODELS.md |
| 「支持 Cloudflare 隧道」 | ✅ `--access cloudflare` quick tunnel，需自装 cloudflared（视频未提此前置）；另有 Tailscale 档视频未提 | TERMINAL_LAUNCH.md |
| 「Agent 子代理 + 手动/计划/完全三档权限」 | ⚠️ 桌面 App 演示可信，但官方 README/docs 未文档化子代理细节，以视频 UI 为准 | 字幕 + README |
| 「8~12G 显存就能跑」 | ⚠️ 显存门槛属实，但完整门槛 = 显存 + 48GB+ RAM + NVMe；RAM 不足落 SSD 官方明言「much slower」 | MODELS.md Will it fit |
| 「300 万 token、35% 上下文、完全免费」 | ❓ 视频自报，无独立出处 | 字幕 |
| 「国外网友都玩疯了」 | ✅ HN 帖 940 points / 420 comments（2026-10-04）；repo 一个月 34.6K stars | Algolia HN API + GitHub API |

---

## 10 与既有笔记的互补

vault 里已有 5 篇 Qwen3.8 系笔记，覆盖模型本身与高配实测，本笔记的差异化角度是**引擎工具侧 + 低配消费级部署**：

- [[Qwen3.8-Flash-Next vs GLM-5.3-Flash 64GB Mac 溢出实测]] — 模型侧：为什么 Flash-Next 溢出快（MoE 专家 vs n-gram 查表），Mac/MLX 视角；本笔记的 §2 分片调度正是 Strata 对同一原理的工程化实现，两篇互为印证
- [[NInfer-Qwen3.8-27B-单卡4090-256K上下文-1.5倍速实战笔记]] — 单卡 4090 高配路线（NInfer 引擎），与本篇的低配分片路线是同一模型的两种预算方案
- [[Mac-mini-M6-Qwen3.8-27B-本地推理实测笔记]] — Apple Silicon 路线；Strata 的 macOS Metal 后端可作对照实验
- [[Qwen3.8-27B 回归 Dense 实战 - vLLM 调参、四模型对比与 Hermes 蜂群架构]] — 服务端 vLLM 路线 + Hermes 蜂群；本篇的 `strata connect` 官方适配列表含 Hermes Agent，可把 Strata 当蜂群的本地免费后端

---

## 参考资料

- [零度解说：太爽了！8~12G 显存就能跑 125B 大模型？（视频，2026-10-26）](https://www.youtube.com/watch?v=aa-t-Jpvx70)
- [零度博客配套文章：老旧电脑也能跑 1250 亿参数大模型？](https://www.freedidi.com/25831.html)
- [Niko1221/Strata — GitHub 仓库（MIT，v0.2.7）](https://github.com/Niko1221/Strata)
- [Qwen/Qwen3.8-Flash-Next — HuggingFace 官方权重](https://huggingface.co/Qwen/Qwen3.8-Flash-Next)
- [HN：Run Qwen 3.8 Flash Next (125B) on consumer hardware (RTX 4090) at 100T/s（940 pts）](https://news.ycombinator.com/item?id=49953495)
- [ISTA-DASLab/Qwen3.8-Flash-Next-GSQ-RCO-GGUF — 官方推荐量化包](https://huggingface.co/ISTA-DASLab/Qwen3.8-Flash-Next-GSQ-RCO-GGUF)
- [ukisai/Swift-1.5-Qwen3.8-Flash-Next-GSQ-RCO-GGUF — 思考更短的微调版](https://huggingface.co/ukisai/Swift-1.5-Qwen3.8-Flash-Next-GSQ-RCO-GGUF)

## 相关笔记

- [[Qwen3.8-Flash-Next vs GLM-5.3-Flash 64GB Mac 溢出实测]]
- [[NInfer-Qwen3.8-27B-单卡4090-256K上下文-1.5倍速实战笔记]]
- [[Mac-mini-M6-Qwen3.8-27B-本地推理实测笔记]]
- [[Qwen3.8-27B 回归 Dense 实战 - vLLM 调参、四模型对比与 Hermes 蜂群架构]]

---

*文档生成时间：2026-10-28*
*基于 Strata v0.2.7（2026-10-27）官方文档与零度解说视频（2026-10-26）*
