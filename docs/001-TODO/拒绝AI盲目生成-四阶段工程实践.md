---
title: 拒绝 AI 盲目生成：一套四阶段工程实践
aliases: [Why Software Factories Fail, HumanLayer 四阶段, Dex Horthy 软件工厂]
tags:
  - ai-coding
  - software-factory
  - code-review
  - humanlayer
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=YYgrTVzNrZI"
  - "https://github.com/humanlayer/advanced-context-engineering-for-coding-agents/blob/main/wsff.md"
  - "https://www.humanlayer.dev/blog/advanced-context-engineering"
  - "https://www.faros.ai/blog/ai-software-engineering"
author: 为什么叫QQ（视频）；Dex Horthy / HumanLayer（原始来源）
created: 2026-07-30
updated: 2026-07-30
description: 基于 Dex Horthy「Why Software Factories Fail」深度文章，结合 Faros AI 遥测数据，剖析 AI 编码代理导致代码质量下降的系统性原因，给出四阶段工程实践框架
level: intermediate
stars: 4
note: 视频无字幕（频道"为什么叫QQ"系统性无字幕），基于用户提供的 Content Insights + Dex Horthy 原始文章（wsff.md）+ Faros AI 报告综合整理
---

# 拒绝 AI 盲目生成：一套四阶段工程实践

> SWE-bench 分数飙升 ≠ 工程效益提升。高 AI 采用率带来的不是效率飞跃，而是更大的 PR、更长的审查、更多的事故。Dex Horthy 从 HumanLayer 团队的亲身失败出发，提出四阶段工程实践框架，把代码审查放回环路。

---

## 目录

