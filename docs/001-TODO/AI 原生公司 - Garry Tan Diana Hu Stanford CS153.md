---
title: AI 原生公司 — Garry Tan & Diana Hu Stanford CS153 演讲导读
aliases:
  - AI Native Company
  - Stanford CS153 Garry Tan
tags:
  - ai/agent
  - startup/strategy
  - status/active
  - type/notes
  - source/youtube
source:
  - "https://www.youtube.com/watch?v=ZvDNlw9FrTY"
  - "https://www.youtube.com/watch?v=Lri2LNYtERM"
author: Garry Tan (YC CEO), Diana Hu (YC General Partner)
created: 2026-05-23
updated: 2026-05-23
description: YC 主席 Garry Tan 与 GP Diana Hu 在 Stanford CS153 Frontier Systems 演讲中，提出以 LLM 为基底构建全自动化 AI 原生公司的完整蓝图
level: intermediate
stars: 4
---

# AI 原生公司 — Garry Tan & Diana Hu Stanford CS153 演讲导读

> YC CEO Garry Tan 与 General Partner Diana Hu 回到母校 Stanford CS153 Frontier Systems，不谈工具测评，不讲 prompt 工程，而是端出一套以 LLM 为基底的「全自动化公司」构建蓝图——把开源 agent 框架的技术术语直接映射为企业人资架构，重新定义一人公司的组织形态。

---

## 目录

