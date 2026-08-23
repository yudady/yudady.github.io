---
title: Claude Code Dynamic Workflows 何时使用
aliases:
  - Claude Code 动态工作流
  - Dynamic Workflows
tags:
  - claude-code
  - ai-agent
  - multi-agent
  - workflow
  - status/active
  - type/doc
source:
  - "https://youtube.com/watch?v=zt-9LP-flUU"
  - "https://claude.com/blog/a-harness-for-every-task-dynamic-workflows-in-claude-code"
  - "https://heymaibao.com/claude-code-dynamic-workflows-when-to-use/"
author: 脈報 (思思主播) / Anthropic (Thariq Shihipar)
created: 2026-06-03
updated: 2026-06-03
description: |
  Claude Code 推出 Dynamic Workflows 功能，让 Claude 即时自写多代理协作框架。但 Anthropic 作者反而提醒用户想清楚再用，因为 token 成本显著增加。
level: intermediate
stars: 4
note: 无字幕，基于视频元数据 + 博客全文综合整理
---

# Claude Code Dynamic Workflows 何时使用

> 这篇笔记整理 Anthropic 发布的 Dynamic Workflows 功能，以及中文频道「脈報」的解读。核心观点：**Dynamic Workflows 本质是用结构买品质，代价是 token——不该是默认选项，留给真正扛不动的硬任务。**

## 目录

