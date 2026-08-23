---
title: notebooklm-py - 用开源 CLI 把 NotebookLM 变成 Claude Code 的零 Token 大脑
aliases: [notebooklm-py 学习笔记, Claude Code + NotebookLM 工作流]
tags:
  - ai-agent
  - notebooklm
  - claude-code
  - cli-tool
  - token-optimization
  - status/active
  - type/doc
source:
  - "https://github.com/teng-lin/notebooklm-py"
  - "https://pypi.org/project/notebooklm-py/"
author: teng-lin (notebooklm-py 作者) / YouTube 影片讲者
created: 2026-07-25
updated: 2026-07-25
description: |
  如何用开源 CLI 工具 notebooklm-py 将 Google NotebookLM 接入 Claude Code，
  让 Google 免费算力承担长文档/影片的重度阅读，大幅降低 LLM Token 消耗。
level: intermediate
stars: 4
note: 基于用户提供之影片大纲 + GitHub 仓库官方 README + 搜索结果交叉验证整理。2026-07 影片未涵盖的品牌更名已补充。
---

# notebooklm-py - 用开源 CLI 把 NotebookLM 变成 Claude Code 的零 Token 大脑

> 把"读 10 万字 PDF / 2 小时 YouTube 字幕"这种重活，外包给 Google NotebookLM 的免费 Gemini 算力，
> Claude Code 只负责调指令、拿结论。`notebooklm-py` 就是打通这条链路的开源 CLI。
> 适合：长文档研究、批量内容生成、想压低 LLM API 帐单的开发者。

## 目录

