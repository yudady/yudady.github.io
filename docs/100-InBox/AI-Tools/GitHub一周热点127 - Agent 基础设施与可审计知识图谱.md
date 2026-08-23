---
title: GitHub 一周热点 127 期 — Agent 基础设施、出版级图表、自进化 RLM 与可审计图谱
aliases: [Github一周热点127, deepseek-harness, prime-agent, diagram-design, semantica]
tags:
  - github-trending
  - ai-agent
  - open-source
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=TY4JQhwEm1s"
  - "https://github.com/deepseek-ai/deepseek-harness"
  - "https://github.com/cathrynlavery/diagram-design"
  - "https://github.com/PrimeIntellect-ai/prime-agent"
  - "https://github.com/macro-inc/macro"
  - "https://github.com/semantica-agi/semantica"
author: IT咖啡馆（频道）；项目分属 DeepSeek AI / Cathryn Lavery / Prime Intellect / Macro Inc / Semantica
created: 2026-08-15
updated: 2026-08-15
description: 5 个热门开源项目（DeepSeek Harness、diagram-design、prime-agent、macro、semantica）+ 2 份行业趋势（2026 AI 校招、阿里云 Agent 安全），从单一对话框走向可插拔平台、持久化执行环境、全域关联记忆与可审计图谱。
level: intermediate
stars: 4
note: 无字幕（频道关闭字幕），基于视频元数据 + 官方仓库 README + 外部资料交叉整理。视频/转述中部分项目名称有误，已按官方仓库纠正（见「名称纠错」章节）。
---

# GitHub 一周热点 127 期 — Agent 基础设施与可审计知识图谱

> 本期 5 个开源项目覆盖 Agent 运行时（deepseek-harness）、Agent 视觉产出（diagram-design）、自进化编程范式（prime-agent）、团队协作工作台（macro）与企业级语义图谱（semantica），外加 2026 AI 校招趋势与阿里云 Agent 安全实践。适合正在选型 Agent 基础设施、或关注 AI 系统可审计性的开发者与架构师。

## 目录