- [核心前提：Markdown 即可执行代码](#核心前提markdown-即可执行代码)
- [AI 公司词典：技术术语 × 企业人资](#ai-公司词典技术术语--企业人资)
- [OpenClaw：Agent 工作平台](#openclawagent-工作平台)
- [控制系统的视角：开放迴路 vs 封闭迴路](#控制系统的视角开放迴路-vs-封闭迴路)
- [AI 原生公司的三种核心角色](#ai-原生公司的三种核心角色)
- [生产力数据与冷靜提示](#生产力数据与冷靜提示)
- [人类不可替代的东西：Taste + Evals](#人类不可替代的东西taste--evals)
- [构建護城河的四步心法](#构建護城河的四步心法)
- [Wedge 策略：寻找切入点](#wedge-策略寻找切入点)
- [Posterous 的时代对比](#posterous-的时代对比)

---

## 核心前提：Markdown 即可执行代码

Garry Tan 一开场就抛出一个颠覆性观点：

> **Markdown 不再只是文档格式，它现在是可以执行的程式码（executable code）。**

以前写 Markdown 是做笔记、写说明书；但在 AI 原生时代，Markdown 文件直接驱动整家公司运转——它是公司的「数位地基」。

```
传统认知                          AI 原生时代
─────────────────────────────────────────────────
Markdown = 靜態文件               Markdown = 可执行的程式碼
README.md = 說明書                README.md = 員工守則 / SOP
prompt = 一次性指令               prompt = 可重複執行的流程定義
```

这个前提贯穿全场，是理解后续所有概念的锚点。

---

## AI 公司词典：技术术语 × 企业人资

Garry Tan 的核心贡献之一，是把开源 agent 系统里的技術名词「翻译」成人人能懂的企业人资架构：

| 技术术语 | 企业人资类比 | 说明 |
|----------|-------------|------|
| **Skill** | 员工能力 | 教 AI 怎麼做事的超详细指南 |
| **Resolver** | 组织图（Org Chart） | 决定哪個任務发包給哪個 skill |
| **Skillify** | 制定 SOP | 把臨時招式升級為標準作業流程，AI 從臨時工變正職 |
| **Check-resolvable** | 内部稽核 / 合规 | 確保組織圖在對的情境下喚醒對的員工 |
| **Trigger-eval** | 績效考核（KPI） | 盯著 AI 員工有沒有好好做事 |

### Skillify 的意义

```
一次性 prompt          Skillify 之后
─────────────────────────────────────
臨時工模式：           正職員工模式：
每次手動輸入指令       定義好流程 → 反覆執行
結果不一致             結果穩定可預期
需要人盯著             自動運轉
```

Garry 坦承 skillify 的概念是他凌晨 3 点在 OpenClaw 上「东摸西摸」不小心搞出来的——侧面反映了当前 AI 技术迭代的速度。

---

## OpenClaw：Agent 工作平台

OpenClaw 是 Garry Tan 重点介绍的环境，可以理解为**专为 agent 打造的工作平台**：

- 真正做到把 Markdown 技能变成可执行的单元
- 开源项目，由 Peter Steinberger（@steipete）创建
- GitHub 星标 247K+，GitHub 历史上增长最快的开源项目之一

> ⚠️ **产品归属澄清**：OpenClaw 是独立开发者 Peter Steinberger 的开源项目，非 Google/YC 开发。Garry Tan 作为 YC CEO 和重度用户大力推荐。

---

## 控制系统的视角：开放迴路 vs 封闭迴路

Diana Hu 从**控制系统（Control Systems）**角度切入，一针见血地指出传统公司的结构性问题：

```
傳統公司（開放迴路）              AI 原生公司（封閉迴路）
────────────────────────────────────────────────────
決策層層傳遞                    Agent 直接讀取原始數據
資訊傳來傳去損失大半             完整上下文 → 準確判斷
發現問題 → 開會 → 討論 → 決策   發現問題 → 自動修復
中階主管是資訊中繼站             中階主管大幅壓縮
```

**封闭迴路的关键**：给 agent 「上帝视角」——让它们可以读取 GitHub、Discord、甚至所有会议录音。有了完整上下文，AI 能主动发现问题、建议下一步、甚至自己修 bug。

```
         ┌──────────────────────────────┐
         │                              │
    ┌────▼────┐                    ┌────▼────┐
    │  觀察    │                    │  執行    │
    │ Observe │                    │  Act    │
    └────┬────┘                    └────▲────┘
         │                              │
         └──────────────────────────────┘
                   反饋迴圈
               （封閉迴路 = 自動修正）
```

---

## AI 原生公司的三种核心角色

Diana 预言，未来的 AI 原生公司只需要三种人：

| 角色 | 缩写 | 职责 | 类比 |
|------|------|------|------|
| **个人贡献者** | IC | 亲自动手产出成品 | 做事的人 |
| **直接负责人** | DRI | 跨团队指挥排程 | 管事的人 |
| **AI 创办人** | — | 把最新 AI 工具搬进公司 | 领路的人 |

> ❌ 被大幅压缩的：传统中阶主管（信息中继站角色）

**关键洞察**：当 agent 已经接管了传话和协调的工作，传统以「信息传递」为核心价值的管理层就成了冗余。

---

## 生产力数据与冷静提示

Diana 透露 YC 内部工程团队导入这套模式后的效果：

- 开发周期**砍半**
- 产出**飆升 10 倍**

> ⚠️ **重要警示**：这是讲者内部感受到的趋势信号，**不是经过外部审计的财报绝对事实**。当作方向标即可，不要当作逼死自己的 KPI。

---

## 人类不可替代的东西：Taste + Evals

当写程式的成本无限逼近于零时，人类真正的護城河是什么？

**Diana 的答案**：

1. **Taste（品味）**——机器能写几万行代码，但不懂什么叫「好用」、什么叫「符合市场需求」
2. **Evals（评估系统）**——针对特定领域建立的专属评分标准

```
代碼成本 → 趨近於零          人類價值 → 反而上升
─────────────────────────────────────────────
機器擅長：                     人類擅長：
  ✅ 寫大量代碼                 ✅ 判斷什麼值得做
  ✅ 執行重複任務               ✅ 定義「好」的標準
  ✅ 24/7 運轉                 ✅ 品味 / 審美判斷
```

---

## 构建護城河的四步心法

Diana 提出了一套实用的 eval 构建方法论：

```
Step 1                    Step 2
忽略通用跑分     ────→    提取真實軌跡
（MMLU、GPQA 等）        （自傢產品的成功/失敗案例）

Step 4                    Step 3
丟回系統         ←────    人工轉換為評分標準
（Agent 自動除錯）        （人類定義好/壞的邊界）
```

| 步骤 | 行动 | 要点 |
|------|------|------|
| 1 | 别管通用 benchmark | MMLU 等测不出你产品的真实表现 |
| 2 | 抓取自家产品运行轨迹 | 收集成功案例和失败案例 |
| 3 | 人工转为评分标准 | 人类定义什么是「好」 |
| 4 | 丟回系统 | 让 agent 学会自我修正 |

**Garry 的额外大招**：让不同的顶尖 AI 模型互相改考卷（模型互评），品质可飙升 10 倍以上。

---

## Wedge 策略：寻找切入点

Diana 最后给出了 YC 风格的实战策略——**Wedge（切入点）**：

| 步骤 | 行动 |
|------|------|
| 1 | 找出业界最痛苦、最烦人的工作流程 |
| 2 | 像卧底一样潜入行业，摸透细节 |
| 3 | 把支离碎片的琐事全部 agent 化 |

**成功案例特征**：很多 YC 校友公司创办人一开始根本不懂那个行业，他们就是亲自跳进去找痛点，然后用 AI 全部剷平。

### 未被 AI 渗透的蓝海

```
行業 AI 滲透率
──────────────────────────────
資訊科學 / 軟體工程    ≈ 50%
金融業                 ≈ 低
傳統後勤辦公室         ≈ 極低
傳統客服部門           ≈ 極低
──────────────────────────────
滿地黃金 = 靠 LINE 群組 + Excel 報表
            土法煉鋼撐著的行業
```

---

## Posterous 的时代对比

Garry 用自己的创业经历做了一个震撼对比：

| 维度 | 2008 年（Posterous） | 现在（AI 时代） |
|------|---------------------|----------------|
| 时间 | 3 年 | **5 天** |
| 人力 | 10 人 | 1 人 + AI |
| 成本 | 2000 万美金（收购价） | 每月 200 美金（AI 工具） |
| 结果 | 卖给 Twitter | 同等产品 |

**但 Garry 立即点破了盲点**：他是极度熟练的老手，产品架构、技术选择、商业逻辑刻在骨子里。对新手来说，下载 AI 工具和产出巨大商业成果之间，依然隔着巨大的鸿沟。

---

## 总结：从指令到守则的思维转换

演讲的收尾留下一个核心问题：

> 你现在打下的那些 prompt，到底是在写一条**一次性的指令**，还是在为你的公司编写一套**可以日夜自动运转的员工守则**？

**思维转换对照**：

```
❌ 旧思维                          ✅ 新思维
─────────────────────────────────────────────
「讓 AI 幫我做一件事」              「讓 AI 成為我的員工」
寫一次性 prompt                    定義可重複的 skill
每次手動操作                        自動化運轉
AI = 工具                          AI = 團隊成員
```

---

## 参考资料

- [Stanford CS153 Frontier Systems 课程主页](https://cs153.stanford.edu/)
- [原始演讲视频 - How One Founder Becomes a 1000x Engineer](https://www.youtube.com/watch?v=Lri2LNYtERM)
- [OpenClaw GitHub 仓库](https://github.com/openclaw/openclaw)
- [Garry Tan on Wedge Strategy (X/Twitter)](https://x.com/garrytan/status/2041267467562193249)
- [YC Library - Tokenmaxxing](https://www.ycombinator.com/library/Pa-tokenmaxxing-how-top-builders-use-ai-to-do-the-work-of-400-engineers)
