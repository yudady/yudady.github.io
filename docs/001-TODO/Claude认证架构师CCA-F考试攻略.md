---
title: Claude 认证架构师（CCA-F）考试攻略 — 904/1000 一手备考手册
aliases: ["CCA-F", "CCAR-F", "Claude Certified Architect", "Anthropic 认证"]
tags:
  - claude
  - anthropic
  - certification
  - ai-agent
  - status/active
  - type/doc
source:
  - "https://www.linkedin.com/pulse/only-playbook-you-need-crack-claude-architect-majumdar-pmp-spc--lggec"
  - "https://theaiarchitects.com/blog/claude-certified-architect-exam"
  - "https://preporato.com/blog/claude-certified-architect-complete-guide-2026"
author: Santanu Majumdar（PMP / PMI-CPMAI / SPC）
created: 2026-10-29
updated: 2026-10-29
description: Anthropic 首个官方技术认证 CCA-F 全攻略，考试结构、五域权重、五个高频踩坑点、四周备考路径与官方资源清单，附 2026-10 时效勘误（费用已涨至 $125、监考换 Pearson VUE）。
level: intermediate
stars: 4
note: 原文为 LinkedIn 长文，作者文末推广自建 647 题付费题库（营销成分，方法与资源部分已独立验证）。文章口径为 2026-04，考试费用与监考方式其后已变更。
---

# Claude 认证架构师（CCA-F）考试攻略 — 904/1000 一手备考手册

> Anthropic 于 2026-03-12 推出首个官方技术认证 **Claude Certified Architect – Foundations（CCA-F）**。本文整理自通过者 Santanu Majumdar（亚马逊 TPM，904/1000）的一手长文，并用 2026-10 的多个独立来源交叉验证了全部关键事实。适合评估「要不要考、怎么考、考什么」的工程师 / TPM / 架构师。

## 目录

