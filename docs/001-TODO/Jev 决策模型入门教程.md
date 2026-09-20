---
title: Jev 决策模型入门教程
aliases:
  - Jev 入门
  - Jev 三大输出模式
  - AI随风 Jev 教程
tags:
  - decision-model
  - typesafe-ai
  - model-routing
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=GJJq4LXHtW4"
  - "https://typesafe.ai/blog/introducing-system-one-models-and-jev"
  - "https://docs.typesafe.ai/concepts/system-one"
  - "https://flaviocopes.com/jev"
  - "https://www.langchain.com/blog/building-a-harness-with-jev"
author: AI随风（频道）；产品方 TypeSafe AI
created: 2026-09-20
updated: 2026-09-20
description: AI随风「保姆级介绍和使用教程」视频的入门整理：Jev 只做判断不生成文本，三大输出模式（Noul/Choice/Score）+ 模型路由与内容审查实战场景，官方一手来源交叉验证。
level: beginner
stars: 4
---

# Jev 决策模型入门教程

> AI随风 2026-09-18 视频整理。Jev 是 TypeSafe AI 的「System One Model」：不能聊天、不能写代码，只负责做选择与判断。本笔记为入门视角，实测数据与生态细节见 [[Jev 极速决策模型实测笔记]]。

> [!info] 视频信息
> - **频道**: AI随风
> - **发布**: 2026-09-18 · **时长**: 6:45 · **观看**: 8030
> - **产品**: TypeSafe AI「Jev」（2026-09-15 early access）
> - **字幕**: 频道未开放（Transcripts disabled），基于 YouTube 内容资讯 + 官方文档交叉验证

## 目录

