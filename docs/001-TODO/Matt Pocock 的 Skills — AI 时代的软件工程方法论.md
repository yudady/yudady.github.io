---
title: Matt Pocock 的 Skills — AI 时代的软件工程方法论
aliases: [Matt Pocock skills, Skills for Real Engineers, AI 编程工程实践, Claude Code Skills]
tags:
  - ai/engineering
  - claude-code
  - tdd
  - ddd
  - software-architecture
  - status/active
  - type/learning-note
source:
  - "https://www.youtube.com/watch?v=_y772bb9VZE"
  - "https://github.com/mattpocock/skills"
author: Matt Pocock (TypeScript 社区教育者，前 Vercel / XState)
created: 2026-05-15
updated: 2026-05-15
description: GitHub 8 万 Star 的 AI Agent Skills 集合，将 TDD、DDD、ADR 等传统软件工程方法论蒸馏为可被 AI Agent 消费的结构化指令，对抗 Vibe Coding 带来的代码熵增
level: intermediate
stars: 4
---

# Matt Pocock 的 Skills — AI 时代的软件工程方法论

> GitHub 8 万 Star，前 Vercel / XState 核心贡献者 Matt Pocock 开源了一套 AI Agent Skills 集合。核心理念：**不是让 AI 接管开发流程，而是把 AI 融入成熟的工程实践**。

---

## 目录

