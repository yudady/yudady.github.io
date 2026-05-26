---
title: Harness Engineering 五件套：普通人也能用的 AI 工作系统
aliases:
  - 驾驭工程
  - Agent Harness
tags:
  - ai-agent
  - harness-engineering
  - context-engineering
  - prompt-engineering
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=biy_bKu4gDk"
  - "https://martinfowler.com/articles/harness-engineering.html"
  - "https://openai.com/index/harness-engineering/"
  - "https://addyosmani.com/blog/agent-harness-engineering/"
author: AI元点观察
created: 2026-05-26
updated: 2026-05-26
description: |
  Harness Engineering 是 AI 工程领域继 Prompt Engineering、Context Engineering 之后的
  第三次重心迁移。本文从"Agent = Model + Harness"的定义出发，拆解普通人版五件套工作系统，
  并结合 OpenAI、Martin Fowler、Addy Osmani 的工程实践，给出完整的方法论。
level: beginner
stars: 5
---

# Harness Engineering 五件套：普通人也能用的 AI 工作系统

> AI 编程和应用生成的门槛越来越低，但"能做出来"不等于"可靠"、"安全"、"该发布"。本文从普通人视角拆解 Harness Engineering 的核心概念，给出五件套工作系统，让非技术人员也能把 AI 从"聊天搭子"升级为"可靠的执行者"。适合所有用 AI 做文案、建应用、处理数据的日常用户。

## 目录

