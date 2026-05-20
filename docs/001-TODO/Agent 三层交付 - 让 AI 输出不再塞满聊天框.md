---
title: Agent 三层交付 - 让 AI 输出不再塞满聊天框
aliases: [Agent Three Layer Delivery, Artifact Contract, AI 输出架构]
tags:
  - ai-agent
  - workflow
  - productivity
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=WDB1BN7yhsU"
  - "https://x.com/IntuitMachine/status/2053166572340932854"
author: YouTube Creator (解读 voxyz_ai 的 Agent 交付架构)
created: 2026-05-19
updated: 2026-05-19
description: |
  解读 voxyz_ai 提出的 Agent 三层交付架构：Telegram 五行通知 + Markdown 机器源 + HTML 人类报告。核心论点：Markdown 是草稿，HTML 才是给人看的。注意力成本远大于渲染成本。
level: beginner
stars: 4
---

# Agent 三层交付 - 让 AI 输出不再塞满聊天框

> 开发者 voxyz_ai 翻修了整套自动化系统后说了一句：「我试着让我的排程 Agent 只交付 HTML 格式，然后就彻底回不去了。」核心发现：Markdown 是草稿，HTML 才是给人类看的。真正的成本不是渲染，是你的注意力。

## 目录

- [[#注意力才是真正的成本]]
- [[#Markdown vs HTML：打破二元对立]]
- [[#三层交付架构]]
- [[#产出物契约（Artifact Contract）]]
- [[#落地行动指南]]

---

## 注意力才是真正的成本

### 常见误区

大多数人过度关注**渲染成本**（CPU、跑分、格式转换效率），却忽视了真正的终极大魔王——**注意力成本**。

```
每天的真实场景：
  AI → 2000 字纯文字报告 → 塞进 Telegram 对话框
  你 → 硬着头皮在里面捞重点 → 脑袋变浆糊

  ❌ 这才是最致命的消耗
```

### 关键数据

| 操作 | 耗时 | 意义 |
|------|------|------|
| 22.4KB Markdown → HTML 网页 | **51 毫秒** | 对一个跑几分钟的 AI 任务来说，连杂讯都称不上 |
| 人类阅读 2000 字纯文字报告 | **3-5 分钟** | 而且信息提取效率极低 |

> **结论**：为解救注意力而在运算上付出的代价，便宜到不可思议。

---

## Markdown vs HTML：打破二元对立

### 原始争论

很多人陷入「Markdown 好还是 HTML 好」的无聊争论。直到看到 html-anything 项目的这句话：

> **Markdown 是草稿，HTML 才是给人类看的。**

### 本质区分

```
              Markdown                    HTML
              ─────────                   ─────
用途         机器的草稿本                  人类阅读的成品
读者         AI Agent / 版本控制           人类决策者
格式         纯文本，结构简单               视觉化，排版精美
可操作性     Diff 比对、grep 搜索          浏览器直接查看
生命周期     系统的绝对真相来源              最终交付物
```

### Anthropic 工程师的验证

Claude Code 团队工程师 Thariq Shihipar 在公开演讲中也表达了类似观点：

> **HTML has become the superior format for communicating with AI agents, replacing Markdown for planning and specs.**

HTML 提供更丰富的表达力——交互元素、视觉原型、可滚动区块、更好的信息密度。

---

## 三层交付架构

### 架构总览

```
┌─────────────────────────────────────────────┐
│  第一层：Telegram / Slack                     │
│  角色：呼叫器                                │
│  内容：最多五行通知                           │
│  "结论：XXX | 进度：80% | 下一步：YYY"        │
│  人类只需 5 秒扫一眼                         │
├─────────────────────────────────────────────┤
│  第二层：Markdown 文件                        │
│  角色：系统的绝对真相来源                      │
│  读者：AI Agent / 版本控制 / Diff 工具         │
│  内容：结构化原始数据，机器可读可搜索            │
│  下一个 Agent 可直接读取                      │
├─────────────────────────────────────────────┤
│  第三层：HTML 报告                             │
│  角色：最终交付物                             │
│  读者：人类决策者                             │
│  内容：排版精美的可视化报告                    │
│  产出耗时：51 毫秒                            │
│  存放：专属文件夹，每天早上泡咖啡扫一遍          │
└─────────────────────────────────────────────┘
```

### 各层职责

| 层级 | 交付物 | 读者 | 核心价值 |
|------|--------|------|----------|
| 通知层 | ≤5 行文字 | 人类（快速扫描） | 0 认知负担 |
| 数据层 | Markdown 文件 | 机器（Agent/工具链） | 可搜索、可 Diff、可追溯 |
| 展示层 | HTML 网页 | 人类（深度阅读） | 高信息密度、低阅读疲劳 |

### 核心分工原则

```
机器懂机器该懂的 → Markdown
人类看人类觉得顺眼的 → HTML
通讯软件就只负责叫你来看 → 五行通知
```

---

## 产出物契约（Artifact Contract）

### 为什么需要契约

> **没有契约的 HTML 报告，只是一张比较漂亮的废纸。**

HTML 只是外壳，真正有价值的是结构化的内容。AI 的输出必须被强制塞进一组结构化字段，确保每次产出都有脉络、有行动指南。

### 七字段模板（可直接抄）

| 字段 | 说明 | 回答的问题 |
|------|------|-----------|
| **Sources** | AI 参考了哪些来源 | 信息可追溯性 |
| **Goal** | 今天的用户目标是什么 | 任务对齐 |
| **Status** | 目前的执行状态 | 进度透明 |
| **Risks** | 有没有踩到风险红线 | 安全预警 |
| **Usage** | 用了多少工具和预算 | 资源监控 |
| **Approval** | 需不需要人类点头同意 | 决策门控 |
| **Next Steps** | 接下来打算做什么 | 行动指南 |

### 契约的分层应用

```
通知层（Telegram 五行）：
  ✅ Status + Approval + Next Steps（3 个字段浓缩为 5 行）

数据层（Markdown）：
  ✅ 全部 7 个字段，完整结构化数据

展示层（HTML）：
  ✅ 全部 7 个字段，视觉化排版
  重点：Status 醒目展示、Risks 红色标记、Next Steps 置底
```

---

## 落地行动指南

### 黄金法则

> **任何执行超过 1-2 分钟的 Agent 任务，绝对不应该用 Telegram 或 Slack 的文字墙来交差。**

管它是每天跑的排程，还是花很久的代码重构——只要跑得久、信息量大，就不准塞进对话框。

### 三步实施

```
Step 1：审计现状
  ├── 检查 Telegram/Slack 是否已成为 AI 的垃圾场
  └── 如果是 → 抓出需要升级的通知

Step 2：建立契约（优先级最高）
  ├── 为你的 AI 定义 Artifact Contract
  ├── 不必照抄七字段，但必须定义类似的规则
  └── 这会瞬间把报告品质拉一个档次

Step 3：加 HTML 输出
  ├── 当内容够扎实后，花那 51 毫秒转 HTML
  ├── 保存到专属文件夹
  └── 通知层只发链接 + 三行摘要
```

### 判断决策树

```
你的 Agent 任务需要用三层交付吗？

  任务执行 < 1 分钟？
    ├── Yes → 纯 Telegram 通知即可
    └── No →
        输出信息 > 200 字？
          ├── Yes → 三层交付（必须）
          └── No →
              需要人类做决策？
                ├── Yes → 三层交付
                └── No → Telegram + Markdown 即可
```

### 最佳实践

- ✅ 通知层 ≤5 行：结论、进度、下一步
- ✅ Markdown 层做系统的 Single Source of Truth
- ✅ HTML 层在 Markdown 内容足够扎实后再加
- ✅ Artifact Contract 先行，HTML 外壳后加
- ❌ 不要直接把 Markdown 当 HTML 丢给人类看
- ❌ 不要在 Telegram 里塞 2000 字报告
- ❌ 不要花时间美化没有结构化内容的 HTML

---

## 参考资料

- [voxyz_ai (@Voxyz_ai) - X](https://x.com/Voxyz_ai)
- [Markdown is for AI, HTML is for humans - IntuitMachine](https://x.com/IntuitMachine/status/2053166572340932854)
- [HTML is the new Markdown - Lenny's Podcast / Anthropic](https://www.lennysnewsletter.com/p/how-i-ai-html-is-the-new-markdown)

## 相关笔记

- [[Claude Code 大项目最佳实践 - 生态配置详解]]
- [[Higsfield Agent 基础设施全解 - MCP CLI Skills Soul ID]]
