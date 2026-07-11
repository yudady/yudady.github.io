---
title: notebooklm-py — 把 Google NotebookLM 搬进命令行，让 AI 免费替你读资料
aliases:
  - NotebookLM CLI
  - notebooklm-py 教程
tags:
  - ai-tool
  - status/active
  - type/doc
  - claude-code
  - token-optimization
  - notebooklm
source:
  - "https://youtube.com/watch?v=CwnoYYHOgv8"
  - "https://github.com/teng-lin/notebooklm-py"
author:
  channel: YAHA学堂
  tool-dev: teng-lin
created: 2026-07-11
updated: 2026-07-11
description: |
  notebooklm-py 开源工具将 Google NotebookLM 命令行化，配合 Claude Code 实现"零 Token 研究工作流"：读文档、生成播客、出测验、免费 Deep Research，重活全让 Google 免费干。
level: beginner
stars: 4
---

# notebooklm-py — 把 Google NotebookLM 搬进命令行，让 AI 免费替你读资料

> 一份 8 分钟的实战教程拆解：用开源 CLI 将 Google 免费的 NotebookLM 接入终端和 Claude Code，实现资料消化、播客生成、测验导出和免费 Deep Research，彻底解决"长文档烧 Token"问题。适合所有用 AI 处理大量文本/视频资料的开发者。

## 目录

