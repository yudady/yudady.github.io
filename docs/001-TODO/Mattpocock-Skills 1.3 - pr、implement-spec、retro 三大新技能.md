---
title: Mattpocock-Skills 1.3 — pr、implement-spec、retro 三大新技能
aliases: ["Mattpocock-Skills 1.3版本三大新技能", "AI随风 mattpocock skills 1.3"]
tags:
  - ai-agent
  - claude-code
  - workflow
  - mattpocock-skills
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=oAxUCb-71JI"
  - "https://github.com/mattpocock/skills"
  - "https://github.com/mattpocock/skills/blob/main/CHANGELOG.md"
author: AI随风（频道）/ mattpocock（技能作者）
created: 2026-10-15
updated: 2026-10-15
description: mattpocock/skills 1.3 的三个新技能拆解：pr 结构化 PR 描述、implement-spec 依赖导向并行开发、retro 会话覆盘自我演进，附官方 CHANGELOG 交叉验证与勘误
level: intermediate
stars: 4
note: 视频字幕被创作者禁用（Tier 0 报 Transcripts are disabled），本文基于用户提供的 Content Insights 骨架 + GitHub 官方 CHANGELOG/仓库实况交叉验证撰写
---

# Mattpocock-Skills 1.3 — pr、implement-spec、retro 三大新技能

