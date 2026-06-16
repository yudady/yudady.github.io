---
title: 为何有了Skill，还需要MCP？
aliases: [Skill vs MCP, Agent Skill与MCP对比]
tags:
  - ai-agent
  - mcp
  - agent-skills
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=xxn2qLw5y-M"
author: 白白说大模型
created: 2026-06-16
updated: 2026-06-16
description: |
  Skill 和 MCP 是 AI Agent 扩展能力的两大框架，分别解决"怎么想"和"能做到"的问题。本笔记基于视频主题 + 外部资料综合整理。
level: beginner
stars: 4
note: 无字幕，基于视频元数据 + 外部资料综合整理
---

# 为何有了 Skill，还需要 MCP？

> Skill 和 MCP 分别赋予 AI Agent 思考的"大脑"和行动的"双手"。两者不是竞争关系，而是天生的最佳合作伙伴。适合想搞清楚 Agent 扩展技术栈的开发者阅读。

## 目录

- [[#核心类比：专业手册 vs USB-C 接口]]
- [[#本质差异：教 AI「怎么想」vs 让 AI「能做到」]]
- [[#架构对比：渐进式披露 vs 急切加载]]
- [[#开发门槛：写文档 vs 写代码]]
- [[#协同工作：Skill 指挥 MCP]]
- [[#选择决策树：什么时候用哪个]]
- [[#与 Function Calling 的关系]]

---

## 核心类比：专业手册 vs USB-C 接口

理解 Skill 和 MCP 最快的方式——类比。

| 维度 | Skill（技能） | MCP（协议） |
|------|--------------|-------------|
| 本质 | Prompt 模板注入 | 开放通信协议 |
| 类比 | 给 AI 一本「专业手册」 | 给 AI 一个「USB-C 接口」 |
| 作用 | 教 AI 怎么做（知识 / 流程） | 让 AI 能做什么（工具 / 数据） |
| 数据来源 | 本地文件系统（静态） | 外部 API / 数据库（实时） |
| 开发难度 | 写 Markdown 即可 | 需编程开发 Server |
| 扩展边界 | 增强 AI 认知能力 | 连接无限外部世界 |

**一句话**：Skill 是「内部知识化」，MCP 是「外部工具化」。

---

## 本质差异：教 AI「怎么想」vs 让 AI「能做到」

两者作用于 AI 的不同层面，是两种不同的哲学：

```
Skill → 增强 AI 的内在认知（怎么分析、怎么决策、怎么输出）
MCP  → 扩展 AI 的外在能力（能读数据库、能调 API、能操作文件）

具体例子：
┌─────────────────────────────────────────────┐
│ 任务：查询公司 Q3 销售数据并生成分析报告      │
├─────────────────────────────────────────────┤
│ MCP 提供：连接数据库的管道                    │
│         → SELECT * FROM sales WHERE ...      │
│                                             │
│ Skill 提供：分析报告的模板和规范               │
│         → "先用 3 句话概括趋势"               │
│         → "再用表格对比各产品线"               │
│         → "最后给出 3 条可执行建议"           │
└─────────────────────────────────────────────┘
```

Anthropic 官方的表述最精准：

> MCP connects Claude to **data**; Skills teach Claude **what to do** with that data.

---

## 架构对比：渐进式披露 vs 急切加载

这是两者在设计哲学上最被忽视但极其重要的差异。

### Skill：渐进式披露（Progressive Disclosure）

Skill 采用类似「按需翻手册」的三级加载策略：

```
Skill 加载流程：
┌──────────┐    ┌──────────────┐    ┌───────────────┐
│ 元数据    │ →  │ 完整指令      │ →  │ 关联文件/脚本  │
│ ~100 tok │    │ < 5000 tok   │    │ 仅在需要时加载  │
└──────────┘    └──────────────┘    └───────────────┘
     ↑               ↑                    ↑
  每次对话都加载    匹配时才加载        执行时才加载
```

类比：招新员工第一天，先给一份岗位说明（元数据），遇到具体问题时再告诉他翻哪本手册的哪一页（完整指令 + 关联文件）。

### MCP：急切加载（Eager Loading）

MCP 在对话开始时就预加载所有工具定义：

```
MCP 加载流程：
┌──────────────────────────────────────────────────┐
│ 所有工具定义一次性注入 context                      │
│                                                    │
│ 工具 1: search_web(query, limit)        ~500-800 tok │
│ 工具 2: read_database(sql)             ~500-800 tok │
│ 工具 3: send_slack(channel, message)   ~500-800 tok │
│ ...                                              │
│ 一个 MCP Server 通常 10-20 个工具                    │
│ 总消耗：5,000 - 16,000 tokens（固定成本）            │
└──────────────────────────────────────────────────┘
```

### Token 效率对比

| 场景 | Skill | MCP |
|------|-------|-----|
| 只需要 1 个工具时 | 只消耗相关 Skill 的 ~100 tok | 消耗全部工具定义 5000-16000 tok |
| 有 20 个 Skill 时 | 只加载匹配的那个 | 需要定义所有 20 个工具 |
| 实际案例 | 500 行 Python 脚本 → Skill 运行仅 ~50 tok 输出 | Anthropic 用「代码执行 + MCP」将 150K tok 压缩到 2K tok |

---

## 开发门槛：写文档 vs 写代码

| 维度 | Skill | MCP |
|------|-------|-----|
| 开发者 | 任何人（产品经理、运营、领域专家） | 开发者（需懂 auth、transport、CLI） |
| 技术要求 | 会写 Markdown | 需编程（Python/TS/Go） |
| 分发方式 | 复制文件即可 | 需要部署 Server 进程 |
| 运维成本 | 零（纯文件） | 需维护 Server 运行状态 |

Skill 的极低门槛意味着：团队中的任何人都可以将知识沉淀为 AI 可复用的技能，无需开发资源。

---

## 协同工作：Skill 指挥 MCP

两者不是二选一，而是组合使用：

```
完整工作流示例：处理一张发票

┌─────────┐     ┌─────────────┐     ┌──────────────┐
│ 用户请求 │ ──→ │ Skill: 发票  │ ──→ │ MCP: OCR 识别 │
│ 处理发票  │     │ 处理规范     │     │ MCP: 数据库   │
│          │     │             │     │ MCP: 财务系统  │
└─────────┘     └─────────────┘     └──────────────┘
                      │                     │
                      ▼                     ▼
              "先提取金额"          返回：金额 $1,230
              "再验证发票号"         返回：发票号 INV-2024-089
              "最后按模板输出"       返回：格式化报告
                      │                     │
                      └──────────┬──────────┘
                                 ▼
                          格式化分析报告
```

**Skill 提供推理框架，MCP 提供数据访问**——这是最有效的使用模式。

---

## 选择决策树

```
你需要什么？
│
├─ 教 AI 遵循特定流程/规范？
│   ✅ 代码 Review 标准
│   ✅ 数据分析模板
│   ✅ 输出格式规范
│   → 选 Skill
│
├─ 让 AI 连接外部系统？
│   ✅ 读取数据库
│   ✅ 调用第三方 API
│   ✅ 操作文件系统
│   → 选 MCP
│
├─ 领域知识快速变化？
│   ✅ SDK 文档频繁更新
│   ✅ API 接口迭代快
│   → 选 MCP（自动同步最新信息）
│
├─ 团队成员都需要贡献 AI 知识？
│   ✅ 非技术人员参与
│   ✅ 低门槛分发
│   → 选 Skill
│
└─ 需要同时具备？
    → Skill + MCP 组合使用
    Skill 定义「怎么做」，MCP 提供「能连什么」
```

### 简化版对照

| 你的需求 | 用 Skill | 用 MCP |
|----------|---------|--------|
| 注入领域知识 | **是** | |
| 连接外部数据 | | **是** |
| 标准化工作流程 | **是** | |
| 实时 API 调用 | | **是** |
| 低门槛快速落地 | **是** | |
| 跨应用通用集成 | | **是** |

---

## 与 Function Calling 的关系

```
AI Agent 工具演进路线：

Function Calling  →  Tool Call  →  MCP  →  Skills
(单体)            (标准化)     (协议层)  (知识层)
                                              │
                                              ▼
                                    核心变化：工程实践和
                                    表现形式的优化演进
```

| 维度 | Function Calling / Tool Call | MCP | Skills |
|------|------|-----|--------|
| 归属 | 各家模型各自实现 | 开放标准（Anthropic 发布） | Anthropic 发布 |
| 连接范围 | 仅限当前模型 | 跨模型、跨应用通用 | 本地知识 |
| 解决问题 | AI 能调用函数 | AI 能连接任何外部系统 | AI 知道怎么用工具 |

---

## 参考资料

- [一张图读懂 Skills 与 MCP - 腾讯云](https://cloud.tencent.com/developer/article/2624372)
- [How Skills compares to prompts, Projects, MCP - Claude Blog](https://claude.com/blog/skills-explained)
- [Skills vs MCP tools for agents - LlamaIndex](https://www.llamaindex.ai/blog/skills-vs-mcp-tools-for-agents-when-to-use-what)
- [MCP 和 Skills 到底什么区别 - 葡萄城开发者空间](https://grapecity.csdn.net/69a69e3254b52172bc5ebd14.html)
- [Agent Skills 与 MCP：智能体能力扩展的两种范式 - Datawhale](https://github.com/datawhalechina/hello-agents/blob/main/Extra-Chapter/Extra05-AgentSkills解读.md)
- [MCP vs Skills: Choosing the right context - Red Hat](https://developers.redhat.com/articles/2026/05/25/mcp-servers-vs-skills-choosing-right-context-your-ai)
