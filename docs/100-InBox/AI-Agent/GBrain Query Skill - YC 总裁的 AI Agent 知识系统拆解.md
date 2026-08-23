---
title: GBrain Query Skill — YC 总裁的 AI Agent 知识系统拆解
aliases: [GBrain, Thin Harness Fat Skills, Garry Tan AI Brain]
tags:
  - ai-agent
  - knowledge-graph
  - rag
  - architecture
  - status/active
  - type/video-notes
source:
  - "https://www.youtube.com/watch?v=2sG9UpQ4y1g"
  - "https://github.com/garrytan/gbrain"
author: 思思主播 (视频), Garry Tan (项目作者)
created: 2026-05-07
updated: 2026-05-07
description: YC 总裁 Garry Tan 开源的 GBrain 知识系统，通过「合约式」Skill 设计让 AI Agent 拒绝幻觉、精准回答问题
level: intermediate
stars: 4
---

# GBrain Query Skill — YC 总裁的 AI Agent 知识系统拆解

> GBrain 是 YC 总裁 Garry Tan 个人使用的 AI Agent 知识管理系统，支撑着 17,888 页文档、4,383 个人物实体、723 家公司的规模，通过 34 个 Markdown Skill 文件和严格的知识治理规则，实现了 AI Agent 的精准、可控、零幻觉运作。GitHub 13.3k stars，MIT 协议。

---

## 目录

