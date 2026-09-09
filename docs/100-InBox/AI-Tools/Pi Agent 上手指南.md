---
title: Pi Agent 完整上手指南 — 极简可扩展的 AI Coding Agent
aliases: [Pi Agent, pi coding agent, Pi Coding Agent]
tags:
  - ai-coding-agent
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=Wah1vdFE92k"
  - "https://pi.dev/"
author: 畅的科技工坊（视频讲解）/ Pi Agent 官方（pi.dev）
created: 2026-08-23
updated: 2026-08-23
description: Pi Agent 极简 Agent Harness：4 个原生工具、Extension/Package 扩展体系、AGENTS.md 分层上下文、树状 Session Tree 与赛车游戏实战
level: beginner
stars: 4
---

# Pi Agent 完整上手指南 — 极简可扩展的 AI Coding Agent

> Pi Agent（pi.dev）是一个终端上的极简 Agent Harness：默认只给 4 个原生工具（读/写/改文件 + 执行命令），没有计划模式、子代理、MCP，连权限弹窗都没有。所有能力靠 Extension / Skill / Package 按需拼装，会话（Session）是树状结构，可以回到任意历史节点分叉新方案。OpenClaw 底层就是用 Pi 的 SDK 搭的。
>
> 视频作者用一个赛车游戏 Demo 完整演示了「快速模型规划 → 强模型 review → 生成代码 → /tree 回溯分支做伪 3D 版」的工作流，全程花费约 $0.15。

> [!note] 字幕校正说明
> 原视频字幕为 YouTube 自动生成，音译/同音错误极多。本笔记已对照 pi.dev 官方信息校正：
> 「寫稿 login」→「/login」、「繪畫」→「会话 (session)」、「全線彈窗」→「权限弹窗」、「DeepSync / deep sea wave 4」→「DeepSeek V4」、「板链/百恋」→「百炼 (Bailian，阿里 DashScope)」、「毛屁房」→「毛坯房」、「插畫」→「插话 (steering)」、「steal」→「skill」、「py.py 文件夹」→「.pi 文件夹」。

## 目录

