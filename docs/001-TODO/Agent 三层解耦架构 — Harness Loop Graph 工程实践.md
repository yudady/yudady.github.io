---
title: Agent 三层解耦架构 — Harness / Loop / Graph 工程实践与排障
aliases:
  - Harness Loop Graph
  - Agent 三层架构
  - Agent 排障表
tags:
  - ai-agent
  - agent-engineering
  - harness-engineering
  - loop-engineering
  - graph-engineering
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=s3yiXTxueoI"
  - "https://www.dailydoseofds.com/p/the-anatomy-of-an-agent-harness/"
  - "https://www.dailydoseofds.com/p/loop-engineering-clearly-explained/"
  - "https://www.truefoundry.com/blog/graph-engineering-enterprise-guide"
author: Why QQ（频道）；核心概念源自 LangChain (Vivek Trivedy)、dailydoseofds (Avi Chawla)、explainx.ai (Yash Thakker)
created: 2026-08-01
updated: 2026-08-01
description: 将 Agent 系统拆解为 Harness/Loop/Graph 三层，提供症状到分层的排障决策表、七条可落地工程实践、三大避坑指南与趋势预测。
level: intermediate
stars: 5
note: 视频无字幕，笔记基于视频描述 + 用户提供的详细内容拆解 + dailydoseofds/TrueFoundry 权威原文交叉整理。
---

# Agent 三层解耦架构 — Harness / Loop / Graph

> 一篇在 X 上获得 58 万浏览、6.5 万收藏的长文，把 Agent 工程划分为三层：Harness 管环境、Loop 管反馈、Graph 管流程。本文从一线工程师视角，拆解一张「症状到层」的排障决策表、七条可直接抄进代码库的实践，以及三个要警惕的坑。

## 目录

