---
title: Hermes Agent + NotebookLM 打造数据飞轮
aliases: [Hermes NotebookLM 集成, notebooklm-py 数据飞轮]
tags:
  - hermes-agent
  - notebooklm
  - ai-agent
  - knowledge-base
  - automation
  - status/active
  - type/tool-guide
source:
  - "https://www.youtube.com/watch?v=S6XCelOhZ6w"
author: AI超元域
created: 2026-05-06
updated: 2026-05-06
description: 在 Hermes Agent 中集成 Google NotebookLM，通过自然语言操控笔记本管理、论文检索、自动生成 PPT/视频/播客/思维导图，构建持续积累的知识飞轮
level: intermediate
stars: 3
---

# Hermes Agent + NotebookLM 打造数据飞轮

> [!info] 基本信息
> - **频道**: AI超元域
> - **时长**: 13:50
> - **发布**: 2026-04-26
> - **来源**: [YouTube](https://www.youtube.com/watch?v=S6XCelOhZ6w)
> - **相关项目**: [win4r/notebooklm-py](https://github.com/win4r/notebooklm-py)

---

## 目录

- [核心定位](#核心定位)
- [解决的问题](#解决的问题)
- [三层架构设计](#三层架构设计)
- [功能演示](#功能演示)
- [安装与认证](#安装与认证)
- [Cookies 持久化优化](#cookies-持久化优化)
- [实际应用场景](#实际应用场景)
- [知识飞轮模型](#知识飞轮模型)
- [与 RAG 知识库的对比](#与-rag-知识库的对比)
- [参考资料](#参考资料)
- [相关笔记](#相关笔记)

---

## 核心定位

在 Hermes Agent 中集成 Google NotebookLM，相当于为 Agent **外挂了一个云端知识库**。用户只需用自然语言就能完成 NotebookLM 网页版的全部操作，包括笔记本管理、来源添加、内容生成、跨笔记本检索等。

**关键区别**：之前用 RAG（如 Mem0）构建知识库是 Hermes Agent 的内置方案，NotebookLM 则是外部云端方案的集成，两者互补而非替代。

---

## 解决的问题

| 痛点 | RAG（Mem0 等） | NotebookLM 集成 |
|------|---------------|----------------|
| 多设备同步 | 需自建同步机制 | Google 账号天然同步 |
| 内容生成 | 不支持 | PPT/视频/播客/思维导图/Quiz |
| 多模态输出 | 纯文本 | 11 种格式 |
| 部署成本 | 需要 Vector DB + Embedding | 零成本（Google 免费提供） |
| 设置复杂度 | 高（向量库+Embedding 模型） | 低（一行命令安装） |

---

## 三层架构设计

```
┌─────────────────────────────────────────────────┐
│              Layer 1: 用户输入层                 │
│                                                 │
│  用户自然语言指令                                │
│  "把这篇论文加到 NotebookLM"                     │
│  "生成讲解视频保存到桌面"                        │
│  "对比 Notebook A 和 B 的内容"                   │
└──────────────────────┬──────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────┐
│              Layer 2: Hermes Agent 核心层         │
│                                                 │
│  ┌─────────────┐    ┌────────────────────────┐  │
│  │ LLM 推理    │───→│ notebooklm-py CLI      │  │
│  │ 意图识别    │    │ notebooklm login       │  │
│  │ 指令转换    │    │ notebooklm source add  │  │
│  └─────────────┘    │ notebooklm generate    │  │
│                     │ notebooklm download    │  │
│                     └───────────┬────────────┘  │
│                                 │               │
│  ┌──────────────────────────────┴───────────┐   │
│  │ Cookies 自动刷新（每 15-30 分钟）         │   │
│  │ rookiepy 读取 Chrome cookies              │   │
│  └──────────────────────────────────────────┘   │
└──────────────────────┬──────────────────────────┘
                       │ HTTP（undocumented API）
                       ▼
┌─────────────────────────────────────────────────┐
│              Layer 3: NotebookLM 云端层          │
│                                                 │
│  Google 服务器                                   │
│  ├── 来源处理（PDF/URL/YouTube 转写）           │
│  ├── 内容生成（Audio/Video/Slides/Quiz...）     │
│  ├── 跨来源检索                                  │
│  └── 笔记本管理与分享                            │
└─────────────────────────────────────────────────┘
```

**中英文提示词智能转换**：用户输入中文，Hermes Agent 自动转换为英文 API 调用（NotebookLM API 以英文为主），检索精准且响应迅速。

---

## 功能演示

### 自然语言操控笔记本

```
用户: "列出我的 NotebookLM 所有笔记"
Agent → notebooklm list → 返回所有笔记本列表

用户: "对比第一和第二个笔记本的内容"
Agent → notebooklm use <id1> → ask → notebooklm use <id2> → ask → 综合对比
```

### 添加来源

```
用户: "把这篇论文加到智能体记忆的笔记里"
Agent → notebooklm use <nb_id> → source add "https://arxiv.org/..." → wait
```

网页端 NotebookLM 同步可见已添加的论文。

### 自动生成内容

| 指令 | 生成内容 | 下载格式 |
|------|----------|----------|
| "生成幻灯片" | Slide Deck | PDF / PPTX |
| "生成讲解视频" | Video Overview | MP4 |
| "生成播客音频" | Audio Overview | MP3 |
| "生成思维导图" | Mind Map | JSON |
| "生成 Quiz 测验" | Quiz | JSON / Markdown |

### 跨笔记本检索

```
用户: "在 LoRA 微调相关的笔记本中搜索"
Agent → notebooklm use <nb_id> → ask "LoRA 微调的关键概念" → 返回结构化答案
```

---

## 安装与认证

### 方式一：Hermes Agent 一键安装

```bash
# 1. 安装 skill
hermes skills tap add win4r/notebooklm-py
hermes skills install win4r/notebooklm-py/skills/notebooklm --force

# 2. 安装 Python 包（审计标签）
VIRTUAL_ENV=~/.hermes/hermes-agent/venv uv pip install \
  "notebooklm-py[browser,cookies] @ git+https://github.com/win4r/notebooklm-py@v0.3.4-hermes.4"

# 3. 认证（推荐 browser-cookies）
notebooklm login --browser-cookies chrome

# 4. 配置自动刷新
echo 'NOTEBOOKLM_REFRESH_CMD=notebooklm login --browser-cookies chrome' >> ~/.hermes/.env
```

### 方式二：Codex / Claude Code 安装

```bash
npx skills add win4r/notebooklm-py
```

### 认证方案对比

| 方式 | 优点 | 缺点 |
|------|------|------|
| `--browser-cookies` | 无需手动登录，自动续期 | 首次需 Keychain 授权 |
| `notebooklm login` | 标准流程 | Google 可能限制新设备 |
| 手动导出 Cookie | 兜底方案 | 15-30 分钟过期 |

---

## Cookies 持久化优化

**核心问题**：Google 的 `__Secure-*PSIDTS` cookies 每 15-30 分钟轮换一次，静态 cookie 文件很快失效。

**解决方案**（win4r fork 的 PR #298）：

```
RPC 调用过期
    │
    ▼
执行 $NOTEBOOKLM_REFRESH_CMD
    │
    ▼
rookiepy 从 Chrome 读取最新 cookies
    │
    ▼
重写 storage_state.json
    │
    ▼
自动重试原始调用（一次性，不循环）
```

设置后完全无感——cookies 过期时自动刷新，无需人工干预。

---

## 实际应用场景

### 场景一：学术论文研究

```
1. 搜索论文 → 获取 arxiv 链接
2. notebooklm source add "https://arxiv.org/..."
3. notebooklm ask "这篇论文的核心贡献是什么？"
4. notebooklm generate audio "总结论文要点" --wait
5. notebooklm download audio ./summary.mp3
```

**视频演示**：以 LoRA 微调论文为例，创建专门笔记本 → 添加论文 → 深度问答 → 生成讲解视频。

### 场景二：内容创作

```
1. 收集多个信息源（URL/YouTube/PDF）
2. notebooklm generate slide-deck --wait
3. notebooklm generate cinematic-video "教程风格" --wait
4. notebooklm generate mind-map --wait
```

一次性从同一组来源生成多种内容格式。

### 场景三：自动化任务流水线

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│ 论文检索  │───→│ 自动入库  │───→│ 内容生成  │───→│ 本地保存  │
│ (搜索)   │    │ (添加来源)│    │ (多格式)  │    │ (下载)   │
└──────────┘    └──────────┘    └──────────┘    └──────────┘
```

全部通过 Hermes Agent 的自然语言指令串联，无需手动操作。

---

## 知识飞轮模型

```
        ┌──────────────────────────────┐
        │                              │
        ▼                              │
   收集信息源                       分享笔记
   (论文/URL/PDF)                  (公开链接)
        │                              │
        ▼                              │
   NotebookLM                     多人协作
   索引 + 理解                    同步访问
        │                              │
        ▼                              │
   持续添加来源 ◄─────────────── 知识积累
        │
        ▼
   生成多格式内容
   (PPT/视频/播客/导图)
        │
        └──────────→ 分享 → 获取反馈 → 收集更多来源
```

**核心循环**：添加来源 → 生成内容 → 分享笔记 → 持续添加 → 形成长效积累

关键优势：
- Google 账号天然支持多设备同步
- 笔记可分享给他人，协作构建知识库
- 每次添加新来源都在增强现有笔记本的检索能力

---

## 与 RAG 知识库的对比

```
┌─────────────────────────────────────────────────────────────┐
│                    RAG（Mem0 等方案）                        │
│                                                             │
│  本地部署 → 需要向量数据库 + Embedding 模型                 │
│  优势: 数据完全本地控制，无外部依赖                          │
│  劣势: 不支持多媒体生成，部署复杂                            │
├─────────────────────────────────────────────────────────────┤
│                    NotebookLM 集成                          │
│                                                             │
│  云端服务 → Google 免费提供                                  │
│  优势: 零部署，多模态生成，多设备同步                        │
│  劣势: 依赖 Google API（非官方），数据在 Google 端           │
├─────────────────────────────────────────────────────────────┤
│                    最佳实践: 两者结合                        │
│                                                             │
│  NotebookLM → 研究总结、内容生成、知识分享                  │
│  RAG → 实时查询、本地偏好记忆、隐私数据                     │
└─────────────────────────────────────────────────────────────┘
```

---

## 参考资料

- [win4r/notebooklm-py (Hermes Fork)](https://github.com/win4r/notebooklm-py)
- [teng-lin/notebooklm-py (上游)](https://github.com/teng-lin/notebooklm-py)
- [视频: Hermes Agent+NotebookLM 打造永久数据飞轮](https://www.youtube.com/watch?v=S6XCelOhZ6w)

## 相关笔记

- [[../Hermes/notebooklm-py - NotebookLM Python API 与 Agent Skill]]
- [[Gemini 整合 NotebookLM + 多模态文件输出]]
- [[OpenClaw - ChatGPT Plus OAuth 免 API 费用使用]]
