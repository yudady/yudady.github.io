---
title: Anthropic 团队用 Claude Code 一年学到的 5 件反直觉的事
aliases:
  - Claude Code 一年实战心得
  - Anthropic AI Coding 最佳实践
tags:
  - ai-agent
  - claude-code
  - status/active
  - type/doc
source:
  - "https://youtu.be/NXmv0HjELwc"
  - "https://paddo.dev/blog/one-year-of-claude-code"
  - "https://www.ernestchiang.com/en/posts/2026/the-man-who-built-claude-code-was-changed-by-it"
  - "https://www.youtube.com/watch?v=We7BZVKbCVw"
author: 思思主播（摘要） / Boris Cherny（Anthropic Claude Code 负责人）
created: 2026-06-10
updated: 2026-06-10
description: |
  Anthropic 团队内部使用 Claude Code 一整年后的实战心得总结，涵盖 5 个颠覆工程直觉的核心判断，以及 AI 工作流的哲学反思。
level: intermediate
stars: 4
---

# Anthropic 团队用 Claude Code 一年学到的 5 件反直觉的事

> Anthropic 团队用 Claude Code 一整年后公开分享实战心得，核心结论：过去一年的工程直觉几乎每一条都跟直觉相反。适合每天使用 AI 辅助开发的工程师和知识工作者阅读。

## 目录

