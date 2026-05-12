---
title: 2026 AI Coding Agent 全景 - Hermes Agent + OpenHands 实战
aliases:
  - AI Coding Agent 比较
  - OpenHands Claude Code Codex AgentGravity
tags:
  - ai-agents
  - coding-agent
  - hermes-agent
  - openhands
  - claude-code
  - codex
  - status/active
  - type/study-note
source:
  - "https://www.youtube.com/watch?v=I9Ttb3Jz3TM"
author: YouTube 粤语科技频道
created: 2025-05-12
updated: 2025-05-12
description: 2026 年 AI Coding Agent 全景对比（OpenHands/AgentGravity/Claude Code/Codex），以及 Hermes Agent + OpenHands 实战自动化工作流
level: intermediate
stars: 4
---

# 2026 AI Coding Agent 全景 - Hermes Agent + OpenHands 实战

> 视频全面梳理 2026 年 AI Coding Agent 生态，对比四大主流工具的设计哲学与适用场景，并以 Hermes Agent + OpenHands 为例演示如何构建"睡觉时 AI 帮你写程序"的自动化工作流。

---

## 目录

1. [AI Agent 生态全景](#ai-agent-生态全景)
2. [四大 AI Coding Agent 对比](#四大-ai-coding-agent-对比)
3. [Hermes Agent + OpenHands 实战](#hermes-agent--openhands-实战)
4. [Subagent-Driven Development 工作流](#subagent-driven-development-工作流)
5. [成本优化与常见陷阱](#成本优化与常见陷阱)
6. [行业趋势与安全警示](#行业趋势与安全警示)
7. [参考资料](#参考资料)

---

## AI Agent 生态全景

围绕 OpenClaw（34 万+ GitHub Stars），已衍生出不同定位的替代品和进化版：

### 五大高自主 Agent 对比

| Agent | 语言/核心 | 启动资源 | 定位 | Stars |
|-------|----------|---------|------|-------|
| **NanoClaw** | ~500 行 Shell Script | 独立容器 | 安全加固版 OpenClaw | - |
| **ZeroClaw** | Rust | 启动 10ms，RAM <5MB | 极致轻量、供应商无关 | 3 万+ |
| **MIR / ICal** | 专用语言 | $5 嵌入式芯片 | 嵌入式 AI，无 OS/JS | - |
| **Hermes Agent** | Python | 云端部署 | 长时间自主 Agent，自我学习 | 6 万+ |

### Hermes Agent 核心特性

```
Hermes Agent（高自主、云优先）
├── 自我学习：完成任务后自动回顾 → 提取模式 → 存为持久 Skill
├── 20+ 通讯平台：Telegram / Discord / WhatsApp / Signal / Email / CLI
├── 记忆系统：越用越聪明，重复任务 Token 成本递减
└── 独立于本地 OS：设计原则，非附加功能
```

**设计哲学对比**：

```
OpenClaw:  绑死在 laptop 上的 Coding Copilot
App 集成:  某个 App 的 Track 包装
Hermes:   住在服务器上的自主 Agent，记得学过的东西
```

---

## 四大 AI Coding Agent 对比

### 1. OpenHands（原 OpenDevin）

```
定位: 开源平台，让 AI Agent 像人类软件工程师一样工作
执行: 指令、网络、调用 API 全自动化
成绩: SWE-Bench 上解决 70%+ 真实 GitHub Issue
融资: $1880 万（Allied Computing）
许可: 开源 MIT，可用任何 LLM 驱动
```

### 2. Google AgentGravity（Gemini 2.1）

```
发布: 2025 年随 Gemini 2.1 推出
理念: Agent First — IDE 不是帮你打字的，是指挥 AI 自主工作的
基础: Visual Studio Code 深度 Fork
```

**双视图架构**：

```
+---------------------------+---------------------------+
|     CodeView              |     AgentView             |
|  传统代码编辑界面           |     控制中心               |
|  + 侧边栏                 |  同时派 5 个 Agent 处理     |
|                           |  5 个不同任务              |
|                           |  Agent 异步运行            |
+---------------------------+---------------------------+
```

**Artifact 机制**（关键创新）：
- Agent 完成后不只是给 code diff
- 生成：任务清单 → 实作计划 → 截图 → 录影
- 一眼就能验证 Agent 做的对不对
- 直接在 artifact 上留 review，自动 incorporate feedback

**模型支持**：Gemini 2.1 Pro / Sonnet 4.6 / Claude Opus 4.6 / 开源 GPT 等

### 3. Claude Code（Anthropic）

```
定位: 终端原生工具，直接在你 project 里面跑
特色: 紧密交互 — 每一步都可以商量
适用: 喜欢在 terminal 工作、保持对每个细节控制的开发者
缺点: 不会在后台长时间运行任务
```

### 4. Codex CLI（OpenAI）

```
注意: 不是 2021 年的 Codex，是 2025 年重新推出的同名 Agent 产品
定位: 端点优先（Endpoint-First）
模式: 委派工作 — 下完指令就走人
规模: 200 万+ 每周活跃用户
专用模型: GPT-5.x（专为 Agentic Coding 优化）
```

**与 GitHub 集成**：直接由 Issue 或 PR 触发，完成后自动提交。

### 四大 Agent 设计哲学总结

```
OpenHands      = 开源的瑞士军刀（拥有权 + 弹性）
AgentGravity   = 心脏（适合大型项目，多线并行推进）
Claude Code    = 终端里的高手 Pair Programmer（精准控制）
Codex          = 扔了就走的委派模式（后台批量处理）
```

**不是零和关系**：很多用户混用，如 OpenHands 处理后端 + Claude Code 做 UI。

---

## Hermes Agent + OpenHands 实战

### 三种集成方式

**方式 1：delegate_task（推荐入门）**

```bash
# 在 Hermes 对话中直接委派
# Hermes 会 spawn 一个有独立 context 和 terminal 的子 Agent
# 子 Agent 在 OpenHands sandbox 里执行，完成后汇报
```

```
+------------------+     delegate_task     +------------------+
|  Hermes Agent    | ───────────────────→ |  OpenHands       |
|  (大脑/协调者)    | ←─────────────────── |  (双手/执行者)    |
|  记忆 + 规划 + 排程|      返回结果         |  Docker sandbox   |
+------------------+                      +------------------+
```

**特点**：Parent Agent 同步阻塞等待子 Agent 完成。

**方式 2：Skill 系统（推荐长期使用）**

```yaml
# Hermes skill 文件示例
# 定义何时使用 OpenHands + 如何构造 prompt
# 一次配置，之后重复使用
# Hermes 自动改进 skill（self-improvement）
# 兼容 Anthropic Claude Code skills 格式
```

**方式 3：SDK 程序化集成（最灵活）**

```python
from openhands import Agent, Conversation
from openhands.tools import FileEditorTool, TaskTrackerTool

# 组装 Agent + 工具 + 对话
# 通过 Hermes skill handler 执行
```

### OpenHands 执行环境

```
OpenHands 支持的 Backend
├── Terminal
├── Browser
├── 本地 Docker
├── SSH
├── Singularity
├── Modal
├── E2B
└── RunOS
```

---

## Subagent-Driven Development 工作流

Hermes 官方推荐的 `subagent-driven-development` 模式：

```
用户（Telegram 发消息）
    │
    ▼
Hermes 主 Agent（读取 plan → 提出 task → 建立 todo list）
    │
    ├──→ SubAgent 1: box（在 Docker 里写代码）
    ├──→ SubAgent 2: spec-review（审查是否符合 spec）
    ├──→ SubAgent 3: quality（质量检查）
    │
    ▼
当前 task 完成 → 下一个 task → 最终跑全部测试
```

**核心原则**：每个 task 用一个新的 page，两阶段审查（先审 spec 再审 quality）。

---

## 成本优化与常见陷阱

### 成本策略

| 角色 | 推荐模型 | 理由 |
|------|---------|------|
| Hermes 主 Agent | 便宜模型（Haiku 3.6 等） | 规划/排程不需要最强推理 |
| 子 Agent Worker | 强模型（Claude Sonnet 4.5 等） | 实际编码需要高质量输出 |

**效果**：混合使用可节省 50%-80% 成本。

**关键参数**：

```
concurrent_children: 3     # 同时最多跑 3 个任务
timeout: 1800s             # 建议切长，OpenHands 编码任务可能耗时
max_spawn_depth: 2         # 允许两层嵌套深度
```

### 五大陷阱

| # | 陷阱 | 后果 | 解决方案 |
|---|------|------|---------|
| 1 | delegate_task 是同步的 | 父 Agent 阻塞，中断则子 Agent 全部丢失 | 长任务用 background terminal 或 cron job |
| 2 | 工作目录未指定 | OpenHands 可能在错误项目里操作 | delegate 内明确指定绝对路径 |
| 3 | 双 LLM 成本叠加 | Hermes + OpenHands = 2 倍模型调用 | 主 Agent 用便宜模型 + 重复任务做成 skill |
| 4 | 并行任务文件冲突 | 多个子 Agent 改同一文件 | 用不同 git 分支或 git worktree |
| 5 | 首次直接上生产 | 移动部分、失败案例未验证 | 先手动 delegation 测试，确认稳定再自动化 |

### 何时不用 Hermes + OpenHands

**直接打开 Claude Code / Codex 的场景**：
- 正坐在键盘前，准备交互式编码
- 只需要单任务、短时间完成

**适合 Hermes + OpenHands 的场景**：
- 不在电脑前（睡觉、外出）
- 排程任务（cron job）
- 作为更大自主工作流的一部分
- 从 Telegram 收到指令后异步执行

---

## 行业趋势与安全警示

### SWE-Bench 准确率变化

```
AI Agent 准确率: ~12% → 66%（接近人类 68%）
```

### Arena Ratings 四大厂商

```
Anthropic:  1503 分
OpenAI:     1495 分
Google:     1494 分
xAI:        1481 分
差距: 仅 20+ 分
```

**关键洞察**：模型能力开始收敛，真正的差异来自工作流编排、协调性和记忆。

### 安全警示

```
SecurityScorec 报告:
- 数千个公开暴露的 OpenClaw 实例
- CVSS 评分高达 8.8 的安全漏洞

必要安全措施:
- NanoClaw 的物理隔离
- Claude Code 的 capability boundary
- Hermes 的 container hardening + namespace isolation
```

**结论**：好的 AI 工作不只是跑得快，还要跑得安全。

---

## 最终选型建议

| 需求 | 推荐 |
|------|------|
| 本地 24/7 全天候 AI 助手 | OpenHands / Claude Code / Hermes |
| 交互式开发助手 | Claude Code / Codex |
| 最强自动化（离开电脑后 AI 继续写 PR） | **Hermes Agent（大脑）+ OpenHands（工人）** |
| 大型项目多线并行 | Google AgentGravity |

---

## 参考资料

- [OpenHands GitHub](https://github.com/All-Hands-AI/OpenHands) - 开源 AI 软件工程师平台
- [Google AgentGravity](https://developers.google.com/) - Agent First IDE
- [Hermes Agent](https://hermes-agent.nousresearch.com/) - 自主 AI Agent 框架

## 相关笔记

- [[Anthropic Managed Agents - 解耦大脑与双手的 Agent 操作系统]]
