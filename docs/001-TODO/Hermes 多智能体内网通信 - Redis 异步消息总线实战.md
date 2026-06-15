---
title: Hermes 多智能体内网通信 - Redis 异步消息总线实战
aliases: [Hermes-AgentMesh, 多智能体调度器, 异步消息队列]
tags:
  - hermes
  - multi-agent
  - redis
  - orchestrator
  - status/active
  - type/doc
source:
  - "https://youtu.be/T02cvjDZeJM"
  - "https://github.com/seleman66eeddwegger3-art/hermes-agentmesh"
author: wow.insight (Bobo)
created: 2026-06-11
updated: 2026-06-11
description: 用 Redis 异步消息总线替代群聊和 HTTP 同步，实现跨设备多智能体长期稳定协同工作
level: intermediate
stars: 4
note: 无字幕（频道手动关闭），基于视频描述 + GitHub README 综合整理。Vercel 链接不可公开访问。
---

# Hermes 多智能体内网通信 — Redis 异步消息总线实战

> 视频演示如何在 Mac mini（Bobo）和 X230i（99）上构建基于 Redis 的异步消息总线，替代传统的 AI 群聊和 HTTP 同步模式，实现跨设备的工业级多智能体调度（Orchestrator）。Gemini 评价该系统为"工业级（Production-Ready）"。

---

## 目录

