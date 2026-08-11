---
title: Orca — 开源 AI Agent 编排开发环境 (ADE)
aliases: [Orca ADE, Orca agent orchestrator, stablyai/orca]
tags:
  - ai-agent
  - agent-orchestration
  - ade
  - devtools
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=nM6tvi48nMs"
  - "https://github.com/stablyai/orca"
  - "https://www.onorca.dev"
author: AICodeKing (视频) / stablyai (产品开发者)
created: 2026-08-11
updated: 2026-08-11
description: Orca 是 MIT 开源的多 Agent 编排开发环境 (ADE)，支持 30+ CLI Agent 并行工作，自带 Git Worktree 隔离、IDE 工具链、可视化调试、移动端远程控制。
level: intermediate
stars: 5
---

# Orca — 开源 AI Agent 编排开发环境 (ADE)

> Orca 由 stablyai（Y Combinator backed）开发，定位为 **Agent Development Environment (ADE)** — 不是给人类写代码的 IDE，而是指挥多个 AI Agent 并行写代码的「驾驶舱」。MIT 开源，自带 30+ Agent 兼容、Git Worktree 隔离、可视化 Diff、内建浏览器/模拟器、移动端远程控制。

## 目录

1. [核心定位：从 IDE 到 ADE](#一核心定位从-ide-到-ade)
2. [底层机制：Git Worktree + 多 Agent 协同](#二底层机制git-worktree--多-agent-协同)
3. [开发者体验：内建工具链与可视化工作流](#三开发者体验内建工具链与可视化工作流)
4. [自动化与资源控管](#四自动化与资源控管)
5. [移动端与远程延伸](#五移动端与远程延伸)
6. [竞品对比与适用场景](#六竞品对比与适用场景)
7. [安装与快速开始](#七安装与快速开始)
8. [局限性与注意事项](#八局限性与注意事项)

---

## 一、核心定位：从 IDE 到 ADE

### 概念转型

传统 IDE（Integrated Development Environment）是为**人类编写代码**设计的。Orca 提出的 ADE（Agent Development Environment）是**为 AI Agent 编写代码**设计的新范式 — 人类角色从「写代码的人」转变为「指挥官」。

```
┌─────────────────────────────────────────────┐
│              传统 IDE 工作流                   │
│  人 ──写代码──> IDE ──编译/运行──> 结果        │
│  (AI 是辅助工具，如 Copilot)                   │
├─────────────────────────────────────────────┤
│              ADE 工作流 (Orca)                 │
│  人 ──下指令──> Orca ──启动Agent──> Agent写代码 │
│       ↑                    ↓                  │
│       └──── 监控/审查/合并 ←─┘                 │
│  (AI 是执行主体，人是监督者)                    │
└─────────────────────────────────────────────┘
```

### 授权与商业模式

| 维度 | Orca | 典型竞品 (Cursor, Windsurf 等) |
|------|------|------|
| 协议 | MIT 开源 | 闭源/部分开源 |
| 费用 | 完全免费 | $20-40/月订阅 |
| 模型访问 | BYOS (Bring Your Own Subscription) | 平台转售模型（含加价） |
| 数据路由 | 直连原服务商，不经中继 | 多数经平台服务器 |
| 隐私 | 所有请求直达原服务商 | 取决于平台政策 |

> [!tip] BYOS 模式的含义
> 你不需要向 Orca 付费，只需使用已有的 Agent 订阅（如 Claude Pro、Codex 计划），Orca 直接调用这些服务的 API/CLI。没有中间商赚差价，也没有数据过境风险。

---

## 二、底层机制：Git Worktree + 多 Agent 协同

### 平行隔离：Git Worktree

Orca 的核心机制是为**每个任务创建独立的 Git Worktree**（真实的 Git Worktree，非文件拷贝），实现多个 Agent 在同一仓库上并行工作而不冲突。

```
                    ┌── Worktree A (branch: feat/login)
                    │   └── Agent: Claude Code
                    │
  主仓库 (main) ─────┼── Worktree B (branch: feat/api)
                    │   └── Agent: Codex
                    │
                    ├── Worktree C (branch: fix/bug-42)
                    │   └── Agent: Grok
                    │
                    └── Worktree D (branch: experiment)
                        └── Agent: Gemini (竞速同一 Prompt)
```

**竞速模式 (Race)**：同一个 Prompt 分发给多个 Agent，各自在独立 Worktree 中执行，完成后人工选择最佳方案。这在以前需要大量手动 tmux + worktree 配置，现在内置一键完成。

### Agent 兼容性 (Agent Agnostic)

Orca 支持超过 30 种 CLI Agent，视频中展示的包括：

| Agent 名称 | 视频中的称呼 | 备注 |
|------------|-------------|------|
| Claude Code | Claude Code | Anthropic |
| Codex | Codex | OpenAI |
| Gemini | Gemini | Google |
| Grok | Grok | xAI |
| Open Code | Open Code | 开源 CLI |
| Pi | Pi | — |
| Qwen Code | "Quen Code" | 阿里通义千问 |
| Kimi | "Kimmy" | Moonshot AI |
| Hermes | Hermes | Nous Research |
| Kilo Code | "Kilo Code" | — |
| Codex CLI | — | — |

> [!note] 名称勘误
> 视频为英文自动语音转录，部分产品名存在语音识别偏差。"Kimmy" 实为 **Kimi**，"Quen Code" 实为 **Qwen Code**。Orca 还会自动检测本机已安装的 CLI 工具。

**核心原则**：如果某个 Agent 能在终端运行，Orca 就能驱动它。支持 Mac、Windows、Linux 三平台。

### Agent 间编排 (Orchestration & Handoff)

Orca 内建 Orchestration Skill，支持 Agent 之间的深度协同：

```
┌─────────────────────────────────────────────────────┐
│                  编排模式一览                         │
├──────────────┬──────────────────────────────────────┤
│ Handoff      │ Agent A 将任务带完整 Context          │
│ (任务交接)    │ 交接给 Agent B 执行                   │
│              │ 例: Claude Code → Codex               │
├──────────────┼──────────────────────────────────────┤
│ Phase        │ 子 Agent 按阶段顺序执行               │
│ Workflow     │ A完成后B接力，B完成后C接力              │
│ (阶段式工作流)│                                      │
├──────────────┼──────────────────────────────────────┤
│ Parallel     │ 独立子任务分发给并行子 Agent           │
│ Split        │ 各自在独立 Worktree 中同时执行          │
│ (并行分拆)    │                                      │
├──────────────┼──────────────────────────────────────┤
│ Stacked PRs  │ 大型变更拆解为多个小型 PR              │
│ (堆叠 PR)     │ 每个 Worktree 对应一个子 Agent + PR   │
└──────────────┴──────────────────────────────────────┘
```

实测演示：Claude Code 会话可以将特定模块重构任务交接给 Codex 会话执行，完整的上下文随之传递。

---

## 三、开发者体验：内建工具链与可视化工作流

### 完整 IDE 工具链（每个 Worktree 自带）

| 组件 | 技术/说明 |
|------|-----------|
| 终端机 | WebGL 渲染，无限滚动缓冲，支持标签页和分屏 |
| 代码编辑器 | Monaco Engine（与 VS Code 同款），自动保存，CSS 颜色色板 |
| Markdown | 编辑器 + Mermaid 图表预览 |
| 文件查看器 | PDF、图片等 |
| Diff 审查 | 红绿对比 + Mini Map，支持内嵌评论反馈给 Agent |
| Attribution | AI/人类编写行数归属标记 |
| Git 操作 | 内建 commit、push，集成 GitHub/GitLab/Bitbucket/Azure DevOps |

### 可视化调试：Design Mode

这是视频中最亮眼的功能 — 直接在浏览器中与 Agent 进行双向交互：

```
┌──────────────────────────────────────────────┐
│              Design Mode 工作流                │
│                                              │
│  ┌─────────┐    ┌──────────┐    ┌─────────┐  │
│  │ 内建浏览器│    │ Element  │    │ Agent   │  │
│  │ 预览页面 │───>│ Picker   │───>│ 接收    │  │
│  │         │    │ 点击元件  │    │ HTML/CSS│  │
│  └─────────┘    └──────────┘    └─────────┘  │
│       ↑              ↑               │        │
│       │         ┌────┴────┐          │        │
│       │         │ Draw on │          │        │
│       └─────────│Screenshot│<─────────┘        │
│    渲染修改结果   │ 涂鸦标注 │    生成修改指令    │
│                 └─────────┘                  │
└──────────────────────────────────────────────┘
```

**Element Picker**：点击网页元素 → 自动提取 HTML/CSS → 直接喂给 Agent。不再需要用文字描述「哪个 div 坏了」。

**Draw on Screenshot**：在截图上画圈、涂鸦标注 → 标注内容直接传给 Agent 作为修改指令。

> [!tip] 为什么这是正确的交互界面
> 前端调试的核心痛点是「描述位置」。用文字描述「右侧第二个按钮的间距」远不如直接圈出来。Orca 的 Element Picker + Draw on Screenshot 把这个交互做到了极致。

### Computer Use 与 iOS 模拟器

- **内建 iPhone 17 Pro 模拟器**：完全可交互，可旋转、点击。配 Orca CLI Skill 后，Agent 可自动驱动模拟器进行 UI 测试。
- **Computer Use**：授予辅助功能和屏幕录制权限后，Agent 可以检查和操作桌面上的任何应用程序 — 不限于模拟器。

### Diff 审查与 Attribution

```
Diff Viewer:
  ✓ 001 - import { Router } from 'express'        [AI]
  ✓ 002 -                                            [AI]
  + 003 - router.post('/login', async (req, res) => [AI]
  + 004 -   const user = await auth(req.body)       [AI]
  + 005 -   res.json({ token: user.token })         [AI]
  -- 003 - // TODO: implement login                 [Human]
  
  → 点击行号添加评论 → 发送给 Agent 作为修改指令
```

Attribution 功能用颜色标记区分哪些代码行是 AI 写的、哪些是人类写的 — 对理解 Agent 的实际产出质量非常有帮助。

---

## 四、自动化与资源控管

### Automations：Agent 的 Cron 排程

Orca 内建 Automations Tab，提供「Agent 版 Cron Job」：

| 模板 | 频率 | 功能 |
|------|------|------|
| Repo Audit | 工作日 | 检查依赖更新、失败测试、有风险的未提交变更 |
| Release Readiness | 每周 | 发布就绪度总结报告 |
| Daily Change Review | 每日 | 审查当天所有变更 |
| Stuck Work Check | 每小时 | 检查卡住的 Agent 任务 |

> [!example] 实际场景
> 「睡觉时 AI 自动做 Code Review」— 利用你已有的订阅（如 Claude Pro），在工作日自动运行依赖检测和测试审查。

### 任务管理整合

| 平台 | 整合方式 |
|------|---------|
| GitHub Issues | 直接从 Issue 启动 Worktree |
| GitLab | 同上 |
| Linear | Agent 可读写/更新 Ticket 状态（需 Linear Skill） |
| Jira | 连接后在 Orca Tasks 面板查看 |

### 实时用量监控

```
┌─────────────────────────────────────────────┐
│         状态栏 — 实时 API 用量仪表板          │
├──────────┬──────────────────────────────────┤
│ Claude   │ ████████░░ 80%  │ 每周重置: 2天3h │
│ OpenAI   │ ███░░░░░░░ 30%  │                │
│ Grok     │ ██████░░░░ 60%  │                │
│ Kimi     │ ██░░░░░░░░ 20%  │                │
├──────────┴──────────────────────────────────┤
│ 🔔 Claude Prompt Cache 过期倒计时: 12:34     │
│ 🔁 多账号热切换: [Account 1 ▼]               │
└─────────────────────────────────────────────┘
```

关键功能：
- **多账号热切换**：管理多个 Claude/Codex 账号，一键切换
- **Prompt Cache 倒数**：Claude 的 Prompt Cache 有过期时间，Orca 显示倒计时提醒你何时恢复会话会变贵
- **每周限额重置倒数**：实时显示各服务商的配额用量和重置时间

---

## 五、移动端与远程延伸

### Orca Mobile（iOS / Android）

```
配对方式:
  ┌──────────────────────────────┐
  │  QR Code 配对                 │
  │  ┌──┐                        │
  │  │██│  ← 手机扫描             │
  │  └──┘                        │
  └──────────┬───────────────────┘
             │
  ┌──────────┴───────────────────┐
  │  连接方式:                    │
  │  ① 局域网 (免费)              │
  │  ② Tailscale (免费)           │
  │  ③ 官方 Relay (任意网络, beta) │
  └──────────────────────────────┘
```

移动端能力：
- 实时查看终端机进度
- 接受 Agent 通知（完成/需要输入）
- 批准 Agent 的下一步操作
- 终端界面自适应手机屏幕（CLI 工具保持可读）
- 实际场景：启动长时间重构任务 → 出门 → 手机批准下一步

### 进阶远程功能

| 功能 | 说明 |
|------|------|
| 语音听写 | 本地 on-device 语音模型，完全离线 |
| SSH Worktree | Agent 在远程服务器运行，UI 留在本机（适合用强力服务器跑重任务） |
| Headless 模式 | 无 GUI 的服务器模式 |
| Agent Hibernation | 空闲 Agent 暂停以节省资源，不丢失 Context |
| Workspace 快照 | 保存工作区状态 |
| Orca CLI | 脚本化控制全部功能 |

---

## 六、竞品对比与适用场景

### 与其他工具对比

| 维度 | Orca | tmux/终端多路复用 | Cursor / Windsurf | Claude Code (原生) |
|------|------|-------------------|-------------------|-------------------|
| 定位 | ADE (多 Agent 编排) | 终端管理 | AI IDE (单 Agent) | 单 CLI Agent |
| 多 Agent 并行 | ✅ 原生 Worktree | ⚠️ 手动配置 | ❌ | ❌ |
| Agent 编排 | ✅ Handoff/Phase/PR | ❌ | ❌ | ❌ |
| 可视化 Diff | ✅ 内建 | ❌ | ✅ | ❌ |
| Design Mode | ✅ Element Picker + Draw | ❌ | ⚠️ 部分 | ❌ |
| 开源 | ✅ MIT | ✅ | ❌ | ❌ |
| 费用 | 免费 | 免费 | $20-40/月 | 需 Claude 订阅 |
| 移动端 | ✅ iOS/Android | ❌ | ❌ | ❌ |
| iOS 模拟器 | ✅ 内建 | ❌ | ❌ | ❌ |

### 适用场景决策树

```
你需要什么？
│
├─ 同时管理 3+ 个 AI Agent 任务？
│   └─ ✅ Orca (Git Worktree 隔离 + 用量监控)
│
├─ 需要 Agent 间协作/交接？
│   └─ ✅ Orca (Orchestration Skill)
│
├─ 前端开发，需要可视化引导 Agent？
│   └─ ✅ Orca (Design Mode + Element Picker)
│
├─ 只用单个 Agent，想要轻量体验？
│   └─ 考虑 Cursor / Claude Code 原生
│
├─ 需要移动端远程控制 Agent？
│   └─ ✅ Orca Mobile (目前独有)
│
└─ 团队需要自动化 CI/CD 级别的 Agent 排程？
    └─ ✅ Orca (Automations + Linear/Jira 整合)
```

---

## 七、安装与快速开始

```bash
# macOS (Homebrew)
brew install --cask stablyai/orca/orca

# Arch Linux (AUR)
yay -S orca

# 其他平台：从官网下载
# https://www.onorca.dev

# 启动后：
# 1. Orca 自动检测本机已安装的 CLI Agent
# 2. 选择一个仓库，创建 Worktree
# 3. 选择 Agent (Claude Code / Codex / ...) 开始任务
```

**前提条件**：你需要至少安装一个 CLI Agent（如 Claude Code、Codex 等）并配置好相应的 API Key 或订阅登录。

---

## 八、局限性与注意事项

| 局限 | 详情 |
|------|------|
| 项目年轻 | 首个稳定版 2026 年 3 月发布，功能迭代极快（几乎每日发布），版本间可能有 breaking change |
| 移动端 Relay | 仍处于 beta，稳定性待验证 |
| 屏幕空间 | 面板较多，小屏幕笔记本可能感到拥挤 |
| 文档成熟度 | 快速迭代意味着文档可能滞后于实际功能 |

> [!warning] 版本管理建议
> 如果你基于特定功能构建工作流，更新前请先查看 changelog，不要假设功能行为不变。

---

## 参考资料

- [Orca GitHub 仓库](https://github.com/stablyai/orca) — MIT 开源，TypeScript/Electron
- [Orca 官网](https://www.onorca.dev) — 下载、文档、Discord 社区
- [原始视频 — AICodeKing](https://www.youtube.com/watch?v=nM6tvi48nMs) — 2026-08-10 发布
- [aistarted.com 深度评测](https://aistarted.com/tutorials/orca-open-source-ai-coding-agent-orchestrator)

## 相关笔记

- [[AI Agent 编排工具对比]]
- [[Claude Code 使用指南]]
- [[Git Worktree 工作流]]
