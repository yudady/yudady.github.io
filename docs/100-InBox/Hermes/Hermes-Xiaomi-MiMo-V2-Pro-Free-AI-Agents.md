---
created: 2026-04-11
title: Hermes + Xiaomi MiMo V2 Pro — 免费 AI Agent
source: https://www.youtube.com/watch?v=MrPWA2SNdKA
author: Julian Goldie SEO
tags:
  - AI
  - agent
  - hermes
  - xiaomi
  - mimov2pro
  - nous-research
type: video
---

# Hermes + Xiaomi MiMo V2 Pro — 免费 AI Agent

> 频道: Julian Goldie SEO | 发布: 2026-04-10 | 时长: 8:01

## 核心内容

Nous Research 与 Xiaomi 合作，将 Xiaomi 旗舰 AI 模型 MiMo V2 Pro 免费接入 Hermes Agent，免费期 2 周。

## Hermes Agent 是什么

- 由 Nous Research 开发，2026 年 2 月发布
- 开源、MIT 协议、自托管（$5 VPS 即可）
- 核心特性：Closed Learning Loop（闭环学习）
  - 每次完成任务后自动生成 Skill 文档，下次复用
  - 持久记忆：所有对话、项目、上下文跨 session 保留
  - 不需要手动配置 YAML，用得越久越智能
- 多平台消息网关：Telegram、Discord、Slack、WhatsApp、Signal、Email
- 40+ 内置 Skill，支持 200+ AI 模型（Nous Portal / OpenRouter / OpenAI 兼容端点）
- 32,000+ GitHub Stars，200+ 贡献者

## MiMo V2 Pro 模型

- Xiaomi 旗舰 AI 模型
- 超过 1 万亿总参数，100 万 token 上下文窗口
- 专为 Agentic 工作设计：规划、工具使用、错误恢复、多步决策
- 在 Hermes 中处理：压缩、摘要、视觉处理
- 测试者反馈表现与付费模型相当

## 设置步骤

1. 安装 Hermes：一条 curl 命令（Linux/macOS/WSL2）
2. 更新到 v0.8.0（The Intelligence Release，2026-04-08）
3. 创建 Nous Portal 免费账号
4. 在 Hermes 中选择 Nous Portal 作为 provider，浏览器授权
5. 选择 Xiaomi MiMo V2 Pro，确认激活

## v0.8.0 更新亮点

- 209 个 PR，82 个 issue 修复
- 后台任务自动通知（无需轮询）
- 中途切换模型（快模型 vs 深度模型）
- 自我修复：Agent 自行运行基准测试，发现 5 种失败模式并修补自身系统提示
- Google AI Studio 集成
- 智能 inactive timeout（按实际活动追踪，不按墙钟）
- Slack/Telegram 审批按钮
- OAuth 2.1 支持（MCP 认证）

## 实际应用场景

- **研究监控**：cron job 自动监控 Reddit/X 趋势，生成早报推送到 Discord
- **编码助手**：跨 session 记住代码库、约定、部署流程
- **文档分析**：100 万 token 上下文一次处理多份长文档
- **自动化任务流水线**：子 agent 并行工作流
