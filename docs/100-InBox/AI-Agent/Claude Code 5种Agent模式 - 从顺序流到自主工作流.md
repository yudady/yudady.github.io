---
title: Claude Code 5种Agent模式 - 从顺序流到自主工作流
aliases: [Claude Code Agent Patterns, Claude Code Subagents, Claude Code Agent Teams]
tags: [ai-agent, status/active, area/distill, type/doc, topic-claude-code, topic-agent-patterns]
source: ["https://www.youtube.com/watch?v=38t5UBCa4OI"]
author: Julian Goldie
created: 2026-04-09 21:25
updated: 2026-04-09 21:25
description: |
  详解 Claude Code 的 5 种 Agent 模式：Sequential Flow、Operator、Split & Merge、Agent Teams、Headless，从简单到自主的渐进式工作流。
level: intermediate
stars: 4
---

# Claude Code 5种Agent模式 - 从顺序流到自主工作流

> Claude Code 内建了 3 种 subagent，日常使用中已自动调度。理解完整的光谱后，你可以从手动控制到完全自主，按任务复杂度选择合适的工作模式。

---

## 前置知识：内建 Subagent

即使是最基本的对话，Claude Code 也在后台自动使用 subagent：

| Subagent | 模型 | 能力 | 触发方式 |
|----------|------|------|----------|
| Explore | Haiku（快速便宜） | 只读：搜索文件、查看目录结构 | Claude 自动判断 |
| Plan | Haiku | 只读：进入 plan mode 后研究代码库 | `/plan` 或 Shift+Tab x2 |
| General Purpose | Sonnet | 完全读写：复杂多步骤任务 | Claude 自动判断 |

关键点：每个 subagent 有独立 context window，不污染主对话。

---

## 模式总览

```
复杂度 ↑
         ┌──────────────────┐
         │  5. Headless     │  ← 无需人在环中，定时/脚本驱动
         │  (自主工作流)     │
         ├──────────────────┤
         │  4. Agent Teams  │  ← Agent 间可通信，共享任务列表
         │  (团队协作)       │
         ├──────────────────┤
         │  3. Split&Merge  │  ← 单会话内 fan-out/fan-in
         │  (拆分合并)       │
         ├──────────────────┤
         │  2. Operator     │  ← 多终端并行，人工协调
         │  (人工编排)       │
         ├──────────────────┤
         │  1. Sequential   │  ← 单终端，任务依次执行
         │  (顺序流)         │
         └──────────────────┘
复杂度 ↓
```

---

## Pattern 1: Sequential Flow（顺序流）

最基础的模式。一个终端，任务依次执行，共享 context 累积。

```
Task 1 → Task 2 → Task 3 → Task 4
   ↓        ↓        ↓
  context 不断增长
```

适用：任务之间有依赖关系（如：建页面 → 加 hero image → 加表单）

**核心限制**：Context Window 天花板

```
context 积累 → 绿色进度条增长 → "Context Rot"
  ↓
Claude 开始遗忘、无法找到之前的信息
  ↓
解决方案：skills + /compact + /clear
```

- 结构良好的 `claude.md` 和 skill 让 Claude 按需加载/卸载信息
- `/compact` 保留对话摘要
- `/clear` 清空重新开始

**最终会撞墙，此时升级到 Pattern 2。**

---

## Pattern 2: Operator（人工编排）

开多个终端，每个终端一个独立的 Claude 实例，你作为协调者。

```
Terminal 1: 新 onboarding 流程    ─┐
Terminal 2: 修复 checkout bug      ├─ 人工协调
Terminal 3: 重新设计 settings 页面  ─┘
```

### 使用 `claude -w` 创建隔离工作空间

```bash
# 每个命令创建独立 git worktree + 分支
claude -w "new onboarding flow"
claude -w "checkout bug fix"
claude -w "redesign user settings"
```

```
项目目录/
├── main branch
├── .claude/
│   ├── new-onboarding-flow/   ← 独立 worktree + 分支
│   ├── fix-checkout-bug/
│   └── redesign-user-settings/
```

特点：
- 每个实例有干净 context window，互不干扰
- 关闭窗口时 Claude 自动清理（无变更则删除 worktree）
- 有变更则询问如何处理

适用：**互不依赖的并行任务**，需要最大控制权

限制：4-5 个终端以上难以管理

---

## Pattern 3: Split & Merge（拆分合并）

在**单个** Claude Code 会话内，Claude 自己拆分任务到多个 subagent 并行执行。

```
          你给一个任务
              ↓
     ┌── Sub 1 ──┐
     ├── Sub 2 ──┤  ← 并行执行，各自独立 context
     ├── Sub 3 ──┤
     └── Sub 4 ──┘
              ↓
       主 Agent 合并结果
              ↓
          返回综合答案
```

