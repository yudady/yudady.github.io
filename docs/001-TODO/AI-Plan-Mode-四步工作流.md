---
title: AI Plan Mode 的正确用法 — 四步对齐工作流
aliases:
  - Plan Mode 之前那段对话
  - AI 规划工作流
tags:
  - ai-agent
  - workflow
  - prompt-engineering
  - status/active
  - type/learning-note
source:
  - "https://www.youtube.com/watch?v=5J876d8q5jw"
  - "https://heymaibao.com/planning-before-plan-mode/"
  - "https://www.youtube.com/watch?v=MyRs5hdE7vo"
author:
  - 思思主播（摘要视频）
  - heymaibao.com（深度文章）
  - Ras Mic（原始视频）
created: 2026-06-07
updated: 2026-06-07
description: 多数人用 Plan Mode 的方式完全错了。真正的规划发生在按下 Plan Mode 之前那段与 AI 的对话中。
level: beginner
stars: 3
---

# AI Plan Mode 的正确用法 — 四步对齐工作流

> 一行需求 → Plan Mode → 不看计划直接 Build，是大多数人翻车的原因。正确的做法是先跟 AI 多轮对话，把 context 对齐，再让 Plan Mode 生成可携带的 Markdown 计划档。

---

## 问题：Plan Mode 被当成了读心术

典型错误操作：

```
用户：帮我做一个能看所有数据分析的 Dashboard
     → 开 Plan Mode → 不看计划 → 直接 Build
```

**结果**：产出与预期差距巨大。

**根因**：模型手上只有一句话的 context，剩下的全靠猜。假设越多，产出离真实需求越远。

核心观点：
> **如果你自己都还没想清楚，Plan Mode 再强也救不了你。**

AI 的作用是**放大你已厘清的想法**，而不是凭空替你做出你脑袋中还没成型的工程决策。

### 常见错误模式对比

| 模式 | 操作 | 结果 |
|------|------|------|
| ❌ 无脑 Plan | 一行需求 → Plan → Build | 模型盲猜，产出偏差大 |
| ❌ 不用 Plan | 直接丢需求 Build | 无全局规划，架构混乱 |
| ✅ 四步对齐 | 对话 → 确认 → Plan → Build | context 充分，一次到位 |

---

## 四步工作流

来源：Ras Mic（Pluto 企业 Agent 平台创作者）

```
  ┌─────────────────────────────────────────────────┐
  │                四步对齐工作流                      │
  ├─────────────────────────────────────────────────┤
  │                                                 │
  │  ① 强迫对话（不开 Plan Mode）                      │
  │     丟想法、讨论 trade-off、                       │
  │     问"如果这样做会怎样"                            │
  │          │                                      │
  │          ▼                                      │
  │  ② 确认 AI 真的懂了                               │
  │     验证它理解了你的蓝图和决策                      │
  │          │                                      │
  │          ▼                                      │
  │  ③ Plan Mode → 生成 Markdown 计划档               │
  │     存入固定目录（如 .hermes/plans/）              │
  │          │                                      │
  │          ▼                                      │
  │  ④ Build                                        │
  │     用计划档驱动实际编码                            │
  │                                                 │
  └─────────────────────────────────────────────────┘
```

### 第一步：强迫对话（核心步骤）

这是整个工作流中**最花力气也最值钱**的一步。

**做什么**：
- 把想法不断丢给 AI，来回讨论技术取舍
- 问"如果这样做会怎样、那样做又怎样"
- 把 trade-off 一个个摊开讨论
- **阻止 AI 提前 Build** — 如果模型聊到一半想动手，直接叫停

**唯一目标**：确保 AI 百分之百理解你脑袋里的蓝图。

### 第二步：确认理解

验证 AI 真正理解了你的决策和约束条件，而非表面附和。

### 第三步：生成计划档（关键交付物）

进入 Plan Mode 后，**不要直接 Build**，而是生成一份独立的 Markdown 计划档。

