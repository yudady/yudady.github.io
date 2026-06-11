---
title: 用 Hermes Kanban 构建 LLM Wiki 维护的多 Agent 工作流
aliases:
  - Hermes Kanban 多 Agent 工作流
  - LLM Wiki 自动维护
  - Knowledge Base 自动更新流水线
tags:
  - ai-agent
  - hermes-agent
  - kanban
  - knowledge-base
  - status/active
  - type/doc
source:
  - "https://youtu.be/hbKvO5MWq08"
  - "https://github.com/tobistudio/hermes-multi-agent-workflow"
author: Tonbi's AI Garage (Tonbi)
created: 2026-06-10
updated: 2026-06-10
description: |
  实战演示如何用 Hermes Agent 的 Kanban 看板系统构建多 Agent 工作流，自动维护 LLM Wiki（Karpathy 风格知识库）——侦察、判断、研究、摄取、校验、提交，全流程自动化。
level: intermediate
stars: 4
---

# 用 Hermes Kanban 构建 LLM Wiki 维护的多 Agent 工作流

> Tonbi 实战演示从开源模板搭建多 Agent 工作流，让一群 AI Agent 自动发现、验证、摄取和清理 LLM Wiki 内容。适合正在使用或评估 Hermes Agent Kanban 系统的工程师和知识工作者。

## 目录

