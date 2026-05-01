---
created: 2026-04-08
tags:
  - 工具
  - AI编程
  - MCP
  - 知识图谱
  - 代码分析
  - AST
  - Tree-sitter
  - Claude-Code
  - Cursor
  - status/active
  - area/distill
  - type/doc
source: https://github.com/abhigyanpatwari/GitNexus
---

# GitNexus - 零服务器代码知识图谱引擎

> [!info] 基本信息
> - **仓库**: https://github.com/abhigyanpatwari/GitNexus
> - **Stars**: 25k+ | **Forks**: 2.8k+ | **语言**: TypeScript 97.3%
> - **作者**: abhigyanpatwari (Akon Labs)
> - **协议**: PolyForm Noncommercial 1.0（非商业用途免费，商业需授权）
> - **版本**: v1.5.3 (2026-04-01)
> - **npm**: `gitnexus`
> - **Discord**: https://discord.gg/AAsRVT6fGb
> - **官网**: https://gitnexus.vercel.app
> - **企业版**: https://akonlabs.com

---

## 一句话定位

将任意代码库索引为**知识图谱** — 包含所有依赖关系、调用链、功能集群和执行流 — 然后通过 MCP 工具暴露给 AI 编码代理，让 AI 永远不会遗漏代码上下文。

> 类似 DeepWiki，但更深。DeepWiki 帮你*理解*代码，GitNexus 让你*分析*代码 — 因为知识图谱追踪的是每一条关系，而不仅仅是描述。

---

## 核心解决的问题

Cursor、Claude Code、Codex 等工具虽然强大，但并不真正了解代码库结构。

典型场景：
1. AI 编辑 `UserService.validate()`
2. 不知道有 47 个函数依赖它的返回类型
3. **破坏性变更上线了**

GitNexus 的做法是**在索引时预计算结构** — 聚类、追踪、评分 — 工具一次调用就返回完整上下文，而非让 LLM 做 4+ 次查询。

核心创新：**预计算关系智能**
- **可靠性** — LLM 不可能遗漏上下文，已经在工具响应里了
- **Token 效率** — 不需要 10 次查询链来理解一个函数
- **模型民主化** — 小模型也能工作，因为工具承担了重活

---

## 两种使用方式

| | CLI + MCP | Web UI |
|---|---|---|
| **用途** | 本地索引仓库，通过 MCP 连接 AI 代理 | 浏览器内图形化探索 + AI 聊天 |
| **适用场景** | 日常开发 (Cursor, Claude Code, Codex, Windsurf, OpenCode) | 快速探索、演示、一次性分析 |
| **规模** | 任意大小仓库 | 受浏览器内存限制 (~5k 文件)，或通过后端模式无限制 |
| **安装** | `npm install -g gitnexus` | 无需安装 — gitnexus.vercel.app |
| **存储** | LadybugDB 原生 (快速, 持久化) | LadybugDB WASM (内存中, 每会话) |
| **解析** | Tree-sitter 原生绑定 | Tree-sitter WASM |
| **隐私** | 全部本地运行，无网络调用 | 全部浏览器内运行，无服务器 |

**Bridge 模式**: `gitnexus serve` 连接两者 — Web UI 自动检测本地服务器，浏览所有 CLI 索引的仓库。

---

## 快速开始

```bash
# 索引你的仓库（在仓库根目录运行）
npx gitnexus analyze

# 一次性配置 MCP
npx gitnexus setup
```

`analyze` 一条命令完成：索引代码库、安装 agent skills、注册 Claude Code hooks、生成 AGENTS.md / CLAUDE.md 上下文文件。

---

## 编辑器支持

| 编辑器 | MCP | Skills | Hooks | 支持级别 |
|---|---|---|---|---|
| **Claude Code** | Yes | Yes | Yes (PreToolUse + PostToolUse) | **Full** |
| **Cursor** | Yes | Yes | -- | MCP + Skills |
| **Codex** | Yes | Yes | -- | MCP + Skills |
| **Windsurf** | Yes | -- | -- | MCP |
| **OpenCode** | Yes | Yes | -- | MCP + Skills |