- [背景：Vibe Coding 的三大反噬](#背景vibe-coding-的三大反噬)
- [Matt Pocock 其人](#matt-pocock-其人)
- [四大问题与对应方案](#四大问题与对应方案)
- [核心技能一览](#核心技能一览)
- [关键技能深度解析](#关键技能深度解析)
- [与 GSD / BMAD 的本质区别](#与-gsd--bmad-的本质区别)
- [AI 时代的核心认知反转](#ai-时代的核心认知反转)
- [参考资料](#参考资料)

---

## 背景：Vibe Coding 的三大反噬

```
Vibe Coding（凭直觉让 AI 生成代码）：
  动嘴皮子 → AI 搞定一切
  
现实问题：
  1. AI 生成的代码经常跑不通，甚至听不懂需求
  2. AI 长篇大论用废话演示对项目的无知
  3. AI 写代码太快 → 代码熵增加速 → 架构退化成一坨屎山
```

---

## Matt Pocock 其人

| 维度 | 信息 |
|------|------|
| TypeScript 社区最有影响力的教育者之一 | TalTypeScript 课程创作者 |
| 前 Vercel 开发者倡导者 | — |
| XState 核心贡献者 | XState 背后公司 stately 工作过 |
| **前健身教练** | 对沟通和表达有极其敏锐的直觉 |

---

## 四大问题与对应方案

### 问题 1：AI 没听懂你的需求

```
失败模式：你以为 AI 懂了，其实它根本没懂

解决方案：Grilling（盘问）技能
  - AI 不是顺从助手，而是严厉面试官
  - 不断提出尖锐问题逼你把模糊想法具体化
  - 直到需求书的每一个分支都被理清

示例：写登录页面
  AI 不会马上开始写 HTML/CSS，而是问：
  - 用户忘记密码怎么办？
  - 需要支持第三方登录吗？
  - 登录失败的错误提示怎么设计？

结果：极大降低需求理解偏差
```

### 问题 2：AI 太冗长，废话太多

```
根因：AI 被空降到新项目 → 不了解团队黑话和业务背景
      → 用大量通用词汇猜测（DDD 中叫「缺乏通用语言」）

解决方案：Grilling with Docs
  - AI 检查项目已有的领域语言和 CONTEXT.md
  - 当你使用模糊或冲突术语时，AI 当场追问澄清
  - 已确认的术语和关键决策沉淀到 CONTEXT.md / ADR

效果：
  +------------------+------------------------------------+
  | 之前             | 之后                               |
  +------------------+------------------------------------+
  | "课程中的章节被   | "材料级联（materialization cascade）|"
  | 变为'真实'时出   |                                    |
  | 问题"            |                                    |
  +------------------+------------------------------------+

额外收益：命名变量/函数时保持一致性，节省大量思考 token
```

### 问题 3：AI 写的代码跑不通

```
解决方案：TDD + Diagnose

TDD（测试驱动开发）技能：
  - 红绿重构循环，不是一次性写完所有测试再实现代码
  - 一个行为 → 一个测试 → 按垂直切片推进
  - AI 先写失败测试 → 实现功能 → 重构 → 循环

Diagnose（诊断）技能：
  - 结构化调试循环：重现 → 最小化 → 假设验证 → 修复 → 回归测试
  - 核心不是直接猜答案，而是先建立可重复的反馈循环
```

### 问题 4：代码库变成屎山（熵增）

```
根因：AI 写代码太快，来不及思考架构设计

解决方案：Architecture Review（架构审查）
  - Improve Codebase Architecture 技能
  - AI 从全局视角审视代码库
  - 找出接口复杂、难以测试、容易出 bug 的地方
  - 提出改进方向

频率：每几天运行一次，像给代码库做深度体检
```

---

## 核心技能一览

### 工程类技能

| 技能                               | 用途                                  |
| -------------------------------- | ----------------------------------- |
| `/diagnose`                      | 结构化调试循环：重现 → 最小化 → 假设验证 → 修复 → 回归测试 |
| `/grill-with-docs`               | 盘问 + 领域语言验证 + CONTEXT.md + ADR 更新   |
| `/triage`                        | Issue 状态机，按角色流转处理                   |
| `/improve-codebase-architecture` | 基于 CONTEXT.md 和 ADR 找出深化机会          |
| `/setup-matt-pocock-skills`      | **首次运行必须**：配置 Issue 追踪器、标签、文档布局     |
| `/tdd`                           | 红绿重构 TDD，每次一个垂直切片                   |
| `/to-issues`                     | 将计划/PRD 拆为可独立获取的 GitHub Issue（垂直切片） |
| `/to-prd`                        | 将对话综合为 PRD Issue（无需面试）              |
| `/zoom-out`                      | 获取陌生代码的系统级上下文                       |
| `/prototype`                     | 一次性原型：终端应用逻辑或多 UI 变体                |

### 生产力技能

| 技能 | 用途 |
|------|------|
| `/caveman` | 超压缩模式，~75% Token 减少，保持完整技术准确性 |
| `/grill-me` | 持续面试直到每个决策分支都解决 |
| `/handoff` | 将对话压缩为交接文档，保证 Agent 连续性 |
| `/write-a-skill` | 创建新 Skill（正确结构 + 渐进式披露） |

### 杂项技能

| 技能 | 用途 |
|------|------|
| `/git-guardrails-claude-code` | 通过 Claude Code hooks 阻止危险 git 命令（push/reset --hard/clean） |
| `/migrate-to-shoehorn` | 迁移 `as` 类型断言到 @total-typescript/shoehorn |
| `/scaffold-exercises` | 创建练习目录（章节/问题/解答/说明） |
| `/setup-pre-commit` | Husky + lint-staged + Prettier + 类型检查 + 测试 |

---

## 关键技能深度解析

### Grilling（盘问）系列

```
/grill-me：纯需求盘问
  → AI 不断追问，直到每个决策分支都明确

/grill-with-docs：盘问 + 领域语言
  → 在盘问的同时，AI 检查项目已有的 CONTEXT.md
  → 冲突术语当场追问澄清
  → 确认的术语沉淀到 CONTEXT.md / ADR

使用场景：写登录页面、设计 API 接口、定义数据模型
```

### TDD（测试驱动开发）技能

```
传统 TDD：一次写完所有测试 → 实现所有代码
Matt Pocock 版：一个行为 → 一个测试 → 按垂直切片推进

循环：
  1. AI 写失败测试（红）
  2. AI 实现功能使测试通过（绿）
  3. AI 重构代码（重构）
  4. 循环下一个行为

优势：保证质量 + AI 输出可控 + 每次反馈周期短
```

### Improve Codebase Architecture（架构审查）

```
定期运行，频率：每几天一次

AI 做三件事：
  1. 读取 CONTEXT.md + docs/adr/ 理解项目上下文
  2. 全局扫描代码库，找出接口复杂、难测试、易出 bug 的地方
  3. 提出「深化（deepening）」改进方向

效果：对抗 AI 加速的代码熵增
```

### Caveman（超压缩模式）

```
~75% Token 减少，保持完整技术准确性
适用于：Token 敏感场景、快速迭代、不需要详细解释时
```

---

## 与 GSD / BMAD 的本质区别

```
GSD / BMAD / Spec-Kit：
  - 试图接管整个开发流程
  - 做成大文件（kitchen-sink）
  - Bug 难以排查

Matt Pocock Skills：
  - 小型、可组合、可适配的构建块
  - 开发者保持控制权
  - 与任何大模型配合使用

核心差异：不是颠覆现有流程，而是让 AI 融入现有工程实践
```

---

## AI 时代的核心认知反转

### 反直觉结论

```
传统观念：有了 AI，不需要懂架构、不需要写测试、甚至不需要懂编程

事实恰恰相反：
  - AI 是极其强大的执行者，但缺乏对复杂系统的全局把控能力
  - 如果你不懂软件工程 → AI 会以 10 倍速度制造垃圾代码
  - 过去写烂代码需要几天 → AI 几秒生成上百行烂代码
  - 不加控制，烂代码很快拖垮整个项目

核心观点：在 AI 时代，坏代码的代价更大
          → 扎实的软件工程基础不仅没过时，反而成为开发者最核心的竞争力
```

### 人机分工新模式

```
以前：人写代码 → 人测试 → 人维护
现在：人定义问题 + 设计架构 + 制定规范
      AI 编写代码 + 生成测试 + 执行重构

人类负责：系统思考能力
AI 负责：高效执行能力

这才是真正的「AI 工程师」工作模式
```

### 未来顶尖工程师画像

```
既懂底层技术，又懂如何与 AI 高效人机协同：
  - 知道什么时候该让 AI 自由发挥
  - 知道什么时候该对 AI 进行严格约束
  - 懂得利用 AI 提升效率，同时防止负面影响

不是被 AI 替代的码农
而是指挥 AI 大军的架构师
```

---

## 参考资料

- [mattpocock/skills GitHub](https://github.com/mattpocock/skills) — 8 万 Star，MIT 协议
- [Matt Pocock YouTube](https://www.youtube.com/@mattpocockuk) — "We don't do vibe coding"
- [Matt Pocock LinkedIn - TDD Skill](https://www.linkedin.com/posts/mapocock_my-skill-makes-claude-code-great-at-tdd-activity-7427802789721911296-w09U)
- [知乎解析：告别「AI 写了一堆屎」](https://zhuanlan.zhihu.com/p/2033476732142007734)
- [视频来源：为什么叫QQ](https://www.youtube.com/watch?v=_y772bb9VZE)

## 相关笔记

- [[HTML 的不合理有效性 — Claude Code 文档范式转移]]
- [[Hermes Agent + CUA Driver — 开源后台桌面控制]]
