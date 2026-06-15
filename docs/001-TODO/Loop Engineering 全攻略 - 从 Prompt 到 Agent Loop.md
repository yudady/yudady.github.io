---
title: Loop Engineering 全攻略 - 从 Prompt 到 Agent Loop 的范式转移
aliases: [loop engineering, agent loop, ralph loop]
tags:
  - ai-agent
  - loop-engineering
  - prompt-engineering
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=8VMx8Yg5fI4"
  - "https://addyo.substack.com/p/loop-engineering"
  - "https://github.com/cobusgreyling/loop-engineering"
author: Jim AI Notebook / Addy Osmani / Boris Cherny
created: 2026-06-16
updated: 2026-06-16
description: Loop Engineering 是 2026 年 6 月由 Addy Osmani 命名的概念：不再手动 Prompt agent，而是设计一个系统（Loop）来替你 Prompt、验证、记忆和决策。本文从 Boris Cherny 的核心洞察出发，拆解五层架构、Ralph Loop、Maker-Checker 模式和三阶段成熟度路线图。
level: intermediate
stars: 5
note: 视频无字幕，基于视频元数据 + Addy Osmani 原文 + 多源搜索综合整理
---

# Loop Engineering 全攻略 - 从 Prompt 到 Agent Loop 的范式转移

> **核心一句话**：工程师的工作正在从"Prompt Agent"变成"设计 Loop 让 Agent Prompt Agent"。这不是又一个 buzzword，而是 AI agent 能力达到可自主运行数小时后的必然结果。

---

## 目录

