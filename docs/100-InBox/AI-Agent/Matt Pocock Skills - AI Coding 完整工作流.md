---
title: Matt Pocock Skills — AI Coding 完整工作流（Idea → Ship）
aliases: [mattpocock-skills, matt-pocock-ai-coding-workflow]
tags:
  - ai-coding
  - claude-code
  - agent-skills
  - workflow
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=M6mYodf0dJM"
  - "https://github.com/mattpocock/skills"
author: Matt Pocock
created: 2026-07-25
updated: 2026-07-25
description: Matt Pocock 的 AI Agent Skills 系统，提供从需求对齐到代码审查的完整工程化工作流，解决 AI Coding 中最常见的对齐、上下文、质量三大问题。
level: intermediate
stars: 5
---

# Matt Pocock Skills — AI Coding 完整工作流（Idea → Ship）

> 162K stars、750 万下载量的 AI Agent Skills 系统。核心理念：**先对齐再写码，分阶段控管上下文，独立子代理审查**。

> [!info] 基本信息
> - **仓库**: https://github.com/mattpocock/skills
> - **视频**: [mattpocock/skills: A complete AI Coding workflow, end-to-end](https://www.youtube.com/watch?v=M6mYodf0dJM)（17:17）
> - **作者**: Matt Pocock（Total TypeScript 创始人）
> - **数据**: 162K+ Stars, 7.5M Downloads, 38 Skills
> - **兼容**: Claude Code, Cursor, Codex 等所有 Agent 框架

---

## 目录

- [一、设计核心：为什么要用这套 Skills](#一设计核心为什么要用这套-skills)
- [二、安装与初始配置](#二安装与初始配置)
- [三、核心工作流（Main Flow）](#三核心工作流main-flow)
- [四、上下文管理策略](#四上下文管理策略)
- [五、完整 Skill 清单与分类](#五完整-skill-清单与分类)
- [六、行动建议](#六行动建议)

---

## 一、设计核心：为什么要用这套 Skills

Matt Pocock 将 AI Coding 的常见失败归纳为 **4 个根本问题**，每个问题对应一套 Skill 作为解法。

### 1.1 四大失败模式与解法

```
┌─────────────────────────────────────────────────────────────────┐
│                    AI Coding 失败模式 × 解法                      │
├──────────────────┬──────────────────────────────────────────────┤
│ #1 对齐失败        │ → /grill-me, /grill-with-docs              │
│ "Agent 没做我要的"  │   多轮问答收斂需求，建立共享语言              │
├──────────────────┼──────────────────────────────────────────────┤
│ #2 过度冗长        │ → CONTEXT.md + 领域建模 (Domain Modeling)   │
│ "用 20 个词说 1 个" │   统一术语表，变量/函数命名一致               │
├──────────────────┼──────────────────────────────────────────────┤
│ #3 代码不工作      │ → /tdd, /diagnosing-bugs                   │
│ "产出质量不稳定"    │   Red-Green-Refactor 反馈循环               │
├──────────────────┼──────────────────────────────────────────────┤
│ #4 大泥球 (Ball    │ → /to-spec (deep modules),                 │
│   of Mud)         │   /improve-codebase-architecture            │
│ "代码库快速腐化"    │   模块深度设计 + 定期架构扫描                │
└──────────────────┴──────────────────────────────────────────────┘
```

### 1.2 与其他框架的差异化定位

| 维度 | GSD / BMAD / Spec-Kit | Matt Pocock Skills |
|------|----------------------|-------------------|
| 设计哲学 | 接管整个流程，黑盒化 | 小巧可组合，透明可控 |
| 上下文负担 | 高（skill 描述自动注入） | **极低（660 tokens）** |
| 触发方式 | 多为 Model-invoked（自动） | **多为 User-invoked（手动 /）** |
| 可定制性 | 难，流程绑死 | 易，fork 后随意改 |
| 适用模型 | 特定模型优化 | 任何模型 |

**关键设计决策：User-invoked 优先**

大部分 skill 不会自动注入 context，只有用户主动输入 `/skill-name` 时才加载。这意味着即使安装了全部 38 个 skills，主 context 仅增加约 660 tokens——对 LLM 思考干扰极小。

```
User-Invoked vs Model-Invoked
═════════════════════════════
                    ┌─ 输入 / 后才触发
User-Invoked  ──────┤  作用：编排流程（orchestrator）
  例: grill-with-docs│  特点：不占 context，用户完全掌控
  例: to-spec        │
  例: implement      │  规则：User-Invoked 可以调用 Model-Invoked
                    │       但绝不调用另一个 User-Invoked
                    │
                    ┌─ Agent 自动判断后触发
Model-Invoked ──────┤  作用：可复用的纪律性操作
  例: tdd            │  特点：作为底层工具被上层编排
  例: code-review    │
  例: domain-modeling│
```

---

## 二、安装与初始配置

### 2.1 安装方式

```bash
# 方式 1：skills.sh 安装器（可编辑，推荐想深度定制的用户）
npx skills@latest add mattpocock/skills

# 方式 2：Claude Code Plugin（只读自动更新，推荐即用型用户）
claude plugin marketplace add mattpocock/skills
claude plugin install mattpocock-skills@mattpocock
```

**两种方式对比：**

| | skills.sh | Plugin |
|---|---|---|
| 文件管理 | 复制到项目，可编辑 | 只读 bundle，自动更新 |
| 适合场景 | 想深度定制、团队 fork | 跟随官方版本、即装即用 |
| 维护 | 手动 `npx skills update` | 自动 |

### 2.2 安装时的关键选择

```
安装交互流程
──────────────────────────────────────
1. 选择 Skills 组
   ├── mattpocock-skills  ← 官方审核，推荐
   └── other-skills       ← 实验性，可能删除

2. 选择目标 Agent
   └── Claude Code / Cursor / Codex ...

3. 安装作用域 (Installation Scope)
   ├── Project Level  ← 团队协作（推荐）
   └── Global         ← 个人独立开发

4. 链接方式
   └── Symlink  ← 推荐，简单干净
```

### 2.3 初始化配置（/setup-matt-pocock-skills）

安装后**必须运行一次**此 skill，完成三项配置：

| 配置项 | 作用 | 选项 |
|--------|------|------|
| Issue Tracker | 存储 specs 和 tickets 的地方 | GitHub Issues, Linear, Jira, **Local Markdown** |
| Triage Labels | ticket 状态机标签 | 可用默认值 |
| Domain Docs | 项目上下文文档 | Single Context（推荐）/ Multi-Context（monorepo） |

```bash
# 配置完成后，项目根目录会生成：
CLAUDE.md          # 链接到 issue tracker / triage / domain docs
docs/
  agents/
    domain/
      issue-tracker.md   # tracker 配置
  adr/                    # 架构决策记录（ADR）
```

> [!tip] Issue Tracker 的灵活配置
> Skills 本身不绑定特定 tracker。只需在配置时告诉 agent "用 Jira" 或 "用 Linear"，它会自动适配。核心机制是读取本地配置文件，通过 agent 与 tracker API 交互。

### 2.4 随身向导：/ask-matt

随时可用 `/ask-matt` 提问，它是内建的路由 skill，了解整个 skills 体系：

```
> /ask-matt 我有一个新功能想开发，应该用什么流程？

# Agent 回答：
  主流程是 Idea → Ship
  1. 先用 /grill-with-docs 对齐需求
  2. 再用 /to-spec 生成规格
  3. /to-tickets 拆解任务
  4. /implement 实现代码
  5. 自动触发 /code-review 审查
```

---

## 三、核心工作流（Main Flow）

这是视频的核心——一条从模糊想法到代码上线的完整链路。

### 3.0 全景流程图

```
                    ┌──────────────────────────────────────────────────────┐
                    │              Main Flow: Idea → Ship                   │
                    └──────────────────────────────────────────────────────┘

  ┌─────────┐     ┌──────────────┐     ┌──────────┐     ┌────────────┐     ┌──────────────┐
  │ 模糊想法 │ ──> │/grill-with-  │ ──> │ /to-spec │ ──> │/to-tickets │ ──> │ /implement   │
  │ (Idea)  │     │docs          │     │          │     │            │     │              │
  └─────────┘     │ 多轮问答对齐  │     │ 压缩为   │     │ 拆成独立   │     │ 逐个执行     │
                  │ + 写 CONTEXT  │     │ 结构化   │     │ ticket     │     │ + TDD        │
                  │ + 写 ADR       │     │ 规格书   │     │            │     │              │
                  └──────┬───────┘     └──────────┘     └─────┬──────┘     └──────┬───────┘
                         │                                    │                    │
                         │  小任务可跳过 spec/tickets          │  每个 ticket 在     │
                         │  直接 → /implement                 │  全新 context 执行  │
                         │                                    │                    ▼
                         │                                    │           ┌──────────────┐
                         │                                    │           │ /code-review │
                         │                                    │           │ 双轴审查      │
                         │                                    │           │ (sub-agent)  │
                         │                                    │           └──────┬───────┘
                         │                                    │                  │
                         │                                    │                  ▼
                         │                                    │           ┌──────────────┐
                         │                                    │           │ commit       │
                         │                                    │           └──────────────┘
                         │                                    │
                         └────────────────────────────────────┘
```

### 3.1 阶段一：需求对齐 — /grill-with-docs

这是整个流程的**起点**，也是 Matt 认为"最重要的 skill"。

**核心机制：** 即便你只给一句模糊描述（如"我想精简这个 CLI 的内部工具"），Agent 会：

1. **扫描整个代码库**，理解现有结构
2. **一次只问一个问题**，给出推荐答案
3. 能从代码回答的，不问你
4. 每解决一个术语 → 写入 `CONTEXT.md`
5. 每做一个不可逆决策 → 写入 ADR

```
Grilling Session 示例
─────────────────────────────────────────────
用户: "我想移除 CLI 的大部分内部工具"

Agent 扫描代码 → 发现 internal namespace 有 11 个子命令

Q1: "要保留哪些 internal 命令？目前有 11 个：xxx, yyy, zzz..."
    [推荐答案: 全部移除]

Q2: "shared modules 被 3 个 internal 命令依赖，移除后如何处理？"
    [推荐答案: 一并清理，因为无外部引用]

Q3: "对应的测试文件是否同步删除？"
    ...（继续追问，通常 10-20 个问题）

最终产出：
  - 6-20 个已解答的问题
  - 更新后的 CONTEXT.md（新术语）
  - 0-2 个 ADR（重大决策）
  - 一份清晰的执行计划
```

**判断决策树：什么时候用哪个 grilling skill？**

```
需求模糊吗？
├── 否（已经很清楚）
│   └── 直接 /to-spec 或 /implement
├── 是，且涉及代码
│   ├── 工作量 < 1 个 context window
│   │   └── /grill-with-docs → /implement（跳过 spec/tickets）
│   ├── 工作量大，需要多个 session
│   │   └── /grill-with-docs → /to-spec → /to-tickets → /implement × N
│   └── 工作量极大且路径不清（greenfield 大项目）
│       └── /wayfinder → 清除决策迷雾 → 再进入主流程
└── 否，但涉及代码之外的设计/计划
    └── /grill-me（不写 CONTEXT.md/ADR 的纯问答版）
```

### 3.2 阶段二：规格化 — /to-spec

将 grilling 的成果（可能高达 46K tokens 的对话）**压缩**为结构化文档。

**注意：/to-spec 不会重新采访你。** 它只综合已有的对齐结果。

Spec 文档结构：

```markdown
## Problem Statement      # 用项目自己的语言描述问题
## Solution               # 高层解决方案轮廓
## User Stories           # 编号列表，每个可独立验收
## Implementation Decisions # 对齐时已确定的实现选择
## Testing Decisions      # 测试策略和 "完成" 标准
## Out-of-Scope           # 明确不做什么，控制范围
## Further Notes          # 其他值得保留的信息
```

**关键设计：Deep Modules（深度模块）**

`/to-spec` 在写 spec 前，会主动寻找 **deep module** 机会——用一个小而稳定的接口隐藏大量功能。这让测试有稳定的目标，底层实现可以自由变化。

```
Deep Module 设计原则
──────────────────────────────────────────
  接口 (Interface)  ←── 小而稳定（seam）
  ┌──────────────────────────────┐
  │                              │
  │     大量功能 (Implementation) │  ← 可自由变化
  │                              │
  └──────────────────────────────┘

  好处：测试针对接口写，不受实现变化影响
  来源：John Ousterhout《A Philosophy of Software Design》
```

### 3.3 阶段三：任务拆解 — /to-tickets

将 spec 拆成多个可独立执行的 tickets。

**核心原则：Tracer Bullet（曳光弹）— 纵切而非横切**

```
纵向切片 vs 横向切片
─────────────────────────────────────────────────────

  ❌ 横向切片（Horizontal）        ✅ 纵向切片（Tracer Bullet）
  做完所有层才能用                 每一片都是端到端可验证的

  ┌─────────┐                    ┌───┬───┬───┐
  │   UI    │ ← ticket 1          │ U │ U │ U │
  ├─────────┤                    │ I │ I │ I │ ← 每片含 UI+API+DB+Test
  │   API   │ ← ticket 2          │   │   │   │
  ├─────────┤                    │ A │ A │ A │
  │   DB    │ ← ticket 3          │ P │ P │ P │
  ├─────────┤                    │ I │ I │ I │
  │  Tests  │ ← ticket 4          │   │   │   │
  └─────────┘                    │DB │DB │DB │
  全做完才有反馈                   │   │   │   │
                                 │Tst│Tst│Tst│
                                 └───┴───┴───┘
                                 每片完成即可 demo
```

**Blocking Edges（阻塞关系）：**

每个 ticket 声明它依赖哪些其他 ticket 先完成。这让同一组 ticket 有两种读法：

| Tracker 类型 | 表现形式 | 工作方式 |
|-------------|---------|---------|
| Local Markdown | `.scratch/feature/issues/` 下编号文件 | 手动从上到下执行 |
| GitHub / Linear | 原生 blocking links / sub-issues | 无阻塞的 ticket 可并行（多 agent 同时跑） |

**例外：Wide Refactor（广域重构）**

像"重命名一个被上千处引用的共享符号"这类改动，无法切出绿色纵向切片。用 **Expand-Contract 模式**：

```
Expand-Contract 重构流程
──────────────────────────────
1. EXPAND：在旧符号旁添加新符号（不破坏任何东西）
2. MIGRATE：分批迁移调用点（每批一个 ticket，CI 全程绿色）
3. CONTRACT：所有调用点迁移完后，删除旧符号
```

### 3.4 阶段四：实现与审查 — /implement + /code-review

**`/implement` 的职责：**

```
/implement 执行循环
──────────────────────────────────────
1. 加载当前 ticket 的 spec
2. 在 pre-agreed seam 上用 /tdd 写测试（Red-Green-Refactor）
3. 频繁 typecheck
4. 单文件测试 → 最终全量测试
5. 所有 ticket 完成后，自动触发 /code-review
6. 审查通过 → commit 到当前分支
```

**`/code-review` 的双轴审查（核心亮点）：**

```
                    Code Review 双轴模型
  ┌──────────────────────────────────────────────────────────┐
  │                                                          │
  │    轴 1: Standards (代码规范)          独立 sub-agent     │
  │    "代码写得对吗？"                    ──────────────>     │
  │    - 是否遵循 CODING_STANDARDS.md                         │
  │    - Fowler 代码异味基线（~12 种）                         │
  │    - 文档标准 > 基线标准                                   │
  │                                                          │
  │    轴 2: Spec (规格对齐)               独立 sub-agent     │
  │    "做的是对的东西吗？"                ──────────────>     │
  │    - 是否完整实现 spec 的 user stories                    │
  │    - 有无遗漏的验收条件                                   │
  │    - 有无 scope creep（范围蔓延）                         │
  │                                                          │
  │    两轴独立运行，从不合并排名                              │
  │    （一个可以通过，另一个可以失败）                        │
  └──────────────────────────────────────────────────────────┘
```

**为什么用 sub-agent？** 解决 LLM 的 Self-Correction Bias——写了代码的 agent 不擅长审查自己刚写的代码，因为"它写的，所以它觉得没问题"。全新 context 的 sub-agent 没有这个盲区。

---

## 四、上下文管理策略

这是 Matt 反复强调的核心实践：**有意识地管理 context window**。

### 4.1 140K Token "Smart Zone"

```
LLM 上下文窗口的认知曲线
─────────────────────────────────────────────────────────
  
  智慧度
    ▲
    │  ★★★★★
    │        ★★★★★          ← Smart Zone (< 140K tokens)
    │              ★★★★★          注意力强，逻辑清晰
    │                    ★★★
    │                         ★★    ← Degradation Zone (> 140K)
    │                              ★   注意力下降，幻觉增加
    │                                   忘记细节，逻辑出错
    └───────────────────────────────────────> Token 数
    0          50K        100K       140K       200K
```

### 4.2 上下文管理决策

```
上下文管理策略
──────────────────────────────────────────
你的任务能在一个 session 内完成吗？
│
├── 能（小改动）
│   └── 同一 context 内：
│       grill-with-docs → /implement（跳过 spec/tickets）
│
└── 不能（大型变更）
    └── 分阶段，每阶段产出文件：
        grill-with-docs → /to-spec → /clear
                                      │
                        新 context: /to-tickets → /clear
                                                │
                                  新 context: /implement ticket 1 → /clear
                                              /implement ticket 2 → /clear
                                              ...
                                              /code-review
```

### 4.3 预算思维

把 token 当预算管理。Matt 在视频中的实例：

```
实例：移除 10 个 CLI 命令
──────────────────────────────────────────
Grilling 阶段消耗：~46K tokens
预估实现消耗：   ~43K tokens（实际 42.7K）
总预算评估：     < 100K tokens（在 Smart Zone 内）

决策：任务足够小 → 直接 /implement
     （跳过 /to-spec 和 /to-tickets）

Matt 判断依据：
  "我们有 100K 预算来移除 10 个命令，这非常充裕"
```

---

## 五、完整 Skill 清单与分类

### 5.1 Engineering Skills（日常代码工作）

| Skill | 类型 | 作用 |
|-------|------|------|
| **ask-matt** | User | 路由 skill，告诉你该用哪个 |
| **grill-with-docs** | User | 问答对齐 + 写 CONTEXT.md/ADR |
| **to-spec** | User | 对话压缩为结构化 spec |
| **to-tickets** | User | spec 拆成 tracer-bullet tickets |
| **implement** | User | 执行 spec/tickets + TDD + code-review |
| **wayfinder** | User | 超大模糊项目 → 决策地图 |
| **triage** | User | ticket 状态机管理 |
| **improve-codebase-architecture** | User | 扫描架构 → HTML 报告 → grill 改进 |
| **setup-matt-pocock-skills** | User | 初始化配置（每项目运行一次） |
| tdd | Model | Red-Green-Refactor 循环 |
| code-review | Model | 双轴审查（Standards + Spec） |
| domain-modeling | Model | 构建/维护领域模型词汇表 |
| codebase-design | Model | Deep Module 设计纪律 |
| prototype | Model | 一次性原型验证设计问题 |
| diagnosing-bugs | Model | 系统化调试循环 |
| research | Model | 调研 + 引用文档（后台 sub-agent） |
| resolving-merge-conflicts | Model | 逐 hunk 解决 merge 冲突 |

### 5.2 Productivity Skills（通用工作流）

| Skill | 类型 | 作用 |
|-------|------|------|
| **grill-me** | User | 纯问答（不写文档版 grill-with-docs） |
| **handoff** | User | 压缩对话为 handoff 文档供下个 agent |
| **teach** | User | 多 session 教学 |
| **writing-great-skills** | User | 如何写好 skill 的参考 |
| grilling | Model | grill 循环引擎（grill-me 和 grill-with-docs 的底层） |

### 5.3 调用规则总结

```
Skill 调用关系图
──────────────────────────────────────
  User-Invoked（/ 触发）
    │
    ├── 可调用 ──> Model-Invoked
    │                ├── tdd
    │                ├── code-review
    │                ├── domain-modeling
    │                └── ...
    │
    └── ✗ 不可调用 ──> 另一个 User-Invoked
                         （防止无限递归）
```

---

## 六、行动建议

### 最佳实践清单

```
✅ DO
──────────────────────────────────────────
□ 每次新功能都从 /grill-with-docs 开始
□ 关注 context 消耗，140K 是警戒线
□ 大型变更走完整流程：spec + tickets + /clear 间隔
□ 信任 sub-agent 的 code review（它没有自我盲区）
□ 每个 ticket 在全新 context 中执行
□ 定期运行 /improve-codebase-architecture 防腐化
□ Fork skills 后改成自己的版本

❌ DON'T
──────────────────────────────────────────
□ 不要直接让 agent "帮我写个 XXX 功能"
□ 不要在一个 context 里做完所有 ticket
□ 不要跳过 code review（即使你觉得没问题）
□ 不要用 plan mode 跑 grill（auto mode 即可）
□ 不要忽视 CONTEXT.md 的维护——它是效率倍增器
```

### 快速上手 3 步

```bash
# 1. 安装（30 秒）
npx skills@latest add mattpocock/skills
# 记得选中 /setup-matt-pocock-skills

# 2. 初始化
# 在 agent 中运行：
/setup-matt-pocock-skills
# 选择 issue tracker + 配置 domain docs

# 3. 开始第一个流程
/ask-matt          # 不知道用哪个时先问它
/grill-with-docs   # 开始对齐你的需求
```

---

## 参考资料

- [GitHub: mattpocock/skills](https://github.com/mattpocock/skills)
- [YouTube: A complete AI Coding workflow, end-to-end](https://www.youtube.com/watch?v=M6mYodf0dJM)
- [Matt Pocock Skills Newsletter](https://www.aihero.dev/s/skills-newsletter)
- [skills.sh 安装器](https://skills.sh/mattpocock/skills)

## 相关笔记

- [[Claude Code 配置]]
- [[Agent Skills 设计原则]]
- [[TDD 与 AI Coding]]
