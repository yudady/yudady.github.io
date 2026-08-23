---
title: Codex /goal 底层逻辑 — Ralph Loop 与长时间自主编码
aliases:
  - Codex /goal
  - Ralph Loop
  - Codex 自主编码
tags:
  - codex-cli
  - openai
  - autonomous-coding
  - ralph-loop
  - agentic-loop
  - status/active
  - type/note
source:
  - "https://www.youtube.com/watch?v=XrxeBz02QOI"
  - "https://developers.openai.com/blog/run-long-horizon-tasks-with-codex"
  - "https://simonwillison.net/2026/Apr/30/codex-goals/"
  - "https://ghuntley.com/loop/"
author: 为什么叫QQ (视频作者) / OpenAI / Geoffrey Huntley (Ralph Loop 发明者)
created: 2026-05-04
updated: 2026-05-04
description: Codex CLI 0.128.0 新增 /goal 命令的底层逻辑拆解，包括 Ralph Loop 概念、持久化项目记忆、Agent 循环机制和长时间自主编码实践
level: intermediate
stars: 4
---

# Codex /goal 底层逻辑 — Ralph Loop 与长时间自主编码

> Codex CLI 0.128.0（2026-05-01 发布）新增 `/goal` 命令，本质是 OpenAI 对 Ralph Loop 的原生实现。有人用它跑了 53 小时写出一个完整游戏，25 小时构建了一个设计工具（~30K 行代码，~13M tokens）。这不是"更聪明的模型"，而是 Agent 循环架构的转变——从单轮对话到持久化、可恢复、可审计的长时间工作流。

**注**：该视频无字幕，本文基于视频元数据 + OpenAI 官方博客 + 社区资料整理，内容可能不完全覆盖视频细节。

---

## 目录

