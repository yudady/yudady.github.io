---
title: Harness 时代 — AI 编程的底盤戰爭
aliases: [Harness 运行架子, AI Agent Harness, Vercel HarnessAgent, GitHub Token 量化, Codex 上云]
tags:
  - ai-agent
  - harness
  - token-efficiency
  - vercel-ai-sdk
  - codex
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=sUBqbhyW4Ik"
  - "https://github.blog/ai-and-ml/github-copilot/improving-token-efficiency-in-github-agentic-workflows/"
  - "https://vercel.com/changelog/program-agent-harnesses-with-ai-sdk"
  - "https://ai-sdk.dev/docs/introduction"
author: 影片作者 + GitHub Blog + Vercel
created: 2026-06-27
updated: 2026-06-27
description: AI 编程智能体的技术红利从模型参数卷动转向外部 Harness（运行架子）优化——价值被量化、工具上云端、架构被统一
level: intermediate
stars: 5
---

# Harness 时代 — AI 编程的底盤戰爭

> **核心论点**：AI 编程智能体的竞争焦点已从"谁的模型更强"转向"谁的 Harness（运行架子）更高效"。2026 年三大关键节点：GitHub 量化了 Harness 的工程价值、OpenAI Codex 完成云端化与团队级工程化、Vercel AI SDK 用 `HarnessAgent` 实现了不同 Harness 的统一编排与随意切换。

---

## 目录