- [起源：三句话引爆社区](#起源三句话引爆社区)
- [什么是 Loop Engineering](#什么是-loop-engineering)
- [范式演进：三层阶序](#范式演进三层阶序)
- [五层架构：Building Blocks](#五层架构building-blocks)
- [Ralph Loop：让 Agent 整夜自主运行](#ralph-loop让-agent-整夜自主运行)
- [Maker-Checker：交叉验证模式](#maker-checker交叉验证模式)
- [验证回路：Loop 的心脏](#验证回路loop-的心脏)
- [三阶段成熟度路线图](#三阶段成熟度路线图)
- [关键工具对比](#关键工具对比)
- [实战：第一周怎么动手](#实战第一周怎么动手)
- [失败模式与陷阱](#失败模式与陷阱)
- [资料来源](#资料来源)

---

## 起源：三句话引爆社区

2026 年 6 月，三段独立的话汇聚成了同一个方向：

| 人物 | 身份 | 话 |
|------|------|-----|
| **Boris Cherny** | Claude Code 负责人 (Anthropic) | "我不再 Prompt Claude 了。我有 Loop 在跑，它们负责 Prompt Claude 并决定下一步做什么。" |
| **Peter Steinberger** | PSPDFKit/Nutrient 创始人 | "你不应该再 Prompt coding agent 了。你应该设计 Loop 来 Prompt 你的 agent。"（X 帖子 650 万浏览） |
| **Addy Osmani** | Google Chrome 工程负责人 | 将这些观点系统化为 **"Loop Engineering"** 概念，发表 Substack 长文 + GitHub 仓库 |

Karpathy 的注脚：同年 3 月，他用 630 行 Python 让一群 Agent 自主跑了 700 次实验，发现 20 个优化，在更大模型上实现 11% 训练加速。他说："All LLM frontier labs will do this. It's the final boss battle."

---

## 什么是 Loop Engineering

### 定义

> **Loop Engineering 是用系统替代你来 Prompt agent。你设计一个递归目标，让 agent 自行迭代直到完成。**

### 对比

| 维度 | Prompt Engineering | Loop Engineering |
|------|-------------------|------------------|
| **核心问题** | "我该说什么才能得到最佳输出？" | "我该建什么系统才能让 agent 自己找活、干活、验证、记忆？" |
| **人的角色** | 在 Loop 内部，每次手动输入 | 在 Loop 外部，设计 Loop 本身 |
| **Agent 定位** | 手里的工具 | 长时间运行的进程 |
| **记忆** | 限于单次对话上下文 | 持久化到磁盘/看板 |
| **验证** | 人工阅读判断 | 机器可验证的停止条件 |

---

## 范式演进：三层阶序

Boris Cherny 提出的清晰分层：

```
┌──────────────────────────────────────────────────┐
│  Layer 3: Loop Engineering                       │
│  设计自主循环系统：调度、编排、验证、记忆        │
│  → "我的工作是写 Loop"                            │
├──────────────────────────────────────────────────┤
│  Layer 2: Harness Engineering                    │
│  为单个 Agent 建约束环境：权限、工具、上下文      │
│  → 给 Agent 建一个安全的"驾驶舱"                  │
├──────────────────────────────────────────────────┤
│  Layer 1: Prompt Engineering                     │
│  手动一问一答：写好 Prompt → 读回复 → 再问       │
│  → "Prompt 是手动对话"                            │
└──────────────────────────────────────────────────┘
```

每一层建立在前一层之上。Harness 是给单个 Agent 建约束，Loop 是在 Harness 之上管理多个 Agent 的时序、决策和自主循环。

---

## 五层架构：Building Blocks

Addy Osmani 定义的 Loop 五大组件 + 记忆：

```
                    ┌───────────────┐
                    │   Automations │ ← "心跳"：定时/事件触发
                    │   (自动化)     │
                    └───────┬───────┘
                            │
          ┌─────────────────┼─────────────────┐
          ▼                 ▼                 ▼
  ┌───────────────┐ ┌───────────────┐ ┌───────────────┐
  │   Worktrees   │ │    Skills     │ │  Connectors   │
  │  (工作树隔离)  │ │  (项目知识)    │ │  (MCP 连接器)  │
  │  防并行冲突    │ │  停止重复解释  │ │  接入真实工具  │
  └───────┬───────┘ └───────┬───────┘ └───────┬───────┘
          │                 │                 │
          └─────────────────┼─────────────────┘
                            ▼
                    ┌───────────────┐
                    │  Sub-agents   │ ← "Maker ≠ Checker"
                    │  (子代理)      │
                    └───────┬───────┘
                            │
                    ┌───────┴───────┐
                    │    Memory     │ ← "非官方第六件"
                    │  (持久记忆)    │    磁盘上的 .md / 看板
                    └───────────────┘
```

### 1. Automations（自动化）— "心跳"

让 Loop 成为真正的循环，而非一次性运行。

| 功能 | Codex | Claude Code |
|------|-------|-------------|
| 定时循环 | Automations 标签页 | `/loop` 指令、cron 任务 |
| 目标循环 | — | `/goal`（持续运行直到可验证条件为真） |
| 生命周期钩子 | — | hooks（生命周期节点触发） |
| 持久化 | — | GitHub Actions |

**`/goal` 的精妙设计**：定义一个可验证条件（如"test/auth 全部通过且 lint 干净"），**用另一个小模型**检查每轮是否完成。Maker ≠ Checker。你可以直接走开。

### 2. Worktrees（工作树）— 防并行混乱

两个 agent 写同一个文件 = 核心故障模式。Git Worktree 让每个 agent 在独立目录、独立分支上工作，共享仓库历史。

> "Worktree 消除了机械冲突，但**你的审查带宽仍然决定上限** — 不是工具。"

### 3. Skills（技能）— 停止重复解释

```
skill-folder/
├── SKILL.md          ← 指令 + 元数据
├── scripts/          ← 可执行脚本
├── references/       ← 参考文档
└── assets/           ← 模板等资源
```

> **"没有 Skills，Loop 每个周期都从零重新推导整个项目；有了 Skills，它开始复利。"**

Skill vs Plugin 的区别：Skill = 编写格式。Plugin = 打包分享方式（含 connectors + skills）。

### 4. Connectors（连接器）— 触达真实工具

基于 **MCP (Model Context Protocol)**，让 agent 能：读 Issue 追踪器、查数据库、打 staging API、发 Slack 通知。

> "这区别在于：agent 说'这是修复方案' vs Loop 自己开 PR、关联 Linear ticket、CI 绿了 ping 频道。"

### 5. Sub-agents（子代理）— Maker 远离 Checker

**核心原则**：写代码的模型打分自己的作业太宽容。必须拆分编写和验证。

```
Agent A (Explorer)  → 探索代码、收集上下文
    ↓
Agent B (Implementer) → 编写代码
    ↓
Agent C (Verifier)  → 对照规范验证（用不同模型最佳）
```

> "Loop 在你不看的时候跑，所以**你真正信任的验证器是你能走开的唯一理由**。"

⚠️ Sub-agent 烧更多 token — 在值得花第二个意见的地方才用。

### 6. Memory（记忆）— 非官方第六件

```
┌─────────────────────────────────────┐
│  对话上下文 (会话内)  ← 会被遗忘      │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │
│  磁盘记忆 (跨会话)    ← Loop 的基础   │
│  • markdown 文件                     │
│  • Linear / GitHub 看板              │
│  • git 历史                          │
└─────────────────────────────────────┘
```

> **"Agent 会忘记，仓库不会。"**

---

## Ralph Loop：让 Agent 整夜自主运行

由 Geoffrey Huntley 命名（致敬《辛普森一家》的 Ralph Wiggum），是 Loop Engineering 最激进的实践模式。

### 解决三个问题

| 问题 | 传统方式 | Ralph Loop |
|------|----------|------------|
| **Context 窗口耗尽** | 单次长会话，90 分钟后降级 | 每轮迭代刷新 200K token 上下文 |
| **状态持久化** | 对话内记忆，退出即失 | 文件系统 + git 作为记忆层 |
| **过早退出** | Agent 看到进展就宣布完成 | stop-hook 拦截退出，强制继续 |

### 工作原理

```
┌─ Iteration 1 ─────────────────────────────┐
│ [200K tokens 全新] 读规范、读活动日志      │
│ 执行任务、写代码、跑测试                   │
│ 更新活动日志到磁盘 → 尝试退出              │
│ ← stop-hook 拦截：检查完成条件             │
│   ├── 条件满足 → 允许退出                  │
│   └── 条件不满足 → 继续                    │
└──────────────────────────────────────────────┘
         │ (上下文被清空)
         ▼
┌─ Iteration 2 ─────────────────────────────┐
│ [200K tokens 全新] 读磁盘上的活动日志      │
│ 读上一轮的代码变更 → 继续工作              │
└──────────────────────────────────────────────┘
         │
         ▼
        ...直到完成条件满足
```

### 对比：长会话 vs Ralph Loop

```
长会话（退化曲线）：
Minute 0:  [200K] → 高产
Minute 30: [150K] → 还行
Minute 60: [100K] → 降级
Minute 90: [50K]  → 隧道视野，忘记早期架构

Ralph Loop（稳定输出）：
Iteration 1: [200K] → 高产
Iteration 2: [200K] → 高产（从磁盘恢复上下文）
Iteration 3: [200K] → 高产
...
```

**实战成绩**：Blake Crosley 用此模式在多个夜间会话中交付了 3,455 行 Python（141 个测试）。

---

## Maker-Checker：交叉验证模式

### 核心逻辑

```
Implementer (Maker)          Verifier (Checker)
     │                            │
     │  Claude Code 写代码         │  Codex 审代码
     │                             │
     │         ──── 交接 ────▶     │
     │                             │
     │     ◀── 反馈/批准/拒绝 ──   │
     │                             │
     ▼                             ▼
  不同模型 = 不同盲区         独立判断
```

Boris Cherny 的量化结论：**给 Claude 一种验证自身工作的方式，质量提升 2-3 倍。**

### 防护层级

| 层级 | 检查方式 | 速度 | 可靠性 |
|------|----------|------|--------|
| L0 | Agent 自检 | ⚡ 最快 | ❌ 最不可靠 |
| L1 | 同模型 sub-agent | ⚡ 快 | ⚠️ 盲区可能重合 |
| L2 | **跨模型互审** | 中等 | ✅ 利用模型差异 |
| L3 | 确定性测试（test/lint/typecheck） | 快 | ✅✅ 最可靠 |
| L4 | 人工最终审查 | 慢 | ✅✅ 最后把关 |

> 最佳实践：L3 + L2 组合。用确定性测试做硬门禁，用跨模型互审补充盲区。

---

## 验证回路：Loop 的心脏

Loop Engineering 的核心不是"让 agent 跑"，而是"怎么知道跑完了且做得对"。

### 验证层级（优先级从高到低）

```
1. 测试通过？     pytest / vitest / jest   ← 最硬信号
2. Lint 干净？    eslint / ruff / golangci-lint
3. 类型检查？     tsc / mypy / pyright
4. 构建成功？     npm run build / cargo build
5. 安全扫描？     npm audit / safety check
```

### 最小实现：验证回路脚本

```bash
#!/bin/bash
# verify.sh — Agent Loop 的验证信号

echo "=== Running tests ==="
npm test 2>&1 || exit 1

echo "=== Lint check ==="
npm run lint 2>&1 || exit 1

echo "=== Type check ==="
npx tsc --noEmit 2>&1 || exit 1

echo "=== All checks passed ==="
exit 0
```

Agent 在 Loop 中反复执行 `verify.sh`，只有全部通过才允许退出。

---

## 三阶段成熟度路线图

### Stage 1: 手动 + 半自动（第一周）

```
你写 Prompt → Agent 执行 → 你验证 → 你写下一个 Prompt
                                    ↑
                          开始用 /goal 让 Agent 自验证
```

- [ ] 写一个 `verify.sh` 脚本（test + lint + typecheck）
- [ ] 用 `/goal "all tests pass and lint is clean"` 试跑
- [ ] 观察行为，调优 Prompt

### Stage 2: 编排 Loop（第一月）

```
GitHub Issue → 自动触发 → Agent 实现 → Sub-agent 验证 → 自动合并
```

- [ ] 配置 Automations（cron / GitHub Actions）
- [ ] 编写 2-3 个核心 Skills（项目约定、构建步骤、测试规范）
- [ ] 设置 Worktree 隔离
- [ ] 实现 Maker-Checker 分工

### Stage 3: 全自主 Factory（长期）

```
定时触发 → 发现工作 → 规划 → 并行实现 → 交叉审查 → 合并 → 记忆更新
```

- [ ] 持久记忆层（markdown / Linear board）
- [ ] MCP Connectors（Issue 追踪、Slack、CI/CD）
- [ ] Spawn budget（防止无限递归）
- [ ] 成本监控和告警

---

## 关键工具对比

| 能力 | Claude Code | Codex | 其他 |
|------|-------------|-------|------|
| **定时循环** | `/loop`, cron, hooks | Automations 标签页 | GitHub Actions (通用) |
| **目标循环** | `/goal` (Maker-Checker 内置) | `/goal` | — |
| **Worktree** | `--worktree`, `isolation: worktree` | 内建多线程 | git worktree (手动) |
| **Skills** | `.claude/skills/`, `$skill-name` | `.codex/skills/` | — |
| **Sub-agents** | `.claude/agents/` | `.codex/agents/` (TOML) | Sandcastle (编排层) |
| **MCP Connectors** | ✅ | ✅ | LangGraph, Temporal |
| **会话恢复** | `claude --resume` | session 持久化 | — |
| **编排框架** | — | — | LangGraph (图), Temporal (持久工作流) |

### LangGraph vs Temporal vs 自建

| 方案 | 定位 | 适合场景 |
|------|------|----------|
| **LangGraph** | 结构化图编排 | 复杂多 Agent 工作流，需要状态机 |
| **Temporal** | 持久工作流引擎 | 需要强一致性、长时运行、容错恢复 |
| **自建 Loop** | shell + cron + git | 简单场景，快速验证想法 |
| **Sandcastle** | TypeScript 编排库 | Docker 沙箱 + 并行 agent |

---

## 实战：第一周怎么动手

### Day 1-2：建立验证回路

```bash
# 1. 确保项目有测试
npm test  # 或 pytest, cargo test, go test

# 2. 写验证脚本
cat > scripts/verify.sh << 'EOF'
#!/bin/bash
set -e
npm run lint && npm run typecheck && npm test
echo "✅ All checks passed"
EOF
chmod +x scripts/verify.sh
```

### Day 3-4：试跑 /goal

```
# 在 Claude Code 或 Codex 中
/goal "实现用户登录功能，要求：
1. 所有测试通过 (npm test)
2. Lint 干净 (npm run lint)  
3. 类型检查通过 (npx tsc --noEmit)
4. 新增测试覆盖率不低于 80%"
```

观察 Agent 如何自主迭代。

### Day 5-7：编写第一个 Skill

```markdown
# SKILL.md - 项目构建规范

## 构建步骤
1. npm install
2. npm run build
3. npm test

## 代码规范
- 函数不超过 50 行
- 必须有类型注解
- 禁止 any 类型

## 测试要求
- 每个公开函数至少 2 个测试用例
- 先写测试再写实现（TDD）
```

---

## 失败模式与陷阱

| 失败模式 | 原因 | 防护 |
|----------|------|------|
| **Context 腐烂** | 长会话累积垃圾，模型忘记原始目标 | Ralph Loop：每轮刷新上下文 |
| **过早退出** | Agent 看到进展就宣布完成 | stop-hook + 机器可验证条件 |
| **单次脆弱性** | 一发命中失败就全盘崩溃 | Loop 模式：失败自动重试 |
| **无限递归** | Agent 卡在循环中不停跑 | Spawn budget（限制最大迭代数） |
| **Token 失控** | Sub-agent 并行烧钱 | 成本监控 + 告警 + 预算上限 |
| **自我评分** | Maker 自己审自己太宽容 | 强制 Maker ≠ Checker |
| **文件系统污染** | 活动日志写入生产代码目录 | 隔离的 .agent/ 目录 |
| **创造性工作不适用** | 编辑/设计需要人工驾驭 | "只在边缘加 Loop"（Reddit 用户智慧） |

### Reddit 用户的清醒发言

> "I don't want to loop-engineer the creative/editorial part, because that works better when I'm actively steering it."

实践做法 — 只在「边缘」加 Loop：
- **Before I show up**: 收集候选、去重、检查来源
- **After I write/select**: 验证链接、检查事实、验证图片
- **中间的创意决策**：留给人

---

## 核心总结

```
┌─────────────────────────────────────────────────┐
│           Loop Engineering 核心公式              │
│                                                  │
│  Loop = Trigger + Context + Action              │
│         + Verification + State + Stop Rules     │
│                                                  │
│  前提 = 沙箱隔离（安全放手）                      │
│  心脏 = 验证回路（可信任的停止条件）               │
│  记忆 = 文件系统（Agent 会忘，仓库不会）           │
│  质变 = Maker ≠ Checker（跨模型互审）             │
│  天花板 = 你的审查带宽（不是工具）                 │
└─────────────────────────────────────────────────┘
```

---

## 资料来源

- **Addy Osmani 原文**：[Loop Engineering](https://addyo.substack.com/p/loop-engineering) (2026/6/8)
- **Cobus Greyling**：[Loop Engineering GitHub Playbook](https://github.com/cobusgreyling/loop)
- **Boris Cherny 引用**：Anthropic Claude Code 负责人
- **Peter Steinberger**：X 帖子 (650 万浏览, 2026/6/8)
- **Ralph Loop**：[Geoffrey Huntley 的 awesome-ralph-loops](https://github.com/snwfdhmp/awesome-ralph-loops)
- **Blake Crosley**：[The Ralph Loop: How I Run Autonomous AI Agents Overnight](https://blakecrosley.com/blog/ralph-agent-architecture)
- **Karpathy Loop**：Fortune 报道 (2026/3/17)
- **概念分析**：[ai-coding.wiselychen.com](https://ai-coding.wiselychen.com/loop-engineering-from-prompt-to-loop-paradigm-shift)
- **Anthropic**：[Building Effective Agents](https://www.anthropic.com/research/building-effective-agents)
- **论文**：[From Agent Loops to Structured Graphs](https://arxiv.org/html/2604.11378v1)

## 相关笔记

- [[Sandcastle - AI Agent 并行沙箱软件工厂]]
- [[AI Agent 编排模式]]
