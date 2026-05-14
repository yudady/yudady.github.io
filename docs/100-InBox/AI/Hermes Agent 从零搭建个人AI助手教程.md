---
title: Hermes Agent 从零搭建个人 AI 助手完整教程
aliases:
  - Hermes Agent 零到一教程
  - Hermes Agent 五大支柱
tags:
  - hermes-agent
  - ai-agent
  - tutorial
  - status/active
  - type/study-note
source:
  - "https://youtu.be/gb5TlGw6Uks"
author: Panda Making Money (Nate)
created: 2025-05-10
updated: 2025-05-10
description: 从零开始在 VPS 上搭建 Hermes Agent 个人 AI 助手，涵盖五大支柱、安装配置、Skill 编写、Cron 自动化、安全最佳实践及多 Agent 扩展策略。
level: beginner
stars: 3
---

# Hermes Agent 从零搭建个人 AI 助手完整教程

> 一小时完整教程：从理解 Hermes Agent 的核心理念，到在 VPS 上完成 Docker 部署、API 配置、Skill 创建和 Cron 自动化设置。适合有基础技术背景但未接触过 Hermes Agent 的开发者。

## 目录

- [Hermes Agent 是什么](#hermes-agent-是什么)
- [Hermes vs Claude Code vs OpenClaw 对比](#hermes-vs-claude-code-vs-openclaw-对比)
- [五大支柱](#五大支柱)
  - [Pillar 1: Memory（记忆）](#pillar-1-memory记忆)
  - [Pillar 2: Skills（技能）](#pillar-2-skills技能)
  - [Pillar 3: Soul（灵魂）](#pillar-3-soul灵魂)
  - [Pillar 4: Cron Jobs（定时自动化）](#pillar-4-cron-jobs定时自动化)
  - [Pillar 5: Gateway & Messaging（网关与消息平台）](#pillar-5-gateway--messaging网关与消息平台)
- [VPS 部署实战](#vps-部署实战)
- [CLI vs Telegram 界面对比](#cli-vs-telegram-界面对比)
- [安全与最佳实践](#安全与最佳实践)
- [多 Agent 扩展策略](#多-agent-扩展策略)
- [维护与迭代](#维护与迭代)

---

## Hermes Agent 是什么

**定位**：开源 AI Agent（MIT 协议），由 Nous Research 开发，GitHub 140K+ Stars，增长最快的开源项目之一。

**核心卖点**：自改进能力（self-improvement）—— 随使用时间自动编写和更新 Skills，不是 Day 1 强大后就停滞的工具。

**运行方式**：
- 自有基础设施（Mac Mini / 笔记本 / VPS / Docker / Android Termux）
- 多平台消息接口（Telegram / Discord / Slack / WhatsApp / iMessage）
- 91 个内置 Skill，520+ 社区 Skill，684 总 Skill

---

## Hermes vs Claude Code vs OpenClaw 对比

| 维度 | Claude Code | OpenClaw | Hermes Agent |
|------|------------|----------|-------------|
| **定位** | 终端内编码助手 | 手机端 AI Agent | 自改进个人助手 |
| **使用场景** | 坐下来深度编码 | 走在路上快速任务 | 定时自动化 + 跨平台 |
| **交互方式** | 终端驱动 | 手机消息 | CLI + 消息平台双模 |
| **自改进** | ❌ | 有限 | ✅ 核心卖点 |
| **开源** | Anthropic 产品 | 开源（350K Stars） | 开源（MIT） |
| **稳定性** | 稳定 | 更新频繁易崩溃 | 相对稳定 |
| ** Skill 系统** | CLAUDE.md | claw.md | skill.md + 自改进 |
| **底层模型** | Claude | Claude / GPT | 支持开源模型（Qwen/Llama） |

### 核心使用原则

```
┌──────────────────────────────────────────────────┐
│  Claude Code = 深度工作（日常编码的 90%）           │
│  OpenClaw / Hermes = 移动端（走路时也能干活）       │
│  三者可共存，共享 GitHub Repo 作为知识库             │
└──────────────────────────────────────────────────┘
```

> **关键洞察**：所有 Agent 都工作在某个目录结构中。如果你把知识、业务上下文、Skills 同步到 GitHub Repo，任何 Agent 都可以「放在」这个 Repo 上使用，只需适配各自术语（CLAUDE.md / claw.md / AGENTS.md）。

---

## 五大支柱

### Pillar 1: Memory（记忆）

**定义**：Agent 跨会话携带的持久化上下文。

#### 两个核心文件

```
Session 启动
    │
    ├──▶ user.md    → 你是谁、风格、偏好、禁忌
    ├──▶ memory.md  → 环境信息、项目、业务上下文
    └──▶ CLAUDE.md  → 项目级指令（每个会话加载）
    │
    ▼
Agent 拥有上下文，不再「失忆」
```

#### 类比

> 就像电影《Memento》—— Agent 每次醒来都是失忆状态。你的工作是确保注入的上下文文件足够完整，让它不需要你重复自己。

#### Memory 使用原则

| ✅ 应该存 | ❌ 不应该存 |
|----------|-----------|
| 持久偏好和事实 | API 密钥 / 密码 |
| 环境信息（路径、版本） | 临时任务状态 |
| 业务上下文 | 会话中间结果 |

#### Auto-update 机制

```
对话开始 → 用户交互 → Agent 自动提取信息 → 更新 user.md / memory.md
```

Hermes 会在对话过程中自动提取关于你的信息并更新这些文件，不需要你手动维护。但你仍应主动说"记住这个"或"以后别这样做"来引导。

---

### Pillar 2: Skills（技能）

**定义**：程序性记忆（Procedural Memory），可复用的任务执行手册。

#### 类比

> Skill = 食谱。没有食谱，每次做松饼可能 burnt 或巧克力片数量不同；有了食谱，每次结果一致。

#### Skill 文件结构

```markdown
---
name: generate-image
description: |
  根据文本描述生成图片
tags: [image, creative]
---

# 生成图片

## 使用方法
1. 接收用户描述
2. 调用图片生成 API
3. 返回生成结果
```

- **YAML Front Matter** → Agent 读取判断是否需要使用（Progressive Disclosure）
- **Markdown Body** → 具体执行步骤（仅在使用时加载，避免上下文膨胀）

#### Skill 生命周期

```
重复执行某任务
    │
    ▼
Agent 分析对话 + 工作流
    │
    ├──▶ 自动创建 Skill（如果频繁使用且未记录）
    ├──▶ 更新 Skill（根据反馈）
    └──▶ 从 Skills Hub 安装社区 Skill（520+ 可选）
```

#### Skills Hub

- 520+ 社区贡献 Skill
- 16 个 Anthropic 官方 Skill
- 安装方式：`hermes skills install <url>` 或直接给 Agent URL 说"安装这个 Skill"

---

### Pillar 3: Soul（灵魂）

**定义**：塑造 Agent 个性（Personality）的 Markdown 文件。

#### 特性

- 每个 Hermes Agent 都可以有不同的「性格」
- 影响 Agent 的语气、风格、回应方式
- 会随反馈自动进化
- 其他人与你的 Agent 交互时也能感受到个性

#### 示例

```
helpful, knowledgeable, direct
think, build, research, automate
```

> 如果你让 Agent 在 YouTube 上自动回复评论，给它一个有趣但不冒犯的个性，回复效果会更好。

---

### Pillar 4: Cron Jobs（定时自动化）

**核心价值主张**：这是 Hermes 相比 Claude Code 的最大差异点之一。

#### Cron 能力

```
┌─────────────────────────────────────────────────────┐
│  固定周期：每天午夜 12 点同步到 GitHub                │
│  临时循环：发布 YouTube 后每 10 分钟检查评论（持续 12h） │
│  条件触发：每小时检查是否到达指定时间再执行              │
│  上下文链：Job A 的输出成为 Job B 的输入               │
└─────────────────────────────────────────────────────┘
```

#### 创建方式

```bash
# 自然语言创建
"每天晚上 12 点（中部时间）把变更推到 GitHub"

# 临时循环
"接下来的 12 小时，每 10 分钟检查一次 YouTube 评论并回复"

# 上下文链
context_from: job_A_id  # 继承上一个 Job 的输出
```

#### 实际运行示例

| Cron 任务 | 频率 | 描述 |
|----------|------|------|
| AI 新闻简报 | 每日 | 发布到社区 |
| YouTube 评论监控 | 视频发布后 | 每 10 分钟检查并回复 |
| GitHub 同步 | 每小时 | 自检时区后同步 |
| 服务器巡检 | 定时 | 检查服务状态 |
| 研究报告 | 定时 | 生成行业摘要 |

---

### Pillar 5: Gateway & Messaging（网关与消息平台）

**支持的 20 个消息平台**：Telegram、Discord、Slack、WhatsApp、iMessage、Google Chat、Matrix、Mattermost、DingTalk 等。

#### 核心理念

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Telegram    │────▶│   Gateway    │────▶│  Agent Core  │
│  Discord     │     │  (消息路由)   │     │  (大脑)      │
│  Slack       │     └─────────────┘     └─────────────┘
│  iMessage    │            │
└─────────────┘            ▼
                   ┌─────────────┐
                   │  Dashboard   │
                   │  Kanban      │
                   │  Cron 管理   │
                   └─────────────┘
```

- Gateway 是消息路由中枢
- 同一个 Agent 可以通过多个平台访问
- Dashboard 提供 Web 管理界面

---

## VPS 部署实战

### 推荐部署方式：Docker 一键安装

```
VPS (Hostinger / AWS / 任意云)
    │
    ├── 一键安装 Docker
    │
    ├── Docker 容器运行 Hermes
    │   ├── 独立的 .env 文件（API 密钥）
    │   ├── 独立的文件系统
    │   └── 独立的网络
    │
    └── Telegram Bot 接入
```

### 环境变量配置

`.env` 文件存储所有敏感信息，**不提交到 GitHub**：

```bash
# 必需
ANTHROPIC_API_KEY=sk-ant-...
OPENAI_API_KEY=sk-...
TELEGRAM_BOT_TOKEN=...

# 可选
GITHUB_TOKEN=ghp_...
OPENROUTER_API_KEY=sk-or-...
```

### API 密钥管理最佳实践

| 做法 | 说明 |
|------|------|
| ✅ 每个 Agent 独立 API 密钥 | 追踪各 Agent 的 token 消耗 |
| ✅ 给 Agent 自己的邮箱账户 | 不共享你的个人账户 |
| ✅ 最小权限原则 | Finance Agent 不需要社交媒体 API |
| ✅ 命名 API 密钥 | `hermes-finance-router` / `hermes-marketing-router` |
| ❌ 直接给 Agent 你的信用卡 | 当作新员工对待 |

### GitHub Token 配置

1. 创建 GitHub Classic Token
2. 选择最小权限 Scope（repo 公开访问 + invitations）
3. `hermes config set github.token <token>`
4. 设置 `always allow` 权限以便 Cron 自动提交

### Docker 容器文件系统理解

```
VPS Root
├── .env                    ← 不在容器内！容易搞错
└── Docker Container
    ├── .env                ← 容器内的实际配置文件
    ├── /root/.hermes/      ← Agent 数据目录
    │   ├── skills/
    │   ├── memory.md
    │   └── user.md
    └── 所有 Agent 文件
```

> **Pitfall**：Agent 运行在容器内，但你在 VPS root 编辑文件时看不到容器的文件系统。需要用 `docker exec` 进入容器操作。

---

## CLI vs Telegram 界面对比

| 维度 | CLI（终端） | Telegram |
|------|-----------|----------|
| **类比** | 驾驶舱 | 遥控器 |
| **上下文可见性** | ✅ 高（可看到窗口使用情况） | ⚠️ 低（autocompaction 后不清楚） |
| **Slash 命令** | ✅ 全部可用 | ⚠️ 部分受限 |
| **适合任务** | 深度编码、vibe coding | 快速任务、Cron 管理、日常事务 |
| **风险** | 低（高可控性） | 中等（上下文不透明） |
| **底层** | 同一个 Agent、同一个上下文窗口 | 同上 |
| **Token 管理** | 清晰可见 | 模糊（~136K 阈值自动压缩） |

### 上下文压缩（Compaction）

当 Token 使用接近 ~136K 阈值时，Agent 会自动执行 Compaction：
- 尝试压缩历史上下文
- 如果压缩失败 → 插入 fallback context marker
- 你可以随时要求 Agent "读取你的 memory 文件" 来检查状态

### 使用建议

```
深度编码 / 新项目搭建   → CLI
日常事务 / Cron / 快速查询 → Telegram
高风险操作              → CLI（上下文可控）
低风险操作              → Telegram（方便即可）
```

---

## 安全与最佳实践

### VPS 安全加固

```
┌──────────────────────────────────────────┐
│  VPS 安全检查清单                          │
│  ├─ ✅ 配置防火墙（限制 IP + 端口）         │
│  ├─ ✅ 每个 Agent 独立 Docker 容器         │
│  ├─ ✅ .env 不提交到 GitHub                │
│  ├─ ✅ API 密钥最小权限                    │
│  ├─ ✅ 定期安全审计（可让 Agent 自动做）     │
│  └─ ✅ 给 Agent 自己的账户，不共享你的      │
└──────────────────────────────────────────┘
```

### Skill 编写心态

> 把 Agent 当作一个新员工或实习生。你会给实习生什么权限？不会直接给信用卡。同理，不要给 Agent 过多权限。

---

## 多 Agent 扩展策略

### 何时创建新 Agent（决策树）

```
当前任务/角色是否需要：
    │
    ├─ 不同的权限？        → Yes → 创建新 Agent
    ├─ 不同的 Secrets？     → Yes → 创建新 Agent
    ├─ 不同的 Tools？       → Yes → 创建新 Agent
    ├─ 独立的长程记忆？     → Yes → 创建新 Agent
    ├─ 独立的日程？         → Yes → 创建新 Agent
    ├─ 不同的受众？         → Yes → 创建新 Agent
    │
    └─ 是一次性任务？       → Yes → 留在主 Agent
```

### 扩展模式对比

```
❌ 反模式：一个 Mega Agent
├── 所有 API 密钥
├── 所有 Skills
├── 所有 Cron
└── 结果：高混乱 + 高风险

✅ 推荐模式：按职责分割
├── Personal Agent（主 Agent / COO）
├── Finance Agent（财务专用）
├── Marketing Agent（社媒专用）
└── 每个容器独立：内存、密钥、工具隔离
```

### 迁移策略

当从主 Agent 拆分出专用 Agent 时：
1. 迁移相关 Skills（Markdown 文件，直接复制）
2. 迁移 Cron Jobs
3. 迁移 API 密钥（只给必要的）
4. 更新主 Agent 的记忆，记录分工

> **建议**：先用好一个主 Agent，等真正感到需要拆分时再扩展。过早拆分会增加管理复杂度。

---

## 维护与迭代

### 日常维护清单

| 情况 | 操作 |
|------|------|
| Agent 连续两次做错 | 立即纠正，让它更新 Skill / Memory |
| 同一指令给过两次 | 让 Hermes 写成 Skill |
| Agent 太啰嗦或语气不对 | 让它编辑 Soul 文件 |
| 需要新定时任务 | 先写 Skill，再调 Cron |
| Agent 行为异常 | 先检查 memory.md（陈旧记忆是异常第一原因） |
| 发布新视频 | 设置临时 Cron（每 10 分钟检查评论，12h 后自动停止） |

### 核心心态

> Hermes 不是你「设置完」就不管的工具。它是一个你持续使用和训练的队友。投入时间越多，它越强大。

---

## 参考资料

- [Hermes Agent from Zero to Your Own Assistant](https://youtu.be/gb5TlGw6Uks) - YouTube
- [Hermes Agent GitHub](https://github.com/nousresearch/hermes-agent)
- [Hermes Agent Skills Hub](https://hermes.nousresearch.com/skills)
