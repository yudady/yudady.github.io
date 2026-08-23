---
title: Stanford BeyondLM — AI 工程师五层技术框架
aliases: [Stanford Beyond LLM, AI工程师技术栈, 吳恩達 Agent Workflow]
tags:
  - ai-engineering
  - status/active
  - type/learning-note
  - llm
  - prompt-engineering
  - rag
  - fine-tuning
  - agent
  - multiagent
source:
  - "https://www.youtube.com/watch?v=br6IQzn5eHs"
author: AI启示录（基于 Stanford BeyondLM 课程）
created: 2026-05-15
updated: 2026-05-15
description: Stanford BeyondLM 课程精华整理：从 base model 限制出发，系统讲解 Prompt Engineering → Fine Tuning → RAG → Agent Workflow → Multi-Agent 五层技术框架，建立 AI 工程的完整认知体系。
level: intermediate
stars: 4
---

# Stanford BeyondLM — AI 工程师五层技术框架

> Stanford 大学 BeyondLM 课程的精华整理。从 base model 的本质限制出发，逐层讲解强化 LLM 的五大技术手段，最终建立 AI 系统设计的完整认知框架。

## 目录

- [核心问题：Base Model 的限制](#核心问题base-model-的限制)
- [第一层：Prompt Engineering](#第一层prompt-engineering)
- [第二层：Fine Tuning](#第二层fine-tuning)
- [第三层：RAG（检索增强生成）](#第三层rag检索增强生成)
- [第四层：Agent Workflow](#第四层agent-workflow)
- [第五层：Multi-Agent](#第五层multi-agent)
- [Evaluation 评估体系](#evaluation-评估体系)
- [Case Study：AI 客服系统](#case-studyai-客服系统)
- [学习路线建议](#学习路线建议)

---

## 核心问题：Base Model 的限制

LLM 变强有两条路：

```
横轴（Horizontal）          纵轴（Vertical）
换更强的 base model    vs   在现有 model 上叠工程技术
GPT-4 → GPT-5               augmenting LLM
（OpenAI/Anthropic 做）     （AI 工程师该做的事）
```

**普通人能发力的是纵轴。** 课程全部内容都在讲纵轴的事。

### Base Model 四大限制

| # | 限制 | 说明 | 举例 |
|---|------|------|------|
| 1 | 缺乏 Domain Knowledge | 训练数据不含公司内部资料 | 农业病害数据、内部产品规格 |
| 2 | 资讯滞后 | 模型不可能每月重训 | 新公司、新词、网络用语 |
| 3 | 控制困难 | 概率性输出，结果不稳定 | 退费场景：一下说可以一下说不行 |
| 4 | 长 Context 腿部 | 即使 100K+ context window 仍有 lost-in-the-middle | 细节淹没在大量信息中 |

**结论**：光换更强的 base model 不够，必须在纵轴上叠加工程能力。

---

## 第一层：Prompt Engineering

> Stanford 教授观点：Prompt Engineering 不会是一个职业，但会是每个工程师一辈子的基本功。就像九九乘法表。

### BCG 研究：AI 使用的三个发现

| 发现 | 含义 | 启示 |
|------|------|------|
| **Capability Frontier** | AI 只在部分任务上加分，有些反而扯后腿 | 要知道 AI 的边界在哪 |
| **Falling Asleep at the Wheel** | 在 AI 不擅长的任务上太信任它，结果更差 | 不确定时要人工审核 |
| **Centaur vs Cyborg** | 两种使用模式 | 按任务性质切换 |

```
Centaur（半人马）              Cyborg（生化人）
+-------------------+          +-------------------+
| 丢一个 prompt     |          | 一来一回对话      |
| AI 独立完成       |          | 高频交互协作      |
| 自己去做别的事     |          | 逼出 AI 最佳输出  |
+-------------------+          +-------------------+

✅ 适用：重复性高、流程清楚    ✅ 适用：需判断、创意、来回讨论
   企业自动化 workflow            学生/写作者的常见模式
```

**核心原则**：两种模式没有好坏，重点是**有意识地切换**。

### Prompt 的三要素

好 prompt 要回答三个问题：
1. **角色是谁？** — 定位 AI 的身份
2. **产出格式是什么？** — 长度、结构、受众
3. **重点是什么？** — 聚焦方向

```
❌ 請幫我總結這篇文章

✅ 請將這份再生能源論文整理成五個重點摘要，
   並且聚焦在其背後的政策意涵上。
   （對象 = 政策制定者，長度 = 5 點，焦點 = 政策意涵）
```

### Prompt Chaining（最重要的技巧）

> 注意：Chaining ≠ Chain of Thought。CoT 是叫模型 step by step 思考，Chaining 是把复杂 prompt 拆成多个独立 prompt。

```
單一 Prompt（黑盒子）              Prompt Chaining（可觀察）
+-----------------------+          +-----------------------+
| 讀這封投訴              |          | Prompt 1:              |
| 寫一封專業回應           |    →     |   抽出客戶在抱怨什麼？ |
| ❌ 出錯不知道調哪裡     |          |           ↓           |
+-----------------------+          | Prompt 2:              |
                                   |   用問題大綱 → 起草回信 |
                                   |           ↓           |
                                   | Prompt 3:              |
                                   |   寫完整回信           |
                                   | ✅ 每步可獨立測試/Debug |
                                   +-----------------------+
```

**Chaining 的两大价值**：
- **提升模型表现**：拆解后每步任务更聚焦
- **Observability（可观察性）**：知道 LLM 在做什么，哪步出了问题

---

## 第二层：Fine Tuning

> Stanford 教授立场：**能不做就不做。**

### 四个不推荐的原因

| # | 原因 | 说明 |
|---|------|------|
| 1 | 数据成本高 | 需要大量高质量标注数据 |
| 2 | 容易 Overfit | 特定任务变强，通用能力下降 |
| 3 | 时效性差 | 花两个月微调完，新 base model 直接碾压 |
| 4 | Prompt 通常够用 | 大多数场景下 PE 能达到同样效果，且成本低 |

### Fine Tuning 什么时候值得做？

```
+-------------------+     +-------------------+
| ✅ 值得 Fine Tune |     | ❌ 不值得 Fine Tune|
+-------------------+     +-------------------+
| 法律/医学等需要    |     | 大多数商业场景      |
| 高精度输出的领域    |     | 可被 Prompt/RAG    |
| Base model 在某    |     | 替代的任务          |
| domain 表现吃力    |     |                     |
+-------------------+     +-------------------+
```

### Domain Knowledge 的解决方案选择

```
你的 Domain Knowledge 怎麼塞進去？

Prompt Engineering        →  能塞的就用 PE（成本最低）
      ↓ 不夠
Fine Tuning              →  成本效益比通常不划算
      ↓ 不划算
RAG                       →  ✅ 標準解法（下一節）
```

---

## 第三层：RAG（检索增强生成）

> AI 工程师面试最常考的题目之一。面试官常会让你用五岁小孩听得懂的方式解释。

### RAG 解决的痛点

| 痛点 | 说明 |
|------|------|
| Context Window 太小 | 长文档塞不进去 |
| 长文本抓不准信息 | 细节被忽略 |
| 时效性 | 模型知识截止 |
| 幻觉（Hallucination） | 模型编造不存在的答案 |

### RAG 工作流程

```
準備階段（離線）                    查詢階段（線上）
+-------------------+              +-------------------+
| 文件/資料庫        |              | 使用者問題         |
|       ↓           |              |       ↓           |
| Embedding Model   |              | Embedding Model   |
| (文字 → 向量)     |              | (問題 → 向量)     |
|       ↓           |              |       ↓           |
| Vector Database   |  ← 搜尋 →  | 語意相似度匹配     |
| (儲存所有向量)     |  最近K個片段  |       ↓           |
+-------------------+              +-------------------+
                                          ↓
                              +-----------------------+
                              | 組合最終 Prompt:       |
                              | + System Prompt        |
                              | + 檢索到的 Documents   |
                              | + User Query           |
                              +-----------------------+
                                          ↓
                                   LLM 生成回答
```

**关键概念**：
- **向量（Vector）**：把文字的语义转换成一串数字，语义相近的文字在数学空间中距离也近
- **不是关键词比对**：文件写「不良反应」也能匹配「副作用」的查询
- **约束模型**：prompt 中明确「如果 documents 里没有答案，就说不知道」——避免幻觉
- **来源追溯**：要求模型附上答案来自第几页、第几章，让用户自行验证

### Chunking（分块）策略

```
基本做法                         進階做法（多層次存儲）
+-------------------+            +-------------------+
| 50 頁文件         |            | 整本書 (向量)     |
| 切成固定大小片段   |     →      |   ↓               |
| 每段各自轉向量     |            | 每章節 (向量)     |
| ❌ 細節容易丟失   |            |   ↓               |
+-------------------+            | 每段落 (向量)     |
                                 | ✅ 先找章節再鑽段落|
                                 +-------------------+
```

### RAG vs 长 Context Window

> 有人问：模型支持超长 context window 后，RAG 是否会被淘汰？

**Stanford 教授观点：理论上对，实际上错。**

| 维度 | 直接读全文 | RAG |
|------|-----------|-----|
| 延迟（Latency） | 每次重读整个数据库，没人等得了 | 预建索引，快速定位 |
| 准确度 | 仍有 lost-in-the-middle | 语义检索更精准 |
| 更新效率 | 资料更新需重新处理全文 | 只更新变更部分 |
| 成本 | Token 消耗巨大 | 只传入相关片段 |

**类比**：搜索引擎靠预先建好的索引来快速定位，不可能每次查询都重新爬一遍整个网络。

---

## 第四层：Agent Workflow

> 术语来自吴恩达（Andrew Ng）。因为「AI Agent」已经被滥用，他用「Agent Workflow」来精确描述：把 prompt、工具、元件组合进一个有结构的工作流程里。

### RAG vs Agent

```
RAG（工具）                        Agent（系統）
+-------------------+              +-------------------+
| 使用者：我想退訂單 |              | 使用者：我想退訂單 |
|       ↓           |              |       ↓           |
| 撈取退費政策文件   |              | 1. RAG 撈取政策    |
| 給 LLM 當參考     |              | 2. 主動問訂單編號  |
| ❌ 只能提供資料    |              | 3. Tool 查訂單     |
+-------------------+              | 4. 確認 → 退費      |
                                   | 5. 告知處理時間     |
                                   | ✅ 完整處理請求     |
                                   +-------------------+
```

**RAG 是工具，Agent 是使用 RAG 的系统。**

### 传统软件 vs Agentic AI 的四大差异

| 维度 | 传统软件 | Agentic AI |
|------|---------|------------|
| **资料** | 结构化（JSON、DB、表单） | 非结构化（自由文本、图片、语音） |
| **逻辑** | Deterministic（同 input = 同 output） | Probabilistic（同 input 可能不同 output） |
| **架构心态** | 写 microservices/monolith，精确控制每步 | Think like a manager，给目标和限制，让 AI 自己决定 |
| **测试** | 确定性：跑 100 次结果一样 | 探索式：对 context 高度敏感，无法穷举 |

### 落地原则

> **能 deterministic 解的问题就 deterministic 解，剩下的部分加上护栏。**

```
Stanford 教授自己的例子：Skills Assessment

選擇題/配對題/拖拉題          語音體/混合體型
+-------------------+        +-------------------+
| ✅ Deterministic  |        | ❌ 無標準答案      |
| 有標準答案         |   vs   | LLM 模糊評分      |
| 程式自動算分       |        | Fuzzy Scoring     |
+-------------------+        | + Appeal Feature  |
                            |   (真人可覆審)     |
                            +-------------------+
```

**Human-in-the-loop 不是让 AI 零错误，而是在它出错时有人接得住。**

### Agent 的三个核心要素

```
+-------------------+
| 1. Prompt         | ← 角色、能力、限制
+-------------------+
| 2. Context Mgmt   | ← Memory + RAG 資料
+-------------------+
| 3. Tools          | ← 外部能力（做事 + 查資料）
+-------------------+
```

#### Context Management

```
Working Memory                    Archival Memory
+-------------------+              +-------------------+
| 高頻、即時         |              | 低頻、可延遲       |
| 使用者名字         |              | 過去五年的訂房記錄 |
| 當次目的地         |              | 歷史偏好           |
| 對話歷史           |              | 需要時再去撈       |
+-------------------+              +-------------------+

Context Management 本質：把對的資訊，在對的時間，提供給 Agent
```

### Agent 自主性三层模型

```
Level 3: Fully Autonomous          ← 能力最強，風險最高
| 給 code editor、web search
| 自己決定步驟、自己選工具
| ⚠️ 判斷錯誤可能定 100 張機票
+----------------------------------+
Level 2: LLM + Tools (推薦起點)    ← 目前最常見的 production setup
| 給一組工具，Agent 自己決定怎麼用
| 掌控工具範圍，給判斷空間
+----------------------------------+
Level 1: Hard-coded Steps          ← 安全可預測，但僵硬
| 步驟固定：識別意圖 → 查歷史 → 呼叫 API
| 遇到預期外情況就卡住
+----------------------------------+
```

### MCP（Model Context Protocol）

```
傳統做法（每個 API 單獨串接）        MCP 做法（通用協議層）
+-------------------+              +-------------------+
| Agent → API A     |              |                   |
| Agent → API B     |     →        | Agent → MCP Server → 所有服務
| Agent → API C     |              |        (一個插頭)  |
| ❌ 每個都要教      |              | ✅ 統一介面       |
+-------------------+              +-------------------+

MCP 的更大想像：Agent to Agent Communication
→ 把別人做好的 Agent 當工具呼叫（Multi-Agent 的基礎）
```

**MCP 类比**：以前去不同国家要带一堆转换插头，有了 MCP 一个插头全通。

---

## 第五层：Multi-Agent

### 为什么需要 Multi-Agent？

| 原因 | 说明 | 举例 |
|------|------|------|
| **平行处理** | 可并行的任务不需要排队跑 | 找航班 + 找饭店 + 查天气同时进行 |
| **可复用性** | 同一个 Agent 可服务多个场景 | Design Agent 给行销 + 产品团队用 |

### 什么时候不需要？

> **一个 Agent 能解决的任务，硬上 Multi-Agent 反而增加复杂度。**
> 工程设计原则：能简单就简单，不要 over-design。

### 两种互动模式

```
Hierarchical（推薦）                 Flat
+-------------------+               +-------------------+
| 使用者             |               | Agent A ←→ Agent B|
|       ↓           |               |   ↕          ↕   |
| Orchestrator      |               | Agent C ←→ Agent D|
|   ↓     ↓    ↓    |               | ❌ 溝通成本高     |
| Agent  Agent Agent |               +-------------------+
| ✅ 指揮清楚       |
+-------------------+

混合模式（實際推薦）：
- 使用者只跟 Orchestrator 講話
- 某些 Agent 之間可以有水平溝通
  （例：溫控 Agent ↔ 能源管理 Agent 直接互通）
```

**Agent 间通信 = MCP Protocol**：每个 Agent 对外暴露一组接口，其他 Agent 像调用工具一样调用它。结构干净、容易 debug、支持并行。

---

## Evaluation 评估体系

> Evaluation 是 production 系统的命脉。不管是单 Agent 还是 Multi-Agent，上线前都要回答同一个问题：**怎么知道它真的有用？**

### 三维评估框架

```
                    Objective                    Subjective
                 （可自動驗證）                 （無標準答案）
                +-------------------+        +-------------------+
End to End      | 整體回復正確率    |        | 使用者滿意度       |
（看整體）      | 語氣是否合適      |        | 同理心夠不夠       |
                +-------------------+        +-------------------+
Component-based | 抽出的 Order ID    |        | 回信是否有禮貌     |
（拆開看）      | 是否正確           |        | 各步驟表現評分     |
                +-------------------+        +-------------------+

                Quantitative                   Qualitative
                （數字）                        （感覺）
                +-------------------+        +-------------------+
                | 成功率幾%         |        | 哪裡有幻覺        |
                | 延遲多久          |        | 語氣哪裡不對      |
                +-------------------+        | 使用者哪步卡住    |
                                                +-------------------+
```

**八个象限都要做**：光看整体不知道哪里坏，光看组件可能整体体验还是差。

### LLM-as-Judge 四种玩法

| # | 玩法 | 说明 |
|---|------|------|
| 1 | **Pairwise Comparison** | 给 Judge 两个答案，问哪个好 |
| 2 | **Single Answer Grading** | 直接打 1-5 分 |
| 3 | **Reference Guided** | 多给一个标准答案做对比 |
| 4 | **Rubric Based** | 自定义评分标准（例：5 分 = 100 字内含 3 个重点，第一句是 overview） |

四种可混用（如 Rubric + Few-shot examples）。

### Subjective Review 四步法（Travel Agent 礼貌度评估实例）

```
Step 1: Analysis（人工）
  抽 20 個對話人工閱讀
  發現問題：LLM 講話超短、有點機車、沒同理心
      ↓
Step 2: Design（設計自動化）
  用 LLM-as-Judge + 自訂禮貌度 Rubric
  把 Step 1 發現的問題翻譯成評分標準
      ↓
Step 3: A/B Test Model（一次只動一個變數）
  固定 Prompt，換底層模型（GPT → Claude）
  讓 Judge 評分，看哪個模型禮貌度最高
      ↓
Step 4: A/B Test Prompt（一次只動一個變數）
  固定 Model，改 Prompt
  "act like a travel agent" → "act like a helpful travel agent"
  看一個詞的差距影響多大
```

**核心原则**：
1. 先人工扫出问题，再设计自动化 eval
2. 模型和 Prompt 两个变量**一次只动一个**

---

## Case Study：AI 客服系统

> 任务：使用者说「我要改 A127 订单的地址，因为我搬家到建国南路了」，AI 客服怎么做？

### Step 1：Task Decomposition（任务拆解）

> 教授最欣赏的学生回答：「先去客服旁边走一两天，看他们实际怎么处理。」

观察后发现的五步流程：

```
Step 1: 抽出關鍵資訊        → LLM（一次 API 呼叫）
  - 客戶 ID？Order ID？新地址？

Step 2: 查資料庫            → Custom Tool / MCP Server
  - 撈取客戶記錄

Step 3: 查公司政策          → RAG
  - 訂單能不能改地址？是否已出貨？
  （政策會更新，需要 RAG 的效率 + 時效性）

Step 4: 起草回信            → LLM
  - 根據前幾步收集的資訊撰寫

Step 5: 發送 Email          → Email Tool
  - 用實際的 email 寄送工具
```

### Step 2：判断每步用什么工具

```
判斷方式：哪些步驟是 Fuzzy？哪些是 Deterministic？

Fuzzy（用 LLM）               Deterministic（用 Tool/RAG）
+-------------------+         +-------------------+
| Step 1: 抽資訊    |         | Step 2: 查 DB     |
| Step 4: 寫回信    |         | Step 3: 查政策    |
+-------------------+         | Step 5: 發信      |
                              +-------------------+

AI Builder 的真實工作：
知道每個工具/技術的能力和限制 → 炒出一盤你需要的菜
```

### Step 3：建立评估系统

```
End to End:
  ✅ 回復正確率、語氣、使用者滿意度

Component-based:
  ✅ 抽資訊準確度、API 錯誤率、政策遵守率

Objective:
  ✅ 抽出的 Order ID 是否正確？有沒有違反退貨政策？

Subjective:
  ✅ 回信是否有禮貌？有沒有同理心？（人工 + LLM Judge）

Quantitative:
  ✅ 改地址成功率、latency、退費正確率

Qualitative:
  ✅ 哪裡有幻覺？哪裡語氣不一致？使用者哪步困惑？
```

**完整流程总结**：
1. 把大任务拆解成小任务
2. 设计工作流程（选工具 + 判断 fuzzy/deterministic）
3. 建立评估系统确保产出稳定

---

## 学习路线建议

> 教授建议：从实作中学习。找生活或工作中的痛点，从痛点出发思考解决方法，过程中自然会发现需要学会哪些技术。

### 五层技术速查

| 层级 | 技术 | 成本 | 适用场景 | 优先级 |
|------|------|------|---------|--------|
| L1 | Prompt Engineering | 最低 | 所有场景 | ⭐⭐⭐⭐⭐ |
| L2 | Fine Tuning | 高 | 法律/医学等高精度领域 | ⭐⭐ |
| L3 | RAG | 中 | 需要 domain knowledge | ⭐⭐⭐⭐⭐ |
| L4 | Agent Workflow | 中-高 | 需要多步驟自動化 | ⭐⭐⭐⭐ |
| L5 | Multi-Agent | 高 | 平行處理 + 可複用性 | ⭐⭐⭐ |

**关键心态**：
- 不要盲目跟风，看到别人做 Multi-Agent 就跟着做
- 不要觉得 Fine Tuning 帅就去玩模型
- 每个技术都有存在理由和适用情境，搞清楚再选

---

## 参考资料

- [Stanford BeyondLM 课程](https://www.youtube.com/watch?v=br6IQzn5eHs)（AI启示录 整理）
- Stanford CS25 / Beyond LLMs 原始课程
- 吴恩达 Agent Workflow 系列

## 相关笔记

- [[RAG 检索增强生成]]
- [[Prompt Engineering 技巧集]]
- [[AI Agent 设计模式]]
