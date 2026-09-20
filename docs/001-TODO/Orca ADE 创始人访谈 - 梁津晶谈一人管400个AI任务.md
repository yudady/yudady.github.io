---
title: Orca ADE 创始人访谈 - 梁津晶谈一人管400个AI任务
aliases:
  - Orca 创始人访谈
  - 梁津晶 Jinjing Liang
  - 课代表立正 Orca 访谈
tags:
  - ai-agent
  - ai-tools
  - startup
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=LZlbXApG60E"
  - "https://github.com/stablyai/orca"
  - "https://www.ycombinator.com/companies/stably-ai"
  - "https://www.superlinear.academy/c/recording/orca"
author: 课代表立正（频道）· 梁津晶（受访者，Orca 联合创始人/CEO）
created: 2026-09-20
updated: 2026-09-20
description: |
  Orca（stablyai/orca）联合创始人梁津晶（Jinjing Liang）访谈笔记：ADE 与 Harness 的概念边界、
  一人调度 400+ Agent 任务的工作流与自动化 SOP、PTY 快照等工程深水区、4 人团队日更的产品方法论，
  以及她看好的三类 AI 创业机会。与既有实测/入门笔记互补，本篇偏产品哲学与创业方法论。
level: intermediate
stars: 5
---

# Orca ADE 创始人访谈 - 梁津晶谈一人管400个AI任务

> 「课代表立正」频道对 Orca 联合创始人梁津晶的 66 分钟访谈（2026-09-18）。访谈时 Orca 约 5 万 GitHub 星，视频发布时已达 7 万。核心命题：一个人如何同时推进 400 多个 AI 任务而不变成「AI 监工」。适合关注多 Agent 编排、Agent 时代工具创业与产品方法论的开发者。
>
> 同主题既有笔记：产品实测见 [[Orca ADE - 开源多 Agent 开发环境实测]]，概念入门见 [[Orca ADE - 多 Agent 工作台入门导览]]。本篇为创始人视角，三者互补。

## 目录

