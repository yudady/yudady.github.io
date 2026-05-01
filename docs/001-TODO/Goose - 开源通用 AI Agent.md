---
title: Goose - 开源通用 AI Agent
aliases: [goose, goose-agent, block-goose]
tags:
  - ai-agent
  - status/active
  - area/distill
  - type/doc
  - MCP
  - ACP
  - Rust
  - open-source
source:
  - "https://github.com/aaif-goose/goose"
author: Agentic AI Foundation (AAIF) at Linux Foundation
created: 2026-04-08 23:15
updated: 2026-04-08 23:15
description: |
  开源通用 AI Agent — 桌面应用 + CLI + API，支持 15+ LLM 提供商和 70+ MCP 扩展，Rust 构建，Apache 2.0 协议。
level: intermediate
stars: 5
---

# Goose - 开源通用 AI Agent

> 通用 AI Agent，不仅限于代码 — 研究、写作、自动化、数据分析。原生桌面应用（macOS/Linux/Windows）+ CLI + API，Rust 构建，支持 15+ LLM 提供商和 70+ MCP 扩展。已加入 Linux Foundation 下的 Agentic AI Foundation (AAIF)。

## 基本信息

| 项目 | 详情 |
|---|---|
| **仓库** | https://github.com/aaif-goose/goose |
| **前身** | `block/goose` (Square/Block 公司开发，后捐赠给 AAIF) |
| **Stars** | 39.5k+ |
| **Forks** | 3.9k+ |
| **Watchers** | 207 |
| **贡献者** | 439+ |
| **协议** | Apache 2.0 |
| **版本** | v1.29.1 (2026-04-03)，cargo 版本 1.30.0 |
| **语言** | Rust 58.2%, TypeScript 34.1%, Shell 1.9%, JS 1.9%, Python 1.5% |
| **Commits** | 4,098 |
| **Releases** | 126 |
| **治理** | Linux Foundation AAIF |
| **文档** | https://goose-docs.ai |
| **Discord** | https://discord.gg/goose-oss |

---

## 核心定位

goose 是一个**通用 AI Agent**，不仅限于代码。可以在本机运行，用于研究、写作、自动化、数据分析等任何任务。

与 Cursor/Claude Code 等编码专用 Agent 的关键区别：

| 维度 | goose | Cursor | Claude Code |
|---|---|---|---|
| **用途** | 通用（代码+工作流+一切） | 编码专用 | 编码专用 |
| **界面** | 桌面 App + CLI + API | IDE 插件 | CLI |
| **提供商** | 15+ (含订阅) | OpenAI/Anthropic | Anthropic |
| **扩展** | 70+ MCP 扩展 | MCP 支持 | MCP 支持 |
| **开源** | Apache 2.0 | 闭源 | 闭源 |
| **协议标准** | MCP + ACP | MCP | MCP |

---

## 主要功能/特性

### 1. 多界面

- **桌面应用**: macOS / Linux / Windows 原生应用（Electron + Rust 后端）
- **CLI**: 终端工作流，`cargo run -p goose-cli`
- **API**: 嵌入到任何应用，基于 Axum HTTP server

### 2. 15+ LLM 提供商

| 类别 | 提供商 |
|---|---|
| API Key | Anthropic, OpenAI, Google, OpenRouter, Azure, Bedrock, Databricks |
| 本地/自托管 | Ollama |
| 订阅接入 (ACP) | Claude, ChatGPT, Gemini（无需 API Key，直接用现有订阅） |

ACP (Agent Client Protocol) 是 goose 独有的优势 — 可以直接使用已有的 Claude/ChatGPT/Gemini 订阅，不需要额外购买 API 额度。

### 3. MCP 生态系统 (70+ 扩展)

goose 是 MCP 协议最早的采用者之一，拥有最深的集成。通过 Model Context Protocol 标准连接外部工具和数据源。

### 4. Recipes (工作流配方)

用 YAML 捕获可移植的工作流配置。可以：
- 与团队分享
- 在 CI 中运行
- 包含指令、扩展、参数和子配方

### 5. MCP Apps (交互式 UI)

扩展可以直接在 goose 桌面应用内渲染交互式 UI — 按钮、表单、可视化。一种新的 Agent 驱动工具构建方式。

### 6. Subagents (子代理)

生成独立子代理并行处理任务 — 代码审查、研究、文件处理 — 保持主对话清洁。

### 7. 安全特性

- Prompt injection 检测
- 工具权限控制
- Sandbox 模式
- Adversary reviewer（对抗审查器，监视不安全操作）

### 8. 自定义发行版

可以构建自己的 goose 发行版，预配置提供商、扩展和品牌。见 `CUSTOM_DISTROS.md`。

---

## 技术栈

