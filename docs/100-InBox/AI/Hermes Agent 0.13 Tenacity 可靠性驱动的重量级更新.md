---
title: Hermes Agent 0.13 Tenacity — 可靠性驱动的重量级更新
aliases: [Hermes Agent 0.13, Tenacity Release, Hermes Agent 3.0]
tags:
  - Hermes
  - AI-Agent
  - release-notes
  - status/active
  - type/video-note
source:
  - "https://www.youtube.com/watch?v=4XvM-0o3A-4"
author: AICodeKing
created: 2026-05-13
updated: 2026-05-13
description: AICodeKing 频道对 Hermes Agent 0.13 Tenacity 版本的更新解读，涵盖持久化 Kanban、/goal 持久目标、Checkpoints V2、安全加固、Provider 插件化、No-Agent Cron 等核心改进
level: intermediate
stars: 3
---

# Hermes Agent 0.13 Tenacity — 可靠性驱动的重量级更新

> AICodeKing 频道解读 Hermes Agent 0.13 "Tenacity" 版本，核心主题是 **可靠性（Reliability）**——让 Agent 在长时间运行中不丢状态、不偏离目标、不静默崩溃、不卡死。视频标题称 "3.0" 为吸引眼球，实际版本号为 0.13。

**注意：** 以下内容基于视频转录整理，部分功能细节未经官方文档交叉验证，具体行为以 Hermes Agent 官方文档为准。

## 目录

