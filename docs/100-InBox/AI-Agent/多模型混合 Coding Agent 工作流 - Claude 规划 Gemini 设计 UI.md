---
title: 多模型混合 Coding Agent 工作流 - Claude 规划 + Gemini 设计 UI
aliases:
  - Claude Plans Gemini Designs
  - Multi-Provider Coding Agent Workflow
tags:
  - ai-coding
  - workflow
  - anthropic/opus
  - google/gemini
  - status/active
  - type/learning-note
source:
  - "https://www.youtube.com/watch?v=iICZTWcryac"
author: Cole Medin
created: 2026-06-06
updated: 2026-06-06
description: 利用 Opus 4.8 推理 + Gemini 3.5 Flash UI 设计的多步骤 Coding Agent 工作流，通过 handoff 文档实现跨模型协作
level: intermediate
stars: 4
---

# 多模型混合 Coding Agent 工作流 - Claude 规划 + Gemini 设计 UI

> Cole Medin 演示了一个混合 Anthropic Claude Opus 4.8 和 Google Gemini 3.5 Flash 的 AI 编码工作流。核心理念：**每个步骤用最擅长的模型**，通过 handoff 文档串联，实现单次执行构建完整全栈应用。

## 背景：两款新模型各自的优势

2026 年 5 月，两大厂商密集发布新模型：

| 模型 | 发布日期 | 核心优势 | 弱点 |
|------|----------|----------|------|
| Gemini 3.5 Flash | 2026-05-19 (Google I/O) | UI 生成质量极高，速度快，价格极低 ($1.50/M input) | 页面文案容易幻觉，推理能力弱于 Opus |
| Claude Opus 4.8 | 2026-05-28 | 推理能力最强，适合规划/集成/复杂工程 | 前端 UI 生成本身不如 Gemini 美观 |

Gemini 3.5 Flash 一次生成的 UI 质量已明显优于 Claude Code、Lovable 等工具的默认输出。但 Cole Medin 发现：**UI 好看但内容（页面文案、数据）经常是幻觉编造的**。

---

## 核心设计思想

### 为什么要混合 Provider？

```
┌─────────────────────────────────────────────────────┐
│              混合 Provider 工作流                      │
├──────────────┬──────────────┬───────────────────────┤
│   Sonnet     │   Opus 4.8   │  Gemini 3.5 Flash     │
│  (探索/验证)  │ (规划/集成/修复)│   (UI 设计)            │
│   便宜快速    │   推理最强    │   美观 + 极便宜         │
└──────┬───────┴──────┬───────┴───────────┬───────────┘
       │              │                   │
       ▼              ▼                   ▼
  context.md      plan.md            UI 代码
       │              │                   │
       └──────┬───────┘                   │
              ▼                           │
         plan.md (Section A) ──────────────┘
              │
              ▼
         集成 → 验证 → 修复 → 部署 → 冒烟测试
```

**两个设计理由：**

1. **技术限制**：无法在单个对话窗口内切换 provider（Claude Code 不能中途切到 Gemini）
2. **单一职责**：即使是最强的 Opus，同时做探索+规划+UI+集成也会崩溃。**每步聚焦一个任务 = 最佳结果**

### Handoff 文档机制

每个步骤是**独立的 coding agent session**，步骤间通过 **Markdown handoff 文档**通信：

```
Step 1 (Sonnet) ──→ context.md ──→ Step 2 (Opus)
                                    │
                                    ▼
                                plan.md ──→ Step 3 (Gemini)
                                                │
                                                ▼
                                            ui-summary.md ──→ Step 4+ (Opus)
```

- 每步输出一个 markdown 文档，下一步读取后执行
- 大量 prompt/context engineering 确保文档传递有效
- 优点：可在任意步骤插入人工验证

---

## 工作流步骤详解

### Step 1: Exploration（Sonnet）

**输入**: spec.md（规格文档）
**输出**: context.md
**工具**: Claude Code + `/model sonnet`

探索步骤不需要强推理，用 Sonnet 节省 token 并加速。输出包含：
- 仓库状态（greenfield 或已有模板）
- 框架推荐
- 品牌素材
- 环境变量需求
- 数据层概况

> 💡 spec.md 是整个工作流的入口。推荐为完整应用编写详细规格文档。

### Step 2: Planning（Opus 4.8）

**输入**: context.md
**输出**: plan.md
**工具**: Claude Code + `/model opus`

plan.md 是三段式规格文档：

| Section | 内容 | 说明 |
|---------|------|------|
| A | Site Content & Intent | 语音/语调、页面文案、信息结构 |
| B | Integration Scope | 后端 API、数据库模型、认证 |
| C | Deployment Plan | 部署平台、配置 |

