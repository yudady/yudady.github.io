---
title: OpenHuman — Local-First 个人 AI 工作台
aliases: [OpenHuman, tinyhumansai/openhuman]
tags:
  - ai-agent
  - local-first
  - obsidian
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=H8kHxp6YlJY"
  - "https://github.com/tinyhumansai/openhuman"
author: Github雷達（频道），senamakel（OpenHuman 创始人）
created: 2026-07-28
updated: 2026-07-28
description: OpenHuman 是一个 local-first 的开源个人 AI 工作台，通过 Memory Tree、Graph Orchestration 和可审核 Workflow 解决传统聊天框 AI 的记忆断层问题。
level: intermediate
stars: 4
---

# OpenHuman — Local-First 个人 AI 工作台

> OpenHuman 想做的不是另一个聊天框，而是一个 local-first 的个人 AI 系统：有记忆、有工具、有代理协作，也能把工作流变成可审核的图。

## 目录

- [核心定位](#核心定位)
- [三大核心模块](#三大核心模块)
- [Memory Tree 与 Obsidian Vault](#memory-tree-与-obsidian-vault)
- [Graph Orchestration vs 传统 Agent Loop](#graph-orchestration-vs-传统-agent-loop)
- [可审核 Workflow](#可审核-workflow)
- [Local-First 与权限边界](#local-first-与权限边界)
- [工具整合与执行能力](#工具整合与执行能力)
- [竞品对比](#竞品对比)
- [Early Beta 注意事项](#early-beta-注意事项)
- [快速安装](#快速安装)

---

## 核心定位

传统 LLM 介面（ChatGPT 等）的痛点：每次对话从零开始，AI 不知道你昨天做了什么、资料在哪里、下一步该找谁。你不断在「教一个很聪明但永远失忆的助理」。

OpenHuman 的目标是将 AI 直接嵌入日常工作现场：

```
传统聊天框 AI                    OpenHuman
┌──────────────┐               ┌──────────────────────┐
│  无状态对话   │               │  持续记忆 + 上下文    │
│  单次 prompt  │     ──→      │  工具调度 + 代理协作  │
│  黑箱执行     │               │  可审核 workflow      │
│  数据上云     │               │  local-first 数据    │
└──────────────┘               └──────────────────────┘
```

一句话定位：**把 AI 助理从聊天框往个人作业系统的方向推一步**。

---

## 三大核心模块

OpenHuman README 将自己分为三块：

| 模块 | 角色 | 核心能力 |
|------|------|----------|
| **Brain**（本机记忆） | 知识库 | Memory Tree → SQLite → Obsidian vault |
| **Orchestrator**（代理协调器） | 任务调度 | Checkpointed graph，可暂停/恢复/分派子代理 |
| **Deep Researcher & Doer** | 执行层 | Web search、scraper、browser、voice、meeting agents、image/video 生成 |

### 模块协作流程

```
用户下达任务
     │
     ▼
┌─────────────┐     ┌──────────────────┐     ┌───────────────┐
│   Brain     │────→│  Orchestrator    │────→│ Deep Researcher│
│ (Memory     │上下文│ (Graph 调度，    │拆解为│ (执行：搜索、   │
│  Tree)      │     │  可暂停/恢复)    │子任务│  抓取、工具调用) │
└─────────────┘     └──────────────────┘     └───────────────┘
     ↑                     │                        │
     │         卡住时能回来报告卡在哪                  │
     └─────────────────────┴────────────────────────┘
             结果写回 Memory Tree，下次不用从零开始
```

---

## Memory Tree 与 Obsidian Vault

这是 OpenHuman 最核心的差异化设计。

**数据来源**：文件、邮件（Gmail）、行事历、程式码仓库（GitHub repo）、通讯软体（Slack）等。

**处理流程**：

1. 连接账户（OAuth 一次性授权）
2. **Auto-fetch**：每 20 分钟自动拉取各连接的最新数据
3. 所有内容标准化为 ≤3k token 的 Markdown chunk
4. 评分后折叠成层级式摘要树（hierarchical summary tree）
5. 存入本机 **SQLite** + 同步为 **Obsidian vault**（可打开、可编辑）

```
Gmail  ─┐
GitHub ─┤
Slack  ─┼──→ Auto-fetch (20min) ──→ 标准化为 ≤3k MD chunk
Notion ─┤                              │
Calendar┘                              ▼
                                   评分 + 折叠成 Memory Tree
                                       │
                              ┌────────┴────────┐
                              ▼                 ▼
                         SQLite (本机)    Obsidian Vault
                         (查询用)        (.md 文件，可编辑)
```

设计灵感来自 Karpathy 的 [obsidian-wiki workflow](https://x.com/karpathy/status/2039805659525644595)。

> 已自托管 [agentmemory](https://github.com/rohitg00/agentmemory)？OpenHuman 支持设置 `memory.backend = "agentmemory"`，同一存储可同时驱动 OpenHuman、Claude Code、Cursor、Codex、OpenCode。

---

## Graph Orchestration vs 传统 Agent Loop

| 特性 | 传统 Agent Loop | OpenHuman Checkpointed Graph |
|------|----------------|------------------------------|
| 执行模型 | 单一迴圈（收任务→呼工具→回覆） | 图结构，节点可分派给不同子代理 |
| 暂停/恢复 | ❌ 不支持 | ✅ 可暂停、可恢复 |
| 失败处理 | 默默消失或偏离方向 | 能回来报告卡在哪里 |
| 任务复杂度 | 适合短任务 | 适合长任务、多步骤 |
| 可观测性 | 低 | 高（每步可追踪） |

关键差异：传统 agent 收到任务后是一条直线跑到底；OpenHuman 的 checkpointed graph 让流程可以**暂停、恢复、分派给子代理**，如果卡住，系统至少能说明卡在哪里，而不是默默消失。

---

## 可审核 Workflow

OpenHuman 强调 workflow 是**看得见的**：

1. 请 agent 帮你设计自动化
2. 在 **canvas** 上审核生成的 **tinyflows graph**
3. 决定是否保存、是否触发

```
┌─────────────────────────────────────────┐
│           Workflow Canvas               │
│                                         │
│   [触发条件] ──→ [搜索] ──→ [摘要]      │
│                      │                   │
│                      ▼                   │
│                 [写入文件]               │
│                      │                   │
│                      ▼                   │
│                 [发送通知]               │
│                                         │
│   ✅ 审核模式：每步可见、可修改、可中断   │
└─────────────────────────────────────────┘
```

对需要长期执行的任务，这比单次 prompt 安心很多——你知道它会在什么条件下触发、会做哪些动作。

---

## Local-First 与权限边界

### 数据存储

| 数据类型 | 存储位置 |
|----------|----------|
| Memory Tree | 本机 SQLite |
| Obsidian Vault | 本机 .md 文件 |
| 工作区配置 | 本机 |
| 运行时状态 | 本机 |

提供 **Privacy Mode**，但 local-first 不等于零风险。

### 授权范围意识

一旦把 Gmail、GitHub、Slack、Notion 这类账户接进去，就必须理解：

- ✅ 授权范围（OAuth scope 给了什么权限）
- ✅ 批准机制（哪些操作需要你确认）
- ✅ 本机安全（本机数据如何保护）

> **越强的工具越需要清楚的边界。** README 已标注 early beta，功能范围大但可能有粗糙边角。

---

## 工具整合与执行能力

OpenHuman 内建 118+ 第三方整合（via Composio connector layer），包括：

```
工具类别          具体整合
─────────────────────────────────────────────
通讯协作     →   Gmail, Slack, Notion, Discord
开发工具     →   GitHub, Linear, Jira
生产力      →   Calendar, Drive, Stripe
AI/搜索     →   Web search, scraper, browser
语音/会议   →   Voice (STT + ElevenLabs TTS), Google Meet agent
模型路由     →   内建 routing（reasoning/fast/vision 自动选择）
```

### TokenJuice — Token 压缩层

每个工具调用、抓取结果、邮件正文、搜索 payload 在进入 LLM 前都会经过压缩：

- HTML → Markdown
- 长 URL 缩短
- 冗余工具输出去重 + 摘要
- **CJK、emoji 等多字节文本按 grapheme 保留**（不会丢失）
- 效果：相同信息，token 减少最多 80%，降低成本与延迟

### 模型路由

默认使用 OpenHuman 后端做 model routing，自动为每种任务选择合适的 LLM（reasoning / fast / vision）。也支持通过 [Ollama](https://tinyhumans.gitbook.io/openhuman/features/model-routing/local-ai) 运行本地模型。

---

## 竞品对比

README 提供的横向对比（产品会演进，需对照各厂商最新状态验证）：

| 特性 | Claude Cowork | OpenClaw | Hermes Agent | OpenHuman |
|------|--------------|----------|--------------|-----------|
| 开源 | ❌ 闭源 | ✅ MIT | ✅ MIT | ✅ GNU |
| 上手难度 | ✅ 桌面+CLI | ⚠️ 终端优先 | ⚠️ 终端优先 | ✅ UI 优先，分钟级 |
| 成本 | ⚠️ 订阅+加购 | ⚠️ 自带模型 | ⚠️ 自带模型 | ✅ 单一订阅+TokenJuice |
| 记忆 | ✅ 对话级 | ⚠️ 依赖插件 | ✅ 自学习 | 🚀 Memory Tree + Obsidian |
| 整合数 | ⚠️ 少 | ⚠️ 自带 | ⚠️ 自带 | 🚀 118+ OAuth |
| Auto-fetch | ❌ 无 | ❌ 无 | ❌ 无 | ✅ 20 分钟同步 |
| 模型路由 | ❌ 单一 | ⚠️ 手动 | ⚠️ 手动 | ✅ 内建 |
| 原生工具 | ✅ 仅代码 | ✅ 仅代码 | ✅ 仅代码 | ✅ 代码+搜索+抓取+语音 |

OpenHuman 的核心差异化：**最小化 vendor sprawl**、**工作流知识在设备上**、**对数据的持久记忆**（不只是对话）。

---

## Early Beta 注意事项

视频和 README 都诚实标注 early beta，需要注意：

| 维度 | 风险/限制 |
|------|----------|
| 安装整合 | 可能有粗糙边角，需实测 |
| 权限设定 | 接入大量账户后授权范围需仔细审查 |
| 长任务稳定性 | checkpointed graph 理论上可恢复，但需实测失败恢复能力 |
| QA | demo 漂亮不代表失败时能透明恢复 |

> 视频原话：「不能只看 demo 漂亮，还要看失败时能不能透明、能不能恢复、能不能让使用者掌控。」

### 判断：适合谁？

```
                        ┌─ 偶尔问 AI 几个问题？
                        │     └─→ OpenHuman 太重，不需要
                        │
你现在的 AI 使用场景 ────┤
                        │   开源维护 / 研究整理 / 内容制作 /
                        └─  团队协作 / 跨多工具追任务？
                              └─→ OpenHuman 的记忆与 workflow 设计
                                  很值得放进观察清单
```

---

## 快速安装

### 推荐：原生包安装（有签名验证）

**macOS (Homebrew):**
```bash
brew tap tinyhumansai/core
brew install openhuman
```

**Linux (Debian/Ubuntu):**
```bash
sudo apt-get install -y --no-install-recommends gnupg2 curl ca-certificates
curl -fsSL https://tinyhumansai.github.io/openhuman/apt/KEY.gpg \
  | sudo gpg --dearmor -o /etc/apt/keyrings/openhuman.gpg
echo "deb [signed-by=/etc/apt/keyrings/openhuman.gpg arch=amd64] \
  https://tinyhumansai.github.io/openhuman/apt stable main" \
  | sudo tee /etc/apt/sources.list.d/openhuman.list
sudo apt-get update
sudo apt-get install -y openhuman
```

**Windows:** 从 [GitHub Releases](https://github.com/tinyhumansai/openhuman/releases/latest) 下载签名 `.msi`。

> Linux AppImage 在 Wayland 下可能崩溃（见 [issue #2463](https://github.com/tinyhumansai/openhuman/issues/2463)），Debian/Ubuntu 建议用 `.deb`。

### 从源码构建

依赖：Node.js 24+, pnpm 10.10.0, Rust 1.93.0, CMake, Ninja, ripgrep。

```bash
git clone https://github.com/tinyhumansai/openhuman.git
cd openhuman
git submodule update --init --recursive
pnpm install
pnpm dev              # web UI 开发
pnpm --filter openhuman-app dev:app  # 桌面端开发
```

---

## 技术栈概览

| 层 | 技术 |
|----|------|
| 桌面框架 | Tauri（Rust + Web frontend） |
| 包管理 | pnpm workspace（monorepo） |
| 原生组件 | Rust + CMake + Ninja |
| 本机数据库 | SQLite |
| 知识库格式 | Obsidian-compatible Markdown vault |
| 整合层 | Composio connector（118+ OAuth） |
| 语音 | STT in, ElevenLabs TTS out |
| 许可证 | GNU |

---

## 参考资料

- [YouTube 视频 — OpenHuman：把 AI 從聊天框拉回你的工作現場（Github雷達）](https://www.youtube.com/watch?v=H8kHxp6YlJY)
- [GitHub 仓库 — tinyhumansai/openhuman](https://github.com/tinyhumansai/openhuman)
- [官方文档 — tinyhumans.gitbook.io/openhuman](https://tinyhumans.gitbook.io/openhuman/)
- [Karpathy 的 obsidian-wiki workflow（灵感来源）](https://x.com/karpathy/status/2039805659525644595)
- [agentmemory（可选后端）](https://github.com/rohitg00/agentmemory)

## 相关笔记

- [[Hermes Agent]]
- [[Local-First AI]]
