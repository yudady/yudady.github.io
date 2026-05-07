---
title: Stanford AI 系統課程精華 — Beyond LLM 技術地圖
aliases:
  - Beyond LLM
  - Stanford CS230 AI Agents
  - AI Builder 技術地圖
tags:
  - stanford
  - cs230
  - llm
  - prompt-engineering
  - rag
  - fine-tuning
  - agentic-workflow
  - multi-agent
  - ai-engineering
  - status/active
  - type/note
source:
  - "https://www.youtube.com/watch?v=eKW9ITaltWw"
  - "https://cs230.stanford.edu/syllabus/fall_2024/rag_agents.pdf"
author: Gary Chen (视频作者) / Kian Katanforoosh (Stanford CS230 教授)
created: 2026-05-04
updated: 2026-05-04
description: Stanford CS230 "Beyond the Model" 课程精华整理，涵盖 LLM 限制、Prompt Engineering、Fine-Tuning、RAG、Agentic Workflow、Multi-Agent 六层技术栈及 AI Builder 学习路线
level: beginner
stars: 5
---

# Stanford AI 系統課程精華 — Beyond LLM 技術地圖

> Stanford CS230 教授 Kian Katanforoosh 的 "Beyond the Model: Enhancing LLM Applications" 课程，用「横轴 vs 纵轴」的框架，系统梳理了 AI Builder 必须掌握的六层技术栈：从 Prompt Engineering 到 Multi-Agent。本文基于视频总结 + 原始课程 PDF 整理。

**注**：视频转录稿仅有前 3 分钟（YouTube 转录面板渲染异常），后续内容基于课程 PDF 和视频章节结构补充。

---

## 目录

