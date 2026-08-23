---
title: I-HAVE-ADHD — 让 AI 回复不再废话的 Claude Code 技能
aliases: [i-have-adhd, ADHD skill, ADHD plugin]
tags:
  - ai-agent
  - claude-code
  - developer-productivity
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=EpU0Cj4jlVg"
  - "https://github.com/ayghri/i-have-adhd"
author: AICodeKing（视频）/ ayghri（项目作者）
created: 2026-08-08
updated: 2026-08-08
description: 一个开源 Skill，用 10 条规则重塑 AI 编程助手的回复格式——行动优先、步骤编号、零废话。
level: beginner
stars: 4
---

# I-HAVE-ADHD — 让 AI 回复不再废话的 Claude Code 技能

> 一个仅由单个 `SKILL.md` 文件构成的开源技能（Skill），用 10 条规则强制 AI 把答案放在第一行、步骤编号、砍掉所有客套话。GitHub 18.2k+ stars，MIT 授权，支持 Claude Code / Codex / Cursor / Gemini / Kimi / Qwen。

## 目录

- [[#一、核心痛点：AI 废话太多]]
- [[#二、10 大规则详解]]
- [[#三、安装与使用]]
- [[#四、Before / After 实测对比]]
- [[#五、客制化与多平台支持]]
- [[#六、优缺点与适用场景]]

---

## 一、核心痛点：AI 废话太多

传统 AI 编程助手（Claude Code 等）的回复模式有个通病：

```
用户提问（1 行）
    ↓
AI 回复（5 段）:
  ├─ 3 段前言（preamble）
  ├─ 最佳实践讲座
  ├─ 5 个你没问的附带建议
  └─ "Hope this helps! Let me know..."
         ↑
    真正的答案埋在中间某个角落
```

**I-HAVE-ADHD 解决的问题**：把上述模式翻转——答案第一行，步骤严格编号，零冗余。

### 灵感来源

技能灵感来自书籍《The Adult ADHD Tool Kit》（J. Russell Ramsay & Anthony L. Rostain 著），但做了关键改编：**不是教人怎么管理时间，而是教 LLM 怎么跟人沟通**。

ADHD 患者的三个认知特征被映射到 LLM 沟通设计：

| ADHD 特征 | 沟通含义 | 技能对应规则 |
|-----------|---------|-------------|
| 工作记忆有限 | 长对话容易迷失 | 每轮重述当前状态 |
| 理解与行动之间有鸿沟 | 知道该做什么但不启动 | 结尾给 <2 分钟可完成的下一步 |
| 需要可见进度才能保持动力 | 黑箱过程令人焦虑 | 步骤编号 + 进度更新 |

**关键洞察**：这些特征适用于所有人——疲劳时、分心时、凌晨两点赶工时，你跟 ADHD 患者的需求是一样的。

---

## 二、10 大规则详解

以下是 I-HAVE-ADHD 的 10 条核心规则，每条附实际效果说明：

### 规则速览表

| # | 规则 | 核心要求 | 违反时的表现 |
|---|------|---------|-------------|
| 1 | Lead with action（行动优先） | 第一行就是命令或修改位置 | 先解释为什么需要这个功能 |
| 2 | Number multi-step tasks（步骤编号） | 严格编号，每步单一动作 | "然后…然后…" 链式嵌套 |
| 3 | End with one next step（<2分钟下一步） | 结尾给一个微小可执行动作 | "如有问题随时联系" |
| 4 | Suppress tangents（抑制定见） | 先解决核心问题 | 附带 5 个你没问的建议 |
| 5 | Restate state every turn（状态回述） | "步骤 3/5 已完成，下一步…" | 长对话中忘记当前进度 |
| 6 | Specific time estimates（精确估时） | "约 15 分钟" | "一会儿"、"很快" |
| 7 | Make wins visible（让进展可见） | 标记完成的里程碑 | 默默往下走 |
| 8 | Matter-of-fact errors（客观报错） | 直述原因和修复方案 | "哎呀，出了点小问题…" |
| 9 | Cap lists at 5 items（清单上限 5） | 所有列表不超过 5 项 | 无限长的 bullet list |
| 10 | No preamble/recap/closers（零冗余） | 无前言、无总结、无客套 | "Great question!" / "Hope this helps!" |

### 合理覆盖机制（Overrides）

技能并非机械执行，有三条智能覆盖：

```
请求类型              → 技能行为
────────────────────────────────────────
用户主动要求解释       → 正常结构化说明（不缩略）
涉及破坏性操作         → 先确认再执行
请求有歧义             → 只问一个澄清问题（不多问）
```

### 10 条规则的 ASCII 优先级图

```
                    ┌──────────────────────┐
                    │  用户发出请求         │
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │ 规则 4: 先抑制定见     │
                    │ 只处理核心请求         │
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │ 规则 1: 行动优先       │
                    │ 第一行给命令/位置      │
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │ 规则 2: 步骤编号       │
                    │ 每步单一动作           │
                    └──────────┬───────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
   ┌──────────▼────┐  ┌───────▼────────┐ ┌─────▼──────────┐
   │ 规则 5: 状态   │  │ 规则 6: 精确    │ │ 规则 8: 客观    │
   │ 回述           │  │ 估时            │ │ 报错            │
   └──────────┬────┘  └───────┬────────┘ └─────┬──────────┘
              │                │                │
              └────────────────┼────────────────┘
                               │
                    ┌──────────▼───────────┐
                    │ 规则 3: <2分钟下一步   │
                    │ 规则 10: 零客套结尾    │
                    └──────────────────────┘
```

---

## 三、安装与使用

### 快速安装（Claude Code）

```bash
# 安装插件
claude plugin install i-have-adhd@i-have-adhd

# 激活（在 Claude Code 中输入）
/i-have-adhd

# 停用
claude plugin disable i-have-adhd
```

### 支持的 Agent 平台

| Agent | 配置方式 | 仓库路径 |
|-------|---------|---------|
| Claude Code | Plugin marketplace | `.claude-plugin/` |
| Codex | 插件配置 | `.codex-plugin/` |
| Cursor | Skill 目录 | `.cursor/skills/i-have-adhd/` |
| Gemini | Extension JSON | `GEMINI.md` + `gemini-extension.json` |
| Kimi | Plugin JSON | `kimi.plugin.json` |
| Qwen | Extension JSON | `qwen-extension.json` |

### 安装流程图

```
claude plugin install i-have-adhd@i-have-adhd
        │
        ▼
  ┌─────────────┐     失败    ┌──────────────────────┐
  │ 下载成功？   │────────────│ 检查网络/Claude 版本  │
  └──────┬──────┘             └──────────────────────┘
         │ 成功
         ▼
  在 Claude Code 输入 /i-have-adhd
         │
         ▼
  ┌─────────────┐     未激活   ┌──────────────────────┐
  │ 技能激活？   │────────────│ 重启 Claude Code 重试  │
  └──────┬──────┘             └──────────────────────┘
         │ 激活
         ▼
     正常使用
```

---

## 四、Before / After 实测对比

视频用「Express 加 JWT 认证」作为测试案例，对比如下：

### 场景：给 Express 应用添加 JWT 认证

**未启用 I-HAVE-ADHD（Before）：**

```
┌──────────────────────────────────────────────────────────┐
│  AI 回复结构：                                             │
│                                                          │
│  [段落 1] JWT 是什么...                                    │
│  [段落 2] 为什么认证很重要...                               │
│  [段落 3] 多种实现方式对比...                               │
│  [段落 4] 终于——命令在这里（但你要翻到中间才能看到）         │
│  [段落 5] "Hope this helps!"                              │
│                                                          │
│  问题：答案被埋在 4 段废话后面                              │
└──────────────────────────────────────────────────────────┘
```

**启用 I-HAVE-ADHD（After）：**

```
┌──────────────────────────────────────────────────────────┐
│  AI 回复结构：                                             │
│                                                          │
│  Line 1: npm install jsonwebtoken                        │
│                                                          │
│  1. 安装依赖                                              │
│  2. 编辑 src/auth.js:12 — 添加 JWT import               │
│  3. 编辑 src/auth.js:28 — 替换 verifyToken 函数          │
│  4. 运行测试验证                                           │
│                                                          │
│  下一步：运行 dev server 测试登录路由                      │
│  预计时间：~20 分钟                                       │
│                                                          │
│  零废话，答案第一行                                       │
└──────────────────────────────────────────────────────────┘
```

### Before / After 维度对比表

| 维度 | Before（默认） | After（+I-HAVE-ADHD） |
|------|---------------|----------------------|
| 第一行内容 | "Great question! Let me..." | `npm install ...` |
| 步骤格式 | 散落段落中 | 严格编号 1. 2. 3. |
| 附带建议 | 5 个你没问的 | 0（被 tangent suppression 砍掉） |
| 结尾 | "Hope this helps!" | 具体的下一步动作 |
| 时间估算 | 无 | "约 20 分钟" |
| 信息密度 | 低（大量填充文字） | 高（每行都有用） |
| Token 消耗 | 高 | 显著降低 |

---

## 五、客制化与多平台支持

### Fork 自定义

整个技能本质是一个 `skills/i-have-adhd/SKILL.md` 文件，Fork 后可直接编辑：

```
Fork 仓库
    │
    ├── 编辑 skills/i-have-adhd/SKILL.md
    │   ├── 修改清单上限（5 → 3）
    │   ├── 调整简洁程度
    │   └── 添加自定义规则
    │
    ├── 重新安装你的 Fork 版本
    │
    └── 重启 Claude Code → /i-have-adhd 激活
```

### 多语言社区翻译

仓库已有社区翻译的 README：

| 语言 | 文件路径 |
|------|---------|
| 简体中文 | `.github/readme/README.zh-CN.md` |
| 葡萄牙语 | `.github/readme/README.pt-BR.md` |
| 日语 | `.github/readme/README.ja.md` |
| 越南语 | `.github/readme/README.vi.md` |
| 韩语 | `.github/readme/README.ko.md` |

### 仓库结构概览

```
ayghri/i-have-adhd
├── .claude-plugin/          # Claude Code 插件配置
├── .codex-plugin/           # Codex 插件配置
├── .cursor/skills/          # Cursor 技能目录
├── .agents/plugins/         # 通用 Agent 插件
├── evals/                   # 评估测试集
├── extensions/              # 扩展
├── hooks/                   # Git hooks
├── scripts/                 # 脚本工具
├── skills/i-have-adhd/      # ← 核心：SKILL.md
├── tests/                   # 测试
├── GEMINI.md                # Gemini 集成
├── gemini-extension.json    # Gemini 配置
├── kimi.plugin.json         # Kimi 配置
├── qwen-extension.json      # Qwen 配置
├── INSTALL.md               # 安装指南
├── package.json             # npm 配置
└── plugin.json              # 插件元数据
```

---

## 六、优缺点与适用场景

### 优点

- ✅ **零成本**：免费、开源、MIT 授权
- ✅ **安装极快**：一条命令 + 重启即可
- ✅ **状态回述（规则 5）是长会话利器**：agentic session 中不再迷失进度
- ✅ **<2 分钟下一步（规则 3）降低启动阻力**：最难的永远是开始
- ✅ **Token 节省**：砍掉废话 = 省钱（尤其有限额计划）
- ✅ **多平台**：Claude Code / Codex / Cursor / Gemini / Kimi / Qwen 全覆盖

### 缺点

- ❌ **对初学者不友好**：需要详细解释的场景，默认模式太精简
- ❌ **需要主动加「请详细解释」**：初学者每次都要手动要求展开
- ❌ **可能过度精简**：某些复杂概念确实需要上下文铺垫

### 判断决策树：该不该用？

```
你是否已经有编程经验？
│
├─ 否 → ❌ 暂时别用，或用的时候手动加"请详细解释"
│
└─ 是 → 你是否经常觉得 AI 说太多废话？
       │
       ├─ 否 → 可能不需要，但值得试一周
       │
       └─ 是 → ✅ 强烈推荐
              │
              └─ 你是否经常在长 agentic session 中迷失？
                 │
                 ├─ 是 → ✅ 必装（规则 5 状态回述 + 规则 3 下一步 = 神器）
                 │
                 └─ 否 → ✅ 仍推荐（Token 节省 + 回复可读性提升）
```

### 最佳实践清单

- ✅ 有经验开发者 + 长编码 session：全程开启
- ✅ 初学者学新技术时：关闭，或提问时加「请详细解释」
- ✅ 想自定义规则强度：Fork 后编辑 SKILL.md
- ❌ 不要在不了解规则的情况下盲目开启后给团队用（先个人试用）
- ❌ 不要期望它自动判断什么时候该详细——需要你主动用语言控制

---

## 项目数据

| 属性 | 值 |
|------|-----|
| 仓库 | [github.com/ayghri/i-have-adhd](https://github.com/ayghri/i-have-adhd) |
| Stars | 18.2k+（截至 2026-08） |
| Forks | 1.1k+ |
| 授权 | MIT |
| 作者 | ayghri（GitHub 用户名） |
| 核心文件 | `skills/i-have-adhd/SKILL.md`（单个文件） |
| 支持 Agent | Claude Code, Codex, Cursor, Gemini, Kimi, Qwen |
| 灵感来源 | 《The Adult ADHD Tool Kit》(J. Russell Ramsay & Anthony L. Rostain) |

---

## 参考资料

- [GitHub 仓库：ayghri/i-have-adhd](https://github.com/ayghri/i-have-adhd)
- [YouTube 视频：I-HAVE-ADHD Skill + Claude,Codex,Deepseek](https://www.youtube.com/watch?v=EpU0Cj4jlVg)（AICodeKing 频道）
- [The Adult ADHD Tool Kit — J. Russell Ramsay & Anthony L. Rostain](https://www.routledge.com/9780415703348)（技能灵感来源书籍）

## 相关笔记

- [[Claude Code 技能系统]]
- [[AI Agent 沟通效率优化]]
