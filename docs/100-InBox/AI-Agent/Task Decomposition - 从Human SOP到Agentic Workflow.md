---
title: Task Decomposition - 从 Human SOP 到 Agentic Workflow
aliases: [任务拆解, task-decomposition, agentic-workflow]
tags:
  - ai-agent
  - prompt-engineering
  - mcp
  - status/active
  - type/doc
source:
  - "https://youtu.be/Yzpx4Xaigms"
author: Gary Chen
created: 2026-05-31
updated: 2026-05-31
description: 从 Human SOP 转化为 Agentic Workflow 的四步方法论：格式标准化、任务拆解与连接、双向开发、整合与执行环境。适合工程师和非技术人员学习如何为 Agent 设计可执行的工作流。
level: intermediate
stars: 4
---

# Task Decomposition - 从 Human SOP 到 Agentic Workflow

> 模型能力已经足够强，但大多数人不知道如何把大任务拆成 Agent 能跑得动的小任务。本笔记整理了从 Human SOP（给人看的流程文件）转化为 Agentic Workflow（Agent 能稳定执行的自动化工作流）的四步方法论。

## 目录

- [[#三个核心名词辨析]]
- [[#为什么 Mega Agent 注定失败]]
- [[#四步方法论：从 Human SOP 到 Agentic Workflow]]
- [[#真实案例：公司请求分类系统]]
- [[#行动建议]]

---

## 三个核心名词辨析

很多人会搞混这三个概念，但它们的层级和功能完全不同。

### 层级关系

```
┌─────────────────────────────────────────────┐
│         Agentic Workflow（整条生产线）          │
│  多个 Skill + Tools + 资料串起来的完整工作流     │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐        │
│  │ Skill A │→│ Skill B │→│ Skill C │        │
│  └─────────┘ └─────────┘ └─────────┘        │
└─────────────────────────────────────────────┘
         ↑ 每个 Skill 是一个独立执行单位
```

### 对比表格

| 概念 | 面向对象 | 本质 | 特点 |
|------|----------|------|------|
| **Human SOP** | 人类 | 传流程文件（简报/Word） | 依赖人类脑补判断、隐性规则多 |
| **Skill** | Agent | 打包好的执行单位 | 含 Markdown SOP + 参考资料 + 脚本 |
| **Agentic Workflow** | 整条生产线 | 多 Skill + Tools 串接的 Pipeline | 有明确 input/output，可上 production |

### Skill 的结构

```
skill-name/
├── SKILL.md          # 核心文件：人类写给 Agent 的 SOP + 方法论
├── references/       # 额外参考：范例输出、术语表、踩坑记录
└── scripts/           # 可执行脚本：格式转换、文件处理等确定性操作
```

- 一个 Skill 对应**单一任务**，不是整条工作流
- 命名示例：`weekly-report-drafting`、`pdf-processing`、`invoice-categorization`

---

## 为什么 Mega Agent 注定失败

### 核心问题：不是模型不够聪明

> 就算帮手什么都会、什么都懂，你没告诉他你在想什么，只要没有读心术，他就永远满足不了你的需求。

### 生活化比喻

假设你出门前请帮手「把家里打扫干净」：

- **你心目中的干净**：地板不粘、桌面清爽、厨房水槽没碗、垃圾倒掉
- **帮手心目中的干净**：东西有归位、看起来顺眼、角落不用动
- **结果**：表面上有做，但你在意的都没动

→ 问题的本质是**你对「干净」的定义没有显式化**，跟帮手够不够聪明无关。

### Mega Agent vs Pipeline 对比

| 维度 | Mega Agent | Pipeline（拆解后） |
|------|-----------|-------------------|
| 任务粒度 | 整包丟进去 | 每个节点一件明确的事 |
| 出错定位 | 不知道哪一段推理出问题 | 哪里坏改哪里 |
| 可观测性 | 黑箱，中间发生什么看不见 | 每个节点有 input/output |
| 稳定性 | 每次执行像买彩券 | 可预测、有边界 |
| 修复成本 | 整个重写 | 只改出问题的那段 SOP |
| 上线可行性 | 不敢让它上 production | 有可观测性，可修复，可上线 |

### 企业级实践

IBM、AWS、ServiceNow 都在用拆解式 Agentic Workflow，而非 Mega Agent。

> Divide and conquer —— 分治的古老观念在 Agent 时代反而更重要。你不是在训练一个超人 Agent，而是在设计一条生产线。

---

## 四步方法论：从 Human SOP 到 Agentic Workflow

### 总览流程

```
Human SOP（给人看）
    │
    ▼ Step 1: 格式标准化
Agent SOP（Agent 读得懂的版本）
    │
    ▼ Step 2: 任务拆解与连接
Pipeline（独立节点 + Artifact 串接）
    │
    ▼ Step 3: 双向开发（迭代）
成熟的 Pipeline
    │
    ▼ Step 4: 整合与执行环境
Production-ready Workflow
```

### Step 1：格式标准化

把 Human SOP 改成 Agent 能读的版本，三个关键点：

**1. 参数化（Parameterization）**

```
❌ SOP 写死："一定要用 normal mode"
✅ SOP 用参数："mode 可以是 {normal, delicate, heavy} 三选一"

❌ SOP 写死："温度设定 30 度"
✅ SOP 用参数："temperature 可以是 {cold, warm, hot} 三选一"
```

> SOP 一旦写死，就只能 cover 一种情景。参数化让同一份 SOP 可以复用。

**2. MUST / SHOULD / MAY（RFC 2119 写法）**

| 关键词 | 强度 | 含义 | 示例 |
|--------|------|------|------|
| **MUST** | 硬性规定 | 必须做，不能跳过 | MUST 先检查每件衣服的洗标 |
| **SHOULD** | 强烈建议 | 可不做但要说明原因 | SHOULD 根据材质选合适模式 |
| **MAY** | 可选项 | 做不做都行 | MAY 在湿度>70% 时用烘乾机 |

> 强制你把每条规则的强度想清楚，Agent 看到就知道哪些不能讨价还价。

**3. 结构化格式（Markdown 区块化）**

```markdown
## Parameters
- mode: {normal, delicate, heavy}
- temperature: {cold, warm, hot}
- humidity: number (当前湿度百分比)

## Steps
1. MUST 检查每件衣物洗标
2. MUST 将白色与深色分开
3. SHOULD 根据材质选择洗衣模式
4. 启动洗衣机，等待完成

## Error Handling
- 如果洗衣机报错 → MUST 停止并通知人类
- 如果衣物含有特殊材质 → SHOULD 跳过自动流程
```

### Step 2：任务拆解与连接

**核心原则：每个步骤都是独立节点，有自己的 input/output。**

```
洗衣服 Pipeline：

┌───────────┐     ┌───────────┐     ┌───────────┐     ┌───────────┐
│ 分类衣物   │────→│ 检查口袋   │────→│ 设定洗衣机 │────→│ 决定晾/烘 │
│           │     │           │     │           │     │           │
│ In: 衣物列表│     │ In: 分类结果│     │ In: 检查后 │     │ In: 洗衣设定│
│ Out: 分类JSON│     │ Out: 清洁列表│     │ Out: 洗衣设定│     │ Out: 最终方案│
└───────────┘     └───────────┘     └───────────┘     └───────────┘
```

**Artifact 串接示例：**

```
分类 Agent 输出:
{
  "whites": ["白色衬衫 x2"],
  "delicates": ["毛衣 x1"],
  "darks": ["牛仔裤 x3"]
}
      │
      ▼ （直接变成下一个 Agent 的 Input）
设定 Agent 根据 JSON 决定每批怎么洗
      │
      ▼
输出洗衣设定 JSON → 传给晾/烘 Agent
```

**独立性的价值：**
- 分类有 bug → 只修分类，后面的逻辑不动
- 想换晾烘逻辑 → 只替换最后一个节点
- 每个节点可独立测试、独立替换成新逻辑

### Step 3：双向开发（最重要的一步）

> 你的第一版 SOP 一定会有问题。无论你是多资深的工程师。

#### 默会知识（Tacit Knowledge）

默会知识 = 难以用文字明确表达、存在于个人大脑中的知识。

SOP 的本质就是把脑中的默会知识转成明确的文字规则。但默会知识的特性是**你自己不会发现它的存在，直到出错**。

#### 双向开发流程

```
Round 1:
  你描述流程 → Agent 写第一版 SOP
  → 跑一次 → 发现 Agent 把 T-Shirt 丢进超高溫烘衣机（缩水！）
  → 补一条规则："MUST NOT 把含棉>80% 的衣物用高温烘乾"

Round 2:
  → 再跑一次 → 发现 Agent 没把衣服装进洗衣袋
  → 再补一条："MUST 将 delicate 类衣物放入洗衣袋"

Round 3..N:
  → 每一轮踩到新的坑 → 补一条规则
  → 逐渐收敛，SOP 覆盖大多数场景
  → 剩余 5% → Human-in-the-loop 处理
```

#### 最佳实践

- ✅ 跟 Agent 一起跑、一起 debug、一起 iterate
- ✅ 两天写粗糙版本，一周内跑 50 次迭代
- ✅ 速度的关键不是写得多完美，而是迭代得有多快
- ❌ 关在房间里想像一份完美的 SOP 然后丢给 Agent

> 案例对比：一位客户花两个月写「完美」Agent SOP，跑一次就垮——cover 的全是想像中的情景。后来改成小步快跑，两周内上线。

### Step 4：整合与执行环境

**SOP 写得再漂亮，如果不接到真实世界的工具，就只是一份文件。**

#### MCP（Model Context Protocol）

> MCP 对 Agent 来说就像 AI 世界的 USB-C——一条线接所有东西。

```
Before MCP:
  ChatGPT → 一套整合代码
  Claude   → 另一套整合代码
  Cursor   → 又一套整合代码

After MCP:
  ┌─────────┐
  │ 任何 Agent│──→ MCP（统一标准）──→ 外部 Tools/Resources/Prompts
  └─────────┘    (Slack, DB, API, 文件系统...)
```

- 2025 年初 Anthropic 将 MCP 交给 Linux Foundation 下的 Agentic AI Foundation
- 已被 ChatGPT、Claude、Cursor 等主流平台采用
- 开放标准，非某一家私有规格

#### Human-in-the-Loop Checkpoint

在关键决策点让 Agent 停下来请人类确认：

| Checkpoint 类型 | 触发条件 | 行为 |
|----------------|----------|------|
| 高风险决策 | 涉及财务金额 >5000 | MUST 停下来等人按 OK |
| 大规模变更 | 涉及 admin 权限变更 | MUST 停下来等人确认 |
| 模糊请求 | 分类置信度低于阈值 | SHOULD 发回澄清后再继续 |

> 整条 Workflow 不是黑箱，不是失控的机器。你依然是最后拍板的人，但所有重复、机械、确定性的事都不用自己做。

---

## 真实案例：公司请求分类系统

### 场景

200 人公司，每天有人通过表单/Slack/Email 丢杂事进来：
- 申请新系统权限
- 发票可不可以报账
- 新同事需要哪些工具

### Step 1：标准化 SOP

```markdown
## Parameters
- source: {form, slack, email}
- raw_text: string (原始请求内容)
- employee_id: string

## Steps
1. MUST 验证员工 ID 是否在员工数据库
2. MUST 将请求分类为 {IT, HR, finance}
3. MUST 根据 SLA 规则判断 priority: {high, medium, low}
4. MUST 输出结构化 JSON（含 category, priority, recommended_assignee）
5. MUST 若 needs_clarification=true → 产出 2-3 个澄清问题
```

### Step 2：拆解为 Pipeline

```
┌───────────────────────┐    JSON    ┌──────────────────────────┐
│ internal-request-    │──────────→│ internal-request-reply   │
│ classification        │           │ drafting                  │
│                       │           │                           │
│ In: ticket 文字       │           │ In: 分类结果 JSON          │
│ Out: 分类 + priority  │           │ Out: 回复草稿              │
│     + assignee       │           │     + 预估回复时间          │
└───────────────────────┘           └──────────────────────────┘
```

- 两个 Skill 各做各的，靠 JSON Artifact 串接
- 分类失败不影响回复起草的逻辑

### Step 3：双向开发

- 某类请求被误分类 → 改分类 SOP
- Priority 永远判 medium → 补优先级判断规则
- Assignee 推荐给已离职的人 → 加验证逻辑

### Step 4：整合到真实系统

```
Ticket 进来
    │
    ▼
┌─────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│ 分类 Agent│→│ 起草 Agent │→│ 写回追踪  │→│ 人确认?   │
└─────────┘   └──────────┘   │ 系统     │   │(如需)    │
                            │(Notion/  │   └──────────┘
                            │ Sheets)  │
                            └──────────┘
```

- 小 script 把分类结果写回公司追踪系统
- 高风险请求（财务>5000 / admin 权限变更）→ Human-in-the-Loop

---

## 行动建议

### 判断决策树

```
你要开始做 Agentic Workflow 吗？

  ├─ 想一步到位做完美 → ❌ 先放弃完美主义
  ├─ 想把公司所有流程都自动化 → ❌ 从最无聊的一个开始
  └─ 想先把 AI 学好再用 → ❌ 跑起来比学得完美重要

  ✅ 找一份最无聊但一直重复做的 Human SOP
     （周报 / Release Checklist / 新人 Onboard 流程）
     挑你最不想做的那一个
     照着四步做一遍
     先有一个省 30% 时间的版本
     再慢慢迭代
```

### 关键认知

> 你不是在学怎么用 AI，而是在学怎么设计给 AI 用的工作流。前者半年就会过时，后者越来越值钱。

### 免费层 vs 前沿模型对比

（本笔记与 FreeLLMAPI 笔记互补）

| 能力层级 | 用什么模型 |
|----------|-----------|
| 流程分类、文本回复起草 | 免费层（Mistral Large, DeepSeek V3）就够了 |
| 复杂推理、代码生成、长上下文 | 需要前沿模型（Claude Opus, GPT-4.5） |

---

## 参考资料

- [YouTube: 一部影片教你怎麼把任務拆成 agent 能跑的工作流](https://youtu.be/Yzpx4Xaigms)
- [MCP 协议（Model Context Protocol）- 已移交 Linux Foundation](https://modelcontextprotocol.io)
- [RFC 2119 - MUST/SHOULD/MAY 规范](https://datatracker.ietf.org/doc/html/rfc2119)

## 相关笔记

- [[FreeLLMAPI - 开源免费LLM代理]]
- [[MCP - Model Context Protocol]]
- [[Agentic Workflow 最佳实践]]
