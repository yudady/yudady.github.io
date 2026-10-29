---
title: Herdr vs Orca 深度对比 - 极简运行时 vs 全功能 ADE
aliases:
  - Panda Making Money Herdr Orca 横评
  - Herdr Orca 26分钟深评
  - AI Agent 编排器哲学分野
tags:
  - ai-agent
  - ai-tools
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=6m9mDTmgP3s"
  - "https://github.com/herdrdev/herdr"
  - "https://github.com/stablyai/orca"
  - "https://herdr.dev/"
  - "https://www.onorca.dev/"
author: Panda Making Money（频道）
created: 2026-09-21
updated: 2026-09-21
description: |
  Panda Making Money 26 分钟横评的深度笔记：Herdr（Rust 终端运行时）与 Orca（YC 孵化的完整 ADE）从架构哲学到企业就绪度的全维度对比。2026-09-21 全量交叉验证发现重大时效点：Herdr 已于视频发布十天后从 AGPL 双授权改为 Apache 2.0，仓库迁移至 herdrdev org。与 Trevor 横评、津晶访谈、实测篇互补，本篇偏「验证时间线 + 风险结构分析」。
level: intermediate
stars: 3
---

# Herdr vs Orca 深度对比 - 极简运行时 vs 全功能 ADE

> Panda Making Money 频道（2026-07-12 发布，26:09，6021 次观看）的对决赛笔记。一句话：**两款工具不是竞争关系，而是同一个问题的两个极端答案**——Herdr 选择「做小」，嵌进你现有的终端只加一层 agent 状态感知；Orca 选择「做大」，围绕并行 agent 从零重构整个开发环境。本篇 2026-09-21 逐项验证时发现视频的关键事实有两条已过时（Herdr 许可证、仓库地址），时间线见下文。

## 目录

