---
created: 2026-04-10
tags:
  - AI/Agent
  - 开源项目
  - Web-UI
  - React
  - status/active
  - area/distill
  - type/doc
source: https://github.com/outsourc-e/hermes-workspace
---

# Hermes Workspace - AI Agent 的 Web 前端工作台

> [!info] 基本信息
> - **仓库**: https://github.com/outsourc-e/hermes-workspace
> - **Stars** 916 / **Forks** 92 / **Open Issues** 9
> - **作者**: [@outsourc-e](https://github.com/outsourc-e) (Eric)
> - **协议**: MIT
> - **版本**: 0.1.0
> - **创建时间**: 2026-03-16（约 25 天前）
> - **主要语言**: TypeScript (98%+)
> - **关联项目**: [hermes-agent](https://github.com/outsourc-e/hermes-agent) (Python 后端网关)

---

## 一句话定位

Hermes Agent 的官方 Web 前端 -- 把 AI agent 的 chat、文件、记忆、技能、终端整合到一个 Web workspace 里，PWA 支持移动端。

---

## 核心解决的问题

提供一个完整的 AI Agent 操作界面，不是简单的 chat wrapper，而是一个集成化的 workspace。对比 OpenAI ChatGPT / Claude Web 的区别在于：它面向自部署的 agent 后端，支持本地模型（Ollama/vLLM），并提供了记忆管理、技能浏览、终端访问等 agent-specific 功能。

---

## 主要功能

| 模块 | 功能 |
|---|---|
| Chat | SSE 实时流式输出、多 session 管理、Markdown + 代码高亮、tool call 渲染、Inspector 面板 |
| Files | 完整的文件浏览器、目录导航、Monaco Editor 编辑器 |
| Terminal | 完整 PTY 终端、跨平台支持、持久化 shell session |
| Memory | 浏览/编辑 agent 记忆文件、搜索、Markdown 实时预览 |
| Skills | 浏览 2000+ 技能注册表、查看详情/分类/文档、session 级管理 |
| Settings | 8 套主题（Official/Classic/Slate/Mono，各含 light/dark）、模型切换、provider 配置 |
| Security | Auth 中间件、CSP headers、路径遍历防护、Rate limiting、可选密码保护 |
| PWA | 移动端全功能支持、可安装为桌面/手机 App、Tailscale 远程访问 |

---

## 技术栈

| 层面 | 技术 |
|---|---|
| 框架 | React 19 + TypeScript |
| 路由 | TanStack Router + TanStack Start (SSR) |
| 状态管理 | Zustand |
| 数据请求 | TanStack React Query |
| 构建 | Vite 7 + pnpm |
| UI | Tailwind CSS 4 + Base UI + Motion (动画) |
| 代码编辑 | Monaco Editor |
| 终端 | xterm.js |
| Markdown | react-markdown + shiki (语法高亮) + remark-gfm |
| 图表 | Recharts |
| 图标 | HugeIcons + Lobehub Icons |
| 测试 | Vitest + Testing Library + Playwright |
| 后端对接 | OpenAI-compatible API + Hermes Gateway SSE |
| 容器化 | Docker Compose (agent + workspace) |

---

## 仓库结构

```
src/
├── components/          # UI 组件（50+）
│   ├── agent-chat/      # 聊天面板、流式渲染、工具调用
│   ├── file-explorer/   # 文件浏览器
│   ├── terminal/        # 终端组件
│   ├── memory-viewer/   # 记忆查看器
│   ├── settings-dialog/ # 设置弹窗
│   ├── onboarding/      # 引导流程
│   ├── prompt-kit/      # 提示词工具
│   ├── inspector/       # Inspector 面板
│   ├── usage-meter/     # 用量统计
│   ├── ui/              # 通用 UI 组件
│   └── ...
├── hooks/               # 自定义 hooks (20+)
├── routes/              # 页面路由
├── screens/             # 页面组件
│   ├── chat/
│   ├── dashboard/
│   ├── files/
│   ├── terminal/
│   ├── memory/
│   ├── skills/
│   ├── settings/
│   └── jobs/
├── server/              # 后端逻辑（SSR/API）
│   ├── hermes-api.ts    # Hermes Agent API 对接
│   ├── openai-compat-api.ts  # OpenAI 兼容 API
│   ├── terminal-sessions.ts  # 终端 session 管理
│   ├── memory-browser.ts     # 记忆浏览器后端
│   ├── chat-mode.ts          # 聊天模式
│   └── auth-middleware.ts    # 认证中间件
├── stores/              # Zustand 状态
└── utils/               # 工具函数
```

---

## 架构要点

**双模式运行**：
1. **Portable 模式** -- 直接对接任何 OpenAI-compatible 后端（Ollama/LM Studio/vLLM），仅 chat 可用
2. **Enhanced 模式** -- 通过 Hermes Agent 网关，解锁全部功能（session、记忆、技能、任务、工具调用）

**通信方式**：
- Chat: SSE (Server-Sent Events) 实时流式
- Gateway API: HTTP REST
- Terminal: WebSocket (xterm.js)

---

## 与 hermes-agent 的关系

hermes-workspace 是 [hermes-agent](https://github.com/outsourc-e/hermes-agent) 的前端。hermes-agent 是 Python 编写的 AI Agent 框架/网关（基于 NousResearch/hermes-agent 的 fork），提供：
- OpenAI-compatible `/v1/chat/completions` 接口
- 扩展 API：sessions、memory、skills、config、jobs
- `hermes --gateway` 命令启动 FastAPI 网关（端口 8642）

workspace 检测网关能力后自动启用/隐藏对应功能。

---

## Roadmap

| 功能 | 状态 |
|---|---|
| Chat + SSE Streaming | 已发布 |
| Files + Terminal | 已发布 |
| Memory Browser | 已发布 |
| Skills Browser | 已发布 |
| Mobile PWA + Tailscale | 已发布 |
| 8-Theme System | 已发布 |
| Native Desktop App (Electron) | 开发中 |
| Model Switching & Config | 开发中 |
| Chat Abort / Cancel | 开发中 |
| Cloud / Hosted Version | 计划中 |
| Team Collaboration | 计划中 |

---

## 部署方式

```bash
# 本地开发
git clone https://github.com/outsourc-e/hermes-workspace.git
cd hermes-workspace
pnpm install
cp .env.example .env
# 编辑 .env 设置 HERMES_API_URL
pnpm dev  # http://localhost:3000

# Docker
docker compose up  # agent(:8642) + workspace(:3000)
```

环境变量：
- `HERMES_API_URL` -- 后端 API 地址（必填）
- `ANTHROPIC_API_KEY` -- Anthropic API key（gateway 需要）
- `HERMES_PASSWORD` -- Web UI 密码保护（可选）
