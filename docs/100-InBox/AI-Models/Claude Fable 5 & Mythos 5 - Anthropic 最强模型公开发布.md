---
title: Claude Fable 5 & Mythos 5 - Anthropic 最强模型公开发布
aliases: [Claude Fable 5, Claude Mythos 5, Claude 5]
tags:
  - claude
  - anthropic
  - llm
  - frontier-model
  - status/active
  - type/doc
source:
  - "https://youtu.be/p6oon7IOXts"
  - "https://www.anthropic.com/news/claude-fable-5-mythos-5"
  - "https://simonwillison.net/2026/Jun/9/claude-fable-5/"
  - "https://techcrunch.com/2026/06/09/anthropics-claude-fable-5-is-a-version-of-mythos-the-public-can-access-today/"
author: Anthropic
created: 2026-06-11
updated: 2026-06-11
description: Anthropic 发布 Mythos 级模型 Fable 5（公开发布）和 Mythos 5（受限访问），开启 Claude 5 时代
level: intermediate
stars: 5
note: 无字幕，基于视频元数据 + Anthropic 官方公告 + Simon Willison + TechCrunch 综合整理。视频为 AI 生成内容，信息经外部源交叉验证。
---

# Claude Fable 5 & Mythos 5 — Anthropic 最强模型公开发布

> 2026-06-09，Anthropic 发布 **Claude Fable 5**（公众可用的 Mythos 级模型）和 **Claude Mythos 5**（仅限受信任机构）。Mythos-class 是超越 Opus 的新能力层级，Fable 5 带有安全护栏，Mythos 5 移除部分限制专供网络安全/生物研究。

---

## 目录