- [更新全景图](#更新全景图)
- [持久化多智能体 Kanban 系统](#持久化多智能体-kanban-系统)
- [/goal 持久目标](#goal-持久目标)
- [Checkpoints V2 状态持久化](#checkpoints-v2-状态持久化)
- [安全加固](#安全加固)
- [平台与 Provider 插件化](#平台与-provider-插件化)
- [No-Agent Cron 模式](#no-agent-cron-模式)
- [工具系统改进](#工具系统改进)
- [国际化与 UI](#国际化与-ui)
- [ACP 适配器与 Skills](#acp-适配器与-skills)
- [适用场景评估](#适用场景评估)

---

## 更新全景图

```
Hermes Agent 0.13 "Tenacity" 更新地图

  可靠性
  ├── 持久化 Kanban（心跳/僵尸检测/重试预算）
  ├── /goal 持久目标（跨 Turn 保持对齐）
  └── Checkpoints V2（状态恢复 + 自动续跑）

  安全
  ├── 8 个 P0 漏洞修复
  ├── Secret Redaction 默认开启
  ├── SSRF / Prompt Injection 防护
  └── 各平台 Allow List 收紧

  扩展性
  ├── Provider Plugin 系统
  ├── Platform Plugin Hooks
  └── 新模型路由（DeepSeek V4 Pro, Grok 4.3 等）

  效率
  ├── No-Agent Cron（纯脚本看门狗）
  ├── Post-write Delta Linting
  └── OpenRouter Response Caching

  体验
  ├── i18n（中/日/德/西/法/乌/土）
  ├── Dashboard / TUI 升级
  └── ACP 适配器（/steer, /q）
```

---

## 持久化多智能体 Kanban 系统

### 新增能力

| 特性 | 说明 |
|------|------|
| Heartbeats | Worker 心跳检测，确认存活 |
| Reclaim Logic | 任务回收机制，防止任务悬空 |
| Zombie Detection | 僵尸进程检测，识别无响应 Worker |
| Retry Budgets | 每任务最大重试次数限制 |
| Auto-blocking | Worker 退出未完成任务时自动阻塞 |
| Hallucination Gate | Agent 声称完成任务但 Board 状态不符时拦截 |
| Recovery UX | 幻觉检测后的恢复交互流程 |

### 架构演进

```
旧 Kanban：
  ┌──────────────────────────┐
  │  可视化看板（展示层）      │
  │  任务状态 = 人工追踪       │
  └──────────────────────────┘

新 Kanban：
  ┌──────────────────────────┐
  │  持久化工作队列（执行层）   │
  │  ├── 心跳监控             │
  │  ├── 僵尸检测 + 自动回收   │
  │  ├── 重试预算控制          │
  │  └── 幻觉门 + 状态校验     │
  │                           │
  │  多 Agent Profile 协作     │
  └──────────────────────────┘
```

**核心价值：** Kanban 从「看板」进化为「可靠的多智能体任务队列」，Worker 崩溃/卡死/消失时系统能自动检测和恢复，而非任由任务处于不一致状态。

---

## /goal 持久目标

### 解决的问题

```
长时间任务中的 Agent 聚焦流失：

  Turn 1  ──→  理解目标，开始执行
  Turn 5  ──→  被中间细节吸引，开始偏移
  Turn 10 ──→  已经忘了原始目标
  Turn 20 ──→  做了一堆无关工作
```

### 工作方式

```
/goal 持久化目标机制：

  ┌──────────┐     ┌─────────────────────┐
  │ /goal    │ →   │  持久目标写入上下文    │
  │ "重构    │     │  每个 Turn 自动注入    │
  │  支付模块"│     │  Agent 持续对齐      │
  └──────────┘     └─────────────────────┘

  ✅ 单步能力强 → 长期对齐也强
  ❌ 没有目标锚定 → 容易漂移
```

---

## Checkpoints V2 状态持久化

```
Checkpoints V2 改进：

  ┌────────────────────────────────────────┐
  │  旧 Checkpoints                        │
  │  ├── 简单状态快照                       │
  │  └── 恢复能力有限                       │
  │                                        │
  │  V2 重写                               │
  │  ├── Pruning（状态修剪，控制体积）        │
  │  ├── Discard Rails（丢弃防护栏）         │
  │  └── Auto-resume（网关重启后自动续跑）    │
  └────────────────────────────────────────┘

  适用场景：
  ├── 消息平台（Telegram/Discord）中断恢复
  ├── 后台 Agent 运行中网关重启
  └── 长时间任务断点续传
```

---

## 安全加固

### P0 漏洞修复

- 修复 8 个 P0 级安全漏洞
- Secret Redaction（密钥脱敏）默认开启

### 平台安全

| 平台 | 安全改进 |
|------|----------|
| Discord | Role Allow List 按 Guild 隔离 |
| WhatsApp | 默认拒绝陌生人，避免自聊响应 |
| Slack/Telegram/Mattermost/Matrix/DingTalk | 新增 Channel Allow List |

### 通用安全

```
安全防护矩阵：

  ├── Cloud Metadata Protection ─── 防止泄露云环境元数据
  ├── SSRF 防护 ────────────────── 阻止服务端请求伪造
  ├── Cron Prompt Injection Scan ── 定时任务提示注入扫描
  ├── Log Redaction ────────────── 调试日志自动脱敏
  ├── MCP OAuth Handling ───────── MCP OAuth 安全处理
  └── Credential Right Safety ──── 凭证权限收紧
```

---

## 平台与 Provider 插件化

### 新增平台

- **Google Chat** 作为新的消息平台

### 平台插件架构

```
平台插件化演进：

  旧模式：每个平台硬编码在核心中
  新模式：
  ┌──────────────────────────────────────┐
  │           Hermes Core                │
  │    ┌─────────────────────────┐      │
  │    │  Platform Plugin Hooks  │      │
  │    └─────────────────────────┘      │
  │         │         │         │       │
  │    ┌────▼──┐ ┌────▼──┐ ┌───▼───┐  │
  │    │Discord│ │Teams  │ │  IRC  │  │
  │    └───────┘ └───────┘ └───────┘  │
  │    ┌───────┐ ┌───────┐ ┌───────┐  │
  │    │Slack  │ │WhatsApp│ │GChat  │  │
  │    └───────┘ └───────┘ └───────┘  │
  └──────────────────────────────────────┘
  
  新适配器无需修改核心代码
  IRC / Teams 已迁移至插件风格
```

### Provider 插件

```
Provider Profile 抽象：

  ┌────────────────────────────────────┐
  │  Model Providers Plugin Directory  │
  │                                    │
  │  新增模型路由：                      │
  │  ├── DeepSeek V4 Pro               │
  │  ├── xAI Grok 4.3                  │
  │  ├── OpenRouter Owl Alpha（免费）   │
  │  └── Tencent HY3 Preview           │
  │                                    │
  │  OAuth 跨 Profile 持久化            │
  │  OpenRouter Response Caching       │
  └────────────────────────────────────┘
```

---

## No-Agent Cron 模式

### 解决的问题

```
旧模式：
  Cron 触发 → 调用 LLM → 消耗 Token → 执行脚本
  问题：简单监控不需要 AI 推理，浪费成本

新模式（No-Agent）：
  Cron 触发 → 直接运行脚本 → 输出则投递，静默则不发
  ✓ 零 Token 消耗
  ✓ 适合看门狗/心跳检测/阈值告警
```

### 运作逻辑

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  Cron 触发    │ →   │  运行脚本     │ →   │  检查输出     │
└──────────────┘     └──────────────┘     └──────────────┘
                                                │
                                    ┌───────────┴───────────┐
                                    ▼                       ▼
                              有输出                    无输出
                              直接投递                  静默（不发消息）
```

**典型用途：**

| 场景 | 脚本行为 |
|------|----------|
| 磁盘监控 | 用量 > 90% 时输出告警，否则静默 |
| 服务健康检查 | 挂了才输出，正常时沉默 |
| Git 变更检测 | 有新 commit 才通知 |
| API 可用性 | 超时/报错时告警 |

---

## 工具系统改进

### Post-write Delta Linting

```
文件写入后自动检查：

  Agent 写入 config.yaml
       │
       ▼
  Delta Linter 自动触发
       │
       ├── Python / JSON / YAML / TOML
       ├── 仅检查本次变更引入的新错误
       ├── 旧错误自动过滤（不干扰）
       └── 语法错误立即 Surface 给 Agent
```

**关键设计：** 只报告**新引入**的错误，忽略文件中已有的旧问题。避免 Agent 被无关错误淹没。

### MCP 改进

| 改进 | 效果 |
|------|------|
| SSE Transport 支持 | 连接方式更灵活 |
| OAuth Forwarding for SSE | SSE 通道认证 |
| Stale Pipe Retries | 断管自动重连 |
| Keep-alive Improvements | 长连接更稳定 |
| Image Tool Results | 多模态工具输出更好 |

### 新工具

- **Video Analyze** — Gemini 及兼容多模态模型的视频理解
- **xAI Custom Voices** — TTS 提供商，支持语音克隆

---

## 国际化与 UI

### 语言支持

静态 Gateway 和 CLI 消息新增：中文、日文、德文、西班牙文、法文、乌克兰文、土耳其文。文档站新增中文 Locale。

### Dashboard / TUI 升级

- Model Picker 匹配 Hermes 模型流 + 内联关闭
- Startup Banner 可折叠区块
- Status Bar 显示 Context Compression 计数
- Plugins 页面 / Profiles 管理页
- 可排序 Analytics 表格
- Reverse Proxy 支持
- 新默认 Large 主题

---

## ACP 适配器与 Skills

### IDE 集成

| IDE | 新增能力 |
|-----|----------|
| Zed | /steer, /q |
| VS Code | /steer, /q |
| JetBrains | /steer, /q |

```
/steer 和 /q 的价值：

  /steer ─── 实时引导正在运行的 Agent（不中断）
  /q ────── 排队后续工作（Agent 完成后自动执行）

  对比直接 /stop：
  ├── /stop → 中断当前任务，可能丢失进度
  ├── /steer → 不中断，追加方向修正
  └── /q → 不中断，排队下一个任务
```

### 新 Skills

- Shopify（电商）
- Here Now（个人购物助手）
- Anthropic Financial Services（金融服务）
- Kanban Video Orchestrator（视频编排）
- SearXNG Search（自托管搜索，连接 Web 工具链拆分）

---

## 适用场景评估

### ✅ 适合使用 Hermes 的场景

```
需要以下能力的用户会受益最大：

  ├── 多 Profile + 多平台（Telegram/Discord/Slack...）
  ├── 定时任务（Cron / No-Agent Cron）
  ├── Kanban 工作流（多智能体协作）
  ├── 插件扩展（Provider / Platform / Skills）
  ├── 长时间后台任务
  └── 本地部署 + 数据隐私
```

### ❌ 可能过重的场景

```
以下需求用更简单的工具更合适：

  ├── 只需要基础 AI 编程助手
  ├── 不需要多平台 / 定时任务
  └── 不需要 Agent 自主运行
  → Cursor / Copilot / 基础 ChatGPT 即可
```

---

## 参考资料

- [Hermes Agent 官方文档](https://hermes-agent.nousresearch.com/docs)
- [Hermes Agent GitHub](https://github.com/nousresearch/hermes-agent)

## 相关笔记

- [[AI Agent 自主智能体]]
- [[MCP Model Context Protocol]]