Hub-and-Spoke 模型：subagent 只能与主 agent 通信，彼此之间**不能**通信。

- 最多同时 10 个 subagent
- Claude 自动分析任务并决定拆分策略
- Boris Churney（Claude Code 创造者）提到有时同时启动 15 个

### 自定义 Subagent

在 `.claude/agents/` 目录创建自定义 agent 文件：

```
.claude/agents/
├── advisor-researcher.md    ← 研究灰度决策，返回对比表
├── code-reviewer.md
└── test-writer.md
```

Claude 会读取所有 agent 的名称和描述，自动匹配或手动指定使用。

### Builder-Validator Chain（构建-验证链）

```
Sub 1 (Builder)     Sub 2 (Validator)
      ↓                   ↑
   构建代码          审查代码
      ↓                   ↑
      └──→ 主 Agent ←─────┘
```

通过主 Agent 中转实现构建 + 质量检查的自动化。

适用：可拆分为独立子任务的复杂工作

---

## Pattern 4: Agent Teams（团队协作）

Agent 之间可以**直接通信**，通过共享任务列表协调。

```
     共享任务列表
    ┌───────────┐
    │  Task Board │
    └─────┬───────┘
   ┌──────┼──────┐
   ↓      ↓      ↓
 Agent1  Agent2  Agent3    ← 各自是完整的 Claude Code 实例
(Front)  (Back)  (Test)       各自有独立 context window
   └──────┼──────┘
          ↓
       Team Lead
```

与 Split & Merge 的区别：

| 维度 | Split & Merge | Agent Teams |
|------|---------------|-------------|
| Agent 间通信 | 不能，需经主 Agent | 可以，通过共享任务列表 |
| 触发方式 | Claude 自动 | 需在 prompt 中明确指定 |
| Token 消耗 | 中等 | 高（4-7 倍单会话）|
| 状态 | 稳定 | 实验性（Opus 4.6 research preview）|

### 启用方式

在 `settings.json` 添加：
```json
{
  "claudeCodeExperimentalAgentTeams": true
}
```

### 操作方式

- `Shift+Up/Down` 在 teammates 之间导航
- 可直接给任何 teammate 发消息（绕过 team lead）
- 也可只与 team lead 对话

适用：**需要跨角色协作的复杂项目**（如前后端 + 测试联调）

**警告**：Token 消耗极高，只在 subagent 无法胜任时使用。社区观点：能产出大量工作 ≠ 能产出正确工作。

---

## Pattern 5: Headless（自主工作流）

完全无人工干预，Claude 独立完成任务。

```bash
# -p flag：传入 prompt，无需交互
claude -p "review yesterday's git commits and write a summary to morning-report.md"
```

### 结合定时任务

```bash
# crontab 示例：每天早上 7 点自动生成日报
0 7 * * * claude -p "review yesterday's work and write summary to morning-report.md"
```

### 实际应用场景

| 场景 | 命令 |
|------|------|
| 自动日报 | `claude -p "analyze yesterday's commits, write report"` |
| 内容生产 | 脚本拉取视频字幕 → `claude -p` 生成社交媒体帖子 |
| 批量处理 | `claude -p` + skill 调用，全自动化 |

### 限制与护栏

```bash
# 限制工具权限（只读）
claude -p "task" --allowed-tools "Read,Search"

# Ralph Loop：反复迭代直到满意
# 社区方案：同一 prompt 循环喂入，Claude 在上一轮结果上迭代
```

关键原则：**只用于输出容易验证的任务**。不要用于难以撤销的操作。

---

## 模式选择决策树

```
任务有依赖关系？
├── 是 → Pattern 1 (Sequential)
│         context 快满了？
│         ├── 是 → Pattern 2 (Operator) 或拆分任务
│         └── 否 → 继续 Pattern 1
└── 否 → 任务可拆分为独立子任务？
          ├── 是 → 子任务间需要通信？
          │        ├── 否 → Pattern 3 (Split & Merge)
          │        └── 是 → Pattern 4 (Agent Teams)
          └── 否 → 任务可以无人值守？
                   ├── 是 → Pattern 5 (Headless)
                   └── 否 → Pattern 2 (Operator)
```

---

## 参考资料

- [视频：5 Claude Code Agent Patterns](https://www.youtube.com/watch?v=38t5UBCa4OI)
- Boris Churney（Claude Code 创造者）关于多 subagent 的讨论
- GSD (Get Shit Done) 框架：规划 + 拆分 + 执行的项目管理框架

## 相关笔记
- [[../../200-Distill/200-AI-Tools/Hermes Agent - 自改进AI代理框架]]
- [[../002-To-Archive/Hermes Agent v0.8.0 - 自诊断自修复的AI Agent]]