- [[#什么是 Dynamic Workflows]]
- [[#为什么需要：单一对话的三大失败模式]]
- [[#六种核心 Pattern]]
- [[#适用场景]]
- [[#作者为什么叫你别乱用]]
- [[#如何开始]]

---

## 什么是 Dynamic Workflows

### Harness 是什么

Harness（工作框架）是 Claude Code 做事时依循的「外框」，包含可用工具、任务拆解方式和推进结构。

```
默认 Harness (为写代码设计)
  │
  ├─ 简单编程任务 → 直接用，效果好
  │
  └─ 复杂任务 (研究/安全分析/代码审查)
       │
       └─ 以前：手动刻一套专用框架 (静态工作流)
          现在：Claude 自己即时生成 (动态工作流)
```

### 动态 vs 静态工作流

| 特性 | 静态工作流 (Static) | 动态工作流 (Dynamic) |
|------|---------------------|----------------------|
| 定义方式 | Claude Agent SDK / 手写 | Claude Opus 4.8 即时生成 |
| 适用范围 | 通用，覆盖所有边缘情况 | 专为你这个任务量身定制 |
| 框架质量 | 较笨重，需预设所有分支 | 更精准，按需组合 |
| 灵活性 | 低，改需求需重新编排 | 高，Claude 自主调整 |
| 门槛 | 需要编程能力 | 自然语言触发即可 |

### 触发方式

- 直接告诉 Claude 创建一个工作流
- 使用触发词：`/goal` 或 `/loop`
- 触发词 `ultracode` 也可使用

### 技术实现

Dynamic Workflows 本质是一个 **JavaScript 文件**，包含：
- 特殊函数：生成和协调子代理 (subagent)
- 标准 JS 能力：JSON、Math、Array 等数据处理
- 模型选择：为每个子代理指定不同模型（聪明但慢 vs 快但弱）
- 工作区隔离：可选让子代理在独立 worktree 中运行
- **可中断可恢复**：中途关掉终端，重开能从断点继续

---

## 为什么需要：单一对话的三大失败模式

这是整篇最有价值的部分。默认 Claude Code 在同一个对话视窗里同时做规划和执行，多数编程任务没问题，但面对长任务会出现三种退化：

### 失败模式一览

```
┌─────────────────────────────────────────────────────┐
│           单一对话窗口的三大失败模式                    │
├───────────────┬─────────────────────────────────────┤
│ 1. 偷懒退化    │ 任务越长，后期步骤越粗糙               │
│               │ (context 撑不住 → 省略细节)             │
├───────────────┼─────────────────────────────────────┤
│ 2. 自我偏心    │ 自己写的代码自己审查，缺乏客观性          │
│               │ (同一 agent 既做又审 → 盲区)            │
├───────────────┼─────────────────────────────────────┤
│ 3. 走神偏题    │ 长对话中注意力漂移，偏离原始目标          │
│               │ (context 污染 → 跑偏)                  │
└───────────────┴─────────────────────────────────────┘
```

### 解决方式

开一个 Workflow → 每个子代理有独立的、干净的对话窗口：
- 不会被别人的内容污染
- 不会在一个越来越长的上下文里退化
- 多个独立脑袋互相牵制，把品质下限拉高

---

## 六种核心 Pattern

这六种 pattern 可以自由组合，构成判断「何时该用」的心智模型。

### Pattern 1: Classify-and-Act（分类执行）

```
输入任务
  │
  ├─ Classifier Agent (判断任务类型)
  │     │
  │     ├─ 类型 A → Agent A (专用处理)
  │     ├─ 类型 B → Agent B (专用处理)
  │     └─ 类型 C → Agent C (专用处理)
```

**适合**：需要根据输入特征路由到不同处理逻辑的场景。

### Pattern 2: Fan-Out-and-Synthesize（扇出聚合）

```
大任务
  │
  ├─ 子任务 1 → Agent 1 ─┐
  ├─ 子任务 2 → Agent 2 ─┤
  ├─ 子任务 3 → Agent 3 ─┼─→ Synthesizer Agent (汇总)
  └─ 子任务 N → Agent N ─┘
```

**关键**：Synthesizer 作为 barrier，等所有子代理完成后合并结构化输出。每个子步骤在干净、不干扰的 context window 中执行。

**适合**：大迁移、大重构、多模块并行修改。

### Pattern 3: Adversarial Verification（对抗验证）

```
Agent (执行任务)
  │
  └─→ Verifier Agent (按标准对抗审查)
        │
        ├─ Pass → 输出结果
        └─ Fail → 反馈 → Agent 重做
```

**适合**：代码审查、安全审计、质量把关。

### Pattern 4: Generate-and-Filter（生成过滤）

```
Brainstorm Agents (大量生成想法)
  │
  └─→ Filter Agent (按标准筛选)
        │
        ├─ 去重
        ├─ 验证
        └─ 只保留最高质量、已验证的
```

**适合**：创意发散后需要严格筛选的场景。

### Pattern 5: Tournament（锦标赛）

```
Task ──→ Agent 1 (方法 A) ──┐
      ├─ Agent 2 (方法 B) ──┼─→ Judge Agent (两两比较)
      ├─ Agent 3 (方法 C) ──┤       │
      └─ Agent N (方法 N) ──┘       └─→ Winner
```

**适合**：排序大量数据（1000+ 条目，单一 prompt 质量会退化）。

### Pattern 6: Loop Until Done（循环直到完成）

```
┌─→ Hypothesis Agent (生成假设)
│       │
│       └─→ Verification Agent (验证)
│               │
│               ├─ 有新发现 → 继续循环
│               └─ 无新发现 / 无新错误 → 停止
└───────┘
```

**适合**：根因分析、bug 排查、日志挖掘——不知道要跑几轮才能找到答案。

---

## 适用场景

### 编程相关

| 场景 | 推荐 Pattern | 说明 |
|------|-------------|------|
| 大规模迁移/重构 | Fan-Out + Adversarial | 每个模块一个 worktree，独立修改 + 对抗审查后合并 |
| 代码审查 | Adversarial Verification | 独立 agent 审查，避免自我偏心 |
| 安全分析 | Adversarial + Quarantine | 读不可信内容的 agent 隔离，禁止执行高权限操作 |
| 评估 (Evals) | Tournament / Fan-Out | 多 agent 输出 → 比较 agent 按标准打分 |

### 非编程场景（作者说反而更有用）

| 场景 | 推荐 Pattern | 说明 |
|------|-------------|------|
| 深度研究 | Fan-Out + Verify | 并行搜索 → 获取来源 → 对抗验证 → 综合报告 |
| 大量任务分流 | Classify-and-Act + Loop | 分类、去重、执行/升级，配合 quarantine 模式 |
| 根因调查 | Loop + Adversarial | 多个 agent 从不同证据源生成假设，面板式验证 |
| 规则挖掘 | Fan-Out + Tournament | 从历史会话中挖掘常见修正 → 聚类 → 验证 → 提炼为规则 |

### Quarantine（隔离）安全设计

```
不可信外部内容
  │
  └─→ Read-Only Agent (只读，不可执行)
        │
        └─→ 提取结构化信息
              │
              └─→ Action Agent (可执行高权限操作)
                    │
                    └─→ 基于已清洗的信息做决策
```

防止恶意内容通过 agent 链操纵高权限操作。

---

## 作者为什么叫你别乱用

Anthropic 技术成员 Thariq Shihipar 亲自发文，讲完功能有多强之后，主动踩刹车。

### 核心提醒

| 事实 | 说明 |
|------|------|
| token 成本显著增加 | 多个子代理 = 多份 context = 成倍 token 消耗 |
| 不是所有任务都需要 | 多数传统编程任务，默认 harness 就够了 |
| 最佳实践还在发展 | 功能很新，用法和边界仍在摸索 |
| 强项不在加速 | 在于用多独立脑袋互相牵制，拉高品质下限 |

### 什么时候用 / 什么时候不用

```
需要 Dynamic Workflow 如果你：
  ✅ 任务需要多个独立 agent 互相验证/协作
  ✅ 单一对话会出现偷懒退化、自我偏心、走神偏题
  ✅ 任务需要大量并行处理（迁移、批量审查）
  ✅ 任务是对抗性的（安全分析、红队测试）
  ✅ 任务有明确的停止条件（Loop Until Done）

不需要 Dynamic Workflow 如果你：
  ❌ 就是普通的日常编程任务
  ❌ 任务简单到一个 agent 就能搞定
  ❌ 你不确定是否需要 → 先不用，默认模式试了再说
  ❌ token 预算有限且任务非关键
```

脈報作者的总结：**Dynamic Workflows 的本质，是用「结构」去买「品质」，而代价是 token。下次想开之前先问一句话——这个任务，真的需要更多算力吗？**

---

## 如何开始

### 触发命令

```bash
# 在 Claude Code 中
/goal    # 目标导向的工作流
/loop    # 循环执行直到完成
ultracode # 触发词
```

### 实际使用建议

1. 先用默认模式做一遍，看效果如何
2. 如果遇到「一个 agent 搞不定」的信号（质量退化、遗漏、偏心），再考虑 workflow
3. 从简单 pattern 开始（Fan-Out 或 Adversarial），不要一上来就组合所有 pattern
4. 关注 token 消耗，用 `--cost` 或对应工具监控

---

## 参考资料

- [A Harness for Every Task: Dynamic Workflows in Claude Code (Anthropic 官方博客)](https://claude.com/blog/a-harness-for-every-task-dynamic-workflows-in-claude-code)
- [Claude Code 會自己寫工作流了，但作者反而叫你別亂用 (脈報/思思主播)](https://heymaibao.com/claude-code-dynamic-workflows-when-to-use/)
- [YouTube 视频：Claude Code 會自己寫工作流了，但作者反而叫你別亂用](https://youtube.com/watch?v=zt-9LP-flUU)
