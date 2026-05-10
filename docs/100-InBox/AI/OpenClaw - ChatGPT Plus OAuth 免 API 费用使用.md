---
title: OpenClaw - 通过 OAuth 免 API 费用使用 ChatGPT Plus
aliases: [OpenClaw OAuth, ChatGPT Plus 免API]
tags:
  - openclaw
  - ai-agent
  - openai
  - oauth
  - status/active
  - type/tutorial
source:
  - "https://www.youtube.com/watch?v=qvvtbS4Wj8g"
  - "https://lumadock.com/tutorials/openclaw-openai-codex-chatgpt-subscription"
  - "https://github.com/openclaw/openclaw"
author: Phil Lougher (YouTube) / Daniel Ignat (LumaDock 教程)
created: 2026-05-06
updated: 2026-05-06
description: OpenClaw 支持 OAuth 方式接入 ChatGPT Plus 订阅，无需 API Key 即可使用 GPT-5.x 模型，大幅降低 AI Agent 运行成本
level: intermediate
stars: 3
note: 无字幕，基于视频元数据（章节+描述）+ 外部教程资料综合整理
---

# OpenClaw - 通过 OAuth 免 API 费用使用 ChatGPT Plus

> OpenClaw 是开源的自托管 AI Agent 平台，支持通过 OAuth 登录你的 ChatGPT Plus/Pro 订阅，直接使用 GPT-5.x 模型而不需要按 token 付费的 API Key。视频演示了完整的配置流程和多模型 Agent 团队搭建。

---

## 什么是 OpenClaw

OpenClaw（前身 Clawdbot / Moltbot）是开源的自主 AI Agent 平台，GitHub 100k+ stars。核心特点：

- **自托管**：跑在自己的硬件上（VPS 或本地）
- **多渠道**：接入 Telegram、WhatsApp、Discord 等已有聊天工具
- **多模型**：支持 Anthropic Claude、OpenAI、Gemini、本地 Ollama 等
- **工具系统**：文件读写、终端执行、日历、邮件、GitHub 监控等
- **配置驱动**：通过 `SOUL.md`、`AGENTS.md` 等 workspace 文件定制行为

---

## OAuth 接入 ChatGPT Plus 的核心价值

传统方式 vs OAuth 方式的成本对比：

```
┌─────────────────────────────────────────────────────────┐
│                    传统 API 方式                         │
│  ChatGPT Plus $20/月 ─┐                                │
│  + API 费用（按 token）│→ 每月 $50~$500+ 不等           │
│  ⚠ 费用不可预测        │                                │
├─────────────────────────────────────────────────────────┤
│                    OAuth 方式                            │
│  ChatGPT Plus $20/月 ─┤→ 固定 $20/月                   │
│  ✅ 无额外 API 费用    │  (含 5h/周 Codex 配额)         │
└─────────────────────────────────────────────────────────┘
```

| 维度 | API Key | OAuth (ChatGPT Plus) |
|------|---------|---------------------|
| 费用模型 | 按 token 计费 | 包含在订阅内 |
| 月成本 | $50~$500+ | $20（Plus）/$200（Pro） |
| 可用模型 | 全部 | Codex 系列（GPT-5.x） |
| 配额限制 | 按账户 tier | Plus: 5h/周 Codex |
| 适合场景 | 生产环境 | 个人 / 团队内部 |

---

## 配置步骤

### Step 1：更新 OpenClaw 并运行 onboard

```bash
# 更新到最新版本
openclaw update

# 运行 onboard 向导，选择 OpenAI Codex 认证
openclaw onboard --auth-choice openai-codex
```

**关键选择**：向导中选 "Use existing values"，不要选 "Reset"（会清除已有配置）。

### Step 2：浏览器 OAuth 登录

1. 向导生成 OAuth URL → 复制到浏览器
2. 登录 OpenAI 账号 → 点击 Continue 授权
3. 浏览器跳转 `localhost:1455?code=...`（显示错误是正常的）
4. **复制完整 URL**（含 `?code=` 参数）→ 粘贴回终端

```
终端显示 OAuth URL          浏览器授权
     ┌──────┐              ┌──────────┐
     │ URL  │──复制──→─────│ OpenAI   │
     └──────┘              │ 登录页   │
                           └────┬─────┘
                                │ 授权
                                ▼
     ┌──────────┐        ┌──────────────┐
     │ 终端     │←粘贴──│ localhost URL │
     │ 粘贴 URL │        │ ?code=xxx    │
     └──────────┘        └──────────────┘
```

