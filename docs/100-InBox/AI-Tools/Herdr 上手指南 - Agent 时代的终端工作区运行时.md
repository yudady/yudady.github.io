---
title: Herdr 上手指南 - Agent 时代的终端工作区运行时
aliases: [Herdr 教程, 十分钟掌握 Herdr, Herdr terminal workspace]
tags:
  - terminal
  - ai-agents
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=BC-Fhy65Sbk"
  - "https://herdr.dev/"
  - "https://github.com/herdrdev/herdr"
author: GeekHour
created: 2026-09-09
updated: 2026-09-09
description: 基于 GeekHour 教学视频 + Herdr 官方文档交叉验证的上手笔记：四层模型、状态感知、快捷键、Agent Skill 跨代理协作，以及 herdr vs tmux/Zellij/cmux 的本质区别（runtime vs app）。
level: beginner
stars: 4
---

# Herdr 上手指南 - Agent 时代的终端工作区运行时

> GeekHour 频道教学视频（2026-09-06，9:20）的学习笔记。Herdr 是用 Rust 写的开源（Apache 2.0）终端工作区运行时，被称作「Agent 时代的 tmux」：多 Agent 并行时你不再逐窗轮询谁在等你，侧边栏直接标出每个 Agent 的 blocked / working / idle。视频本身无字幕（创作者禁用），本笔记以用户 Content Insights 为骨架，全部技术声称已对官方文档（herdr.dev/docs）逐条核对，勘误见文末表——最重要的是：**Herdr 与 tmux 的本质区别不是功能多少，而是 runtime（服务端进程持有终端）与 app（窗口持有进程）的架构区别**。

## 目录