- [[#为什么需要自动化维护 LLM Wiki]]
- [[#工作流架构]]
- [[#五个 Agent 的职责分工]]
- [[#判断评分机制]]
- [[#路由决策]]
- [[#安全模型]]
- [[#实战构建过程]]
- [[#多模型混合部署实验]]
- [[#运行中观察到的行为]]
- [[#关键收获]]

---

## 为什么需要自动化维护 LLM Wiki

### LLM Wiki 的价值

Karpathy 风格的 LLM Wiki 是为 Agent 准备的结构化知识库，在 Obsidian 中组织：

```
Knowledge Base
├── raw/           ← 原始资料（官方文档、changelog、GitHub、社区资源）
├── wiki/          ← 整理后的知识页面
│   ├── memory-system/
│   ├── skills/
│   ├── mcp-servers/
│   └── ...
└── index.md       ← 索引
```

### 不维护会怎样

| 问题 | 后果 |
|------|------|
| 信息过时 | Agent 基于旧信息回答，产生错误 |
| 信息膨胀 | Wiki 越来越大，响应变慢、噪音增多 |
| 死链堆积 | 引用的外部链接失效 |
| 格式不一致 | 页面结构混乱，Agent 难以解析 |

### 自动化的收益

```
手动维护：
  发现新信息 → 阅读整理 → 写入 Wiki → 检查死链 → 清理过时内容 → Git commit
  ↑ 每个步骤都要人来做，耗时且容易遗忘

自动化：
  Scout 发现 → Orchestrator 判断 → Researcher 验证 → Ingestor 写入 → Linter 校验 → Git commit
  ↑ 人类只需在关键节点审批，其余全自动
```

---

## 工作流架构

### Agent 舰队

```
┌──────────────────────────────────────────────────────┐
│                    Scout（侦察）                       │
│  搜索新发布、transcripts、文档、社区资源                │
└────────────────────┬─────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────┐
│              Orchestrator（编排 + 判断）                │
│  评分、路由、计划变更、驱动整个流水线                   │
└────────────────────┬─────────────────────────────────┘
                     │ 评分 ≥ 65 才通过
                     ▼
         ┌───────────┴───────────┐
         │                       │
         ▼                       ▼
┌────────────────┐    ┌────────────────┐
│ Researcher #1  │    │ Researcher #2  │   ← 并行
│ 验证信息在仓库 │    │ 找受影响的页面 │
└───────┬────────┘    └───────┬────────┘
        │                     │
        └──────────┬──────────┘
                   │
                   ▼
         ┌─────────────────┐
         │  Human Gate #1  │   ← 审批：新增/更新
         └────────┬────────┘
                  │
                  ▼
         ┌─────────────────┐
         │    Ingestor     │   ← 写入/更新 Wiki 页面
         └────────┬────────┘
                  │
                  ▼
         ┌─────────────────┐
         │     Linter      │   ← 校验格式、链接、新鲜度
         └────────┬────────┘
                  │
                  ▼
         ┌─────────────────┐
         │  Human Gate #2  │   ← 审批：删除内容
         └────────┬────────┘
                  │
                  ▼
         ┌─────────────────┐
         │  Git Commit      │   ← 分支隔离，可回滚
         └─────────────────┘
```

### 关键设计原则

```
两大核心：
  1. 共享看板（Shared Board）
     → 所有 Agent 通过 Kanban 通信，不直接互相对话
     → 状态透明、可追踪

  2. 判断循环中有人类把关
     → 关键决策点保留人类审批
     → 不是完全自主运行
```

---

## 五个 Agent 的职责分工

| Agent | 职责 | 模型选择（本视频） |
|-------|------|-------------------|
| **Scout** | 搜索新发布、transcripts、文档 | Grok（via OpenRouter） |
| **Orchestrator** | 评分、路由、驱动流水线 | GPT 5.5 |
| **Researcher** | 验证信息 + 找受影响的页面 | NVIDIA Neotron 3 Ultra（免费） |
| **Ingestor** | 写入/更新 Wiki 页面（摘要、概念、实体） | Minimax M3 |
| **Linter** | 校验格式、链接有效性、内容新鲜度 | NVIDIA Neotron 3 Ultra |

### 每个需要的资产

```
每个 Agent = 一个 Hermes Profile + 一个 Skill

Profile 包含：
  - 模型配置（model, provider, API key）
  - 环境变量（如 OpenRouter API key）

Skill 包含：
  - 明确的职责描述
  - 输入/输出规范
  - 工作边界和限制
```

---

## 判断评分机制

### 六维度评分

Orchestrator 对 Scout 发现的信息进行评分，总分 ≥ 65 才进入后续流程：

| 维度 | 说明 | 权重 |
|------|------|------|
| **Novelty** | 信息新颖度（是否已被覆盖） | 高 |
| **Source Confidence** | 来源可信度（官方 vs 社区 vs 未知） | 高 |
| **Scope Relevance** | 是否与目标领域直接相关 | 中 |
| **Version Relevance** | 是否涉及特定版本更新 | 低 |
| **Clarity Gain** | 是否能让知识更清晰准确 | 中 |

### 评分决策树

```
信息进来
  │
  ├─→ 总分 ≥ 65？
  │     ├─→ 是 → 进入研究阶段
  │     └─→ 否 → 丢弃，不浪费 Token
  │
  ├─→ 已在 Wiki 中覆盖？
  │     ├─→ 是 → Shelved（搁置）
  │     └─→ 否 → 继续
  │
  └─→ 来源可信度低？
        ├─→ 是 → 降分，可能被过滤
        └─→ 否 → 正常流程
```

---

## 路由决策

Orchestrator 对通过评分的信息进行四种路由：

| 路由 | 条件 | 行为 |
|------|------|------|
| **New Page** | 全新概念，Wiki 中无相关页面 | Ingestor 创建新页面 |
| **Update** | 已有页面需要补充新信息 | Ingestor 更新现有页面 |
| **Conflict** | 新信息与现有页面矛盾 | 暂停 → 人类裁决 |
| **Shelve** | 不值得处理 | 丢弃，不做任何操作 |

### Conflict 路由的重要性

```
Conflict 是最难处理的场景：
  - 需要判断是否真的矛盾（而非表述差异）
  - 人类必须参与裁决
  - 系统只负责标记，不做删除或覆盖
```

---

## 安全模型

### 三层防护

```
Layer 1: 分支隔离
  → 每次 ingest 在独立 Git 分支上操作
  → 出问题可以直接丢弃分支，不影响 main

Layer 2: 人类审批
  → Gate 1：审批新增/更新（进入前）
  → Gate 2：审批删除（移除旧内容前）
  → 审批通过 Telegram 推送，一键回复 "approve"

Layer 3: 原始资料不动
  → raw/ 目录永远不被修改
  → 只修改 wiki/ 目录
  → 如需回溯，原始资料始终可用
```

### CLAUDE.md 的三重职责

```
一个 markdown 文件做三件事：

1. Ingestor 写页面的规范（"按这个格式写"）
2. Linter 校验的标准（"按这个格式检查"）
3. 工作流文档本身（"这就是我们要自动化的流程"）

→ 所以这个领域几乎不需要额外逻辑代码
→ Linter 是从 CLAUDE.md 派生的确定性代码
```

---

## 实战构建过程

### 从模板开始

Tonbi 提供了两个开源资产：

| 资产 | 说明 |
|------|------|
| [hermes-multi-agent-workflow](https://github.com/tobistudio/hermes-multi-agent-workflow) | 多 Agent 工作流模板（看板配置、proposal actions、engine CLI） |
| [tobistudio/llm-wiki](https://github.com/tobistudio/llm-wiki) | LLM Wiki 结构模板 |

### 需要自定义的部分

```
从模板克隆后，需要修改的文件：

1. triage.yml          ← 切换到 knowledgebase 领域配置
2. 新建：kb_lint.py    ← 确定性校验脚本（Python）
3. 新建：kb_git.py     ← Git 操作辅助脚本
4. 新建：4 个 Skills    ← Scout/Researcher/Ingestor/Linter 各一个

不需要修改的部分：
  - engine CLI
  - proposal actions
  - KB 仓库结构
```

### 构建步骤

```bash
# Phase 1: 克隆模板
git clone https://github.com/tobistudio/hermes-multi-agent-workflow

# Phase 2: 构建 Linter（先用 Claude Code）
#  → 编写 kb_lint.py
#  → 测试：故意写坏链接验证检测
#  → Git init 知识库仓库

# Phase 3: 构建 Git helper
#  → 编写 kb_git.py（让 commit 阶段确定性化）

# Phase 4: 构建配置和 Skills
#  → 修改 triage.yml（评分规则、路由映射）
#  → 创建 4 个 Worker Agent Skills

# Phase 5: 在 Hermes 中执行 Runbook
#  → 读取 runbook 文件
#  → 创建 5 个 Profiles（每个 Agent 一个）
#  → 配置 API keys
#  → 创建 Kanban 看板
```

### ✅ 最佳实践

- ✅ Linter 先于工作流构建——先有验证标准，再自动化
- ✅ 用 Claude Code 构建 Python 脚本，用 Hermes Agent 运行工作流
- ✅ Runbook 一次执行，Hermes 自动创建所有 Profile 和看板
- ✅ API key 手动填入环境变量文件，不要在聊天中暴露
- ❌ 不要跳过 Linter 构建直接上工作流
- ❌ 不要让 Agent 自由操作 Git——用确定性脚本封装

---

## 多模型混合部署实验

### 模型配置

| Agent | 模型 | 提供方 | 费用 |
|-------|------|--------|------|
| Scout | Grok | OpenRouter | 付费 |
| Orchestrator | GPT 5.5 | OpenAI | 付费 |
| Researcher | Neotron 3 Ultra | OpenRouter | **免费** |
| Ingestor | Minimax M3 | OpenRouter | ~$0.95/批次 |
| Linter | Neotron 3 Ultra | OpenRouter | **免费** |

### 实验结论

```
多模型混合可行性：✅ 可行
  → Agent 之间不直接通信，全通过 Kanban 交互
  → 各自用不同模型完全没有互操作问题

开源模型表现：✅ 出乎意料地好
  → Neotron 3 Ultra（免费）完成了所有 Researcher 和 Linter 任务
  → Minimax M3（$0.95）完成了所有 Ingestor 任务
  → 没有遇到超时或 API 错误

结论：你不需要全用 GPT 5.5
  → 免费模型和开源权重模型同样能把活干完
  → Worker Agent 可以用便宜模型，Orchestrator 用贵模型
```

---

## 运行中观察到的行为

### 去重（Dedup）有效

```
场景：Hermes v15 Velocity Release
  → Scout 发现了 v15 更新
  → Researcher 检查 KB 后发现 Tonbi 已经手动更新了 v15
  → Orchestrator 标记为 "already covered"，Shelved
  → 证明了去重机制正常工作
```

### 并发写入冲突处理

```
场景：两个 Ingestor 同时要写同一个页面
  → 第一个 Ingestor 正在写入
  → 第二个 Ingestor 被自动暂停（Paused by operator）
  → 等第一个完成后，第二个自动恢复

状态信息："Another ingest branch is actively writing in the shared space."
→ 避免了 Git 冲突和文件覆盖
```

### 审批流程

```
Ingest Proposal 推送到 Telegram：
  ├── 来源信息
  ├── 变更评分（89/100）
  ├── 受影响页面列表
  └── 回复 "approve" 即可继续

→ 整个过程只需人类操作一次
→ 不会浪费 Token 在明显错误的内容上
```

---

## 关键收获

### 判断决策树：你是否需要这个工作流？

```
你有需要持续维护的知识库吗？
  ├─→ 否 → 不需要，手动维护即可
  └─→ 是 → 知识库更新频繁吗（每天/每周）？
        ├─→ 否 → 手动维护 + 偶尔更新
        └─→ 是 → 你用 Hermes Agent 吗？
              ├─→ 否 → 可参考架构，用其他工具实现
              └─→ 是 → ✅ 这个工作流适合你
```

### 核心架构总结

```
多 Agent 工作流成功的三个要素：

1. 共享看板（Shared Board）
   → Agent 之间零耦合，状态透明

2. 确定性检查（Deterministic Checks）
   → Linter 用代码验证，不用 AI 猜
   → Git 操作用脚本封装，不让 Agent 自由发挥

3. 人类在关键节点把关
   → 不是全自动，也不是全手动
   → 在最需要判断力的地方保留人类
```

---

## 参考资料

- [Hermes Multi-Agent Workflow Template (GitHub)](https://github.com/tobistudio/hermes-multi-agent-workflow)
- [Tonbi's AI Garage - YouTube](https://www.youtube.com/@TonbisAIGarage)
- [Hermes Agent Documentation](https://hermes-agent.nousresearch.com/docs)
- [tobistudio/llm-wiki (GitHub)](https://github.com/tobistudio/llm-wiki)

## 相关笔记

- [[Hermes Agent 多 Agent 架构]]
- [[Kanban 看板工作流]]
- [[LLM Wiki / Knowledge Base 构建]]
- [[Karpathy 风格知识库]]
