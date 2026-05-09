---
title: Garry Tan 的个人 AI 作业系统 - Fat Skills, Fat Code, Thin Harness
tags:
  - ai-agent
  - personal-knowledge-system
  - skill-design
  - garry-tan
  - compounding
  - workflow
  - status/active
  - type/video-notes
source:
  - "https://www.youtube.com/watch?v=Oml94I2DRfE"
author: 思思主播 (解读 Garry Tan)
created: 2026-05-09
updated: 2026-05-09
description: YC CEO Garry Tan 的个人 AI 系统架构拆解：fat skills + fat code + thin harness，让每次 AI 交互产生知识复利
level: intermediate
stars: 5
---

# Garry Tan 的个人 AI 作业系统

> YC CEO Garry Tan 凌晨两点还在写 code，因为他不是在交付任务，而是在砌一块会跟其他砖块自动产生联结的砖。核心架构九个字：Fat skills. Fat code. Thin harness.

## 目录

- [从 chatbot 到操作系统](#从-chatbot-到操作系统)
- [三大核心架构](#三大核心架构)
- [Book-mirror 案例：知识复利的具象化](#book-mirror-案例知识复利的具象化)
- [Skillify：为什么 skill 比 prompt 划算](#skillify为什么-skill-比-prompt-划算)
- [100,000 页 Brain 与 Entity Propagation](#100000-页-brain-与-entity-propagation)
- [模型选择逻辑](#模型选择逻辑)
- [如何起步](#如何起步)
- [两种生存策略](#两种生存策略)
- [参考资料](#参考资料)

---

## 从 chatbot 到操作系统

**Garry Tan 的核心洞见：**

```
大多数人用 AI 的方式:
  ChatGPT → 问问题 → 拿答案 → 关页面 → 结束
  = 档案柜（存东西，不连结）

Garry Tan 用 AI 的方式:
  AI 系统 → 接收需求 → 调用知识库 → 产生输出 → 反思修正 → 写入知识库
  = 神经系统（连结一切，自动反应）
```

> "This is not a writing tool. It's not a search engine. It's not a chatbot. It's a second brain that actually works."
> — Garry Tan

**关键区别：**

| 维度 | Chatbot（档案柜） | 个人 AI（神经系统） |
|------|-------------------|-------------------|
| 记忆 | 下次要重新解释 | 记得过去的所有上下文 |
| 复利 | ❌ 每次从零开始 | ✅ 每次从上次结束的地方接续 |
| 累积 | 价值留在平台 | 价值留在你自己的 brain |
| 个性化 | 泛泛回答 | 基于你的真实人生脉络 |

---

## 三大核心架构

```
┌──────────────────────────────────────────────────────┐
│                    Thin Harness                       │
│            (OpenClaw / Hermes Agent)                  │
│                                                      │
│   接收讯息 → 判断意图 → 调度 skill → 派工            │
│   几千行 routing 逻辑，越薄越好                       │
│   不知道书/会议/创始人是什么，只负责路由                │
├──────────────────────────────────────────────────────┤
│                    Fat Skills                         │
│              100+ 独立技能模块                         │
│                                                      │
│   meeting-ingest │ book-mirror │ enrich │ eval       │
│   media-ingest   │ perplexity  │ pdf-gen │ ...       │
│                                                      │
│   每个 skill 是独立 markdown，可组合、可被修正          │
│   每个 skill 自己决定调用哪个模型                      │
├──────────────────────────────────────────────────────┤
│               Fat Code + Fat Data                     │
│                                                      │
│   Fat Data: 100,000 页结构化知识库 (GBrain)           │
│   Fat Code: 100+ cron job 24h 自动抓取数据            │
│   社群/Slack/email/OCR/screenshot → 全部喂进系统      │
└──────────────────────────────────────────────────────┘
```

**设计哲学：**
- Harness 薄 → skill 完全独立 → 可随时切换底层引擎
- Skill 厚 → 每个 skill 被充分测试、修正、组合
- Data 厚 → 自动化管道持续喂入新数据

---

## Book-mirror 案例：知识复利的具象化

**做什么：** 系统读一本书（如 Pema Chodron 的《When Things Fall Apart》，162 页），逐章生成两栏对照表：

| 左栏：作者观点 | 右栏：映射到使用者人生 |
|---------------|---------------------|
| 佛教如何处理苦难 | 具体引用 Garry Tan 的家族背景、工作脉络、会议记录 |

**规模：** 30,000 字，40 分钟跑完（人类治疗师约需 40 小时，且缺乏完整个人脉络）

**三次迭代：**

```
V1: 第一次输出
├── 结果: terrible（事实错误 3 处）
├── 错例: 说父母离婚、说在香港长大（实际出生在加拿大）
└── 态度: 公开承认，这是设计的一部分

V2: 加入 cross-modal evaluation
├── Opus 4.7 1M → 抓精准层事实错
├── GPT-5.5 → 抓遗漏的脉络
├── DeepSeek V4-Pro → 抓"读起来太通用"的句子
└── 修正烙进 skill → 后续所有 book-mirror 自动受惠

V3: 加入 deep retrieval
├── 每段右栏引用具体 brain page
├── 拉出 19 年前的对话记录
├── 拉出跟哥哥吃饭的讨论
└── 抽象哲学观念扎根在真实人生经验上
```

**复利效应：** 已对 20+ 本书做过 → brain 越来越厚 → 下一本书 mirror 的密度越来越高

---

## Skillify：为什么 skill 比 prompt 划算

**核心主张：** "I don't prompt my AI. The skills are the prompts."

```
Prompt vs Skill:

  Prompt (一次性):           Skill (持续演化):
  ┌──────────────┐          ┌──────────────┐
  │ 手写指令      │          │ 可重复模块    │
  │ 用完即丢      │    VS    │ 会被测试      │
  │ 不可组合      │          │ 会被组合      │
  │ 不积累经验    │          │ 持续被修正    │
  └──────────────┘          │ 改一处全局受益 │
                            └──────────────┘
```

**Skillify 工作流：**

```
发现自己在重复某工作流
    │
    ▼
手动做一次
    │
    ▼
说 "skillify this"
    │
    ▼
系统分析 pattern → 抽取可重复部分 → 写成 skill 文件
    │                                         │
    │                                         ▼
    │                                    注册到路由表
    │                                         │
    └────────────── 下次同类型需求 ──────── 自动触发
```

**Skill 组合的威力：**

```
book-mirror (高级 skill)
    │
    ├── brain-ops (查询知识库)
    ├── enrich (补充人物背景)
    ├── cross-modal-eval (多模型交叉评分)
    └── pdf-generation (排版输出)

改善任一子 skill → 所有使用它的工作流自动变好
```

---

## 100,000 页 Brain 与 Entity Propagation

**Brain 页面结构：**

```
┌────────────────────────────────────┐
│  Entity Page: Demis Hassabis        │
│                                    │
│  ┌────────────────────────────┐    │
│  │  Compiled Truth            │    │
│  │  (目前的最佳理解)           │    │
│  └────────────────────────────┘    │
│                                    │
│  ┌────────────────────────────┐    │
│  │  Timeline (append-only)     │    │
│  │  2026-03: YC 炉边谈话      │    │
│  │  2026-01: 传记出版          │    │
│  │  2025-11: Podcast 讨论      │    │
│  └────────────────────────────┘    │
│                                    │
│  ┌────────────────────────────┐    │
│  │  Raw Data Sidecar           │    │
│  │  (原始素材引用)              │    │
│  └────────────────────────────┘    │
└────────────────────────────────────┘
```

**Entity Propagation（实体传播）：**

```
一场会议结束后:
    │
    ▼
会议 transcript → 结构化摘要
    │
    ▼
扫描所有提到的实体 (人/公司/概念)
    │
    ▼
自动更新每个实体的 brain page
    │
    ├── Demis Hassabis page → 新增 AGI 立场
    ├── DeepMind page → 更新研究方向
    └── Garry Tan 自己的 page → 新增会议笔记
```

**会议准备的实际效果（Demis Hassabis 炉边谈话）：**
- Demis 的完整 brain page（累积数月）
- 公开的 AGI 立场（50% scaling + 50% innovation）
- 传记重点摘要
- Garry Tan 自己过去谈 AI 的引用 → 与 Demis 的交集/分歧
- 三个 demo 脚本（现场展示多跳推理）
- 一组对话 hook（从世界观重疊/分歧切入）

> "This wasn't just a better Google search." — Garry Tan

---

## 模型选择逻辑

```
模型只是引擎，skill 库/brain/管道才是车身

引擎可以换，车身才是价值累积的地方
```

| 模型 | 擅长 | 使用场景 |
|------|------|---------|
| Opus 4.7 1M | 精准、严密推理 | 事实核查、复杂推理 |
| GPT-5.5 | 广泛 recall | 穷举、补全遗漏 |
| DeepSeek V4-Pro | 第三视角、创意 | 通用视角、避免太 narrow |
| Groq Llama | 速度 | 快问快答、低延迟场景 |

**关键：** 每个 skill 自己决定调用哪个模型，harness 不关心。这是 skill 独立性的体现。

---

## 如何起步

**Garry Tan 的四步指南：**

```
Step 1: 选 Harness
├── OpenClaw、Hermes Agent、或自己写
├── 越薄越好，几千行 routing 够了
└── 可放家里备用电脑 + Tailscale，或 Render/Railway

Step 2: 开始 Brain
├── GBrain（Garry Tan 开源，39 个内置 skill）
├── 灵感来自 Karpathy 的 LLM Wiki gist
└── LongMemEval 上 97.6% 召回率

Step 3: 做一件你在乎的事
├── ❌ 别先规划 skill 架构
├── ✅ 先手动做一件真实的事
├── 写报告、研究一个人、分析投资组合...
└── 做完说 "skillify this"

Step 4: 持续用、持续看输出
├── 第一次输出很烂 = 设计的一部分，不是 bug
├── 发现问题 → cross-modal eval → fix 烙进 skill
└── 复利曲线在后段，不在头一两次
```

> "The first thing I built with this system was terrible. The hundredth was something I'd trust with my calendar, my inbox, my meeting prep, and my reading list."
> — Garry Tan

---

## 两种生存策略

```
策略 A: 依赖中心化平台              策略 B: 建立个人 AI 系统
┌──────────────────────┐          ┌──────────────────────┐
│ 每月付钱给科技巨头     │          │ 价值积累在自己的 brain │
│ 使用 ChatGPT/Claude   │          │ 知识复利在自己手里     │
│ 互动记录留在平台       │          │ 每次修正让系统更强     │
│ 用完即丢，不积累       │          │ skill 越来越厚        │
│                      │          │                      │
│ 风险: 平台涨价/关停   │          │ 风险: 维护成本/复杂性  │
│       数据不归你      │          │       但数据归你      │
└──────────────────────┘          └──────────────────────┘
```

> "The future belongs to individuals who build compounding AI systems, not to individuals who use corporate-owned centralized AI tools."
> — Garry Tan

---

## 参考资料

- [Garry Tan 原文 X Post](https://x.com/garrytan/status/2053127519872614419) — "Meta-Meta-Prompting: The Secret to Making AI Agents Work"
- [GBrain (GitHub)](https://github.com/garrytan/gbrain) — Garry Tan 的个人知识库开源实现
- [GStack (GitHub)](https://github.com/garrytan/gstack) — Garry Tan 的 Claude Code skill pack（28 种 skill）
- [深度解读文章 - heymaibao](https://heymaibao.com/garrytan-fat-skills-personal-ai/) — 本笔记主要参考来源
- [TechCrunch 报道](https://techcrunch.com/2026/03/17/why-garry-tans-claude-code-setup-has-gotten-so-much-love-and-hate/) — 社区对 gstack 的评价
- [Hacker News 讨论](https://news.ycombinator.com/item?id=47418576) — 技术社区讨论

## 相关笔记

- [[多层AI协同工作流 - 独立开发者效率架构]]
- [[Ollama MLX 后端 - Mac本地模型速度翻倍指南]]