- [[#一、什么是 Harness（运行架子）]]
- [[#二、GitHub：首次量化 Harness 的工程价值]]
- [[#三、OpenAI Codex：从本地工具到云端生产力]]
- [[#四、Vercel AI SDK：Harness 的统一编排]]
- [[#五、三大进展的工程启示]]
- [[#六、实战落地指南]]
- [[#参考资料]]

---

## 一、什么是 Harness（运行架子）

### 隐喻

> 如果 AI 模型是**发动机**，Harness 就是**底盘与变速箱**。

### 具体功能

Harness 是包裹在 AI 模型外面的那一层**编排架构**，负责：

| 职责 | 说明 |
|------|------|
| **工具配置** | 为模型配置什么工具（MCP tools、CLI、API） |
| **上下文管理** | 塞入什么上下文（系统提示、文件、历史） |
| **工作流编排** | 按什么流程运行（多轮推理、子代理、权限流） |
| **会话管理** | 会话隔离、压缩（compaction）、运行时配置 |
| **沙箱执行** | 安全的代码执行环境 |

### 为什么 Harness 比模型更重要

OpenAI 的 Codex 在过去半年输出量暴增（研究部门涨 56 倍、工程部门涨 27 倍），但真正决定编程智能体好不好用的，往往是这层外在的"架子"——同样一个模型，放进不同的 Harness，Token 消耗和完成质量可以差距巨大。

```
  ┌─────────────────────────────────────────┐
  │              Harness（运行架子）           │
  │  ┌───────────────────────────────────┐  │
  │  │  工具    上下文    工作流    沙箱   │  │
  │  │  ┌─────────────────────────────┐  │  │
  │  │  │       AI 模型（发动机）       │  │  │
  │  │  │   Claude / GPT / Gemini     │  │  │
  │  │  └─────────────────────────────┘  │  │
  │  └───────────────────────────────────┘  │
  └─────────────────────────────────────────┘
```

---

## 二、GitHub：首次量化 Harness 的工程价值

### 对照实验

GitHub 做了一件前所未有的事：**在控制变量（拉平上下文窗口、推理强度）的前提下，用同一个模型执行同一批任务，分别放入不同 Harness 运行**。

> *"在达到相同代码完成率的情况下，换一个架子，Token 消耗量会产生显著差异。"*

### Effective Tokens（ET）公式

GitHub 发明了 **Effective Tokens** 指标来跨模型层级标准化 Token 消耗：

```
ET = m × (1.0 × I + 0.1 × C + 4.0 × O)
```

| 变量 | 含义 | 权重说明 |
|------|------|---------|
| `m` | 模型成本乘数 | Haiku=0.25×, Sonnet=1.0×, Opus=5.0× |
| `I` | 新处理的输入 Token | 基准权重 1.0 |
| `C` | 缓存读取 Token | 仅 0.1×（从缓存读取成本极低） |
| `O` | 输出 Token | **4.0×**（所有厂商最贵的 Token 类型） |

> **工程意义**：一个 10% ET 减少意味着真实成本减少 10%，无论用哪个模型。

### GitHub 实测数据（12 个生产 Workflow）

| Workflow | ET 降幅 | 运行频率 | 说明 |
|---------|---------|---------|------|
| **Auto-Triage Issues** | **-62%** | 6.8 次/天 | 109 次 post-fix 运行，最显著持续改善 |
| **Smoke Claude** | **-59%** | 高频 | MCP 工具裁剪 + 降级到 Haiku 模型 |
| **Security Guard** | **-43%** | 每个 PR 触发 | 安全审查，不涉密文件直接跳过 LLM |
| **Community Attribution** | -37% | 8 次 post-fix | 移除完全未使用的 8 个 MCP 工具 |
| **Daily Compiler Quality** | -19% | 1 次/天 | 较小改善幅度 |

> **频率乘以省量**：Auto-Triage Issues 的 62% × 6.8 次/天，在观察期内累计节省约 **7.8M ET**。

### 三大 Token 杀手（GitHub 发现的最常见低效模式）

#### 1. 未使用的 MCP 工具注册

```
GitHub MCP Server: 40 个工具注册
  └── 实际使用: 2 个
  └── 纯开销: 38 个 × 10-15KB schema = 每轮 +8-12KB Token
```

> *"Workflow 作者自然倾向于注册完整工具集（最省事），但随着时间推移，大多数 workflow 只依赖一小段稳定工具。"*

#### 2. 用 MCP 做确定性数据获取

```
❌ 低效：MCP 工具调用（一次完整 LLM 推理轮）
   Agent → 决定调用工具 → 生成参数 → 接收响应 → 回到推理

✅ 高效：预下载 / CLI 代理
   Agent 启动前 → gh pr diff → 写入文件 → Agent 直接读文件
   （零 LLM 调用，纯确定性 HTTP 请求）
```

**GitHub 的核心洞察**：

> *"许多 agent 轮次实际上在做确定性数据收集——读取 issue 元数据、扫描标签——这些根本不需要推理。把它们移出 LLM 推理循环。"*

#### 3. 单条规则配置错误导致失控循环

> *"Daily Syntax Error Quality 是优化前 ET 最高的 workflow。根因是一行配置错误：sandbox 只允许相对路径，每次 compile 都被阻止。Agent 陷入 64 轮 fallback 循环，手动读取源码来重建编译器本应输出的信息。修正一行 bash 白名单规则就消除了循环。"*

---

## 三、OpenAI Codex：从本地工具到云端生产力

### 定位转变

| 维度 | 之前（本地工具） | 现在（云端生产力工具） |
|------|----------------|---------------------|
| **执行位置** | 本地终端 | 云端远程执行 |
| **团队协作** | 个人开发脚手架 | 团队/组织级管理 |
| **成本管控** | 个人 API key | 花费上限监控 + 分析面板 |
| **工作流** | 本地连续操作 | 手机启动 → 云端运行 → 稍后查看 |

### 新增能力

- **远程执行**：在手机上启动任务，让其在云端运行，稍后再查看结果
- **Codex SDK 整合**：编程化接入团队工作流
- **管理后台**：团队级成员管理和权限控制
- **花费上限监控**：设置 per-provider 消费限制
- **分析面板**：可视化 Token 消耗和成本分布

### 工程意义

AI 编程的 Harness 从个人开发工具升级为**组织级基础设施**——开始具备企业管理所需的可观测性、成本控制和权限治理能力。

---

## 四、Vercel AI SDK：Harness 的统一编排

### HarnessAgent — 像换模型一样换 Harness

Vercel AI SDK v7 引入 `HarnessAgent`，用一个统一接口驱动多种现成 Harness：

> *"AI SDK 一直让你不重写代码就能换模型。现在你能用同样的方式换 Harness。"*

```typescript
import { HarnessAgent } from '@ai-sdk/harness/agent';
import { claudeCode } from '@ai-sdk/harness-claude-code';
import { createVercelSandbox } from '@ai-sdk/sandbox-vercel';

const agent = new HarnessAgent({
  harness: claudeCode,    // ← 换成 codex 或 pi 即可切换
  sandbox: createVercelSandbox({
    runtime: 'node24',
    ports: [4000],
  }),
  tools: { /* 自定义工具 */ },
  skills: [ /* 自定义技能 */ },
});

const session = await agent.createSession();
const result = await agent.stream({
  session,
  prompt: '检查测试失败并修复生产代码。',
});
```

> **一行切换**：`harness: claudeCode` → `harness: codex`，其余代码不变。

### Harness 管理的组件

```
HarnessAgent 统一抽象
├── Skills（技能）
├── Sandboxes（沙箱）
├── Sessions（会话）
├── Permission Flows（权限流）
├── Compaction（上下文压缩）
├── Runtime Configuration（运行时配置）
└── Sub-agents（子代理）
```

### 工程红利

结合 GitHub 的量化结论：

```
前提：不同 Harness 的 Token 效率差距可达 40-62%
     ↓
推论：不修改代码就能随时切换 Harness = 巨大的成本弹性
     ↓
实践：写一次 Agent 代码 → 按场景选最优 Harness
      ├── 日常 CI → 选 Token 效率最高的
      ├── 复杂推理 → 选推理能力最强的
      └── 成本敏感 → 选最省钱的
```

### 初始支持的 Harness Adapter

| Adapter | 状态 |
|---------|------|
| Claude Code | 已支持 |
| Codex | 已支持 |
| Pi | 已支持 |
| 更多 | 开发中 |

> **注意**：当前为 experimental 发布，版本间可能有 breaking changes。

---

## 五、三大进展的工程启示

### 技术红利转移路线图

```
2023-2025              2026                    未来
   │                     │                      │
   ▼                     ▼                      ▼
模型参数卷动     →   Harness 底盘优化     →   全链路编排优化
(谁的模型更强)      (谁的架子更高效)        (谁的管线更智能)
   │                     │                      │
   ▼                     ▼                      ▼
GPT-4 → Claude      GitHub量化 + Codex上云   跨Harness编排
→ Gemini → ...      + Vercel统一抽象         + 组合最优策略
```

### 核心启示

| # | 启示 | 来源 |
|---|------|------|
| 1 | **Harness 和模型一样关键**，且优劣可以被量化测量 | GitHub 对照实验 |
| 2 | **效果不佳或成本过高时，先别换模型，先换/优化 Harness** | 影片核心建议 |
| 3 | **最便宜的 LLM 调用是你不做的那个** | GitHub Security Guard 案例 |
| 4 | **确定性操作移出 LLM 推理循环** | GitHub MCP→CLI 迁移 |
| 5 | **Harness 切换能力本身就是工程红利** | Vercel HarnessAgent |
| 6 | **AI 编程开始走向团队和组织层级管理** | Codex 云端化 |

---

## 六、实战落地指南

### 诊断决策树：Agent 效果不好怎么办？

```
Agent 效果不佳 / 成本过高
│
├── 第一步：检查 Token 消耗分布
│   ├── 启用 API proxy 日志（GitHub 模式）
│   ├── 输出 token-usage.jsonl（每次调用的 input/output/cache）
│   └── 计算 Effective Tokens（跨模型标准化）
│
├── 第二步：识别 Token 杀手
│   ├── 未使用的 MCP 工具？→ 裁剪工具集
│   ├── 确定性数据获取用 MCP？→ 改用 CLI 预下载
│   └── 失控循环？→ 检查 sandbox 权限配置
│
├── 第三步：优化 Harness（不换模型）
│   ├── 换更高效的 Harness
│   ├── 降级模型层级（Opus → Haiku）
│   └── 重构为子代理团队（小模型分工）
│
└── 最后才考虑：换底层模型
```

### Token 优化清单

```
✅ Token 效率检查清单：
□ MCP 工具注册：实际调用的 vs 注册的，裁剪未使用项
□ 确定性数据获取：能用 gh/CLI 的不用 MCP
□ 预下载策略：Agent 启动前预取必需数据
□ 模型分层：数据收集用 Haiku，推理用 Sonnet/Opus
□ 缓存利用：最大化 cache-read Token 占比
□ 失控检测：监控 turns-per-run 异常飙升
□ Sandbox 权限：确保必需工具不被阻断
□ Run 频率 × 省量：优先优化高频 workflow
```

### Harness 选型对比

| 维度 | Claude Code | Codex CLI | GitHub Copilot CLI |
|------|------------|-----------|-------------------|
| **定位** | 深度推理 + 重构 | 快速生成 + 模式补全 | CI/CD 集成 + 代码审查 |
| **Token 效率** | 高（上下文管理精细） | 中（模式补全流畅但可能冗余） | 高（量化验证） |
| **MCP 支持** | 原生 | 原生 | 原生 |
| **Sandbox** | 有 | 有 | 有 |
| **团队管理** | 弱 | 强（云端化） | 强（GitHub 集成） |
| **切换成本** | 需重写 Agent 代码 | 需重写 Agent 代码 | 需重写 Agent 代码 |
| **Vercel 统一** | 已适配 | 已适配 | 待适配 |

> **Vercel HarnessAgent 的价值**：消除"切换成本"列——写一次代码，随时换 Harness。

### GitHub 实测中学到的最佳实践

| 实践 | 来源案例 | 效果 |
|------|---------|------|
| 移除未使用的 MCP 工具 | Smoke Claude | -59% ET |
| 用 `gh pr diff` 替代 MCP | Auto-Triage Issues | -62% ET |
| 安全文件跳过 LLM | Security Guard | -43% ET |
| 修正 sandbox 白名单 | Daily Syntax Error | 消除 64 轮死循环 |
| 模型降级（Opus→Haiku） | Smoke Claude | 配合工具裁剪叠加省量 |
| 优先优化高频 workflow | Auto-Triage Issues | 6.8 次/天 × 62% = 7.8M ET |

---

## 参考资料

- **影片**：[GitHub量化Harness、Codex上云、Vercel AI SDK统一编排Harness](https://www.youtube.com/watch?v=sUBqbhyW4Ik)
- **GitHub Token 量化**（强烈推荐）：[Improving token efficiency in GitHub Agentic Workflows](https://github.blog/ai-and-ml/github-copilot/improving-token-efficiency-in-github-agentic-workflows/) — 含 ET 公式、实测数据、优化模式
- **Vercel HarnessAgent**：[Program Claude Code, Codex, Pi with AI SDK](https://vercel.com/changelog/program-agent-harnesses-with-ai-sdk)
- **AI SDK 文档**：[ai-sdk.dev/docs/introduction](https://ai-sdk.dev/docs/introduction) — Harness 抽象层设计
- **VS Code Token 优化**：[Improving token efficiency for GitHub Copilot in VS Code](https://code.visualstudio.com/blogs/2026/06/17/improving-token-efficiency-in-github-copilot)

## 相关笔记

- [[跨代理审计工作流 — Cross-Agent Adversarial Review]]
- [[AI Agent 工作流模式]]
