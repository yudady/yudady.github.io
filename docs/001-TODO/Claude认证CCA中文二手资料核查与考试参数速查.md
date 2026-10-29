---
title: Claude 认证 CCA — 中文二手资料核查与考试参数权威速查
aliases: ["CCA 勘误", "Claude Certified Architect 核查", "CCA-F 参数速查"]
tags:
  - claude
  - anthropic
  - certification
  - fact-check
  - status/active
  - type/doc
source:
  - "https://post.smzdm.com/p/al3556lp/"
  - "https://www.bilibili.com/video/BV1NmXPBHEmb"
  - "https://linkedin.com/pulse/claude-certified-architect-exam-cost-format-how-register-poorna-reddy-mlqrc"
  - "https://claudecertprep.com/overview"
  - "https://github.com/timothywarner-org/claude-architect/blob/main/docs/CERT-PROGRAM-BRIEFING.md"
  - "https://devoteam.com/expert-view/claude-certified-architect-foundations"
author: smzdm AIGC（原始来源：B 站 UP 主 Terminator-AI）；本篇为独立事实核查
created: 2026-10-29
updated: 2026-10-29
description: 以 smzdm AI 生成文章为样本逐条核查 CCA 考试参数——75 题/750 分/有效期 2 年均为错误口径，附 2026-10 官方权威速查表与 7 月后四认证体系全貌
level: beginner
stars: 3
note: 被核查的 smzdm 文章页面自标「内容由AI生成」，原始来源为 B 站视频（2026-03-27，认证发布两周后）。文章口径为 2026-04 时点，其后价格、监考方、认证体系均已变更；「德勤、埃森哲大规模认证」未找到独立证据。
---

# Claude 认证 CCA — 中文二手资料核查与考试参数权威速查

> Anthropic 首个官方技术认证 **Claude Certified Architect – Foundations**（2026-03-12 上线）在中文圈已出现多轮二手转述。本篇以一篇 smzdm AI 生成文章为样本逐条核对官方口径：**题数、及格线、有效期、价格四个硬参数全部错或过时**，且完全遗漏 2026-07 后的四认证体系扩展。适合用中文资料备考的人先过一遍本篇再入手，避免背错数字。

## 目录

