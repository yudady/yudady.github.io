---
title: Claude Code Workflow 功能 - 脚本化多 Agent 编排
aliases: [Claude Code ultrawork, Claude Code /workflows, workflow.js]
tags:
  - claude-code
  - ai-agent
  - workflow
  - status/active
  - type/doc
source:
  - "https://youtu.be/ozVTJm3n2U4"
  - "https://www.reddit.com/r/ClaudeCode/comments/1tkjy4u/claude_code_dropped_workflows/"
author: AI超元域 (YouTube 频道)
created: 2026-05-24
updated: 2026-05-24
description: |
  Anthropic 在 Claude Code V2.1.47/V2.1.48 中秘密新增的 Workflow 功能完整解析。
  通过 JS 脚本实现多 Agent 可观测、可验证、可复跑的精准编排，
  从「模型临场建议」推进到「代码化控制流」的工程化新阶段。
level: intermediate
stars: 4
note: 该功能在 V2.1.47 中短暂上线后被从 Changelog 移除（代码未删），Reddit 社区确认已被暂时下线。本文记录其设计理念与使用方式，供后续功能正式发布时参考。
---

# Claude Code Workflow 功能 - 脚本化多 Agent 编排

> 深度解析 Anthropic 在 Claude Code V2.1.47/V2.1.48 中秘密新增的 Workflow 功能。这个被官方从 Changelog 中紧急删除却未从代码中移除的隐藏功能，用 JavaScript 脚本替代 LLM 作为编排器，实现了多 Agent 的精准可控协同。适合 Claude Code 高级用户和关注 AI Agent 编排的开发者。

## 目录

