---
title: PrintingPress - AI Agent 原生的 CLI 生成器
aliases: [CLI Printing Press, Printing Press]
tags:
  - ai-agent
  - cli
  - claude-code
  - mcp
  - tool-generation
  - status/active
source:
  - "https://www.youtube.com/watch?v=48yu2garzl0"
  - "https://github.com/mvanhorn/cli-printing-press"
  - "https://printingpress.dev"
author: Michael Van Horn (mvanhorn)
created: 2026-05-17
updated: 2026-05-17
description: 从 API 文档或网站自动生成 token 高效的 Go CLI + Claude Code Skill + MCP Server，专为 AI Agent 设计
level: intermediate
stars: 4
note: 无字幕，基于视频元数据 + GitHub README + 官网综合整理
---

# PrintingPress - AI Agent 原生的 CLI 生成器

> 大多数你想让 Claude Code 操作的网站都没有公开 API。MCP 的 token 成本太高，API 的 JSON 回传太笨重——agent 需要一个更干净的方式。PrintingPress 就是这个答案。

---

## 目录

1. [核心问题与定位](#核心问题与定位)
2. [工作原理](#工作原理)
3. [安装与使用](#安装与使用)
4. [Pipeline 流水线](#pipeline-流水线)
5. [设计哲学：非显而易见的洞察 (NOI)](#设计哲学非显而易见的洞察-noi)
6. [创造力阶梯](#创造力阶梯)
7. [生成的产物](#生成的产物)
8. [Agent-First 设计特性](#agent-first-设计特性)
9. [社区 CLI 库](#社区-cli-库)
10. [与其他方案的对比](#与其他方案的对比)

---

## 核心问题与定位

AI Agent 操作网站/服务面临三难：

```
                    ┌─────────────────────┐
                    │  你想让 Agent 操作的  │
                    │   网站或服务         │
                    └──────────┬──────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
         ┌────▼────┐    ┌─────▼─────┐   ┌─────▼─────┐
         │ 有 API  │    │ 无 API    │   │ MCP 连接  │
         │ 但笨重  │    │ 需要浏览器│   │ Token贵   │
         └────┬────┘    └─────┬─────┘   └─────┬─────┘
              │               │               │
              │         ┌─────▼─────┐         │
              └────────►│PrintingPress│◄────────┘
                        │  一条命令   │
                        │  全部解决   │
                        └───────────┘
```

**核心价值**：一个命令，从任何输入生成 AI Agent 能高效使用的 CLI。

---

## 工作原理

三种输入方式，一种输出：

| 输入方式 | 参数 | 适用场景 |
|----------|------|----------|
| OpenAPI Spec | `--spec` | 有 API 文档的服务 |
| 网站 URL | 直接传 URL | 无公开 API 的网站 |
| HAR 文件 | `--har` | 从浏览器 DevTools 导出 |

每次运行输出：
- `<api>-pp-cli` — Go 编译的 CLI 二进制
- `<api>-pp-mcp` — MCP Server 二进制
- 研究文档 + 验证报告 + 质量评分

---

## 安装与使用

**前置条件**：Go 1.26.3+、Claude Code

```bash
# 安装二进制
go install github.com/mvanhorn/cli-printing-press/v4/cmd/printing-press@latest

# 安装 Skills（推荐克隆仓库）
git clone https://github.com/mvanhorn/cli-printing-press.git

# 启动 Claude Code 会话
claude --plugin-dir .
```

关键命令：

```bash
/printing-press <app-name>              # 按 API 名生成 CLI
/printing-press https://postman.com     # 指向网站（无需 spec）
/printing-press-reprint notion          # 用最新模型重新生成
/printing-press HubSpot codex           # Codex 模式（省 60% Opus tokens）
/printing-press-polish notion           # 定向修补
/printing-press-publish linear          # 验证 + 打包 + 创建 PR
```

---

## Pipeline 流水线

完整的 9 阶段流水线：

```
Phase 0: Resolve + Reuse     [1-3 min]  复用研究、检测 token、解析 spec/URL
    │
Phase 1: Research Brief      [5-10 min] API 身份、竞品、数据层、产品论点
    │
Phase 1.5: Ecosystem Absorb  [5-10 min] 收录所有 MCP/skill/CLI 特性
    │
Phase 1.7: Browser-Sniff     [2-5 min]  浏览器抓取、HAR 导入、发现溯源
    │
Phase 2: Generate            [1-2 min]  从 spec 生成 Go CLI + MCP Server
    │
Phase 3: Build The GOAT      [10-20 min] 融合所有吸收的特性 + 洞察命令
    │
Phase 4: Shipcheck           [3-8 min]  Dogfood + 验证 + 评分
    │
Phase 5: Live Smoke (可选)   [2-5 min]  只读 API 烟雾测试
```

**关键规则**：Phase 0 无法完成，除非写出 NOI（非显而易见的洞察）。

---

## 设计哲学：非显而易见的洞察 (NOI)

> "每个 API 都有一个秘密身份。它暴露的数据，对其创造者从未设计过的用途是有价值的。"

| API | 表面身份 | 秘密身份 |
|-----|----------|----------|
| Discord | 聊天应用 | 可搜索的知识库；每个 thread = 机构记忆 |
| Linear | Issue 追踪器 | 团队行为观测台；状态变化 = 工作方式信号 |
| Stripe | 支付处理器 | 商业健康监控；扣款失败 = PMF 信号 |
| GitHub | 代码托管 | 工程文化指纹；Review 模式 = 交付能力信号 |
| Notion | 文档编辑器 | 知识衰变检测器；过期页面 = 被遗忘的知识 |
| HubSpot | CRM | 关系记忆；交易流转 = 管线健康信号 |
| Slack | 消息工具 | 组织神经系统；响应时间 = 团队健康信号 |
| ESPN | 体育数据 | 博彩情报终端；伤病/赔率 = 结果预测信号 |

这个理念驱动了 CLI 生成时**不只是包装 API 端点**，而是生成「洞察命令」——让 Agent 能回答 API 本身无法直接回答的问题。

---

## 创造力阶梯

生成的 CLI 不只是 API wrapper，而是一个五层能力栈：

```
Rung 5: 行为洞察 ─── health, similar, composite metrics
           │
Rung 4: 领域分析 ─── stale --days 30, orphans, load
           │
Rung 3: 本地持久化 ─ sync, search, export, tail (SQLite)
           │
Rung 2: 输出格式化 ─ --json, --select, --csv, --dry-run
           │
Rung 1: API 包装 ─── issue create --title "..."
```

> "Rung 3 是及格线。Rung 4 是核心价值。Rung 5 是竞争壁垒。"

---

## 生成的产物

### Agent-First 设计原则

- **管道自动 JSON**：输出被管道时自动 JSON，无需 `--json`
- **`--compact` 模式**：只保留高价值字段，减少 60-80% token
- **类型化退出码**：`0`=成功, `2`=无结果（区分「失败」和「没有」）

### 本地 SQLite 同步

每个生成的 CLI 都内置 SQLite 支持：

```bash
# 同步远端数据到本地
linear-pp-cli sync

# 本地查询（毫秒级）
linear-pp-cli SQL 'blocked_by' 'in_progress' interval '7 days'

# 全文搜索
slack-pp-cli search "deploy pipeline" --days 30
```

**对比**：远程 API 调用 vs 本地 SQLite 查询

| 维度 | 远端 API | SQLite 本地 |
|------|----------|-------------|
| 延迟 | 200-2000ms | <50ms |
| Token 消耗 | 高（完整 JSON） | 低（精简字段） |
| 离线可用 | ❌ | ✅ |
| 复合查询 | 多次 round-trip | 单次 SQL |

---

## 社区 CLI 库

官网已收录 **44 个社区贡献的 CLI**，按类别：

| 类别 | 示例 CLI |
|------|----------|
| Commerce | amazon-seller, ebay, craigslist, shopify, fedex, instacart |
| Project Mgmt | linear, jira, asana, trello, clickup, todoist |
| Productivity | slack, notion, google-workspace, calendar |
| Media | espn, hackernews, movie-goat, recipe-goat |
| Dev | github, sentry, vercel, railway, flyio |
| Travel | flight-goat（航班搜索） |
| Finance | yahoo-finance, plaid |

**亮点案例**：`flight-goat` 能直接在终端查航班、比价；`espn x flight-goat` 能回答"OKC 下场比赛是什么时候，最便宜的往返机票多少"。

---

## 与其他方案的对比

| 维度 | PrintingPress | MCP Server | 直接 HTTP API |
|------|--------------|------------|--------------|
| Token 效率 | 高（--compact 模式） | 低（完整 schema） | 中（需自行格式化） |
| 离线能力 | ✅ SQLite 本地同步 | ❌ | ❌ |
| 复合查询 | ✅ 单条 SQL | ❌ 多次工具调用 | ❌ 多次请求 |
| 生成速度 | ~10-20 分钟 | 手动编写 | N/A |
| 适用范围 | 有/无 API 均可 | 需 API | 需 API |
| 输出格式 | CLI + MCP + Skill | 仅 MCP | 仅数据 |
| 本质 | 代码生成（生成 CLI 代码） | 运行时协议 | 直接调用 |

**关键差异**：MCP 是运行时协议（每次调用都要传 schema），PrintingPress 是编译时生成（生成精简的 CLI 二进制），token 效率差距显著。

---

## 总结

PrintingPress 解决了一个真实痛点：AI Agent 需要高效地操作各种服务，但现有方案（MCP、裸 API）要么 token 成本高，要么需要手写适配。它通过**自动生成** Go CLI + MCP Server + Claude Code Skill，并内置 SQLite 同步和洞察命令，让 Agent 能以最低 token 成本完成最复杂的操作。

**适用场景**：
- 你有一个常用服务想接入 Claude Code
- 服务没有公开 API（通过网站 URL 或 HAR 文件）
- MCP 连接太慢或太贵
- 需要离线查询和复合分析

**不适用**：
- 需要写入/修改操作的场景（当前偏只读）
- 对实时性要求极高的场景（有 sync 延迟）

---

## 参考资料

- [GitHub: mvanhorn/cli-printing-press](https://github.com/mvanhorn/cli-printing-press)
- [官网: printingpress.dev](https://printingpress.dev)
- [社区 CLI 库: printing-press-library](https://github.com/mvanhorn/printing-press-library)
- [MindStudio 教程](https://www.mindstudio.ai/blog/build-cli-claude-code-printing-press/)
- [YouTube: JayLuxAI 介绍视频](https://www.youtube.com/watch?v=48yu2garzl0)

## 相关笔记

- [[Claude Code]]
- [[MCP Model Context Protocol]]
- [[AI Agent 工具生态]]
