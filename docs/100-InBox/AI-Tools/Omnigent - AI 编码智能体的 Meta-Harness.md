---
title: Omnigent — AI 编码智能体的 Meta-Harness 元编排层
aliases: [Omnigent, OmnigentAI, meta-harness]
tags:
  - ai-agent
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=oGE_Dwz-rMk"
  - "https://github.com/omnigent-ai/omnigent"
author: Cole Medin（频道）/ Databricks（项目方）
created: 2026-06-28
updated: 2026-06-28
description: |
  Databricks 开源的 Omnigent 是一个 Meta-Harness 元编排层，让 Claude Code、Codex、Pi 等多个 AI 编码智能体在同一环境中协作，支持跨设备、Guardrails 和沙盒隔离。
level: intermediate
stars: 4
---

# Omnigent — AI 编码智能体的 Meta-Harness 元编排层

> Omnigent 是 Databricks（CTO Matei Zaharia 主导）开源的 AI 智能体编排框架（Apache 2.0）。它作为「Harness 之上的 Harness」，让 Claude Code、Codex、Pi 等多个编码智能体在同一 session 中无缝协作，无需手动切换终端或撰写交接文档。适合多模型工作流、团队协作、生产环境安全管控的开发者。

## 目录

- [[#为什么需要 Meta-Harness]]
- [[#Omnigent 核心架构]]
- [[#实战：Polly 与 Debby 工作流]]
- [[#自定义智能体与 Guardrails]]
- [[#跨设备协作与部署]]
- [[#与现有方案的定位对比]]
- [[#行动建议]]

---

## 为什么需要 Meta-Harness

### 核心洞察：Harness（架）> 模型（Model）

顶级工程师的工作方式正在从「等待更好的模型」转向「建立更好的系统」。即使在 Fable 5 被封禁的背景下，这个趋势更加明显——**既然 LLM 不一定越来越好，那就让 LLM 周围的系统更强**。

```
传统思路：模型升级 → 产出提升
新思路：  模型不变 + 系统编排升级 → 产出大幅提升
```

### Harness 的构成

Harness 是将模型变成可用 Agent 的完整包装，包含：

| 组成部分 | 说明 |
|---------|------|
| System Prompt | 系统提示词，定义 Agent 行为边界 |
| Tools | 可调用的工具集（文件、终端、浏览器等） |
| Skills | 可重用的工作流（Workflow） |
| Rules | 约束和规则（如不可删除 prod 数据库） |
| Workflows | 多步骤编排流程 |

### 单一模型的局限性

不同 AI 编码助手各有所长：

| 助手 | 擅长领域 | 典型用途 |
|------|---------|---------|
| Claude Code | 代码实现（Implementation） | 写功能、修 Bug |
| Codex | 代码审查（Code Review） | 审查、质量把控 |
| Kimi / Minimax | 简单任务 | 快速问答、格式转换 |

**没有 Meta-Harness 的痛点**：
- ❌ 多个终端来回切换
- ❌ 手动创建交接文档（Handoff Documents）
- ❌ 上下文（Context）和 Token 优化困难
- ❌ 每个 Agent 独立配置，无法统一治理

---

## Omnigent 核心架构

### 项目背景

- **来源**：Databricks 开源，CTO Matei Zaharia 推动
- **协议**：Apache 2.0
- **内部使用**：Databricks 自身 5000 人工程团队已在日常使用
- **GitHub**：`github.com/omnigent-ai/omnigent`（4.7k+ Stars）
- **技术栈**：Neon

### 架构分层

```
┌──────────────────────────────────────────────┐
│              Omnigent（Meta-Harness）          │
│  ┌──────────────────────────────────────────┐ │
│  │   Orchestrator（编排器）                   │ │
│  │   + Configuration + Skills + Agents       │ │
│  └──────────────────────────────────────────┘ │
│                    │ 调度                      │
│  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────────┐  │
│  │Claude│  │Codex │  │  Pi  │  │Custom    │  │
│  │Code  │  │      │  │      │  │Agents    │  │
│  └──────┘  └──────┘  └──────┘  └──────────┘  │
│       Sandbox / Docker / E2B（可选隔离层）       │
└──────────────────────────────────────────────┘
           │
     ┌─────┼─────┐
     │     │     │
  Terminal  App  Web UI
         REST API
         手机端
```

### 三大基础组件（Primitives）

每个 Orchestrator 由三部分组成：

**1. Configuration（配置）**

```yaml
orchestrator:
  executor: claude        # 顶层使用的核心 LLM
  system_prompt: "..."   # 长系统提示词
  sandbox: docker/e2b    # 沙盒环境（可选）
  tools: [...]           # 可用工具列表
```

| 配置项 | 说明 |
|--------|------|
| Executor | Orchestrator 自身使用的 LLM（不调用 Agent 时） |
| System Prompt | 编排逻辑的指令集 |
| Sandbox | 无沙盒 / Docker / E2B 等生产级隔离 |
| Guardrails | Human-in-the-loop 审批规则 |
| Tools | 声明可委派工作的 Agent 列表 |

**2. Skills（技能）**

可重用的跨 Agent 工作流指南。例如 Poly 的 `cross-review` skill 定义了「实现 → 审查」的完整流程。

**3. Agents（智能体）**

底层可调度的 AI 编码助手，每个 Agent 也有自己的独立配置（System Prompt、Executor、Tools、Guardrails）。

### 多接口接入

| 接入方式 | 适用场景 |
|---------|---------|
| Native App | 桌面日常使用 |
| REST API | 程序化集成 |
| Terminal（终端） | Claude Code 风格交互 |
| Web UI | 浏览器/手机访问 |

### 凭证继承

Omnigent 运行在本地机器上，**自动继承已登录的 CLI 凭证**。已装好 Claude Code / Codex / Pi CLI 的用户，无需重新认证。

---

## 实战：Polly 与 Debby 工作流

### Polly — 实现与审查分离（Cross-Review）

Polly 是 Omnigent 内置的编排器，展示最经典的 Meta-Harness 用途：

```
用户请求
    │
    ▼
Polly（Orchestrator）
    │ 加载 context + cross-review skill
    │
    ├──→ Claude Code（Implementation）
    │        │ 在独立 git worktree 中编写代码
    │        │
    │        ▼ 产出 diff
    │
    └──→ Codex（Review）
             │ 独立 session 审查代码
             │ 避免 Implementation 的偏见
             │
             ▼ 审查报告
```

**关键原则**：在**不同的 Agent Session** 中进行实现和审查。如果让同一个 LLM 既写又审，会累积过多偏见。

| 工作流 | 优势 |
|--------|------|
| Claude 实现 + Codex 审查 | ✅ 审查者对实现代码无先入为主偏见 |
| 同一 LLM 写 + 审 | ❌ 审查会倾向于"我写的代码是对的" |
| Git Worktree 隔离 | ✅ 实现过程不影响主分支，可并行开发 |

### Debby — 多模型辩论（Multi-Model Debate）

Debby 展示了 Omnigent 超越纯编码场景的潜力：

```
用户提出问题
    │
    ▼
Debby（Orchestrator）
    │
    ├──→ Claude（正方立场）
    │        │ 第一轮论证
    │        ▼
    ├──→ GPT（反方立场）
    │        │ 第一轮反驳
    │        ▼
    │
    │  第二轮：互给 feedback
    │
    ├──→ Claude ←── GPT 的反馈
    │        │ 第二轮回应
    │        ▼
    ├──→ GPT   ←── Claude 的反馈
    │        │ 第二轮回应
    │        ▼
    │
    └──→ Debby 综合双方观点
             │ 输出最终答案
             ▼
```

**适用场景**：技术方案选型、架构决策、产品策略评估等需要多角度论证的场景。

---

## 自定义智能体与 Guardrails

### 构建自定义 Agent

Omnigent 的 Agent 配置用 YAML 定义，整个流程甚至可以让 AI 编码助手自动生成。视频演示中，博主直接让 Claude Code 参考 Polly 和 Debby 的配置模板生成了自定义 Agent。

### Guardrails（安全防护）

用 Python 代码定义策略，放在 Agent 配置旁边：

```python
# 示例：阻止 force push，其他操作自动放行
def policy(command):
    if "git push" in command and "--force" in command:
        return "BLOCKED"  # 需要人工审批
    return "ALLOWED"
```

| 操作级别 | 示例 | 处理方式 |
|---------|------|---------|
| 低风险 | `git status`、`ls` | 自动放行 |
| 高风险 | `git push --force` | 暂停等待人工确认 |
| 极高风险 | `DROP TABLE` | 永久拦截 |

### Human-in-the-Loop 工作流

```
Agent 请求执行高风险操作
    │
    ▼
Guardrail 拦截
    │
    ▼
通知用户："需要审批：git push --force"
    │
    ├── 用户批准 → 继续执行
    └── 用户拒绝 → 中止/修改
```

**对比传统方案**：

| 特性 | Claude Code Hooks | Omnigent Guardrails |
|------|------------------|---------------------|
| 适用范围 | 仅 Claude Code | 所有 Agent（Claude、Codex、Pi 等） |
| 配置方式 | Bash 脚本 | Python 策略文件 |
| 统一治理 | ❌ 每个工具单独配 | ✅ 一套规则覆盖全部 |

---

## 跨设备协作与部署

### 本地多设备同步

同一 Wi-Fi 下，手机 Web UI 输入的消息即时同步到桌面端——开发者在不同空间切换时保持对话上下文。

### 远程协作

部署到服务器后，session 可从任何设备访问：

```
┌─────────────┐       ┌──────────────┐
│ 手机 Web UI  │───────│              │
└─────────────┘       │  Omnigent    │
┌─────────────┐       │  Server      │
│ 桌面 Terminal│───────│  (LAN/WAN)   │
└─────────────┘       │              │
┌─────────────┐       │              │
│ REST API     │───────┘              │
└─────────────┘
```

| 部署方式 | 适用场景 |
|---------|---------|
| 本地运行 | 个人开发 |
| 局域网共享 | 小团队同办公室 |
| 云端部署 | 跨地域远程协作 |

---

## 与现有方案的定位对比

### 判断决策树

```
你需要什么？
│
├─ 调度多个不同模型的编码 Agent
│   ├─ 需要统一治理 + Guardrails → Omnigent
│   └─ 简单任务切换 → 手动切换即可
│
├─ 单模型深度使用
│   ├─ Claude Code 独立工作 → 直接用 Claude Code
│   └─ Codex 独立工作 → 直接用 Codex
│
└─ 构建自定义 Agent 框架
    ├─ 生产级 + 多模型编排 → Omnigent
    └─ 完全自定义 → 自建框架
```

### Omnigent vs 其他方案

| 特性 | Omnigent | Hermes Agent | Claude Code |
|------|---------|-------------|-------------|
| 多模型编排 | ✅ 核心功能 | ✅ 多 provider | ❌ 单模型 |
| Guardrails | ✅ Python 策略 | ✅ config 层级 | ⚠️ Hooks |
| 跨设备协作 | ✅ Web/手机/终端 | ✅ 多平台 | ❌ 仅终端 |
| 沙盒隔离 | ✅ Docker/E2B | ❌ | ❌ |
| Session 共享 | ✅ 多人同 session | ❌ 单人 | ❌ 单人 |
| 开源 | ✅ Apache 2.0 | ✅ 开源 | ❌ 闭源 |
| Git Worktree | ✅ 内置 | ❌ | ✅ 支持 |

---

## 行动建议

### 立即可做

1. **本地试用**：将 GitHub 仓库丢给现有 AI 编码助手，一条指令即可完成安装
2. **体验 Polly 工作流**：用 Claude Code 实现 + Codex 审查，感受跨模型协作
3. **自定义 Agent**：参考 `examples/polly/` 配置模板，按团队需求定制

### 进阶实践

4. **配置 Guardrails**：对 `git push --force`、数据库删除等高风险操作设置人工审批
5. **部署到服务器**：启用跨设备/跨地域协作
6. **构建团队级 Orchestrator**：按项目需求设计专属编排流程

### 最佳实践清单

- ✅ 实现和审查使用不同 Agent Session
- ✅ 利用 Git Worktree 隔离并行开发
- ✅ 为高风险操作配置 Guardrails
- ✅ Orchestrator 级别统一配置，避免每个 Agent 重复设置
- ❌ 不要让同一 LLM 在同一 session 中既写又审
- ❌ 不要在无沙盒的环境下让 Agent 直接操作生产代码

---

## 参考资料

- [Omnigent GitHub 仓库](https://github.com/omnigent-ai/omnigent)
- [Databricks 官方博客介绍](https://www.marktechpost.com/2026/06/13/databricks-open-sources-omnigent-a-meta-harness-that-composes-governs-and-shares-ai-agents-across-claude-code-codex-and-pi)
- [Heise Online 报道](https://www.heise.de/en/news/Meta-Harness-for-AI-Agents-Databricks-Releases-Omnigent-as-Open-Source-11335496.html)
- [Cole Medin 原视频](https://www.youtube.com/watch?v=oGE_Dwz-rMk)

## 相关笔记

- [[AI Agent]]
- [[Claude Code]]
- [[Codex CLI]]
- [[Agentic Coding]]

---

*文档生成时间：2026-06-28*
*基于 Omnigent v0.1（刚发布的开源项目）*
