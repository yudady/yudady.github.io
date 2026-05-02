---
title: LLM Knowledge Base - 为 Claude Code 构建自进化记忆系统
aliases: [Claude Memory Compiler, Karpathy LLM KB, Claude Code Memory]
tags: [ai-agent, status/active, area/distill, type/doc, topic-claude-code, topic-knowledge-base, topic-obsidian, karpathy]
source: ["https://www.youtube.com/watch?v=7huCP6RkcY4"]
author: Cole Medin
created: 2026-04-07 20:40
updated: 2026-04-07 20:40
description: |
  基于 Karpathy 的 LLM Knowledge Base 架构，用 Claude Code Hooks 为编码代理构建基于内部对话数据的自进化记忆系统。
level: intermediate
stars: 4
---

# LLM Knowledge Base - 为 Claude Code 构建自进化记忆系统

> Karpathy 提出「用 LLM 管理个人知识库」的概念大火后，Cole Medin 基于相同架构但换了数据源——不收集外部文章，而是自动捕获 Claude Code 的会话日志，编译成结构化知识库，让编码代理随代码库进化而越来越聪明。

---

## Karpathy 的编译器类比

Karpathy 用软件编译器的概念来解释知识库架构，这是理解整个系统的核心心智模型：

```
┌─────────────────────────────────────────────────────┐
│               知识库编译器类比                         │
│                                                      │
│  源代码              编译器              可执行文件     │
│  ┌──────┐           ┌──────┐           ┌──────┐      │
│  │ 原始  │ ────────▶ │ LLM  │ ────────▶ │ Wiki │      │
│  │ 数据  │           │ 处理  │           │ 查询  │      │
│  └──────┘           └──────┘           └──────┘      │
│                                                      │
│  articles/        总结/链接/        structured         │
│  papers/          结构化知识        concepts +         │
│  transcripts                       connections         │
│                                                      │
│       测试套件 = Linting（健康检查）                    │
│       运行时 = Agent 查询知识库                        │
└─────────────────────────────────────────────────────┘
```

### 编译器各阶段详解

| 编译器阶段 | 知识库对应 | 说明 |
|-----------|-----------|------|
| Source Code | Raw Folder | 未处理的原始 markdown（文章/论文/转录） |
| Compiler | LLM 处理 | 总结、链接文档、结构化知识 |
| Executable | Wiki | 编译后的知识，带 back links |
| Test Suite | Linting | 检查空白、过期数据、断链 |
| Runtime | Agent 查询 | 搜索 wiki 获取答案 |

### 关键洞察：不需要 RAG

Karpathy 原话：「我本来以为需要搞复杂的 RAG，但 LLM 在自动维护索引文件方面做得很好。」

核心依赖 **index 文件**：一个描述所有文件夹和资源的索引，agent 从这里出发导航，不需要向量数据库或语义搜索。

---

## 数据流架构

### Karpathy 原版（外部数据）

```
外部信息（网页/论文）
      │
      ▼ Obsidian Web Clipper
  ┌──────────┐
  │ Raw 文件夹 │ ← 原始 markdown
  └─────┬────┘
        │ LLM 处理（compile 脚本）
        ▼
  ┌──────────┐
  │   Wiki   │ ← concepts/connections/index
  └─────┬────┘
        │ Agent 查询
        ▼
     回答问题
```

### Cole Medin 版（内部数据）— 核心区别

```
Claude Code 会话
      │
      ▼ Hooks 自动捕获
  ┌──────────┐
  │ Daily Log │ ← 对话摘要（等价于 Raw）
  └─────┬────┘
        │ Flush（每日）+ Claude Agent SDK
        ▼
  ┌──────────┐
  │ Knowledge │ ← 概念/连接/索引（等价于 Wiki）
  └─────┬────┘
        │ Agent 查询
        ▼
     回答问题
```

### 两个版本的对比

| 维度 | Karpathy（外部） | Cole Medin（内部） |
|------|-----------------|-------------------|
| 数据源 | 网页文章、论文 | Claude Code 会话日志 |
| 输入方式 | Obsidian Web Clipper 手动收集 | Hooks 自动捕获 |
| 处理方式 | compile 脚本 + LLM | Claude Agent SDK |
| 编译触发 | 手动运行脚本 | 每日 flush 自动 |
| 适用场景 | 通用知识研究 | 代码项目记忆 |
| 核心价值 | 组织外部知识 | 保留决策和经验教训 |

---

## Claude Code Hooks 机制（核心实现）

整个系统完全由 Claude Code Hooks 驱动，不需要额外安装任何东西。

### 三个 Hook

```
┌───────────────────────────────────────────────────────┐
│                  Claude Code Hooks                     │
│                                                        │
│  ① Session Start Hook                                 │
│     ├─ 加载 agents.mmd（系统描述 → 元推理）             │
│     └─ 加载 index.md（知识索引 → 高效查询起点）          │
│                                                        │
│  ② Pre-Compact Hook                                   │
│     └─ 上下文即将压缩时 → Claude Agent SDK 生成摘要     │
│                                                        │
│  ③ Session End Hook                                   │
│     └─ 会话关闭时 → Claude Agent SDK 生成摘要           │
│                                                        │
│  摘要写入 ──▶ Daily Log（标准化格式）                   │
│  每日 Flush ──▶ Daily Log → Knowledge Wiki             │
└───────────────────────────────────────────────────────┘
```