- [1. 核心定位与设计哲学](#1-核心定位与设计哲学)
- [2. 安装](#2-安装)
- [3. Extension 与 Package：两种扩展层级](#3-extension-与-package两种扩展层级)
- [4. 配置架构：~/.pi/agent/ 目录](#4-配置架构piagent-目录)
- [5. Sandbox 安全扩展](#5-sandbox-安全扩展)
- [6. 四种运行模式](#6-四种运行模式)
- [7. 模型接入与切换](#7-模型接入与切换)
- [8. 上下文工程：AGENTS.md 分层与信任机制](#8-上下文工程agentsmd-分层与信任机制)
- [9. Session Tree：树状会话管理](#9-session-tree树状会话管理)
- [10. 实战：赛车游戏 Demo 与成本数据](#10-实战赛车游戏-demo-与成本数据)
- [11. 总结与选型建议](#11-总结与选型建议)
- [参考资料](#参考资料)
- [相关笔记](#相关笔记)

---

## 1. 核心定位与设计哲学

与 Claude Code、Codex 这类「功能焊死在里面的成品」不同，Pi Agent 走的是「毛坯房」路线：默认只提供 4 个原生工具——读文件、写文件、改文件、执行命令。没有计划模式（plan mode）、没有子代理（sub-agents）、没有 MCP、没有权限弹窗。

设计哲学一句话：**模型会越来越强，Harness 这层应用应该越薄越好**。Pi 只做最少的活，其余一切做成积木（Extension、Skill、Package），想要什么功能直接让 Pi 自己写，写完 `/reload` 就生效——它甚至能修改自己。

两个最具辨识度的能力：

1. **运行中插话（Steering）**：Agent 执行中途可以随时插话，当前工具跑完后立刻转向（官方：`Enter` 发送 steering 消息打断后续工具，`Alt+Enter` 排队等 Agent 干完再发）。
2. **树状会话（Session Tree）**：会话不是线性聊天记录，而是树。可以回到历史任意节点，从那里分叉出另一条方案，做多路径探索时不污染原分支。

### Pi 与主流 Agent Harness 对比

| 维度 | Pi Agent | Claude Code / Codex 等重型工具 |
|------|----------|-------------------------------|
| 定位 | 极简 Harness（毛坯房） | 功能完整的成品 |
| 原生工具 | 仅 4 个（读/写/改/执行） | 大量内置（plan mode、子代理、MCP…） |
| 能力扩展 | Extension/Skill/Package 拼装，热重载 | 等厂商发版 |
| 自我修改 | 支持（让 Pi 写代码改自己） | 不支持 |
| 会话结构 | 树状（Session Tree） | 线性为主 |
| 安全确认 | 原生无，装 sandbox 扩展补 | 内置权限弹窗 |
| 典型用法 | 本地开发工作流、多方案探索 | 全功能开发环境 |

### 设计哲学示意

```
传统重型 Harness                Pi 薄 Harness
┌──────────────────┐           ┌──────────────────┐
│  厂商决定的一切    │           │  4 个原生工具     │
│  plan / 子代理    │           │  (读/写/改/执行)  │
│  MCP / 权限弹窗   │           │       +          │
│  功能焊死，等更新  │           │  按需拼装的积木:   │
│                  │           │  ext + skill +   │
│                  │           │  pkg, 写完即生效  │
└──────────────────┘           └──────────────────┘
   模型变强 → 应用仍重             模型变强 → 应用更薄
```

---

## 2. 安装

视频演示了两种方式，命令从 pi.dev 首页复制：

```bash
# 方式 1：npm 安装
npm install -g <pi 官方包>   # 具体包名以 pi.dev 首页为准

# 方式 2：官方脚本（curl）
# 同样见 pi.dev 首页

# 验证安装
pi --version
```

安装完成后 `pi` 命令进入交互模式。官方也支持从 npm 或 git 安装社区 Package：

```bash
pi install npm:@foo/pi-tools
pi install git:github.com/badlogic/pi-doom
```

✅ 建议：安装后先跑 `pi --version` 确认，再进配置环节；具体安装命令以 pi.dev 首页实时版本为准。

---

## 3. Extension 与 Package：两种扩展层级

这两个概念不是同一层的东西：**Extension 是运行时的功能单元，Package 是分发和组织的容器**。

### Extension（运行时扩展）

- 被 Pi 加载并执行的代码，通常是 TypeScript / JavaScript（或带入口的小目录）
- 拿到 Extension API 后可以：
  - 注册新工具给模型使用
  - 监听事件、拦截高危操作（如 bash 执行危险命令前先拦截确认）
  - 注册 slash command
  - 改变终端里的交互和显示（状态栏、overlay 等）
- 适合**本地开发的单点增强**，比如写一个安全检测插件

### Package（功能包/分发容器）

- 通过 `package.json` 里的 Pi 配置或约定目录，把 Extension、Skill、Prompt Template、Theme 及 npm 依赖组织在一起
- 面向**团队复用和社区发布**：一个 DevOps toolkit 可以同时带 2 个 extension、1 个 skill、1 个 CI review prompt 和所需依赖

### 两者关系对比

| 维度 | Extension | Package |
|------|-----------|---------|
| 本质 | 运行时功能单元 | 分发/组织容器 |
| 形态 | TS/JS 代码 | package.json + 约定目录 |
| 关注点 | 「让 Pi 多一种运行时能力」 | 「把能力、规则、依赖打包，便于安装/版本管理/分享」 |
| 数量关系 | 一个 Package 可包含 0 到多个 Extension | — |
| 典型场景 | 拦截危险命令、自定义终端交互 | DevOps toolkit、团队共享配置 |

一句话总结：**Extension 负责能力，Package 负责打包**。

---

## 4. 配置架构：~/.pi/agent/ 目录

所有配置文件都在家目录 `~/.pi/agent/` 下：

```
~/.pi/agent/
├── models.json         # 你的自定义模型配置（手写）
├── models-store.json   # 官方模型目录缓存（自动生成，勿手改）
├── auth.json           # 密钥保险箱：API key / OAuth 令牌
├── settings.json       # 全局偏好：默认 provider/模型/主题/推理等级
├── AGENTS.md           # 全局智能体指令（上下文工程）
├── sessions/           # 所有会话记录（树状归档，分支全记录在案）
└── skills/             # 技能包目录（按需加载）
```

### 各文件职责

| 文件 | 职责 | 要点 |
|------|------|------|
| `models.json` | 自定义第三方模型 | 配 baseUrl、模型 ID、API key（支持环境变量或 bash 命令引用）、多模态、上下文窗口等 |
| `models-store.json` | 官方自带模型目录缓存 | 自动生成，删了会重建；支持热加载，**千万不要手动改** |
| `auth.json` | 密钥保管库 | 存各模型 API key / OAuth 登录令牌；交互模式下 `/login` 登录订阅服务自动写入 |
| `settings.json` | 全局配置 | 默认模型提供商、默认模型、主题、推理等级等；可用 `/settings` 直接改 |
| `sessions/` | 会话归档 | 树状结构，所有分支记录在案，随时回溯任意节点 |
| `skills/` | 技能包 | 指令+工具打包的模块，用到才进上下文，不用时只占名称和描述 |

### models.json 自定义模型示例（示意，字段以官方文档为准）

```json
{
  "bailian": {
    "baseUrl": "https://dashscope.example/v1",
    "api": "openai-completions",
    "apiKey": { "env": "DASHSCOPE_API_KEY" },
    "models": [
      {
        "id": "qwen3-max",
        "name": "Qwen3 Max",
        "reasoning": true,
        "multimodal": false,
        "contextWindow": 1000000,
        "maxOutputTokens": 32768,
        "thinkingLevel": "high"
      }
    ]
  }
}
```

关键配置项：`baseUrl`、API 接口类型（多数为 OpenAI compatible）、API key（环境变量 / 直接写 / bash 命令导出）、`models` 列表（每个元素含 ID、名称、是否支持推理、是否多模态、上下文窗口大小、最大输出 token、thinking level 推理级别）。

✅ 改完 `models.json` 后在交互模式执行 `/model` 或 `/reload` 即可热加载，无需重启。
❌ 不要手动编辑 `models-store.json`——它是 Pi 自动生成的离线缓存。

---

## 5. Sandbox 安全扩展

原生 Pi 没有任何安全确认机制（连权限弹窗都没有），跑之前建议先补上安全层。方案：安装社区 `pi-sandbox` extension。

### 安装与配置流程

```bash
# 1. 在 pi.dev → Packages 搜索 sandbox（类型选 extension）
# 2. 安装（视频中演示的命令）
pi install npm:pi-sandbox

# 3. 手工创建全局配置 ~/.pi/agent/sandbox.json
#    （示例配置来自该 package 的说明页，按自己场景微调）
```

`sandbox.json` 可对三类操作做权限审批：

```
sandbox.json 权限面
├── 文件读写（路径级审批）
├── 命令执行（高危 shell 命令拦截）
└── 网络访问（对外部网络的访问控制）
```

### 安全最佳实践

✅ 安装任何社区 Package 前先读它的说明页，确认适合你的场景
✅ 社区 Package **未经官方验证和测试**，务必评估对本机的影响
✅ Sandbox 装好后，文件操作/命令执行会弹出审批（可选 allow for this project）
❌ 不要裸跑无安全机制的 Pi 去执行不可信任务

---

## 6. 四种运行模式

| 模式 | 用法 | 适用场景 |
|------|------|----------|
| Interactive（交互） | 终端输入 `pi` 进入 TUI | 日常写代码、改项目（最常用） |
| Print / JSON | `pi -p "query"`，`--mode json` 输出事件流 | 命令行一次性任务、接脚本和自动化 |
| RPC | JSON 协议走 stdin/stdout（JSONL） | 让其他程序调用 Pi；非 TS 技术栈系统集成 |
| SDK | 直接嵌入自己的应用 | 把 Pi 作为组件集成（OpenClaw 即实例） |

选择示意：

```
你要怎么用 Pi?
├─ 日常开发、人机交互 ──────────► Interactive
├─ 脚本/CI 自动化一次性任务 ────► Print / JSON
├─ 其他语言/系统要调用 Pi ──────► RPC (JSONL)
└─ 自己的 App 里嵌 Agent 能力 ──► SDK
```

视频的全部演示基于 Interactive 模式。

---

## 7. 模型接入与切换

### 三种接入方式

1. **Sign in with account（订阅账号）**：买了 Codex 或 Claude Code 订阅的话，直接用订阅登录，走订阅计划里的 quota 和 token
2. **Sign in with API key**：Pi 集成了 15+ 提供商（Anthropic、OpenAI、Google、Azure、Bedrock、Mistral、Groq、Cerebras、xAI、Hugging Face、Kimi For Coding、MiniMax、NVIDIA、OpenRouter、Ollama 等）。密钥放环境变量即可被自动侦测（如 DashScope、Gemini API key），或在 `/login` 界面选提供商直接输入；也可以配在 models-store 环境变量里显示为 "stored"
3. **models.json 自定义**：对接任意自定义模型平台（视频演示了百炼、NVIDIA），配 baseUrl + key + models 列表

### 切换与热加载

```bash
# 交互模式内
/login     # 进入提供商/密钥管理界面
/model     # 列出所有可用模型（官方 models-store + 自定义 models.json），
           # 支持关键字过滤搜索；列表热更新，改完 json 实时可见
/reload    # 热重载配置
```

官方快捷键：`Ctrl+L` 快速切模型，`Ctrl+P` 在收藏模型间循环。

### 模型接入方式对比

| 方式 | 前提 | 适合 |
|------|------|------|
| 订阅账号登录 | 已有 Codex / Claude Code 等订阅 | 想复用订阅 quota |
| API key | 提供商在集成列表内 | 主流云厂商模型 |
| models.json | 任意 OpenAI 兼容端点 | 自定义/第三方平台（百炼、NVIDIA 等） |

---

## 8. 上下文工程：AGENTS.md 分层与信任机制

Pi 和很多 Harness 一样读取 AGENTS.md（自动字幕误作 agent.md/agent.mb），但它是**分层加载**的：从项目目录逐级向上查找，越接近项目目录优先级越高。

### 优先级（高 → 低）

| 优先级 | 文件 | 建议放什么 |
|--------|------|-----------|
| 1 | `项目目录/AGENTS.override.md` | 临时任务覆写；存在时**完全覆盖**同目录 AGENTS.md |
| 2 | `项目目录/AGENTS.md` | 本项目的技术栈、架构约束、代码风格 |
| 3 | 各级父目录/AGENTS.md | 跨项目共享的团队规范 |
| 4 | `~/.pi/agent/AGENTS.md` | 个人全局编码习惯 |

视频用 robot1（AGENTS.md）vs robot2（AGENTS.override.md）实验验证：同目录下 override 完胜；项目级胜过全局。

### Trust（信任）机制——容易搞混的点

首次在项目目录启动 Pi 会问是否信任该文件夹。**信任与否不影响 AGENTS.md 的加载**（选 Do not Trust 也照样读 AGENTS.md）；它影响的是项目目录下 `.pi/` 文件夹里的内容：

```
启动项目时 Pi 问: Trust this folder?
├─ Trust      → 加载 项目/.pi/ 下的 settings、skills、
│               extensions、packages 等全部资源
└─ Don't Trust → 项目/.pi/ 下所有资源不加载
                （AGENTS.md 照常加载，不受影响）
```

### 上下文工程工具箱（官方）

除 AGENTS.md 外，Pi 还提供：

- **SYSTEM.md**：按项目替换或追加默认 system prompt
- **Compaction**：接近上下文上限时自动摘要压缩旧消息，可经 Extension 定制（按主题压缩、代码感知摘要、换摘要模型）
- **Skills**：按需加载的能力包，渐进披露（progressive disclosure）不破坏 prompt cache
- **Prompt Templates**：Markdown 复用提示词，`/name` 展开
- **Dynamic Context**：Extension 可在每轮前注入消息、过滤历史、实现 RAG 或长期记忆

✅ 全局放编码习惯，项目根放技术栈约束，临时排错用 override 覆写
✅ 项目专属的 extension/skill/settings 放 `项目/.pi/`，配合 Trust 机制控制加载

---

## 9. Session Tree：树状会话管理

传统 Agent 会话是一条线性聊天记录，想回到中间某步继续探索，只能复制重开或手动整理上下文。Pi 的 Session 是**树状**的：每次对话、工具调用和结果都存成 JSONL 记录，带 ID 或命名标识，所有分支都在同一个文件里，不丢任何探索路径。

### 启动与恢复

```bash
pi -c          # 继续最近一个 session
pi -r          # 从历史 session 列表中选择继续
pi -n new-task # 新建 session 并命名 new-task（口播还原，以 pi --help 为准）
```

### /tree vs /fork

| 命令 | 行为 | 原 session | 适用 |
|------|------|-----------|------|
| `/tree` | 列出当前 session 所有历史节点，选一个回溯，从该节点修改 prompt 衍生新分支 | 新分支仍在**原 session 树内** | 同一 session 内多方案探索、对比迭代 |
| `/fork` | 选定某条历史指令，完整复制出**独立的新 session** | 原 session 保持不动，两条线互不污染 | 方案成熟后独立演化、避免污染主线 |

回溯时 Pi 会询问是否生成摘要（总结之前的分支内容）、用自定义指令总结，或 No summary 直接回到节点。

### 树状分支示意

```
session: racing-car (单文件, 树状)
│
├─● 需求 → 2D 俯视方案 ──→ 生成 2D 版 ✓
│
└─● (回到「回答规划问题」节点, 改 prompt:
    "改用伪 3D 追尾视角 + 固定摄像机")
   └─ 新分支: 伪 3D 方案 ──→ 生成 3D 版 ✓

/ fork 则是整棵拷贝出去另立新 session, 原树不动
```

✅ 官方大量斜杠命令围绕 session 管理，多方案对比/架构重构时善用 `/tree`、`/fork` 做非破坏性分支测试
✅ `/export` 导出 HTML，`/share` 上传 GitHub gist 得到可分享 URL

---

## 10. 实战：赛车游戏 Demo 与成本数据

视频完整演示了用 Pi 从零做一个赛车游戏，核心是展示**「快速模型规划 + 强模型 review 生成 + 树状分支探索」**的工作流。

### 项目准备

```
racing-car/
├── AGENTS.md      # 项目要求: 技术栈、工作风格、
│                  # 遇到问题及时提问、性能/功能优先级
└── .pi/           # 项目级 settings/skills/extensions/packages
                    # (有内容时首启会触发 Trust 询问)
```

### 工作流：Flash 规划 → Pro Review → 生成

```
1. pi -n racing-car-webapp-game   # 新建命名 session
2. /model → 切到 flash (快速模型)
3. 发需求 prompt → 快速生成实现计划
4. /model → 切到 DeepSeek V4 Pro (强推理模型)
5. 让 Pro review 计划 → 提出修改建议和疑问
6. 回答问题 → 让它按回复更新计划
7. 确认执行 → 生成完整代码 (~20 分钟)
8. Sandbox 弹审批 → Allow for this project
```

### 第一版（2D 俯视）实测成本

| 指标 | 数值（视频口播） |
|------|-----------------|
| 输入 token | ~59,000 |
| 输出 token | ~100,000 |
| 缓存命中率 | 99.9%（口播累计缓存 ~9.2M token） |
| 花费 | ~$0.15（约 1 元多人民币） |
| 上下文窗口占用 | ~12.8% |
| 耗时 | ~20 分钟 |

生成结束后 Pi 给出详细报告：遇到什么问题、修复了什么、额外实现了什么。游戏可玩（三关卡、top-down 视角），但摄像机不停转动、方向控制有瑕疵，作者评价不如之前用千问 Qwen 大模型写的版本。

### /tree 分支：改造成伪 3D 版

```
/tree → 回到「回答规划问题」的节点 → No summary
→ 修改 prompt: "伪 3D 追尾视角 + 固定摄像机位"
→ 原 session 树上长出新分支 → 新计划 → 执行 (~30 分钟)
```

第二版效果：第一视角、车辆有近大远小的透视过渡，视角合理可玩；但前方车辆过密遮挡视线、弯道渲染仍需优化——**比第一版明显更好，且原 2D 分支完整保留，可随时跳回**。

这个 Demo 的重点不是游戏本身，而是：树状会话让「改一个早期决策重新生成」变成一次节点回溯，而不是从零重开——大幅省 token、不污染上下文。

---

## 11. 总结与选型建议

Pi Agent 的核心价值：以极简「薄客户端」架构，把上下文控制权与扩展自由度完全交还开发者，用非线性 Session Tree 降低多方案探索的 token 浪费和上下文污染。

### 四条辨识度机制

1. **Extension + Package**：能力可扩展、可打包分享（npm/git）
2. **AGENTS.md 分层**：override > 项目 > 父目录 > 全局，做分层 context engineering
3. **树状 Session 管理**：任意节点伸新分支（`/tree`），或整体拷贝独立 session（`/fork`）
4. **模型随切随用**：`/model` 热切换，规划用便宜快模型、写码用强模型

### 选型决策树

```
你的需求?
├─ 要 IDE 深度集成、开箱即用全功能
│   → Claude Code / Codex 等重型工具
├─ 要极简内核、自己拼能力、贴近本地开发流
│   → Pi Agent
└─ 已用 Pi 且要多方案探索
    ├─ 同 session 内试另一条路 → /tree
    └─ 完全隔离保留原线      → /fork
```

### 上手清单

1. npm / 官方脚本安装，`pi --version` 验证
2. 先装 `pi-sandbox` 并配置 `sandbox.json`（原生无确认机制）
3. `/login` 接模型（订阅 / API key / models.json 自定义）
4. 全局 `~/.pi/agent/AGENTS.md` 写编码习惯；项目根写技术栈约束；临时任务用 `AGENTS.override.md`
5. 多方案探索时用 `/tree` / `/fork` 非破坏性分支

✅ 规划用 Flash 级便宜模型、review 和写码用 Pro 级强模型，配合高缓存命中把成本压到分级别
❌ 别把 API key 硬编码进配置——用环境变量或 bash 命令引用
❌ 社区 Package 未经验证，安装前必读文档

---

## 参考资料

- [Pi Agent 完整上手指南（视频）](https://www.youtube.com/watch?v=Wah1vdFE92k) — 畅的科技工坊
- [Pi Coding Agent 官网](https://pi.dev/) — 官方文档、Package 目录、50+ 示例
- [pi-doom（Pi Package 示例）](https://github.com/badlogic/pi-doom)

## 相关笔记

- [[Claude Code]]
- [[OpenClaw]]
- [[AI Coding Agent]]
