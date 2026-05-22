---
title: Hermes Agent + Claude + Codex — AI Agent 全栈集成指南
aliases:
  - Hermes MCP Codex 集成
  - Claude 第二大脑 OMI Obsidian
  - Computer Use CUA Driver
tags:
  - ai-agent
  - hermes-agent
  - claude
  - codex
  - mcp
  - computer-use
  - obsidian
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=lhpbJO0S4Ls"
author: Julian Goldie (Julian Goldie SEO)
created: 2026-05-16
updated: 2026-05-16
description: |
  Julian Goldie 直播合集：Hermes Agent 与 Codex/Claude 的集成方法、Claude 作为第二大脑的 OMI+Obsidian 架构、以及本地模型配合 Computer Use 的实践。
level: beginner
stars: 3
---

# Hermes Agent + Claude + Codex — AI Agent 全栈集成指南

> Julian Goldie 直播内容合集，覆盖 Hermes Agent 与 Codex 的两种集成方式、Claude 作为第二大脑的完整工作流、以及本地模型运行 Computer Use。适合刚接触 AI Agent 协同工作的开发者。

## 目录

- [[#Hermes Agent + Codex 集成]]
- [[#AI Agent 四层架构]]
- [[#Claude 作为第二大脑]]
- [[#本地模型 + Computer Use]]
- [[#实用技巧速查]]

---

## Hermes Agent + Codex 集成

将 Hermes Agent（自主执行 Agent）与 OpenAI Codex（编码 Agent）结合，实现代码编写 + 自动化部署的全流程。

### 方式一：Terminal 内嵌运行（简单）

直接在 Codex 的终端面板中运行 Hermes Agent：

```
┌─────────────────────────────────────┐
│           Codex IDE                  │
│  ┌──────────────┐ ┌───────────────┐  │
│  │  代码编辑区   │ │  Terminal     │  │
│  │              │ │  $ hermes     │  │
│  │  Codex 编写  │ │  Hermes 执行  │  │
│  │  前端页面    │ │  部署/任务    │  │
│  └──────────────┘ └───────────────┘  │
└─────────────────────────────────────┘
```

**操作步骤：**
1. 在 Codex 中打开项目
2. 点击 Terminal 面板切换终端
3. 直接运行 `hermes` 命令
4. Hermes 获得项目目录的完整访问权限

**适用场景：** Codex 写页面 → Hermes 部署到 Netlify 等外部服务

### 方式二：MCP Server 集成（进阶）

将 Hermes Agent 注册为 Codex 的 MCP Server，让 Codex 直接调用 Hermes 的全部工具：

```
┌─────────────────────────────────────┐
│              Codex                   │
│         ┌───────────┐               │
│         │ Codex LLM │               │
│         └─────┬─────┘               │
│               │ MCP Protocol        │
│         ┌─────▼─────┐               │
│         │ Hermes    │               │
│         │ MCP Server│               │
│         └─────┬─────┘               │
│               │                     │
│    ┌──────────┼──────────┐          │
│    ▼          ▼          ▼          │
│ Terminal    Web         File I/O    │
│ Commands    Requests    Operations  │
└─────────────────────────────────────┘
```

**操作步骤：**
1. 在 Codex 中新建项目
2. 将 Hermes MCP 文档（GitHub + 官方文档）粘贴到 Codex 上下文
3. 指示 Codex 将 Hermes 注册为本地 MCP Server
4. Codex 自动验证配置并连接
5. 重启 Codex 后生效

### 两种方式对比

| 维度 | Terminal 内嵌 | MCP Server |
|------|-------------|------------|
| 设置难度 | 极低（一行命令） | 中等（需配置文档） |
| 功能范围 | Hermes 独立执行 | Codex 直接调用 Hermes 工具 |
| 协作模式 | 串行（各干各的） | 深度集成（工具共享） |
| API 切换 | 通过 Hermes 配置切换 | 可混用多个 API |
| 适用场景 | 快速部署、后台任务 | 深度自动化、并行 Agent |

### Claude Desktop 同理

同样的 MCP 集成方式适用于 Claude Desktop：Claude 作为"大脑"决策，Hermes 作为"执行者"自主行动。

```
Claude Desktop (决策/规划)
        │ MCP
        ▼
Hermes Agent (自主执行)
```

---

## AI Agent 四层架构

Julian 提出的 AI Agent 全栈模型：

```
┌──────────────────────────────────┐
│  Layer 4: Output (自动化产出)     │
│  SEO / 内容 / 部署 / 通知         │
├──────────────────────────────────┤
│  Layer 3: Builder (代码构建)      │
│  Codex / Claude Code              │
├──────────────────────────────────┤
│  Layer 2: Hands (工具连接)        │
│  MCP (Model Context Protocol)     │
├──────────────────────────────────┤
│  Layer 1: Brain (智能决策)        │
│  Hermes Agent / Claude / GPT      │
└──────────────────────────────────┘
```

### 各层职责

| 层级 | 名称 | 作用 | 代表工具 |
|------|------|------|---------|
| 1 | Brain | 接收指令、规划任务、协调工具 | Hermes Agent, Claude |
| 2 | Hands | AI 与外部工具的通信桥梁 | MCP Protocol |
| 3 | Builder | 读写代码、构建项目 | Codex, Claude Code |
| 4 | Output | 最终自动化产出 | Netlify 部署、SEO 内容等 |

### Hermes Agent vs ChatGPT

| 特性 | Hermes Agent | ChatGPT |
|------|-------------|---------|
| 自主性 | 可自主发送消息、读写文件、部署网站、定时执行 | 仅回答问题 |
| 后台运行 | 24/7 运行，无需保持窗口打开 | 需要人工触发 |
| 工具集成 | 通过 MCP 连接任意外部工具 | 插件生态受限 |
| 适用场景 | 自动化工作流、定时任务、多步操作 | 问答、写作、分析 |

### 判断决策树

```
需要自动化执行任务？
├─ YES → 需要 24/7 后台运行？
│         ├─ YES → Hermes Agent
│         └─ NO → Codex / Claude Code
└─ NO → 只需要问答/分析？
          └─ YES → ChatGPT / Claude Desktop
```

---

## Claude 作为第二大脑

用 OMI + Obsidian + Claude 构建个人知识管理系统。

### 架构概览

```
┌─────────────────────────────────────────┐
│              数据采集层                   │
│  OMI (屏幕录制 + 对话记录 + 自动笔记)     │
└─────────────────┬───────────────────────┘
                  │ 导出笔记
                  ▼
┌─────────────────────────────────────────┐
│              知识存储层                   │
│  Obsidian Vault (Markdown 文件)          │
│  ┌──────┬──────┬──────┬──────┐          │
│  │Daily │Project│Areas │Archive│         │
│  └──────┴──────┴──────┴──────┘          │
└─────────────────┬───────────────────────┘
                  │ 读取 + 整理
                  ▼
┌─────────────────────────────────────────┐
│              AI 整理层                    │
│  Claude Desktop (打开 Obsidian 目录)     │
│  - 重组 Vault 结构                       │
│  - 创建 Maps of Content                 │
│  - 生成导航索引                          │
└─────────────────┬───────────────────────┘
                  │ 上下文
                  ▼
┌─────────────────────────────────────────┐
│              AI 应用层                    │
│  Hermes Agent / Claude Code / Codex     │
│  读取 Vault 获取用户上下文               │
└─────────────────────────────────────────┘
```

### PAR 方法（Obsidian 组织结构）

Claude 将 Obsidian Vault 重组为 PARA 结构（此处为 PAR）：

| 目录 | 用途 | 示例 |
|------|------|------|
| Daily | 每日记录 | 收件箱、今日待办、记忆 |
| Projects | 活跃项目 | 当前正在推进的事项 |
| Areas | 持续关注领域 | 健身、学习、工作方向 |
| Resources | 参考资料 | 文档、模板、工具清单 |
| Archive | 归档 | 已完成的项目和旧笔记 |

### 操作步骤

**Step 1：设置 OMI**
- OMI 免费使用（不使用内置聊天功能）
- 全天候记录屏幕、对话、任务
- 自动生成记忆和待办

**Step 2：导出到 Obsidian**
- 将 OMI 记忆导出为 Markdown 文件
- 存入 Obsidian Vault

**Step 3：Claude 重组 Vault**
- 在 Claude Desktop 中打开 Obsidian 文件夹
- 指令示例：「基于我的 Obsidian Vault，将其重组为结构化的第二大脑」
- Claude 自动创建 PARA 目录、Maps of Content、导航索引

**Step 4：Agent 读取上下文**
- Hermes Agent 等工具可读取 Vault 中的 `MEMORY.md`
- 获得用户偏好、项目状态等上下文
- 产出更精准的自动化结果

### 连接方式

| 方式 | 操作 | 优缺点 |
|------|------|--------|
| 直接打开文件夹 | Claude Desktop → 打开 Obsidian 目录 | 最简单，但无双向同步 |
| MCP Obsidian 插件 | 安装 MCP Obsidian 连接 Claude | 双向读写，需要配置 |
| Hermes Agent 读取 | 配置 workdir 指向 Vault | Hermes 可自动引用 |

### 最佳实践

- ✅ 让 OMI 持续采集，而非手动记录
- ✅ 定期让 Claude 重组 Vault（防止混乱堆积）
- ✅ 在 Vault 中维护 `MEMORY.md` 供 Agent 读取
- ✅ 使用 PARA 结构保持长期可维护性
- ❌ 不要用 Claude 本身作为唯一的笔记存储（它不会主动记录你的日常）
- ❌ 不要忽略 Vault 的组织，否则 Graph View 混乱不可用

---

## 本地模型 + Computer Use

在不依赖云 API 的情况下运行 AI Agent 并控制桌面。

### 工具链

| 组件 | 作用 | 说明 |
|------|------|------|
| Ollama | 本地 LLM 运行时 | 下载并运行本地模型 |
| Claude Code / Codex | 编码 Agent | 支持切换底层 LLM |
| CUA Driver | Computer Use 技能 | macOS 后台桌面控制 |

### 设置流程

```
1. 安装 Ollama
       │
       ▼
2. 下载本地模型
   例: ollama pull qwen3.6
       │
       ▼
3. 启动 Claude Code / Codex（指定本地模型）
   例: ANTHROPIC_BASE_URL=http://localhost:11434 claude
       │
       ▼
4. 配置 CUA Driver
       │
       ▼
5. 使用 Computer Use 指令
```

### 代码示例

```bash
# 1. 安装 Ollama（macOS）
brew install ollama

# 2. 下载模型
ollama pull qwen3.6

# 3. 用本地模型启动 Claude Code
# （需配合 Ollama 的 OpenAI 兼容 API）
ANTHROPIC_BASE_URL=http://localhost:11434/v1 claude

# 4. 使用 CUA Driver 控制 macOS
# 在 Claude Code 中发送：
# "Use CUA driver skill to open Notes application"
```

### Computer Use 的实际效果

- ✅ 能打开 macOS 原生应用（Notes、Safari 等）
- ✅ 能在后台运行，不抢占用户光标
- ✅ 能写入文本、导航界面
- ❌ 操作不够流畅，速度较慢
- ❌ 复杂交互容易出错
- 💡 适合简单重复任务，不适合精细操作

### 硬件要求

| 场景 | 推荐 | 可用 |
|------|------|------|
| 高性能本地模型 | DGX Spark / 高配工作站 | Mac Studio / Mac Mini（仅小模型） |
| 轻量本地模型 | Gemma 4 / Qwen 3.6 | 任意 Mac（性能受限） |
| 云 API 替代 | Step 3.5 Flash（免费） | OpenRouter / Nova Portal |

---

## 实用技巧速查

### Token 限额绕过

Codex 达到 Token 限额时，在 Codex 终端内直接运行 Hermes Agent 继续工作，绕过 Codex 的限额限制。

### 免费 API 替代

Hermes 3.6+ 不再免费时，切换到 **Step 3.5 Flash**（通过 Nova Portal）作为免费替代：

```
Hermes Agent 设置 → Nova Portal → 选择 Step 3.5 Flash → 重启
```

### 磁盘清理

在 Claude Desktop 中直接要求分析磁盘：
```
"分析我的整个磁盘，找出最佳清理位置，从占用空间最大的开始"
```

如果分析不够深入，追问：「深入查找占用空间最大的文件」。

---

## 参考资料

- [Julian Goldie SEO - YouTube](https://www.youtube.com/@JulianGoldieSEO)
- [Hermes Agent 文档](https://hermes-agent.nousresearch.com/docs)
- [MCP Obsidian 插件](https://github.com/smithery-ai/mcp-obsidian)
- [Ollama](https://ollama.com)
- [OMI](https://github.com/omi-me/omi)

## 相关笔记

- [[Hermes Agent 配置与使用]]
- [[MCP 协议详解]]
- [[Obsidian 知识管理]]
