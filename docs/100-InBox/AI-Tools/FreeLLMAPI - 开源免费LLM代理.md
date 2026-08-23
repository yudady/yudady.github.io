---
title: FreeLLMAPI - 开源免费 LLM 代理聚合 10 亿月 Token
aliases: [FreeLLMAPI, free-llm-api, 免费LLM代理]
tags:
  - llm
  - open-source
  - self-hosted
  - status/active
  - type/doc
source:
  - "https://youtu.be/Sy0r4QlDUIs"
  - "https://github.com/tashfeenahmed/freellmapi"
author: Tashfeen Ahmed
created: 2026-05-31
updated: 2026-05-31
description: FreeLLMAPI 是一个自托管的 OpenAI 兼容代理，自动路由请求到 14 个免费 LLM 提供商，整合约 10 亿月 Token 额度，含自动故障转移和统一仪表盘。
level: intermediate
stars: 4
---

# FreeLLMAPI - 开源免费 LLM 代理聚合 10 亿月 Token

> FreeLLMAPI 是一个自托管代理服务器，兼容 OpenAI API 格式，将 14 家 AI 提供商的免费额度整合到一个端点。适合个人开发者做实验、原型验证和本地 Agent 开发，不适合生产环境。

## 目录

- [[#项目概览与核心价值]]
- [[#提供商清单与免费额度]]
- [[#路由引擎工作原理]]
- [[#管理仪表盘]]
- [[#五大适用场景]]
- [[#真实限制与风险]]
- [[#部署方案]]

---

## 项目概览与核心价值

### 一句话定义

FreeLLMAPI（Free LLM API）是一个 TypeScript 编写的自托管代理服务器，将多家 LLM 提供商的免费 API 额度整合为统一的 OpenAI 兼容端点。

### 技术栈

| 组件 | 技术选型 |
|------|----------|
| 语言 | TypeScript |
| 运行时 | Node.js >= 20 |
| Web 框架 | Express |
| 存储 | SQLite（AES-256-GCM 加密） |
| 前端 | React v5 + shadcn/ui |
| 协议 | MIT License |
| 仓库 | `tashfeenahmed/freellmapi` |

### 核心价值

```
14 个免费 API → 1 个端点 + 1 个 Bearer Token + 1 个仪表盘
```

- 60 个免费模型，11 个活跃提供商
- 约每月 10 亿 Token（Mistral 贡献约 10 亿，其余提供商叠加）
- 上线数天内获得 6500+ Stars、1000+ Forks
- 3 分钟部署完成

### 与手动管理的对比

| 维度 | 手动管理多个 Key | FreeLLMAPI |
|------|------------------|------------|
| SDK 适配 | 14 个不同 SDK | 1 个 OpenAI 兼容接口 |
| 速率限制追踪 | 手动检查每个提供商 | 自动 ledger 追踪 |
| 故障转移 | 手动切换或写脚本 | 自动 fallback（最多 20 次重试） |
| Key 存储 | 分散在各处 | AES-256-GCM 加密存储 |
| 可观测性 | 无 | XRouted-via / X-fallback-attempts 响应头 |

---

## 提供商清单与免费额度

### 11 个活跃提供商一览

| 提供商 | 代表模型 | 月 Token 预算 | 速率限制 | 特点 |
|--------|----------|---------------|----------|------|
| **Mistral La Plateforme** | Mistral Large, Codestral, Devstral, Magistral | ~10 亿（共享池） | 最宽裕 | 锚定提供商，贡献最大 |
| **Groq** | Llama 3.3 70B, Llama 4 Scout, Qwen3 32B, Qwen3 235B | ~3000 万/模型 | 1000 请求/天 | 速度最快 |
| **Cerebras** | Qwen3 235B, GPT-OSS 120B, Llama 3.1 8B | ~3000 万 | 100 万/天, 30 RPM | 高速推理，免费大模型 |
| **Google Gemini** | Gemini 2.5 Flash/Flash-Lite, 3.1 Pro/Flash 预览 | ~300 万/模型 | 20 请求/天/模型 | Gemini 2.5 Pro 推理最强 |
| **OpenRouter** | DeepSeek, Kimi, Qwen, Llama 等 19 个模型 | ~600 万/模型 | 多模型覆盖 | 广度优先 |
| **GitHub Models** | GPT-4.1, GPT-4o | ~1800 万 | 50 请求/天 | 需 GitHub Copilot 免费版 |
| **SambaNova** | DeepSeek V3.1/V3.2, Llama 4 Maverick, Gemma 3 12B | ~300 万（共享） | DeepSeek V3 质量突出 |
| **Cloudflare Workers AI** | Kimi K2.5/K2.6, Qwen3 30B, GLM-4.7 Flash, Granite 4.0 | ~2000 万 | 10000 Neurons/天 | 边缘计算部署 |
| **Z.ai（智谱 AI 国际品牌）** | GLM-4.5 Flash, GLM-4.7 Flash | ~3000 万 | 永久免费，无需信用卡 | 最稳定类别 |
| **Cohere** | Command R+ | ~100-200 万 | 1000 次/月, 20 RPM | ⚠️ 试用条款禁止个人使用 |
| **NVIDIA NIM** | 预置但默认禁用 | 信用额（非月度） | 仅评估用途 | 不适合轮转模式 |

### 额度分布图

```
Mistral        ████████████████████████████████████████  ~10 亿（95%）
Groq           ██                                        ~3000 万
Cerebras       ██                                        ~3000 万
Cloudflare     █                                         ~2000 万
Z.ai           ██                                        ~3000 万
GitHub Models  █                                         ~1800 万
SambaNova      █                                         ~300 万
OpenRouter     █（×19 模型）                              ~1.14 亿（总计）
Gemini         █                                         ~300 万
Cohere         ▏                                         ~150 万
────────────────────────────────────────────────────────
合计          ~10 亿 Token/月
```

### 关键认知

- 10 亿数字**技术上准确**但 95% 来自 Mistral 一个提供商
- 免费池是**共享**的（Shared Pool），高负载时竞争
- Gemini 2.5 Pro 和 GitHub Models 的 GPT-4o 质量最高但日限额最紧（20-50 请求/天）

---

## 路由引擎工作原理

### 请求处理流程

```
客户端请求
    │
    ▼
┌──────────────┐
│  优先级扫描    │ ← 按用户配置的 Fallback Chain 顺序
│  Fallback    │
│  Chain       │
└──────┬───────┘
       │
       ▼  条件 1: Key 健康状态 = healthy
       │  条件 2: 4 项速率限制全部有余量
       │    ├─ Requests/min
       │    ├─ Requests/day
       │    ├─ Tokens/min
       │    └─ Tokens/day
       │
  ┌────┴────┐
  │ 通过？   │
  └────┬────┘
    是 │        否 → 移到 Chain 下一个模型
       │              │
       ▼              ▼
  解密 API Key    检查下一个...
       │
       ▼
  通过 Adapter 转发请求
       │
       ▼
  ┌────────────┐    ┌─────────────┐
  │ 成功响应     │    │ 失败(429/5xx)│
  │ → 返回客户端 │    │ → Key 冷却   │
  │ + 诊断 Header│    │ → 重试下一个 │
  └────────────┘    │ (最多 20 次)  │
                    └─────────────┘
```

### 核心机制详解

**1. Fallback Chain（优先级链）**
- **确定性**优先级扫描，非随机/轮询
- 每个请求都从链顶开始扫描
- 用户通过仪表盘拖拽排序配置

**2. 速率限制账本（Rate Limit Ledger）**
- 内存中维护每个 Key 的四维计数器
- 定期持久化到 SQLite（重启不丢失）
- 预判式跳过：接近限额（差 2 个请求内）直接跳过
- 避免「先打到 429 再处理」的被动模式

**3. Sticky Sessions（会话粘滞）**
- 多轮对话锁定同一模型 30 分钟
- 防止中途切换模型导致上下文断裂
- 仅当锁定模型完全不可用时才切换

**4. 健康检查服务**
- 后台独立运行，定期探测每个 Key
- 四种状态：healthy / rate-limited / invalid / error
- 路由器优先使用健康检查数据，非仅靠实时反馈

**5. 诊断响应头**

| Header | 用途 | 示例值 |
|--------|------|--------|
| `X-Routed-Via` | 实际处理请求的提供商和模型 | `gemini/gemini-2.5-pro` |
| `X-Fallback-Attempts` | 尝试了多少个提供商才成功 | `3` |

如果持续看到高 fallback 数值 → 需要调整 Fallback Chain 配置。

### 关键限制

| 缺失功能 | 影响 | 状态 |
|----------|------|------|
| **Tool Calling 透传** | Agent 无法使用结构化函数调用 | 开放贡献目标，无时间表 |
| **多模态输入** | 不支持图像/音频 | 同上 |

---

## 管理仪表盘

### 功能面板

```
┌─────────────────────────────────────────┐
│            FreeLLMAPI Dashboard          │
├──────────┬──────────┬───────────────────┤
│ Keys 页面 │ Fallback │ Analytics         │
│          │ Chain    │                   │
│ 添加 API │ 拖拽排序 │ 24h / 7d / 30d    │
│ Keys     │ 优先级   │ 请求量 / 成功率    │
│ 查看状态  │          │ Token 消耗 / 延迟  │
│ 获取统一  │          │ 各提供商分析       │
│ Bearer   │          │                   │
│ Token    │          │                   │
├──────────┴──────────┼───────────────────┤
│ Playground          │                   │
│                     │                   │
│ 聊天界面 + 路由     │                   │
│ 元数据实时显示      │                   │
│ (提供商/模型/延迟)  │                   │
└─────────────────────┴───────────────────┘
```

- 暗色模式支持
- 开发端口：5173，生产端口：301
- 空闲内存占用：~40MB
- 与代理服务器同进程，无需单独部署

---

## 五大适用场景

### 场景 1：本地 AI Agent 的免费推理后端

```
Agent (Hermes/OpenClaw/OpenHands)
    │
    │  base_url → FreeLLMAPI 代理
    │  api_key  → 统一 Bearer Token
    │
    ▼
FreeLLMAPI (localhost:3001)
    │
    ├─→ Gemini 2.5 Pro (用完 20 次/天)
    ├─→ Groq (用完 1000 次/天)
    ├─→ Cerebras
    └─→ Mistral (最后兜底)
```

- ⚠️ 仅适用于**自然语言推理**型 Agent
- ❌ 不支持 Tool Calling 依赖的 Agent

### 场景 2：开源编程助手免费后端

| 工具 | 配置方式 | 可用模型 |
|------|----------|----------|
| OpenCode | 设置 base URL | Devstral, DeepSeek V3.2 |
| Kilo Code | 设置 base URL | Qwen3 235B via Cerebras |
| QuinnCode | 设置 base URL | Mistral Large |

质量上限低于 Claude Opus/GPT-4.5 付费版，但日常编码任务足够。

### 场景 3：大批量文档处理与摘要

- Mistral 贡献的 ~10 亿 Token/月是核心优势
- 适合：论文摘要、结构化数据提取、日志分析、内容管线
- 单用户个人规模，非多租户

### 场景 4：多模型评测与选型

- `X-Routed-Via` 头精确标记每个请求使用的模型
- Playground 可重复发送相同 prompt 观察不同模型表现
- Analytics 面板提供延迟/成功率/Token 消耗对比
- 用实际数据选模型，而非看排行榜

### 场景 5：VPS 部署为个人推理网关

```
手机 App ──┐
笔记本 IDE ──┼──→ VPS (FreeLLMAPI + PM2 + Nginx/SSL)
桌面 Agent ──┘
```

- 所有设备共享同一 HTTPS 端点 + 统一 Token
- API Keys 加密存储在 VPS 上，无需分发到各设备
- 任何时刻在线可用

---

## 真实限制与风险

### 限制决策树

```
你需要以下任何一项？
  ├─ Claude Opus / GPT-4.5 级别推理  → ❌ 免费层无前沿模型
  ├─ 结构化 Tool Calling / Function Calling → ❌ 当前不支持
  ├─ 图像/音频多模态输入  → ❌ 仅支持文本
  ├─ 多用户/团队共享  → ❌ 单用户设计
  └─ 生产环境 SLA 保障  → ❌ 无 SLA

都不需要？
  → ✅ FreeLLMAPI 适合你
```

### 逐项分析

**1. 无前沿模型（Frontier Models）**
- 免费层天花板：Llama 3.3 70B、Gemini 2.5 Pro（限额紧）、Qwen3 235B、DeepSeek V3.2
- 需要深度推理/长上下文/细微指令遵循 → 付费 API

**2. 智力降级效应（Intelligence Degradation）**
- 高质量模型（Gemini 2.5 Pro、GPT-4o）日限额最紧（20-50 请求）
- 上午 10 点 vs 晚上 10 点的响应质量**明显不同**
- UTC 午夜重置
- 对需要全天一致质量的场景是硬伤

**3. 免费条款随时变动**
- 提供商可能收紧限额、添加信用卡要求、取消免费层
- 需要持续关注配置状态，不能设完就不管

**4. 服务条款合规风险**

| 提供商 | TOS 立场 |
|--------|----------|
| Groq | ✅ 明确允许集成到客户应用 |
| Mistral | ✅ 允许个人及内部商业使用 |
| OpenRouter | ✅ 私人使用，不公开代理 |
| NVIDIA | ⚠️ 仅限评估用途 |
| GitHub Models | ⚠️ 仅限实验 |
| Cohere | ❌ 试用条款明确禁止个人/家庭使用 |

**经验法则**：每个提供商一个账号、不转售、不与他人共享端点、不把免费层当生产后端。

---

## 部署方案

### 推荐配置

```
VPS 实例（Hostinger 等）
    │
    ├── PM2        → 进程守护，自动重启
    ├── Nginx      → SSL 终止 + 反向代理
    └── FreeLLMAPI → 主服务 (port 3001)
```

### 快速启动

```bash
# 克隆仓库
git clone https://github.com/tashfeenahmed/freellmapi.git
cd freellmapi

# 安装依赖
npm install

# 添加 API Keys（通过仪表盘或配置）
# 启动开发服务器（端口 5173 仪表盘 + 3001 API）
npm run dev

# 生产构建
npm run build
pm2 start ecosystem.config.js
```

### 配置要点

1. **Fallback Chain 排序策略**：
   - 顶部：高质量但限额紧的模型（Gemini 2.5 Pro, GPT-4o）
   - 中部：高速提供商（Groq, Cerebras）承接溢出
   - 底部：大共享池（Mistral）作为可靠兜底

2. **这是 Fallback Chain 中最具影响力的配置决策**

---

## 参考资料

- [FreeLLMAPI GitHub 仓库](https://github.com/tashfeenahmed/freellmapi)
- [FreeLLMAPI 官方文档站](https://tashfeenahmed.github.io/freellmapi)
- [YouTube 视频：This Open Source Tool Gives You 1B Free LLM Tokens/Month](https://youtu.be/Sy0r4QlDUIs)

## 相关笔记

- [[LLM API Providers Comparison]]
- [[Self-Hosted AI Agent Architecture]]
