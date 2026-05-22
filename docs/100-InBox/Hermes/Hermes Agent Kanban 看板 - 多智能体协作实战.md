---
title: Hermes Agent Kanban 看板 — 多智能体协作实战
aliases: [Hermes Kanban, Hermes 看板]
tags:
  - hermes-agent
  - kanban
  - multi-agent
  - ai-agent
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=0kgye6cvq6k"
  - "https://hermes-agent.nousresearch.com/docs/user-guide/features/kanban"
  - "https://hermes-agent.nousresearch.com/docs/user-guide/features/kanban-tutorial"
author: AI 雷达站
created: 2026-05-18
updated: 2026-05-18
description: |
  Hermes Agent 看板功能完整教程：5 个智能体角色协作开发项目，覆盖看板创建、任务流转、阻塞干预、通知监控与 Profile 配置。
level: intermediate
stars: 4
---

# Hermes Agent Kanban 看板 — 多智能体协作实战

> Hermes Kanban 是一个基于 SQLite 的持久化任务看板，让多个命名 Profile（智能体）协作完成复杂项目。本笔记基于视频实战演示 + 官方文档整理，覆盖团队配置、看板操作、任务生命周期和远程监控。

## 目录

- [[#Kanban vs 普通 Agent]]
- [[#团队角色与 Profile 配置]]
- [[#看板操作：创建与任务流转]]
- [[#任务生命周期与状态机]]
- [[#阻塞处理与人工干预]]
- [[#远程监控与通知]]
- [[#协作模式参考]]

---

## Kanban vs 普通 Agent

### 核心区别

| 特性 | 普通 Agent（delegate_task） | Kanban 看板 |
|------|---------------------------|-------------|
| 形态 | RPC 调用（fork → join） | 持久化消息队列 + 状态机 |
| 生命周期 | 子代理随父代理结束 | 任务持久化到 SQLite，重启不丢 |
| 人工干预 | 不支持 | 随时评论 / 解除阻塞 / 重新分配 |
| 审计追踪 | 上下文压缩后丢失 | 完整事件日志永久留存 |
| 多代理协作 | 单次调用 = 单个子代理 | N 个代理参与同一任务全生命周期 |
| 父子任务 | 无依赖关系 | 父任务完成后自动激活子任务 |

### 架构概览

```
用户 / Orchestrator（PM）
         │
         ▼
    ┌─────────┐
    │  Kanban  │ ← ~/.hermes/kanban.db (SQLite)
    │  Board   │
    └────┬─────┘
         │ Dispatcher（Gateway 内嵌，60s 一次 tick）
         │
    ┌────┼──────────────────────┐
    ▼    ▼         ▼            ▼
  Alice  Danny    Ray          Tess
 (架构师)(开发)  (Code Review) (测试)
```

### 判断决策树

```
选 delegate_task 如果你需要：
  ✅ 短时间推理结果返回给父代理
  ✅ 无需人工干预
  ✅ 一次性任务

选 Kanban 如果你需要：
  ✅ 任务跨代理边界、需持久化
  ✅ 可能需要人工介入
  ✅ 多代理接力（开发 → Review → 测试）
  ✅ 需要事后审计和回溯
```

---

## 团队角色与 Profile 配置

### 视频演示的 5 角色团队

| 角色 | Profile 名 | 职责 | SOUL.md 定位 |
|------|-----------|------|-------------|
| 产品经理（PM） | Paul | 接收需求 → 拆分用户故事 | 需求分析与任务分解 |
| 架构师 | Alice | 用户故事 → 开发/Review/测试任务 | 技术方案设计与任务分配 |
| 开发 | Danny | 执行开发任务 | 编码实现 |
| Code Review | Ray | 审查代码质量 | 代码审查 |
| 测试 | Tess | 执行测试任务 | 质量保证 |

### 创建 Profile

```bash
# 从主 Agent 克隆（自动复制 API Key 等配置）
hermes profile create "Alice" --clone
hermes profile create "Danny" --clone
hermes profile create "Ray" --clone
hermes profile create "Tess" --clone

# 每个 Profile 需要编写 SOUL.md 定义角色人格和职责
# 路径: ~/.hermes/profiles/<name>/SOUL.md
```

### Gateway 启动

```bash
# 只需启动 PM（Paul）的 Gateway，其余 Profile 由 Dispatcher 按需拉起
hermes -p Paul gateway start
```

> 只启动 PM 的 Gateway 即可，其他 Profile 由 Dispatcher 在分配任务时自动 spawn。

### 最佳实践

- ✅ 用 `--clone` 从主 Profile 克隆，避免重复配置 API Key
- ✅ SOUL.md 中明确角色边界，避免角色越权
- ✅ 架构师角色负责拆分任务，不直接执行开发
- ❌ 不要给每个 Profile 都启动 Gateway，只需启动 Orchestrator

---

## 看板操作：创建与任务流转

### 三种创建看板的方式

| 方式 | 操作 | 适用场景 |
|------|------|----------|
| 对话创建 | 在 TUI 中告诉 PM "创建看板 DevTeam02" | 最简单 |
| Dashboard | 打开 Dashboard → Kanban 标签 → 创建 | 可视化操作 |
| 命令行 | `hermes kanban init` | 脚本 / 自动化 |

### 看板泳道布局

```
┌────────┬────────┬───────┬────────┬────────┬──────────┐
│ 待分类 │  待办  │  就绪 │ 进行中 │  阻塞  │  已完成  │
│triage  │  todo  │ ready │running │blocked │   done   │
└────────┴────────┴───────┴────────┴────────┴──────────┘
```

- **待分类（triage）**：保存想法的地方，不是智能体工作泳道
- **待办（todo）**：实际需要智能体执行的任务
- **就绪（ready）**：前置依赖已完成，等待 Dispatcher 分配
- **进行中（running）**：正在执行
- **阻塞（blocked）**：需要人工干预
- **已完成（done）**：任务完成

### 任务流转流程

```
用户需求 → PM 创建用户故事（triage）
    │
    ▼ 拖到 todo（或 Auto Decompose）
Alice 架构师拆分 → 开发任务 + Review 任务 + 测试任务
    │
    ▼ 父子依赖链接
Dispatcher 按 60s tick 自动调度 → Profile 认领执行
    │
    ▼
开发完成 → Code Review → 测试 → 全部 done
```

### 代码示例

```bash
# 初始化看板
hermes kanban init

# 创建任务（分配给 PM）
hermes kanban create "开发 Todo List Web APP" \
  --assignee Paul \
  --body "使用 Next.js + SQLite，邮箱密码登录"

# 查看所有任务
hermes kanban list

# 查看特定任务详情（含父子关系）
hermes kanban show <task_id>

# 查看统计
hermes kanban stats
```

### 注意事项

- ❌ 待分类泳道的任务**不会被自动调度**，需拖到待办
- ✅ 待分类 → 拖到待办 → 点击"触发调度器"即可启动
- ✅ 父子任务用数字标识：`0/2` = 0 完成 / 共 2 子任务，`1/1` = 全部完成

---

## 任务生命周期与状态机

### 状态流转

```
           ┌──────────────────────────────────┐
           │                                  │
           ▼                                  │
  triage → todo → ready → running → done
                      ↑    │       ↑    │
                      │    │       │    │
                      │    ▼       │    ▼
                      │  blocked ──┘    │
                      │    │            │
                      └────┘ (unblock)  │
                                       │
                                  archived
```

### 关键特性

| 特性 | 说明 |
|------|------|
| 消息持久化 | 所有状态变更写入 SQLite，重启不丢 |
| 状态机 | 严格的状态流转规则，防止非法跳转 |
| 父子依赖 | 父任务 done 后自动 promote 子任务到 ready |
| 多次尝试 | 同一任务可被多个代理多次执行（run 历史） |
| 结构化交接 | `kanban_complete` 时传递 summary + metadata 给下游 |

### Worker 的典型工具调用序列

```
1. kanban_show()           → 读取任务详情 + 父任务交接 + 评论
2. terminal/file 工具      → 执行实际工作
3. kanban_heartbeat()      → 长任务中发心跳（每几分钟）
4. kanban_complete(        → 完成并交接
     summary="...",
     metadata={"changed_files": [...], "tests_run": 14}
   )
```

---

## 阻塞处理与人工干预

### 阻塞场景

```
Agent 执行中遇到不确定问题
    │
    ▼ kanban_block(reason="需要确认...")
    │
    ▼ Dashboard 显示红色"阻塞"
    │
    ├── 评论交互：在任务评论区与 Agent 对话
    ├── 查看原因：阅读阻塞原因描述
    └── 解除阻塞：点击 Unblock 或 /kanban unblock <id>
    │
    ▼ Dispatcher 下一 tick 自动重新调度
```

### 干预方式

| 方式 | 操作位置 | 说明 |
|------|----------|------|
| 评论 | Dashboard 任务评论区 | 给 Agent 留言，下次运行时读取 |
| 拖拽 | Dashboard 拖拽卡片 | 改变任务状态 |
| Unblock | Dashboard 按钮 / CLI | 解除阻塞，恢复调度 |
| 重新分配 | Dashboard / CLI | 更改任务负责人 |

### 代码示例

```bash
# 查看阻塞原因
hermes kanban show <task_id>

# 在评论区与 Agent 交互
hermes kanban comment <task_id> "请使用 2026 schema"

# 解除阻塞
hermes kanban unblock <task_id>

# 重新分配
hermes kanban assign <task_id> Danny
```

### 最佳实践

- ✅ 阻塞时先阅读原因，再决定是否干预
- ✅ 通过评论提供上下文，而非直接修改代码
- ✅ 不确定时可以先问 Agent 需要什么确认
- ❌ 不要盲目 Unblock，先理解阻塞原因

---

## 远程监控与通知

### 通知渠道

视频演示中作者通过 Telegram 每 5 分钟汇报进度，因为看板自带的阻塞推送通知功能还在优化中。

### 代码示例

```bash
# 订阅任务通知（自动推送 completed/blocked 等事件）
hermes kanban notify-subscribe <task_id> \
  --platform telegram --chat-id 12345678

# 查看已订阅的通知
hermes kanban notify-list

# 取消订阅
hermes kanban notify-unsubscribe <task_id> \
  --platform telegram --chat-id 12345678

# 实时监控所有事件
hermes kanban watch

# 监控特定代理的任务
hermes kanban watch --assignee Danny
```

### Cron 定时汇报（视频作者的替代方案）

```bash
# 通过 cron job 每 5 分钟汇报进度
hermes cron create "5m" \
  -q "检查 Kanban 看板状态，汇报进行中/已完成/阻塞的任务" \
  --deliver telegram
```

### Gateway 自动订阅

通过 Gateway（Telegram 等）创建任务时，自动订阅该任务的终端事件：

```
you> /kanban create "研究 AI 融资" --assignee researcher
bot> Created t_9fc1a3 (ready, assignee=researcher)
     (subscribed — 会在完成或阻塞时通知你)

... 几分钟后 ...

bot> ✓ t_9fc1a3 completed by researcher
     研究完成，已保存到 research/funding.md
```

### 已知限制

- ❌ 阻塞推送通知功能尚在优化中（截至视频发布时）
- ❌ 通知渠道配置默认显示微信，改 Telegram 可能不生效（已知 bug）

---

## 协作模式参考

### 9 种官方协作模式

| 模式 | 形态 | 示例 |
|------|------|------|
| P1 Fan-out | N 个并行同角色 | 5 个研究员并行研究 |
| P2 Pipeline | 角色链式传递 | 采集 → 编辑 → 写作 |
| P3 Voting | N 个 + 1 个汇总 | 3 研究员 → 1 审核员 |
| P4 Journal | 同 Profile + 共享目录 + Cron | Obsidian vault 日记 |
| P5 Human-in-the-loop | 阻塞 → 人工评论 → 解除 | 模糊决策点 |
| P6 @mention | 文本内联路由 | `@reviewer 看一下这个` |
| P7 Thread-scoped | 线程级工作区 | Gateway 按项目分线程 |
| P8 Fleet farming | 一个 Profile 管理 N 个主体 | 50 个社媒账号 |
| P9 Triage specifier | 粗略想法 → LLM 展开 → 正式任务 | 一句话变完整 spec |

### 视频演示的流程对应 P2 Pipeline + P5 Human-in-the-loop

```
PM(Paul) → 架构师(Alice) → 开发(Danny) → Review(Ray) → 测试(Tess)
     │                                              │
     └──────────── 阻塞时人工干预 ◄──────────────────┘
```

---

## 参考资料

- [Hermes Kanban 官方文档](https://hermes-agent.nousresearch.com/docs/user-guide/features/kanban)
- [Kanban Tutorial（官方教程）](https://hermes-agent.nousresearch.com/docs/user-guide/features/kanban-tutorial)
- [Kanban Setup — Profile 配置指南](https://github.com/NousResearch/hermes-agent/blob/main/optional-skills/creative/kanban-video-orchestrator/references/kanban-setup.md)
- [视频作者分享的 SOUL.md 配置](https://docs.google.com/document/d/1mHoPRa82N1tTP6RDDLv37Dv35bY49mMECOWvw7Zi0WQ/edit)
- [Hermes Agent v0.13 参考](https://blakecrosley.com/guides/hermes)
- [Multi-Agent Kanban 深度解析](https://www.theunwindai.com/p/multi-agent-kanban-board-in-hermes-agent)

## 相关笔记

- [[Hermes Agent 隐藏功能 - Dashboard, Claude Code CLI, TUI Chat]]