- [[#TL;DR]]
- [[#一、CCA-F 是什么考试]]
- [[#二、五大领域与权重]]
- [[#三、谁该考 / 谁不该考]]
- [[#四、五个高频踩坑点]]
- [[#五、Do this, Not that（8 组对照）]]
- [[#六、四周备考路径]]
- [[#七、Claude 生态系统地图（考点全景）]]
- [[#八、报名与考试日]]
- [[#九、验证与勘误记录（2026-10-29 核验）]]
- [[#十、关键资源]]

---

## TL;DR

> [!summary] 60 秒版本
> - **考什么**：60 道情景题（单选 + 多选）/ 120 分钟 / 满分 1000，720 及格；6 个生产场景随机抽 4 个；全程监考，无 Claude、无文档、无第二标签页
> - **权重**：域1 Agentic 架构 27% ＞ 域2 Claude Code 20% = 域3 提示工程 20% ＞ 域4 MCP 18% ＞ 域5 上下文管理 15%
> - **门槛**：官方建议 6+ 个月 Claude API / Agent SDK / Claude Code / MCP 实战；没搭过真实系统很快露馅
> - **备考**：通过者普遍 2-4 周专注准备；路径 = 官方 Exam Guide PDF 打底 → Skilljar 免费课 → 每周都有 build
> - **时效警告**：文章口径 $99 / ProctorFree；2026-09-30 前后已变 $125 / Pearson VUE，且仅限 Claude Partner Network 成员报考

---

## 一、CCA-F 是什么考试

> [!info] 基本信息
> - **定位**：Anthropic 首个官方技术认证，2026-03-12 上线（非课程完成徽章、非 AI 素养测试）
> - **一句话**：测验「你能否为企业级规模设计生产级 Claude 应用」
> - **现名**：考试编码已更名 **CCAR-F**（2026-10 核实）

| 项目 | 内容 |
|------|------|
| 题量 / 时长 | 60 题（单选 + 多选）/ 120 分钟，约 2 分钟一题 |
| 场景机制 | 题库 6 个真实生产场景，每场随机抽 4 个（必须全部学习） |
| 计分 | 100-1000 scaled，**720 及格**；未作答按错误计，务必每题必答 |
| 监考 | 全程摄像头 + 房间扫描，禁止任何外部工具，不能暂停 |
| 有效期 | 12 个月；到期前通过免费的开放书在线评估可续 12 个月（2026-10 新信息） |
| 语言 | 英文或日文 |

```
题库（6 个生产场景）                 考试（随机抽 4 个，约 15 题/场景）
┌──────────────────────┐          ┌──────────────────────────┐
│ S1 · S2 · S3          │  抽 4/6  │ 场景 A ── 约 15 题          │
│ S4 · S5 · S6          │ ──────→ │ 场景 B ── 约 15 题          │
│ （全部 6 个都要学）      │          │ 场景 C ── 约 15 题   60 题   │
└──────────────────────┘          │ 场景 D ── 约 15 题  /120 min │
                                  └──────────────────────────┘
                                       720/1000 及格（scaled）
```

---

## 二、五大领域与权重

> 权重数据已与官方 Exam Guide v1.0 及多名独立通过者核对一致（27/20/20/18/15）。

| # | 领域 | 覆盖子话题 | 权重 |
|---|------|-----------|------|
| 1 | Agentic Architecture & Orchestration | 多代理系统、hub-and-spoke 设计、任务分解、fallback 循环、lifecycle hooks | **27%** |
| 2 | Claude Code Configuration & Workflows | CLAUDE.md 层级、hooks、CI 集成、`-p` flag、plan mode、自定义 slash commands | **20%** |
| 3 | Prompt Engineering & Structured Output | JSON schema 设计、防幻觉、few-shot 模式、重试循环、自我评估 | **20%** |
| 4 | Tool Design & MCP Integration | MCP server/client 设计、工具边界决策、传输机制（Python/TS）、三大原语 | **18%** |
| 5 | Context Management & Reliability | 升级触发器（escalation triggers）、错误传播、上下文溢出处理、provenance tracking | **15%** |

```
域1 Agentic 架构与编排         █████████████████████████████  27%
域2 Claude Code 配置与工作流   ██████████████████████         20%
域3 提示工程与结构化输出        ██████████████████████         20%
域4 工具设计与 MCP 集成        ████████████████████           18%
域5 上下文管理与可靠性          ████████████████               15%
```

**解读**

- 域1 是「考生赢输的地方」（Agent SDK 主场），作者建议投入 **40% 备考时间**
- 域5 权重最小，但作者强调：那里的每一次失败都会连锁反应到其他场景题——「大多数人投资不足的领域」
- ⚠️ 原文说「超过一半的考试集中在两个领域」，与权重图不符：任两域最高 47%（27+20），前三域合计 67%——见[[#九、验证与勘误记录（2026-10-29 核验）]]

**时间紧张时的分配决策树**

```
备考时间只有 ~2 周？
├─ 是 → 只走主线：Exam Guide PDF + Skilljar「Building with the Claude API」
│        + 官方模考；时间分配向域1 倾斜（~40%）
└─ 否 → 走完整四周路径（见第六节）
     ├─ 已有多代理 / MCP 实战 → Week 3 可压缩，模考刷到 900+ 再约考
     └─ 无任何实战 → 先 build 再报名（多代理工作流 + MCP server 至少各一）
```

---

## 三、谁该考 / 谁不该考

| 类别 | 人群 |
|------|------|
| ✅ 强匹配 | 设计生产级 Claude 系统的解决方案架构师；规模化集成 Claude API 的 AI 工程师；管理 AI 重度项目的高级 TPM / 技术负责人；为企业客户构建 Claude 咨询实践的顾问 |
| ✅ 也值得 | AI 平台的产品经理；已交付多代理工作流的开发者；管理 Claude 内部工具的平台工程师；认真布局 2026 AI 就业市场的人 |
| ❌ 还没准备好 | 只写过 prompt、没建过系统的开发者；想要速成 LinkedIn 徽章的人；唯一的 Claude 经验是网页聊天界面的人 |
| ❌ 先省下报名费 | 还在学 Claude API 基础的工程师——先修完免费的 Anthropic Academy 课程、build 出真实东西，再报名 |

**TPM / 非工程师视角**（原文专节）：不必是工程师，但必须像系统设计师思考——风险权衡、失效模式分析、build vs buy、升级框架。「程序管理与技术架构的边界正在溶解」；考试不要求写代码，要求做架构决策。

---

## 四、五个高频踩坑点

作者「真正差点丢分的地方」，每条都被考题直接针对。

### 1. 提示工程 ≠ 写好提示（域3）

以为考「指令措辞」，实际考**生产级可靠输出管道**：模式强制、结构化输出、重试逻辑、自我评估循环、JSON schema 验证。

| ❌ 误区理解 | ✅ 考试实际要求 |
|------------|---------------|
| 写出清晰的指令 | 设计模式强制的结构化输出流程 |
| 一次性生成结果 | 重试循环 + 自我评估（self-evaluation） |
| few-shot 堆例子 | JSON schema 设计 + 输出校验 |

```python
# 域3 考的是这种「可靠性管道」（示意伪代码），不是措辞技巧
resp = client.messages.create(
    model="claude-sonnet-4-5",
    system="只输出符合 SCHEMA 的 JSON 对象",
    messages=[{"role": "user", "content": task}],
)
data = json.loads(resp.content)     # 1. 解析
errors = validate(data, SCHEMA)     # 2. schema 校验
if errors or data["confidence"] < THRESHOLD:
    retry_or_escalate()             # 3. 校验失败/自评低 → 重试或升级
```

### 2. MCP 工具描述被严重低估（域4）

两个工具描述重叠或描述过简时，模型会猜——**生产环境里它会猜错**。生产级描述必须包含：输入格式、示例查询、边界情况、明确边界。

| ❌ 差描述 | ✅ 生产级描述 |
|----------|-------------|
| `"查询订单"` | 精确动词 + 输入格式 + 示例 + 边界 + 错误语义 |

```json
{
  "name": "get_order_status",
  "description": "按订单号查询支付订单状态。输入：18 位数字订单号（如 202604290000000001）。示例：调用后返回 {status: 'PAID', paid_at: ...}。边界：仅支持近 90 天订单；退款单请改用 query_refund；无权限返回 403 而非空结果。"
}
```

### 3. Claude Code 的 `-p` flag 不是可选知识（域2）

`-p`（print mode）启用非交互模式——CI job 里没加它会**无限期挂起**。文章：明确考过，「我现在知道了，第一天我不知道」。

```bash
# CI/CD 中必须用 -p（print mode），否则进程等待 TTY 交互，job 永远挂起
claude -p "summarize the changes in this diff" --output-format json
```

### 4. 升级触发器有「听起来对的错误答案」（域5）

域5 最狡猾的干扰项：两个**听起来合理但实际无效**的升级触发器（escalation triggers）。作者要求把三种有效触发点背到条件反射。
⚠️ 文章强调其重要性但**未列出具体三种**——需到官方 Exam Guide 的域5 任务陈述里找（子话题：escalation triggers / error propagation / context overflow / provenance tracking）。

### 5. 子代理没有共享内存（域1）

多代理系统中，子代理需要的**每条信息都必须在 prompt 里显式传递**，没有自动历史传播。合成代理需要研究代理的发现？直接传。作者模拟考错一道，真题没错。

```
❌ 错误假设：子代理能「看到」orchestrator 的对话历史
✅ 正确做法：所需上下文一律显式写入子代理 prompt

Orchestrator
   │ 显式传参（任务、约束、输出格式）
   ├─→ Research Agent ──返回 findings──┐
   │                                   ↓
   └─ 显式传参（findings 全文）────→ Synthesizer Agent
```

---

## 五、Do this, Not that（8 组对照）

| ✅ Do | ❌ Don't |
|-------|---------|
| 第 1 天读官方 Exam Guide PDF——里面有全部 6 个场景 | 没建过至少一个多代理系统就去报名 |
| 报名前先 build 真实的东西 | 把提示工程当成「写更好的提示」 |
| 6 个场景全部学习，虽然只考 4 个 | 低估 MCP 工具描述——常见丢分点 |
| 先故意挂一次官方模考 | 因为域5 只有 15% 就轻视它 |
| 复盘错答案**为什么**错，而不只是正确答案是什么 | 只刷课程、不做动手 build |
| 40% 时间投域1——最难也最大 | 试图一个周末突击——考判断，不考记忆 |
| 每题必答——未作答按错误计 | 依赖第三方题库 dump——Anthropic 官方材料才是 source of truth |
| 日历留足 2.5 小时——考试不能暂停 | 跳过 Anthropic Academy 课程走捷径 |

---

## 六、四周备考路径

| 周 | 主题 | 实际做了什么 |
|----|------|-------------|
| W1 | Foundation | 先通读官方 Exam Guide PDF（碰任何课程之前）；修完 Anthropic Academy 的 Claude 101 + AI Fluency Framework；build 一个带消息历史与流式的 Claude API 聊天应用。「主要是知道自己有多不知道」 |
| W2 | API + MCP | 旗舰课 Building with the Claude API（**8.1 小时，别跳**）；Introduction to MCP + MCP Advanced Topics；build 一个连接外部数据源的可用 MCP server；第一次真实架构决策——工具边界设计，先做错、再弄懂 |
| W3 | Agentic Systems + Claude Code | Introduction to Subagents + Introduction to Agent Skills；build 带错误处理与 fallback 循环的多代理工作流——「考试模式在这里 click」；Claude Code 开发者训练：真实项目上配 CLAUDE.md、hooks、自定义 slash commands；**二读 Exam Guide PDF**，感受完全不同 |
| W4 | Exam Prep | Skilljar 官方备考课；整卷模考——分数低于预期；逐题复盘到理解推理模式；claudecertifications.com 刷题；4 月 19 日考试 → 904 |

```
W1 基础            W2 API+MCP           W3 Agent+Code         W4 冲刺
├─ Exam Guide ①读  ├─ 旗舰课 8.1h        ├─ Subagents/Skills   ├─ 官方备考课
├─ Claude 101      ├─ MCP 入门 + 进阶     ├─ 多代理+fallback    ├─ 模考（先挂）
├─ AI Fluency      └─ build MCP server   └─ CLAUDE.md/hooks    ├─ 逐题复盘
└─ build API 聊天                            + slash cmds     └─ 考试 → 904
                                          Exam Guide ②读（W3）
```

**要点提炼**

- ✅ Exam Guide PDF 读两遍（Day 1 + Week 3 后），作者原话「第一天地毯式读，第三周后再读一次」
- ✅ 每周都有 build——「课程间的 build 让情景题感觉像真实决策，而非抽象选择题」
- ✅ 模考策略：先挂 → 逐题复盘 → 900+ 再约真考
- ❌ 别指望课程本身到 904——作者明说「光靠课程达不到」

---

## 七、Claude 生态系统地图（考点全景）

| 组件 | 是什么 | 考试相关性 |
|------|--------|-----------|
| 模型三档 | Opus（复杂推理/最高能力）、Sonnet（平衡/日常主力）、Haiku（最快/最省） | 按工作负载选模型是架构决策，直接进考题 |
| Claude API | 流式、tool use、JSON 模式结构化输出、prompt caching、Batch API | Batch API 成本优化出现在考题场景 |
| Claude Code | 终端编码代理：读代码库、写码、跑测试、管 Git；CLAUDE.md 是常驻「技术负责人」；hooks 拦截校验代理动作；`-p` 非交互模式 | **占权重 20%**（域2） |
| MCP | AI 连接外部工具/数据的新兴标准（「AI 界 USB-C」）；三原语 tools / resources / prompts | 域4；考 server 设计、client 配置、传输机制（Python/TS）、工具边界对下游推理质量的影响 |
| Agent SDK | 多代理框架：orchestrator-子代理编排、lifecycle hooks、会话状态 | 域1（27%）的主场，「考生赢输的地方」 |
| Projects & Artifacts | Projects 提供跨会话持久记忆与指令；Artifacts 生成独立交付物 | 非核心域，生态背景 |
| Anthropic Academy | Skilljar 上 13 门免费自学课程，直接映射考试领域 | 主学习平台，零付费墙 |

```
                    ┌───────────────────────────────┐
                    │         Claude 模型三档          │
                    │   Opus   /   Sonnet   /  Haiku  │
                    └───────────────┬───────────────┘
                                    │ API（流式 / tools / JSON / prompt cache / Batch）
              ┌─────────────────────┼─────────────────────┐
              ↓                     ↓                     ↓
     Claude Code（终端）       Agent SDK（多代理）      MCP（工具/数据总线）
     CLAUDE.md / hooks / -p    编排 / lifecycle hooks   tools·resources·prompts
     = 域2（20%）               = 域1（27%）            = 域4（18%）
```

---

## 八、报名与考试日

**七步流程**（作者 2026-04 口径，⚠️ 标注 2026-10 已变项）：

```
① 下载官方 Exam Guide PDF —— Day 1 就读，含全部 6 场景 / 任务陈述 / 示例
        ↓
② 修 Anthropic Academy 免费课（13 门；熟手从 Building with the Claude API 起步）
        ↓
③ 加入 Claude Partner Network（claude.com/partners，免费）
        ⚠️ 2026-09-30 起为报考前提，须以合作企业邮箱注册
        ↓
④ 报名前先 build：多代理工作流（含工具使用）+ 结构化输出 + 错误处理
        ↓
⑤ 报名（Skilljar access-request 页面）
        ⚠️ 文章口径 $99（前 5000 名合作伙伴员工免费）→ 现价 $125/次
        ↓
⑥ 官方模考：先故意挂 → 复盘每个错答案 → 复考目标 900+
        ↓
⑦ 监考考试：60 题 / 120 分钟，禁暂停，成绩数日内出
        ⚠️ 文章口径 ProctorFree → 现为 Pearson OnVUE 在线监考或 Pearson 考试中心
```

**考试日实战**（作者个案）：

- 2 分钟/题的节奏足够，慢下来的都是干扰项——错误答案被设计成「听起来像好工程」
- 标记 8 题回看，改了 2 题，剩 14 分钟交卷
- 成绩 36 小时到达：**904/1000**
- 分数拆解：域1 Agentic 架构 + 域2 Claude Code 最强；域5 上下文管理扣分——「域5 是谨慎考生捡回分心考生所丢分数的地方」

**重考与续期**（2026-10 现行规则，文章未提）：

- 重考全价；等待期 14 → 30 → 90 天递增，12 个月滚动最多 4 次
- 证书有效期 12 个月，到期前通过免费的开放书在线评估续期 12 个月；过期则重考

---

## 九、验证与勘误记录（2026-10-29 核验）

| 文章声称（2026-04 口径） | 验证结果 | 来源 |
|--------------------------|---------|------|
| CCA-F 2026-03-12 上线，Anthropic 首个官方技术认证 | ✅ | theaiarchitects / preporato / fru.dev 多源一致 |
| 60 题 / 120 分钟 / 720 及格 / 6 场景抽 4 / 监考闭卷 | ✅ 题型补充为单选 + 多选 | 同上 |
| 五域权重信息图 | ✅ 27/20/20/18/15，与官方 Exam Guide v1.0 及多名通过者一致 | theaiarchitects / farasat.me |
| 「超过一半的考试集中在两个领域」 | ❌ 数字口径有误：任两域最高 47%（27+20），前三域合计 67% | 权重图 + 官方 blueprint 推算 |
| 考试费 $99，首批 5000 名合作伙伴员工免费 | ⚠️ 已过时：2026-09 前后涨至 **$125**，免费窗口结束，仅限 Partner Network 成员（须合作企业邮箱） | fru.dev（2026-09-25 查证 $125）/ preporato（确认 $99→$125） |
| 监考方 ProctorFree | ⚠️ 已变更：现为 Pearson OnVUE 在线监考或 Pearson 考试中心 | preporato / fru.dev |
| 考试编码 CCA-F | ⚠️ 现更名 **CCAR-F**（与 CCAR-P 同系列命名） | preporato（2026-10 核实） |
| 成绩 36 小时（作者个案）/ 5-7 个工作日（文中步骤） | ❓ 各来源口径不一（另有 2 个工作日说法），以 Pearson 现行流程为准 | claudecodetutorials 等 |
| 后续将推 Sales / Developer / 高级架构师认证（2026 内） | ✅ 已落地：Associate $99 / Developer $125 / Architect-F $125 / Architect-P $175 | farasat.me |
| （文章未提及） | ➕ 有效期 12 个月 + 免费续期评估；英日双语；徽章经 Credly 发放 | fru.dev / preporato |
| 文末推广 647 题付费 Udemy 题库 | ⚠️ 利益相关声明：营销内容；本文备考方法论与资源清单不依赖该产品 | 原文文末 |

---

## 十、关键资源

**官方**

- [CCA-F 考试指南 PDF（直接下载）](https://everpath-course-content.s3-accelerate.amazonaws.com/instructor/8lsy243ftffjjy1cx9lm3o2bw/public/1773274827/Claude+Certified+Architect+%E2%80%93+Foundations+Certification+Exam+Guide.pdf) —— 全部 5 域任务陈述 + 6 场景 + 示例练习
- [Anthropic Academy（13 门免费课）](https://anthropic.skilljar.com/)
- [考试报名入口（access request）](https://anthropic.skilljar.com/claude-certified-architect-foundations-access-request)
- [Claude Partner Network（免费加入）](https://claude.com/partners)
- [Anthropic 文档（考试内容 source of truth）](https://docs.anthropic.com/)
- [anthropic-cookbook（GitHub 官方示例库）](https://github.com/anthropics/anthropic-cookbook)

**第三方（2026-10 时点核验源）**

- [theaiarchitects：5 Things I Learned Becoming Claude Certified（2026-09 通过）](https://theaiarchitects.com/blog/claude-certified-architect-exam)
- [preporato：CCA-F 完全指南（2026-10 更新）](https://preporato.com/blog/claude-certified-architect-complete-guide-2026)
- [farasat.me：四张 Claude 认证对比](https://farasat.me/blog/claude-certification)
- [claudecertifications.com（免费学习指南 + 25 练习题）](https://claudecertifications.com/)

---

## 相关笔记

- [[Claude Code Hooks - 确定性自动化防线]] —— 域2 考点：hooks 拦截与校验代理动作的实战配置
- [[新版MCP-Server开发指南-5分钟从零接入Claude]] —— 域4 考点：MCP server 接入实操

---
*文档生成时间：2026-10-29*
*基于 Santanu Majumdar LinkedIn 长文（约 2026-04），关键事实经 2026-10 多源核验*
