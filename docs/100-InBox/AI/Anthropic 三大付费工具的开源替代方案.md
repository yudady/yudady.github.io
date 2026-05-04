---
title: Anthropic 三大付费工具的开源替代方案
aliases:
  - 3 Free Tools That Replace Claude Code, Co-work and Design
  - OpenCode OpenWork OpenDesign 开源替代
tags:
  - ai
  - open-source
  - claude
  - developer-tools
  - status/active
  - type/video-note
source:
  - "https://youtube.com/watch?v=IUSZGifKUSM"
author: Panda Making Money
created: 2026-05-04
updated: 2026-05-04
description: Anthropic 推出的 Claude Code、Claude Co-work、Claude Design 的开源替代方案（OpenCode、OpenWork、Open Design），以及 Claude Code 源码泄露事件催生的 OpenClaude
level: intermediate
stars: 4
---

# Anthropic 三大付费工具的开源替代方案

> [!info] 基本信息
> - **来源**: [YouTube - 3 Free Tools That Replace Claude Code, Cowork and Design](https://youtube.com/watch?v=IUSZGifKUSM)
> - **频道**: Panda Making Money
> - **发布日期**: 2026-05-03
> - **时长**: 17 分 51 秒
>
> ⚠️ 视频描述中的 OpenCode 链接（`anomalyco/opencode`）是错误的，实际仓库为 `sst/opencode`。本文已更正。

---

## 目录

- [背景：Anthropic 的产品生态与锁定策略](#背景anthropic-的产品生态与锁定策略)
- [OpenCode — 终端编码代理的开放替代](#opencode--终端编码代理的开放替代)
- [OpenWork — 桌面自动化与任务编排](#openwork--桌面自动化与任务编排)
- [Open Design — Agent 无关的 UI/UX 生成](#open-design--agent-无关的-uiux-生成)
- [OpenClaude — 源码泄露催生的「特殊存在」](#openclaude--源码泄露催生的特殊存在)
- [泄露事件中的隐藏发现](#泄露事件中的隐藏发现)
- [全局对比](#全局对比)
- [参考来源](#参考来源)

---

## 背景：Anthropic 的产品生态与锁定策略

Anthropic 过去一年从「聊天机器人」升级为完整产品生态，推出三大付费工具：

| 工具 | 定位 | 锁定方式 |
|------|------|----------|
| Claude Code | 终端 AI 编码代理 | 仅支持 Anthropic 模型，需 Pro/Max 订阅或按 token 付费 |
| Claude Co-work | 桌面自动化（Beta） | 捆绑自有 agent，无模型选择权 |
| Claude Design | 网页端设计生成工具 | 锁定 Opus 4.7，仅限 Claude.ai 网页，闭源，不可自部署 |

**共同问题**：三个产品都锁定在 Anthropic 的模型、定价和基础设施上。用户不拥有技术栈。

开源社区的回应速度极快 — Claude Design 4月17日上线，Open Design 5天内发布并登上 GitHub 趋势榜首。

---

## OpenCode — 终端编码代理的开放替代

> [!quote] 定位
> Claude Code 的开源替代品。Provider-agnostic（供应商无关），支持任何模型。

**仓库**: [sst/opencode](https://github.com/sst/opencode)
**团队**: SST 和 terminal.shop 背后的开发团队，已有生产级基础设施经验

### 核心特性

- **模型无关**：Claude、GPT、Gemini、DeepSeek、Ollama 本地模型等随意切换
- **双内置 Agent**：
  - `Build` — 完整权限模式，用于主动开发
  - `Plan` — 只读模式，用于探索代码库和规划方案
  - 切换方式：Tab 键
- **原生 LSP 支持**：像 IDE 一样理解代码，而非把代码当纯文本处理（Claude Code 无此功能）
- **Client-Server 架构**：Agent 作为本地 server 运行，可通过 desktop app、mobile 界面或自定义前端驱动
- **OpenCode Zen**：自带模型服务，无需管理 API key

### 关键数字（视频数据，截至 2026-05）

- 154,000 GitHub stars
- 17,804 forks
- 近 1,000 releases

### 架构示意

```
┌─────────────────────────────────────────────┐
│              OpenCode Server                 │
│  ┌──────────┐  ┌──────────┐                 │
│  │  Build   │  │   Plan   │   ← 双 Agent    │
│  │  Agent   │  │  (只读)  │                 │
│  └────┬─────┘  └────┬─────┘                 │
│       │              │                       │
│  ┌────▼──────────────▼─────┐                 │
│  │    LSP Integration      │                 │
│  │  (代码理解，非纯文本)     │                 │
│  └──────────┬──────────────┘                 │
│             │                                │
│  ┌──────────▼──────────────┐                 │
│  │  Provider Router        │                 │
│  │  Claude│GPT│Gemini│Local│                │
│  └─────────────────────────┘                 │
└─────────────┬───────────────────────────────┘
              │
   ┌──────────┼──────────┐
   ▼          ▼          ▼
 Desktop    Mobile    Custom
   App       UI       Frontend
```

---

## OpenWork — 桌面自动化与任务编排

> [!quote] 定位
> Claude Co-work 的开源替代品。内置 Open Code 引擎的 Tauri 桌面应用。

**仓库**: [different-ai/openwork](https://github.com/different-ai/openwork)
**团队**: Different AI（Y Combinator 孵化）

### 核心特性

- **Tauri 构建**（Rust + Web 前端），轻量级原生 macOS/Linux 应用
- **实时执行时间线**：Agent 执行计划以 live timeline 形式渲染，实时可见每一步
- **权限提示机制**：敏感操作（删除文件、API 调用、项目外修改）会暂停并请求确认
- **模板系统**：保存工作流，后续可重放，无需重建上下文
- **Skills 管理器**：UI 内直接安装和管理 OpenCode skill 模块
- **CLI-only 路径**：Open Work Orchestrator 提供 CLI 方式使用

### 架构优势：代理层解耦

```
Claude Co-work:     工具 + Agent 捆绑（一体式）
                    ┌──────────────────┐
                    │  Co-work App     │
                    │  (内置自有 Agent) │
                    └──────────────────┘
                    新能力需自己开发 ✗

Open Work:          工具层 ↔ Agent 层 解耦
                    ┌──────────────────┐
                    │  Open Work UI    │ ──→ 显示层
                    └────────┬─────────┘
                             │
                    ┌────────▼─────────┐
                    │  OpenCode Agent  │ ──→ 智能层
                    └──────────────────┘
                    OpenCode 更新 → OpenWork 自动继承 ✓
```

**关键数字**: ~14,600 GitHub stars（截至视频制作时）

---

## Open Design — Agent 无关的 UI/UX 生成

> [!quote] 定位
> Claude Design 的开源替代品。自带设计智能层，不绑定特定 Agent。

**仓库**: [nexu-io/open-design](https://github.com/nexu-io/open-design)
**团队**: Nexus
**许可**: Apache 2.0

### 核心特性

- **Agent 无关**：自动检测本地已安装的 CLI agent（Claude Code、OpenCode、CodeX、Gemini CLI、GitHub Copilot CLI 等），直接复用
- **结构化设计 Brief**：每次 session 必须指定 surface type、受众、tone、品牌上下文、scale — 没有自由起点，约束是刻意为之
- **五种视觉方向**（无自定义品牌时）：

| 方向 | 风格 |
|------|------|
| Editorial Monocle | 编辑/杂志风格 |
| Modern Minimal | 现代极简 |
| Tech Utility | 技术实用 |
| Brutalist | 粗野主义 |
| Soft Warm | 柔和温暖 |

每种方向锁定确定性（deterministic）的色彩体系、字号比例和间距系统。

- **129 个设计系统**：以可移植 Markdown 格式覆盖 Linear、Stripe、Vercel、Notion、Apple、Tesla、SpaceX、Anthropic、Figma、Supabase 等品牌。切换设计系统，同一 artifact 以该品牌 token 重新渲染
- **多格式导出**：HTML、PDF、PPTX、MP4（Hyperframes 视频模板）
- **沙盒实时预览**：导出前即可查看效果
- **Vercel 一键部署**

### 与 Claude Design 直接对比

```
┌──────────────────┬──────────────────┬──────────────────┐
│       维度        │  Claude Design    │   Open Design     │
├──────────────────┼──────────────────┼──────────────────┤
│ 运行位置          │ 仅 Web (Claude.ai)│ 本地机器          │
│ 模型绑定          │ 仅 Opus 4.7      │ 任意 Agent        │
│ 开源              │ ✗ 闭源           │ ✓ Apache 2.0     │
│ 自部署            │ ✗                │ ✓                │
│ Agent 切换        │ ✗                │ ✓                │
│ 设计系统          │ 有限             │ 129 个品牌        │
│ 输出质量上限      │ ✓ 高（Opus 4.7） │ 取决于底层 Agent  │
└──────────────────┴──────────────────┴──────────────────┘
```

**发展速度**：视频制作时仅上线 5 天，已有 macOS 签名桌面应用、Windows Beta、9 种 UI 语言。

---

## OpenClaude — 源码泄露催生的「特殊存在」

> [!warning] 法律风险提示
> OpenClaude 基于泄露的 Claude Code 源码构建，Anthropic 仍持有知识产权。
> 该项目存在法律风险，使用前需自行评估。

**仓库**: [Gitlawb/openclaude](https://github.com/Gitlawb/openclaude)

### 泄露事件时间线

```
2026-03-26  CMS 配置错误泄露 ~3,000 内部文档
               （含未发布 Mythos 模型规格）
                    │
2026-03-31  Claude Code npm v2.1.88 发布
               npmignore 遗漏一行 → 59.8 MB source map 暴露
               → 指向 Anthropic Cloudflare 上的 zip 压缩包
               → 512,000 行 TypeScript，1,906 个文件
                    │
               Soul Year Labs 实习生 Chaofan Show 凌晨 4:23 发现
               X 帖子获 41,000 赞
               GitHub 备份被 fork 41,500 次
                    │
2026-04-10  Anthropic 曾发律师函威胁 OpenCode 团队
               （使用 Claude Code 内部 API 按订阅价访问 Opus）
                    │
               社区讽刺：威胁开源项目 → 两周后泄露自家全部源码
```

### OpenClaude 的改造

从 Claude Code 源码出发，做了以下改造：

- 移除 Anthropic 锁定
- 支持 200+ 模型（OpenAI 兼容 API）
- Agent 路由：不同任务使用不同模型（成本优化）
- gRPC server 模式（CI/CD 集成）
- VS Code 扩展
- 内置 DuckDuckGo 搜索（非 Anthropic 模型）

**关键数字**: ~25,200 GitHub stars，v0.7

---

## 泄露事件中的隐藏发现

分析泄露的 Claude Code 源码后发现三个未公开功能：

| 功能 | 说明 |
|------|------|
| **Kairos** | 命名自古希腊「关键时刻」概念。完全开发但未发布的自主后台代理模式，可独立运行 session，含「auto dream」内存整合进程（用户空闲时运行） |
| **Undercover Mode** | 隐藏 Anthropic 员工使用 Claude Code 为开源项目贡献代码的事实 |
| **anti-distillation_cc** | 静默注入虚假工具定义到 API 请求中，毒化任何试图蒸馏 Claude Code 行为的竞争模型训练数据 |

---

## 全局对比

| 工具 | 替代对象 | GitHub Stars | 技术栈 | 核心优势 | 风险 |
|------|----------|-------------|--------|----------|------|
| OpenCode | Claude Code | ~154K | TypeScript/Bun | LSP 支持，Provider 无关 | 低 |
| OpenWork | Claude Co-work | ~14.6K | Tauri (Rust) + SolidJS | 实时时间线，权限控制，模板系统 | 低 |
| Open Design | Claude Design | ~16K | TypeScript + Daemon | 129 设计系统，Agent 无关，Apache 2.0 | 低 |
| OpenClaude | Claude Code (fork) | ~25.2K | TypeScript/Bun | 200+ 模型，gRPC，VS Code | **高**（知识产权风险） |

---

## 参考资料

- [OpenCode - GitHub](https://github.com/sst/opencode)
- [OpenWork - GitHub](https://github.com/different-ai/openwork)
- [Open Design - GitHub](https://github.com/nexu-io/open-design)
- [OpenClaude - GitHub](https://github.com/Gitlawb/openclaude)
- [YouTube 视频](https://youtube.com/watch?v=IUSZGifKUSM)

## 相关笔记

- [[AI Coding Agents]]
- [[开源替代方案]]
