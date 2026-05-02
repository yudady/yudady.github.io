---
title: Hermes Agent v0.8.0 - 自诊断自修复的AI Agent
aliases: [Hermes v0.8, Hermes Agent 0.8, Hermes self-optimization]
tags: [ai-agent, status/active, area/distill, type/doc, topic-hermes, topic-self-optimization]
source: ["https://www.youtube.com/watch?v=0QgTB7dxgWE"]
author: Julian Goldie
created: 2026-04-09 21:20
updated: 2026-04-09 21:20
description: |
  Hermes Agent v0.8.0 发布，核心亮点：自优化工具调用（自动诊断并修复自身失败模式），209 PR / 5天 / 82 issue。
level: beginner
stars: 3
---

# Hermes Agent v0.8.0 - 自诊断自修复的AI Agent

> Hermes Agent v0.8.0 在 5 天内合并 209 个 PR，最引人注目的功能是 Self-Optimized Tool Use——Agent 自动运行行为基准测试，发现失败模式并自行修复，无需人工介入。

---

## 头条功能：Self-Optimized Tool Use

这是 AI Agent 领域前所未见的功能：

```
Agent 自动运行行为基准测试
       ↓
发现 5 种工具调用失败模式（GPT / Codex 模型）
       ↓
自动记录问题 + 生成修复补丁
       ↓
无需人工提交 bug report 或编写修复代码
```

**解决的核心痛点**：AI Agent 的可靠性问题——设好能用 3 次，第 4 次莫名失败。Hermes 内建了一层自动发现并修复此类问题的机制。

---

## v0.8.0 主要功能更新

### 1. 后台任务自动通知

| 旧版 | v0.8.0 |
|------|--------|
| 需要反复轮询「做完了吗？」 | 任务完成后 Agent 自动收到通知 |
| 手动 babysitting | 启动后走开，完成即恢复 |

适用于：大型研究任务、批量文件处理、测试运行等长时间任务。

### 2. 会话中切换模型

- 命令：`/model`
- 支持跨 Telegram / Discord / Slack 实时切换
- Aggregator-aware：优先保持在当前聚合器（OpenRouter / Nous Portal），仅在必要时回退
- 典型用法：简单任务用快速廉价模型，复杂推理切换到强力模型

### 3. 新增模型支持

| 模型 | 说明 |
|------|------|
| Google AI Studio（Gemini） | 原生 provider，无需 workaround |
| MiMo v2 Pro（小米） | Nous Portal 免费层，处理压缩/摘要/视觉等辅助任务 |

上下文长度通过 models.dev 注册表自动检测，新模型发布后 Hermes 自动适配。

### 4. 安全加固

| 安全措施 | 说明 |
|----------|------|
| SSRF 防护 | 防止服务端请求伪造 |
| Timing 缓解 | 防止时序攻击 |
| Credential 泄露防护 | 防止凭证意外暴露 |
| Path Traversal 修复 | 防止路径遍历攻击 |
| MCP OAuth 2.1 | 外部工具连接支持 OAuth 认证 |
| MCP 包恶意软件扫描 | 安装 MCP 包时自动扫描 |

### 5. 超时机制改进

```
旧机制：墙钟时间超时
  → 任务运行超过 X 分钟 → 强制终止（即使仍在工作）

新机制：工具活跃度追踪
  → Agent 正在使用工具 → 继续运行
  → Agent 真正空闲    → 超时终止
```

### 6. 平台支持增强

| 平台 | 改进内容 |
|------|----------|
| Matrix | 完整 Tier 1 支持：反应、已读回执、富文本 |
| Signal | 正常发送图片、语音等 |
| Slack | 更智能的线程处理 |
| Telegram | Emoji 反应用于审批 |
| Mattermost | 文件附件支持 |

### 7. 插件系统升级

- 插件可注册自定义 CLI 命令
- 安装时自动提示所需环境变量
- 可挂载到 Session 生命周期事件
- 对生产环境定制化意义重大

---

## 稳定性修复

- 57 个失败的 CI 测试修复
- 40+ 文档不一致问题修复
- 这不仅是功能发布，更是稳定性加固

**数据汇总**：5 天 / 209 PR / 82 issue 解决

---

## 与 OpenClaw 的对比

| 维度 | Hermes | OpenClaw |
|------|--------|----------|
| 开源 | 是 | 是 |
| 用户基数 | 较小但增长快 | 较大 |
| 集成数量 | 较少 | 较多 |
| 发货速度 | 极快（社区并行推进） | 较慢 |
| 自优化 | 内建 | 无 |
| 插件生态 | 开放，可注册 CLI 命令 | 有限 |
| 迁移工具 | 提供从 OpenClaw 迁移工具 | — |

Hermes 提供了 OpenClaw 用户的专属迁移工具。关键观察点：Hermes 的自优化 + 开放插件生态是否能缩小可靠性差距。

---

## 参考资料

- [视频：Hermes Agent v0.8.0 Just Upgraded Itself](https://www.youtube.com/watch?v=0QgTB7dxgWE)
- [Hermes Agent GitHub](https://github.com/nouseResearch/hermes)

## 相关笔记
- [[../AI/LLM Wiki + Hermes Agent - 持续积累的AI知识库]]
- [[../../200-Distill/200-AI-Tools/Hermes Agent - 自改进AI代理框架]]
