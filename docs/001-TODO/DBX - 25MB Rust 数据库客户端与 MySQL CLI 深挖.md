---
title: DBX - 25MB Rust 多数据库客户端与 MySQL CLI 深挖
aliases: ["DBX database client", "t8y2/dbx", "dbxio"]
tags:
  - database
  - rust
  - mysql
  - cli
  - mcp
  - type/doc
  - status/active
source:
  - "https://github.com/t8y2/dbx"
  - "https://github.com/t8y2/dbx/tree/main/packages/cli"
  - "https://registry.npmjs.org/@dbx-app/cli"
  - "https://dbxio.com"
author: t8y2
created: 2026-10-03
updated: 2026-10-03
description: Rust+Tauri 轻量级数据库客户端，一个 25MB 二进制管 100+ 数据库；CLI/MCP 双 AI 入口，MySQL CLI 深挖——方言级 SQL 风险分类 + 双 flag 写保护 + 生产库硬拦截
level: beginner
stars: 4
---

# DBX - 25MB Rust 多数据库客户端与 MySQL CLI 深挖

DBX 是一个用 Rust + Tauri 2 写的跨平台数据库客户端：**单个约 25MB 二进制支持 100+ 数据库**（MySQL、PostgreSQL、Redis、MongoDB、达梦等），无 Java、无 Python、无内置 Chromium。除桌面版外还提供 Docker/Web 部署、独立 CLI 和 MCP Server，让 Claude Code / Cursor 等 AI Agent 直接用你配置好的连接查库。本文按仓库研究模式整理，重点深挖 **MySQL 在 CLI 下的功能与安全机制**。

---

## 目录

