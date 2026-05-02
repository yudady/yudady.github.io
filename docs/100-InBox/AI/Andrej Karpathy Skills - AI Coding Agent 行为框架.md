---
title: Andrej Karpathy Skills - AI Coding Agent 行为框架
created: 2026-04-13
source: https://www.youtube.com/watch?v=PzhTLHQfdRE
tags:
  - ai
  - coding-agent
  - prompt-engineering
  - claude
---

# Andrej Karpathy Skills - AI Coding Agent 行为框架

## 概述

[Andrej Karpathy Skills](https://github.com/forest-chang/andrej-karpathy-skills) 是一个轻量级指令层，基于 Andrej Karpathy 对 AI coding agent 常见失败模式的观察，旨在让 AI coding agent 行为更像谨慎的工程师，而非过度自信的写手。

核心思路：不是让模型更聪明，而是让工作流更可靠 — **装的是纪律，不是功能**。

## 四大原则

### 1. Think Before Coding（先思考再写码）

Agent 不应盲目猜测意图。遇到模糊需求时，应该：
- 暴露歧义，主动提问
- 展示 trade-off，而非直接开干

### 2. Simplicity First（简单优先）

- 只写解决问题所需的最少代码
- 不做推测性抽象
- 不为一个函数任务搭建整套框架
- 不为炫技而过度设计

### 3. Surgical Changes（精准修改）

- 只碰任务相关的代码
- 不随意清理无关代码
- 不重写无关注释
- 不重构相邻函数
- 不改进不在需求范围内的东西

### 4. Goal-Driven Execution（目标驱动执行）

- 把模糊需求转化为可验证的结果
- 流程：复现问题 → 修复 → 验证 → 停止
- 明确成功标准，而非"修完了 hope it works"

## 安装方式

**方式一：Claude Code 插件（推荐）**
```bash
claude plugin marketplace add forest-chang/andrej-karpathy-skills
claude plugin install andrej-karpathy-skills@karpathy-skills
```

**方式二：项目级 CLAUDE.md**
直接将仓库中的 `CLAUDE.md` 下载到项目根目录，或追加到已有的 `CLAUDE.md`。

## 使用模式

安装后无需额外操作，它会改变 agent 的默认行为。以"加一个计费仪表板"为例：

| 无指导原则 | 有指导原则 |
|---|---|
| 直接开写，一次创建表、API、webhook、UI 组件、验证、设置页 | 先澄清范围：一次性支付还是订阅？用哪个 provider？全功能还是只读摘要？最小可行版本是什么？ |
| 巨大 diff，难以审查 | 小而聚焦的 diff |
| 可能随机重构无关文件 | 只改必要的文件 |
| "我实现了"就结束 | 明确验证结果后才结束 |

## 效果判断指标

- Agent 开始问更好的澄清问题 — 好迹象
- Diff 变小且更聚焦 — 好迹象
- 不再随机重构相邻文件 — 很好的迹象
- 开始以验证思维工作（而非"我实现了"） — 理想状态

## 核心价值

这四大原则即使不安装仓库，作为提示词策略也完全适用。安装后则可以默认内置这些纪律，无需每次重复声明。原则是可移植的，适用于任何支持注入规则的 AI coding 工具（Claude Code、Cursor、Verdant 等）。

## 关键引言

> You are not installing a feature. You are installing discipline.