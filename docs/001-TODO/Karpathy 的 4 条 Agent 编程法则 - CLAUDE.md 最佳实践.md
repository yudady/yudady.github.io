---
title: Karpathy 的 4 条 Agent 编程法则 - 从 152K Stars 的 CLAUDE.md 说起
aliases:
  - andrej-karpathy-skills
  - Claude Code 最佳实践
tags:
  - ai-agent
  - claude-code
  - llm-coding
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=wXaLvavnExY"
  - "https://github.com/multica-ai/andrej-karpathy-skills"
  - "https://www.techtimes.com/articles/316798/20260518/karpathy-inspired-claudemd-passes-220000-combined-github-stars-four-rules-that-stop-ai-breaking.htm"
author: 小木头（视频）、Forrest Chang（CLAUDE.md 作者）、Andrej Karpathy（原始洞察）
created: 2026-05-25
updated: 2026-05-25
description: |
  基于 Andrej Karpathy 对 LLM 编程 Agent 的三条批评，开发者 Forrest Chang 将其蒸馏为 4 条 CLAUDE.md 规则，仓库获 152K+ stars。
level: beginner
stars: 4
---

# Karpathy 的 4 条 Agent 编程法则

> 视频解读 Andrej Karpathy 对 LLM 编程 Agent 常见缺陷的观察，以及由此衍生的 CLAUDE.md 四条规则。适合日常使用 Claude Code / Codex 等编程 Agent 的开发者。

---

## 目录

