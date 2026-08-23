---
title: Agent 技能设计 — 从技能地狱逃生的工程指南
aliases: [Matt Pocock Writing Great Skills, Skill Hell, Agent 技能地狱]
tags:
  - ai-agent
  - prompt-engineering
  - skill-design
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=PH4gP42_b8g"
  - "https://github.com/mattpocock/skills/blob/main/skills/productivity/writing-great-skills/SKILL.md"
  - "https://github.com/mattpocock/skills"
author: Matt Pocock（原始演讲）/ wow 频道（中文拆解）
created: 2026-07-21
updated: 2026-07-21
description: Matt Pocock《Writing Great Skills》的系统化拆解 — 如何用工程化思维克制地设计 Agent 技能，避免"技能地狱"
level: intermediate
stars: 5
note: 视频字幕被创作者禁用，基于用户提供的 Content Insights + 原始 SKILL.md 全文 + 外部资料综合整理
---

# Agent 技能设计 — 从技能地狱逃生的工程指南

> Matt Pocock 的《Writing Great Skills》是关于 Agent 技能（Skill）设计的权威指南。
> 核心论点：优秀的 Agent 不是靠堆砌工具，而是靠克制地设计每一个技能。
> 本文综合 wow 频道的中文深度拆解视频与 Matt Pocock 原始 SKILL.md 全文。

## 目录

