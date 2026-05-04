---
title: Hermes Agent v0.11 & v0.12 — Kanban 多智能体工作流
aliases:
  - Hermes Kanban Tutorial
  - Hermes Agent 多智能体协调
tags:
  - hermes-agent
  - multi-agent
  - kanban
  - status/active
  - type/note
source:
  - "https://www.youtube.com/watch?v=8beheGoYTHM"
  - "https://hermes-agent.nousresearch.com/docs/user-guide/features/kanban-tutorial"
author: AICodeKing (视频作者) / NousResearch (Hermes 开发团队)
created: 2026-05-04
updated: 2026-05-04
description: Hermes Agent v0.11（Interface Release）和 v0.12（Curator Release）核心变更，以及 Kanban 持久化任务板的多智能体协调设计
level: intermediate
stars: 4
---

# Hermes Agent v0.11 & v0.12 — Kanban 多智能体工作流

> Hermes 正从单纯的聊天式 Agent 转向持久化多智能体协调系统。v0.11 重构了交互界面和 Provider 架构，v0.12 引入了自主 Curator 和 Kanban 任务板，让不同 Agent Profile 之间的工作可以跨越时间、重启和角色边界持续推进。

---

## 目录

- [v0.11 — Interface Release](#v011--interface-release)
- [v0.12 — Curator Release](#v012--curator-release)
- [Hermes Kanban 核心概念](#hermes-kanban-核心概念)
- [delegate_task vs Kanban](#delegate_task-vs-kanban)
- [四大实战场景](#四大实战场景)
  - [Story 1：单人功能开发（依赖链）](#story-1单人功能开发依赖链)
  - [Story 2：Fleet Farming（并行专业队）](#story-2fleet-farming并行专业队)
  - [Story 3：角色流水线 + 重试](#story-3角色流水线--重试)
  - [Story 4：熔断器与崩溃恢复](#story-4熔断器与崩溃恢复)
- [适用场景与限制](#适用场景与限制)
- [快速上手命令](#快速上手命令)
- [参考资料](#参考资料)

---

## v0.11 — Interface Release

核心主题：**界面重构 + Provider 架构解耦 + 更多推理路径**。

**Ink-based TUI 重写**
- 基于 React + Ink 的全新交互式 CLI
- Sticky composer（输入框始终可见）
- Live streaming、better picker keys
- Status bar + Light theme
- Sub-agent 生成时的可见性增强

**可插拔 Transport 架构**
```
Provider
  └── Transport Layer（新增抽象层）
        ├── HTTP
        ├── AWS Bedrock (Converse API)
        └── ...（新的 provider 只需实现 transport）
```

**新增推理路径**

| Provider | 类型 |
|----------|------|
| NVIDIA NIM | 云端推理 |
| RCAI Step Plan | 推理规划 |
| Google Gemini CLI | 本地/云端 |
| Ollama | 本地推理 |
| Vercel AI Gateway | 网关代理 |
| GPT 5.5 (Codex Ollama) | 带实时模型发现 |

**其他改进**
- QQBot adapter、Plugin 系统扩展
- `/steer` 命令：运行中 Agent 的下一个 tool call 后注入指令
- Shell hooks、Webhook、Direct delivery mode
- Orchestrator 风格 sub-agent（更智能的 delegation）
- Auxiliary model 配置（辅助模型独立设置）
- 更可扩展的 Dashboard

---

## v0.12 — Curator Release

核心主题：**自主维护 + 冷启动加速 + 更多集成**。

**自主 Curator（最大亮点）**

后台 Agent 按计划自动完成 Skill 库的维护：
- **评分（Grade）**：按 rubric 评估 skill 质量
- **修剪（Prune）**：移除过时/重复的 skill
- **合并（Consolidate）**：将相似 skill 整合

自改进循环升级：
- Background review fork 更 rubric 化
- 优先更新刚使用过的 skill
- 支持引用文件和模板文件
- 继承父 runtime（provider/model/credentials）

**新增 Provider**

| Provider | 说明 |
|----------|------|
| Groq Cloud | 低延迟推理 |
| Azure AI Foundry | 企业级推理 |
| MiniMax (Ollama) | 多模态 |
| 10Cent TokenHub | Token 管理 |
| LM Studio | 一等公民支持 |

**新平台与集成**
- Microsoft Teams（首个 plugin 平台）
- WeChat Bot（新增消息平台）
- 原生 Spotify 工具
- Google Meet 插件
- ComfyUI + TouchDesigner MCP（默认打包）
- Dashboard Models tab
- 本地 Piper TTS（cell sandbox 支持）

**性能**
- 冷启动提升约 **57%**

---

## Hermes Kanban 核心概念

**不是可视化看板，是持久化协调层。**

```
┌─────────────────────────────────────────────────┐
│              ~/.hermes/kanban.db (SQLite)         │
│                                                   │
│  Task: status, assignee, parent/child deps,       │
│        comments, run_history, handoff_data         │
│                                                   │
│  ┌─────────┐  ┌──────┐  ┌───────┐  ┌──────────┐  │
│  │ TRIAGE  │→ │ TODO │→ │ READY │→ │IN PROGRESS│  │
│  └─────────┘  └──────┘  └───────┘  └────┬─────┘  │
│                                            │        │
│         ┌──────────────┐  ┌──────┐        │        │
│         │    BLOCKED   │←┘      │  ┌─────▼─────┐  │
│         └──────────────┘       └─→│    DONE    │  │
│                                └───────────┘     │
└─────────────────────────────────────────────────┘
```

**六列状态流**

| 列 | 含义 |
|----|------|
| Triage | 粗略想法，待完善 spec |
| Todo | 已创建，等依赖或未分配 |
| Ready | 已分配，等 dispatcher 认领 |
| In Progress | Worker 正在执行 |
| Blocked | 需人工输入 / 熔断器触发 |
| Done | 完成 |

**关键设计**

- **结构化交接（Structured Handoff）**：完成任务时附带 `--summary` + `--metadata`，下游 Worker 直接读取，无需翻阅历史对话
- **依赖提升引擎**：父任务完成 → 自动将子任务从 Todo 提升到 Ready
- **Run History**：每次尝试独立记录（outcome/summary/error/metadata），重试历史是一等公民数据

---

## delegate_task vs Kanban

```
delegate_task                    Kanban
┌──────────────┐                ┌──────────────────────────┐
│ 类比：函数调用 │                │ 类比：工作队列（Work Q）   │
│              │                │                          │
│ • 短生命子 agent│               │ • 持久化任务               │
│ • 同步等待结果 │                │ • 跨 agent 角色交接       │
│ • 无重试/审计  │                │ • 可重试、可 block、可审计  │
│ • 结果直接返回 │                │ • 重启后恢复              │
│              │                │ • 可需人工介入             │
└──────────────┘                └──────────────────────────┘
```

| 维度 | delegate_task | Kanban |
|------|---------------|--------|
| 生命周期 | 同步，函数级 | 持久化，跨 session |
| 适用规模 | 单个小问题 | 多步骤、多角色、长时间 |
| 上下文传递 | 参数直接传入 | 结构化 summary + metadata |
| 失败处理 | 无自动重试 | 熔断器 + 崩溃恢复 |
| 可视性 | 无 | Dashboard 实时看板 |
| 适用场景 | 快速子任务 | 研发→实现→审核、内容流水线 |

---

## 四大实战场景

### Story 1：单人功能开发（依赖链）

**场景**：开发一个 Auth 功能，三步走 — 设计 Schema → 实现 API → 写集成测试。

```
[Design Schema] ──complete──→ [Implement API] ──complete──→ [Write Tests]
   Ready                          Todo→Ready                      Todo→Ready
 (backend-dev)                   (backend-dev)                  (qa-dev)
   │                               │                              │
   └── summary: 表结构设计 ────────→│                              │
   └── metadata: changed_files ────→│                              │
                                   └── summary: API 实现 ─────────→│
                                   └── metadata: endpoints ───────→│
```

**核心机制**：
- 子任务依赖父任务，只有 Schema 是 Ready 状态
- Schema 完成 → 依赖引擎自动将 API 提升到 Ready
- API Worker 通过 summary/metadata 获取上游设计决策，无需重读长对话

**最佳实践**：
- ✅ 每次 complete 都附带 `--summary` 和 `--metadata`
- ✅ metadata 包含 changed_files、decisions、tests_run 等结构化数据
- ❌ 不要绕过依赖链手动将子任务设为 Ready

---

### Story 2：Fleet Farming（并行专业队）

**场景**：三个专业 Worker（翻译、转录、文案）处理一堆独立任务。

```bash
# 创建翻译任务
hermes kanban create "Translate homepage to Spanish" \
  --assignee translator --tenant content-ops

# 创建转录任务
hermes kanban create "Transcribe Q3 customer call #1" \
  --assignee transcriber --tenant content-ops

# 创建文案任务
hermes kanban create "Generate product description: SKU-1001" \
  --assignee copywriter --tenant content-ops

# 启动 gateway，dispatcher 自动分发
hermes gateway start
```

**Dashboard 视图**（Lanes by Profile 开启）：
- In Progress 列按 Profile 分组，一眼看到每个 Worker 在干什么
- 多个 Worker 并行消费各自的 Ready 队列

**适用场景判断**：
- ✅ 任务之间无依赖，可完全并行
- ✅ 需要追踪每个 Worker 的进度和产出
- ❌ 任务有严格先后顺序 → 用 Story 1 的依赖链

---

### Story 3：角色流水线 + 重试

**场景**：PM 写 spec → 工程师实现 → Reviewer 审核。首次实现被驳回，工程师根据反馈重试。

```
┌──────────┐    ┌──────────────┐    ┌──────────────┐
│  PM      │    │  Engineer    │    │  Reviewer    │
│ 写 Spec  │───→│  实现        │───→│  审核        │
│          │    │              │    │              │
│ Run 1:   │    │ Run 1:       │    │              │
│ completed│    │ BLOCKED ❌   │    │ 反馈:         │
│          │    │ (密码强度缺失) │    │ - 缺 strength│
│          │    │              │    │ - link 可重放  │
│          │    │ Run 2:       │    │              │
│          │    │ COMPLETED ✅ │    │              │
│          │    │ (修复后)      │    │              │
└──────────┘    └──────────────┘    └──────────────┘
```

**关键设计**：
- 每次 Run 独立记录 outcome、summary、error、metadata
- 重试 Worker 在 context 中看到之前失败的原因
- Reviewer 的 Worker 在审核前就能看到 `changed_files` 和 `tests_run`
- `review_iteration: 2` 这样的 metadata 记录迭代次数

**与 chat-based agent 的区别**：
- 传统 Agent：重试历史埋在聊天记录里，难以结构化检索
- Kanban：重试历史是**一等公民数据**，每次 attempt 是独立行

---

### Story 4：熔断器与崩溃恢复

**两道防线**：

```
Worker 失败
    │
    ├── 启动失败（spawn_failed）
    │     │
    │     └── 重试 → 仍失败 → 重试 → ...（最多 N 次）
    │           │
    │           └── 超过 failure_limit（默认 3）
    │                 │
    │                 ▼
    │           Circuit Breaker 触发
    │           Task → BLOCKED (gave_up)
    │           发送通知到 Telegram/Discord
    │
    └── 运行中崩溃（crashed）
          │
          └── Dispatcher 检测到 PID 消失
                │
                ├── 释放 claim
                ├── Task → READY（等待新 Worker）
                └── Run History 记录 crash + error
```

**熔断器（Circuit Breaker）**
- 默认 3 次连续失败后触发
- Task 移入 Blocked，outcome 为 `gave_up`
- 防止无限重试浪费资源
- 通过 Gateway 通知到连接的消息平台

**崩溃恢复（Crash Recovery）**
- Dispatcher 轮询 `kill(pid, 0)` 检测进程存活
- 进程消失 → 释放 claim → Task 回到 Ready
- 新 Worker 可以从 Run History 中看到上次的崩溃原因

**实际案例**：
- Run 1: `crashed` — `OOM kill at row 2.3M`
- Run 2: `completed` — metadata: `strategy: chunked with LIMIT + WHERE id > last_id`
- 重试 Worker 主动选择了分块策略避免再次 OOM

---

## 适用场景与限制

**适合用 Kanban 的场景**

| 场景 | 原因 |
|------|------|
| 研究 → 写作 → 审核 | 多角色、多步骤 |
| PM → 工程师 → Reviewer | 需要审计追踪 |
| 内容批量处理（翻译/转录/文案） | 并行队列 + 可见性 |
| 运维监控 | 长时间运行 + 失败恢复 |
| 周期性工作 | 持久化 + 可恢复 |

**适合用 delegate_task 的场景**
- 快速子任务，单次调用
- 不需要审计追踪
- 不需要跨 session 持久化

**当前限制**

| 限制 | 说明 |
|------|------|
| 单机设计 | Board 是本地 SQLite，不支持多服务器共享 |
| 安全注意 | Dashboard 监听 0.0.0.0 时 plugin 路由可能暴露到网络 |
| 非企业级 | 不是多服务器工作流引擎，是本地协调层 |

---

## 快速上手命令

```bash
# 初始化 + 启动
hermes kanban init              # 首次使用（可选，首次命令会自动初始化）
hermes dashboard                # 打开 http://127.0.0.1:9119
hermes gateway start            # 启动内嵌 dispatcher

# 任务管理
hermes kanban create "任务名" --assignee worker --tenant my-project
hermes kanban claim <task-id>   # 认领任务
hermes kanban complete <id> --summary "摘要" --metadata '{...}'
hermes kanban block <id> "原因"
hermes kanban unblock <id>
hermes kanban show <task-id>    # 查看详情
hermes kanban runs <task-id>    # 查看运行历史

# 实时监控
hermes kanban watch --kinds completed,gave_up,timed_out
hermes kanban notify-subscribe <task-id> --platform telegram --chat-id <id>
```

---

## 参考资料

- [Hermes Agent v0.12 Release Notes](https://github.com/NousResearch/hermes-agent/blob/main/RELEASE_v0.12.0.md)
- [Kanban Tutorial — Hermes Docs](https://hermes-agent.nousresearch.com/docs/user-guide/features/kanban-tutorial)
- [Hermes Agent Releases](https://github.com/NousResearch/hermes-agent/releases)
- [NousResearch X: Hermes Agent multi-agent via Kanban](https://x.com/NousResearch/status/2050997692977844324)

## 相关笔记

- [[Hermes Agent]] — 项目主笔记（如有）