- [[#⚠️ 重要时效性更正（优先阅读）]]
- [[#核心问题：Token 消耗的指数级陷阱]]
- [[#解法架构：NotebookLM 作为零 Token 合成层]]
- [[#环境搭建与认证]]
- [[#CLI 核心指令]]
- [[#多元内容生成]]
- [[#Claude Code 自动化整合（外挂工作流）]]
- [[#避坑指南]]
- [[#参考资料]]
- [[#相关笔记]]

---

## ⚠️ 重要时效性更正（优先阅读）

影片制作于 Google 改名前，以下三点务必先看：

| 项目 | 影片说法 | 2026-07 最新事实 |
|------|----------|------------------|
| 产品名 | NotebookLM | **Gemini Notebook**（2026-07 Google 改名，服务不变，链接自动跳转） |
| 库名 | notebooklm-py | 库名保留 `notebooklm-py`，不受改名影响 |
| 项目性质 | 开源工具 | **非官方库（Unofficial Library）**，使用 Google 未公开 API，随时可能失效 |

> [!warning] 非官方库风险
> `notebooklm-py` 使用 Google 的**未公开 API（undocumented APIs）**，Google 可以在任何时候改接口让库失效，且重度使用可能被限流。仅适合原型、研究、个人项目，不要用在生产关键路径。

---

## 核心问题：Token 消耗的指数级陷阱

### 痛点

传统模式下，把长文档或长影片字幕（十几万字 / 2 小时字幕）直接喂给 Claude Code 这类 LLM：

```
单次提问成本
  = 输入 Token (长文档塞入 prompt)
  + 输出 Token
  + 后续每轮对话持续拖载同一份 Context
       ↓
  对话越长，每轮成本越呈指数级上升
```

**典型数字**：处理一部长影片或长文档，单轮提问可能烧掉 **30,000 ~ 100,000+ Token**。

### Token 消耗对比

| 模式 | 单次处理 Token | Context 累积 | 适合场景 |
|------|----------------|--------------|----------|
| 传统（塞原文给 LLM） | 30,000 ~ 100,000+ | 每轮叠加，成本指数增长 | 短文档、一次性问答 |
| NotebookLM 外挂（影片对照实验） | ~0（仅调指令） | 不进入 Context | 长文档、反复研究、批量处理 |

> 影片引用的对照实验：传统模式处理长影片/文档耗费 **34,000+ Token**；外挂模式 **零上下文负担**（Claude Code 只读 NotebookLM 回传的精简结论）。

### 为什么 NotebookLM 适合做这件事

NotebookLM（现 Gemini Notebook）是 Google 的免费 AI 笔记本，具备**精确的检索增强生成（Retrieval-Augmented Generation, RAG）**能力：
- 回答均附带**来源引用编号**，不瞎编内容
- Gemini 算力在 Google 端做重度阅读
- 免费额度覆盖大部分个人 / 研究用途

---

## 解法架构：NotebookLM 作为零 Token 合成层

### 核心思路

```
┌─────────────────────────────────────────────────────────────┐
│  传统模式：所有重活都在 LLM 端做                              │
│                                                             │
│  长文档 ────────→ Claude Code Context Window ──→ 回答       │
│  (10万字)           (吃掉数万 Token)                         │
│                                                             │
│  问题：每一轮对话都重新载入，成本指数上升                     │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  外挂模式：重活外包给 Google                                 │
│                                                             │
│  长文档 ──→ NotebookLM (Google 端 Gemini 处理)               │
│                    │                                        │
│                    ↓                                        │
│              精简结论 + 来源引用                             │
│                    │                                        │
│                    ↓                                        │
│             Claude Code (只读结论，零 Context 占用)          │
└─────────────────────────────────────────────────────────────┘
```

### 为什么需要 CLI 化

网页版 NotebookLM 无法被外部 AI 工具或自动化脚本直接存取——这是限制，也是机会：

```
网页版 NotebookLM
  ✗ 无法被脚本调用
  ✗ 无法嵌入 Claude Code / CI/CD
  ✗ 无批量操作
        │
        ↓  notebooklm-py 做的事
  ✓ 把网页功能包装成 CLI / Python API
  ✓ 可被任何 AI Agent 调用
  ✓ 支持批量、脚本化、自动化工作流
```

---

## 环境搭建与认证

### 安装（两种方式）

**方式一：`uv tool`（README 官方推荐）**

```bash
# 推荐：uv tool 隔离安装，不会污染系统 Python
uv tool install "notebooklm-py[browser]"

# 或用 pipx（同样隔离安装）
pipx install "notebooklm-py[browser]"
```

> [!warning] macOS / 现代 Linux 的 pip 陷阱
> 影片提到 `pip install notebooklm-py`。但现代 macOS（Homebrew Python）和 Debian/Ubuntu 上，系统级 `pip install` 会被 **PEP 668** 拦下，报 `error: externally-managed-environment`。
> **务必用 `uv tool` 或 `pipx`**，或先进入 virtualenv 再 pip。

**方式二：virtualenv + pip**

```bash
python3 -m venv .venv && source .venv/bin/activate
pip install "notebooklm-py[browser]"
```

### 首次登录认证

```bash
# 安装后首次运行，会自动下载 Chromium (~170 MB) 并开浏览器登 Google
notebooklm login

# 验证认证成功
notebooklm auth check --test --json
# 期望输出: "status": "ok"
```

### 登录避坑：三种认证方式

影片提到 `--browser chrome` 技巧。README 实际提供**三种认证路径**，按场景选：

| 方式 | 指令 | 适合场景 |
|------|------|----------|
| 默认 Playwright | `notebooklm login` | 首次使用、本机开发 |
| 复用浏览器 Cookie | `notebooklm login --browser-cookies chrome` | 已登入 Chrome，不想再登一次 |
| Microsoft Edge（SSO） | `notebooklm login --browser msedge` | 企业帐号要求 Edge 做 SSO |

> **进阶**：还有 `--master-token` 模式，可无浏览器自动刷新 Cookie，适合服务器 / CI / 远程 MCP connector 场景。

### 认证决策树

```
登录 notebooklm 时怎么选？

├─ 本机首次使用、有 GUI？
│    └─ ✅ notebooklm login（默认 Playwright + Chromium）
│
├─ Chrome 已经登入 Google，想复用？
│    └─ ✅ notebooklm login --browser-cookies chrome
│       (可选特定 profile: --browser-cookies 'chrome::Profile 1')
│
├─ 企业帐号需要 Edge 做 SSO？
│    └─ ✅ notebooklm login --browser msedge
│
└─ 无头服务器 / CI / 自动化？
     └─ ✅ notebooklm login --master-token --account you@example.com
        (一次设定，后续自动刷新 Cookie)
```

---

## CLI 核心指令

### 知识库（Notebook）基本操作

```bash
# 建立知识库
notebooklm create "我的研究"

# 切换到指定知识库（后续指令作用于此库）
notebooklm use <notebook_id>

# 餵资料（三种来源类型）
notebooklm source add "https://example.com/article"        # 网址
notebooklm source add "./paper.pdf"                        # 本地文件
notebooklm source add "https://youtube.com/watch?v=xxx"    # YouTube 影片

# 问答（关键：回答来自你的来源，附引用编号）
notebooklm ask "这份文件的核心论点是什么？"
```

### 支持的来源类型

| 类别 | 格式 |
|------|------|
| 网址 | 任意公开 URL |
| 影片 | YouTube 连结（Google 后端转写） |
| 文件 | PDF、text、Markdown、Word、EPUB |
| 音视频 | audio、video |
| 图片 | images |
| Google Drive | 云端硬盘文件 |
| 文字 | 直接贴文 |

---

## 多元内容生成

这是 NotebookLM 的杀手锏——一个知识库，能产出多维度内容资产。

### 生成类型总览

| 内容类型 | CLI 指令 | 输出格式 | 影片提到的亮点 |
|----------|----------|----------|----------------|
| **Podcast 音频** | `generate audio` | MP3 | 支持 4 种格式（含辩论 debate）、50+ 语言、含中文 |
| **影片概述** | `generate video` / `cinematic-video` | MP4 | 多种视觉风格（whiteboard、cinematic 等） |
| **简报** | `generate slide-deck` | PDF / PPTX | 可微调单张投影片 |
| **测验题库** | `generate quiz --difficulty hard` | JSON / Markdown / HTML | 含标准答案，适合自我检测 |
| **闪卡** | `generate flashcards` | JSON / Markdown / HTML | 可控制数量与难度 |
| **思维导图** | `generate mind-map` | JSON | 结构化 JSON，可汇入笔记软件 |
| **信息图** | `generate infographic` | PNG | 3 种方向、3 种细度 |
| **报告** | `generate report` | Markdown | briefing doc、study guide、blog post |
| **数据表** | `generate data-table` | CSV | 自然语言描述表结构 |

### 生成 + 下载完整流程

```bash
# 生成 Podcast（中文、辩论模式、等待完成）
notebooklm generate audio "make it a debate" --wait
notebooklm download audio ./podcast.mp3

# 生成测验并汇出 Markdown
notebooklm generate quiz --difficulty hard
notebooklm download quiz --format markdown ./quiz.md

# 生成思维导图 JSON
notebooklm generate mind-map --kind note-backed
notebooklm download mind-map ./mindmap.json
```

### Deep Research（免费深度研究）

```bash
# 发起网络研究并自动汇入找到的来源
notebooklm source add-research "AI Agent 框架对比" --import-all

# 或 Drive 研究（fast / deep 两种模式）
notebooklm source add-research "主题" --mode deep
```

> 影片定位这功能为"**免费替代每月数百美元的付费深度研究工具**"——NotebookLM 的 Research Agent 会自动联网检索多个网页、汇整成报告，成本完全在 Google 端。

---

## Claude Code 自动化整合（外挂工作流）

### 核心价值：一句话触发完整流程

把 `notebooklm-py` 注册为 Claude Code 的 Skill（技能）后，使用者只要说一句话：

> "帮我分析这个 YouTube 影片并产出报告"

Claude Code 就会自动执行完整链路：

```
使用者一句话
     │
     ↓
Claude Code (编排者)
     │
     ├─→ notebooklm create      (建库)
     ├─→ notebooklm source add  (丢 YouTube 连结)
     ├─→ notebooklm ask         (提问)
     ├─→ notebooklm generate    (生成报告/测验/Podcast)
     └─→ notebooklm download    (取回产物)
     │
     ↓
回传给使用者：精简结论 + 本地文件
(Context Window 零占用)
```

### 安装为 Skill 的两种方式

```bash
# 方式一：CLI 直接安装到 ~/.claude/skills/notebooklm
notebooklm skill install

# 方式二：npx 从 GitHub 拉取（开放技能生态）
npx skills add teng-lin/notebooklm-py
```

### Context Window 零负担的关键

```
┌──────────────────────────────────────────────┐
│  全文字幕 / 巨量 PDF                           │
│  (仅在 NotebookLM 端被处理)                   │
│         │                                     │
│         ↓  (Google Gemini 算力)               │
│  精简结论 / 报告 / 产物文件                    │
│         │                                     │
│         ↓                                     │
│  回传给 Claude Code                           │
│  → 只有最终结果进入 Context Window             │
│  → 原始长文档从未进入 LLM Context              │
└──────────────────────────────────────────────┘
```

### 深度玩法：知识蒸馏成永久 Skill（README 补充）

影片没提到，但 README 里的进阶用法值得注意：

```
Deep Research / 文档语料
       │
       ↓  NotebookLM Gemini 浓缩
    精炼知识
       │
       ↓  烘焙成 SKILL.md
    永久技能（git 版本化）
       │
       ↓  Agent 启动时载入
    零运行时 Token / 零网络调用
```

把 NotebookLM 蒸馏的领域知识写成 `SKILL.md`，Agent 启动时载入——**建一次，永久复用**，比每次都现查省得多。

---

## 避坑指南

### ✅ 推荐做法

- **用 `uv tool` 或 `pipx` 安装**，避免 PEP 668 限制和依赖冲突
- **企业 SSO 帐号用 `--browser msedge`**，避免认证卡关
- **服务器场景用 `--master-token`**，一次设定，自动刷新 Cookie
- **重度研究拆成多个 Notebook**，单库来源数受 Google 帐号层级限制
- **优先用 `--browser-cookies chrome` 复用浏览器 session**，省去重复登录
- **用 `auth refresh --quiet` 配合 cron / launchd**，定期保活 Cookie

### ❌ 避免做法

- **不要用 `pip install` 装到系统 Python**（macOS/Linux 会报 PEP 668 错误）
- **不要用在生产关键路径**——非官方库，Google 改 API 会随时失效
- **不要重度滥用**——Heavy usage 会被 Google 限流
- **不要假设接口稳定**——`notebooklm-py` 驱动的是未公开 API，随时可能 break
- **不要忽略 `auth check --test`**——Cookie 过期是常见问题，定期检测

### 常见问题排查

```bash
# 诊断认证 / Cookie 问题
notebooklm auth check --test

# Cookie 过期保活（可放 cron）
notebooklm auth refresh --quiet

# 检查本地 skill 安装状态
notebooklm skill status

# 查看多帐号 profile
notebooklm profile list
notebooklm profile switch work
```

---

## 参考资料

- [teng-lin/notebooklm-py - GitHub](https://github.com/teng-lin/notebooklm-py)（官方仓库、README、文档）
- [notebooklm-py - PyPI](https://pypi.org/project/notebooklm-py/)
- [NotebookLM-py: The CLI Tool That Unlocks Google NotebookLM - Medium](https://medium.com/@tentenco/notebooklm-py-the-cli-tool-that-unlocks-google-notebooklm-1de7106fd7ca)
- [This NotebookLM + Claude Code Workflow Is Insane - YouTube](https://www.youtube.com/watch?v=fV17ZkPBlAc)
- [I used Claude Code to automate NotebookLM - LinkedIn](https://www.linkedin.com/posts/boguniewicz_i-used-claude-code-to-automate-notebooklm-activity-7435358095381348352-8rSJ)

## 相关笔记

- [[Claude Code 技能系统]]
- [[Token 优化策略]]
- [[RAG 与检索增强生成]]
- [[AI Agent 工作流设计]]

---

*文档生成时间：2026-07-25*
*基于 notebooklm-py 官方 README + 影片内容大纲 + 搜索交叉验证*
*注意：Google 已于 2026-07 将 NotebookLM 改名为 Gemini Notebook，库名不变*
