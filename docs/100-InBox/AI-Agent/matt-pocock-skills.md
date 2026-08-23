# Matt Pocock Skills — 完整文档

> 仓库：https://github.com/mattpocock/skills
> 作者：Matt Pocock (Total TypeScript)
> 定位：面向真实工程师的 Agent Skill 集合，基于数十年工程经验，帮助 Claude Code / Codex 等编码 Agent 更好地工作。不是 vibe coding，而是可组合、易改造的小型技能。

---

## 核心理念

Matt Pocock 认为当前 AI 编码 Agent 存在四大失败模式：

1. **Agent 没做你想要的事** — 人机沟通存在鸿沟。解决方式：Grilling（拷问式访谈），在动手前对齐需求。
2. **Agent 太啰嗦** — Agent 不懂项目术语，用 20 个词说 1 个词的事。解决方式：建立共享语言（Ubiquitous Language），通过 `CONTEXT.md` 和 ADR。
3. **代码跑不通** — 缺乏反馈循环（静态类型、浏览器、自动化测试）。解决方式：TDD（红绿重构）和结构化调试。
4. **代码变成一团泥** — Agent 加速编码的同时也加速了软件熵增。解决方式：关注代码设计，定期改善架构。

---

## Skill 分类体系

所有 Skill 按调用方式分为两类：

- **User-invoked**（用户调用）：只有你手动输入 `/skill-name` 才会触发。设置 `disable-model-invocation: true`。职责是编排。
- **Model-invoked**（模型调用）：Agent 可以自动根据任务场景调用，用户也可以手动调用。拥有丰富的 trigger description。

规则：User-invoked skill 可以调用 Model-invoked skill，但不能调用另一个 User-invoked skill。

---

## Engineering Skills（日常编码用）

### User-invoked（用户手动触发）

#### `/ask-matt` — 路由器
> "我该用哪个 skill？"

整个 repo 的入口路由。描述了从 idea 到 ship 的主流程和各 on-ramp。

**主流程：idea → ship**
1. `/grill-with-docs` — 访谈打磨想法（有代码库时用）
2. 分支 — 需要可运行的答案？→ `/handoff` → `/prototype` → `/handoff` 回来
3. 分支 — 多 session 构建？
   - Yes → `/to-prd` → `/to-issues` → 每个独立 issue 开新 session `/implement`
   - No → 直接 `/implement`

**On-ramps**
- Bug/请求堆积 → `/triage`

**代码库健康**
- `/improve-codebase-architecture` — 空闲时跑

**跨 session**
- `/handoff` — 压缩对话为 markdown，在新 session 中继续
- `/compact`（内置）— 在同一对话中压缩历史

#### `/grill-with-docs` — 带文档的访谈
> 拷问式访谈 + 同时构建领域模型

结合 `/grilling` 和 `/domain-modeling`。在访谈过程中：
- 打磨术语，写入 `CONTEXT.md`
- 记录架构决策，写入 ADR

**适用场景**：有代码库时使用。无代码库用 `/grill-me`。

#### `/triage` — Issue 分流
> 将 issue 按状态机流转

**角色系统**：
- 分类：`bug` | `enhancement`
- 状态：`needs-triage` → `needs-info` | `ready-for-agent` | `ready-for-human` | `wontfix`

**流程**：
1. 收集上下文（issue 内容、代码库探索）
2. 给出分类+状态建议
3. Bug 尝试复现
4. 需要时 grill 补充信息
5. 发布 agent brief 或 needs-info 模板

#### `/improve-codebase-architecture` — 架构改善
> 扫描代码库的深度化机会

**流程**：
1. Explore — 找出浅模块、耦合、测试困难的地方
2. 生成 HTML 报告（Tailwind + Mermaid），每个候选包含 before/after 可视化
3. 用户选择后，通过 `/grilling` 深入打磨方案

核心概念来自 `/codebase-design`（深度模块、seam、adapter）和 `/domain-modeling`。

#### `/setup-matt-pocock-skills` — 初始化配置
> 每个 repo 跑一次

配置：issue tracker（GitHub/Linear/本地文件）、triage labels、文档布局。

#### `/to-issues` — 拆分为 Issue
> 将计划/PRD 拆成独立的 vertical slice

**关键原则**：tracer-bullet vertical slices，不是 horizontal slices。
- 每个 slice 穿透所有层（schema, API, UI, tests），独立可验证
- 发布到 issue tracker，带 agent-ready 的模板

