---
title: Hermes Agent Multi-Agent Kanban Board 工作流
aliases:
  - Hermes Kanban Board
  - Multi-Agent Workflow
  - 自主多代理工作流
tags:
  - hermes-agent
  - ai-agent
  - multi-agent
  - kanban
  - workflow
  - autonomous
  - status/active
  - type/doc
source:
  - "https://youtube.com/watch?v=EKVRqcpTT6s"
  - "https://github.com/tonbistudio/hermes-multi-agent-workflow"
  - "https://hermes-agent.nousresearch.com/docs/user-guide/features/kanban"
author: Tonbi's AI Garage / NousResearch (Hermes Agent)
created: 2026-06-03
updated: 2026-06-03
description: |
  用 Hermes Agent Kanban Board 构建全自主多代理工作流：从问题发现到交付，18 个 agent 并行协作，SQLite 持久化状态，只有一步需要人工审批。
level: intermediate
stars: 4
note: 无字幕，基于视频元数据 + YouTube 摘要 + 外部资料综合整理
---

# Hermes Agent Multi-Agent Kanban Board 工作流

> 这篇笔记整理 YouTube 频道「Tonbi's AI Garage」展示的 Hermes Agent 多代理自主工作流。核心：用 Kanban Board 作为唯一协调层，让一群 agent 从问题发现到交付完全自主运转，只有一步需要人工审批。

## 目录