1. [现状困境：技能地狱](#现状困境技能地狱)
2. [第一关：触发机制（Invocation）](#第一关触发机制invocation)
3. [第二关：内部架构（Architecture）](#第二关内部架构architecture)
4. [第三关：引导与执行（Prompting）](#第三关引导与执行prompting)
5. [第四关：修剪与维护（Pruning）](#第四关修剪与维护pruning)
6. [Matt Pocock 的技能体系](#matt-pocock-的技能体系)
7. [核心术语速查](#核心术语速查)
8. [参考资料](#参考资料)

---

## 现状困境：技能地狱

### 盲目囤积 = 系统失控

很多开发者习惯给 Agent 塞入数百个技能工具（搜索、操作数据库、发邮件...），以为能创造"钢铁人"。但工具过多过杂，Agent 反而变成"无头苍蝇"，在简单任务中也会错误调用底层指令。

### 不可预测性是工程灾难

过多的技能文件使系统变得笨重、缓慢、不可预测。在严肃的软件工程中，不可预测性就是定时炸弹。

```
技能数量增长 → 上下文膨胀 → 注意力稀释 → 误调用 → 不可预测 → 系统失控
     ↑                                                      |
     └──────────── 用户以为"更多技能 = 更强 Agent" ─────────┘
```

### Matt Pocock 的核心论点

> A skill exists to wrangle determinism out of a stochastic system.
> （技能的存在，是为了从随机系统中驯服出确定性。）

**Predictability（可预测性）** 不是指每次输出相同，而是指每次走相同的 *过程*。这是所有技能设计原则的根基。

| 误区 | 正确理解 |
|------|----------|
| 技能越多越强 | 每个技能都有成本（上下文/认知负担） |
| AI 能自己判断该用什么 | 不加约束的自动调用是灾难源头 |
| 技能描述越详细越好 | 每个字都在消耗 token 和注意力 |
| 写完就不用管了 | 技能不维护会"沉积"成废弃补丁 |

---

## 第一关：触发机制（Invocation）

### 两条路线的根本对立

每个技能必须明确选择一种调用方式：

| 维度 | Model-Invoked（模型调用） | User-Invoked（用户调用） |
|------|--------------------------|--------------------------|
| 触发方式 | AI 自主判断条件后调用 | 用户手动输入指令名称 |
| 上下文成本 | 每轮都把 description 塞入窗口 | 零上下文成本 |
| 认知成本 | 零（AI 自己记得） | 高（用户必须记得存在） |
| 可预测性 | 低（AI 可能乱触发） | 高（完全人类掌控） |
| 跨技能可达 | 其他技能可以调用它 | 其他技能无法调用它 |
| 配置方式 | 省略 `disable-model-invocation` | 设置 `disable-model-invocation: true` |
| description 角色 | 模型可见的触发文本（含丰富关键词） | 仅人类可读的一句话摘要 |

### 选择决策树

```
该技能需要被其他技能调用吗？
├── 是 → Model-Invoked（必须保留 description）
│        └── 为 description 写丰富的触发短语
│            "Use when the user wants…, mentions…"
│
└── 否 → 它需要 AI 自动触发吗？
    ├── 是 → Model-Invoked
    │        （接受上下文成本换取自动化）
    │
    └── 否 → User-Invoked ✅ 推荐
             disable-model-invocation: true
             用 description 做一句话人类摘要
             不含触发关键词
```

### 实测效果：63% Token 缩减

Matt Pocock 在 v1 版本中大规模使用 `disable-model-invocation: true`，实现了 **63% 的技能描述 token 成本缩减**。这是一个巨大的改进，尤其对上下文有限的模型。

### 认知负担的解决方案：Router Skill

当 user-invoked 技能多到记不住时，用一个 **router skill**（如 Matt 的 `/ask-matt`）作为入口，它列出所有其他技能并说明何时使用哪个。用一个 user-invoked 技能管理认知负担，而非让模型承担。

```
用户输入 /ask-matt
     ↓
Router Skill 列出所有可用技能 + 触发条件
     ↓
用户选择 → 手动输入对应技能名 → 执行
```

---

## 第二关：内部架构（Architecture）

### 极简原则：两种内容类型

任何技能文件内部只有两种内容：

| 类型 | 作用 | 示例 |
|------|------|------|
| **Steps（步骤）** | 有序动作，每步以完成标准结尾 | PRD 生成：1.收集需求 → 2.填充模板 → 3.输出 |
| **Reference（参考资料）** | 定义、规则、事实，按需查阅 | 术语表、编码规范、决策矩阵 |

> Matt 原文：A skill is built from two content types — steps and reference — that mix freely.
> 一个技能可以全是步骤、全是参考资料、或两者混合。

### 信息层级（Information Hierarchy）

内容按"即时需要程度"分为三层：

```
┌─────────────────────────────────────────────┐
│  1. In-Skill Step（技能内步骤）               │  ← 最高优先级
│     SKILL.md 中的有序动作                     │     每步以完成标准结尾
│     可检查（checkable）+ 可穷尽（exhaustive） │
├─────────────────────────────────────────────┤
│  2. In-Skill Reference（技能内参考资料）       │  ← 按需查阅
│     SKILL.md 中的定义/规则/事实               │     合理的平级集合
├─────────────────────────────────────────────┤
│  3. External Reference（外部参考资料）         │  ← 最低优先级
│     推到 SKILL.md 之外的单独文件              │     仅指针触发时加载
│     通过 Context Pointer（上下文指针）访问     │
└─────────────────────────────────────────────┘
```

### Context Pointers（上下文指针）与懒加载

**核心思想**：不要把所有规范和模板硬塞进主文件。用路径指引（指针），仅在推理轨迹走到该分支时才动态拉取。

```
SKILL.md（主控文件，永远轻量）
  │
  ├── "若需更新词汇表 → 读取 vocabulary_template.md"
  │      ↑ 这就是 Context Pointer
  │      仅在实际需要时才加载 vocabulary_template.md
  │
  ├── "若需 API 规范 → 读取 api_spec.md"
  │
  └── "若需审查清单 → 读取 review_checklist.md"
```

### Progressive Disclosure（渐进式披露）

将内容从 SKILL.md 推到链接文件的动作。目的是让顶层保持可读。

**分支测试（Branching）** 是最干净的披露测试：
- 内联每个分支都需要的内容
- 将只有部分分支需要的推到指针后面

### 完成标准（Completion Criterion）

每个步骤必须以可检查的完成标准结尾：

```
❌ 模糊标准："生成变更列表"
   → 模型会偷懒，随便列几个就算完成

✅ 穷尽标准："每个修改过的模型都被记录"
   → 可验证：检查模型数量是否匹配
```

**Premature Completion（过早完成）** 是最常见的失败模式：模型在步骤还没真正完成时就宣布完成。防御方法：先锐化完成标准（便宜、局部）；只有在标准无法再锐化 *且* 观察到抢跑时，才用拆分（隐藏后续步骤）。

---

## 第三关：引导与执行（Prompting）

### 高密度术语 vs 大白话

大白话提示词缺乏强制约束力。专业术语（Leading Words）可以作为"钥匙"，唤醒模型预训练时储存的深层知识。

| 大白话提示 | Leading Word 提示 | 效果差异 |
|-----------|-------------------|----------|
| "要快速、确定性高、开销低" | *tight*（紧凑） | 一个词召回整个概念簇 |
| "一个你信赖的循环" | *red*（变红） | 模糊判断 → 二元可观测状态 |
| "端到端测试覆盖" | *vertical slice*（垂直切片） | 召唤敏捷开发 + TDD 深层知识 |
| "要彻底一些" | *relentless*（无情） | 比"thorough"更强，真正的行为改变 |

### Leading Words 的双重作用

```
Leading Word（如 "vertical slice"）
     │
     ├── 在 body 中 → 锚定执行（execution）
     │   模型每次遇到这个词都走相同行为
     │
     └── 在 description 中 → 锚定调用（invocation）
        当用户 prompt/docs/code 中出现相同词汇
        模型更可靠地链接到该技能并触发
```

> Matt 原文：Hunt for opportunities to refactor skills to use leading words.
> 假设每个技能都携带可以用 leading word 替换的重述 —— 去找它们。

### 拆分技能与"隐藏未来"

**问题**：模型如果过早看到最终目标（如"写计划"），会在前期需求澄清阶段敷衍了事。

**解决**：将前期阶段拆分为独立技能（如 `grill-me`），剥夺模型的全局上帝视角。

```
传统做法（模型看到全局）：
  需求澄清 → 设计 → 编码 → 测试
  ↑ 模型知道后面还有编码，会急于跳到"能跑的代码"
    导致需求澄清阶段敷衍

拆分做法（隐藏未来）：
  技能1: grill-me（只做需求澄清）
     ↓ 交棒
  技能2: design（只做设计）
     ↓ 交棒
  技能3: tdd（只做编码+测试）

  每个技能只看到当前环节，集中算力做好单一任务
```

### 何时拆分技能（Granularity）

两种拆分依据，每种都有成本：

| 拆分类型 | 触发条件 | 成本 |
|----------|----------|------|
| **By Invocation** | 有独立的 leading word 需要自动触发，或其他技能需要调用它 | 新增一个 always-loaded description（上下文成本） |
| **By Sequence** | 后续步骤的存在会诱惑模型抢跑当前步骤 | 维护多个技能文件（维护成本） |

---

## 第四关：修剪与维护（Pruning）

### DRY 原则

确保背景知识和调用模板有且仅有唯一引用源头。同一概念出现在三处（triad）就是 **Duplication（重复）**，应该 collapse 成一个 leading word。

### 清理提示词沉积物（Sediment）

团队为解决早期模型漏洞留下的历史补丁，在更强的新模型面前多已失效。继续保留只浪费上下文窗口并干扰判断。

```
Sediment（沉积物）的形成：

模型 A 时代 → 发现 bug → 添加提示词补丁
     ↓
模型 B 时代 → 补丁可能已无效，但没人敢删
     ↓
模型 C 时代 → 补丁层叠堆积 → 浪费 token + 干扰判断
     ↓
Sediment 的定义：因为添加感觉安全、删除感觉危险而沉积的旧层
```

### 删除测试（The No-op Test）

怀疑某句话是废话？直接删除它，跑一遍系统。

```python
# 删除测试伪代码
def no_op_test(sentence, skill, eval_suite):
    """
    测试一句话是否是 no-op（无效操作）
    """
    original_behavior = run(skill, eval_suite)
    
    # 删除这句话
    skill_without = skill.remove(sentence)
    new_behavior = run(skill_without, eval_suite)
    
    if new_behavior == original_behavior:
        # 行为没变 → 这句话是废话 → 永久删除
        return "DELETE: no-op confirmed"
    else:
        # 行为变了 → 保留
        return "KEEP: sentence changes behavior"
```

> Matt 原文：Run the no-op test on each sentence in isolation, and when one fails, delete the whole sentence rather than trim words from it. Be aggressive.

### 六大失败模式速查

| 失败模式 | 症状 | 防御 |
|----------|------|------|
| **Premature Completion** | 步骤没做完就宣布完成 | 锐化完成标准；若仍模糊则拆分隐藏后续步骤 |
| **Duplication** | 同一含义多处出现 | Collapse 成单一 leading word |
| **Sediment** | 旧补丁层叠堆积 | 定期修剪，执行删除测试 |
| **Sprawl** | 技能太长，即使每行都有效 | 用层级披露参考资料，按分支/序列拆分 |
| **No-op** | 模型默认行为已满足的指令 | 删除测试验证后永久删除 |
| **Negation** | "不要想大象"反而让人想大象 | 用正面表述（说该做什么，而非不该做什么） |

---

## Matt Pocock 的技能体系

### 项目概览

| 属性 | 值 |
|------|-----|
| 仓库 | https://github.com/mattpocock/skills |
| Stars | 173k |
| Forks | 14.8k |
| 下载量 | 4.2M+ |
| 定位 | Skills for Real Engineers（非 vibe coding） |
| 核心理念 | 小、易改、可组合、适配任何模型 |

### 技能分类

**Engineering（工程类）— User-Invoked**

| 技能 | 一句话描述 |
|------|-----------|
| `/triage` | 工单分类整理 |
| `/grill-with-docs` | 带文档的深度需求访谈（含共享语言构建） |
| `/setup-matt-pocock-skills` | 初始化配置 |

**Engineering — Model-Invoked**

| 技能 | 一句话描述 |
|------|-----------|
| `tdd` | 红绿重构循环，一次一个垂直切片 |
| `domain-modeling` | 主动构建和锐化项目领域模型 |
| `codebase-design` | 深模块设计：大量行为藏在小组件后 |
| `code-review` | 双轴审查（Standards + Spec），并行子代理 |
| `resolving-merge-conflicts` | 逐 hunk 解决合并冲突 |
| `diagnosing-bugs` | 诊断疑难 bug |

**Productivity（通用工具）— User-Invoked**

| 技能 | 一句话描述 |
|------|-----------|
| `grill-me` | 无情访谈直到决策树每个分支都解决 |
| `handoff` | 压缩当前对话为交接文档 |
| `teach` | 跨多次会话教授新技能/概念 |
| `writing-great-skills` | 编写和编辑技能的参考指南（本文主题） |

**Productivity — Model-Invoked**

| 技能 | 一句话描述 |
|------|-----------|
| `grilling` | grill-me 和 grill-with-docs 背后的可复用访谈循环 |

### 三大设计哲学

1. **The Agent Didn't Do What I Want** → 用 `/grill-me` 对齐需求
2. **The Agent Is Way Too Verbose** → 用 `CONTEXT.md` 建立共享语言
3. **The Code Doesn't Work** → 用反馈循环（类型、测试、浏览器）

```
问题诊断 → 解决方案映射：

沟通鸿沟 ───→ /grill-me 或 /grill-with-docs
                ↓
冗长啰嗦 ───→ CONTEXT.md（共享语言）
                ↓
代码不工作 ──→ tdd（红绿循环）+ 反馈回路
                ↓
技能失控 ───→ writing-great-skills（本指南）
```

---

## 核心术语速查

| 术语 | 英文 | 定义 |
|------|------|------|
| 可预测性 | Predictability | 每次走相同过程，而非产出相同结果 |
| 模型调用 | Model-Invoked | AI 自主判断触发 |
| 用户调用 | User-Invoked | 仅用户手动触发 |
| 上下文负担 | Context Load | description 每轮占据的窗口空间 |
| 认知负担 | Cognitive Load | 用户必须记住技能存在的脑力成本 |
| 上下文指针 | Context Pointer | 指向外部文件的路径指引，懒加载 |
| 渐进式披露 | Progressive Disclosure | 将内容推到链接文件以保持顶层可读 |
| 引导词 | Leading Word | 召唤模型预训练知识的紧凑概念词 |
| 垂直切片 | Vertical Slice | 端到端功能的一个完整薄片 |
| 完成标准 | Completion Criterion | 判断步骤是否完成的可检查条件 |
| 过早完成 | Premature Completion | 步骤未做完就宣布完成 |
| 腿功 | Legwork | 模型在工作中进行的深入挖掘 |
| 拆分粒度 | Granularity | 技能划分的精细程度 |
| 重复 | Duplication | 同一含义多处出现 |
| 沉积 | Sediment | 旧补丁因"删除感觉危险"而堆积 |
| 蔓延 | Sprawl | 技能太长，即使每行都有效 |
| 无效操作 | No-op | 模型默认行为已满足的指令 |
| 否定 | Negation | 用禁止表述反而强化被禁止的行为 |
| 单一真相源 | Single Source of Truth | 每个含义只有一个权威位置 |
| 路由技能 | Router Skill | 列出所有技能的管理入口 |

---

## 参考资料

- [视频：从技能地狱中逃生（wow 频道中文拆解）](https://www.youtube.com/watch?v=PH4gP42_b8g)
- [Matt Pocock 原始 SKILL.md（Writing Great Skills）](https://github.com/mattpocock/skills/blob/main/skills/productivity/writing-great-skills/SKILL.md)
- [Matt Pocock Skills 仓库](https://github.com/mattpocock/skills)
- [Skills v1 更新公告：63% Token 缩减](https://www.aihero.dev/skills/skills-changelog-v1-announcement)
- [Matt Pocock Writing Great Skills 分析（remio.ai）](https://www.remio.ast/post/matt-pocock-shares-writing-great-skills-guide-for-predictable-ai-agents)

## 相关笔记

- [[AI Agent 架构模式]]
- [[Prompt Engineering 最佳实践]]

---

*文档生成时间：2026-07-21*
*基于 Matt Pocock Skills v1.1.0 + wow 频道深度拆解视频*
