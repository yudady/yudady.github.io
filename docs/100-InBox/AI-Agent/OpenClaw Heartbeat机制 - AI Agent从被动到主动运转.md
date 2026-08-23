---
title: OpenClaw Heartbeat 机制 — AI Agent 从被动到主动运转
tags:
  - AI-Agent
  - OpenClaw
  - 自动化
  - Heartbeat
source: https://www.youtube.com/watch?v=rlt9brP8FM4
author: Web3 Blockchain School
created: 2026-05-10
updated: 2026-05-10
description: 讲解 OpenClaw Heartbeat 心跳机制的设计哲学与实战经验，让 AI Agent 从被动应答变为定时自动运转的持续系统。
level: 初中级
stars: 3
---

## 核心观点

大部分 AI Agent 预设是**被动的** — 你不叫它，它就不动。这本质上是一个高级聊天机器人，不是真正的"员工"。

**Heartbeat（心跳机制）** 解决这个问题：在固定时间唤醒 Agent，让它执行检查、判断、行动、汇报的完整流程。

## Heartbeat 是什么

> Heartbeat = 定时触发机制，让 Agent 从**一次性任务**变成**持续性系统**

类比：AI Agent 的"闹钟"，到点自动醒来执行预设流程。

### 没有 Heartbeat 的问题

- 你是整个流程的"人肉 API" — 发动机是你，AI 只是指令执行器
- 有 AI 反而比没 AI 更忙（每天手动下各种指令）
- 表面上 AI 在帮你工作，实际你不动它就不动

## 踩坑经验

**不要让 Agent 每分钟/每 5 分钟跑一次**，会导致：

1. **成本暴增** — ChatGPT Plus Codex 额度很快用完
2. **错误变多** — 日志混乱
3. **重复工作** — 上一轮没完成，下一轮又重做（作者亲身经历：每分钟生成视频，10 分钟跑了 10 次，电脑直接卡死）
4. **系统难排查** — 密集调用导致问题定位困难

## Heartbeat 三种类型

### 1. 日常启动（晨会模式）

- 每天 9:00 自动读取任务看板，生成当日计划
- 明确优先级、待处理卡片
- 不马上执行所有事，而是先知道今天做什么

### 2. 定时巡检

- 每 30 分钟 ~ 1 小时检查一次
- 检查 Trello 有无新卡片、Doing 列有无卡住的任务
- 定时任务状态巡检（有问题即时修复）

### 3. 收尾总结

- 每天晚上做回顾：完成什么、失败什么、明天做什么
- 记录问题和优化点
- **Agent 只做事不总结，系统永远变不聪明**

## 核心公式

```
Heartbeat = 时间节奏 + 任务检查 + 行动决策 + 结果回报
```

不只是"到时间跑一下"，而是一套完整工作规则：
- **任务从哪来** — 哪个看板、哪个列表、哪些标签
- **检查什么** — 具体的检查清单
- **怎么判断** — 什么条件触发行动
- **用什么工具** — 明确工具链
- **记录在哪** — 结果归档位置
- **失败怎么办** — 通知和回退策略

> 只给 Agent 一个定时器是不够的，要给一套工作规则。

## 延伸：OpenClaw 定时系统全貌

OpenClaw 提供四层自动化组件（按触发源从内到外）：

| 组件 | 触发方式 | 类比 | 典型场景 |
|------|----------|------|----------|
| Heartbeat | 自检（每 N 分钟） | 保安巡逻 | 每 30 分钟巡检服务状态 |
| Cron | 时间驱动 | 日程表 | 每天 9:00 生成晨报 |
| Hooks | 内部事件 | 系统回调 | Skill 执行完自动记日志 |
| Webhook | 外部事件 | 门铃 | GitHub PR 触发代码审查 |

省钱机制：HEARTBEAT.md 为空时跳过执行，不产生 API 调用；可设活跃时段（仅工作时间心跳）。

## 关键启示

1. **Heartbeat 的设计重点不是频率，是流程清晰度** — 流程写得越清楚，Agent 越稳定
2. **从管理者视角设计** — 你不会每分钟开会，也不会每 5 分钟问员工进度
3. **持续累积经验** — 真正厉害的 AI 系统不只执行，还会定期反思和优化
4. **最好的工具不是用得最多的，而是忘了它在运行的**

## 参考资源

- [OpenClaw 定时系统设计哲学（知乎专栏）](https://zhuanlan.zhihu.com/p/2019549032645669412) — Heartbeat/Cron/Hooks/Webhook 四层详解
- [OpenClaw Heartbeat vs Cron 选型指南](https://www.w3cschool.cn/openclawdocs/openclaws-automation-cron-vs-heartbeat.html)
- [OpenClaw 心跳机制完全指南（AtomGit）](https://gitcode.csdn.net/69b18c9154b52172bc6098d5.html)
