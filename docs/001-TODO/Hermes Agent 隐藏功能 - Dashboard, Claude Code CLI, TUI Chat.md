---
title: Hermes Agent 隐藏功能 - Dashboard、Claude Code CLI 认证、TUI Chat
aliases: [Hermes Agent Hidden Features]
tags:
  - hermes-agent
  - ai-agent
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=tmwnqe6RBRg"
  - "https://hermes-agent.nousresearch.com/docs/reference/cli-commands"
author: Clearmud (Marcelo)
created: 2026-05-18
updated: 2026-05-18
description: |
  Hermes Agent 三个隐藏功能：启用 Web Dashboard 作为系统服务常驻运行、认证 Claude Code CLI 让子代理复用 Claude 订阅、在 Dashboard 中嵌入 TUI Chat 交互界面。
level: beginner
stars: 3
---

# Hermes Agent 隐藏功能

> Hermes Agent 默认以 TUI（Terminal User Interface）运行，但内置了一个 Web Dashboard，以及将 Claude Code CLI 认证共享给子代理的能力。本笔记覆盖三个实用功能，适合刚部署 Hermes Agent 想要提升管理体验的用户。

## 目录

- [[#功能一：Hermes Dashboard 启用与常驻运行]]
- [[#功能二：Claude Code CLI 认证 — 子代理复用订阅]]
- [[#功能三：Dashboard 中嵌入 TUI Chat]]

---

## 功能一：Hermes Dashboard 启用与常驻运行

### 核心概念

Hermes Dashboard 是一个基于 Web 的管理界面，默认未启用。启用后可通过浏览器管理会话、日志、工具、定时任务等，端口默认 `9119`。

### 启用方式

| 方式 | 命令 | 行为 |
|------|------|------|
| 手动启动 | `hermes dashboard` | 前台运行，关闭终端即停 |
| 常驻后台 | 在 TUI 中告诉 Agent "enable dashboard, always run in background" | 注册为 systemd 用户服务，开机自启 |

### 手动 vs 常驻 对比

```
手动启动流程:
  hermes dashboard → 前台运行 → 关闭终端 → Dashboard 停止

常驻服务流程:
  TUI 对话 → Agent 执行 → systemd --user enable → 后台常驻
```

### 关键区别

```
hermes dashboard          → 一次性前台进程（会话结束即停止）
Agent 注册 systemd 服务   → 持久化后台服务（重启机器也会自动恢复）
```

### 代码示例

```bash
# 手动启用（临时）
hermes dashboard

# 通过 Agent 设为常驻（推荐）
# 在 TUI 中发送：
# "I'd like to enable the Hermes dashboard and would like it to always
#  start up with Ubuntu. It should always run in the background."

# 验证服务状态
systemctl --user status hermes-dashboard
```

### 最佳实践

- ✅ 让 Agent 注册 systemd 服务，而非手动启动
- ✅ 适合 VPS 或长期运行的 Ubuntu/WSL 环境
- ❌ 不要只手动运行 `hermes dashboard`，关闭终端就会停止

### 判断决策树

```
选 systemd 服务（推荐）如果你需要：
  ✅ Dashboard 7x24 运行
  ✅ 机器重启后自动恢复
  ✅ 不想额外开终端窗口

选手动启动如果你只需要：
  ✅ 临时查看 Dashboard
  ✅ 调试或一次性使用
```

---

## 功能二：Claude Code CLI 认证 — 子代理复用订阅

### 核心概念

Hermes Agent 支持多代理架构（Delegation），主代理可以派生子代理（sub-agents）执行任务。通过在 CLI 中认证 Claude Code，子代理可以使用你的 Claude 订阅额度（如 Opus 4.7），而非消耗 API tokens。

### 工作原理

```
主代理 (Primary Agent)
  │
  ├─ 使用 OpenRouter / Anthropic API 等
  │
  └─ delegate_task → 子代理
                      │
                      └─ 使用 Claude Code CLI（OAuth 认证）
                         消耗 Claude 订阅额度，非 API tokens
```

### 认证流程

```
Step 1: TUI 中发送认证请求
  "I'd like to authenticate Claude Code in the CLI
   so that sub-agents can benefit from our Claude subscription."

Step 2: Agent 自动执行认证流程
  → 打开浏览器 OAuth 页面
  → 用户登录 Claude 账户
  → 粘贴授权码

Step 3: 设置代理分工规则
  → 前端/UI/UX/研究 → Claude Opus 4.7
  → 后端/安全 → Codex GPT 5.5
```

### 代码示例

```bash
# 手动认证 Claude Code CLI（如需独立操作）
hermes login --provider openai-codex

# 验证认证状态
hermes auth list
```

### Claude Code vs API Tokens 对比

| 特性 | Claude Code CLI (OAuth) | API Tokens |
|------|------------------------|------------|
| 计费 | Claude 订阅月费 | 按 token 付费 |
| 适用 | 子代理任务 | 主代理对话 |
| 认证方式 | OAuth 浏览器登录 | API Key |
| 限制 | 订阅用量限制 | 额度限制 |

### 最佳实践

- ✅ 前端/UI/UX/研究类任务派给 Claude Opus 4.7 子代理
- ✅ 后端/安全类任务派给 Codex GPT 5.5 子代理
- ✅ 已在日常机器上使用 Claude Code 的用户无需重复认证
- ❌ 这不是免费的 API 绕过方案，是合法使用已有的订阅工具

### 注意事项

> 视频作者强调：这是通过正规 OAuth 认证使用 Claude Code，不是 TOS 违规或 API key 绕过。子代理使用的是你环境中已认证的工具。

---

## 功能三：Dashboard 中嵌入 TUI Chat

### 核心概念

通过 `--tui` 标志启动 Dashboard，可以在 Web 界面中嵌入一个完整的 TUI Chat 标签页，实现浏览器内直接与 Hermes Agent 交互。

### 启用方式

| 方式 | 命令 |
|------|------|
| 手动 | `hermes dashboard --tui` |
| Agent | "Let's enable the TUI chat in the dashboard, please." |

### 效果

启用后，Dashboard 页面顶部会出现一个 **Chat** 标签页，内嵌完整的 TUI 界面，可以直接在浏览器中发送消息、查看输出，无需额外终端窗口。

### 代码示例

```bash
# 手动启用（含 TUI Chat）
hermes dashboard --tui

# 访问 Dashboard
# 浏览器打开 http://localhost:9119
```

### 最佳实践

- ✅ 与功能一结合：先用 Agent 注册 systemd 服务常驻 Dashboard，再启用 TUI
- ✅ 适合远程 VPS 管理 — 不需要 SSH 保持连接
- ❌ 如果遇到 `[session ended]` 问题，检查 Hermes 版本（v0.13.0 有已知 bug）

---

## 参考资料

- [Hermes Agent CLI 命令参考](https://hermes-agent.nousresearch.com/docs/reference/cli-commands)
- [Hermes Agent Dashboard 设置指南 (YouTube)](https://www.youtube.com/watch?v=GfQEdMZ9LlA)
- [hermes dashboard --tui issue #15972](https://github.com/NousResearch/hermes-agent/issues/15972)
- [Hermes Agent v0.13 TUI bug #21801](https://github.com/NousResearch/hermes-agent/issues/21801)
- [Hermes Dashboard Web UI 指南](https://www.bitdoze.com/hermes-dashboard-guide/)

## 相关笔记

- [[Hermes Agent]]
