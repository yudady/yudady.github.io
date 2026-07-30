---
title: Oh My Pi (omp) — 把 IDE 编进终端的 AI 编程代理
aliases: [oh-my-pi, omp, terminal coding agent]
tags:
  - coding-agent
  - terminal-tools
  - rust
  - lsp
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=wNw9fKErhdg"
  - "https://github.com/can1357/oh-my-pi"
  - "https://omp.sh/docs"
author: AI Stack Engineer（视频）；Can Bölük（项目作者）
created: 2026-07-30
updated: 2026-07-30
description: 开源终端 AI 编程代理 omp 深度拆解：Rust 进程内执行、Hashline 编辑机制、LSP/DAP 集成，让同款模型 benchmark 通过率从 6.7% 飙升至 68.3%
level: intermediate
stars: 5
---

# Oh My Pi (omp) — 把 IDE 编进终端的 AI 编程代理

> 一个开源终端 AI 编程代理，核心卖点是"同样的模型，换一个运行环境，benchmark 通过率从 6.7% 飙到 68.3%"。秘密在于 Rust 进程内执行 + Hashline 内容哈希编辑机制 + LSP/DAP 深度集成。

## 目录

- [一、工具背景与核心理念](#一工具背景与核心理念)
- [二、安装部署与跨平台兼容性](#二安装部署与跨平台兼容性)
- [三、模型配置与灵活路由机制](#三模型配置与灵活路由机制)
- [四、底层技术创新与架构优势](#四底层技术创新与架构优势)
- [五、高级开发功能与实战测试](#五高级开发功能与实战测试)
- [六、限制、社群反馈与综合评价](#六限制社群反馈与综合评价)
- [参考资料](#参考资料)

---

## 一、工具背景与核心理念

### 项目源起

Oh My Pi（命令 `omp`）是一款开源终端 AI 编程代理（Terminal Coding Agent），由开发者 **Can Bölük** 创建。它 fork 自 Mario Zechner 的极简代理 Pi（`badlogic/pi-mono`），保留其极简哲学的同时叠加了完整的编程工作流。

> **注意**：视频字幕将作者名读作 "Can Balouk"，一些中文摘要写为 "Kan Belaluke"——正确拼写是 **Can Bölük**（见 GitHub LICENSE 署名）。

### 核心论点：Harness 决定模型上限

视频开篇用一个 benchmark 数据抓住注意力：同一款模型（Grok Code Fast 1），不改变权重、不改变 prompt，仅换用 omp 运行环境，测试通过率从 6.7% 飙升至 68.3%。

这证明了一个被忽视的事实：**Agent 架构设计（Harness）对模型发挥起决定性作用**，而不只是模型本身的能力。

### Benchmark 数据一览（README 官方确认）

| 模型 | 指标 | 变化 | 原因 |
|------|------|------|------|
| Grok Code Fast 1 | 通过率 | 6.7% → 68.3% | Hashline 编辑格式消除了空白字符导致的重试循环 |
| Gemini 3 Flash | 通过率 | +5 pp | 超越 str_replace，甚至超过 Google 自家最佳格式方案 |
| Grok 4 Fast | 输出 Token | -61% | 错误 diff 重试循环消失，token 消耗骤降 |
| MiniMax | 通过率 | 2.1× | 通过率翻倍，同权重同 prompt |

测试规模：16 个模型 × 180 个任务 × 3 次运行。

**关键启示**：弱模型在好的 Harness 下突然变得可用，这悄然改变了"你需要哪个订阅"的经济学。

---

## 二、安装部署与跨平台兼容性

### 多平台原生安装

```
┌─────────────────────────────────────────────────────┐
│                  omp 安装方式                        │
├──────────────┬──────────────────────────────────────┤
│ macOS/Linux  │ curl -fsSL https://omp.sh/install|sh │
│ Homebrew     │ brew install can1357/tap/omp         │
│ Bun (推荐)   │ bun install -g @oh-my-pi/pi-coding-agent │
│ Windows      │ irm https://omp.sh/install.ps1 | iex │
│ mise (锁版本)│ mise use -g github:can1357/oh-my-pi  │
├──────────────┴──────────────────────────────────────┤
│ 前置条件：Bun ≥ 1.3.14                              │
│ Windows 无需 WSL，PowerShell 原生运行               │
└─────────────────────────────────────────────────────┘
```

Windows 原生支持是架构决定的，不是补丁——因为底层 Rust 核心不依赖任何 Unix 二进制（详见第四章）。

### Shell 补全

```bash
# zsh
eval "$(omp completions zsh)"
# bash
eval "$(omp completions bash)"
# fish
omp completions fish > ~/.config/fish/completions/omp.fish
```

补全脚本从实时的命令/flag 元数据动态生成，CLI 变化时自动同步，不会漂移。

### 零迁移成本：8 种配置格式自动继承

启动时 omp 自动读取并解析项目中已有的 8 种工具配置文件，无需手动转换：

| 工具 | 配置目录 | 继承内容 |
|------|----------|----------|
| Claude Code | `.claude` | Rules, Skills, MCP |
| Cursor | `.cursor` | MDC rules |
| Windsurf | `.windsurf` | Rules |
| Cline | `.cline` | `.clinerules` |
| Codex | `.codex` | `AGENTS.md` |
| Gemini | `.gemini` | Rules |
| Copilot | `.github/copilot` | `applyTo` 指令 |
| VS Code | `.vscode` | Settings |

视频实测：作者项目里有 Claude Code、Cursor、Copilot 三套配置，omp 全部读取成功，"零文件转换"。

---

## 三、模型配置与灵活路由机制

### 订阅制复用：不花额外 API 费

omp 支持 40+ 个 API 提供商和数百款模型。关键是 **coding plan 支持**——直接 OAuth 登录现有订阅，走既有额度计费，不按 token 付 API 费：

| 订阅类型 | 提供商 |
|----------|--------|
| OAuth 订阅复用 | Claude, OpenAI Codex, GitHub Copilot, Cursor, Gemini Antigravity, Perplexity |
| Coding Plan | Kimi Code, MiniMax, Alibaba, Z.AI/GLM, Xiaomi MiMo |
| 本地模型（无需 key） | Ollama, LM Studio, llama.cpp, vLLM |

### 角色（Role）分工系统

```
┌──────────────────────────────────────────────┐
│            omp Role 路由架构                  │
├──────────┬───────────────┬───────────────────┤
│ Role     │ 用途          │ 推荐模型           │
├──────────┼───────────────┼───────────────────┤
│ default  │ 日常标准编程  │ Claude 3.5 Sonnet │
│ smol     │ 子 Agent 发散 │ 便宜快速模型       │
│ slow     │ 复杂推理解题  │ Claude 3 Opus     │
│ plan     │ 架构规划      │ 高推理模型         │
│ commit   │ 生成 changelog│ —                 │
├──────────┴───────────────┴───────────────────┤
│ Ctrl+P 循环切换 · /model 会话中切换           │
│ 启动参数：--smol, --slow, --plan              │
└──────────────────────────────────────────────┘
```

作者配置实例：Opus → slow，Sonnet → default，便宜模型 → smol。

### 高可用备援

```
请求流 → Provider A (主 Key 1)
           │ 429 Too Many Requests
           ▼
         Key 2 (轮替 + backoff)
           │ 仍失败
           ▼
         Fallback 模型链 → 续传剩余 turn
```

- **多 Key 轮替**：同一 provider 可堆叠多个 API key，带 session affinity 和 per-key backoff
- **Fallback 链**：per-role 或 per-model 配置，主 provider 抛 429 时自动切换备用模型
- **Path-scoped 模型**：可用 `path:` 前缀为特定 repo 钉不同模型集

---

## 四、底层技术创新与架构优势

### Rust 核心与进程内执行（In-Process Execution）

这是 omp 与其他 agent 的根本架构差异。

```
传统 Agent 架构：               omp 架构：
┌─────────────┐                ┌─────────────────────┐
│  Agent 进程  │                │     omp 进程         │
├─────────────┤                ├─────────────────────┤
│             │                │  TypeScript 层       │
│  调用工具 →  │                │    ↓                 │
│             │                │  Rust 核心 (~55k行)  │
│  fork/exec  │                │  ┌─────────────────┐│
│  ├→ rg 进程  │                │  │ ripgrep (内置)  ││
│  ├→ find 进程│                │  │ file walker     ││
│  └→ bash 进程│                │  │ brush (bash)    ││
└─────────────┘                │  │ tree-sitter     ││
                               │  │ syntect (高亮)  ││
每次工具调用 = 进程创建开销      │  │ tiktoken (BPE) ││
                               │  └─────────────────┘│
                               └─────────────────────┘
                               每次工具调用 = 纯函数调用
```

Rust 核心约 55,000 行，编译为 4 个 crate + 1 个 N-API addon，覆盖 5 个平台（linux-x64/arm64, darwin-x64/arm64, win32-x64）。

**关键模块**（从 README 提取的 per-module 细节）：

| 模块 | 功能 | ~LoC | 底层依赖 |
|------|------|-----:|----------|
| shell | 内嵌 bash · 持久 session · 超时/中止 | 3,700 | brush-shell |
| grep | 正则搜索 · 并行/串行 · glob 过滤 | 1,900 | grep-regex |
| keys | Kitty 键盘协议 · 完美哈希查找 | 1,490 | phf |
| text | ANSI 感知宽度 · SGR 保留换行 | 1,450 | unicode-width |
| summary | tree-sitter 结构化源码摘要 | 1,040 | tree-sitter + ast-grep |
| ast | ast-grep 模式匹配与结构重写 | 1,000 | ast-grep-core |
| fs_cache | mtime 键控文件缓存 | 840 | 自研 |
| highlight | 语法高亮 · 11 类 · 30+ 别名 | 470 | syntect |
| pty | 原生 PTY 分配 · sudo/ssh 交互 | 455 | portable-pty |
| glob | glob 发现 · gitignore 尊重 | 410 | ignore |
| tokens | O200k/Cl100k BPE token 计数 | 65 | tiktoken-rs |

### Hashline：内容哈希编辑机制

这是 benchmark 飙升的核心技术。

```
传统编辑方式（str_replace）：          Hashline 编辑方式：
                                       
模型输出：                             每行附带 content hash：
  old_text: "function foo() {"          #abc123  function foo() {
  new_text: "function bar() {"          #def456    return x
                                        #ghi789  }
问题：                               
  - 模型必须逐字重写要改的文本           模型输出：
  - 一个空格错误 → 匹配失败              anchor: #abc123
  - 重试 → 烧 token                      replacement: function bar() {
                                       
                                       优势：
                                        ✓ 只需指定锚点 + 替换内容
                                        ✓ 不重写任何字符
                                        ✓ 文件被外部修改 → hash 不匹配
                                          → patch 被拒绝（防止破坏）
                                        ✓ 消除空白字符战争
```

**实测效果**：这就是 Grok Code Fast 1 从 6.7% → 68.3%、Grok 4 Fast 输出 token 减少 61% 的直接原因。

代码包 `@oh-my-pi/hashline` 独立存在于 monorepo 中（`packages/hashline`）。

### Tree-sitter 结构化读取

`read` 工具不把文件全文塞进 context，而是通过 tree-sitter 生成结构化摘要和定向片段。context 在大项目中保持小体积——这对长 session 尤其关键。

---

## 五、高级开发功能与实战测试

### LSP 语言服务器整合（14 种操作）

重命名不再靠 grep & replace，而是走 IDE 级别的语言服务器协议。

```
视频实测：重命名 parseLinks 函数
  该函数被 5 处导入（含 barrel file re-export）
                   
传统 Agent：              omp (LSP)：
  grep "parseLinks"       workspace/willRenameFiles
  replace 替换              ↓
  → 可能漏掉 re-export    所有 import、alias、re-export
  → 语义错误              作为一次原子工作区操作更新
```

14 种 LSP 操作覆盖：诊断（diagnostics）、引用（references）、符号（symbols）、重命名（rename）、code actions、原始请求（raw requests）。Agent 像编辑器一样看你的代码，而不是当纯文本。

### DAP 偵錯器整合（28 种操作）

这是视频中最令人印象深刻的功能——终端 agent 里罕见的调试器级集成。

```
视频实测：排查死锁（Deadlock）
  两个 async 函数互相等待
  作者只说"工具偶尔卡死"
                   
omp 执行：
  1. 附加调试器到挂起进程
  2. 暂停执行
  3. 遍历调用栈
  4. 读取内存中的实际变量值
  5. 识别死锁两侧
  6. 基于证据给出修复
```

| 语言 | 调试器 | 场景 |
|------|--------|------|
| Go | Delve (dlv) | 协程死锁排查 |
| Python | debugpy | 进程卡死诊断 |
| 原生/C | LLDB | 段错误定位（读指针、步进） |

告别"插 print 猜测"的调试时代。

### Time-Traveling Stream Rules（时空旅行流规则）

```
传统规则系统：                    omp TTSR：
  所有规则常驻 system prompt        规则休眠，不占 context
  → 每轮消耗 token                   ↓
  → context 膨胀                    模型生成中违反规则
                                     ↓
                                   中断流式输出（mid-token）
                                     ↓
                                   注入规则为 system reminder
                                     ↓
                                   从精确位置续传输出
                                   
                                   ✓ 违规前零 context 开销
                                   ✓ 注入在 compaction 后仍生效
```

配置示例（从视频和文档推断）：定义正则规则，模型输出匹配到违规模式时触发中断。

### 审查与协作系统

**Advisor Mode / Reviewer**

```
主 Agent 执行 turn → Advisor（第二个模型，独立 context）
                      ↓
                    读取每个 turn
                      ↓
                    注入 inline notes
                    ├→ 轻提示（aside）
                    ├→ 关注（concern）
                    └→ 硬阻断（hard blocker）
```

`/review` 命令生成专门的 reviewer 子 agent，扫描分支/单次 commit/未提交改动，每个问题评级 P0-P3 并给出 confidence 分数，最终给出"是否可发布"的明确裁决。

**Hindsight 专案记忆**

```
Session N：
  Agent 工作中 → retain 写入事实
  ↓
  Session 结束 → 压缩为心智模型
  ↓
Session N+1 开头：
  加载压缩后的项目心智地图（per-project scope）
  → Agent 无需重新理解项目架构
```

记忆引擎是独立的 SQLite 后端（包 `@oh-my-pi/pi-mnemopi`）。

**Collab 实时协作**

`/collab` 将实时 session 放到 relay 上，返回加密链接 + QR Code：
- 队友用 `omp join` 从终端加入（read-write 配对编程）
- 或浏览器只读观看（`/collab view`）
- 帧 client-side 加密，relay 看不到你的 key

### 其他值得注意的功能

| 功能 | 说明 |
|------|------|
| `omp commit` | 原子化拆分 commit，按依赖排序，拒绝循环依赖，源文件优先于测试/文档 |
| GitHub 文件系统 | PR/issue 是路径：`read pr://1428`，`search` 遍历 diff 像遍历目录 |
| 冲突解决 | merge conflict 变成 URL：写 `@theirs`/`@ours` 到 `conflict://N` 自动解决 |
| AST 预览编辑 | `ast_edit` 返回预览卡片，`resolve` 后原子应用 |
| 浏览器驱动 | Puppeteer 标签页，stealth 默认开启；同一 API 可驱动任何 Electron 应用 |
| ACP 协议 | 在 Zed 编辑器中运行，读写走编辑器的保存路径，工具调用需权限确认 |
| SDK 嵌入 | `@oh-my-pi/pi-coding-agent` 可嵌入 Node/TypeScript 进程 |
| RPC 模式 | `omp --mode rpc` 通过 stdio 的 NDJSON 驱动，适合非 Node 嵌入器 |

### 完整工具清单（32 个）

**文件与搜索**：`read`, `write`, `edit`, `ast_edit`, `ast_grep`, `search`, `find`
**运行时**：`bash`, `eval`（持久 Python + JS cell）, `ssh`
**代码智能**：`lsp`, `debug`
**协调**：`task`（并行子 agent）, `hub`（消息/后台任务）, `todo`, `ask`
**外部**：`browser`, `web_search`, `github`, `generate_image`, `inspect_image`, `tts`
**记忆与状态**：`checkpoint`, `rewind`, `retain`, `recall`, `reflect`, `resolve`

---

## 六、限制、社群反馈与综合评价

### 主要批评：选项过载（Option Overload）

Hacker News 上的主要反馈是设置参数过于庞杂，初始门槛较高。但：

```
上手策略决策树：

  你是首次使用？
    ↓ 是
  用开箱默认值（defaults 足够好）
    ↓ 遇到具体需求
  需要重构？ → 深入 LSP 配置
  需要排查死锁？ → 深入 DAP 配置
  需要管理大项目知识？ → 深入 Hindsight
    ↓
  渐进式采纳，不要一次配所有东西
```

部分用户直接通过 Zed 的 ACP 协议驱动 omp，完全跳过终端 UI。

### 综合评价

| 维度 | 评价 |
|------|------|
| 性能 | Rust 进程内执行 + Hashline，明显更快更省 token |
| 开发工具链 | LSP + DAP 集成弥合了终端 agent 与 IDE 的差距 |
| 迁移成本 | 8 种配置格式自动继承 + 订阅复用，几乎为零 |
| 学习曲线 | 功能多但默认值好，渐进式采纳可行 |
| 开源生态 | MIT 协议，monorepo 结构清晰，Discord 社群活跃 |
| 适用场景 | 终端优先的真实编程工作，非"包装 API 的脚本" |

### 行动建议

1. **立即体验**：已有 Claude/Cursor 订阅可直接绑定，无额外费用
2. **分层配置模型**：贵模型设 slow，平价模型设 smol，兼顾质量与成本
3. **渐进式采纳**：先用默认值，遇到大型重构或死锁排查时再深入 LSP/DAP/Hindsight

---

## 参考资料

- [GitHub: can1357/oh-my-pi](https://github.com/can1357/oh-my-pi)
- [官方文档: omp.sh/docs](https://omp.sh/docs)
- [Provider 文档: omp.sh/docs/providers](https://omp.sh/docs/providers)
- [Benchmark 博文: The Harness Problem](https://blog.can.ac/2026/02/12/the-harness-problem/)
- [原项目 Pi: badlogic/pi-mono](https://github.com/badlogic/pi-mono)
- [YouTube 视频: This Free Terminal Agent Made Me Delete Claude Code](https://www.youtube.com/watch?v=wNw9fKErhdg)

## 相关笔记

- [[Coding Agent 对比]]
- [[Rust 在开发工具中的应用]]
