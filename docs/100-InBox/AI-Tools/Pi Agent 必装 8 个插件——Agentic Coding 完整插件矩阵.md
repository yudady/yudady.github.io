---
title: Pi Agent 必装 8 个插件——从轻量 Harness 到工业级 Agentic Coding
aliases: ["Pi Agent 插件", "pi-sandbox", "pi-herdr-agents"]
tags:
  - pi-agent
  - agentic-coding
  - coding-agent
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=QOK3KxlkPlM"
  - "https://pi.dev/packages"
author: 畅的科技工坊
created: 2026-09-09
updated: 2026-09-09
description: Pi Agent 八大核心插件全景：沙箱权限、MCP、多智能体编排、长期记忆、人机确认、联网检索、浏览器自动化与终端游戏，附完整实战工作流。
level: intermediate
stars: 4
---

# Pi Agent 必装 8 个插件——从轻量 Harness 到工业级 Agentic Coding

> Pi Agent 原生只有文件读、写、编辑、Bash 执行四个能力，胜在轻量省 Token 与出色的 Session 管理。本笔记整理「畅的科技工坊」推荐的 8 个核心插件，并用 pi.dev 官方包目录逐个交叉验证了真实包名、作者与版本。最后附一个个人财务 Web App 的多智能体实战工作流还原。

## 目录