- [[#背景：人物与项目快照]]
- [[#ADE 与 Harness：概念边界]]
- [[#中立架构与成本策略]]
- [[#为什么不怕被 OpenAI/Anthropic 做掉]]
- [[#工程深水区：PTY 快照与日更底气]]
- [[#一人管 400 个任务的三层工作流]]
- [[#创业历程与产品方法论]]
- [[#宏观判断与人机互补]]
- [[#三类 AI 创业机会]]
- [[#验证与勘误记录]]
- [[#全文总结与行动建议]]
- [[#参考资料]]
- [[#相关笔记]]

---

## 背景：人物与项目快照

访谈开场时（录制时点）Orca 刚过 49,000 星；受访者预估「视频发的时候你们就 10 多万了」——实际发布时为 7 万+（见[[#验证与勘误记录]]）。

| 项 | 内容 |
|---|---|
| 受访者 | 梁津晶（Jinjing Liang），Stably AI 联合创始人/CEO |
| 履历 | 2016 年赴美毕业后先做量化（quant），后至 Google 搭建 Chrome 测试与发布基础设施；co-founder Neil Parker（CTO）为 Uber 早期 Tech Lead |
| 公司 | Stably AI（YC 孵化），产品 Orca，录制时团队 4 人 |
| 仓库 | github.com/stablyai/orca，MIT，TypeScript/Electron，2026-03-17 创建 |
| 命名 | Orca = 虎鲸；产品名来自「**O**rchest**rca**tion for agent」——让 agent 互相编排 |
| 节奏 | 每天发布一个 stable 版本，每版含上百个 PR |

三次创业轨迹（后详）：

```
直播平台（special events，婚礼/企业活动）
   └─→ 增长好、进了 YC，但留存差 → 放弃
Stably（AI 软件测试 agent）
   └─→ 有企业客户，但发现 Dev tool 在坍缩成 general coding workflow
Orca（ADE，多 Agent 编排环境）
   └─→ 把前作 agent session 管理能力迁移到通用 coding agent 调度
```

---

## ADE 与 Harness：概念边界

### 两个定义

- **Harness**：单一 agent 的执行回路（loop）——接收输入、调用工具（bash、web search 等）、产出输出。Claude Code、Codex、Pi、OpenCode、Devin 都是 Harness。
- **ADE（Agentic Development Environment）**：立于 Harness 之上的「人与多 Agent 协同的工作流管理平台」。不自研 loop，而是调度市面上所有成熟 Harness。

梁津晶用访谈者的翻译任务类比：用 Markdown 文件 + `claude -p` 跑翻译，就是「调用了 Claude Code 这个 Harness」；ADE 是把这类过程整体 UI 化、系统化，从「调一个」变成「调度 100 个」。

### 分层对比

| 维度 | Harness（Claude Code/Codex/Pi/OpenCode） | ADE（Orca） |
|---|---|---|
| 核心职责 | 单 agent 的 loop + tool use | 人与多 agent 的 workflow 管理 |
| 自身定位 | 自家 loop 最强 | 无自研 Harness，全兼容 |
| 用户切换成本 | 选了 Pi 就不能用 Claude Code 这个 Harness | 不逼用户选边，1 分钟上手 |
| 形态 | CLI/TUI/Desktop 各异 | 70% UI + 30% CLI（可调度） |

### 「开放」的三层辨析

OpenCode 的开放只在 **model 层**（可接各种模型），loop 层是 opinionated 的——「你得用它给你的那一套」；Pi 同理，可用 Anthropic model 但无法用 Claude Code 这个 Harness。Orca 的兼容在 **Harness 层**：Claude Code/Codex 有的它都有，且不要求放弃任何一个。访谈中还提到有人为了用 Claude Code 这个 Harness，宁可走中转 API 接 DeepSeek、GLM、Kimi 的模型——说明「Harness 偏好」独立于「模型偏好」存在。

```
model 层开放？   loop 层开放？   harness 层开放？
OpenCode    ✅          ❌(opinionated)   —
Pi          ✅          ❌(必须选 Pi)     —
Orca        ✅          借力各家           ✅(不逼选边)
```

> TUI 与 GUI 之争：梁津晶明确表示「没有 strong opinion」。Orca 两种都支持且可同 session 切换。商业考量上优先 TUI 体验，因为 Claude Code/Codex 的 CLI 用户远大于 Desktop 用户，且非 first-party GUI 每次跟版重做 rendering 会拖慢迭代（first-party 如 Codex 的 GUI 才与 CLI on par）。

---

## 中立架构与成本策略

### Vendor-Agnostic 不是姿态，是采购现实

用户与大企业的趋势都是从单一 vendor 采购转向多 vendor + open model。原因：

1. **Rate Limit / 降智**：单一模型随时可能撞限流或突发变笨；
2. **成本**：纯 API 计价跑自动化「每月好几万美元」；
3. **大厂 incentive 不对齐**：Anthropic/OpenAI 的利益是让你 lock in。

### 订阅制套利（访谈最实操的一段）

梁津晶的成本方案：每家只买顶配订阅（200 美元档），多家组合覆盖全部自动化需求。

```
纯 API 计费          第一方订阅（如 $200/月档）
─────────────        ─────────────────────
按 token 结算         重度补贴（heavily subsidized）
月费 $30,000+         每家 $200 × 5-6 家
无限但昂贵            有额度上限 → 撞限就 handoff
```

她的原话：Claude Code 和 OpenAI 按 API 约 90% 毛利，但订阅档是重补贴的「最好的 deal」。**Handoff 功能**是这个策略的执行器：Claude Code 撞限后一键把任务（含上下文）转给 Codex「像没事一样继续干」，再撞限还有 DeepSeek（OpenCode Go）、Grok、Antigravity 轮换。

> 企业侧同构逻辑：Anthropic 账单已成一些企业第一大开销、超过工程师薪资——这是 enterprise 转向 Codex 与 open-weight model 的直接原因。

✅ 个人可执行清单（从访谈提炼）：
- 每家 provider 各买 1-2 个顶配订阅，而非一家买多个号（账号数会被限制）
- 把 skills/配置放公共目录，不锁进某家工具的私有目录
- 任务描述保持 Harness 无关，随时可 handoff

---

## 为什么不怕被 OpenAI/Anthropic 做掉

访谈者直问：你们的功能与 Claude Desktop/Codex 重合度很高，为什么不是大厂的一个 feature？梁津晶的三段式回答：

| 论点 | 依据 |
|---|---|
| 1. 迭代速度 | 日更（每天 stable 版 × 上百 PR）；大厂 GUI 跟版周期长 |
| 2. 用户同理心 | 团队自身就是 100x builder，每天几百个 feature 并行，「Token 浪费」「切换摩擦」是切肤之痛；Claude Code/Codex 团队 unlimited token，感受不到订阅管理与效率焦虑 |
| 3. Incentive 不对齐 | 大厂做中立调度层等于自我拆墙——他们的 KPI 是 lock-in |

社区原话的转述：「用了 Orca 以后，不知道为什么 Claude Code 和 Codex 就是没把这事做好」。她自己也承认这不是「defensible 的长久 position」，护城河本质是速度与对 builder 的感同身受。

另一个被追问的点：为什么能在 Pi/OpenCode 之上做出增量？答案还是开放性 + 借力——Orca 正在转向「一切皆 plugin」的 Generative UI 方向，plugin 偏 UI 层（自定义文件编辑器、PPT 编辑器、video editor——比如芯片设计公司想在 Orca 里打开自家格式），与面向 AI 的 skill/command 形成互补。

---

## 工程深水区：PTY 快照与日更底气

### 起点：Ghostty/iTerm 的三个缺陷

团队当年为追求 100x/1000x 试遍了工具，最爱 Ghostty/iTerm 的切屏（split panes），但有三个硬伤：

```
Ghostty / iTerm 的三宗罪
├─ ① 非 worktree centric ── 切屏能跑 agent，但 git worktree
│                             创建/删除的 lifecycle 全手工
├─ ② 无真 agent status ──── Claude Code 只能改终端 title，
│                             多 tab 下看不到「谁 blocked/谁完成」
└─ ③ update 即团灭 ──────── 一更新全部 session 丢失；跑 50 个
                              agent 后没人敢更新 → 用户跟不上迭代
```

Orca 对 ①② 的解法是 worktree 生命周期管理 + Agent Status API（可被 extension 消费，用户能自建管理视图）。对 ③ 的解法是 **PTY 快照**。

### PTY 快照机制

每个终端 session 本质是一个 PTY（pseudo-terminal）。Orca 用本地数据库对 PTY 做快照，更新重启后：

- 不只恢复 scrollback history，**跑到一半的进程继续跑**（含 dev server，不会被 kill）
- 恢复「哪 50 个 agent 在跑、哪些已完成」的运行时状态

难点不在快照/恢复本身，而在性能：几百个 session 的状态追踪极耗内存，需要整套 caching 与 storage 优化。这段是访谈中「prototype 到 production 有鸿沟」论点的实证——AI 能把你更快带到鸿沟边上，跨过去仍要时间、精力和工程水平。

---

## 一人管 400 个任务的三层工作流

访谈核心段落。400 多个 worktree 同时推进，方法论分三层：

```
第 1 层：看得见（Status + 搜索）
  Agent Status 集成：跨所有 worktree、跨所有 Harness
  ├─ working
  ├─ blocked ── 需要人立即反馈（you need to answer immediately）
  └─ done ───── 待 review（you need to review the result）
  深度索引搜索：session title / worktree name / Linear ticket 关键词
  → 「三天前那个任务」一秒回到现场

第 2 层：Agent 管 Agent（编排 SOP）
  Master agent 读 Linear ticket
  ├─ 收集 requirements → 写 design doc → 多轮交叉 review
  ├─ implement（Claude Code 驾驭 Codex 互相协作）
  ├─ QA testing（Orca 自带 browser use + mobile emulator）
  └─ 产出 PR + HTML report + artifacts 供人验收
  人只看第一步（目标/验收标准）和最后一步（产物是否合理）
  中途重大决策：让 AI 给「易 review 的可视化」（HTML 页面）再拍板

第 3 层：定时自动化（SOP 化）
  400+ 任务中 200+ 已全自动跑
  ├─ Release Manager：每小时巡检 release 进度，自动选 branch、
  │   收集 Linear/Slack/内测 bug 信号、必要时 Slack 找人
  ├─ 社区运营：Discord/微信/X 的 triage 与归纳
  └─ 全部跑在团队共享 remote host（Orca Automation 连接远端）
```

### 真实用户画像

- **Cloudflare 工程师案例**：每天 20 个 PR review → 一个 master agent + top-level worktree 开 20 个 nested sub-worktree 并行跟进，下班前验收提交。
- 个人项目以 Web/Mobile/Desktop（Electron 变容易了）为主；也有人每天炒股信息流自动化——左边 Claude Code，右边 HTML 股票分析加批注回喂。
- 用户群与早期 Cursor、早期 Claude Code 用户高度重合；典型路径是 personal project 用爽后带到 day job。
- 留存规律：用户几乎都「试遍了所有同类工具」，无论先试还是后试 Orca，最后留下——「留存是产品决定的，好到一定程度自带传播系数（1 传 2、2 传 3）」。

### 判断：你在第几层？

```
你该从哪层入手？
├─ 同时并行 ≤5 个 agent，常忘记谁在干嘛
│    → 先解决第 1 层（状态面板 + 可搜索的任务索引）
├─ 每天重复写同类需求 → 交给 agent 走全流程
│    → 建第 2 层 SOP（spec → 交叉 review → QA → 报告）
└─ 有固定周期的运营/发布/巡检工作
     → 上第 3 层（定时 agent + 远端常驻）
```

---

## 创业历程与产品方法论

### 三次创业的复盘逻辑

| 项目 | 领域 | 结局 | 教训 |
|---|---|---|---|
| 直播平台 | 婚礼/活动直播 | 增长好、进 YC | 留存差——增长永远依赖拉新，做不成十亿级公司；要做高频使用的东西 |
| Stably | AI 软件测试 | 有企业客户 | 发现所有 Dev tool 的 niche workflow 正被 general coding agent 吞掉（她举 video editor 也能用 Claude Code + Seedance 拼出来的例子） |
| Orca | ADE | 现象级增长 | 把 Stably 的 agent session 管理积累迁移到通用调度 |

每次启动都遵循同一套动作：像「第一次在黑板上头脑风暴」一样，**深度访谈约 100 个人**。

### The Mom Test 式访谈三问

1. 你为什么会想到这个点子/为什么找到这个解决方案？
2. 你考虑过哪些替代方案（比如「为什么不用 Zoom」）？→ 据此放大真正的差异化
3. 你在类似产品上实际花过多少钱？→ 只问历史支出，不问「愿不愿为我的产品付钱」

YC group partner Michael Seibel 的标准更狠：做 B2B 就要知道客户去年的收入、增速、最大开销是什么、你能不能成为他们第一/第二大开销——「不知道这些，宣传和产品设计都是隔靴搔痒」。湾区人脉让这些问题的答案可以直接问出来（她顺口举例：很多公司第一大开销已从 Datadog 变成 AI）。

### 反馈处理与决策权

社区 PR、issue、论坛反馈全部只作「信息输入」，qualitative + quantitative 各占一半也仅是参考；**最终决定权在团队 4 个人的 product sense**。AI 时代信息获取渠道爆炸，但判断不能外包给音量。

Agent Status 集成的迭代是实例：第一版放在界面右侧角落「想看再看」，毫无用处；换到中心位置、反复多版、按反馈调整，才成了现在被广泛抄袭的形态。

### 两个「开悟瞬间」

1. **以自己为标准用户**：feature 犹豫不决时，唯一裁量标准是「能否显著提升我们自己团队的效率」——再不济也自用了。
2. **不 fight against 大方向**：现有产品的大方向已经很好，该 build on top of it；另一个分化是把 chatbox 塞进现有 UI 的产品，「context loss 太多」。

### 增长：Leverage Marketing

95% 时间做产品，5% 想到什么发什么（5 分钟一条的 X post 常有几百赞）。打法与产品设计同构——借力：

- Claude Code/Codex 发新功能 → 发「如何在 Orca 里更好地用这个新特性」，借大厂流量转化，而非视其为 competitor（「他目的是来 compete，我们不把任何一家视为 competitor」）
- **造词**：GitHub 上第一个（当时唯一）自标 ADE 的项目，吃透「ADE 是什么」的新词搜索红利
- 相信平台分发：三个项目起步都是 inbound（直播赶上 COVID 尾巴的 App Store 自然流量；Stably 赶上 AI testing 焦虑；Orca 赶上 agent 管理焦虑）

✅ 心法清单：时间点 > 营销投入；留存决定传播系数；不与用户既有习惯为敌（「我不是要你换工具，是让你把 Claude Code 用得更好」——这降低 adoption 摩擦并激活同事间分享）。

---

## 宏观判断与人机互补

梁津晶的乐观建立在一个非共识前提上：**AI 与人类智能是互补（complementary）而非同维度替代**。

- 类比：人类与虎鲸（Orca 的本义）都是高智能，但各有擅长维度；AI 擅长常规工程生成与流程推进，人类不可替代处在 taste、范式级假设（如相对论式的突破）与对人性需求的感知。
- 财富分配的担忧也系于此：若 AI 可完全替代人，财富将集中到掌握 AI 的少数人；若互补，分配形态得以延续。
- 访谈者的补充：工业革命后教育把人打造成「标准件」，而标准件最易被 AI 取代；AI native 的下一代反而会重新强调 craft、passion、mastery。
- 历史视角：纺织机出现时英国 80-90% 青年失业，转型期对一代人是艰难的，对历史只是一个 blink。她另一句松弛感的来源：「我们可能刚在文明进程的十亿分之一处，饭机器人都没有、火星也没去——可以 build 的东西太多了，这个不行就做下一个。」

---

## 三类 AI 创业机会

### 1. ToC 回潮（B2B 采购反而变慢）

| | B2B/SaaS | ToC |
|---|---|---|
| 采购/决策 | AI 时代更犹豫：反复权衡 tool vs AI usage 配比，签了 3 个月就想解约 | 付费意愿在上升——广告补贴时代结束，用户接受为 AI usage 付费 |
| 投入 | sales cycle 拉长 | 一个人一个月能做好多个 APP，ROI 变好 |
| 竞争 | — | 零和型（抢 TikTok 时长）更卷；做增量型（iPhone 时代的 Uber/Airbnb 类）空间大 |

### 2. 软硬结合（国内供应链优势）

纯软件层同质化；结合供应链与物理世界的方案壁垒更高。案例：Plaud。她认识的美国硬件创业者每月飞一次中国也搞不定 supply chain——这是国内创业者的先天优势。

### 3. 算力层与芯片推理优化

第一性思考：compute、芯片、存储及其集成（算力）。观察：

- YC 新 batch 热点集中在此：model hosting、enterprise self-host、帮找 agent/model 算力
- 湾区佐证：Apple Store/Best Buy 的高配 MacBook 全线脱销 back order
- 黄仁勋「five layers cake」：算力与 model 之间的**芯片推理优化层**是纯 software 机会——此前 CUDA 一统天下，AI 之后缝隙重新打开（从芯片到产出 token 之间的优化）

外加第四个散点：**传统深水区行业**（建筑等）——AI 纯属 enabling，但 Anthropic 式产品设计与工地场景之间鸿沟巨大，「填沟填缝」都是机会（访谈者用自己下单等一个月、多层中间商的采购经历共鸣）。FDE 类 AI 落地服务也可做，但需求热点逐月漂移，须持续 adapt。

---

## 验证与勘误记录

| 视频声称 | 验证结果 | 来源 |
|---|---|---|
| 标题「GitHub 七万星」 | ✅ 视频发布时 71.6k，2026-09-20 API 实测 72,712 stars / 4,754 forks（快照口径，活计数） | GitHub API `repos/stablyai/orca` |
| 录制时「49,000 星，马上 5 万」 | ✅ 与发布时 7 万+ 一致，符合增长斜率 | 访谈口述 0:55-1:00 |
| 团队 4 个人 | ⚠️ 口述「录制时 4 人」为真；YC 公司页显示 25 employees，为页面更新滞后或后续扩张，两口径并存 | 访谈 14:51 + YC 公司页快照 |
| 每天一个 stable 版本、每版上百 PR | ✅ 仓库 11,261 commits（2026-03-17 创建，约 6 个月），高频发布与日更节奏吻合；「每版上百 PR」为口述无法逐版核验 | GitHub API + 访谈 12:00 |
| Orca 是 MIT 开源、Electron/TypeScript | ✅ license: MIT，language: TypeScript | GitHub API |
| 创始人：津晶，前 Google，co-founder 前 Uber | ✅ YC 页：Jinjing Liang（CEO，建过 Chrome 测试与发布基础设施）、Neil Parker（CTO，Uber 早期 Tech Lead） | YC 公司页 |
| 「第一个创业项目（直播平台）进的 YC」 | ✅ YC 公司页存在且 Stably AI 为其后续演化；口述与页面一致 | YC 公司页 + 访谈 33:03 |
| Claude Code/OpenAI 按 API 约 90% 毛利、订阅重补贴 | ❓ 口述观点，无公开财报数据佐证；订阅套利策略本身（多家 $200 档组合）是可执行的一手经验 | 访谈 27:37-28:09 |
| Pi/OpenCode 会逼用户二选一 Harness | ✅ 与两品产品形态一致（OpenCode 主打 model 层开放） | 访谈 15:01-16:37 + 产品公开文档 |
| 伴读文章（superlinear.academy） | ⚠️ 登录墙，仅标题与导语可读：《Orca CEO：当 AI 同时做二十件事，人该在哪里出手？》 | webReader 抓取 |

---

## 全文总结与行动建议

梁津晶的实践展示的范式转移：生产力瓶颈不在模型产出代码的速度，而在人面对海量异步任务时的**调度架构、注意力分配与验收机制**。Orca 的立身之本是三层：中立调度（借力所有 Harness 而非替代）、工程深水区（PTY 快照这类「跨过鸿沟」的硬功夫）、builder 同理心（自己就是极限用户）。

**给开发者的行动建议：**

1. **升级为 AI 架构师视角**：停止在单一聊天窗手动修 bug；把重复工作封装为「输入 → 规格文件 → 交叉审核 → QA 验收 → 报告」的 SOP，注意力只留给顶层评审与目标设定。
2. **建立 Vendor-Agnostic 弹性**：多家顶配订阅 + handoff 轮换，避免被单一厂商的定价与限流绑架；配置和 skills 放公共目录。
3. **状态先于数量**：并行 agent 数量上来之前，先解决「看得见」（status 面板 + 可搜索索引），再谈编排与自动化。
4. **产品方法论**：以自己为重度用户极限打磨；营销学 leverage（借生态巨头的浪）；访谈用户问历史支出而非未来意愿。

---

## 参考资料

- [一个人管400个AI任务，已经是标配了？｜对话GitHub七万星的Orca创始人津晶（课代表立正，2026-09-18）](https://www.youtube.com/watch?v=LZlbXApG60E)
- [stablyai/orca - GitHub](https://github.com/stablyai/orca)
- [Orca 官网（onorca.dev）](https://www.onorca.dev/)
- [Stably AI (Orca) - Y Combinator 公司页](https://www.ycombinator.com/companies/stably-ai)
- [社区伴读文章 - Superlinear Academy（登录墙）](https://www.superlinear.academy/c/recording/orca)

## 相关笔记

- [[Orca ADE - 开源多 Agent 开发环境实测]]（Tonbi's AI Garage：安装、四模型并行、Design Mode、SSH 远端实测）
- [[Orca ADE - 多 Agent 工作台入门导览]]（大力Thinking：ADE 概念框架与上手导览）
- [[GitHub 开源周选 20 项目 - ManuAGI #293]]
