---
title: 跨代理审计工作流 — 当 AI 开始互相挑错
aliases: [Cross-Agent Workflow, 跨代理审计, AI交叉审核, Adversarial Review Pipeline]
tags:
  - ai-agent
  - cross-agent-review
  - quality-assurance
  - autonomous-workflow
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=-9Uh6lbGe2I"
  - "https://github.com/wanshuiyin/auto-claude-code-research-in-sleep"
  - "https://www.mindstudio.ai/blog/cross-vendor-ai-agent-review-claude-codex"
author: AgentWikis 作者 + ARIS (wanshuiyin) + MindStudio
created: 2026-06-27
updated: 2026-06-27
description: 用两个不同厂商的 AI 代理互相审计——一个建构、一个挑错、再回传对质，实现全自动高质量产出管线
level: intermediate
stars: 5
---

# 跨代理审计工作流 — 当 AI 开始互相挑错

> **核心思想**：单个 AI 代理写完代码/文档后，让**另一个不同厂商的 AI 代理**独立审计。审计完不是直接执行，而是回传给原建构方逐条自证。三层对质，把假阳性降到极低。

---

## 目录

- [[#一、为什么需要跨代理审计]]
- [[#二、核心架构：五阶段管线]]
- [[#三、三大实战系统对比]]
- [[#四、关键技术机制深度解析]]
- [[#五、跨厂商审核的理论基础]]
- [[#六、落地实践指南]]
- [[#参考资料]]

---

## 一、为什么需要跨代理审计

### 单一模型的根本盲点

> *"让同一个模型既写代码又审代码，就像让拼写检查器审查自己的自动纠错。"*
> —— MindStudio《Cross-Vendor AI Agent Review》

每个 AI 模型都有**特征性思维模式**（characteristic way of thinking）——特定的训练数据、微调反馈、优化目标。这种一致性让它好用，但也让它**无法有效审查自己的输出**：

| 问题 | 说明 | 类比 |
|------|------|------|
| **盲点问题** | 生成时编码进输出的假设，审查时通过相同视角评估，导致"看不见自己的错" | 人类校对自己的文章——读到的是"意图写的"而非"实际写的" |
| **一致的失败模式** | Codex 倾向漏掉某类安全问题，Claude 倾向漏掉另一类——**不同厂商的盲点不同** | 两个不同背景的审计师互补 |
| **同质性复合放大** | 全管线跑同一模型 = 生成和审查共享盲点，错误在每一步都被放过 | 近亲繁殖 |

### 跨厂商审核的力量

> *"这不是寻求哪个模型更优越。你在寻找的是两个模型有**不同的失败模式**。"*

```
单模型自审：  生成(Claude) → 审查(Claude) → 相同盲点 → 漏检
跨厂商审查：  生成(Claude) → 审查(GPT)   → 盲点不重叠 → 捕获更多问题
```

**ARIS 的精辟类比**（对抗式 vs 随机式）：

> *"单模型自审像随机赌博（stochastic bandits）——奖励噪声可预测；跨模型审查像对抗博弈（adversarial）——审查者主动探测执行者未预见的弱点。"*

---

## 二、核心架构：五阶段管线

### AgentWikis 作者的实战管线

作者用这套系统在**睡觉时**自动产出 AgentWikis 知识库页面——通宵看棒球，AI 自动协作完成高质量内容。

```
┌─────────────────────────────────────────────────────────────────────┐
│                    跨代理审计全自动管线                                │
│                                                                     │
│  [人工]               [Claude Code]           [Hermes Agent]         │
│  选题写入 ──────────▶ 核心建构 ──────────────▶ 独立审计              │
│  topics_日期.txt      new_wiki.py             review.md 规范        │
│                       Opus 4.8                GPT-5.5               │
│                       30-60min/主题           地毯式 Findings        │
│                                                                     │
│                            │                   │                    │
│                            ▼                   ▼                    │
│                     ┌──────────────────────────┘                    │
│                     ▼                                               │
│              [Claude Code -p]          [人工]        [VPS]          │
│              非交互式对质 ──────────▶ 终审 ──────▶ Admiral 拉取部署   │
│              逐条自证清白              Obsidian      15min 自动同步   │
│              过滤假阳性                肉眼复审                      │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 五阶段详解

#### 阶段 1：人工选题与需求触发

- 每日流量监控（Cron Job 收集热门搜索）
- 社群成员许愿 + 技术趋势追踪（`llama.cpp`、`Ollama`、`Hugging Face` 等）
- 将主题写入 `topics_[日期].txt` → **完成人工交办的唯一动作**

#### 阶段 2：Claude Code 核心建构（Scaffolding）

| 项目 | 说明 |
|------|------|
| 工具 | 自制 `new_wiki.py` 脚本 → Claude Code (Opus 4.8) |
| 任务 | 网页爬取、资料摄取、结构设计 |
| 耗时 | 每个主题 30 分钟至 1 小时 |
| 完成标记 | 文件顶部标注 `finished` |

#### 阶段 3：Hermes Agent 独立审计（Auditing）

- **Cron Job 每 5 分钟执行**，检测 `finished` 标签触发
- Hermes（基于 GPT-5.5）依据 `review.md` 规范进行地毯式审计
- 输出详细 Findings 报告（检查规格、数据来源完整性、格式等）

#### 阶段 4：Claude 非交互式二次验证（Reconciliation）

> **这是整个管线的精髓。**

审计完成后，立即用 `claude -p`（非交互式模式）对质：

- Claude **不盲信**审计报告，逐条重新验证 Hermes 指出的问题
- 有效抓出审计代理的**假阳性**（False Positives）
- 例：将刻意微调的排版误判为字数超标 → Claude 自证"这是有意的"

#### 阶段 5：人工终审与自动化部署

- 起床后阅读 `findings_verification_report.md`
- Obsidian 中简单肉眼复审（此时出错率已极低）
- 确认后推送到 Private GitHub → VPS 上 Admiral 代理每 15 分钟自动拉取重启

---

## 三、三大实战系统对比

### 对比总览

| 维度 | AgentWikis 管线 | ARIS (Auto-Research-In-Sleep) | 我们的 `task-dual-review` |
|------|----------------|-------------------------------|--------------------------|
| **定位** | 知识库内容生产 | 学术研究全流程（论文→投稿） | 通用代码/文档双重审核 |
| **建构方** | Claude Code (Opus) | Claude Code (Opus 4.8) | Claude Code / Subagent |
| **审计方** | Hermes (GPT-5.5) | Codex MCP (GPT-5.5 xhigh) | Codex CLI |
| **核心创新** | **Reconciliation 回传对质** | 77+ 组合 Skills + 安全门控 | Codex 数据修正夸大 |
| **自动化程度** | 全自动 + 人工终审 | 全自动 + 用户确认门 | 半自动（agent 编排） |
| **规模** | 单一管线 | 77 个可组合 skill | 单一三步流程 |

### ARIS：学术级的系统化实践

ARIS（Auto-Research-In-Sleep）是目前**最成熟的跨代理审计开源系统**：

```
ARIS 三角色架构
├── Depth → Breadth    （深度执行 → 广度探索）
├── Cross-model Review  （跨模型审查 → 准确性）
└── Research Wiki       （研究记忆 → 持久化）
```

**核心设计哲学**：

| 设计决策 | 理由 |
|---------|------|
| 用两个模型而非更多 | 两个是对抗自审盲点的最小数量；2-player 博弈比 n-player 更高效收敛到 Nash 均衡 |
| 跨模型而非同模型子代理 | 同模型自审容易陷入局部最优（local minima）——相同模式创造相同盲点 |
| 审查者可升级为 Pro | `--reviewer: oracle-pro` → GPT-5.4 Pro（最强推理），用于证明验证、实验审计 |

**ARIS 安全门控三原则**（Rebuttal 不会在以下任何一条失败时定稿）：

- **No fabrication** — 每个声明都映射到论文/审查/用户确认的结果
- **No overpromise** — 每个承诺都经用户批准
- **Provenance** — 完整溯源链，零捏造

**ARIS Assurance Gate（保障门控）**：

```
effort 轴（做多少）：lite → balanced → max → beast
assurance 轴（审计是否强制）：draft → polished → submission

max/beast + submission = 强制运行三个审计：
  ├── /proof-checker      （证明验证）
  ├── /paper-claim-audit   （声明审计）
  └── /citation-audit      （引用审计）
  
verify_paper_audits.sh 的 exit code 是真实来源，
不是执行者的自我报告。
```

---

## 四、关键技术机制深度解析

### 1. 文件标签门控（File-based Gate）— 零 Token 浪费

> *"Cron 每 5 分钟跑一次，但只有文件第一行精确出现 `finished` 才触发 API 调用。不满足则立即中止，完全不消耗费用。"*

```bash
# 伪代码：Wake Agent Gate
#!/bin/bash
TOPIC_FILE="topics_$(date +%Y%m%d).txt"

# 检查第一行是否为 finished
FIRST_LINE=$(head -1 "$TOPIC_FILE")
if [ "$FIRST_LINE" != "finished" ]; then
    exit 0  # 静默退出，零 API 调用
fi

# 条件满足才唤醒 agent
hermes-agent --audit "$TOPIC_FILE"
```

**为什么有效**：
- Cron 高频轮询（5 分钟）实现近实时响应
- 文件状态做条件判断 → 不满足时零成本
- 比用消息队列更简单，比轮询 API 更省钱

### 2. 非交互式对质循环（`claude -p` Reconciliation）

```
审计方 Findings ──────────────┐
                               ▼
                    ┌─────────────────────┐
                    │  claude -p 对质循环   │
                    │                     │
                    │  对每条 Finding：     │
                    │  ├── 确认（认错修改） │
                    │  ├── 反驳（附证据）   │
                    │  └── 部分确认         │
                    │                     │
                    │  循环直到清零         │
                    └─────────────────────┘
                               │
                               ▼
                    findings_verification_report.md
```

**关键特性**：
- **无状态**：每次 `-p` 调用都是全新 session，无历史记忆
- **必须给极清晰目标**（Goal Mode）：反复执行检查循环直到所有条件满足
- **自动过滤 ~80% 假阳性**：审计方误判的点被建构方用证据反驳

### 3. 模型分工策略

| 角色 | 选谁 | 为什么 |
|------|------|--------|
| **建构方** | Anthropic Opus 4.8 | 极强推理 + 程式能力，擅长结构设计 |
| **审计方** | GPT-5.5（Hermes 平台） | 整合丰富工具箱（Harness），擅长多文件审查 + 标准检验 |

> **ARIS 的补充**：Claude 擅长谨慎推理 + 指令遵循；Codex/GPT 擅长代码生成 + 模式补全。**不对称性正是重点**——不是找更优模型，是找不同失败模式。

### 4. 独立上下文隔离

> *"不要给审查者与生成者相同的对话历史。审查 agent 应该以干净的上下文开始，只包含：原始需求/规格 + 待审查输出 + 审查 rubric。"*

```
❌ 错误：共享上下文
   生成对话历史 ──→ 审查者（被锚定到相同推理链）

✅ 正确：独立上下文
   原始规格 + 生成输出 + 审查标准 ──→ 审查者（独立判断）
```

---

## 五、跨厂商审核的理论基础

### 为什么跨厂商审核有效

MindStudio 的文章系统化阐述了理论：

**1. 盲点不重叠**

不同厂商的模型（OpenAI vs Anthropic）不共享训练数据、架构、微调管线。当 Codex 倾向漏掉某类安全问题（如特定 SQL 注入向量、异步代码竞态条件），Claude 往往能捕获，反之亦然。

> *"2024 年 LLM 代码安全研究发现，不同模型展现出显著不同的漏洞敏感性特征。"*

**2. 模式识别 vs 逻辑追踪**

模型在大规模代码上训练后学会了模式。一个结构上看起来正确的模式（for 循环、条件检查、错误处理）可能包含微妙逻辑缺陷——生成模型因为"认出模式"而不标记，但不同模型会**追踪实际逻辑**而非识别模式。

**3. 过度自信检测**

两个模型都会产生"听起来自信但微妙错误"的输出。当一个模型审查另一个的输出时，它不继承相同的信心——它**从头评估**，捕获自审会忽略的过度自信错误。

### 跨厂商审核实际捕获的问题类型

| 问题类型 | 说明 | 单模型为何漏 |
|---------|------|-------------|
| **"看起来正确"的逻辑错误** | 结构正确但逻辑有缺陷 | 生成模型识别模式而非追踪逻辑 |
| **安全漏洞** | 输入验证缺失、认证不当、竞态条件 | 模型在含缺陷模式上训练过，不标记为风险 |
| **规格漂移**（Spec Drift） | 代码技术上有效但不匹配原始需求 | 生成模型内化了相同解读 |
| **过度自信的输出** | 听起来合理但微妙错误 | 自审继承相同信心 |

---

## 六、落地实践指南

### 最小可行模式：Generate → Review → Reconcile

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  Generation   │────▶│   Review     │────▶│ Reconcile    │
│              │     │              │     │              │
│ 模型 A 生成   │     │ 模型 B 审查  │     │ 人工或自动   │
│ 基于输入规格  │     │ 接收规格+输出│     │ 综合反馈     │
│              │     │ 结构化批评   │     │ 生成最终输出 │
└──────────────┘     └──────────────┘     └──────────────┘
```

### 高风险输出：双轮审查

```
1. 模型 A 生成输出
2. 模型 B 审查批评
3. 模型 A 根据批评修订
4. 模型 B 最终确认问题已解决
```

### 实施清单

**审查 Prompt 必须结构化**（否则结果不一致）：

```
审查 Rubric 清单：
□ Correctness — 是否准确实现规格？
□ Security — 有无明显漏洞模式？
□ Edge cases — 什么输入/条件会导致失败？
□ Readability — 逻辑是否清晰到人类可维护？
□ Assumptions — 有什么隐含假设应该显式化？
```

**分歧处理**：

> *"当两个模型分歧时，不要自动解决——显式标记为需要人类判断。'Claude 标记为潜在问题；GPT 未标记——需要人工判断' 比静默选择一个更有用。"*

### 成本与延迟考量

| 策略 | 适用场景 | 说明 |
|------|---------|------|
| 全量跨厂商审查 | 高价值输出（生产代码、法律文档） | 成本 ~2x，延迟 ~2x，但准确性提升值得 |
| 分层审查 | 大批量低风险任务 | 多数用单模型，按置信度或随机采样触发跨厂商 |
| 仅最终输出审查 | 流水线中间步骤多 | 只在最终产物做跨厂商，中间步骤用单模型 |

### 判断决策树

```
需要审查？
│
├── 低风险（格式化、注释）─→ 单模型审查即可
│
├── 中风险（业务逻辑）──→ 跨厂商审查（1 轮）
│
├── 高风险（安全、支付）──→ 跨厂商双轮审查
│                         + 人工终审
│
└── 自动化管线？──→ 加文件标签门控（零 Token 空转）
                   + Reconciliation 回传对质（过滤假阳性）
```

---

## 与我们现有系统的关系

我们的 `task-dual-review` skill 已实现跨代理审计的核心模式：

| 影片/ARIS 特性 | 我们的状态 | 差距 |
|---------------|-----------|------|
| 跨厂商审计方 | Codex 独立验证 | 已有 |
| Reconciliation 回传 | 缺失（Codex 直接裁定） | **值得补充** |
| 文件标签门控 | 未实现 | 可加（cronjob + 文件检测） |
| 无状态对质循环 | 部分（`--print` 模式） | 可强化循环终止条件 |
| 安全门控强制 | 未实现 | 可参考 ARIS Assurance Gate |

**最值得吸收的点**：Reconciliation 回传模式——审计方产出 Findings 后，回传给原建构方逐条自证，过滤 ~80% 假阳性。

---

## 参考资料

- **影片**：[I Built an Autonomous Cross-Agent Workflow (ClaudeCode to Hermes and Back)](https://www.youtube.com/watch?v=-9Uh6lbGe2I)
- **ARIS**：[wanshuiyin/auto-claude-code-research-in-sleep](https://github.com/wanshuiyin/auto-claude-code-research-in-sleep) — 77+ skills 的学术级跨模型审查系统
- **理论**：[Cross-Vendor AI Agent Review — MindStudio](https://www.mindstudio.ai/blog/cross-vendor-ai-agent-review-claude-codex)
- **社区实践**：[Reddit - Two AI coding agents cross-reviewing each other](https://www.reddit.com/r/vibecoding/comments/1r4i8sf/)
- **自主循环**：[How to Run Claude Code For Hours Autonomously](https://www.youtube.com/watch?v=o-pMCoVPN_k)

## 相关笔记

- [[AI Agent 工作流模式]]
- [[Claude Code 自主循环与 Goal Mode]]
