---
title: Hermes Agent /go Goal 功能完全指南
aliases: [Hermes Goal, /go 命令, Hermes Agent 自主循环]
tags:
  - hermes-agent
  - goal-mode
  - claude-code
  - codex
  - agent
  - autonomous
  - status/active
source:
  - "https://www.youtube.com/watch?v=9oOZ3PB6n4Y"
  - "https://github.com/nousresearch/hermes-agent"
author: "David / BNY（视频）; Hermes Agent: NousResearch"
created: 2026-05-17
updated: 2026-05-17
description: Hermes Agent 的 /go 目标功能详解，对比 Codex /goal，演示 CEO/CTO 编排模式
level: intermediate
stars: 4
note: 视频含商业推广（Hostinger 赞助、New Society 课程推广），笔记已过滤营销内容
---

# Hermes Agent /go Goal 功能完全指南

> /go 是 Hermes Agent 内置的目标驱动自主循环命令。给它一个明确的完成标准，它会自主执行、验证、修正，直到达标——期间可随时注入子目标修正方向。

---

## 目录

1. [从 Ralph Loop 到 /go 的演进](#从-ralph-loop-到-go-的演进)
2. [Hermes /go vs Codex /goal](#hermes-go-vs-codex-goal)
3. [/go 核心机制：Judge 评判系统](#go-核心机制judge-评判系统)
4. [CEO/CTO 编排模式](#ceocto-编排模式)
5. [实战案例：自动生成 PPT](#实战案例自动生成-ppt)
6. [实战案例：CEO 编排 CTO+CMO 建应用](#实战案例ceo-编排-ctocmo-建应用)
7. [社区真实用例](#社区真实用例)
8. [Goal 指令编写原则](#goal-指令编写原则)
9. [设置要点](#设置要点)

---

## 从 Ralph Loop 到 /go 的演进

```
2026.01 ── Ralph Wigum Loop（简单循环）
│  AI → 保存文件 → 重新运行 → AI → ...
│  ❌ 无评判标准
│  ❌ 无停止条件
│  ❌ 无 token 控制
│  ❌ 纯无限循环
│
▼
2026.04.30 ── OpenAI Codex /goal
│  给定目标 + 测试方法 → AI 自主循环
│  ✅ 有停止条件
│  ⚠️  无独立评判
│  ⚠️  无子目标注入
│
▼
2026.05 ── Hermes Agent /go
   ✅ 独立 Judge 评判
   ✅ 子目标注入
   ✅ Turn 限制
   ✅ 完整工具链集成
   ✅ 崩溃恢复
   ✅ 会话持久化
```

---

## Hermes /go vs Codex /goal

| 维度 | Hermes /go | Codex /goal |
|------|-----------|-------------|
| 评判机制 | ✅ 独立 Judge Agent | ❌ 自我评判 |
| Judge 输入 | Goal 文本 + 最近 4KB Agent 响应 + System Prompt | — |
| 子目标 | ✅ /subgoal 随时注入 | ❌ 不支持 |
| Turn 限制 | ✅ 可配置 | ✅ 可配置 |
| 崩溃恢复 | ✅ /go resume 恢复 | ❌ 不支持 |
| 会话持久化 | ✅ 跨会话 | ❌ 单会话 |
| 工具链 | ✅ 完整（Skills、MCP、LSP、Terminal） | ⚠️ 仅 Shell + File Edit |
| Checkpoint/Rollback | ✅ 内置 | ❌ 不支持 |
| 快速启动 | ✅ 内置 /go 命令 | ✅ 内置 /goal 命令 |
| Token 报告 | ✅ 详细 | ❌ 不报告 |

**核心差异**：Hermes 有独立 Judge Agent 做评判，执行 Agent 和 Judge 解耦，避免「自己判自己」的偏差。

---

## /go 核心机制：Judge 评判系统

```
/go 执行循环:

  用户输入 /go "目标描述"
       │
       ▼
  ┌─────────────┐     ┌─────────────┐
  │  执行 Agent  │────►│ Judge Agent  │
  │  (干活)      │     │ (评判)       │
  └──────┬──────┘     └──────┬──────┘
         │                   │
         │            达标？
         │            ┌───┴───┐
         │           Yes     No
         │            │       │
         │            ▼       ▼
         │        交付    继续执行
         │                 │
         └─────────────────┘
              (循环)
```

**Judge 的三个输入**：
1. **Goal 文本** — 用户设定的完成标准
2. **Agent 响应** — 最近 4KB 的执行输出
3. **System Prompt** — 系统上下文

**Judge 的两个判断**：
- 执行方向是否正确？
- 任务是否已完成？

**关键特性**：
- Judge 与执行 Agent 完全独立，无偏差
- 可用 `/go status` 查看进度
- 可用 `/subgoal` 在运行中注入新标准
- 可用 `/go resume` 从中断恢复

---

## CEO/CTO 编排模式

这是 /go 的高级用法：Hermes 作为 CEO 编排，Codex CLI 作为 CTO 执行。

```
你（老板）
  │
  ▼
Hermes Agent (CEO)
  ├── /go 启动目标循环
  ├── 解读任务，制定计划
  ├── 通过 Codex Skill 生成子 Agent
  │     │
  │     ▼
  │   Codex CLI (CTO) ── 子 Agent 1: 后端开发
  │   Codex CLI (CTO) ── 子 Agent 2: 前端/营销
  │     │
  │     ▼
  │   Judge 评判 → 是否达标？
  │     │
  │     ▼
  └── 交付结果
```

**为什么这个模式强大**：
- Hermes 可从 WhatsApp/Discord/Telegram 触发
- 不需要你懂技术，Hermes 会处理
- 多个 Codex 子 Agent 并行工作
- Hermes 持续监控进度

---

## 实战案例：自动生成 PPT

**Goal 指令**：
```
/go Follow the openrouter image generation skill.
Use the Midjourney reference image to create a
5-slide presentation. Do not stop working until
the full presentation with text on each slide
is finished.
```

**执行过程**：
1. Hermes 读取 openrouter-image-generation Skill
2. 下载 Midjourney 参考图
3. 用 GPT Images 2 生成风格一致的图片
4. 创建带文本的 PPT 幻灯片
5. Judge 验证每张幻灯片都有文字
6. 17 分钟完成，5 张幻灯片

**技术栈**：
- 图片生成：OpenRouter → GPT Images 2
- PPT 生成：Python (python-pptx)
- 风格参考：Midjourney 探索页图片

---

## 实战案例：CEO 编排 CTO+CMO 建应用

**Goal 指令**：
```
/go You are the CEO. Spawn two Codex sub agents
via the autonomous-agents/codex skill.

CTO: Build a modern Next.js team weekly reporting app.
Clear outcome: Team members submit weekly reports
with wins, blockers, plans, mood.

CMO: Build a B2B launch campaign strategy document,
free blog post, 2 goal emails.

Goal criteria: Both repos committed with all
required deliverables. CTO integrates the marketing
blog post into the app.
```

**执行过程**：
1. Hermes 读取 codex Skill
2. 自动安装 Codex CLI
3. 复制 ChatGPT 认证到 Codex
4. 生成两个 Codex 子 Agent
5. CTO Agent 构建 Next.js 周报应用
6. CMO Agent 构建营销策略 + 博客文章
7. CTO 将博客集成到应用中
8. Judge 验证所有交付物

**运行中注入子目标**：
```
/subgoal Make the app design like 1990s computer terminal
```

---

## 社区真实用例

### 1. 数据回填（Ken Lee）
- 工具：Codex /goal
- 场景：睡觉时跑了 3 小时
- 结果：回填近 17,000 条记录
- 价值：节省数百小时人工

### 2. 克隆付费应用（Kevin）
- 工具：Claude Code /goal
- 场景：克隆 Canon Webcam Pro 应用
- 指令：「Keep going until we have a working copy that connects my Canon R6」
- 结果：5 分钟内完成克隆
- 关键：明确的「连接 Canon R6」完成标准

### 3. 端到端测试（iOS 开发者）
- 工具：Codex /goal + Hermes /go
- 场景：自主测试 iOS 应用每个功能
- 运行时间：数小时
- 验证方式：模拟器 + 浏览器

### 4. 销售（农场主案例）
- 工具：Codex /goal
- 场景：卖掉农场过剩农产品
- 过程：AI 自动联系潜在客户
- 结果：实际完成销售

---

## Goal 指令编写原则

```
❌ 差的 Goal:
"Build a great app"
→ 不具体、不可衡量、无完成标准

⚠️ 一般的 Goal:
"Build a Next.js app with team reporting"
→ 有方向但缺完成标准

✅ 好的 Goal:
"Build a Next.js team weekly reporting app.
Clear outcome: Team members submit weekly reports
with wins, blockers, plans, mood.
Goal criteria: All tests passing, /blog route
accessible with marketing content integrated."
→ 有方向 + 有交付物 + 有完成标准
```

**四要素**：
- ✅ **角色定义** — Agent 扮演什么
- ✅ **明确交付物** — 具体产出什么
- ✅ **可衡量的完成标准** — 什么算「做完」
- ✅ **测试/验证方式** — 怎么验证达标

**关键原则**：目标必须是**可衡量的**（measurable），Judge 才能判断进度。模糊目标 = 无限循环。

---

## 设置要点

### 快速安装
```bash
# Hermes 一键安装（官方 GitHub）
# 安装所有依赖：后端、库、包
curl -fsSL https://raw.githubusercontent.com/nousresearch/hermes-agent/main/install.sh | bash
```

### 连接 ChatGPT 订阅（免费使用）
1. 运行 `hermes` 进入 quick setup
2. 选择 OpenAI provider
3. 在浏览器中登录 ChatGPT 账号
4. 输入终端显示的 9 位验证码
5. 选择模型（如 GPT-4.5）
6. 完成 — 用 ChatGPT 订阅额度驱动 Hermes

### 配置外部 API Key
```bash
# 安全存储 API Key（不进入 context window）
hermes config set openrouter_api_key "sk-or-xxx"

# 验证
hermes config list
```

### /go 常用命令
```bash
/go "你的目标"        # 启动目标循环
/go status             # 查看当前进度
/subgoal "补充标准"     # 运行中注入子目标
/go resume             # 从中断恢复
```

---

## 总结

/go 的本质是把 AI Agent 从「工具」提升为「自主工作者」。核心差异化：

| 能力 | 价值 |
|------|------|
| 独立 Judge | 避免自我评判偏差 |
| 子目标注入 | 运行中修正方向 |
| 崩溃恢复 | 长时间运行不丢进度 |
| 完整工具链 | 不只是 Shell，有 MCP/Skills/LSP |
| CEO/CTO 模式 | 一个 Agent 编排多个 Agent |

**最重要的一点**：写好 Goal 的完成标准。可衡量 = 有效，模糊 = 浪费 token。

---

## 参考资料

- [Hermes Agent GitHub](https://github.com/nousresearch/hermes-agent)
- [YouTube: David /go 完全指南](https://www.youtube.com/watch?v=9oOZ3PB6n4Y)
- [OpenRouter](https://openrouter.ai/)

## 相关笔记

- [[../../100-InBox/AI-WORKFLOW/Claude Code vs Codex Goal 目标功能对比]]
- [[../../100-InBox/AI/Google Antigravity - Agent-First IDE 开发平台]]
- [[AI 编码 Agent 生态]]
