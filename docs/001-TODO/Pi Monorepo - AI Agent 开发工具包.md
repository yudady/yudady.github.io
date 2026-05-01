---
title: Pi Monorepo - AI Agent 开发工具包
tags:
  - 工具/coding-agent
  - 工具/llm-api
  - 工具/monorepo
  - 开源项目
source: https://github.com/badlogic/pi-mono
author: Mario Zechner (badlogic)
created: 2026-04-19
updated: 2026-04-19
description: AI agent 工具包，包含 coding agent CLI、统一 LLM API、TUI/Web UI 库、Slack bot、vLLM pods 管理
level: 精读
---

# Pi Monorepo - AI Agent 开发工具包

> [!info] 基本信息
> - **仓库**: https://github.com/badlogic/pi-mono
> - **官网**: https://shittycodingagent.ai
> - **作者**: Mario Zechner (badlogic) — libGDX 框架作者
> - **协议**: MIT
> - **语言**: TypeScript (npm workspaces monorepo)
> - **Node**: >= 20.0.0
> - **工具链**: Biome (lint/format)、TypeScript 5.9、Vitest、Husky
> - **Discord**: https://discord.com/invite/3cU7Bz4UPx

---

## 一句话定位

面向 AI Agent 开发的全栈 TypeScript 工具包，核心产品是一个开源 interactive coding agent CLI（自称 "Shitty Coding Agent"），底层提供统一多 provider LLM API、Agent runtime、TUI/Web UI 组件库。

---

## 核心解决的问题

1. **LLM API 碎片化** — 20+ LLM provider 各有不同 API 格式，pi-ai 提供统一抽象层
2. **Agent 开发复杂度** — tool calling、状态管理、事件流、跨 provider 切换，pi-agent-core 封装这些复杂性
3. **Coding Agent 可观测性** — 开源 session 数据供社区研究，而非依赖玩具 benchmark
4. **TUI/Web UI 复用** — 通用终端 UI 和 Web chat 组件，可独立使用

---

## 包结构

| 包名 | NPM | 说明 |
|------|-----|------|
| **pi-ai** | `@mariozechner/pi-ai` | 统一多 provider LLM API，支持 20+ provider |
| **pi-agent-core** | `@mariozechner/pi-agent-core` | Agent runtime：tool calling、状态管理、事件流 |
| **pi-coding-agent** | `@mariozechner/pi-coding-agent` | Interactive coding agent CLI（核心产品） |
| **pi-mom** | `@mariozechner/pi-mom` | Slack bot，委托消息给 pi coding agent |
| **pi-tui** | `@mariozechner/pi-tui` | 终端 UI 库，差分渲染 |
| **pi-web-ui** | `@mariozechner/pi-web-ui` | AI chat 界面 Web 组件 |
| **pi-pods** | `@mariozechner/pi-pods` | GPU pod 上的 vLLM 部署管理 CLI |

---

## pi-ai: 统一 LLM API（核心亮点）

### 支持的 Provider（20+）

| 类别 | Provider |
|------|----------|
| 直连 | OpenAI、Anthropic、Google、Mistral、Groq、Cerebras、xAI、OpenRouter、MiniMax、Vercel AI Gateway |
| 云平台 | Azure OpenAI、Vertex AI、Amazon Bedrock |
| OAuth | OpenAI Codex、GitHub Copilot、Google Gemini CLI、Antigravity（免费 Gemini 3） |
| 兼容 API | Ollama、vLLM、LM Studio、DeepSeek、zAI、OpenCode |
| 特殊 | Kimi For Coding（Moonshot AI，Anthropic 兼容 API） |

### 关键特性

- **统一 streaming 事件**：text_start/delta/end、thinking_start/delta/end、toolcall_start/delta/end、done、error
- **跨 provider 切换**：同一 conversation 可无缝从 Claude 切到 GPT 再切 Gemini，thinking blocks 自动转为 `<thinking>` 标签
- **Context 序列化**：标准 JSON，支持持久化和跨服务传输
- **TypeBox schema 验证**：tool 参数用 TypeBox 定义，AJV 自动验证
- **Token/Cost 追踪**：每次调用自动计算 token 用量和费用
- **OAuth 支持**：内置 Anthropic/Copilot/Gemini CLI/Antigravity 的 OAuth 登录和 token 刷新
- **Browser 兼容**：可在浏览器中运行（Bedrock 除外）
- **Faux Provider**：用于测试的内存 mock provider