⚠️ **关键设计**：Section A 中**不要描述 UI 结构**（如"用网格布局"或"两列设计"）。让 Gemini 自主决定 UI 怎么做。Cole Medin 测试发现：当 Opus 在 plan 中规定 UI 结构后，Gemini 的输出质量显著下降 — 这证明了 Gemini 确实更擅长前端设计。

### Step 3: Design（Gemini 3.5 Flash）

**输入**: plan.md（仅读取 Section A）
**输出**: UI 代码 + ui-summary.md
**工具**: Pi (OpenRouter) 或 Google Antigravity

Gemini 只负责 UI 层。通过 OpenRouter 配置 Pi 使用 Gemini 3.5 Flash：

```bash
# Pi 中切换模型
/model gemini-3.5-flash

# 调用 design skill
/frontend-mix-design path/to/plan.md
```

即使是不支持 skill 的 agent，也可以直接让它读取 skill 文件内容并执行指令。

### Step 4-8: 后续步骤（Opus/Sonnet 混合）

所有后续步骤重复同样的 handoff 模式：

| 步骤 | 模型 | 任务 | 输出 |
|------|------|------|------|
| 4. Integrations | Opus | 后端 API、数据库、认证 | 集成代码 |
| 5. Validation | Sonnet | 单元测试、lint 检查 | 测试报告 |
| 6. Fix | Opus | 修复 Validation 发现的问题 | 修复代码 |
| 7. Deploy | Opus | 自动部署（Vercel/DigitalOcean 等） | 部署 URL |
| 8. Smoke Test | Agent Browser | 浏览器自动化验证网站功能 | 测试结果 |

---

## 工具链概览

| 工具 | 角色 | 说明 |
|------|------|------|
| **Claude Code** | Anthropic 的 CLI coding agent | 支持 `/model` 切换模型，支持 skill |
| **Pi** | 支持多 provider 的 CLI agent | 通过 OpenRouter 接入 Gemini |
| **Google Antigravity** | Google 官方 Gemini coding agent | 原生支持 Gemini 3.5 Flash |
| **Archon** | 开源 workflow engine（Cole Medin 作品） | 用 YAML 定义多步骤工作流，自动编排所有 skill |
| **OpenRouter** | 多模型 API 路由 | 统一接入 Anthropic / Google / 其他模型 |

### Archon：一键执行整个工作流

Archon 将上述所有 skill 打包为一个 YAML workflow，agent 自动按序执行所有步骤：

```yaml
# Archon workflow 示例（概念）
steps:
  - skill: frontend-mix-explore
    model: sonnet
  - skill: frontend-mix-plan
    model: opus
  - skill: frontend-mix-design
    model: gemini-3.5-flash
    provider: openrouter
  - skill: frontend-mix-integrate
    model: opus
  # ... 后续步骤
```

GitHub: [coleam00/archon](https://github.com/coleam00) — 22K+ stars

---

## 最佳实践总结

✅ **每步一个模型，单一职责** — 不要让一个模型同时做多件事
✅ **Handoff 文档是核心** — 投入 prompt engineering 确保 step 间通信有效
✅ **规划中不规定 UI 结构** — 让 Gemini 发挥前端设计优势
✅ **探索/验证用便宜模型** — Sonnet 足够，省 token
✅ **支持任意步骤人工验证** — handoff 文档让你可以在任意节点介入检查
✅ **实验性灵活** — 几分钟就能替换某步的模型/provider，找到最佳组合

❌ 不要在一个 session 内让 Opus 处理整个全栈应用 — 非平凡应用必崩
❌ 不要在 plan 中规定 UI 布局细节 — 会压制 Gemini 的设计优势
❌ 不要跳过 handoff 文档直接拼接 prompt — 文档格式经过精心 engineering

---

## 适用范围

- ✅ PoC（概念验证）
- ✅ MVP（最小可行产品）
- ✅ 非平凡全栈应用的单次构建
- ❌ 非常复杂的全栈应用（需要多轮迭代）
- ❌ 需要持续维护和迭代的长期项目

---

## 参考资料

- [Claude Plans, Gemini Designs: The Workflow to Build Beautiful Full-Stack Apps](https://www.youtube.com/watch?v=iICZTWcryac) — 本笔记来源视频
- [Claude Opus 4.8 发布公告](https://www.anthropic.com/news/claude-opus-4-8) — Anthropic 官方
- [Gemini 3.5 Flash 发布](https://blog.google/innovation-and-ai/models-and-research/gemini-models/gemini-3-5/) — Google 官方
- [Archon 官网](https://archon.diy/) — Cole Medin 的开源 workflow engine
- [Cole Medin GitHub](https://github.com/coleam00)

## 相关笔记

- [[Archon - AI Coding Workflow Engine]]