- [一、核心定位：只做决策的「非通用」模型](#一核心定位只做决策的非通用模型)
- [二、三大输出模式](#二三大输出模式)
- [三、速度与成本](#三速度与成本)
- [四、实战场景](#四实战场景)
- [五、什么情况该用 Jev](#五什么情况该用-jev)
- [六、验证与勘误记录](#六验证与勘误记录)
- [参考资料](#参考资料)
- [相关笔记](#相关笔记)

---

## 一、核心定位：只做决策的「非通用」模型

Jev 刻意砍掉了通用 LLM 的三大能力——**不对话、不写代码、不做多模态生成**，唯一职责是「辅助做选择与判断」。换取的是数量级的速度与成本优势。

```
          通用 LLM（ChatGPT/Claude）            Jev（System One Model）
        ┌────────────────────────┐          ┌────────────────────────┐
 输入   │ 自然语言 prompt         │          │ state（背景/状态）      │
        │ （ Sequential 消息流）  │          │ question（具体问题）    │
        └───────────┬────────────┘          └───────────┬────────────┘
                    │ 逐 token 生成                      │ 并行采样
                    ▼                                    ▼
 输出   │ 自由文本（需解析+校验）  │          │ 类型化结果 + 校准概率    │
        │ 可能幻觉/跑偏           │          │ 不可能幻觉、零类型错误   │
        └────────────────────────┘          └────────────────────────┘
          3 ~ 329 秒 / $0.20-10 每M输入        ~100 毫秒 / $0.042 每M输入
```

**工作流中的角色**：不是替代品，而是 Agent 架构里的「中间路由」或「逻辑审查节点」——放在通用大模型前面做分流、过滤、裁决。

调用只需两项输入（官方 API 即 `state` + `questions`）：

| 输入 | 含义 | 例子 |
|------|------|------|
| **背景资讯（State）** | 决策依据的上下文 | 学生成绩单 + 家长性格描述 |
| **具体问题（Question）** | 要判断什么 | 「父母会生气吗？」 |

> 💡 一个请求可以同时问多个问题，全部**并行**回答——加问题几乎不增加响应时间，只多付问题的 token 钱。

---

## 二、三大输出模式

Jev 的输出是三种「原语（primitive）」，正好覆盖决策的三种形态：

| 模式 | 官方原语名 | 回答的问题形态 | 返回值 | 视频例子 |
|------|-----------|---------------|--------|---------|
| 是非判断 | **Noul** | 某陈述为真的概率？ | 概率值 0~1 | 「父母会生气吗？」→ 生气 10% / 不生气 90% |
| 多选一评估 | **Choice** | 给定选项中选哪个？ | 各选项概率 + 最高项 | A表扬/B鼓励/C沟通/D批评 → C (0.55) |
| 程度评分 | **Score** | 在梯度标尺上打几分？ | 连续分数 + 分布 | 「有多不高兴？」→ 略有失望 65% |

### 2.1 Noul：是非判断

输入情境与问题，返回事件成立与否的概率。适合做「门卫」——一个阈值就完成分流。

```json
// 请求
{
  "model": "jev-latest",
  "state": "学生数学 62 分（及格线 60），家长平时要求严格",
  "questions": {
    "parents_angry": {
      "type": "noul",
      "instructions": "The parents will be angry about this grade"
    }
  }
}
// 响应（示意）
{ "parents_angry": { "type": "noul", "noul": 0.10 } }
```

### 2.2 Choice：多选一评估（实务中最常用）

给每个选项附一段 `criteria` 描述，模型对全部选项打分。**选项空间封闭**——不可能 invent 出第五个选项，这是「不能幻觉」的结构性来源。

```json
{
  "action": {
    "type": "choice",
    "instructions": "How should the teacher respond to the parents?",
    "criteria": {
      "A_praise": "公开表扬进步",
      "B_encourage": "私下鼓励继续保持",
      "C_communicate": "约家长面谈沟通",
      "D_scold": "严厉批评退步"
    }
  }
}
// 响应（示意）
{
  "action": {
    "choice": "C_communicate",
    "probabilities": { "A_praise": 0.10, "B_encourage": 0.30,
                       "C_communicate": 0.55, "D_scold": 0.05 },
    "confidence": 0.82
  }
}
```

### 2.3 Score：程度评分

对情绪强度、偏好梯度等**连续量**输出渐进分布，工作流可按分数门槛分流：

```
0 ────────── 1 ────────── 2
基本满意    略有失望      非常生气
             ▲ 65%
             └─ score: 0.9 → 概率质量集中在「略有失望」档
```

> 注意 Noul/Choice/Score 每个答案都带 **confidence（置信度）**——校准过的：置信度越高，历史准确率越高。这是「设阈值自动执行、低于阈值转人工」的基础。

---

## 三、速度与成本

| 指标 | Jev 官方数字 | 通用 LLM 对照 | 验证 |
|------|-------------|--------------|------|
| 端到端延迟 | 70~500ms，多数 **~100ms** | 前沿模型 3~329 秒 | ✅ 官方博客 + flaviocopes 实测口径一致 |
| 输入价格 | **$0.042 / 百万 token** | $0.20~$10 / MTok | ✅ 官方 + OpenRouter + Vercel Gateway 同价 |
| 输出价格 | **免费**（too cheap to meter） | 约为输入价 5 倍 | ✅ 官方 |
| 加速倍数 | 40x~200x（自称高位区间） | — | ⚠️ 官方口径，首页 193.6x/444.6x 出自自建 workflow evals |

**成本直觉**（flaviocopes 算的账）：一张工单 + 问题约 300 token ≈ **$0.0000126/次**，10 万张同规模工单 ≈ $1.26。「大砲打蚊子」的反面——蚊子拍就按蚊子的价格卖。

```
              ┌──────────────┐
   所有请求 ──▶│ Jev 预检/路由 │──简单/明确──▶ 轻量模型（便宜）
              │  (~100ms)     │            │
              └──────┬───────┘            │
                     │复杂/模糊/低置信度    │
                     ▼                    ▼
              重量级 LLM（Claude/GPT）   人工复核
```

视频引用的外网案例：引入 Jev 做前置过滤后，Claude 的 token 消耗从 **100 万降至 186K**（约 5.4x）。❓ 该具体数字未在公开来源中检索到原始出处，官方与 LangChain 只给出「40-400x 更便宜」量级——量级吻合，个案存疑。

---

## 四、实战场景

### 4.1 模型路由（Model Routing）

多 Agent 系统中先由 Jev 判断任务难度，简单任务给轻量模型，复杂任务才上顶级模型。LangChain 已有官方中间件实证（`langchain-typesafe`）：

```python
from langchain.agents import create_agent
from langchain_typesafe.experimental.middleware import (
    ModelChoice, ModelRouterMiddleware,
)

router = ModelRouterMiddleware(
    choices={
        "fast": ModelChoice(model="openai:luna",
                            criteria="Direct lookups, extraction, localized changes."),
        "powerful": ModelChoice(model="openai:sol",
                                criteria="Architecture and high-stakes decisions."),
    },
    instructions="Choose the least costly model that can complete the task.",
)
agent = create_agent("openai:gpt-5.6-luna", middleware=[router])
```

### 4.2 规则与行为控制：俄罗斯方块实测

视频实测：模型接收盘面坐标（结构化 state，非画面像素），判定下移/旋转方向。与官方首发 demo（DOOM，每秒约 10 次决策、~$7/小时）同一模式——**实时控制类任务**正是 100ms 延迟的甜点区。

### 4.3 内容审查与安全过滤

| 场景 | state | 判断 | 对应官方用例 |
|------|-------|------|-------------|
| 网页广告屏蔽 | 浏览器 DOM 结构 | 广告元素概率 → 遮蔽 | classify + extract |
| 诈骗邮件判定 | 邮件内容 + 情境 | 诈骗概率 → 隔离/放行 | guardrail |
| 社群评论打标 | B站/YouTube 留言 | 情绪标签 + 程度 | score |

官方博客把这类用途归纳为「**Verify everything**」——给 LLM 的 prompt、推理轨迹、输出做打分、裁决、护栏、越狱检测。

### 4.4 企业级业务分支

客服意图分流、保险理赔初审、法规合规检查——共同特征是**结构化选择**：选项封闭、可按概率 + 置信度设门槛，未达阈值转人工。

---

## 五、什么情况该用 Jev

```
你的决策点是什么性质？
│
├─ 需要生成文本/代码/解释 ──────────▶ 不适合，用通用 LLM
│
├─ 纯规则可判定（order.total > 100）─▶ 普通 if 就够，别上 AI
│
└─ 模糊判断但答案空间封闭
   （分类/路由/打分/是非）
   │
   ├─ 有大量标注数据 + 固定标签 ─────▶ 传统分类器也可（更便宜）
   │                                   但零样本/规则常变时 Jev 更灵活
   │
   └─ 规则写不出 / 常变 / 长尾 ──────▶ ✅ Jev 甜点区
      实操：先挑一个现有 if/regex 分支
      用 Noul 或 Choice 并行跑一周，
      对比日志后再接管线上。
```

**上手路径**（flaviocopes 建议，值得照抄）：

1. Playground 里贴**自己的真实数据**（真实工单/日志/表单），别用官方示例
2. 挑一个代码里写死的脆弱分支（route / approve / skip），换成一个 Noul 或 Choice
3. **影子模式跑一周**：Jev 结果只记日志不动行为，与现有逻辑对比
4. 置信度门槛内才自动执行，低于阈值走原路径或人工

> ⚠️ 两个已知边界：延迟数字是美西实测，其他地区要自测网络延迟；官方加速/省钱倍数出自自建 evals，属于高位区间（公司自己也这么承认）。

---

## 六、验证与勘误记录

| 视频声称 | 验证结果 | 来源 |
|---------|---------|------|
| Jev 定位：不能聊天/写代码，只做决策 | ✅ | 官方博客 + docs「System One models do not write replies, produce code」 |
| 输入仅需背景资讯 + 问题 | ✅ | 官方 API 即 `state` + `questions` |
| 三种输出：是非/多选一/程度评分 | ✅ | docs 三原语 Noul / Choice / Score |
| 响应 ~100ms | ✅ | 官方「70ms-500ms，多数 ~100ms」 |
| $0.042/M 输入、输出免费 | ✅ | 官方博客 + OpenRouter + Vercel Gateway 三方一致 |
| 模型路由场景 | ✅ | LangChain `ModelRouterMiddleware` 官方集成 |
| 俄罗斯方块/广告/诈骗邮件场景 | ✅ 方向一致 | 同为官方 demo 模式（DOOM/护栏/verify）；个案为视频自测 |
| 「是非判断」小节标题作「Yes / No / Noul」 | ⚠️ 易误解 | Noul 是**原语名**（返回真实概率，非三选项）；返回值即概率 0~1 |
| Claude token 100万 → 186K 案例 | ❓ 未找到出处 | 公开来源仅有「40-400x cheaper」量级，个案数字无法溯源 |
| 「Jev」名称含义 | ✅ 补充 | 官方 FAQ：纪念经济学家 William Stanley Jevons——智能成本每降一个数量级，需求反而爆炸（Jevons 悖论） |

> [!note] 字幕说明
> 本视频未开放字幕（Tier 0 检测为 Transcripts are disabled），笔记骨架来自 YouTube 内容资讯（Content Insights），全部关键声称已对照官方一手来源交叉验证。

---

## 参考资料

- [视频：超火大模型Jev，保姆级介绍和使用教程（AI随风）](https://www.youtube.com/watch?v=GJJq4LXHtW4)
- [Introducing System One Models & Jev — TypeSafe AI 官方博客](https://typesafe.ai/blog/introducing-system-one-models-and-jev)
- [System One — TypeSafe AI Docs](https://docs.typesafe.ai/concepts/system-one)
- [A deep dive into Jev — Flavio Copes](https://flaviocopes.com/jev)
- [Building a Harness with Jev — LangChain Blog](https://www.langchain.com/blog/building-a-harness-with-jev)

## 相关笔记

- [[Jev 极速决策模型实测笔记]] — 01Coder 实测向：Playground 五场景、官方数字 vs 独立实测、生态一周动态
- [[Claude Fable 5 & Mythos 5 - Anthropic 最强模型公开发布]] — 重量级 LLM 对照：Jev 路由的另一端
