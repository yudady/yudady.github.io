---
title: Graphify - AI编程助手知识图谱技能
aliases: [graphify, knowledge graph skill, 代码知识图谱]
tags: [ai-tool, knowledge-graph, code-analysis, tree-sitter, status/active, area/distill, type/doc, topic-graphify]
source: ["https://github.com/safishamsi/graphify"]
author: Safi Shamsi
created: 2026-04-19 19:52
updated: 2026-04-19 19:52
description: |
  将任意文件夹（代码、文档、论文、图片、视频）转为可查询的知识图谱，作为 AI 编程助手的 skill 使用。
level: intermediate
stars: 4
---

# Graphify - AI 编程助手知识图谱技能

> 一句话定位：把任意文件夹变成可查询的知识图谱，让 AI 编程助手理解代码库的结构和设计决策，查询 token 消耗降低 71.5x。

## 核心解决的问题

- AI 编程助手面对大型代码库时，每次都要重新读取全部文件，token 消耗巨大
- 代码、文档、论文、截图、视频等异构素材之间缺乏关联
- 无法追溯"为什么这样设计"，只知道"代码做了什么"

Graphify 解决这三个问题：提取结构 → 建立关联 → 持久化图谱，后续查询只读压缩后的图谱而非原始文件。

---

## 主要功能与特性

| 特性 | 说明 |
|------|------|
| 多模态输入 | 代码(25 语言)、PDF、Markdown、截图、白板照片、视频、音频 |
| AST 提取 | 代码通过 tree-sitter 本地解析，不消耗 LLM token |
| 视频转录 | faster-whisper 本地转录，领域感知 prompt 提升准确度 |
| Leiden 聚类 | 基于图拓扑的社区发现，无需 embedding/vector DB |
| 置信度标签 | EXTRACTED / INFERRED / AMBIGUOUS，区分"发现"与"猜测" |
| SHA256 缓存 | 增量更新，只处理变更文件 |
| MCP Server | 可作为 MCP 工具暴露，支持 query_graph / get_node / shortest_path |
| Git Hooks | post-commit / post-checkout 自动重建图谱 |
| Obsidian 输出 | `--obsidian` 参数直接生成 vault |
| 团队协作 | graphify-out/ 可提交 git，全队共享 |

---

## 工作原理（三遍扫描）

```
Pass 1: AST 扫描（本地，无 LLM）
  tree-sitter 解析代码文件 → 类/函数/import/调用图/docstring/设计注释

Pass 2: 视频/音频转录（本地，无 LLM）
  faster-whisper 转录 → 领域感知 prompt（从语料 god nodes 生成）

Pass 3: 语义提取（LLM 子代理并行）
  Claude/GPT 子代理处理文档、论文、图片、转录文本
  → 提取概念、关系、设计决策
  → 合并到 NetworkX 图
  → Leiden 社区发现
  → 输出 HTML + JSON + 报告
```

---

## 技术栈

| 组件 | 技术 |
|------|------|
| 图引擎 | NetworkX |
| 社区发现 | Leiden (graspologic) |
| 代码解析 | tree-sitter (25 语言) |
| 可视化 | vis.js |
| 视频转录 | faster-whisper + yt-dlp (可选) |
| 语义提取 | Claude / GPT-4 (取决于平台) |
| 导出格式 | graph.json / graph.html / GRAPH_REPORT.md / Obsidian / SVG / GraphML / Neo4j |

---

## 仓库结构

```
graphify/
├── detect.py      # 文件发现与过滤
├── extract.py     # AST + LLM 提取（核心）
├── build.py       # 构建 NetworkX 图
├── cluster.py     # Leiden 社区发现
├── analyze.py     # God nodes、惊奇连接、建议问题
├── report.py      # 生成 GRAPH_REPORT.md
├── export.py      # HTML/JSON/Obsidian/SVG/GraphML/Neo4j 导出
├── ingest.py      # URL 拉取（论文、推文、视频）
├── cache.py       # SHA256 增量缓存
├── security.py    # 输入验证（URL/路径/标签）
├── validate.py    # 提取结果 schema 校验
├── serve.py       # MCP stdio server
├── watch.py       # 文件监听自动重建
├── wiki.py        # Wikipedia 风格 markdown 输出
├── benchmark.py   # Token 压缩基准测试
└── skill*.md      # 各平台 skill 定义文件
```

