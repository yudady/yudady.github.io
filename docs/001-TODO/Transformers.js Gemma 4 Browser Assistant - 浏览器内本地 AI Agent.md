---
title: Transformers.js Gemma 4 Browser Assistant — 浏览器内本地 AI Agent
aliases:
  - Gemma 4 Browser Extension
  - WebMCP
  - 浏览器本地 AI Agent
tags:
  - transformers.js
  - gemma-4
  - webgpu
  - chrome-extension
  - local-ai
  - browser-agent
  - on-device
  - status/active
  - type/note
source:
  - "https://www.youtube.com/watch?v=8P3enx5Z490"
  - "https://huggingface.co/blog/transformersjs-chrome-extension"
  - "https://github.com/nico-martin/gemma4-browser-extension"
author: AI Stack Engineer (视频作者) / Nico Martin (开发者, Hugging Face ML 工程师)
created: 2026-05-04
updated: 2026-05-04
description: 深度拆解 Transformers.js Gemma 4 Browser Assistant Chrome 扩展的架构，包括双模型设计、WebMCP 工具调用层、Manifest V3 三层架构和语义搜索历史功能
level: intermediate
stars: 4
---

# Transformers.js Gemma 4 Browser Assistant — 浏览器内本地 AI Agent

> 一个完全本地运行的浏览器 AI Agent Chrome 扩展。模型下载到浏览器后驻留在 IndexedDB，所有推理在本地 GPU（WebGPU）上完成，无需 API Key、无需云端服务器、支持离线使用。由 Hugging Face ML 工程师 Nico Martin 开发，Google Gemma 官方账号背书。

---

## 目录

