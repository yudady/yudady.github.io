---
title: Hermes Agent /learn 与 /goal：技能化与自主执行
aliases: [Hermes Agent Skills, learn goal, Completion Contracts, Supabase 持久化]
tags:
  - hermes-agent
  - ai-agent
  - skills
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=eBGWEngWL7Y"
author: Derek Cheung | AI Agents Automation
created: 2026-08-11
updated: 2026-08-11
description: Hermes Agent 的 /learn 和 /goal 双轨机制：将知识技能化、目标自主执行，配合 Supabase 云端持久化实现跨设备共享。
level: intermediate
stars: 4
---

# Hermes Agent /learn 与 /goal：技能化与自主执行

> Hermes Agent 通过 `/learn`（知识技能化）和 `/goal`（自主闭环执行）双轨机制，结合完成合约（Completion Contracts）自我验证，将 AI 从问答工具升级为具备记忆与自主完成能力的数字员工。配合 Supabase 云端存储，技能可在任意设备、任意 Agent 间共享。

## 目录

- [核心双轨机制](#核心双轨机制)
- [完成合约（Completion Contracts）](#完成合约completion-contracts)
- [8 大 /learn 应用场景](#8-大-learn-应用场景)
- [Supabase 云端持久化](#supabase-云端持久化)
- [批判性评估](#批判性评估)
- [参考资料](#参考资料)

---

## 核心双轨机制

Hermes Agent 解决 AI 的「失忆症问题（Amnesia Problem）」：每次新会话都要从头解释背景。双轨机制将其拆分为「记住」和「执行」。

```
┌─────────────────────────────────────────────────────┐
│              Hermes Agent 双轨架构                     │
│                                                       │
│   ┌───────────┐                    ┌───────────┐     │
│   │  /learn   │  ──── 产出 ────▶   │  /goal    │     │
│   │           │     Skill(.md)     │           │     │
│   │ 知识技能化  │                   │ 自主执行    │     │
│   │ (输入端)   │                   │ (输出端)   │     │
│   └─────┬─────┘                   └─────┬─────┘     │
│         │                                │            │
│         ▼                                ▼            │
│   ┌───────────┐               ┌─────────────────┐    │
│   │ Skill 库   │ ◀── 复用 ──── │ 完成合约验证     │    │
│   │ (本地/云端) │               │ (自我检查)       │    │
│   └───────────┘               └─────────────────┘    │
└─────────────────────────────────────────────────────┘
```

### /learn 指令

| 特性 | 说明 |
|------|------|
| 输入 | 文件夹、URL（含 YouTube）、历史对话 |
| 输出 | Markdown 技能文件（Skill），自动生成 |
| 效果 | 永久记忆，跨会话复用 |
| 核心 | 不是总结，而是将方法论提炼为可应用的过程 |

```
/learn [指令描述]
示例：/learn the brand and style for this slide deck so
      that I can create new slide decks with this brand
```

### /goal 指令

| 特性 | 说明 |
|------|------|
| 输入 | 持续性目标（standing objective），非单次请求 |
| 行为 | 自主循环工作，直到目标达成并验证 |
| 终止 | 达标验证通过 / 预算耗尽 / 手动暂停 |
| 核心 | 不是「回答问题」，而是「完成任务」 |

关键区别：

```
传统 AI：    用户提问 → AI 回答 → 结束（一次性）
/goal：      用户设目标 → AI 自主规划+执行 → 自我验证 → 达标才停
```

---

## 完成合约（Completion Contracts）

视频称这是 Hermes「刚加入」的功能。其本质是让 Agent 在停止前自行对照成功标准。

### 合约四要素

```
完成合约 = 可衡量的终态（Measurable End State）
         + 发生的证明（Proof That It Happened）
         + 必须保持的约束（Constraints That Must Hold）
         + 防止无限运行的限制（A Limit So It Doesn't Run Forever）
```

### 范式转变

| 旧模式 | 新模式 |
|--------|--------|
| 「我觉得做完了」 | 「这是完成的证明」 |
| 靠感觉终止 | 靠条件验证终止 |
| 需要人工全程盯 | 可以完全无人监督 |

```
          传统模式                    完成合约模式
┌───────────────┐           ┌───────────────────────┐
│  AI 尝试一次   │           │  AI 执行任务           │
│      ↓        │           │      ↓                 │
│  AI 说「好了」 │           │  对照合约条件           │
│      ↓        │           │      ↓                 │
│  人工检查结果   │           │  ✅ 满足 → 停止         │
│      ↓        │           │  ❌ 不满足 → 继续迭代    │
│  人工修正      │           │      ↓                 │
│      ↓        │           │  直到所有条件达标        │
│  交付          │           │      ↓                 │
└───────────────┘           │  交付（附完成证明）      │
                            └───────────────────────┘
```

### 实战流程（视频 #3 示例）

1. 用 `/learn` 读取 Loop Engineering 指南，学习如何写好 /goal 条件
2. 生成「goal-writing」技能
3. 用 `/goal` 调用该技能，将模糊指令重写为严格合约

```
模糊输入：  "make the deck better"
           ↓ /goal + goal-writing skill
严格合约：  可衡量终态 + 验证标准 + 约束条件 + 时间限制
```

视频提醒：首次使用此技能时建议人工复核，因为 Agent 容易写出「看起来像完成合约但实际无法验证」的条件。

---

## 8 大 /learn 应用场景

### 场景总览

| # | 场景 | 分类 | /goal 联动 | 时间戳 |
|---|------|------|-----------|--------|
| 1 | 简报风格复制 | 视觉与模板 | ✅ 全流程演示 | 1:49 |
| 2 | YouTube 教程转技能+学习计划 | 知识萃取 | ✅ 生成 30 天仪表板 | 3:52 |
| 3 | 学写更好的 /goal 条件 | 元学习 | ✅ 重写模糊指令 | 5:16 |
| 4 | 个人写作风格学习 | 语调与人格 | — | 6:56 |
| 5 | 客服语调学习 | 情境应对 | — | 7:02 |
| 6 | 外展邮件写作 | 行销推广 | —（需人工复核） | 7:16 |
| 7 | 月度报告结构复制 | 结构复制 | — | 7:29 |
| 8 | 部署流程指令化 | 流程自动化 | — | 7:44 |

### 场景 1：简报风格复制（完整流程演示）

最完整的 /learn → /goal 联动示例：

```
步骤 1：/learn（教）
┌──────────────────────────────────┐
│ 输入：参考品牌简报（PDF）          │
│ 指令：/learn 此简报的品牌风格      │
│ 分析：颜色、品牌风格、排版布局      │
│ 产出：editorial-slide-deck-brand  │
│       技能（Markdown）             │
└──────────────────────────────────┘

步骤 2：/goal（用）
┌──────────────────────────────────┐
│ 输入：新内容 PDF（原始数据）        │
│ 指令：/goal 用上述技能制作简报      │
│ 执行：读取 PDF → 迭代 → HTML → PDF │
│ 验证：生成内容正确性检查            │
│ 产出：同品牌风格的新简报            │
└──────────────────────────────────┘
```

### 场景 2：YouTube 教程转技能

```
/learn [YouTube URL] + 方法描述
          ↓
Hermes 直接拉取转录稿（无需浏览器/下载）
          ↓
分析方法论 → 生成可应用技能（非摘要）
          ↓
/goal 调用技能 → 生成个性化学习计划
```

视频演示了日语学习教程：从 0 学日语的方法 → 30 天交互式学习仪表板（含每日目标 + 所需资源）。

### 场景 3：学写更好的 /goal（元学习）

将技能系统指向自身：教 Agent 如何写好 /goal 条件。

```
输入：Loop Engineering 指南（URL）
/learn → 提炼 goal-writing 公式
     ↓
不是总结文章，而是将公式转化为可应用过程
     ↓
/goal 调用 → 将 "make the deck better" 重写为严格合约
```

### 场景 4-8：快速场景清单

| 场景 | 输入素材 | 学习成果 | 注意事项 |
|------|----------|----------|----------|
| 个人写作风格 | 多年历史文本 | 精准口吻捕捉 | 甚至发现作者未指出的习惯 |
| 客服语调 | 实际客服对话记录 | 最佳员工化解冲突的沟通细节 | 优于没人读的政策文档 |
| 外展邮件 | 高回复率邮件样本 | 写出「你最佳状态」的推广内容 | 品牌风险高，需人工复核 |
| 月报生成 | 过往月报 | 同格式自动生成 | 只复制结构，不复制判断力 |
| 部署流程 | 一次手动部署过程 | 团队可执行的 /command | 一次性教学，永久复用 |

### 决策树：什么场景适合 /learn？

```
你有重复性工作流吗？
│
├─ 否 → 不需要 /learn，直接对话即可
│
└─ 是 → 该工作流有明确的输入输出格式吗？
    │
    ├─ 否（如创意写作） → 适合「风格/语调」类 /learn（场景 4-6）
    │
    └─ 是 → 需要多步骤自动完成吗？
        │
        ├─ 否 → 单独 /learn 即可
        │
        └─ 是 → /learn + /goal 联动（场景 1-3）
```

---

## Supabase 云端持久化

### 解决的问题

```
本地 Skill 的局限：
┌─────────────┐     换设备/新 Agent     ┌─────────────┐
│  Agent A     │ ──────────────────▶   │  Agent B     │
│  Skills ✅   │     知识丢失！         │  Skills ❌   │
│  (仅存本地)  │                       │  (空白)      │
└─────────────┘                       └─────────────┘
```

### 解决方案：Supabase MCP + 官方 Agent Skill

经搜索验证，Supabase 确实有官方 Agent Skills 项目（github.com/supabase/agent-skills），使用与 Hermes 兼容的开放技能格式（Markdown SKILL.md + YAML frontmatter）。

```
┌─────────────────────────────────────────────────────────┐
│                 Supabase 云端架构                        │
│                                                          │
│  本地 Agent               Supabase 云端                   │
│  ┌───────────┐           ┌───────────────────┐          │
│  │  /learn    │ ── 写入 → │  skills 表         │          │
│  │  Skills    │           │  (一行一个技能)     │          │
│  └───────────┘           │                   │          │
│                          │  原子化操作         │          │
│  ┌───────────┐           │  tenant_id 隔离    │          │
│  │  新 Agent  │ ← 读取 ── │  跨设备共享         │          │
│  │  (空)      │           │                   │          │
│  └───────────┘           └───────────────────┘          │
│       ↓                                                  │
│  30 秒内加载所有技能                                      │
└─────────────────────────────────────────────────────────┘
```

### 配置步骤

```
步骤 1：添加 Supabase Agent Skill
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
"Please add this skill"
→ 指向 Supabase skill URL

步骤 2：连接 Supabase MCP Server
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
"Connect to the Supabase MCP server."
→ Supabase Dashboard → Account → Access Tokens
→ 生成 Personal Access Token → 粘贴

步骤 3：上传本地技能
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
"Save all my learned skills from the local Hermes
 folder into Supabase. One row per skill in the
 skills table. Set the tenant ID and don't skip any."
→ 原子化批量写入

步骤 4：新 Agent 拉取（任意新环境）
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
"Connect to the Supabase MCP server and load all
 the skills from the skills table, especially any
 I don't already have."
→ 30 秒内加载完毕
```

### 官方 Agent Skills 背景

| 信息点 | 内容 |
|--------|------|
| 仓库 | github.com/supabase/agent-skills |
| 安装 | `npx skills add supabase/agent-skills` |
| 测试模型 | Claude Code (Opus/Sonnet)、Codex (GPT-5.4) |
| 效果 | MCP + Skill 组合准确率显著提升（Codex 71%→88%） |
| 核心理念 | 技能提供「如何正确操作」的引导，MCP 提供工具能力 |

---

## 批判性评估

### 视频的可信度

| 维度 | 评估 |
|------|------|
| 核心功能（/learn, /goal） | ✅ 真实，官方文档确认 |
| 完成合约 | ✅ 真实，Hermes /goal draft 功能 |
| Supabase 集成 | ✅ 官方产品，但 Supabase 测试以 Claude Code/Codex 为主 |
| 「秒级拉取」 | ⚠️ 理论可行，实际取决于技能数量和网络 |
| 「Hermes 原生兼容」 | ⚠️ 技能格式兼容（同为 SKILL.md），但非 Hermes 官方深度集成 |

### 营销内容标记

此视频包含多项商业推广：

| 类型 | 内容 |
|------|------|
| Supabase 联盟链接 | `supabase.plug.dev/Rp9T09g`（推广码） |
| Udemy 付费课程 | "Hermes Agent Masterclass" + 折扣码 |
| Skool 社区 | AI Automation Engineering 社区 |
| Newsletter | aifornoncoders.com 邮件订阅 |

### 实用建议

```
适合 /learn + /goal 的任务特征：
┌─────────────────────────────────────────┐
│ ✅ 有明确的输入格式（文件/URL/数据）      │
│ ✅ 有明确的输出标准（格式/结构/验证条件）  │
│ ✅ 重复性高（值得一次教学、多次复用）      │
│ ✅ 可以量化「完成」（可验证的结果）        │
└─────────────────────────────────────────┘

不适合的场景：
┌─────────────────────────────────────────┐
│ ❌ 需要实时人类判断的任务                 │
│ ❌ 输入输出不可预测的创意工作             │
│ ❌ 一次性任务（不值得教技能）             │
│ ❌ 高风险决策（部署生产、发送对外邮件）   │
└─────────────────────────────────────────┘
```

### 技术细节补充

- 视频演示环境：Hermes Agent Desktop + z.ai GLM 5.2 模型
- Loop Engineering 指南（场景 3 用到）：developersdigest.tech 的 /goal 条件写作指南
- Supabase Agent Skills 的核心价值：技能提供「怎么用」的引导，MCP 提供「能不能用」的能力——两者缺一不可

---

## 参考资料

- [影片：Hermes Agent Skills Tutorial: 8 /learn Superpowers](https://www.youtube.com/watch?v=eBGWEngWL7Y)
- [Supabase Agent Skills（官方仓库）](https://github.com/supabase/agent-skills)
- [Supabase AI Skills 官方文档](https://supabase.com/docs/guides/ai-tools/ai-skills)
- [Supabase Blog: Agent Skills](https://supabase.com/blog/supabase-agent-skills)
- [Hermes Agent /goal 官方文档](https://hermes-agent.nousresearch.com/docs/user-guide/features/goals)
- [Hermes Agent Skills 指南](https://hermes-agent.nousresearch.com/docs/guides/work-with-skills)
- [Loop Engineering Guide（场景 3 参考）](https://www.developersdigest.tech/blog/loop-engineering-definitive-guide)

## 相关笔记

- [[Hermes Agent]]
- [[AI Agent Skills]]
- [[Supabase MCP]]
