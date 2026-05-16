---
title: Matt Pocock Skills - 一文件夹 Markdown 拿下 68K Stars
aliases: [mattpocock/skills, dotclaude skills, vibe coding 解药]
tags:
  - ai-coding
  - claude-code
  - skills
  - github
  - prompt-engineering
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=z0y1nuiXdoc"
  - "https://github.com/mattpocock/skills"
author: Matt Pocock / Bitwise AI (YouTube)
created: 2026-05-16
updated: 2026-05-16
description: Matt Pocock 公开他的 .claude 文件夹，仅用 Markdown 文件解决 AI 编程四大失败模式，90 天 68K Stars
level: beginner
stars: 4
---

# Matt Pocock Skills - 一文件夹 Markdown 拿下 68K Stars

> TypeScript 圈的大佬 Matt Pocock 把自己日常使用的 `.claude` 文件夹开源了。没有运行时、没有编排器、没有 12 个 Agent 层级——只有 Markdown 文件。90 天 68,000 Stars，超过 Next.js 头三年的增长。

---

## 什么是 "Vibe Coding"

```
传统 AI 编程循环（Vibe Coding）:

  模糊请求 → Agent 猜测 → 交付错误结果 → 修改提示词 → Agent 猜得更努力 → ...
```

Pocock 给这种现象起了个名字：**Vibe Coding**——凭感觉写提示词，Agent 凭感觉写代码。整个 skills 仓库就是为了杀掉它。

---

## 仓库结构

```
skills/
  engineering/
    tdd/SKILL.md        # TDD 垂直切片
    diagnose/SKILL.md   # 系统化 Debug
  productivity/
    grill-me/SKILL.md   # 需求追问
  personal/
    ...
  misc/
    ...
```

每个文件夹里只有一个 `SKILL.md` 文件。Agent 按需读取。没有运行时，没有 magic。

---

## 四大旗舰 Skill

### Skill 1: /grill-me — 需求追问

**解决的问题：** 方向性错误——Agent 不问就猜，写了一堆不符合预期的代码。

**核心机制：** Agent 不再客气，逐个问题追问你的方案，走遍决策树的每个分支，在写任何代码之前让你 commit。

> *"Interview me relentlessly until we reach a shared understanding."*

**本质：** 解决不对齐的方法是在**你身上施加摩擦力**。

```
传统流程:
  需求 → Agent 猜测理解 → 写代码 → 发现不对 → 重写

grill-me 流程:
  需求 → Agent 追问 → 用户回答 → Agent 再追问 → ... → 共识达成 → 写代码（一次到位）
```

这是 Pocock 自己使用最多的 Skill。

---

### Skill 2: CONTEXT.md — 杀死冗余

**解决的问题：** AI 生成冗长的描述，浪费 token。

**核心机制：** 维护一个共享语言文件 `CONTEXT.md`，用精炼的术语替代冗长的解释。

```
Before (无 CONTEXT.md):
  "There's a problem when a lesson inside a section
   of a course is made real, given a spot in the file system."
  → 25 个词

After (有 CONTEXT.md):
  "There's a problem with the materialization cascade."
  → 8 个词
```

**效果：** 同样的 bug，同样的 Agent，token 消耗减半。Pocock 称之为"整个仓库最酷的技术"。

**工作方式：**
1. 项目根目录维护 `CONTEXT.md`
2. 记录领域术语、缩写、项目约定
3. Agent 自动读取并使用这些术语
4. 新术语出现时追加到文件

---

### Skill 3: /tdd — 垂直切片 TDD

**解决的问题：** 批量写测试导致测试验证的是"想象中的行为"而非"实际行为"。

**核心原则：** 一个测试，一个实现，一个绿灯，重复。

```
❌ 水平切片（Horizontal Slicing）— Pocock 明确禁止:
  写所有测试 → 写所有代码 → 运行
  问题: 测试验证的是想象，不是实际

✅ 垂直切片（Vertical Slicing）— Tracer Bullets:
  写 1 个测试 → 写实现让它通过 → 绿灯 → 下一个
  循环: Red → Green → Refactor
```