#### `/to-prd` — 生成 PRD
> 将当前对话综合为 PRD，发布到 issue tracker

不访谈，直接综合已有讨论。包含：Problem、Solution、User Stories、Implementation Decisions、Testing Decisions、Out of Scope。

#### `/prototype` — 原型验证
> 用一次性的代码回答一个设计问题

**两个分支**：
- 逻辑/状态问题 → 可交互的终端 app
- UI 问题 → 多种 UI 变体，可通过 URL 参数切换

**规则**：一次性代码、一条命令启动、内存状态、完成后删除或吸收。

---

### Model-invoked（Agent 可自动触发）

#### `/diagnosing-bugs` — 结构化调试
> 6 阶段调试循环

**Phase 1 — 构建反馈循环**（核心）
这是最重要的阶段。建立 tight、fast、deterministic 的反馈循环。
- 从 failing test → curl script → headless browser → property/fuzz loop 逐步升级
- 完成标准：能说出一条命令， deterministic, fast, agent-runnable

**Phase 2 — 复现 + 最小化**
- 确认 bug 与用户描述一致
- 缩小到最小复现场景

**Phase 3 — 假设**
- 生成 3-5 个可证伪的 ranked 假设
- 展示给用户，让他们用领域知识重新排序

**Phase 4 — 探测**
- 一次只改一个变量
- Debugger > Targeted logs > "log everything and grep"

**Phase 5 — 修复 + 回归测试**
- 在正确的 seam 写回归测试
- 先写测试，再修复

**Phase 6 — 清理 + 复盘**
- 清理 debug logs、临时代码
- 记录正确的假设到 commit message
- 反思什么能预防此类 bug

#### `/tdd` — 测试驱动开发
> 红绿重构，vertical slices

**核心理念**：
- 好测试验证行为（通过公共接口），不测试实现细节
- 测试应像规格说明："user can checkout with valid cart"

**反模式 — Horizontal Slices**：
```
WRONG: RED(全部测试) → GREEN(全部实现)
RIGHT: RED(test1) → GREEN(impl1) → RED(test2) → GREEN(impl2) → ...
```

**流程**：
1. Planning — 确认接口变更、优先测试的行为、识别深度模块机会
2. Tracer Bullet — 一个测试确认一件事
3. Incremental Loop — 每次一个 test → impl
4. Refactor — 提取重复、深化模块

#### `/domain-modeling` — 领域建模
> 主动构建和打磨项目的领域模型

**文件结构**：
```
/
├── CONTEXT.md          ← 术语表
├── docs/adr/           ← 架构决策记录
└── src/
```

**在会话中**：
- 挑战与术语表冲突的用词
- 打磨模糊语言（"account" → Customer 还是 User?）
- 用具体场景压力测试概念边界
- 交叉验证代码与描述是否一致
- 即时更新 CONTEXT.md
- 只在满足三个条件时创建 ADR：难逆转、令人意外、真实权衡的结果

#### `/codebase-design` — 代码库设计
> 深度模块的设计语言

**核心词汇表**（严格使用，不用 component/service/API/boundary 替代）：

| 术语 | 含义 |
|------|------|
| **Module** | 有接口和实现的任何东西（函数、类、包） |
| **Interface** | 调用者必须知道的一切（类型签名 + 不变量 + 约束 + 错误模式） |
| **Depth** | 接口可调用的行为量 vs 接口复杂度。深模块 = 小接口 + 大实现 |
| **Seam** | 可以在不编辑该处的情况下改变行为的位置 |
| **Adapter** | 在 seam 处满足接口的具体实现 |
| **Leverage** | 调用者从深度获得的收益 |
| **Locality** | 维护者从深度获得的收益（修改集中在一处） |

**关键原则**：
- 深度是接口的属性，不是实现的属性
- 删除测试：删除模块后复杂度是否分散到 N 个调用者？是 = 模块有价值
- 接口就是测试表面
- 一个 adapter = 假设性 seam；两个 adapter = 真实的 seam

#### `/resolving-merge-conflicts` — 合并冲突解决
1. 查看当前状态
2. 找到每个冲突的主要来源（读 commit message、PR、issue）
3. 逐个解决，保留双方意图
4. 运行自动化检查
5. 完成合并/变基

---

