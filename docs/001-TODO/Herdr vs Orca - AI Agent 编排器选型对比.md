---
title: Herdr vs Orca - AI Agent 编排器选型对比
aliases:
  - Herdr 对比 Orca
  - Agent 编排器选型
  - Learn With Trevor Herdr vs Orca
tags:
  - ai-agent
  - ai-tools
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=5V8WEjZwVCs"
  - "https://herdr.dev/"
  - "https://www.onorca.dev/"
  - "https://github.com/herdrdev/herdr"
  - "https://github.com/stablyai/orca"
author: Learn With Trevor
created: 2026-09-20
updated: 2026-09-20
description: |
  Learn With Trevor 频道 3 分半钟横评的结构化选型笔记：Herdr（Rust 单二进制终端 runtime）与 Orca（Electron 桌面 ADE）的架构、并行模型、工作流差异，全部关键声称已对两个官方源核实。本站唯一横向对比视角，与三篇单品笔记互补。
level: beginner
stars: 3
---

# Herdr vs Orca - AI Agent 编排器选型对比

> Learn With Trevor（2026-07-27，3:31）的横评笔记。一句话结论：**两款都是「多 Agent 并行」工具，但走的是两条路——Herdr 是嵌在终端里的 agent runtime（运行时），Orca 是围着 worktree 转的桌面 ADE（Agent Development Environment）**。选谁不取决于谁功能多，而取决于你的手放在键盘上还是鼠标上。视频很短（3 分半），本笔记以 transcript 为骨架，逐项对 herdr.dev 与 onorca.dev 官方源核实补强。

## 目录