- [[#速览：本期五大项目]]
- [[#1. deepseek-harness — Everything is a Plugin]]
- [[#2. diagram-design — 出版级图表 Skill]]
- [[#3. prime-agent — 自进化 RLM 编程 Agent]]
- [[#4. macro — 团队统一工作台]]
- [[#5. semantica — 可审计语义图谱基础设施]]
- [[#6. 行业趋势]]
- [[#7. 选型决策树]]
- [[#名称纠错：视频转述 vs 官方仓库]]
- [[#参考资料]]

---

## 速览：本期五大项目

| 项目 | 定位 | Stars | 协议 | 语言/技术栈 | 阶段 |
|------|------|-------|------|------------|------|
| deepseek-harness | Agent 运行平台，一切皆插件 | 106.8k | MIT | TypeScript (Node.js) + Python | Developer Preview |
| prime-agent | 自进化 RLM 编程/研究 Agent | 15.5k | MIT | TypeScript + 持久化 IPython | 活跃（4.5k commits） |
| diagram-design | Coding Agent 出版级图表 Skill | 12.7k | MIT | 自包含 HTML/SVG | v2.3 |
| semantica | 可审计语义图谱基础设施 | 6.3k | MIT | Python | 活跃（PyPI 可装） |
| macro | 团队统一工作台 + 共享记忆 | 3.1k | AGPLv3 | Rust + SolidJS | 可自托管 |

共同趋势：五个项目分别代表 AI 开发栈的五个层面——运行时、产出质量、编程范式、协作记忆、知识可信。都在回答同一个问题：Agent 时代的基础设施长什么样。

---

## 1. deepseek-harness — Everything is a Plugin

> [!info] 基本信息
> - **仓库**: https://github.com/deepseek-ai/deepseek-harness
> - **作者**: DeepSeek AI（官方开源）
> - **Stars 106.8k** / Forks 10.2k / 12,293 commits / MIT
> - **状态**: Developer Preview，官方明示「THERE WILL BE COMPATIBILITY-BREAKING CHANGES」

### 核心定位

DeepSeek 官方开源的 Agent Harness（`dsh`），架构哲学是「一切皆插件」（Everything is a Plugin）。底层基于 **Cordis** 框架（github.com/cordiverse/cordis），其设计来自论文《A Programming Paradigm for Spatiotemporal Composability》（时空可组合性的编程范式）。

注意：视频转述中说的「CDS 框架」是 Cordis 的误听，官方 README 明确写的是 Cordis。

### 架构：插件化的 Agent 运行平台

```
┌─────────────────────────────────────────┐
│           DeepSeek Harness (dsh)        │
│                                         │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  │
│  │ 插件:Web │  │ 插件:终端│  │ 插件:工具│  │
│  │   UI    │  │  运行时  │  │  调用   │  │
│  └────┬────┘  └────┬────┘  └────┬────┘  │
│       │            │            │       │
│       └────────────┼────────────┘       │
│                    ▼                    │
│         Cordis 插件内核（时空组合）        │
│      「Everything is a Plugin」          │
└─────────────────────────────────────────┘
```

### 使用方式

```bash
# 需要 Node.js，一条命令启动 Web UI（默认 http://127.0.0.1:3080）
npx @deepseek-ai/deepseek-harness web   # 官方包名 @deepseek-ai/dsh

# 插件生态：给自己的插件仓库打上 topic 即可被发现
# GitHub topic: dsh-plugin
```

仓库结构（apps / packages / python / native / examples / docs）显示它同时容纳 TypeScript 与 Python 两侧的扩展面，且提供中英双语 README（README.zh.md）。

### 视频观点 vs 官方信息

| 维度 | 视频转述 | 官方仓库可验证 |
|------|---------|---------------|
| 底层框架 | 「CDS 框架」 | Cordis（cordiverse/cordis） |
| 插件理念 | Everything is a plugin | ✅ 一致（README 标题即此） |
| DeepSeek V4 长程任务优化 + Prompt Cache 高命中 | 视频强调的成本优势 | README 未提及，属视频/实测观点，待自行验证 |
| macOS 电脑控制等社区扩展 | 视频提到 | 与 dsh-plugin topic 插件生态相符 |

### 最佳实践

- ✅ 适合现在研究其插件架构、读 docs/architecture.md 与 Cordis 论文
- ✅ 插件仓库打 `dsh-plugin` topic，进入官方发现通道
- ❌ 不要用于生产——Developer Preview 阶段明确会有破坏性变更
- ❌ 不要基于它做深度定制后不锁版本（迭代极快，12k+ commits）

---

## 2. diagram-design — 出版级图表 Skill

> [!info] 基本信息
> - **仓库**: https://github.com/cathrynlavery/diagram-design
> - **作者**: Cathryn Lavery（littlemight.com 博主、BestSelf.co 创始人）
> - **Stars 12.7k** / Forks 772 / MIT / v2.3

### 核心定位

给 Claude Code、Codex（以及 Pi）用的图表设计 Skill，产出「编辑级」（editorial）质量的图，解决 Agent 默认产出的「通用圆角盒子 + Mermaid 味」图表问题。

- **27 种视觉类型**：架构图、流程图、时序图、状态机、ER 图、泳道图、象限图、飞轮（Loop，v2.0 新增）、Gantt、Medallion 数据分层等
- **每种 3 个静态变体**：minimal light / minimal dark / full-editorial
- **零依赖**：自包含 HTML + SVG，无构建步骤、无 JS、无外部图片
- **v2.3 语义系统模式**（semantic system patterns）：把「行为」与「布局」分离——队列、策略轨迹、信任边界这类语义可直接复用最近的现有图形，不膨胀图形数量
- **重绘能力**：可将 draw.io 或 Mermaid 源文件重绘为指定格式、尺寸、细节等级的成品

### 安装

```bash
# Claude Code
/plugin marketplace add cathrynlavery/diagram-design
/plugin install diagram-design@diagram-design

# Codex
codex plugin marketplace add cathrynlavery/diagram-design
codex plugin add diagram-design@diagram-design
```

安装后记得在 `/plugin` → Marketplaces 里开启 Enable auto-update（Claude Code 对第三方市场默认关闭自更新）。

### 与默认方案对比

| 维度 | Agent 默认 Mermaid | Figma 手工 | diagram-design |
|------|-------------------|-------------|----------------|
| 产出质量 | 通用、简陋 | 可完全定制 | 编辑级、有品牌一致性 |
| 时间成本 | 秒级但需返工 | 30 分钟起步 | 秒级，60 秒内匹配网站品牌 |
| 可编辑性 | 源码可改 | 二进制编辑 | HTML/SVG 原生可编辑 |
| 依赖 | Mermaid 渲染器 | Figma 账号 | 无（浏览器直接打开） |
| 无障碍 | 弱 | 手工 | 可选 accessible motion（v2.3，默认仍静态） |

### 设计哲学（作者的品味清单）

- ✅ 「删除是最高质量的操作」——每个节点都必须挣得自己的位置
- ✅ 强调色只留给读者应该最先看的 1-2 个东西
- ✅ 目标信息密度 4/10，留白优先
- ❌ 不做阴影、不做通用圆角盒子（README 原话：No Mermaid-slop）
- ❌ 不追求图形数量堆砌（语义模式复用现有类型）

---

## 3. prime-agent — 自进化 RLM 编程 Agent

> [!info] 基本信息
> - **仓库**: https://github.com/PrimeIntellect-ai/prime-agent
> - **作者**: Prime Intellect（去中心化 AI 训练基础设施公司）
> - **Stars 15.5k** / Forks 1.6k / 4,507 commits / MIT
> - **TUI 基于 pi（earendil-works/pi）**

### 两个核心抽象

1. **RLM（Recursive Language Model，递归语言模型）**
   - 上下文是变量（prompt-as-a-variable）
   - 工具与子 Agent 是函数调用（programmatic tool / sub-agent calling）
   - 全部发生在**持久化 Python REPL（IPython）**里——不是每轮从聊天记录恢复状态

2. **Continual Harness（持续化 Harness，论文 arXiv:2605.09998）**
   - 补充提示词、记忆、技能描述、可复用子 Agent 规格都存为持久状态
   - Agent 可通过小的、有证据支撑的更新来精炼自己的 Harness

### 传统 chat-loop vs RLM 运行模型

```
传统 Agent（chat-loop）:
  用户 → LLM → 工具调用 → 结果追加到聊天记录 → LLM ...
  （每轮都从历史消息窗口"重新理解"状态，上下文即字符串）

prime-agent（RLM）:
  ┌──────────────────────────────────┐
  │  持久化 IPython REPL（控制环境）   │
  │                                  │
  │  context = {...}   ← 上下文是变量 │
  │  result = rlm(...) ← 子Agent是函数│
  │  state 在进程内持续存活            │
  └──────────────────────────────────┘
        ▲                │
        └── /refine ─────┘
   小步自进化：审查轨迹 → 证据支撑的
   harness 更新（不改基础 system prompt，
   快照可回滚）
```

### 关键特性清单

- **一切皆代码**：文件操作、shell、工具、子 Agent、上下文管理都通过 Python 代码完成
- **内置子 Agent**：`rlm(...)` 生成真实子 Agent 做并行/后台工作，结果程序化返回
- **`/refine` 自进化**：审查当前轨迹，将可复用的经验固化为 harness 状态的小更新
- **技能即 Python 包**：技能是可导入的包，内置 skill creator 可把重复工作流固化为项目/个人技能
- **后台会话**：daemon 支撑，终端断开任务继续跑，随时重连
- **Agent 间直接通信**：运行中的 Agent 可互发消息、互相编排，不必经用户中转
- **长任务保活**：自动压缩（compaction）、持久目标（/goal）、心跳（/heartbeat）、定时（schedule）、有界自治模式（/autonomous，带 turn/token/时间预算和质量门）

### 安全注意（官方明示）

- ⚠️ prime-agent 以你的用户权限执行模型生成的 Python 与项目命令
- ⚠️ worker/kernel 进程只做生命周期隔离，**不是安全沙箱**——不可信代码必须放外部沙箱跑

```bash
# macOS / Linux 安装（install.sh 校验 SHA-256 后安装 prime-agent 命令）
curl -fsSL https://raw.githubusercontent.com/PrimeIntellect-ai/prime-agent/main/install.sh | bash
# 首次启动运行 /login 选择 provider（订阅或 API key）
```

---

## 4. macro — 团队统一工作台

> [!info] 基本信息
> - **仓库**: https://github.com/macro-inc/macro
> - **作者**: Macro Inc（纽约 + 多伦多团队，自用 dogfood 两年）
> - **Stars 3.1k** / Forks 312 / 5,024 commits
> - **AGPLv3 完全开源**（官方强调 not "open core"，可自托管）/ SOC 2 Type II

注意：视频转述中的「m-core」实为 **macro**。

### 核心定位

把 email + 聊天 + 文档 + 任务 + Agent + 通话 + CRM 统一成一个工作台，所有对象之间 **@互相链接**，共享**团队级记忆**。创始团队的痛点：公司规模到 ~20 人时，每个团队各买各的工具，公司靠 MCP 和 Zapier 粘合——「公司不可计算」（The company was not computable）。

### 数据模型：双向图（Bidirectional Graph)

```
邮件 ──@──→ 任务 ──@──→ 文档 ──@──→ Agent 执行记录 ──→ PR
  ▲                                                   │
  └──────────────── 团队级共享记忆（双向图）◄───────────┘

  任意对象间 cross-reference 以图的原生边存储，
  而非每个工具各存一份外键/链接
```

这正对应视频里描述的链路：邮件关联任务 → 任务关联文档 → 文档触发 Agent → Agent 产出 PR，全部成为可检索、可追溯的团队记忆。

### 工程规模

```
macro/
├── apps/       web（SolidJS：浏览器 + Tauri 桌面 + 移动）、docs
├── services/   42 个可部署服务/worker/Lambda
├── crates/     167 个 Rust 库（领域逻辑、模型、db clients）
├── packages/   共享 TS（协作、lexical-core、loro-mirror）
├── infra/      Pulumi
└── docker/     本地 Compose 栈
```

服务遵循六边形架构（hexagonal layout）：入站适配器 → 领域核心（ports）→ 出站适配器。

### 拼装方案 vs macro

| 维度 | Slack+Linear+Notion+HubSpot 拼装 | macro |
|------|----------------------------------|-------|
| 跨工具关联 | 靠 URL 粘贴 / Zapier / MCP | 原生双向图存储 |
| 团队记忆 | 分散在各工具，Agent 无法整体检索 | 单一共享记忆层，Agent 可用 |
| 切换成本 | 每工具一份账号、一套学习曲线 | 单一界面 |
| 数据主权 | 各 SaaS 厂商 | 可自托管（AGPLv3）+ 零数据保留（模型供应商不训练客户数据） |
| 成熟度 | 各自成熟 | 相对早期（3.1k stars），适合小团队/大团队内先试点 |

### 判断

- ✅ 适合：10-50 人初创团队想摆脱工具拼装、且能接受 AGPLv3 自托管
- ❌ 不适合：已深度绑定现有工具链且迁移成本高的组织；需要非 AGPL 商业授权的（需联系 licensing@macro.com）

---

## 5. semantica — 可审计语义图谱基础设施

> [!info] 基本信息
> - **仓库**: https://github.com/semantica-agi/semantica
> - **作者**: Semantica（Kaif Ahmad 等）
> - **Stars 6.3k** / Forks 671 / MIT / PyPI: `pip install semantica`
> - 自我定位：「The Open Source Palantir for AI Agents」

注意：视频转述中的「scitt / context graph」实为 **semantica**，Context Graph 是其核心概念之一。

### 核心定位

给高风险、强监管场景（金融、医疗、法律、政府）的 AI 系统提供图谱原生的底层基础设施：摄取企业数据 → 抽取实体/关系/因果 → 构建 Context Graph + 知识图谱 → 在其上跑图分析与因果推理，全程带决策溯源（provenance）。

关键卖点：**图构建、推理、溯源全部确定性（deterministic），无需 LLM 参与**。它不是替换你的 LLM/向量库/Agent 框架，而是在它们下面垫一层可解释、可审计的语义基础设施。

### Vector RAG vs Semantica（官方对比）

| 维度 | Vector DB + RAG | 普通 LLM 记忆 | Semantica |
|------|----------------|--------------|-----------|
| 召回方式 | 向量相似度 | token 窗口 | 图遍历 + 语义搜索 |
| 决策历史 | 不存储 | 不存储 | 一等公民对象，可按先例检索 |
| 溯源 | 无 | 无 | W3C PROV-O，链接到源 |
| 推理 | 无 | 黑箱 | Forward chaining / Rete / Datalog / SPARQL，路径可解释 |
| 冲突检测 | 静默覆盖 | 静默覆盖 | 检测、标记、解决 |
| 时间旅行 | 无 | 无 | 任意时间点图谱快照 |
| 合规导出 | 无 | 无 | PROV-O / SHACL / OWL / RDF |
| 实体解析 | 无 | 无 | Blocking + 语义去重 |

### 核心能力地图

```
数据源 ──► 摄取管道 ──► Context Graph + KG ──► 应用面
              │                │                  │
   NER/关系/事件抽取      确定性推理引擎        REST API
   实体感知分块          (Rete/Datalog/SPARQL)  MCP Server
   语义去重合并          SHACL 约束/冲突检测     CLI
   (provenance 保留)     W3C PROV-O 溯源        Agno 原生集成
                        图分析(中心性/社区/链路预测)
                              │
                    多模态图存储（可热插拔）:
                    RDF: Oxigraph/Blazegraph/Jena/RDF4J
                    LPG: Neo4j/FalkorDB/AGE/Neptune
                    + Databricks(Unity Catalog)/Snowflake 连接器
```

### 代码示例：决策成为可审计节点

```python
from semantica.context import ContextGraph

graph = ContextGraph(advanced_analytics=True)

# 每个决策成为可查询、可审计的知识节点
decision_id = graph.record_decision(
    category="vendor_selection",
    scenario="Choose cloud provider for HIPAA workload",
    reasoning="AWS offers BAA, mature HIPAA tooling, and existing team expertise",
    outcome="selected_aws",
    confidence=0.93,
)

# 监管者问「为什么」时，给出结构化的因果祖先链
chain = graph.trace_decision_chain(decision_id)          # 完整因果链
similar = graph.find_similar_decisions("cloud vendor", max_results=5)  # 先例
impact = graph.analyze_decision_impact(decision_id)      # 下游影响图
compliant = graph.check_decision_rules("category", "vendor_selection") # 策略门
```

### 典型场景：信贷审批的审计链路

监管场景的痛点：underwriting Agent 的批准决定，数月后必须能回答监管者的「为什么」。向量库里只有 embedding，没有意义（meaning）；semantica 让每个决策带着完整因果链与来源记录存活在图里。

---

## 6. 行业趋势

### 6.1 2026 校招：AI 岗位井喷

视频核心数据（标注为视频转述，具体出处待查）：AI 职缺占校招比例攀升至 **37.56%**；企业筛选标准中「开源项目、GitHub 贡献、实战经验」权重已超越学校标签。

外部交叉验证（多源新闻，2025 年中发布）：

| 指标 | 数据 | 来源 |
|------|------|------|
| 新发校招 AI 岗位同比 | **+47.3%**（同期整体校招仅 +3.56%） | 脉脉高聘人才智库（2026 年 1-5 月） |
| 百度 2026 届校招 AI 岗占比 | **超 90%** | 观点网/新浪财经 |
| 阿里 2026 届校招 AI 岗占比 | **超 60%**（阿里云/国际/钉钉达 80%，高德 75%），总量超 7000 人 | 财联社、江苏教育频道 |
| 腾讯校招 | 技术类扩招 36%、产品类 39%，AI 相关岗位需求 +30% | 潮新闻 |
| 岗位结构 | 算法工程师需求占比从 2024 届 2.31% 升至 2026 届 2.96% | 2026 校招白皮书 |

结论一致：**校招增量几乎全部来自 AI 及相关技术岗位，非 AI 赛道原地踏步甚至萎缩**。对个人开发者的含义与视频建议一致——GitHub 上的开源与实战项目就是新的简历。

### 6.2 阿里云 Agent 安全最佳实践

核心论点（视频 + 报告摘要一致）：Agent 安全防护不能只靠 Prompt 输入/输出过滤，必须把 Agent 当作**具备执行权限的实体**，做全生命周期管控。

阿里云的多层防护体系（《2026 阿里云 AI Agent 安全最佳实践》）：

| 防护层 | 覆盖内容 |
|--------|---------|
| 资产与供应链层 | Agent 资产全量识别、血缘追踪、供应链组件审计 |
| 身份与访问层 | Agent 身份认证、权限边界、最小权限 |
| 行为检测与防护层 | 运行时行为监控、风险检测、实时防护 |

配套产品：智能体安全中心 ASC（Agent Security Center）——围绕智能体的输入输出、运行时行为、数据访问、身份认证、网络通信构建端到端安全闭环。

落地清单（结合视频观点整理）：

- ✅ 工具设计：每个工具最小权限、默认拒绝、显式声明副作用
- ✅ 权限边界：Agent 与用户权限分离，敏感操作需确认
- ✅ 上下文来源：对外部注入内容（网页、邮件、文档）做隔离，防间接提示词注入
- ✅ 审计日志：全链路记录 Agent 决策与工具调用，可回放
- ❌ 只做 Prompt 层输出过滤就上线
- ❌ 给 Agent 与人相同的命令行/账号权限（英伟达红队指南同样指出这是间接注入的重灾区）

---

## 7. 选型决策树

```
你的需求是什么？
│
├─ 给 Agent 找运行平台？
│   ├─ 要 Web UI + 插件生态，可接受 preview 不稳定
│   │   └─► deepseek-harness（npx 一键起，dsh-plugin 生态）
│   └─ 要持久化 REPL、自进化、长程后台任务
│       └─► prime-agent（RLM + Continual Harness）
│
├─ 提升产出物的视觉质量？
│   └─► diagram-design（Claude Code/Codex 一装即用，27 种图）
│
├─ 团队协作工具整合？
│   ├─ 10-50 人团队、接受 AGPL 自托管
│   │   └─► macro（双向图 + 团队级记忆）
│   └─ 大组织、高风险决策场景（金融/医疗/合规）
│       └─► semantica（确定性推理 + PROV-O 审计）
│
└─ 什么都不想装？
    └─ 至少记住趋势：Agent 基础设施正在从
       「单一对话框 + 向量 RAG」走向
       「可插拔平台 + 持久执行环境 + 图谱化记忆 + 全链路审计」
```

---

## 8. 名称纠错：视频转述 vs 官方仓库

本期视频/转述稿中有三处项目名称与官方仓库不符，搜索验证时务必用右列：

| 视频转述 | 官方实际 | 说明 |
|---------|---------|------|
| 「CDS 框架」 | **Cordis**（cordiverse/cordis） | deepseek-harness 底层框架 |
| 「m-core」 | **macro**（macro-inc/macro） | 团队工作台 |
| 「scitt / context graph」 | **semantica**（semantica-agi/semantica） | Context Graph 是其概念，产品名 semantica |
| 「27 种视觉图形」 | 27 visual types ✅ | README 正文一致（About 栏写 29，以正文为准） |

---

## 参考资料

- [视频：Github一周热点127期（IT咖啡馆）](https://www.youtube.com/watch?v=TY4JQhwEm1s)
- [deepseek-ai/deepseek-harness](https://github.com/deepseek-ai/deepseek-harness) · [Cordis 框架](https://github.com/cordiverse/cordis)
- [cathrynlavery/diagram-design](https://github.com/cathrynlavery/diagram-design) · [在线图库](https://cathrynlavery.github.io/diagram-design/)
- [PrimeIntellect-ai/prime-agent](https://github.com/PrimeIntellect-ai/prime-agent) · [RLM 博客](https://www.primeintellect.ai/blog/rlm) · [Continual Harness 论文 arXiv:2605.09998](https://arxiv.org/abs/2605.09998)
- [macro-inc/macro](https://github.com/macro-inc/macro) · [docs.macro.com](https://docs.macro.com)
- [semantica-agi/semantica](https://github.com/semantica-agi/semantica) · [docs.getsemantica.ai](https://docs.getsemantica.ai/)
- [2026 阿里云 AI Agent 安全最佳实践（发现报告）](https://www.fxbaogao.com/detail/5597654) · [阿里云智能体安全中心 ASC](https://help.aliyun.com/zh/asc/user-guide/agent-risk-1)
- [脉脉高聘：新发校招 AI 岗位同比大增 47.3%（证券时报）](https://www.stcn.com/article/detail/3995683.html) · [百度 2026 届校招 AI 岗占比超 90%（新浪财经）](https://finance.sina.com.cn/stock/estate/integration/2025-07-08/doc-infeufem5705195.shtml) · [阿里、字节 2026 届校招（财联社）](https://www.cls.cn/detail/2107582)
- [英伟达红队 AI Agent 安全实践指南（安全内参）](https://www.secrss.com/articles/87733)

## 相关笔记

- [[CLI 復興浪潮 - CLI Anything 與 OpenCLI 深度解析]]（同为 Agent 基础设施趋势）