- [为什么 Pi Agent 需要插件](#为什么-pi-agent-需要插件)
- [8 大插件全景](#8-大插件全景)
- [pi-sandbox：安全护栏](#pi-sandbox安全护栏)
- [MCP：协议扩展](#mcp协议扩展)
- [pi-herdr-agents：多智能体编排](#pi-herdr-agents多智能体编排)
- [pi-memory：跨 Session 记忆](#pi-memory跨-session-记忆)
- [ask-user-question：人机协同](#ask-user-question人机协同)
- [pi-web-access vs pi-agent-browser-native](#pi-web-access-vs-pi-agent-browser-native)
- [pi-arcade-games：摸鱼神器](#pi-arcade-games摸鱼神器)
- [综合实战：个人财务 Web App](#综合实战个人财务-web-app)
- [验证与勘误记录](#验证与勘误记录)
- [参考资料](#参考资料)

---

## 为什么 Pi Agent 需要插件

Pi Agent 的设计哲学是「极简核心 + 模块化装配」：

- 原生能力只有四个：文件读、文件写、编辑、Bash 命令执行
- 强项是 Session（对话）管理与上下文控制，轻量、省 Token、灵活
- 但真实 Agentic Coding 场景需要的能力——权限管控、外部工具接入、多智能体协作、长期记忆、人机确认、联网感知——全部交给插件生态

```
                 ┌─────────────────────────────────┐
                 │        Pi Agent 极简核心          │
                 │   读 / 写 / 编辑 / Bash 四能力    │
                 │   + Session 管理（原生强项）       │
                 └───────────────┬─────────────────┘
                                 │ 统一安装: pi install npm:<包名>
        ┌──────────┬─────────────┼──────────────┬─────────────┐
        ▼          ▼             ▼              ▼             ▼
     安全护栏    协议扩展      多智能体编排    长期记忆      人机协同
    pi-sandbox   MCP       pi-herdr-agents   pi-memory  ask-user-question
        ▲          ▲             ▲              ▲             ▲
        └──────────┴─────────────┴──────────────┴─────────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              ▼                  ▼                  ▼
          环境感知           环境感知            开发体验
      pi-web-access   pi-agent-browser-native  pi-arcade-games
      （API 检索）      （真浏览器自动化）        （等待时摸鱼）
```

插件在 pi.dev 网站的 Packages 页统一检索，分 Extensions / Packages / Skills 三类。日常使用不必深究概念差异，找到满足需求的包装上即可。

---

## 8 大插件全景

| 插件 | 解决的问题 | 验证包名（npm） | 版本 | 作者 |
|------|-----------|----------------|------|------|
| pi-sandbox | 权限控制与文件安全 | `pi-sandbox` | 0.6.6 | carderne |
| MCP | 外部工具/数据接入 | `pi-mcp-adapter` | 2.32.1 | nicopreme (nicobailon) |
| Pi-Herdr-Agents | 多 Agent 团队编排 | `pi-herdr-agents` | 1.6.0 | giuseppecrj |
| pi-memory | 跨 Session 记忆 | `pi-memory` | 0.4.2 | jayzeng |
| ask-user-question | Human-in-the-loop | `@juicesharp/rpiv-ask-user-question` | 2.9.0 | juicesharp |
| pi-web-access | 联网检索与信息获取 | `pi-web-access` | 0.28.0 | nicopreme |
| pi-agent-browser-native | 浏览器自动化与 Web 测试 | `pi-agent-browser-native` | 0.5.0 | fitchmultz |
| pi-arcade-games | 等待时的终端游戏 | `pi-arcade-games` | 1.0.14 | imkingjh999 |

安装通式（所有插件一致）：

```bash
pi install npm:<插件名>
# 例：pi install npm:pi-sandbox
```

安装量参考（pi.dev，2026-09）：pi-web-access 约 401K/月、pi-mcp-adapter 约 61.4K/月、pi-subagents 生态约 36-47K/月——MCP 与联网检索是生态里最热门的两类需求。

---

## pi-sandbox：安全护栏

### 为什么第一个装

不装 sandbox 的 Pi Agent 完全没有权限限制，Agent 误判可能直接覆写、删除重要文件。视频作者明确建议：**装完 Pi Agent 第一时间装 pi-sandbox**。

### 机制

```
Agent 发起操作（如写 .env 文件）
        │
        ▼
┌─────────────────────────────┐
│ 查 ~/.pi/agent 下的 sandbox  │
│ 规则文件（动态累积）           │
└──────────────┬──────────────┘
       │                │
   规则允许          规则未覆盖/禁止
       │                │
       ▼                ▼
   直接执行      弹出确认框：允许（临时/永久）或阻止
                        │
                        ▼
        用户决策 → 写回规则文件（下次同类型操作不再询问）
```

规则文件是**动态配置**的：你与 Agent 交互过程中每一次同意或否决，都会沉淀为规则条目，覆盖：

- 文件读取、目录读取
- 用户目录访问
- Bash 命令执行
- 域名访问、URL 授权
- 特定敏感文件写入拦截（视频演示：Agent 尝试在本地目录创建 `.env`，因未授权写入权限，弹出确认框被用户拒绝）

### 底层实现（官方 README 补充）

- OS 级沙箱委托给 `@carderne/sandbox-runtime`（fork 自 Anthropic 的 `anthropic-experimental/sandbox-runtime`）
- 前置依赖 ripgrep：macOS/Linux 沙箱初始化都检查 `rg`，缺失会报 `Sandbox initialization failed`，需 `brew install ripgrep`
- 代码源自 badlogic/pi-mono 的 sandbox 示例（Mario Zechner，MIT）
- 注意：为兼容 agent-browser 等工具开的默认规则会放宽安全边界，敏感环境需自查配置

---

## MCP：协议扩展

### 定位

MCP（Model Context Protocol）是 Pi 接入外部数据库、第三方服务、本机工具的标准协议。在 pi.dev 的 Packages 页按安装量排第一位的就是 MCP 适配器。

### 配置要点（与一般插件不同）

| 项目 | 位置 |
|------|------|
| MCP 配置文件 | `~/.config/mcp/mcp.json`（注意：**不在** `~/.pi` 下） |
| 一般插件配置 | `~/.pi/agent/` |

视频演示配置 Stitch MCP（Google Labs 的 UI 设计工具）：

1. 登录 Stitch 网页端，在设置里找到 MCP 配置信息（含 key）
2. 复制粘贴到 `mcp.json`（演示时 key 已打码）
3. 对 Pi 说「用 Stitch 设计一个单页 To-do Web App」——Agent 很快识别到已配置的 Stitch MCP 并自动调用

官方包目录交叉验证：Stitch MCP 真实存在，Google 官方 Codelabs 有 Design-to-Code with Stitch MCP 教程，社区已将其接入 Claude Code / Antigravity / Cursor 等。

---

## pi-herdr-agents：多智能体编排

### 定位

视频作者自称「本人最喜欢的插件」。名字容易让人以为它只做 Pi 与 Herdr 的集成，实际它提供：

- **Agent Team 编排**：装它之后不需要再装任何其他 subagent 插件
- **预定义子智能体团队**：Planner（规划）、Scout（代码侦测）、Worker（编程）、测试智能体等，开箱即用
- **Herdr 深度整合**：每个子智能体在 Herdr 独立 pane 中运行，实时展示运行状态；主智能体界面有方框汇总各子 Agent 进度，且**主界面不被 block**，可继续操作

### Herdr 是什么

Herdr 是「AI 编程智能体的运行时工作区」——一个终端工作区管理器（terminal workspace manager），多 pane/tab 承载多个 agent CLI（pi / claude / codex / opencode），跟踪每个 pane 的智能体状态（idle / working / blocked），通过本地 JSON-over-socket API 暴露给插件。

### 使用

```
/subagent <预定义角色> <任务>      # 生成并派发子智能体
   例：/subagent Scout Read the code（梳理当前目录代码并总结功能）

/plan <任务描述>                  # 整个 workflow 的入口（综合实战用）
```

子智能体在侧边栏可切换查看详情；主 Agent 界面持续显示子 Agent 状态。

### 子智能体定义与优先级

| 优先级 | 定义位置 | 说明 |
|--------|---------|------|
| 高 | `<项目目录>/.pi/` 下同名定义 | 项目级覆盖，不同项目可定制不同团队/workflow |
| 低 | `~/.pi/agent/npm/node_modules/pi-herdr-agents/agents/` | 安装包内置的预定义团队 |

两处定义文件都可直接修改或新建智能体，加入自己的工作流调用。

### ⚠️ 生态辨析（易混淆，见勘误表）

pi.dev 上有三个相关但不同的包：

| 包 | 作者 | 特点 |
|----|------|------|
| `pi-herdr-agents` | giuseppecrj | 异步 subagents 跑在 Herdr pane，可选 Git worktree 隔离；**视频用的这个** |
| `@andrewjacop/pi-herdr` | AndrewJacop | 把 pi 变成 fleet 编排器，可 spawn pi/claude/codex/opencode 异构智能体 |
| `pi-subagents` | nicopreme | 子智能体**进程内**运行（快、共享上下文），不依赖 Herdr |

---

## pi-memory：跨 Session 记忆

### 解决的问题

Pi 原生对话管控强，但 Session 之间信息不同步。pi-memory 把所有对话信息总结存档，**新对话初始化时自动注入历史上下文**——你的工具偏好、历史决策会被自动延续，大幅降低重复沟通成本。

### 存储结构（`~/.pi/agent/memory/`）

```
~/.pi/agent/memory/
├── memory/      # 长期记忆：你让 Agent 记住的信息，自动分类存放
├── daily/       # 每日工作记忆：以日期命名的文件，沟通记录分类归档
└── recovery/    # forget 命令删除的记忆暂存于此，可恢复
```

| 能力 | 说明 |
|------|------|
| 自动注入 | 初始化/调用对话时自动带上历史总结 |
| memory search | 安装 QMD 语义数据库后，可对历史对话做语义搜索 |
| forget / 恢复 | `memory forget` 删除的记忆进 recovery 目录，支持恢复 |

官方包描述确认：qmd-powered semantic search across daily logs, long-term memory, and scratchpad——与视频演示的三类结构吻合。

---

## ask-user-question：人机协同

### 解决的问题

Agent 自主编码时容易擅自假设架构。这个插件给 Agent 一个 `ask_user_question` 工具：遇到模糊需求或关键技术选型时，主动向用户发结构化问卷而不是瞎猜。作者的用后感：**所有沟通插件里最好用的一个**。

### 交互形态

```
Agent 需要确认 → 弹出问卷（可含多题）
   ├─ 单选题（选项列表）
   ├─ 多选题（方格勾选）
   ├─ 左右箭头在题目间跳转、修改
   └─ Submit 前随时可改 → 提交后 Agent 按答案执行
```

视频演示：只说「帮我生成一个文本文件」什么都没指定，Agent 立刻通过插件发来两题问卷——文件内容是什么？文件名叫什么？各给了候选项，选择后提交执行。

官方 README 一句话总结设计哲学：宁可花 15 秒选题，不要花 1 小时撤销错误假设。

---

## pi-web-access vs pi-agent-browser-native

两个插件都「访问网络」，功能定位完全不同：

| 维度 | pi-web-access | pi-agent-browser-native |
|------|--------------|------------------------|
| 访问方式 | API/URL 抓取，不开浏览器 | 打开真实浏览器（后台） |
| 输出 | 处理过的汇总信息（也可设为 raw） | 页面交互、截图、控制台 |
| 核心场景 | 调研、资料搜集、信息提取 | **Web App 功能验证**、截图、动态渲染测试 |
| 信息渠道 | OpenAI API、Gemini API、Brave 等第三方搜索 API | agent-browser CLI 的原生 `agent_browser` 工具 |
| 附加能力 | 配合 FFmpeg / yt-dlp 可分析网络视频 | 持久化 profile、认证态 Web App 操作 |

选择决策树：

```
需要获取网络信息？
├─ 只要内容/数据/总结 ──────────→ pi-web-access
│    （fetch sina.com 演示：自动补协议、汇总归类）
└─ 需要验证页面行为/截图/测 Web App → pi-agent-browser-native
     （脚本语法错误只有真浏览器执行才能暴露）
```

pi-agent-browser-native 安装后需 `/reload`，把命令写进 Agent 的使用流程说明（如 AGENTS.md），Agent 即可自主调用。

---

## pi-arcade-games：摸鱼神器

等待子智能体跑长任务（综合实战约 30 分钟）时，`/game` 打开游戏菜单：

| 项目 | 详情 |
|------|------|
| 游戏数 | 17 款（2048、Snake、Tetris、Wordle、Minesweeper、Sudoku 等） |
| 语言 | 中英双语菜单 |
| 存档 | 自动保存进度（`~/.pi-arcade/`），退出再进接着玩 |
| 操作 | `/game` 打开 → 数字键选游戏 → ESC 退出换游戏 |
| 独立运行 | 不开 Pi 也能玩：`npx pi-arcade-games snake` |

---

## 综合实战：个人财务 Web App

任务：已有记账 Web App（每日消费记录 + 报告两功能），要求新增「个人理财」分页。全程约 30 分钟。

### 完整工作流还原

```
/herdr 启动工作区
   │
   ▼
/plan 给个人记账 Web App 增加个人理财分页        ← pi-herdr-agents 工作流入口
   │
   ▼
[Planner] 分析需求 → 抛出问题（理财页展示哪些信息？）
   │        用户选两项继续
   ▼
[Scout] 侦测已有代码结构与功能                    ← 子 Agent 在独立 pane 运行
   │        主界面实时显示进度，不 block
   ▼
[Planner] 基于侦察结果产出计划 → 问卷确认          ← ask-user-question
   │
   ▼
[Worker1] 服务器代码
   │   ├─ 第 1 轮: Gemini
   │   └─ 第 2 轮: DeepSeek V4 Flash             ← Planner 定义中允许灵活选模型
   │      （等待期间 /game 玩贪吃蛇）              ← pi-arcade-games
   ▼
[Worker2] HTML 内容（新 pane 弹出）
   │        主 Agent 确认 Task2 完成
   ▼
[Worker3] 渲染 + 功能验证（Task3+4 合并）
   │        Worker pane 内调 Agent Browser 检查网页脚本语法错误
   ▼
[Master] Agent Browser 访问页面确认功能正确       ← pi-agent-browser-native
   │
   ▼
[Docs Agent] 撰写 README → Master 汇报，交付
```

### 插件协作矩阵

| 环节 | 参与插件 |
|------|---------|
| 需求确认 | pi-herdr-agents（/plan）+ ask-user-question（两次问卷） |
| 代码侦察 | pi-herdr-agents（Scout 子智能体 + Herdr pane 展示） |
| 编码 | pi-herdr-agents（Worker 分工：Server/HTML/渲染）+ 灵活模型路由 |
| 验证 | pi-agent-browser-native（Worker 自检语法 + Master 验收功能） |
| 全程记忆 | pi-memory（跨 Session 保留项目偏好与决策） |
| 安全兜底 | pi-sandbox（文件写入授权） |
| 等待调剂 | pi-arcade-games（/game 贪吃蛇） |

### 结果

最终交付新增 tab：可输入数值、存储账号信息、记录收入，功能按最初规划完成。作者评价：Pi-Herdr 对 workflow 前后管控与子智能体协作协调非常有效。

---

## 验证与勘误记录

视频声称经 pi.dev 官方包目录 + 官方 README 逐个交叉验证（2026-09-09）：

| 视频声称 | 验证结果 | 说明 |
|---------|---------|------|
| pi-sandbox 存在，权限规则动态配置 | ✅ | npm v0.6.6，carderne；基于 @carderne/sandbox-runtime（fork 自 Anthropic sandbox-runtime） |
| MCP 插件在 Packages 页排第一 | ✅ | pi-mcp-adapter v2.32.1，61.4K 安装/月，列表首位 |
| Pi-Herdr-Agents 提供多 Agent 团队 | ✅ | npm v1.6.0，giuseppecrj；预定义 Scout 等角色与 README 吻合 |
| ⚠️ 生态同名包易混淆 | ⚠️ | `pi-herdr-agents`（视频所用）≠ `@andrewjacop/pi-herdr`（异构 fleet 编排）≠ `pi-subagents`（进程内 subagent）。安装时认准 giuseppecrj 的包 |
| pi-memory 三类存储 memory/daily/recovery | ✅ | npm v0.4.2，jayzeng；README 描述 daily logs / long-term memory / scratchpad 吻合 |
| ask-user-question 单选多选问卷 | ✅ | 精确包名 `@juicesharp/rpiv-ask-user-question` v2.9.0，juicesharp；ask_user_question 工具，最多四题的终端问卷 |
| pi-web-access 支持 OpenAI/Gemini/Brave | ✅ | v0.28.0；官方列 OpenAI, Brave, Parallel, TinyFish, Search1API, Tavily, Firecrawl, Jina 等（401K 安装/月，生态最热门） |
| pi-agent-browser-native 可测 Web App | ✅ | v0.5.0，fitchmultz；原生 agent_browser 工具驱动真实浏览器 |
| pi-arcade-games 十七款游戏 | ✅ | v1.0.14，imkingjh999；README 明确 17 games + 中英双语 + 自动存档 |
| 编码用了 DeepSeek V4 Flash | ✅ | 型号真实存在：DeepSeek-V4-Flash（284B MoE / 13B 激活，1M 上下文），HF deepseek-ai/DeepSeek-V4-Flash |
| Stitch MCP 可配置 | ✅ | Google Labs Stitch 官方支持 MCP，有官方 Codelabs 教程 |
| sandbox 规则文件在 ~/.pi/agent 下 | ⚠️ | 视频实操路径可信；官方 README 另提供 sandbox.json 示例配置，具体文件名以实际安装为准 |
| MCP 配置在 ~/.config/mcp/mcp.json | ⚠️ | 视频实操演示路径，未见官方文档独立佐证，以实际安装为准 |

---

## 参考资料

- [视频：Pi Agent 必装 8 个插件分享（畅的科技工坊）](https://www.youtube.com/watch?v=QOK3KxlkPlM)
- [Pi 官方包目录 pi.dev/packages](https://pi.dev/packages)
- [pi-sandbox 包页](https://pi.dev/packages/pi-sandbox)
- [pi-herdr-agents 包页](https://pi.dev/packages/pi-herdr-agents)
- [pi-memory 包页](https://pi.dev/packages/pi-memory)
- [@juicesharp/rpiv-ask-user-question 包页](https://pi.dev/packages/@juicesharp/rpiv-ask-user-question)
- [pi-web-access 包页](https://pi.dev/packages/pi-web-access)
- [pi-agent-browser-native 包页](https://pi.dev/packages/pi-agent-browser-native)
- [pi-arcade-games 包页](https://pi.dev/packages/pi-arcade-games)
- [Herdr 官网](https://herdr.dev/)
- [Google Stitch MCP Codelabs](https://codelabs.developers.google.com/design-to-code-with-antigravity-stitch)

## 相关笔记

- [[Pi Agent 使用教程（同频道前作）]]
- [[Herdr 详细教程（同频道）]]

---

*笔记生成：2026-09-09，基于视频完整字幕（Playwright Tier 1 提取）+ pi.dev 官方包目录交叉验证*