- [定位与痛点：为什么不是又一个 tmux](#定位与痛点为什么不是又一个-tmux)
- [核心概念：四层模型与状态感知](#核心概念四层模型与状态感知)
- [安装与基础操作](#安装与基础操作)
- [Agent 生态与状态检测机制](#agent-生态与状态检测机制)
- [Agent Skill：跨代理自动化协作](#agent-skill跨代理自动化协作)
- [进阶特性速览](#进阶特性速览)
- [选型决策树](#选型决策树)
- [验证与勘误记录](#验证与勘误记录)
- [参考资料](#参考资料)
- [相关笔记](#相关笔记)

---

## 定位与痛点：为什么不是又一个 tmux

视频开头的场景：你同时开着 3 个 Claude Code / Codex 窗口，其中一个在等输入 `yes`、一个跑卡了、一个早完成了——但你只能逐个窗口切过去看。Tmux 能持久化会话但配置维护成本高；Ghostty / iTerm 这类分页终端只负责渲染字符，**完全不知道进程背后的 Agent 处于什么状态**。

官方对比页把市面工具分成两类，这个二分法是理解 Herdr 的钥匙：

```
        ┌──────────────────────────────────────────────┐
        │  App（窗口持有进程）                          │
        │  tmux · Zellij │ cmux · Warp │ Conductor 等   │
        │     ↑ 窗口一关/一崩，Agent 跟着停             │
        └──────────────────────────────────────────────┘
        ┌──────────────────────────────────────────────┐
        │  Runtime（服务端持有终端，UI 只是客户端）      │
        │  Herdr：herdr-server 后台常驻                 │
        │     ↑ TUI 关掉、SSH 断掉、重启机器，Agent 活着 │
        └──────────────────────────────────────────────┘
```

官方对比矩阵（来自 herdr.dev/compare，逐行核对）：

| 能力 | herdr | tmux · Zellij | cmux · Warp | Solo | Conductor · Emdash · Superset |
|------|-------|---------------|-------------|------|------------------------------|
| 本质 | runtime + 客户端 | 终端复用器 | 终端 app | 进程仪表盘 | 管理器 app |
| UI 关掉后工作还在 | ✅ server 持有终端 | ✅ detach | 会话恢复 | 仅 app 开着时 | 仅 app 开着时 |
| 跑在你现有终端里 | ✅ | ✅ | ❌ 取代它 | ❌ 桌面 app | ❌ 桌面 app |
| 语义化 Agent 状态 | blocked·working·done·idle | — | 注意力提示 | 进程状态 | 工作区状态 |
| Agent 自驱 API | 读·发·等待·分割·附加 | 终端脚本 | app API | 进程级 MCP | 工作流 API |
| 直接附加到单个 Agent | ✅ | — | — | — | — |

一句话版官方对比：tmux 能保住终端，但不知道哪个 pane 是 Agent、它是 blocked 还是在干活；Conductor/Emdash 这类管理器有用，但关掉 app 群组就没了——Herdr 是它们底下那层。

**当前体量**（2026-09-09 抓取）：GitHub 33.4k stars / 2.4k forks、743,473 次安装、986 个社区插件、21 种 Agent CLI 开箱识别、YC 孵化。注意官网首页显示 36,150 stars 而 GitHub 页面抓取为 33.4k，两者有时差，引用时以 GitHub 实时数为准。

---

## 核心概念：四层模型与状态感知

```
Workspace（工作区：一个 repo / 一件事）
 ├── Tab（布局：agents / logs / server / review 等视图分组）
 │    ├── Pane（真实终端：独立 shell，可上下/左右分割）
 │    │    └── Agent（被识别的进程：Claude Code / Codex / …）
 │    └── Pane …
 └── Tab …
```

| 层 | 官方定义（concepts 文档） | 实践用法 |
|----|--------------------------|---------|
| Workspace | 顶层项目容器，拥有 tabs 和 panes，侧边栏状态自内向上汇总 | 一个 repo 一个 workspace |
| Tab | workspace 内的布局，可从 CLI / socket API 寻址 | 分离 agents / logs / review 视图 |
| Pane | 真实终端，跨客户端 detach 存续，可重命名/读取/注入输入 | — |
| Agent | pane 内被识别的进程，带语义状态 | 21 种开箱识别 |

**Agent 状态机（官方五态，注意与视频口播差异）**：

| 状态 | 含义 |
|------|------|
| `blocked` | 需要输入、批准或决策——**这就是「等你」的那个** |
| `working` | 正在跑 |
| `done` | 跑完了但你还没看过（每个客户端各自记录已读） |
| `idle` | 已结束或等待中，且已被看过 |
| `unknown` | 无法确信分类 |

**状态上卷（rollup）**：一个 Agent blocked → 它的 pane、tab、workspace 侧边栏全部标红。视频说「任一 Agent Blocked 整个 Workspace 同步标记」，官方文档确认这正是核心工作流：开一堆 Agent 并行跑，看侧边栏就知道哪个项目需要决策、哪个还在跑、哪个可review。

✅ Herdr 是鼠标原生（mouse-native）：点击 pane/tab/workspace/agent、拖分割线、右键菜单全部可用，快捷键是可选层
❌ 不要把 Herdr 塞进 tmux 里跑（官方警告：Agent 检测看不到 tmux 会话后面的 agent；若 shell 框架自动进 tmux，Herdr 只认得 tmux 进程本身）

---

## 安装与基础操作

安装（官方 runblock）：

```bash
# macOS / Linux
curl -fsSL https://herdr.dev/install.sh | sh
# Homebrew / mise
brew install herdr
mise use -g herdr
# Windows (PowerShell)
powershell -ExecutionPolicy Bypass -c "irm https://herdr.dev/install.ps1 | iex"
```

启动即用：在工作目录敲 `herdr`，跑 Agent、分割 pane、走人。`ctrl+b q` detach，再敲 `herdr` reattach。

**先学这五个键（官方 Keyboard 文档「Learn these five first」）**：

| 动作 | 键 |
|------|-----|
| 新建 Tab | `prefix+c` |
| 向右 / 向下分割 | `prefix+v` / `prefix+-` |
| pane 间移动 | `prefix+h/j/k/l` |
| Workspace 导航 | `prefix+w`（配合 `j/k` 选择） |
| Detach（全部继续跑） | `prefix+q` |

prefix 默认 `ctrl+b`（与 tmux 相同的「按前缀、松开、再按动作键」模式）。`prefix+?` 随时看全部绑定，按 `/` 可过滤。

常用补充：`prefix+z` 放大聚焦 pane、`prefix+x` 关 pane、`prefix+[` 进 copy mode（进程不暂停，输出继续滚）、`prefix+g` goto picker、`prefix+b` 切侧边栏、`prefix+1..9` 跳 Tab。

**Prefix 冲突与改键**（视频建议改 `ctrl+s` 的背景）：官方文档明确记载默认 `ctrl+b` 在 copy mode 里会被 prefix 拦截——你想用 `ctrl+b` 向上翻页时它进了 prefix 模式，所以 Vim/阅读习惯用户建议换 prefix。改键之外还有 prefix-free 方案：直接绑定免前缀和弦。官方扫描了 Ghostty/iTerm2/kitty/WezTerm/Warp/Windows Terminal 等默认键位后结论——**`ctrl+alt` 家族几乎全线空闲**，是最安全的免前缀选择；但要避开 `ctrl+alt+方向键`（GNOME 工作区切换）、`ctrl+alt+t`（Ubuntu 启动终端）、`ctrl+alt+f1..f12`（Linux 虚拟控制台）。

---

## Agent 生态与状态检测机制

开箱识别 21 种 Agent CLI（官方 agents 文档全表精简）：

| 检测方式 | Agent |
|----------|-------|
| lifecycle hooks（装了集成后为权威） | Pi、OMP、Kimi Code CLI、MastraCode |
| lifecycle plugin 可选 | OpenCode、Kilo Code CLI |
| screen manifest（屏幕快照识别） | Claude Code、Codex、Cursor Agent CLI、Grok CLI、GitHub Copilot CLI、Qwen Code、Droid、Qoder CLI、Hermes Agent、Amp、Antigravity CLI、Kiro CLI、Maki、Muse、Devin CLI |
| 检测到但测试较少 | Gemini CLI、Cline |

（对 Hermes Agent 用户：Hermes Agent 在识别列表中，screen manifest 方式，session 角色。）

**两级检测权威**，这是理解「Herdr 怎么知道 Agent blocked」的关键：

```
pane 前台进程识别
   ↓
有完整 lifecycle hooks？──是──→ hook 上报为权威（idle/working/blocked + 会话身份）
   │                            （不再跑屏幕检测，避免双真相源）
   否
   ↓
screen manifest：读 pane 底部实时缓冲快照
   → 用 TOML manifest 规则匹配（含终端标题 / OSC 进度序列）
   → 分类 idle / working / blocked
```

工程细节值得注意：

- **blocked 判定刻意从严**：只有快照命中已知的批准/提问/权限 UI 才标 blocked；认不出的新提示形态先落 idle（explain 输出里标 `default_known_agent_idle_fallback`），宁可漏报不误报——误报只影响显示和等待，不会让 Herdr 替你按键
- **manifest 热更新**：内置 + herdr.dev 远端 manifest，远端规则更新无需重启即生效；本地 override 永远赢
- **排障命令**：`herdr agent explain` 显示某个 pane 为什么是这个状态（规则来源、版本、命中证据）
- **沙箱/VM 包裹**：wrapper 会藏住真实 Agent 进程，用 `HERDR_AGENT=claude fence -- claude` 这类环境变量提示 Herdr 该用哪个 manifest

---

## Agent Skill：跨代理自动化协作

视频最实用的段落：Claude Code 改完代码 → 以前要人工复制需求给 Codex review → 再把结果贴回来。用 Herdr 的 Agent Skill 后，Claude Code 完成任务即自动开新 pane、唤 Codex 审查、回收反馈，端到端无人搬运。

官方实现（agent-skill 文档）：Herdr 内置一份 `skills/herdr/SKILL.md`，装进任何支持 skill 的 Agent，教会它通过 `herdr` CLI 操作自己所处的环境：

```
Claude Code（pane A，任务完成）
   │  安装了 herdr skill，HERDR_ENV=1 已设
   ↓
herdr pane split  →  新 pane B（不抢焦点）
herdr pane run    →  启动 Codex review
herdr agent wait  →  等 Codex 真正 blocked/完成（而非盲发按键）
herdr pane read   →  读回审查结论
   ↓
Claude Code 拿到反馈继续迭代
```

安装：

```bash
# 官方推荐（npx skills，-g 全局装进支持的 agent）
npx skills add herdrdev/herdr -g

# 或直接打印当前二进制匹配的 skill 副本
herdr --skill
```

**安全护栏**（skill 第一条规则）：`HERDR_ENV=1` 未设置时，Agent 必须停下并声明自己不在 Herdr 管理的 pane 里——防止 pane 外的 Agent 试图控制不属于它的会话。这个环境变量由 Herdr 在启动 pane 内进程时注入。

能力清单（官方）：查看 workspaces/tabs/panes/邻居 Agent、split pane 不抢焦点、读 pane 输出与近期日志、等服务器/测试/另一个 Agent 完成、在兄弟 pane 启动辅助 Agent。

注意区分两份文件：`SKILL.md` 教 Agent **操作** Herdr；`herdr.dev/agent-guide.md` 教 Agent **给人类讲解** Herdr。给编程 Agent 装前者。

---

## 进阶特性速览

| 特性 | 说明 | 视频是否覆盖 |
|------|------|-------------|
| 多机组网 | `herdr machine add workbox` 把桌面/服务器 SSH 进来，workspace 与本地并列，断线继续跑 | ❌ 视频未提（发布后新功能） |
| 直接附加单 Agent | `herdr attach <agent>` 只附加到一个 Agent 终端而非整个 UI | ❌ |
| 插件市场 | 986 个社区插件扩展 pane 与工作流 | 提及外掛擴充 |
| 命名 session | 完全隔离的运行时命名空间（官方建议：先用 workspace，需要彻底隔离的 pane/socket/持久状态才用 session） | ❌ |
| 多客户端 | 多个客户端各自看不同 workspace/tab；看同一个 tab 时最后聚焦者控制 pane 尺寸 | ❌ |
| Herdr Cloud | 官网预告：免 SSH 配置连自家机器，waitlist 中 | ❌ |

**检测生态位**：[[Pi Agent 必装 8 个插件——Agentic Coding 完整插件矩阵]] 里的 `pi-herdr-agents` 插件就是建在这层之上——Pi 的子 Agent 各占一个 Herdr pane，主界面方框汇总进度且不被 block。Herdr 的 socket API（JSON-over-socket）正在成为多个编排工具的公共底座。

---

## 选型决策树

```
你的需求是？
├─ 只是终端会话持久化，不跑 AI Agent
│    └─→ tmux / Zellij 足矣，Herdr 的语义状态用不上
├─ 同时跑 2+ 个 CLI Agent（Claude Code / Codex / …）
│    ├─ 想要「谁在等我」一眼可见 ─────→ Herdr（本笔记）
│    ├─ 只想看进程健康/日志 ────────→ Solo（仪表盘）
│    └─ 想要 worktree/diff/review 队列 ─→ Conductor / Emdash（可搭配 Herdr 用）
├─ 嫌终端丑，想要一体化体验
│    └─→ cmux / Warp（代价：换掉你的终端，工作搬进它的窗口）
└─ 服务器上跑 Agent，本机随时看
     └─→ Herdr（SSH reattach + 多机组网）
```

✅ 安装后第一件事：改 prefix（`ctrl+b` 与 copy mode 翻页冲突），`ctrl+alt` 家族最安全
✅ 第二件事：给常用 Agent 装 skill（`npx skills add herdrdev/herdr -g`），解锁跨 Agent 流水线
❌ 不要在 tmux 里嵌套跑 Herdr；shell 框架若自动进 tmux 要先关掉

---

## 验证与勘误记录

视频无字幕（GeekHour 创作者级禁用，Tier 0 实测），核对基准为用户 Content Insights vs 官方文档：

| 声称（视频/Insights） | 验证结果 | 实际情况 | 来源 |
|------|---------|---------|------|
| 状态为 Block / Wait / Idle / Done | ⚠️ | 官方五态：`blocked` / `working` / `done` / `idle` / `unknown`。无 Wait 态；Insights 漏了 working 和 unknown | herdr.dev/docs/concepts |
| 原生支持「超过 20 种」Agent | ✅ | 精确为 21 种开箱识别（+Gemini CLI/Cline 检测较少），Claude Code/Codex/Cursor/OpenCode/Copilot 全在列 | herdr.dev/docs/agents |
| Rust 编写、后台 server 进程、关 TUI 不断 Agent | ✅ | 「one rust binary, no electron」「server owns panes」 | GitHub README |
| Prefix 默认 Ctrl+B；V/‑ 分割；h/j/k/l 切换；W workspace；Q detach | ✅ | 官方 Keyboard 文档逐一吻合 | herdr.dev/docs/keyboard |
| Ctrl+B 与 Vim 翻页冲突，建议改键 | ✅ | 官方明确记载 copy mode 中 ctrl+b 进 prefix 模式而非翻页；另给出 ctrl+alt 免前缀方案 | herdr.dev/docs/keyboard |
| 全局模糊搜索 `Prefix + /` | ⚠️ | 官方文档为：`prefix+?` 打开键位帮助后按 `/` 过滤；workspace 跳转是 `prefix+g`（goto picker）。`Prefix+/` 直达全局搜索未在文档见到 | herdr.dev/docs/keyboard |
| Agent Skill：Claude Code 完成后自动开 pane 唤 Codex review 并回传 | ✅ | agent-skill 文档：split panes、start helper agents in sibling panes、agent wait、pane read 全套能力支持该流水线 | herdr.dev/docs/agent-skill |
| Workspace 状态继承（任一 Agent blocked 则整体标记） | ✅ | 「A blocked agent makes its pane, tab, and workspace look blocked」 | herdr.dev/docs/agents |
| 「Agent 时代的 Tmux」定位 | ✅ | 官方自己也用这个类比，但强调差异在语义状态 + Agent 自驱 API | herdr.dev/compare |
| 视频标题「十分钟」 | ⚠️ | 实际时长 9:20（营销取整，无伤大雅） | webReader 元数据 |
| GitHub stars | ⚠️ | 官网 36,150 vs GitHub 页 33.4k（2026-09-09 抓取，存在缓存时差）；743K 安装、986 插件、YC 孵化为官网口径 | herdr.dev / GitHub |

---

## 参考资料

- [视频：十分钟掌握 Herdr - AI 时代的终端神器](https://www.youtube.com/watch?v=BC-Fhy65Sbk)（GeekHour，2026-09-06）
- [Herdr 官网](https://herdr.dev/) · [GitHub: herdrdev/herdr](https://github.com/herdrdev/herdr)（Apache 2.0，Rust）
- 官方文档：[Concepts](https://herdr.dev/docs/concepts/) · [Keyboard](https://herdr.dev/docs/keyboard/) · [Agents](https://herdr.dev/docs/agents/) · [Agent skill](https://herdr.dev/docs/agent-skill/) · [Compare](https://herdr.dev/compare/)
- 第三方评测：[BetterStack: Terminal Multiplexer with Built-in AI Agent State](https://betterstack.com/community/guides/ai/herdr-ai-agent/)

## 相关笔记

- [[Pi Agent 必装 8 个插件——Agentic Coding 完整插件矩阵]]（pi-herdr-agents 插件建立在 Herdr socket API 之上）
- [[热门 AI Agent 项目速览 - Tucky、Airuncode、HyperProbe 与 GPT-6 Astra]]（同期生态：Agent 工具的本地化/工作流嵌入趋势）

---

*文档生成时间：2026-09-09。视频无字幕，笔记基于用户 Content Insights + Herdr 官方文档（v0.9.0 时代）交叉验证写成。Herdr 迭代极快（1,479 commits、远端 manifest 热更新），快捷键与状态机以 herdr.dev/docs 当前版本为准。*