> [!info] 基本信息
> - **视频**: [Mattpocock-Skills发布1.3版本，三个非常重要且实用的技能详细讲解](https://www.youtube.com/watch?v=oAxUCb-71JI)（AI随风，2026-10-14，12:18）
> - **仓库**: [mattpocock/skills](https://github.com/mattpocock/skills)（MIT，Stars 291,274 / Forks 24,430，2026-10-15 查询快照）
> - **版本**: v1.3.0 / v1.3.1（2026-10-13 前后发布，视频覆盖的就是这两个版本）
> - **定位**: 把 AI 编码从「代码补全」升级为标准化、具自愈能力的软件工程流水线

## 目录

- [基础工作流回顾](#基础工作流回顾)
- [新技能一：pr — 结构化 PR 描述](#新技能一pr-结构化-pr-描述)
- [新技能二：implement-spec — 依赖导向并行开发](#新技能二implement-spec-依赖导向并行开发)
- [新技能三：retro — 会话覆盘与规则进化](#新技能三retro-会话覆盘与规则进化)
- [工作流实践心法](#工作流实践心法)
- [验证与勘误记录](#验证与勘误记录)

---

## 基础工作流回顾

视频前半段先回顾 1.3 之前的主流程，全部位于仓库 `skills/engineering/` bucket（已逐一核实存在）：

| 技能 | 阶段 | 作用 |
|------|------|------|
| `grill-with-docs` | 需求对齐 | 开工前把需求喂给 AI，模型从边界条件、业务逻辑等多维度主动发问，消除歧义 |
| `to-spec` | 规格化 | 把对齐结果整理为含 User Story、验收标准的标准规格书 |
| `to-tickets` | 任务拆分 | 超大任务拆成多个具备独立 Context 的小 Ticket |
| `implement` | 落地实现 | AI 读取规格或单个 Ticket，进入实质编码 |

```
完整流水线（官方 README 口径）:

  需求 ──> grill-with-docs ──> to-spec ──> to-tickets ──┬─> implement (ticket 1)
              （主动追问对齐）      （规格书）   （拆 Ticket）  ├─> implement (ticket 2)
                                                                  └─> ... 手动逐个串行
                                                                 ──> code-review
```

1.3 的三个新技能分别补上流水线的三个缺口：**收尾交付**（pr）、**并行调度**（implement-spec）、**自我演进**（retro）。

---

## 新技能一：pr — 结构化 PR 描述

### 痛点

AI 生成的 Commit Message / PR 描述冗长繁琐，审查者难以一眼抓到重点。

### 核心结构（三段式）

1. **核心摘要**：以「能说清变更的最小视觉物」呈现——伪代码、调用树、文件树、Mermaid 图、diff 皆可，按变更性质选最直观的一种；复杂架构自动附图。
2. **客观证据**：提交前必须自测验证，呈现改动前后对比（报错 500 转 200、UI 修正前后截图等），证明「它真的能跑」。
3. **合并风险评估**（核心亮点）：引入单向门/双向门概念，结合变更范围（blast radius）让审核者快速决策。

| 门的类型 | 含义 | 回滚成本 | 审查策略 |
|----------|------|----------|----------|
| 单向门（One-Way Door） | 高风险、难回滚（数据迁移、协议变更、删除接口） | 高 | 必须详读验证证据与摘要 |
| 双向门（Two-Way Door） | 低风险、可轻易回滚（加字段、改样式、纯新增） | 低 | 范围极小时可直接放行 |

### 审查策略：由下往上读

传统 PR 从摘要往下读，信息密度递减。`pr` 的模板刻意把风险声明放在末尾，推荐审查者**反着读**：

```
PR 阅读决策树（由下往上）:

  看合并风险 ──┬─ 双向门 + 影响范围极小 ──> 直接放行
               │
               └─ 单向门 / 全栈改动 ──> 往上详读验证证据
                                          ──> 仍有疑虑 ──> 读核心摘要与附图
```

> [!note] 官方细节（CHANGELOG 坐实）
> `pr` 在官方归类中是 **model-invoked**（模型在开发收尾时自动调用，作为 PR-body 的收口形态），不是用户手动敲的命令。其摘要视觉物设计改编自 Dex Horthy 的 `show-me` skill，来源在技能的 `CREDITS.md` 中注明。

---

## 新技能二：implement-spec — 依赖导向并行开发

### 痛点

旧版 `implement` 面对拆解出的多个 Ticket 时，工程师要手动串行、一条条指派，极其费时。

### 自动化机制（官方 CHANGELOG 比视频更细）

`implement-spec`（**user-invoked**，用户主动调用）一次性实现整个 spec，机制四步：

1. **读 Ticket 为任务图（task graph）**：自动解析依赖与阻塞状态（Blocked / Unblocked）。
2. **并行推进 ready frontier**：给每个无阻塞的 Ticket 分配一个 implementer 子代理，各自在**独立 worktree** 中工作。
3. **动态解锁**：前置依赖完成即唤醒后续任务；每个 implementer 完成前需确认自己的 worktree 基于 integration branch、用 `tdd` 构建 Ticket、并把 integration tip merge 进自己分支——保证最后每个合并都是 fast-forward。
4. **统一收口**：全部落到一条 **integration branch**，以 `code-review` 收尾。目标是 integration branch 而非 PR；draft PR 只在 issue tracker 以 PR 收口工作、或你主动要求时才开。

```
implement-spec 并行调度示意:

  tickets: T1  T2(T1依赖)  T3  T4(T2依赖)

  时刻1:  [T1:subagent-A]  [T3:subagent-B]      <- ready frontier 并行
  时刻2:  T1 done ──> [T2:subagent-C]  [T3...]   <- 依赖解锁自动唤醒
  时刻3:  T2 done ──> [T4:subagent-D]
  收口:   各分支 fast-forward ──> integration branch ──> code-review
```

### 对比：implement vs implement-spec

| 维度 | implement | implement-spec |
|------|-----------|----------------|
| 输入 | 单个 Ticket / 规格 | 整个 spec（全部 Ticket） |
| 执行方式 | 人工逐个指派，串行 | 子代理按依赖图自动并行 |
| 分支策略 | 单工作区 | 每 implementer 独立 worktree |
| 收尾 | 手动 | 自动落 integration branch + code-review |
| 适用规模 | 小任务 | 多 Ticket 跨模块大任务 |

> [!warning] 前置条件
> 官方 CHANGELOG 特别指出：`implement-spec` 会像兄弟技能一样指向 issue tracker 配置，没有配置时会提示先跑 `/setup-matt-pocock-skills`，而不是静默默认用 `gh`。首次使用前先完成 setup（选 issue tracker、triage 标签、文档存放位置）。

---

## 新技能三：retro — 会话覆盘与规则进化

### 核心功能

针对已结束的开发会话做主动诊断，找出 Prompt 不足、Context 浪费、工具缺失、沟通死循环的症结——但改进对象是 **agent 的环境而非代码**：

- 导航指针（navigation pointers）
- 自动化检查（automated checks）
- 编码规范（coding standards）
- steering 文件（如 `AGENTS.md`）
- 工具经济（tool economy）
- 信息可及性（information access）

### 沉淀为持久记忆

能指出「缺乏前端渲染指引」「缺少错误日志设定」等高优先级缺陷，并直接建议补充到项目规范文档。官方机制比视频更精确：

- 每条 coding-standards 发现**先分类**：机械性违规 → 落成确定性检查（linter 规则、pre-commit hook、CI job）；`CODING_STANDARDS.md` 只保留真正需要人判断的条款。
- 一个完全没有 guardrail 的仓库，本身就是一条 finding。

```
retro 发现的处理分流:

  发现 ──> 是机械性违规吗？
             ├─ 是 ──> linter 规则 / pre-commit hook / CI job（确定性拦截）
             └─ 否 ──> CODING_STANDARDS.md（留给判断力）
  另: 仓库无任何 guardrail ──> 本身记为一条 finding
```

### 使用定位

作者建议**每次完成 PR 后、或感到协作效率低下时必跑**。三个新技能中复利效应最强：pr 管一次交付，implement-spec 管一个 spec，retro 管的是「下一次所有会话都变好」。1.3.1 还把 `ask-matt` 的 bug 修复收尾指向 `/retro`——修完 bug 追问「什么能预防它再发生」。

---

## 工作流实践心法

### 破除「每步必走」的教条陷阱

工作流不是刚性流水线。常见误区：每个小改动都跑完「对齐 → 规格 → 拆分 → 执行」全套，反而增加负担。

### 按任务量级分级处置

```
任务量级决策树:

  需求来了 ──> 改动跨模块/历史对话已很长？
                ├─ 否（轻量/常规）
                │     grill-with-docs ──> implement ──> pr ──> retro
                │    （对齐后直接开发，跳过中间文档）
                │
                └─ 是（大型/跨模块）
                      grill-with-docs ──> to-spec ──> to-tickets
                        ──> 规格落盘成文档（Checkpoint）
                        ──> 清空/另开会话
                        ──> implement-spec 并行调度 ──> retro
```

| 路径 | 触发条件 | 流程 | 关键点 |
|------|----------|------|--------|
| 轻量路径 | 常规需求、单模块 | 对齐 → implement → pr → retro | 跳过 spec/tickets 中间文档 |
| 完整路径 | 大型、跨模块 | 对齐 → spec → tickets → 新会话 → implement-spec → retro | 规格落盘 = 对话重置点 |

### Context 是昂贵资源

大型任务的历史对话过长会引发 Context 压缩与幻觉。规格文档不是形式主义，而是**对话重置点（Checkpoint）**：主 Agent 专注架构设计，子 Agent 各自携带 Ticket 级 Context 专注局部实现。

### 行动建议

1. **立即落地**：项目里装好技能插件（`claude plugin install mattpocock-skills@claude-plugins-official`），跑一次 `/setup-matt-pocock-skills` 绑定 issue tracker。
2. **把 retro 设为例行动作**：每次重大发布后或每周固定覆盘，让规范文档持续进化。
3. **灵活裁切**：小步快跑跳过中间文档；多模块改动才启动完整 Spec 拆解与并行调度。

---

## 验证与勘误记录

| 视频/Insights 声称 | 验证结果 | 来源 |
|---|---|---|
| mattpocock/skills 1.3 发布三个新技能 pr / implement-spec / retro | ✅ CHANGELOG 1.3.0 证实：三者经 PR #1120 graduate 进 Engineering bucket，随 Claude Code 插件发布 | CHANGELOG.md |
| 「`/pr` 提交」——用户手动敲 /pr 收尾 | ⚠️ 口径差异：官方归类 `pr` 为 **model-invoked**（模型收尾自动调用），`implement-spec` 和 `retro` 才是 user-invoked | CHANGELOG 1.3.0 |
| implement-spec 自动解析依赖、子代理并行、完成后 review 合并 | ✅ 且官方更细：task graph、每 implementer 独立 worktree、integration branch 收口（默认不开 PR）、每分支用 tdd 构建、fast-forward 合并 | CHANGELOG 1.3.0 |
| pr 的三段结构（摘要/证据/风险）与单向门双向门 | ✅ 官方原文：smallest visual（伪代码/调用树/文件树/Mermaid/diff）+ before/after evidence + merge-danger call（one-way or two-way door, plus blast radius） | CHANGELOG 1.3.0 |
| retro 建议补充 AGENTS.md 等规范文档 | ✅ 官方列出改 env 清单含 steering files / coding standards；且补充「机械性违规落确定性检查、CODING_STANDARDS.md 只留判断题」机制 | CHANGELOG 1.3.0 |
| 基础技能 grill-with-docs / to-spec / to-tickets / implement 存在 | ✅ 全部在 skills/engineering/ 目录实查存在 | GitHub API contents |
| 视频章节时间戳（02:16 PR / 06:52 implement-spec / 09:22 retro / 10:22 常见错误） | ✅ 与官方描述 timestamps 一致，Insights 更细粒度互洽 | 视频描述 |
| —（视频未提） | ⚠️ 1.3.0 还有多个变更：移除 `resolving-merge-conflicts` 技能；`CONTEXT.md`/`CONTEXT-MAP.md` 全仓改名 `GLOSSARY.md`/`GLOSSARY-MAP.md`（涉及 11 个技能）；跨技能调用约定统一为「Call the Skill tool with name」 | CHANGELOG 1.3.0 |
| Stars 数 | 快照口径：291,274（2026-10-15 查询），仓库 2026-02-03 创建 | GitHub API |

---

## 参考资料

- [视频：Mattpocock-Skills发布1.3版本，三个非常重要且实用的技能详细讲解](https://www.youtube.com/watch?v=oAxUCb-71JI)（AI随风，2026-10-14）
- [mattpocock/skills 仓库](https://github.com/mattpocock/skills)
- [CHANGELOG.md（1.3.0 / 1.3.1 变更原文）](https://github.com/mattpocock/skills/blob/main/CHANGELOG.md)
- [aihero.dev/skills（作者技能主页）](https://aihero.dev/skills)

## 相关笔记

- [[Matt Pocock 修复 PR 瓶颈演讲 - 三层刹车审查体系]]（同仓库方法论侧：AI Engineer Paris 演讲导读，讲清 retro / pr / code-review 这些 skill 背后的三层刹车设计动机）
- [[Claude Code Hooks - 确定性自动化防线]]（retro 的「机械性违规落 linter/hook/CI」与 Hooks 思路同源）
- [[open-code-review - 阿里混合架构 AI Code Review]]（同为 AI 代码审查工程化实践）

---

*文档生成时间：2026-10-15*
*基于 mattpocock-skills v1.3.1（CHANGELOG 快照）*
