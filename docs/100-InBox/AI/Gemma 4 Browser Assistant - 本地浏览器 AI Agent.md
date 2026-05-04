---
title: Gemma 4 Browser Assistant - 本地浏览器 AI Agent
aliases: [Gemma 4 浏览器扩展, 本地浏览器 AI, BrowserAgent]
tags:
  - ai/local-llm
  - ai/browser-agent
  - tool/chrome-extension
  - status/reviewed
source:
  - "https://www.youtube.com/watch?v=5PRb-_39wko"
  - "https://github.com/nico-martin/gemma4-browser-extension"
author: Nico Martin
created: 2026-05-02
updated: 2026-05-02
description: 基于 Gemma 4 + Transformers.js 的 Chrome 扩展，100% 本地运行，支持标签页管理、历史语义搜索和页面 RAG 问答
level: beginner
stars: 4
---

# Gemma 4 Browser Assistant - 本地浏览器 AI Agent

> 一个完全在浏览器内运行的 AI Agent Chrome 扩展，由 Nico Martin 开发，使用 Google Gemma 4 模型 + Transformers.js + WebGPU，无需服务器、无需 API Key、无需联网，实现标签页管理、浏览器历史语义搜索和页面内容 RAG 问答。

---

## 目录

1. [什么是 Gemma 4 Browser Assistant](#什么是-gemma-4-browser-assistant)
2. [Edge AI：从云端到本地的范式转变](#edge-ai从云端到本地的范式转变)
3. [核心功能详解](#核心功能详解)
4. [技术架构](#技术架构)
5. [安装与使用](#安装与使用)
6. [行业趋势：Personal AI 的崛起](#行业趋势personal-ai-的崛起)
7. [适用场景与局限](#适用场景与局限)

---

## 什么是 Gemma 4 Browser Assistant

由开发者 **Nico Martin** 构建的开源 Chrome 扩展，将 Google 的 **Gemma 4 E2B** 模型完全运行在浏览器内。

**核心卖点**：
- **100% 本地**：所有推理在设备上完成，数据不离开浏览器
- **零成本**：无需 API Key、无需订阅
- **离线可用**：首次下载模型后即可断网使用
- **完全开源**：GitHub 仓库可自行构建和修改

**关键参数**：

| 项目 | 详情 |
|------|------|
| 模型 | `onnx-community/gemma-4-E2B-it-ONNX`（instruction-tuned） |
| 推理框架 | Transformers.js（ONNX Runtime + WebGPU） |
| Embedding 模型 | `all-MiniLM-L6-v2` |
| 向量存储 | IndexedDB（浏览器内置） |
| 最低 Chrome 版本 | 113+（需 WebGPU 支持） |
| 扩展大小 | ~9.8 MiB（不含模型） |

---

## Edge AI：从云端到本地的范式转变

### 云端 AI vs 本地 AI

```
云端 AI 模式                        本地 AI 模式（Edge AI）
+-----------+     +-----------+     +-----------+     +-----------+
|  用户输入  | --> | 上传数据   | --> | 云端推理   |     |  用户输入  |
+-----------+     +-----------+     +-----------+     +-----------+
                                         |                  |
+-----------+     +-----------+     +-----------+     +-----------+
|  返回结果  | <-- | 下载响应   | <-- |  模型服务  |     | 本地推理   |
+-----------+     +-----------+     +-----------+     +-----------+
                                                              |
                                   +-----------+     +-----------+
                                   |  延迟高    |     | 即时响应   |
                                   |  隐私风险  |     | 数据不出设备|
                                   |  需要联网  |     | 离线可用   |
                                   +-----------+     +-----------+
```

### 为什么 Edge AI 现在可行

| 因素 | 过去 | 现在 |
|------|------|------|
| 模型体积 | 数十 GB，必须云端 | Gemma 4 E2B 轻量化，浏览器可承载 |
| 硬件能力 | CPU 推理极慢 | WebGPU 利用 GPU 并行加速 |
| 浏览器 API | 无 ML 支持 | WebGPU + ONNX Runtime 成熟 |
| 框架支持 | 无 | Transformers.js 将 HuggingFace 模型带进浏览器 |

**类比**：云端 AI 像卡车（原始算力大），本地 AI 像跑车（高效、灵活、响应快）。

---

## 核心功能详解

### 功能 1：跨标签页语义搜索

**问题场景**：开了 40 个标签页，想找 3 天前读过的某篇文章，记不住 URL 也记不住搜索词。

**解决方案**：用自然语言描述需求，Agent 扫描所有打开的标签页并返回答案。

```
示例提问：
"What are the best AI automation strategies
 mentioned across all my open tabs?"

Agent 行为：
1. get_open_tabs -> 获取所有标签页标题/URL/内容
2. 对每个页面提取结构化内容（标题、段落、列表）
3. 生成 Embedding 并做语义匹配
4. 返回综合答案
```

**适用工具**：`get_open_tabs`, `ask_website`, `go_to_tab`

### 功能 2：浏览器历史语义搜索

**问题场景**：上周读了一篇关于用 AI 写着陆页文案的文章，现在想找回来。

**解决方案**：自然语言查询历史记录，而非精确关键词匹配。

```
传统方式：                          语义搜索方式：
history -> 手动翻页                  "Find the article I read about
history -> Ctrl+F 关键词              using AI to write landing
结果：找不到（忘了关键词）              page copy for community offer"
                                    结果：精准返回目标页面
```

**技术实现**：
- 浏览历史存储在 **IndexedDB** 向量数据库
- 页面标题 + 描述 + URL 生成 Embedding
- 支持基于时间的过滤（"上周"、"最近三天"）

**适用工具**：`find_history`

### 功能 3：即时页面理解（RAG）

**问题场景**：打开一篇 40 分钟的研究长文，只想提取与当前项目相关的部分。

**解决方案**：对当前页面直接提问，Agent 提取并总结相关内容。

```
示例提问：
"What parts of this page are most relevant
 for someone building AI automation
 workflows for an online business community?"

Agent 行为（RAG Pipeline）：
1. Content Script 提取页面结构化内容
2. 生成 Embedding（all-MiniLM-L6-v2）
3. 语义匹配最相关段落
4. Gemma 4 生成总结回答
5. highlight_website_element 标注关键位置
```

**适用工具**：`ask_website`, `highlight_website_element`

---

## 技术架构

### 三层架构

```
+--------------------------------------------------+
|              Side Panel（React UI）                |
|  聊天界面 / 会话上下文 / 结果展示                    |
+--------------------------------------------------+
           |  chrome.runtime.sendMessage
           v
+--------------------------------------------------+
|        Background Script（AI Engine）              |
|  Transformers.js / 模型加载 / 推理执行               |
|  工具调度 / Embedding 生成 / 向量搜索                |
+--------------------------------------------------+
           |  chrome.tabs.sendMessage
           v
+--------------------------------------------------+
|          Content Script（DOM 交互）                 |
|  页面内容提取 / 结构化解析 / 元素高亮                 |
+--------------------------------------------------+
```

### 为什么这样设计

| 设计决策 | 原因 |
|----------|------|
| Background Script 加载模型 | 模型只加载一次，所有标签页共享，避免重复加载 |
| Side Panel 而非 Popup | Panel 不会因点击外部关闭，保持会话连续性 |
| Content Script 做 DOM 操作 | 唯一能访问页面 DOM 的组件，安全隔离 |
| IndexedDB 存向量 | 浏览器内置，无外部依赖，持久化存储 |
| WebGPU 推理 | 利用 GPU 并行计算，比 WASM CPU 快数倍 |

### Agent 工具清单

```
Tab Management:
  get_open_tabs          - 列出所有标签页
  go_to_tab              - 切换到指定标签页
  open_url               - 打开新 URL
  close_tab              - 关闭标签页

Website Interaction (RAG):
  ask_website            - 语义搜索当前页面内容
  highlight_website_element - 高亮页面元素

History Search:
  find_history           - 自然语言搜索浏览器历史
```

---

## 安装与使用

### 前置条件

```
- Chrome 113+（需要 WebGPU 支持）
- 现代显卡（集成显卡即可，但独显体验更好）
- 首次使用需下载模型（一次性，约数 GB）
```

### 安装方式

```
方式一：Chrome Web Store（推荐）
  直接搜索 "TransformerJS Gemma 4" 安装

方式二：从源码构建
  git clone https://github.com/nico-martin/gemma4-browser-extension
  npm install && npm run build
  chrome://extensions/ -> 开发者模式 -> 加载已解压扩展 -> 选择 dist/
```

### 使用步骤

```
1. 点击扩展图标打开 Side Panel
2. 首次使用自动下载模型（等待完成）
3. 在聊天框输入自然语言指令
4. Agent 自动调用工具并返回结果
```

---

## 行业趋势：Personal AI 的崛起

### AI 发展的三个阶段

```
阶段 1：集中式 Cloud AI（当前主流）
  ChatGPT / Claude / Gemini
  用户 -> 云端 -> 结果
  特点：强大但依赖网络、隐私顾虑

阶段 2：边缘 AI（正在发生）
  Gemma 4 Browser Agent / Apple Intelligence
  用户 -> 本地设备 -> 结果
  特点：隐私、离线、即时

阶段 3：操作系统级 AI（未来）
  AI 作为 OS 内置能力，非独立 App
  手机/笔记本/平板 -> AI 即系统
  特点：无缝集成、零感知
```

### 当前参与方

| 公司 | 产品 | 形态 |
|------|------|------|
| Google | Gemma 4 + Chrome Built-in AI | 浏览器扩展 + 系统集成 |
| Apple | Apple Intelligence | OS 级集成 |
| Mozilla | Firefox Llama integration | 浏览器内置 |
| HuggingFace | Transformers.js | 开发框架 |

**趋势判断**：模型小型化 + 设备算力提升 + WebGPU 成熟，三重驱动下 Edge AI 将成为主流。先掌握本地 AI 工具的人在效率和隐私上都占据优势。

---

## 适用场景与局限

### 适合的场景

- **信息整合**：多标签页内容交叉分析
- **历史回溯**：语义搜索过去的浏览记录
- **快速阅读**：长文提取关键信息
- **隐私敏感场景**：不希望数据上传云端
- **离线环境**：飞机、弱网环境下的 AI 辅助

### 当前局限

| 局限 | 说明 |
|------|------|
| 模型能力 | Gemma 4 E2B 轻量化模型，复杂推理不如 GPT-4/Claude |
| 硬件要求 | 需要 WebGPU 支持，旧设备可能无法运行 |
| 首次加载 | 模型下载较慢（数 GB），需等待 |
| 页面兼容 | 部分动态渲染页面可能提取失败 |
| 多语言 | 英文为主，中文能力有限 |
| 不执行操作 | 只能读取和总结，不能自动填表/点击/提交 |

---

## 参考资料

- [GitHub - nico-martin/gemma4-browser-extension](https://github.com/nico-martin/gemma4-browser-extension)
- [Gemma 4 E2B ONNX Model](https://huggingface.co/onnx-community/gemma-4-E2B-it-ONNX)
- [Chrome Built-in AI - Chrome for Developers](https://developer.chrome.com/docs/ai/built-in)
- [Gemini in Chrome - Google](https://www.google.com/chrome/ai-innovations/)
- [视频来源：Julian Goldie AI - YouTube](https://www.youtube.com/watch?v=5PRb-_39wko)

## 相关笔记

- [[Edge AI]]
- [[Transformers.js]]
- [[Gemma 4]]