- [横轴与纵轴：LLM 增强的两条路](#横轴与纵轴llm-增强的两条路)
- [Base Model 的四大限制](#base-model-的四大限制)
- [五层纵轴技术栈全景](#五层纵轴技术栈全景)
- [第 1 层：Prompt Engineering](#第-1-层prompt-engineering)
- [第 2 层：Fine-Tuning（谨慎使用）](#第-2-层fine-tuning谨慎使用)
- [第 3 层：RAG（检索增强生成）](#第-3-层rag检索增强生成)
- [第 4 层：AI Agent（自主系统）](#第-4-层ai-agent自主系统)
- [第 5 层：Multi-Agent（多智能体协作）](#第-5-层multi-agent多智能体协作)
- [Evaluation（贯穿所有层级）](#evaluation贯穿所有层级)
- [AI Builder 学习路线图](#ai-builder-学习路线图)
- [参考资料](#参考资料)

---

## 横轴与纵轴：LLM 增强的两条路

```
                  能力 ↑
                  │
        GPT-5 ───┤─── 横轴（换更强模型）
                  │   OpenAI / Anthropic 在做
                  │   普通人无法参与
                  │
        GPT-4 ───┤
                  │         ┌─ 纵轴（在现有模型上叠工程技术）
                  │         │  你能施力的方向
        GPT-3.5 ─┤─────────┤──────────────────→ 时间
                  │  Prompt │  RAG │ Agent │ Multi-Agent
                  │  Eng.   │      │       │
                  └─────────┴──────┴───────┘
                     Augmenting LLM
```

**核心观点**：
- **横轴** = 换更好的 base model（不是你能做的）
- **纵轴** = 在现有 LLM 上面叠各种工程技术（Augmenting LLM）
- 整堂课 / 整支影片讲的**全是纵轴**

---

## Base Model 的四大限制

| 限制 | 说明 | 例子 |
|------|------|------|
| **缺乏领域知识** | 公司内部文件、产品规格、专业 dataset，base model 都不知道 | 自动化农业设备的作物病害数据集市面上找不到 |
| **信息落后** | 模型不可能每几个月重训一次，新词、新事件、新公司不认识 | 年轻人的网络流行语模型听不懂 |
| **控制困难** | LLM 是概率性输出，同一 prompt 两次跑结果不同 | 生产环境退费问题：一下说可以一下说不行 |
| **长 context 衰退** | context 太长时出现 "lost in the middle" 现象 | 把小细节藏进一年会议记录里，模型找不到 |

---

## 五层纵轴技术栈全景

```
┌─────────────────────────────────────────────────────┐
│                Multi-Agent System                    │  ← 第 5 层
│         多个专业 Agent 协作完成任务                      │
├─────────────────────────────────────────────────────┤
│                  AI Agent                           │  ← 第 4 层
│     自主系统：context + memory + tools + actions      │
├─────────────────────────────────────────────────────┤
│                     RAG                             │  ← 第 3 层
│        检索增强生成：接入外部知识库                      │
├─────────────────────────────────────────────────────┤
│                Fine-Tuning                          │  ← 第 2 层
│          微调模型（谨慎使用，数据成本高）                   │
├─────────────────────────────────────────────────────┤
│           Prompt Engineering                        │  ← 第 1 层
│      Prompt 优化 + Chain + Few-Shot + 模板            │
├─────────────────────────────────────────────────────┤
│                  Base Model                         │  ← 底层
│          GPT-4 / Claude / Gemini / ...              │
└─────────────────────────────────────────────────────┘
```

**选择原则**：**从第 1 层开始尝试，不够再加第 3 层，最后才考虑第 2 层。**

---

## 第 1 层：Prompt Engineering

**定位**：第一道防线，成本最低、见效最快的优化手段。

### Centaur vs Cyborg（BCG 研究）

| 类型 | 定义 | 适用场景 |
|------|------|----------|
| **Centaurs（半人马）** | 把任务拆分，一部分自己做、一部分交给 AI | AI 能力边界内的任务 |
| **Cyborgs（赛博格）** | 完全与 AI 融合，持续交互 | 需要深度人机协作的复杂任务 |

> 对 AI 能力前沿内的任务，用 AI 的顾问效率显著更高；对 AI 能力前沿外的任务，用 AI 反而比不用低 19 个百分点。

### 四个技巧

**1. 给出明确指令**

```
❌ "总结这个文档"
✅ "用 5 个要点总结这份 10 页的再生能源科学论文，
    面向政策制定者，聚焦关键发现和政策影响"
```

**2. 鼓励逐步思考（Chain-of-Thought）**

```
"请逐步完成以下任务，不要跳过任何步骤：
 Step 1: 识别论文中三个最重要的发现
 Step 2: 解释这些发现对再生能源政策的影响
 Step 3: 写出 5 要点总结"
```

**3. Zero-Shot vs Few-Shot**

```
Zero-Shot:  直接给任务，不给例子
Few-Shot:  先给 2-3 个输入→输出示例，再给新任务
            效果通常更好，帮助模型理解期望的风格和格式
```

**4. Chain 复杂 Prompt（拆分 + 串联）**

```
单个复杂 Prompt 的效果 < 拆成多个简单 Prompt 串联

Prompt 1: "提取客户评论中的关键问题"
    → 输出: 1. 延迟 2. 包装损坏 3. 紧急需求
Prompt 2: "基于这些问题，拟定专业回复大纲"
    → 输出: 1. 确认问题 2. 解释原因 3. 提供解决方案
Prompt 3: "按大纲写出完整回复"
    → 输出: "亲爱的顾客，我们深表歉意..."
```

### Prompt 测试：PromptFoo

```
┌────────────────┬────────────────────┬─────────┐
│   Test Case    │   Prompt Version   │  Score  │
├────────────────┼────────────────────┼─────────┤
│ 金融新闻 A     │ summarization_v1   │  80%    │
│ 金融新闻 A     │ summarization_v2   │  95%    │
│ 股市新闻 B     │ summarization_v1   │  75%    │
│ 股市新闻 B     │ summarization_v2   │  90%    │
└────────────────┴────────────────────┴─────────┘
```

使用 **LLM-as-Judge** + 量化评估框架（如 PromptFoo、MT-Bench）来系统性测试 Prompt 效果。

---

## 第 2 层：Fine-Tuning（谨慎使用）

**教授的态度**：Proceed with Caution（慎用）

### 为什么不推荐

| 问题 | 说明 |
|------|------|
| 需要大量标注数据 | 高质量的 domain-specific 数据很难获得 |
| 过拟合风险 | Fine-tuned 模型可能丢失通用能力 |
| 时间和成本高 | 尤其当 base model 频繁更新时 |
| RAG 通常够用 | 大多数场景用 RAG 就能解决知识缺口 |

### 什么时候仍然值得 Fine-Tune

- 任务需要**反复、高精度**的输出（法律文书、科学解释）
- 通用 LLM 在**特定领域语言**上持续表现不佳
- 需要大幅改变模型的行为模式（语气、格式、风格）

### 实际案例：Slack 消息 Fine-Tune

基于 140K Slack 消息 fine-tune GPT-3.5-turbo，让模型学会团队内部沟通风格。但这属于"锦上添花"，不是必需品。

**结论**：在 RAG 和 Prompt Engineering 无法解决问题时，再考虑 Fine-Tuning。

---

## 第 3 层：RAG（检索增强生成）

**定位**：解决 base model 知识缺口的最实用方案。

### RAG 解决的三个问题

| 问题 | RAG 如何解决 |
|------|-------------|
| Context window 有限 | 只检索相关片段，不需要塞入全部文档 |
| 知识缺口 | 接入外部知识库（数据库、文档、API） |
| 幻觉（Hallucination） | 基于**检索到的真实文档**生成答案 |

### RAG 核心流程

```
用户查询: "药物 X 有什么副作用？"
    │
    ▼
┌─────────────────────────────────────────────┐
│                RAG Pipeline                   │
│                                              │
│  1. INDEXING（索引）                          │
│     知识库文档 → Embedding → 向量数据库         │
│                                              │
│  2. RETRIEVAL（检索）                         │
│     用户查询 → Embedding → 相似度匹配           │
│     → 找到最相关的文档片段                      │
│                                              │
│  3. AUGMENTATION（增强）                      │
│     用户查询 + 检索到的文档 → 组合成新 Prompt    │
│                                              │
│  4. GENERATION（生成）                        │
│     LLM 基于增强后的 Prompt 生成回答            │
│     "基于文档，答案是..."                      │
│     "如果文档中没有相关信息，回答'我不知道'"      │
│                                              │
└─────────────────────────────────────────────┘
```

### RAG vs Fine-Tuning 决策

| 维度 | RAG | Fine-Tuning |
|------|-----|-------------|
| 知识更新 | 实时（更新文档库即可） | 需要重新训练 |
| 数据需求 | 文档即可 | 需要标注问答对 |
| 成本 | 低 | 高 |
| 幻觉控制 | 好（基于检索文档） | 差 |
| 行为改变 | 有限 | 可改变输出风格 |
| 推荐优先级 | **第一选择** | 最后考虑 |

---

## 第 4 层：AI Agent（自主系统）

**定义**：AI Agent 是一个自主的 AI 系统，使用 context、memory 和 tools 进行推理和行动，处理复杂的多步骤任务以达到特定目标。

### RAG vs Agent 的本质区别

```
RAG 回答:
  Q: "你们的退款政策是什么？"
  A: "购买后 30 天内可退款。" ← 单轮信息检索

Agent 回答:
  Q: "我能退款吗？"
  Agent 内部流程:
    1. RAG 检索退款政策
    2. 询问: "请提供您的订单号"
    3. 调用 API 查询订单详情
    4. 确认: "您的订单符合退款条件，
       金额将在 3-5 个工作日内处理" ← 多步骤自主行动
```

### AI Agent 核心架构

```
┌─────────────────────────────────────────────────┐
│                  AI Agent                         │
│                                                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────────┐   │
│  │  Context  │  │  Memory  │  │    Tools     │   │
│  │  管理     │  │  记忆     │  │   工具调用    │   │
│  │          │  │          │  │              │   │
│  │ • Prompt │  │ • Core   │  │ • API 调用   │   │
│  │ • 历史   │  │   Memory │  │ • 数据库查询  │   │
│  │ • System │  │ • Archival│  │ • 代码执行   │   │
│  │   指令   │  │   Memory │  │ • 搜索引擎   │   │
│  └──────────┘  └──────────┘  └──────────────┘   │
│         │             │              │           │
│         └─────────────┼──────────────┘           │
│                       ▼                          │
│              ┌────────────────┐                   │
│              │  Reasoning     │                   │
│              │  推理引擎       │                   │
│              │  (LLM Core)    │                   │
│              └────────────────┘                   │
│                       │                          │
│                       ▼                          │
│              ┌────────────────┐                   │
│              │    Actions     │                   │
│              │  执行行动       │                   │
│              └────────────────┘                   │
└─────────────────────────────────────────────────┘
```

### 传统软件 vs AI Agent 软件的范式转变

| 维度 | 传统软件 | AI Agent 软件 |
|------|----------|---------------|
| 数据处理 | 结构化数据，强类型输入输出 | 非结构化输入（自由文本），模糊输入输出 |
| 逻辑 | 确定性、规则驱动 | 概率性推理，结果不完全可预测 |
| 开发方式 | 定义函数和工作流 | 组合 Prompt、Chain、Tools |
| 维护 | 修一个 bug 很少影响其他功能 | 调整一个 Prompt 可能破坏多个工作流 |
| 测试 | 确定性输入→确定性输出 | 需要迭代式、探索式测试 |
| 系统设计 | 微服务 / 单体 | "Think like a Manager" |

### Agent 实战案例：旅行规划 Agent

```
用户: "规划 12/15-20 巴黎之旅，含机票、铁塔附近酒店、景点行程"

Step 1: Agent 拆解任务
  ├── 搜索航班 (Flight Search API)
  ├── 搜索酒店 (Hotel Booking API, 靠近埃菲尔铁塔)
  ├── 推荐景点 (Location Recommendation API)
  ├── 确认偏好 (与用户交互)
  └── 预订支付 (Payment Processing API)

Step 2: 执行工具调用 → 组合结果

Step 3: 主动与用户确认
  "以下是方案：
   - 航班：法航直飞 $500 起
   - 酒店：铁塔附近 4 星 $180/晚
   - 景点：铁塔、卢浮宫、巴黎圣母院
   要继续预订吗？"

Step 4: 存储偏好到 Memory
  "用户偏好直飞航班" / "喜欢 4 星酒店" / "对文化景点感兴趣"
```

---

## 第 5 层：Multi-Agent（多智能体协作）

**从单一 Agent 到多个专业 Agent 协作**。

### 三种拓扑结构

```
Sequential（顺序）     Parallel（并行）       Hierarchical（层级）
┌───┐   ┌───┐        ┌───┐                  ┌───────────┐
│ A │──→│ B │──→│ C │ │ A │──→┐               │Orchestrator│
└───┘   └───┘   └───┘ └───┘   │               │   编排器    │
  串行执行                ┌───┐ │               └──┬──┬──┬──┘
                         │ B │─┘                  │  │  │  │
                         └───┘              ┌─────┘  │  │  └──────┐
 适合：严格的先后顺序      并行执行            ┌───┐ ┌───┐ ┌───┐ ┌───┐
                         ┌───┐              │ A │ │ B │ │ C │ │ D │
                         │ C │─┘              └───┘ └───┘ └───┘ └───┘
                         └───┘               各司其职，编排器协调
 适合：独立子任务可并行
                                            适合：复杂多角色协作
```

### 案例：智能家居 Multi-Agent

```
┌─────────────────────────────────────────────────┐
│              Orchestrator Agent                   │
│              (编排器：协调工作流和依赖)              │
├──────┬──────┬──────┬──────┬──────┬──────────────┤
│ 温控  │ 照明  │ 安全  │ 能源  │ 娱乐  │   通知     │
│ Agent │ Agent │ Agent │ Agent │ Agent │   Agent    │
│      │      │      │      │      │            │
│温度调节│亮度控制│监控摄像头│设备节能│影音控制│系统事件提醒│
│通风管理│定时计划│门锁管理│待机模式│媒体播放│能耗报告    │
└──────┴──────┴──────┴──────┴──────┴──────────────┘
```

### Single Agent vs Multi-Agent

| 维度 | Single Agent | Multi-Agent |
|------|-------------|-------------|
| 模块化 | 难以更新，全在一体内 | 容易替换特定组件 |
| 故障隔离 | 一个失败影响全部 | 问题限定在失败的 Agent 内 |
| 优化 | 通用型，效率较低 | 专业 Agent 各自优化 |
| 调试 | 复杂耗时 | 容易隔离问题 |
| 并行处理 | 串行处理，较慢 | 可同时处理多任务 |
| 灵活性 | 所有任务绑在一起 | 可选择性集成第三方工具 |

---

## Evaluation（贯穿所有层级）

**没有评估，就没有优化。**

### 评估框架

```
┌────────────────────────────────────────────┐
│           Evaluation Pipeline               │
│                                             │
│  Test Cases (测试用例)                       │
│      │                                      │
│      ▼                                      │
│  Prompt Variants (多个 Prompt 版本)          │
│      │                                      │
│      ▼                                      │
│  LLM-as-Judge (用 LLM 评估输出质量)          │
│      │                                      │
│      ▼                                      │
│  量化评分 + 对比                             │
│      │                                      │
│      ▼                                      │
│  选择最优 Prompt / 部署                      │
│                                             │
│  工具: PromptFoo, MT-Bench, Chatbot Arena    │
└────────────────────────────────────────────┘
```

### 评估维度

| 维度 | 说明 |
|------|------|
| 准确性 | 输出是否正确、是否有幻觉 |
| 相关性 | 是否回答了用户的问题 |
| 格式一致性 | 输出格式是否符合预期 |
| 延迟 | 响应速度是否可接受 |
| 成本 | Token 消耗是否合理 |

---

## AI Builder 学习路线图

```
阶段 1: 基础 (1-2 周)
├── 理解 LLM 的本质和限制
├── 掌握 Prompt Engineering 基础
│   ├── 明确指令 + 逐步思考
│   ├── Zero-Shot / Few-Shot
│   └── Chain 复杂 Prompt
└── 动手：用 ChatGPT/Claude API 跑通第一个 Prompt

阶段 2: 增强基础 (2-4 周)
├── RAG 系统搭建
│   ├── Embedding + 向量数据库
│   ├── 检索 + 增强 + 生成
│   └── 评估检索质量
├── Prompt 测试框架（PromptFoo）
└── 动手：搭建一个基于公司文档的问答系统

阶段 3: Agent 开发 (4-8 周)
├── AI Agent 概念和架构
├── 工具调用（Function Calling / Tool Use）
├── Memory 管理
├── 评估 Agent 系统
└── 动手：构建一个客服 Agent（RAG + Tools + Memory）

阶段 4: Multi-Agent (8+ 周)
├── Multi-Agent 拓扑设计
├── 编排器模式
├── Agent 间通信
└── 动手：构建多角色协作系统

阶段 5: 持续学习
├── Fine-Tuning（需要时再学）
├── 最新模型和工具跟进
├── 实际项目经验积累
└── 部署和运维（安全、成本、监控）
```

**关键原则**：
- ✅ 从 Prompt Engineering 开始，成本最低
- ✅ RAG 是解决知识缺口的第一选择
- ✅ Agent 是从"回答问题"到"完成任务"的跃迁
- ❌ 不要一开始就 Fine-Tune
- ❌ 不要跳过 Evaluation

---

## 参考资料

- [Stanford CS230 Lecture PDF: Beyond the Model](https://cs230.stanford.edu/syllabus/fall_2024/rag_agents.pdf)
- [Stanford CS230 Course](https://cs230.stanford.edu/)
- [YouTube: Stanford AI Lecture - How to Build AI Agents](https://www.youtube.com/watch?v=E1ytrEBe34I)
- [PromptFoo - Prompt Testing Framework](https://www.promptfoo.dev/)
- [McKinsey: Why Agents Are the Next Frontier of Gen AI](https://www.mckinsey.com/capabilities/mckinsey-digital/our-insights/why-agents-are-the-next-frontier-of-generative-ai)
- [Chain-of-Thought Prompting (Wei et al., 2023)](https://arxiv.org/abs/2201.11903)
- [RAG Survey (Gao et al., 2024)](https://arxiv.org/abs/2312.10997)

## 相关笔记

- [[Hermes Agent v0.11-v0.12 - Kanban 多智能体工作流]] — Multi-Agent 实际案例
- [[Codex goal 底层逻辑 - Ralph Loop 与长时间自主编码]] — Agent Loop 实践