- [为什么选择本地浏览器 Agent](#为什么选择本地浏览器-agent)
- [双模型架构](#双模型架构)
- [Chrome 扩展三层架构（Manifest V3）](#chrome-扩展三层架构manifest-v3)
- [Agent 循环与 WebMCP 工具调用层](#agent-循环与-webmcp-工具调用层)
- [核心能力](#核心能力)
- [性能表现](#性能表现)
- [安装与开发](#安装与开发)
- [架构的扩展可能性](#架构的扩展可能性)
- [参考资料](#参考资料)

---

## 为什么选择本地浏览器 Agent

```
传统浏览器 AI 助手                    Gemma 4 Browser Assistant
┌──────────────────────┐            ┌──────────────────────────┐
│ 页面数据 ──→ 云服务器 │            │ 页面数据 ──→ 本地 GPU    │
│     │        │       │            │     │        │           │
│     │     大模型推理  │            │     │   Gemma 4 (3GB)    │
│     │        │       │            │     │        │           │
│     │     结果返回   │            │     │     结果返回        │
│     │                 │            │     │                     │
│ ❌ 隐私风险          │            │ ✅ 数据不离机            │
│ ❌ 需要付费          │            │ ✅ 完全免费              │
│ ❌ 依赖网络          │            │ ✅ 离线可用              │
└──────────────────────┘            └──────────────────────────┘
```

| 维度 | 云端方案 | 本地方案 |
|------|----------|----------|
| 隐私 | 浏览数据上传到服务器 | 数据永不离开机器 |
| 成本 | GPU 服务器费用 → 付费墙 | 零成本（用自己的 GPU） |
| 网络 | 必须联网 | 离线可用 |
| 适用范围 | 受限于服务商 TOS | 可用于内网、银行等敏感页面 |
| 模型能力 | 更强的云端模型 | 受限于浏览器可运行的模型大小 |

---

## 双模型架构

扩展使用**两个模型**，各有分工：

```
┌─────────────────────────────────────────────────┐
│                  Chrome Extension                 │
│                                                   │
│  ┌───────────────────────┐  ┌──────────────────┐ │
│  │   Gemma 4 E2B Instruct │  │ MiniLM-L6-v2     │ │
│  │   (推理大脑)            │  │ (嵌入模型)        │ │
│  │                       │  │                  │ │
│  │ • Agent 循环控制       │  │ • 文本 → 向量    │ │
│  │ • 工具调用决策         │  │ • 语义搜索        │ │
│  │ • 响应生成             │  │ • 历史检索        │ │
│  │                       │  │ • 页面内容检索    │ │
│  │ ~2B 有效参数          │  │ ~22M 参数        │ │
│  │ 128K context window   │  │                  │ │
│  │ 35+ 语言              │  │                  │ │
│  │ Q4F16 量化            │  │ FP32             │ │
│  └───────────────────────┘  └──────────────────┘ │
│                                                   │
│  总下载大小：约 3GB（两个模型合计）                   │
│  缓存位置：IndexedDB (chrome-extension:// origin)   │
└─────────────────────────────────────────────────┘
```

### Gemma 4 E2B 关键特性

- **E = Effective parameters**（有效参数，非总参数量）
- 实际权重 > 2B，但推理时仅 ~2B 参数激活（per-layer embeddings 技术）
- 推理时保持小模型体积，同时保留大量知识
- 专为手机、笔记本、浏览器设计
- 原生 Function Calling 支持

### 量化策略

| 模型 | 量化方式 | 效果 |
|------|----------|------|
| Gemma 4 E2B | Q4F16（权重 4bit + 激活 FP16） | 工作站级模型塞进浏览器内存 |
| MiniLM-L6-v2 | FP32 | 小模型无需量化 |

---

## Chrome 扩展三层架构（Manifest V3）

```
┌─────────────────────────────────────────────────────────────┐
│                    Manifest V3 Architecture                   │
│                                                              │
│  ┌────────────────────────────────────────────────────────┐  │
│  │          Background Service Worker                      │  │
│  │          (控制平面 - 重量级操作)                          │  │
│  │                                                        │  │
│  │  • 模型加载与缓存                                      │  │
│  │  • Agent 生命周期管理                                   │  │
│  │  • 工具执行（WebMCP）                                  │  │
│  │  • 特征提取（Embedding pipeline）                       │  │
│  │  • 对话历史（Agent.chatMessages）                       │  │
│  │                                                        │  │
│  │  模型只加载一次，不随标签页增加                          │  │
│  └──────────┬──────────────────────────┬───────────────────┘  │
│             │ chrome.runtime.sendMessage │                      │
│             ▼                          ▼                       │
│  ┌──────────────────────┐  ┌──────────────────────────────┐  │
│  │   Side Panel (侧边栏) │  │  Content Script (内容脚本)    │  │
│  │   (交互层 - 轻量)     │  │  (页面桥接 - 轻量)            │  │
│  │                      │  │                              │  │
│  │  • 聊天输入/输出      │  │  • DOM 提取                  │  │
│  │  • 流式响应渲染       │  │  • 元素高亮                  │  │
│  │  • 模型下载进度       │  │  • 页面滚动                  │  │
│  │  • 设置控制           │  │                              │  │
│  │                      │  │  注入到每个 HTTP(S) 页面       │  │
│  └──────────────────────┘  └──────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### 为什么这样拆分

Chrome Manifest V3 有严格的运行时规则：
- **Background Service Worker** 可以执行重量级计算，但无法直接访问页面 DOM
- **Content Script** 可以访问页面 DOM，但生命周期短、不适合加载大模型
- **Side Panel** 是持久化 UI，适合聊天交互

**核心原则**：Background 是唯一协调者，Side Panel 和 Content Script 是专职客户端。

### 消息传递契约

```
Side Panel ──→ Background              Background ──→ Content Script
──────────────────────────            ──────────────────────────────
CHECK_MODELS                        EXTRACT_PAGE_DATA
INITIALIZE_MODELS                   HIGHLIGHT_ELEMENTS
AGENT_GENERATE_TEXT                 CLEAR_HIGHLIGHTS
AGENT_GET_MESSAGES
AGENT_CLEAR
EXTRACT_FEATURES

Background ──→ Side Panel
──────────────────────────
DOWNLOAD_PROGRESS
MESSAGES_UPDATE
```

---

## Agent 循环与 WebMCP 工具调用层

### WebMCP — 工具调用规范化层

Nico 构建了一个叫 **WebMCP** 的工具调用层，将浏览器工具规范化为模型可理解的格式：

```
用户输入: "总结当前页面"
    │
    ▼
┌─────────────────────────────────────────────────┐
│              Agent Loop (runAgent)                │
│                                                   │
│  1. 用户消息 → chatMessages                       │
│  2. 创建占位 assistant 消息 → 流式输出 tokens      │
│  3. 解析输出 → extractToolCalls()                 │
│     │                                             │
│     ├── 有工具调用？                               │
│     │     │                                       │
│     │     ▼                                       │
│     │   ┌─────────────────────┐                   │
│     │   │   WebMCP 工具执行    │                   │
│     │   │                     │                   │
│     │   │ tool = {            │                   │
│     │   │   name,             │                   │
│     │   │   description,      │                   │
│     │   │   inputSchema,      │                   │
│     │   │   execute()         │                   │
│     │   │ }                   │                   │
│     │   └────────┬────────────┘                   │
│     │            │                                │
│     │            ▼                                │
│     │   执行结果 → 下一轮 prompt                   │
│     │   └──→ 回到步骤 2                            │
│     │                                             │
│     └── 无工具调用？→ 最终确定响应，附加 metrics     │
│                                                   │
└─────────────────────────────────────────────────┘
```

### 可用工具

| 工具名 | 功能 | 说明 |
|--------|------|------|
| `get_open_tabs` | 列出所有打开的标签页 | 返回标题和 URL |
| `go_to_tab` | 切换到指定标签页 | 支持语义匹配 |
| `open_url` | 打开新 URL | 前台/后台可选 |
| `close_tab` | 关闭标签页 | — |
| `find_history` | 语义搜索浏览历史 | 基于 IndexedDB 向量库 |
| `ask_website` | 读取并总结页面内容 | RAG：提取 → 嵌入 → 检索 → 生成 |
| `highlight_website_element` | 高亮页面元素 | 滚动到目标段落并高亮 |

### Gemma 4 的 Tool Calling 格式

```
模型输出格式（Gemma-4 风格 chat template）：

<|tool_call|>call:ask_website{query:<|"|>summarize this page in three sentences<|"|>}
```

模型决定调用工具时，输出一个特殊的 tool-call token block。`extractToolCalls` 解析器将其提取出来，执行实际函数，然后将结果喂回模型。

---

## 核心能力

### 1. 标签页管理

```
"切换到我的银行标签页"
    │
    ├── get_open_tabs → 列出所有标签页（标题+URL）
    └── go_to_tab → 找到匹配标签页并切换
```

替代手动翻找 20+ 个标签页。

### 2. 页面交互（RAG）

```
"找到这个产品页面上的定价部分"
    │
    ├── Content Script 提取 DOM（标题、段落、列表）
    ├── MiniLM 生成嵌入向量
    ├── 语义检索最相关内容块
    ├── Gemma 4 基于检索结果生成回答
    └── highlight_website_element → 滚动并高亮
```

不是关键词匹配，而是**理解语义**。

### 3. 浏览历史语义搜索（最亮眼功能）

```
传统历史搜索:  关键词匹配 URL/标题
语义历史搜索:  向量相似度匹配页面内容

"找到我上周看的那篇关于降低 API 成本的文章"
    │
    ├── 每个访问过的页面 → MiniLM → 向量
    │   存储: {title, description, url, embedding}
    ├── 用户查询 → MiniLM → 向量
    ├── 余弦相似度检索
    └── 返回最匹配的历史记录
```

即使记不住标题或网站，用自然语言描述内容即可找到。

---

## 性能表现

| 硬件环境 | Token 速度 | 体验 |
|----------|-----------|------|
| MacBook M3/M4 | 15-25 tok/s | 流畅可用 |
| Windows + 独立 GPU | 15-25 tok/s | 流畅可用 |
| 旧笔记本 / 集成显卡 | ~5 tok/s | 可用，较慢 |
| 无 WebGPU 支持 | 不支持 | 无法运行 |

**页面总结测试**：长博客文章 3 句总结， decent GPU 机器约 20 秒。

---

## 安装与开发

### 方式 1：Chrome Web Store（推荐普通用户）

1. Chrome Web Store 搜索 "transformers.js Gemma 4 browser assistant"
2. 安装（2 秒，扩展本身很小）
3. 点击图标打开侧边栏 → 首次下载模型（约 3GB）
4. 下载完成后，即使离线也可使用

### 方式 2：从源码构建（开发者）

```bash
git clone https://github.com/nico-martin/gemma4-browser-extension
cd gemma4-browser-extension
pnpm install
pnpm run dev
# Chrome → 扩展程序 → 加载已解压的扩展 → 选择 dist/ 目录
```

适用场景：修改 Agent 行为、添加自定义工具、研究架构。

---

## 架构的扩展可能性

Nico 的架构设计为多种变体留了空间：

```
当前实现                        可扩展方向
─────────                       ──────────
Side Panel 聊天        →       Popup 快捷助手
全局单一 Agent         →       Per-Tab 独立 Agent
预设工具集             →       自定义工具注册
单页面交互             →       Options 页面配置
```

| 变体 | 实现方式 | 适用场景 |
|------|----------|----------|
| Popup 助手 | `action.default_popup` | 快速问答 |
| Side Panel Copilot | 当前方案 | 持久对话 |
| Per-Tab Agent | Background 中按 `tabId` 隔离状态 | 每个标签页独立上下文 |
| Hybrid UI | Popup + Side Panel + Options 共享 Background | 全场景覆盖 |

**关键设计原则**：Background 拥有状态和推理，UI/Content 是专职客户端。

---

## 参考资料

- [GitHub: nico-martin/gemma4-browser-extension](https://github.com/nico-martin/gemma4-browser-extension)
- [Hugging Face Blog: How to Use Transformers.js in a Chrome Extension](https://huggingface.co/blog/transformersjs-chrome-extension)
- [Chrome Web Store: Transformers.js Gemma 4 Browser Assistant](https://chromewebstore.google.com/detail/transformersjs-gemma-4-br/dhaknnnkcdkjhcclchmnfdhddoehoool)
- [Gemma 4 E2B 模型页](https://huggingface.co/google/gemma-4-E2B)
- [Gemma 4 发布博客](https://huggingface.co/blog/gemma4)

## 相关笔记

- [[Codex goal 底层逻辑 - Ralph Loop 与长时间自主编码]] — 本地/云端 Agent 循环对比
