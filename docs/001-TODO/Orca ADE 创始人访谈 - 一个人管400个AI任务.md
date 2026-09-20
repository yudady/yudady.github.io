---
title: Orca ADE 创始人访谈 - 一个人管400个AI任务
aliases:
  - 津晶 Orca 访谈
  - 课代表立正 Orca 创始人对谈
  - Orca 七万星访谈
tags:
  - ai-agent
  - ai-tools
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=LZlbXApG60E"
  - "https://github.com/stablyai/orca"
  - "https://www.onorca.dev/"
  - "https://www.superlinear.academy/c/recording/orca"
author: 课代表立正（频道）／津晶（Orca 联合创始人，受访）
created: 2026-09-20
updated: 2026-09-20
description: |
  Orca（stablyai/orca，GitHub 7 万+ 星）联合创始人津晶的访谈笔记：ADE 与 Harness 的边界、一个人管 400+ Agent 任务的工程实现（Agent Status／Agent 管 Agent／PTY 快照）、订阅制成本策略、4 人团队日更背后的增长方法论，以及 AI 时代的三类创业机会。与实测篇、入门篇互补，本篇偏产品哲学与创业方法论。
level: intermediate
stars: 4
---

# Orca ADE 创始人访谈 - 一个人管400个AI任务

> 课代表立正频道对 Orca 联合创始人**津晶**的访谈（2026-09-18 发布，66 分钟）。核心命题：开 5 个 AI 窗口就手忙脚乱的人，和一个人同时推进 400+ 任务的人，差距不在模型能力，而在**调度架构、注意力分配与验收机制**。录制时 Orca 约 4.9 万星，视频发布时已 7 万星，访问者本人也在采访后投资了 Orca。

