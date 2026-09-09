---
title: 提示词负债大扫除：删光 CLAUDE.md 的维护哲学
aliases: [Prompt Debt, 提示词负債, Unhobbling, CLAUDE.md 大扫除, Boris Cherny 访谈, 消融实验提示词]
tags:
  - prompt-engineering
  - claude-code
  - ai-agent
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=Z-4AsgTYv2c"
  - "https://www.patreon.com/GaryChen/posts/ni-gai-qing-li-167820602"
  - "https://www.ycombinator.com/library/UN-boris-cherny-building-claude-code"
  - "https://charliehills.substack.com/p/delete-your-claudemd"
author: Gary Chen（频道）；核心观点出自 Boris Cherny（Claude Code 创造者）
created: 2026-09-02
updated: 2026-09-02
description: Claude Code 之父 Boris Cherny 建议 every six months delete your CLAUDE.md——提示词有折旧性，本文拆解 Prompt Debt、Unhobbling、消融实验与三分法清理框架。
level: intermediate
stars: 4
note: 视频无字幕（Tier 0 IP 封锁、Tier 1 转录面板空壳、Tier 1.5 NotebookLM 后端异常），内容基于视频章节 + Patreon 文章免费预览 + YC 访谈原文 + Charlie Hills 文章交叉验证。视频现标题为「我刪掉 2500 行 Prompt，結果 AI 反而變聰明了？」（频道已从「Claude Code 之父建議，每六個月刪光你的 CLAUDE.md？」改名）。
---

# 提示词负债大扫除：删光 CLAUDE.md 的维护哲学

> Claude Code 之父 Boris Cherny 在 YC Startup School 2026 访谈（与 Diana Hu 对谈）中说："For people that aren't building agentic products, but you're using Claude Code, every 6 months delete your Claude.md. Delete your skills. Delete your hooks. See what the model does and it might surprise you." 这不是随口一说——Anthropic 自己在 Opus 5 发布时删掉了超过 80% 的 system prompt。本笔记拆解背后的 Prompt Debt / Unhobbling 逻辑，以及一套可落地的清理方法论。

> [!info] 基本信息
> - **视频**: 我刪掉 2500 行 Prompt，結果 AI 反而變聰明了？（Gary Chen，2026-09-02，约 15 分钟，7.92 万订阅）
> - **原始来源**: Boris Cherny @ YC Startup School 2026 访谈
> - **完整文章**: Patreon 付费文章「你該清理 Agentic 時代下的技術債了嗎？｜AI Instructions Rebuild Set」（本笔记基于免费预览大纲）

## 目录