1. [基本信息](#基本信息)
2. [核心解决的问题](#核心解决的问题)
3. [主要功能/特性](#主要功能特性)
4. [四种形态对比](#四种形态对比)
5. [使用方式](#使用方式)
6. [MySQL CLI 深挖（重点）](#mysql-cli-深挖重点)
7. [MCP Server：AI 集成](#mcp-serverai-集成)
8. [技术栈与仓库结构](#技术栈与仓库结构)
9. [安全与隐私](#安全与隐私)
10. [项目活跃度](#项目活跃度)
11. [验证与勘误记录](#验证与勘误记录)
12. [参考资料](#参考资料)

---

## 基本信息

> [!info] 基本信息
> - **仓库**: https://github.com/t8y2/dbx
> - **Stars**: 23,976（2026-10-03 查询）/ **Forks**: 2,196 / **Open Issues**: 1,166
> - **语言**: Rust（后端）+ Vue 3 + TypeScript（前端）
> - **协议**: Apache-2.0
> - **最新版**: v0.6.31（2026-10-02 发布，33 个资产）
> - **官网**: https://dbxio.com
> - **创建时间**: 2026-04-29 —— 5 个月冲到 2.4 万 star

---

## 核心解决的问题

传统数据库客户端的三座大山，DBX 各给了一个直接答案：

| 痛点 | 传统方案 | DBX 方案 |
|------|---------|---------|
| 体积/运行时 | DBeaver 需要 Java JRE；Navicat 商业收费 | 单二进制 ~25MB，无 JRE/无 venv/无 Chromium |
| 数据库覆盖 | 每种库装一个工具（redis-cli、mongosh、mysql client…） | 100+ 数据库统一连接管理，含达梦/金仓/OceanBase 等国产库 |
| AI 时代访问 | AI Agent 各自配驱动、各拿一份凭据 | MCP Server + CLI 双入口，Agent 复用桌面端已配好的连接与权限策略 |

数据操作不止于 SQL：Kafka、RocketMQ、RabbitMQ、Pulsar、MQTT 消息队列和 Nacos、Consul、ZooKeeper、etcd 中间件也有一等控制台。

---

## 主要功能/特性

- **Query Editor** — CodeMirror 6，元数据感知补全、`Cmd+Enter` 执行、选中执行、格式化、9 主题、持久历史 + SQL 片段
- **Data Grid** — 虚拟滚动大数据集、行内编辑（保存前 SQL 预览）、DataGrip 式过滤器，导出 CSV/JSON/Markdown/XLSX/INSERT
- **Schema Tools** — ER 图、跨连接 schema diff、可视化执行计划、字段级血缘分析、对象搜索
- **Data Operations** — CSV/Excel 导入、跨库数据迁移、全库导出、数据比对；拖拽 Parquet/CSV/JSON 即时预览（DuckDB 驱动）；**可从 DBeaver/Navicat 导入连接配置**
- **AI SQL Assistant** — 自然语言生成 SQL，内置安全检查后才执行；支持 Claude/OpenAI/Ollama 本地模型/任意 OpenAI 兼容端点
- **插件系统** — 签名沙箱插件（S3、Kubernetes、LDAP…），Go/TypeScript SDK，`npx @dbx-app/plugin-cli` 开发

---

## 四种形态对比

| 维度 | Desktop | Docker/Web | CLI | MCP Server |
|------|---------|-----------|-----|-----------|
| 包名/镜像 | Homebrew/Scoop/WinGet/Flatpak | `t8y2/dbx:latest`（端口 4224） | `@dbx-app/cli` / `brew install dbx-cli` | `@dbx-app/mcp-server` |
| 界面 | 完整 GUI | 浏览器 | 终端 | 被 AI Agent 调用 |
| MySQL 直连 | ✅ | ✅ | ✅ 无需 Desktop 运行 | ✅ Rust 进程内直连 |
| 写操作 | GUI 确认框 | GUI 确认框 | `--allow-writes` 双 flag 门 | 中央策略三档模式 |
| 适用场景 | 日常开发 | 团队自托管/服务器 | 脚本、Codex/CLI Agent 工作流 | Claude Code/Cursor/Windsurf |
| 共享数据 | `dbx.db`（连接库） | 同 | 同（读同一存储） | 同 |

四种形态共用同一份连接存储（macOS: `~/Library/Application Support/com.dbx.app/dbx.db`），连接配置一次、处处可用。

---

## 使用方式

### 安装（macOS）

```bash
# 桌面版
brew install --cask dbx        # 或从 Releases 下载

# CLI（二选一）
brew tap t8y2/tap && brew install dbx-cli
npm install -g @dbx-app/cli    # 需要 Node >= 18.18.0（仅 launcher 需要）

# MCP Server（Claude Code 等）
npm install -g @dbx-app/mcp-server
```

### CLI 快速上手

```bash
dbx doctor                       # 体检：连接库/SQLite loader/桌面桥
dbx capabilities                 # 哪些库可直连、哪些要走桌面桥
dbx connections list --json      # 列连接（不打印密钥）
dbx schema list local --json     # 列表和视图
dbx schema describe local users --json
dbx query local "select count(*) as total from users" --json
dbx context local --tables users,orders   # 给 AI prompt 的紧凑 schema 上下文
dbx open local users             # 在桌面端打开该表
```

`dbx agent setup` 可将官方 Agent Skill 离线装到 `~/.agents/skills/dbx`，让 shell 型 AI Agent 安全使用 CLI。

---

## MySQL CLI 深挖（重点）

> 本章基于 `crates/dbx-cli/src/main.rs` 源码（v0.4.104 时点）+ 官方 CLI README + npm registry 实证。

### MySQL 是 CLI 一等公民

源码中的直连类型清单（`DIRECT_QUERY_TYPES`）：

```rust
const DIRECT_QUERY_TYPES: &[&str] = &[
    "postgres", "redshift", "mysql", "doris", "starrocks",
    "manticoresearch", "sqlite", "rqlite", "kwdb", "questdb",
];
```

关键点：

1. **MySQL 无需 DBX Desktop 运行**——CLI 用 Rust 原生驱动进程内直连（连库执行不经过桌面桥）
2. **MySQL 协议兼容族共享同一通道**——Doris、StarRocks、Manticoresearch 走 MySQL 协议直连；KWDB、QuestDB 也在直连名单
3. Redis 不接受 SQL（返回 `REDIS_COMMAND_REQUIRED`），MongoDB 走独立的 `mongo::validate_safety` 安全链

### SQL 风险分类与写保护（源码级）

每条 SQL 执行前先过方言级风险分类器：

```
dbx query <conn> "<sql>"
        │
        ▼
classify_sql_risk_for_database()      ← 按 MySQL/PG/... 各自方言分类
        │
  ┌───┴─────┬──────────┬─────────────┐
  ▼         ▼          ▼             ▼
ReadOnly   Write      DDL        Transaction
  │         │          │             │
  │    --allow-writes? │ 双 flag:     │
  │        │且         │ --allow-writes
  │      通过          │ + --allow-dangerous-sql
  │                    │             ▼
  │                   通过      ❌ 一律 SQL_BLOCKED
  ▼                    ▼            （CLI 不执行事务）
执行 ◄── targets_production_database()?
               │
         命中生产库 → ❌ SQL_BLOCKED（flag 也救不回来）
```

四级风险对应的放行条件：

| 风险级别 | 典型语句 | 放行条件 |
|---------|---------|---------|
| `ReadOnly` | SELECT / SHOW / DESC | 直接执行 |
| `Write` | INSERT / UPDATE / DELETE | `--allow-writes` |
| `Ddl` | DROP / TRUNCATE / ALTER | `--allow-writes` **且** `--allow-dangerous-sql` |
| `Transaction` | BEGIN / COMMIT | 一律拒绝（CLI 无事务语义） |

两个容易踩的细节（源码确认）：

- **只给 `--allow-dangerous-sql` 不给 `--allow-writes` 直接报 `INVALID_OPTION`**，不是放行也不是静默降级：
  ```
  --allow-dangerous-sql requires --allow-writes.
  ```
- **生产库保护是最后一道硬门**：即使两个 flag 都给了，`targets_production_database()` 命中（连接名/库名/SQL 综合判定）仍然 `SQL_BLOCKED`——"Writes and DDL are blocked for production databases."

### 环境变量等价物

CLI flags 有对应的环境变量开关（脚本场景更方便）：

```bash
DBX_MCP_ALLOW_WRITES=1          # 等价 --allow-writes
DBX_MCP_ALLOW_DANGEROUS_SQL=1   # 等价 --allow-dangerous-sql
DBX_CONNECTION=local            # 省略连接名参数
```

### 查询控制

```bash
# 行数限制 + 超时（单位支持 ms/s/m），探索性查询的推荐姿势
dbx query local "select * from users" --limit 50 --timeout 10s --json

# 从文件执行
dbx query local --file ./query.sql --json

# SQL 以 -- 开头时，用 -- 分隔符防歧义
dbx query local --json -- "-- 注释
select 1"
```

结果超出 `--limit` 时标记 `truncated: true`（JSON 输出可感知截断，不会误当全量）。

### 输出与错误码

- `--json` 机器可读稳定输出；`--format csv` 便于管道给其他 CLI 工具；错误走 stderr + 非零退出码
- 稳定错误码 8 个：`UNKNOWN_OPTION` / `INVALID_OPTION` / `INVALID_ARGUMENT` / `CONNECTION_STORE_ERROR` / `CONNECTION_NOT_FOUND` / `SQL_BLOCKED` / `DBX_NOT_RUNNING` / `ERROR`

### 官方 Agent Skill 的使用纪律

仓库自带 `skills/dbx/SKILL.md`（`dbx agent setup` 安装），给 AI Agent 立了几条规矩，值得 humans 借鉴：

1. 任何操作前先 `dbx connections list --json`，**不要从对话上下文猜连接名**
2. 探索性查询固定 `--limit 50 --timeout 10s`，用户明确要求才放大
3. **绝不绕过 dbx CLI** 直连数据库（不用 Python sqlite3 / shell redirect 旁路），`SQL_BLOCKED` 时把结果告诉用户等决策
4. 从不自动加写 flag，写操作必须用户显式确认

---

## MCP Server：AI 集成

Rust 实现的 MCP Server（npm 包只是薄 launcher，二进制为原生编译），18 个工具：

```json
{
  "mcpServers": {
    "dbx": { "command": "npx", "args": ["-y", "@dbx-app/mcp-server"] }
  }
}
```

核心工具：`dbx_list_connections`、`dbx_describe_table`、`dbx_get_schema_context`、`dbx_execute_query`（最多 100 行）、`dbx_execute_batch`（多语句 + 可选事务合并）、`dbx_execute_redis_command`、`dbx_peek_messages`（Kafka 只读 peek，不提交消费位点）等。

权限不在客户端配置里散落，而是 **DBX 设置 → MCP 的中央策略**（每次请求重新加载）：

| 模式 | 允许的操作 |
|------|-----------|
| Read only | 查询与元数据读 |
| Data read/write | 普通 INSERT、带有效过滤的 UPDATE/DELETE、普通 Redis 写 |
| Full access | 追加大范围更新/删除、DDL、TRUNCATE、MongoDB 破坏性操作、Redis `FLUSH*` |

细节亮点：`WHERE TRUE`、`WHERE 1 = 1`、`_id: {$exists: true}` 这类"全覆盖过滤"按高风险处理；未知 Redis 命令 fail-closed；新版不允许 `DBX_MCP_ALLOW_WRITES` 环境变量放宽中央策略（旧变量只能收窄不能放大）。

---

## 技术栈与仓库结构

| 层 | 技术 |
|----|------|
| 框架 | Tauri 2 |
| 前端 | Vue 3 + TypeScript + shadcn-vue + Tailwind CSS |
| 编辑器 | CodeMirror 6 |
| 后端 | Rust + sqlx / tiberius / redis-rs / mongodb |

```
t8y2/dbx
├── src-tauri/          # Tauri 桌面壳
├── crates/
│   ├── dbx-cli/        # CLI（Rust 原生）
│   ├── dbx-core/       # 核心：连接/SQL 方言与安全/schema diff/迁移
│   ├── dbx-mcp/        # MCP Server
│   ├── dbx-web/        # Web 后端（axum）
│   └── dbx-sqlite-worker/
├── agents/             # Java Agent/JDBC 驱动（达梦/金仓/Oracle 等）
├── packages/           # npm 发行包（cli、mcp-server、平台二进制）
├── plugins/            # 插件 SDK 与清单 schema
├── skills/dbx/         # 官方 Agent Skill
├── deploy/             # Dockerfile、compose、1Panel
└── docs/               # 官网（Next.js）
```

`dbx-core/src` 里能看到每个数据域一个模块的拆分：`sql_risk.rs`（风险分类）、`production_safety.rs`（生产库判定）、`mysql_ddl_normalize.rs`、`schema_diff/`、`two_phase_commit.rs`、`sql_error_position.rs` 等，测试目录有大量 `live_mysql_*` / `live_postgres_*` 真库集成测试。

---

## 安全与隐私

- 连接/AI/隧道凭据**加密后**写入 `dbx.db`：桌面版走系统凭据库（macOS Keychain / Windows Credential Manager / Linux Secret Service）；Web/Docker 用 `${DBX_DATA_DIR}/.dbx/secret.key`，生产环境可换 `DBX_SECRET_KEY_FILE` 注入 K8s Secret
- 旧版明文凭据有「Data Security Upgrade」迁移向导（带受限备份与重试）
- 跨设备同步走独立 passphrase 的加密导出，**直接拷 `dbx.db` 不是合法迁移方式**（平台密钥不随库走）
- 破坏性操作 GUI 确认框；CLI 双 flag；MCP 中央策略——三层各管各的入口

---

## 项目活跃度

- 2026-04-29 创建 → 2026-10-03 已 23,976 star，**5 个月 ~24k**，Trendshift/HelloGitHub/Product Hunt 三平台露出
- 发布节奏极快：桌面 v0.6.31（10-02）、npm CLI/MCP 同日 0.4.104；npm 包带 SLSA provenance 签名
- Open issues 1,166 偏高——量大+迭代快的代价，提 issue 前先看是不是已被新版本修掉
- 生态位：1Panel Partner（服务器面板集成）、国产数据库适配是差异化卖点（达梦/金仓/OceanBase/GaussDB/人大金仓全在支持列表）

---

## 验证与勘误记录

| 声称 | 验证结果 | 来源 |
|------|---------|------|
| "90+ databases" | ⚠️ 已过时 → 当前 README 为 **100+**（README 自身也在快速膨胀） | GitHub 页面 vs webReader 缓存标题对比 |
| mcp-server README 称 CLI "仍为 TypeScript/Node.js" | ❌ 已过时 → **CLI 已迁移 Rust**：`@dbx-app/cli@0.4.104` 的 npm scripts 直接 `cargo build -p dbx-cli`，仓库另有 `scripts/verify-cli-migration.mjs`；crates/dbx-cli 存在 Rust 源码 | npm registry JSON + 仓库结构 |
| npm 包需 Node >= 18 | ✅ `engines: {node: ">=18.18.0"}`，仅 npm launcher 需要；原生二进制不需要 Node | registry JSON |
| MySQL CLI 直连无需 Desktop | ✅ 源码 `DIRECT_QUERY_TYPES` 含 `mysql`，执行走 `backend.execute_query`（LocalBackend 进程内） | crates/dbx-cli/src/main.rs |
| 双 flag 门与生产库硬拦截 | ✅ 源码 L333-385：dangerous 需 writes 前置；production 检查在 flag 检查之后无条件执行 | crates/dbx-cli/src/main.rs |
| stars 23,976 / v0.6.31 | ✅ GitHub API 实时查询（2026-10-03）；活计数是移动目标，以查询日为准 | api.github.com |
| 25MB 体积 | ⚠️ 官方口径，未实测安装包体积（Release 33 个资产未逐一下载核对） | README 自述 |

---

## 参考资料

- [t8y2/dbx 仓库](https://github.com/t8y2/dbx)
- [CLI README](https://github.com/t8y2/dbx/blob/main/packages/cli/README.md)
- [MCP Server README](https://github.com/t8y2/dbx/blob/main/packages/mcp-server/README.md)
- [官方 Agent Skill](https://github.com/t8y2/dbx/blob/main/skills/dbx/SKILL.md)
- [@dbx-app/cli npm](https://www.npmjs.com/package/@dbx-app/cli)
- [DBX 官网](https://dbxio.com)

## 相关笔记

- [[新版MCP-Server开发指南-5分钟从零接入Claude]] — MCP 协议与 Server 开发全景，DBX MCP 是"成品 Server 复用已有连接"路线的实例
- [[Claude Code Hooks - 确定性自动化防线]] — 同主题：AI 执行危险操作的确定性拦截；DBX 的 SQL 风险分类是数据库侧的同类思路

---

*文档生成时间：2026-10-03*
*基于 t8y2/dbx v0.6.31 / @dbx-app/cli 0.4.104*