- [[#背景：从一条推文到 152K Stars]]
- [[#Karpathy 的三条诊断]]
- [[#四条规则详解]]
- [[#安装与使用]]
- [[#效果如何判断]]
- [[#注意事项与安全警告]]

---

## 背景：从一条推文到 152K Stars

2026 年 1 月，Andrej Karpathy（OpenAI 联合创始人、前 Tesla AI 总监、vibe coding 一词的创造者）发了一条推，描述自己编程工作流从"80% 手写 + 20% Agent"翻转为"80% Agent + 20% 收尾"的体验：

> "有点伤自尊。像在作弊。过去 20 年没见过比这更大的工作流变化。"

开发者 **Forrest Chang** 在次日（1 月 27 日）将 Karpathy 观察到的问题蒸馏为一份 65 行的 `CLAUDE.md` 文件，发布到 GitHub。截至 2026 年 5 月，仓库在两个账号合计获得 **220K+ stars**（`multica-ai/andrej-karpathy-skills` 152K + `forrestchang/andrej-karpathy-skills` 91K），成为 GitHub 增长最快的仓库之一。

```
Karpathy 推文（1月26日）
    │
    ▼
Forrest Chang 蒸馏为 CLAUDE.md（1月27日）
    │
    ├─► forrestchang/andrej-karpathy-skills (~91K stars)
    └─► multica-ai/andrej-karpathy-skills  (~152K stars)
```

**注意：Karpathy 本人并未公开背书此仓库。** 仓库名使用他的名字是因为规则源自他的观察，而非他亲自编写。

---

## Karpathy 的三条诊断

视频归纳了 Karpathy 指出的 LLM 编程 Agent 三大常见错误：

| # | 问题 | 表现 |
|---|------|------|
| 1 | **沉默地假设** | Agent 替你做错误假设，不验证、不澄清、不暴露不一致，遇到歧义装没看到 |
| 2 | **过度复杂化** | 100 行能搞定的事写成 1000 行，默认交付企业级方案 |
| 3 | **顺手乱改** | 修一个 bug 顺手删注释、重构无关函数，PR 里混入大量无关改动 |

---

## 四条规则详解

每条规则对应一个诊断，形成「先思考 → 再编码 → 精准改动 → 目标闭环」的完整链路。

### 1. Think Before Coding（先思考，再编码）

**对症：沉默地假设**

核心原则：不假设、不藏困惑、把权衡摊到桌面上。

```
用户需求到达
    │
    ├─ 有歧义？──► 呈现多种解读，让用户选择
    │
    ├─ 有更简单方案？──► 主动提出并说明理由
    │
    └─ 不确定？──► 停下来，命名困惑点，询问
```

**对比示例 — "加个用户登录"：**

| 方式 | Agent 行为 |
|------|-----------|
| ❌ 烂的 | 默认 email + password + JWT hash + users 表（6 字段），全程零确认 |
| ✅ 好的 | 先问：email/password 还是 OAuth？Session 还是 JWT？users 表已存在？ |

**最佳实践：**
- ✅ 先陈述假设，再实现
- ✅ 存在多种解读时全部列出，不要自己选
- ✅ 有更简单的方案时主动 push back
- ❌ 不要在不确定时沉默推进

### 2. Simplicity First（简单优先）

**对症：过度复杂化**

核心原则：解决问题的最少代码，没要的别加，推测的别上。

**对比示例 — "读个 JSON 文件"：**

| 方式 | Agent 行为 |
|------|-----------|
| ❌ 烂的 | 建一个 `JSONLoaderFactory`（strategy、cache、parser pool、retry），200 行，调用 1 次 |
| ✅ 好的 | `json.load(open('data.json'))`，2 行搞定 |

**判断决策树 — 你的代码是否过度复杂？**

```
自检清单：
  ✅ 有没有被请求的功能？──► 删掉
  ✅ 有没有只使用一次的抽象？──► 内联
  ✅ 有没有被要求的"灵活性"？──► 删掉
  ✅ 有没有不可能发生的错误处理？──► 删掉
  ✅ 200 行能否缩减到 50 行？──► 重写
```

**最佳实践：**
- ✅ 写之前问自己：「资深工程师会觉得这过度复杂吗？」
- ✅ 需求简单就简单实现
- ❌ 不要给简单任务加企业级架构
- ❌ 不要加没被要求的"灵活性"

### 3. Surgical Changes（外科手术式修改）

**对症：顺手乱改**

核心原则：只动你必须动的，只清理你自己制造的烂摊子。

**对比示例 — "修 token 过期的 bug"：**

| 方式 | Agent 行为 |
|------|-----------|
| ❌ 烂的 | 改 auth.py + login.tsx + utils.py，PR 标题只说修 token bug |
| ✅ 好的 | auth.py 加 3 行删 1 行，其他文件不动，死代码单独提 issue |

**每一行 diff 都应追溯到用户请求。**

```
修改请求到达
    │
    ├─ 只修改与请求直接相关的代码
    │
    ├─ 发现死代码？──► 提一句，不删除，建议单独 issue
    │
    └─ 自己的修改产生了孤立代码？──► 清理自己的，不动之前的
```

**最佳实践：**
- ✅ 匹配现有代码风格，即使你会用不同方式写
- ✅ 发现无关死代码时提出来，但不要动
- ✅ 自己修改造成的孤立代码要清理
- ❌ 不要"顺手"改进相邻代码的格式/注释/风格
- ❌ 不要重构没坏的东西

### 4. Goal-Driven Execution（目标驱动执行）

**对症：标准模糊，靠反复问**

核心原则：先定义可验证的成功标准，然后自己跑闭环。

**对比示例 — "加个邮箱校验"：**

| 方式 | Agent 行为 |
|------|-----------|
| ❌ 弱标准 | 写 `return "@" in email`，算做完了吗？谁知道 |
| ✅ 好的 | 1. 写测试 → 2. 实现 → 3. 全套 pytest → 4. 验证 0 failures |

**多步骤任务的标准格式：**

```
1. [写 test_invalid_email] → verify: 测试失败
2. [实现 validate_email]   → verify: 测试通过
3. [跑全套 pytest]         → verify: 0 failures
```

**判断决策树 — 什么时候用目标驱动？**

```
任务类型：
  │
  ├─ 简单一行修复 ──► 直接做，TDD 反而加摩擦
  │
  ├─ 多文件改动 ──► 强烈建议先列 plan + verify 步骤
  │
  └─ 架构决策 ──► 必须先对齐成功标准
```

**最佳实践：**
- ✅ 将模糊任务转化为可验证目标
- ✅ 每一步都带验证检查点
- ✅ 强标准让 Agent 能独立闭环
- ❌ 不要用弱标准（"让它工作"）导致反复来回

---

## 安装与使用

### 方式一：下载 CLAUDE.md 到项目根目录（推荐）

```bash
curl -o CLAUDE.md https://raw.githubusercontent.com/multica-ai/andrej-karpathy-skills/main/CLAUDE.md
```

Claude Code 会自动读取项目根目录的 `CLAUDE.md`，内容成为每次会话的持久行为上下文。

### 方式二：作为 Claude Code 插件安装（全局生效）

```bash
/plugin marketplace add forrestchang/andrej-karpathy-skills
/plugin install andrej-karpathy-skills@karpathy-skills
```

### 方式三：Cursor 用户

仓库内包含 `.cursor/rules/karpathy-guidelines.mdc`，打开项目即生效。详见 `CURSOR.md`。

### 已有 CLAUDE.md 的项目

如果项目已有 `CLAUDE.md`，可以用追加方式合并。项目特定指令放在追加内容**之后**，冲突时项目指令覆盖通用规则。

---

## 效果如何判断

四条规则是否在发挥作用，可通过以下信号判断：

- ✅ diff 中不必要的改动变少了
- ✅ 代码变得更简单清晰（基于目标驱动，只做必要实现）
- ✅ Agent 在实现之前会先提问澄清需求
- ✅ PR 中的代码改动更加清爽聚焦

```
生效信号：
  fewer unnecessary changes in diffs
  fewer rewrites due to overcomplication
  clarifying questions come BEFORE implementation
  (not after mistakes)
```

---

## 注意事项与安全警告

### CLAUDE.md 不是银弹

- Karpathy **本人未公开背书**此仓库
- 152K stars 证明**问题普遍存在**，不代表此文件在所有项目都有效
- 规则偏向**谨慎而非速度**——对简单一行修复，澄清问题反而加摩擦
- **最佳场景**：复杂多文件改动、架构决策、错误假设会导致全面重写的 bug 修复

### 安全警告

> CLAUDE.md 文件**可能被武器化**。Adversa AI 和 LayerX 的安全研究人员发现，恶意仓库中的配置文件可以指示 Claude Code 生成窃取 SSH 密钥和 API 凭证的管道。

**最佳实践：**
- ✅ 只从官方仓库下载（`forrestchang/` 或 `multica-ai/`）
- ✅ 安装前检查文件内容（70 行纯文本，1 分钟可读完）
- ✅ 克隆第三方仓库时检查 `CLAUDE.md` / `AGENTS.md` 内容
- ❌ 不要从不明来源安装 Agent 技能包
  - Snyk 审计（2026.02）发现 **13% 的 Agent 技能包**包含严重安全漏洞

---

## 参考资料

- [multica-ai/andrej-karpathy-skills (GitHub)](https://github.com/multica-ai/andrej-karpathy-skills)
- [CLAUDE.md 原文](https://github.com/multica-ai/andrej-karpathy-skills/blob/main/CLAUDE.md)
- [Karpathy-Inspired CLAUDE.md Passes 220K Stars (TechTimes)](https://www.techtimes.com/articles/316798/20260518/karpathy-inspired-claudemd-passes-220000-combined-github-stars-four-rules-that-stop-ai-breaking.htm)
- [YouTube 视频 - 小木头频道](https://www.youtube.com/watch?v=wXaLvavnExY)

## 相关笔记

- [[Claude Code]] - Anthropic 的编程 Agent
- [[vibe-coding]] - Karpathy 创造的概念