- [[#1. 核心论点：提示词有折旧性]]
- [[#2. Unhobbling：删提示词不是减法，是放虎归山]]
- [[#3. Prompt Debt 的累积循环]]
- [[#4. Instructions 三分法：哪些能删、哪些不能删]]
- [[#5. 消融实验（Ablation）：三步测试法]]
- [[#6. 两大过滤提问与「由难到易」的补正逻辑]]
- [[#7. 真正做错的是验证：Evaluation 优先]]
- [[#8. Gary 的 2500 行大扫除实录]]
- [[#9. 外部印证：Anthropic 官方指导与研究文献]]
- [[#10. 行动清单]]
- [[#参考资料]]

---

## 1. 核心论点：提示词有折旧性

视频章节：`0:00 Boris 说每半年删光设定` / `2:22 什么是提示词负债`

一句话总结：**过去有效的规矩，只代表它解决了当时特定模型的局限，不代表具备长久价值。** 提示词和代码一样有「技术债」（Technical Debt），而且折旧更快——每次模型升级都在重新定价你写的每一条规则。

| 维度 | 代码的技术债 | 提示词负债（Prompt Debt） |
|------|-------------|--------------------------|
| 产生方式 | 赶工绕过设计、补丁叠补丁 | AI 犯一次错就加一条规矩，「只增不减」 |
| 折旧速度 | 随需求变化缓慢折旧 | 每次模型版本升级都可能整体贬值 |
| 检测难度 | Lint / 测试 / 架构评审可发现 | 很难一眼看出哪句指令已「过期」 |
| 代价 | 维护成本上升 | 占用 Context Window、限制模型发挥、拖慢执行 |
| 清理手段 | 重构 | 删除 + 消融实验（见第 5 节） |

✅ 正确心态：把 CLAUDE.md 当「有保质期的消耗品」，随模型版本「换季」
❌ 错误心态：把 CLAUDE.md 当「只进不出的资产」，越长越安心

---

## 2. Unhobbling：删提示词不是减法，是放虎归山

视频章节：`2:18`（Unhobbling 概念）

Boris 透露：每逢新模型推出（如 Opus 5），Claude Code 团队会大扫除，这次删掉了超过 80% 的 system prompt。关键洞察是——**这不是单纯把基础模型练强，而是移除过去针对旧模型弱点层层堆叠的补丁，让新模型原本就具备的高阶能力得以释放。**

「解開束縛」（Unhobbling）的本意：去掉拐杖，让本来能跑的人跑步。

```
        旧模型时代                          新模型时代
   ┌─────────────────┐                ┌─────────────────┐
   │   基础模型能力    │                │   基础模型能力    │
   │   （较弱）       │                │   （大幅提升）    │
   └────────┬────────┘                └────────┬────────┘
            │ 为弱点打补丁                        │
            ▼                                    ▼
   ┌─────────────────┐                ┌─────────────────┐
   │  系统提示词补丁   │                │  补丁成了枷锁：   │
   │  「必须先做 X」   │      ──►      │  绕路流程限制智慧  │
   │  「禁止直接 Y」   │    删掉补丁    │  严格检查拖慢执行  │
   └─────────────────┘    (unhobbling) └─────────────────┘
                                     模型原生能力被释放
```

判断信号：你的 Instructions 里是否有「模型现在本来就会做」的规定？这类规则在新模型上从「辅助」变成了「束缚」——模型被强迫走为旧模型设计的绕路流程。

---

## 3. Prompt Debt 的累积循环

视频章节：`14:34`（只增不减的开发习惯）

多数人的提示词演化路径是一个单行道：

```
   AI 犯错 ──► 加一条规则 ──► 暂时有效 ──► 模型升级
     ▲                                        │
     │                                        ▼
     └────── 规则失效/冲突 ◄─── 旧补丁成为阻力 ◄┘
   
   ✗ 断裂点：从来没有人走「删除」这条边
```

特征对照：

| 症状 | 表现 |
|------|------|
| 只增不减 | AI 犯一次错就加一条规矩，从不回顾 |
| 规则冲突 | 多条旧规则互相矛盾，模型任意取舍（你看不到哪条赢了） |
| 上下文膨胀 | 每条规则每个 session 都吃 Context Window |
| 越用越笨 | 规则越多，模型自由推理空间越小 |

---

## 4. Instructions 三分法：哪些能删、哪些不能删

视频章节：`4:18 哪些能删、哪些不能删`

这是全文最具操作性的框架。清理前先把所有 Instructions 分成三类：

| 类别 | 定义 | 典型例子 | 处置策略 |
|------|------|----------|----------|
| **必要 Context** | 模型不可能通过推理得知的背景信息 | 品牌定位、项目存放路径、受众定义、团队不成文惯例 | ✅ 必须保留 |
| **Workflow Control** | 硬性规定执行顺序、指定逐步格式、写死操作逻辑的规则 | 「先跑测试再改代码」「必须按步骤 1-2-3」「禁止直接重构」 | ⚠️ 优先清理与测试（最常由旧版补丁演变而来） |
| **安全与验收边界** | 涉及底线设定的规则 | 资料存取权限、人工审查节点、发布标准、引用要求 | 🔒 审慎验证，切勿为精简而贸然关闭 |

判断决策树：

```
这条 Instruction 是什么？
│
├─ 模型推不出来、查不到的背景信息？
│    └─ YES → 必要 Context → 保留
│
├─ 规定了「怎么做」的流程/顺序/格式？
│    └─ YES → Workflow Control
│         ├─ 先删掉裸跑测试
│         └─ 输出变差 → 逐行加回，观察是哪一条在起作用
│
└─ 涉及权限、审查、发布、合规底线？
     └─ YES → 安全边界 → 单独验证后再动，
              且验证标准是「安全性不下降」而非「输出更好」
```

关键认知：**真正该记录的是模型查不到的东西**（不成文惯例、决策脉络），而项目结构、指令语法这类模型读一眼就懂的内容不需要抄写进 Instructions。

---

## 5. 消融实验（Ablation）：三步测试法

视频章节：`7:21 Ablation 测试三步骤` / `4:00`

删减不是盲目全清，而是借鉴研究方法：**将设定拿掉后逐行加回（line by line），观察拿掉某条规则对整体输出的实质影响。** 这正是机器学习研究里的「消融实验」（Ablation Study）——逐个移除组件，量化每个组件的真实贡献。

```
  Step 1                Step 2                Step 3
  清空设定              逐行加回              记录判定
┌──────────┐      ┌──────────────────┐     ┌─────────────┐
│ 备份现有  │      │ 加回第 N 条规则   │     │ 有实质影响？  │
│ CLAUDE.md │ ───► │ 跑同一组任务     │ ──► │  YES → 保留  │
│ Skills、 │      │ (Evaluation 集)  │     │  NO  → 删除  │
│ Hooks    │      │ 对比输出差异      │     │  下一行 ↺   │
└──────────┘      └──────────────────┘     └─────────────┘
```

Claude Code 提供了一条现成的对照组命令（Gary 在 Patreon 文章中重点介绍）：

```bash
# --safe-mode 关闭全部客制化：CLAUDE.md、Skills、MCP、Hooks
# 但模型、工具与权限完全不变（v2.1.169+）
claude --safe-mode
```

用途对照：

| 工具 | 关掉什么 | 回答什么问题 |
|------|----------|-------------|
| `--safe-mode` | CLAUDE.md + Skills + MCP + Hooks 全部 | 整套设定到底有没有在帮忙？值不值得逐句细查？ |
| 手动备份 + 删除 | 仅 CLAUDE.md 或某一层 | 单层设定的净贡献是多少？ |
| `/clear` | 当前对话上下文 | （对照）与设定无关，勿混淆 |

先跑 `--safe-mode` 裸奔基线：如果裸奔结果不差甚至更好，说明大扫除收益可观；如果明显变差，恭喜你——你的设定真的有用，再做逐行消融找出关键行。

---

## 6. 两大过滤提问与「由难到易」的补正逻辑

视频章节：`13:08 大扫除先问这两题` / `13:32` / `13:42` / `12:33`

大扫除时的两大灵魂拷问：

1. **「这句话有没有真的改变模型的行为？」**——若模型原本就会做，这条规则只是在白白消耗 Context Window。
2. **「这件事它自己查得到吗？」**——项目结构、指令语法，模型读取后即可理解，不需重复抄写；该记录的是查不到的不成文惯例与决策脉络。

补正路径则应该「由难到易」，而不是预先塞满防御性提示词：

```
  先交付真实且具挑战性的任务
            │
            ▼
  观察反复失败的确切瓶颈
            │
            ├── 信息缺失？ ──► 补强 Prompt（写清查不到的 Context）
            ├── 流程复杂易错？ ──► 挂载 Skill（固化多步操作）
            └── 缺外部能力/数据？ ──► MCP 串接工具与资料
```

✅ 先跑再补：让真实失败告诉你缺什么
❌ 先补再跑：想象出 100 条「可能有用」的规则，大部分在解决不存在的问题

---

## 7. 真正做错的是验证：Evaluation 优先

视频章节：`9:27 真正做错的是验证`

Gary 的核心反省：多数人（包括过去的他）删除设定时翻车，问题不在「删」，而在**没有先定义什么叫做「做得好」**。没有 Evaluation，删了之后你根本说不清输出是变好还是变坏，只能靠感觉。

正确顺序：

```
  定义验收标准（什么叫做得好）
            │
            ▼
  准备一组代表性任务（覆盖日常主要场景）
            │
            ▼
  --safe-mode 裸奔 vs 带全套设定 各跑一遍
            │
            ▼
  有证据 → 再做逐行消融，每个删除决定都有数据撑腰
```

| 做法 | 结果 |
|------|------|
| 凭感觉删 | 输出变差也不知道是哪条规则在背锅，只能全量回滚 |
| Eval + 消融 | 每条规则的去留都有对照数据，删得安心、留得明白 |

---

## 8. Gary 的 2500 行大扫除实录

视频章节：`6:16 我删掉 2500 行的实录`

Gary 用这套方法对自己的 Instructions 做了一次大扫除，数据如下：

| 指标 | 数值 |
|------|------|
| 删除行数 | 2500+ 行 prompt |
| 测试耗时 | 2 天 |
| 心智负担 | 明显减少 |
| Token 消耗 | 降低 |
| 工作流可维护性 | 显著改善 |

（细节步骤与 Rebuild Set 提示词模板在 Patreon 付费部分，免费预览仅到大纲。）

---

## 9. 外部印证：Anthropic 官方指导与研究文献

Gary 的实战经验与外部权威来源高度一致：

1. **Anthropic 官方对 CLAUDE.md 的指导**（via Charlie Hills, 2026-08-09）：
   - "target under 200 lines per CLAUDE.md file. Longer files consume more context and reduce adherence."（建议每个 CLAUDE.md 控制在 200 行以内）
   - "Claude treats them as context, not enforced configuration."（规则是上下文，不是强制配置）
   - "if two rules contradict each other, Claude may pick one arbitrarily."（冲突规则会被任意取舍）
2. **Lost in the Middle**（Liu et al., TACL）：模型对长上下文的开头和结尾读得仔细，**中间部分是被略读的**。CLAUDE.md 在每次会话最先注入，行数越多，你真正要问的内容被推离「仔细阅读区」越远。
3. **Charlie Hills 的审计实践**（与 Gary 方法互补）：
   - 195 个 Skills 里真正用过的只有 65 个——问题不是数量，而是多少个在做同一件事
   - 审计 prompt 会先抓取 Anthropic 当日发布的 live guidance 再逐行判定 DELETE/KEEP/REWRITE，每条 DELETE 必须引用官方原文佐证
   - 他的结论与 Gary 呼应："I'm not deleting mine. I'm maintaining it."——大扫除不是一次性事件，是维护习惯

三层架构的分工（Charlie Hills 模型，可作为清理后的目标形态）：

```
  CLAUDE.md（前门接待，<200 行）
      │  只放：我是谁、东西在哪、全局偏好
      ▼
  Hooks（触发器：说到关键词 → 拉起对应项目文件）
      │  「说 carousel」→ 自动载入 carousel 项目规范
      ▼
  Skills / 项目 .md（按需加载的档案柜）
       项目专属规范平时不进上下文，用到才加载
```

---

## 10. 行动清单

结合视频与外部资料的落地步骤：

1. **备份**：完整备份 CLAUDE.md、Skills、Hooks（git 或复制目录）
2. **建 Evaluation**：写下 5-10 个代表性任务与「做得好」的标准
3. **跑基线**：`claude --safe-mode` 裸奔跑一遍，对比带全套设定的输出
4. **三分法盘点**：把每条 Instruction 归入 必要 Context / Workflow Control / 安全边界
5. **消融**：Workflow Control 逐行删除/加回测试；安全边界单独验证
6. **两问过滤**：每条保留项都要回答「真的改变行为吗」「它自己查得到吗」
7. **换季提醒**：与模型版本升级周期绑定（约每 6 个月），重复 1-6

✅ 定期消融审查：配合模型升级周期，逐条增删比对
❌ 盲目全清：跳过 Evaluation 直接删光，把品牌调性、安全边界一起丢掉
✅ 流程做减法：先清空死板的 Workflow Control，让新模型裸跑，只在真实出错点精准补充

---

## 参考资料

- [我刪掉 2500 行 Prompt，結果 AI 反而變聰明了？（Gary Chen, YouTube）](https://www.youtube.com/watch?v=Z-4AsgTYv2c)
- [你該清理 Agentic 時代下的技術債了嗎？｜AI Instructions Rebuild Set（Gary Chen, Patreon）](https://www.patreon.com/GaryChen/posts/ni-gai-qing-li-167820602)
- [Boris Cherny: Building Claude Code（YC Startup School 2026 访谈页）](https://www.ycombinator.com/library/UN-boris-cherny-building-claude-code)
- [Delete your CLAUDE.md（Charlie Hills, MarTech AI）](https://charliehills.substack.com/p/delete-your-claudemd)
- [Claude Code --safe-mode 说明（ofox.ai 指南）](https://ofox.ai/blog/claude-code-safe-mode-guide-2026/)
- [safe-mode flag 讨论（claude-code GitHub issue #53650）](https://github.com/anthropics/claude-code/issues/53650)
- [Lost in the Middle: How Language Models Use Long Contexts（Liu et al., TACL）](https://arxiv.org/abs/2307.03172)

## 相关笔记

- [[Andrej Karpathy Skills - 四条规则驯服AI编程助手]]
- [[Hermes Agent 从零搭建个人AI助手教程]]
- [[Anthropic 创始人手册 - 构建AI原生创业公司]]

---

*文档生成时间：2026-09-02*
*基于 Gary Chen 视频《我刪掉 2500 行 Prompt，結果 AI 反而變聰明了？》及外部交叉验证来源*