- [[#TL;DR]]
- [[#问题背景：Agent Chaos 与 tmux 的盲区]]
- [[#Herdr 深读：做小的哲学]]
- [[#Orca 深读：做大的哲学]]
- [[#七维对比矩阵]]
- [[#谁该选谁]]
- [[#两边都不完美：限制清单]]
- [[#验证时间线：视频发布后的三大变化]]
- [[#验证与勘误记录]]
- [[#与既有笔记的互补]]
- [[#参考资料]]
- [[#相关笔记]]

---

## TL;DR

| 你是谁 | 选 | 理由 |
|--------|-----|------|
| 终端重度用户，2-3 个 agent，不想换环境 | **Herdr** | 单 Rust 二进制嵌入现有终端，侧边栏看 working/idle/blocked，零迁移成本 |
| 5-10+ agent 并行、同题比稿、要视觉 review 和手机盯盘 | **Orca** | worktree fan-out + diff 行内注释 + mobile app，为规模化并行而生 |
| 企业采购（SSO/RBAC/审计日志硬需求） | **都不行** | 视频明确指出两者当前都没有企业功能 |

视频总结原话：**Herdr 是给「想要更聪明的终端」的人，Orca 是给「想要一种全新 agent 工作方式」的人**。两者不互斥——平时 Herdr 管日常，大任务切 Orca 开比稿。

---

## 问题背景：Agent Chaos 与 tmux 的盲区

视频开篇的场景：Claude Code 在一个窗口重构支付模块，Codex 在另一个窗口写测试，第三个 tab 的 agent 已经安静了 10 分钟——你分不清它是完成了、在等你授权、还是直接崩了。于是你开始 alt-tab 巡检每个窗口。

传统方案的盲区（02:53 章节）：

```
tmux/Screen 视角                    期望的视角
┌──────┬──────┬──────┐          ┌──────┬──────┬──────┐
│ text │ text │ text │          │Agent1│Agent2│Agent3│
│stream│stream│stream│          │ ▲工作中│ ■阻塞!│ ○空闲 │
│      │      │      │          │      │等你授权│已完成 │
└──────┴──────┴──────┘          └──────┴──────┴──────┘
  对 tmux 全是等价的文本流          需要「状态 + 紧急度」语义
```

- tmux 对「刚完成任务的 agent」和「卡在权限确认的 agent」一视同仁——都只是 pane 里的一串文本
- 没有 state、没有 urgency、不知道谁此刻需要你
- 检查成本随 agent 数量线性增长：5 个 agent = 5 个 pane 要巡检，10 个 = 你变成了全职监控员
- 这是 Claude Code / Codex / Copilot CLI 变成日常主力后的真实痛点，不是假想问题

---

## Herdr 深读：做小的哲学

### 定位：带 agent 感知的终端复用器

视频定义（04:54）：terminal multiplexer + agent awareness。名字本身是双关——像牧羊犬（herding）管羊群一样管你的 agent 群。

**核心姿态：不替换你现有的终端。** iTerm、WezTerm、Kitty、Alacritty 用户零迁移成本——字体、配色、shell 配置、多年肌肉记忆全部保留。（字幕把 WezTerm 听成 "West Term"，勘误见后表。）

### 架构与技术事实

```
┌─────────────────────────────────────┐
│ 你现有的终端 (iTerm/Ghostty/Kitty…)   │
│ ┌─────────────────────────────────┐ │
│ │ herdr (Rust + Ratatui TUI)       │ │
│ │ ┌─────┐┌─────┐┌─────┐           │ │
│ │ │pane1││pane2││pane3│  ← 各 agent│ │
│ │ └─────┘└─────┘└─────┘    独占pane│ │
│ │ [▲working ■blocked ○idle 侧边栏] │ │
│ └───────────────┬─────────────────┘ │
└─────────────────┼───────────────────┘
                  │ Unix Socket
          ┌───────┴────────┐
          │ herdr server   │ ← detach/关机后会话仍在
          │ + herdr CLI    │ ← agent 可程序化调度
          └────────────────┘
```

| 声称 | 验证结果 | 说明 |
|------|---------|------|
| Rust + Ratatui | ✅ | Cargo.toml 确认 `ratatui = "0.30"` |
| Client/Server + Unix socket | ✅ | 官方文档有 socket api 专页；README「background server」 |
| 自动检测 working/idle/blocked | ✅ | 官方 agents 页：前台进程检测 + screen manifest（屏幕输出分析）/ lifecycle hooks 两级状态权威 |
| 单二进制 ~10MB | ⚠️ 已过时 | 当前 v0.9.1（2026-09-16）：Linux 23-25MB、macOS 20.5-22MB、Windows zip 9.2MB |
| 零遥测、无账号 | ✅（间接） | 安装即用无登录步骤，官方文档无遥测配置项；但非官方明文声明 |
| Linux/macOS 完整支持，Windows beta | ✅ | README install 段链接 windows-beta 文档 |

### 最有意思的设计：herdr CLI（06:36）

socket 同时暴露 CLI——**外部进程（包括 agent 自己）可以程序化控制整个工作区**。视频举的高级用法：一个 lead agent 自己拉起 helper agents、监控进度、读输出、做反应，全程无人类敲键盘。这与官方 README 的「agent-native」条目一致：agent 可以 spawn panes、互相 prompt、等另一个 agent 真正 blocked。

### SSH 远程（07:48）

`herdr` 的 SSH 旗标把本地配置转发到远程机器——远程会话的手感与本地一致。当前 README 已进化为「several machines, one window」：本地 + 多台保存的 SSH 机器合到一个窗口，agent 列表合并、独立重连。

---

## Orca 深读：做大的哲学

### 定位：Agent Development Environment（ADE）

视频定义（09:36）：不是加在终端上的层，而是**从零构建的独立应用**，前提假设是「开发者不再独自工作，而是在指挥一整支 agent 舰队」。

### 团队背景（09:50-10:40）

| 人物 | 角色 | 背景（视频声称 → 验证） |
|------|------|----------------------|
| Jinjing Liang（梁津晶） | CEO | 前 Google，做过给数十亿用户发布 Chrome 的测试/发布基建 → ✅ YC 页 + [[Orca ADE 创始人访谈 - 一个人管400个AI任务]] 交叉印证 |
| Neil Parker | CTO | Uber 最年轻 tech lead 之一，safety 团队大规模 ML 项目 → ✅ YC 公司页原文 |

- 公司 Stably AI，2022 年创立，YC W2022 batch → ✅ YC 官网确认
- 团队规模：视频说 ~4 人 → ⚠️ 双口径（YC 公司页 2026-09 显示 25 employees；津晶本人在访谈中也说 4 人团队——可能官方页滞后或后期扩张，不判错）

### 核心工作流：Worktree 比稿（10:45）

```
        一个 prompt
            │ fan-out
   ┌────────┼────────┐
   ▼        ▼        ▼
┌──────┐ ┌──────┐ ┌──────┐
│wt-1  │ │wt-2  │ │wt-3  │  ← 各自独立的 git worktree
│Claude│ │Codex │ │Grok  │  ← 不同 agent 各写各的
└──┬───┘ └──┬───┘ └──┬───┘
   └────┬───┴────┬───┘
        ▼ 并排 diff 比稿
   merge 最优解
```

官方 README 原文坐实：「Fan one prompt across five agents, each in its own isolated git worktree — compare the results and merge the winner」。视频的表述「每次都在办一场小竞赛，选赢家」是准确的。

### 功能全景（11:17-13:50，全部 README 验证 ✅）

| 功能 | 内容 |
|------|------|
| 终端 | Ghostty-class WebGL 渲染、无限分屏、restart 后 scrollback 存活 |
| Computer Use | 真 Chromium 窗口内点击任意元素 → HTML + CSS + 裁剪截图直送 agent prompt |
| Diff 审查 | 行内评论直接回传 agent；review/edit/commit 不出环境 |
| 账号管理 | Claude/Codex 用量 + rate limit 重置显示，账号热切换免登出 |
| 任务集成 | PR / issue / 项目板（GitHub + Linear）应用内浏览，任意任务直开 worktree |
| 远程 | remote box 跑 agent，断线自动重连 + 端口转发 |
| **手机** | iOS App Store / Android APK，收完成通知、发后续指令——**全对比中独一份** |
| Agent 支持 | README 具名 29 个（Claude Code、Codex、Grok、Cursor、Copilot、OpenCode、Hermes Agent、Kimi、Qwen Code…）+ any-CLI 兜底 |

授权：MIT 开源 + BYOS（Bring Your Own Subscription）——接你自己的 Claude/Codex 订阅，不向 Stably 付模型费。安装：macOS（Apple Silicon/Intel）/ Windows / Linux AppImage，另有 Homebrew cask 与 Arch AUR。

---

## 七维对比矩阵

| 维度 | Herdr | Orca |
|------|-------|------|
| **类别** | 终端复用器 + agent 感知 | 完整 ADE，独立应用 |
| **哲学** | 嵌入现状，零侵入 | 推倒重来，agent 一等公民 |
| **Git 并行** | 纯状态监控，Git 工作流全留给你 | worktree 深度整合，单 prompt 多 agent 比稿合并 |
| **远程/移动** | SSH 旗标转发本地配置 | remote box + 重连/端口转发 + **手机 App** |
| **浏览器** | 无（纯终端工具） | Computer Use：点击元素进 prompt |
| **审查** | 无内置 diff/review | 行内 diff 注释回传 agent |
| **隐私/用量** | 零遥测、无账号、本地 | 内建账号切换 + 用量/限额追踪 |

**验证后需要修正的两行**（视频原话 vs 2026-09-21 现实）：

| 维度 | 视频说法（07-12） | 当前现实（09-21 验证） |
|------|------------------|----------------------|
| 授权 | Herdr AGPL 3.0 双授权 vs Orca MIT | **两者现在都是宽松协议**：Herdr 已于 07-22 改 Apache 2.0 单授权 |
| 背后团队 | 独立开发者 vs 4 人 YC 团队 | 成立，但 Herdr 已迁至 herdrdev org（仍个人主导）；Stably 在 YC 页显示 25 人 |

企业就绪度：**两边都没有** SSO / RBAC / 审计日志——视频这一点对两者的批评是公允的。Orca 的账号/用量功能体现略偏团队的设计取向。

---

## 谁该选谁

```
你怎么用 agent？
│
├─ 2-3 个并行，键盘流，爱现有终端配置
│   └─→ Herdr（零迁移成本）
│        ├─ 想在手机上盯进度？ ──否──→ 就它了
│        └─ 是 → 只有 Orca 有 mobile，妥协点重量
│
├─ 5-10+ 并行 / 同题比稿 / 视觉 review 依赖
│   └─→ Orca（worktree + diff 注释 + mobile 全套）
│        └─ 代价：更重的应用、更多界面要学、迭代快
│           （changelog 高频发版，界面可能版本间漂移）
│
└─ Windows 主力机
    └─→ Orca 更稳（Herdr Windows 仍 beta，官方自认体验较糙）
```

视频的最后提醒值得记录：**两个工具都不改变你的代码和 prompt 最终流向哪里**——agent 照样把源码发给 Anthropic/OpenAI 或你配置的任何 provider。选编排器改变的是「舰队可见性和可控性」，不改变底层数据暴露。这句话在营销横评视频里属于难得的诚实。

---

## 两边都不完美：限制清单

视频 21:04 章节，逐条记录：

**Herdr 的五个短板：**
1. 零内置 code review/diff 工具——告诉你谁需要关注，到了之后还是切回原编辑器看代码
2. 无 mobile app——离开桌面就失联
3. Windows 仍在 beta
4. 状态检测本质是 pattern matching（进程名 + 终端输出），对未适配的新 agent/冷门 agent 不保证无漏判
5. **单人维护的结构性风险**——无 SLA、无支持团队；维护者一忙/转向，没有团队补位。个人与小团队可接受，组织长期依赖要三思

**Orca 的三个代价：**
1. 重——独立应用的安装/更新/资源占用 vs 单个小二进制
2. 要你彻底走出现有终端，采纳它的环境（对多年精调命令行工作流的人是大 ask）
3. 发版节奏快，功能与界面可能版本间变动——喜欢稳定不变工具的人要留意

---

## 验证时间线：视频发布后的三大变化

本次验证（2026-09-21）最重要的发现是**视频发布（07-12）后 Herdr 侧发生了三个结构性变化**，GitHub 考古时间线：

```
2026-03-17  stablyai/orca 仓库创建（TypeScript）
2026-03-22  Herdr initial release（LICENSE 随初始提交）
2026-03-27  herdr 仓库创建（时名 ogulcancelik/herdr）
2026-05-26  commit: "docs: clarify dual licensing" ← AGPL+商业双授权期
2026-07-12  ★ 本视频发布 —— 此时「AGPL 3.0 双授权」说法属实
2026-07-22  commit: "chore: relicense herdr under apache-2.0" ★★
2026-07-25  herdrdev org 建立，仓库迁移 → github.com/herdrdev/herdr
            （旧链接 ogulcancelik/herdr 自动 301 重定向，未断链）
2026-09-16  Herdr v0.9.1（二进制 20.5-25MB，早已非 ~10MB）
2026-09-21  本笔记验证时点：Herdr 39,871★ / Orca 73,620★
```

**含义**：视频发布时点的内容是准确的；但任何在 2026-07-22 之后看到这篇笔记的人，按视频选型会拿到错误的法律条款认知——AGPL 顾虑（copyleft 传染、商业授权需求）已整体消失，Herdr 现在是 Apache 2.0，比 Orca 的 MIT 只多了专利授权条款，两者对企业使用都无障碍。这是「型号/条款存在性结论有保质期」的又一实证。

---

## 验证与勘误记录

| # | 视频声称 | 验证结果 | 来源 |
|---|---------|---------|------|
| 1 | Herdr 双授权：AGPL 3.0 开源 + 商业授权 | ⚠️ 已过时 | 视频 07-12 属实；07-22 commit relicense 为 Apache 2.0 单授权（LICENSE 文件 + commit 史实锤） |
| 2 | 描述链接 github.com/ogulcancelik/herdr | ⚠️ 已迁移 | 301 → herdrdev/herdr（org 建于 07-25）；GitHub 自动重定向不断链 |
| 3 | Herdr 单二进制 ~10MB | ⚠️ 已过时 | v0.9.1 实测 20.5-25MB（Windows zip 9.2MB）；视频口径可能是早期版本或约数 |
| 4 | Rust + Ratatui | ✅ | Cargo.toml `ratatui 0.30` |
| 5 | 检测 = 进程名匹配 + 输出分析 | ✅ | 官方：前台进程检测 + screen manifest / lifecycle hooks 两级状态权威 |
| 6 | 开箱识别 Claude Code、Codex、Copilot CLI、Devin CLI | ✅ | herdr.dev/docs/agents 全在列（官方具名 21+ agent） |
| 7 | 零遥测、无账号 | ✅（间接） | 安装无登录、文档无遥测配置；非官方明文声明 |
| 8 | Stably AI 2022 年创、Jinjing Liang + Neil Parker、YC W2022 | ✅ | ycombinator.com 公司页 |
| 9 | Jinjing：Google Chrome 测试/发布基建 | ✅ | YC 页 + 津晶访谈交叉印证 |
| 10 | Neil：Uber 最年轻 tech lead 之一、safety 团队 ML | ✅ | YC 公司页原文 |
| 11 | Stably 团队 ~4 人 | ⚠️ 双口径 | YC 页 2026-09 显示 25 employees；访谈中津晶亦称 4 人——官方页滞后或扩张，并存不判错 |
| 12 | Orca 支持 25+ agent | ✅ | README 具名 29 个 + any-CLI 兜底 |
| 13 | 字幕 "Kimmy and Quinn Code" | ❌ ASR 误听 | 实为 Kimi and Qwen Code（README 具名） |
| 14 | 字幕 "West Term" | ❌ ASR 误听 | 实为 WezTerm 终端 |
| 15 | worktree 比稿 / WebGL 终端 / Computer Use / mobile / SSH remote / 账号热切换 | ✅ | 全部 Orca README 原文坐实 |
| 16 | Orca MIT 开源 + BYOS | ✅ | GitHub API license: MIT；YC 页同 |
| 17 | 两边都无 SSO/RBAC/审计日志 | ❓ 未逐一验证 | 两者官方文档未见企业功能描述，倾向属实 |

---

## 与既有笔记的互补

本 vault 已有多篇 Herdr/Orca 笔记，本篇的独特角度是**「深评视频的验证时间线 + 风险结构分析」**：

- [[Herdr vs Orca - AI Agent 编排器选型对比]]——Learn With Trevor 3 分半速览版横评（2026-09-20），快速选型看它；本篇是其 26 分钟深评版 + 许可证变更考据
- [[Orca ADE 创始人访谈 - 一个人管400个AI任务]]——津晶第一手访谈：400+ 任务调度方法论、ADE vs Harness 边界、日更工程实践
- [[Orca ADE - 开源多 Agent 开发环境实测]]——上手实测篇
- [[Orca ADE - 多 Agent 工作台入门导览]]——功能导览篇
- [[Herdr 上手指南 - Agent 时代的终端工作区运行时]]——Herdr 单品教程
- [[Orca - 开源 AI Agent 编排开发环境]]——Orca 单品档案

横向串联：Trevor 横评（快选）→ 本篇（深评+验证）→ 津晶访谈（内部视角）→ 各单品笔记（实操）。

---

## 参考资料

- [视频：Herdr vs Orca: Which AI Agent Orchestrator Actually Wins?](https://www.youtube.com/watch?v=6m9mDTmgP3s)（Panda Making Money，2026-07-12）
- [herdrdev/herdr](https://github.com/herdrdev/herdr)（Apache 2.0，39,871★ @ 2026-09-21）
- [stablyai/orca](https://github.com/stablyai/orca)（MIT，73,620★ @ 2026-09-21）
- [Herdr 官方文档](https://herdr.dev/docs/)（agents 检测机制、socket API）
- [Orca 官网](https://www.onorca.dev/)
- [Stably AI 的 YC 公司页](https://www.ycombinator.com/companies/stably-ai)

## 相关笔记

- [[Herdr vs Orca - AI Agent 编排器选型对比]]
- [[Orca ADE 创始人访谈 - 一个人管400个AI任务]]
- [[Orca ADE - 开源多 Agent 开发环境实测]]
- [[Herdr 上手指南 - Agent 时代的终端工作区运行时]]
- [[Pi Agent 必装 8 个插件——Agentic Coding 完整插件矩阵]]

---

*基于 2026-09-21 全量交叉验证（GitHub API、两仓库 README/LICENSE、herdr.dev 官方文档、YC 公司页）。星数为查询日快照。*
