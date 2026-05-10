---
title: Gemini 整合 NotebookLM + 多模态文件输出 - 2026 全新工作流
aliases: [Gemini Notebooks, NotebookLM 整合 Gemini, Gemini 文件生成]
tags:
  - gemini
  - notebooklm
  - google-ai
  - ai-workflow
  - multimodal
  - status/active
  - type/tutorial
source:
  - "https://www.youtube.com/watch?v=kSgxHy-mqxI"
  - "https://www.jeffsu.org/notebooklm-changed-completely-heres-what-matters-in-2026/"
  - "https://blog.google/innovation-and-ai/products/gemini-app/generate-files-in-gemini/"
author: 大有牧森 Austin Chou (YouTube) / Jeff Su / Google 官方博客
created: 2026-05-06
updated: 2026-05-06
description: Google 将 NotebookLM 完整整合进 Gemini，加上多模态文件输出能力（PDF/Word/Excel/网页等 11 种格式），彻底终结来回切换的碎片化工作流
level: intermediate
stars: 4
note: 无字幕（转录面板空壳），基于视频章节+描述 + 外部教程综合整理
---

# Gemini 整合 NotebookLM + 多模态文件输出

> Google 于 2026 年 4 月正式将 NotebookLM 完整集成到 Gemini 应用中（Notebooks 功能），同月底推出多模态文件生成能力，支持 11 种格式直接输出到 Google Drive。这两项更新彻底终结了「在 NotebookLM 和 Gemini 之间来回切换」的碎片化体验，将 Gemini 从聊天助手进化为完整的「Research-to-Content」生产线。

---

## 目录

