---
title: GBrain - AI Agent 个人知识库与混合检索引擎
tags:
  - AI
  - knowledge-base
  - RAG
  - personal-AI
  - agent
  - vector-search
source: https://github.com/garrytan/gbrain
author: garrytan (Garry Tan, YC President & CEO)
created: 2026-04-19
updated: 2026-04-19
description: YC 总裁 Garry Tan 的个人 AI Agent 知识库系统，Postgres + pgvector 混合检索，25 个 skill，开源
level: detailed
stars: 9000
---

# GBrain - AI Agent 个人知识库与混合检索引擎

> [!info] 基本信息
> - **仓库**: https://github.com/garrytan/gbrain
> - **Stars**: 9k | **Forks**: 1k | **Watchers**: 28
> - **语言**: TypeScript 98.7%, PLpgSQL 1.2%, Shell 0.1%
> - **版本**: v0.9.2
> - **协议**: MIT
> - **作者**: Garry Tan（Y Combinator 总裁兼 CEO）
> - **运行时**: Bun
> - **相关**: [OpenClaw](https://openclaw.ai), [Hermes Agent](https://github.com/NousResearch/hermes-agent), GStack

## 一句话定位

为 AI Agent 打造的个人知识库系统 -- Markdown 文件作为数据源，Postgres + pgvector 混合检索，Agent 每次对话前先查知识库、对话后自动更新，形成"越用越聪明"的复利效应。

## 核心解决的问题

AI Agent 很聪明但不了解你的生活。GBrain 通过构建一个可搜索的知识库，让 Agent 在每次交互前查询、交互后更新，实现对人物、公司、会议、想法等个人知识的持续积累和检索。

Garry Tan 的生产数据：**17,888 页面、4,383 人物、723 公司、21 个 cron job 自动运行**。

## 知识模型

每个页面遵循 **compiled truth + timeline** 模式：

```
---
type: person | company | concept | deal | yc | civic | project | source | media
title: ...
tags: [...]
---

（compiled truth：当前最佳理解，随时根据新证据重写）

---

- 2024-01-15: 事件 A
- 2024-03-20: 事件 B
（timeline：仅追加，从不编辑）
```

## 架构设计

```
Brain Repo (git)          GBrain (检索层)         AI Agent (读写)
  markdown 文件  ──────>  Postgres + pgvector  <────>  25 个 skills
  = 数据源                  混合搜索                  定义 HOW 使用 brain
                            vector + keyword + RRF     实体检测/丰富/摄入
```

三层协作：
- **Brain Repo**: git 仓库是系统真相源，人类可直接编辑
- **GBrain**: 检索层，提供混合搜索和知识操作
- **AI Agent**: 通过 25 个 skill 定义何时读、何时写、如何丰富

## 搜索系统

```
Query → Multi-query expansion (Claude Haiku)
         ↓
    Vector (HNSW cosine) + Keyword (tsvector + ts_rank)
         ↓
    RRF Fusion: score = sum(1/(60 + rank))
         ↓
    4-Layer Dedup + Stale alerts
         ↓
    Results
```

三种分块策略：
1. **Recursive** (timeline, 批量导入): 5 级分隔符层级，300 词 + 50 词重叠
2. **Semantic** (compiled truth): 嵌入相邻句子的余弦相似度找主题边界
3. **LLM-guided** (高价值内容): Claude Haiku 滑动窗口识别主题切换

## 技术栈

| 组件 | 技术 |
|------|------|
| 运行时 | Bun |
| 语言 | TypeScript |
| 数据库 | PGLite（本地嵌入式 PG 17.5）或 Supabase（托管 Postgres） |
| 向量搜索 | pgvector + HNSW 索引 (text-embedding-3-large, 1536-dim) |
| 全文搜索 | tsvector + ts_rank + pg_trgm |
| Embedding | OpenAI text-embedding-3-large |
| Query Expansion | Anthropic Claude Haiku |
| MCP | @modelcontextprotocol/sdk (30+ MCP tools) |
| 存储 | S3/R2/Supabase Storage |

## 25 个 Skills（核心能力）

### 常驻 Skills
| Skill | 功能 |
|-------|------|
| signal-detector | 每条消息触发，并行捕获原创想法和实体提及 |
| brain-ops | 任何外部 API 调用前先查 brain，读-丰富-写循环 |

### 内容摄入
| Skill | 功能 |
|-------|------|
| ingest | 路由器，检测输入类型分发到对应摄入 skill |
| idea-ingest | 链接/文章/推文 → brain 页面 + 分析 + 人物页 + 交叉链接 |
| media-ingest | 视频/音频/PDF/书籍/截图/GitHub 仓库 → 转录 + 实体提取 |
| meeting-ingestion | 会议转录 → brain 页面 + 出席者丰富 + 公司时间线 |

### Brain 运维
| Skill | 功能 |
|-------|------|
| enrich | 分层丰富 (Tier 1/2/3)，创建/更新人物/公司页面 |
| query | 3 层搜索 + 合成 + 引用，诚实说"brain 中没有此信息" |
| maintain | 健康检查：过期页面、孤儿页、死链、引用审计 |
| citation-fixer | 扫描缺失/格式错误的引用并修复 |
| publish | 分享 brain 页面为密码保护 HTML |

### 运维操作
| Skill | 功能 |
|-------|------|
| daily-task-manager | 任务生命周期 (P0-P3)，存储为可搜索 brain 页面 |
| daily-task-prep | 晨间准备：日历前瞻 + 每位出席者上下文 |
| cron-scheduler | 调度交错、安静时段、幂等性 |
| cross-modal-review | 第二模型质量门控，拒绝路由 |

### 身份与设置
| Skill | 功能 |
|-------|------|
| soul-audit | 6 阶段面试生成 SOUL.md / USER.md / ACCESS_POLICY.md |
| setup | 自动配置 PGLite 或 Supabase |
| migrate | 从 Obsidian/Notion/Logseq/markdown/CSV/JSON/Roam 迁移 |
| briefing | 每日简报：会议上下文、活跃交易、引用追踪 |

## 集成（数据输入）

| Recipe | 功能 |
|--------|------|
| Voice-to-Brain | 电话 → brain 页面 (Twilio + OpenAI Realtime) |
| Email-to-Brain | Gmail → 实体页面 |
| X-to-Brain | Twitter → brain 页面 |
| Calendar-to-Brain | Google Calendar → 可搜索日页面 |
| Meeting Sync | Circleback 转录 → brain 页面 |
| Credential Gateway | Gmail + Calendar 凭证管理 |

## 存储估算

7,500 页面的 brain：约 750MB（含嵌入和索引）。Supabase Pro ($25/月, 8GB) 是生产起步点。

## 安装方式

1. **Agent 平台**（推荐）：粘贴安装指令给 Agent，30 分钟全自动
2. **Standalone CLI**：`bun add -g github:garrytan/gbrain`
3. **MCP Server**：Claude Code / Cursor / Windsurf 通过 stdio 暴露 30+ tools
4. **TypeScript Library**：`bun add github:garrytan/gbrain`

## 核心命令

```bash
gbrain init                        # 创建 brain (PGLite 默认)
gbrain import ~/brain/ --no-embed  # 导入 markdown
gbrain embed --stale               # 生成向量嵌入
gbrain query "key themes?"         # 混合搜索
gbrain get <slug>                  # 读取页面
gbrain put <slug>                  # 写入页面
gbrain serve                       # MCP server
gbrain doctor --json               # 健康检查
```

## 核心洞察

1. **复利效应**：Agent 每次交互都读-写知识库，知识随时间自动积累
2. **人类至上**：git 仓库是真相源，可直接编辑，`gbrain sync` 自动同步
3. **Thin Harness, Fat Skills**：运行时尽量薄，智能全在 skill markdown 文件中
4. **零配置起步**：PGLite 嵌入式 PG，2 秒初始化，无需任何外部服务
5. **Dream Cycle**：夜间自动运行 -- 实体扫描、引用修复、记忆整合