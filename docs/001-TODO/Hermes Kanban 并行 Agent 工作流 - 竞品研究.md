---
title: 用 Hermes Kanban 构建并行 Agent 工作流（竞品研究）
aliases: [Hermes Parallel Agents, 多 Agent 并行工作流]
tags:
  - ai-agent
  - hermes-agent
  - kanban
  - multi-agent
  - status/active
  - type/doc
source:
  - "https://youtube.com/watch?v=1MaFErWfL24"
  - "https://hermes-agent.nousresearch.com/docs/user-guide/features/kanban"
author: NoCodeHack
created: 2026-06-16
updated: 2026-06-16
description: 用 Hermes Kanban 看板实现 5 个 Agent 并行研究竞品，1 个 Agent 汇总生成品牌化 PDF 报告，CRON 自动化每日执行
level: intermediate
stars: 4
note: 无字幕，基于视频元数据 + Hermes 官方 Kanban 文档 + 外部资料综合整理
---

# 用 Hermes Kanban 构建并行 Agent 工作流

> 一条 Telegram 消息，5 个 Agent 同时研究 ClickUp、Asana、Monday.com、Notion、Linear，汇总生成品牌化 PDF 报告。全程零代码，用 Hermes Kanban 的 fan-out / fan-in 机制实现并行协调。适合需要了解多 Agent 并行编排的中间用户。

## 目录

