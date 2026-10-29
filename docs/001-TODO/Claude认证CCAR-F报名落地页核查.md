---
title: Claude 认证 CCAR-F 报名落地页核查 — JR Academy 产品页拆解与官方口径对照
aliases: ["JR Academy CCAR-F", "CCAR-F 报名", "jiangren CCAR-F 落地页", "CCAR-F 备考产品页"]
tags:
  - claude
  - anthropic
  - certification
  - fact-check
  - status/active
  - type/doc
source:
  - "https://jiangren.com.au/certifications/ccar-f"
  - "https://anthropic-partners.skilljar.com/page/faq-certifications"
  - "https://everpath-course-content.s3-accelerate.amazonaws.com/instructor%2F6nizmqk8tpzpfjvt6qmmav7rh%2Fpublic%2F1783542750%2FClaude+Certified+Architect+%E2%80%93+Foundations+Exam+Guide.pdf"
author: JR Academy / 匠人学院（页面）；本篇为独立事实核查
created: 2026-10-29
updated: 2026-10-29
description: JR Academy CCAR-F 认证落地页拆解——负面考纲清单、重考冷却期数字、CCAR-P 分流与备考时长基线逐条对官方核验；页面题库数字 1465 vs 725 自相矛盾，考试语言漏报日语。
level: beginner
stars: 3
note: JR Academy 为澳洲付费备考机构，页面产品数字（题库量、课程时长、价格）为自报口径且内部不一致（题库 1465 vs 725）；官方事实逐条对照 Skilljar FAQ 与 CCAR-F Exam Guide v1.0 核验（2026-10-29）。source 已剥除 utm 跟踪参数。
---

# Claude 认证 CCAR-F 报名落地页核查

> JR Academy（澳洲匠人学院）的 CCAR-F 认证产品落地页——本 vault 已拆过它两支视频（[[Claude官方架构师认证CCAR-F备考体系拆解|6 分钟拆解]]、[[Claude官方四认证选型与应试机制答疑会拆解|105 分钟答疑会]]），这篇补上第三块拼图：**报名口径与产品页本身**。页面的独有增量是三组视频里没有的硬信息：官方负面考纲清单（别在哪花时间）、重考冷却期完整数字、CCAR-F→CCAR-P 分流量化与备考时长基线；同时页面也有一处自相矛盾（题库 1465 vs 725）与一处漏报（考试语言漏日语）。全部声称逐条对照官方核验。

## 目录

