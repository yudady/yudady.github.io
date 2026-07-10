---
title: Matt Pocock Skills v1.1 — AI 辅助编程 SDLC 工作流
aliases:
  - Matt Pocock Skills v1.1
  - mattpocock-skills v1.1
tags:
  - ai-coding
  - claude-skills
  - sdcl
  - vibe-coding
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=A8mokin_YOs"
  - "https://github.com/mattpocock/skills"
author: Matt Pocock
created: 2026-07-10
updated: 2026-07-10
description: |
  Matt Pocock 的 AI Coding Skills v1.1 更新，将 vibe coding 收敛为完整的软件开发生命周期（SDLC），
  涵盖重命名流程技能、修复 Grilling 机制、引入 Wayfinder/Research/Prototype/Implement 四大新技能，
  以及 Code Review 融入 Martin Fowler 重构心智模型。
level: intermediate
stars: 4
---

# Matt Pocock Skills v1.1 — AI 辅助编程 SDLC 工作流

> Matt Pocock 的 AI Coding Skills 仓库（16 万 stars、700 万次下载）发布 v1.1，
> 从单纯的「规划工具」演化为完整的软件开发生命周期（SDLC）。
> 适合正在使用 Claude Code / Codex 等 AI Agent 辅助开发的工程师。

## 目录

