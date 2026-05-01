---
title: Hermes Agent - 自改进 AI 代理框架
aliases: [Hermes Agent, Hermes, GAPA Agent]
tags: [ai-agent, status/active, area/distill, type/doc, topic-hermes-agent, nous-research]
source: ["https://www.youtube.com/watch?v=cu2fgknmemA"]
author: WorldofAI
created: 2026-04-07 20:36
updated: 2026-04-07 20:36
description: |
  Nous Research 推出的开源自改进 AI 代理，通过 GAPA 机制自动学习进化，无需微调或提示工程。
level: beginner
stars: 3
---

# Hermes Agent - 自改进 AI 代理框架

> Nous Research 开发的开源 AI 代理框架，核心卖点是「越用越聪明」——不需要微调模型或手动调 prompt，代理会自动从使用过程中学习、建立技能、持续进化。被视为 OpenClaw 的自改进替代方案。

---

## 核心机制：GAPA（Generalized Agent Prompt Adaptation）

Hermes 的自改进能力来自 GAPA 系统，工作原理类似反向传播（Back Propagation），但作用对象不是模型权重，而是 prompt：

```
┌─────────────────────────────────────────────┐
│              GAPA 学习循环                    │
│                                              │
│  执行任务 ──→ 每约15次工具调用后暂停           │
│       │              │                       │
│       │         ┌────▼────┐                   │
│       │         │ 回顾审查 │                   │
│       │         │ 哪些成功 │                   │
│       │         │ 哪些失败 │                   │
│       │         └────┬────┘                   │
│       │              │                       │
│       │         ┌────▼────┐                   │
│       │         │ 自我更新 │                   │
│       │         │ 调整策略 │                   │
│       │         └────┬────┘                   │
│       │              │                       │
│       └──── ◀ ──────┘                        │
│   用新策略继续执行                              │
└─────────────────────────────────────────────┘
```

### 关键特性

| 特性 | 说明 |
|------|------|
| 自动技能创建 | 解决问题、修复错误后自动生成可复用技能 |
| 持久化记忆 | 跨会话记住有效方法，搜索历史对话 |
| 适应使用习惯 | 根据用户工作方式动态调整行为 |
| 视觉化能力 | 通过 manim 技能将技术概念转为动画视频 |

---

## Hermes vs OpenClaw

视频将其定位为 OpenClaw 的替代品，两者功能相近但理念不同：

| 维度 | OpenClaw | Hermes Agent |
|------|----------|--------------|
| 核心理念 | 执行任务 | 反思、学习、进化 |
| 学习能力 | 静态 | 自改进（GAPA） |
| 技能系统 | 固定 | 自动创建和积累 |
| 记忆 | 有限 | 持久化 + 自适应 |
| 本地模型 | 支持 | 支持 |
| 多平台 | 支持 | 支持（WhatsApp/Telegram/Slack） |
| 感觉像 | 工具 | 随使用成长的学习系统 |

**结论**：Hermes 基本能做 OpenClaw 做的所有事，但额外拥有自改进能力。

---

## 安装与配置

### 前置要求

- 非 Windows 系统直接安装；Windows 需要 WSL2
- 需要 Python 环境
- 推荐 GPU 支持本地模型

### 安装步骤

```bash
# 1. 快速安装（复制官方 quick install 命令）
curl -fsSL https://hermes-agent.nousresearch.com/install.sh | bash

# 2. 配置
hermes setup
```

### 配置方式

| 方式 | 说明 |
|------|------|
| Quick Setup | 指定模型 provider + 消息方式 |
| Full Setup | 完整配置所有选项（推荐） |

### 支持的模型 Provider

```
┌──────────────────────────────────────────┐
│           模型 Provider 选择              │
│                                          │
│  ┌───────────┐  ┌────────────┐           │
│  │ OpenRouter │  │  Anthropic  │           │
│  └─────┬─────┘  └─────┬──────┘           │
│        │              │                   │
│  ┌─────▼─────┐  ┌─────▼──────┐           │
│  │  OpenAI   │  │ 自定义(更多) │           │
│  └───────────┘  └──────┬─────┘           │
│                        │                  │
│                 ┌──────▼──────┐           │
│                 │ MiniMax/Zai │           │
│                 └─────────────┘           │
└──────────────────────────────────────────┘
```

### 模型推荐

| 场景 | 推荐 |
|------|------|
| 本地运行 | Gemma 4（Ollama）— 强大的 agentic 模型 |
| 无本地硬件 | OpenRouter 免费模型 |
| 最佳效果 | NanoBanana（视频作者推荐，适合图像生成） |

检查本地能否运行 Gemma 4：访问 https://whatmodelscanirun.com/

---

## 使用示例

### 示例 1：图像生成（缩略图）

通过 Telegram 发送指令：
```
生成一个 "Hermes Agent vs OpenClaw" 视频的缩略图概念
```

Hermes 会自动初始化所需代理、使用相关技能，生成多个概念方案。

### 示例 2：知识管理 + 前端开发

1. 使用 `/skills browse` 添加 Obsidian 技能
2. 指定 vault 路径，让 Hermes 自动从 Shadcn 文档抓取最新包信息
3. 后续生成前端时自动参考 vault 中的最新组件文档

```
┌──────────────────────────────────────────────┐
│        知识增强工作流                          │
│                                              │
│  添加 Obsidian 技能                           │
│       │                                      │
│       ▼                                      │
│  从文档站抓取最新包/组件信息                    │
│       │                                      │
│       ▼                                      │
│  写入 Obsidian Vault（持久化）                 │
│       │                                      │
│       ▼                                      │
│  AI 代理生成代码时自动参考 vault                │
│       │                                      │
│       ▼                                      │
│  GAPA 记录成功经验 → 下次优先使用              │
└──────────────────────────────────────────────┘
```

**效果**：视频展示了用此工作流在几分钟内生成带最新 Shadcn 组件的金融仪表盘。

---

## 技能系统

### 管理技能

```
/skills          # 查看所有可用技能
/skills browse   # 浏览和添加新技能
```

### 技能特性

- 自动创建：从成功经验中提取
- 手动添加：通过 URL 或 browse 添加
- MCP 集成：支持 MCP 协议的技能
- 跨会话复用：保存为可重复使用的模块

---

## 注意事项

| 项目 | 说明 |
|------|------|
| Windows 支持 | 暂不支持原生，需 WSL2 |
| 免费使用 | OpenRouter 提供免费模型 |
| 多平台 | 支持 Telegram/WhatsApp/Slack 等第三方平台 |

---

## 官方资源

- 网站：https://hermes-agent.nousresearch.com/
- GitHub：https://github.com/NousResearch/hermes-agent
- 文档：https://hermes-agent.nousresearch.com/docs/getting-started/installation

## 参考资料
- [Hermes Agent The 24/7 Self-Evolving AI Agent! (YouTube)](https://www.youtube.com/watch?v=cu2fgknmemA)

## 相关笔记
- [[../../200-Distill/wiki/entities/OpenClaw]]