1. [核心架构分层：Agent 三层解耦模型](#核心架构分层agent-三层解耦模型)
2. [排障决策表：症状到分层的精准对应](#排障决策表症状到分层的精准对应)
3. [七大可落地的工程实践](#七大可落地的工程实践)
4. [工程避坑指南与趋势预测](#工程避坑指南与趋势预测)
5. [行动建议](#行动建议)
6. [参考资料](#参考资料)

---

## 核心架构分层：Agent 三层解耦模型

判定标准很简单：**把基座模型（base model）拿掉之后，剩下哪些工程结构**。这些结构就是 Agent 工程的三个嵌套抽象层。

### 三层嵌套关系（ASCII）

```
┌─────────────────────────────────────────────┐
│  Graph（管流程）                              │
│  显式定义节点、边、路由、汇合                  │
│  ┌───────────────────────────────────────┐   │
│  │  Loop（管反馈）                         │   │
│  │  可重复的工作循环 + 外部评分器 + 停止规则 │   │
│  │  ┌─────────────────────────────────┐   │   │
│  │  │  Harness（管环境）                │   │   │
│  │  │  System Prompt / 工具 / 记忆      │   │   │
│  │  │  沙盒权限 / 日志 / 上下文压缩 / 路由│   │   │
│  │  │  ┌───────────────────────┐       │   │   │
│  │  │  │   Base Model（被拿掉） │       │   │   │
│  │  │  └───────────────────────┘       │   │   │
│  │  └─────────────────────────────────┘   │   │
│  └───────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
```

> [01:56](https://www.youtube.com/watch?v=s3yiXTxueoI&t=116) 影片将复杂的 Agent 系统拆解为三个相互嵌套的抽象层。

### 三层职责对比

| 层 | 职责一句话 | 管什么 | 关键问题 | 概念提出者 |
|----|-----------|--------|---------|-----------|
| **Harness（环境层）** | 模型周围的机器与基础设施 | System Prompt、工具定义、记忆、沙盒权限、日志、上下文压缩、路由 | 两队用同一个模型但效果天差地别 [02:37](https://www.youtube.com/watch?v=s3yiXTxueoI&t=157) | LangChain Vivek Trivedy：「If you're not the model, you're the harness」 |
| **Loop（反馈层）** | 设计可重复的工作反馈循环 | 外部评分器、确定性测试、停止规则、重试机制 | 引导模型根据结果修正输出 [02:00](https://www.youtube.com/watch?v=s3yiXTxueoI&t=120) | dailydoseofds Avi Chawla（2026-06） |
| **Graph（流程层）** | 显式定义工作流拓扑 | 节点、分支、汇合、路由、多专家协同 | 仅在需要严格顺序、并行与多专家协同时发挥 [02:05](https://www.youtube.com/watch?v=s3yiXTxueoI&t=125) | explainx.ai Yash Thakker（2026-07-18） |

### 工程重心迁移路径

工程努力不是均匀分布的，而是随抽象层外移：

```
Prompt Engineering  →  Context Engineering  →  Harness  →  Loop  →  Graph
（控制单次响应）        （控制模型所见）        （应用基础设施）（工作循环）（多智能体拓扑）
   词语                  检索/记忆/窗口         工具/状态/错误   停止/验证/重试  节点/边/路由
                  ────────── 工程重心外移 ──────────→
```

每一层包裹（wrap）前一层。你的 prompt 现在只是更大系统的一个输入。模型和 loop 本身是「你不写的部分」——LangGraph、OpenAI Agents SDK、Claude Code 的 while 循环几乎一样，没人靠 while 语句竞争。

> [!info] Harness 的经典定义
> Beren Millidge（2023）的类比：裸 LLM 是没有 RAM、磁盘、I/O 的 CPU；上下文窗口是 RAM；外部数据库是磁盘；工具集成是设备驱动。**Harness 就是操作系统**。

---

## 排障决策表：症状到分层的精准对应

> [03:03](https://www.youtube.com/watch?v=s3yiXTxueoI&t=183) 将模糊的线上事故抱怨，转化为工程师可精准定位的排查清单。

| 故障症状 | 归属分层 | 排查与修复方向 | 时间戳 |
|----------|---------|---------------|--------|
| 无法安全存取数据或工具 | **Harness** | 检查工具契约、权限控制、沙盒隔离、上下文注入 | [03:19](https://www.youtube.com/watch?v=s3yiXTxueoI&t=199) |
| 跨会话遗忘进度 | **Harness** | 排查持久化状态、检查点（Checkpoint）、进度产物、压缩策略 | [03:27](https://www.youtube.com/watch?v=s3yiXTxueoI&t=207) |
| 跑多次结果极不稳定 | **Loop** | 增加外部评分器、确定性测试、有界重试机制 | [03:36](https://www.youtube.com/watch?v=s3yiXTxueoI&t=216) |
| 成功不停手 / 无证据即宣称完成 | **Loop** | 设计基于证据的终止状态、Token 预算感知停止规则 | [03:44](https://www.youtube.com/watch?v=s3yiXTxueoI&t=224) |
| 多专家需依严格顺序协同 | **Graph** | 显式定义节点、边、路由与汇合逻辑 | [03:55](https://www.youtube.com/watch?v=s3yiXTxueoI&t=235) |
| 多流程出错且无法定位问题 | **Harness + Graph** | 导入与图节点对齐、带状态的 Trace 追踪 | [04:03](https://www.youtube.com/watch?v=s3yiXTxueoI&t=243) |
| 业务需求快速迭代（三天两头变） | **退回 Harness** | 推迟 Graph 形式化，保持纯模型驱动以维护弹性 | [04:15](https://www.youtube.com/watch?v=s3yiXTxueoI&t=255) |

### 排障决策树

```
线上 Agent 故障 → 先问：故障的本质是什么？
│
├─ 环境边界问题（工具不可用 / 权限不足 / 数据访问失败）
│  └─→ Harness 层
│      检查：工具契约 → 权限模型 → 沙盒隔离 → 上下文注入
│
├─ 收敛与反馈问题（结果不稳定 / 不停手 / 无证据就宣称完成）
│  └─→ Loop 层
│      检查：停止规则 → 外部评分器 → 确定性测试 → 重试上限
│
├─ 流程与协同问题（多专家顺序错乱 / 分支路由混乱）
│  └─→ Graph 层
│      检查：节点定义 → 边与路由 → 汇合逻辑 → 状态机
│
└─ 定位困难（多流程纠缠、日志不足）
   └─→ Harness + Graph
       导入：带 graph_id/node_id 的结构化 Trace
```

> [!warning] 切忌盲目换模型或改 Prompt
> 遇到线上故障，优先用排障决策表定位是哪一层的问题，而不是第一时间更换大模型或修改 Prompt。多数「模型不行」的真实原因是 Harness 工程品质不足。

---

## 七大可落地的工程实践

> [04:41](https://www.youtube.com/watch?v=s3yiXTxueoI&t=281) 可直接抄进代码库的 7 条工程纪律。

### 实践 1：证据驱动的停止规则（Evidence-driven Stop）

**问题**：Agent loop 的默认停止条件是「模型回复时不带 tool call」——也就是说，**模型自己在判断自己是否完成**。这个判断经常出错：编码 Agent 做了编辑、返回自信的总结、loop 就退出了，但它根本没跑测试，或跑了但测试失败。

**原则**：不听模型「口头承诺」，必须用硬性指标作为终止条件。

✅ 可信终止信号            | ❌ 不可信终止信号
-------------------------|------------------
自动化测试通过              | 模型说「已完成」
URL 链接可解析（HTTP 200）  | 模型返回无 tool call 的文本
Schema 校验通过            | 模型的自信总结
人工审批通过                | 模型声称「测试应该没问题」

> [04:49](https://www.youtube.com/watch?v=s3yiXTxueoI&t=289)
>
> Claude Code 的 `/goal` 命令就是这个模式：loop 跑到可验证条件成立为止，并用独立模型确认。Karpathy 的 AutoResearch 项目同理：不告诉 Agent 怎么做，给成功标准让它自己跑。

**最小实现**：

```python
# 除了模型自停，还要加这些 brake
MAX_ITERATIONS = 20        # 硬上限
TOKEN_BUDGET = 100_000     # 预算上限
TIME_LIMIT = 300           # 秒

while True:
    response = model(context)
    if not response.has_tool_calls():
        # 模型说做完了 —— 但先验证
        if verify_completion(response):  # 硬性检查
            break
        else:
            context += "你的上一次输出未通过验证，请继续"
    if iterations > MAX_ITERATIONS:
        break  # 兜底
```

### 实践 2：有界重试（Bounded Retries）

**问题**：无界重试 = Token 成本黑洞。模型卡在同一个错误上反复尝试，每次都消耗预算。

> [05:06](https://www.youtube.com/watch?v=s3yiXTxueoI&t=306)

每个循环必须明确四要素：

```
有界重试四要素
┌──────────────────────────────────────┐
│ 1. 可测量目标（measurable goal）      │
│ 2. 每轮新证据（new evidence/round）   │
│ 3. 最大重试次数（max retries）        │
│ 4. 具名兜底路径（named fallback）     │
└──────────────────────────────────────┘
```

```python
for attempt in range(MAX_RETRIES):
    result = try_step(context)
    if is_success(result):       # 每轮有新证据
        break
    context += extract_evidence(result)  # 不是盲目重试
else:
    fallback_path()  # 具名兜底，不静默失败
```

### 实践 3：最小权限原则（Least Privilege）

> [05:26](https://www.youtube.com/watch?v=s3yiXTxueoI&t=326)

在 Harness 层做好三件事：

```
┌─────────────────────────────────────────┐
│  网络隔离        只暴露白名单域名/API     │
│  密钥管理        按需注入，用完即弃       │
│  高风险动作      人工授权（Human-in-loop）│
└─────────────────────────────────────────┘
```

Anthropic 的架构原则：**模型决定要做什么，工具系统决定允许做什么**——权限执行与模型推理在架构上分离。Claude Code 对约 40 个离散工具能力独立设门，三阶段：项目加载时建立信任、每次工具调用前检查权限、高风险操作显式用户确认。

### 实践 4：进度文件 + Git 历史持久化

**问题**：单靠上下文压缩不够——压缩会丢失细节，新会话无法接续进度。

> [05:42](https://www.youtube.com/watch?v=s3yiXTxueoI&t=342)

**Anthropic 的「Ralph Loop」模式**（跨多上下文窗口的长任务）：

```
Phase 1: Initializer Agent
  → 环境初始化（init 脚本、进度文件、功能清单、初始 git commit）

Phase 2: Coding Agent（每次新会话）
  → 读 git log + 进度文件 → 定位未完成功能 → 开发 → commit → 写摘要
  → 文件系统提供跨上下文窗口的连续性
```

**Context Rot（上下文腐烂）是根因**：模型性能在关键内容落入窗口中间位置时下降 30%+，即使是百万 token 窗口也无法避免。Loop 越长，上下文越塞满垃圾（旧工具输出、放弃的死路、过时推理），质量越低——社区称之为 **doom loop**。

```
正常运行 → 上下文积累垃圾 → 推理质量下降 → 产生更差决策
    ↑                                        │
    └──── 加入更多噪音 ←──────────────────────┘
                    （doom loop 螺旋）
```

**解法**：把上下文当预算（budget）而非桶（bucket）。

策略              | 做法
-----------------|-----
Compaction       | 对话过长时摘要压缩，保留架构决策和未解决 bug
Offloading       | 大输出推到文件，只保留需要的切片
Sub-agent 委托   | 把脏子任务交给独立 Agent，只收回干净的结论

### 实践 5：确定性检查优先于模型自评

> [06:12](https://www.youtube.com/watch?v=s3yiXTxueoI&t=372)

**核心洞察**：模型兼任裁判存在盲区。一个被要求给自己打分的模型通常会给自己通过。

```
验证手段优先级（从高到低）：

  自动化测试 / Linter / 类型检查器     ← 确定性，最可信
       ↓
  Schema 校验 / URL 可达性检查         ← 确定性
       ↓
  独立模型审查（上下文分离）           ← 必须与生产者分离
       ↓
  生产模型自评                         ← 最不可信，最后手段
```

能用自动化测试解决的，绝不交给另一个 LLM 审查。Boris Cherny（Claude Code 作者）指出：给模型验证自身工作的能力，质量提升 2-3 倍。

### 实践 6：先跑 Trace 再画 Graph

> [06:47](https://www.youtube.com/watch?v=s3yiXTxueoI&t=407)

**工作流**：

```
1. 用最简 Harness 跑起来
       ↓
2. 收集真实 Trace（实际执行路径）
       ↓
3. 分析稳定路径 vs 不稳定路径
       ↓
4. 用 Graph 固化已验证的稳定路径
       ↓
5. 不稳定路径保持 Loop 驱动
```

**不要先画图再跑**——你会过早冻结假设，把本来灵活的部分锁死。

### 实践 7：工具宁窄勿宽（Tools: Fewer is Better）

> [07:06](https://www.youtube.com/watch?v=s3yiXTxueoI&t=426)

**数据支撑**：

| 来源 | 发现 |
|------|------|
| Vercel | 从 v0 砍掉 80% 工具后效果更好 |
| Anthropic 经验法则 | 如果人类工程师都无法确定该用哪个工具，Agent 也不能 |
| Claude Code | 通过懒加载实现 95% 上下文缩减 |
| 通用规律 | 给 Agent 100 个重叠工具，它会迷失 |

**工具设计在 Loop 中的两个特殊要求**：

```
要求 1：写入必须幂等（safe to repeat）
  Loop 会重试失败步骤
  重试 "create customer" → 创建第二个客户 → 重复记录 + 双重计费 ❌

要求 2：错误信息必须可操作（actionable）
  Loop 中错误信息是下一轮的输入
  模糊错误："Something went wrong"     → 浪费一轮
  精确错误："Port 8080 already in use"  → Agent 知道杀进程 ✅
```

---

## 工程避坑指南与趋势预测

### 三大警惕

> [07:35](https://www.youtube.com/watch?v=s3yiXTxueoI&t=455)

**警惕 1：Graph 过早仪式化** [07:35](https://www.youtube.com/watch?v=s3yiXTxueoI&t=455)

简单任务强行上图 → 过早冻结假设 → 丧失灵活性。Graph 是工具不是目的，只有当你已经通过 Trace 确认了稳定的执行路径后才该用它固化流程。

**警惕 2：术语包装通胀** [08:18](https://www.youtube.com/watch?v=s3yiXTxueoI&t=498)

Harness / Loop / Graph 这些名词是 2026 年才集中出现的。不要过度追逐新名词，应回归工程本质。TrueFoundry 的判断很到位：

> Prompt engineering, context engineering, harness engineering, loop engineering, and now graph engineering — the labels will keep arriving, and some will be weekly. What has not changed once in that entire run is what it takes to put any of them in front of customers: governed access, budgets, guardrails, identity, and traces.

**警惕 3：厂商叙事夹带私货** [08:42](https://www.youtube.com/watch?v=s3yiXTxueoI&t=522)

框架厂商常推销复杂图架构，应以生产环境真实的 Trace 验证，而非 PPT。Graph engineering 这个词本身就是 2026-07-18 由 explainx.ai 发表的「命名事件（naming event）」——它不是发明，而是已有实践被集中命名的那一刻。

### 趋势预测

> [10:32](https://www.youtube.com/watch?v=s3yiXTxueoI&t=632)

| 层 | 趋势 | 逻辑 |
|----|------|------|
| **Harness** | 加速标准化 | 工具协议、权限、Trace 格式将成为基础设施，造轮子空间缩小 |
| **Loop** | 聚焦证据系统 | 自动化评分器越便宜精准，测试工程含金量与重要性越高 |
| **Graph** | 经历去泡沫化 | 多数跟风项目将退回简单 Harness，仅留存在复杂分支/并行/恢复刚需的重型系统 |

**Harness 变薄的规律**：Manus 在六个月内被重写了五次，每次重写都在删减复杂度——复杂工具定义变成通用 shell 执行、「管理 Agent」变成简单的结构化交接。脚手架（scaffolding）在建筑完成后会被拆除；随着模型能力提升，Harness 复杂度应该下降。

**未来验证标准**：如果性能随更强大的模型线性提升而不需要增加 Harness 复杂度，设计就是合理的。

---

## 行动建议

> [09:20](https://www.youtube.com/watch?v=s3yiXTxueoI&t=560) 核心价值：将 Agent「不可靠、说不清」的技术焦虑，拆解为三个边界清晰的工程切面。

### 核心论点

Agent 工程的演进，实质上是**传统分布式系统、SRE 运维与网络安全规范在 AI 时代的复苏** [10:02](https://www.youtube.com/watch?v=s3yiXTxueoI&t=602)。开发者过去累积的传统工程直觉，正是建构稳定 Agent 系统最坚固的护城河。

### 两条行动原则

**原则 1：坚持极简** [11:29](https://www.youtube.com/watch?v=s3yiXTxueoI&t=689)

```
何时加 Loop 循环？
│
├─ 失败成本 >> 验证成本？ ──→ 加 Loop（有界重试 + 证据停止）
│
└─ 否则 ──→ 保持最简架构
            （最简单的能用的架构 = 最正确的架构）
```

**原则 2：导入排障决策表**

遇到线上故障时，按排障决策表先定位层级，切忌盲目更换大模型或修改 Prompt。

### 渐进式落地路径（dailydoseofds 建议）

```
Step 1  基础 loop + 立即加上限（max-iteration / timeout / cost ceiling）
  ↓
Step 2  在开始前定义 "done" = 自动化检查（不是事后凭感觉）
  ↓
Step 3  保护上下文（compaction / offloading / sub-agent 隔离）
  ↓
Step 4  审计工具（少而精 / 写入幂等 / 错误可操作）
  ↓
Step 5  在 loop 里放一个 critic（只有当你信任 "说不" 的东西时才完全放手）
```

---

## 参考资料

- 视频：[58万浏览6.5万人收藏的Agent排障表，我抄了](https://www.youtube.com/watch?v=s3yiXTxueoI) — Why QQ 频道，2026-07-31
- [The Anatomy of an Agent Harness](https://www.dailydoseofds.com/p/the-anatomy-of-an-agent-harness/) — Avi Chawla, dailydoseofds, 2026-04-06（Harness 11 组件详解）
- [Loop Engineering, Clearly Explained!](https://www.dailydoseofds.com/p/loop-engineering-clearly-explained/) — Avi Chawla, dailydoseofds, 2026-06-26（Loop 四大难点）
- [Graph Engineering for Multi-Agent Systems](https://www.truefoundry.com/blog/graph-engineering-enterprise-guide) — TrueFoundry, 2026-07-20（企业级 Graph 治理指南）
- [Hands-on: Rebuilding Claude Code's Harness](https://blog.dailydoseofds.com/p/hands-on-rebuilding-claude-codes) — dailydoseofds（Ralph Loop 模式详解）

## 相关笔记

- [[Agent 评估与测试]]
- [[Context Engineering 上下文工程]]
- [[MCP 工具协议]]

---

*文档生成时间：2026-08-01*
*基于 Why QQ 频道视频 + dailydoseofds / TrueFoundry 原文交叉整理*
