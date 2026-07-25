---
title: Claude Code Loop 工程设计 - 四种模式与边界控制
aliases: [Claude Code Loops, Loop Engineering, Agent Loop 设计]
tags:
  - claude-code
  - agent-loop
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=G97fHIATSlw"
  - "https://claude.com/blog/getting-started-with-loops"
author: 为什么叫QQ（频道） / Delba de Oliveira & Michael Segner（官方原文）
created: 2026-07-25
updated: 2026-07-25
description: 基于 Anthropic 官方《Getting started with loops》深度拆解 Claude Code 的四种 Loop 模式（Turn-based / Goal-based / Time-based / Proactive），涵盖触发、验证、停止三大核心工程问题与边界控制。
level: intermediate
stars: 5
---

# Claude Code Loop 工程设计 - 四种模式与边界控制

> 从「写好一句 Prompt」到「设计一套会自动停止的系统」。本文基于 Anthropic 官方博客《Getting started with loops》（2026-06-30 发布）深度拆解，结合「为什么叫QQ」频道的解读视频综合整理。
>
> note: 视频无字幕，基于官方原文全文 + 视频 webReader 元数据 + 多篇社区深度讨论综合整理。

## 目录

- [一、核心范式转移](#一核心范式转移从提示词优化到系统化-loop-设计)
- [二、四大 Loop 模式详解](#二anthropic-官方四大-loop-模式详解)
- [三、模式对比总览](#三四大模式对比总览)
- [四、边界控制与工程陷阱](#四边界控制与工程陷阱)
- [五、选型决策树](#五选型决策树)
- [参考资料](#参考资料)
- [相关笔记](#相关笔记)

---

## 一、核心范式转移：从「提示词优化」到「系统化 Loop 设计」

### 1.1 终结「人肉调度器」

Loop 的官方定义：**Agent 重复执行工作周期（agentic cycle），直到满足停止条件**。

关键认知：提示词工程并没有过时，但人类不应再持续充当 AI 的手动调度器与追问者。每一次你发 Prompt，都已经在启动一个 loop——只是过去由你人工驱动每一轮（manual loop）。

### 1.2 可靠 Loop 的三大核心工程问题

设计可靠的 Agent Loop，不能只回答「怎么问（Prompt）」，必须同时回答三个工程问题：

| 工程问题 | 核心追问 | 失败后果 |
|----------|----------|----------|
| 触发机制（Trigger） | 由谁、在何时启动工作流？ | 任务永远不开始，或无意义地频繁启动 |
| 验证机制（Verification） | 由谁、依据什么客观标准检查结果？ | Agent「自以为完成」实则错误，提前结束 |
| 停止规则（Stopping Rules） | 什么条件下终止循环？ | 无穷递归、Token 暴增、错误逐轮放大 |

```
┌─────────────────────────────────────────────┐
│            Agent Loop 三大支柱              │
├─────────────────────────────────────────────┤
│                                             │
│   ┌─────────┐    ┌──────────┐   ┌────────┐ │
│   │ Trigger │───▶│ Execute  │──▶│Verify? │ │
│   └─────────┘    └──────────┘   └───┬────┘ │
│        ▲              ▲            │Yes    │
│        │              └────────────┘No     │
│        │              (retry)        │Stop  │
│        │                             ▼     │
│   ┌────┴───────────────────────────────┐  │
│   │ Stop Condition                     │  │
│   │ - Goal met / Max turns / Timeout   │◀─┘
│   └────────────────────────────────────┘  │
└─────────────────────────────────────────────┘
```

> 💡 **范式转变**：开发者的核心能力，已从「撰写精妙提示词」转向「定义验证条件、边界规则与异常处理」的系统工程能力。

---

## 二、Anthropic 官方四大 Loop 模式详解

> ⚠️ **术语纠正**：部分中文解读（包括本文所基于的视频的部分流传笔记）将第一种模式称为「Task-based（任务型 / T-based）」。**这是误称**。Anthropic 官方原文明确使用的是 **「Turn-based loops」（按轮次）**。下文一律以官方术语为准。

### 2.1 Turn-based Loops（按轮次循环）

- **触发（Trigger）**：用户 Prompt
- **停止（Stop）**：Claude 自判任务完成，或判定需要更多上下文而交回给你
- **适用**：较短的、不属于常规流程或排程的任务
- **对应命令**：无（这就是你日常发 Prompt 的默认行为）

**运作机制**：你每发一条 Prompt，就启动一个由你主导每一轮的手动 loop。Claude 收集上下文 → 修改代码 → 运行测试 → 自检 → 返回它「认为」可行的结果。你随后手动验收，写下下一条 Prompt。

**如何增强（官方推荐）**：把你的手动验收步骤编码成 `SKILL.md`，让 Claude 自己完成更多端到端的自我检查：

```markdown
---
name: verify-frontend-change
description: Verify any UI change end-to-end before declaring it done.
---

# Verifying frontend changes
Never report a UI change as complete based on a successful edit alone.
Verify it the way a human reviewer would:

1. Start the dev server and open the edited page in the browser.
2. Interact with the change directly. For a new control (button, input,
   toggle): click it, confirm the expected state change, screenshot before/after.
3. Check the browser console: zero new errors or warnings.
4. Use the Chrome Devtools MCP, run a performance trace and audit
   Core Web Vitals.

If any step fails, fix the issue and rerun from step 1 — do not hand
back partially verified work.
```

> 关键：检查越是**量化**（quantitative），Claude 自我验证就越容易。给它「能看、能测、能交互」的工具，而不是让它凭感觉判断。

| 特性 | 表现 |
|------|------|
| 可控性 | ★★★★★ 人始终在决策环（Human-in-the-loop） |
| 推进速度 | ★★☆☆☆ 每轮都需人工确认 |
| 适用场景 | 探索性任务、短任务、非常规任务 |

### 2.2 Goal-based Loop（目标循环，`/goal`）

- **触发**：实时手动 Prompt
- **停止**：目标达成 **或** 达到最大轮次上限（explicit turn caps）
- **适用**：有可验证退出条件的任务
- **对应命令**：`/goal`

**运作机制**：当你定义了成功标准，Claude 就不必自己判断「够好了」而提前结束。每次 Claude 想停止时，一个 **evaluator model（评估模型）** 会检查你的条件，不满足就把它送回去继续——直到目标达成或达到你设定的轮次上限。

**为什么确定性标准如此有效**：如「测试通过数」「达到某个分数阈值」这类**可自动判定**的条件，让评估器无需主观判断。

```text
/goal get the homepage Lighthouse score to 90 or above, stop after 5 tries.
```

**关键约束**：

- ✅ 目标必须**量化且可自动验证**（测试 100% 通过、覆盖率 ≥80%、性能 <5s）
- ❌ 不能给「代码要写得好」这类模糊指令——评估器无法判断
- ⚠️ 需配合**适当工具调用权限**（如 auto mode）才能真正离手运行

**`/goal` 与其他命令的协作关系**：

```
/goal       ← 上一轮结束后触发评估，直到条件满足
/loop       ← 按时间间隔重复运行，直到用户停止或 Claude 判断完成
Stop hook   ← 可作为额外的停止闸门
auto mode   ← 让循环无需逐次请求权限即可跑下去
```

### 2.3 Time-based Loop（时间循环，`/loop` 与 `/schedule`）

- **触发**：指定的时间间隔
- **停止**：你手动取消，或工作完成（PR 合并、队列清空）
- **适用**：周期性工作，或与外部系统交互
- **对应命令**：`/loop`（本机）、`/schedule`（云端，research preview）

**两种场景**：

1. **周期性任务**——任务不变，只换输入。例：每天早上汇总 Slack 消息。
2. **外部系统轮询**——以固定间隔检查某系统的变化并响应。例：盯一个 PR 是否收到 review 或 CI 失败。

```text
/loop 5m check my PR, address review comments, and fix failing CI
```

> ⚠️ **部署陷阱**：`/loop` 跑在**你的本机**，关机即停。需要长期稳定运行，必须用 `/schedule` 把 loop 搬到云端 Routine。对话级 loop 依赖本机会话空闲，不可靠。

### 2.4 Proactive Loops（主动循环）

- **触发**：事件或排程，**无人实时参与**
- **停止**：每个子任务目标达成即退出；routine 本身跑到你关闭为止
- **适用**：周期性、定义良好的工作流：bug 报告、issue 分流、迁移、依赖升级
- **对应组合**：`/schedule` + `/goal` + skills + Dynamic Workflows + auto mode

**运作机制**：这是上述基本原语 + Claude Code 其他特性（auto mode、dynamic workflows）**组合**而成的长周期 loop。

**官方示例**（处理反馈）：

```text
/schedule every hour: check #project-feedback for bug reports.
/goal: don't stop until every report found this run is triaged,
       actioned, and responded to.
       When fixing a bug, use a workflow to explore three solutions
       in parallel worktrees and have a judge adversarially review them.
```

组合分解：

| 组件 | 职责 |
|------|------|
| `/schedule` | 每小时检查是否有新报告 |
| `/goal` | 定义「什么叫这一轮做完」 |
| skills | 文档化如何验证 |
| Dynamic workflows | 编排多个 agent：分流、修复、审查修复 |
| auto mode | routine 无需逐次请求许可即可运行 |

> ⚠️ **防护机制**：Proactive 模式需要**极其严密**的触发条件与人工审核节点。单点失效极易导致系统失控。官方建议：把 routine 路由到**更小更快的模型**跑常规判断，把**最强模型**留给真正的判断性决策。

---

## 三、四大模式对比总览

| 维度 | Turn-based | Goal-based | Time-based | Proactive |
|------|-----------|------------|------------|-----------|
| **触发** | 用户 Prompt | 手动 Prompt（实时） | 时间间隔 | 事件/排程，无人在环 |
| **停止** | Claude 自判完成/需上下文 | 目标达成 OR 最大轮次 | 用户取消 / 工作完成 | 子任务达标退出；routine 跑到关闭 |
| **你交出什么** | The check（验收） | The stop condition（停止条件） | The trigger（触发） | The prompt（整段提示） |
| **适用场景** | 探索、决策、短任务 | 已知「完成长什么样」 | 定时/外部系统轮询 | 周期性、定义良好的工作流 |
| **人类参与** | 每轮在环 | 设定目标后离手 | 设定后离手 | 完全离手（仅排程在） |
| **对应工具** | 自定义 verification skills | `/goal` | `/loop`、`/schedule` | 上述全部 + Dynamic Workflows |
| **自动化程度** | 低 | 中 | 中高 | 高 |
| **失控风险** | 低 | 中（需设轮次上限） | 低-中 | **高**（需严密防护） |

> 一句话记忆：**Turn-based 交出验收，Goal-based 交出停止条件，Time-based 交出触发，Proactive 交出整段 Prompt。**

---

## 四、边界控制与工程陷阱

### 4.1 渐进式采用原则（Complexity Escalation）

**不要一上来就追求完全自动化**，也不要按顺序逐个解锁模式。官方的建议是按任务特征匹配：

```
任务短、非周期          → Turn-based + 自定义 verification skill
有明确可量化验收条件     → 升级 /goal
工作天然按时间到来       → /loop 或 /schedule
工作是事件驱动 + 定义良好  → Proactive（组合全部）
```

### 4.2 明确停止条件与 Token 防护

无穷循环是 Loop 设计最大的误区。缺乏最大尝试次数或明确停止边界，会造成 Token 暴增与错误逐轮放大。官方给出六条边界：

| 策略 | 说明 |
|------|------|
| 选对 primitive 与模型 | 小任务别上多 agent/loop；有些任务用更便宜更快的模型就够 |
| 定义清晰的成功与停止标准 | 具体——让 Claude 尽快到位，但又不会太早收手 |
| 大规模运行前先试跑 | Dynamic workflows 可起数百个 agent，先用小切片估算用量 |
| 确定性工作用脚本 | 跑脚本比让模型推理每一步便宜得多 |
| 别让 routine 跑得比需要的更频繁 | 间隔匹配「你盯的东西多久变一次」 |
| 定期审查用量 | `/usage`（按 skills/subagents/MCPs 拆分）、`/goal`（无参时显示轮次与 token）、`/workflows`（各 agent 用量，可随时停） |

### 4.3 维护代码质量（系统而非个案）

Loop 输出质量取决于**它周围的系统**。当单个结果不达标时，不要只修这个个案，要把它**编码进系统**，改善所有未来迭代：

- 保持代码库本身干净——Claude 会跟随已有模式与惯例
- 给 Claude 验证自己工作的手段——用 skills 编码「什么叫好」
- 让文档触手可及——框架/库文档有最新最佳实践
- 用**第二个 agent 做 code review**——新鲜上下文的审查者更少偏见，不受主 agent 推理影响（可用内置 `/code-review` skill 或 Code Review for GitHub）

```
┌──────────────────────────────────────────────┐
│        质量保障四层防线                        │
├──────────────────────────────────────────────┤
│ 1. 干净的代码库（patterns/conventions）       │
│ 2. skills 编码的验证标准                       │
│ 3. 可达的官方文档                              │
│ 4. 独立 agent 做对抗性 review                 │
└──────────────────────────────────────────────┘
```

### 4.4 从「问问题」到「建系统」

这是整个 Loop 工程理念的落脚点。当你发现某个环节反复出问题，不要只优化那一句 Prompt，而要问：

- 这里的**测试性能线**（performance bar）是什么？
- **审查规则**能否编码进 skill？
- 失败时的**降级方案**（fallback）是什么？

把 Prompt 从「一句话优化」提升到「系统工程」层面。

---

## 五、选型决策树

```
你手头的任务是什么？
│
├─ 短任务、一次性、需要我亲自把关
│  └─▶ Turn-based
│       └─ 增强：把验收步骤写进 SKILL.md（越量化越好）
│
├─ 我知道「完成」长什么样，且能量化
│  └─▶ Goal-based（/goal）
│       └─ 必设：明确成功标准 + 最大轮次（"stop after N tries"）
│       └─ 可选：auto mode 让它离手跑
│
├─ 工作按固定时间到来 / 要盯外部系统
│  └─▶ Time-based
│       └─ 本机临时：/loop
│       └─ 长期稳定：/schedule（云端 routine）
│       └─ 间隔匹配「被盯对象变化频率」
│
└─ 事件驱动 + 定义良好 + 周期性到达
   └─▶ Proactive（组合体）
        ├─ /schedule 设节奏
        ├─ /goal 设每轮完成标准
        ├─ skills 设验证方法
        ├─ Dynamic Workflows 编排多 agent
        ├─ auto mode 免逐次许可
        └─ ⚠️ 必加：人工审核节点 + 触发条件严密化
```

**三步落地行动**：

1. 审视当前工作流——找出你在当「人肉调度器」反复追问的环节
2. 把手动多轮追问改造成**可量化的验收条件**（写出可执行的单元测试）
3. 在所有自动化 loop 中加 **最大重试上限 + 降级人工确认机制**，防 Token 浪费与错误累积

---

## 参考资料

- [Anthropic 官方：Getting started with loops（Loop engineering）](https://claude.com/blog/getting-started-with-loops) — Delba de Oliveira & Michael Segner，2026-06-30
- [为什么叫QQ - Claude官方把Loop讲清了：4种模式到底怎么选（YouTube）](https://www.youtube.com/watch?v=G97fHIATSlw)
- [Claude Code Loops Guide: /goal, /loop, /schedule (2026) - explainx.ai](https://explainx.ai/blog/claude-code-loops-official-guide-turn-goal-schedule-2026)
- [Mastering Claude Code Loops: AI Agent Automation Guide - youmind](https://youmind.com/landing/x-viral-articles/claude-code-loops-automation-guide)
- [Building Autonomous Loops with Claude Code - DevelopersIO](https://dev.classmethod.jp/en/articles/loop-engineering-claude-code-autonomous/)
- [Claude Code 的三种「循环」：/loop、/goal、Dynamic Workflows - 腾讯云](https://cloud.tencent.cn/developer/article/2691900)
- [loop 最佳实践：从 /goal 到 /loop - oschina](https://my.oschina.net/u/9489775/blog/19719345)
- [Codex 与 Claude Code 的 /goal 模式对比 - guoxudong.io](https://guoxudong.io/post/codex-vs-claude-code-goal-mode/)

## 相关笔记

- [[Claude Code]]
- [[Agent 设计]]
- [[AI 工作流]]

---

*文档生成时间：2026-07-25*
*基于 Anthropic 官方博客《Getting started with loops》(2026-06-30) 及「为什么叫QQ」解读视频*