- [[#功能背景与状态]]
- [[#启用方式]]
- [[#核心设计理念]]
- [[#功能演示：PR 深度 Review]]
- [[#Workflow vs Subagents vs Agent Teams vs Skills]]
- [[#Workflow 脚本结构]]
- [[#六种工作流形态]]
- [[#实战：Deep Research 脚本复用]]
- [[#脚本持久化方案]]
- [[#总结]]

---

## 功能背景与状态

### 功能定位

Anthropic 为 Claude Code 新增的 Workflow 功能，定位为继 MCP 和 Skills 之后的又一个划时代创新。

### 当前状态

```
时间线：
  V2.1.47 → Workflow 首次上线，出现在 Changelog
  V2.1.47 稍后 → Anthropic 从 Changelog 中紧急删除 Workflow 介绍
  V2.1.48 → 功能仍存在于代码中，可正常使用
  当前 → 社区确认已被暂时下线（代码未删）
```

> **注意**：截至记录时间，该功能已从官方 Changelog 中移除，Reddit 社区确认 `/workflows` 已被暂时下线。但代码层面尚未删除，可能在未来版本中正式发布。

---

## 启用方式

### 步骤

```bash
# Step 1: 设置环境变量启用 Workflow
export ENABLE_WORKFLOWS=true

# Step 2: 启动 Claude Code
claude

# Step 3: 使用 ultrawork 关键词触发 Workflow 模式
ultrawork <任务描述>
```

### 触发关键词

在 Claude Code 中输入 `ultrawork` 后，文字会变成彩色并具备渐变效果，表示已进入 Workflow 模式。后续跟上任务描述即可。

### 查看进度

```
# 在 Claude Code 中输入斜杠命令
/workflows

# 可查看：
# - 各阶段执行状态
# - 各 Agent 运行时长
# - Token 消耗
# - 工具调用记录
# 用方向键选择进入查看详情，按 ESC 退出
```

---

## 核心设计理念

### 核心问题：Subagent 的 Token Tax

```
传统多 Agent 模式（Subagents / Agent Teams）：

  主 Agent (LLM) ──派生──→ Sub-Agent #1 ──结果回到──→ 主 Agent 上下文
       │                                                      │ Token 膨胀
       ├──派生──→ Sub-Agent #2 ──结果回到──────────────────────┤ 上下文越来越满
       │                                                      │ 模型越来越「健忘」
       └──派生──→ Sub-Agent #N ──结果回到──────────────────────┘

  问题：10 个 Agent 的结果全部回到主 Agent 上下文
  → Token 快速积累
  → 模型随着上下文窗口填充变得「更马虎、更健忘」
```

### Workflow 的解决方案：代码做编排

```
Workflow 模式：

  Claude Code 先编写 workflow.js 脚本（不是直接执行）
       │
       ▼
  脚本定义阶段（Phases）和 Agent
       │
       ▼
  Agent 输出直接在阶段间流动，不经过主上下文
       │
       ▼
  主会话保持空闲，只接收最终报告

  核心原则：
  "用代码做代码擅长的事（控制流），
   用模型做模型擅长的事（每个步骤内的判断）"
```

### 三大特性

| 特性 | 说明 |
|------|------|
| 可观测 (Observable) | 通过 `/workflows` 实时查看每个 Agent 的状态、token 消耗、工具调用 |
| 可验证 (Verifiable) | 结构化 Schema 定义每个阶段的预期输出 |
| 可复跑 (Reproducible) | 脚本可分享、可修改、可重复执行 |

---

## 功能演示：PR 深度 Review

### 实战流程

```
ultrawork 为当前 PR 生成一个 multi-agent 的 review workflow 并运行
  │
  ▼
Claude Code 设计并启动 Workflow
  │  1. 生成 context.md（项目上下文）
  │  2. 编写 workflow.js 脚本（~300 行 JS）
  │
  ▼
Workflow 启动 → 并行运行 6 个专业审查者 Agent
  │
  ▼
97 个 Agent 调用完成 → 生成 Review 报告
```

### 运行数据

- 脚本代码量：约 300 行 JavaScript
- Agent 调用次数：97 次
- 三个阶段：Review → 验证 → 编写报告

---

## Workflow vs Subagents vs Agent Teams vs Skills

### 四者对比

| 维度 | Subagents | Agent Teams | Workflow | Skills |
|------|-----------|-------------|----------|--------|
| **编排方式** | 自然语言描述 | 多角色并行对话 | JS 脚本代码 | Prompt 模板 |
| **编排者** | LLM（主 Agent） | 人类主导调度 | 代码（非 LLM） | 模型自动发现 |
| **Agent 输出** | 回到主上下文 | 回到共享会话 | 在阶段间直接流动 | 单次执行 |
| **可复用性** | 低（每次重新生成） | 低 | 高（脚本可分享） | 高（封装复用） |
| **可观测性** | 有限 | 中等 | 完整（/workflows 面板） | 无专门面板 |
| **精准控制** | 低（LLM 临场决策） | 中（人类干预） | 高（代码定义） | 低（Prompt 引导） |
| **启动成本** | 极低（自然语言） | 低 | 中（需编写脚本） | 低（自动触发） |
| **核心价值** | 快速拆分任务 | 多角色协作 | 工程化编排 | 封装技能知识 |

### 一句话总结

```
Subagents  = 「帮我做个东西」 → LLM 自己决定怎么派活
Agent Teams = 「你们几个一起讨论」 → 人类参与调度
Workflow    = 「按这个脚本跑」 → 代码精确控制每一步
Skills      = 「遇到这种情况用这招」 → 模型自动触发
```

### Workflow 与 Skills 的本质差异

```
Skills：把某项技能封装成 Prompt 模板
  → 告诉大模型什么时候用、怎么用、有什么限制
  → 模型自动发现并自动使用
  → 核心载体：Prompt

Workflow：把多 Agent 编排固化成 JS 脚本
  → 用代码定义 Agent、阶段、数据流
  → 需要用 ultrawork 关键词触发
  → 核心载体：JavaScript 代码

  Skills = 怎么做某件事（知识封装）
  Workflow = 怎么协调多个 Agent（流程编排）
```

---

## Workflow 脚本结构

### 最小化代码（三要素）

```javascript
// 1. 元数据（Metadata）— 名称和描述必填
const workflow = {
  name: "minimal-workflow",
  description: "A minimal workflow example",

  // 2. Agent 方法 — 至少调用一次
  agent: async (ctx) => {
    const result = await ctx.agent("do something");
    // 3. 传递结果 — 必须 retain
    ctx.retain(result);
  }
};

export default workflow;
```

### 完整脚本结构

```javascript
const workflow = {
  // 元数据
  name: "multi-agent-review",
  description: "多 Agent 深度代码审查",

  // 定义阶段（Phases）
  phases: [
    {
      name: "review",
      description: "并行审查",
      // 并行 fan-out 多个 Agent
      agents: ["安全审查者", "性能审查者", "架构审查者", ...]
    },
    {
      name: "verify",
      description: "交叉验证",
      agents: ["验证者"]
    },
    {
      name: "report",
      description: "生成报告",
      agents: ["报告生成者"]
    }
  ],

  // Agent 执行逻辑
  agent: async (ctx) => {
    // 调用 Agent 并传递上下文
    const reviewResult = await ctx.agent(reviewPrompt);
    ctx.retain(reviewResult);
  }
};

export default workflow;
```

### 脚本路径

| 路径类型 | 位置 | 生命周期 |
|----------|------|---------|
| 临时生成路径 | Claude Code 默认输出目录 | 3 天（自动清理） |
| 用户级持久化 | `~/.claude/workflows/` | 永久（需手动复制） |

```bash
# 将成熟脚本复制到持久化路径（在 Claude Code 中）
ultrawork 将这个脚本复制到 ~/.claude/workflows/ 目录下

# 查看可用脚本
/workflows
```

---

## 六种工作流形态

| # | 形态 | 说明 | 适用场景 |
|---|------|------|---------|
| 1 | **流水线 (Pipeline)** | 阶段顺序执行，前一步输出流入下一步 | 代码审查、文档生成 |
| 2 | **同步聚合 (Fan-in)** | 多个 Agent 并行执行，结果汇总 | 多维度代码分析 |
| 3 | **对抗验证 (Adversarial)** | Agent 之间互相检验，找出问题 | 安全审计、质量保障 |
| 4 | **评委模式 (Judge)** | 多个 Agent 提出方案，由评委 Agent 选优 | 设计方案探索 |
| 5 | **累积式 (Accumulative)** | Agent 逐步积累结果，渐进增强 | Deep Research |
| 6 | **嵌套式 (Nested)** | Workflow 内嵌套子 Workflow | 复杂多阶段项目 |

---

## 实战：Deep Research 脚本复用

### 应用场景

通过编写一个 Deep Research 的 Workflow 脚本，实现 Harness Engineering（装备工程）的技术调研。

### 脚本结构

```
Deep Research Workflow
  │
  ├── Phase 1: 搜索
  │   ├── Agent: 研究官方文档
  │   ├── Agent: 研究论文
  │   └── Agent: 研究社区讨论
  │
  ├── Phase 2: 验证
  │   └── Agent: 交叉验证搜索结果
  │
  └── Phase 3: 合成报告
      └── Agent: 生成中文报告
```

### 使用流程

```
1. 首次使用 → Claude Code 自动生成脚本
   ultrawork 深度研究 Harness Engineering

2. 后续复用 → 直接调用已有脚本
   ultrawork 调用 deep-research 脚本研究 [新主题]

3. 查看进度
   /workflows → 选择阶段 → 查看各 Agent 状态
```

### 高级集成：Codex CLI

```
通过 Workflow 脚本调用 Codex CLI 实现精准代码 Review
  │
  ├─ 优势：比开源的 Codex Review Skill/插件更精准
  ├─ 原因：JS 脚本可以精确控制 Codex 的调用方式
  └─ 场景：生产级代码审查
```

---

## 总结

### Workflow 的本质价值

```
从「模型临场建议」到「代码化控制流」

  之前：
    人类 → 自然语言 → LLM 临场决定 → Subagents → 结果回主上下文
    （不可控、不可复用、Token 膨胀）

  Workflow：
    人类 → 编写 JS 脚本 → 代码定义流程 → Agents 按脚本执行
    （精准可控、可观测、可复跑、Token 隔离）
```

### 未来展望

> 未来 GitHub 上必将涌现成千上万的开源 Workflow 脚本，如同 MCP 和 Skills 的生态一样。

### 最佳实践清单

- ✅ 用 Workflow 处理需要精确控制的多 Agent 协作任务
- ✅ 成熟脚本迁移到 `~/.claude/workflows/` 持久化
- ✅ 用 `/workflows` 命令监控 Agent 执行状态
- ✅ 脚本定义结构化 Schema，让阶段输出可预测
- ✅ 善用脚本分享，团队复用跑通的工作流
- ❌ 不要让 Subagent 结果全部回到主上下文（Token Tax）
- ❌ 简单任务不需要 Workflow（Subagents 就够）
- ❌ 不要忽视脚本的 3 天自动清理机制（记得持久化）

### 关键概念速查表

| 概念 | 说明 |
|------|------|
| `ultrawork` | Claude Code 中触发 Workflow 的关键词 |
| `/workflows` | 查看正在运行的 Workflow 进度的斜杠命令 |
| `workflow.js` | Claude Code 自动生成的 JS 工作流脚本 |
| Token Tax | Subagent 结果回到主上下文导致的 Token 膨胀 |
| Phases | Workflow 中的执行阶段 |
| Agent 方法 | 脚本中定义 Agent 执行逻辑的函数 |
| `ctx.retain()` | 将 Agent 结果传递到下一阶段 |
| Harness Engineering | 利用 Workflow 脚本复用实现工程化调研 |

---

## 参考资料

- [Reddit: Claude Code dropped /workflows](https://www.reddit.com/r/ClaudeCode/comments/1tkjy4u/claude_code_dropped_workflows/) - 社区讨论与功能状态确认
- [Claude Code Workflows (GitHub)](https://github.com/shinpr/claude-code-workflows) - 生产级 Workflow 脚本集合
- [Anthropic: Enabling Claude Code to Work More Autonomously](https://www.anthropic.com/news/enabling-claude-code-to-work-more-autonomously) - Anthropic 官方博客
- [win4r GitHub](https://github.com/win4r/) - 视频作者开源项目

## 相关笔记

- [[../100-InBox/AI-WORKFLOW/2026 開發者藍圖 - 自主代理時代的編碼革命]]
- [[Claude Code]]
- [[MCP]]
- [[AI Agent]]
