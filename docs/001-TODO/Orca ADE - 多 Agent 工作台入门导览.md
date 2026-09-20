---
title: Orca ADE - 多 Agent 工作台入门导览
aliases:
  - Orca ADE 入门导览
  - 大力Thinking Orca 体验
  - Herdr vs Orca
tags:
  - ai-agent
  - ai-tools
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=U6ZHJPNvlxU"
  - "https://github.com/stablyai/orca"
  - "https://www.onorca.dev/"
  - "https://github.com/herdrdev/herdr"
author: 大力Thinking
created: 2026-09-20
updated: 2026-09-20
description: |
  大力Thinking 频道的 Orca 入门导览笔记：ADE（Agent Development Environment）概念、Git Worktree 任务隔离、Design Mode 元素抓取、Diff 批量评审与手机配对。与《Orca ADE - 开源多 Agent 开发环境实测》互补，本篇偏概念框架与上手导览。
level: beginner
stars: 3
---

# Orca ADE - 多 Agent 工作台入门导览

> 基于「介绍 Herdr 后，评论区为什么都在推荐 Orca？」（大力Thinking，2026-09-14，8:38）整理。Up 主此前分享过终端 agent 复用器 Herdr，评论区大量推荐 Orca，于是装来体验。本篇是概念入门 + 工作流导览；深度实测（安装向导、四模型并行、SSH 远端、会话持久）见 [[Orca ADE - 开源多 Agent 开发环境实测]]。

## 目录