> "Tests written in bulk verify imagined behavior, not actual behavior."
> "Vertical slices, tracer bullets, red green refactor, boring, old, works."

---

### Skill 4: /diagnose — 系统化 Debug

**解决的问题：** 遇到 bug 乱改一气，靠运气修复。

**六阶段流程：**

```
1. Reproduce   → 稳定复现 bug
2. Minimize    → 最小化复现条件
3. Hypothesize → 提出假说
4. Instrument  → 加日志/断点验证
5. Fix         → 修复
6. Regression  → 回归测试
```

**真正的杀手锏：** 第一阶段——建立反馈循环。Pocock 列出了 10 种方式，按效率排序：

| 优先级 | 反馈方式 | 说明 |
|--------|---------|------|
| 1 | Failing test | 最快最可靠 |
| 2 | Type error | 编译期捕获 |
| 3 | Console.log | 快速但有噪音 |
| ... | ... | ... |
| 10 | Human + bash script | 最后手段 |

> *"Build the right feedback loop and the bug is 90% fixed."*

---

## 框架对比

| 框架 | Stars | 核心理念 | 复杂度 |
|------|-------|---------|--------|
| **Spec-Kit** | 93K | 宪法文件 + 规范驱动 | 中 |
| **Matt Pocock Skills** | **68K** | 纯 Markdown，无运行时 | **极低** |
| **GSD** | 61K | 专精 Claude Code | 中 |
| **BMAD** | 46K | 12 个专业 Agent 层级 | 高 |

**Pocock 的论点：** 大框架拥有流程。给你 12 个 Agent + 宪法 + 方法论，出问题时你不知道该修哪一层。Skills 做相反的事——每个 Skill 是一个独立文件，你能读、能 fork、能删除，无锁定、无 magic。

---

## 一行安装：Distribution as a Feature

```bash
npx skills@latest add mattpocock/skills
```

一个命令：选择你想要的 Skills → 选择目标 Agent（Claude Code / Codex / Cursor 等）→ 自动安装。

**这是 68K Stars 的关键。** 大多数开源项目忘了"分发"这一步。Pocock 不只是发布了文件，他发布了安装器。

---

## 核心洞察

**"Skills are the new package.json."**

如果你用 Claude Code、Cursor 或 Codex，你已经在用这个模式了——只是用得很差。偷 Pocock 的版本。

- 每个 Skill 是一个可读、可 fork、可删除的独立文件
- 没有 lock-in，没有 magic，没有 Kool-Aid
- Catalog 本身就是 artifact
- 开始策展你的 Skills，否则别人会替你策展

---

## 客观评价

### 优势
- 极简主义哲学——一个 Markdown 文件解决一个具体问题
- 每条规则都针对真实痛点，不是象牙塔理论
- 安装体验极佳（一行 npx）
- 可组合、可定制、可替换

### 局限
- Skills 是静态文本，无法根据项目状态动态调整
- 缺少量化效果数据（只有定性描述）
- 某些 Skill（如 /grill-me）会增加交互轮次，不适合追求速度的场景
- 仓库中 Skill 数量仍在增长，质量参差不齐

---

## 参考资料

- [mattpocock/skills (GitHub)](https://github.com/mattpocock/skills)
- [视频：He Open-Sourced His Claude Folder. 68K Stars](https://www.youtube.com/watch?v=z0y1nuiXdoc) by Bitwise AI
- [/grill-me Skill](https://github.com/mattpocock/skills/blob/main/skills/productivity/grill-me/SKILL.md)
- [/tdd Skill](https://github.com/mattpocock/skills/blob/main/skills/engineering/tdd/SKILL.md)
- [/diagnose Skill](https://github.com/mattpocock/skills/blob/main/skills/engineering/diagnose/SKILL.md)
- [Spec-Driven 开发框架对比 (MarkTechPost)](https://www.marktechpost.com/2026/05/08/9-best-ai-tools-for-spec-driven-development-in-2026-kiro-bmad-gsd-and-more-compare/)

## 相关笔记

- [[Andrej Karpathy Skills - 四条规则驯服AI编程助手]]
- [[Codex AI 代理人 - 从实践到自动化指南]]