- [[#从 Prompt 到 Harness：三次重心迁移]]
- [[#什么是 Harness]]
- [[#普通人版五件套工作系统]]
- [[#工程级 Harness：三篇权威实践]]
- [[#专业能力的位置变了]]

---

## 从 Prompt 到 Harness：三次重心迁移

### 演进脉络

```
AI 工程的三次重心迁移

  第一波：Prompt Engineering
  "怎么跟 AI 说话"
  → 优化措辞、结构、few-shot 示例
  → 局限：单轮交互，无法处理长线任务

  第二波：Context Engineering
  "给 AI 看什么资料"
  → 管理上下文窗口、优化信噪比
  → 局限：还是单模型视角，缺少系统级约束

  第三波：Harness Engineering
  "让 AI 在什么环境里工作"
  → 工具、状态、约束、执行环境、反馈循环
  → Agent = Model + Harness
```

### 三者的关系

如果 AI 想成发动机：

| 概念 | 类比 | 解决什么 |
|------|------|---------|
| **Prompt Engineering** | 油门 | 怎么跟 AI 说话 |
| **Context Engineering** | 燃料 | 给 AI 看什么资料 |
| **Harness Engineering** | 方向盘 + 刹车 + 仪表盘 + 道路规则 | AI 在什么环境里工作 |

> Prompt 能让你从 0 到 1，但 Harness 才能让你从 1 到 100 稳定复用。

---

## 什么是 Harness

### 核心公式

```
Agent = Model + Harness
```

Harness 指的是**模型之外的整套系统**：系统提示词、工具调用、文件系统、沙箱环境、编排逻辑、钩子中间件、反馈回路、约束机制。模型只提供推理和生成能力，Harness 决定了它能不能连续可靠地做事。

### Martin Fowler 的双维控制框架

| 控制类型 | 方向 | 作用 | 单独使用的风险 |
|---------|------|------|-------------|
| **Guides**（引导） | 前馈（Feedforward） | Agent 行动**之前**引导方向 | Agent 不知道规则是否生效 |
| **Sensors**（传感器） | 反馈（Feedback） | Agent 行动**之后**观察结果并自我修正 | Agent 重复同样的错误 |

两种控制缺一不可。只有引导没有反馈 = 盲飞；只有反馈没有引导 = 被动救火。

### Addy Osmani 的核心洞察

> *"A decent model with a great harness beats a great model with a bad harness."*

**一个过得去的模型 + 好的 Harness > 一个顶级模型 + 差的 Harness。**

Claude Code、Cursor、Codex、Aider、Cline 都在用类似的底层模型，但产出的行为差异主要由 Harness 决定。

### Harness 的组成要素

```
┌─────────────────────────────────────────────────┐
│                    Harness                       │
│                                                   │
│  ┌───────────┐  ┌───────────┐  ┌──────────────┐ │
│  │ 约束层     │  │ 执行层     │  │ 反馈层        │ │
│  │           │  │           │  │              │ │
│  │ CLAUDE.md │  │ 工具系统   │  │ 传感器       │ │
│  │ AGENTS.md │  │ MCP 服务   │  │ 测试/lint   │ │
│  │ Skills    │  │ 沙箱环境   │  │ Agent 互审   │ │
│  │ 子代理配置 │  │ 文件系统   │  │ 人类闸门     │ │
│  │           │  │ 浏览器     │  │ 成本监控     │ │
│  └───────────┘  └───────────┘  └──────────────┘ │
│         ↓              ↓               ↓        │
│    "能做什么"      "怎么做"         "做得对不对"    │
└─────────────────────────────────────────────────┘
```

---

## 普通人版五件套工作系统

视频的核心贡献：不需要学框架、不需要懂代码，五件套就构成一条完整的闭环。

```
五件套工作闭环

  ① 任务卡（明确目标）
      ↓
  ② 资料包（提供支撑）
      ↓
  ③ 工具边界（划定禁区）
      ↓
  ④ 验收清单（检验结果）
      ↓
  ⑤ 进展记录（记录过程）
      ↓
      └──→ 回到 ①（下一轮优化）
```

### 1. 任务卡（Task Card）

**问题**：很多人用 AI 的第一个问题是任务太模糊——"帮我写个文案"、"帮我做个方案"。

**任务卡要写清楚五件事**：

| 要素 | 差的做法 | 好的做法 |
|------|---------|---------|
| **目标** | "帮我写文案" | "写一条新品上线文案" |
| **受众** | （没说） | "发在公众号和朋友圈，目标读者是老用户" |
| **场景** | （没说） | "希望他们理解新功能能节省整理资料的时间" |
| **禁区** | （没说） | "不要承诺具体效率提升数字，语气要克制" |
| **完成标准** | "写得好一点" | "有行动引导、没有夸张承诺、符合品牌语气" |

### 2. 资料包（Data Pack）

AI 不会自动知道你的背景。复杂任务一定要给资料包。

```
资料包误区 vs 正确做法

  ❌ 误区：上下文越多越好
     "上下文不是垃圾桶，你把一堆杂乱资料都塞进去，
      AI 反而更容易抓不住重点"

  ✅ 正确：少而准
     写文案时给：产品功能说明 + 用户痛点 + 真实使用场景 +
     品牌常用词 + 过去写得好的样例
```

- ✅ 高信号：功能说明、用户痛点、真实场景、品牌语气、好样例
- ❌ 低信号：大段无关背景、重复信息、过时资料

### 3. 工具边界（Tool Boundaries）

当 AI 能访问文件、搜索网页、调用插件、改代码、发布内容时，边界极其重要。

```
需要人类闸门的操作：

  涉及客户数据  →  🔒 人工确认
  涉及财务操作  →  🔒 人工确认
  涉及医疗数据  →  🔒 人工确认
  对外发布内容  →  🔒 人工确认
  删除操作      →  🔒 人工确认
  发送邮件      →  🔒 人工确认
  付款操作      →  🔒 人工确认
```

> **本质**：给 AI 设置权限界。它可以建议，但不能越权执行。

### 4. 验收清单（Acceptance Checklist）

AI 很擅长让一个东西"看起来完成了"，但不等于真的满足目标。

| 任务类型 | 验收清单示例 |
|---------|------------|
| **文案** | 受众是否明确？卖点是否具体？有行动引导？没夸张承诺？没编造数据？语气是否像品牌？ |
| **小应用** | 谁能登录？真实数据有没有参与测试？普通用户能不能看到别人的数据？公开链接会暴露信息？删除和导出有没有权限？出了问题能不能回滚？ |

> **不要让 AI 自己给自己打满分。**

### 5. 进展记录（Progress Log）

很多人和 AI 协作最大的问题是"断片"——今天改了一般，明天忘了为什么这么改、那个判断确认过没有、那个版本为什么删掉。

只要任务超过一轮对话，就写四行：

```
本轮进展记录模板

  1. 本轮完成了什么
  2. 改了哪些地方
  3. 还剩哪些问题
  4. 下次继续时先看什么
```

> 这件事听起来很小，但它会让 AI 协作从"一次聊天"变成一个"可以接力的项目"。

### 对比：差的用法 vs Harness 用法

以写文案为例：

```
  ❌ 差的用法：                ✅ Harness 用法：

  "帮我写一条新品上线文案"      ① 任务卡：写给谁、发在哪、目标、语气
                                ② 资料包：功能说明 + 痛点 + 场景 + 语气
  → AI 写得很顺但不知道写给谁    ③ 工具边界：不编造用户数、不承诺比例
  → 你不断追问"再短一点"         ④ 验收清单：受众明确、卖点具体、无过度承诺
  → 像在碰运气                  ⑤ 进展记录：这一版确认了什么、下一版调什么
```

---

## 工程级 Harness：三篇权威实践

### OpenAI：3 人 5 个月 100 万行代码

OpenAI 的实验证明了 Harness 的极限：

| 指标 | 数据 |
|------|------|
| 团队 | 3 → 7 工程师 |
| 时间 | 5 个月（2025.8 起） |
| 代码量 | ~100 万行 |
| 合并 PR | ~1,500 个 |
| 吞吐量 | 3.5 PR/工程师/天 |
| 时间节省 | ~90% vs 手写 |

**关键设计**：

```
OpenAI 的 Harness 架构

  AGENTS.md（~100 行，作为地图）
  ├── ARCHITECTURE.md
  ├── docs/
  │   ├── design-docs/
  │   ├── exec-plans/
  │   ├── generated/
  │   └── product-specs/
  └── references/
```

> *"给 Codex 一张地图，而不是一本 1000 页的手册。"*

核心原则：
- **渐进式披露**：AGENTS.md ~100 行，指向更深文档
- **机械执行**：专用 linter 和 CI 验证知识库，"文档园丁"Agent 扫描过期文档
- **Agent-to-Agent 审查**：几乎所有 review 推给 Agent 完成
- **可观测性集成**：每个 worktree 有独立的日志/指标/追踪栈

### Martin Fowler：前馈 + 反馈双维控制

关键分类：

| 控制类别 | 说明 | 可靠性 |
|---------|------|--------|
| **可维护性 Harness** | 重复代码、复杂度、缺测试 → computational sensors | 高 |
| **架构适配 Harness** | 性能需求 + 性能测试、日志规范 + 调试指令 | 中 |
| **行为 Harness**（最大缺口） | 功能正确性，目前没有完美传感器 | 低 |

### Addy Osmani：每个错误变成一条规则

```
"棘轮机制"（Ratchet）：

  Agent 犯了一个错
       ↓
  不是骂一句"模型不行"
       ↓
  而是把错误变成可执行的规则

  例：Agent 提交了被注释掉的测试
  → AGENTS.md：永远不要注释测试，删除或修复
  → Pre-commit hook：grep 检测 .skip( 和 xit(
  → Review 子代理标记为 blocker
```

> *"AGENTS.md 里的每一行都应该能追溯到一次具体的失败。"* 如果你不能说出一个组件存在的理由，它可能不应该在那里。

---

## 专业能力的位置变了

### 从"亲手做"到"定义清楚"

> **Prompt 能让你从 0 到 1，但 Harness 才能让你从 1 到 100 稳定复用。**

过去很多能力体现在"我能不能亲手做出来"。以后会越来越多地体现在：

```
专业能力的新位置

  过去                            以后
  ┌──────────────┐              ┌──────────────┐
  │ 我能不能写    │              │ 我能不能定义    │
  │ 出这段代码    │    ──→       │ 清楚目标       │
  ├──────────────┤              ├──────────────┤
  │ 我能不能设计  │              │ 我能不能组织    │
  │ 这个系统      │    ──→       │ 清楚资料和边界  │
  ├──────────────┤              ├──────────────┤
  │ 我能不能调试  │              │ 我能不能检查    │
  │ 这个 bug     │    ──→       │ 清楚结果       │
  └──────────────┘              └──────────────┘
```

### 非技术人员不是劣势

一个老师可能比工程师更懂学生哪里卡住，一个运营可能比工程师更懂客户怎么流转。**场景理解在 AI 时代会更值钱**。

但不知道自己哪里不懂才是问题。你可以不会写代码，但你要知道：
- 数据会不会公开？
- 权限有没有分开？
- 结果有没有验收？
- 除了问题谁负责？

### 三个自检问题

下次用 AI 做一件事之前，先问自己：

1. **任务卡写了吗？**（目标、受众、场景、禁区、完成标准）
2. **资料给了吗？**（少而准的高信号信息）
3. **验收标准定了吗？**（不要凭感觉满意）

> **会用 AI 的人不是在背提示词，他是在搭系统。**

---

## 参考资料

- [Harness engineering for coding agent users - Martin Fowler](https://martinfowler.com/articles/harness-engineering.html)
- [Harness engineering: leveraging Codex in an agent-first world - OpenAI](https://openai.com/index/harness-engineering/)
- [Agent Harness Engineering - Addy Osmani](https://addyosmani.com/blog/agent-harness-engineering/)
- [一文搞懂 Harness Engineering - JavaGuide](https://javaguide.cn/ai/agent/harness-engineering.html)
- [Harness Engineering（驾驭工程）- 菜鸟教程](https://www.runoob.com/ai-agent/harness-engineering.html)

## 相关笔记

- [[上下文熵增与 Claude Code 架构拆解]]
- [[Vibe Coding Token 省钱指南]]
- [[AI Agent]]
- [[上下文工程]]
