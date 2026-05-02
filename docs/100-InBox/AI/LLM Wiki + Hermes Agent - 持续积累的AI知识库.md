---
title: LLM Wiki + Hermes Agent - 持续积累的AI知识库
aliases: [LLM Wiki, Hermes LLM Wiki, Karpathy Wiki]
tags: [ai-agent, status/active, area/distill, type/doc, topic-llm-wiki, topic-knowledge-base]
source: ["https://www.youtube.com/watch?v=Mb5N08xcxtg"]
author: Julian Goldie
created: 2026-04-09 21:16
updated: 2026-04-09 21:16
description: |
  Karpathy 提出 LLM Wiki 理念，Hermes Agent 已内置集成，解决 RAG 无记忆积累的核心痛点。
level: beginner
stars: 3
---

# LLM Wiki + Hermes Agent - 持续积累的AI知识库

> RAG 每次查询都从零开始，知识无法积累。Karpathy 提出的 LLM Wiki 方案让 AI 编译并维护一个持久化的结构化知识库，Hermes Agent 已将其作为内置 skill 交付。

---

## 核心问题：RAG 的记忆缺失

传统 RAG 的工作方式：

```
用户上传文档 → AI 检索 → 拼装答案 → 会话结束 → 一切归零
```

每次提问都是从原始文档重新发现信息，没有记忆、没有积累、没有复合效应。

---

## Karpathy 的 LLM Wiki 方案

### 基本思路

不直接从原始文档检索，而是让 AI 构建和维护一个**持久化的 Wiki**——一组结构化的 Markdown 文件，位于用户和原始素材之间。

**核心理念**：知识编译一次并持续更新，而非每次重新发现。

### 三层架构

```
+-------------------+
|   Raw Sources     |  ← 文档、论文、代码（不可变，只读）
+-------------------+
         ↓
+-------------------+
|   Wiki Layer      |  ← AI 生成的 Markdown 文件（AI 写，人读）
|   - 摘要页面       |
|   - 实体页面       |
|   - 概念页面       |
|   - 对比页面       |
+-------------------+
         ↓
+-------------------+
|   Schema          |  ← 配置文档：结构规则、约定、处理流程
+-------------------+
```

Karpathy 的比喻：LLM 是程序员，Wiki 是代码库。LLM 在一边，Obsidian 在另一边。

### 三大核心操作

| 操作 | 说明 | 复合效应 |
|------|------|----------|
| Ingest | 投入新素材 → AI 读取、提取、创建/更新 Wiki 页面 | 单个素材可能更新 10-15 个页面 |
| Query | 提问 → AI 搜索 Wiki 页面并返回带引用的综合答案 | 好的问答结果可以**回写**为新页面 |
| Lint | 健康检查 → 发现矛盾、过期信息、孤立页面、缺失链接 | 保持 Wiki 长期质量 |

---

## Hermes Agent 集成

### Hermes 是什么

- 开源 AI Agent，GitHub 26,000+ stars
- 自带学习循环：从经验创建 skill，持续改进
- 支持 200+ 模型（通过 OpenRouter），无锁定
- 连接 Telegram / Discord / Slack / WhatsApp / Signal
- 可运行在 $5 VPS 或 GPU 集群

### 集成方式

```
hermes update          # 更新到最新版，获取 LLM Wiki skill
/wiki <研究主题>       # 开始构建 Wiki
```

Hermes 会读取所有投入的素材（项目文档、网页、代码、论文），自动构建跨领域的结构化知识库。

---

## 适用场景

| 场景 | 价值 |
|------|------|
| 学术研究 | 每读一篇论文自动融入已有知识体系 |
| 内容创作 | 研究素材持续积累，不再每个话题从零开始 |
| 开发者 | 代码库 + 文档 + 论文一体化组织 |
| 团队协作 | 会议记录、Slack 讨论自动汇入 Wiki，AI 维护 |

---

## 实践建议

1. 用 Obsidian 作为 Wiki 查看器（免费），Graph View 可视化页面关联
2. 用 Obsidian Web Clipper 抓取网页为 Markdown，放入 raw sources 目录
3. 关注两个文件：
   - Index：所有页面的目录 + 一行摘要
   - Log：AI 操作的时序记录
4. Schema 值得投入时间——它是 AI 维护 Wiki 的规则手册

---

## Karpathy 的洞见

维护知识库最繁琐的不是阅读或思考，而是**簿记工作**——更新交叉引用、保持摘要最新。

人类放弃 Wiki 是因为维护负担增长快于价值增长。AI Agent 不会厌烦，不会忘记交叉引用，一次可以更新 15 个文件。

他还把 LLM Wiki 关联到 Vannevar Bush 1945 年的 Memex 概念——个人知识库 + 关联索引。Bush 没解决的问题是谁来做维护，现在有了答案。

---

## 参考资料

- [视频：Hermes AI + Karpathy LLM Wiki is INSANE!](https://www.youtube.com/watch?v=Mb5N08xcxtg)
- Karpathy LLM Wiki 文档（2026-04-04 发布，48 小时内 5000+ GitHub stars）
- [Hermes Agent GitHub](https://github.com/nouseResearch/hermes)

## 相关笔记
- [[../../200-Distill/200-AI-Tools/Hermes Agent - 自改进AI代理框架]]