- [什么是 /goal](#什么是-goal)
- [Ralph Loop 起源与哲学](#ralph-loop-起源与哲学)
- [/goal 的内部实现](#goal-的内部实现)
- [Codex Agent Loop](#codex-agent-loop)
- [持久化项目记忆（关键技巧）](#持久化项目记忆关键技巧)
- [实际成果案例](#实际成果案例)
- [时间跨度（Time Horizon）才是核心指标](#时间跨度time-horizon才是核心指标)
- [/goal 与传统模式的对比](#goal-与传统模式的对比)
- [实践建议](#实践建议)
- [参考资料](#参考资料)

---

## 什么是 /goal

`/goal` 是 Codex CLI 0.128.0 新增的实验性功能，让你设置一个**目标（Goal）**，Codex 会**跨轮次持续工作直到目标完成**或 Token 预算耗尽。

```bash
# 启用（需在配置中开启）
# config.toml
[features]
goals = true

# 使用
codex
> /goal Build a complete Figma-like design tool from scratch
```

**与传统模式的根本区别**：
- `codex "做一件事"` — 单轮，做完就停
- `/task` — 单任务，完成后退出
- `/goal` — **持续循环**，自我评估是否完成，未完成则继续

---

## Ralph Loop 起源与哲学

**Ralph Loop** 由 Geoffrey Huntley 提出，名字来源于《辛普森一家》角色 Ralph Wiggum。核心理念：

> "300 行代码在一个循环里跑 LLM tokens。你不断往循环里扔 tokens，就有了 Agent。"

```
┌─────────────────────────────────────────────────────┐
│                   Ralph Loop                         │
│                                                      │
│   ┌──────────┐                                       │
│   │ 读取任务  │                                       │
│   └────┬─────┘                                       │
│        ▼                                             │
│   ┌──────────┐     ┌──────────────┐                  │
│   │ LLM 执行  │────→│ 观察结果     │                  │
│   │ (编码/修复)│     │ (测试/lint)  │                  │
│   └──────────┘     └──────┬───────┘                  │
│        ▲                   │                          │
│        │            ┌──────▼───────┐                  │
│        │            │ 完成了吗？    │                  │
│        │            └──┬───────┬───┘                  │
│        │           是  │       │ 否                    │
│        │               ▼       ▼                      │
│        │          ┌──────┐ ┌──────────┐               │
│        └──────────│ 停止  │ │ 回到 LLM  │               │
│                   └──────┘ │ (带上下文)│               │
│                            └──────────┘               │
└─────────────────────────────────────────────────────┘
```

**Ralph Loop 的关键原则**：
- **单仓库、单进程、单任务** — 每个循环只做一件事（反微服务，倾向单体）
- **可观察** — 你需要看循环在做什么，从中学习 failure domain
- **迭代式编程** — 软件不再是积木搭建（Jenga），而是陶轮上的泥巴，不满意就重新扔上去
- **非确定性容忍** — 模型是非确定性的，所以每个循环都需要验证和自我修复

**Geoffrey 的原话**：
> "Ralph 是一种编排模式，你分配需求规格数组，然后给它一个 goal，让它循环执行。"

---

## /goal 的内部实现

根据 Simon Willison 的分析，`/goal` 主要通过两个自动注入的 prompt 文件实现：

```
/goal 执行流程
    │
    ├── goals/continuation.md
    │     └── 在每轮结束时注入
    │         • 自我评估：目标达成了吗？
    │         • 如果未完成，生成下一轮计划
    │         • 如果完成，生成总结并退出
    │
    └── goals/budget_limit.md
          └── Token 预算监控
              • 追踪已消耗 tokens
              • 接近预算时提醒
              • 预算耗尽时优雅退出
```

**0.128.0 的完整功能集**：
- 持久化 /goal 工作流（app-server API）
- 模型工具（model tools）
- 运行时续接（runtime continuation）
- TUI 控制（create / pause / resume）

---

## Codex Agent Loop

Codex 的循环不仅仅是 LLM 生成代码，而是一个**结构化的 Agent 循环**：

```
Plan → Edit Code → Run Tools → Observe → Repair → Update Docs → Repeat
  │       │            │           │         │          │         │
  │       │            │           │         │          │         └→ 回到 Plan
  │       │            │           │         │          └→ 更新状态文件
  │       │            │           │         └→ 根据错误信息修复
  │       │            │           └→ 读取测试/lint/构建输出
  │       │            └→ 执行测试、lint、typecheck、build
  │       └→ 修改源代码
  └→ 规划下一步行动
```

**这个循环为什么有效**：
- **真实反馈** — 不是幻觉式编码，有实际的错误、diff、日志
- **外部化状态** — 代码仓库、文件、文档都是持久化的上下文
- **可操控性** — 每个里程碑后都可以人工介入修正方向

---

## 持久化项目记忆（关键技巧）

OpenAI 官方 25 小时实验中，**最重要的技术不是某个巧妙 prompt**，而是四层文件结构组成的持久化记忆：

```
project-root/
├── Prompt.md          ← 冻结目标
├── Plan.md            ← 里程碑计划
├── Implement.md       ← 执行手册
└── Documentation.md   ← 状态审计日志
```

### Prompt.md — 冻结目标

**目的**：防止 Agent "建了令人印象深刻但错误的东西"

```markdown
## Goals
- 构建一个基于 Canvas 的设计工具

## Non-Goals
- 不支持 3D
- 不做实时协作（v1）

## Hard Constraints
- 性能：10K 元素不卡顿
- 平台：仅 Chrome

## Done When
- [ ] 可创建/编辑/删除形状
- [ ] 导出为 React + Tailwind 代码
- [ ] 通过全部测试
```

### Plan.md — 里程碑计划

**目的**：将开放式工作转化为可验证的检查点序列

```markdown
## Milestone 1: Data Model
- Acceptance: 定义 Canvas/Frame/Shape 数据结构
- Validation: `npm run typecheck`

## Milestone 2: Canvas Rendering
- Acceptance: 可渲染帧和基本形状
- Validation: `npm run test && npm run build`
- Stop-and-fix: 验证失败时修复后再继续
```

### Implement.md — 执行手册

**目的**：告诉 Agent "怎么干活"

```markdown
- Plan.md 是 source of truth
- 每完成一个 milestone 运行验证
- 保持 diff 范围小，不扩展 scope
- 持续更新 Documentation.md
```

### Documentation.md — 状态审计日志

**目的**：人离开几小时后回来仍能理解发生了什么

```markdown
## Current Status
- Milestone 1: DONE (2026-04-28)
- Milestone 2: IN PROGRESS — 渲染层基本完成，正在处理事件

## Decisions
- 使用 Y.js 做数据层（支持协作）
- Shape 类型用 TypeScript discriminated union

## Known Issues
- 大量文本渲染性能差（待优化）
```

**四层文件的职责分工**：

| 文件 | 角色 | 谁写 | 谁读 |
|------|------|------|------|
| Prompt.md | 项目宪法 | 人 | Agent（每轮） |
| Plan.md | 路线图 | Agent 生成，人审核 | Agent（每轮） |
| Implement.md | 操作手册 | 人 | Agent（每轮） |
| Documentation.md | 审计日志 | Agent 持续更新 | Agent + 人 |

---

## 实际成果案例

### 案例 1：25 小时设计工具（OpenAI 官方）

| 指标 | 数值 |
|------|------|
| 模型 | GPT-5.3-Codex (Extra High reasoning) |
| 运行时间 | ~25 小时不间断 |
| Token 消耗 | ~13M tokens |
| 代码产出 | ~30K 行 |

**实现的能力**：
1. Canvas 编辑（帧、组、形状、文本、图像、按钮、图表）
2. 实时协作（光标、选区、编辑同步）
3. Inspector 控件（几何、样式、文本属性）
4. 图层管理（搜索、重命名、锁定/隐藏、排序）
5. 对齐/辅助线/吸附
6. 历史快照 + 恢复
7. 回放时间线 + 从历史分支
8. 原型模式（热点 + 流程导航）
9. 评论（固定线程 + 解决/重开）
10. 导出（保存/导入 + CLI 导出为 JSON + React/Tailwind）

### 案例 2：53 小时完整游戏（社区）

社区用户用 `/goal` 让 Codex 运行 53 小时，产出完整游戏。具体细节来自视频标题引用。

### 案例 3：6 小时 TypeScript 重构（社区）

复杂 TypeScript 项目重构，`/goal` 运行 6+ 小时完成。

---

## 时间跨度（Time Horizon）才是核心指标

**不是"模型更聪明了"，而是 Agent 能在更长时间内保持连贯。**

METR 的时间跨度基准测试显示：

```
软件任务可完成时长（~80% 可靠性）
    │
    │         ╱──────────────────
    │       ╱
    │     ╱         ← 粗略 7 个月翻倍
    │   ╱
    │ ╱
    └──────────────────────────────── 时间
      2024    2025    2026
```

**GPT-5.3-Codex 在两个维度推动边界**：
1. **多步执行能力**更强（Plan → Implement → Validate → Repair）
2. **飞行中可操控**，修正方向不会丢失已有进度

---

## /goal 与传统模式的对比

```
传统单轮                  /task 单任务              /goal 持续循环
┌──────────┐            ┌──────────┐            ┌──────────────────┐
│ 输入提示  │            │ 分配任务  │            │ 设定目标          │
│ LLM 生成  │            │ 执行      │            │ 循环：            │
│ 输出结果  │            │ 完成退出  │            │   Plan → Code    │
│ 结束      │            │          │            │   → Test → Fix  │
└──────────┘            └──────────┘            │   → Evaluate     │
                                                 │   → 继续/停止   │
                                                 └──────────────────┘
```

| 维度 | 单轮对话 | /task | /goal |
|------|----------|-------|-------|
| 跨轮次 | ❌ | ❌ | ✅ |
| 自我评估 | ❌ | 有限 | ✅（核心能力） |
| Token 预算 | 无限制 | 无限制 | 有预算管理 |
| 错误恢复 | 手动重试 | 手动重试 | 自动重试 |
| 中途暂停 | 不适用 | 不适用 | ✅ pause/resume |
| 产出审计 | 无 | 有限 | 完整运行历史 |
| 适用场景 | 小任务 | 中等任务 | 大型/长时间任务 |

---

## 实践建议

### 适合 /goal 的任务

| ✅ 推荐 | ❌ 不推荐 |
|----------|-----------|
| 从零构建完整功能 | 简单 bug 修复 |
| 大规模重构 | 单文件编辑 |
| 多文件/多模块协调 | 需要大量人工决策 |
| 有明确验收标准的任务 | 需求模糊的任务 |

### 最佳实践清单

- ✅ **写好 Prompt.md** — 冻结目标和约束，防止 drift
- ✅ **里程碑足够小** — 每个 milestone 应在单轮循环内可完成
- ✅ **每个 milestone 有验收条件** — 必须可自动化验证
- ✅ **持续验证** — 每完成一步运行 test/lint/typecheck
- ✅ **维护 Documentation.md** — 让运行可审计、可恢复
- ✅ **设置合理 Token 预算** — 防止无限循环烧 tokens
- ❌ 不要设定模糊目标（"做一个好的网站"）
- ❌ 不要在目标中频繁修改需求
- ❌ 不要完全不管 — 需要定期检查方向是否正确

### 与 Hermes Agent 的对比思考

| 特性 | Codex /goal | Hermes Kanban |
|------|-------------|---------------|
| 核心机制 | 单 Agent 自循环 | 多 Agent 协调 |
| 状态持久化 | 文件系统（Markdown） | SQLite 数据库 |
| 多角色 | ❌ 单角色 | ✅ 多 Profile |
| 人工介入 | 中途可 steer | Blocked 状态等待人工 |
| 重试机制 | 内置循环重试 | Run History + 熔断器 |
| 跨 Session | 通过文件记忆 | 通过 Kanban DB |
| 适用场景 | 单项目长任务 | 多项目多角色工作流 |

---

## 参考资料

- [Codex CLI 0.128.0 adds /goal — Simon Willison](https://simonwillison.net/2026/Apr/30/codex-goals/)
- [Run long horizon tasks with Codex — OpenAI Developers Blog](https://developers.openai.com/blog/run-long-horizon-tasks-with-codex)
- [Everything is a Ralph Loop — Geoffrey Huntley](https://ghuntley.com/loop/)
- [Codex CLI GitHub (openai/codex)](https://github.com/openai/codex)
- [Codex CLI 0.128.0 Release Notes](https://github.com/openai/codex/releases)
- [METR: Measuring AI Ability to Complete Long Tasks](https://metr.org/)

## 相关笔记

- [[Hermes Agent v0.11-v0.12 - Kanban 多智能体工作流]] — Hermes 的多 Agent 协调方案
