---
title: OpenCode Desktop 入门指南 - 20 分钟掌握 80% 核心功能
aliases: [OpenCode Desktop 教程, OpenCode 入门, Claude Code 开源替代]
tags:
  - opencode
  - ai-coding-agent
  - open-source
  - tutorial
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=npjJyQnLM6U"
  - "https://opencode.ai/"
  - "https://www.skills.sh/"
author: Thanh-y David Nguyen
created: 2026-07-30
updated: 2026-07-30
description: OpenCode Desktop 完整入门教程，涵盖安装、模型配置、Plan/Build 双模式、Skills、MCP Servers、Git 版本控制与 GitHub 集成
level: beginner
stars: 4
---

# OpenCode Desktop 入门指南 - 20 分钟掌握 80% 核心功能

> OpenCode Desktop 是 Claude Code 的 100% 开源替代品，支持几乎任意模型，内置免费模型，多标签页（Tabs）多任务并行。本笔记基于 Thanh-y David Nguyen 的 20 分钟教程，系统拆解从安装到 GitHub 集成的完整工作流。

---

## 目录

- [1. 产品定位：OpenCode vs Claude Code](#1-产品定位opencode-vs-claude-code)
- [2. 安装与基础设置](#2-安装与基础设置)
- [3. 模型配置（Model Providers）](#3-模型配置model-providers)
- [4. Plan/Build 双模式开发](#4-planbuild-双模式开发)
- [5. Git 版本控制（Checkpoint）](#5-git-版本控制checkpoint)
- [6. Slash Commands（斜线指令）](#6-slash-commands斜线指令)
- [7. Skills（技能）](#7-skills技能)
- [8. MCP Servers（外部工具）](#8-mcp-servers外部工具)
- [9. AGENTS.md 记忆与规则](#9-agentsmd-记忆与规则)
- [10. GitHub 集成](#10-github-集成)
- [核心概念对比速查](#核心概念对比速查)
- [参考资料](#参考资料)

---

## 1. 产品定位：OpenCode vs Claude Code

OpenCode 的核心卖点是 **模型自由** —— 不绑定单一 AI 生态，可随时接入新模型（如 Kimi、DeepSeek、Gemini、OpenAI）。当新的旗舰模型发布时，无需等待平台支持，直接切换测试。

### Android vs Apple 比喻

| 维度 | Claude Code（Apple） | OpenCode（Android） |
|------|---------------------|---------------------|
| **模型生态** | 仅 Anthropic 模型 | 几乎任意模型（Kimi/DeepSeek/Gemini/OpenAI/Ollama 本地） |
| **成本** | 较高，依赖 Anthropic API | 免费起步，按需付费，可绑现有 ChatGPT 订阅 |
| **体验** | 深度优化、流畅顺滑 | 高度灵活，偶尔遇到 bug（如 Agent 选项不显示） |
| **锁定风险** | 生态锁定（如 Opus 4.5 曾被下架） | 无锁定，随时换模型 |
| **功能对等** | — | OpenCode 能做 Claude Code 几乎所有事情 |

> 关键结论：学会 OpenCode 等于学会 Claude Code，两者的操作逻辑高度互通，不存在"学错工具"的风险。

### 选型决策树

```
你需要 AI Coding Agent？
├── 预算充足 + 追求极致顺滑 → Claude Code (Apple)
├── 预算有限 / 想免费开始 → OpenCode (Android)
├── 需要本地模型 / 数据隐私 → OpenCode + Ollama
└── 想同时用多个模型对比 → OpenCode（多 Provider）
```

---

## 2. 安装与基础设置

### 安装步骤

1. 访问 opencode.ai，点击下载按钮
2. 如果自动检测的系统不对，滚动页面手动选择对应 OS 版本
3. 安装后打开应用，界面类似常见聊天/Coding Agent

### 推荐 Settings 配置

打开 `File → Settings → Advanced`，启用以下三个选项：

| 选项 | 作用 | 为什么需要 |
|------|------|-----------|
| **File Tree** | 左侧显示项目文件树 | 直观查看项目结构和 AI 创建/修改的文件 |
| **Service Status** | 顶部显示 MCP Server 等服务状态 | 实时监控外部工具连接状态，排错必备 |
| **Show Agent** | 显示 Agent 模式切换按钮 | 启用 Plan/Build 双模式（见第 4 节） |

> Pitfall：开启 Show Agent 后如果按钮没立即出现，是已知 bug。解决：点击右上角按钮 → 新建 session → 回到原 session，Agent 选项就会出现。

---

## 3. 模型配置（Model Providers）

OpenCode 内置免费模型（开箱即用），也可连接付费 Provider 获取更强的模型。

### Provider 选项对比

| Provider | 计费方式 | 特点 |
|----------|---------|------|
| **内置免费模型** | 免费 | 开箱即用，适合入门体验和学习 |
| **OpenCode Zen** | Pay-as-you-go 按量计费 | 官方集中式 API，一个 key 访问 Anthropic/OpenAI/Google 等多家模型 |
| **OpenCode Go** | 订阅制 | 同上，月费固定，适合重度用户 |
| **OpenAI 直连** | 绑定 ChatGPT 订阅 | 已有 ChatGPT Plus/Pro 订阅直接用，比 API 计费更省 |

### ChatGPT 订阅直连技巧

已有 ChatGPT 付费订阅的用户，**不要用 API key**，直接绑定账号：

```
Manage Models → Connect Provider → OpenAI → Connect ChatGPT Subscription
→ 点击授权链接 → 完成
```

这样能用 GPT 模型而无需额外购买 API 额度。

### 模型切换

- 点击顶部模型名称 → 下拉选择
- 或输入 `/model` + Enter 选择
- 点击模型可查看 context window 大小和当前使用率（如 14% used）

---

## 4. Plan/Build 双模式开发

这是 OpenCode 的核心工作流：先规划再执行，避免 AI 跑偏。

### 双模式工作流

```
┌─────────────┐     审核规划      ┌──────────────┐
│  Plan Agent │ ──────────────▶ │ Build Agent  │
│  (规划方案)  │   确认无误后切换   │ (自动写代码)  │
└─────────────┘                  └──────────────┘
       │                                │
       │ 1. 询问需求（App类型/框架）       │ 拆解步骤逐个执行
       │ 2. 产出完整架构规划              │ 完成后可直接运行测试
       │ 3. 用户审核/修改                 │
       └────────────────────────────────┘
```

### 实作示例：会议记录转 PDF Web App

视频中的完整演示流程：

1. **选择 Plan 模式**，设置 Thinking Level 为 High
2. **输入 Prompt**：
   ```
   Build me an app that can turn meeting notes into
   well organized PDFs with AI. The app should use a
   local AI model that is running on my local machine.
   ```
3. **AI 反问**：App 类型（选 Web App）、本地 AI 框架（选 Ollama）
4. **生成规划** → 审核通过 → 切换 Build 模式
5. **Build 自动拆解步骤**，逐个完成代码编写
6. **结果**：可运行的 Web App，支持粘贴/上传会议记录 → 本地 LLM 处理 → 下载 PDF

> 数据隐私亮点：使用 Ollama 本地模型处理，敏感会议记录不出本机。

### Plan vs Build 对比

| 维度 | Plan Agent | Build Agent |
|------|-----------|-------------|
| **职责** | 理解需求、产出架构规划 | 拆解步骤、执行编码 |
| **交互** | 会反问澄清需求 | 收到指令即开始写代码 |
| **产出** | 项目规划文档（供审核） | 实际可运行的项目文件 |
| **适用场景** | 新项目启动、复杂需求 | 确认方案后快速实现 |

---

## 5. Git 版本控制（Checkpoint）

OpenCode 内置 Git 交互，AI 写错代码时可随时回滚。

### 核心操作

```bash
# 让 AI 帮你初始化 Git（也可手动）
> Initialize this project as a Git project using git init

# 安装 Git 本身也可以让 AI 代劳
> Install Git for me
```

初始化后的效果：

- 左侧 File Tree / Changes 面板实时显示 AI 的所有文件改动
- 点击文件可查看 AI 修改的具体代码
- 可以随时回滚到之前的版本（Checkpoint）

### Git 在 OpenCode 中的工作流

```
项目开始
  │
  ├── git init（初始化版本控制）
  │
  ├── AI 编码 → 文件变更实时可见
  │
  ├── AI 写错？ → git 回滚到上个版本
  │
  └── 满意？ → git commit 固化当前状态
```

---

## 6. Slash Commands（斜线指令）

在对话框输入 `/` 查看所有可用指令。

### 常用指令

| 指令 | 功能 | 使用场景 |
|------|------|---------|
| `/compact` | 压缩上下文窗口（Context Window） | 长对话后 AI 开始"遗忘"早期内容时 |
| `/model` | 切换模型 | 需要换模型时 |
| `/terminal` | 打开内置终端 | 需要手动执行命令（如安装 Skills） |
| `/init` | 生成 AGENTS.md 规则文件 | 项目初始化时配置 Agent 行为（见第 9 节） |

### /compact 详解

```
压缩前：Context Window 使用 14%
            │
            ▼  执行 /compact
压缩后：Context Window 使用 5%

效果：保留关键信息，丢弃冗余，AI 记忆力恢复
```

> ChatGPT 长对话的"遗忘"问题，在 OpenCode 中通过 `/compact` 主动管理。开发过程中对话增长极快，建议定期执行。

---

## 7. Skills（技能）

Skills 是给 AI Agent 的**方法论指引** —— "怎么做事"的步骤指南。

### 概念理解

```
Skill = 做事的方法/步骤
（例：知道怎么洗衣服 = 收集脏衣服→选洗涤剂→分类→清洗→晾干）

Tool = 执行动作的实体工具
（例：洗衣机 = 实际执行清洗的设备）
```

### 安装 Skill 示例

视频演示从 skills.sh 安装 "web design guidelines" 来美化 UI：

```bash
# 方式 1：手动安装（推荐，省 token）
# 从 skills.sh 复制安装命令 → 在 OpenCode 中用 /terminal 打开终端 → 粘贴执行

# 方式 2：让 AI 安装（消耗 token）
# 直接把安装命令发给 AI 让它执行
```

安装后，Skill 文件存放在 `agents/skills/` 目录下。然后可以这样使用：

```
> Use the skill to make the UI look more aesthetic and modern
```

AI 会读取 Skill 内容并按指引优化前端。

### Skill 文件位置

```
项目根目录/
└── agents/
    └── skills/
        └── web-design-guidelines/  # 安装的 skill
            └── SKILL.md             # skill 内容
```

---

## 8. MCP Servers（外部工具）

MCP（Model Context Protocol）Servers 为 Agent 提供额外的**外部工具能力**。

### Skill vs MCP Server

| 维度 | Skills | MCP Servers |
|------|--------|-------------|
| **本质** | 方法论/步骤指南（软件） | 实体工具（硬件级能力） |
| **比喻** | 知道洗衣服的步骤 | 拥有洗衣机 |
| **示例** | Web 设计指南 → 更好看的 UI | Document Generator → 导出 PDF/Word |
| **配置方式** | 安装到 agents/skills/ | 配置 opencode.json |
| **关系** | Skill 中可能调用 MCP 工具 | 提供工具供 Agent 调用 |

### 配置 MCP Server

视频演示添加 Document Generator MCP（来自 mcpservers.org）：

```json
// opencode.json（项目根目录）
{
  "mcp": {
    "document-generator": {
      // MCP server 配置（根据具体 server 文档填写）
    }
  }
}
```

**配置方法**：把 MCP Server 文档链接 + 配置说明发给 AI，让它自动创建 `opencode.json`：

```
> Here's a documentation how to add an MCP server to your
  configuration. Create an opencode.json file and add this
  MCP server accordingly.
  [贴入文档链接 + MCP server 链接]
```

> Pitfall：配置完成后 MCP Server 可能不立即显示在 Service Status 中。解决：关闭并重新打开 OpenCode 刷新。

配置成功后，可以让 AI 使用 MCP 工具：

```
> Create a documentation of this entire project using the MCP server
```

AI 会生成 Word 和 PDF 文档，存放在 `generated-documents/` 目录。

---

## 9. AGENTS.md 记忆与规则

`AGENTS.md`（视频中读作 agents.md）是 Agent 的行为规则文件，定义 Agent 的行为边界和自定义规则。

### 创建方式

```
> /init
```

执行后自动生成 `AGENTS.md`，包含基于项目结构的标准规则（如如何运行项目、如何配置 MCP）。

### 自定义规则示例

手动编辑 `AGENTS.md`，添加自定义行为：

```markdown
<!-- AGENTS.md 示例 -->

Always add a joke at the end of every response.
```

保存后，Agent 的每个回复都会附带一个笑话。

### 最佳实践

```
✅ 推荐                          ❌ 避免
─────────────────────────────────────────────
保持规则精简                      塞入大量冗余指令
规则明确、可执行                  模糊的描述
一条规则解决一个行为问题          重复/矛盾的规则
```

> 经验法则：AGENTS.md 内容越少，Agent 行为越一致。规则过多反而会让 Agent 困惑，降低一致性。

---

## 10. GitHub 集成

通过 GitHub CLI 实现 OpenCode 项目到 GitHub 远程仓库的自动推送。

### 前置条件

1. **安装 GitHub CLI**：
   ```bash
   # macOS (Homebrew)
   brew install gh

   # 或从 cli.github.com 下载安装包
   # 也可让 OpenCode 代为安装
   ```

2. **创建 GitHub 账号**（如还没有）

### 连接流程

```
┌──────────────────┐
│ 输入连接 Prompt   │
│ "Connect this    │
│  project to my   │
│  GitHub account" │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐     ┌──────────────────┐
│ AI 触发 OAuth    │────▶│ 浏览器打开授权页  │
│ 登录命令         │     │ 输入授权码        │
└──────────────────┘     └────────┬─────────┘
                                  │
                                  ▼
                         ┌──────────────────┐
                         │ 2FA 手机验证      │
                         └────────┬─────────┘
                                  │
                                  ▼
                         ┌──────────────────┐
                         │ 输入仓库名称      │
                         │ "meeting-notes-  │
                         │  to-pdf"         │
                         └────────┬─────────┘
                                  │
                                  ▼
                         ┌──────────────────┐
                         │ 自动 push 完成    │
                         │ 代码上云 ✓        │
                         └──────────────────┘
```

连接成功后，AI 会自动创建远程仓库并推送所有代码。推送后 Changes 面板清空（因为没有未提交的变更了）。

---

## 核心概念对比速查

### 扩展能力三层架构

```
┌─────────────────────────────────────────────┐
│              AGENTS.md（大脑/规则）           │
│         定义 Agent 行为边界与自定义规则        │
├─────────────────────────────────────────────┤
│  Skills（方法论）    │  MCP Servers（工具）   │
│  "怎么做事"          │  "用什么工具做事"      │
│  - Web 设计指南      │  - Document Generator │
│  - 代码审查规范      │  - Database 连接      │
│  - 部署流程          │  - API 调用           │
├─────────────────────────────────────────────┤
│           Slash Commands（控制指令）          │
│      /compact  /model  /terminal  /init     │
├─────────────────────────────────────────────┤
│            Plan/Build（执行引擎）             │
│         规划 → 确认 → 自动编码                │
└─────────────────────────────────────────────┘
```

### 完整工作流速查

| 阶段 | 操作 | 关键命令/动作 |
|------|------|--------------|
| 初始化 | 安装 + Settings 配置 | 启用 File Tree / Service Status / Show Agent |
| 配模型 | 连接 Provider | OpenCode Zen / OpenAI 直连 / 用免费模型 |
| 开发 | Plan → 审核 → Build | Plan 模式产出规划，Build 模式写代码 |
| 防护 | Git 初始化 | `git init`（可让 AI 执行） |
| 优化 | 压缩上下文 | `/compact` |
| 扩展 | 安装 Skills + MCP | skills.sh + mcpservers.org |
| 规则 | 配置 Agent 行为 | `/init` 生成 AGENTS.md，手动精简 |
| 上云 | GitHub 集成 | 安装 gh CLI → OAuth 授权 → 自动 push |

### 成本优化策略

| 策略 | 节省方式 |
|------|---------|
| 入门用免费模型 | 不花一分钱开始体验 |
| 绑 ChatGPT 订阅 | 复用已有订阅，不额外买 API |
| 手动安装 Skills（非让 AI 装） | 省 token |
| 定期 `/compact` | 压缩上下文 = 降低 token 消耗 |
| 用本地模型（Ollama） | 敏感数据零成本处理 |

---

## 参考资料

- [视频：Learn 80% of OpenCode Desktop in 20 Minutes!](https://www.youtube.com/watch?v=npjJyQnLM6U) — Thanh-y David Nguyen
- [OpenCode 官网](https://opencode.ai/)
- [Skills 市场](https://www.skills.sh/)
- [MCP Server: Document Generator](https://mcpservers.org/de/servers/thiagotw10/document-generator-mcp)

## 相关笔记

- [[Claude Code]]
- [[MCP Model Context Protocol]]
- [[AI Coding Agent 对比]]