计划档的价值：

| 特性 | 说明 |
|------|------|
| 跨分页记忆 | 多 tab 并行工作时，计划档就是 Agent 的记忆 |
| 跨时间存档 | 搁置两周后回来，计划档告诉你上次做到哪、为什么这样决定 |
| 跨工具携带 | 可丢给 Cursor、Claude Code、Codex 任何工具接手 |
| 规格书本质 | 本质上就是一份个人规格书（Spec-driven Development） |

### 第四步：Build

把 context 完全对齐的计划档丢给 Agent 执行。

---

## 实战案例：Expo 还是原生 Swift UI？

Ras Mic 在规划 Pluto 手机 Companion App 时演示了这个流程：

**决策问题**：跨平台 App 选 Expo 还是原生 Swift UI？

| 方案 | 优势 | 劣势 |
|------|------|------|
| Expo | 一套代码搞定 iOS + Android | 需要花力气让 UI 不像网页壳 |
| Swift UI | Apple 黄金标准质感 | 两个平台各写一套，工作量翻倍 |

**通过对话逼出的真实决策**：
- 没有靠技术品味拍脑袋
- 回头问产品问题：**"我的企业客户会不会用 Android？"**
- 答案：**会** → 选 Expo

**Agent 主动发现的性能坑**：
- 跨平台聊天 App 的消息流渲染是唯一真正的性能瓶颈
- 每条新消息触发整个列表重渲染 → 严重卡顿
- ✅ 解法：**列表虚拟化（List Virtualization）**，只渲染可视区域的消息
- 必须从第一行代码就设计进去，不能事后硬补

> 如果一键 Build 等到架构写死、画面跑出来才测试发现滑起来卡，拆掉重练的代价极大。

---

## "规划会拖慢出货吗？" — 不会

这个直觉是**错的**。

```
  ┌──────────────────────────────────┐
  │  模型估算：人类工程师 → 2 周       │
  │  Agent 实际执行 → 2-3 小时        │
  │                                  │
  │  先慢后快的悖论：                   │
  │  投资几分钟对话 → 换取几小时顺畅    │
  └──────────────────────────────────┘
```

**本质**：这等于工程界的 **Spec-driven Development（规格先行）**，只是被做成了一套个人 Vibe Coder 就能跑的工作流。

---

## 最佳实践清单

✅ **应该做的**：
- 先花 5-10 分钟跟 AI 对话，把需求和约束聊透
- 让 AI 主动提出你可能没想到的问题（如性能、兼容性）
- 生成独立的 Markdown 计划档，存入项目目录
- 计划档包含技术选型理由和 trade-off 分析

❌ **不要做的**：
- 一行需求就按 Plan Mode
- Plan 跑出来看都不看直接 Build
- 让 AI 在对话阶段就开始写代码
- 把计划档当成"过渡期垃圾"忽略

### 决策树：什么时候需要对话？

```
  需求复杂度？
     │
     ├─ 低（单文件修改、明确 bug fix）→ 可以直接 Build
     │
     └─ 高（新功能、架构决策、多模块）→ 必须走四步工作流
           │
           ├─ 有技术选型？ → 对话讨论 trade-off
           ├─ 有性能要求？ → 让 AI 预判瓶颈
           └─ 有多方案？   → 逐个对比后再定案
```

---

## 参考资料与原始来源

- [原始视频：I don't use plan mode, I do this instead - Ras Mic](https://www.youtube.com/watch?v=MyRs5hdE7vo)
- [深度文章：plan mode 之前那段对话到底要聊什么 - heymaibao.com](https://heymaibao.com/planning-before-plan-mode/)
- [摘要视频：思思主播](https://www.youtube.com/watch?v=5J876d8q5jw)

## 相关笔记

- [[AI Agent 工作流]]
- [[Spec-driven Development]]
- [[Prompt Engineering 最佳实践]]
