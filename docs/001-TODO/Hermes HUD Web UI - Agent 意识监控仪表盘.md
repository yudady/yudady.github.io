---
title: Hermes HUD Web UI - Agent 意识监控仪表盘
tags:
  - ai/agent
  - 工具/dashboard
  - 工具/hermes-生态
source:
  - "https://github.com/joeynyc/hermes-hudui"
author: Joey Rodriguez
created: 2026-04-18 09:40
updated: 2026-04-18 09:40
description: |
  Hermes AI Agent 的浏览器端"意识监控"仪表盘，13 个 tab 实时展示 agent 的记忆、技能、会话、cron 任务、成本等全部状态。
level: beginner
stars: 3
---

# Hermes HUD Web UI - Agent 意识监控仪表盘

> [!info] 基本信息
> | 项目 | 详情 |
> |------|------|
> | **仓库** | https://github.com/joeynyc/hermes-hudui |
> | **版本** | 0.1.0 |
> | **协议** | MIT |
> | **作者** | Joey Rodriguez (@joeynyc) |
> | **要求** | Python 3.11+, Node.js 18+, 运行中的 Hermes agent |

---

## 一句话定位

Hermes AI Agent 的浏览器端监控仪表盘，读取 `~/.hermes/` 数据目录，通过 WebSocket 实时展示 agent 的全部内部状态。

---

## 核心解决的问题

Hermes 是一个带持久记忆的 AI Agent，所有状态数据（记忆、技能、会话历史、cron 任务、token 消耗等）存储在 `~/.hermes/` 目录。hermes-hudui 将这些数据可视化为 Web 仪表盘，让用户能直观监控 agent 的"意识"状态。

---

## 功能特性

### 13 个 Tab 面板

身份、记忆、技能、会话、cron 任务、项目、健康、成本、模式、纠错、实时聊天等。

### 实时更新

基于 WebSocket + 文件监听，无需手动刷新：

```
文件变更 → file_watcher 检测 → 清除缓存 → WebSocket 广播 → 前端 SWR 静默重新验证
```

### 语言支持

英文（默认）和中文，通过 header 右侧切换按钮，设置持久化到 localStorage。中文模式下 agent 聊天回复也会用中文。

### 4 套主题

| 主题 | 配色 | 切换快捷键 |
|------|------|-----------|
| Neural Awakening | 青色 (cyan) | `t` |
| Blade Runner | 琥珀色 (amber) | `t` |
| fsociety | 绿色 (green) | `t` |
| Anime | 紫色 (purple) | `t` |

支持 CRT 扫描线效果。

---

## 技术架构

```
+-------------------+     WebSocket     +-------------------+
|                   | <=============>   |                   |
|   FastAPI Backend |                   |  React Frontend   |
|   (port 3001)     |     REST API      |  (port 5173)      |
|                   | <=============>   |                   |
+-------------------+                   +-------------------+
        |                                         |
        | 读取                                    | SWR
        v                                         v
  ~/.hermes/                                Tailwind CSS
  数据目录                                 4 套主题
```

### 后端 (Python/FastAPI)

| 组件 | 说明 |
|------|------|
| **FastAPI + Uvicorn** | Web 服务器，端口 3001 |
| **watchfiles** | 文件系统监听，检测 `~/.hermes/` 变更 |
| **WebSocket** | 实时推送变更事件到前端 |
| **collectors/** | 数据收集层，每个数据域一个 collector |
| **cache.py** | 基于 mtime 的缓存层，`@cache_with_mtime()` 装饰器 |
| **api/** | REST 路由，每个数据域一个路由文件 |

### 前端 (React/TypeScript)

| 组件 | 说明 |
|------|------|
| **React 19** + **Vite 8** | 构建工具 |
| **Tailwind CSS 4** | 样式 |
| **SWR** | 数据获取 + 缓存 + 自动重新验证 |
| **react-router-dom 7** | 路由 |
| **TypeScript 6** | 类型检查 |

---

## 快速开始

```bash
git clone https://github.com/joeynyc/hermes-hudui.git
cd hermes-hudui
./install.sh
hermes-hudui
# 打开 http://localhost:3001
```

后续运行：

```bash
source venv/bin/activate && hermes-hudui
```

### 开发模式（前后端分离）

```bash
# 终端 1 - 后端
hermes-hudui --dev          # port 3001

# 终端 2 - 前端（代理 /api 到 3001）
cd frontend && npm run dev  # port 5173
```

---

## 键盘快捷键

| 按键 | 功能 |
|------|------|
| `1`-`9`, `0` | 切换 tab |
| `t` | 主题选择器 |
| `Ctrl+K` | 命令面板 (Command Palette) |

---

## 与 TUI 版的关系

hermes-hud 是终端版，hermes-hudui 是浏览器版。两者独立读取同一个 `~/.hermes/` 数据目录，可以同时运行。

Web UI 独有的功能：
- 专属 Memory、Skills、Sessions tab
- 按模型追踪 token 成本
- 命令面板 (Command Palette)
- 实时聊天
- 主题切换

如果同时安装 TUI：`pip install 'hermes-hudui[tui]'`

---

## 仓库结构

```
hermes-hudui/
├── backend/
│   ├── api/                 # REST 路由（每个数据域一个文件）
│   │   ├── agents.py        # Agent 身份
│   │   ├── memory.py        # 记忆
│   │   ├── skills.py        # 技能
│   │   ├── sessions.py      # 会话
│   │   ├── cron.py          # 定时任务
│   │   ├── token_costs.py   # Token 成本
│   │   ├── dashboard.py     # 仪表盘概览
│   │   ├── health.py        # 健康状态
│   │   ├── patterns.py      # 行为模式
│   │   ├── corrections.py   # 纠错记录
│   │   └── ...
│   ├── collectors/          # 数据收集 + 智能缓存
│   ├── cache.py             # mtime 缓存层
│   ├── file_watcher.py      # 文件变更监听
│   ├── websocket_manager.py # WebSocket 管理
│   ├── main.py              # 入口 + CLI
│   └── static/index.html    # 内嵌前端（生产模式）
├── frontend/
│   ├── src/
│   │   ├── components/      # 每个 tab 一个面板
│   │   ├── hooks/           # useApi, useTheme, useWebSocket
│   │   └── lib/             # 共享工具函数
│   ├── package.json
│   └── vite.config.ts
├── pyproject.toml
├── install.sh
└── CONTRIBUTING.md
```

---

## 平台支持

macOS / Linux / WSL

---

## 参考资料

- [GitHub 仓库](https://github.com/joeynyc/hermes-hudui)
- [CONTRIBUTING.md](https://github.com/joeynyc/hermes-hudui/blob/main/CONTRIBUTING.md)

## 相关笔记

- [[../../200-Distill/200-AI-Tools/Hermes Agent - 自改进AI代理框架]]
- [[AI Agent 监控与可观测性]]