---

## 支持平台

| 平台 | 安装命令 | Always-on 机制 |
|------|----------|---------------|
| Claude Code | `graphify install` | CLAUDE.md + PreToolUse hook |
| Codex | `graphify install --platform codex` | AGENTS.md + hooks.json |
| OpenCode | `graphify install --platform opencode` | AGENTS.md + plugin |
| Cursor | `graphify cursor install` | .cursor/rules/graphify.mdc |
| Gemini CLI | `graphify install --platform gemini` | GEMINI.md + BeforeTool hook |
| Copilot CLI | `graphify install --platform copilot` | skill 文件 |
| VS Code Copilot | `graphify vscode install` | copilot-instructions.md |
| Aider | `graphify install --platform aider` | AGENTS.md |
| Hermes | `graphify install --platform hermes` | AGENTS.md + ~/.hermes/skills/ |
| Kiro | `graphify kiro install` | .kiro/skills/ + steering |
| Trae | `graphify install --platform trae` | AGENTS.md |
| Google Antigravity | `graphify antigravity install` | .agent/rules + workflows |

---

## 安装与使用

```bash
# 安装（PyPI 包名是 graphifyy，双 y）
pip install graphifyy && graphify install

# Hermes 平台
graphify install --platform hermes
graphify hermes install    # always-on

# 基本使用
/graphify .                              # 当前目录
/graphify ./raw --mode deep              # 深度推断
/graphify ./raw --update                 # 增量更新
/graphify ./raw --obsidian               # 输出到 Obsidian vault
/graphify ./raw --watch                  # 文件变更自动重建

# 查询
/graphify query "show the auth flow"
graphify query "what is CfgNode?" --budget 500
graphify path "DigestAuth" "Response"
graphify explain "SwinTransformer"

# 添加内容
/graphify add https://arxiv.org/abs/1706.03762
/graphify add <video-url> --author "Name"

# MCP Server
python -m graphify.serve graphify-out/graph.json

# Git Hooks
graphify hook install     # post-commit + post-checkout 自动重建
```

---

## 性能数据

| 语料 | 文件数 | Token 压缩比 |
|------|--------|-------------|
| Karpathy repos + 5 论文 + 4 图片 | 52 | **71.5x** |
| graphify 源码 + Transformer 论文 | 4 | 5.4x |
| httpx (Python 库) | 6 | ~1x (少量文件价值在结构清晰) |

---

## 安全与隐私

| 维度 | 策略 |
|------|------|
| 代码文件 | tree-sitter 本地解析，内容不出机器 |
| 视频/音频 | faster-whisper 本地转录，音频不出机器 |
| 文档/论文/图片 | 发送到 LLM API（Anthropic/OpenAI），用自己的 API key |
| 遥测 | 无。零追踪、零分析 |
| 输入验证 | URL 限制 http/https，标签消毒，路径限制在 graphify-out/ 内 |

---

## 输出文件

```
graphify-out/
├── graph.html       # 交互式图谱（浏览器打开，点击节点、搜索、按社区过滤）
├── GRAPH_REPORT.md  # God nodes、惊奇连接、建议问题
├── graph.json       # 持久化图谱（JSON，可 MCP server 查询）
└── cache/           # SHA256 缓存（增量更新用）
```

---

## Roadmap

- **Penpax** — 企业层，持续后台运行，连接浏览器历史/会议/邮件/文件/代码，全本地，无云。面向律师、顾问、高管、医生、研究员。Free trial 即将上线。

---

## 参考资料

- [GitHub 仓库](https://github.com/safishamsi/graphify)
- [PyPI 包](https://pypi.org/project/graphifyy/) (注意双 y)
- [Penpax 等待列表](https://safishamsi.github.io/penpax.ai)

## 相关笔记

- [[LLM Wiki]] - 知识管理相关
- [[Hermes Agent]] - 同类 AI 编程助手平台