---
title: Hermes HUD UI — AI Agent 网页监控面板
aliases: [Hermes HUD, hermes-hudui]
tags: [ai-agent, status/active, area/distill, type/doc, topic-hermes, topic-monitoring]
source: ["https://www.youtube.com/watch?v=78YQMfgPlwA", "https://github.com/joeynyc/hermes-hudui"]
author: YAHA学堂
created: 2026-04-18 10:07
updated: 2026-04-18 10:07
description: |
  Hermes HUD UI 是 Hermes Agent 的浏览器端监控面板，13 个 Tab 实时展示花费、记忆、技能、对话等全部状态。
level: beginner
stars: 3
---

# Hermes HUD UI — AI Agent 网页监控面板

> Hermes Agent（GitHub 57,000+ 星）是最火的开源 AI Agent 之一，但大部分人跑着 Hermes 却不知道它内部在干什么——token 烧了多少、记住了什么、学了哪些 skill、cron 有没有在转。Hermes HUD UI 就是为解决这个问题而生的网页版监控面板。

---

## 概览

Hermes HUD UI 是一个基于浏览器的"意识监控器"（consciousness monitor），让用户可视化查看和管理 Hermes Agent 的全部内部状态。

| 项目 | 信息 |
|------|------|
| 项目地址 | https://github.com/joeynyc/hermes-hudui |
| 技术栈 | Python 3.11+ / Node.js 18+ / WebSocket |
| 平台 | macOS / Linux / WSL |
| 协议 | MIT |
| 数据来源 | 直接读取 `~/.hermes/` 目录 |
| 实时更新 | 通过 WebSocket，无需手动刷新 |

---

## 13 个 Tab 功能一览

按视频中展示顺序分为 5 大类：

```
┌─────────────────────────────────────────────────────────────────┐
│  1/5  Dashboard         一眼看完 AI 全局状态                     │
│  ─────────────────────────────────────────────────────────────  │
│  2/5  Memory             AI 的记忆（持久化存储）                  │
│       Sessions           历史对话记录                             │
│  ─────────────────────────────────────────────────────────────  │
│  3/5  Costs              Token 花费追踪（按模型）                 │
│       Health             健康状态                                 │
│       Patterns           使用习惯统计                              │
│  ─────────────────────────────────────────────────────────────  │
│  4/5  Skills             已学习的技能列表                          │
│       Cron               定时任务管理                              │
│       Projects           项目信息                                 │
│       Agents             子 Agent 状态                            │
│       Profiles           Agent 角色配置                           │
│       Corrections        纠正记录                                 │
│  ─────────────────────────────────────────────────────────────  │
│  5/5  Chat               直接在网页上跟 AI 对话                    │
└─────────────────────────────────────────────────────────────────┘
```

### 1. Dashboard（总览）

全局仪表盘，一眼掌握 Agent 的运行状态——在线状态、最近活动、关键指标汇总。

### 2. Memory & Sessions（记忆与对话）

- **Memory Tab**：查看和编辑 Agent 的持久化记忆（Memory / User Profile），可直接在网页上修改
- **Sessions Tab**：浏览历史对话记录，回溯 Agent 的过往交互

### 3. Costs / Health / Patterns（花费与健康）

- **Costs**：按模型追踪 Token 消耗和费用，这是 Web UI 相比 TUI 版本的新增功能
- **Health**：Agent 健康状态监控
- **Patterns**：使用习惯统计分析

### 4. 管理功能群

- **Skills**：查看和管理 Agent 已学习的技能
- **Cron**：查看和管理定时任务（排程）
- **Projects**：项目信息展示
- **Agents**：子 Agent 运行状态
- **Profiles**：Agent 角色配置切换
- **Corrections**：查看被纠正的记录

### 5. Chat（网页对话）

直接在浏览器中与 Hermes Agent 对话，无需切换到终端。

---

## 四套主题

快捷键 `t` 切换，支持 CRT 扫描线效果：

| 主题 | 风格 | 色调 |
|------|------|------|
| Neural Awakening | 赛博朋克 | 青色（Cyan） |
| Blade Runner | 银翼杀手 | 琥珀色（Amber） |
| fsociety | 黑客军团 | 绿色（Green） |
| Anime | 动漫风格 | 紫色（Purple） |

---

## 安装

前置要求：Python 3.11+、Node.js 18+、已运行的 Hermes Agent（`~/.hermes/` 目录存在）

```bash
git clone https://github.com/joeynyc/hermes-hudui.git
cd hermes-hudui
./install.sh
hermes-hudui
```

打开 http://localhost:3001

后续启动：

```bash
source venv/bin/activate && hermes-hudui
```

如需同时安装 TUI 版本：

```bash
pip install 'hermes-hudui[tui]'
```

> 注意：zsh 下必须用引号包裹 `'hermes-hudui[tui]'`，否则 `[tui]` 会被解释为 glob pattern。

---

## 键盘快捷键

| 快捷键 | 功能 |
|--------|------|
| `1`-`9`, `0` | 切换 Tab（0 = 第 10 个） |
| `t` | 打开主题选择器 |
| `Ctrl+K` | 命令面板（Command Palette） |

---

## 与 TUI 版本的关系

Web UI 是 hermes-hud（终端 TUI 版本）的浏览器伴侣。两者独立读取同一个 `~/.hermes/` 数据目录，可以同时运行。

Web UI 独有功能：
- 独立的 Memory、Skills、Sessions Tab
- 按模型追踪 Token 花费
- 命令面板（Ctrl+K）
- 实时网页对话
- 主题切换器

---

## 多语言支持

默认英文，支持中文。点击 Header 最右侧的语言切换按钮即可。切换后 Chat 的 AI 回复也会自动用中文。

---

## 影片时间轴

| 时间 | 内容 |
|------|------|
| 00:00 | 你的 AI 在背后烧了多少钱你知道吗 |
| 00:16 | 1/5 Dashboard：你的 AI 一眼看完 |
| 02:08 | 2/5 Memory 跟 Sessions：AI 的脑袋跟对话纪录 |
| 03:42 | 3/5 Costs、Health、Patterns：花费、健康、使用习惯 |
| 05:25 | 4/5 Skills、Cron、Projects、Agents、Profiles、Corrections |
| 07:42 | 5/5 Chat：直接在网页上跟 AI 对话 |
| 08:24 | Bonus：四套主题 |
| 08:37 | 安装教学：3 分钟跑起来 |

---

## 参考资料

- [Hermes HUD UI - GitHub](https://github.com/joeynyc/hermes-hudui)
- [Hermes Agent - GitHub](https://github.com/NousResearch/hermes-agent)
- [Hermes Agent 安装教学（影片）](https://youtu.be/NaDftIn1YLk)
- [影片：8萬顆星的Hermes Agent少了它根本不能用](https://www.youtube.com/watch?v=78YQMfgPlwA)

## 相关笔记

- [[../../200-Distill/200-AI-Tools/Hermes Agent - 自改进AI代理框架]]