- [[#Orca 是什么：从 IDE 到 ADE]]
- [[#核心机制：Git Worktree 任务隔离]]
- [[#多工界面与 Agent 状态管理]]
- [[#Design Mode：前端精确交互]]
- [[#Diff 与 Review：协同闭环]]
- [[#周边协同：悬浮窗与手机 App]]
- [[#验证与勘误记录]]
- [[#与既有实测笔记的互补]]
- [[#全文总结与行动建议]]
- [[#参考资料]]
- [[#相关笔记]]

---

## Orca 是什么：从 IDE 到 ADE

### 核心概念

Orca 把自己定义为 **ADE（Agent Development Environment，Agent 开发环境）**——不是又一个 IDE 插件，也不是新的模型或新的 Agent。它是一个执行环境：你惯用的 Codex、Claude Code、Cursor、Gemini CLI、OpenCode 等任何终端 Agent 都可以放进来统一运行。

```
传统 IDE：为人 + 键盘设计          ADE：为人 + 一队 Agent 设计
┌────────────────┐                ┌──────────────────────┐
│ 编辑器 + 插件    │                │ 终端（Agent 驻留）     │
│ AI 对话框(附属)  │                │ 浏览器预览 + 元素抓取  │
│ 外部浏览器       │    →           │ Git Worktree + Diff   │
│ 外部 git GUI    │                │ 状态灯 + 手机监控      │
└────────────────┘                └──────────────────────┘
   四散的工具                          一个工作台收口
```

### 仓库画像（2026-09-20 交叉验证）

| 维度 | 数据 |
|------|------|
| 仓库 | `github.com/stablyai/orca`（org：stablyai，YC-backed） |
| Stars | 72.7k（GitHub API 快照，2026-09-20；项目 daily ships，数字会漂） |
| 协议 | MIT（完全开源） |
| 平台 | macOS / Windows / Linux + iOS / Android companion |
| 支持 Agent | 27+（官网列表：Claude Code、Codex、Grok、Gemini、Cursor、OpenCode、Hermes Agent、Qwen Code…） |
| 官网 | onorca.dev（注意不是 orca.dev，同名软件很多） |

### 三类工具定位对比

| 维度 | 传统 IDE | Herdr（终端复用器） | Orca（ADE） |
|------|----------|--------------------|-------------| 
| 形态 | 图形编辑器 | Rust TUI，跑在现有终端里 | 桌面 GUI 工作台 |
| 多 Agent | 逐个开窗口 | 多 pane 复用，状态常驻 | 多 Worktree + 多 Agent 统一调度 |
| 代码审查 | 内置 diff | 无（终端为主） | 可视化 diff + 行内 Note 批量回发 |
| 浏览器 | 外部跳转 | 无 | 内置 Chromium + 元素抓取 |
| 适合 | 单人深度编辑 | 纯终端流重度用户 | 一人调度多 Agent 并行开发 |

> Herdr 官方仓库为 `github.com/herdrdev/herdr`——拼写就是 Herdr（无 e），Rust + Ratatui 写的终端 agent 运行时。

---

## 核心机制：Git Worktree 任务隔离

### 解决什么痛点

Up 主的点破很精准：同时开三个 Agent，最大的问题**不是窗口太多**，而是它们在同一份代码上相互干扰——一个 Agent 改完，另一个又把文件覆盖了，最后连「哪行是哪个 Agent 改的、是 Agent 改的还是我自己改的」都无法溯源。

### Worktree = 独立房间

每开一个新任务，Orca 就建一个独立的 **Git Worktree** 副本：有自己的文件、自己的分支、自己的 Agent 会话（session）。Agent 在各自「房间」里干活互不干扰，最后由开发者决定把哪个成果合并回主分支。

```
Project: myproject (main)
   │
   ├── worktree: test  ──→ Codex     ──→ 独立分支 + 文件 + session
   ├── worktree: test2 ──→ Claude Code ─→ 独立分支 + 文件 + session
   └── worktree: ...   ──→ 任意终端/Agent
   │
   └── 开发者人工评审 → 选择性合并回 main
```

底层就是 Git 原生命令，Orca 把它做成了 UI 一键流：

```bash
# Orca 里点「+」新建任务 ≈ 原生命令：
git worktree add ../myproject-test -b test
# 每个 worktree = 独立目录 + 独立分支 + 独立 agent session
```

### 两种组织模式对比

| 维度 | 同一目录多 Agent | 多 Worktree 各配 Agent |
|------|------------------|------------------------|
| 隔离级别 | 共享工作区，有竞写风险 | 目录 + 分支 + 会话三重隔离 |
| 溯源 | 难（相互覆盖） | 清晰（每任务一份副本） |
| 适用 | 一个 Agent 干活 + 一个终端查资料 | 多任务并行、多模型对比 |
| 合并 | 无需合并 | 开发者评审后合回主线 |

### 最佳实践

- ✅ 新任务默认走新 Worktree，命名带用途（`fix-login-race`），别依赖自动生成的名字
- ✅ 合并前先过 Diff 评审（见下文 Review 闭环），再决定要不要
- ❌ 不要让两个 Agent 挤同一个 Worktree 改同一批文件——回到互相覆盖的老问题

---

## 多工界面与 Agent 状态管理

### 界面布局

```
┌─ Project ──────────────────────────────────────┐
│ [worktree: test/Codex] [test2/Claude] [终端] [+]│
│ ┌──────────────┬───────────────────────────┐   │
│ │ Codex TUI    │ Claude Code TUI            │   │
│ │ ●运行中       │ ○等待输入                   │   │
│ │              │ ✅已完成（test2 后台）       │   │
│ └──────────────┴───────────────────────────┘   │
│  拖曳到边缘即可并排；切到别的页签仍可见状态灯    │
└────────────────────────────────────────────────┘
```

- **多终端/多页签**：同一项目下可开多个终端或 Agent 视窗，拖到边缘实现左右并排
- **状态指示灯**：每个 Agent 的工作状态（运行中 / 等待输入 / 已完成）实时可见——切到其他页签时也能一眼掌握背景 Agent 在干什么
- **不只是启动器**：Orca 在「管理」这些 Agent 的状态，而不只是把它们开出来

---

## Design Mode：前端精确交互

### 元素抓取闭环

内置浏览器（Chromium）直接打开开发中的网页，配合 **Grab Page Element** 把「口述改 UI」变成「点选改 UI」：

```
内置浏览器打开页面
   │
   ▼ 点击「抓取页面元素」按钮
   │
   选中目标元素（如某个按钮）
   │
   ▼ 自动复制并贴入 Agent 对话框
   │
   ┌────────────────────────────────┐
   │ 元素信息包：HTML 标签 + CSS 样式 │
   │ + 尺寸规格 + 选择器             │
   └────────────────────────────────┘
   │
   ▼ 直接描述「把这个改成什么样」→ Agent 精确修改
```

抓取产物结构示意（口播转述，字段以实际为准）：

```html
<!-- 粘贴进对话框的元素描述，包含： -->
<tag class="..." style="...">   <!-- HTML + 内联样式 -->
  <!-- 附：元素尺寸（width/height）与 CSS selector -->
</tag>
```

### 沟通方式对比

| 方式 | 示例 | 结果 |
|------|------|------|
| 模糊自然语言 | 「请改右上角那个蓝色按钮」 | Agent 猜元素，来回返工 |
| Design Mode 抓取 | 点选元素 → HTML/CSS/尺寸/选择器自动入对话 | 像素级、样式级精确修改 |

> Up 主注：类似能力在 Codex 等 Agent 软件里也有；Orca 的差异点是**终端命令行 Agent 与可视化体验合一**——既要 CLI Agent 的高效，又要 GUI 的精确反馈时，它是个不错的组合方案。

---

## Diff 与 Review：协同闭环

Agent 做完 ≠ 代码能合并。Orca 内置了完整的评审链路：

```
Agent 完成改动
   │
   ▼ 展开右侧目录树 → 点开代码/文档直接查看
   │
   ▼ 点 Git 图标 → 内置 Git 可视化界面
   │   （哪些文件修改/新增/删除，一目了然）
   │
   ▼ 疑问行直接点「+」添加评论（Note）
   │   例：note 一条、换个文件再加一条 test
   │
   ▼ 批量发送 → 选「仅本文件」或「所有未发送」
   │
   ▼ Agent 收到全部意见 → 统一重构修正
```

| 能力 | 传统做法 | Orca 内置 |
|------|----------|-----------|
| 变更总览 | `git status` + 外部 diff 工具 | 目录树 + 可视化 diff |
| 行内评论 | 复制代码行贴到对话里描述 | 点行加 Note，上下文自动带 |
| 批量反馈 | 一条条发，打断 Agent | 打包一次发送，Agent 统一处理 |

> 官网对照表原文即含「Inline diff comments sent back to the agent」——评审意见回流 Agent 是产品的一等公民功能。

---

## 周边协同：悬浮窗与手机 App

### 全局悬浮窗

右下角常驻悬浮按钮，点开是一个**始终置顶**的小视窗：工作时不消失，可开临时 Codex 问问题、或开浏览器查资料，不打断主编辑区。

### 手机端配对（Orca App）

| 模式 | 条件 | 用法 |
|------|------|------|
| 局域网配对 | 手机与电脑同一局域网 | 扫 QR Code 直接配对（Up 主实测调通） |
| 远程同步 | 跨网络 | 注册账号走云端（Up 主未实测，仅口播介绍） |

配对后可在移动端随时查看 Agent 开发进程——人离开桌面，Agent 还在干活。

---

## 验证与勘误记录

视频字幕（NotebookLM 转写）与 Content Insights 同源，ASR 噪声较多。以下为对照官方来源的逐项验证（2026-09-20）：

| 视频声称（口播/Insights） | 验证结果 | 权威来源 |
|---------------------------|----------|----------|
| 工具名「歐卡/OK/O卡」 | ❌ 官方名 **Orca**（虎鲸；up 主自述一直不知怎么读） | GitHub / 官网 |
| 「工作數/Wate/Wtrade」 | ❌ **Git Worktree** | 官网 "Every task gets its own git worktree" |
| 之前介绍的工具「Herh」 | ❌ **Herdr**（`herdrdev/herdr`，官方拼写无 e） | GitHub 搜索 |
| 「mCRI 项目」 | ❓ up 主的开源项目名，转写不可靠，未核实 | — |
| 「codex, cloudcode, cursorgeminateopencode」 | ✅ 连读拆开 = Codex、Claude Code、Cursor、Gemini CLI、OpenCode，官网 agent 列表全部在列 | 官网 |
| Orca 是 ADE，非模型非 Agent | ✅ | 官网 "The agent development environment" |
| 每任务独立 worktree + 分支 + 会话 | ✅ | 官网 "its own git worktree, its own agent terminal" |
| 内置浏览器 + 元素抓取（HTML/CSS/尺寸/选择器） | ✅ | 官网 "Embedded Chromium with design mode" |
| 行内 Note 批量发送给 Agent | ✅ | 官网 "Inline diff comments sent back to the agent" |
| 手机 App 扫码配对（局域网/远程账号） | ✅ | 官网 companion app（App Store + Android APK） |
| MIT 开源、成熟度高 | ✅ | GitHub license: MIT，72.7k stars（2026-09-20 快照） |
| 「类似功能 codex 等软件里都有」 | ⚠️ 观点部分属实；Orca 差异点在终端 Agent + 可视化合一 | 口播观点，未逐一核实 |

> 读音勘误连锁提示：[[Orca ADE - 开源多 Agent 开发环境实测]] 的勘误表曾把视频口播的「Herdr」判为「实为 Herder」——经 GitHub 核实官方拼写就是 **Herdr**（org: `herdrdev`），那条勘误属于过度纠正，以本表为准。

---

## 与既有实测笔记的互补

同主题两份笔记，来自两个频道、两个视频，角度互补勿合并：

| 维度 | 本篇（大力Thinking 导览） | [[Orca ADE - 开源多 Agent 开发环境实测]]（Tonbi 实测） |
|------|---------------------------|--------------------------------------------------------|
| 定位 | 概念入门：为什么需要 ADE、核心工作流长什么样 | 深度实测：装完真跑一遍的完整体验 |
| 独有内容 | Herdr→Orca 的推荐脉络、Worktree 痛点表述、Review 批量发送细节 | 安装向导、四模型 Shootout、SSH 远端 Worktree、会话持久化、Design Mode 快捷键（C/S）与 Annotations |
| 适合 | 第一次听说 Orca / 判断要不要上手 | 已决定上手，要看真实生产力表现 |

---

## 全文总结与行动建议

### 一句话定位

Orca 不是给传统 IDE 塞一个 AI 对话框，而是回答一个更根本的问题：**当 Agent 数量变多，开发环境该怎么重新设计**。答案的核心是 Git Worktree——每个任务一间独立房间，人做调度和评审。

同时开多个 AI、多个终端的工具现在非常多（Herdr 就是其一），这不是 Orca 的护城河；Worktree 级的任务管理 + 评审闭环才是。

### 判断决策树

```
选 Orca 如果你：
  ✅ 已在同时使用 Codex / Claude Code 等多个 Agent
  ✅ 有并行处理的多个开发任务（前后端齐头推进）
  ✅ 前端项目想用「点选元素」替代口述改 UI
  ✅ 想要可视化 Diff + 行内批量评审

暂缓如果你：
  ⚠️ 平时只开一个 Agent 做小范围修改（显得重）
  ⚠️ 纯终端流重度用户（Herdr / tmux 更轻）
```

### 行动建议

1. 优先在**有前端展示或重构需求的 Git 项目**里试用
2. 用「元素抓取」+「行内批量 Review」两个功能验证协同提效
3. 手机端先走局域网扫码（远程模式口碑未验证）
4. 深度功能（SSH 远端、四模型并行、会话持久）直接看 [[Orca ADE - 开源多 Agent 开发环境实测]]

---

## 参考资料

- [介绍 Herdr 后，评论区为什么都在推荐 Orca？（大力Thinking, 2026-09-14）](https://www.youtube.com/watch?v=U6ZHJPNvlxU)
- [stablyai/orca — GitHub 仓库](https://github.com/stablyai/orca)
- [Orca 官网（onorca.dev）](https://www.onorca.dev/)
- [herdrdev/herdr — Herdr GitHub 仓库](https://github.com/herdrdev/herdr)

## 相关笔记

- [[Orca ADE - 开源多 Agent 开发环境实测]]
- [[Claude Code Hooks - 确定性自动化防线]]

---

*文档生成时间：2026-09-20；基于 Orca 2026-09 时点版本（GitHub 72.7k stars 快照），项目迭代极快（daily ships），数据有保鲜期*