| 层 | 技术 |
|---|---|
| **核心语言** | Rust (58.2%) |
| **桌面 UI** | TypeScript/Electron (34.1%) |
| **HTTP Server** | Axum |
| **MCP 客户端** | rmcp 1.2.0 |
| **ACP** | sacp 11.0.0 (Agent Client Protocol) |
| **AST 解析** | tree-sitter (Go/Java/JS/Kotlin/Python/Ruby/Rust/Swift/TS) |
| **JS 引擎** | V8 (vendor) |
| **配置管理** | Hermit (开发依赖) |
| **构建工具** | Cargo, just, pnpm |
| **可观测性** | OpenTelemetry, Langfuse (tracing) |
| **测试** | cargo test, wiremock, serial_test |
| **代码审查** | Codex (AI code reviewer) |

---

## 仓库结构

```
goose/
  crates/           # Rust workspace (多个 crate)
    goose-cli/      # CLI 入口
    goose-server/   # Axum HTTP server (goosed)
    goose-*         # 各功能模块
  ui/               # 桌面应用 (Electron + TypeScript)
    desktop/        # 桌面 UI
  documentation/    # 文档源码 (Docusaurus?)
  docs/             # 文档
  examples/         # 示例
  scripts/          # 构建/部署脚本
  evals/            # 评估框架 (open-model-gym)
  recipe-scanner/   # Recipe 扫描器
  services/         # 辅助服务 (ask-ai-bot)
  workflow_recipes/ # 工作流配方
  oidc-proxy/       # OIDC 代理
  vendor/v8/        # V8 引擎 (vendored)
  bin/              # Hermit 工具链
```

---

## 安装方式

### 桌面应用

从 https://goose-docs.ai/docs/getting-started/installation 下载 macOS / Linux / Windows 安装包。

### CLI

```bash
curl -fsSL https://github.com/aaif-goose/goose/releases/download/stable/download_cli.sh | bash
```

### 从源码构建

```bash
git clone https://github.com/aaif-goose/goose.git
cd goose
source ./bin/activate-hermit   # 激活 Hermit 工具链
cargo build                     # 编译
./target/debug/goose configure  # 首次配置
./target/debug/goose session    # 启动会话
```

---

## CLI 常用命令

```bash
goose configure       # 首次配置（选择 LLM 提供商）
goose session         # 启动交互式会话
goose run <recipe>    # 运行 Recipe
goose configure --provider <name>  # 切换提供商
```

---

## 开放标准

### Model Context Protocol (MCP)

goose 是 MCP 生态最深的集成者之一，70+ 文档化扩展且持续增长。

### Agent Client Protocol (ACP)

ACP 是与编码 Agent 通信的标准。goose 作为 ACP server 运行，可以从 Zed、JetBrains、VS Code 连接，也可以将 Claude Code 和 Codex 等 ACP agent 作为提供商使用。

### Agentic AI Foundation (AAIF)

goose 属于 Linux Foundation 下的 AAIF，确保项目保持供应商中立、社区治理、长期开放。

---

## 与其他 AI Agent 的对比

| 项目 | 开源 | 语言 | 通用/编码 | MCP | ACP | 桌面App | CLI |
|---|---|---|---|---|---|---|---|
| **goose** | Apache 2.0 | Rust | 通用 | 70+ | Y | Y | Y |
| Claude Code | 闭源 | TS | 编码 | Y | -- | -- | Y |
| Cursor | 闭源 | TS | 编码 | Y | -- | Y(IDE) | -- |
| Codex | 闭源 | TS | 编码 | Y | Y | -- | Y |
| OpenCode | 开源 | Go | 编码 | Y | -- | -- | Y |
| Cline | 开源 | TS | 编码 | Y | -- | Y(VSCode) | -- |

---

## 社区与生态

- **Discord**: 活跃社区，月度活动（workshops、brainstorming、livestreams）
- **YouTube**: @goose-oss
- **439+ 贡献者**
- **贡献规范**: 首次贡献建议从小 PR 开始建立信任；使用 Codex 做 AI 代码审查；要求 DCO sign-off
- **AI 协作建议**: Think first（先解释架构），Spot the laziness（识别 LLM 偷懒），Spot the uncertainty（识别 LLM 不确定），Spot the bloat（识别冗余）

---

## 安全与隐私

- 全部本地运行
- Prompt injection 检测
- 工具权限控制（细粒度）
- Sandbox 模式
- Adversary reviewer（对抗审查器）
- 支持 Langfuse 自托管追踪
- OpenTelemetry 可观测性

---

## 参考资料

- [GitHub 仓库](https://github.com/aaif-goose/goose)
- [官方文档](https://goose-docs.ai)
- [AAIF (Agentic AI Foundation)](https://aaif.io/)
- [MCP 协议](https://modelcontextprotocol.io/)
- [Discord](https://discord.gg/goose-oss)

## 相关笔记

- [[GitNexus - 零服务器代码知识图谱引擎]]
