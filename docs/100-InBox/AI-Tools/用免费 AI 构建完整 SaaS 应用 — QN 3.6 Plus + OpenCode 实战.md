---
title: 用免费 AI 构建完整 SaaS 应用 — QN 3.6 Plus + OpenCode 实战
aliases: [QN 3.6 Plus SaaS, 免费AI建站, OpenCode实战]
tags: [AI, 编码模型, SaaS, OpenCode, Convex, Clerk, 免费工具]
source: ["https://www.youtube.com/watch?v=q-Y96pp1rWE"]
author: YouTube Creator
created: 2026-04-05 17:45
updated: 2026-04-05 17:45
description: |
  使用免费的 QN 3.6 Plus 模型 + OpenCode 编码代理，从零构建带数据库、认证、AI 功能的完整 SaaS 应用。全程零成本，约 35 分钟完成。
level: intermediate
stars: 4
---

# 用免费 AI 构建完整 SaaS 应用

> 视频演示了如何使用完全免费的 AI 模型（QN 3.6 Plus）和免费编码工具（OpenCode），在约 35 分钟内构建一个带数据库、用户认证和 AI 图像编辑功能的 SaaS 应用。成本为零，整个过程令人印象深刻。

## 目录

- [[#一、视频背景与核心命题]]
- [[#二、技术栈总览]]
- [[#三、项目搭建全过程]]
- [[#四、Convex 后端平台详解]]
- [[#五、QN 3.6 Plus 模型表现分析]]
- [[#六、免费 AI 编码工具市场格局]]
- [[#七、实战经验与注意事项]]
- [[#八、总结评价]]
- [[#附录：完整中文翻译字幕]]

---

## 一、视频背景与核心命题

### 核心命题

**能否仅使用免费 AI + 免费编码工具，构建一个完整的 SaaS 应用？**

博主在视频开头展示了 QN 3.6 Plus 的排行榜表现：

| 领域 | 排名 |
|------|------|
| 学术（Academia） | #11 |
| 金融（Finance） | #10 |
| 其他多个领域 | 均有上榜 |

### 技术选型

| 工具 | 类型 | 费用 |
|------|------|------|
| QN 3.6 Plus | AI 编码模型 | 免费 |
| OpenCode | 终端编码代理 | 免费 |
| Convex | 全栈后端平台 | 免费额度 |
| Clerk | 用户认证服务 | 免费额度 |
| Next.js | 前端框架 | 开源免费 |
| Google AI Studio | AI 功能集成 | 免费额度 |

---

## 二、技术栈总览

### 整体架构

```
┌─────────────────────────────────────────────────┐
│                    用户浏览器                      │
├─────────────────────────────────────────────────┤
│              Next.js (前端)                       │
│         ┌─────────────────────┐                  │
│         │   Clerk (认证 UI)    │                  │
│         └─────────────────────┘                  │
├─────────────────────────────────────────────────┤
│              Convex (后端)                        │
│  ┌──────┬──────────┬───────┬──────────┬────────┐ │
│  │Database│Functions│ Files │Schedules │  Logs  │ │
│  └──────┴──────────┴───────┴──────────┴────────┘ │
├─────────────────────────────────────────────────┤
│          Google AI Studio (AI 图像编辑)            │
└─────────────────────────────────────────────────┘
```

### 开发工具链

```
OpenCode (终端 AI 代理)
    │
    ├── 模型: QN 3.6 Plus (免费)
    ├── Convex Skills (自动识别项目类型)
    ├── 文件修改 + 代码生成
    └── Shell 命令执行
```

---

## 三、项目搭建全过程

### Step 1: 初始化项目

1. Google 搜索 "Convex quick start"
2. 选择 Next.js 模板，复制初始化命令
3. 在 OpenCode 中粘贴命令，自动创建项目
4. 同时在 Clerk.com 创建应用

### Step 2: 配置 Clerk 认证

1. 创建 Clerk 应用（名为 "QN free test"）
2. 配置 JWT 模板：
   - 进入 Configure → JWT → 添加新模板
   - 命名为 `convex`（必须小写）
   - 保存配置
3. 配置环境变量：
   - `NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY`（Clerk Overview 页面获取）
   - `CLERK_JWT_ISSUER_DOMAIN`（JWT 配置页面获取）

### Step 3: 启动 Convex

```bash
cd my-app
npm run dev
```

Convex 部署自动启动，提供：
- Data（数据库）
- Functions（函数）
- Files（文件存储）
- Schedules（定时任务）
- Logs（日志）

### Step 4: OpenCode 设置认证

在 OpenCode 中使用 Convex Skills：

```
/skills  → 选择 Convex Quick Start
```

然后指示 OpenCode：

> "请在此项目中使用 Clerk 设置认证。我会自己配置 .env，但请告诉我需要设置哪些占位符环境变量。"

### Step 5: 实现 Dashboard 和 AI 功能

1. 从 Google AI Studio 获取图像编辑器示例代码
2. 指示 OpenCode：
   - 将 AI 编辑器实现为 `/dashboard`（需登录访问）
   - 使用 Clerk 而非示例中的 OAuth
   - **确保在 Convex 中创建用户表**（关键步骤）
3. 配置登录后重定向到 `/dashboard`

### Step 6: 调试与修复

**遇到的问题及解决方案：**

| 问题 | 原因 | 解决方案 |
|------|------|---------|
| 登录后未显示 Dashboard | Auth Provider 未取消注释 | 在 Convex auth 配置中取消注释 Clerk provider |
| 无用户表 | AI 遗漏了 schema 定义 | 显式要求添加用户表 |
| 速率限制 | 免费路由有速率约束 | 添加自己的 API key 或等待重试 |
| 颜色样式问题 | 样式未完全适配 | 非关键问题，后续可调整 |

### 最终结果

约 35 分钟完成了一个带以下功能的 SaaS 应用：
- ✅ 用户认证（Clerk + Google 登录）
- ✅ 数据库（Convex — users 表、edits 表、numbers 表）
- ✅ 受保护的 Dashboard 页面
- ✅ AI 图像编辑功能（背景移除等）
- ✅ 登录后自动重定向

---

## 四、Convex 后端平台详解

### 什么是 Convex

Convex 是一个全栈后端平台（BaaS），由前 Stripe 工程师创建，提供实时数据库、无服务器函数、文件存储和认证的一体化解决方案。

### 核心特性

| 特性 | 说明 |
|------|------|
| Database | 实时、响应式、强一致性数据库，TypeScript Schema 定义 |
| Functions | 三种类型：Query（读取）、Mutation（写入）、Action（任意逻辑+外部 API） |
| Files | 内置文件存储，无需单独的 S3 bucket |
| Schedules | 内置定时任务（Cron Jobs） |
| Logs | 服务器日志实时查看 |
| Auth | 内置认证 + Clerk/Auth0/Firebase Auth 集成 |

### Convex vs 竞品对比

| 特性 | Convex | Supabase | Firebase |
|------|--------|----------|----------|
| 数据库 | 专用响应式 DB | PostgreSQL（关系型） | Firestore（NoSQL） |
| 实时 | 自动/响应式 | 订阅机制 | 监听器 |
| 函数 | TypeScript（Query/Mutation/Action） | Edge Functions（Deno） | Cloud Functions（Node.js） |
| 文件存储 | 内置 | S3 兼容 | Firebase Storage |
| 认证 | 内置 + Clerk 集成 | 内置（GoTrue） | 内置（Firebase Auth） |
| 语言 | TypeScript 优先 | SQL + TypeScript/JS | TypeScript/JS |
| 开源 | 否 | 是（基于 PostgreSQL） | 否 |
| SQL 支持 | 无 | 完整 PostgreSQL | 无 |

### Convex + Clerk 认证集成

```
Clerk (前端认证)
    │
    │ JWT Token
    ▼
Convex Auth (后端验证)
    │
    ▼
ctx.auth.getUserIdentity()
    │
    ▼
Convex Functions (业务逻辑)
```

配置要点：
- 在 `convex/auth.config.ts` 中设置 `provider: "clerk"`
- 通过环境变量提供 `CLERK_SECRET_KEY`
- Convex 自动验证 Clerk JWT
- 前端用 `<ClerkProvider>` 包裹应用

参考：https://www.convex.dev/docs/auth/clerk-auth

---

## 五、QN 3.6 Plus 模型表现分析

### 速度与效率

| 指标 | 数值 |
|------|------|
| Token 使用量 | ~39,000 tokens |
| 预算使用率 | 4% |
| 成本 | $0（完全免费） |
| 构建时间 | ~35 分钟 |

### 模型能力亮点

1. **记忆保持能力**：博主在构建早期提到需要用户表，模型在大量代码分析后仍然记得并自动添加
   > "我告诉它这个需求已经挺久了……它做了所有这些研究后，仍然记得需要创建用户表。这和 Opus 4.6 一样好。"

2. **Convex Skills 识别**：OpenCode 自动识别项目类型并加载 Convex 相关技能（Quick Start、Migration、Auth 设置等）

3. **代码生成质量**：能正确处理 Clerk 认证集成、Convex schema 定义、Next.js 页面路由等复杂任务

### 与顶级模型对比（博主观点）

| 模型 | 编码能力 | 费用 | 可访问性 |
|------|---------|------|---------|
| Claude Code + Opus 4.6 | 🥇 最佳 | $1,000-3,000/月 | 需订阅 |
| GPT 5.4 / Codex | 🥈 接近最佳 | 付费 | 需订阅 |
| QN 3.6 Plus | 🥉 接近 Opus 4.5 | 免费 | OpenCode + OpenRouter |
| Claude（有时） | 良好 | 付费 | 需订阅 |

> "一个百万 token 上下文的中国模型，开始接近甚至 Opus 4.5 的水平，这开始变得非常有趣。"

---

## 六、免费 AI 编码工具市场格局

### 当前市场现状

```
免费 AI 编码工具 (2026)
├── OpenCode + QN 3.6 Plus    → 完全免费，开源工具
├── OpenRouter 免费路由        → 免费（有速率限制）
├── QN Code + Q Oath           → 每天 1000 次免费请求
├── Claude Code                → 付费（$1,000-3,000/月）
├── Cursor                     → 付费
└── GitHub Copilot             → 付费
```

### 博主的付费痛点

博主透露其团队每月在 Anthropic（Claude）上的花费为 **$1,000-3,000**，这正是他积极寻找免费替代方案的动力。

> "我不想一直使用 Anthropic。我不想一直使用 Claude Code。如果有一个模型可以通过 OpenCode 达到同样的效果，我会用它。"

### AI 编码工具演进趋势

```
2024                    2025                    2026
┌──────────┐          ┌──────────┐          ┌──────────────────┐
│ 代码补全  │    →     │ AI 编码助手│    →     │ AI 编码智能体      │
│ 单行建议  │          │ Claude Code│          │ 自主规划+执行      │
│ 固定模板  │          │ Cursor    │          │ 长任务保持连贯      │
└──────────┘          └──────────┘          └──────────────────┘
                                                      │
                                              免费方案开始可行
```

---

## 七、实战经验与注意事项

### 最佳实践

- ✅ **手动配置敏感信息**：API Key、JWT Domain 等让 AI 给出占位符，自己填写
- ✅ **实时监控**：始终打开 IDE 和 Convex Dashboard，观察 AI 的修改
- ✅ **显式要求关键步骤**：如用户表创建，需要明确指示
- ✅ **分步验证**：每完成一个功能就测试，不要等全部完成后才发现问题
- ✅ **利用 Skills**：OpenCode 的 Convex Skills 能大幅加速项目搭建

### 常见问题

- ⚠️ **速率限制**：免费路由可能有速率约束，建议添加自己的 API key
- ⚠️ **用户表遗漏**：即 Opus 4.6 也会忘记创建用户表，需要显式要求
- ⚠️ **Auth Provider 配置**：Clerk 集成时容易遗漏取消注释 provider
- ⚠️ **样式细节**：AI 生成的 UI 可能存在颜色/布局偏差

### 快速搭建检查清单

- [ ] Convex 项目初始化
- [ ] Clerk 应用创建 + JWT 配置
- [ ] `.env.local` 环境变量填写
- [ ] Auth Provider 取消注释
- [ ] 用户表 Schema 定义
- [ ] 登录后重定向配置
- [ ] Dashboard 页面受保护
- [ ] AI 功能集成

---

## 八、总结评价

### 核心结论

> "这在以前是完全不可能的。这是一个全新的时代。你不可能完全免费地构建这样的应用。"

### 关键数据

- **构建时间**：~35 分钟
- **总成本**：$0
- **技术栈**：Next.js + Convex + Clerk + AI Studio
- **AI 工具**：OpenCode + QN 3.6 Plus（免费）
- **Token 消耗**：~39,000（仅用 4% 预算）

### 综合评价

**令人印象深刻的部分：**
1. 完全零成本构建功能完整的 SaaS 应用
2. AI 的记忆保持能力（长任务中记住次要需求）
3. OpenCode + Convex Skills 的协同效率
4. QN 3.6 Plus 在编码任务中的速度和质量

**仍需改进的部分：**
1. 免费路由的速率限制
2. 用户表等关键步骤仍需人工提醒
3. UI 细节（颜色、样式）需要人工调整
4. 与 Opus 4.6 仍有差距（博主明确表示）

---

## 参考资料

- [YouTube 原始视频](https://www.youtube.com/watch?v=q-Y96pp1rWE)
- [Convex 官方文档](https://www.convex.dev/docs/)
- [Convex + Clerk 认证集成](https://www.convex.dev/docs/auth/clerk-auth)
- [Convex 定价](https://www.convex.dev/pricing)
- [OpenCode 编码代理](https://github.com/opencode-ai/opencode)
- [Clerk 认证平台](https://clerk.com/)

## 相关笔记

- [[QN 3.6 Plus 发布分析报告]]
