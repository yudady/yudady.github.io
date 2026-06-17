---
title: 循环工程 Loop Engineering - AI Coding Agent 的下一代工作范式
aliases: [Loop Engineering, 迴圈工程, 循环工程]
tags:
  - ai-agent
  - coding-agent
  - loop-engineering
  - claude-code
  - openai-codex
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=KgiwIEBeOHw"
  - "https://addyosmani.com/blog/loop-engineering"
author: Addy Osmani (Google Cloud AI 总监)
created: 2026-06-17
updated: 2026-06-17
description: |
  Loop Engineering 是 2026 年中硅谷 AI Coding Agent 领域的新兴概念，核心理念是从"人提示 AI"转向"设计一套系统替你提示 AI"，由 Addy Osmani 提出，拆解为五大模块加一层记忆体系。
level: intermediate
stars: 4
note: 无字幕，基于视频元数据 + Addy Osmani 原始博客文章 + 外部资料综合整理
---

# 循环工程 Loop Engineering - AI Coding Agent 的下一代工作范式

> 循环工程（Loop Engineering）是 AI Coding Agent 领域 2026 年中兴起的新工作范式。核心理念：**不再亲自给 Agent 写 prompt，而是设计一套能自行运转、验证和触发的循环系统**。由 Google Cloud AI 总监 Addy Osmani 系统化提出，适合正在使用或评估 Claude Code / OpenAI Codex 等 Coding Agent 的开发者。

## 目录

