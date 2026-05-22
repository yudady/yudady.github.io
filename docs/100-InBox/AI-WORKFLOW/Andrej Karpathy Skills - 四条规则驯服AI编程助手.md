---
title: Andrej Karpathy Skills - 用四条规则驯服 AI 编程助手
aliases: [Karpathy Skills, CLAUDE.md 编程规范, AI 编程四原则]
tags:
  - ai-coding
  - claude-code
  - prompt-engineering
  - github
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=-I44R13iLAU"
  - "https://github.com/multica-ai/andrej-karpathy-skills"
  - "https://x.com/karpathy/status/2015883857489522876"
author: multica-ai (GitHub) / 偉代碼 Rick Hau (YouTube)
created: 2026-05-16
updated: 2026-05-16
description: 源自 Andrej Karpathy 对 AI 编程痛点的三条吐槽，提炼为四条可执行规则，单月 GitHub 12 万星
level: beginner
stars: 4
---

# Andrej Karpathy Skills - 用四条规则驯服 AI 编程助手

> 一个不到 100 行的 CLAUDE.md 文件，在 GitHub 上单月斩获 12 万星。它不靠代码、不靠架构、不靠 UI，只靠四条规则解决了 AI 编程最让人抓狂的三大问题。

---

## 起源：Karpathy 的三条吐槽

前特斯拉 AI 总监、OpenAI 联合创始人 Andrej Karpathy 在大量使用 AI 编程后总结了三个核心痛点：

| # | 痛点 | 具体表现 |
|---|------|---------|
| 1 | **错误假设 + 隐藏困惑** | AI 不验证理解是否正确，不提问澄清，遇到歧义自行脑补，不展示权衡取舍 |
| 2 | **过度设计** | 100 行能搞定的事非要写成 1000 行，疯狂堆抽象、加设计模式、搞三层架构 |
| 3 | **无关联改动** | 改 A 函数时顺手改了 B 文件的缩进、C 文件的注释风格，甚至删除不理解的代码 |

---

## 四条规则详解

### Rule 1: Think Before Coding — 编码前先思考

**核心：不要假设，不要隐藏困惑，先搞清楚再动手。**

```
传统 AI 行为:                Karpathy Skills 要求:
  收到指令                      收到指令
    |                              |
    v                              v
  静默选择一种理解               明确列出多种可能的解释
    |                              |
    v                              v
  直接开始写代码                 向用户确认 → 再动手
```

**具体要求：**
- 遇到歧义时，列出多种解释让用户选择，而非自行猜测
- 不确定的地方必须停下来说明，而非硬着头皮写
- 如果有更简单的方案，主动提出并说明利弊
- 完成一次需求确认动作，从根本上杜绝方向性错误

**价值：** 数秒的问答 vs 数百行错误代码 + 20 分钟审查 + 重写。

---

### Rule 2: Simplicity First — 简洁优先

**核心：用最少的代码解决问题，不写任何推测性的代码。**

| ❌ 禁止 | ✅ 要求 |
|---------|---------|
| 超出需求范围的功能 | 只写被要求的功能 |
| 只用一次的功能做抽象 | 直接内联，不搞接口 |
| 未被要求的"灵活性"和"配置项" | 按需硬编码 |
| 不可能场景的错误处理 | 只处理合理场景 |
| 200 行能写成 50 行的代码 | 必须重写 |

**评判标准：** 一段代码被一个有十几年经验的高级工程师看了，会不会觉得过度设计？如果是，简化。

**典型反面教材：**

```
你要一个排序函数
  → AI 给你写：带泛型 + 接口 + 策略模式的排序框架

你要一个简单 API 端点
  → AI 给你装上：RESTful 规范 + 中间件链 + 数据库 ORM
```

---

### Rule 3: Surgical Changes — 精准修改

**核心：只碰必须碰的代码，只清理自己制造的 mess。**

