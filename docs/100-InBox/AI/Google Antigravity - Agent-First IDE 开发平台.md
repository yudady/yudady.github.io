---
title: "Google Antigravity - Agent-First IDE 开发平台"
aliases: [Antigravity, Anti-Gravity, Google AG]
tags:
  - ai-ide
  - google
  - agent
  - claude-code
  - mcp
  - coding-agent
  - status/active
source:
  - "https://www.youtube.com/watch?v=juR2hQL5ShI"
  - "https://antigravity.google/"
  - "https://developers.googleblog.com/build-with-google-antigravity-our-new-agentic-development-platform/"
author: "Google (视频: JayLuxAI | AI 自動化)"
created: 2026-05-17
updated: 2026-05-17
description: Google 的 Agent-First IDE，融合 Jules 编码 Agent，支持多模型、MCP、浏览器控制，免费使用
level: intermediate
stars: 4
---

# Google Antigravity - Agent-First IDE 开发平台

> Google Antigravity 不只是一个编辑器，而是一个 Agent-First 开发平台——让 Agent 自主规划、执行、验证复杂任务，跨编辑器、终端、浏览器协同工作。2025 年 11 月发布，现已与 Jules 合并，预计 Google I/O 正式宣布。

---

## 目录

1. [核心定位](#核心定位)
2. [与 Jules 合并](#与-jules-合并)
3. [关键更新亮点](#关键更新亮点)
4. [权限与安全体系](#权限与安全体系)
5. [设置面板详解](#设置面板详解)
6. [Agent Manager](#agent-manager)
7. [模型与速率限制](#模型与速率限制)
8. [跨面协同](#跨面协同)
9. [与竞品对比](#与竞品对比)
10. [适用场景](#适用场景)

---

## 核心定位

Antigravity 解决的核心问题：**AI 编码 Agent 不应只是侧边栏的聊天机器人，而应有专属工作空间**。

```
传统 AI IDE:
┌────────────────────────────┐
│  编辑器          │ Agent (侧边栏) │
│  ┌──────────────┐ ┌──────────┐ │
│  │ 代码         │ │ 聊天     │ │
│  │              │ │          │ │
│  └──────────────┘ └──────────┘ │
│  Agent 只能看/改代码           │
└────────────────────────────────┘

Antigravity:
┌────────────────────────────────┐
│         Agent Manager          │
│  ┌────────┬────────┬────────┐  │
│  │ 编辑器  │ 终端   │ 浏览器  │  │
│  │ 代码    │ 命令   │ 验证   │  │
│  └────────┴────────┴────────┘  │
│  Agent 跨三个面自主协同         │
└────────────────────────────────┘
```

**核心特性**：
- Agent Manager：多 Agent 编排与任务控制
- 跨面协同（Cross-Surface）：编辑器 + 终端 + 浏览器同步
- Artifacts 验证：Agent 产出可交付物（任务列表、计划、截图、浏览器录制）
- 知识库：Agent 可保存上下文和代码片段供后续任务复用

---

## 与 Jules 合并

**Jules** 是 Google 的自主编码 Agent（jules.google），现已并入 Antigravity：

| 维度 | 合并前 | 合并后 |
|------|--------|--------|
| Jules | 独立 Agent，处理编码任务 | 融入 Antigravity Agent Manager |
| Antigravity | IDE + Agent | IDE + Jules Agent + 在线运行能力 |
| 能力 | 本地编辑 | 本地 + 远程 Agent 执行 |

**影响**：用户可在 Antigravity 内直接让 Jules 式 Agent 处理端到端任务，无需切换工具。

---

## 关键更新亮点

从 Changelog 看，Antigravity 并非「已废弃」，而是在**持续打磨核心基础设施**：

| 时间 | 更新 | 意义 |
|------|------|------|
| 2025.11 | 首次发布 | 公测上线 |
| 2025.12 | Secure Mode | 所有 Agent 操作需人工审核 |
| 2026.02 | 模型配额可见 + Strict Mode | 透明度提升 |
| 2026.02 | 终端沙箱（macOS） | 安全隔离 |
| 2026.03 | 终端沙箱（Linux） | 跨平台安全 |
| 2026.03 | MCP 认证改进 + Rules 支持 | 工具生态完善 |
| 2026.04.07 | 统一 Agent 权限系统 | 权限控制标准化 |
| 2026.04.16 | Bug 修复（MCP 加载、workspace 设置） | 核心管道修复 |

**关键 Bug 修复**：
- ✅ 回滚操作偶尔会删除 Agent 编辑的文件（2 月修复）
- ✅ MCP Server 加载失败（4 月修复）
- ✅ Workspace 特定设置不可访问（4 月修复）

---

## 权限与安全体系

Antigravity 构建了四层递进的权限控制：

```
权限层级（从低到高）:

Level 1: Strict Mode（严格模式）
├── 禁止 Agent 自主执行定向漏洞利用
├── 所有 Agent 操作需人工审核
└── 最高安全级别

Level 2: Review Policy（审核策略）
├── 可配置哪些操作需要审核
└── 终端命令 / 文件编辑 / 浏览器操作分别控制

Level 3: Terminal Sandbox（终端沙箱）
├── macOS + Linux 均已支持
├── Agent 命令在隔离环境执行
└── 防止恶意命令影响宿主系统

Level 4: Browser JS Policy（浏览器 JS 策略）
├── Disabled: 永不执行 JS
├── Request Review: 每次执行前询问
└── Always Proceed: 自由执行（最高风险）
```

**浏览器 URL 白名单**：可控制 Agent 能访问哪些域名/URL。

---

## 设置面板详解

更新后的设置面板分为五大模块：

### 1. Agent Settings（Agent 设置）
- Strict Mode 开关
- Review Policy 配置
- 终端命令自动执行策略
- Shell 集成
- 权限细化控制

### 2. Notification Settings（通知）
- 任务完成通知
- 可配置触发条件

### 3. Models（模型）
- 各模型速率限制透明展示
- 可添加 AI Credits 超出限制
- 速率限制约 4 小时刷新
- 免费版完整可用

### 4. Customizations（自定义）
- **Skills**：支持从目录自动发现，兼容 Claude Code Skills
- **MCP Servers**：Google 官方 MCP Server 直接可选
- 支持自定义 Provider 和模型（Linux）

### 5. Browser（浏览器控制）
- 浏览器工具开关
- JS 执行策略
- URL 白名单
- Tab 和编辑器选项

---

## Agent Manager

Agent Manager 是 Antigravity 的核心界面：

```
┌──────────────────────────────────────────┐
│  Agent Manager                           │
│  ┌─────────────────────────────────────┐ │
│  │ Model: Gemini 3.1 Pro ▼            │ │
│  │ Project: ~/my-app/           [切换]  │ │
│  │ Environment: Local ▼               │ │
│  └─────────────────────────────────────┘ │
│                                          │
│  ┌─────────────────┬───────────────────┐ │
│  │ Conversation    │ Changes Pane      │ │
│  │ History         │ ┌───────────────┐ │ │
│  │                 │ │ 文件变更列表   │ │ │
│  │  - Session 1    │ │ + 添加行       │ │ │
│  │  - Session 2    │ │ - 删除行       │ │ │
│  │  - Session 3    │ └───────────────┘ │ │
│  │                 │                    │ │
│  │                 │ [Review] [Rename]  │ │
│  └─────────────────┴───────────────────┘ │
└──────────────────────────────────────────┘
```

**新增功能**：
- **Changes Pane**：一键查看 Agent 本次会话的所有文件变更
- **Review**：在变更 walkthrough 中标注问题，Agent 自动修复
- **Conversation Rename**：重命名对话便于管理
- **Environment 选项**：目前仅 Local，Remote 即将推出
- **Project 切换**：类似 Codex，可快速切换项目目录

---

## 模型与速率限制

| 特性 | 详情 |
|------|------|
| 主力模型 | Gemini 3.1 Pro |
| 其他模型 | Gemini 3 Flash, Claude Sonnet 4.5, GPT-OSS |
| 自定义模型 | Linux 支持（如 Llama） |
| 速率限制 | 透明展示，约 4h 刷新周期 |
| AI Credits | 可购买超出免费限额 |
| 免费版 | 完整功能可用 |

**透明度**是 Antigravity 相比竞品的优势——用户能清楚看到每个模型的剩余配额。

---

## 跨面协同

Antigravity 的独特卖点是 **Agent 跨编辑器、终端、浏览器三个面协同工作**：

```
典型工作流:

1. Agent 接收任务
   "创建一个 React 登录页面"
       │
       ▼
2. 编辑器面 ─── 编写代码
       │
       ▼
3. 终端面 ──── npm install, npm run dev
       │
       ▼
4. 浏览器面 ─── 打开 localhost 验证
   ├── 截图 → Artifact
   ├── 发现问题 → 回到编辑器修复
   └── 修复完成 → 再次浏览器验证
       │
       ▼
5. 产出 Artifact
   ├── 实现计划
   ├── 截图验证
   └── 测试结果
```

**Artifact 验证**（而非看日志）：
- Agent 产出可交付物：任务列表、实现计划、截图、浏览器录制
- 用户可直接在 Artifact 上标注反馈
- Agent 根据反馈调整，无需中断执行流

---

## 与竞品对比

| 维度 | Google Antigravity | Cursor | Windsurf | Claude Code |
|------|-------------------|--------|----------|-------------|
| 定位 | Agent-First IDE | AI Code Editor | AI Code Editor | CLI Agent |
| 多 Agent 编排 | ✅ Agent Manager | ✅（有限） | ❌ | ❌ |
| 跨面协同 | ✅ 编辑器+终端+浏览器 | 部分 | 部分 | 终端+浏览器 |
| MCP 支持 | ✅ | ✅ | ✅ | ✅ |
| 浏览器控制 | ✅ 内置 | 插件 | 插件 | 内置 |
| 终端沙箱 | ✅ macOS+Linux | ❌ | ❌ | ❌ |
| 模型选择 | Gemini + Claude + GPT | 多模型 | 多模型 | Claude only |
| 价格 | 免费 | $20/月 | $15/月 | $20/月（API） |
| Artifacts 验证 | ✅ | ❌ | ❌ | ❌ |
| 知识库 | ✅ 内置 | ❌ | ❌ | ✅（Memory） |
| 平台 | macOS + Windows + Linux | 全平台 | 全平台 | 全平台 |
| 成熟度 | 公测中（快速迭代） | 成熟 | 成熟 | 成熟 |

**Antigravity 优势**：免费 + 跨面协同 + Artifacts + 多模型
**Antigravity 劣势**：成熟度不足、创新速度慢于 Cursor、UI 仍有 Windsurf 痕迹

---

## 适用场景

**推荐使用**：
- 需要多 Agent 编排的复杂项目
- 需要浏览器验证的前端开发
- 想免费体验 Agent-First IDE
- 已在使用 Google 生态（Gemini、AI Studio）
- 需要 MCP + Skills 扩展能力

**暂不推荐**：
- 需要最高稳定性（仍在快速迭代）
- 深度依赖 Cursor/Windsurf 插件生态
- 需要 Remote Agent 执行（尚未完全推出）

---

## 总结

Antigravity 的定位清晰：**Agent-First IDE，不是 AI 补丁的编辑器**。从 Changelog 看，Google 在持续打磨核心管道——权限、沙箱、MCP 可靠性、Artifacts 验证，这些「无聊但关键」的改进让产品从 demo 走向可用。

虽然创新速度仍落后于 Cursor，但免费 + 多模型 + 跨面协同的组合很有竞争力。如果你之前只在发布时试用过，现在值得重新打开。

---

## 参考资料

- [官网: antigravity.google](https://antigravity.google/)
- [Google Developer Blog 发布文](https://developers.googleblog.com/build-with-google-antigravity-our-new-agentic-development-platform/)
- [Jules 自主编码 Agent](https://jules.google/)
- [Reddit: r/google_antigravity](https://www.reddit.com/r/google_antigravity/)
- [TheNewStack 评测](https://thenewstack.io/hands-on-with-antigravity-googles-newest-ai-coding-experiment/)
- [YouTube: JayLuxAI 介绍视频](https://www.youtube.com/watch?v=juR2hQL5ShI)

## 相关笔记

- [[Claude Code]]
- [[Cursor AI Editor]]
- [[MCP Model Context Protocol]]
- [[AI 编码 Agent 生态]]
