---
title: "mattpocock-skills 保姆级教程 — 学习笔记"
date: 2025-07-02
source: "[超火AI编程工作流mattpocock-skills，保姆级教程详细讲解](http://www.youtube.com/watch?v=qdZ01t-dqw8)"
tags: [AI, skills, claude-code, workflow, mattpocock]
aliases: [mattpocock skills tutorial, AI编程工作流]
related: "[[matt-pocock-skills]]"
---

# mattpocock-skills 保姆级教程笔记

> 影片从**实战使用流程**角度讲解 mattpocock-skills，与 [[matt-pocock-skills]]（文档详析）互补。

---

## 1. 核心设计哲学

**自由度 > 流程管控**。对比 GSD、spec 等重型工作流：

- GSD/spec 会剥夺工程师主导权，强加框架
- mattpocock-skills 强调**可自由组合的小型技能**
- 设计原则：「模型能力越强，技能设计应越简单」
- 适配主流 AI 编程工具：Claude Code、Codex

---

## 2. 安装与初始化

### 全局安装

```bash
npx skills@latest add mattpocock/skills
```

通过**软链接（Soft Link）**全局安装，让通用 AI 技能在所有项目中方便调用。

### 项目初始化（`setup` 技能）

每个新项目执行一次 `/setup-matt-pocock-skills`，配置：

1. **问题追踪器**：本地 / GitHub Issues / GitLab Issues
2. **上下文模式**：
   - Single Context — 项目结构简单时
   - Multi Context — 项目庞大时拆分上下文
3. **核心约束文件**：生成 `claude.md` 或 `agent.md` — AI 每次对话自动注入，定义全局约束

---

## 3. 开发前工作流（需求调研 → 设计）

### 3.1 深度访谈 — `gatekeep` / `gatekeep-with-docs`

> 对应文档中的 `/grill-me` 和 `/grill-with-docs`

想法模糊或不确定需求影响时调用，从 6-7 个维度连续追问：

| 技能 | 输出 |
|------|------|
| `gatekeep` | 纯访谈，不生成落地文件 |
| `gatekeep-with-docs` | 对话后记录术语定义、决策到文件 |

### 3.2 需求落地

- **大需求** → `/to-prd`：生成 PRD（用户故事、技术栈、数据库设计、API 路由）
- **小改动** → `/to-issues`：直接同步到追踪器

### 3.3 界面原型 — `/prototype`

遇到 UI 迭代瓶颈时：
- AI 基于项目现有代码和设计规范
- 产出 **1-3 种不同视觉风格的网页预览**（不是文字描述）
- 挑选后一键转化为真实代码

---

## 4. 开发后工作流（质量维护）

### 4.1 代码审查 — `review`

- 扫描整个代码库，找出可改进的盲点
- 产出详细 **HTML 报告**（说明优化原因 + 预期效果）
- 适合每轮功能迭代后的「代码打扫除」

### 4.2 Bug 偵测 — `bugs`

- 内建 **10 种测试与调试方法**
- 按顺序轮流尝试，直到精确定位根因
- 对应文档中的 `/diagnosing-bugs`（6 阶段调试循环）

---

## 5. 跨工具协作与学习

### 5.1 进度交接 — `handover`

> 对应文档中的 `/handoff`

- 中断 Claude Code 或切换到其他 AI 工具时
- 生成临时交接文档，下一个 Agent 完美继承进度与约定

### 5.2 互动式教练 — `coach`

> 对应文档中的 `/teach`

- AI 直接对接**官方权威文档**（Anthropic / GitHub）
- 动态生成教学课件 + 阶段测验
- 可通过实时对话调整课程难度
- 实现边学边练的互动式学习

---

## 6. 推荐开发节奏

```
setup 初始化
  → gatekeep 釐清需求
    → to-prd 落實文檔
      → 迭代开发
        → review 清理代碼
```

这个节奏能最大化发挥 AI 工作流的价值。

---

## 7. 行动建议

1. **拉取源码研究 Prompt 写法** — 不只是使用，要深度理解 skill 设计思路，据此定制团队专属技能
2. **建立开发仪式感** — 严格遵循上述节奏，让 AI 工作流从"锦上添花"变成"不可或缺"

---

## 8. 与完整文档的关系

| 本文视角 | [[matt-pocock-skills]] 视角 |
|---------|---------------------------|
| 实战使用流程、教学讲解 | 每个 Skill 的详细机制与原理 |
| 适合快速上手 | 适合深度理解与定制 |
| 影片: 保姆级教程 | 来源: 官方文档 + 源码 |

---

*笔记时间：2025-07-02*
*影片：[超火AI编程工作流mattpocock-skills，保姆级教程详细讲解](http://www.youtube.com/watch?v=qdZ01t-dqw8)*