### Step 3：设置主模型

```bash
# 设置 Codex 为主模型
openclaw models set openai-codex/gpt-5.3-codex

# 验证状态
openclaw models status --plain
```

### Step 4：配置 Fallback

```bash
# 添加免费备选模型（配额用完时自动切换）
openclaw models fallbacks add openrouter/google/gemini-3-flash-preview
```

---

## 搭建多模型 Agent 团队

视频中展示的 Prompt 模板：

```prompt
Use the following models with aliases:

- OpenAI Codex GPT-5.5 - Chief of Staff as the default primary agent
- OpenAI GPT-5.4 - CTO for coding/technical implementation
- OpenAI GPT-4.1 - CMO for marketing/growth/content
- OpenAI GPT-5.4 mini - daily tasks for smaller routine tasks

Fallback option:
- openrouter/google/gemini-3.1-pro-preview

Add this to my config file.
```

模型角色分工：

| 别名 | 模型 | 角色 | 用途 |
|------|------|------|------|
| Chief of Staff | GPT-5.5 Codex | 默认主 Agent | 任务分发、综合决策 |
| CTO | GPT-5.4 | 技术 Agent | 编码、技术实现 |
| CMO | GPT-4.1 | 营销 Agent | 内容创作、增长策略 |
| Daily Tasks | GPT-5.4 mini | 日常 Agent | 小型常规任务 |

---

## 使用配额与限制

```
┌─────────────────────────────────────────┐
│          ChatGPT Plus 配额               │
│                                         │
│  Codex 使用: 5 小时/周                   │
│  ████████░░░░░░░░░░  约 40% 已用        │
│                                         │
│  ⚠ 达到配额 → 自动 fallback 到          │
│    gemini-3-flash-preview               │
│                                         │
│  Pro 订阅: 更高/无限配额                 │
└─────────────────────────────────────────┘
```

---

## 常见问题排查

| 问题 | 解决方案 |
|------|----------|
| OAuth 授权失败 | 去 OpenAI connected apps 撤销 OpenClaw 权限，重新 `onboard` |
| 模型显示不可用 | `openclaw models list --all` 确认目录，清除旧 fallback 后重试 |
| Token 过期 | `openclaw models auth login --provider openai-codex` 重新认证 |
| GPT-5.4 路由 401 | 已知 bug（issue #38706），用 `gpt-5.3-codex` 替代 |
| Codex vs Claude 行为差异 | 调整 `SOUL.md` 和 `AGENTS.md` 适配新模型，不要急于下结论 |

**重要规则**：不要让 OpenClaw Agent 自己运行 onboard 或修改自身配置，会造成循环依赖。始终用单独终端操作。

---

## 适用场景判断

```
              你需要 AI Agent 吗？
                    │
          ┌─────────┴─────────┐
          │ 是                │ 否
          ▼                   ▼
    ┌──────────┐        直接用 ChatGPT
    │ 已有 Plus │              或 Claude 网页版
    │ 订阅？    │
    └────┬─────┘
     是  │  否
     ▼   ▼
  ┌────┐ ┌────────┐
  │OAuth│ │ API Key│
  │方案 │ │ 或本地  │
  └────┘ └────────┘
```

---

## 参考资料

- [How to use OpenAI Codex on OpenClaw with a ChatGPT subscription (LumaDock)](https://lumadock.com/tutorials/openclaw-openai-codex-chatgpt-subscription)
- [OpenClaw - GitHub](https://github.com/openclaw/openclaw)
- [OpenClaw 官网](https://openclaw.ai/)
- [openclaw + ChatGPT OAuth: Run GPT-5.4 (ai-muninn)](https://ai-muninn.com/en/blog/openclaw-chatgpt-oauth-gpt54-no-api-key)
- [What Is OpenClaw? (MindStudio)](https://www.mindstudio.ai/blog/what-is-openclaw-ai-agent/)
- [OpenClaw 完整指南 (Milvus)](https://milvus.io/blog/openclaw-formerly-clawdbot-moltbot-explained-a-complete-guide-to-the-autonomous-ai-agent.md)

## 相关笔记

- [[AI Agent]] - 自主 AI Agent 概念