- [[#背景：Claude Code 一年演进]]
- [[#1. 犯错写成规则，而非当场纠正]]
- [[#2. 验证 = 让 AI 自己跑起来看到结果]]
- [[#3. 全自动核准比逐条把关更安全]]
- [[#4. 让 AI 自己接活]]
- [[#5. Context 越少越好]]
- [[#核心哲学：AI 放中心还是边缘]]
- [[#补充：Boris Cherny 的个人转变]]

---

## 背景：Claude Code 一年演进

Claude Code 于 2025 年 2 月上线，负责 Boris Cherny 透露其代码库每隔几个月就完全重写一次——6 个月前的代码已不存在于生产环境，约 80% 的代码不到 2 个月。

### 关键数字

| 指标 | 数值 |
|------|------|
| Boris Cherny 代码由 Claude Code 撰写的比例 | 2 月 20% → 5 月 30% → 11 月 **100%** |
| Anthropic 工程团队规模增长 | **4x** |
| 每位工程师生产力提升 | **200%** |
| GitHub 上由 Claude Code 提交的 commit | **4%**（预计年底达 20%） |
| Claude Code 年收入 | **$2B** |
| Anthropic 当前内部 AI Agent 数量 | **1000+ 个**（树状结构） |

### 五个时代演变

```
Core Loop → Context Wars → Multi-Model → Controllability → Agent Teams
(Feb-Oct)   (Oct-Nov)     (Nov-Dec)      (Dec-Jan)        (Jan-Feb 2026)
```

每个时代在当时都觉得是"最终形态"，但都不是。

---

## 1. 犯错写成规则，而非当场纠正

### 核心判断

当 AI 犯错时，**不要只在对话中纠正**——把它写成一条固定规则。

### 直觉 vs 反直觉

| 做法 | 直觉反应 | Anthropic 实践 |
|------|---------|----------------|
| AI 写错代码 | 在对话中说"下次别这样" | 写入 CLAUDE.md 或打包成 Skill |
| AI 用错命令 | 手动改回正确命令 | 规则固化，每次启动自动读取 |
| AI 遗忘上下文 | 重新碎碎念你的规矩 | 错误变规则，零重复沟通成本 |

### 为什么当场纠正无效

```
对话中纠正 → AI 只在本次听话
               │
               └─→ 新对话 = 归零，忘记一切

写入 CLAUDE.md / Skill → 每次启动自动加载
                          │
                          └─→ 永久生效，纠正次数递减
```

### 实际操作

```bash
# Claude Code 中的 CLAUDE.md（项目根目录）
# 每次启动 Claude Code 时自动读取

- 所有数据库迁移使用 bunx prisma migrate dev
- 不要修改 .env 文件，环境变量通过 ci.yaml 管理
- PR 标题格式：feat|fix|refactor(scope): 描述
- 测试框架：vitest，不使用 jest
```

**核心原则**：纠正 AI 的次数越少，它就越能自动自发地持续运转。

---

## 2. 验证 = 让 AI 自己跑起来看到结果

### 核心判断

验证不再是写单元测试、检查格式。验证是：**这个 AI Agent 能不能自己把东西跑起来，亲眼看到结果对不对？**

### 验证定义的演变

```
过去：写单元测试 → 检查格式 → lint → code review
              ↓ AI 时代
现在：给 AI 一个能自己看到成果的环境 → 让它自己验收
```

### 实战案例

Anthropic 内部工程师教 Claude 在本机运行桌面 App 后，Claude 的行为：

```
1. 启动 App（用 computer use 功能操作鼠标/键盘）
2. 像人类一样点来点去测试 UI
3. 遇到环境异常 → 读 Slack 消息判断是全局问题还是自身问题
4. 自己定位 → 自己修复 → 自己验证
```

### 思维转变

| 旧思维 | 新思维 |
|--------|--------|
| 想尽办法叫 AI 照做 | 给它一个能自己看到成果的环境 |
| AI 是写代码的工具 | AI 是能自己验收的 Agent |
| 人是验证者 | AI 是验证者，人是把关者 |

---

## 3. 全自动核准比逐条把关更安全

### 核心判断

**全自动核准模式比人工逐条把关更安全。** 听起来疯狂，但背后有硬数据支撑。

### 人类疲劳盲点

```
AI 执行第 1 次操作 → "主人，可以执行吗？"
AI 执行第 2 次操作 → "主人，可以执行吗？"
...
AI 执行第 99 次操作 → "主人，可以执行吗？"

连续点击 99 次同意后：
  → 眼睛放空
  → 大脑关机
  → 变成无情的同意点击机器
  → 第 100 次真正有风险的操作也自动通过
```

### Anthropic 的解决方案

```
每个 AI 请求
    │
    └─→ 另一个 AI 模型自动判断安全性
            │
            ├─→ 安全：自动批准（99% 的请求）
            │
            └─→ 有疑虑：才交给人类审核（1%）
```

这个安全机制经过内部严格测试，甚至找了专门模拟攻击的**红队（Red Team）**来狂测。

### ✅ 最佳实践

- ✅ 让 AI 审核 AI，人类只看有疑虑的 1%
- ✅ 把宝贵的注意力留给真正需要判断的场景
- ❌ 不要对每一个 AI 操作都手动点击"同意"
- ❌ 不要以为"人工把关"就是更安全

---

## 4. 让 AI 自己接活

### 核心判断

让 AI 主动监控系统，**自动接手符合条件的任务**，而非等待人类分配。

### Anthropic 内部实战

一位工程师半夜上线新功能后发现 bug，正准备自己修——画面跳出提示：**"另一个 Claude 已经把这个修好了。"**

他根本没有交代任何 AI 去做这件事。

### 实现方式：例行任务（Routine Tasks）

```
监控系统 / GitHub Issues / Bug Reports
         │
         └─→ 触发条件：超过 5 小时无人处理的 bug
                    │
                    └─→ AI 自动接手 → 修复 → 提交 PR
```

### 对普通人的启示

```
你不需要 1000 个 AI Agent
你只需要 1 个会自动接活的 AI

可自动化的任务特征：
  ✅ 重复性高
  ✅ 有明确的触发条件
  ✅ 判断标准清晰
  ✅ 不需要创意/战略决策

示例：
  - 超时未处理的 bug → 自动修复
  - 日报/周报收集 → 自动汇总
  - 依赖更新 → 自动检测并 PR
  - 代码质量检查 → 自动 fix 并提交
```

### Claude Code 的具体能力

Boris Cherny 原话：**"Every day I ship 10, 20, 30 pull requests."** 背后就是大量自动化 Agent 在并行工作。

---

## 5. Context 越少越好

### 核心判断

**给 AI 越少 Context（上下文），越少脉络，反而越好。** 这是对"塞满上下文"做法的彻底反叛。

### Prompt Engineering 的三阶段演变

```
阶段 1：提示工程（Prompt Engineering）
  → 字斟句酌写指令，让 AI 听懂

阶段 2：脉络工程（Context Engineering）
  → 塞满几十页背景资料给 AI

阶段 3：极简主义（Minimalism）  ← 当前阶段
  → 给最精简的任务目标 + 让 AI 自己拉取数据的管道
```

### 为什么更多 Context 反而更差

```
给太多上下文
    │
    ├─→ 你在微管理 AI
    ├─→ 限制了它的发挥空间
    ├─→ AI 明明知道更快的路径，却被你的指令绑住
    └─→ 噪音信息稀释了关键指令的权重
```

### ✅ 最佳实践

- ✅ 给最精简的任务目标
- ✅ 给一个能自己拉取资料的管道（MCP、工具调用）
- ✅ 放手让 AI 自己搞定
- ❌ 不要塞几十页背景知识
- ❌ 不要写冗长的 system prompt
- ❌ 不要把所有规则都堆进 context

### Boris Cherny 的原话

> "Clean context. Explicit goals. Plan before executing. Read before editing. Verify before trusting."
> "工具变了五次。使用它的纪律一次都没变。"

---

## 核心哲学：AI 放中心还是边缘

### 1990 年代哈佛商业评论的启示

```
电脑普及了 → 生产力没爆发
    │
    原因：电脑只放在桌子旁边
    │     纸本作业照旧
    │     电脑只是偶尔用来打字的机器
    │
    真正的爆发 = 把纸本档案柜丢掉
                 把电脑放在每个工作流程的正中心
```

### Anthropic 内部的 AI 中心化实践

```
新人报到有问题 → 不要问同事 → 去问 Claude
工程师在沙发上 → 用手机远程遥控 AI 合并代码上线
产品经理（不会写代码） → 通过 AI 直接修改产品
```

### 判断决策树

```
你的 AI 现在在哪里？

├─→ 偶尔卡关才叫出来问问 → 边缘位置
│     → AI = 工具（像以前的电脑打字机）
│
├─→ 每天都在用，但主要做辅助 → 过渡位置
│     → AI = 助手
│
└─→ 每项任务的启动都经过 AI → 中心位置
      → AI = 核心枢纽
      → 这两种思维在一年后拉开的差距巨大
```

---

## 补充：Boris Cherny 的个人转变

### 从 Instagram 到 Anthropic

Boris 曾是 Instagram 最有生产力的工程师之一。使用 Claude Code 后：

| 维度 | 变化 |
|------|------|
| 代码撰写 | 100% 由 Claude Code，11 月起未手动编辑一行 |
| 每日产出 | 10-30 个 PR / 天 |
| 验证态度 | 更多怀疑，不是更少——AI 流畅性让错误更难发现 |
| 核心问题 | 从"AI 能做这个吗？"变成"AI **应该**做这个吗？" |

### 活字印刷术类比

Boris 用印刷术类比 AI 对编程的影响：

```
15 世纪抄写员 → 兴奋地拥抱印刷术
  → "我最讨厌的就是反复抄写"
  → "我热爱的是插画和装帧"

Boris Cherny → 兴奋地拥抱 Claude Code
  → "我从没像现在这样享受写代码"
  → "因为我不用处理那些琐碎细节了"
```

### 核心竞争力三要素

Boris 认为，真正的竞争优势在三个交叉点：

```
     领域洞察 (Domain Insight)
            │
            ├─── 系统思维 (Systems Thinking) ───┐
            │                                  │
            └──────────────────────────────────┤
                                               │
              验证能力 (Verification) ──────────┘

这三个能力的交叉点 = 不可替代的价值
```

> "关键不在于你写代码多快，而在于你能否拆解需求、清晰定义、验证正确性。"

---

## 参考资料

- [One Year of Claude Code - paddo.dev](https://paddo.dev/blog/one-year-of-claude-code)
- [The Man Who Built Claude Code Was Changed By It - Ernest Chiang](https://www.ernestchiang.com/en/posts/2026/the-man-who-built-claude-code-was-changed-by-it)
- [Head of Claude Code: What happens after coding is solved | Boris Cherny - Lenny's Podcast](https://www.youtube.com/watch?v=We7BZVKbCVw)
- [Code with Claude 2026: Opening Keynote](https://www.youtube.com/watch?v=wjvESxKgqaQ)
- [Introducing Anthropic's first developer conference: Code with Claude](https://www.anthropic.com/news/Introducing-code-with-claude)

## 相关笔记

- [[Claude Code]]
- [[AI Agent 架构]]
- [[Prompt Engineering 演进]]
