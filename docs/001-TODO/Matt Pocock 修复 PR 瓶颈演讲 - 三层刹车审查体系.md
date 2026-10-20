---
title: Matt Pocock 修复 PR 瓶颈演讲 - 三层刹车审查体系
aliases: ["Fixing the PR Bottleneck", "Matt Pocock AI Engineer Paris 演讲重点", "Slop Cannon 防御指南"]
tags:
  - ai-coding
  - code-review
  - mattpocock-skills
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=xPEr7P45rK0"
  - "https://www.youtube.com/watch?v=LlgiOCmFG_w"
  - "https://aihero.dev/skills"
  - "https://github.com/mattpocock/skills"
author: Matt Pocock（原演讲）/ MUKI WU 哈啰沐奇（导读视频）
created: 2026-10-20
updated: 2026-10-20
description: Matt Pocock 在 AI Engineer Paris 2026 的 Fixing the PR Bottleneck 演讲导读：软件工厂时代用自动检查、AI 审查、人工审查三层刹车防御 Slop Cannon，深模块防作弊测试、规范写审分离、单双向门分级与 retro 复利循环
level: intermediate
stars: 5
note: 导读视频性质——MUKI 影片是 Matt Pocock 原演讲（AI Engineer Paris 2026）的中文摘要；本笔记以原演讲英文字幕与 mattpocock/skills 仓库源码交叉验证后撰写
---

# Matt Pocock 修复 PR 瓶颈演讲 - 三层刹车审查体系

> AI 写代码的速度远超人类审查速度，PR 队列塞车是软件工厂时代的核心瓶颈。Matt Pocock（Total TypeScript / AI Hero 作者，mattpocock/skills 仓库 29.7 万星）在 AI Engineer Paris 2026 提出「三层刹车」防御体系：让底下两层（自动检查 + AI 审查）做得越扎实，顶层的人类工程师越轻松。本笔记基于导读视频 + 原演讲英文字幕 + 仓库源码三方交叉验证。

## 目录