> [!info] 视频信息
> - **频道**：课代表立正（超线性学院创始人）
> - **受访者**：津晶（Orca 联合创始人，前 Google，连续创业者，YC 校友）
> - **时长**：1:06:53 ／ **发布**：2026-09-18
> - **仓库**：[stablyai/orca](https://github.com/stablyai/orca)（MIT，TypeScript，2026-03-17 创建，2026-09-20 快照 72,712 星）
> - **官网**：[onorca.dev](https://www.onorca.dev/)（YC backed）

---

## 目录

- [一、Orca 是什么：ADE vs Harness](#一orca-是什么ade-vs-harness)
- [二、一个人管 400+ 任务的工程实现](#二一个人管-400-任务的工程实现)
- [三、成本策略：订阅混合编排](#三成本策略订阅混合编排)
- [四、为什么不怕 Anthropic／OpenAI「顺手做掉」](#四为什么不怕-anthropicopenai顺手做掉)
- [五、产品与增长方法论](#五产品与增长方法论)
- [六、AI 时代的宏观思考与创业机会](#六ai-时代的宏观思考与创业机会)
- [七、验证与勘误记录](#七验证与勘误记录)
- [八、给开发者的行动清单](#八给开发者的行动清单)
- [参考资料](#参考资料)
- [相关笔记](#相关笔记)

---

## 一、Orca 是什么：ADE vs Harness

访谈第一个关键澄清是概念分层。津晶的定义（[03:49](https://www.youtube.com/watch?v=LZlbXApG60E&t=229)）：

- **Harness**＝单 Agent 执行回路：读输入 → 调用工具（bash、Web 搜索）→ 回传输出。Claude Code、Codex、Devin、OpenCode、Pi 都是 Harness。
- **ADE（Agent Development Environment）**＝Harness 之上的一层：不管 Loop 本身，专注「人与多 Agent 协同的工作流管理」——状态追踪、快速反馈、结果审查。

```text
        ┌─────────────────────────────────────────────┐
        │              ADE（Orca 所在层）               │
        │   工作流管理 · Agent Status · 调度 · 审查      │
        │   GitHub/Linear 集成 · Worktree 生命周期      │
        └──────┬──────────┬──────────┬─────────┬─────┘
               │          │          │         │
        ┌──────▼───┐ ┌────▼─────┐ ┌──▼─────┐ ┌─▼──────┐
        │Claude Code│ │  Codex   │ │OpenCode│ │  Pi …  │
        │ (Harness) │ │(Harness) │ │(Harn.) │ │(Harn.) │
        └──────┬────┘ └────┬─────┘ └──┬─────┘ └─┬──────┘
               │           │          │         │
        ┌──────▼───────────▼──────────▼─────────▼──────┐
        │     模型层：Claude ／ GPT ／ DeepSeek ／ GLM    │
        └──────────────────────────────────────────────┘
```

Orca 的三个差异化卖点（[01:16](https://www.youtube.com/watch?v=LZlbXApG60E&t=76)）：

| 差异点 | 说明 | 对应痛点 |
|---|---|---|
| **集成性** | 20 条 Linear tickets → 20 个 parallel threads，进度自动回写 Linear；code review、测试全在应用内 | 不用在五六个 App 间切换 |
| **高并行** | 用户同时跑几百个 agent：10-20 个 feature × 每个 4-5 个 agent 协作，Claude Code 与 Codex 可互调 | 终端开 5 个 session 就追踪不过来 |
| **无自研 Harness** | 不绑死执行层，1 分钟上手，直接在自带 Orca Terminal 里跑现有工具 | 不改变用户已有使用习惯 |

一个值得注意的措辞细节：口播中津晶把 ADE 解释成「你可以把它想象成 Agent IDE」，而仓库 README 与官网现在统一写 **agent development environment**（官网对比表则用 ADE 缩写）。官网对比页自称支持 **27 种 agent CLI**——Claude Code、Codex、Grok、Cursor、Copilot、OpenCode、Amp、Antigravity、Pi、Hermes Agent、Goose、Kimi、Qwen Code 等全在列，甚至包括「+ any CLI agent」的兜底承诺。

> [!tip] Orca 名字来源
> 或问（Orchestration for Agent）——agent 指挥 agent、任务在 agent 间接力（handoff），是产品第一天就有的概念。津晶补充 Orca 本义虎鲸（[54:09](https://www.youtube.com/watch?v=LZlbXApG60E&t=3249)）：虎鲸与人类的智能不同维度、各有所长——这也是她对 AI 与人关系的隐喻。

---

## 二、一个人管 400+ 任务的工程实现

这是全片信息密度最高的部分。津晶本人同时推进 400 多个 worktree（[08:26](https://www.youtube.com/watch?v=LZlbXApG60E&t=506)），其中约 200 个已按固定流程自动运行（[25:51](https://www.youtube.com/watch?v=LZlbXApG60E&t=1551)）。她给出三层答案：

### 支柱 1：结构化状态追踪 + 深度检索

- **Agent Status API**：跨所有 worktree、跨所有 Harness（Claude Code / Codex / OpenCode）统一标记每个 session 的状态——`working`／`blocked, you need to answer immediately`／`done, you need to review`。状态作为 API 暴露，还支持 extension，用户可自建管理视图。
- **深度索引搜索**（[22:04](https://www.youtube.com/watch?v=LZlbXApG60E&t=1324)）：检索维度包括 session 标题、worktree 名称、关联 Linear ticket 关键词。「三天前我在做的东西现在进展如何」一搜即达终端现场。她的评价很直白：Claude Code 和 Codex 自带的搜索「我搜不出我想搜的东西，我不信任他们的搜索」。

### 支柱 2：Agent 管 Agent（阶层式 SOP）

用户社区里沉淀出的典型 recipe（[23:47](https://www.youtube.com/watch?v=LZlbXApG60E&t=1427)）：

```text
 Linear ticket
      │
      ▼
 Master Agent（读需求单，收集 requirements）
      │
      ▼
 撰写 design doc ──► 交叉 review 数轮 ──► implement
      │                                     │
      │                    Orca 自带 browser use + mobile emulator
      ▼                                     ▼
 QA testing  ◄────────────────────────  实际产品上跑
      │
      ▼
 产出 PR + HTML report（artifact sharing）
      │
      ▼
 人：只看第一步（目标/验收标准）和最后一步（PR 是否合理）
```

关键设计：人若需中途参与，agent 会以**易审查的可视化形式**提问——「你看一下这个 HTML 页面，OK 就继续」。工程师的职责从手动调试代码转移到**验收与关键决策**。

实际案例（[30:29](https://www.youtube.com/watch?v=LZlbXApG60E&t=1829)）：一位 Cloudflare 工程师每天用 master agent 在 top-level worktree 下开 20 个 sub-worktree，并行跟进 20 个 PR review，下班前喝咖啡验收。Orca 的 nested worktree 原生支持这种结构。

### 支柱 3：PTY 快照与无感日更

Orca 能日更的底层保障（[19:43](https://www.youtube.com/watch?v=LZlbXApG60E&t=1183)）：

| 环节 | 传统终端（Ghostty／iTerm） | Orca |
|---|---|---|
| 更新后 session | 全部丢失，50 个 agent 谁在跑要自己 figure out | 本地数据库对每个 PTY 快照，重启后恢复到原进度 |
| Dev Server | 被 kill | 不 kill，继续跑 |
| 后果 | 没人敢频繁更新 → 用户与工具迭代脱节 | 用户愿意每天跟着更新，永远拿到最新 feature |

工程上不小：几百个 session 的 state tracking 非常吃内存，团队为此做了一整套 caching 与 storage 优化。津晶的原话值得记：「prototype 到真正的 production 中间有一个鸿沟，能力不够你是看不见这个鸿沟的。AI 可以把你更快地带到鸿沟边上，但跨过去还是需要时间、精力和水平。」

### 自动化的现实比例

400+ 任务 ≠ 400 个都要盯。Orca 自身的日常运营就是样板（[25:51](https://www.youtube.com/watch?v=LZlbXApG60E&t=1551)）：

- **Release Manager agent**：每小时跑一次，搜集 Linear／Slack 内测 bug 报告 → 决定 branch cut → 需要人拍板时主动发 Slack 问津晶 → 伺服每日 stable release（每版上百 PR）。
- **社区运营 agent**：Discord（大社区）＋微信社群＋X 数万 follower 的 triage、回复归纳，全部自动化，跑在团队共享的 remote host 上。

---

## 三、成本策略：订阅混合编排

口播给出的账（[27:18](https://www.youtube.com/watch?v=LZlbXApG60E&t=1638)，个案数据，仅代表受访者口径）：

- 纯 API 计费跑这种规模：「每个月好几万美元」量级。
- 她的解法：**每个 provider 买 1-2 个最高档订阅（200 美元档）**，Claude Code、Codex、Grok、Antigravity、OpenCode 全配齐——第一方订阅是重补贴的（她估计 API 毛利约 90%），订阅档是「best deal」。
- 配套机制：usage hit limit 时 Orca 的 **handoff** 一键把任务从 Claude Code 转给 Codex「像没事一样继续干」。

| 计费方式 | 月成本量级 | 适合 |
|---|---|---|
| 纯 API | 数万美元（口播估计） | 用量小且突发 |
| 多家第一方订阅混合 | 数百美元（5 家 × $200） | 持续高并发的个人／小团队 |

背后的行业观察：enterprise 的 Anthropic 账单已超过工程师人力开销，成为第一大支出——这也是企业当前积极评估 Codex 与 open-weight 模型的原因。对个人的启示一样：**Vendor-agnostic 不是意识形态，是成本结构**。谁家 rate limit、谁家突发降智，换一家接着跑。

---

## 四、为什么不怕 Anthropic／OpenAI「顺手做掉」

主持人替所有人问了这个问题（[11:27](https://www.youtube.com/watch?v=LZlbXApG60E&t=687)）。津晶的答案分三层：

| 论点 | 逻辑 | 证据 |
|---|---|---|
| **迭代速度** | 每日一版 stable、每版上百 PR；大厂即便资源多，节奏跟不上 | Codex 的 session 间 context handover 直到访谈当周才上线，Orca 第一天就在做 agent 间接力 |
| **同理心差异** | Claude Code／Codex 团队 unlimited token，感受不到「订阅到期」「token 浪费」这类真实痛点；Orca 团队自己就是极限用户 | 功能取舍犹豫时，最终裁量＝「能否显著提升团队自身开发效率」 |
| **激励不对齐** | 大厂的 incentive 是 lock-in；把用户锁进自家生态的功能反而做不出中立调度 | 「只要还有 Codex 在，中立层就永远有价值」——更何况现在远不止两家 |

她也坦承这不是「defensible 的长久 position」——护城河本质上是执行速度 + 社区生态，而非某个单点功能。

与 Pi／OpenCode 的边界同样清晰（[14:59](https://www.youtube.com/watch?v=LZlbXApG60E&t=899)）：OpenCode 的开放在模型接入层，Harness 本身是 opinionated 的；Pi 选了 Pi 就不能用 Claude Code 这个 Harness。Orca 不逼用户做选择题——「Claude Code 有的我们都有，Codex 有的我们都有，我们有他们没有的」。甚至有用户为了用 Claude Code 这个 Harness，在中转 API 上跑 DeepSeek、GLM、Kimi 模型。

---

## 五、产品与增长方法论

### 5.1 极限用户（Eat Your Own Dog Food）

4 人团队（访谈时点），全员既是工程师又是产品经理，每天同时推进数百个 feature。「大厂产品经理通过层层访谈推测需求；我们对痛点的感知是第一手的。」（[14:03](https://www.youtube.com/watch?v=LZlbXApG60E&t=843)）

### 5.2 杠杆营销（Leverage Marketing）

95% 时间做产品、5% 做传播的情况下增长到 7 万星，津晶归因于「借力」（[40:26](https://www.youtube.com/watch?v=LZlbXApG60E&t=2426)）：

```text
  同行心态                            Orca 心态
  ─────────────                      ─────────────
  Claude Code 出新功能                Claude Code 出新功能
      │                                   │
      ▼                                   ▼
  「天塌了，用户都被抢走」           「好啊！在 Orca 里可以这样用」
      │                                   │
      ▼                                   ▼
  焦虑式防御宣传                     蹭干货流量：教大家新 tricks
                                      （5 分钟发一条，数百赞）
```

不把任何一家视为 competitor——「他目的是来 compete，但我们觉得这是一个 open ecosystem」。

### 5.3 造词红利

Orca 是 GitHub 上**第一个也是当时唯一**标明 ADE 的项目（[51:48](https://www.youtube.com/watch?v=LZlbXApG60E&t=3108)）。团队当年 deliberated 过「标这个风险是不是太大」，最后决定造词——现在越来越多人直接在 GitHub 搜 ADE、搜 agentic development，自然流量随之而来。新范式命名权＝搜索红利。

### 5.4 用户访谈：The Mom Test 实践

每个新项目深度访谈约 100 人（[42:59](https://www.youtube.com/watch?v=LZlbXApG60E&t=2579)），三问框架：

1. **为什么想到这个点子／为什么找到这个解决方案？**（挖真实动机）
2. **考虑过哪些替代方案？**（「你为什么不用 Zoom？」→ 定位自己的真优势）
3. **在类似产品上花了多少钱？**（不问「你愿不愿意付费」，问已付过多少）

YC group partner Michael Seibel 的标准更狠：做 B2B 要知道客户去年的收入、增速、最大开销是什么，能不能成为他们的第一／第二大开销。「做不到这个了解程度，宣传和产品设计都是隔靴搔痒。」湾区创业圈的人脉让这些数字可以直接问到——问不到就换人接着问，答案大同小异。

### 5.5 反馈处理：信息收集 + 集体判断

社区 PR、issue、论坛反馈全部只作**参考信息**，最终决定权在 4 人团队（[47:40](https://www.youtube.com/watch?v=LZlbXApG60E&t=2860)）。「AI 时代获取信息的方式越来越多，但都只是信息。」受访者（数据科学背景）补了刀：数据能告诉你的也就百分之二三十，最后看的是 mental model、product sense、taste。

Agent Status 显示位置的迭代就是例子：第一版放在界面角落「想看再看」，实测完全没人用；反复挪位置、多轮反馈调优，才成了现在被同行抄的形态。

### 5.6 造词之外：三个创业项目的共同模式

| 项目 | 领域 | 结局 | 入场时机 |
|---|---|---|---|
| 婚礼／活动直播平台 | Virtual event | 增长好、进 YC；但低频留存撑不起十亿级，主动放弃 | COVID 尾部，App Store 自然流量 |
| Stably | AI 软件测试 | 有企业客户；发现 general coding workflow 正在吞掉 specialized B2B 工作流 | AI junk code 泛滥、测试需求爆发 |
| Orca | ADE | 6 个月 7 万星 | 「管理 agent 有困难」成为普遍痛点 |

津晶的总结：**每个项目起步都吃 inbound**——在正确的时间点解决正确的问题，相信平台分发（App Store 搜索、GitHub 搜 ADE），而不是硬买流量。

---

## 六、AI 时代的宏观思考与创业机会

### 6.1 人机智能互补论

津晶两年前相信「AI 会取代人类」，现在转向互补论（[53:40](https://www.youtube.com/watch?v=LZlbXApG60E&t=3220)）：脑力不是单维度——虎鲸有虎鲸擅长的，人有人的；AI 擅长常规工程生成与流程推进，人的不可替代性在 taste、范式级假设（相对论式的突破）与对人性需求的感知。若互补成立，AI 是生产力工具，社会分配形态延续；若不成立（AI 全维度替代），财富分配才是真正的问题。她对此的态度：「I 类文明（能驾驭整颗行星能量的文明）我们才走到十亿分之一，可做的事太多了。」

主持人补的观点值得并置：工业革命后教育把人打造成标准件，而标准件恰恰最容易被 AI 取代；下一代的希望在于 AI native 世界里反过来强调 craft、passion、mastery。

### 6.2 三类创业机会（[57:50](https://www.youtube.com/watch?v=LZlbXApG60E&t=3470)）

| 方向 | 逻辑 | 备注 |
|---|---|---|
| **① ToC 回潮** | ToB sales cycle 反而变慢（采购在 AI usage 上反复权衡，签约 3 个月就想解约）；ToC 的 ROI 双向改善——开发成本骤降（一个月做多个 App）+ 付费意愿上升（补贴时代结束，用户接受为 AI usage 付费） | 零和型 App（与 TikTok 抢时间）竞争更烈；做「把饼做大」型（iPhone 时代的 Uber／Airbnb）不用抢注意力 |
| **② 软硬结合** | 国内创业者的供应链先天优势，美国多年难赶超 | 例：Plaud；朋友做美国 heating system 每月飞一次中国 |
| **③ 算力层** | compute／芯片／存储及集成；YC 新 batch 大量项目在 model hosting、算力撮合 | 湾区 MacBook 高配全线脱销；黄仁勋 five-layers cake 中，算力与模型之间的芯片优化层是纯 software 机会，CUDA 一统的格局正在松动 |

隐藏的第四类（[1:03:07](https://www.youtube.com/watch?v=LZlbXApG60E&t=3787) 起）：**传统深水区行业**。建筑、供应链这类十几二十年前的系统、层层中间商、巨大信息鸿沟的领域，AI 对它们「纯是 enable」——在 Anthropic 的产品设计模式与工地具体场景之间填沟填缝。

---

## 七、验证与勘误记录

按惯例对视频关键声称做了外部交叉验证（查询日期 2026-09-20）：

| 视频声称 | 验证结果 | 来源 |
|---|---|---|
| GitHub 七万星（标题＋描述「上传的时候已经7万星」） | ✅ API 快照 72,712 stars；口播开场为 49,000（录制时点）——约半年 4.9万→7万+，增速本身是数据点 | GitHub API `repos/stablyai/orca` |
| 仓库归属 stablyai/orca | ✅ 描述链接与 API 一致；MIT 协议、TypeScript、2026-03-17 创建 | GitHub API |
| 官网域名为 orca.dev | ⚠️ 口播与 README 链接均为 **onorca.dev**（口播快读易听成 orca.dev） | 官网／README |
| 「Claude Code／OpenAI API 毛利 90%，订阅是重补贴」 | ❓ 创始人口播估计，无公开财报佐证；仅记录为受访者观点 | 口播 27:44 |
| 纯 API 月成本「好几万美元」／只买 $200 订阅档 | ❓ 个案数据，无法溯源，不作为通用基准 | 口播 27:18 |
| 4 人团队、每日一版 stable、每版上百 PR | ✅ 口播自述；releases 页存在高频发布节奏（活数据，以 [releases](https://github.com/stablyai/orca/releases) 实时为准） | 口播 12:00／14:51 |
| 27 种 agent CLI 支持（含 Hermes Agent） | ✅ 官网对比表 27 supported agents；README Supported Agents 清单含 Hermes Agent、Kimi、Qwen Code 等 | 官网／README |
| ADE = Agentic Development Environment | ⚠️ 官网现用 **agent development environment**（无 -ic）；口播解释为「可想象成 Agent IDE」。两种用法都在流通 | 官网 title／口播 1:21 |
| 第二个项目进 YC | ❌ 口播当场纠正：**第一个**项目（直播平台）进的 YC | 口播 33:05 |
| Orca = 虎鲸／orchestration for agent | ✅ 口播自述命名来源 | 口播 12:21／54:09 |

---

## 八、给开发者的行动清单

1. **升级为 AI 架构师视角**：停止在单一聊天窗口手动改 bug；把重复工作封装为「输入 → 规格文件 → 交叉审核 → QA 验收」的 SOP，注意力只留给顶层评审与目标设定。
2. **构建 Vendor-agnostic 弹性**：把 skills／配置放公共目录、跨工具复用；混合编排多家第一方订阅与开源模型，别被单一厂商的定价与 rate limit 绑架。
3. **为并行而设计**：任务进 worktree 隔离、状态结构化（working／blocked／done），让「同时推进 20 件事」在认知上可负担。
4. **做产品以自身极限痛点为锚**：先当重度用户打磨；营销学会借力——在巨头的浪上搭最懂用户的梯子。
5. **访谈用户问已付成本**：不问「愿不愿意买」，问「过去为这个问题付过什么、预算排第几」。

---

## 参考资料

- [一个人管400个AI任务，已经是标配了？｜对话GitHub七万星的Orca创始人津晶（课代表立正）](https://www.youtube.com/watch?v=LZlbXApG60E)
- [stablyai/orca — GitHub 仓库](https://github.com/stablyai/orca)
- [Orca 官网（onorca.dev）](https://www.onorca.dev/)
- [超线性学院伴读文章（社区内）](https://www.superlinear.academy/c/recording/orca)
- [Orca Releases（日更 changelog）](https://github.com/stablyai/orca/releases)

## 相关笔记

- [[Orca ADE - 开源多 Agent 开发环境实测]]——同产品上手实测：Worktree 隔离、跨模型并行、Design Mode、SSH 远端
- [[Orca ADE - 多 Agent 工作台入门导览]]——同产品概念框架与上手导览（大力Thinking 频道）
- [[Claude Code Hooks - 确定性自动化防线]]——与「Agent 管 Agent」呼应的确定性自动化另一路径

---

*笔记基于视频字幕全文 + 官网／README／GitHub API 交叉验证整理，生成于 2026-09-20。*