- [[#TL;DR 选型一句话]]
- [[#两款工具是什么]]
- [[#架构对比：runtime vs app]]
- [[#并行模型对比：socket 扇出 vs worktree 隔离]]
- [[#七维选型矩阵]]
- [[#工作流体验对比]]
- [[#选型决策树]]
- [[#验证与勘误记录]]
- [[#与既有笔记的互补]]
- [[#全文总结]]
- [[#参考资料]]
- [[#相关笔记]]

---

## TL;DR 选型一句话

| 你是谁 | 选 | 一句话理由 |
|--------|-----|-----------|
| 终端重度用户，tmux/SSH/远程服务器常态 | **Herdr** | 单 Rust 二进制、socket API、会话持久，agent 在你不在时继续跑 |
| 想把一个 prompt 撒给 5 个 agent 比稿的人 | **Orca** | fan-out + 独立 worktree + 双栏 diff，发散-收敛全流程可视化 |
| 两个都想要 | 都装 | 不互斥：Herdr 管「住」，Orca 管「比」；不少人 Orca 里开 Herdr pane |

视频原话的框架很准：**better option depends on how you like to work——native terminal 还是 graphical app**。

---

##两款工具是什么

### Herdr：agent 的运行时（runtime）

- 官方自我定位：**the agent runtime**——「Herdr is where your coding agents live」（herdr.dev 首页标题）
- 单一 Rust 二进制，`curl -fsSL https://herdr.dev/install.sh | sh` 一条命令安装，Apache 2.0 开源
- 不包裹、不替代任何 agent：Claude Code、Codex、Cursor、opencode、Grok 等照常跑，Herdr 只「拥有它们的终端」
- 当前规模（herdr.dev 首页快照，2026-09-20）：36,541 GitHub stars、767,576 installs、1,022 社区插件、21 种 agent CLI 自动检测；已融 $6M seed

### Orca：给 agent 舰队造的桌面环境（ADE）

- 官方自我定位：**The agent development environment**——ADE 的 A 是 Agent，理解为「专为 AI agent 设计的 IDE」
- 独立桌面应用（macOS / Windows / Linux），YC-backed，MIT 开源，仓库 `stablyai/orca`
- 当前规模（GitHub 快照，2026-09-20）：71.6k stars、4.7k forks、11,261 commits
- 内建全家桶：GPU 渲染终端（可分割）、双栏 diff、内嵌 Chromium（Design Mode）、手机 companion App（iOS App Store + Android APK）、GitHub/Linear 整合

```bash
# Herdr：一条命令，活在你的终端里
curl -fsSL https://herdr.dev/install.sh | sh

# Orca：去官网下载桌面安装包（也支持 brew/cask）
# https://www.onorca.dev/download
```

---

## 架构对比：runtime vs app

这是两款工具最本质的分野——**谁来持有终端会话**：

```
Herdr（runtime 模式）                    Orca（app 模式）
─────────────────────                   ─────────────────────
你的终端（任意）                          Orca 桌面窗口
   │ attach/detach                          │
   ▼                                        ▼
herdr-server（后台常驻进程）             Electron 应用（GPU 渲染终端）
   │ 持有全部 PTY/会话状态                  │ 持有 worktree × N
   ▼                                        ▼
agent CLI × N（各自 pane）               agent CLI × N（各自 worktree）
   │                                        │
   └─ JSON-over-socket API                 └─ 内嵌 Chromium + diff viewer
      （agent 可互相操作）                     + 手机 pairing
```

| 维度 | Herdr | Orca |
|------|-------|------|
| 形态 | 单 Rust 二进制，嵌入现有终端 | 独立桌面应用（Electron + 原生模块） |
| 会话持有者 | herdr-server 后台进程 | 桌面 app 进程 |
| 断线/重启 | 布局与 agent 会话恢复（官网：restart the machine and Herdr brings the layout back） | worktree 终端与内建浏览器状态恢复（实测笔记验证） |
| Agent 互操作 | socket API 即 agent 操作面：互相开 pane、互相提问、等对方 blocked | CLI 驱动环境 + MCP/hooks/skills |
| 远端场景 | SSH attach，多机组网（herdr machine add） | SSH worktrees + 手机 pairing |

关键洞察（承接 [[Herdr 上手指南 - Agent 时代的终端工作区运行时]] 的核心结论）：tmux 也能保终端，但不知道哪个 pane 是 agent、它是 blocked 还是 idle；Orca 这类 app 有用，但关掉 app 群组就没了——**Herdr 做的是它们底下那层**。

---

## 并行模型对比：socket 扇出 vs worktree 隔离

两款都解决「多 agent 同时跑」，但隔离边界不同：

```
Herdr：pane 边界                     Orca：worktree 边界
──────────────                      ──────────────────
一个 workspace                       主仓库（main worktree）
 ├─ pane 1: claude                   ├─ .worktrees/feat-a  ← agent 1（独立分支）
 ├─ pane 2: codex                    ├─ .worktrees/feat-b  ← agent 2（独立分支）
 └─ pane 3: opencode                 └─ .worktrees/feat-c  ← agent 3（独立分支）
      │                                   │
      └─ agent 通过 socket API             └─ 同一 prompt fan-out 最多 5 路
         互相 spawn/读取输出                  双栏 diff 比稿 → 一键合并最优
```

- **Herdr 的并行**：agent 之间是对等协作者——Claude 写完代码可以自动开 pane 唤 Codex 审查（Agent Skill 能力，见上手指南笔记）。隔离靠 pane/workspace，代码改动共享同一棵工作树（要隔离需自行配 worktree）
- **Orca 的并行**：核心是 **fan-out 模型**——单条 prompt 同时派发至最多 5 个并行 agent，每个落在独立 Git worktree，天然不互踩；产出去向是**双栏 diff 比对 + 一键选优合并**，为「发散探索-收敛决策」设计
- 视频对 Orca 场景的概括很准：compare multiple implementation paths without mixing changes together

---

## 七维选型矩阵

| # | 维度 | Herdr | Orca | 胜者倾向 |
|---|------|-------|------|---------|
| 1 | 资源占用 | 单二进制，无 GUI 外壳 | Electron + GPU 渲染，较重 | Herdr |
| 2 | 并行隔离 | pane/workspace（代码树共享） | 每路独立 worktree | Orca |
| 3 | 会话持久 | server 持有，重启恢复，detached 常态 | app 内恢复 | Herdr |
| 4 | 结果比对 | 无内建 diff（外接工具） | 双栏 diff + 一键合并 | Orca |
| 5 | 远程/移动 | SSH attach + 多机；无官方手机端 | SSH worktrees + iOS/Android App | 平手 |
| 6 | Agent 生态 | 21 种 CLI 自动检测 + 1,022 插件 | 27 种具名 + 任意 CLI agent | 平手 |
| 7 | 学习成本 | 要熟终端与 prefix key | 桌面隐喻，所见即所得 | Orca（对非终端流） |

> 数字口径见「验证与勘误记录」：stars/插件数是 2026-09-20 官网快照，活计数会漂移。

---

## 工作流体验对比

### Herdr：键盘优先，终端即界面

- tmux 风格前缀键（prefix keys）+ 鼠标点击拖拽分割，两层操作可选
- 保留**真实终端视图**——不是模拟终端的 GUI 包装，Vim/Neovim 用户零妥协
- 视频画像：terminal power users who want persistent agent multiplexing without leaving the command line

### Orca：视觉优先，比稿即界面

- 多 agent 输出并排呈现，review parallel outputs → select/merge 一键完成
- Design Mode：内嵌 Chromium 点选页面元素，HTML/CSS 上下文直接回喂 agent（详见 [[Orca ADE - 开源多 Agent 开发环境实测]]）
- GitHub/Linear 任务追踪打通，手机上看进度

### 最佳实践

- ✅ 已有 tmux 肌肉记忆、常 SSH 到服务器跑长任务 → Herdr，别折腾桌面 app
- ✅ 评审型工作流（同一任务要 3-5 个方案比稿）→ Orca，fan-out + diff 省的是真时间
- ❌ 不要把 Herdr 塞进 tmux 里跑——agent 检测看不到 tmux 会话后面的进程（官方警告）
- ❌ 不要用 Orca 开满 5 个 agent 跑 UI 重任务还挂着内建 Chromium——内存吃紧（实测笔记教训）
- ❌ 别按「功能多少」选——两条路线都在进化，按你的操作惯性选才不返工

---

## 选型决策树

```
你的多 Agent 需求是什么？
│
├─ 长时间挂着跑（CI 式、夜间批处理、服务器常驻）
│   └─→ 键盘流熟吗？
│        ├─ 是 ────────────→ Herdr（persistence + detached）
│        └─ 否，要 GUI ─────→ Orca（app 内恢复 + 手机盯进度）
│
├─ 同一任务多方案比稿（架构选型、实现竞速）
│   └─→ 要代码级隔离吗？
│        ├─ 要（改的是同一批文件）→ Orca（worktree 天生隔离）
│        └─ 不要（各改各的模块）─→ Herdr 也行（pane 并排看输出）
│
├─ Agent 之间要互相协作（A 写完唤 B 审查）
│   └─→ Herdr（socket API 即 agent 操作面）
│
└─ 前端/设计相关（要点选页面元素回喂）
    └─→ Orca（Design Mode 独此一家）
```

---

## 验证与勘误记录

视频声称与官方源逐项核对（2026-09-20，快照口径）：

| 视频声称（transcript/Insights） | 验证结果 | 权威来源 |
|-------------------------------|---------|---------|
| Orca 支持 30+ CLI agents | ⚠️ 官网对比表列 **27 个具名 agent** + 「Any other CLI agent」兜底；30+ 属口径夸大（把兜底算上勉强成立） | onorca.dev 对比表 |
| Herdr 是 lightweight terminal multiplexer | ⚠️ 官方自我定位已演进为 **the agent runtime**；multiplexer 是其呈现层，runtime 才是卖点 | herdr.dev 首页 |
| transcript 全程拼作 Herder | ❌ 官方拼写 **Herdr**（herdr.dev / GitHub herdrdev / X @herdrdev）；Herder 是 ASR 词典词噪声 | herdr.dev |
| 单条 prompt fan-out 至 5 个并行 agent，各自独立 worktree | ✅ 口播与官网 workspaces 表述一致；既有实测笔记同口径验证 | transcript + onorca.dev |
| Herdr 会话可 survive restarts、支持 detached 后台进程 | ✅ 官网原文 restart the machine and Herdr brings the layout back and resumes their sessions | herdr.dev |
| Herdr 纯 socket API，agent 可 spawn panes / read output | ✅ 官网 agent-automation 表述：CLI 与 socket API 是同一操作面 | herdr.dev |
| Orca 是 macOS/Windows/Linux 桌面 app | ✅ 独立应用；仓库含 electron.vite.config.ts，技术栈坐实 | onorca.dev + GitHub |
| Orca 内建双栏 diff / 内嵌浏览器 / 手机 App | ✅ 对比表 + App Store / Android APK 链接俱在 | onorca.dev |
| 两款都开源 | ✅ Herdr **Apache 2.0** / Orca **MIT**（协议不同，商用注意） | GitHub LICENSE |
| GitHub stars（数量级） | 📊 Herdr 36.5k vs Orca 71.6k（2026-09-20 快照，活计数） | herdr.dev + GitHub |

> 本视频为英文频道横评，无中文音译问题；主要噪声是 ASR 把 Herdr 规范化为词典词 Herder（与既有两篇 Orca 笔记发现的同源问题，详见 [[Orca ADE - 多 Agent 工作台入门导览]] 的读音勘误连锁提示）。

---

## 与既有笔记的互补

本站已有三篇单品笔记，本篇补上横向视角：

| 笔记 | 视角 | 侧重 |
|------|------|------|
| [[Herdr 上手指南 - Agent 时代的终端工作区运行时]] | Herdr 单品教学 | 四层模型、状态检测、Agent Skill、vs tmux 的 runtime/app 之辨 |
| [[Orca ADE - 开源多 Agent 开发环境实测]] | Orca 单品实测 | Windows 安装、四模型并行实测、Design Mode、SSH worktrees |
| [[Orca ADE - 多 Agent 工作台入门导览]] | Orca 概念框架 | ADE 概念、worktree 机制、Diff 评审闭环、手机配对 |
| **本篇** | **横向选型** | **架构分野、并行模型对比、七维矩阵、决策树** |

---

## 全文总结

两款工具回答的是同一个问题的两个半边：「多个 coding agent 同时跑，人怎么管？」

- **Herdr 的答案是把基础设施做薄**——一个常驻 runtime 持有所有终端会话，agent 之间通过 socket API 互操作，人随时 attach/detach。它信任你的终端，只补终端缺的东西：状态感知与会话持久
- **Orca 的答案是把驾驶舱做全**——一个桌面 app 把 worktree、diff、浏览器、手机全收进来，为「发散 5 路再收敛 1 路」的比稿工作流优化。它信任可视化，替你把并行治理变成点击

视频标题问 which is actually worth it——实测答案是**都对，但对的人不同**。真正该问的是自己的问题：你的 agent 是「挂机跑」还是「比稿跑」？前者 Herdr，后者 Orca，两端都重的人，两个都装也不冲突。

---

## 参考资料

- [视频：Herdr vs Orca: Which AI Agent Orchestrator is Actually Worth It? (2026 Honest Review)](https://www.youtube.com/watch?v=5V8WEjZwVCs)（Learn With Trevor，2026-07-27，3:31）
- [Herdr 官网](https://herdr.dev/) · [GitHub: herdrdev/herdr](https://github.com/herdrdev/herdr)（Apache 2.0，Rust）
- [Orca 官网（onorca.dev）](https://www.onorca.dev/) · [GitHub: stablyai/orca](https://github.com/stablyai/orca)（MIT，YC-backed）

## 相关笔记

- [[Herdr vs Orca 深度对比 - 极简运行时 vs 全功能 ADE]]——Panda Making Money 26 分钟深评版，含 AGPL→Apache 2.0 许可证变更时间线考据
- [[Herdr 上手指南 - Agent 时代的终端工作区运行时]]
- [[Orca ADE - 开源多 Agent 开发环境实测]]
- [[Orca ADE - 多 Agent 工作台入门导览]]
- [[Pi Agent 必装 8 个插件——Agentic Coding 完整插件矩阵]]
