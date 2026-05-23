---
title: Markdown 取代程式碼 — Skill 就是新程式語言
aliases: [Thin Harness Fat Skills, Markdown 取代專案, Skill 工作流]
tags:
  - ai-agent
  - skill-system
  - markdown
  - claude-code
  - status/active
  - type/doc
source:
  - "https://youtube.com/watch?v=YIodRJNshVo"
  - "https://youtube.com/watch?v=n6nF6jhsal4"
author: 思思主播 (本片) / Ben Davis / Gary Tan
created: 2026-05-22
updated: 2026-05-22
description: |
  思思主播解读 Ben Davis 和 Gary Tan 的理念：Markdown 正在变成新的程式语言，Skill 就是用自然语言写的程式，Coding Agent 就是它的 Runtime。几个月的硬编码工作流被一个 Markdown 文件取代。
level: beginner
stars: 4
---

# Markdown 取代程式碼 — Skill 就是新程式語言

> 一群顶尖 AI 高手正在做一件反直觉的事：把写了几个月的项目代码全部删掉，换成 Markdown 文件。Skill 就是自然语言写的程式，Coding Agent 就是 Runtime。不会写 TypeScript 也能做出资深工程师的自动化工具。

## 目录

- [[#核心论点 — Markdown 成为新程式语言]]
- [[#Ben Davis 从嘲讽到改观]]
- [[#核心心智模型 — Skill 就是程式，Agent 就是 Runtime]]
- [[#案例一 — 几个月代码被一个 Markdown 取代]]
- [[#案例二 — Thin Harness, Fat Skills 五个定义]]
- [[#案例三 — Impeccable 浏览器点选改 UI]]
- [[#行动指南 — 今天就可以开始的三步]]

---

## 核心论点 — Markdown 成为新程式语言

```
传统范式：
  自动化工作流 = 学 TypeScript/Python → 写代码 → 调试 → 部署

新范式：
  自动化工作流 = 用白话文写 Markdown 步骤 → 丢给 AI Agent → 完成

  Markdown（自然语言）= 新的程式语言
  AI Agent（Coding Agent）= 执行环境（Runtime）
```

### 关键但书

**不是所有传统代码都没用**。需要区分两类系统：

| 类型 | 特征 | 适用技术 |
|------|------|---------|
| **确定性系统** | 同样输入必须得到相同输出 | 传统代码（数据库、认证、金流）|
| **黏合层** | 需要高度弹性、灵活编排 | AI Skill（Markdown）|

```
什么该用传统代码：
  ✅ 数据库事务
  ✅ 账号登入认证
  ✅ 金流支付处理

什么该用 AI Skill：
  ✅ 前端介面
  ✅ CRUD 读写
  ✅ API 同步
  ✅ 内容生成与整理
```

---

## Ben Davis 从嘲讽到改观

### 态度转变时间线

```
一个月前：
  Ben 拍视频吐槽 Gary Tan 的网站
  "50 万行代码堆出来的 AI 垃圾，载入超慢"
  ❌ 嘲讽态度

Gary 推出 GBrain 记忆系统后：
  Ben 亲自试用
  系统每天结束时吃进所有对话、email、笔记
  让 AI 像做梦一样整理洞察
  ✅ 惊呆，承认看走眼

现在：
  收起嘲讽，开始认真研究
  以 Markdown 为核心的工作流
  ✅ 完全转向 Skill 范式
```

---

## 核心心智模型 — Skill 就是程式，Agent 就是 Runtime

### 两个关键定义

```
┌─────────────────────────────────────────┐
│  Skill（技能）                          │
│  = 用自然语言写成的程式                    │
│  = Markdown 文件                         │
│  不再只是给人看的说明档，它本身就是程式      │
├─────────────────────────────────────────┤
│  Coding Agent（编码代理）                 │
│  = Skill 的执行环境                      │
│  = Runtime                               │
│  负责读懂 Markdown 并执行其中的步骤         │
└─────────────────────────────────────────┘
```

### Skill 的神奇之处

同一个 Markdown 文件，给不同的指令就能改变行为：

```
investigate skill：
  ├── 丟给吹哨者 email → 医疗调查员
  ├── 丟给政治献金记录 → 法务调查员
  └── 完全一样的 Markdown，靠白话文逻辑改行为
```

---

## 案例一 — 几个月代码被一个 Markdown 取代

### Ben Davis 的 BTCA 项目

| 维度 | 旧版（几个月心血） | 新版（一个 Markdown） |
|------|-------------------|---------------------|
| 代码量 | 大量 TypeScript | 1 个 Markdown 文件 |
| TUI 介面 | 手写 | 不需要 |
| CLI 指令介面 | 自建 | Agent 内建 |
| 模型认证 | 自写逻辑 | Agent 处理 |
| Repo 管理 | 自写 clone 管线 | Agent 处理 |
| 解决的问题 | 模型不懂最新套件用法 | 同样解决 |
| 效果 | — | 完全没打折 |

```
旧版 BTCA 项目                新版
━━━━━━━━━━━━━━━━━━━━━━━     ━━━━━━━━━━━━
  TUI 介面（手写）
  CLI 指令介面（自建）         btca-local.md
  模型认证（自写）      →     只要在 Agent 中
  Repo Clone 管线              输入启动指令
  几个月的 TypeScript           → 自动完成一切
━━━━━━━━━━━━━━━━━━━━━━━     ━━━━━━━━━━━━
```

---

## 案例二 — Thin Harness, Fat Skills 五个定义

Gary Tan 在 GStack 中提出的架构概念，由 Ben 在影片中整段引述：

### 五个核心名词翻译

| 英文术语 | 白话翻译 | 说明 |
|----------|---------|------|
| **Skill** | 教模型怎么做的 Markdown 剧本 | 就是自然语言写的程式 |
| **Harness** | 实际跑指令码的 AI 工具 | Claude Code、Hermes 等 |
| **Resolver** | 目录档 | 告诉 AI 该去哪找资料 |
| **Latent vs Deterministic** | 区分什么交给 AI、什么用死规则 | 弹性推理 vs 固定规则 |
| **Diorization** | 结构化记忆 | 让 AI 每天把零散笔记整理成精华 |

### 架构对比

```
传统架构：Fat Application, Thin Data
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  大量业务逻辑写在代码里
  数据只是配角

Skill 架构：Thin Harness, Fat Skills
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Harness（Agent 工具）保持薄
  Skill（Markdown）承载核心逻辑
  Resolver 负责数据路由
  结构化记忆提供长期知识
```

### 判断决策树

```
这个功能应该怎么实现？

  需要绝对确定性吗？
  ├── 是 → 用传统代码（Deterministic）
  │     ✅ 金流、认证、数据校验
  │
  └── 否 → 用 AI Skill（Latent）
        ✅ 内容生成、数据处理、UI 编排
        ✅ 用 Markdown 写步骤，Agent 执行
```

---

## 案例三 — Impeccable 浏览器点选改 UI

### Theo 的许愿

多年前，科技圈大神 Theo 就许愿：能不能在浏览器上点选某个按钮，随便讲句话，网页就自己改好。现在用 Impeccable 真的实现了。

### 工作原理

```
用户操作流程：

  1. 在浏览器点选一个元素（如图标）
  2. 打字说"把这个图标变成蓝色"
  3. Markdown Skill 指挥 Agent
  4. Agent 在后台修改原始码
  5. 页面刷新 → 图标变蓝了

  全程不切换到代码编辑器
```

### 技术实现

```
Impeccable 技术栈：

  核心逻辑 → Markdown 技能文件（大部分）
  运行入口 → live.mjs（很小的 JS 文件）

  Markdown 赋予 AI 的操控力：
  用自然语言指挥 Agent 修改任何网页元素
```

---

## 行动指南 — 今天就可以开始的三步

### 适合谁

- 内容创作者、自媒体经营者
- 每天都在用 AI 但觉得自己不会写程式的人
- 有重复性工作流想自动化的人

### 三步走

```
Step 1：找出最无聊的凡人琐事
  ├── 电子报排版
  ├── 整理几万字的采访逐字稿
  ├── 从长影片切出社群短影音
  └── Notion 里每天手动复制贴上的模板

Step 2：用白话文写成几十行 Markdown 步骤
  └── 把"你平时怎么做"写成"AI 应该怎么做"

Step 3：丢进 AI Agent 跑跑看
  └── 看它跑完的那一刻 → 既兴奋又觉得过去都在浪费时间
```

### 最佳实践

- ✅ 从最简单、最重复的工作流开始
- ✅ 用白话文写步骤，不要追求"程式风格"
- ✅ 先让 Agent 跑通，再逐步优化
- ❌ 不要一上来就挑战复杂系统
- ❌ 不要等"学完程式"再开始 — 这正是这个范式要打破的

---

## 参考资料

- [Ben Davis 原始影片：I Replaced the Project I Spent Months on With a Markdown File](https://youtu.be/n6nF6jhsal4)
- [思思主播深度文章](https://heymaibao.com/markdown-replaces-coding-project/)

## 相关笔记

- [[Agent Skills - 结构化AI编码工作流架构]]
- [[Garry Tan 的 Skills — YC 总裁的 AI Agent 知识系统架构]]
- [[Matt Pocock 的 Skills — AI 时代的软件工程方法论]]
