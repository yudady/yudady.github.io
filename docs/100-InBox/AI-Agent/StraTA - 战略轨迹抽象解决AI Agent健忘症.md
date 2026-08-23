---
title: StraTA - 用战略轨迹抽象解决 AI Agent 健忘症
aliases: [StraTA, Strategic Trajectory Abstraction, AI Agent 强化学习]
tags:
  - ai-agent
  - reinforcement-learning
  - paper-reading
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=JyaPt5z4EP8"
  - "https://arxiv.org/abs/2605.06642"
author: Xiangyuan Xue, Yifan Zhou, Zidong Wang 等
created: 2026-05-16
updated: 2026-05-16
description: StraTA 通过在 Agentic RL 中引入显式的轨迹级战略规划，让 7B 模型在 SciWorld 上超越 Claude Sonnet
level: intermediate
stars: 4
---

# StraTA - 用战略轨迹抽象解决 AI Agent 健忘症

> AI Agent 在长程任务中"走一步忘一步"是当前最大的痛点之一。StraTA 提出了一个朴素的解法：先画地图再出发。通过分层 GRPO 训练，让 7B 开源模型在科学推理、网页购物等场景中碾压闭源巨头。

---

## 核心问题：AI Agent 的"健忘症"

当前主流 AI Agent 的工作模式是**反应式（Reactive）**：每一步都根据当前状态决定下一步做什么，但缺乏对全局目标的持续记忆。

**典型症状：**
- 给 Agent 一个复合指令（查数据 → 做表格 → 写邮件），它执行完前两步就忘了第三步
- 追问时 Agent 会重新分析，甚至推翻前面已完成的工作
- 越复杂的任务，遗忘率越高

**根本原因：** 纯反应式决策在长程任务中面临两个致命问题：
1. **探索不足（Shortsighted Exploration）** — 没有"地图"指引，Agent 容易陷入局部最优
2. **信用分配灾难（Credit Assignment Problem）** — 任务成功/失败后，无法判断哪一步的决策是关键

---

## StraTA 的核心思路

**一句话：在执行动作之前，先生成一段全局战略（Strategy），然后用这个战略约束后续所有执行步骤。**

```
用户输入
    |
    v
+-------------------+
| 战略生成器 (Policy) | --> 输出一段 Strategy（紧凑文本）
+-------------------+         |
                              v
                    +-------------------+
                    | 动作执行器 (Policy) | --> 每一步都参考 Strategy
                    +-------------------+
```

这就像出门旅行先做攻略 vs 走到哪算哪。

---

## 技术架构：分层 GRPO 训练

### GRPO 回顾

GRPO（Group Relative Policy Optimization）是 DeepSeek 提出的强化学习算法，无需 Critic 网络，直接用组内相对排名计算奖励。StraTA 将其扩展到 Agent 多回合场景。

### 分层设计

StraTA 将训练分为两层，联合优化：

| 层级 | 角色 | 输入 | 输出 |
|------|------|------|------|
| **高层** | 战略生成器 | 任务初始状态 | 紧凑的 Strategy 文本 |
| **低层** | 动作执行器 | 当前状态 + Strategy | 具体动作 |

**训练流程：**

```
1. 从任务初始状态采样多个 Strategy
2. 对每个 Strategy，执行完整的动作轨迹 (Trajectory)
3. 用任务最终奖励同时更新两层：
   - 高层：这个 Strategy 好不好？
   - 低层：在这个 Strategy 下，每步动作好不好？
```

**关键创新：** 低层策略的 reward 不只看动作本身，还参考高层 Strategy 的质量，解决了信用分配问题。

---

## 三个核心组件

### 1. 多样化战略采样（Farthest Point Sampling）

**问题：** 如果所有 Strategy 都差不多（同质化），Agent 学不到真正好的策略。

**解法：** 借用 3D 几何中的**最远点采样**算法。

```
已有策略池: S1, S2, S3 ...

最远点采样:
  1. 随机选 S1 作为第一个
  2. 选距离 S1 最远的 S2
  3. 选距离 {S1, S2} 都最远的 S3
  4. 重复直到采够 N 个

距离度量: 策略文本的 Embedding 余弦距离
```

这确保了策略的多样性，逼迫 Agent 探索不同的解题路径。

### 2. 关键自我反思（Critical Self-Judgment）