- [系统概况](#系统概况)
- [核心设计哲学：Thin Harness, Fat Skills](#核心设计哲学thin-harness-fat-skills)
- [Query Skill 的五条铁律](#query-skill-的五条铁律)
- [架构深度解析](#架构深度解析)
- [三大可带走的核心原则](#三大可带走的核心原则)
- [参考资料](#参考资料)

---

## 系统概况

**实际运行规模：**

- 17,888 个页面（Markdown 文件）
- 4,383 个人物实体
- 723 家公司
- 21 个 Cron Job 自动运行
- 34 个 Skill 文件（总计约 6KB 的 Markdown）
- 12 天内构建完成

**基准测试（BrainBench）：**

- Precision@5: 49.1%，Recall@5: 97.9%
- 加入知识图谱后比纯向量搜索提升 +31.4 个百分点 P@5
- 基于 240 页 Opus 生成的富文本语料库

**技术栈：**

- 后端：TypeScript（97.3%），Bun 运行时
- 数据库：PGLite（嵌入式）/ Supabase Postgres + pgvector
- 搜索：向量搜索（HNSW cosine）+ 关键词搜索（tsvector）+ RRF 融合 + 知识图谱遍历
- 接口：MCP Server（stdio / HTTP + OAuth 2.1）
- 配套：GStack（编码技能包，70k+ stars）

---

## 核心设计哲学：Thin Harness, Fat Skills

Garry Tan 的核心哲学可以浓缩为一句话：**Markdown is code**（Markdown 就是代码）。

```
┌─────────────────────────────────────────────────┐
│           Fat Skills（34 个 Markdown）           │
│  编码判断逻辑、工作流程、领域知识、质量门控        │
│  90% 的系统智能存在于这里                        │
├─────────────────────────────────────────────────┤
│           Thin Harness（~200 行）                │
│  运行模型循环、读写文件、管理上下文、强制安全      │
├─────────────────────────────────────────────────┤
│           Deterministic Tools                   │
│  SQL 查询、代码执行、文件操作、图谱遍历           │
│  相同输入 → 相同输出，100% 可信                  │
└─────────────────────────────────────────────────┘
```

**Skill 文件不是 Prompt，是合约。** 区别在于：

| 维度 | 传统 Prompt | GBrain Skill（合约） |
|------|-------------|---------------------|
| 性质 | 建议 + 拜托 | 强制条款 |
| 约束 | "请不要..." | "如果你做 X，系统必须 Y" |
| 验证 | 事后检查 | 机制上可验证（输入/输出格式锁定） |
| 边界 | 模糊、可协商 | 明确、不可让步 |
| 智力归属 | LLM 的通用知识 | Skill 文件中的编码逻辑 |

---

## Query Skill 的五条铁律

### 规矩一：Grounded Only（终结幻觉）

**强制条款**：每一个回答都必须从大脑已有内容中生长出来，每个主张都必须附上明确的页面标识作为引用。

关键设计 — **反模式（Anti-pattern）设定**：

```text
┌──────────────────────────────────────┐
│  大脑有相关信息？                     │
│     ├── YES → 正常回答 + 引用         │
│     └── NO  → 严格禁止用通用知识脑补  │
│              → 明确输出「缺失标记」   │
└──────────────────────────────────────┘
```

**为什么这么硬？** 因为 GBrain 是一个会不断自我连接的知识图谱。一旦在写入阶段把幻觉写回去，这些错误信息会在图里建立连接，"像在水源里下毒"，最终污染整个系统。

### 规矩二：Source Precedence（来源优先级）

遇到多个来源互相冲突时，严格按以下层级裁决：

```
优先级 1 (最高): 用户直接陈述
优先级 2:         系统编译过的事实 (Compiled Truth)
优先级 3:         按时间倒序排列的原始证据 (Timeline)
优先级 4 (最低):  外部搜索结果
```

**规则要求**：冲突发生时必须把矛盾点标出来，两边引用都要附上，绝对不允许悄悄自己挑一边站。

### 规矩三：Gap Flag（缺失标记也是标准输出）

在传统 LLM 系统中，"我不知道" 被视为失败。在 GBrain 中，缺失被重新定义为**一等公民字段**：

```yaml
# 标准输出格式
answer: "..."
citations: [page-A, page-B]
gaps: ["大脑中未收录此主题的 XX 维度信息"]
```

**缺失标记的价值**：

- 在 17,888 页的规模下，明确指出缺失本身就是高价值信号
- 系统层面可直接触发下游的数据抓取或扩充工作流
- **未知不是系统漏洞，而是可执行的系统信号**

### 规矩四：Chunk-First（精准搜索）

Token 预算不是省钱的工具，而是**判断查询意图的关键指标**：

| 查询意图 | 策略 | Token 消耗 |
|----------|------|-----------|
| "跟我说说 X 的全貌" | 载入完整页面 | 高 |
| "有没有人提过 Y？" | 载入数据碎片 | 极低 |

Skill 合约没有把 text window 写死，而是给出了一套意图→载入策略的规则。这给 AI 开发者提供了极度清晰的判断依据。

### 规矩五：Graph First（关系型问题走结构化图谱）

遇到关系型问题（谁任职于哪里、谁投资了谁），直接绕过混合搜索，走知识图谱遍历：

```text
传统向量搜索方式:
  问题 → Embedding → 几万维向量空间大海捞针 → 可能命中

GBrain 图谱方式:
  "谁参加了 YC Demo Day?"
  → 触发 graph-query --type attended --depth 1
  → 精准提取所有通过 attended 关联到该活动的实体
  → 一秒钟完成，零歧义
```

**性能数据**：加入图谱遍历后，P@5 从 17.7% 提升到 49.1%（+31.4 个百分点）。

---

## 架构深度解析

### 搜索管线（约 20 种确定性技术叠加）

```
问题
  │
  ├─ 意图分类器（实体/时间/事件/通用 — 自动路由）
  ├─ 多查询扩展（Haiku 将问题改写 3 种方式）
  ├─ 向量搜索（HNSW cosine over OpenAI embeddings）
  ├─ 关键词搜索（Postgres tsvector）
  ├─ 源感知排序（curated dirs 优先于 chat/daily 噪音）
  ├─ 硬排除（test/ / archive/ / .raw/ 直接过滤）
  ├─ RRF 融合（score = sum(1/(60+rank))）
  ├─ Cosine 重排序
  ├─ Compiled Truth 加权
  ├─ Backlink 加权（关联多的实体排名更高）
  └─ 4 层去重 + Compiled Truth 保证
```

### 知识图谱（自动连线，零 LLM 调用）

```
写入会议页面（提及 Alice 和 Acme AI）
  → 自动提取实体引用（正则匹配，零 LLM 调用）
  → 推断关系类型：
    "CEO of X"     → works_at
    "invested in"  → invested_in
    "advises"      → advises
    "founded"      → founded
  → 协调过期链接
  → Backlink 排名加权
```

### 数据模型：Compiled Truth + Timeline

每个页面遵循编译事实 + 时间线的模式：

```
---
type: concept
title: Do Things That Don't Scale
tags: [startups, growth, pg-essay]
---

Paul Graham 的核心论点：初创公司早期应该做不可扩展的事。
关键洞察：不可扩展的努力教会你用户真正想要什么。

---

- 2013-07-01: 发表于 paulgraham.com
- 2024-11-15: 在 W25 批次启动演讲中被引用
```

上方 `---` 内：**Compiled Truth**（当前最佳理解，有新证据时覆写）
下方：**Timeline**（只追加，永不编辑）

### Skill vs Code 判断决策树

```
这个步骤需要 Agent 思考、适应或提问吗？
  ├── YES → Skill（Markdown 流程）
  └── NO  → 相同输入总是产生相同输出吗？
              ├── YES → Code（CLI 命令）
              └── NO  → Skill
```

**示例**：
- `gbrain integrations list` = **Code**（读文件、查环境变量，确定性）
- `gbrain query "who works at Acme?"` = **Skill**（需要判断意图、选择策略）

---

## 三大可带走的核心原则

### 1. 把 Skill 当合约写

不是写"建议"，而是写"不可让步的判断标准 + 输出格式"。用输入/输出约束把 Agent 能活动的边界钉死。

### 2. 用反模式定义系统边界

与其疯狂堆叠"请务必做 X"，不如精确写下"你绝对不能做 Y"。用反模式来划定系统的边界，远比正向约束有效。

### 3. 按职责切分搜索工具

```
关键字搜索  → 精确匹配 slug、专有名词
混合搜索    → 概念匹配 + 语义相似度
图谱遍历    → 关系型问题（谁投资了谁）
```

一个干净的架构会把这三者的职责严格分开，而不是一股脑全塞进单一向量数据库。

---

## 参考资料

- [GBrain GitHub 仓库](https://github.com/garrytan/gbrain) — 13.3k stars, MIT
- [Thin Harness, Fat Skills — 设计哲学原文](https://github.com/garrytan/gbrain/blob/master/docs/ethos/THIN_HARNESS_FAT_SKILLS.md)
- [BrainBench 评估框架](https://github.com/garrytan/gbrain-evals)
- [视频来源：思思主播 YouTube](https://www.youtube.com/watch?v=2sG9UpQ4y1g)

## 相关笔记

- [[RAG 系统设计]]
- [[AI Agent 架构模式]]
- [[知识图谱在 AI 系统中的应用]]