- [为什么群聊和 HTTP 同步不行](#为什么群聊和-http-同步不行)
- [核心哲学：信箱而非电话](#核心哲学信箱而非电话)
- [系统架构](#系统架构)
- [三大核心设计](#三大核心设计)
- [协议规范](#协议规范)
- [跨框架兼容性](#跨框架兼容性)
- [实测结果](#实测结果)
- [Bug 故事：Loopback Masking](#bug-故事loopback-maskin)
- [部署要点](#部署要点)
- [参考资料](#参考资料)

---

## 为什么群聊和 HTTP 同步不行

### 群聊模式的失败

把两个 AI 拉进 Telegram 群聊的典型问题：

- AI 之间互相吹捧，形成"互吹灾难"
- 缺乏中心化的"法官机制"控制流程
- Token 消耗高但产出低
- 无法做物理隔离和规则约束

> "群聊是给人类用的，智能体用群聊 = 把严谨协议塞进噪声池。"

### HTTP 同步模式的失败

多步任务（6 轮 Tool Call + 文件写入 + 自测 + 报告）通常需要 **5-15 分钟/轮**：

| 问题 | 说明 |
|------|------|
| 客户端阻塞 | `requests.post(url, timeout=600)` 导致调用方卡死 |
| 超时崩溃 | 5-10 分钟的 HTTP 超时仍不够 |
| 服务端宕机 | 服务 down = 客户端 deadlock |
| 跨机通信 | 需要 SSH 隧道 |

> "HTTP 同步是给人类短交互设计的，智能体长任务用它 = 自找崩溃。"

---

## 核心哲学：信箱而非电话

```
  电话模式（HTTP 同步）         信箱模式（Redis 队列）
  ─────────────────────        ──────────────────────
  打电话 → 必须等对方接          写信 → 扔进信箱就走
  对方忙 → 你卡在原地            对方忙 → 信在信箱等
  对方不在线 → 死锁              对方不在线 → 信还在
  通话结束 → 连接断开            信箱永不停业
```

**解耦本质**：任何人都能往任何节点的 inbox 推任务，任何人都能从任何节点的 inbox 消费。节点不需要知道其他节点是否在线。**信箱本身就是协议。**

---

## 系统架构

```
┌────────────── Mac mini (<YOUR_MAC_MINI_IP>) ──────────────┐
│ ┌─────────────┐ ┌──────────────┐ ┌───────────────────┐    │
│ │ Redis :6379 │ │ Hermes API   │ │ HTTP serve :8080  │    │
│ │ bind 0/0    │ │ :8642 (LLM)  │ │ (LaunchAgent)     │    │
│ └──────┬──────┘ └──────────────┘ └───────────────────┘    │
│        │ 0.7ms LAN, Redis shared                           │
└────────┼──────────────────────────────────────────────────┘
         │
    ┌────┴──── inbox:macmini ◄──── lpush(task) ──────┐
    └──── inbox:macmini ────► brpop(block=0) ──► worker_macmini
    ┌──── outbox:orch ────► brpop ──► orchestrator
    ┌──── inbox:99 ◄── 99 pushes ──────────────┘
         ▼
┌──── X230i / Pi 5 / Any Linux Node ────────────────────────┐
│ ~/.hermes/async_bus/                                       │
│ ├── worker_node.py      (protocol=2 for RESP3 fix)         │
│ ├── orchestrator_async.py (4-step, N-round debate)         │
│ ├── .env_common         (REDIS_HOST, API_URL, API_KEY)     │
│ └── worker_<NODE>.log    (systemd managed)                  │
│ systemd --user async_bus_worker_<NODE>.service             │
│ (Linger=yes → survives reboot / ssh logout)                 │
└───────────────────────────────────────────────────────────┘
```

**关键特性**：
- Redis 监听 `0.0.0.0`，内网所有设备共享
- 0 SSH — 完全通过 Redis 队列通信
- 报告自动回到发起方的本地机器，无需 scp
- systemd 托管，Linger=yes 保证 SSH 登出后不退出

---

## 三大核心设计

### 1. 隔离记忆池（Isolated Memory Pools）

每个 Agent 节点有独立的记忆空间，不共享上下文：

```
  ❌ 错误做法                    ✅ 正确做法
  ┌──────────────────┐          ┌──────────────────┐
  │   共享上下文       │          │ Bobo 记忆池       │
  │   Agent A 看到    │    →     │ 99 记忆池         │
  │   Agent B 的思考  │          │ Orchestrator 记忆 │
  └──────────────────┘          └──────────────────┘
```

- 防止 Agent 间互相影响判断
- Orchestrator 只看最终结果，不看中间过程
- 各节点的 `.env_common` 独立配置

### 2. 严厉的系统提示词限制（Strict System Prompts）

| 限制类型 | 作用 |
|---------|------|
| 角色锁定 | 每个 Agent 只做自己的事（编码/测试/审核） |
| 输出格式 | 强制结构化输出（JSON、代码块） |
| Token 预算 | 限制每轮最大输出，防止无限展开 |
| 禁止社交 | Agent 不被允许"讨论"或"协商" |

### 3. 抢占式令牌机制（Preemptive Token Mechanism）

Orchestrator 控制任务分配和资源调度：

```
  Orchestrator 流程（4 步协议）:
  ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
  │ 1. 分发   │───►│ 2. 执行   │───►│ 3. 审核   │───►│ 4. 汇总   │
  │   任务    │    │   任务    │    │   结果    │    │   报告    │
  └──────────┘    └──────────┘    └──────────┘    └──────────┘
       │               │               │               │
       ▼               ▼               ▼               ▼
    lpush inbox     brpop inbox     brpop outbox    写入报告
    (推到 worker)   (worker 消费)   (Orch 消费)     (发起方本地)
```

---

## 协议规范

任何框架实现这 3 条规则即可接入 Hermes-AgentMesh：

| 规则 | 格式 | 说明 |
|------|------|------|
| 队列命名 | `inbox:<NODE_NAME>` | 每个节点有独立 inbox |
| 消息载荷 | `{turn: int, messages: [{role, content}]}` | 标准化的多轮消息格式 |
| 响应队列 | `outbox:orchestrator` | 所有节点统一回复到 Orchestrator |

---

## 跨框架兼容性

| 框架 | 接入方式 | 推任务 | 消费任务 |
|------|---------|--------|---------|
| **Hermes** | `requests.post(API_URL, messages=...)` | 内置 | 内置 |
| **OpenClaw** | `openclaw.run(inbox="my_agent", task=...)` | SDK | SDK |
| **LangGraph/AutoGen/CrewAI** | 包装 Redis client | `redis.lpush("inbox:node", task_json)` | `redis.brpop("inbox:node")` |
| **自定义 Agent** | 任何支持 Redis + HTTP 的语言 | 同上 | 同上 |

---

## 实测结果

| 场景 | 结果 | 日期 |
|------|------|------|
| Mac mini 模拟 macmini+99，4 轮 | 4.5 分钟 | 2026-06-09 |
| Mac mini + X230i 真实跨机，4 轮 | 3.5 分钟，0 SSH | 2026-06-09 |
| 99 worker 收到 Turn 999 ping | 14 字 "99 在，收到 ping." | 2026-06-10 |
| Mac mini worker 收到 Turn 888 ping | 65 字 "Bobo 在 Mac-mini.local 活着" | 2026-06-10 |
| 99 发起 2 轮（WWDC 2026 话题） | 2 分钟，报告回到 99 本地 | 2026-06-10 |

**0 次失败，0 次回滚。**

---

## Bug 故事：Loopback Masking

Agent "99" 发现了 Bobo 在 `orchestrator_async.py` 第 6 行的一个 bug：

```python
# Bug: if False 永远为假，else 分支总是执行
if False
    redis_host = os.getenv("REDIS_HOST", "127.0.0.1")
else:
    redis_host = "127.0.0.1"  # ← 实际总是走这里
```

**为什么 Mac mini 上测不出**：Redis 就在 localhost，`127.0.0.1` 恰好能连。6 轮辩论全部通过。

**为什么在 99 上失败**：Redis 在 Mac mini 上而非本机，连接立即失败。99 修复了 bug，加了 `import os`，设置了正确的回退 IP。

**教训**：单端测试永远抓不到"loopback masking" bug。真正的跨机测试必不可少。

---

## 部署要点

### Mac mini（Redis 宿主）

```bash
# Redis 监听所有接口（内网使用）
# redis.conf: bind 0.0.0.0

# Hermes API 端口 :8642
# HTTP 服务 :8080（LaunchAgent 托管）
```

### X230i（Worker 节点）

```bash
# ~/.hermes/async_bus/ 目录结构
# worker_node.py — 协议 2（redis-py 8 RESP3 修复）
# orchestrator_async.py — 4 步协议，N 轮辩论
# .env_common — REDIS_HOST, API_URL, API_KEY

# systemd 托管（Linger=yes）
systemctl --user enable async_bus_worker_99.service
```

### 关键配置项

| 配置 | 说明 |
|------|------|
| `REDIS_HOST` | Mac mini 的内网 IP |
| `protocol=2` | redis-py 8 的 RESP3 兼容修复 |
| `Linger=yes` | systemd 用户服务在 SSH 登出后继续运行 |
| `bind 0.0.0.0` | Redis 监听所有网络接口 |

---

## 参考资料

- [Hermes-AgentMesh GitHub 仓库](https://github.com/seleman66eeddwegger3-art/hermes-agentmesh)
- [原视频](https://youtu.be/T02cvjDZeJM)
- [频道 wow.insight](https://www.youtube.com/@wow.insight)

## 相关笔记

- [[../100-InBox/AI/Claude Fable 5 & Mythos 5 - Anthropic 最强模型公开发布]]
- [[Hermes Agent 配置指南]]