- [[#讲者与背景：29 万星 skills 仓库]]
- [[#核心痛点：软件工厂只有油门]]
- [[#三层刹车模型]]
- [[#重点一：测试会撒谎（Checks Can Lie）]]
- [[#重点二：深模块（Deep Module）]]
- [[#重点三：写审分离，规范只给审查 AI 读]]
- [[#重点四：人类审查加速器 - 单向门与双向门]]
- [[#重点五：retro 复利循环]]
- [[#三步落地行动清单]]
- [[#验证与勘误记录]]

---

## 讲者与背景：29 万星 skills 仓库

Matt Pocock：TypeScript 圈知名教育者，Total TypeScript 课程作者，曾任职 Vercel 与 Stately（GitHub bio 原文 Ex-Vercel, Stately），现经营 AI Hero（aihero.dev）。GitHub followers 51,152（2026-10-20 查询）。

2026-02-03 他把自己在用的 AI skills 整理成 `mattpocock/skills` 仓库，8 个月涨到近 30 万星：

```
2026-02-03 ──────── 仓库创建
      │
2026-04 底 ──────── 约 4.5 万星（视频口径，无独立出处）
      │
2026-05 中 ──────── 约 7.6 万星（视频口径，无独立出处）
      │
2026-09-25 ──────── AI Engineer Paris 演讲「Fixing the PR Bottleneck」
      │
2026-10-05 ──────── v1.3 发布（implement-spec / pr / retro 转正 + CONTEXT.md → GLOSSARY.md）
      │
2026-10-20 ──────── 297,596 星 / 24,988 fork（GitHub API 实查）
```

仓库定位：「Skills for Real Engineers. Straight from my .agents directory.」MIT 协议，27 个 skill，按使用时机分 6 组（Getting Started / Main Flow / Shaping / Engineering / Productivity / Reference）。

```bash
# 安装（二选一）
npx skills@latest add mattpocock/skills     # 任意 agent（Claude Code / Cursor / Codex / Copilot）
claude plugins install mattpocock-skills    # Claude Code 官方插件市场，自动更新
```

---

## 核心痛点：软件工厂只有油门

核心概念：软件工厂（software factory）——工作的发起方不再只有人，而是由确定性代码 / agent 自动触发。

Matt 给的油门例子（演讲原细节，导读视频略去）：

```
油门（自动触发，只加速不把关）
  错误上报 → 分类 agent（演讲举例 classifier「Jev」）→ 自动转 fix / repro
  PlanetScale 慢查询报告 → 自动触发优化任务
  更多后台 agent 自动开 PR
        │
        ▼  只踩油门的结果
  Slop Cannon（烂代码加农炮）
  大量没法改、没法审、连看都不想看的 PR 塞爆审查队列
        │
        ▼  更深的毒性
  烂代码污染代码库 = 毒化 agent 的工作环境
  「code is the environment your agent operates in」→ 更多烂代码
```

关键转念：瓶颈不在生成速度，在验证与吞吐。所以解法不是更快地写，而是**主动安装刹车**——放慢速度、拉高品质。

---

## 三层刹车模型

三道刹车叠成一块蛋糕，每层挡掉不同性质的烂代码：

```
        ┌───────────────────────────────────────┐
  顶层  │  人工审查 Human Review                  │  最贵最慢 → 只审该审的
        ├───────────────────────────────────────┤
  中层  │  AI 自动审查 Automated Review           │  抓测试没抓到的：结构/规范
        ├───────────────────────────────────────┤
  底层  │  自动检查 Automated Checks              │  lint / type / test
        └───────────────────────────────────────┘
   底下两层越扎实 → 顶层越轻松（ Matt 的核心防御原则）
```

| 层 | 内容 | 成本 | 挡什么 |
|---|------|------|--------|
| 底层：自动检查 | lint、type check、测试、代码质量指标 | 只花 CPU（修复 bug 花的 token 「花得很值」） | 机械性、确定性的错误 |
| 中层：AI 自动审查 | agent 读 diff + 周边代码 | 花 token | 测试没抓到的问题 + 整体结构 |
| 顶层：人工审查 | 人看 PR | 人力，最贵 | 最终裁决、单向门风险 |

最佳实践：

- ✅ 自动检查「极大化布营」——大部分团队的检查数量远不够，要更有创意地加
- ✅ 把下层当「测谎仪」（lie detector）：人工审查与 AI 审查的存在意义就是发现自动检查在撒谎
- ❌ 只加速生成、不建刹车 → Slop Cannon + 代码库熵增

---

## 重点一：测试会撒谎（Checks Can Lie）

CI 全绿 ≠ 可以合并。AI 撞长写三种「假测试」——它不是故意写烂，而是照着我们的指示写出了**与代码结构绑太紧**的测试：

| 类型 | 症状（演讲真实案例） | 为何绿得毫无意义 | 共同根源 |
|------|---------------------|-----------------|---------|
| 同义反复测试（tautological test） | `expect(xPostCharacterLimit).toBe(280)`——把实现常量抄一遍 | 改常量值或重命名都立刻红，毫无行为验证 | 对实现结构过度敏感 |
| 读源码字符串测试 | 不渲染画面，把模块读进内存，断言「video section 字符串排在 content plan 之后」 | 调整源码排版/重构就碎 | 不执行代码，只翻文本 |
| 不可能失败的测试 | `useAudioBoost` 内部用 AudioContext API，测试把整个 API stub 成假方法 | AudioContext 的复杂错误模式全被 mock 掉，永远绿灯，上线才爆 | 把真实错误路径 mock 没了 |

```javascript
// 假测试典型：同义反复
// 实现里写 280，测试就检查它是不是 280
expect(xPostCharacterLimit).toBe(280);   // 重命名常量 → 红；行为对错 → 不可知
```

演讲补充细节（导读视频未提）：Matt 说 Opus 5 「对这种测试上瘾」（got addicted to these）——高阶模型也会稳定产出这类假绿。

---

## 重点二：深模块（Deep Module）

问题转化：怎么让自动检查更难作弊？Matt 的答案是从代码设计下手——深模块（deep module）出自 John Ousterhout《软件设计的哲学》（A Philosophy of Software Design）：

```
   深模块 A                          浅模块 B
  ┌────────┐                      ┌──────────────┐
  │  ───   │ ← 小介面              │ □ □ □ □ □ □  │ ← 大介面（一堆函数）
  ├────────┤                      ├──────────────┤
  │        │                      │ ▏ ▏  ▏ ▏  ▏  │
  │  大实作 │ ← 复杂实现藏在后面      │ ▏ ▏  ▏ ▏  ▏  │ ← 每个函数
  │        │                      │ ▏ ▏  ▏ ▏  ▏  │   都没做多少事
  └────────┘                      └──────────────┘
  实现藏在介面后 →                  测试被迫伸手进实现 →
  结构敏感测试自然变少               结构敏感测试泛滥
```

- 深模块 → 只对介面写测试 → 测试品质自然变好
- 你的任务是**逼 agent 只透过小介面测试**，不许伸进实现细节

配套 skill（仓库 engineering bucket 实查存在）：

| Skill | 作用（官方描述） |
|-------|----------------|
| `improve-codebase-architecture` | 找出值得重构的模块，输出可视化 HTML 报告（before/after 对照），「再烂的 vibecode 代码库跑一次都会变好」 |
| `codebase-design` | 统一团队描述模块的词汇：定义 locality（局部性）、leverage（杠杆）、seam（接缝）——防止团队里 20 种说法都叫 DDD |

---

## 重点三：写审分离，规范只给审查 AI 读

最反直觉的一刀：**不要把团队规范塞给写代码的 agent**。两个 agent 的 context 预算完全不对称：

```
 Implement Agent（超载）                Review Agent（闲置）
 ┌──────────────────────────┐        ┌──────────────────────────┐
 │ 检索代码 ████████         │        │ 读 diff ███              │
 │ 改文件  ██████            │        │ 周边代码 ██（少量探索）    │
 │ 跑测试+调试 ████████       │        │ 无实现、无调试            │
 │ 再塞规范 → 表现下滑 ✗      │        │ 规范随便塞 ✓              │
 └──────────────────────────┘        └──────────────────────────┘
```

| 维度 | Implement Agent | Review Agent |
|------|----------------|--------------|
| context 负载 | 检索 + 改档 + 测试调试，已经超载 | 只看 diff + 周边代码，大量闲置 |
| 塞团队规范的效果 | 表现下滑 | 正好装满，审查品质大增 |
| 预设动作 | 让它能跑（make it work） | **直接 commit 修好**，有疑问才留言 |

写好代码 = 两段式流程（Matt：stop trying to oneshot good code）：

```
 implement（make it work）      code review（make it good）
        │                             │
        └── 类比 red-green-refactor ──┘
            一个 context 让它能跑
            另一个 context 重构变好
```

规范文件的正确归宿（文件名以仓库为准）：

- ❌ 不要写进 `AGENTS.md` / `CLAUDE.md`——它们被推进所有 agent 的 context，implement agent 「可能读也可能不读」，规范会淹没它
- ✅ 写进 `CODING_STANDARDS.md`（仓库实际文件名，全大写下划线）——只有 code review agent 读
- ❌ 不要外包给第三方审查服务（演讲点名 Cursor Bug Bot、CodeRabbit 一类）——通用的审查 skill 要么太泛（满屏误报）要么太窄（只懂 TypeScript，Rust 团队用不了）
- ✅ 自己积累团队规范，随时间越长越准，团队共享；「没人读的旧文档」终于有了归宿

审查 AI 的行为规范：

```
 审查发现小问题 → 直接改、直接 commit（默认动作）
 审查发现存疑点 → 才留言提问
 目标：人类审查者看到的是「很干净的成品」，不是一屏待处理意见
```

---

## 重点四：人类审查加速器 - 单向门与双向门

前两层做好后，让人类审查越快越好。Matt 正在做新的 PR skill（演讲时 in progress，已随 v1.3 于 2026-10-05 转正进仓库），第一原则：**不是每个 PR 都值得同样的审查强度**。

判断决策树（借 AWS 术语 one-way door / two-way door）：

```
 这个 PR 合并后出问题，能轻松 revert 吗？
 ├── 能（无外部副作用）→ 双向门 two-way door
 │      → 合并后发现不对随时拉回 → 大致过一眼即可
 └── 不能 / 不确定 → 单向门 one-way door
        ├── 会向大量用户发信（邮件/通知/webhook）→ 单向门
        ├── 高成本数据迁移 / 可能丢数据        → 单向门
        └── 其他不可逆副作用                  → 单向门
                                    → 严审到底（review the hell out of it）
```

关键认知：**改动大小 ≠ 风险大小**。「一个很小的改动也可能不小心给 6 万人寄信」——那就是单向门。Matt 的比喻：软件工程师相对土木工程师的幸福在于大多数门是双向的（桥塌了可回不去）。

配套概念：blast radius（爆炸半径）——出错概率多高？错了有多糟？落到 PR 模板就是底部的 merge danger 段落（`pr/SKILL.md` 实查有 `**Door:**` 字段）：

```markdown
## Merge danger
**Door:** two-way          ← one-way or two-way
**Blast radius:** localized ← 影响范围局部
```

第二原则：**用图取代文字**。PR 必须自带流程图/示意图，让人最快搞懂「为什么这么改」。做法致谢 HumanLayer 的 `show me` skill（humanlayer/skills 仓库，作者 Dex Holley）：用 mermaid / UML / 极简示意图代替大段文字 diff 说明。

---

## 重点五：retro 复利循环

第五个重点升维：人工审查审的不只是代码，而是**产生这些代码的系统**（the system that creates it）。

原则：「同一句评论永远不要写第二次」——绝不在两个 PR 里抓到 AI 犯同样的错。

新 skill `retro`（retrospective，回溯）复利机制：

```
 人工审查发现 AI 犯错
        │
        ▼
 retro 回看素材：单个 session / PR+session / 整周所有 PR+审查记录
        │
        ├─→ 机械性违规 → 固化为确定性检查（lint 规则 / pre-commit / CI job）
        ├─→ 判断类违规 → 写进 CODING_STANDARDS.md 给审查 agent
        └─→ 附带三项体检（导读视频未展开，演讲原文）：
        │     ① navigation pointers：agent 找资料顺不顺？要不要在 AGENTS.md 加指针
        │     ② tool economy：哪些工具调用不省 token？外部很难 debug 的盲区
        │     ③ bloat：规范文件/skill 是否太肥，反而拖累结果
        ▼
 下一次人工审查更轻松 → 品质滚雪球（compounding effect）
```

retro 原文还强调：能用确定性检查解决的违规，**默认建检查而非写规则**（Default to building the check over writing the rule）；`CODING_STANDARDS.md` 只留给真正需要判断力的条款，且超过 1000 行就该加导航指针。

---

## 三步落地行动清单

视频结尾的三步（可直接搬回自己的工作流）：

1. **强化基底**：打开 lint、type check、测试；测试只对公开介面写行为断言，不测实现细节（深模块原则）
2. **职责解耦**：建一份 `CODING_STANDARDS.md`，让独立的审查 agent 读它，并赋予它直接 commit 修正的权限
3. **门禁与复利**：PR 模板强制标注单向门/双向门 + 影响范围；每周跑一次 retro，把审查心得固化成新规则

视频作者 MUKI 的补充建议（非 Matt 原话）：先从 `improve-codebase-architecture` 和 `code-review` 两个 skill 装起试水。

---

## 验证与勘误记录

| 视频声称 | 验证结果 | 来源 |
|---------|---------|------|
| 仓库「今年二月」创建 | ✅ created_at 2026-02-03 | GitHub API（2026-10-20 查） |
| 29 万星 / 2.4 万 fork | ✅ 297,596★ / 24,988 forks（2026-10-20；2026-10-15 快照 291,274★，移动目标按快照口径） | GitHub API |
| Matt GitHub「将近五万人」追蹤 | ✅ 51,152（2026-10-20，略超五万） | GitHub API |
| Total TypeScript / 待过 Vercel 和 Stately | ✅ GitHub bio 原文 Ex-Vercel, Stately + Total TypeScript | GitHub API |
| 四月底破 4.5 万星、五月中 7.6 万 | ❓ 历史快照无独立出处（未核 star-history），仅视频口径 | 演讲/视频口播 |
| 三层刹车 = 自动检查 / AI 审查 / 人工审查，「蛋糕」比喻 | ✅ 原演讲 [2:58-3:40] 「these three layers form this kind of cake」 | 原演讲字幕 |
| 三种假测试（280 重抄 / 读源码查顺序 / mock AudioContext） | ✅ 全部为演讲真实案例 [5:25-7:55]；演讲另提「Opus 5 got addicted」导读未提 | 原演讲字幕 |
| 深模块出自 John Ousterhout《软件设计的哲学》 | ✅ [8:32-8:47] | 原演讲字幕 |
| `improve-codebase-architecture` / `codebase-design` skill 存在 | ✅ engineering/ 目录实查；codebase-design 定义 locality/leverage/seam | GitHub API + raw SKILL.md |
| 规范文件名 | ⚠️ 视频字幕写 coding-standards.md、Content Insights 写 codingstandards.md——仓库实际为 **`CODING_STANDARDS.md`**（全大写下划线，retro / code-review SKILL.md 原文） | GitHub raw |
| 规范别塞 agents.md，只给审查 agent | ✅ [11:30-14:16] implementation overloaded / review underloaded | 原演讲字幕 |
| 别外包第三方审查工具 | ✅ [14:22-15:08] 「don't outsource automated review. Build your own.」（演讲点名 Cursor Bug Bot、CodeRabbit） | 原演讲字幕 |
| 审查 AI 预设直接 commit 而非留言 | ✅ [15:13-16:04] 「The reviewer should commit… the default should be commits」 | 原演讲字幕 |
| 单向门/双向门借 AWS 说法 | ✅ [17:20] 「using this AWS terminology」 | 原演讲字幕 |
| 小改动可能寄信给 6 万人 | ✅ [17:58] 「blasts out an email to 60,000 people」 | 原演讲字幕 |
| PR 底部 merge danger / 影响范围 | ✅ [18:06-18:34]；pr/SKILL.md 含 `**Door:**` 字段 | 原演讲字幕 + raw SKILL.md |
| show me skill 来自 HumanLayer | ✅ humanlayer/skills 仓库存在；演讲致谢作者 **Dex Holley**（视频未提名字） | web 搜索 + 原演讲 [18:48] |
| retro 三查：找资料顺不顺 / 工具省不省 / 规范肥不肥 | ✅ [21:12-21:52] navigation pointers / tool economy / bloat | 原演讲字幕 |
| 时效注记 | 演讲（2026-09-25）说「v1.3 本周发布」，实际 2026-10-05 发布（implement-spec / pr / retro 转正 + CONTEXT.md → GLOSSARY.md）；pr skill 演讲时 in progress | aihero.dev changelog |

---

## 参考资料

- [导读视频：29 万星的 AI skill 作者怎么审 PR？（MUKI WU，2026-10-15）](https://www.youtube.com/watch?v=xPEr7P45rK0)
- [原演讲：Fixing the PR Bottleneck — Matt Pocock, AI Engineer Paris 2026](https://www.youtube.com/watch?v=LlgiOCmFG_w)（[官方讲题页](https://ai.engineer/talks/LlgiOCmFG_w)）
- [mattpocock/skills 仓库](https://github.com/mattpocock/skills)（MIT，297,596★ @ 2026-10-20）
- [aihero.dev/skills（作者 skill 主页 + 安装器）](https://aihero.dev/skills)
- [humanlayer/skills（show me skill 出处）](https://github.com/humanlayer/skills)

## 相关笔记

- [[Mattpocock-Skills 1.3 - pr、implement-spec、retro 三大新技能]] — 同仓库不同角度：该笔记是 v1.3 三个 skill 的功能拆解说明书（AI 随风视频），本笔记是这些 skill 背后的方法论演讲导读；retro / pr 的用法细节以该笔记为深挖入口

---

*基于 mattpocock/skills @ 2026-10-20（297,596★）与 AI Engineer Paris 2026 原演讲字幕交叉验证*
