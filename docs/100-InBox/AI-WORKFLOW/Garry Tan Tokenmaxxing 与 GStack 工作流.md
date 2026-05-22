---
title: Garry Tan 的 AI 生产力革命 — Tokenmaxxing 与 GStack 工作流
aliases:
  - Tokenmaxxing
  - GStack AI Workflow
  - Garry Tan 400倍效率
  - 薄壳厚技能 Thin Harness Fat Skills
tags:
  - ai-agent
  - productivity
  - tokenmaxxing
  - gstack
  - claude-code
  - codex
  - ycombinator
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=fmR91KKSEuc"
  - "https://www.ycombinator.com/library/Pa-tokenmaxxing-how-top-builders-use-ai-to-do-the-work-of-400-engineers"
author: Garry Tan (YC 总裁) / 最佳拍档频道解读
created: 2026-05-16
updated: 2026-05-16
description: |
  YC 总裁 Garry Tan 在 Lightcone 播客中分享的 AI 生产力方法论：Tokenmaxxing 哲学、GStack 人机协作工作流、薄壳厚技能架构思想，以及用 $200 / 5 天重构价值 $400 万项目的实战案例。
level: intermediate
stars: 5
---

# Garry Tan 的 AI 生产力革命 — Tokenmaxxing 与 GStack 工作流

> YC 总裁 Garry Tan 时隔 13 年重返代码开发，借助 Claude Code + Codex 实现了 400 倍产出效率。这期笔记梳理他的 Tokenmaxxing 哲学、GStack 工作流系统、以及「薄壳厚技能」架构思想。适合希望提升 AI 辅助开发效率的工程师和创业者。

## 目录

