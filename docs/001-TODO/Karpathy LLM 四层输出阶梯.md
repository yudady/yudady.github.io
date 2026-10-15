---
title: Karpathy LLM 四层输出阶梯 — 从写 Prompt 到读输出
aliases: [LLM 四层输出阶梯, Karpathy 输出阶梯, ASD-STE100 受控语言, Disposable Artifacts 一次性制品]
tags:
  - ai/agent
  - prompt-engineering
  - llm
  - knowledge-management
  - status/active
  - type/learning-note
source:
  - "https://www.youtube.com/watch?v=FESIfabbzy8"
  - "https://x.com/karpathy/status/2105819303471976479"
  - "https://asd-ste100.org/"
  - "https://x.com/trq212/status/2052811606032269638"
  - "https://dev.to/aj1thkr1sh/i-turned-andrej-karpathys-tips-on-understanding-llm-output-into-an-open-source-agent-skill-1mia"
author: Why QQ（解读 Andrej Karpathy 2026-10-02 原推）
created: 2026-10-15
updated: 2026-10-15
description: Karpathy 提出 LLM 四层输出阶梯（受控语言→图表→交互 HTML→定制讲解视频），把高密度 AI 输出降维成人脑能快速吞吐的形态，应对「生成快、审读慢」的新瓶颈
level: intermediate
stars: 4
---

# Karpathy LLM 四层输出阶梯 — 从写 Prompt 到读输出

> 2026-10-02，Andrej Karpathy 发推：我们会花越来越多时间去**理解**语言模型的输出。他给出四层方案——从航空维修手册的受控语言 ASD-STE100，到图表，到交互 HTML，再到他最看好的 3b1b 风格定制讲解视频。推文一天内冲到 400 万浏览、39K 赞、54K 收藏（Why QQ 频道 2026-10-05 解读视频笔记）。

## 目录

