---
title: OpenCode + NVIDIA Build：免费使用顶级 AI 编码模型
aliases: [OpenCode NVIDIA, NVIDIA NIM 免费 API, MiniMax M3 Nemotron GLM 免费编码]
tags:
  - ai-coding
  - opencode
  - nvidia-nim
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=G7dSIip5jF4"
  - "https://opencode.ai/docs"
  - "https://build.nvidia.com/models"
author: AI Stack Engineer
created: 2026-06-19
updated: 2026-06-19
description: 通过 OpenCode 连接 NVIDIA Build 平台，免费使用 MiniMax M3、Nemotron 3 Ultra、GLM 5.1 等顶级 AI 编码模型
level: beginner
stars: 4
note: 无字幕，基于视频元数据 + 官方文档 + 社区资源综合整理
---

# OpenCode + NVIDIA Build：免费使用顶级 AI 编码模型

> 把 **OpenCode**（开源终端 AI 编码 agent）连接到 **NVIDIA Build** 平台的免费推理端点（NIM API），无需 GPU、无需信用卡，即可使用 MiniMax M3、Nemotron 3 Ultra、GLM 5.1 等顶级编码模型。

## 目录

- [[#什么是 OpenCode]]
- [[#为什么选 OpenCode]]
- [[#NVIDIA NIM / Build 是什么]]
- [[#免费可用模型清单]]
- [[#配置完整流程]]
- [[#Free Tier 限制与注意事项]]
- [[#对比：OpenCode + NVIDIA vs 其他方案]]
- [[#常见问题]]
- [[#参考资料]]

---

## 什么是 OpenCode

**OpenCode** 是一个开源 AI 编码 agent，由 Anomaly（[anoma.ly](https://anoma.ly)）开发。可以理解为「开源版 Claude Code」。

### 三种使用形态

| 形态 | 说明 |
|------|------|
| TUI（Terminal UI） | 终端界面，主要形态，类似 Claude Code |
| Desktop App | 桌面应用 |
| IDE Extension | 编辑器扩展 |

### 核心功能

```
┌─────────────────── OpenCode TUI ───────────────────┐
│                                                     │
│  /connect    → 配置 LLM provider + API key         │
│  /init       → 初始化项目，生成 AGENTS.md           │
│  /models     → 切换模型                             │
│  /undo       → 撤销上次修改（可连续多次）            │
│  /redo       → 恢复撤销                             │
│  /share      → 生成对话分享链接                     │
│  @file       → 模糊搜索文件并引用                   │
│  Tab         → Plan / Build 模式切换                 │
│                                                     │
│  Plan 模式  → AI 只建议不修改，像带新人一样沟通      │
│  Build 模式 → AI 直接执行修改                       │
│                                                     │
└─────────────────────────────────────────────────────┘
```

### 安装

```bash
# 方式 1：一键脚本（推荐）
curl -fsSL https://opencode.ai/install | bash

# 方式 2：npm
npm install -g opencode-ai

# 方式 3：Homebrew（注意用社区 tap，更新更快）
brew install anomalyco/tap/opencode

# 方式 4：Docker
docker run -it --rm ghcr.io/anomalyco/opencode
```

---

## 为什么选 OpenCode

视频提到开发者正在转向 OpenCode，核心原因：

| 特性 | OpenCode | Claude Code | Cursor |
|------|----------|-------------|--------|
| 开源 | ✅ 完全开源 | ❌ 闭源 | ❌ 闭源 |
| 厂商锁定 | ❌ 支持 75+ provider | ✅ 只能用 Anthropic | ✅ 内置 provider |
| 免费模型 | ✅ NVIDIA NIM / 本地模型 | ❌ 需 Anthropic API | ❌ 订阅制 |
| 项目上下文 | `AGENTS.md` | `CLAUDE.md` | `.cursorrules` |
| Plan/Build 模式 | ✅ Tab 切换 | ✅ | ✅ |
| IDE 集成 | ✅ 扩展 | ✅ | ✅ 原生 |
| 本地模型 | ✅ 支持 | ❌ | ❌ |

**关键优势**：不锁定任何 LLM provider，可以自由接入 NVIDIA 免费端点、OpenRouter、本地 Ollama 等。

---

## NVIDIA NIM / Build 是什么

### 概念图

```
┌─────────────────────────────────────────────────────────┐
│                   build.nvidia.com                       │
│                                                          │
│  NVIDIA Developer Program 会员免费使用                    │
│                                                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐              │
│  │ MiniMax  │  │ Nemotron │  │ GLM 5.1  │  ...77 models │
│  │   M3     │  │ 3 Ultra  │  │          │              │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘              │
│       │              │              │                     │
│       └──────────────┴──────────────┘                     │
│                      │                                    │
│              OpenAI-compatible API                        │
│              (integrate.api.nvidia.com/v1)                │
└──────────────────────┬───────────────────────────────────┘
                       │
                       │  HTTP POST /chat/completions
                       │  Bearer nvapi-xxxxx
                       │
          ┌────────────┴────────────┐
          │     你的 OpenCode        │
          │   (自定义 provider)      │
          └─────────────────────────┘
```

### 关键特点

- **NIM（NVIDIA Inference Microservices）**：NVIDIA 的推理微服务平台
- **OpenAI 兼容 API**：`https://integrate.api.nvidia.com/v1`，任何支持 OpenAI SDK 的工具都能接入
- **77 个 Free Endpoint 模型**：无需 GPU、无需信用卡
- **已废弃信用积分制**：现在改为速率限制（rate limit）模式
- **可下载容器**：105 个模型支持本地部署（Docker NIM 容器）

---

## 免费可用模型清单

### 视频重点推荐

| 模型 | 发布方 | 参数规模 | 强项 | 热度（使用次数） |
|------|--------|---------|------|-----------------|
| **MiniMax M3** | MiniMax | MoE 多模态 | 推理 + 编码 + 工具调用 | 770K（4 天前上线） |
| **Nemotron 3 Ultra** | NVIDIA | 550B（55B active） | Agentic reasoning, 1M context | 7.73M |
| **GLM 5.1** | Z.ai | — | 编码、长程推理 | 27.59M（最热门） |
| **Diffusion Gemma 26B** | Google | 26B（A4B active） | 并行 token 生成 | 171K |

### NVIDIA Build 上其他热门编码模型

| 模型 | 参数 | Context | 编码能力 |
|------|------|---------|---------|
| DeepSeek V4 Flash | 284B MoE | 1M | 快速编码 + agent |
| DeepSeek V4 Pro | — | 1M | 编码专项 |
| Kimi K2.6 | 1T MoE | — | 长程编码 + 工具 |
| Qwen 3 Coder | 480B | — | 编码（Cerebras 端点也提供） |

> **注意**：视频发布于 2026-06-18，模型列表和热度数据截至此时。NVIDIA Build 平台模型更新很快，建议直接查看 [build.nvidia.com/models](https://build.nvidia.com/models) 获取最新列表。

---

## 配置完整流程

### Step 1：安装 OpenCode

```bash
curl -fsSL https://opencode.ai/install | bash
```

### Step 2：获取 NVIDIA API Key

```
1. 访问 https://build.nvidia.com/models
2. 登录 / 注册 NVIDIA 账号（免费）
3. 选择任意模型 → 点击 "Get API Key"
4. 复制 API Key（格式：nvapi-xxxxxxxx）
```

### Step 3：配置环境变量

```bash
# 添加到 ~/.bashrc 或 ~/.zshrc
export NVIDIA_API_KEY="nvapi-xxxxxxxx"
```

### Step 4：配置 OpenCode Provider

NVIDIA NIM 使用 OpenAI 兼容 API，需要在 OpenCode 中配置为自定义 provider。

**方式 A：通过 `/connect` 命令**（如果列表中有 NVIDIA）

```
# 在 OpenCode TUI 中
/connect
# → 选择 NVIDIA 或 OpenAI-compatible
# → 粘贴 API key
```

**方式 B：手动编辑配置文件**

创建或编辑 `~/.config/opencode/opencode.json`（项目级用 `.opencode.json`）：

```json
{
  "$schema": "https://opencode.ai/config.json",
  "provider": {
    "nvidia": {
      "npm": "@ai-sdk/openai-compatible",
      "name": "NVIDIA NIM",
      "options": {
        "baseURL": "https://integrate.api.nvidia.com/v1",
        "apiKey": "{env:NVIDIA_API_KEY}"
      },
      "models": {
        "minimax-m3": {
          "name": "MiniMax M3"
        },
        "nemotron-3-ultra-550b-a55b": {
          "name": "Nemotron 3 Ultra"
        },
        "z-ai/glm-5-1": {
          "name": "GLM 5.1"
        }
      }
    }
  }
}
```

> **模型 ID** 必须与 NVIDIA Build 页面上的模型 slug 一致。到 [build.nvidia.com/models](https://build.nvidia.com/models) 点击具体模型，URL 中的路径就是模型 ID。

### Step 5：初始化项目 + 使用

```bash
cd /path/to/your/project
opencode

# 在 OpenCode TUI 中：
/init          # 生成 AGENTS.md（项目上下文文件）
/models        # 选择 NVIDIA 模型
# 开始编码...
```

### 验证连通性

```
# 在 OpenCode 中直接问：
Hello, which model are you?
```

---

## Free Tier 限制与注意事项

### 速率限制（核心限制）

| 限制项 | 默认值 | 说明 |
|--------|--------|------|
| **请求频率** | 40 RPM（每分钟 40 请求） | 所有模型共享 |
| **并发** | 1 | 串行处理 |
| **Context 长度** | 模型相关，部分模型有限缩 | 长上下文可能被截断 |
| **信用积分** | 已废弃 | 不再有 credit 限制 |

### 对编码 agent 的影响

```
问题：Agentic 编码工作流会扇出大量并发请求
      （一次任务可能触发 10-20 个 API 调用）

40 RPM 限制 → 复杂任务中途被限流 → agent 卡住

解决方案：
┌─ 社区已有人申请提升到 200 RPM（NVIDIA 论坛有审批流程）
├─ 简单任务不受影响（单轮对话只消耗 1 个请求）
└─ 大型重构建议用本地模型或付费 API
```

### ✅ 适合的场景

- 学习和原型开发
- 小型项目编码辅助
- 测试不同模型的编码能力
- 无信用卡的纯开发用途

### ❌ 不适合的场景

- 大型项目自动化重构（速率限制瓶颈）
- 需要大量并发请求的 CI/CD 流水线
- 需要高吞吐量的生产环境

---

## 对比：OpenCode + NVIDIA vs 其他方案

### 免费 AI 编码方案对比

| 方案 | 模型质量 | 免费额度 | 配置难度 | 稳定性 |
|------|---------|---------|---------|--------|
| **OpenCode + NVIDIA NIM** | ⭐⭐⭐⭐⭐ | 40 RPM 无限量 | ⭐⭐⭐ 中等 | ⭐⭐⭐ 有时限流 |
| OpenCode + OpenRouter Free | ⭐⭐⭐ | 模型有限 | ⭐⭐ 简单 | ⭐⭐⭐⭐ |
| OpenCode + 本地 Ollama | ⭐⭐⭐ | 无限制 | ⭐ 需 GPU | ⭐⭐⭐⭐⭐ |
| GitHub Copilot Free | ⭐⭐⭐⭐ | 每月有限额 | ⭐ 极简 | ⭐⭐⭐⭐⭐ |
| Claude Code (付费) | ⭐⭐⭐⭐⭐ | 无免费 | ⭐ 极简 | ⭐⭐⭐⭐⭐ |

### 选择决策树

```
需要免费方案？
├─ 有本地 GPU（8GB+）？
│  ├─ 是 → OpenCode + Ollama（零延迟、无限制）
│  └─ 否 → 需要云端推理
│     ├─ 只是简单编码辅助？
│     │  ├─ 是 → OpenCode + NVIDIA NIM（质量高）
│     │  └─ 否 → 需要重度 agent 工作流
│     │     ├─ 接受限流 → OpenCode + NVIDIA NIM
│     │     └─ 需要稳定 → 付费方案（Claude/OpenRouter）
│     └─
└─ 不介意付费 → Claude Code / Cursor（开箱即用）
```

---

## 常见问题

### Q: 40 RPM 够用吗？

日常编码对话足够（单轮交互 = 1 请求）。但 agent 模式下自动探索代码库可能快速消耗配额。NVIDIA 论坛可申请提升到 200 RPM。

### Q: NVIDIA NIM 会突然收费吗？

目前 NVIDIA Developer Program 会员免费。但 NVIDIA 随时可能调整政策。建议不要在生产环境依赖。

### Q: 和 OpenRouter 的免费模型比？

NVIDIA NIM 上的模型更新更前沿（如 Nemotron 3 Ultra、MiniMax M3 刚上线就可用），OpenRouter 免费模型更稳定但选择有限且偏旧。

### Q: 支持流式输出吗？

支持。NIM API 完全兼容 OpenAI 格式，`stream: true` 正常工作。

---

## 参考资料

- **视频**: [OpenCode + NVIDIA: Use Minimax M3, Gemma4, Nemotron 3 & GLM for Free](https://www.youtube.com/watch?v=G7dSIip5jF4) — AI Stack Engineer
- **OpenCode 官网**: [opencode.ai](https://opencode.ai/docs)
- **OpenCode Providers 文档**: [opencode.ai/docs/providers](https://opencode.ai/docs/providers/)
- **NVIDIA Build 模型列表**: [build.nvidia.com/models](https://build.nvidia.com/models)
- **NVIDIA API Key 获取**: [build.nvidia.com/settings/api-keys](https://build.nvidia.com/settings/api-keys)
- **NVIDIA NIM FAQ**: [forums.developer.nvidia.com](https://forums.developer.nvidia.com/t/nvidia-nim-faq/300317)
- **速率限制提升请求**: [NVIDIA 论坛](https://forums.developer.nvidia.com/t/request-for-nvidia-nim-api-rate-limit-increase-40-200-rpm-ai-coding-with-opencode/368287)

## 相关笔记

- [[OpenCode]] — OpenCode CLI 任务委派
- [[free-coding-models]] — 免费编码模型追踪