**问题：** Agent 需要能判断自己执行得好不好。

**解法：** 在轨迹结束后，让模型自我评估：

```
反思 Prompt:
"回顾你刚才的执行过程，指出哪些步骤可能出了问题，
并给出改进建议。"
```

反思结果被用作辅助奖励信号，帮助模型从失败中学习。

### 3. 分层 GRPO Rollout

```
采样阶段:
  For each task:
    1. 高层生成 K 个 Strategy（最远点采样保证多样性）
    2. 对每个 Strategy，低层执行 G 条轨迹
    3. 每条轨迹获得环境奖励 reward

优化阶段:
  高层 GRPO: 比较 K 个 Strategy 对应的轨迹平均奖励
  低层 GRPO: 比较同一 Strategy 下 G 条轨迹的奖励
  两层共享奖励信号，但信用归因不同
```

---

## 实验结果

### 核心数据

| 环境 | 基线 (GRPO) | StraTA | 提升 |
|------|------------|--------|------|
| **ALFWorld**（家务模拟） | 89.5% | **93.1%** | +3.6pp |
| **WebShop**（网页购物） | 5.3% → 训练后 59.3% | **84.2%** | 从 5.3% 到 84.2%，**15 倍提升** |
| **SciWorld**（科学推理） | ~50% | **63.5** | 超越 Claude Sonnet (57.4) |

### 与闭源模型对比（SciWorld）

| 模型 | 类型 | SciWorld 得分 |
|------|------|--------------|
| Claude Sonnet | 闭源 | 57.4 |
| GPT-4o | 闭源 | ~52 |
| **StraTA (7B)** | 开源 | **63.5** |

> 一个 7B 参数的开源小模型，在科学推理基准上超越了所有闭源模型。

### 样本效率

StraTA 在更少的训练轮次下达到更高的性能，证明战略抽象有效减少了无意义的探索。

---

## 落地启示

### 应用层开发者（不花算力）

在 Workflow 顶部加一个"全局战略规划器"节点：

```
用户输入
    |
    v
[LLM 节点: 生成 Strategy]  <-- 新增，唯一职责是输出策略
    |
    v
[Agent 节点: 执行任务]       <-- System Prompt 注入 Strategy
```

**最佳实践：**

- ✅ Strategy 节点使用独立 prompt，明确要求输出紧凑的战略规划
- ✅ 将 Strategy 作为系统级上下文注入所有后续 Agent 节点
- ✅ 简单架构改动，零额外算力成本
- ❌ 不要让同一个 LLM 同时负责规划和执行

### 模型层从业者

- **SFT 的天花板已现** — 人工标注的 step-by-step 数据昂贵且有限
- **仿生环境 + 规则奖励 + RL 是王道** — 构建好的仿真环境，写好打分规则，让模型自己在环境中试错学习
- StraTA 的分层 GRPO + 自我反思框架是一个可复用的范式

---

## 局限性

| 局限 | 说明 |
|------|------|
| **策略静态不变** | 开局生成的 Strategy 贯穿全程，无法应对环境突变（如网页 404、接口下线） |
| **仿真器与现实差距** | ALFWorld/WebShop 是理想化环境，真实互联网有延迟、弹窗、反爬等干扰 |
| **策略质量瓶颈** | 如果高层生成了错误的 Strategy，低层执行越好可能偏离目标越远 |

> "每个人在被一拳打到脸上之前，都有一个完美的计划。" — Mike Tyson

---

## 哲学洞察：什么是高级智能？

StraTA 讨论的核心命题：**真正的智能不只是对眼前刺激做出反应，而是建立一种"意向性"（Intentionality）** — 在大脑中构建一个尚未发生的未来愿景，并在通往愿景的漫长道路上，用这个愿景对抗每一个当下的局部最优。

这不是代码的升级，是对机器认知模型的一次重塑。

---

## 参考资料

- [StraTA: Incentivizing Agentic Reinforcement Learning with Strategic Trajectory Abstraction (arXiv:2605.06642)](https://arxiv.org/abs/2605.06642)
- [StraTA 官方代码 (GitHub)](https://github.com/xxyQwQ/StraTA)
- [视频：碾压 Claude！7B 开源小模型成功率暴涨 15 倍](https://www.youtube.com/watch?v=JyaPt5z4EP8) by wow.哇