| ❌ 禁止 | ✅ 要求 |
|---------|---------|
| 顺手"优化"相邻代码 | 不改与需求无关的任何行 |
| 重构没出问题的部分 | 保持现有编码风格 |
| 删除自己不理解的注释/代码 | 发现死代码可以提醒，但绝不自己删 |
| 改变项目整体格式 | 即使觉得现有风格不好也不动 |

**检验标准：** 改动的每一行代码都必须能直接追溯到用户的需求。

**为什么这很重要：** AI 的"顺手优化"引入了大量你根本没审查过的改动，出问题时排查比排查自己的 bug 还难十倍——因为你不知道哪些改动是你想要的，哪些是 AI 自作主张的。

---

### Rule 4: Goal-Driven Execution — 目标驱动执行

**核心：把"做什么"转化为"达成什么"，让 AI 自己循环验证。**

```
指令转换示例:

❌ "修复这个 bug"
✅ "写一个能复现该 bug 的测试，然后让它通过"

❌ "添加输入验证"
✅ "为无效输入写测试用例，然后让它们全部通过"

❌ "重构 X 模块"
✅ "确保重构前后测试覆盖率一致，所有测试通过"
```

**多步骤任务格式：**

```
1. [具体步骤] → verify: [验证方式]
2. [具体步骤] → verify: [验证方式]
3. [具体步骤] → verify: [验证方式]
```

**Karpathy 的关键洞察：**

> "LLMs 在明确的验收标准下特别擅长循环执行。不要告诉它做什么，给它成功标准，看着它自己跑。"

---

## 安装方式

### Claude Code（推荐）

```bash
# 添加 marketplace
/plugin marketplace add forrestchang/andrej-karpathy-skills

# 安装插件
/plugin install andrej-karpathy-skills@karpathy-skills
```

### 手动 — 任何读取项目级指令的 AI 工具

```bash
# 新项目
curl -o CLAUDE.md https://raw.githubusercontent.com/forrestchang/andrej-karpathy-skills/main/CLAUDE.md

# 追加到已有项目
echo "" >> CLAUDE.md
curl https://raw.githubusercontent.com/forrestchang/andrej-karpathy-skills/main/CLAUDE.md >> CLAUDE.md
```

支持：Claude Code、Cursor、OpenCode、Codex CLI 等任何读取 `CLAUDE.md` / `agents.md` 的工具。

---

## 使用注意事项

| 注意点 | 说明 |
|--------|------|
| **偏向谨慎而非速度** | 对琐碎任务（改拼写、修一行 bug）可灵活跳过完整流程 |
| **目标不是拖慢工作** | 减少非琐碎任务中代价高昂的错误，而非拖慢所有工作 |
| **可合并项目规则** | 四条规则 + 项目特定规则可共存于同一 CLAUDE.md |
| **仓库已更名** | 原始仓库为 `forrestchang/andrej-karpathy-skills`，视频提到的是 `multica-ai/andrej-karpathy-skills`（fork/镜像） |

---

## 如何判断是否生效

- ✅ Diff 里只出现你要求的改动
- ✅ 不再因为过度设计而需要重写
- ✅ AI 在实现之前先提问澄清
- ✅ PR 干净简洁，没有"顺便优化"

---

## 核心哲学

**不要把 AI 当成一个听话的初级工程师，而要把它当成一个能力强但缺乏判断力的合作伙伴。你需要告诉它的不是"怎么做"，而是"做什么"和"做到什么标准"。**

这个项目的火爆说明了一个道理：有时最有价值的事情不是造新轮子，而是把大家都知道的道理用最低门槛让每个人都能用起来。

---

## 参考资料

- [Karpathy 原始推文](https://x.com/karpathy/status/2015883857489522876)
- [andrej-karpathy-skills (GitHub)](https://github.com/multica-ai/andrej-karpathy-skills)
- [视频：不得不装的 AI Skills](https://www.youtube.com/watch?v=-I44R13iLAU) by 偉代碼 Rick Hau

## 相关笔记

- [[Codex AI 代理人 - 从实践到自动化指南]]
