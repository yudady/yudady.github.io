---
title: Agent Arena 新榜单 — AI 评测从智商测试到工作能力测试
aliases:
  - Agent Arena 榜单
  - AI Agent 评测标准变化
tags:
  - ai-benchmark
  - ai-agent
  - llm-ranking
  - status/active
  - type/learning-note
source:
  - "https://www.youtube.com/watch?v=6L69632__SE"
  - "https://arena.ai/leaderboard/agent"
  - "https://wavespeed.ai/blog/posts/glm-5-1-vs-claude-gpt-gemini-deepseek-llm-comparison/"
author:
  - AI元点观察（视频作者）
note: 无字幕，基于视频元数据 + Agent Arena 官方榜单 + 外部资料综合整理
created: 2026-06-07
updated: 2026-06-07
description: Agent Arena 是首个衡量 AI Agent 实际工作能力的评测榜单，用真实用户任务取代传统静态问答，标志 AI 评测从"智商测试"转向"工作能力测试"。
level: intermediate
stars: 4
---

# Agent Arena 新榜单 — AI 评测从智商测试到工作能力测试

> **核心变化**：模型时代拼的是智商，Agent 时代拼的是组织能力。Arena 发布的 Agent Arena 榜单不再只比较模型回答好不好，而是看 AI Agent 在真实任务里能不能完成工作、调用工具、修复错误、接受用户纠正，并最终交付结果。

---

## Agent Arena 是什么？

Agent Arena（arena.ai/leaderboard/agent）是 LMSYS/Arena Intelligence 推出的 **Agent 能力专属排行榜**，与传统的 Chatbot Arena 有本质区别：

| 维度 | Chatbot Arena | Agent Arena |
|------|---------------|-------------|
| 评测方式 | 静态问答（模型回答好不好） | 真实任务（模型能不能做） |
| 评估维度 | 语义质量、帮助度、流畅度 | 工具调用、错误修复、任务完成、可操控性 |
| 数据来源 | Crowdsourced 对比投票 | 真实 Agent Mode 使用会话 |
| 核心问题 | "模型会不会说？" | "模型能不能做？" |
| 截止 2026-05-30 | 数百万次投票 | 349,257 sessions / 18 models |

---

## 榜单排名（2026-05-30）

### 综合排名（Net Improvement）

```
  Rank  Model                        Score         Labs
  ────  ──────────────────────────  ────────────  ─────────
   #1   GPT 5.5 (High)              10.66% ±1.60  OpenAI
   #2   Claude Opus 4.7 (Thinking)   9.47% ±1.50  Anthropic
   #3   GPT 5.4 (High)               8.92% ±1.68  OpenAI
   #4   Claude Opus 4.6               8.14% ±1.46  Anthropic
   #5   GPT 5.5                       7.47% ±1.54  OpenAI
   #6   Claude Opus 4.7                6.95% ±1.46  Anthropic
   #7   Claude Sonnet 4.6              4.59% ±1.37  Anthropic
   #8   GLM 5.1                        3.38% ±2.00  Z.ai (MIT 开源)
   #9   Gemini 3.1 Pro Preview         1.38% ±1.45  Google
  #10   Kimi K2.6                      0.56% ±1.64  Moonshot
```

**关键发现**：
- **GPT 5.5 (High)** 综合第一，在多个分项信号中也领跑
- **Claude Opus 4.7 (Thinking)** 紧随其后，Thinking 模式在 Agent 场景中价值显著
- **GLM 5.1** 是唯一进入 Top 10 的国产/开源模型，排名第 8
- Google Gemini 表现意外靠后，仅排第 9-10

---

## 五大评测信号解读

Agent Arena 引入了五个细粒度信号，比单一总分更能反映模型的真实能力：

### 信号 1：确认成功率（Confirmed Success）

> 模型多常让用户确认任务已完成

```
  #1  Claude Opus 4.7 (Thinking)  7.95%    ← 稳定性之王
  #2  Claude Opus 4.7              7.17%
  #3  GPT 5.5 (High)               7.06%
  #6  GLM 5.1                       4.63%    ← 国产最佳
```

**洞察**：Claude Thinking 模式在任务确认率上遥遥领先。Thinking 模式的"慢思考"让它在 Agent 场景中更能确保任务真正完成，而非半途而废。

### 信号 2：用户满意度（Praise vs Complaint）

> 模型获得的正面评价比负面评价多多少

```
  #1  GPT 5.5 (High)              14.95%    ← 用户体验最好
  #2  Claude Opus 4.7 (Thinking)   12.18%
  #3  GPT 5.4 (High)                9.72%
  #7  GLM 5.1                        3.79%
```

**洞察**：GPT 5.5 (High) 在用户体验上明显领先，正面评价比第二名高近 3 个百分点。

### 信号 3：可操控性（Steerability）

> 模型多大程度遵循用户指令方向

```
  #1  GPT 5.5 (High)              12.03%    ← 最听话
  #2  GPT 5.4 (High)               10.12%
  #3  Claude Opus 4.7 (Thinking)    9.04%
  #7  Gemini 3.1 Pro Preview       4.33%
```

**洞察**：OpenAI 模型在遵循指令方面表现最好。Agent 场景中"听懂并执行用户要求"是核心能力。