1. [[#一、这页是什么：产品页结构地图]]
2. [[#二、考试档案逐格核对]]
3. [[#三、独有增量 ①：负面考纲清单（备考止损）]]
4. [[#四、独有增量 ②：重考冷却期与应试数字]]
5. [[#五、独有增量 ③：CCAR-P 分流与备考时长基线]]
6. [[#六、JR 产品数字核查：题库量自相矛盾]]
7. [[#七、本 vault 认证笔记分工地图]]
8. [[#八、验证与勘误记录（2026-10-29 核验）]]
9. [[#参考资料]] / [[#相关笔记]]

---

## 一、这页是什么：产品页结构地图

```text
jiangren.com.au/certifications/ccar-f（canonical，已剥 utm 参数）
│
├─ 01 先了解这张认证
│   ├─ 考什么 / 五域权重（27/20/20/18/15）      ← 官方口径 ✅（与 Exam Guide 一致）
│   ├─ 「明确不考的东西」负面清单               ← 页面独有，官方无此显式清单
│   ├─ 报名说明（Partner Academy 准入墙）
│   └─ 考试档案 / 技能清单 / 适考人群 / 前置要求
│
├─ 02 考试会考什么 —— 五域逐域考点展开（对齐官方 blueprint）
│
├─ 03 备考（JR 产品区 —— 自报口径，非官方）
│   ├─ 14 章节学习路径（嵌入官方免费课 + 自研章节）
│   ├─ 备考时长：50h / 2-3 周 / 4-6 周
│   ├─ 备考支持：A$199 单购 · 题库 · 模拟考
│   └─ 做题技巧与误区提醒（通用应试建议）
│
├─ 04 FAQ（报名前你可能想问）—— 全页信息密度最高的一节
│   ├─ 及格线 720 量表分解释 ✅
│   ├─ 「你自己报不了名」准入机制
│   ├─ 考试费不含在课程里 ✅
│   ├─ 有效期 12 个月 / 续证 / 重考冷却期 ✅
│   └─ CCAR-F vs CCAR-P 无先后顺序 ✅
│
├─ 05 学过的人怎么说 —— 尚无评价（产品上线早期的旁证）
│
└─ 06 相关认证卡片（CCAO-F / CCDV-F / AI-900 / AWS AIF-C01）
```

页面定位：**视频引流（YouTube utm：ccar_f_deep_dive）→ 落地页转化 → A$199 备考产品**。免费可见的信息本身就是一份结构不错的中文 CCAR-F 摘要；付费部分（题库、模拟考、精析）与官方无关联。

---

## 二、考试档案逐格核对

页面「考试档案」区的每一格对照官方口径：

| 页面档案 | 页面口径 | 官方核验 | 判定 |
|---------|---------|---------|------|
| 考试代码 | CCAR-F | Exam Guide 封面「Exam code: CCAR-F」 | ✅ |
| 发证机构 | Anthropic | — | ✅ |
| 题型 | 单选 / 多选（题干标明选几项） | Exam Guide §3：each item states how many responses to select | ✅ |
| 考试语言 | **English** | 官方 FAQ 原文：「The exam and prep content are available in **English and Japanese**」 | ⚠️ 漏报日语 |
| 证书有效期 | 1 年 | 12 个月 + 免费免监考续期评估 | ✅ |
| 正式考试题量 | 60 | Exam Guide §3：60 items | ✅ |
| 考试时长 | 120 min | Exam Guide §3（建议预留 135 min 坐席） | ✅ |
| 及格分 | 720/1000 | scaled 100–1000，standard-setting 定线 | ✅ |
| 官方认证说明外链 | Skilljar prep courses 页 | 真实官方页（本次抓取确认存在） | ✅ |

档案区八格对七格，唯一失真是**语言漏报日语**——对中文考生反而是个有用选项：官方 FAQ 连「已约英文场次改约日文场次」的操作流程都写了（取消原场 + 重约日文版，需提前 48 小时）。日文翻译质量对长题干情境题无第三方验证，主流路径仍是英文，但选项存在这件事页面没说。

---

## 三、独有增量 ①：负面考纲清单（备考止损）

页面「明确不考的东西」——这是 JR 两支视频都没讲、官方 Exam Guide 也没写成显式清单的内容：

| 明确不考 | 备考止损含义 |
|---------|-------------|
| 微调与模型训练 | 不要去读 fine-tuning 文档 |
| embedding 与向量库 | RAG / 向量检索不是这门考纲的词 |
| computer use | 浏览器/桌面操作类 Agent 不在范围 |
| 视觉分析 | 多模态图像理解不考 |
| streaming / SSE | 传输层细节不考 |
| 限流配额与价格计算 | 不用背价格表和 rate limit 数字 |
| MCP server 部署与基础设施 | 考 MCP **工具设计**，不考运维部署 |
| Claude 内部架构与权重 | 不考模型本身（参数、训练、MoE 等） |

**核验说明**：官方 Exam Guide v1.0 **没有**显式的「out of scope」清单——考纲是正向定义（5 域 / 30 条 Task Statement / 6 场景）。本篇对这份负面清单做了反向核验：所列关键词（fine-tuning / embedding / vector / computer use / streaming / SSE / rate limit / 内部架构）在 Exam Guide 全文中**零实际命中**（仅样题语境的无关词形）。判定 ✅——这是一份与官方 blueprint 相容的反推清单，方向可信。

```text
备考时间分配决策（负面清单的用法）
├─ 在官方 30 条 Task Statement 里？
│   ├── 是 → 正面强攻（五域权重：D1 Agentic 27% 优先）
│   └── 否 → 对照负面清单
│         ├── 在清单上（fine-tune/embedding/computer use…）→ 直接跳过
│         └── 不在清单也不在 Task Statement → 低优先级扫一眼即可
```

---

## 四、独有增量 ②：重考冷却期与应试数字

页面 FAQ 给出了完整的重考数字（与 [[Claude官方四认证选型与应试机制答疑会拆解]] 已核验的官方口径完全一致）：

| 规则 | 数字 | 官方核验 |
|------|------|---------|
| 第 1 次未过 → 等待 | **14 天** | ✅ Skilljar FAQ |
| 第 2 次未过 → 等待 | **30 天** | ✅ |
| 第 3 次未过 → 等待 | **90 天** | ✅ |
| 尝试上限 | 滚动 12 个月内最多 **4 次** | ✅ |
| 重考费用 | **每次全价**（无打包折扣） | ✅ |
| 限制粒度 | **按单门计算**——CCAR-F 没过不影响报其他三门 | ✅ |

页面没讲、但本次对照官方 FAQ 顺带坐实的报名期机制（应试机制主场见答疑会笔记，这里只记增量）：

```text
报名后容易踩的时间坑（官方 FAQ 增量）
├─ 注册后 5 年内随时可约考（付费即锁定价）
├─ 改期/取消：考前 48 小时截止，逾期或 No-show → 考试费没收
├─ 公司邮箱域名不在 Partner 名录 → 发邮件加域，7-10 天生效
│   （报名前先查域名，别卡在临考）
├─ 申诉：结果通知后 14 天内，向 Pearson 提交
├─ 无障碍 accommodATIon：考前 10 天以上申请
└─ 闭卷的完整含义：禁笔记/文档/AI 助手，浏览器翻译工具也不行
```

**报名流程的精确口径**（页面 FAQ 的「由伙伴在后台为你开通账号」措辞不准）：官方机制不是有人「帮你开户」，而是——你本人拿**公司域名邮箱**登录 Partner Academy 自助注册；域名不在组织 Partner 记录上时，由管理员发邮件给 partner-support 加域（7-10 天）。方向对、机制描述粗。

---

## 五、独有增量 ③：CCAR-P 分流与备考时长基线

**无先后顺序** ✅：页面 FAQ「官方明确不要求先持 Foundations 才能考 Professional」——与官方 FAQ 原文完全一致（"no formal prerequisite, so you can take Professional without holding Foundations"）。四门认证互不设前置。

**分流量化** ❓：页面声称「RAG、合规、干系人沟通这些词在 CCAR-F 考纲里一次都没出现，在 CCAR-P 里合计超过 40% 权重」。前半句与本篇负面清单核验一致（CCAR-F guide 确无 RAG/合规字样）✅；「CCAR-P 超 40%」无法独立核验——CCAR-P 的新版 Exam Guide 要 2026-11-17 才发布，届时可对账。暂按 JR 口径存疑。

**备考时长基线**（JR 自报产品口径，非官方）：

| 起点 | 页面口径 | 交叉参照 |
|------|---------|---------|
| 课程总学习时长 | 50h | JR 产品数字 |
| 有相关实操经验 | 2-3 周 | [[Claude认证架构师CCA-F考试攻略|904 分通过者]]口径：通过者普遍 2-4 周专注准备 ✅ 相容 |
| 从基础开始 | 4-6 周 | — |
| 建议日投入 | 1-2h | — |

页面把官方免费课嵌进了它的 14 章节路径——这部分官方课（AI Fluency 120min、Claude 101 60min、Building with the Claude API 540min、Intro to MCP 180min）本身免费，**不买 JR 产品也能直接去 Anthropic Academy 上**。课程时长为 JR 标注口径。

---

## 六、JR 产品数字核查：题库量自相矛盾

页面内部对自家题库给了两个不同的数：

```text
2026-07（拆解视频时期）   「近 480 道原创英文练习题」      ← 旧笔记已录
        ↓ 扩容
2026-10（本页·备考支持区） 「1465 道题库」                 ← 数字 A
2026-10（本页·FAQ 区）    「题库共 725 道」               ← 数字 B
                              ↑ 同一页面自相矛盾 ⚠️
```

| 页面声称 | 核验判定 |
|---------|---------|
| 题库 1465 道（备考支持区） | ⚠️ 与同页 FAQ 的 725 道矛盾，无法确认哪个是现值 |
| 题库 725 道（FAQ 区） | ⚠️ 同上；另注意「全部围绕官方五域 blueprint 和 30 条 task statement 编排」是产品宣称，官方练习题库已在迁 Pearson 时退役，第三方题库与真题无一致性保证 |
| A$199 单购备考内容；Plus/Premium 会员期内可用 | JR 定价，自报 |
| 账号开通服务、私人辅导、官方考试费另计 | ✅ 与官方「考试费自行向 Anthropic 支付」一致 |
| 模拟考按官方权重 27/18/20/20/15 出卷 | ⚠️ 权重数字本身 ✅，但注意官方编号顺序是 27/20/20/18/15 的 D1/D3/D4/D2/D5——此处按权重降序书写，无实质错误 |
| 每题解析点名另外三个选项错在哪 | 产品宣称，无法核验 |

> [!warning] 利益相关声明
> 本页是付费备考产品的销售落地页（utm 源头为其 YouTube 引流视频）。页面引用的官方参数经核验基本准确，但产品数字（题库量、时长、效果承诺）为自报口径，且评价区显示尚无用户评价（2026-10-29）。报班决策请先看完 [[Claude官方架构师认证CCAR-F备考体系拆解]] 第六节与 [[Claude官方四认证选型与应试机制答疑会拆解]] 第七节的免费资源对照表。

---

## 七、本 vault 认证笔记分工地图

```text
你要解决什么问题？
├── 中文二手资料说 75 题/750 分/2 年有效，对吗？
│     └── [[Claude认证CCA中文二手资料核查与考试参数速查]]（勘误 + 权威参数表）
├── 考纲到底考什么、场景题怎么解？
│     └── [[Claude官方架构师认证CCAR-F备考体系拆解]]（blueprint 逐域 + 解题方法论）
├── 四张证书选哪张、报名/监考/重考全流程？
│     └── [[Claude官方四认证选型与应试机制答疑会拆解]]（机制全景）
├── 通过者的实战经验、四周怎么排？
│     └── [[Claude认证架构师CCA-F考试攻略]]（904/1000 一手考经）
├── 免费的中文学习材料、模拟考场？
│     └── [[claude-certified-architect - CCA-F 认证备考资料库]]（开源仓库）
└── 报名页说了什么、哪些能信？ ← 本篇（落地页核查 + 报名口径）
```

| 笔记 | 角度 | 不可替代的增量 |
|------|------|---------------|
| 参数速查（勘误篇） | 事实核查方法论 | 硬参数权威速查表 + 转述链失真分析 |
| 备考体系拆解 | 考纲结构 | 30 项 Task Statement、6 官方场景、硬约束 vs 概率引导 |
| 答疑会拆解 | 机制全景 | 四认证选型决策树、Partner Network / Frontier Academy 生态 |
| CCA-F 考试攻略 | 一手经验 | 五个高频踩坑点、Do/Not 对照、考试日实战 |
| **本篇** | **产品页核查** | **负面考纲清单、重考冷却期完整数字、报名准入精确口径、JR 产品数字矛盾** |

---

## 八、验证与勘误记录（2026-10-29 核验）

| 页面声称 | 验证结果 | 来源 |
|---------|---------|------|
| 60 题 / 120 min / 720 量表分 / 6 抽 4 场景 / 单选+多选标注选几项 | ✅ 与 Exam Guide v1.0 §3 一致 | Exam Guide / 既有三篇已核验 |
| 五域权重 27/20/20/18/15 | ✅ | Exam Guide §4 |
| 考试语言 English | ⚠️ **漏报日语**——官方 FAQ 原文 English and Japanese（含英→日改约流程） | Skilljar FAQ |
| 证书有效期 1 年、免费非监考续证、过期全价重考、重大变更可要求重考 | ✅ | Skilljar FAQ / 速查笔记 |
| 考试费不含在任何课程/会员价格内 | ✅ 与官方一致（Partner Academy 结账自行支付，结账价反映 partner tier 折扣） | Skilljar FAQ / 速查笔记 |
| 「Anthropic Partner Academy 不对个人开放自助注册，需由伙伴在后台为你开通账号」 | ⚠️ 方向对、机制粗：实为本人以**公司域名邮箱**自助注册；域名不在 Partner 记录需发邮件加域（7-10 天生效），不存在「后台帮你开户」步骤 | Skilljar FAQ |
| 重考冷却期 14/30/90 天、12 个月 4 次、每次全价、按单门计 | ✅ 与官方口径一致 | Skilljar FAQ（答疑会笔记已先行核验） |
| CCAR-F 与 CCAR-P 无先后顺序 | ✅ 官方 FAQ 原文 no formal prerequisite | Skilljar FAQ |
| 「RAG/合规/干系人在 CCAR-P 合计超 40% 权重」 | ❓ JR 口径；CCAR-P 新版 guide 2026-11-17 发布后可对账 | 本篇检索 |
| 负面考纲清单（微调/embedding/computer use/streaming/限流价格/MCP 部署/内部架构） | ✅ 与官方 blueprint 相容的反推清单——所列关键词在 Exam Guide 全文零实际命中；官方无显式 out-of-scope 清单 | Exam Guide v1.0 反向核验 |
| 官方样题反复出现 "most effective first step" / "most likely root cause" | ✅ 与 Exam Guide 样题及四类干扰项描述一致 | Exam Guide §样题 |
| 题库 1465 道（备考支持区）vs 725 道（FAQ 区） | ⚠️ 页面内部矛盾；视频期（2026-07）为「近 480 题」——产品数字自报且频繁变动 | 本页两处对照 / 备考体系拆解笔记 |
| CCDV-F 53 题（相关认证卡片） | ❓ JR 口径；官方 FAQ 未列各证书题量，速查笔记该格亦为空缺 | 本篇检索 |
| 备考时长 50h / 2-3 周（有经验）/ 4-6 周（零基础） | ⚠️ JR 自报；「2-3 周」与 904 分通过者的「2-4 周」口径相容 | 本页 / 考试攻略笔记 |
| 「及格成绩按 domain 的答对百分比只用于诊断，不存在每域过线」 | ✅ 与官方 scaled scoring 解释一致 | Exam Guide / 答疑会笔记 |
| CCAO-F 60 题、AI-900 50 题 45 min、AWS AIF-C01 65 题 90 min（导航卡片） | 未核验（非本篇范围，第三方考试参数） | — |

**参照快照声明**：所有「现行口径」为 2026-10-29 查询值。认证类信息变更频繁（半年内价格/监考方/体系已三变），报名前请到 Anthropic Partner Academy 复核当日参数。

---

## 参考资料

- [JR Academy：Claude 架构师 Foundations 认证指南（本篇核查对象）](https://jiangren.com.au/certifications/ccar-f)
- [Anthropic Partner Academy：Certifications FAQ（语言/报名/重考/改期一手口径）](https://anthropic-partners.skilljar.com/page/faq-certifications)
- [Claude Certified Architect – Foundations Exam Guide v1.0（官方 PDF）](https://everpath-course-content.s3-accelerate.amazonaws.com/instructor%2F6nizmqk8tpzpfjvt6qmmav7rh%2Fpublic%2F1783542750%2FClaude+Certified+Architect+%E2%80%93+Foundations+Exam+Guide.pdf)
- [Anthropic Partner Academy：CCAR-F Prep Courses](https://anthropic-partners.skilljar.com/page/claude-certified-architect-foundations-prep-courses)

## 相关笔记

- [[Claude官方架构师认证CCAR-F备考体系拆解]] —— 同频道视频拆解：blueprint 逐域 + 解题方法论（本篇是它的产品页续篇）
- [[Claude官方四认证选型与应试机制答疑会拆解]] —— 报名/监考/重考机制全景（本次 FAQ 语言条目已就地修正为英/日双语 ✅）
- [[Claude认证CCA中文二手资料核查与考试参数速查]] —— 硬参数权威速查与中文二手资料勘误方法论
- [[Claude认证架构师CCA-F考试攻略]] —— 904/1000 通过者一手考经（备考时长口径互参）
- [[claude-certified-architect - CCA-F 认证备考资料库]] —— 社区开源免费备考资源

---

*文档生成时间：2026-10-29*
*基于 JR Academy CCAR-F 落地页逐节核查，对照 Skilljar FAQ 与 Exam Guide v1.0（2026-10-29 快照）*
