---
title: Jev 极速决策模型实测笔记
aliases:
  - Jev 实测
  - TypeSafe System One Model
tags:
  - decision-model
  - typesafe-ai
  - ai-sdk
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=tYvu6IpSfiM"
  - "https://typesafe.ai/blog/introducing-system-one-models-and-jev"
  - "https://docs.typesafe.ai"
  - "https://vercel.com/changelog/typesafe-ai-jev-now-available-on-ai-gateway"
  - "https://evals.typesafe.ai"
  - "https://github.com/typesafe-ai/skills"
author: 01Coder（小木头）；产品方 TypeSafe AI
created: 2026-09-20
updated: 2026-09-20
description: TypeSafe AI 的 Jev「System One Model」不生成文本、只输出带校准概率的类型化决策；本笔记覆盖三原语、官方数字 vs 独立实测、社区案例与选型边界。
level: intermediate
stars: 4
---

# Jev 极速决策模型实测笔记

> 01Coder（小木头）2026-09-18 视频的实测整理 + 官方一手来源交叉验证。Jev 是 TypeSafe AI 发布的首个 System One Model：状态进、类型化概率出，不生成任何文本。定位不是替代 LLM，而是接管软件里大量的「快决策」——路由、分类、打分、验证。

## 目录

- [一、发布事件与背景](#一发布事件与背景)
- [二、Jev 是什么：System One Model](#二jev-是什么system-one-model)
- [三、三大原语与调用方式](#三三大原语与调用方式)
- [四、官方数字 vs 独立实测](#四官方数字-vs-独立实测)
- [五、社区一周生态](#五社区一周生态)
- [六、Playground 五场景实测](#六playground-五场景实测)
- [七、官方 Coding Agent Skill](#七官方-coding-agent-skill)
- [八、用还是不用：选型边界](#八用还是不用选型边界)
- [九、验证与勘误记录](#九验证与勘误记录)
- [参考资料](#参考资料)
- [相关笔记](#相关笔记)

---

## 一、发布事件与背景

| 事件 | 时间 | 验证 |
|------|------|------|
| TypeSafe AI 结束两年隐身，发布 Jev（early access） | 2026-09-15 | ✅ 官方博客原文「After two years in stealth」 |
| Hacker News 主贴 | 发布当天 | ✅ Algolia API 实测：视频时点 1679 分，现已涨至 **1915 分 / 501 评论** |
| Vercel AI Gateway 接入（模型 ID `typesafe-ai/jev`） | 09-16 | ✅ Vercel changelog，要求 AI SDK ≥ 7.0.105 |
| 官方直连 API | 排队发放中 | ✅ 官网仍为 Join Waitlist；第三方平台（Vercel）是即时体验通道 |

**公司背景**：创始人 Diogo Almeida（CEO）是 RLHF 与 InstructGPT 的共同发明人——「让语言模型学会听指令」的那套方法，研究直接通向 ChatGPT；此前在 Google Brain。团队来自 OpenAI、Google Brain、Meta/FAIR、Stripe、Docker 等。COO Sasha Sheng（ex-Meta/FAIR）、CTO Erik Gafni（连续创业者）。

有意思的反差：发明「让模型会聊天」的人，这次做的是一个**不聊天的模型**。

**官方首发 demo 是 DOOM**：Jev 操控游戏，每秒约 10 次决策（前进/左移/开火/躲避）。它不看画面像素，只读结构化游戏状态；不输出一个字，只在选项中选一个并给出确定度。官方博客披露成本约 **$7/小时**——团队本来担心 10 QPS 太贵，结果发现比预期便宜。

---

## 二、Jev 是什么：System One Model

### 快慢思考的架构转向

类名来自《思考，快与慢》（官方 FAQ 确认）：

```
System Two（传统 LLM）              System One（Jev）
┌─────────────────────┐           ┌─────────────────────┐
│ 逐 token 自回归生成   │           │ 并行评估全部问题      │
│ "想得越多，等得越久"  │           │ 一次请求全部返回      │
│ 输出 = 字符串         │           │ 输出 = 类型化值+概率   │
│ 程序还要 parse/校验   │           │ 代码直接分支/路由      │
└─────────────────────┘           └─────────────────────┘
        慢思考、要推理                     快思考、凭直觉
```

官方博客的核心论点：模型在 chat 上早已超人类，但自动化为什么没随之而来？因为 LLM 输出的是**字符串**——软件要用，得先解析、校验，还随时可能跑偏。而软件里大量决策（工单转给谁、评论是否垃圾、请求给便宜还是贵模型）需要的不是一段话，是**一个选项、一个分数、一个是/否**。

官方口径：「前沿智能的函数调用（frontier-intelligence function call）——非结构化状态进，类型化概率决策出。」

### LLM vs System One 对照（官方博客表格精简）

| 维度 | 现有 LLM | Jev |
|------|----------|-----|
| 训练方法 | RLHF / RLVR | RLCD（Reinforcement Learning for Calibrated Decisions，面向校准决策） |
| 输入 | 顺序消息为主 | 结构化程序状态（state 直接收 JSON） |
| 输出 | 字符串，需 parse + 校验 | 类型安全结构值 + 校准概率，数学上不可能类型出错 |
| 采样 | 串行（一个 token 接一个） | 并行（一次查询出全部答案） |
| 输入成本 | $0.20–$10 / MTok | **$0.042 / MTok，输出免费** |
| 端到端延迟 | 前沿模型 3–329 秒 | 官方标称 70–500ms |
| 置信度 | 倾向过度自信、不稳定 | 每个答案都带 confidence（校准：更高置信 ↔ 更高准确） |

**名字彩蛋**：Jev 得名于经济学家 William Stanley Jevons——蒸汽机效率提升后煤炭消耗反而增长的 Jevons 悖论。TypeSafe 押注：智能成本每降一个数量级，会用出多得多的用例。

---

## 三、三大原语与调用方式

Jev 只回答三种问题（TypeSafe primitives）：

| 原语 | 问什么 | 返回 | AI SDK 7 中的 type |
|------|--------|------|--------------------|
| **Choice** | 从候选列表选一个 | 选中项 + 各选项概率分布 + confidence | `choice` |
| **Score** | 按有序评分表打分（如情绪：平静→不耐烦→愤怒） | 各档概率 + 内插分数 + confidence | `score` |
| **Noul** | 这句话为真吗？ | 0–1 概率 | `boolean` |

三个要点：

1. **三种原语可混在同一次请求里**，对同一段 state 并行评估、互不干扰；加问题几乎不增加响应时间（无 context-rot）。
2. **state 可以直接是 JSON 对象**——不用把记录拼成一段话，模型自己读字段。对开发者非常友好。
3. **Noul ≈ 0.5 的语义**：「是/否各半」，体现不确定性，不是「程度中等」。

### 调用方式（Vercel AI Gateway 路线）

官方直连 API 在排队时，Vercel AI Gateway 是即时通道。两个硬约束（视频实测强调）：

- 只能通过 AI SDK 7 的 `experimental_evaluate` 调用，**OpenAI 兼容 endpoint 不支持**
- 需要 AI SDK ≥ 7.0.105

```ts
import { experimental_evaluate as evaluate } from 'ai';

const result = await evaluate({
  model: 'typesafe-ai/jev',
  state: 'The support agent issued a full refund to the customer.',
  questions: {
    refunded: {
      type: 'boolean',
      instructions: 'Was a refund issued?',
    },
  },
  providerOptions: {
    gateway: { zeroDataRetention: true },  // 可选：零数据保留
  },
});

console.log(result.answers.refunded);
// Choice/Score 的 confidence 在 result.providerMetadata.typesafe.confidence
```

**缓解隐私顾虑的官方选项**（视频没展开）：Vercel Gateway 上 Jev 支持 Zero Data Retention 与 No Training，按请求在 `providerOptions.gateway` 里开启——闭源托管 API 的隐私权衡因此有了部分解法。

---

## 四、官方数字 vs 独立实测

### 成本与延迟

| 指标 | 官方标称 | 独立实测/审计 | 判读 |
|------|----------|----------------|------|
| 输入价格 | $0.042 / MTok，输出免费 | — | ✅ 便宜是真的 |
| 端到端延迟 | 70–500ms | 海外实测约 **1.6–3.7s** | ⚠️ 官方数据从美国西海岸笔记本测得（官方自己承认）；实际看网络 |
| 速度倍数 | 40–200x（首页案例 193.6x faster / 444.6x cheaper） | 约 **5–25x** | ⚠️ 官方也说 193.6x/444.6x 属「真实收益的高端」 |
| 视频引用的评测点 | 准确率 67.8% vs GPT-5.6 Terra 67.9%，成本 1/76，快 25x | DataCamp 转述确认 67.8%/67.9% | ⚠️ 1/76 与 25x 来自官方交互式图表的具体点位，量级与官方 40–200x 区间一致 |
| 结构化输出错误率 | 0% | — | ✅ 受限解空间在数学上保证（schema matching 是构造性保证，非经验测量） |

### 两个必须想清楚的坑

**① 类型安全 ≠ 事实正确。** 官方文档自己写明：类型化输出保证的是接口不是真相——保证答案一定是选项之一，不保证选对。面对乱码输入，它仍会强制选一个（概率偏低、confidence 体现不确定性）。「不会幻觉」的说法实际只覆盖格式层面。

**② 校准目前无法独立验证。** 「80% 置信时真的有 80% 对」是整个产品最核心的卖点，但公开评测的方法论是：用 GPT-6 Astra + Claude Fable 5.1（均开高推理）的**共识均值当参考标签**——衡量的是「和最聪明模型的一致性」，不是 Ground Truth 校准。官方也承认这会偏向 OpenAI/Anthropic 模型、低估 DeepSeek 系。

> 视频结论：便宜和快是真的；倍数看你怎么理解；类型安全是真的，但只是格式安全——答案对不对，得在自己的场景里真实测评。

---

## 五、社区一周生态

发布一周，GitHub 已有多个 awesome-jev 列表（最大 630★），40+ 项目，分类最多的是**分类/路由**，其次是验证/安全防护、打分排序、Agent 决策、游戏机器人。

### 案例一：Jev 玩超级马里奥（fhshaik/typesafe-mario，284★）

```
NES 模拟器 → RAM/遥测解析 → 结构化 JSON → Jev 三原语同请求 → 手柄输入
                                          ├─ Choice: 选手柄动作（7 种）
                                          ├─ Noul:   现在跳还有用吗
                                          └─ Score:  眼前危险程度
                                          （每 8 帧决策一次）
```

README 确认：不看截图，输入是按语义分组的对象（player/trajectory/hazard/terrain…）；精确时序计算留在代码里（如把反应时延、敌人运动、跳跃窗口合成一个 `jump_must_start_this_decision` 布尔事实），Jev 只负责解释事实并拥有最终选择权——没有脚本化的补救动作。

### 案例二：browser-use/jev-ultrafast（8752★）

浏览器 Agent 每步要决策两件事：做什么操作、对哪个元素。传统做法串行两次 LLM 调用；jev-ultrafast 把两个决定做成**两个 Choice，一次请求返回**，只有 `TYPE_TEXT` 操作才调一个小 LLM（Mercury 2.5，关推理）生成文字。

| 指标 | 官方 README 实测 |
|------|------------------|
| Google Flights 苏黎世→伦敦全流程 | **7.073s**（含生成文字、加载等待，1× 速度录制） |
| 对比实验（6 次交替、3/3 通过） | 任务耗时中位数 9.450s → 7.092s（**-25%**） |
| 浏览器协议调用 | **1,092 → 101 次** |
| Wikipedia 任务 / 酒店筛选任务 | 2.798s / 1.896s |

架构精髓：target questions 是**投机性的**（speculative）——问 click_target、type_text_target、select_target 全部，但只有与选中操作匹配的那个会被执行；两个决定共享同一次网络往返。模型输出永远不变成 selector、坐标或可执行 JS。

---

## 六、Playground 五场景实测

up 主用 TypeScript + Next.js 搭了 Playground（服务端核心就一个 `evaluate` 调用：model + state + questions → answers + usage + 计时）。

### 场景 1：单布尔判定

state 换三种，同一问题「是否已完成退款」：

| state | 观察到的行为 |
|-------|--------------|
| 「已全额退款 42.5 元并关闭工单」 | 高置信 true |
| 「我们会评估您的情况，3 个工作日内答复」 | 概率明显变化 |
| 「今天杭州多云」 | 概率再变——语义变化即时反映在置信度上 |

### 场景 2：工单分诊，五问并发（核心场景）

一条工单（登录不上 + 上月多扣钱 + 要求退款 + 语气急），一次请求 5 个问题：类别（Choice 四选项）、故障程度（Score 四档）、有无复现步骤（boolean）、是否要求退款（boolean）、用户情绪（Score 三档）。5 个答案一起回来，延迟与单问时差不多（毫秒级差异）。

**关键用法——投机性 fan-out**：故障程度只在类别=故障时有意义，退款只在类别=收费时有意义。传统做法先问类别、等回来再问下一个（链式延迟）；Jev 的哲学是**全部一次问完，无额外代价**，回来后代码只取相关字段。

```
传统链式:  问类别 → 等 → 问严重度 → 等 → 问退款 → 等   (延迟 ×3)
Jev fan-out: [类别|严重度|复现|退款|情绪] → 一次返回 → 代码过滤
                        ↑ 无效问题照问，代价≈0
```

**压力测试**（矛盾工单「一切正常但什么都用不了，我不要退款把钱退给我」与纯乱码）：类别仍然必选一个（构造使然），但概率分布/considence 已如实体现不确定性 → 实践解法是代码里加规则：**confidence 低于阈值就转人工**（官方文档 confidence-gated routing 推荐姿势）。

### 场景 3：模型动态路由

state = 最近几条对话，Choice 两选项（便宜模型 vs 主力大模型，各配一句描述）。测试：「改错别字」→便宜模型；「写带 LRU+TTL 的缓存并写测试」→大模型；「2 亿行表的慢查询 SQL」→ 概率分布清晰可见。路由耗时相对后续生成可忽略。Vercel 自家 Agent 框架 eve 的 `auto()`（给几个模型和一句描述，Jev 挑选）已内置此能力。

### 场景 4：多字段内容审核

state 直接是 JSON 对象（作者/时间/评论正文/历史被举报次数），三问：是否垃圾广告（boolean）、冒犯程度（Score）、处置方案（Choice：放行/人工复核/删除）。引流广告、正常吐槽、人身攻击三类输入的输出分布符合预期。**state 原生 JSON、模型自己读字段**是相对「拼自然语言再喂 LLM」的实打实工程优势。

### 场景 5：LLM 输出把关（Guardrails）

state = 用户问题 + LLM 客服回复草稿，三问：回复质量打分（Score）、是否泄露内部政策（boolean）、语气是道歉/中性/推诿（Choice）。草稿里故意埋一句内部政策（「投诉两次以上可额外申请折扣券」），泄露指标立刻命中；删掉后回落。定位：**LLM 负责生成，Jev 负责判断这段能不能发、该不该重写**——快到可以放在每次生成之后。

---

## 七、官方 Coding Agent Skill

`typesafe-ai/skills` 仓库（674★）：**创建于 2026-08-24，比模型发布早三周**——先教 Agent 怎么用，再放模型，这个顺序本身就是信号。整仓库就一个不到 150 行的 SKILL.md。

安装：

```bash
# Claude Code（两条命令装成插件）
claude plugin marketplace add typesafe-ai/skills
claude plugin install typesafe@typesafe-ai

# 其他 Agent（如 Codex/Cursor）
npx skills add typesafe-ai/skills --skill typesafe-ai   # -g 全局
```

skill 的自我定位（文档原话）：**不是 API 参考手册**——在线文档才是事实来源，skill 只负责工作流/方法论。三件事：

1. **按需读文档**：Mintlify 页面 URL 加 `.md` 即得 Markdown；skill 列了一张「想理解编程模型读哪页 / 选原语读哪页 / 写代码读哪页」的表，Agent 按需读取，不把全站塞进上下文。
2. **教 Agent 拆需求**：从前端最终展示什么、选择什么、改变什么**倒推需要哪些判断**；规则、计算、精确查表、确定性执行留在代码里，只在需要语义理解的地方放一个问题。列了 6 种形态：路由并填参数、选择而不生成、检索后判断证据、判断变可复用数据、校验并升级、随状态变化定下一步。
3. **纠正 LLM 时代的惯性**：
   - 相互独立的问题一次性并发问掉（包括投机性问题）
   - confidence 只表示概率分布的集中程度，**不表示流程正确性**
   - Noul ≈ 0.5 是是非各半，不是程度中等
   - 问题与阈值常量放独立文件，人审只看这一处
   - FAQ 原话：只要选最优，直接取概率最高那个，**别到处设 confidence 阈值**

---

## 八、用还是不用：选型边界

```
你的任务需要什么？
│
├─ 长篇文字/代码生成 ──────────────→ 别用（它不写字）
├─ 需要解释「为什么」 ─────────────→ 别用（给不了理由）
├─ 决策空间能事先枚举？
│    ├─ 能（分类/路由/打分/验证）→ ✅ 甜点区
│    │    └─ 需要每秒几次的实时循环？→ ✅✅ 更甜（DOOM/Mario/浏览器）
│    └─ 不能，或有未覆盖长尾 ─────→ 慎用，必须补「其他/未知」兜底选项
│                                     + confidence 阈值回退
└─ 隐私敏感 ──→ 闭源托管 API，无权重；Gateway 可开 ZDR/No Training，自行权衡
```

| ✅ 适合 | ❌ 不适合 |
|--------|----------|
| 答案空间可预先枚举：分类、路由、打分、验证 | 需要生成内容（它没有输出 token） |
| 高频实时循环（每秒多次决策） | 需要推理过程或解释（Why） |
| 高吞吐管线里的分诊/审核分支 | 选项设计不周全的场景（正确答案不在选项里时，概率仍会摊到剩余选项上） |
| LLM 输出的 guardrails/校验 | 需要严格校准保证的关键决策（校准暂无独立验证） |

**官方也承认**：难的部分是从「写提示词」变成「设计决策模式」——正确答案得真的在选项闭包里。

**开源复刻生态已在萌芽**：社区有多个 Jev 风格开源权重项目——`shamazharikh/qwen-rlcd`（Qwen3.5-0.8B 上做 Choice/Score/Noul 校准决策）、`ikermoel/open-alternative-jev`（12★）、HF Space 上的 Qwen-2.5-1B-RLCD 等。闭源顾虑的长期解法。

### 落地行动建议

1. **小规模验证起步**：拿生产环境 50–100 条带真实标注的分类/审核数据，看 Jev 概率与人工标注对不对得上（校准曲线），再决定是否进系统——不要全量替换。
2. **Confidence gating**：对边缘/未覆盖输入按概率设阈值，低于阈值自动回退到 LLM 推理或转人工。
3. **前置完善选项闭包**：Choice 选项必须完整且互斥，永远留「其他/未知」缓冲项，避免模型在错误维度上强制猜测。上限参考：Jev 单 Choice 支持 255 个选项，更高基数官方建议两段式（先 Score 独立打分再显式 Choice）。

---

## 九、验证与勘误记录

| 视频声称 | 验证结果 | 来源 |
|----------|----------|------|
| 9/15 发布，当天 HN 1679 分、400+ 评论 | ✅（快照数字）：发布日属实；Algolia 当前 **1915 分 / 501 评论**，持续上涨中 | HN Algolia API |
| 次日 Vercel AI Gateway 接入；模型 ID `typesafe-ai/jev`；仅 `experimental_evaluate`，OpenAI 兼容 endpoint 不支持；需 AI SDK 7 | ✅ 全部确认（7.0.105+） | Vercel changelog |
| Diogo 为 RLHF 共同发明人、前 OpenAI | ✅（补充：也曾任职 Google Brain；co-invented RLHF & InstructGPT） | typesafe.ai/team |
| 三原语 Choice/Score/Noul；AI SDK 内 Noul=boolean | ✅ | docs.typesafe.ai |
| 输入 $0.042/MTok、输出免费、延迟 70–500ms | ✅ 官方标称；延迟为西海岸笔记本实测口径 | 官方博客 |
| 准确率 67.8% vs GPT-5.6 Terra 67.9% | ✅（媒体转述一致；evals 方法论 = GPT-6 Astra + Fable 5.1 共识标签） | DataCamp / evals.typesafe.ai |
| 成本 1/76、快 25 倍 | ⚠️ 未在一手页面直接复现（交互式图表点位）；量级与官方 40–200x 区间一致 | 官方博客 |
| 独立审计：5–25x、海外延迟 1.6–3.7s | ⚠️ 方向与多方独立评测一致（kingy.ai 等「193.6× 不是普遍结果」），具体审计帖未定位，属视频转述 | 视频口播 |
| 结构化错误率 0%、「不幻觉」 | ⚠️ 半真：类型错误 0% 是构造性保证；官方自认「保证接口不保证真相」 | 官方博客 nuance 节 |
| awesome-jev 40+ 项目、路由分类最多 | ✅（多个列表，最大 630★） | GitHub API |
| 马里奥：内存→JSON、三原语、每 8 帧决策 | ✅ 逐条对上（fhshaik/typesafe-mario，284★） | GitHub README |
| browser-use：双 Choice 一次返回、仅 TYPE_TEXT 调 LLM、Flights 7.1s、协议调用 1000+→100 | ✅（7.073s；1,092→101；中位数耗时 -25%） | jev-ultrafast README |
| Vercel eve 已把 Jev 路由做成默认能力 | ⚠️ 措辞略强：eve `auto()` 用 Jev 选模型属实（官方博文），是否「默认」未逐字核实 | Vercel 博客 |
| skills 仓库 8/25 建、早于发布三周、单文件 <150 行 | ✅（created_at 2026-08-24T23:58Z——UTC 尾差 2 分钟到 8/25，可忽略；现 674★） | GitHub API |
| 闭源、托管 API、无权重；社区在用 Qwen 微调校准概率小模型 | ✅（qwen-rlcd、open-alternative-jev、Qwen-2.5-1B-RLCD 等） | GitHub / HF |

**Insights 与字幕一致性**：本次用户提供的 Content Insights 与字幕逐条核对无冲突、无 ASR 错名（Jev/TypeSafe/browser-use 等专有名词全部正确）。

---

## 参考资料

- [视频：Jev 实测：这款爆火的极速决策模型的基本玩法（01Coder）](https://www.youtube.com/watch?v=tYvu6IpSfiM)
- [TypeSafe 发布博客：Introducing System One Models & Jev](https://typesafe.ai/blog/introducing-system-one-models-and-jev)
- [TypeSafe 官方文档](https://docs.typesafe.ai)
- [TypeSafe workflow evals 站点](https://evals.typesafe.ai)
- [Vercel changelog：Jev on AI Gateway](https://vercel.com/changelog/typesafe-ai-jev-now-available-on-ai-gateway)
- [typesafe-ai/skills（官方 Agent Skill）](https://github.com/typesafe-ai/skills)
- [browser-use/jev-ultrafast](https://github.com/browser-use/jev-ultrafast)
- [fhshaik/typesafe-mario](https://github.com/fhshaik/typesafe-mario)
- [HN 讨论帖](https://news.ycombinator.com/item?id=49717558)（1915 分 / 501 评论，2026-09-20 查询）

## 相关笔记

- [[本周热门开发工具项目汇总 - AI Agent 与基础设施]]
- [[Pi Agent 必装 8 个插件——Agentic Coding 完整插件矩阵]]

---

*笔记生成时间：2026-09-20；基于 01Coder 视频字幕 + 官方一手来源交叉验证*
