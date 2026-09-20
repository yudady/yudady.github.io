---
title: Orca ADE - 开源多 Agent 开发环境实测
aliases:
  - Orca Agent Development Environment
  - stablyai-orca
tags:
  - ai-agent
  - ai-tools
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=Tg-6_3iFYPs"
  - "https://github.com/stablyai/orca"
  - "https://www.onorca.dev/"
author: Tonbi's AI Garage
created: 2026-09-20
updated: 2026-09-20
description: |
  Orca（github.com/stablyai/orca）开源 Agent 开发环境（ADE）首测笔记：Git Worktree 隔离、跨模型并行评测、Design Mode 视觉反馈闭环、SSH 远端工作流与会话持久性。
level: intermediate
stars: 4
---

# Orca ADE - 开源多 Agent 开发环境实测

> Tonbi's AI Garage 频道对 Orca 的 live first look：从 Windows 安装、Hermes 设为默认 Agent，到 Worktree 隔离、四模型并行跑同一前端任务、Design Mode 点选网页元素回喂 Agent。适合正在评估多 Agent 工作区（Herder / tmux / 多开终端）的工程师。

## 目录

- [[#定位与安装架构]]
- [[#核心组织架构：Git Worktrees 与多 Agent 视窗管理]]
- [[#平行评测与全局状态仪表板]]
- [[#Design Mode 与视觉化反馈闭环]]
- [[#远端工作流、文件管理与会话持久性]]
- [[#验证与勘误记录]]
- [[#全文总结与行动建议]]
- [[#参考资料]]

---

## 定位与安装架构

### 核心概念

Orca 是一套开源的 **Agent 开发环境（ADE，Agent Development Environment）**，定位「为平行 Agent 舰队打造的工作环境」——不只是终端多工器（Terminal Multiplexer），而是把终端、diff、内建浏览器、文件管理、SSH 远端整合成一个应用。由 YC-backed 的 stablyai 团队开发，MIT 协议，GitHub 71.6k stars / 4.7k forks（2026-09 实测数据）。

官方入口（注意：视频口播与 Google 摘要给的拼写有误，见[[#验证与勘误记录|勘误表]]）：

- **源码**：`github.com/stablyai/orca`（非 `stably/orca`）
- **官网下载**：`onorca.dev`（非 `orca.dev`——同名软件很多，别下错）

### 仓库画像（交叉验证自 GitHub）

| 维度 | 实测数据 |
|------|----------|
| Stars / Forks | 71.6k / 4.7k |
| 协议 | MIT（完全开源） |
| 背书 | Y Combinator |
| 技术栈 | Electron + TypeScript（pnpm workspace） |
| 平台 | macOS / Windows / Linux + iOS / Android companion |
| 提交活跃度 | 11,261 commits，官方自述「daily ships」 |
| 支持 Agent 数 | 27+（Claude Code、Codex、Grok、Hermes Agent、OpenCode、Qwen Code…任何 CLI agent） |

### 安装与初始化向导

```
下载安装包（onorca.dev/download，Win/macOS/Linux）
        │
        ▼
┌─ 初始化向导（首启依序引导）─────────────┐
│ 1. 预设 Agent   → 选 Hermes（也有       │
│                  YOLO 模式可跳确认）     │
│ 2. 终端环境     → PowerShell / bash 等  │
│ 3. 主题         → Dark Mode             │
│ 4. 系统通知偏好 → agent 完成时提醒       │
└─────────────────────────────────────────┘
        │
        ▼
添加第一个项目 → 进入主界面
```

macOS 也可走 Homebrew；Arch 有 AUR（`stably-orca` / `stably-orca-git` 源码构建）。

### 最佳实践

- ✅ 从官网 `onorca.dev/download` 下载签名构建（Windows 侧由 SignPath 签名）
- ✅ 不信任口播拼写，同名「Orca」软件很多，认准 GitHub org `stablyai`
- ❌ 不要从第三方下载站拿安装包

---

## 核心组织架构：Git Worktrees 与多 Agent 视窗管理

### 独立 Worktree 隔离

专案下可建立多个独立 **Git Worktree**，每个 Worktree 拥有专属分支。多个 Agent 平行改代码时天然互不冲突——这是 Orca 解决「多 Agent 同时编辑」的底层机制，也是它区别于纯终端多工器的核心。

实测中 Up 主为 Little John（区块链代币化股票分析专案，code name `onchain agent`）建了 4 个 Worktree：`codex`、`grok build`（留空时自动生成「frigid bird」这类名字）、`claude` 等。

### 灵活的视窗拆分（Splits & Tabs）

```
┌─ Project: onchain agent ─────────────────────────┐
│ [tab: Hermes] [tab: Claude+] [+ 新增 Agent 頁籤]  │
│ ┌───────────────┬───────────────────────────────┐│
│ │  Hermes TUI   │  Grok TUI（split right）       ││
│ │  （初始图标）  ├───────────────────────────────┤│
│ │               │  Claude TUI（split down）      ││
│ └───────────────┴───────────────────────────────┘│
│  ↳ 水平/垂直任意分割；终端为 Ghostty 级 WebGL 渲染 │
└───────────────────────────────────────────────────┘
```

单个 Worktree 内可任意水平/垂直分割终端，或用页籤挂多个 Agent TUI（Hermes / Claude / Grok…），每个 Agent 有识别图标（如 Hermes 的新研究图标、xAI 的 𝕏 图标）。

### 两种组织模式对比

| 维度 | 一个 Worktree + 多 Agent | 多 Worktree + 单 Agent |
|------|--------------------------|------------------------|
| 适用场景 | 多角色协作同一份代码（如一个写码一个 review） | 同一任务多方案并行（model shootout） |
| 隔离级别 | 共享工作目录，共享分支 | 独立分支 + 独立目录 |
| 冲突风险 | 有（同文件竞写） | 无（Git 隔离） |
| 看板呈现 | 一张卡 | 每树一张卡，可拖动流转 |

### 看板式 Workspace Board

拖动 Worktree 卡片即可唤出看板视图（To-Do / In Progress / In Review / Done），一眼掌握所有 Worktree 状态；支持 `pin` 固定卡片而不改状态。看板可按 project / status / none 分组，卡片布局可选 compact。

### 最佳实践

- ✅ 并行实验一律走多 Worktree（每个一支分支），赢家合并回主线
- ✅ Worktree 命名带上用途（`codex-mockui`），自动生成的名字（frigid-bird）不好检索
- ❌ 不要让多个 Agent 挤在同一 Worktree 里改同一批文件

---

## 平行评测与全局状态仪表板

### 一键提示词并行发送（Model Shootout）

视频用真实专案实测：把同一条提示词分发给 Grok、Claude（×2）、Codex、Hermes 四类五个 Agent：

> "Create a mock UI for the Little John project based on the codebase. You can use some mock data, but I want the UI to look professional, refined, and clear."

```
        一条 Prompt
            │
   ┌────┬───┼────┬────┐        每个 Agent 各自的
   ▼    ▼   ▼    ▼    ▼        Git Worktree
 Claude Codex Grok Claude Hermes
 (wt1) (wt2) (wt3) (wt4) (wt5)
   │    │    │    │    │
   ▼    ▼    ▼    ▼    ▼
 完成先后 → 内建 Chromium 开产物 → Design Mode 点选迭代
   │
   └─→ Agent Dashboard 集中看谁在忙 / 谁 Needs You
```

实测产出对比（Up 主目测评价）：

| Agent | 完成顺序 | 产出形态 | 风格特征 |
|-------|----------|----------|----------|
| Claude | 最先完成 | Claude Artifact（托管页） | 专业精炼，第二实例带侧边栏变体 |
| Codex | 较晚 | 纯 HTML | Light 模式风格 |
| Grok | 中途完成 | 纯 HTML（可点选元素） | 好看 |
| Hermes | 最后 | 独立 HTML | 绿色系，干净 |

### Agent Dashboard（实验性）

单窗集中显示所有执行中 Agent 的状态：谁在忙、谁空闲、谁「Needs You」等待人工确认——点一下即可跳去回应问题或确认完成。需在 settings 手动开启。

### 判断决策树

```
要跨模型对比产出质量？
  ✅ 同一提示词 + 多 Worktree 并行 → 看板/Dashboard 监控 → 合并赢家
要长期驻留一套主力工作流？
  ✅ 先跑 1-2 个 Worktree 验证 RAM 占用，再扩编
只是想让两个 Agent 串流对话/协作？
  ❌ 不需要 Orca——单终端 + tmux 即可
```

### 最佳实践

- ✅ Dashboard 开「Needs You」通知，配合系统通知避免空等
- ✅ 底部 usage 面板可看各订阅用量与限额重置（Claude/Codex 订阅混用时很有用）
- ❌ 别同时开满 5 个 Agent 做 UI 任务还挂着内建 Chromium——内存吃紧（Up 主机器流畅，但任务负载轻）

---

## Design Mode 与视觉化反馈闭环

### 内建 Chromium 浏览器

Agent 产出的 HTML / 网页可直接在 Orca 内部浏览器打开（Claude Artifact 也能内嵌打开，需登录一次）。省去「切窗口 → 本地起 server → 截图贴回对话」的循环。

### 精准 DOM 拾取与截图

进入 Design Mode 后，鼠标悬停任意 UI 元素高亮，按键操作：

| 按键 | 动作 | 产物 |
|------|------|------|
| `C` | Copy markup | 该元素 HTML + CSS 进剪贴板，贴给 Agent 当上下文 |
| `S` | Screenshot | 元素裁剪截图，直接附进 prompt |

实测案例：点选一个按钮 → `C` 复制 → 告诉 Codex「删除这个元素」→ 元素以 attached browser context 形式进入对话 → Codex 下一轮即删掉。

### 批量视觉化注释（Annotations）

```
Design Mode 开启
   │
   ├─ 标注 1（intent）：把这个数字改成 1000
   ├─ 标注 2（question）：这拼写对吗？
   ├─ 标注 N …
   │
   ▼
一次性打包发送 → 指定任一 Agent（即使它还在跑）
   │
   ▼
Agent 下一轮统一回应（Codex 确认拼写无误 + 改值 + 删按钮 ✅）
```

注释分两类：**intent**（描述要改什么）与 **question**（提问）。Up 主实测 Codex 正确回答了「Tokens on watch list 拼写无误」并把数值从 100 改到 1000。

### 最佳实践

- ✅ UI 调优走「拾取 + 注释批量发」，比口头描述坐标/类名精确得多
- ✅ 优先对纯 HTML 产物用 Design Mode；Artifact 托管页有时拾取受限
- ❌ 不要用画笔在截图上画完不发——annotation 不发送不产生任何效果

---

## 远端工作流、文件管理与会话持久性

### 自动识别 SSH 主机

Orca 自动扫描本机 `~/.ssh/config`，把已有主机（实测：Up 主的 DGX Spark「Hibana」）列为可用 target，测试连接即成功。远端 Worktree 支持完整文件编辑、git、终端，断线自动重连 + 端口转发。

注意：远端 Worktree 不能直接访问本地目录——要么 clone 公开 repo，要么用远端本地路径。Up 主顺手新加了一台主机，同一专案内就有了跑在 DGX Spark 上的 Worktree。

### 原生文件拖曳注入

内建文件树支持 Markdown 渲染预览、PNG 直接查看；把图片/资产**拖曳到 Agent 终端视窗**即作为对话输入。实测把 logo 图拖进 Claude 会话，Claude 把 logo 加进了 UI。

### 会话持久性（Persistent Sessions）

完全关闭 Orca 再启动：所有 Worktree 终端进程、内建浏览器状态（含当时打开的页面）原样恢复。与 Herder 同级的体验保障。

### 远端工作流能力对照

| 能力 | Orca 实测 | 传统做法（ssh + tmux） |
|------|-----------|------------------------|
| 主机发现 | 自动扫 ssh config | 手动维护别名 |
| 远端 Worktree | UI 内一键建/切 | 手动 `git worktree add` + cd |
| 断线恢复 | 自动重连 | tmux attach |
| 手机监控 | Mobile companion（iOS/Android，cloud/ relay） | 无 |
| 会话持久 | 重启全恢复 | tmux 常驻（机器重启丢） |

### 最佳实践

- ✅ 算力大的远端盒子（DGX Spark 类）跑重任务，本地 Orca 做驾驶舱
- ✅ 敏感目录不要进自动扫描范围——先审一遍 ssh config 再装
- ❌ 不要假设远端 Worktree 能读本地文件，路径不跨机

---

## 验证与勘误记录

用户附带的 Content Insights 与视频口播存在同源 ASR 错名（同一转写管道），以下为对照官方来源的逐项验证（2026-09-20）：

| 视频声称（Insights/口播） | 验证结果 | 权威来源 |
|---------------------------|----------|----------|
| 仓库在 `github.com/stably/orca` | ❌ 实为 **`github.com/stablyai/orca`**（org 名 `stablyai`） | 视频描述 timestamps 区链接 + GitHub 实抓 |
| 官网 `orca.dev` | ❌ 实为 **`onorca.dev`** | 视频描述链接 + README `Download Orca → onorca.dev/download` |
| 现用工具「Herdr」 | ❌ 实为 **Herder**（transcript 全文多处） | webReader transcript |
| 对比过的终端工具「T3」 | ⚠️ 未找到权威仓库归属，保留口播原名不判 | web_search 无唯一命中 |
| 开源、MIT、YC-backed | ✅ | GitHub LICENSE + README + 官网「Backed by Y Combinator」 |
| 支持 27 个 Agent | ✅ | 官网 FAQ 对比表「27 supported agents」 |
| Worktree 各自独立分支 | ✅ | README「Parallel Worktrees … each in its own isolated git worktree」 |
| Design Mode 点元素发 HTML/CSS + 裁剪截图 | ✅ | README feature 表原文一致 |
| `C` 复制 / `S` 截图快捷键 | ✅（transcript 实测口播） | webReader transcript |
| SSH 主机自动发现 | ✅ | README「SSH Worktrees … auto-reconnect」+ transcript |
| 会话持久化 | ✅ | transcript 实测重启恢复 |

> 纠错最高权威 = 视频描述 timestamps 区的官方链接（`stablyai/orca`、`onorca.dev`），Insights 错名与 transcript 同源，冲突时以描述链接为准。

---

## 全文总结与行动建议

### 一句话定位

Orca 不是又一个终端多工器，而是**围绕 Git Worktree 组织的平行 Agent 舰队驾驶舱**：终端分割、看板、Dashboard、Design Mode、SSH 远端、会话持久全部内建，且完全开源（MIT）。

三个核心优势：

1. **工程隔离极佳** — Worktree 原生机制从根上解决多 Agent 平行编辑冲突
2. **前端闭环高效** — Design Mode 拾取 + 批量注释，把「口述改 UI」变成「点选改 UI」
3. **体验完整** — SSH 整合、跨模型看板、手机 companion、重启全恢复

Up 主结论：第一印象非常正面（流畅无卡顿、有点像 Codex app），功能比 T3 等同类多；但他自认对 Herder「忠诚/懒惰」，从 Open Claw 换到 Hermes Agent 花了整整一个月，切换意愿偏保守。

### 行动建议

- ✅ **评估硬件配置**：多 Worktree + 多终端 + 内建 Chromium 同开，RAM 充足再上
- ✅ **循序导入**：先用于高差异场景——多模型平行比对（Model Shootout）与前端 UI 视觉化调整，再评估全面迁移
- ✅ **装完先翻 settings**：Agent Dashboard（实验性）默认不开，需手动加
- ❌ 不要因为「大家都在推荐」就立刻弃用现役工具——Up 主的保守切换策略对重度工作流用户反而是对的

---

## 参考资料

- [Testing Orca: Installation, Setup, Worktrees, & Design Mode（Tonbi's AI Garage, 2026-09-14）](https://www.youtube.com/watch?v=Tg-6_3iFYPs)
- [stablyai/orca — GitHub 仓库](https://github.com/stablyai/orca)
- [Orca 官网（onorca.dev）](https://www.onorca.dev/)
- [Orca Design Mode 文档](https://www.onorca.dev/docs/browser/design-mode)
- [Orca SSH Worktrees 文档](https://www.onorca.dev/docs/ssh)
- [Hermes Agent](https://github.com/NousResearch/hermes-agent)

## 相关笔记

- [[Claude Code Hooks - 确定性自动化防线]]
- [[GitHub 开源周选 20 项目 - ManuAGI #293]]

---

*文档生成时间：2026-09-20；基于 Orca 2026-09 时点版本（GitHub 71.6k stars），项目迭代极快（daily ships），数据有保鲜期*