- [[#一、核心概念与技能重命名]]
- [[#二、Grilling 机制优化]]
- [[#三、完整 SDLC 流程]]
- [[#四、Wayfinder — 大型项目规划核心]]
- [[#五、支持技能：Research / Prototype / Implement]]
- [[#六、代码品质与测试升级]]
- [[#七、迁移指南与行动建议]]

---

## 一、核心概念与技能重命名

v1.1 对两个核心流程技能更名，消除实际使用中的认知摩擦（Friction）：

| 旧名称 | 新名称 | 更名原因 |
|--------|--------|----------|
| `/to-prd` | `/to-spec` | 原来产出的内容夹杂技术细节，不符合传统 PRD（产品需求文档）的范畴。改用 Spec（Specification）更宽泛，容许结合技术与非技术层面 |
| `/to-issues` | `/to-tickets` | 原名偏向 GitHub / Linear 特定术语。Ticket 是更普遍的软件工程概念——一个 Spec 下拆分多个具体任务 |

**关键区别**：

```
PRD（产品需求文档）    → 偏向产品本身的功能描述
Spec（规格书）         → 更宽泛，可混合技术 + 非技术内容
Issue（议题）          → 偏向 GitHub/Linear
Ticket（工单）         → 通用概念，一个 Spec 下拆解出的执行单元
```

---

## 二、Grilling 机制优化

针对社区反馈，大幅修复 AI 与用户之间的「拷问（Grilling）」互动机制：

### 三项修复

| 问题 | 修复方案 |
|------|----------|
| AI 连珠炮式提问 | 强制「一次只问一个问题」 |
| 拷问完 AI 自动暴走写代码 | 引入确认閘門（Confirmation Gate），未确认不进入实作 |
| AI 自己拷问自己（探索代码库后自问自答） | 明确区分「事实（Facts）」与「决策（Decisions）」 |

### Facts vs Decisions 的界线

```
Facts（事实）                    Decisions（决策）
─────────────────────           ─────────────────────
由 AI 自行探索代码库获得          必须由用户拍板
例：发现用了 Prisma ORM          例：选择 REST 还是 GraphQL
例：发现 auth 逻辑在 middleware  例：是否引入新依赖
```

**核心原则**：AI 可以自己去调查事实，但项目走向的选择权永远在用户手中。

---

## 三、完整 SDLC 流程

v1.1 首次将整个工作流补完为标准软件开发生命周期：

```
Grill with Docs / Wayfinder
         │
         ▼
     /to-spec
    （规格书）
         │
         ▼
   /to-tickets
  （拆解工单）
         │
         ▼
    /implement
  （逐单实作）──→ TDD（Red → Green）
         │              │
         ▼              ▼
   /code-review     重构留给 Code Review
  （双轴审查）
         │
         ▼
     Git Commit
```

**设计哲学**：每个阶段都是独立的 AI Session。一个 Spec 拆出多个 Ticket，每个 Ticket 在单独的 Session 中实作，避免 Context Window 爆炸。

### /implement 技能

回答用户最常问的「规划完下一步该怎么做」：

- 根据 Spec 或 Ticket 执行实作
- 尽可能使用 TDD（在预设的 seam 处）
- 定期运行 Type Check + 单一测试文件
- 完成后全量测试
- 自动触发 Code Review → Git Commit

> 这是一个非常简单的技能，核心价值在于给用户一个明确的「下一步」锚点，避免规划完成后迷失方向。

---

## 四、Wayfinder — 大型项目规划核心

**这是 v1.1 最令人兴奋的新技能。**

### 适用场景

当你有一个「模糊、庞大、无法在单一 AI Session 中完成」的构想时：

```
传统做法（Grill with Docs）:
  用户 ──焦虑地管理──→ AI Session（担心 Smart Zone / Context 爆炸）
  手动 Handoff，信息容易丢失

Wayfinder 做法:
  用户 ──→ GitHub Issues（共享地图）
              │
              ├── Research Ticket（调研）
              ├── Grilling Ticket（拷问决策）
              ├── Prototype Ticket（原型验证）
              └── Task Ticket（杂项任务）
  每个工单 = 一个 AI Session 的容量
```

### 核心机制

1. 在 GitHub Issues 上绘制「依赖关系图（Shared Map）」
2. 大任务拆解为具备 Blocking Relationships 的子工单
3. 每个子工单精确控制在一个 AI Session 的容量内
4. 所有工单完成后，信息汇总回 Map，再用 `/to-spec` 生成规格书

### 四种工单类型

| 类型 | 用途 | 是否需要用户参与 |
|------|------|-----------------|
| `Research` | 背景 AI Agent 调研第一手资料，自动产出 Markdown 报告 | 否（AFK） |
| `Grilling` | 需要 AI 拷问用户做决策 | 是 |
| `Prototype` | 制作粗糙但具体的原型，拉高讨论保真度 | 部分参与 |
| `Task` | 配置、权限、数据迁移等琐碎杂项 | 否（人工或自动化） |

### Grilling 与 Wayfinder 的选择

```
选 Grill with Docs 如果你需要：
  ✅ 快速、单次对话就能理清的小型需求
  ✅ 不涉及前端 UI / 复杂依赖
  ✅ 可以在一个 AI Session 内完成

选 Wayfinder 如果你需要：
  ✅ 涉及前端 UI（强烈推荐先 Prototype）
  ✅ 需要调研外部依赖/技术选型
  ✅ 多人协作（信息保存在 GitHub Issues）
  ✅ 需要跨多次 Session 逐步推进
  ❌ 不适合：单次对话就能搞定的简单需求
```

---

## 五、支持技能：Research / Prototype / Implement

### /research（调研技能）

- 启动一个背景 AI Agent（Background Agent）
- 调查第一手资料，期间用户可继续其他工作
- 自动产出符合专案既定格式的 Markdown 报告
- 保存位置遵循仓库已有的文档约定

### /prototype（原型技能）

专为前端与交互逻辑设计，区分两种类型：

| 类型 | 说明 | 适用场景 |
|------|------|----------|
| Logic Prototype（逻辑原型） | 验证状态管理与业务逻辑 | 表单流程、权限逻辑、数据转换 |
| UI Prototype（介面原型） | 快速搭建可交互的粗糙 UI | 布局验证、交互流程、视觉方向 |

**核心价值**：用极低成本制作粗糙但具体的产出，拉高 Spec 讨论时的具體真实度。不要在 Spec 阶段讨论抽象概念——先做个东西出来反应（React to）。

### /implement（实作引导）

详见 [[#三、完整 SDLC 流程]] 章节。

---

## 六、代码品质与测试升级

### Code Review 融入 Martin Fowler 重构心智模型

新版 `/code-review` 在双轴审查基础上引入 Code Smells：

**双轴审查架构**：

```
/code-review
    │
    ├── Sub-Agent A：Standards Axis
    │   └── 对照 coding-standards.md 检查编码规范
    │
    └── Sub-Agent B：Spec Axis
        └── 对照原始 Spec / Issue 检查实现一致性
```

**新增的 Code Smells 检测**（利用 LLM 的预训练权重 Prior）：

| Code Smell | 说明 |
|-----------|------|
| Mysterious Name | 命名不清晰，意图不明 |
| Duplicated Code | 重复代码 |
| Feature Envy | 函数过度依赖另一个类的数据 |
| Data Clumps | 相同数据项反复出现 |
| Primitive Obsession | 过度使用基本类型而非领域对象 |
| Repeated Switches | 重复的 switch/case 逻辑 |
| Divergent Change | 一个类因多种原因被修改 |
| Speculative Generality | 过度设计，为不存在的需求预留扩展 |
| Message Chains | 过长的方法调用链 |

**为什么有效**：《Refactoring》是经典著作，这些术语深度嵌入 LLM 的预训练权重。Prompt 中只需点到为止地列出术语，AI 就会精准识别并主动报告代码中的架构缺陷。

### TDD 技能降级为「纯參考資料」

过去的问题：

```
v1.0 TDD 流程：
  Red → Green → Refactor（在同一个 Skill 中循环）
  问题：AFK 自动化代理时常卡住

v1.1 TDD 流程：
  Red → Green（实作阶段，专注让测试亮绿灯）
  重构 → 交由 Code Review 处理
```

| 对比 | v1.0 | v1.1 |
|------|------|------|
| 性质 | 强行规范执行步骤 | 纯参考资料 |
| 循环 | Red → Green → Refactor | Red → Green |
| 重构 | 在 TDD 循环内 | 移至 Code Review |
| AFK 兼容性 | 差（容易卡住） | 好（无需人工干预） |

---

## 七、迁移指南与行动建议

### 升级步骤

由于涉及核心技能名称变更，自动更新可能无法完美覆盖：

```bash
# 1. 手动清理旧技能
rm -rf .claude/skills/to-prd .claude/skills/to-issues

# 2. 重新安装全部技能
npx skills add mattpocock/skills

# 3. 检查 skills 文件夹，确认无残留旧文件
```

### 三个核心建议

| 优先级 | 建议 |
|--------|------|
| 高 | 复杂项目停止盲目使用 Grill with Docs，改用 Wayfinder 建立 GitHub Issues 解题地图 |
| 中 | 前端项目搭配 Prototype 进行介面/逻辑的早期验证，拉高讨论保真度 |
| 中 | 实作时专注 Red-Green 循环，将重构与坏味道修正留给 Code Review，最大化开发速率 |

---

## 参考资料

- [Matt Pocock Skills GitHub Repo](https://github.com/mattpocock/skills)
- [Skills.sh 订阅更新](https://aihero.dev/skills/subscribe)
- [YouTube 视频：New Skills! v1.1](https://www.youtube.com/watch?v=A8mokin_YOs)

## 相关笔记

- [[Matt Pocock Skills 仓库研究]]（如有）

---

*文档生成时间：2026-07-10*
*基于 Matt Pocock Skills v1.1 YouTube 视频*