- [[#从法拉利比喻说起]]
- [[#Posterous 三次重构的启示]]
- [[#Tokenmaxxing — 用 Token 买时间]]
- [[#GStack 工作流系统]]
- [[#薄壳厚技能（Thin Harness, Fat Skills）]]
- [[#工具互补策略]]
- [[#代码密度 vs 代码行数]]
- [[#关键金句与决策参考]]

---

## 从法拉利比喻说起

Garry Tan 的核心追问：**在 AI 时代，是你掌控 AI 工具，还是工具掌控了你？**

他的比喻：使用 OpenClaw 等 AI Agent 就像驾驶法拉利——极致性能让人血脉喷张，但极易故障。如果你只会踩油门而不会修车，它会在你最需要时抛锚。

```
┌──────────────────────────────────────────────────┐
│              AI 工具使用者的两种角色                │
├───────────────────────┬──────────────────────────┤
│     驾驶员（浅层使用）  │    机械师（深层掌控）      │
├───────────────────────┼──────────────────────────┤
│ 只会输入指令、等待输出  │ 理解提示词编写逻辑         │
│ 被工具局限性和错误拖累  │ 掌握 AI 模型底层原理       │
│ 出错时束手无策          │ 能打开引擎盖排查故障        │
└───────────────────────┴──────────────────────────┘
```

**核心结论**：掌控底层能力，才是使用 AI 的前提。AI 是生产力工具，但不是万能的。

---

## Posterous 三次重构的启示

Garry Tan 2008 年的 YC 第一个创业项目 Posterous（通过发邮件发博客），被 Twitter 以 $2000 万收购。他三次重构同一产品，完美展示了 AI 时代的效率跃迁：

| 版本 | 年份 | 团队 | 成本 | 耗时 | 最终形态 |
|------|------|------|------|------|---------|
| Posterous | 2008 | 6-7 人 | ~$400 万 | 1.5 年 | 全球前 200 网站 |
| Post Haven | ~2013 | 2 人 | ~$10 万 | 3 个月 | 简化版博客 |
| **Gary's List** | **2026** | **1 人** | **~$200** | **5 天** | **AI 调研 Agent** |

### Gary's List 不只是博客

第三次重构的产品已超越传统软件：

```
传统软件：开发工具 → 供人使用
Gary's List：AI 驱动 → 本身就是智能体
                │
                ├─ 递归爬取互联网信息
                ├─ 汇总所有推文和文章
                ├─ 交叉比对 20+ 信息源
                ├─ 支持观点 vs 反对观点
                └─ 生成资料完备的深度报告
                    ≈ 顶尖调查记者的全部工作
```

**成本**：调用 Opus 模型做一份完整调研仅需 $5-10，等价于一个人查阅几十篇文章 + 通读整本书并逐一批注。

---

## Tokenmaxxing — 用 Token 买时间

### 核心理念

Tokenmaxxing（Token 最大化）= 不计成本地让 AI 做全面覆盖，换取成果的极致完整、深度和精准。

这个理念也被称为 **"煮沸海洋（Boil the Ocean）"**：过去人类做研究因精力有限被迫简化，但 AI Agent 没有这个限制。

### 人机分工重新划分

```
┌─────────────────────────────────────────┐
│         机器负责（Token 驱动）            │
│  - 消耗算力、处理海量数据                  │
│  - 执行重复性工作、全面覆盖调研             │
│  - 交叉比对、生成报告                      │
├─────────────────────────────────────────┤
│         人类负责（判断驱动）               │
│  - 提出需求、赋予主观能动性                 │
│  - 把控核心方向和价值判断                   │
│  - 品味、领域理解、最终验证                 │
└─────────────────────────────────────────┘
```

### Token 投入的经济学类比

Garry Tan 用旧金山房租做类比：很多人觉得 SF 房租太贵不愿搬来，但不住 SF 的机会成本更高。Token 投入同理：

> 不要在算力和模型调用上省钱。每天花 $500 在 Token 上，只要能换回超高的生产力和价值，就是最划算的投资。

### 最佳实践

- ✅ 让 AI 全面覆盖所有信源，不要人为限制
- ✅ 宁可多花 Token 也不遗漏关键信息
- ✅ 在调研、编码、测试等所有环节 Token 拉满
- ❌ 不要为了"省钱"而牺牲 AI 输出的完整度
- ❌ 不要把 Token 成本和人力成本等价比较

---

## GStack 工作流系统

GStack 不是预先设计的产品，而是 Garry Tan 在开发中反复输入相同指令后，逐渐整理出来的固定流程。

### 核心组件

```
┌──────────────────────────────────────────────────┐
│                  GStack 工作流                    │
│                                                    │
│  ┌─────────────┐   ┌─────────────┐                │
│  │  CEO Plan    │   │ Plan/Eng    │                │
│  │  (战略层)     │   │ Review      │                │
│  │  十星级标准   │   │ (工程层)     │                │
│  └──────┬──────┘   └──────┬──────┘                │
│         │                 │                        │
│  ┌──────┴──────┐   ┌──────┴──────┐                │
│  │  Design     │   │  Dev Ex     │                │
│  │  Review     │   │  Review     │                │
│  │  (UI/UX)    │   │ (开发者体验) │                │
│  └──────┬──────┘   └──────┬──────┘                │
│         │                 │                        │
│         └────────┬────────┘                        │
│                  ▼                                  │
│         ┌──────────────┐                            │
│         │  End Review   │                            │
│         │  (最终审查)    │                            │
│         └──────┬───────┘                            │
│                │                                     │
│         ┌──────┴───────┐                            │
│         │   Codex 执行   │                            │
│         │ (主力编程Agent)│                            │
│         └──────┬───────┘                            │
│                │                                     │
│         ┌──────┴───────┐                            │
│         │    QA 测试     │                            │
│         │ (Playwright)  │                            │
│         └──────────────┘                            │
└──────────────────────────────────────────────────┘
```

### 各组件详解

| 组件 | 作用 | 关键技术 |
|------|------|---------|
| **Office Hours** | 答疑时间，拷问项目核心 | 为谁设计？解决什么问题？ |
| **CEO Plan** | 战略层规划，十星级标准 | 元提示词 + Brian Chesky 理念 |
| **Design Review** | UI/UX 审查 | 仅涉及 UI 时触发 |
| **Dev Ex Review** | 开发者体验审查 | 仅涉及 API/SDK 时触发 |
| **Plan/Eng Review** | 工程层规划 | **ASCII 架构图先行** |
| **End Review** | 最终审查 | 架构 + 代码质量 + 测试 |
| **Codex** | 主力编程 Agent | 解决硬核复杂问题 |
| **QA** | 自动化测试 | 封装 Playwright 浏览器测试 |

### Plan/Eng Review：最关键的发现

Garry Tan 发现让 Claude Code **在写代码前先用 ASCII 画出完整架构**，能大幅提升后续开发质量：

```
写代码前必须覆盖的 ASCII 图：
├─ 数据流图 (Data Flow)
├─ 输入输出图 (I/O)
├─ 用户动线图 (User Flow)
├─ 状态机 (State Machine)
├─ 依赖图 (Dependency Graph)
├─ 处理流水线 (Pipeline)
└─ 决策树 (Decision Tree)
```

**原理**：相当于给 AI 预载完整上下文，直接解决 Bug 和逻辑不全的问题。

### CEO Plan：十星级体验元提示词

灵感来自 Airbnb 联合创始人 Brian Chesky 的理念：普通人评价产品只到五星，而 Chesky 追问六星、七星、十星的体验是什么。

应用到 AI 提示词中：

> 如何用 2 倍的力气，交付 10 倍的价值？

这个指令让 AI 从模型的隐空间（Latent Space）中提取更优质方案，把项目目标具象化。

### 日常产出数据

- 48 小时内提交 13 个 PR
- 60 天交付 60 万行代码（其中 35% 是测试）
- 通过 Conductor 实例管理任务队列
- AI 同时扮演 CEO、设计师、DX 专员、测试工程师

---

## 薄壳厚技能（Thin Harness, Fat Skills）

Garry Tan 与 YC 合伙人 Pete Kuhns 共同提出的架构思想，这是 Agent 工程的关键。

### 核心定义

| 概念 | 定义 | 应该放哪 |
|------|------|---------|
| **Harness（壳）** | 核心循环：接收输入 → 交给 LLM → 执行工具调用 | 确定性代码 |
| **Skills（技能）** | Markdown 编写的意图、指令、执行流程 | LLM / 隐空间 |

### 为什么大多数 AI 应用失败

```
❌ 错误做法：
  把本该用自然语言描述的逻辑 → 硬塞进脆弱的代码里
  代码是确定性的 0 和 1，不理解特殊情况和泛化场景

✅ 正确做法：
  不确定性意图 → Markdown（Skill）
  确定性操作（如调用 Twilio 打 20 个电话）→ 传统代码
```

### 技能 = 婚礼策划师的执行清单

Garry Tan 的比喻：写 Skill 就像婚礼策划师给助手写执行清单——用大白话传递核心思路，不需要写代码。

> Markdown 本质上就是另一种形式的代码，只是编译方式不同。

### 工程师核心能力转移

```
过去：编写代码
现在：编写 Skill
      │
      ├─ 划分 LLM 处理范围 vs 代码处理范围
      ├─ 保证 80%-90% 测试覆盖率
      └─ 做出稳定、高效的 AI 应用
```

### 测试覆盖率

| 覆盖率 | 评价 |
|--------|------|
| 100% | 过于冗余，不值得 |
| **80%-90%** | **最佳实践** |
| < 80% | Vibe Coding 陷阱，真实用户一碰就崩 |

---

## 工具互补策略

Garry Tan 根据自身 ADHD 工作模式，形成了 Claude Code + Codex 的互补逻辑：

| 维度 | Claude Code | Codex |
|------|------------|-------|
| 定位 | 统筹规划的 CEO | 解决硬核问题的 CTO |
| 擅长 | 规划、设计、审查 | 复杂 Bug 排查、底层实现 |
| 弱点 | 偶尔出现幻觉 | 需要明确指令 |
| 使用比例 | 50%-60% | 40%-50% |
| 角色 | "能跟你聊天的管理者" | "智商 200 的沉默极客" |

### 协作方式

- **Slash Codex**：在 Claude Code 环境中调用 Codex 处理难题
- **Slash Claude**：在 Codex 环境中调用 Claude 做规划
- **Conductor**：管理任务队列，48 小时提交 13 个 PR

### 个人项目 GBrain

Garry Tan 还在研发 gbrain 项目，解决 OpenClaw 暴力检索、浪费上下文窗口的问题：

```
gbrain 技术栈：
├─ 向量嵌入 (Vector Embeddings)
├─ Hybrid RRF (Reciprocal Rank Fusion)
├─ Chunking 策略
└─ PostgreSQL + pgvector → 完整 RAG 系统
```

---

## 代码密度 vs 代码行数

Garry Tan 承认早期用"代码行数"引发争议，是他的表述问题。核心逻辑：

| 指标 | 意义 |
|------|------|
| 物理代码行数 | 毫无意义，AI 不会为了刷行数注水 |
| **逻辑代码密度** | **衡量生产力的核心** |

### 标准化数据

| 时期 | 逻辑代码密度（每天） |
|------|---------------------|
| 1990-2000 专业工程师（文献基准） | 30-50 行 |
| Garry Tan 2013（兼职写代码） | 14 行 |
| **Garry Tan 2026（AI 辅助）** | **14 行 × 400 = 5600 行等效** |

AI 输出的都是高逻辑密度的有效代码，这是 400 倍效率的真实依据。

---

## 关键金句与决策参考

### 判断决策树

```
你在用 AI 工具吗？
├─ YES → 你是哪种使用者？
│         ├─ 只会输入指令 → 需要学习底层原理
│         └─ 理解原理但效率低 → 检查是否 Tokenmaxxing
│
├─ 不确定要不要投入 Token？
│   └─ 问自己：省 $10 Token 的机会成本是多少？
│
└─ 还没开始用 AI Agent？
    └─ 现在是 1976 年 Homebrew Computer Club 时刻
        2-3 小时 + $500-1000 Token 成本
        就能搭建专属 AI 系统
```

### 核心金句

> - "生活在未来，构建缺失的东西" — YC 核心格言
> - "用 Token 买回时间" — Tokenmaxxing 的终极意义
> - "如果有人造出完全不需要人类的软件开发系统，我会无比震惊"
> - "AI 时代的 Homebrew Computer Club 已经到来"
> - "每个人都能调用几百万年的机器时间，为自己的目标服务"

### GStack 效率数据一览

| 任务类型 | 效率提升（vs 传统） |
|---------|-------------------|
| 样板代码生成 | ~100x |
| 测试编写 | ~50x |
| 架构设计 | ~5-10x（复杂任务提升有限） |
| 调研报告 | 一个人 = 一整个调查团队 |

---

## 参考资料

- [Tokenmaxxing - YC Library (原始播客)](https://www.ycombinator.com/library/Pa-tokenmaxxing-how-top-builders-use-ai-to-do-the-work-of-400-engineers)
- [Lightcone Podcast 原始视频](https://www.youtube.com/watch?v=57lDpTwiW6g)
- [Garry Tan 400 倍效率 - 虎嗅](https://www.huxiu.com/article/4858620.html)
- [GStack 系统解析 - 知乎](https://zhuanlan.zhihu.com/p/2018645722786998091)
- [YC CEO 别再省 Token 了 - 36氪](https://m.36kr.com/p/3805436091457285)
- [Thin Harness Fat Skills - GLN](https://gln75.com/en/blog/garry-tan-thin-harness-fat-skills)

## 相关笔记

- [[AI Agent 框架对比]]
- [[MCP 协议详解]]
- [[Hermes Agent + Claude + Codex AI Agent Stack]]
