---
title: 9arm-skills — Claude Code 工程纪律技能包
aliases:
  - 9arm skills
  - nine arm skills
tags:
  - ai/agent
  - tool/claude-code
  - engineering/debugging
  - status/active
  - type/notes
  - source/youtube
source:
  - "https://www.youtube.com/watch?v=VjwBI1nWsM8"
  - "https://github.com/thananon/9arm-skills"
author: "Thananon（GitHub: thananon）"
created: 2026-05-23
updated: 2026-05-23
description: 一套为 Claude Code 设计的工程纪律技能包，用「更好的约束」而非「更多能力」来解决 AI Agent 的典型失败模式
level: beginner
stars: 4
---

# 9arm-skills — Claude Code 工程纪律技能包

> 一个小型 GitHub 仓库，不是框架、不是 IDE、不是 Claude Code 替代品。它是一组**聚焦的行为模板**，告诉 Agent 在关键时刻该慢下来——因为好的 Agent 行为不只是给模型更多能力，有时候是告诉它什么时候该克制。

---

## 目录

- [核心理念：约束比能力更重要](#核心理念約束比能力更重要)
- [四大 Shipable Skills](#四大-shipable-skills)
- [Debug Mantra — 调试四步法](#debug-mantra--調試四步法)
- [Postmortem — 工程事故记录](#postmortem--工程事故記錄)
- [Scrutinize — 冷眼审查](#scrutinize--冷眼審查)
- [Management Talk — 跨频道翻译](#management-talk--跨頻道翻譯)
- [四大技能形成完整闭环](#四大技能形成完整閉環)
- [在 Verdant 中的落地思路](#在-verdant-中的落地思路)
- [Agent 的典型失败模式](#agent-的典型失敗模式)

---

## 核心理念：约束比能力更重要

大多数人在改善 AI 编码时，方向是「加更多」：

```
❌ 常见思路                    ✅ 9arm-skills 思路
─────────────────────────────────────────────
更多模型能力                    更好的行为约束
更多工具                        什么时候该停下来
更多上下文                      什么时候该质疑自己
更多命令                        什么时候该拒绝行动
```

> "The highest leverage thing is not more capability. Sometimes, it is better constraints."

---

## 四大 Shipable Skills

```
skills/
├── engineering/
│   ├── debug-mantra/       ← 调试纪律
│   ├── postmortem/         ← 事故记录
│   └── scrutinize/         ← 冷眼审查
├── productivity/
│   └── management-talk/    ← 管理层沟通
├── misc/
├── personal/
├── in-progress/
└── deprecated/
```

---

## Debug Mantra — 调试四步法

这是全场最有价值的一个 Skill。

**核心规则**：在修复之前，必须完成四步。

```
Step 1                 Step 2
Reproduce              Know the Failing Path
穩定再現問題            追蹤失敗的代碼路徑
    │                      │
    │                      │
    ▼                      ▼
Step 3                 Step 4
Question the           Treat Every Run
Hypothesis             as a Breadcrumb
質疑假設                每次運行都留下線索
```

### 对比：无约束 vs 有约束

| 场景 | 无约束 Agent | 有 Debug Mantra |
|------|-------------|-----------------|
| 收到错误信息 | 立刻说 "I found the issue" | 先问：能稳定复现吗？ |
| 第一轮修改 | 改 3 个文件 | 不改，先追失败路径 |
| 错误变了 | 再改 4 个文件 | 不改，先质疑之前的假设 |
| 结果 | 症状追逐（chase symptoms） | 根因修复（fix the root） |

### 关键原则

```
❌ Do NOT:
  - 在有可靠复现之前提出修复方案
  - 在追踪到失败路径之前猜测原因
  - 在尝试证伪之前信任假设

✅ Do:
  - 在正确位置添加摩擦力（friction in the right place）
  - 让 Agent 在修复前变慢，这样修复本身可以更干净
```

> 问题的本质：很多 Coding Agent 的毛病不是不会写代码，而是**太急于在理解失败之前就写代码**。

---

## Postmortem — 工程事故记录

写 bug 修复后的工程记录，核心特性：**硬门控（hard gates）**——缺少必要事实时拒绝撰写。

```
撰寫前必備條件（缺一不可）：
┌─────────────────────────────────────┐
│  ☐ 可靠的复现步骤（Reliable Repro）  │
│  ☐ 根因已确认（Root Cause Known）    │
│  ☐ 修复方案已识别（Fix Identified）  │
│  ☐ 修复已验证（Fix Validated）       │
│                                     │
│  任何一项缺失 → 拒絕撰寫             │
└─────────────────────────────────────┘
```

### 为什么要硬门控？

```
AI 的超能力                              造成的問題
─────────────────────────────────────────────────
能写出看起来很专业的废话                 推测性根因听起来像确定结论
能把猜测包装成 RCA                      把 "可能是" 写成 "确认是"
能把模糊的描述变得有说服力               半年后回看发现全是空话
```

**Postmortem 技能要的不是漂亮文章，是 6 个月后同一个 bug 再次出现时能用的真实记录。**

必须包含：文件路径、函数名、测试名、具体验证命令。

---

## Scrutinize — 冷眼审查

不是 diff checker，是**站在变更之外的审视者**。

```
淺層 Review（常見 AI 行為）          Scrutinize（深度審查）
─────────────────────────────────────────────────
看 diff                              站在变更外部审视
檢查明顯問題                          質疑這個變更是否應該存在
建議加測試                            追問有沒有更簡單的方法
"Looks good overall"                 "What inputs break this?"
                                    "Are the tests testing the real path?"
```

### 关键洞察

> "Coherent does not mean correct."

Agent 很擅长让自己的工作**看起来连贯**：写计划 → 写代码 → 总结代码使之与计划一致。但这不等于正确。Scrutinize 就是那个「更冷的第二次审视」。

**最佳实践**：让 Scrutinize 由一个**独立的 Agent** 执行，而非实现 Agent 自审。实现者有确认偏误（confirmation bias），审查者应该没有。

---

## Management Talk — 跨频道翻译

把工程师对工程师的技术内容，翻译给管理层、PM、Release Manager 等受众。

```
保留                                     剥离
─────────────────────────────────────────────
  ✅ 产品/框架名稱                        ❌ 函數名
  ✅ JIRA ticket ID                      ❌ 堆疊追蹤
  ✅ PR 編號                             ❌ 代碼級細節
  ✅ 影響範圍                            ❌ 內部變數名
  ✅ 負責人                              ❌ 框架內部邏輯
  ✅ 下一步行動
```

> 不是 dum things down，是**把同样的真相翻译到正确的频道**。

---

## 四大技能形成完整闭环

```
    ┌──────────┐
    │  Debug   │  正確地調試
    │  Mantra  │
    └────┬─────┘
         │
         ▼
    ┌──────────┐
    │  Post-   │  寫下工程記錄
    │ mortem   │
    └────┬─────┘
         │
         ▼
    ┌──────────┐
    │Scrutinize│  冷眼審查變更
    └────┬─────┘
         │
         ▼
    ┌──────────┐
    │Management│  翻譯給對的人
    │  Talk    │
    └──────────┘

Debug → Review → Record → Communicate
調試  →  審查  →  記錄  →  溝通
```

这四个技能覆盖了一个严肃 bug 修复的完整生命周期。

---

## 在 Verdant 中的落地思路

虽然 9arm-skills 是为 Claude Code 设计的，但视频中提出了把它作为 **Verdant 工作流蓝图**的思路：

| 9arm-skill | Verdant 落地方式 | Agent 角色 |
|-----------|-----------------|-----------|
| Debug Mantra | 调试子 Agent（project rule） | 只负责复现 → 追踪 → 拒绝提前修复 |
| Scrutinize | 独立审查 Agent | 在隔离 workspace 中冷眼审查 |
| Postmortem | 记录 Agent | 拿到真实证据后撰写工程记录 |
| Management Talk | 沟通 Agent | 把技术记录转为 Jira/Slack 更新 |

### 推荐的 Verdant 工作流

```
Bug Report 進來
      │
      ▼
┌───────────┐     ┌──────────────┐
│ Debugger  │────→│Implementation│
│  Agent    │     │    Agent     │
│(复現+追蹤) │     │ (隔離workspace)│
└───────────┘     └──────┬───────┘
                         │
      ┌──────────────────┤
      ▼                  ▼
┌───────────┐     ┌──────────────┐
│ Reviewer  │     │    Test      │
│  Agent    │     │   Agent      │
│(Scrutinize)│     │  (驗證修復)   │
└─────┬─────┘     └──────┬───────┘
      │                  │
      └────────┬─────────┘
               ▼
       ┌──────────────┐
       │  Postmortem  │
       │    Agent     │
       └──────┬───────┘
              ▼
       ┌──────────────┐
       │    Comms     │
       │    Agent     │
       │(Management  │
       │   Talk)      │
       └──────────────┘
```

### 落地关键原则

```
✅ 正確做法                        ❌ 錯誤做法
─────────────────────────────────────────────
每個 Agent 只做一件事              把所有技能塞進每個任務
在正確時機載入正確行為             全局載入所有 skill
Agent 之間隔離                     實現者自審自批
每個產出物有明確職責               調試記錄 = 復盤報告
```

> "Do not dump every skill into every task. Use the right behavior at the right moment."

---

## Agent 的典型失败模式

9arm-skills 精准瞄准了 Coding Agent 的四大通病：

| 失败模式 | 症状 | 对应 Skill |
|----------|------|-----------|
| 修复前不调试 | Paste error → Agent 改 3 个文件 → 错误变了 → 改 4 个 | Debug Mantra |
| 自我审批太快 | 写代码 → 自审 → "Looks good" → 提交 | Scrutinize |
| 虚假确定性 | 把猜测写成 RCA，推测性根因包装成确定结论 | Postmortem |
| 受众错位 | 给管理层发堆栈追踪，给工程师发高管摘要 | Management Talk |

---

## 总结

```
核心洞察（一句話）：

Agent performance = Model Intelligence × Workflow Intelligence

模型聰明 ≠ 工作流正確。再強的模型也需要行為約束。
```

9arm-skills 的价值不在于它有多少行代码或多少个技能，而在于它展示了一个重要原则：**让 Agent 在它通常急躁的地方慢下来，在并行工作真正安全的地方并行化。**

---

## 参考资料

- [9arm-skills GitHub 仓库](https://github.com/thananon/9arm-skills)
- [Claude Code Skills 介绍](https://batsov.com/articles/2026/03/11/essential-claude-code-skills-and-commands/)
- [Best Claude Code Skills to Try in 2026 - Firecrawl](https://www.firecrawl.dev/blog/best-claude-code-skills)