## Productivity Skills（通用工作流）

### User-invoked

#### `/grill-me` — 无代码库的访谈
> 与 `/grill-with-docs` 相同的拷问式访谈，但不保存状态、不写 CONTEXT.md。

适用场景：没有代码库的计划/设计打磨。

#### `/handoff` — 对话交接
> 将当前对话压缩为 handoff 文档

保存到系统临时目录（非工作区）。包含 suggested skills 部分。不重复已有 artifact（PRD、plan、ADR）的内容。

#### `/teach` — 教学技能
> 多 session 状态化教学

**工作区结构**：
- `MISSION.md` — 学习动机
- `./lessons/*.html` — 课程（美观的单页 HTML）
- `./reference/*.html` — 参考资料
- `./learning-records/*.md` — 学习记录
- `./assets/*` — 可复用组件（样式表、测验组件等）
- `RESOURCES.md` — 资源列表

**教学理念**：知识（Knowledge）→ 技能（Skills）→ 智慧（Wisdom）
- Fluency vs Storage Strength — 通过 desirable difficulty（检索练习、间隔、交错）建立长期记忆
- Zone of Proximal Development — 每课"刚好有挑战"
- 组件复用优先 — 先读 `./assets/`，再构建新课程

#### `/writing-great-skills` — Skill 编写参考
> 如何编写可预测的 skill

**核心概念**：
- **可预测性**是根本美德 — agent 每次走相同过程
- **信息层级**：In-skill step > In-skill reference > External reference（progressive disclosure）
- **Leading words** — 用预训练中已有的紧凑概念词来锚定行为（如 "tight"、"red"、"tracer bullets"）
- **No-op 测试** — 这行是否改变了 agent 的默认行为？没有就删掉
- **失败模式**：Premature completion、Duplication、Sediment、Sprawl、No-op

### Model-invoked

#### `/grilling` — 访谈循环
> 可复用的拷问式访谈核心

`/grill-me` 和 `/grill-with-docs` 的底层实现。逐一提问，等待反馈。能用代码库探索回答的，不问用户。

---

## Misc Skills（不常用工具）

| Skill | 用途 |
|-------|------|
| `/git-guardrails-claude-code` | 为 Claude Code 设置 git hooks，阻止危险命令（push, reset --hard, clean 等） |
| `/migrate-to-shoehorn` | 将测试从 `as` 类型断言迁移到 @total-typescript/shoehorn |
| `/scaffold-exercises` | 创建练习目录结构（sections, problems, solutions, explainers） |
| `/setup-pre-commit` | 设置 Husky pre-commit hooks（lint-staged, Prettier, type checking, tests） |

---

## CONTEXT.md — 共享语言文档

Matt Pocock 推荐的领域语言文档格式：

```markdown
# Project Name

## Language

**Term**: definition
_Avoid_: deprecated terms

## Relationships

- Term A holds many Term B

## Flagged ambiguities

- "X" was used for both A and B — resolved: ...
```

用途：
- Agent 用项目术语命名变量、函数、文件
- 代码库更容易导航
- Agent 花更少 token 在思考上
- 示例：`"materialization cascade"` 替代 20 词描述

---

## ADR — 架构决策记录

只在满足三个条件时创建：
1. **难逆转** — 改变想法的成本高
2. **令人意外** — 未来读者会问"为什么这样做？"
3. **真实权衡** — 有真正替代方案，选了一个有具体原因

---

## 快速安装

```bash
npx skills@latest add mattpocock/skills
```

选择需要的 skills 和目标 Agent，务必选择 `/setup-matt-pocock-skills`，然后运行它完成配置。

---

## 关键设计原则总结

1. **Vertical slices > Horizontal slices** — 每次穿透所有层，不要先写所有测试再写所有代码
2. **Feedback loops are king** — 调试中 tight loop 比代码阅读更有效
3. **Shared language pays compound interest** — CONTEXT.md 里的术语每次 session 都省钱省 token
4. **Deep modules over shallow ones** — 小接口、大实现、干净的 seam
5. **Small, composable skills** — 不像 GSD/BMAD/Spec-Kit 那样接管流程，而是可组合的工具
6. **Each skill solves one failure mode** — 诊断 → feedback loop；对齐 → grilling；啰嗦 → 共享语言

---

*文档生成时间：2025-06-23*
*基于 mattpocock/skills v1.0.1*