- [范式转移：瓶颈从「写 Prompt」变为「读输出」](#范式转移瓶颈从写-prompt-变为读输出)
- [四层输出阶梯](#四层输出阶梯)
- [软件新物种：Disposable Artifacts](#软件新物种disposable-artifacts)
- [社区接力与工程批评](#社区接力与工程批评)
- [输出形态决策模型与三条即用提示词](#输出形态决策模型与三条即用提示词)
- [验证与勘误记录](#验证与勘误记录)
- [参考资料](#参考资料)
- [相关笔记](#相关笔记)

---

## 范式转移：瓶颈从「写 Prompt」变为「读输出」

过去三年开发者都在研究怎么给 AI 写提示词、把需求说清楚、让模型少犯错。Karpathy 这条推文把视线拉到另一头：**AI 写得越来越快，人读得越来越慢**。

背景人物：Karpathy 是 OpenAI 创始成员、特斯拉前 AI 总监，后创办教育公司 Eureka Labs；2026-05-19 加入 Anthropic 预训练团队，带队方向是用 Claude 加速下一代模型的预训练（用 AI 训练 AI）。他先后带火两个词：vibe coding（2025）与 agentic engineering（2026 年初）——你不再亲手写代码，你监督 agent 干活。

核心矛盾：

```
AI 生成速度（秒级出 500 行方案）
        │
        ▼
人类审查带宽（逐行读 + 理解 + 判断）
        │
        ▼
监督的前提是「看得懂」── 看不懂的监督 = 摆设

审核员的阅读带宽 = 整条 AI 研发流水线的吞吐上限
```

所以关键不再是「如何让 AI 写更多」，而是「如何让 AI 把信息降维成人类大脑能快速吞吐的形态」。四层阶梯的本质：**把同一份信息重排成人类吃得起的样子**。

---

## 四层输出阶梯

```
理解成本高 ▲
          │  ┌──────────────────────────────────────────┐
          │  │ 第 4 层 定制讲解视频                       │ ← Karpathy 最看好
          │  │ 3b1b 风格 / Manim 动画 + ElevenLabs 配音   │   深度教学
          │  ├──────────────────────────────────────────┤
          │  │ 第 3 层 单文件交互 HTML                    │ ←「HTML 是新 Markdown」
          │  │ 侧栏导航 / Tabs / 折叠区 / 轻交互          │   方案评审
          │  ├──────────────────────────────────────────┤
          │  │ 第 2 层 图表                               │ ← 视觉 = 高带宽通道
          │  │ Mermaid 流程图 / SVG                       │   架构 / 状态机 / 调用链
          │  ├──────────────────────────────────────────┤
          │  │ 第 1 层 受控语言 ASD-STE100                 │ ← 一词一义 / 短句 / 主动语态
          │  │ strict / 80% / light 三档                  │   逼模型「把话说死」
          │  └──────────────────────────────────────────┘
          │    生成 token 成本逐层上升，人类阅读速度逐层加快
理解成本低 ▼
```

### 第 1 层：受控语言（ASD-STE100）

源自 1970s 末欧洲航空工业的维修手册标准（AECMA 开发，1986 年发布指南，2025 年成为国际标准 Issue 9）。规范要点：

| 规则 | 约束 |
|------|------|
| 词表 | 只批准约 900 个词 |
| 一词一义 | 每个词只允许一个意思 |
| 句长 | 程序性句子 ≤20 词，描述性句子 ≤25 词 |
| 语态 | 必须主动语态，不许省略冠词 |
| 命名 | 同一个东西永远用同一个名字 |

设计目标：让任何一名机务人员在任何国家读一遍就能做对动作。

**AI 场景的价值**：模型倾向用圆滑、听起来自信但不用负责的模糊措辞。受控语言会把这类表述直接卡掉——一旦逻辑有错，审核者立刻能看出来。这一点比文笔重要得多。

**落地不用全量死磕**：Karpathy 原推给了三档 strict / 80% / light。「八成严格度」保留短句、主动语态、一词一义，放开专业术语词表——既能读，又不会每句都像航空手册。

### 第 2 层：图表（Mermaid / SVG）

与其让模型写，不如让它画。人脑约三分之一区域在处理视觉信息——文字是窄管子，图是宽管子，一图的信息吸收速度和逐段读文字不在一个量级。

- 适用：系统架构、状态流转、模块调用链
- 做法：问题结尾加一句「画一张图说明」，让它生成 Mermaid 流程图，或直接写 SVG

### 第 3 层：单文件交互 HTML（HTML as New Markdown）

呼应五个月前 Claude Code 团队工程师 Thariq Shihipar 的长文「HTML is the new markdown」（2026-05）——他几乎不再手写 Markdown，方案评审报告全让 Claude 输出 HTML。Karpathy 当时就在评论区附议「把回答组织成 HTML 再打开浏览器」，5 月那条评论正是 10 月推文的草稿。

论点核心：**超过 100 行的 Markdown 没人读完**。同样内容排成网页，有导航、有标签页、有折叠区，人才会真的看完。看没看完决定审核质量；格式决定的是信息有没有真正进入大脑，好不好看只是顺带的事。

### 第 4 层：定制讲解视频（3b1b 风格）

Karpathy 原推最看好（most bullish）的一层：

- 3b1b = 3Blue1Brown，用动画讲数学的频道，背后是开源动画引擎 Manim
- 模型完全可以生成 Manim 代码，渲染出带数学轨迹的动画，再接上语音合成（如 ElevenLabs）配好解说
- 提示词原话：「给我做一个 3b1b 风格的视频讲解 <主题>，用我的 ElevenLabs 配音」
- 没有付费 API，就让模型帮你找本地算力能跑的免费替代方案
- Karpathy 跟了一句：这条路现在已经真的能走通了

四层横向对比：

| 层 | 形态 | 工具/约束 | 适用场景 | Token 成本 | 验证成本 |
|----|------|-----------|----------|-----------|----------|
| 1 | 受控语言 | ASD-STE100（strict/80%/light） | 复杂概念解释 | 最低 | 最低 |
| 2 | 图表 | Mermaid / SVG | 架构、状态机、调用链 | 低 | 低 |
| 3 | 交互 HTML | 单文件 + 侧栏 + 折叠 | >100 行方案评审 | 中（MD 的 2-4 倍） | 中 |
| 4 | 讲解视频 | Manim + ElevenLabs | 深度教学 | 最高 | 最高（难约 10 倍） |

---

## 软件新物种：Disposable Artifacts

推文真正的点睛在结尾：智能和代码越来越便宜，便宜到可以要求一种以前根本不合理的东西——**大型定制、用完就扔的软件制品**。

```
传统软件观                        抛弃式制品观
─────────────                    ─────────────
软件 = 长期资产                    软件 = 一次性消耗品
要版本控制 / 重构 / 维护            为一个具体疑问现场生成
成本：持续投入                     成本：趋近于零
价值：长期运行                     价值：为你一个人省大脑带宽
                                  看完即可删除
```

为你一个人的一个问题，现场写一个交互网页、渲染一条 30 秒讲解视频，看完就删。软件存在的唯一目的是帮你的大脑省带宽。回想这一年：多少次让 AI 随手写个小脚本，跑完就再没打开过？这个行为早已开始，Karpathy 只是给它起了名字。

---

## 社区接力与工程批评

推文发出后社区快速接力：

- **中文圈**：当天出现长文解读，把四层方案叫做「降维输出」——每上一层，理解成本往下砸一个量级，并配中英双语阶梯图
- **英文圈（工程化）**：有人直接写成 CLAUDE.md / AGENTS.md 全局配置；有人做成 Claude Code 技能（开源项目 make-it-click，MIT，2026-10-05 发布，按「最简单的能让想法 click 的格式」自动选层）

社区 CLAUDE.md 配置片段（把第 1/2/3 层固化成规则）：

```markdown
# 输出规范（贴进全局 CLAUDE.md / AGENTS.md）
- 每句一个指令，最多 20 个词
- 主动语态，说清楚谁做了什么
- 先给答案，再给细节
- 超过 3 个步骤的流程必须附带 SVG 图
- 我要求「解释成 HTML」时，输出单文件交互页面
```

工程现实中的取舍与批评：

| 批评 | 内容 | 应对 |
|------|------|------|
| 指令漂移 | 单靠 prompt 约束受控语言，长文写着写着就破功 | 把检查做成 Git hook / 自动化测试级别的强制流程 |
| Token 开销 | HTML 的 token 消耗是 Markdown 的 2-4 倍 | 给人看的才用 HTML，机器链路保持 MD/JSON |
| Git diff 噪音 | 大量标签与样式属性淹没真正的改动 | MD 做源文件，HTML 只做生成产物 |
| 格式治不了废话 | 500 行没人需要的方案换成 HTML 也救不了 | 格式不能减少废话，但能让废话无所遁形 |
| 验证成本悖论 | 层级越高画面越流畅，逐行核实难度反而高约 10 倍（视频里一个错误公式比文档里难发现十倍） | 高层制品定位为可抛弃辅助品 |

最佳实践清单：

- ✅ 先问「这份输出给谁看」，再选层
- ✅ 复杂概念解释用八成严格度 STE100
- ✅ 代码审查、排错先图后文
- ✅ 超过 100 行的方案让模型输出单文件 HTML
- ✅ 真相底本（Source of Truth）永远留在底层可验证文本里
- ❌ 不要把四层阶梯当格式清单收藏完就走——开头那句「工作向监督和理解层面迁移」才是正题
- ❌ 不要给机器读的中间产物套 HTML（diff 乱、token 贵）
- ❌ 不要指望格式挽救内容质量

---

## 输出形态决策模型与三条即用提示词

### 决策树：以「下游接收端是谁」定形态

```
这份输出给谁看？
│
├── 给机器 / Git 读（CI、diff、下一个 agent）
│     → Markdown / JSON：结构干净、diff 可读、token 经济
│
├── 给人快速决策 / 审阅
│     → 第 2 层图 + 第 3 层 HTML：牺牲一点 token 换阅读效率
│
└── 给人深度教学 / 掌握概念
      → 第 4 层视频 / 交互 sandbox

隐藏维度：验证成本随层级上升
锚点原则：毛点（锚点）永远留在底层那份可验证的文本里，
高层制品可抛弃
```

### 三条今天就能用的提示词

```text
# 1. 解释复杂概念（追加在 prompt 结尾）
按 ASD-STE100 八成严格度回答本问题：强制使用短句、主动语态、
同一概念全篇使用同一专有名词，去除模棱两可的套话。
```

```text
# 2. 接手老仓库 / 审查 AI 大改动 / 排查报错（先图后文）
请先绘制架构流程图（Mermaid 或 SVG）说明调用链与状态流转，
随后再附上核心文本说明。
```

```text
# 3. 超过 100 行的方案 / 报告评审（单文件 HTML）
请将方案整合为单一 HTML 文件输出（内联样式 + 侧边导航 + 折叠区），
以便直接在浏览器阅读审阅。
```

三条不用装任何插件。频道作者自述用得最多的是第 2 条：接手别人代码时先出图再读码，省的时间以小时计。

### 趋势判断

- Karpathy：视频之上还有一层——交互式的、由模型直接生成的动态内容（5 月已给过答案）
- 频道作者的保守判断：一年内「输出格式」会变成和模型名称一样的显式参数，CLI 工具会把渲染层（纯文本/图/网页/视频）按场景切换；人退到更高位置做判断做取舍，工具负责把信息摆到眼前最舒服的位置
- 一句话：**会问 AI 的人很多，会读 AI 的人很少**——这是这条推文值 5 万收藏的原因

---

## 验证与勘误记录

| 视频声称 | 验证结果 | 来源 |
|----------|----------|------|
| Karpathy 2026-10-02 发推，一天 400 万浏览 / 39K 赞 / 54K 收藏 | ✅ 推文存在；互动数据为视频快照口径（10-03 采集），移动目标 | x.com/karpathy/status/2105819303471976479 |
| 标题「5.7 万加收藏」 vs 正文字幕「54000 次收藏」 | ⚠️ 同一视频内口径不一，以字幕 54K（10-03 快照）为准 | 视频字幕第 4 行 |
| ASD-STE100 源自 1979 年欧洲航空工业 | ✅ 基本准确：官方口径为 late 1970s 由 AECMA 开发、1986 年发布指南、2005 年成国际规范、2025 年成国际标准（Issue 9，2025-01-15） | asd-ste100.org |
| 「八成严格度」是 Karpathy 的建议 | ✅ 原推含三档：strict / 80% / light | dev.to（make-it-click 作者转述） |
| Thariq 是 Claude Code 团队「工程负责人」 | ⚠️ Thariq Shihipar 为 Claude Code 团队工程师（Simon Willison 口径），「负责人/核心」表述偏重 | simonwillison.net/2026/May/8/ |
| Thariq 文章 48 小时上千万浏览 | ⚠️ 口径分歧：本库旧笔记《HTML 的不合理有效性》记 1200 万阅读；「上千万」为约数，两处均无一手快照 | 本库笔记对照 |
| Karpathy 2026-05-19 加入 Anthropic 预训练团队，用 Claude 加速预训练 | ✅ Axios / TechCrunch / Reuters 多源确认，团队向 Nick Joseph 汇报 | axios.com/2026/05/19/ |
| 字幕「YUICALAABS」教育公司 | ✅ ASR 噪声，正名为 Eureka Labs | TechCrunch |
| 有人做成 Claude Code 技能 | ✅ make-it-click（github.com/ajithraghavan/make-it-click，MIT），2026-10-06 有 Show HN 帖 | dev.to + HN Algolia |
| 「格式治不了废话」的冷水来自 HN | ❓ 未能定位到该推文在 HN 的大讨论帖（Algolia 仅 1 分小帖）；批评声主要见于 X / 工程圈，不排除视频转述口径宽泛 | hn.algolia.com（2026-10-15 查询） |
| 字幕 ASR 专名噪声（Carpathy/Carpusy、Feric、stent100、mer、1LabsMEO、mark 和 Jason） | ✅ 正名：Karpathy、Thariq、ASD-STE100、Mermaid、ElevenLabs、Markdown 和 JSON | 字幕清洗对照 |

时间线（本推文的前史）：

```
2026-05-08  Thariq「HTML is the new markdown」长文（Simon Willison 同日评论）
2026-05-14  ── 本库落档《HTML 的不合理有效性》
2026-05-18  Lenny's Newsletter 访谈 Thariq
2026-05-19  Karpathy 加入 Anthropic 预训练团队；在 Thariq 帖下附议
2026-10-02  Karpathy 发四层阶梯推文（一天 400 万浏览 / 39K 赞 / 54K 收藏）
2026-10-05  Why QQ 解读视频；dev.to make-it-click 技能文章
2026-10-06  Show HN：make-it-click 技能帖
2026-10-15  ── 本笔记落档（互动数据均为 10-03 快照口径）
```

---

## 参考资料

- [视频原文：Karpathy：LLM四层输出阶梯（Why QQ）](https://www.youtube.com/watch?v=FESIfabbzy8)
- [Karpathy 原推（2026-10-02）](https://x.com/karpathy/status/2105819303471976479)
- [ASD-STE100 官方站点（STEMG）](https://asd-ste100.org/)
- [Thariq 原推：HTML is the new markdown](https://x.com/trq212/status/2052811606032269638)
- [Simon Willison：The unreasonable effectiveness of HTML](https://simonwillison.net/2026/May/8/unreasonable-effectiveness-of-html)
- [InfoQ：Anthropic Lead — HTML Increasingly Better Than Markdown](https://www.infoq.com/news/2026/06/anthropic-html-markdown-agent)
- [dev.to：make-it-click 开源技能（四层阶梯落地）](https://dev.to/aj1thkr1sh/i-turned-andrej-karpathys-tips-on-understanding-llm-output-into-an-open-source-agent-skill-1mia)
- [TechCrunch：Karpathy joins Anthropic's pre-training team](https://techcrunch.com/2026/05/19/openai-co-founder-andrej-karpathy-joins-anthropics-pre-training-team)
- [Search Engine Journal：Karpathy on Simplifying LLM Outputs](https://www.searchenginejournal.com/karpathy-llm-aircraft-manual-writing/591813)

## 相关笔记

- [[HTML 的不合理有效性 — Claude Code 文档范式转移]] — 第 3 层 HTML 的单点深挖：Thariq 原文解读、20 个 HTML 案例库、MD 党 vs HTML 党之争
- [[Agent 三层交付 - 让 AI 输出不再塞满聊天框]] — 工程流水线视角的输出架构：通知层 / Markdown 机器源 / HTML 人类报告的 Artifact Contract

---

*文档生成时间：2026-10-15*
*基于 Why QQ 视频字幕（NotebookLM 转录）+ 用户 Content Insights + 多源交叉验证*
