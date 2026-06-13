---
title: Loop Engineering 循环工程
aliases: [循环工程, Loop Engineering, Agent Loop]
tags:
  - ai/agent
  - ai/workflow
  - topic/ai-coding
  - status/active
  - type/learning-note
source:
  - "https://youtu.be/WuMlsfKeWHc"
  - "https://addyosmani.com/blog/loop-engineering/"
author: Addy Osmani (概念定义), 01Coder/小木头 (视频讲解)
created: 2026-06-13
updated: 2026-06-13
description: 从手动提示词驱动 Agent 到设计自主循环系统，让 Agent 自己发现任务、执行、验证并决定下一步
level: intermediate
stars: 4
---

# Loop Engineering 循环工程

> Loop Engineering 不是又一个新工具，而是视角的切换——从"我写提示词驱动 Agent"变成"我设计那个用提示词驱动 Agent 的系统"。设计一次，循环系统自主完成每一步，过程中你一条提示词都不用打。

## 目录

- [核心定义](#核心定义)
- [AI 编程的三个阶段](#ai-编程的三个阶段)
- [循环系统的 5+1 组件](#循环系统的-51-组件)
- [典型循环流程](#典型循环流程)
- [必要工具已就绪](#必要工具已就绪)
- [4 条适用条件](#4-条适用条件)
- [风险与失败模式](#风险与失败模式)
- [如何开始](#如何开始)
- [参考资料](#参考资料)

---

## 核心定义

**Loop Engineering = 替换你自己作为提示 Agent 的那个人。你设计那个替你做这件事的系统。**

一个"循环"是一个递归目标（recursive goal）：你定义目的，AI 迭代直到完成。它把手动逐轮提示词对话，转变为构建自主系统——自动发现工作、执行、验证、决定下一步。

> "你不应该再用提示词来驱动智能体为你编程了，你应该去设计那些循环。" — Peter Steinberger（OpenClaw 作者）

> "我不再用提示词驱动 Claude Code。我有一堆 Loop 在跑，是它们在驱动 Claude Code 自己来决定该干什么。我的工作只是写 Loop。" — Boris Cherny（Anthropic Claude Code 负责人）

---

## AI 编程的三个阶段

Boris Cherny 描述了自己一年来的变化轨迹，可对号入座：

| 阶段 | 模式 | 描述 | 类比 |
|------|------|------|------|
| **1. 逐行驾驶** | 手写 + 自动补全 | 手写代码，模型给你 Tab Tab Tab 补全 | 手动挡 |
| **2. 并行手动** | 多窗口调度 | 同时开 5-10 个 Agent 会话，每个都是你亲手发起，你来当调度员 | 开多辆车 |
| **3. Loop 循环** | 设计系统 | 写一个系统让它自己读仓库、读 issue、读 CI 失败，自己决定用什么提示词 | 设交通系统 |

```
阶段 1          阶段 2           阶段 3
────────────────────────────────────────────────
人 → Agent      人 → Agent×N     人 → Loop → Agent×N
(1对1手动)      (1对N手动调度)     (设计一次，自动运行)

你在循环里打字    你在窗口间切换      你写 Loop 的定义
```

**大多数人目前处在阶段 2，Loop Engineering 就是通往阶段 3 的路径。**

---

## 循环系统的 5+1 组件

Addy Osmani 把循环系统拆成 5 个构建块 + 1 根脊柱：

```
┌─────────────────────────────────────────────────────────┐
│                    Loop 循环系统                          │
│                                                          │
│  ┌──────────┐   ┌──────────┐   ┌──────────┐             │
│  │ 1. 心跳   │──►│ 2. 工作树 │──►│ 3. 技能   │             │
│  │ (定时触发) │   │ (隔离并行) │   │ (规则固化) │             │
│  └──────────┘   └──────────┘   └──────────┘             │
│        │              │              │                    │
│        ▼              ▼              ▼                    │
│  ┌──────────┐   ┌──────────┐   ┌──────────┐             │
│  │ 4. 连接器 │◄──│ 5. 子Agent│◄──│ 6. 记忆   │             │
│  │ (MCP工具) │   │ (写/审分离)│   │ (持久状态) │             │
│  └──────────┘   └──────────┘   └──────────┘             │
└─────────────────────────────────────────────────────────┘
```

### 1. 心跳（Automations）

自动触发机制，到点自己跑，无需手动驱动。

| 工具 | 实现方式 |
|------|---------|
| Claude Code | `/loop`（按 cron 时间表盲跑）, `/goal`（运行直到条件为真） |
| Codex App | Automations tab：选项目、提示词、频率、环境 |
| 通用 | GitHub Actions, cron job, 系统定时器 |

**`/goal` vs `/loop` 的区别：**
- `/goal`：给定可判定真假的目标条件，循环执行直到为真自动收工
- `/loop`：按时间表盲跑，到点执行提示词，不判断完成与否

### 2. 工作树（Worktrees）

多个 Agent 同时工作时，各自待在隔离的分支目录里，互相不踩踏对方的文件——就像两个工程师各开各的分支。

```
主分支 (main)
├── worktree-1/  ← Agent A 修 bug #123
├── worktree-2/  ← Agent B 加 feature #456
└── worktree-3/  ← Agent C 重构模块 X
```

**限制**：工作树防止机械碰撞，但你的 review 带宽仍然是天花板。

### 3. 技能（Skills）

把项目的规则、约定写进 `SKILL.md`，写一次，每个 Agent 每次都会读。

解决的问题：**意图债务（Intent Debt）**——Agent 每次启动都是"冷"的，会用地充实间隙做自信的猜测。技能就是把意图写下来，外化到 Agent 的上下文之外。

**关键点**：紧凑、无聊的描述比花哨的描述更利于隐式匹配。

### 4. 连接器（Plugins / MCP）

通过 MCP（Model Context Protocol）接入真实工具：issue 系统、数据库、Slack、staging API 等。

**核心转变**：从"这是修复方案，你帮我开 PR"→ Agent 自己通过连接器开 PR、关联 ticket、CI 通过后发通知。

### 5. 子智能体（Sub-agents）

把写代码和审代码拆开。**写代码的模型给自己的作业打分太宽容了**，需要另一个 Agent 来挑刺。

常见拆分模式：

```
Explorer（探索者）
    → Implementer（实现者）
        → Verifier（验证者）
```

**Token 成本提醒**：每个子 Agent 都独立做模型推理和工具调用，只在值得花的地方用。

### 6. 脊柱：记忆（State）

持久化状态——Markdown 文件、Linear 看板或任何磁盘上的记录。

> "模型在两次运行之间会忘记一切，所以记忆必须在磁盘上，不能只在上下文里。Agent 会忘，仓库不会。"

---

## 典型循环流程

以 Addy Osmani 描述的晨间自动化为例：

```
┌──────────────────────────────────────────────────────────┐
│                    晨间自动循环                            │
│                                                          │
│  08:00 心跳触发                                          │
│    │                                                     │
│    ▼                                                     │
│  调度分诊 Skill → 读昨日 CI 失败 + 最近提交                │
│    │                                                     │
│    ▼                                                     │
│  发现可修复问题                                           │
│    │                                                     │
│    ├──► 问题 A → 开 worktree → 子Agent 修复               │
│    │                  → 子Agent 审查（对照 Skill + 测试）   │
│    │                  → 连接器开 PR + 更新 ticket           │
│    │                                                     │
│    ├──► 问题 B → 同上流程                                  │
│    │                                                     │
│    └──► 搞不定的问题 → 丢进收件箱，等你来看                   │
│                                                          │
│  状态文件记录：试过什么、通过了什么、还有什么待处理            │
│  → 明天接着跑                                             │
└──────────────────────────────────────────────────────────┘
```

**结果**：设计一次，中间每一步你一条提示词都没打。

---

## 必要工具已就绪

工具已经在你手上了：

| 组件 | Claude Code | Codex | 通用 |
|------|------------|-------|------|
| 心跳 | `/loop`, `/goal`, cron | Automations tab | GitHub Actions |
| 工作树 | `git worktree` | 内置 | `git worktree` |
| 技能 | `SKILL.md` | `SKILL.md` | Markdown 规则文件 |
| 连接器 | MCP servers | MCP + plugins | MCP |
| 子Agent | `.claude/agents/` | `.codex/agents/` | 进程隔离 |
| 记忆 | `AGENTS.md` / Linear | Markdown / Linear | 文件系统 |

**不是程序员的专利**：循环的本质是让一个长期进程替你做重复的脑力活。写代码只是其中一部分。日常的资讯筛选、选题评级、报告生成都适合做成 Loop。

---

## 4 条适用条件

不是所有人都需要 Loop。以下 4 条**全部满足**，搭 Loop 才划算：

| # | 条件 | 为什么 |
|---|------|--------|
| 1 | **任务每周以上重复** | 一次性活不值得搭这套系统 |
| 2 | **验证能自动化** | 测试、类型检查、Linter 能自动把坏结果挡掉，不用肉眼审 |
| 3 | **Token 预算扛得住** | Loop 会反复读取上下文、重试、来回试探，不管代码最后用没用上都花 token |
| 4 | **Agent 有资深工程师的工具链** | 有日志、能浮现问题的环境、能跑代码看哪里崩掉 |

---

## 风险与失败模式

### 核心警告

> "Loop 越快地交付你没有亲手写的代码，仓库里有的东西和你脑子里真正搞懂的东西差距就越大。" — Addy Osmani

> "最危险的姿态是舒舒服服地接受 Loop 吐出来的一切。"

### 失败模式：Over-baking（发酵过头）

Geoff 的拉尔夫循环（Ralph）是最著名的失败案例——锲而不舍、永不放弃。让它修个小 bug，会跑很久，最后自作主张加一堆没人要的功能，甚至把本来能编译的代码改坏了。

```
Over-baking 模式:

  修小 bug → 多跑了一会儿 → 加了点"改进"
     → 再跑了一会儿 → "优化"了无关代码
        → 继续跑 → 改坏了本来能编译的部分

无人盯着的 Loop = 无人盯着的在犯错
```

### 三种债务（Linas Beliūnas 总结）

随着 Loop 越来越好，三种债务会越来越严重：

1. **理解债务** — 你没读过、没亲手验证的代码越来越多
2. **信任债务** — 你在没检查的情况下信任了更多输出
3. **依赖债务** — 你的工作流越来越依赖循环系统的正常运行

### 应对原则

- 写 Loop 的人，别只当按启动键的人
- 在适合的时间点、适合的环节上具备自我验证的能力
- 定期审查 Loop 的产出，保持对代码库的理解

---

## 如何开始

1. **别一上来就给所有事情套 Loop**
2. **先挑一件你每周都在重复、结果能自动验证的小事**
3. 写成一个 Loop
4. **验证 Loop 的产出**
5. 逐步扩展到更多任务

好的起点示例：
- 每日资讯筛选 + 选题评级
- CI 失败自动分诊
- 依赖更新自动 PR
- 代码风格/Lint 自动修复

---

## 参考资料

- [Loop Engineering - Addy Osmani 原文](https://addyosmani.com/blog/loop-engineering/)（权威来源）
- [Agent Harness Engineering - Addy Osmani](https://addyosmani.com/blog/agent-harness-engineering/)
- [Loop Engineering 完整指南 - Linas's Newsletter](https://linas.substack.com/p/loop-engineering-complete-guide)
- [Loop Engineering: 从 Prompt 到 Loop - WiselyChen](https://ai-coding.wiselychen.com/loop-engineering-from-prompt-to-loop-paradigm-shift/)
- [Should You Stop Prompting Agents - Firecrawl Blog](https://www.firecrawl.dev/blog/loop-engineering)
- [Boris Cherny: Claude Code 工作流分享](https://www.youtube.com/watch?v=EE0AZAUIBrk)

## 相关笔记

- [[Gemma 4 MTP 多Token预测加速]]