- [[#核心痛点：Token 燃烧危机]]
- [[#notebooklm-py 项目概览]]
- [[#核心功能拆解]]
- [[#Claude Code 集成：对照实验]]
- [[#免费 Deep Research]]
- [[#安装与排错]]
- [[#适用场景决策树]]

---

## 核心痛点：Token 燃烧危机

**问题本质**：直接把长文档、PDF、2 小时 YouTube 视频字幕塞给 Claude Code 等 AI 工具，单次操作消耗数万至十几万 Token。

**双重成本**：

| 成本类型 | 说明 | 影响 |
|---------|------|------|
| **首次输入成本** | 一次性消耗数万 Token 读取资料 | API 计费直接烧钱 |
| **Context 持续成本** | 后续每一轮对话都要拖着资料运行 | 成本指数级上升 |
| **模型脆化** | 超长 Context 导致模型遗忘/幻觉 | 答案质量下降 |

**对比实验数据**（来自视频）：

| 方式 | Token 消耗 | Context 压力 |
|------|-----------|-------------|
| Claude Code 直接扒字幕总结视频 | **34,000+ Token** | 全程扛着 |
| notebooklm-py + Claude Code | 几乎为零 | Claude 只收最终答案 |

```
传统方式（直接塞资料）：
  资料进 Context ──→ 每轮对话都背着资料跑 ──→ Token 指数增长

notebooklm-py 方式：
  资料进 NotebookLM ──→ Google 后端处理 ──→ 只把结论给 Claude
                                              │
                                              └─→ 零 Token 消化
```

---

## notebooklm-py 项目概览

> [!info] 基本信息
> - **仓库**：https://github.com/teng-lin/notebooklm-py
> - **Stars**：17.3k / **Forks**：2.3k / **语言**：Python 100%
> - **最新版本**：v0.7.3（2026-06-30）/ **协议**：MIT
> - **作者**：teng-lin（社区项目，非 Google 官方）

### 定位

NotebookLM 是 Google 的免费 AI 笔记本工具，资料丢进去后**只根据你给的资料回答，每句话带引用，基本不瞎编**。但它只有网页版，AI 无法直接调用。

notebooklm-py 是社区开源项目，通过逆向 Google 未公开 API，将 NotebookLM 完整功能搬进命令行，并支持 Python API、CLI、MCP Server 三种接入方式。

### 使用方式对比

| 方式 | 最适合 | 特点 |
|------|--------|------|
| **CLI** | Shell 脚本、快速任务、CI/CD | 最简单，视频重点演示 |
| **Python API** | 应用集成、自定义 Pipeline | 异步支持、灵活控制 |
| **MCP Server** | Claude Desktop/Code、Codex | stdio 本地或远程部署 |
| **REST Server** | 本地 HTTP 自动化 | 无需每次启动 CLI |

### 支持的输入源

| 源类型 | 输入方式 |
|--------|---------|
| 网页 | 直接给 URL |
| 本地文件 | 文件路径（PDF、Word、EPUB、Markdown 等） |
| YouTube 视频 | 直接贴链接 |
| Google Drive | 文件 ID |
| 粘贴文本 | 直接粘贴 |

---

## 核心功能拆解

### 1. 带引用的问答（RAG）

NotebookLM 的核心能力：基于你喂入的资料回答问题，每段回答附带引用编号，标注信息来源于哪份资料的哪个位置。

```bash
# 创建笔记本并设为当前笔记本（--use 避免后续重复指定 ID）
notebooklm create "My Research" --use

# 添加多种来源
notebooklm source add "https://example.com/docs"     # 网页
notebooklm source add "./specs.pdf"                   # 本地 PDF
notebooklm source add "https://youtube.com/watch?v=xxx" # YouTube

# 提问（答案带引用编号）
notebooklm ask "这个项目的核心架构是什么？"
```

**最佳实践**：
- ✅ 一份资料喂进去后可反复提问，无需重复上传
- ✅ 引用编号可溯源，避免 AI 幻觉
- ❌ 不要把 NotebookLM 当通用搜索引擎，它只回答你喂的资料范围内的内容

### 2. 定制音频播客（Audio Overview）

NotebookLM 的招牌功能：将资料转化为双人对谈播客。

```bash
# 基本用法：生成中文播客
notebooklm generate audio "侧重讲 MCP 的核心概念" \
  --format debate \
  --language zh-CN \
  --wait
```

**参数说明**：

| 参数 | 用途 | 可选值 |
|------|------|--------|
| `--format` | 对话风格 | `deep-dive`（默认深度对谈）、`debate`（辩论抬杠）、`brief`、`critique` |
| `--language` | 输出语言 | 查看支持列表：`notebooklm language list` |
| `--wait` | 同步等待生成完成 | 不加则异步，用 `notebooklm artifact list` 查状态 |

**决策树：选哪种 format？**

```
想深入了解细节？
├─ ✅ → deep-dive（默认，深度对谈）
├─ 想保持清醒不打瞌睡？
│  └─ ✅ → debate（两主播互抬杠）
├─ 想快速概览？
│  └─ ✅ → brief（简短摘要）
└─ 想听批判性分析？
   └─ ✅ → critique（评论式分析）
```

### 3. 结构化导出（命令行的核心优势）

网页版 NotebookLM 的致命缺点：内容只能在浏览器里看，导不出来。命令行版彻底解决。

| 导出类型 | 格式 | 用途 |
|---------|------|------|
| **测验题库** | Markdown / JSON / HTML | 团队新人技能考核 |
| **思维导图** | JSON（层级树） | 导入笔记软件或让 Claude 渲染 |
| **数据表** | CSV | 结构化数据提取 |
| **Flashcards** | JSON / Markdown / HTML | 间隔复习 |
| **研究报告** | Markdown | 带引用的完整报告 |
| **幻灯片** | PDF / PPTX | 演示文稿 |

```bash
# 生成测验题（Markdown + 标准答案）
notebooklm generate quiz
notebooklm download quiz --output quiz.md

# 生成思维导图（JSON 结构化数据）
notebooklm generate mind-map
notebooklm download mind-map --output mindmap.json
# → JSON 可丢给 Claude 渲染成可视化图表，或导入笔记软件

# 不想处理 JSON？直接让 Claude 画图
# "把这个 mindmap.json 渲染成 ASCII 思维导图"
```

---

## Claude Code 集成：对照实验

### 实验设置

对同一个任务（总结 YouTube 视频），对比有无外挂的 Token 消耗。

### 流程对比

```
【无外挂】Claude Code 直接处理
  用户指令 ──→ Claude 自己找 yt-dlp ──→ 扒字幕进 Context ──→ 总结
                                    │
                                    └─→ 34,000+ Token 💸

【有外挂】Claude Code + notebooklm-py
  用户指令 ──→ Claude 调 notebooklm CLI
                   │
                   ├─→ notebooklm create
                   ├─→ notebooklm source add
                   ├─→ notebooklm ask / generate
                   │
                   └─→ 只把 NotebookLM 的答案返回 Claude
                       │
                       └─→ Context 几乎不动 ✅
```

### 集成方式

```bash
# 安装 Claude Code skill
notebooklm skill install
# → 安装到 ~/.claude/skills/notebooklm
# → 之后在 Claude Code 对话中"点名"即可触发

# Claude Code 自动执行：
# 1. notebooklm create "临时笔记本" --use
# 2. notebooklm source add <资料>
# 3. notebooklm ask <问题>
# 4. 返回答案给 Claude（原始资料零 Token 进 Context）
```

**核心价值**：NotebookLM 当图书馆管理员负责读资料，Claude Code 当大脑负责推理。Google 免费干重活，Claude 只拿最终答案。

---

## 免费 Deep Research

NotebookLM 内置研究代理（Research Agent），功能媲美市面每月几十美金的付费产品。

```
Deep Research 流程：

  给定题目 ──→ NotebookLM Research Agent
                    │
                    ├─→ 自动上网搜索
                    ├─→ 筛选可信来源
                    ├─→ 自动下载文章
                    ├─→ 导入笔记本作为新资料
                    │
                    └─→ 生成带引用的研究报告
```

```bash
# 三行命令出报告
notebooklm create "Deep Research" --use
notebooklm source add-research "MCP 协议的设计理念与生态现状" --mode deep
notebooklm generate report --language zh-CN --wait
notebooklm download report --output report.md
```

| 对比维度 | NotebookLM Deep Research | 付费 Deep Research 产品 |
|---------|------------------------|------------------------|
| 价格 | 免费 | $20-30/月 |
| 输出质量 | 带引用的研究报告 | 相当 |
| 数据来源 | 自动搜索 + 导入 | 自动搜索 |
| 集成能力 | CLI/API 可自动化 | 通常锁定在特定 UI |

---

## 安装与排错

### 安装（两行命令）

```bash
# 方式 1：uv（推荐）
uv tool install "notebooklm-py[browser]"

# 方式 2：pipx
pipx install "notebooklm-py[browser]"

# 方式 3：虚拟环境内用 pip
python -m venv .venv && source .venv/bin/activate
pip install "notebooklm-py[browser]"
```

### 登录翻车解法

首次 `notebooklm login` 时常见报错：提示找不到自带的 Playwright 浏览器，要求下载几百 MB 的 Chromium。

```bash
# ✅ 解决方案：加 --browser 参数直接调用系统 Chrome
notebooklm login --browser chrome

# 如果组织要求 Edge SSO
notebooklm login --browser msedge

# 或直接复用已登录浏览器的 Cookie（无需弹出登录窗口）
notebooklm login --browser-cookies chrome
```

**验证登录成功**：

```bash
notebooklm auth status
# 预期输出：{"status": "ok"}
```

### 安装三步走

```
Step 1: uv tool install "notebooklm-py[browser]"
Step 2: notebooklm login --browser chrome    # 弹出 Chrome 登录 Google
Step 3: notebooklm skill install            # Claude Code 集成
```

---

## 适用场景决策树

```
你有大量资料要处理？
├─ ✅ 文档（PDF/网页/Drive）
│  ├─ 只需要问答？
│  │  └─ ✅ → source add + ask（带引用 RAG）
│  └─ 想转为其他形式？
│     ├─ 想听播客 → generate audio --language zh-CN
│     ├─ 想出考题 → generate quiz + download
│     ├─ 想画导图 → generate mind-map + download
│     └─ 想写报告 → generate report + download
│
├─ ✅ YouTube 视频（2小时长视频）
│  └─ 直接贴链接 → 自动转写 → 问答/播客/测验
│
├─ ✅ 需要深度研究某个主题
│  └─ source add-research "主题" → 自动搜索 → 生成报告
│
└─ ✅ Claude Code / Codex 集成
   └─ skill install → 对话中"点名"触发 → 零 Token 研究工作流
```

---

## 参考资料

- [notebooklm-py GitHub 仓库](https://github.com/teng-lin/notebooklm-py) — 17.3k stars，v0.7.3
- [NotebookLM 官网](https://notebooklm.google/)
- [notebooklm-py CLI Reference](https://github.com/teng-lin/notebooklm-py/blob/main/docs/cli-reference.md)
- [Claude Code 官网](https://claude.com/claude-code)
- [视频来源：YAHA学堂](https://youtube.com/watch?v=CwnoYYHOgv8)

## 相关笔记

- [[Claude Code]] — Anthropic 的终端 AI 编程助手
- [[NotebookLM]] — Google 的免费 AI 笔记本工具

---

*文档生成时间：2026-07-11*
*基于 notebooklm-py v0.7.3 + 视频内容整理*