- [[#从 Prompt Engineering 到 Loop Engineering]]
- [[#核心定义与定位]]
- [[#五大模块 + 一层记忆]]
- [[#Claude Code vs Codex 实现对比]]
- [[#一个完整的 Loop 运行流程]]
- [[#循环的两种规模与两种类型]]
- [[#Loop 的局限性：三个递增的问题]]
- [[#何时值得建 Loop]]
- [[#人类工程师的三个职责]]
- [[#AI 工程范式演进]]

---

## 从 Prompt Engineering 到 Loop Engineering

### 旧模式：Prompt Engineering

过去两年的工作方式：工程师逐轮对话驱动 AI。

```
工程师 ──prompt──→ AI Agent ──code──→ 工程师
  │                                      │
  └────────── review + 新 prompt ────────┘
         （人始终是流程控制者）
```

### 新模式：Loop Engineering

人设计系统，系统驱动 AI，AI 产出交给系统验证。

```
工程师 ──设计一次──→ Loop 系统（自动运转）
                          │
                   ┌──────┴──────┐
                   ▼             ▼
              发现任务        验证结果
                   │             │
                   ▼             ▼
              分派 Agent      记录状态
                   │             │
                   └──────┬──────┘
                          ▼
                    决定下一步
                   （无需人介入）
```

### 关键区别

| 维度 | Prompt Engineering | Loop Engineering |
|------|-------------------|-----------------|
| 交互模式 | 一来一回，人主导 | 系统自动循环 |
| 人角色 | 流程控制者 | 系统设计者 + 质量把关者 |
| 可扩展性 | 受限于人的时间 | 可并行、可定时 |
| 知识传递 | 每次重述项目背景 | Skills 沉淀，跨 session 复用 |
| 适用场景 | 一次性任务 | 重复性、可验证的任务 |

---

## 核心定义与定位

### Osmani 的定义

> "Loop engineering is replacing yourself as the person who prompts the agent. You design the system that does it instead."

### 概念来源

| 人物 | 身份 | 核心观点 |
|------|------|---------|
| Peter Steinberger | OpenClaw 创始人 | "你不该再手动给 coding agent 写 prompt，该设计会自动 prompt agent 的 loop" |
| Boris Cherny | Anthropic Claude Code 负责人 | "我不再手动 prompt Claude，我的 loop 在替我做，我的工作是写 loop" |
| Addy Osmani | Google Cloud AI 总监 | 系统化拆解 Loop 为五大模块，提出完整框架 |

### 在 AI 工程范式中的位置

```
Prompt Engineering        → 如何跟 AI 对话（2023 主流）
    ↓
Context Engineering       → 如何给 AI 提供上下文（2025 主流）
    ↓
Harness Engineering       → 如何为 Agent 搭建运行环境（2026 初）
    ↓
Loop Engineering          → 如何让系统自动驱动 Agent（2026 中）
```

Harness 是单 Agent 的环境封装；Loop 是 Harness 之上的编排层，**按时间运行、获取反馈、喂养自己**。

---

## 五大模块 + 一层记忆

一个 Loop 需要五个组件和一层记忆。

### 模块总览

```
┌─────────────────────────────────────────┐
│            Loop 系统                      │
│                                         │
│  ┌───────────┐    ┌───────────┐        │
│  │Automation │───→│  Worktree  │        │
│  │(定时触发)   │    │(并行隔离)   │        │
│  └───────────┘    └───────────┘        │
│         │                                │
│         ▼                                │
│  ┌───────────┐    ┌───────────┐        │
│  │  Skills   │───→│ Connector  │        │
│  │(知识沉淀)  │    │(工具接入)   │        │
│  └───────────┘    └───────────┘        │
│         │                                │
│         ▼                                │
│  ┌───────────┐                          │
│  │ Sub-Agent │←── Maker/Checker 分离    │
│  │(多Agent协作)│                        │
│  └───────────┘                          │
│         │                                │
│         ▼                                │
│  ═══════════════════════════════════     │
│    State Memory (markdown / Linear)      │
│    ── 循环的脊柱 ──                      │
│  ═══════════════════════════════════     │
└─────────────────────────────────────────┘
```

### 1. Automations — 循环的心跳

**职责**：定期发现工作并启动任务。

| 特性 | Claude Code | OpenAI Codex |
|------|------------|-------------|
| 定时触发 | `/loop`（按间隔重跑）、cron task、hooks | Automations tab（选项目、prompt、频率、环境） |
| 持续运行 | `/goal`（运行直到条件满足） | `/goal`（run-until-done） |
| 结果分流 | GitHub Actions 集成 | Triage inbox（有用的留，无用的归档） |
| 生命周期钩子 | hooks（agent 生命周期的特定点执行 shell） | — |

**核心命令**：
- `/loop` — 按节奏重跑 prompt
- `/goal` — 持续运行直到验证条件为 true（如 "所有测试通过 + lint clean"）
- hooks — 在 agent 开始/结束/失败时触发 shell 命令

### 2. Worktrees — 并行不打架

**职责**：隔离多个 Agent 的工作空间，避免互相覆盖代码。

```
主仓库 (main)
  ├── worktree-1 (feature-A 分支) ← Agent 1 在此工作
  ├── worktree-2 (feature-B 分支) ← Agent 2 在此工作
  └── worktree-3 (bugfix 分支)    ← Agent 3 在此工作

（各自独立的 working directory，共享同一 repo history）
```

| 工具 | 实现方式 |
|------|---------|
| Claude Code | `git worktree`、`--worktree` flag、`isolation: worktree` on subagent |
| Codex | 内建 worktree，每个 thread 自动隔离 |

### 3. Skills — 经验可复用

**职责**：将项目知识、规范、约定写成可跨 session 读取的文件，避免每次从零解释。

**格式**：一个包含 `SKILL.md` 的文件夹，内含指令和元数据，可选 scripts/references/assets。

```
skills/
  └── my-skill/
      ├── SKILL.md          # 核心指令文件
      ├── scripts/          # 可执行脚本
      ├── references/       # 参考文档
      └── assets/           # 资源文件
```

**触发方式**：
- 显式调用：`$skill-name` 或 `/skills`
- 隐式触发：任务描述匹配 skill 的 description 字段时自动加载

**价值**：没有 Skills，循环每轮都从零推导项目；有 Skills，知识跨轮复用并累积。Skill 是编写格式，Plugin 是分发方式。

### 4. Connector — 让 Agent 接入真实工具

**职责**：让 Agent 读取 issue tracker、查数据库、调 staging API、发 Slack 通知。

基于 MCP（Model Context Protocol）构建。Claude Code 和 Codex 都支持 MCP，connector 通用。

**区别**：
- 没有 Connector：Agent 说"这是修复方案"（只输出建议）
- 有 Connector：Agent 开 PR → 关联 Linear ticket → CI 通过后 ping 频道（真正执行）

### 5. Sub-Agent — 执行者和审查者分离

**职责**：让不同的 Agent 扮演不同角色，避免"自己给自己打分"。

```
            ┌─────────────┐
            │ Orchestrator │
            └──────┬──────┘
           ┌───────┼───────┐
           ▼       ▼       ▼
     ┌─────────┐ ┌────────┐ ┌──────────┐
     │Explorer │ │Maker   │ │ Checker  │
     │(探索方案) │ │(编码实现)│ │(验证审查) │
     └─────────┘ └────────┘ └──────────┘
```

**最佳实践**：
- ✅ 写代码的 Agent 和审查的 Agent 分开，最好用不同模型
- ✅ Checker 用强模型 + high reasoning effort
- ✅ Explorer 用轻量、只读的快速模型
- ❌ 不要让同一个 Agent 既写代码又验证代码

| 工具 | 配置方式 |
|------|---------|
| Claude Code | `.claude/agents/` 目录下的 TOML 文件，定义 name、description、instructions |
| Codex | `.codex/agents/` 目录下的 TOML 文件，额外支持指定 model 和 reasoning effort |

### 6. State Memory — 长期 Loop 的脊柱

**职责**：记录任务进度和历史状态，跨 session 持久化。

**原因**：模型在每次 run 之间会忘记一切，记忆必须存在磁盘上而非上下文里。"Agent 会忘记，repo 不会。"

| 形式 | 示例 |
|------|------|
| Markdown 文件 | `AGENTS.md`、progress 文件 |
| 看板 | Linear board via MCP |
| 数据库 | 任何存在单次对话之外的东西 |

---

## Claude Code vs Codex 实现对比

| 原语 | Loop 中的职责 | Claude Code | OpenAI Codex |
|------|-------------|------------|-------------|
| **Automations** | 定时发现 + 分派 | cron、`/loop`、`/goal`、hooks、GitHub Actions | Automations tab（项目/prompt/频率/环境）、Triage inbox、`/goal` |
| **Worktrees** | 并行隔离 | `git worktree`、`--worktree`、`isolation: worktree` | 内建 worktree per thread |
| **Skills** | 沉淀项目知识 | `SKILL.md` 格式，`/skills` 调用 | `SKILL.md` 格式，`$name` 或隐式触发 |
| **Connectors** | 工具接入 | MCP servers + plugins | MCP Connectors + plugins |
| **Sub-agents** | Maker/Checker 分离 | `.claude/agents/`、agent teams | `.codex/agents/`、TOML 定义 |
| **State** | 状态持久化 | Markdown / Linear via MCP | Markdown / Linear via connector |

**关键洞察**：两个工具的模块名称不同但能力本质相同。一旦你看清了这个形状，就不再争论用哪个工具，而是设计一个无论在哪个工具里都能跑的 loop。

---

## 一个完整的 Loop 运行流程

以实际工作场景为例：

```
[每天早上] Automation 触发
    │
    ▼
[发现] Triage Skill 读取：
    │     - 昨天的 CI failures
    │     - Open issues
    │     - Recent commits
    │
    ▼
[记录] 写入 State Memory（markdown / Linear）
    │
    ▼
[分派] 对每个有价值的 finding：
    │
    ├──→ 开启 isolated Worktree
    │
    ├──→ Sub-Agent 1 (Maker) 草拟修复
    │
    ├──→ Sub-Agent 2 (Checker) 对照 Skills + 测试验证
    │
    ▼
[执行] Connector：
    │     - 开 PR
    │     - 更新 Linear ticket
    │     - CI 通过后 ping 频道
    │
    ▼
[收尾] Loop 处理不了的 → 进入 Triage Inbox 等人处理
    │
    ▼
[持续] State Memory 记录 tried/passed/open
         → 明天早上接着来
```

**你实际做的事**：设计一次，不需要手动 prompt 任何步骤。

---

## 循环的两种规模与两种类型

### 规模

| 类型 | 说明 | 适用场景 |
|------|------|---------|
| **Single Agent Loop** | 一个 Agent 循环执行 | 单一任务类型（如修 lint 错误） |
| **Fleet Loop** | Orchestrator Agent 拆分目标，多个专业 Agent 并行 | 复杂多维度任务 |

### 类型

| 类型 | 说明 | 特点 |
|------|------|------|
| **开放循环** | 无质量门禁 | ❌ Agent 会漂移，质量下降 |
| **封闭循环** | 有验证门禁 | ✅ Agent 会改进，质量上升 |

**最佳实践**：必须设计质量门禁。没有质量门禁的自动化只会加速制造问题。

---

## Loop 的局限性：三个递增的问题

Osmani 指出，**Loop 越顺畅，这三个问题越棘手**：

### 1. 验证（Verification）— 仍然在你身上

Loop 在无人看管时运行 = 无人看管地犯错。Split verifier 的目的是让 "done" 有意义，但 "done" 仍然是一个声明，不是证明。

```
你的工作 ≠ 产生代码
你的工作 = 确认代码真的可以运行
```

### 2. 理解债（Comprehension Debt）

Agent 产出越多、你阅读越少 → 对系统的理解差距越大。短期开发速度提升，长期没人真正理解系统怎么运作。

### 3. 认知投降（Cognitive Surrender）

Loop 越顺畅 → 越容易停止思考 → 失去独立判断能力。

> "设计 Loop 本身不是答案。带判断力设计它 → 解方；用它逃避思考 → 加速恶化的催化剂。同样的行动，完全相反的结果。"

### 判断决策树

```
你用 Loop 是为了：
  ✅ 加速你理解的工作 → 好的杠杆
  ✅ 自动化你已经理解的部分 → 高效
  ❌ 逃避理解工作本身 → 危险的认知投降
```

---

## 何时值得建 Loop

AlphaSignal 的四条件框架 — **四个条件同时成立才值得建**：

```
值得建 Loop：
  ✅ 任务具有高度重复性
  ✅ 验证机制已自动化（测试、lint、CI）
  ✅ 团队有足够的 Token 预算
  ✅ Agent 有完整的工具存取权限

不适合建 Loop：
  ❌ 一次性任务（直接 prompt 更高效）
  ❌ 需要大量人类判断（架构设计、产品设计）
  ❌ 验证依赖人工审查（code review 中的人类判断）
```

### 适合 Loop 的场景

| 场景 | 原因 |
|------|------|
| CI 故障排查 | 高度重复，验证自动化 |
| 依赖套件更新 | 标准化流程，测试可自动验证 |
| Lint 修正 | 规则明确，结果可验证 |
| Issue 转 PR | 模式固定，可自动分派 |
| 安全漏洞修补 | CVE 明确，patch 可验证 |

### 不适合 Loop 的场景

| 场景 | 原因 |
|------|------|
| 架构重构 | 需要大量人类判断 |
| 产品设计 | 创意性 + 上下文依赖 |
| 首次集成第三方服务 | 未知因素太多 |
| 需要跨团队协调的工作 | 人类沟通不可替代 |

---

## 人类工程师的三个职责

| 职责 | 说明 |
|------|------|
| **验证者** | 确认代码真的可以运行，不是产出了就结束 |
| **理解者** | 主动阅读 Loop 产出的代码，避免理解债积累 |
| **设计者** | 带判断力设计 Loop，而非用 Loop 逃避思考 |

> "建立你的 Loop，但请以一个打算继续当工程师的人来建立它，而不是只想按下'开始执行'按钮的人。"

---

## AI 工程范式演进

```
2023  Prompt Engineering     如何跟 AI 对话
         │
2025  Context Engineering    如何给 AI 提供上下文
         │
2026初 Harness Engineering   如何为 Agent 搭建环境
         │  "Agent = Model + Harness"
         │
2026中 Loop Engineering      如何让系统自动驱动 Agent
         "不再手动 prompt，设计循环系统"
```

| 范式 | 关注点 | 杠杆点 |
|------|-------|-------|
| Prompt | 写好一条指令 | 单轮对话质量 |
| Context | 喂好上下文 | 单次任务完成度 |
| Harness | 搭好环境 | 单 Agent 效率上限 |
| Loop | 设计循环 | 系统级自动化 + 人的时间释放 |

**关键洞察**：Loop Engineering 不奖励盲目自动化，它奖励**清晰目标、清晰边界、清晰验证，以及每一轮都能沉淀经验**的工作系统。

---

## 参考资料

- [Loop Engineering - Addy Osmani 原始博客](https://addyosmani.com/blog/loop-engineering)
- [Loop Engineering: The Guide for AI Agents - Lushbinary](https://lushbinary.com/blog/loop-engineering-ai-coding-agents-guide)
- [Loop Engineering 崛起：當 AI Agent 能自己工作 - TechOrange](https://today.line.me/tw/v3/article/yzWPyQ6)
- [Loop Engineering 循环工程又是什么鬼 - 知乎](https://zhuanlan.zhihu.com/p/2047996686807589866)
- [Loop Engineering，下一代 Agent 工程理念 - 腾讯新闻](https://view.inews.qq.com/a/20260612A09UQ700)
- [Addy Osmani X/Twitter 原帖](https://x.com/addyosmani/status/2064127981161959567)

## 相关笔记

- [[Agent Harness Engineering]]（待创建）
- [[Factory Model]]（待创建）
- [[Claude Code 使用笔记]]（待创建）
- [[MCP 协议详解]]（待创建）