### 信号 4：命令恢复（Bash Recovery）

> 命令失败后模型多快能恢复

```
  #1  GPT 5.5 (High)              17.73%    ← 最强纠错
  #2  Claude Sonnet 4.6            17.23%    ← 性价比之选
  #3  Claude Opus 4.7 (Thinking)   16.69%
  #8  GLM 5.1                       10.37%
```

**洞察**：Sonnet 4.6 在纠错能力上与 Opus 4.7 (Thinking) 几乎持平，考虑到 Sonnet 的低价格，在 Agent 场景中性价比极高。

### 信号 5：工具幻觉（Tool Hallucination）

> 模型多大程度会"发明"不存在的工具

```
  #1  GPT 5.5                      1.52%    ← 最低幻觉
  #2  Kimi K2.6                      1.52%
  #3  Minimax M2.7                   1.52%
  #5  GLM 5.1                        1.52%
```

**洞察**：顶级模型的工具幻觉率都在 1.5% 左右，差异不大。这个维度目前区分度较低。

---

## GLM 5.1 — 国产模型的突破信号

GLM 5.1（智谱 AI）在 Agent Arena 中排第 8，是国产/开源模型的最高名次。

### 技术规格

| 规格 | 数值 |
|------|------|
| 总参数 | 744B（MoE 架构） |
| 活跃参数 | 40-44B / token |
| 专家架构 | 256 experts，8 active / token |
| Context window | 200K tokens |
| 训练数据 | 28.5T tokens |
| 训练硬件 | 100,000 颗华为昇腾 910B |
| 协议 | MIT（开源权重） |

### 核心亮点

- **编码能力达到 Claude Opus 4.6 的 94.6%**（45.3 vs 47.9 coding score）
- **SWE-bench 77.8%**，与 Claude Opus 4.6（80.8%）仅差 3 个百分点
- **Chatbot Arena 中开源模型第一**（ELO 1451）
- **完全脱离 Nvidia 硬件训练**，地缘政治意义重大
- **价格极具竞争力**：$1.00 / $3.20 per 1M tokens（Opus 4.6 是 $15/$75）

### Agent Arena 中 GLM 5.1 的表现

| 信号 | GLM 5.1 | 第一名（对比） |
|------|---------|---------------|
| 确认成功 | 4.63% | 7.95%（Claude T） |
| 用户满意度 | 3.79% | 14.95%（GPT 5.5H） |
| 可操控性 | 未进前十 | 12.03%（GPT 5.5H） |
| 命令恢复 | 10.37% | 17.73%（GPT 5.5H） |
| 工具幻觉 | 1.52% | 1.52%（并列） |

**评价**：GLM 5.1 在综合排名中挤入 Top 10，但在各分项信号中与头部模型差距明显。视频描述中的"靠成本撕口子"是准确判断 — GLM 5.1 的竞争力主要在性价比（开源 + 低价格 + 接近前沿的能力），而非单点能力领先。

---

## 评测范式的转变

### 从 Chatbot 到 Agent 的关键差异

```
  Chatbot Arena                    Agent Arena
  ─────────────                    ────────────
  输入 → 文字问答                    输入 → 真实任务
  输出 → 文字回答                    输出 → 工具调用 + 代码执行 + 修正
  评分 → 人类对比投票                评分 → 多维信号追踪
  能力 → 语言质量                    能力 → 组织协调 + 纠错 + 完成度
```

### 这意味着什么

1. **"说得好"不再等于"做得好"** — Chatbot Arena 的排名格局在 Agent Arena 中出现明显洗牌
2. **Thinking 模式的价值被验证** — Claude Opus 4.7 (Thinking) 在确认成功率上遥遥领先
3. **纠错能力成为关键分水岭** — 命令恢复（Bash Recovery）是区分"能用"和"好用"的重要信号
4. **成本竞争开辟新维度** — Sonnet 4.6 在纠错能力上接近 Opus 4.7，但价格低得多

### 选型决策树

```
  Agent 使用场景
     │
     ├─ 追求最高成功率 → Claude Opus 4.7 (Thinking)
     │
     ├─ 追求最佳用户体验 → GPT 5.5 (High)
     │
     ├─ 预算有限但需要前沿能力 → Claude Sonnet 4.6（性价比）
     │
     ├─ 开源 / 自部署需求 → GLM 5.1（MIT 协议）
     │
     └─ 成本极度敏感 → DeepSeek V3.2（$0.27/$1.10）
```

---

## 参考资料

- [Agent Arena 榜单 - arena.ai](https://arena.ai/leaderboard/agent)
- [GLM-5.1 vs Claude, GPT, Gemini, DeepSeek 对比 - WaveSpeed](https://wavespeed.ai/blog/posts/glm-5-1-vs-claude-gpt-gemini-deepseek-llm-comparison/)
- [Agent Arena 方法论 - arena.ai](https://arena.ai/blog/agent-methodology)
- [GLM-5.1 Code Arena 排名 - LinkedIn](https://www.linkedin.com/posts/arenaai_with-glm-51-zai-maintains-the-1-open-model-activity-7448409142949515264-rGTP)

## 相关笔记

- [[AI-Plan-Mode-四步工作流]]
- [[GLM-5-1-技术分析]]
- [[LLM-选型指南]]
