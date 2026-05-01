---
in: null
title: Claude-Mem：Claude Code 持久化记忆系统完整指南
aliases: [Claude-Mem：Claude Code 持久化记忆系统完整指南, "claude code 记忆插件", "Claude-Mem：Claude Code 持久化记忆系统完整指南", claude memory plugin, claude-mem]
tags: ["系统指南", "持久化记忆", ai-agent, chromadb, Claude Code, claude-code, context-engineering, memory-system, plugin, sqlite]
source: [https://docs.claude-mem.ai, https://github.com/thedotmack/claude-mem]
author: Alex Newman (@thedotmack)
created: 2026-02-23 10:30
updated: 2026-03-07 22:06
description: "Claude-Mem 是专为 Claude Code 设计的持久化记忆压缩系统。通过 5 个生命周期钩子自动捕获操作，使用三层渐进式披露架构节省 90-95% Token，实现 Claude 跨会话记忆保持，解决 AI 失忆问题。"
level: intermediate
stars: 5
category: "技术"
summary: "Claude-Mem 持久化记忆系统完整指南"
tags:
  - status/active
  - area/distill
  - type/doc
---

# Claude-Mem：Claude Code 持久化记忆系统完整指南

## 目录

- [1. 项目概述](#1-项目概述)
- [2. 核心架构](#2-核心架构)
- [3. 安装与配置](#3-安装与配置)
- [4. 三层渐进式披露](#4-三层渐进式披露)
- [5. MCP 搜索工具](#5-mcp-搜索工具)
- [6. 隐私控制](#6-隐私控制)
- [7. 方案对比](#7-方案对比)
- [8. 性能数据](#8-性能数据)
- [9. 最佳实践](#9-最佳实践)
- [10. 参考资料](#10-参考资料)

---

## 1. 项目概述

### 1.1 什么是 Claude-Mem？

**Claude-Mem** 是专为 [Claude Code](https://claude.com/claude-code) 设计的**持久化记忆压缩系统**，通过事件驱动架构自动捕获工具调用、生成语义摘要，并在后续会话中注入相关上下文，实现**跨会话记忆保持**。

| 项目信息 | 详情 |
|---------|------|
| **GitHub** | [thedotmack/claude-mem](https://github.com/thedotmack/claude-mem) |
| **版本** | v6.5.0 / v10.3.1 |
| **协议** | AGPL-3.0 |
| **Stars** | 23,400+ ⭐ |
| **官方文档** | [docs.claude-mem.ai](https://docs.claude-mem.ai) |
| **主要语言** | TypeScript (83.5%) |

### 1.2 解决的问题

```
┌─────────────────────────────────────────────────────────────┐
│                     传统 Claude Code                         │
│  ┌─────────┐     ┌─────────┐     ┌─────────┐               │
│  │ 会话 1  │ → │ 会话 2  │ → │ 会话 3  │               │
│  │ 背景: A │     │ 背景: ? │     │ 背景: ? │               │
│  └─────────┘     └─────────┘     └─────────┘               │
│        ↓               ↓               ↓                    │
│   记忆丢失        记忆丢失        记忆丢失                   │
└─────────────────────────────────────────────────────────────┘
                              ↓ 安装 Claude-Mem
┌─────────────────────────────────────────────────────────────┐
│                     使用 Claude-Mem                          │
│  ┌─────────┐     ┌─────────┐     ┌─────────┐               │
│  │ 会话 1  │ → │ 会话 2  │ → │ 会话 3  │               │
│  │ 背景: A │     │ 背景: A │     │ 背景: A │               │
│  └────┬────┘     └────┬────┘     └────┬────┘               │
│       ↓               ↓               ↓                    │
│   自动存储 ────→ 自动注入 ────→ 自动注入                    │
└─────────────────────────────────────────────────────────────┘
```

### 1.3 核心特性

| 特性 | 描述 |
|-----|------|
| **🧠 持久化记忆** | 上下文跨会话自动保存 |
| **📊 渐进式披露** | 三层检索，节省 ~90-95% Token |
| **🔍 智能搜索** | 自然语言查询项目历史 |
| **🖥️ Web 可视化** | http://localhost:37777 |
| **🔒 隐私保护** | `<private>` 标签排除敏感内容 |
| **⚙️ 上下文配置** | 精细控制注入范围 |
| **🤖 自动运行** | 无需手动干预 |
| **🔗 引用系统** | 通过 ID 引用历史观察 |

---

## 2. 核心架构

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────────────┐
│                    Claude Code 会话                          │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│              生命周期钩子系统 (Lifecycle Hooks)              │
│  SessionStart → UserPromptSubmit → PostToolUse → Stop       │
│                      (6 个 hook 脚本)                        │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                   Worker 服务 (后台处理)                     │
│         HTTP API + Web UI (端口 37777)                       │
│         通过 Claude Agent SDK 提取学习内容                   │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                     双数据库存储系统                          │
│  ┌─────────────┐         ┌─────────────┐                   │
│  │   SQLite    │         │  ChromaDB   │                   │
│  │  (结构化)   │  ←────→ │  (向量化)   │                   │
│  │   FTS5全文  │  混合   │  语义搜索   │                   │
│  │    检索     │   搜索   │            │                   │
│  └─────────────┘         └─────────────┘                   │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│            下次会话开始时自动注入相关上下文                   │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 5 个生命周期钩子

| 钩子名称 | 触发时机 | 核心作用 |
|---------|---------|---------|
| **Setup** | 插件安装时 | 依赖检查、环境初始化 |
| **SessionStart** | 会话启动/清空/压缩 | 启动 Worker 服务、注入历史记忆上下文 |
| **UserPromptSubmit** | 用户提交问题时 | 创建会话、保存用户提示词 |
| **PostToolUse** | 工具调用完成后 | 捕获文件读写、代码编辑等操作记录 |
| **Stop** | 会话被用户停止时 | 生成摘要、持久化存储 |

### 2.3 Hooks 配置示例

```json
{
  "hooks": {
    "SessionStart": [
      {
        "matcher": "startup|clear|compact",
        "hooks": [
          {
            "type": "command",
            "command": "node \"${CLAUDE_PLUGIN_ROOT}/scripts/worker-service.cjs\" start"
          },
          {
            "type": "command",
            "command": "node \"${CLAUDE_PLUGIN_ROOT}/scripts/worker-service.cjs\" hook claude-code context"
          }
        ]
      }
    ],
    "PostToolUse": [
      {
        "matcher": "*",
        "hooks": [
          {
            "type": "command",
            "command": "node \"${CLAUDE_PLUGIN_ROOT}/scripts/worker-service.cjs\" hook claude-code observation"
          }
        ]
      }
    ]
  }
}
```

### 2.4 数据库架构

#### SQLite 核心表结构

```sql
-- 会话管理表
CREATE TABLE sdk_sessions (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  content_session_id TEXT UNIQUE NOT NULL,    -- 用户会话 ID
  memory_session_id TEXT UNIQUE,               -- SDK 记忆会话 ID
  project TEXT NOT NULL,
  user_prompt TEXT,
  started_at TEXT NOT NULL,
  status TEXT CHECK(status IN ('active', 'completed', 'failed'))
);

-- 观察记录表（核心记忆）
CREATE TABLE observations (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  memory_session_id TEXT NOT NULL,
  project TEXT NOT NULL,
  type TEXT NOT NULL,           -- decision/bugfix/feature/refactor 等
  title TEXT,
  subtitle TEXT,
  facts TEXT,                   -- JSON 数组
  narrative TEXT,
  concepts TEXT,                -- JSON 数组
  files_read TEXT,              -- JSON 数组
  files_modified TEXT,          -- JSON 数组
  created_at TEXT NOT NULL
);

-- FTS5 全文检索虚拟表
CREATE VIRTUAL TABLE observations_fts USING fts5(
  title, subtitle, narrative, text, facts, concepts,
  content='observations',
  content_rowid='id'
);
```

### 2.5 混合搜索策略

```typescript
interface HybridSearchStrategy {
  fullText: SQLite FTS5,      // 关键词精准匹配
  semantic: Chroma Vector,    // 语义相似度计算
  hybrid: RRF 融合排序        // Reciprocal Rank Fusion
}
```

**工作流程**:
1. **FTS5 全文搜索**: 在 `observations_fts` 表中进行关键词匹配
2. **Chroma 向量搜索**: 对查询文本进行向量化，计算语义相似度
3. **RRF 融合**: 合并两个结果集，按相关性重新排序

---

## 3. 安装与配置

### 3.1 快速安装（3 步完成）

```bash
# 步骤 1: 添加插件市场
/plugin marketplace add thedotmack/claude-mem

# 步骤 2: 安装插件
/plugin install claude-mem

# 步骤 3: 重启 Claude Code
# 重启后，之前会话的上下文会自动出现在新会话中
```

### 3.2 系统要求

| 依赖 | 版本要求 | 说明 |
|-----|---------|------|
| **Node.js** | ≥ 18.0.0 | 运行时环境 |
| **Claude Code** | 最新版 | 支持插件的版本 |
| **Bun** | ≥ 1.0.0 | JavaScript 运行时（自动安装） |
| **uv** | 最新版 | Python 包管理器（自动安装） |
| **SQLite 3** | 内置 | 持久化存储 |

### 3.3 配置文件

**位置**: `~/.claude-mem/settings.json`

```json
{
  "aiModel": "claude-sonnet-4-20250514",
  "workerPort": 37777,
  "dataDirectory": "~/.claude-mem",
  "logLevel": "info",
  "contextConfig": {
    "maxObservations": 50,
    "maxSessions": 10,
    "observationTypes": ["decision", "bugfix", "feature", "refactor"]
  }
}
```

### 3.4 数据存储位置

| 文件/目录 | 位置 |
|----------|------|
| **数据库** | `~/.claude-mem/claude-mem.db` |
| **向量库** | `~/.claude-mem/chroma/` |
| **日志** | `~/.claude-mem/logs/` |
| **配置** | `~/.claude-mem/settings.json` |
| **插件** | `~/.claude/plugins/marketplaces/thedotmack/` |

---

## 4. 三层渐进式披露

### 4.1 架构概述

**渐进式披露**（Progressive Disclosure）是 Claude-Mem 的核心创新，通过**三层检索架构**将 token 成本降低 **90-95%**。

```
┌─────────────────────────────────────────────────────────────┐
│                   三层渐进式披露架构                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Layer 1: search (索引层)                                   │
│  ├── 返回: ID + 标题 + 类型 + 时间                          │
│  ├── Token 成本: ~50-100 tokens/结果                        │
│  └── 目的: 快速过滤定位相关历史                              │
│                       ↓                                     │
│  Layer 2: timeline (时间线层)                               │
│  ├── 返回: 特定事件周围的时间顺序上下文                      │
│  ├── Token 成本: 中等开销                                   │
│  └── 目的: 理解因果关系                                     │
│                       ↓                                     │
│  Layer 3: get_observations (完整详情层)                     │
│  ├── 返回: 完整观察数据和日志                                │
│  ├── Token 成本: ~500-1000 tokens/结果                      │
│  └── 目的: 在需要时访问原始代码和完整日志                    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 Token 节省对比

| 场景 | 传统加载 | 渐进式披露 | 节省比例 |
|------|----------|-----------|---------|
| 10 条记录 | ~5,000-10,000 tokens | ~500-1,000 tokens | **~10x** |
| 100 条记录 | ~50,000-100,000 tokens | ~5,000-10,000 tokens | **~10x** |
| 复杂任务 | ~20,000 tokens | ~3,000 tokens | **~85%** |

### 4.3 工作流示例

```
用户问: "上次我们怎么修复认证超时问题的？"
                ↓
┌──────────────────────────────────────────┐
│ Step 1: search(query="auth timeout")     │
│ 返回: [{id: 123, title: "Fix auth       │
│        timeout", type: "bugfix"}]        │
│ Token: ~100                               │
└──────────────────────────────────────────┘
                ↓
┌──────────────────────────────────────────┐
│ Step 2: timeline(observation_id=123)     │
│ 返回: 修复前后 5 条相关操作的时间线        │
│ Token: ~200                               │
└──────────────────────────────────────────┘
                ↓
┌──────────────────────────────────────────┐
│ Step 3: get_observations(ids=[123])      │
│ 返回: 完整的修复代码和解释                 │
│ Token: ~800                               │
└──────────────────────────────────────────┘
                ↓
        总计: ~1,100 tokens
        (原本需要 ~10,000 tokens)
```

---

## 5. MCP 搜索工具

### 5.1 工具概览

Claude-Mem 提供 **5 个 MCP 工具**：

| 工具名称 | 功能描述 | Token 成本 |
|---------|---------|-----------|
| `search` | 搜索记忆索引，按类型/日期/项目过滤 | ~50-100 tokens/结果 |
| `timeline` | 获取特定观察周围的时间顺序上下文 | 中等 |
| `get_observations` | 按 ID 批量获取完整观察详情 | ~500-1000 tokens/结果 |
| `save_memory` | 手动保存记忆/观察 | - |
| `__IMPORTANT` | 工作流文档（始终对 Claude 可见） | - |

### 5.2 search 工具

```typescript
// 基础搜索
search(query="authentication bug", limit=10)

// 带过滤条件
search(
  query="database",
  type="bugfix",           // 类型过滤
  project="my-project",    // 项目过滤
  dateStart="2026-01-01",  // 日期范围
  dateEnd="2026-02-23",
  limit=20
)

// 按概念搜索
search(
  query="optimization",
  obs_type="decision,feature"  // 多类型
)
```

**返回格式**:
```
| ID | Time | T | Title | Read |
|----|------|---|-------|------|
| #11131 | 3:48 PM | 🟣 | Added JWT authentication | ~75 |
| #10942 | 2:15 PM | 🔴 | Fixed auth token expiration | ~50 |
```

### 5.3 timeline 工具

```typescript
// 以特定观察为中心
timeline(anchor=11131, depth_before=3, depth_after=3)

// 从查询自动查找锚点
timeline(query="authentication", depth_before=5, depth_after=5)
```

### 5.4 get_observations 工具

```typescript
// 批量获取（推荐）
get_observations(ids=[11131, 10942, 10855])

// 带排序
get_observations(ids=[11131, 10942], orderBy="date_desc")
```

**重要**: 始终批量获取多个 ID，避免 N 次请求。

### 5.5 save_memory 工具

```typescript
// 手动保存重要信息
save_memory(
  text="API requires auth header X-API-Key",
  title="API Auth",
  project="my-project"
)
```

### 5.6 mem-search Skill

除了 MCP 工具，Claude-Mem 还提供 **mem-search Skill**，支持自然语言查询：

```
用户: "上次我们修改了哪些数据库表结构？"
用户: "这个错误之前遇到过吗？"
用户: "上周修改了哪些 API 接口？"
```

**10 种搜索模式**:
1. 全文搜索观察
2. 搜索 Session 总结
3. 搜索用户 Prompt
4. 按概念搜索
5. 按文件搜索
6. 按操作类型搜索
7. 最近上下文搜索
8. 时间线搜索
9. 混合搜索
10. API 文档搜索

---

## 6. 隐私控制

### 6.1 双标签系统

Claude-Mem 提供 **两种隐私标签**：

| 标签 | 作用 | 使用场景 |
|-----|------|---------|
| `<private>` | **完全排除存储** | API 密钥、密码、敏感配置 |
| `<no-summary>` | 记录但不生成摘要 | 临时调试信息、不太敏感的内容 |

### 6.2 使用示例

```xml
<!-- 完全排除 -->
<private>
API_KEY=sk-xxxxx
DATABASE_PASSWORD=secret123
</private>

<!-- 记录但不摘要 -->
<no-summary>
临时调试日志，不需要长期记忆
</no-summary>
```

### 6.3 实现原理

```
用户输入
    ↓
┌─────────────────────────────────┐
│ Hook 层（边缘处理）              │
│ 解析 <private> <no-summary> 标签 │
└─────────────────────────────────┘
    ↓ 标签剥离后的内容
┌─────────────────────────────────┐
│ Worker 服务                     │
│ AI 压缩 + 语义提取               │
└─────────────────────────────────┘
    ↓
┌─────────────────────────────────┐
│ 数据库存储                       │
│ SQLite + ChromaDB               │
└─────────────────────────────────┘
```

---

## 7. 方案对比

### 7.1 Claude-Mem vs 其他记忆插件

| **特性** | **Claude-Mem** | **Memorix** | **MemContext** |
|---------|--------------|------------|---------------|
| **GitHub Stars** | 23,400+ | 1,500+ | 未公开 |
| **支持工具** | 仅 Claude Code | 7 种 IDE | Claude/Cursor |
| **跨工具共享** | ❌ | ✅ | ✅ |
| **Token 效率** | 95% 节省 | 10 倍节省 | 72% 节省 |
| **Web UI** | ✅ | ✅ | ❌ |
| **隐私保护** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **配置难度** | 低 | 中 | 高 |

### 7.2 Claude-Mem vs CLAUDE.md

| **特性** | **Claude-Mem** | **CLAUDE.md** |
|---------|--------------|--------------|
| **本质** | 动态记忆系统 | 静态文档 |
| **更新方式** | 自动捕获 | 手动编写 |
| **检索能力** | 智能语义搜索 | 顺序读取 |
| **Token 效率** | 极高 | 中等 |
| **适用场景** | 过程记录、决策追溯 | 项目规范、架构说明 |

**最佳实践**: 两者结合使用
- **CLAUDE.md**: 存储项目规范、架构决策等静态知识
- **Claude-Mem**: 自动捕获开发过程中的动态决策和问题解决

### 7.3 选型决策树

```
开始
├─ 仅使用 Claude Code?
│  ├─ 是 → Claude-Mem (最佳选择)
│  └─ 否 → 需要跨工具?
│     ├─ 是 → Memorix
│     └─ 否 → 单独为每种工具选方案
│
├─ 需要团队协作?
│  ├─ 是 → Cursor Memories
│  └─ 否 → Claude-Mem
│
├─ Token 预算有限?
│  ├─ 是 → Claude-Mem (95% 节省)
│  └─ 否 → 任何方案都可以
│
└─ 追求极致简洁?
   └─ CLAUDE.md (基础必备)
```

---

## 8. 性能数据

### 8.1 GitHub 社群数据

| 指标 | 数值 |
|------|------|
| **Stars** | 23,400+ ⭐ |
| **Forks** | 1,600+ |
| **Open Issues** | ~50-200 |
| **贡献者** | 活跃社区 |
| **许可证** | AGPL-3.0 |
| **主要语言** | TypeScript (83.5%) |

### 8.2 Token 节省效率

| 模式 | Token 节省率 |
|------|-------------|
| **常规模式**（三层渐进式披露） | **90%** |
| **无尽模式**（Endless Mode，测试中） | **95%** |

### 8.3 性能基准

| 配置 | 启动时间 | 内存占用 | 响应延迟 |
|------|----------|----------|----------|
| **默认配置**（50 观察，10 会话） | 10-30 ms | ~200MB | 几乎无感知 |
| **高负载配置**（200 观察，50 会话） | 略高 | 更高 | 仍然可用 |

### 8.4 用户反馈

**正面评价** ✅
- "再也不用每个新会话都重新交代背景"
- "客户满意度提升 42%"
- "Claude 变成了那个『对项目背景比你还熟』的搭档"

**潜在问题** ⚠️
- 有用户反映"压缩后重新认识这个世界"
- 需要多个依赖（Node.js、Bun、uv）
- Beta 功能可能不稳定

---

## 9. 最佳实践

### 9.1 推荐配置

```json
// ~/.claude-mem/settings.json
{
  "aiModel": "claude-sonnet-4-20250514",
  "contextConfig": {
    "maxObservations": 50,
    "maxSessions": 10,
    "observationTypes": ["decision", "bugfix", "feature", "discovery"]
  }
}
```

### 9.2 使用 `<private>` 标签

```xml
<!-- 敏感信息必须标记 -->
<private>
AWS_ACCESS_KEY=AKIAxxx
AWS_SECRET_KEY=xxx
</private>
```

### 9.3 定期清理

```bash
# 查看日志
npm run worker:logs

# 检查队列
npm run queue

# 清理失败队列
npm run queue:clear
```

### 9.4 与 CLAUDE.md 配合

```
项目根目录/
├── CLAUDE.md          # 静态规范（手动维护）
│   - 项目架构
│   - 编码规范
│   - 环境配置
│
└── ~/.claude-mem/     # 动态记忆（自动维护）
    - 开发决策
    - Bug 修复记录
    - 问题解决方案
```

### 9.5 Web Viewer 使用

访问 http://localhost:37777 可以：
- 实时查看记忆流
- 自然语言搜索
- 切换稳定版/Beta 版
- 配置设置

---

## 10. 参考资料

### 官方资源

- **GitHub 仓库**: [github.com/thedotmack/claude-mem](https://github.com/thedotmack/claude-mem)
- **官方文档**: [docs.claude-mem.ai](https://docs.claude-mem.ai)
- **官方网站**: [claude-mem.ai](https://claude-mem.ai)
- **NPM 包**: [npmjs.com/package/claude-mem](https://www.npmjs.com/package/claude-mem)

### 技术文档

- [架构概览](https://docs.claude-mem.ai/architecture/overview)
- [Hooks 参考](https://docs.claude-mem.ai/architecture/hooks)
- [搜索架构](https://docs.claude-mem.ai/architecture/search-architecture)
- [Worker 服务](https://docs.claude-mem.ai/architecture/worker-service)
- [数据库 Schema](https://docs.claude-mem.ai/architecture/database)

### 社区资源

- **Twitter**: [@Claude_Memory](https://x.com/Claude_Memory)
- **Discord**: [Join Discord](https://discord.com/invite/J4wttp9vDu)
- **作者**: [@thedotmack](https://github.com/thedotmack)

### 中文教程

- [Claude-Mem 完整指南](https://m.blog.csdn.net/lintser/article/details/156858727)
- [Claude Code 持久化记忆插件完全指南](https://m.blog.csdn.net/u013134676/article/details/157902786)
- [AI 编程节省 95% token，记忆系统登顶 GitHub](https://m.sohu.com/a/985092125_121207965/)

---

## 总结

Claude-Mem 是一款**成熟度高、社区活跃**的 Claude Code 记忆插件：

| 优势 | 说明 |
|-----|------|
| **Token 节省 90-95%** | 三层渐进式披露架构 |
| **全自动运行** | 5 个生命周期钩子自动捕获 |
| **本地存储** | 隐私安全，无云端风险 |
| **自然语言搜索** | mem-search Skill 支持 |
| **Web 可视化** | localhost:37777 实时查看 |

**适合人群**: 使用 Claude Code 进行长期项目开发的个人开发者

**安装命令**:
```bash
/plugin marketplace add thedotmack/claude-mem
/plugin install claude-mem
```

---

*Built with Claude Agent SDK | Powered by Claude Code | Made with TypeScript*

## 相关笔记
- [[004-ai-tools/claude/claude|Claude Code]]
- [[03-knowledge/技术/编程/Claude-Code-MCP完整指南|MCP 指南]]
- [[study-notes/AI编程GSD工作流完全指南|GSD 工作流]]
- [[03-knowledge/技术/编程/Claude-Code-完整指南-v2|完整指南]]