- [[#核心理念：Agent 是角色，不是主题]]
- [[#环境搭建]]
- [[#构建 Fan-Out 看板]]
- [[#Fan-In：汇总合成]]
- [[#CRON 自动化每日报告]]
- [[#Hermes Kanban 核心概念]]
- [[#并行 vs 串行工作流对比]]

---

## 核心理念：Agent 是角色，不是主题

### 传统误区

大多数人的直觉：一个竞品 = 一个 Agent。要研究 5 个竞品就建 5 个 Agent profile。

### 正确做法

Agent 是**角色（Role）**，不是主题（Subject）。建 ONE researcher profile，把它指向任意多个目标：

```
错误思路：
  ClickUp Researcher → Asana Researcher → Monday Researcher → ...

正确思路：
  Researcher × 5（同一角色，5 个目标）
```

新增竞品时只需发一条消息，不需要新建 Agent、不需要新 setup。

### 判断决策树

```
何时用单一 Agent 角色多目标？
  ✅ 每个任务的执行步骤相同（研究→对比→总结）
  ✅ 差异仅在目标对象（公司名/URL）
  ✅ 需要动态扩展目标列表

何时用多个不同角色？
  ✅ 每个任务需要不同能力（搜索 vs 写作 vs 编码）
  ✅ 任务间的输入输出格式不同
```

---

## 环境搭建

### 技术栈

| 组件 | 选择 | 作用 |
|------|------|------|
| VPS | Hostinger KVM2 (Ubuntu 24.04) | 7x24 运行 Hermes Docker |
| Agent 框架 | Hermes Agent | 内置 Kanban 编排，零编排代码 |
| LLM 后端 | Ollama Cloud | 1 个 API key 接入 41 个模型 |
| 快速模型 | DeepSeek V4 Flash | 并行研究（快+便宜） |
| 推理模型 | DeepSeek V4 Pro | 汇总合成（需要深度推理） |
| 通讯 | Telegram + BotFather | 手机收报告、触发运行 |
| 任务协调 | Hermes Kanban | fan-out + fan-in 依赖链 |
| 定时调度 | CRON Scheduler | 每日自动 ping + 执行 |

### 关键配置项

| 配置 | 说明 |
|------|------|
| Profile | 创建 researcher profile（DeepSeek Flash） |
| Toolset | 必须给 Agent `kanban_db` 工具集，否则无法创建任务 |
| Gateway | 避免 Telegram 冲突导致 crash——不要多个 gateway 进程绑定同一个 bot |

### 最佳实践

- ✅ VPS 用官方 Docker image 安装 Hermes，不用 Hostinger 预装 ISO
- ✅ 先在 Telegram 测试 Agent 通讯再跑并行
- ❌ 不要省略 toolset 配置——这是最常遗漏的步骤
- ❌ 不要在多个 gateway 进程中绑定同一个 Telegram bot

---

## 构建 Fan-Out 看板

### 架构

```
              ┌──────────────────────┐
              │  CRON / Telegram 触发  │
              └──────────┬───────────┘
                         │
              ┌──────────▼───────────┐
              │   Synthesizer (Pro)   │ ← parent task
              │   汇总 → 品牌化 PDF    │
              └──────────┬───────────┘
                         │ parent dependency
          ┌──────┬───────┼───────┬──────┐
          ▼      ▼       ▼       ▼      ▼
        ┌─────┐┌─────┐┌─────┐┌─────┐┌─────┐
        │ R-1 ││ R-2 ││ R-3 ││ R-4 ││ R-5 │
        │Click││Asana││Mond ││Noti ││Line │
        │ Up  ││     ││  ay ││ on  ││ ar  │
        └─────┘└─────┘└─────┘└─────┘└─────┘
          │      │       │       │      │
          └──────┴───────┴───────┴──────┘
                    All parallel
```

### 创建步骤

```
步骤 1: 创建研究任务卡片（fan-out），不指定 assignee
  → 5 张卡片，分别对应 5 个竞品

步骤 2: 创建汇总卡片（fan-in）
  → 1 张 synthesizer 卡片

步骤 3: 设置 parent dependency
  → synthesizer 的 parent = 5 张研究卡片
  → 当所有研究完成时，synthesizer 自动从 blocked→ready

步骤 4: 编写 prompt（只写目标，不写步骤）
  → "Research [company] and produce a structured comparison"

步骤 5: 指定 assignee = researcher profile
  → 1 个角色，5 个任务，同时并行
```

### Parent Dependency 机制

| 状态 | 触发条件 |
|------|----------|
| `blocked` | 有未完成的 parent task |
| `ready` | 所有 parent task 已 `done` |
| `running` | Agent 正在处理 |
| `done` | Agent 调用 `kanban_complete()` |

```
R-1: todo → running → done ─┐
R-2: todo → running → done ─┤
R-3: todo → running → done ─┼─→ Synth: blocked → ready → running → done
R-4: todo → running → done ─┤
R-5: todo → running → done ─┘
```

### Prompt 编写原则

```
好的 prompt（目标导向）：
  "Research ClickUp's pricing, features, and recent news.
   Produce a structured comparison with my product."

差的 prompt（步骤导向）：
  "Go to clickup.com, find the pricing page,
   extract the prices, compare with our prices..."
```

Agent 有完整工具集（web search、browser），告诉它**要什么结果**，不是**怎么做**。

---

## Fan-In：汇总合成

### 工作流程

```
1. Synthesizer 等待所有 researcher 完成（blocked 状态）
2. 所有 parent done → synthesizer 自动 ready
3. Synthesizer 用 Pro 模型读取所有 researcher 的输出
4. 汇总成单一品牌化 PDF 报告
5. 通过 Telegram 直接发送
```

### 模型选择决策树

```
选 Flash 模型（DeepSeek V4 Flash）：
  ✅ 并行研究任务（数据收集、信息提取）
  ✅ 需要速度快、成本低
  ✅ 5 个并行任务同时运行

选 Pro 模型（DeepSeek V4 Pro）：
  ✅ 汇总合成（需要跨结果推理和结构化）
  ✅ 报告生成（需要深度理解）
  ✅ 只运行 1 次，成本可控
```

---

## CRON 自动化每日报告

### 自动化架构

```
每日 9:00 AM
    │
    ▼
CRON ping → Telegram 消息
    │
    ├── 用户回复 "GO" → 重建看板 → 5 个并行研究 → 汇总 → PDF → Telegram
    ├── 用户编辑竞品列表 → 重建看板（含新竞品）→ 执行
    └── 用户不回复 → 仅提醒
```

### CRON 不能 ask-and-wait

CRON job 在无用户 session 环境中运行，不能使用 `clarify`（问问题等待回答）。

解决方案：
1. **两阶段模式**：CRON ping 用户 → 用户回复触发执行
2. **Memory 存储**：竞品列表和 builder prompt 存在 Hermes memory 中
3. **动态调整**：用户通过 Telegram 消息添加/删除竞品，下次运行自动包含

### 动态工作流

```
初始列表：ClickUp, Asana, Monday.com, Notion, Linear
    │
用户: "Add Trello to the list"
    │
    ▼
下次 CRON 运行自动包含 6 个竞品
无需修改任何 Agent 配置
```

### 最佳实践

- ✅ 用 Hermes memory 存储竞品列表（持久化、跨 session）
- ✅ CRON ping 只做提醒，不执行研究
- ✅ 用户确认后才执行（避免每天跑无意义的报告）
- ❌ 不要在 CRON job 中使用 `clarify` 工具

---

## Hermes Kanban 核心概念

### Task 生命周期

```
triage → todo → ready → running → done → archived
                    ↑         │
                    └── blocked
```

| 状态 | 含义 | 转换触发 |
|------|------|----------|
| `triage` | 待分类 | — |
| `todo` | 已计划，等待前置任务 | — |
| `ready` | 前置完成，可执行 | 所有 parent `done` |
| `running` | Agent 正在处理 | Agent 认领 |
| `blocked` | 等待人或外部条件 | `kanban_block()` |
| `done` | 已完成 | `kanban_complete()` |

### Kanban vs delegate_task

| 维度 | `delegate_task` | Kanban |
|------|-----------------|--------|
| 形态 | RPC 调用（fork→join） | 持久消息队列 + 状态机 |
| 父任务 | 阻塞直到子返回 | fire-and-forget |
| 子身份 | 匿名子 agent | 命名 profile + 持久 memory |
| 可恢复性 | 无——失败即失败 | block→unblock→重跑 |
| 人在环中 | 不支持 | 任意时刻评论/解锁 |
| 审计追踪 | context 压缩后丢失 | SQLite 永久持久化 |

### Worker 关键工具

| 工具 | 用途 |
|------|------|
| `kanban_show` | 读取当前任务 + worker context |
| `kanban_list` | 列出任务（支持过滤） |
| `kanban_complete` | 完成任务 + 结构化交接 |
| `kanban_create` | 创建子任务（fan-out） |
| `kanban_link` | 添加依赖关系（parent→child） |
| `kanban_block` | 请求人工介入 |
| `kanban_heartbeat` | 长任务心跳信号 |

---

## 并行 vs 串行工作流对比

| 维度 | 串行（Pipeline） | 并行（Fan-Out/Fan-In） |
|------|---------------------|------------------------|
| 执行方式 | researcher→analyst→writer→reviewer | 5 researcher 同时→synthesizer |
| 看板结构 | 4 卡片链式依赖 | fan-out 5 + fan-in 1 |
| 耗时 | ~15 分钟（顺序执行） | ~5 分钟（并行） |
| 适合场景 | 需要逐步深化 | 独立子任务可并行 |
| 扩展性 | 每加一步改全链 | 只需加卡片 + 依赖 |
| 代码量 | 零 | 零 |

### 何时选择并行 vs 串行

```
选并行（Fan-Out / Fan-In）：
  ✅ 子任务之间独立（互不依赖）
  ✅ 每个子任务执行逻辑相同
  ✅ 需要汇总为单一结果
  ✅ 子任务数量可变

选串行（Pipeline）：
  ✅ 上一步的输出是下一步的输入
  ✅ 每步需要不同能力
  ✅ 执行顺序固定
  ✅ 步骤数量少（2-4 步）
```

---

## 参考资料

- [Hermes Kanban 官方文档](https://hermes-agent.nousresearch.com/docs/user-guide/features/kanban)
- [视频源：NoCodeHack - Build a Parallel Agent Workflow](https://youtube.com/watch?v=1MaFErWfL24)
- [上一期：Build a Multi-Agent Kanban Workflow (NoCodeHack)](https://youtube.com/watch?v=Supn70oEJPo)
- [Tonbi: Why Hermes Kanban Makes Multi-Agent Workflows Actually Usable](https://x.com/tonbistudio/article/2062321393153536009)

## 相关笔记

- [[Hermes Agent]]