- [[#核心问题：多代理协调]]
- [[#Kanban Board 架构]]
- [[#完整工作流 Pipeline]]
- [[#Auto-Dependency 自动依赖系统]]
- [[#Agent 舰队配置]]
- [[#评分与过滤机制]]
- [[#实战结果与自愈能力]]
- [[#何时适用这种模式]]

---

## 核心问题：多代理协调

> "Everyone talks about multi-agent workflows; almost no one shows one running end-to-end without breaking."

多代理系统最难的部分不是设置 agent，而是让它们**协调做一件事而不互相踩脚**。

### 没有 Kanban Board 的问题

```
┌─────────────────────────────────────────────────┐
│            多代理无协调的混乱场景                    │
├──────────────┬──────────────────────────────────────┤
│ 重复工作      │ 多个 agent 做同一件事，浪费 token     │
│ 无共享记忆    │ 各自为政，不知道别人做了什么           │
│ 崩溃不可恢复  │ 一个 agent 崩溃，所有进度丢失          │
│ 调度混乱      │ 没有"谁该做什么"的明确规则            │
└──────────────┴──────────────────────────────────────┘
```

### 有 Kanban Board 的解决方案

```
┌─────────────────────────────────────────────────┐
│            Kanban Board = 单一真相来源              │
├──────────────┬──────────────────────────────────────┤
│ 任务即卡片    │ 每个任务单元 = 一张 card              │
│ Agent 认领   │ claim → work → done，不会抢同一个任务  │
│ 持久化       │ SQLite 存盘，重启不丢状态              │
│ 审计日志     │ 所有 claim/comment/complete 全记录     │
└──────────────┴──────────────────────────────────────┘
```

> "This is one SQLite file. That's the entire coordination layer and it's the bus and audit log all in one."

---

## Kanban Board 架构

### 五大优势

| 优势 | 说明 |
|------|------|
| **Durable（持久）** | SQLite 存盘，崩溃/重启后全部可恢复 |
| **Parallel（并行）** | 多个 agent 同时工作，由一个 board 协调 |
| **Event-driven（事件驱动）** | 工作自动沿依赖图流动，无需轮询 |
| **Self-healing（自愈）** | 死掉的任务自动回收并重新派发 |
| **Auditable（可审计）** | 每次认领、评论、完成都有完整日志 |

### 任务卡片结构

```
Card
  ├─ title       : 任务标题
  ├─ description  : 任务详情
  ├─ assignee    : 指定给哪个 agent profile
  ├─ status      : todo / ready / in_progress / done
  └─ parents[]   : 依赖的父任务 ID
```

### Dispatcher Loop（调度循环）

```
Board 有 ready 任务？
  │
  ├─ YES → Dispatcher 认领 (claim)
  │         │
  │         ├─ 启动 assignee 对应的 agent
  │         │   (独立 workspace)
  │         │
  │         ├─ Agent 完成工作 → 标记 done
  │         │
  │         └─ 下一轮循环
  │
  └─ NO → 等待下一 tick
```

**关键**：两个 agent 永远不会抢同一个任务（claim 操作保证互斥）。

---

## 完整工作流 Pipeline

### 端到端流程

```
SCOUTS → ORCHESTRATOR → RESEARCHERS → ORCHESTRATOR → HUMAN GATE → WORKERS → DELIVERABLE
```

### Stage 1: Detect（发现）

定时运行（每小时或每天两次），两个 Scout agent 并行搜索：

| Scout | 模型 | 数据源 |
|-------|------|--------|
| X Scout | Grok | X/Twitter 上的用户抱怨/问题 |
| Web Scout | GPT 5.5 | Reddit, YouTube, Web 搜索 |

### Stage 2: Validate（验证）

Orchestrator agent 执行：
- 去重（过滤已见过的问题）
- 按 rubric 打分
- **低于 65/100 自动搁置**

### Stage 3: Research（研究）

每个问题派 3 个 Researcher agent 并行：

```
问题 X
  │
  ├─ Researcher 1: Source Verifier (来源验证)
  ├─ Researcher 2: Context Researcher (背景调研)
  └─ Researcher 3: Solutions Auditor (现有方案审计)
```

### Stage 4: Route（路由）

Orchestrator 根据研究结果决定：
- **Build path** → 构建工具/skill
- **Video path** → 制作教学视频
- **Shelve** → 搁置

### Stage 5: Human Gate（人工审批）⭐ 唯一人工环节

通过 Telegram 发送待审批项：
- ✅ Approve → 进入执行
- ❌ Shelve → 搁置
- ✏️ Modify → 修改后重新审批

### Stage 6: Ship（交付）

**Build Path**：Analyst → Builder → Tester → CLI script / Skill

**Video Path**：Video Producer → Slides + Script + Speaker notes

---

## Auto-Dependency 自动依赖系统

> "A card will sit in todo until its parents are finished. Then it promotes itself to ready."

这是 Kanban Board 最巧妙的设计之一——**卡片自动感知依赖关系**。

```
Found Task (Scout 发现一个问题)
  │
  ├─→ Research Card 1 (来源验证) ──→ Route Card ──→ Gate Card ──→ Worker Card
  ├─→ Research Card 2 (背景调研) ──┘      ↑
  └─→ Research Card 3 (方案审计) ──────────┘
                                           │
                                           └─ 最后一个 child 完成
                                               → Route Card 自动从 todo → ready
```

**不需要轮询、不需要胶水代码、不需要手动触发**。子任务完成后，父任务自动晋升为 ready 状态，下一轮 dispatcher loop 就会认领它。

---

## Agent 舰队配置

每个 agent 只是同一台机器上的一个 **Hermes profile**，可以用不同模型：

| Agent | 模型 | 职责 |
|-------|------|------|
| X Scout | Grok | 搜索 X/Twitter 上的用户问题 |
| Web Scout | GPT 5.5 | 搜索 Reddit/YouTube/Web |
| Orchestrator | GPT 5.5 | 中心调度：验证、打分、路由 |
| Researcher ×3 | GPT 5.5 | 来源验证 / 背景调研 / 方案审计 |
| Analyst | GPT 5.5 | 综合信息，准备 build 方案 |
| Builder | GPT 5.5 | 写原型代码 |
| Tester | GPT 5.5 | 测试交付物 |
| Video Producer | GPT 5.5 | 生成幻灯片、脚本、大纲 |

**总计**：每次运行 18 个 agent 并行工作。

### 部署方式

- ✅ 单台机器，多 profile
- ✅ 每个 agent 在独立 workspace 中运行
- ✅ 不同 agent 可用不同模型（GPT 5.5 / Grok / Claude 等）
- ✅ 所有 agent 共享同一个 Kanban Board (SQLite)

---

## 评分与过滤机制

### Scoring Rubric（65 分阈值）

| 维度 | 衡量内容 |
|------|----------|
| Frequency | 问题出现的频率 |
| Pain Intensity | 严重程度/影响力 |
| Solvability | 是否真的可解决 |
| Solution Gap | 现有方案是否不足 |
| Strategic Fit | 与频道/开发方向的契合度 |

### 过滤逻辑

```
发现问题 → Orchestrator 评分
  │
  ├─ ≥ 65 分 → 进入 Research 阶段
  └─ < 65 分 → 自动搁置 (shelve)
```

这个设计避免 agent 在低价值问题上浪费资源。

---

## 实战结果与自愈能力

### 运行数据

- **18 个 agent** 并行
- **97 个任务** 一次运行完成
- **0 次崩溃** — 从头到尾没有中断

### 自愈案例

> "The approved post-gate slides and script were written to scratch task workplaces that are no longer present... It realized this was an issue and I didn't tell it this. It did this autonomously."

系统发现交付物引用了已被删除的临时 workspace，**自主重新生成**了视频幻灯片到持久化输出目录——无需人工干预。

### 交付物类型

| 类型 | 产出 |
|------|------|
| Build | CLI script、Hermes Skill |
| Video | 幻灯片、脚本、Speaker Notes |
| Research | 综合报告（带来源引用） |

---

## 何时适用这种模式

### 适合

- ✅ 需要持续监控外部信息源并自主行动
- ✅ 任务可分解为明确的阶段和角色
- ✅ 多个 agent 需要协调但不频繁通信
- ✅ 任务需要持久化（不能因崩溃丢失进度）
- ✅ 需要审计追踪（谁做了什么、何时做的）

### 不适合

- ❌ 简单的单步任务（一个 agent 就够）
- ❌ Agent 之间需要频繁实时通信（Kanban 是异步的）
- ❌ 任务完全不可预测，无法提前定义角色分工
- ❌ Token 预算极低

### 判断决策树

```
你的任务需要多个 agent 协作吗？
  │
  ├─ 否 → 用单 agent + delegate_task
  │
  └─ 是 → 任务需要持久化 / 崩溃恢复吗？
        │
        ├─ 否 → 用 delegate_task 批量
        │
        └─ 是 → 任务有明确阶段分工吗？
              │
              ├─ 否 → 重新设计任务分解
              │
              └─ 是 → ✅ 用 Kanban Board 工作流
```

---

## 参考资料

- [I Built the Ultimate Multi-Agent Workflow w/ Hermes Agent Kanban Board (YouTube)](https://youtube.com/watch?v=EKVRqcpTT6s)
- [tonbistudio/hermes-multi-agent-workflow (GitHub)](https://github.com/tonbistudio/hermes-multi-agent-workflow)
- [Hermes Kanban Tutorial (官方文档)](https://hermes-agent.nousresearch.com/docs/user-guide/features/kanban-tutorial)
- [Hermes Kanban Feature (官方文档)](https://hermes-agent.nousresearch.com/docs/user-guide/features/kanban)
- [Multi-Agent Workflows Megathread (Reddit)](https://www.reddit.com/r/hermesagent/comments/1t5cjlm/megathread_multiagent_workflows_kanban_delegation/)