Claude Code 集成最深：MCP 工具 + agent skills + PreToolUse hooks（用图谱上下文增强搜索）+ PostToolUse hooks（提交后自动重新索引）。

---

## MCP 工具 (16 个)

### 单仓库工具 (11 个)

| 工具 | 功能 |
|---|---|
| `list_repos` | 发现所有已索引仓库 |
| `query` | 混合搜索 (BM25 + semantic + RRF)，按进程分组 |
| `context` | 360 度符号视图 — 分类引用、进程参与 |
| `impact` | 爆炸半径分析，深度分组 + 置信度 |
| `detect_changes` | Git diff 影响映射 — 变更行到受影响进程 |
| `rename` | 多文件协调重命名 (图 + 文本搜索) |
| `cypher` | 原生 Cypher 图查询 |

### 多仓库 Group 工具 (5 个)

| 工具 | 功能 |
|---|---|
| `group_list` | 列出仓库组 |
| `group_sync` | 跨仓库/服务提取和匹配契约 |
| `group_contracts` | 检查提取的契约和交叉引用 |
| `group_query` | 搜索组内所有仓库的执行流 |
| `group_status` | 检查组内仓库索引的新鲜度 |

### MCP Resources (7 个即时上下文)

- `gitnexus://repos` — 所有已索引仓库列表
- `gitnexus://repo/{name}/context` — 代码库统计、新鲜度检查
- `gitnexus://repo/{name}/clusters` — 所有功能集群和内聚度
- `gitnexus://repo/{name}/cluster/{name}` — 集群成员和详情
- `gitnexus://repo/{name}/processes` — 所有执行流
- `gitnexus://repo/{name}/process/{name}` — 完整进程追踪
- `gitnexus://repo/{name}/schema` — Cypher 查询图 schema

### Agent Skills (自动安装到 `.claude/skills/`)

- **Exploring** — 用知识图谱导航陌生代码
- **Debugging** — 通过调用链追踪 bug
- **Impact Analysis** — 变更前分析爆炸半径
- **Refactoring** — 用依赖映射规划安全重构

`--skills` 标志会基于 Leiden 社区检测生成**仓库特定 skills**。

---

## 工作原理：6 阶段索引管道

1. **Structure** — 遍历文件树，映射文件夹/文件关系
2. **Parsing** — 用 Tree-sitter AST 提取函数、类、方法、接口
3. **Resolution** — 解析 imports、函数调用、继承、构造器推断、`self`/`this` receiver 类型（跨文件，语言感知）
4. **Clustering** — 用 Leiden 算法将相关符号分组为功能社区
5. **Processes** — 从入口点追踪执行流，贯穿调用链
6. **Search** — 构建混合搜索索引 (BM25 + 语义 + RRF)

---

## 支持的编程语言 (14 种)

| 语言 | Imports | Named Bindings | Exports | Heritage | Type Annotations | Constructor Inference | Config | Frameworks | Entry Points |
|---|---|---|---|---|---|---|---|---|---|
| TypeScript | Y | Y | Y | Y | Y | Y | Y | Y | Y |
| JavaScript | Y | Y | Y | Y | -- | Y | Y | Y | Y |
| Python | Y | Y | Y | Y | Y | Y | Y | Y | Y |
| Java | Y | Y | Y | Y | Y | Y | -- | Y | Y |
| Kotlin | Y | Y | Y | Y | Y | Y | -- | Y | Y |
| C# | Y | Y | Y | Y | Y | Y | Y | Y | Y |
| Go | Y | -- | Y | Y | Y | Y | Y | Y | Y |
| Rust | Y | Y | Y | Y | Y | Y | -- | Y | Y |
| PHP | Y | Y | Y | -- | Y | Y | Y | Y | Y |
| Ruby | Y | -- | Y | Y | -- | Y | -- | Y | Y |
| Swift | -- | -- | Y | Y | Y | Y | Y | Y | Y |
| C | -- | -- | Y | -- | Y | Y | -- | Y | Y |
| C++ | -- | -- | Y | Y | Y | Y | -- | Y | Y |
| Dart | Y | -- | Y | Y | Y | Y | -- | Y | Y |

