---
title: Multi-Agent 系统 — 5 种协调模式与 3 层架构
aliases: [多Agent系统, Multi-Agent Stack, Agent 协调模式]
tags: [ai-agent, status/active, area/distill, type/doc, topic-multi-agent, topic-context-engineering]
source: ["https://www.youtube.com/watch?v=dI9cj6nPxH8"]
author: Why QQ
created: 2026-05-01 07:00
updated: 2026-05-01 07:00
description: |
  从工程视角拆解多 Agent 系统的真实落地：5 种协调模式（Sub-Agents、Agent Teams、Generator-Verifier、Smart Friend、Agent Swarm）、3 层架构模型、Context Engineering 优于 Prompt Engineering。
level: intermediate
stars: 4
---

# Multi-Agent 系统 — 5 种协调模式与 3 层架构

> 最高效的开发者不忠于单一模型，而是通过**成本路由**构建最优的多 Agent 栈——简单任务用便宜模型，复杂任务用强模型，批量任务用 Agent Swarm。成本直降 85%，产出反而增加。

## 目录

- [[#核心洞察]]
- [[#Context Engineering vs Prompt Engineering]]
- [[#3 层架构模型]]
- [[#5 种协调模式]]
- [[#案例 1: 成本路由 — Kimi K2.6 + Claude 混合栈]]
- [[#案例 2: 11 Agent 对抗辩论 — Council of High Intelligence]]
- [[#案例 3: Generator-Verifier — Devin 的 Code-Review Loop]]
- [[#案例 4: Smart Friend — 小模型 + 大模型协作]]
- [[#案例 5: Agent Swarm — 批量 Map-Reduce]]
- [[#决策框架：何时用单 Agent vs 多 Agent]]
- [[#关键原则总结]]
- [[#参考资料]]
- [[#相关笔记]]

---

## 核心洞察

1. **干净的上下文能发现 58% 的严重 Bug** — Generator-Verifier 模式中，Review Agent 不共享 Coding Agent 的上下文，反而发现更多问题（注意力机制在短上下文中更精准）
2. **Agent Swarm 采用 Map-Reduce 而非真正并行写入** — 子 Agent 只负责产出 intelligence（数据/分析），写入由单一 Manager 控制
3. **Context Engineering > Prompt Engineering** — 关注给模型喂什么上下文，而非怎么写 prompt

---

## Context Engineering vs Prompt Engineering

```
+-----------------------------+-----------------------------+
|   Prompt Engineering        |   Context Engineering       |
+-----------------------------+-----------------------------+
| "你是高级工程师"             | 给模型看实际代码仓库         |
| "think step by step"        | 给模型看现有测试用例         |
| "请仔细分析"                | 给模型看相关 PR 历史         |
| 技巧性强，模型迭代后失效     | 结构性强，随模型能力增强更有效|
+-----------------------------+-----------------------------+
```

**Context Engineering 的核心原则**（Walden Yan, Cognition/Devin）：

| # | 原则 | 说明 |
|---|------|------|
| 1 | 共享尽可能多的上下文 | Agent 之间共享信息源、TODO list、计划文件 |
| 2 | 隐式决策碎片化风险 | 一个 Agent 的代码修改隐含风格/模式选择，并行 Agent 可能冲突 |
| 3 | 写入保持单线程 | 多 Agent 贡献 intelligence，但写入由一个 Agent 负责 |

---

## 3 层架构模型

```
+============================================================+
|                    LAYER 3: 编排层 (Orchestration)          |
|     Manager / Router / Coordinator                          |
|     职责: 任务分解、结果聚合、冲突解决                        |
+============================================================+
         |                    |                    |
+----------------+  +----------------+  +----------------+
| LAYER 2: 专业层|  | LAYER 2: 专业层|  | LAYER 2: 专业层|
| Review Agent   |  | Code Agent     |  | Search Agent   |
| (Verifier)     |  | (Generator)    |  | (Retriever)    |
+----------------+  +----------------+  +----------------+
         |                    |                    |
+============================================================+
|                    LAYER 1: 模型层 (Model)                  |
|     成本路由: 简单 → 便宜模型, 复杂 → 强模型               |
|     DeepSeek V4 / Kimi K2.6 / Claude / GPT                 |
+============================================================+
```

| 层级 | 职责 | 关键决策 |
|------|------|----------|
| **模型层** | 提供基础推理能力 | 成本路由——哪个任务用哪个模型 |
| **专业层** | 各 Agent 承担特定角色 | 角色定义——Generator/Verifier/Searcher |
| **编排层** | 协调 Agent 间协作 | 模式选择——用哪种协调模式 |

---

## 5 种协调模式

### 总览对比

```
模式                Agent 数量    写入模式        适用场景
─────────────────────────────────────────────────────────────
1. Sub-Agents       N 个           单线程写入      需要领域深度
2. Agent Teams      1 Manager+N    Manager 写入    跨领域复杂任务
3. Generator-Verifier 2个          交替写入        代码审查/质量保证
4. Smart Friend     2个(强弱配)    Primary 写入    成本优化+质量兜底
5. Agent Swarm      1+ N Workers   Map-Reduce     批量并行处理
```

### 1. Sub-Agents（子 Agent 模式）

**架构**：前端 Subagent 接收请求 → 路由到专业后端 Subagent → 产出到前端

```
[用户请求] → [Frontend Subagent]
                  |
     +------------+------------+
     |            |            |
[Backend A]  [Backend B]  [Backend C]
(领域专家)   (领域专家)   (领域专家)
     |            |            |
     +------------+------------+
                  |
          [Frontend Subagent]
                  |
             [用户响应]
```

**适用场景**：请求种类多、领域边界清晰
**失败模式**：跨领域信息需要 join 时（此时升级为 Manager-Worker）

### 2. Agent Teams（团队模式 / Manager-Worker）

**架构**：Manager 分解任务 → 子 Agent 执行 → Manager 聚合结果

```
[用户请求] → [Manager Agent]
                  |
     +------+-----+-----+------+
     |      |           |      |
[Worker][Worker] ... [Worker][Worker]
  PR-1    PR-2         PR-N    PR-N+1
     |      |           |      |
     +------+-----+-----+------+
                  |
          [Manager 合成结果]
                  |
             [用户响应]
```

**Devin 的实际实践**：Manager Devin 通过内部 MCP 协调子 Devin

**踩坑点**：
- Manager 默认过度指令化（overly prescriptive），缺乏代码库深度上下文时会误导子 Agent
- 子 Agent 不会主动跨 Agent 通信——需要专门设计通信机制
- Agent 间状态不共享是常见 bug 来源

### 3. Generator-Verifier（生成-验证循环）

**架构**：Coding Agent 写代码 → Review Agent 审查 → 循环修复

```
[Coding Agent]  ──── diff ────→  [Review Agent]
      ↑                                |
      │         bugs + feedback         │
      └────────────────────────────────┘
                     │
              (循环直到 bug < 阈值)
                     │
              [最终 PR]
```

**关键发现**：

| 指标 | 数值 |
|------|------|
| 每个 PR 平均发现 Bug | 2 个 |
| 严重 Bug 占比 | ~58%（逻辑错误、边界遗漏、安全漏洞） |
| 最佳上下文策略 | Review Agent **不共享** Coding Agent 上下文 |

**为什么不共享上下文反而更好？**

- **注意力稀释**：长上下文中模型决策质量下降（Lost in the Middle 现象）
- **逆向推理**：Review Agent 从实现反向推导，能发现 Coding Agent 因 spec 错误而忽略的问题
- **无自我偏见**：Agent 没有自我保护心理，干净上下文 = 更客观

### 4. Smart Friend（聪明朋友模式）

**架构**：Primary Agent（小/快/便宜）处理大部分任务，遇到难题时咨询 Smart Friend（大/慢/贵）

```
[Primary Agent]  ←── 日常任务 (85%)
  (便宜/快速模型)
       │
       │ 遇到难题?
       ├─── Yes → [Smart Friend] (强模型)
       │              │
       │         建议/方案
       │              │
       ├──←────────────┘
       │
  [执行并响应]
```

**两种变体**：

| 变体 | Primary | Smart Friend | 状态 |
|------|---------|--------------|------|
| 异构（成本优化） | 小模型 | 大模型 | 实验中，小模型判断力不足 |
| 同构（能力路由） | Claude | GPT（或反之）| 生产可用 |

**跨 Frontend 模型协作的核心发现**：
- 不是"遇到难题求助"，而是**能力路由**——哪个模型更擅长这个子任务就用哪个
- 有些模型调试更强、有些视觉推理更强、有些写测试更强

### 5. Agent Swarm（蜂群模式）

**架构**：Manager 分发任务到大量 Worker → Worker 并行产出 → Manager 聚合（Map-Reduce）

```
           [Manager]
          /  |  |  \
    [W1] [W2] [W3] ... [W100]
     |    |    |         |
    (并行处理，各自产出 intelligence)
     |    |    |         |
          \  |  |  /
           [聚合/去重/排序]
                |
           [最终输出]
```

**不是真正的"并行写入"**——Walden Yan 明确指出：

> "Unstructured swarm approach, arbitrary networks of agents negotiating with each other, is mostly a distraction. The practical shape is map-reduce-and-manage."

**实际案例**：

| 场景 | 输入 | 输出 |
|------|------|------|
| 文献综述 | 40 篇学术 PDF | 10 万字综述 + 引用数据集 |
| 批量简历 | 100 份 JD | 100 份定制简历 |
| 数据分析 | 1 篇天体物理论文 | 40 页报告 + 2 万行数据集 + 14 张图表 |
| 批量设计 | 1 个 prompt | 10 张杂志封面 |

---

## 案例 1: 成本路由 — Kimi K2.6 + Claude 混合栈

**来源**：Khairallah AL-Awady (@eng\_khairallah1)

### 模型对比

| 指标 | Kimi K2.6 | Claude Opus 4.6 | GPT-5.2 |
|------|-----------|-----------------|---------|
| SWE-Bench Verified | 80.2% | 80.8% | 80.0% |
| 输入价格 ($/M tokens) | $0.80 | $5.00 | — |
| 输出价格 ($/M tokens) | $3.60 | $25.00 | — |
| 价格倍数 | 1x | **7x** | — |
| 上下文窗口 | 262K | 1M | — |
| 开源 | Apache 2.0 | 否 | 否 |

### 路由策略

```
任务类型                    → 路由目标              → 占比
─────────────────────────────────────────────────────────
重构/测试/样板代码/API/文档  → Kimi Code (K2.6)      ~85%
复杂架构推理/长循环/系统设计  → Claude                 ~15%
批量文件/文档并行处理         → Agent Swarm           按需
```

**结果**：周 API 支出降低约 85%，产出量反而增加（不再限量使用 Agent）

### 关键工具特性

```
Kimi Code 常用命令:
  Ctrl-X      切换 shell 模式（不离 Agent 执行命令）
  /sessions   查看和切换会话
  --continue  恢复上次会话
  /compact    压缩上下文（保留关键信息）
  --yolo      自动批准文件修改（信任项目时使用）
  kimi acp    ACP 模式启动（IDE 集成）

MCP 兼容:
  kimi --mcp-config-file config.json   导入现有配置
  kimi mcp add --transport http ctx7   添加 MCP Server
  kimi mcp list                         查看已连接
  kimi mcp test context7                测试连接
```

---

## 案例 2: 11 Agent 对抗辩论 — Council of High Intelligence

**来源**：Nyk (@nyk\_builderz)

### 架构

```
+------------------------------------------------------+
|           Council of High Intelligence                |
|                                                       |
|  [Socrates]  [Aristotle]  [Aurelius]   ← Opus (深度)  |
|  [Feynman]   [Lao Tzu]    [Sun Tzu]                   |
|  [Ada]       [Machiav.]   [Torvalds]   ← Sonnet (速度)|
|  [Musashi]   [Watts]                                  |
|                                                       |
|  Coordinator: 路由、协议执行、反递归保护               |
+------------------------------------------------------+
```

### 3 轮协议

```
Round 1: 独立分析 (并行)
  └─ 每人 400 字，独立输出分析

Round 2: 交叉质询 (顺序)
  └─ 每人 300 字，必须回应至少 2 位成员
  └─ 回答: 最不同意谁？谁强化了你？改变了什么？重申立场

Round 3: 最终结晶 (并行)
  └─ 每人 100 字，不允许新论点
  └─ Socrates 只能问一个问题就被强制表态（毒芹规则）
```

### 反递归保护（踩坑经验）

```
问题: Socrates + Feynman 会进入无限质疑循环，耗尽上下文窗口

解决方案:
  1. 毒芹规则: Socrates 重复已回答的问题 → 强制 50 字表态
  2. 3 层深度限制: 质疑 → 回应 → 再质疑 → 必须表态
  3. 2 消息截止: 任何两人超过 2 轮对话 → 强制进入 Round 3
```

### 11 个预建 Triad

```
DOMAIN       TRIAD                        WHY
────────────────────────────────────────────────────────────
架构决策     Aristotle + Ada + Feynman     分类→形式化→简单性验证
战略         Sun Tzu + Machiavelli + Aurelius 地形→激励→道德根基
伦理         Aurelius + Socrates + Lao Tzu 责任→质疑→自然秩序
调试         Feynman + Socrates + Ada      第一性原理→假设→形式验证
创新         Ada + Lao Tzu + Aristotle     抽象→涌现→分类
冲突         Socrates + Machiavelli + Aurelius 暴露→预测→ grounding
发布         Torvalds + Musashi + Feynman  务实→时机→第一性原理
```

---

## 案例 3: Generator-Verifier — Devin 的 Code-Review Loop

**来源**：Walden Yan (Cognition)

### 实际效果

| 指标 | 数值 |
|------|------|
| 平均 Bug/PR | 2 个 |
| 严重 Bug 率 | ~58% |
| 严重 Bug 类型 | 逻辑错误、边界遗漏、安全漏洞 |
| 关键设计 | Coding Agent 和 Review Agent **不共享上下文** |

### 为什么不共享上下文更好？

```
1. Lost in the Middle 效应
   ┌─────────────────────────────────────┐
   │ 长上下文 → 注意力稀释 → 决策质量下降  │
   │ 短上下文 → 注意力集中 → 更精准发现    │
   └─────────────────────────────────────┘

2. 逆向推理优势
   ┌─────────────────────────────────────┐
   │ Coding Agent: 从 spec → 实现（可能遵循错误 spec）│
   │ Review Agent: 从实现 → 反推（质疑实现本身）     │
   └─────────────────────────────────────┘

3. 无自我确认偏见
   Agent 没有人类的自我保护心理，
   干净上下文让 Review Agent 更客观
```

### 通信桥接（Communication Bridge）

Coding Agent 需要根据自身上下文（用户指令、历史决策）**过滤** Review Agent 返回的 Bug：
- 区分真实 Bug vs 超出范围的修改建议
- 防止无限循环
- 保留有价值的发现

---

## 案例 4: Smart Friend — 小模型 + 大模型协作

**来源**：Walden Yan (Cognition/Windsurf)

### 通信挑战

```
                    ┌─ Primary 不知道自己不知道什么
                    │
[Primary] ──→ ??? ──┤  什么时候该问 Smart Friend？
                    │
                    ├─ 上下文传递粒度？
                    │  全量 vs 子集？
                    │
                    └─ 该问什么问题？
                       具体问题 vs 宽泛问题？
```

### 解决方案

| 挑战 | 方案 | 效果 |
|------|------|------|
| 何时升级 | 鼓励至少调用一次评估 | 80/20 方案 |
| 上下文传递 | 分享 Primary 的完整上下文 fork | 避免信息丢失 |
| 提问方式 | 问宽泛问题 "what should I do?"，让 Smart Friend 自行判断 | 比限定问题效果好 |
| Smart Friend 回答 | 要求 Smart Friend 主动指出 Primary 未关注的文件/方向 | "越界"回答更有价值 |

### 当前状态

| 配置 | 可用性 | 说明 |
|------|--------|------|
| 小模型 → 大模型 | 实验中 | 小模型判断力不足是主要瓶颈 |
| Claude ↔ GPT | 生产可用 | 跨 Frontier 协作，能力路由 |

---

## 案例 5: Agent Swarm — 批量 Map-Reduce

### 本质：不是"蜂群"，是 Map-Reduce

```
传统理解的 Swarm:         实际工作中的 Swarm:
  Agent ←→ Agent            Worker → Worker → Worker
  Agent ←→ Agent                 (互不通信)
  Agent ←→ Agent                     ↓
  (自由通信，混乱)                  Manager 聚合
                                  (有序，可控)
```

### 使用判断

```
需要 Agent Swarm 的场景:
  ✅ 批量处理 10+ 个相似文件/文档
  ✅ 每个任务独立，结果需要聚合
  ✅ 单个任务简单但总量大

不需要的场景:
  ❌ 单一复杂任务（用 Sub-Agents）
  ❌ 需要写入同一个代码库（写入冲突）
  ❌ 任务之间有强依赖关系
```

---

## 决策框架：何时用单 Agent vs 多 Agent

```
                    任务复杂度
                      ↑
                      │
           多 Agent ──┼─────────── 单 Agent 足够
           (Teams/    │           (大部分日常任务)
            Swarm)    │
                      │
     ─────────────────┼──────────────────→ 上下文需求
                      │
                Generator-Verifier
                (需要独立视角)
                      │
              Smart Friend
              (需要兜底/路由)
                      │
               Sub-Agents
               (需要领域专精)
```

| 条件 | 推荐 |
|------|------|
| 单个任务、单领域、简单 | **单 Agent** |
| 单个任务、需要质量保证 | Generator-Verifier |
| 单个任务、可能超出模型能力 | Smart Friend |
| 多领域请求、边界清晰 | Sub-Agents (水平路由) |
| 复杂任务、需要拆分 | Agent Teams (Manager-Worker) |
| 大量相似任务、需并行 | Agent Swarm (Map-Reduce) |

---

## 关键原则总结

### ✅ 最佳实践

1. **写入单线程**：多 Agent 贡献 intelligence，但写入由一个 Agent 负责
2. **Context Engineering**：关注给模型什么上下文，不是怎么写 prompt
3. **成本路由**：85% 任务用便宜模型，15% 用强模型
4. **干净上下文审查**：Review Agent 不共享 Coding Agent 上下文
5. **Map-Reduce > 自由通信**：Swarm 实质是结构化的任务分发与聚合
6. **渐进式复杂度**：从单 Agent 开始，证明不够后再加复杂度

### ❌ 常见反模式

1. 所有 Agent 都写同一个代码库（风格/模式冲突）
2. Manager 过度指令化（缺乏上下文时反而误导）
3. 追求"真正的并行写入"（实际工作中几乎不需要）
4. 11 个 Agent 开全会（用 Triad 选择 3 个就够了）
5. 忽略 Agent 间通信成本（Context Engineering 的核心难点）

---

## 参考资料

- [Multi-Agent Systems and Context Engineering](https://medium.com/@monotykamary/multi-agent-systems-and-context-engineering-when-to-split-the-mind-and-when-to-keep-it-whole-5e3c03b6aaa0)
- [Building Multi-Agent Systems - Shrivu Shankar](https://blog.sshh.io/p/building-multi-agent-systems)
- [Multi-Agents: What's Actually Working - Walden Yan (Cognition)](https://x.com/walden_yan/status/2047054401341370639)
- [Council of High Intelligence - Nyk](https://x.com/nyk_builderz/status/2034492549180625316)
- [How to Build Multi Agent AI Systems With Context Engineering - Vellum](https://www.vellum.ai/blog/multi-agent-systems-building-with-context-engineering)
- [Multi-Agent AI Systems: Architecture & Failure Modes - Augment Code](https://www.augmentcode.com/guides/multi-agent-ai-systems)
- [YouTube 视频：为什么最高效的开发者都在构建多Agent栈？](https://www.youtube.com/watch?v=dI9cj6nPxH8)

## 相关笔记

- [[Context Engineering - 上下文工程]]
- [[Agent 成本优化策略]]
- [[Claude Code 多 Agent 编排]]