### API 实现列表

- `anthropic-messages` — Anthropic Messages API
- `google-generative-ai` — Google Generative AI
- `openai-responses` — OpenAI Responses API
- `openai-completions` — OpenAI Chat Completions（多家兼容）
- `azure-openai-responses` — Azure OpenAI Responses
- `openai-codex-responses` — OpenAI Codex（WebSocket/SSE）
- `google-vertex` — Vertex AI
- `mistral-conversations` — Mistral
- `bedrock-converse-stream` — Amazon Bedrock
- `google-gemini-cli` — Google Cloud Code Assist

---

## pi-agent-core: Agent Runtime

### 核心概念

- **AgentMessage vs LLM Message**：AgentMessage 可扩展自定义类型（declaration merging），convertToLlm 负责过滤和转换
- **事件流**：agent_start -> turn_start -> message_start -> message_update -> message_end -> turn_end -> agent_end
- **工具执行模式**：parallel（默认）或 sequential
- **Steering/Follow-up**：运行中可注入 steering 消息中断 agent，follow-up 消息在 agent 完成后追加任务
- **beforeToolCall/afterToolCall hooks**：工具调用前后可拦截和后处理

### 关键 API

```typescript
const agent = new Agent({
  initialState: { systemPrompt, model, tools, messages },
  convertToLlm,  // AgentMessage[] -> LLM Message[]
  transformContext,  // 消息裁剪/压缩
  toolExecution: "parallel",
  beforeToolCall,  // 可拦截工具执行
  afterToolCall,   // 可后处理工具结果
});

await agent.prompt("Hello");  // 发送 prompt
await agent.continue();       // 从当前 context 继续
agent.abort();                // 取消
```

---

## pi-coding-agent: Coding Agent CLI

主产品，基于 pi-ai + pi-agent-core + pi-tui 构建的交互式编码 agent。

### 特性

- 支持 20+ LLM provider，可随时切换
- 扩展系统（examples/extensions/ 下有 custom-provider、with-deps 等示例）
- TUI 模式 + RPC 模式
- 支持 OAuth provider（Copilot、Gemini CLI 等）
- 自动发布 session 数据到 HuggingFace（用 [pi-share-hf](https://github.com/badlogic/pi-share-hf)）

---

## 技术栈

| 层 | 技术 |
|-----|------|
| 语言 | TypeScript 5.9 |
| 包管理 | npm workspaces |
| 构建 | tsc |
| Lint/Format | Biome 2.3 |
| 类型检查 | @typescript/native-preview (tsgo) |
| 测试 | Vitest |
| Git Hooks | Husky |
| Schema | TypeBox + AJV |

---

## 开发规范（AGENTS.md 要点）

- 无 emoji、无废话、技术 prose only
- 禁止 `any` 类型、禁止 inline imports
- 键绑定必须可配置，禁止硬编码
- 提交前必须 `npm run check`（lint + format + type check）
- 并行 agent Git 协作规则：只能 `git add` 自己改的文件，禁止 `git add -A`
- Lockstep 版本管理：所有包共享同一版本号
- 新贡献者 issue/PR 自动关闭，维护者每日审核

---

## 与类似项目对比

| 维度 | Pi | Cursor/Windsurf |
|------|-----|-----------------|
| 开源 | 完全 MIT | 闭源 |
| LLM provider | 20+，随意切换 | 内置，有限选择 |
| 自定义 | 高（extension 系统） | 低 |
| 定位 | 开发者工具包/框架 | 成品 IDE |
| Session 数据 | 开源共享（HF） | 不公开 |

---

## 安全与隐私

- OAuth token 本地存储（auth.json），调用者负责管理
- Browser 环境需显式传 API key（警告前端暴露风险）
- Bedrock 不支持 browser 环境
- Faux provider 用于隔离测试

---

## 值得关注的设计

1. **跨 provider handoff** — 同一对话可无缝切换 LLM，thinking blocks 自动适配，这对多模型协作场景非常有价值
2. **并行 agent Git 协作规则** — AGENTS.md 中定义了多 agent 同时修改同一 repo 的 Git 安全规范，是 multi-agent 开发的最佳实践参考
3. **Session 数据开源共享** — 鼓励用户发布 OSS coding session 到 HuggingFace，用真实任务数据而非玩具 benchmark 评估 agent
4. **Lockstep 版本** — 所有包强制同一版本，避免依赖地狱