---

## 技术栈

| 层 | CLI | Web |
|---|---|---|
| **Runtime** | Node.js (native) | Browser (WASM) |
| **Parsing** | Tree-sitter native | Tree-sitter WASM |
| **Database** | LadybugDB native | LadybugDB WASM |
| **Embeddings** | HuggingFace transformers.js (GPU/CPU) | transformers.js (WebGPU/WASM) |
| **Search** | BM25 + semantic + RRF | BM25 + semantic + RRF |
| **Agent Interface** | MCP (stdio) | LangChain ReAct agent |
| **Visualization** | -- | Sigma.js + Graphology (WebGL) |
| **Frontend** | -- | React 18, TypeScript, Vite, Tailwind v4 |
| **Clustering** | Graphology | Graphology |
| **Concurrency** | Worker threads + async | Web Workers + Comlink |

---

## 仓库结构 (Monorepo)

| 路径 | 角色 |
|---|---|
| `gitnexus/` | npm 包：CLI、MCP server (stdio)、本地 HTTP API、索引管道、LadybugDB 图数据库 |
| `gitnexus-web/` | Vite + React UI：浏览器内索引 (WASM)、图可视化、可选连接 `gitnexus serve` |
| `gitnexus-shared/` | CLI 和 Web 共享的核心逻辑（图、语言检测、pipeline） |
| `gitnexus-claude-plugin/` | Claude marketplace 插件配置 |
| `gitnexus-cursor-integration/` | Cursor 集成配置 |
| `eval/` | 评估框架 (Python)，SWE-bench benchmark |
| `.claude/`, `AGENTS.md`, `CLAUDE.md` | Agent 工作流和工具使用说明 |

---

## Wiki 生成

```bash
gitnexus wiki                         # 需要 OPENAI_API_KEY
gitnexus wiki --model gpt-4o          # 自定义模型
gitnexus wiki --base-url <url>        # 自定义 API
gitnexus wiki --force                 # 强制重新生成
```

读取索引的图谱结构，用 LLM 将文件分组为模块，生成每个模块的文档页面和总览页面，附带知识图谱交叉引用。

---

## 多仓库架构

- 全局注册表 `~/.gitnexus/registry.json`，一个 MCP server 服务所有已索引仓库
- 每个仓库索引存在 `.gitnexus/` (gitignored)，可移植
- LadybugDB 连接懒加载，5 分钟不活跃后驱逐，最多 5 个并发
- 单仓库时 `repo` 参数可选；多仓库时指定：`query({query: "auth", repo: "my-app"})`

---

## Roadmap

### 正在开发
- [ ] LLM 集群增强 — 通过 LLM API 生成语义集群名称
- [ ] AST 装饰器检测 — 解析 @Controller, @Get 等
- [ ] 增量索引 — 只重新索引变更文件

### 已完成
- [x] 构造器推断类型解析, `self`/`this` receiver 映射
- [x] Wiki 生成, 多文件重命名, Git-Diff 影响分析
- [x] 进程分组搜索, 360 度上下文, Claude Code Hooks
- [x] 多仓库 MCP, 零配置设置, 14 种语言支持
- [x] 社区检测, 进程检测, 置信度评分
- [x] 混合搜索, 向量索引

---

## 企业版功能

- SaaS 和自托管两种部署方式
- PR Review — 自动化爆炸半径分析
- 自动更新 Code Wiki — 持续更新的文档
- 自动重新索引 — 知识图谱保持新鲜
- 多仓库支持 — 统一图谱
- OCaml 额外语言支持
- 优先特性/语言支持

---

## 安全与隐私

- **CLI**: 全部本地运行，无网络调用。索引存储在 `.gitnexus/` (gitignored)。全局注册表 `~/.gitnexus/` 仅存路径和元数据。
- **Web**: 全部浏览器内运行，代码不上传服务器。API keys 仅存 localStorage。
- 开源可审计。