- [[#一、来源链：AI 二手转述的典型样本]]
- [[#二、文章声称 vs 官方口径（核心勘误对照）]]
- [[#三、CCA 考试参数权威速查（2026-10 现行）]]
- [[#四、文章里有价值的部分：架构思维模式清单]]
- [[#五、AI 二手技术内容的核查方法]]
- [[#验证与勘误记录]]
- [[#相关笔记]]

---

## 一、来源链：AI 二手转述的典型样本

```
2026-03-12  Anthropic 发布 CCA-F（首个官方技术认证，Skilljar + ProctorFree 时代，$99）
     │
     ↓  +15 天
2026-03-27  B 站 UP 主 Terminator-AI 发布解读视频（BV1NmXPBHEmb）
     │
     ↓  +18 天（AI 转述）
2026-04-14  smzdm 发布 AIGC 文章（页面自标「内容由AI生成」）
     │        —— 本篇的核查对象
     ↓  其后官方侧连续变更（文章全部未覆盖）
2026-06-30  考试费 $99 → $125
2026-06 前后 考试编码更名 CCAR-F；交付平台迁 Pearson VUE（OnVUE/考试中心）
2026-07-13  认证体系扩展为 4 个（Associate / Developer / Architect-F / Architect-P）
持续        徽章经 Credly 发放；有效期 12 个月 + 免费续期评估
```

来源链每加一层，数字就漂移一次：官方 60 题 → B 站口播「75 题」→ smzdm 文章照抄；官方 720 分 → 转述成「750 分」；12 个月有效期 → 「2 年」。

---

## 二、文章声称 vs 官方口径（核心勘误对照）

| # | smzdm 文章声称 | 官方/多源核验口径 | 判定 |
|---|---------------|------------------|------|
| 1 | 75 道选择题 | **60 题**（单选 + 多选），四独立来源一致 | ❌ |
| 2 | 及格标准 750/1000 分 | **720/1000**（scaled 100–1000） | ❌ |
| 3 | 证书有效期 2 年 | **12 个月**，到期前通过免费免监考评估续期 | ❌ |
| 4 | 前 5000 名 Partner 员工免费，后续恢复 99 美元 | 前 5000 名免费 ✅；但 **2026-06-30 已涨至 $125**，「恢复 $99」永远不会发生 | ⚠️ 过时 |
| 5 | 在线监考 | 文章时点（Skilljar/ProctorFree）成立；现为 **Pearson VUE**（OnVUE 在线或考试中心） | ⚠️ 已变更 |
| 6 | 先申请 Partner Network 再注册考试，公司名义 | ✅ 基本准确：注册须 Partner Network 成员企业**域名邮箱**，个人邮箱无效；Partner Network 本身免费加入（Registered 层） | ✅ |
| 7 | Agentic 架构 27%、Claude Code 20% | ✅ 与官方 blueprint 一致（五域 27/20/20/18/15） | ✅ |
| 8 | 6 个生产场景随机抽 4 个 | ✅ 一致（客户支持/代码生成/多代理研究/CI-CD/结构化抽取/长文档分析） | ✅ |
| 9 | 建议至少 6 个月生产经验 | ✅ 一致（Claude API / Agent SDK / Claude Code / MCP） | ✅ |
| 10 | 德勤、埃森哲已启动大规模员工认证、数万人认证 | 未找到独立证据；可查证的咨询机构案例是 Devoteam（员工发布考经） | ❓ |
| 11 | 只考 CCA Foundations 一门 | 遗漏重大更新：**2026-07-13 起共 4 个认证** | ⚠️ 遗漏 |

> 判定口径：❌ 与官方冲突；⚠️ 文章时点成立但已过时/遗漏；❓ 未能独立证实。这正是 skill 中「时效勘误」与「事实错误」分开标注的实践：第 4/5 条不怪文章（其时点属实），第 1/2/3 条则是转述链的真实失真。

---

## 三、CCA 考试参数权威速查（2026-10 现行）

### CCAR-F（Architect – Foundations）硬参数

| 参数 | 现行口径 | 备注 |
|------|---------|------|
| 题量 / 题型 | **60 题**，单选 + 多选 | 情景题，约 15 题/场景 |
| 时长 | 120 分钟（建议预留 135 分钟坐席） | 不能暂停 |
| 及格线 | **720 / 1000**（scaled） | 答错不倒扣，每题必答 |
| 场景机制 | 题库 6 场景随机抽 4 | 全部场景都要学 |
| 费用 | **$125**（2026-06-30 起） | Select/Preferred 层伙伴 5 折，Global Premier 层 2026 年内免费 |
| 资格 | Claude Partner Network 成员企业邮箱 | 个人邮箱不可注册 |
| 有效期 | **12 个月**，免费在线评估续期 | 过期须重考 |
| 语言 | 英语、日语 | 16 语言只是社区材料，不是考试语言 |
| 交付 | Pearson VUE（OnVUE 在线 / 考试中心） | 徽章走 Credly |
| 编码 | 现名 **CCAR-F** | 文章与早期资料中的 CCA-F 为旧称 |

### 四认证体系全貌（2026-07-13 起，文章完全遗漏）

| 认证 | 编码 | 面向 | 费用 | 题量 |
|------|------|------|------|------|
| Claude Certified Associate | CCAO-F | 非技术角色（运营/PM） | $99 | 60 |
| Claude Certified Developer | CCDV-F | 开发者（API/SDK/MCP 集成） | $125 | — |
| Claude Certified Architect – Foundations | CCAR-F | 架构师/高级开发（多代理设计） | $125 | 60 |
| Claude Certified Architect – Professional | CCAR-P | 技术主管/企业架构师 | $175 | 63 |

```
选哪张证？（按角色）
你是谁？
├─ 用 Claude 提效的非技术岗 ─────────→ CCAO-F（$99）
├─ 写代码集成 API/SDK/MCP ──────────→ CCDV-F（$125）
├─ 设计生产级多代理系统（多数工程师）──→ CCAR-F（$125）★ 本篇主角
└─ 主导企业级规模化部署/治理 ────────→ CCAR-P（$175，建议 3 年+ 架构经验）
```

详细备考路径、五域考点拆解、一手考经 → [[Claude认证架构师CCA-F考试攻略]]；开源中文学习材料 → [[claude-certified-architect - CCA-F 认证备考资料库]]。

---

## 四、文章里有价值的部分：架构思维模式清单

文章虽然数字失真，但「架构思维模式对比」一节的内容与官方五域考点吻合，质量高于其参数部分，收录并映射到考试领域：

**系统设计优先（✅ 正确直觉）**

| 原则 | 对应考试域 |
|------|-----------|
| 提示词仅是建议，代码才是法律——强制执行必须通过代码实现（schema/hooks/校验） | 域3 提示工程 20% |
| 协调器生成子代理后，必须显式传递完整上下文（无共享内存） | 域1 Agentic 架构 27% |
| 工具描述需具体、精确、无歧义，确保路由准确 | 域4 MCP 18% |
| 关键信息置于上下文开头和结尾，避免埋在中间 | 域5 上下文管理 15% |
| 按延迟需求选 API：实时看响应速度，后台任务用 Batch API | 域3/域5 |

**常见错误认知（❌ 考试反模式）**

| 反模式 | 为什么错 |
|--------|---------|
| 把只读数据也封装为 Tool | 只读内容走 resource/context 注入，不需要 LLM 主动调用 |
| 用 LLM 自评做业务分流 | 缺确定性——分流用程序化规则 |
| CI/CD 流水线缺检查点 | Claude Code 集成须有校验关卡（另：CI 中必须 `-p` 非交互模式） |
| 仅靠 Prompt 强制结构化输出 | 须 JSON schema + 校验 + 重试的程序约束 |
| 单 Agent 塞过多工具 | 职责膨胀导致路由错误——按域拆子代理 |

> 这一节恰好是认证考查的核心哲学：**程序化约束 > 提示词约束**。文章把它转述对了，说明 B 站原视频的技术内容大体可靠，失真集中在数字参数——这也符合二手转述的一般规律：定性内容存活率高，定量内容最易漂移。

---

## 五、AI 二手技术内容的核查方法

从本篇案例提炼的可复用清单：

```
拿到一篇 AI 生成技术文章
        │
        ▼
① 找原始来源链（页面「参考来源」/描述链接）
   本篇：smzdm ← B 站视频 ← 官方 03-12 发布
        │
        ▼
② 硬参数逐条核对结构化来源
   考试参数 → 官方 exam guide / 报名页
   npm 包   → registry JSON（非 npm HTML 页）
   GitHub   → api.github.com（非搜索页）
        │
        ▼
③ 区分「错误」与「过时」
   数字冲突 = 转述失真（❌）
   文章时点对、现已变 = 过时（⚠️，注明变更日期）
        │
        ▼
④ 时效敏感字段重点查变更史
   价格/监考方/认证体系在半年内连变三次
   —— 认证类内容的保质期以月计
```

✅ **必查**：题数/分数/价格/有效期等硬数字；认证清单是否已扩展；平台是否迁移
❌ **别信**：AI 转述链上的任何具体数字（不经核对）；「数万人」「大厂都在考」类规模声称（本篇的德勤/埃森哲即无法证实）
⚠️ **注意**：文章标注「内容由AI生成」不等于全错——定性方法论部分（本篇第四节）存活率较高，逐条核查后仍可收录

---

## 验证与勘误记录

| 被核查声称（smzdm 文章 / 2026-04-14） | 验证结果 | 来源 |
|--------------------------------------|---------|------|
| 75 道选择题 | ❌ 实为 **60 题**（单选+多选） | timothywarner CERT-PROGRAM-BRIEFING / Matthew Purcell LinkedIn（60 题、4 场景×15）/ Poorna Reddy / claudecertprep 四源一致 |
| 及格标准 750/1000 | ❌ 实为 **720/1000**（scaled） | 同上三源 + claudecertprep |
| 证书有效期 2 年 | ❌ 实为 **12 个月** + 免费免监考续期评估 | Poorna Reddy（Valid for 12 months） |
| 前 5000 名免费，后续恢复 $99 | ⚠️ 前半 ✅；后半过时——**2026-06-30 起 $125** | Poorna Reddy（明确涨价日 30 June 2026）；前 5000 免费：medium 步骤指南 |
| 在线监考（未提平台） | ⚠️ 文章时点为 Skilljar/ProctorFree；现 **Pearson VUE**（OnVUE/考试中心，英语日语） | Poorna Reddy / Matthew Purcell（pre-Pearson era 说法） |
| Partner Network 前置 + 公司名义申请 | ✅ 基本准确，精确口径：成员企业域名邮箱注册，个人邮箱无效；Partner Network 免费加入（Registered 层） | Poorna Reddy / medium 步骤指南 |
| Agentic 27% / Claude Code 20% | ✅ 与官方五域权重（27/20/20/18/15）一致 | claudecertprep / timothywarner |
| 6 场景抽 4 | ✅ 场景清单六项亦与官方一致 | timothywarner（S1-S6 全列） |
| 6 个月生产经验建议 | ✅ | claudecertprep（ideal candidate 条目） |
| 德勤、埃森哲大规模员工认证 | ❓ 未找到独立证据；可证案例仅 Devoteam 员工考经 | 本篇 2026-10-29 检索 |
| 只有一门 CCA Foundations | ⚠️ 遗漏：2026-07-13 起四认证体系（Associate/Developer/Architect-F/Architect-P，$99/$125/$125/$175） | Devoteam / Poorna Reddy |
| 考试名 CCA Foundations | ⚠️ 编码现名 **CCAR-F** | Poorna Reddy / claudecertifiedarchitects.com |
| 对旧笔记的修正 | ➕ 本篇将 [[Claude认证架构师CCA-F考试攻略]] 勘误表中「2026-09 前后涨价」口径修正为 **2026-06-30**（Poorna Reddy 给出明确日期） | 本篇核验 |

**参照快照声明**：本篇所有「现行口径」均为 2026-10-29 查询值；认证类信息变更频繁，引用时建议到 Anthropic Partner Academy 复核当日参数。

---

## 相关笔记

- [[Claude认证架构师CCA-F考试攻略]] —— 一手考经视角：五域备考路径、高频踩坑点、四周计划（同主题，角度互补）
- [[claude-certified-architect - CCA-F 认证备考资料库]] —— 开源备考仓库：16 语言学习指南 + 60 题模拟考 + Anki 卡组
- [[Claude认证CCAR-F报名落地页核查]] —— JR Academy 落地页核查：报名准入精确口径、考试语言漏报日语勘误、重考冷却期数字

---

*文档生成时间：2026-10-29*
*基于 smzdm AIGC 文章（2026-04-14）逐条核查，对照 2026-10 官方口径与四个独立英文来源*