1. [AI 导入的工程困境](#1-ai-导入的工程困境)
2. [软件工厂为什么会失败](#2-软件工厂为什么会失败)
3. [四阶段工程实践框架](#3-四阶段工程实践框架)
4. [关键反思与行动建议](#4-关键反思与行动建议)

---

## 1. AI 导入的工程困境

### 1.1 Benchmark 迷信：分数涨了，代码没变好

业界普遍用 SWE-bench 等 benchmark 分数来判断 AI 编码能力。管理者看到分数飙升，就以为"代码生产成本骤降"，把任务全权交给 AI Agent。

**问题在于**：SWE-bench 的评分机制只看两件事——

| 评分维度 | 含义 | 能检测到 |
|----------|------|----------|
| FAIL_TO_PASS | 修好了被要求的 bug | 功能正确性 |
| PASS_TO_PASS | 没搞坏其他东西 | 回归安全性 |
| （缺失） | 代码可维护性 | **完全没有** |

SWE-bench 的每个 task 约 15 分钟工作量，来自 Redis、jq、Django 等开源项目。评分方式是"一次性 binary reward"：测试通过 = 1，否则 = 0。

```
benchmark 评分流程：
  bug report + 代码库（修复前的 commit）
        ↓
  agent 写 patch
        ↓
  丢弃 agent 对测试文件的修改（防止作弊）
        ↓
  叠加 benchmark 自己的 test patch
        ↓
  跑全部测试 → 通过 = 1 / 失败 = 0
```

**关键缺陷**：how the model got to a correct answer doesn't matter。模型可以在正确的答案中夹带 try-catch 包裹一切、lazy type cast 破坏类型系统等"代码腐化"操作，只要测试通过就拿满分。

> "there is no penalty for eroding codebase maintainability"
> —— Dex Horthy

### 1.2 Faros AI 遥测数据：高采用率的警讯

Faros AI 基于约 10,000 名开发者、1,255 个团队的遥测数据（2025 年报告）：

| 指标 | 变化幅度 | 含义 |
|------|----------|------|
| PR 数量 | +98% per developer | 产出了更多 PR |
| 任务完成 | +21% | 个人吞吐量上升 |
| PR 审查时间 | +91% | **审查成为瓶颈** |
| PR 平均体积 | +154% | PR 越来越大 |
| 每开发者 bug 数 | +9% | 质量下降 |
| 公司层面 DORA 指标 | **无显著相关性** | 个人收益未传导到组织 |

Dex Horthy 在 wsff.md 中引用的 Faros 数据更触目惊心（可能为更新版本）：

```
代码质量（合并前）：
  +25% 更多审查评论
  +22.7% 更长的评论
  +31.3% 的 PR 完全跳过审查

生产质量：
  +242.7% 每 PR 事故数
  +57.9% 月度事故数
  +54% 每开发者 bug 数
```

**核心悖论**：开发者觉得自己更快了，但公司层面看不到业务收益——这就是 "AI productivity paradox"。

### 1.3 为什么 benchmark 提升没能转化为工程质量

```
┌─────────────────────────────────────────────┐
│  Benchmark 测的是「功能正确性」               │
│  RL 训练优化的也是「测试通过率」              │
│  ↓                                           │
│  模型被训练成「让测试通过」                   │
│  而非「写出可维护的代码」                     │
│  ↓                                           │
│  可维护性的成本函数以「周/月/年」计           │
│  无法在 RL 的快速反馈循环中 reward            │
└─────────────────────────────────────────────┘
```

Dex 的判断：这不是 skill issue（用户能力问题），而是 **model-training issue**（模型训练的结构性缺陷）。再多的 harness engineering / prompt engineering / token 堆叠，都无法弥补训练阶段对可维护性的忽视。

---

## 2. 软件工厂为什么会失败

### 2.1 软件工厂的演进

```
2022 传统软件工厂：
  人决定做什么 → Tracker(Jira/Linear) → 人写代码 → PR审查 → 发布 → 监控 → 反馈
  （build 和 review 都需要数小时到数天）

       ↓ 引入 AI Agent

Agent 软件工厂：
  build 从「小时/天」缩短到「分钟/小时」
  review 仍然是「小时/天」→ 审查成为瓶颈

       ↓ 加速审查

Agent 审查 + Agent 回归测试：
  review 变快了，但仍是瓶颈
  继续加 loops：事故自动修复、用户反馈自动入场

       ↓ 追求极致效率

Lights-off 软件工厂（关灯工厂）：
  "没有人读代码，没有人写代码"
  把 human review 那一步直接删掉
  投资全部转移到：测试、沙盒、自动审查、监控、灰度
```

StrongDM 实现了 lights-off factory，Ramp/Stripe/WorkOS/Brex 等公司也声称用 Agent 工厂产出 75% 的代码。OpenAI 的内部软件工厂叫 Symphony。

### 2.2 HumanLayer 的亲历教训

Dex Horthy 在 2025 年 7 月带着 HumanLayer 团队全面关灯（full lights-off）：

```
时间线：
  2025.07  全面 lights-off，background agents 跑所有中小任务
       ↓
  遇到 agent 无法解决的棘手问题
  团队已经 3 个月没读过自己的代码库
       ↓
  第一次：硬啃两周 claude spaghetti，说服自己"速度值得"
       ↓
  第三次（11月）：决定从零重写
  联合创始人花了两整周在 VS Code（不是 Cursor）里手动重写
```

**结论**：models degrade codebase quality over time。模型无法在没有大量人工干预的情况下维护和提升代码库质量。

### 2.3 为什么 token-maxxing 无效

| 常见建议 | 实际效果 |
|----------|----------|
| "给更多 token" | 提高下限（catch dumb stuff），不提高上限 |
| "写更好的 prompt" | 无法弥补模型训练的结构性缺陷 |
| "加 adversarial review bot" | 同上，raise the floor, not the ceiling |
| "配置更多 linter" | 只能拦截已知的、模式化的问题 |

**AI 是杠杆（lever），不是解决方案**。它会放大交付系统的一切——优点和缺陷都会被放大。如果交付系统本身缺乏隔离、测试、审查机制，AI 快速产出的大量变更会直接冲垮审查能量。

### 2.4 可维护性没有 fast oracle

```
测试反馈：     秒级      → RL 可以跑百万次循环优化
功能正确性：   分钟级    → benchmark 可以批量验证
代码腐化成本： 周/月/年级 → 无法 backpropagate 到当初的决策
```

"一个糟糕的架构决策 → 随机的 slop 代码 → 数周或数月后的 bug/事故，而这条路无法从事故反推回当初导致它的决策。"

Dex 的推论链：

```
如果模型能可靠区分好坏代码
  → 它一开始就该写出好的版本
    → 但可维护性没有 fast oracle
      → RL 无法在训练中 reward 它
        → 模型在可维护性上停滞不前
```

这也是为什么 Dex 不信任任何基于现有 benchmark 的分数提升——它们不测可维护性。

---

## 3. 四阶段工程实践框架

> "Turning the lights back on" —— 把代码审查放回环路

核心理念：接受"目前模型在可维护性上不行"这个约束，用工程流程在约束内优化，追求 2-3x 提速（而非 10-100x 但烧掉代码库）。

### 3.1 总览

```
产品需求 → 系统架构 → 程序设计 → 垂直切片
(Product)  (System)   (Program)   (Vertical)
  ↓          ↓          ↓           ↓
 做什么    服务怎么    代码长     切片式
 和为什么   交互       什么样     交付验证
```

**80/20 分配原则**（Dex 的估算）：

| 任务比例 | 处理方式 |
|----------|----------|
| ~40% | 直接 oneshot 给 agent，加 1-2 轮轻反馈 |
| 中等任务 | Product + System 合并到一个 plan 文档 |
| 大任务 | 走完整四阶段（重构类可跳过 Product） |

### 3.2 阶段一：产品需求（Product Requirements）

**目标**：把两句话或一段语音备忘录变成半结构化的产品文档。

核心要素：
- **要解决的问题**：用用户的语言描述真实痛点
- **成功的样子**：发布后能读到什么信号来判断"值得做"——用户结果（"XYZ 工作流耗时更短"）、错误率、延迟，或最朴素的"关于 X 的客服工单消失了"

```
✅ 好的成功指标：
   - "用户能在 3 分钟内完成 onboarding"
   - "支付失败的客服工单减少 50%"

❌ 坏的成功指标：
   - "代码覆盖率提升到 90%"（技术细节，不是产品结果）
   - "重构后的架构更清晰"（谁在乎？用户看不到）
```

**关键技巧**：不描述界面，直接做粗略 HTML mockup。"一张粗略的 HTML mockup 能解决三段文字都说不清的争论。"

**何时跳过**：copy 微调、一次性脚本、有明显 repro 的 bug——直接丢给 agent。

### 3.3 阶段二：系统架构（System Architecture）

**目标**：对齐服务、端点、schema、队列、存储如何交互，不深入程序设计细节。

核心产出：

```
# 交互序列（sequence diagram 风格）
UI → API: PUT /resources/:slug
API → ResourceService: create(input)
ResourceService → Store: insert resource
ResourceService → UI: 201 resource

# 契约 / 端点形状
PUT /api/resources/:slug
  request:  { destination: string }
  response: { resource: Resource }

# 数据模型
CREATE TABLE resource (
  slug         TEXT PRIMARY KEY,
  destination  TEXT NOT NULL,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);
```

**Pitfall**：Mermaid 图有时会给人"已经对齐了"的错觉。架构阶段的杠杆很高，能预先挡掉很多 model tics，但仅靠架构不足以产出高质量代码——还需要阶段三。

### 3.4 阶段三：程序设计（Program Design）

这是 Dex 认为 "criminally underemphasized"（被严重低估）的环节。

大多数人的假设：架构对了 → 模型就能 cook。Dex 的实际经验：你会不喜欢你拿回来的东西。

**程序设计要确定的**（在任何人/agent 写实现之前）：

```
1. 调用栈树（Call-stack trees）—— 控制流变更
   entrypoint
     runCommand
+      handleCreateResource
+        ResourceClient.create(input)
+          POST /resources
+        renderResult
-    legacyCreateFlow

2. 文件树 diff（File-tree diffs）—— 代码布局
   src
   └── resource
+      ├── resource-client.ts      # NEW - wraps API contract
+      ├── resource-client.test.ts # NEW - covers mapping
~      └── resource-route.ts       # MODIFIED - wires create action

3. 类型与方法签名（Types & method signatures）
   interface Item {
     id: ItemId
     parentId: ItemId | null
   }
   resolveTarget(items: Item[], cursor: Cursor) -> ItemId | null
```

这些产出不需要太久（模型起草，你跟它争论），但每一个都是在代码审查阶段（改主意最贵的时候）才会做的决策，提前到这儿做了。

> "30 minutes of planning saves hours of review."

### 3.5 阶段四：垂直切片（Vertical Slices）

**模型偏爱「水平切片」**（horizontal / stack-order）：

```
模型的默认计划（水平切片）：
  1. 数据库 Migration
  2. Service Layer
  3. API
  4. Frontend
  → 全部写完才能"碰到"整个功能，中间无法验证
```

**Dex 偏爱「垂直切片」**（vertical / tracer bullets）：

```
Dex 的方式（垂直切片）：
  1. 建 API 契约 + mock 数据，用 curl 测试
  2. 建 frontend 消费 mock 数据，在浏览器里迭代打磨
  3. 把 API 接到 services 层（services 先返回 mock）
  4. 加数据库 migration，接通 services 和 DB
  5. 加业务逻辑
  6. 加错误处理
  → 每一步都能"碰到"功能，每一步都在验证
```

**为什么垂直切片更好**：
- 每 100-200 行就能检查 + 重新引导，比读完 2000+ 行再发现问题便宜得多
- 前端时代的经验：很少有人写 500+ 行代码中间完全不验证

Dex 通常一次给模型 1-3 个切片，边做边审。"及早重新引导（internals 或功能）比穿越 2000+ 行不知道哪里坏了的代码便宜得多。"

### 3.6 四阶段决策树

```
任务来了
  │
  ├─ copy 微调 / 一次性脚本 / 明确 repro 的 bug？
  │    └─ YES → 直接 oneshot 给 agent
  │
  ├─ 中等复杂度？
  │    └─ Product + System 合并到一个 plan → 直接实现
  │
  └─ 大任务 / 复杂功能 / 棕地重构？
       └─ 走完整四阶段
            Product → System → Program Design → Vertical Slices
```

---

## 4. 关键反思与行动建议

### 4.1 相关性 ≠ 因果关系

Faros 数据是 correlation signal，不是 smoking gun。Dex 明确说"the whole point of this post is to be wary of slop data"。

但方向上感觉是对的（directionally valid），与社区广泛反馈一致：
- Matt Pocock："codebases are falling apart faster than they ever have before"
- Mario 在 AI Engineer Europe 演讲中恳求大家放慢："毫无理由因 coding-agent 失误而宕机的公司，正在因 coding-agent 失误而宕机"
- FT 报道 Amazon 因 coding-agent 失误导致宕机

### 4.2 行动建议

| 建议 | 具体做法 |
|------|----------|
| 控制变更粒度 | 规范 AI 生成代码的 PR 规模；一次给 agent 1-3 个 vertical slice |
| 升级审查与测试防线 | 在链入 Agent 前，先补齐 CI/CD 自动化测试 + 架构隔离 |
| 把规划前置 | Product + System + Program Design 文档化，再做实现 |
| 优化人机分工 | human-in-the-loop 重心放在需求定义、架构设计、边界验证 |
| 读该读的代码 | 不要追求"完全不读代码"；读 research 和 plan 比读代码杠杆更高 |

### 4.3 重新理解杠杆层级

来自 Dex 早期文章「Advanced Context Engineering」的框架——人的精力应该投在杠杆最高的环节：

```
杠杆从高到低：

  Research（研究）     ── 一行错误的研究 → 数千行错误代码
      ↑
  Plan（计划）         ── 一行错误的计划 → 数百行错误代码
      ↑
  Code（代码）         ── 一行错误的代码 → 一行错误代码
```

"一个糟糕的 plan 能导致数百行糟糕的代码。一个糟糕的 research，对代码库工作方式的误解，能让你得到数千行糟糕的代码。"

**结论**：把人的注意力集中在 research 和 plan 的审查上，比逐行审查代码更高效。

### 4.4 前沿 benchmark 的进展

Dex 提到几个方向正确的努力（但强调仍不足以放心下注）：

| 项目 | 特点 |
|------|------|
| SWE-Marathon (Abundant AI) | ~400 小时的大任务，compound reward 而非单 binary bit |
| DeepSWE (Datacurve) | 从未真实建过的 OSS 大任务（解决训练集污染） |
| Frontier Code (Cognition) | 多 PR 任务 + mutation testing + judge model 检查代码质量规则 |

Frontier Code 最值得关注：它用 judge model 检查 diff 的代码质量规则，并 penalize 写出"在 pre-patch 代码上不会失败的测试"（mutation testing 思路）。

但 Dex 的警告：if a model could reliably tell good code from bad, it might have written the good version to begin with.

---

## 核心观点提炼

1. **Benchmark 骗人** —— SWE-bench 只测功能正确性，不测可维护性；RL 训练优化的是"让测试通过"，不是"写出好代码"
2. **AI 是放大器** —— 它放大交付系统的一切，包括结构缺陷；没有隔离/测试/审查的交付系统会被 AI 冲垮
3. **Lights-off 是幻想** —— HumanLayer 亲历：全面关灯 5 个月后从零重写
4. **可维护性没有 fast oracle** —— 这是模型训练层面的结构性问题，无法靠 prompt/token/harness 解决
5. **把灯打开** —— 接受约束，用四阶段流程在约束内做到 2-3x 安全提速，而非 10-100x 烧掉代码库
6. **规划 > 审查** —— 在 research 和 plan 阶段投入人力，比逐行审查 AI 代码杠杆更高

---

## 参考资料

- [视频：拒绝 AI 盲目生成：一套四阶段工程实践（为什么叫QQ）](https://www.youtube.com/watch?v=YYgrTVzNrZI)
- [Why Software Factories Fail — Dex Horthy（核心原始来源）](https://github.com/humanlayer/advanced-context-engineering-for-coding-agents/blob/main/wsff.md)
- [Advanced Context Engineering for Coding Agents — Dex Horthy](https://www.humanlayer.dev/blog/advanced-context-engineering)
- [The AI Productivity Paradox Report — Faros AI](https://www.faros.ai/blog/ai-software-engineering)
- [Context Engineering with Dex Horthy — Pragmatic Engineer](https://newsletter.pragmaticengineer.com/p/context-engineering-with-dex-horthy)
- [Harness Engineering is not Enough（Dex 演讲，AI Engineer World's Fair 2026）](https://www.youtube.com/watch?v=Ib5GBkD555M)

## 相关笔记

- [[AI编码工具对比]]
- [[SWE-bench局限性分析]]
- [[12-Factor-Agents]]