### agents.mmd 的作用

这是全局规则文件，告诉代理整个系统的元信息：

- 信息从哪里来（Daily Log）
- 编译后的知识在哪（Knowledge）
- 索引和日志文件在哪
- 系统如何工作

让代理具备「元推理」能力——知道自己身处什么系统。

---

## 目录结构

```
project/
├── settings.json          # Claude Code hooks 定义
├── agents.mmd             # 全局规则（系统元描述）
├── daily-logs/            # 对话摘要（Raw 等价物）
│   ├── 2026-04-06.md
│   └── 2026-04-07.md
├── knowledge/             # 编译后知识（Wiki 等价物）
│   ├── index.md           # 索引（最重要文件）
│   ├── concepts/          # 提取的概念
│   └── connections/       # 概念间关系
└── scripts/
    ├── compile.py         # 编译脚本
    └── flush.py           # 日志 → 知识库
```

---

## 知识复利循环（Compounding Loop）

这是整个系统最有价值的部分——知识会自我增长：

```
┌──────────────────────────────────────────────┐
│            知识复利循环                        │
│                                              │
│     提问 ──▶ Agent 搜索 Wiki 多篇文章          │
│                    │                          │
│                    ▼                          │
│          综合信息 → 生成回答                   │
│                    │                          │
│                    ▼                          │
│        会话结束 → 摘要写入 Daily Log           │
│                    │                          │
│                    ▼（每日 Flush）             │
│          新概念/连接 → 写入 Wiki               │
│                    │                          │
│                    ▼                          │
│         Wiki 更大 → 下次查询更准确              │
│                    │                          │
│              回到「提问」↑                     │
└──────────────────────────────────────────────┘
```

关键点：**无需人工维护**。每次会话结束或上下文压缩时自动提取经验教训，每日自动编译到 wiki。

---

## 与其他方案的对比

| 方案 | 复杂度 | 数据源 | 自定义 | 维护成本 |
|------|--------|--------|--------|---------|
| Claude Code 内置记忆 | 低 | .claude/ 文件 | 有限 | 零 |
| RAG + 向量数据库 | 高 | 外部文档 | 完全 | 中 |
| **Claude Memory Compiler** | **中** | **会话日志** | **完全** | **几乎零** |
| Zep / Mem0 等开源方案 | 中 | 混合 | 中 | 低 |

Cole Medin 的方案优势在于：
- 完全可自定义（改 compile/flush 脚本的 prompt 即可）
- 代理自己能帮你改（因为它有 agents.mmd，知道系统怎么运作）
- 不需要向量数据库，架构极简
- 与 Obsidian 的 graph view 天然契合

---

## 快速上手

### Karpathy 版（外部知识）

发送他的 [Gist PRD](https://gist.github.com/karpathy/442a6bf555914893e9891c11519de94f) 到编码代理，一键生成整个系统。

### Cole Medin 版（内部记忆）

```bash
# GitHub 仓库
https://github.com/coleam00/claude-memory-compiler

# 1. 在现有代码库中发送 README 的 quick start prompt 到 Claude Code
# 2. Claude Code 自动克隆 + 配置 hooks
# 3. 推荐用 Obsidian 打开项目目录作为 vault（可视化 wiki）
```

### Obsidian 配置

- 用 Obsidian 打开项目文件夹作为 vault
- 推荐主题：Obsidianite + 暗色模式
- Graph View 可视化知识网络

---

## 与我们现有系统的关联

我们当前 Hermes 系统（dtg-ob）的 llm-wiki 技能本质上就是 Karpathy 原版（外部数据）的实现。Cole Medin 的内部数据思路可以补充到我们的工作流：

| 我们已有的 | 可以借鉴的 |
|-----------|-----------|
| llm-wiki（外部 → wiki 编译） | Hooks 自动捕获会话日志 |
| ob-query（wiki 查询） | agents.mmd 元推理能力 |
| wiki-full-ingest（全量编译） | 每日 flush + linting 健康检查 |

---

## 参考资料

- [Claude Memory Compiler (GitHub)](https://github.com/coleam00/claude-memory-compiler)
- [Karpathy 原始 Tweet](https://x.com/karpathy/thread/2039805659525644595)
- [Karpathy LLM KB PRD (Gist)](https://gist.github.com/karpathy/442a6bf555914893e9891c11519de94f)
- [视频源](https://www.youtube.com/watch?v=7huCP6RkcY4)

## 相关笔记

- [[../../200-Distill/200-AI-Tools/Hermes Agent - 自改进AI代理框架]]
- [[Claude Code Hooks 使用指南]]（待创建）