- [两大更新核心价值](#两大更新核心价值)
- [更新一：NotebookLM 整合进 Gemini](#更新一notebooklm-整合进-gemini)
- [更新二：多模态文件输出](#更新二多模态文件输出)
- [NotebookLM 核心能力回顾](#notebooklm-核心能力回顾)
- [五大高效工作应用](#五大高效工作应用)
- [与其他工具的对比](#与其他工具的对比)
- [参考资料](#参考资料)

---

## 两大更新核心价值

```
┌───────────────────────────────────────────────────────────┐
│                  2026 之前（碎片化）                        │
│                                                           │
│  Gemini ←──手动复制──→ NotebookLM ←──手动上传──→ Docs    │
│                                                           │
│  痛点：来回切换、上下文丢失、重复粘贴                       │
├───────────────────────────────────────────────────────────┤
│                  2026 之后（一体化）                        │
│                                                           │
│  NotebookLM 资料源 ──→ Gemini 对话 ──→ 直接生成文件        │
│        ↑                    │                ↓            │
│   PDF/Drive/Web         内置记忆         Google Drive     │
│                                           自动保存         │
│                                                           │
│  省下 80% 来回查找时间                                     │
└───────────────────────────────────────────────────────────┘
```

---

## 更新一：NotebookLM 整合进 Gemini

### 发布时间与适用范围

- **发布日期**：2026 年 4 月 8 日宣布，网页端先行
- **适用订阅**：Google AI Ultra / Pro / Plus
- **功能名称**：**Notebooks in Gemini**（简称 Notebooks）

### 界面架构：三栏布局

```
┌─────────────┬──────────────────┬─────────────────┐
│  来源面板    │    对话面板       │   工作室面板    │
│  (Sources)  │    (Chat)        │   (Studio)      │
│             │                  │                 │
│ • 上传文件  │ • 提问           │ • 生成报告      │
│ • Drive 搜索│ • 摘要           │ • 生成简报      │
│ • 网页搜索  │ • 深度研究       │ • 生成信息图    │
│ • 影音来源  │ • 记忆上下文     │ • 思维导图      │
│             │                  │ • 数据表格      │
│   告诉AI    │   和来源对话      │ • 影片摘要      │
│  "答案在这" │                  │ • 测验/闪卡     │
│             │                  │ • 音频概述      │
└─────────────┴──────────────────┴─────────────────┘
```

### 三种搜索方式（来源面板）

| 方式 | 类比 | 适用场景 |
|------|------|----------|
| Web + Fast Research | Google 搜索 | 快速找公开资料 |
| Drive + Fast Research | Drive 搜索栏 | 从已有文件中定位 |
| Web + Deep Research | 自动研究报告 | 让 AI 读完资料写报告 |

**注意**：Deep Research 不推荐优先使用 — 如果你有领域知识，手动筛选来源质量更好。Gemini/ChatGPT/Claude 的 Deep Research 效果更好。

### 对话记忆与分类

- 对话可**记忆**：后续对话保留上下文，不需要重复说明背景
- 对话可**分类**：按项目/主题整理历史对话
- 可**关闭记忆**：对敏感或临时任务关闭记忆功能
- **删除对话历史**：防止 AI 被先前对话影响；删除前将有价值的见解存为笔记

### 客制化笔记本语气风格

可在笔记本层级设定自定义指令（Custom Instructions），让所有回复围绕特定目标：

- 专业报告语气
- 教学讲解语气
- 特定行业术语规范
- 品牌风格指南（颜色、字体、设计风格）

**进阶用法**：将重要笔记转化为来源（Source），确保该洞察被纳入后续所有 Studio 输出。

---

## 更新二：多模态文件输出

### 发布时间与支持格式

- **发布日期**：2026 年 4 月 29 日
- **支持格式**（11 种）：

| 类别 | 格式 |
|------|------|
| Google Workspace | Docs, Sheets, Slides |
| Microsoft Office | Word (.docx), Excel (.xlsx), PowerPoint (.pptx) |
| 通用 | PDF (.pdf), CSV, LaTeX |
| 纯文本 | TXT, RTF, Markdown (.md) |

### 核心能力

```
对话提示
    │
    ▼
Gemini 生成内容
    │
    ├──→ PDF 文件 ──→ 下载/分享
    ├──→ Word 文档 ──→ 下载/分享
    ├──→ Excel 表格 ──→ 下载/分享
    ├──→ Google Docs ──→ 自动存入 Drive（活文档）
    ├──→ Google Sheets ──→ 自动存入 Drive
    ├──→ Google Slides ──→ 自动存入 Drive
    ├──→ Markdown ──→ 开发者友好
    ├──→ LaTeX ──→ 学术论文
    └──→ 交互式网页 ──→ 直接生成可运行的 HTML
```

### Gemini 图像编辑功能

除了生成文档，还支持图像编辑：
- 在对话中直接编辑生成图片
- 配合 NotebookLM 的简报功能使用

### AI 生成交互式网页

视频重点演示的功能：
- 提示 AI 直接生成可交互的 HTML 网页
- 适合快速原型、数据可视化、教学演示
- 生成后可直接部署或进一步编辑

---

## NotebookLM 核心能力回顾

### 核心优势（Jeff Su 总结）

NotebookLM 最适合的场景，三个条件同时满足时最强：

1. **你已知道答案在哪些文档里**，只需要 AI 帮你提取
2. **来源格式多样**（PDF、表格、PPT、音视频），单一来源无法呈现全貌
3. **需要 AI 严格基于文档内容回答**，不能幻觉（高风险场景）

### Studio 工具分级

**Tier 1（必用）：**

| 工具 | 用途 | 最佳实践 |
|------|------|----------|
| Reports | 从原始来源到完整报告 | 跳过默认格式，选「建议格式」 |
| Slide Decks | 直接生成简报 | 用于叙事提案（brainstorming），最终输出建议手动调整 |
| Infographics | 信息可视化 | 上传品牌指南作为来源 |
| Mind Maps | 一览全局 + 交互探索 | 点击分支进入深入对话 |

**Tier 2（场景化）：**

| 工具 | 用途 |
|------|------|
| Data Tables | 结构化提取可排序过滤的表格 |
| Video Overviews | 影片式摘要（适合长文/访谈） |
| Quiz | 从来源生成测验题（适合活动互动） |
| Flash Cards | 记忆卡片（适合考试准备） |
| Audio Overviews | 音频摘要（通勤/做家务时听） |

### 来源类型与特性

| 来源类型 | 特性 |
|----------|------|
| Google Docs/Slides/Sheets | **活文档**：自动获取最新修改 |
| PDF | 静态上传，不会自动更新 |
| 网页 URL | 动态抓取 |
| 音视频 | 自动转写分析 |
| 每个来源 | 最高 1,000,000 token 上下文（约 50 万字） |

---

## 五大高效工作应用

### 应用 1：跨来源分析报告

```
供应商 A PDF ──┐
供应商 B Excel ──┼──→ NotebookLM ──→ 比较报告 ──→ Google Docs
供应商 C 影片 ──┘    (严格基于来源)      (自动存 Drive)
```

**场景**：比较多个保险方案，找出最佳牙科覆盖。

### 应用 2：会议记录知识库

```
会议录音 ──→ Fireflies/转写 ──→ NotebookLM 来源
                                    │
                              会前快速提问
                              "上次跟这个客户
                               讨论了什么？"
```

### 应用 3：健康报告趋势追踪

每年上传体检报告，让 NotebookLM：
- 标记与去年相比显著变化的项目
- 高亮需要关注的趋势

### 应用 4：税务与会计

上传财务报表 + 税法条文，提问：
- 「根据我的收入和支出，我有哪些扣除项资格？」

### 应用 5：学习与考试准备

```
考试材料 ──→ NotebookLM ──┬──→ 闪卡（记忆）
                          ├──→ 测验（检验）
                          ├──→ 思维导图（全局理解）
                          └──→ Audio Overview（通勤复习）
```

---

## 与其他工具的对比

| 维度 | Gemini + NotebookLM | ChatGPT + Canvas | Claude + Artifacts |
|------|---------------------|-------------------|---------------------|
| 文档输出格式 | 11 种（含 Office/Google） | 有限 | Markdown/代码为主 |
| 来源严格引用 | 强（Grounded） | 中等 | 中等 |
| Google 生态整合 | 原生（Drive/Gmail/Docs） | 无 | 无 |
| 长期记忆 | 笔记本 + 对话记忆 | 项目级记忆 | 无原生记忆 |
| 幻觉控制 | 最好（限制在来源内） | 一般 | 一般 |
| 多来源格式 | PDF/表格/音视频/网页 | 主要文本 | 主要文本 |
| 订阅费用 | Ultra $249.99/月 | Plus $20/月 | Pro $20/月 |

---

## 参考资料

- [NotebookLM in 2026: What Changed and What Matters (Jeff Su)](https://www.jeffsu.org/notebooklm-changed-completely-heres-what-matters-in-2026/)
- [You can now easily generate files in Gemini (Google Blog)](https://blog.google/innovation-and-ai/products/gemini-app/generate-files-in-gemini/)
- [Gemini app can now generate Google Docs, PDF, and other files (9to5Google)](https://9to5google.com/2026/04/29/gemini-app-generate-files/)
- [Gemini Now Delivers Answers as Docs, PDFs, Excel, More (PCMag)](https://www.pcmag.com/news/skip-copy-and-paste-gemini-now-delivers-answers-as-docs-pdfs-excel-more)
- [如何在 Gemini 中使用 NotebookLM 2026 的笔记本功能 (ai.cc)](https://www.ai.cc/zh/blogs/how-to-use-notebooks-in-gemini-with-notebooklm-2026-complete-tutorial/)
- [Gemini 与 NotebookLM 深度整合 (Vocus)](https://vocus.cc/article/69da5498fd89780001e6582b)
- [New ways to customize and interact with your content in NotebookLM (Google Workspace Updates)](https://workspaceupdates.googleblog.com/2026/03/new-ways-to-customize-and-interact-with-your-content-in-NotebookLM.html)

## 相关笔记

- [[Andrew Wilkinson 六层 AI 架构]]
- [[OpenClaw - ChatGPT Plus OAuth 免 API 费用使用]]