- [模型定位与命名体系](#模型定位与命名体系)
- [核心规格与定价](#核心规格与定价)
- [可用性与时间线](#可用性与时间线)
- [能力亮点](#能力亮点)
- [安全护栏与数据留存](#安全护栏与数据留存)
- [行业反应与第三方评价](#行业反应与第三方评价)
- [战略背景](#战略背景)
- [参考资料](#参考资料)

---

## 模型定位与命名体系

Anthropic 引入新的模型层级 **Mythos-class**，位于 Opus 之上：

```
            Claude 5 能力层级
    ┌──────────────────────────┐
    │  Mythos 5（受限）          │  ← 最高能力，无安全分类器
    │  仅限网络安全/基础设施/    │     仅 Glasswing 合作伙伴
    │  生物研究受信任机构        │
    ├──────────────────────────┤
    │  Fable 5（公开发布）        │  ← Mythos 同源 + 安全护栏
    │  API + 订阅计划可用        │     一般公众可使用
    ├──────────────────────────┤
    │  Opus 4.8（上一代旗舰）     │
    ├──────────────────────────┤
    │  Sonnet / Haiku           │
    └──────────────────────────┘
```

**命名由来**：
- **Mythos**（希腊语 μῦθος）：神话、叙事
- **Fable**（拉丁语 fabula）：寓言、传说
- 两者同源，区别在于安全措施。Fable = Mythos + guardrails

> "Fable 5's capabilities exceed those of any model we've ever made generally available. It is state-of-the-art on nearly all tested benchmarks." — Anthropic

---

## 核心规格与定价

| 参数 | Fable 5 | Opus 4.8 |
|------|---------|----------|
| **Context Window** | 1M tokens | ~200K tokens |
| **Max Output** | 128K tokens | 32K tokens |
| **Knowledge Cutoff** | 2026-01 | 2025-04 |
| **Input Price** | $10/M tokens | ~$5/M tokens |
| **Output Price** | $50/M tokens | ~$25/M tokens |
| **长上下文加价** | 无额外费用 | — |

**定价策略**：价格是 Opus 4.8 的 2 倍，但不到 Mythos Preview 的一半。高价本身是自然使用限制。

---

## 可用性与时间线

| 阶段 | 渠道 | 条件 |
|------|------|------|
| **4 月 2026** | Mythos Preview | 极少数合作伙伴（Project Glasswing） |
| **6 月 2 日** | Mythos 扩展 | 数百家机构，15 个国家，聚焦关键基础设施 |
| **6 月 9 日** | Fable 5 API + 企业 | 完全可用 |
| **6 月 9-22 日** | Pro/Max/Team/企业（席位） | 免费包含，无额外费用 |
| **6 月 23 日起** | 订阅计划 | 需要使用额度（usage credits） |
| **长期** | 订阅计划 | 目标恢复为标准功能（取决于容量） |

**API 调用**：model ID 为 `claude-fable-5`。

---

## 能力亮点

### 软件工程

| 场景 | 表现 |
|------|------|
| **Stripe 大规模迁移** | 50M 行 Ruby 代码库，一天完成全栈迁移（人工需 2+ 个月） |
| **FrontierCode 评测** | Cognition 的 FrontierCode 评测中得分最高，即使 medium effort |
| **Token 效率** | 最 token 高效的 Claude 模型 |

### 知识工作

| 场景 | 表现 |
|------|------|
| **Hebbia 金融基准** | 所有模型中最高分（高级推理、文档解读、图表分析） |
| **IMC 交易分析** | 几乎全科目通过（事实查找、概念推理、根因分析、期望值分析） |

### 视觉能力

- 全新 SOTA 视觉模型
- 从复杂科学图表中提取精确数字
- **仅从截图重建 Web 应用源代码**
- 用最小化视觉驱动工具通关宝可梦 FireRed（之前 Claude 即使复杂辅助工具也做不到）

### 记忆与长上下文

- 在百万 token 级别的长期任务中保持专注
- 在 *Slay the Spire* 测试中，基于文件的持久记忆使性能提升 **3 倍**（vs Opus 4.8）；到达最终关卡的频率 **3 倍**

### 其他演示

- 从物理第一性原理推导行星轨道运动，预测日食
- 自主玩 Factorio 并建造工厂
- 在浏览器 CAD 编辑器中设计完整 3D 打印模型（编辑器本身也是 Fable 5 构建的）
- 生成流体仿真同步古典/EDM 混音（音乐也是通过代码生成）

---

## 安全护栏与数据留存

### Fable 5 的受限领域

| 领域 | 处理方式 |
|------|----------|
| 网络安全 | 阻断 + 降级到 Opus 4.8 |
| 生物学 | 阻断 + 降级 |
| 化学 | 阻断 + 降级 |
| 模型蒸馏 | 阻断 + 降级 |

**降级率**：极低。早期数据显示 **≥95% 的 Fable 会话完全由 Fable 自身回答**。

### Refusal 机制（新 API 特性）

Fable 的 guardrails 触发频率足够高，Anthropic 为此新增了 [refusals and fallback](https://platform.claude.com/docs/en/build-with-claude/refusals-and-fallback) 机制：

- 支持自动降级到另一模型（如 Opus 4.8）当请求被拒绝时
- 开发者可配置 fallback 策略

### 安全测试

- 内部 bug bounty：**1000+ 小时测试，未发现通用越狱**
- 外部红队测试：**同样未能发现通用越狱**
- Anthropic 承认新颖攻击仍有可能

### 数据留存（行业首创？）

| 政策 | 细节 |
|------|------|
| **留存期** | 30 天 |
| **适用范围** | Fable 5 + Mythos 5 的**所有流量** |
| **覆盖先前协议** | 即使企业此前有零留存协议也适用 |
| **训练用途** | **不会用于训练** |
| **目的** | 防御复杂攻击、减少误报 |

---

## 行业反应与第三方评价

| 公司 | 评价 |
|------|------|
| **Hex**（分析） | 首个核心分析基准达 90% 的模型 |
| **Base44**（vibe-coding） | 更擅长一次成型完整应用 |
| **Genspark** | UI 设计和游戏编码显著优于其他模型 |
| **Rakuten** | 高 effort 时会反思验证自身工作 |
| **Harvey**（法律 AI） | 企业客户可选择性 early access |

### Simon Willison 的实际测试

**"大模型的味道"**：Fable 感觉"很大"——不仅速度和成本，更在知识深度上。

**测试 1：micropython-wasm 升级**
- 任务：将 MicroPython WASM 库升级为完整 Python WASM
- 结果：产出可用的 13.9MB wheel 包，展示了跨语言工具链理解能力

**测试 2：Datasette Agent + LLM 0.32a3**
- 任务：为 Datasette Agent 添加人工审批机制
- 结果：不仅解决主问题，还顺带实现了 4 个底座功能（tool_call_id、PauseChain 异常、链恢复机制等）
- Willison 评价：*"API 设计、测试、代码和文档的质量让我印象深刻。花了几个小时但感觉是几天的工作量。"*

---

## 战略背景

```
    Anthropic 战略时间线
    ┌─────────────────────────────────┐
    │ 4/2026  Mythos Preview 发布       │
    │         Project Glasswing        │
    │ 6/2     Mythos 扩展到 15 国       │
    │ 6/8     OpenAI 秘密递交 IPO       │
    │ 6/9     Fable 5 + Mythos 5 发布  │ ← 本期重点
    │         Anthropic IPO 准备中     │
    │         RSI 警告：递归自我改进    │
    └─────────────────────────────────┘
```

- **IPO 竞赛**：Anthropic 与 OpenAI（6/8 秘密递交）、SpaceX 同期筹备 IPO
- **RSI 警告**：Anthropic 近期呼吁全球 AI 实验室建立"协调刹车"，警告系统可能接近递归自我改进（RSI）
- **能力 vs 安全的两难**：Mythos 级能力太强不敢全面放开，Fable 是安全妥协产物

---

## 参考资料

- [Anthropic 官方公告](https://www.anthropic.com/news/claude-fable-5-mythos-5)
- [Simon Willison: Initial impressions of Claude Fable 5](https://simonwillison.net/2026/Jun/9/claude-fable-5/)
- [TechCrunch: Anthropic's Claude Fable 5 is a version of Mythos the public can access today](https://techcrunch.com/2026/06/09/anthropics-claude-fable-5-is-a-version-of-mythos-the-public-can-access-today/)
- [EnterpriseDNA: Claude Fable 5 Launch Analysis](https://enterprisedna.co/resources/news/anthropic-claude-fable-5-mythos-5-public-launch-2026)

## 相关笔记

- [[LLM 前沿模型对比]]
- [[Anthropic 安全框架]]
