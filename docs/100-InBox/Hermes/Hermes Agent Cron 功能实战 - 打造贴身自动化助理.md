---
title: Hermes Agent Cron 功能实战 - 打造贴身自动化助理
aliases: [Hermes Cron, Hermes 定时任务, Hermes 自动化]
tags:
  - hermes-agent
  - cron
  - automation
  - monitoring
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=DJxI0ku9amw"
  - "https://hermes-agent.nousresearch.com/docs/user-guide/features/cron"
  - "https://hermes-agent.nousresearch.com/docs/guides/automate-with-cron"
author: YouTube Creator (Hermes Agent Cron 实战演示)
created: 2026-05-19
updated: 2026-05-19
description: |
  以三个实战场景演示 Hermes Agent 的 Cron 定时任务功能：每日新闻早报、服务器内存监控、项目变更通知。无需命令行，对话即可创建自动化工作流。
level: beginner
stars: 3
---

# Hermes Agent Cron 功能实战 - 打造贴身自动化助理

> 通过三个场景演示如何用 Hermes Agent 的 Cron 功能打造贴身自动化助理。核心发现：普通用户完全不需要看复杂文档，用对话方式指挥 Agent 就能创建定时任务。

## 目录

- [[#Cron 功能概述]]
- [[#场景一：每日新闻早报]]
- [[#场景二：服务器内存监控]]
- [[#场景三：项目变更通知]]
- [[#模型选择的隐性成本]]
- [[#Cron 功能速查]]

---

## Cron 功能概述

### 三种创建方式

| 方式 | 命令 | 适用场景 |
|------|------|----------|
| **对话** | 直接用自然语言描述 | ✅ 普通用户推荐 |
| **`/cron` 命令** | `/cron add "every 2h" "任务"` | 快速创建 |
| **CLI** | `hermes cron create "every 2h" "任务"` | 脚本 / 自动化 |

### 关键特性

```
Hermes Cron 运行机制：
  ┌──────────────────────────────────────┐
  │  Gateway Daemon（每 60 秒 tick）      │
  │       │                              │
  │       ▼                              │
  │  到期的 Job → 独立的 Agent Session    │
  │       │                              │
  │       ▼                              │
  │  加载 Skills（如有）→ 执行 Prompt     │
  │       │                              │
  │       ▼                              │
  │  输出 → deliver 到目标平台            │
  └──────────────────────────────────────┘

  ⚠️ Cron 内部不能再创建 Cron（防止递归循环）
```

---

## 场景一：每日新闻早报

### 需求

每天早上 8 点，搜索最近 24 小时的 Agent 和开源大模型新闻，整合后发到 Telegram，附一句至理名言。

### 操作方式

直接用对话告诉 Hermes：

> 每天早上 8 点，搜索最近 24 小时的 Agent 和开源大模型的新闻，按指定格式发送到 Telegram。

Hermes 自动理解需求 → 创建定时任务 → 测试运行。

### 输出效果

```
📰 每日 AI 新闻早报

1. [标题] — 总结内容
   来源: xxx

2. [标题] — 总结内容
   来源: xxx

...（共 5 条）

💡 今日名言: "..."
```

### 进阶：多话题并行

关注多个话题时，可以让每个话题开启一个**子 Agent** 并行处理，完成后统一整合：

```
串行方式：
  话题 A 搜索 → 话题 B 搜索 → 话题 C 搜索 → 整合
  ❌ 执行时间长

并行方式（子 Agent）：
  ┌─ 子 Agent A: 话题 A 搜索 ─┐
  ├─ 子 Agent B: 话题 B 搜索 ─┤→ 整合 → 发送
  └─ 子 Agent C: 话题 C 搜索 ─┘
  ✅ 效率大幅提升
```

> 子 Agent 缺位时，主 Agent 会自动补位——类似团队协作中的缺位替补。

---

## 场景二：服务器内存监控

### 需求

监控服务器内存使用率，超过 50% 就发 Telegram 预警。

### 操作方式

直接告诉 Hermes：

> 监控服务器内存，大于 50% 就发 Telegram 预警。

Hermes 自动：
1. 创建监控脚本
2. 测试运行
3. 发现问题 → 自行修复
4. 跑通后部署

### 输出效果

```
⚠️ 服务器内存预警

当前内存使用率: 62%
阈值: 50%

TOP 5 内存占用进程:
1. chrome    — 1.2 GB
2. docker    — 890 MB
3. node      — 456 MB
4. postgres  — 340 MB
5. slack     — 210 MB
```

### Dashboard 管理

创建后可在 Dashboard 中管理任务：
- 暂停 / 恢复
- 立即执行（测试）
- 通过界面创建 / 管理任务

---

## 场景三：项目变更通知

### 需求

监控项目 Git 变更，有人提交代码时做总结，发 Telegram 通知。

### 操作方式

> 监控项目，如果有变更，总结谁提交了什么，发 Telegram 消息。

### Sonnet 4.6 的亮点

视频对比了 DeepSeek 和 Sonnet 4.6 处理同一任务的表现：

| 方面 | DeepSeek | Sonnet 4.6 |
|------|----------|------------|
| 理解需求 | 需要反复修复 | 一次性到位 |
| 创建任务 | 中间多次出错 | 一次成功 |
| 边界处理 | 未考虑初始状态 | ✅ 建立变更基线 |
| 通知逻辑 | 发送了空消息 | ✅ 无变化时明确告知原因 |

**基线机制**（Sonnet 4.6 的优秀设计）：

```
首次运行 → 建立当前状态基线 → 存入状态文件
  │
  ▼
后续运行 → 与基线对比
  │
  ├── 有变化 → 发送变更通知 + 更新基线
  └── 无变化 → 告知"当前无变化"（不发送噪音）
```

> Sonnet 的解释非常清楚——不会让你猜"为什么没收到消息"，把原因清晰告诉你。

---

## 模型选择的隐性成本

视频最后分享了模型选择的三点启示：

### 省钱可能反而更贵

| 成本类型 | 便宜模型 | 强大模型 |
|----------|----------|----------|
| **Token 成本** | 5000 Token 解决问题 | 1000 Token 解决问题 |
| **时间成本** | 来回调整耗费更多时间 | 一次到位 |
| **隐性风险** | 可能引入代码/系统风险 | 可靠性更高 |

### 决策树

```
选模型的判断：
  任务是简单的信息整理？
    ├── Yes → 便宜模型可能够用
    └── No →
        涉及系统操作 / 代码修改 / 监控脚本？
          ├── Yes → 用强大模型（减少风险）
          └── No → 按预算选择

  ⚠️ 便宜模型带来的代码问题可能一时无法发现
     隐性风险成本远高于显性 Token 成本
```

---

## Cron 功能速查

### 创建任务

```bash
# 对话方式（推荐普通用户）
直接告诉 Hermes 你的需求即可

# /cron 命令
/cron add "every 2h" "Check server status"
/cron add "every day at 9am" "Morning report" --deliver telegram

# CLI
hermes cron create "every 2h" "Check server status"

# 带 Skill
/cron add "every 1h" "Summarize feeds" --skill blogwatcher
```

### 生命周期管理

| 操作 | 命令 |
|------|------|
| 列出任务 | `/cron list` |
| 暂停 | `/cron pause <job_id>` |
| 恢复 | `/cron resume <job_id>` |
| 立即执行 | `/cron run <job_id>` |
| 删除 | `/cron remove <job_id>` |

### 投递选项

| 选项 | 说明 |
|------|------|
| `"origin"` | 回到创建位置（默认） |
| `"local"` | 保存到 `~/.hermes/cron/output/` |
| `"telegram"` | Telegram 首页频道 |
| `"telegram:chat_id"` | 指定 Telegram 聊天 |

### Script 模式（高级）

```bash
# 脚本做机械工作（抓取、对比），Agent 做推理（是否有意义）
/cron add "every 1h" \
  "If CHANGE DETECTED, summarize. If NO_CHANGE, respond [SILENT]." \
  --script ~/.hermes/scripts/watch-site.py \
  --deliver telegram
```

### 最佳实践

- ✅ 用对话创建任务，不用记忆复杂命令
- ✅ 设置明确的输出格式（契约化交付）
- ✅ 多话题任务用子 Agent 并行
- ✅ 监控类任务建立基线，无变化时静默
- ❌ Cron 内部不要再创建 Cron
- ❌ 不要忽视模型选择的隐性风险
- ❌ 不要用 Telegram 文字墙交付复杂报告（参考三层交付）

---

## 参考资料

- [Hermes Agent - Scheduled Tasks (Cron) 官方文档](https://hermes-agent.nousresearch.com/docs/user-guide/features/cron)
- [Automate Anything with Cron - Hermes Agent 指南](https://hermes-agent.nousresearch.com/docs/guides/automate-with-cron)
- [The cron job every serious Hermes Agent user should have - Reddit](https://www.reddit.com/r/hermesagent/comments/1t9gz2f/the_cron_job_every_serious_hermes_agent_user/)

## 相关笔记

- [[Agent 三层交付 - 让 AI 输出不再塞满聊天框]]
- [[Claude Code 大项目最佳实践 - 生态配置详解]]
