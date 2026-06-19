---
title: Claude Code 14 大超级能力 — 进阶使用指南
aliases: [Claude Code Superpowers, Claude Code 进阶技巧]
tags:
  - claude-code
  - ai-coding
  - status/active
  - type/doc
source:
  - "https://youtube.com/watch?v=mNawxNjrR_E"
author: Simon Scrapes
created: 2025-06-14
updated: 2025-06-14
description: |
  Claude Code 14 个高杠杆设置与技巧，涵盖 Ultra Code、Auto Mode、Skills 系统、
  MCP vs CLI、长期记忆、Agent 架构等核心主题，适合想将 Claude Code 从聊天工具
  升级为生产级 Agent 的开发者。
level: intermediate
stars: 4
---

# Claude Code 14 大超级能力 — 进阶使用指南

> 视频总结了 14 个让 Claude Code 从"稍好的聊天机器人"蜕变为"全业务驱动引擎"的高杠杆设置。内容基于 Simon Scrapes 社区数千用户的实战经验，涵盖最新功能（Ultra Code、Auto Mode、/loop + /goal）到基础架构（Skills 系统、MCP vs CLI、长期记忆），再到高级技巧（Slot Machine Theory、Sub Agent vs Skill、VPS 远程部署）。适合已有 Claude Code 使用经验、想系统化提升效率的中级用户。

## 目录

- [[#1. Ultra Code — 自主多 Agent 工作流]]
- [[#2. Auto Mode — 智能权限分类器]]
- [[#3. 长期自主任务：/loop + /goal]]
- [[#4. Skills 最佳实践与 Skill Systems]]
- [[#5. MCP vs CLI — 工具连接策略]]
- [[#6. 长期记忆：语义搜索取代关键词]]
- [[#7. CLAUDE.md 与 Agentic Operating System]]
- [[#8. 规划策略：Plan Mode vs PRD]]
- [[#9. Slot Machine Theory — 不要纠缠错误]]
- [[#10. Agent View — 多任务并行管理]]
- [[#11. 可移植性 — 避免 Vendor Lock-in]]
- [[#12. VPS 远程部署 — 手机控制 Claude Code]]
- [[#13. Sub Agent vs Skill — 何时用哪个]]
- [[#14. 实战案例：LinkedIn Carousel Skill System]]

---

## 1. Ultra Code — 自主多 Agent 工作流

### 核心概念

Ultra Code（Dynamic Workflows）让 Claude 自主设计工作流，创建专门的子 Agent 团队，每个 Agent 拥有独立上下文和单一职责，由协调者统一管理输入输出。

### 六种工作流模式（来自 Anthropic 官方文档）

```
+------------------+     +------------------+     +------------------+
|  Classify & Act  |     | Fan-out & Synth  |     |   Adversarial    |
|  按任务类型路由   |     | 并行探索 → 合并   |     |   Verification   |
|  A → path A      |     | branch×4 → merge |     |  Agent A → B验证  |
+------------------+     +------------------+     +------------------+

+------------------+     +------------------+     +------------------+
| Gen & Filter     |     |    Tournament    |     | Loop Until Done  |
| 批量生成 → 去重  |     |  Agent 间竞争     |     |  满足条件才停止   |
| options → best  |     |  → 选出胜者       |     |  (类似 ReAct)    |
+------------------+     +------------------+     +------------------+
```

### 使用方式

```bash
# 在 Claude Code 中直接输入 ultra code + 提示
ultra code perform deep research on Claude Code's latest features in the desktop app
```

Claude 会自动选择并组合工作流模式。例如"深度研究"任务会使用 Fan-out & Synthesize 模式：5 个 Scout Agent 并行搜索 → Deep Read 8 个来源 → 验证 → 综合报告。

### 对比表格：Ultra Code vs 单 Agent

| 维度 | 单 Agent | Ultra Code |
|------|----------|------------|
| Token 消耗 | 低（单上下文） | 高（实测 670K tokens） |
| 任务复杂度 | 短/简单任务 | 长/复杂任务 |
| 上下文退化 | 严重（context bloat） | 每个 Agent 独立上下文 |
| 提前终止 | 高概率 | 低概率（目标驱动） |
| 执行时间 | 分钟级 | 13+ 分钟（复杂任务） |

### 最佳实践

- ✅ 用于：深度研究、多源验证、大规模重构
- ❌ 避免用于：简单文件修改、单步操作
- ✅ 通过 `/workflows` 命令监控子 Agent 状态
- ✅ 工作流可保存复用，类似 Skills

---

## 2. Auto Mode — 智能权限分类器

### 三种权限模式对比

| 模式 | 行为 | 风险 | 适合场景 |
|------|------|------|----------|
| 默认模式 | 每次文件写入/命令都要确认 | 极低 | 学习、不熟悉代码 |
| Dangerously Skip Permissions | 完全自由 | 极高 | CI/CD 自动化 |
| **Auto Mode** | 分类器判断风险，仅高风险时询问 | 中等 | **日常开发（推荐）** |

### 切换方式

```bash
# Shift+Tab 两次进入 Auto Mode
# 可直接交办任务后离开电脑
```

### 判断决策树

```
需要 Claude 自主工作时：
  ✅ Auto Mode → 日常开发、中等风险任务
  ❌ Skip Permissions → 除非 CI/CD 环境
  ❌ 默认模式 → 需要频繁监督时不选
```

---

## 3. 长期自主任务：/loop + /goal

### 核心概念

两个命令组合解决"Claude 提前停止"问题：
- `/loop`：设置循环间隔，让提示词周期性执行（最长 3 天）
- `/goal`：设置终止条件，Claude 每轮检查是否达成

```
/loop every day + /goal inbox is empty
        │                │
        │                └─ 每轮检查：条件满足？
        │                    ✅ → 停止
        │                    ❌ → 生成新 Agent 继续执行
        └─ 每天注入提示词
```

### 实战示例：自动邮件分类

```bash
# 1. 设置循环
/loop every day

# 2. 设置目标条件
/goal Connect to my Gmail. File any emails from the last
hour into category folders. Stop when inbox is empty.

# 3. Claude 自动执行：
#    - 连接 Gmail → 搜索最近邮件 → 分类归档 → 检查 inbox
#    - 未清空 → 继续
#    - 清空 → "Goal achieved. 1 minute, 1 turn, 2K tokens."
```

### 最佳实践

- ✅ `/loop` + `/goal` 组合 = 自主任务的标准模式
- ✅ 适合日常重复任务（邮件、文件整理、监控）
- ❌ `/loop` 有 3 天上限，超长周期任务需外部调度

---

## 4. Skills 最佳实践与 Skill Systems

### 好的 Skill 结构

```
skill.md (< 200 行)
  ├── name + description（触发条件 + 排除条件）
  ├── step-by-step 指令（< 200 行，Claude 可高效加载）
  ├── Progressive Disclosure（按需加载额外上下文）
  └── Self-learning rules（每次运行后记录反馈）

references/（独立文件夹）
  ├── context-1.md    ← 按需加载
  ├── context-2.md
  └── templates/
```

### Skill Systems — 核心理念

Skill System 是多个 Skills 链接而成的端到端流水线，一个 Skill 的输出是下一个的输入。

```
                    Skill System: 社交内容创作
+--------+    +--------+    +--------+    +--------+
| 品牌    | → | 内容    | → | 图片    | → | 发布    |
| 语音    |    | 撰写    |    | 生成    |    | 工具    |
+--------+    +--------+    +--------+    +--------+
     ↑             ↑                          ↑
  可复用        可复用                      可复用于
  其他系统      其他系统                    其他系统
```

视频中的社交内容创作 Skill System 由 **18 个 Skills** 组成，包括：
- 品牌语音（Brand Voice）
- 视觉标识（Visual Identity）
- 内容抓取（LinkedIn/URL scraper）
- 图片生成 Agent
- 模板构建器

### 对比表格：单个大 Skill vs Skill System

| 维度 | 单个大 Skill | Skill System |
|------|-------------|--------------|
| 可复用性 | 低（绑定特定流程） | 高（各 Skill 可自由组合） |
| 维护性 | 差（改一处影响全局） | 好（改品牌语音不影响图片生成） |
| 上下文效率 | 差（一次加载所有指令） | 好（按需加载各 Skill） |
| 组合灵活性 | 无 | 可组合成多个不同系统 |

### 最佳实践

- ✅ 每个 Skill < 200 行，保持精简
- ✅ 额外上下文放 references/ 文件夹
- ✅ Skill 设计为"乐高积木"而非"巨型单体"
- ✅ 共享 Skill 被 4 个系统复用 = 改一处更新四处
- ❌ 不要把 Skill 写成只适用于单一场景

---

## 5. MCP vs CLI — 工具连接策略

### 核心区别

```
MCP Server（Model Context Protocol）:
  ┌──────────────────────────────┐
  │ 工具定义始终加载在上下文中      │ ← 无论是否使用都占 token
  │ Claude 始终知道可用工具        │ ← 支持发现 + 链式调用
  │ 适合：日常高频交互             │
  └──────────────────────────────┘

CLI Tool:
  ┌──────────────────────────────┐
  │ 按需执行，执行后释放           │ ← 不占 token
  │ 简单命令行调用                 │ ← 单次交互
  │ 适合：低频、简单、可预测操作   │
  └──────────────────────────────┘
```

### 对比表格

| 维度 | MCP Server | CLI Tool |
|------|-----------|----------|
| Token 成本 | 持续占用（数千 tokens） | 仅执行时消耗 |
| 发现能力 | Claude 可自动发现工具链 | 需明确指定命令 |
| 交互深度 | 多轮复杂交互 | 单次调用 |
| 适用场景 | CRM、数据库等高频工具 | 发消息、取文件、跑脚本 |

### 判断决策树

```
需要连接工具时：
  频繁使用 + 多工具链式调用？
    ✅ → MCP Server（值得 token 成本）
  简单操作 + 偶尔使用？
    ✅ → CLI Tool（按需调用，零常驻成本）
```

---

## 6. 长期记忆：语义搜索取代关键词

### 问题

Claude Code 默认记忆机制基于**关键词搜索**，会丢失压缩后的上下文。用户反复解释相同背景信息。

### 解决方案：三层记忆架构

```
+------------------+     +------------------+     +------------------+
|     Storage      |     |    Injection     |     |    Retrieval     |
| 如何决定存储信息   |     | 注入相关上下文    |     |    Recall        |
|                  |     | 到短期记忆        |     |  回忆 6 个月前   |
| CLAUDE.md rules  | →  | 自动注入上下文    | →  | 语义搜索         |
| Skill rules      |     | 避免手动检索      |     | (向量数据库)     |
+------------------+     +------------------+     +------------------+

推荐开源框架：MemSearch, Hermes, OpenClaw
```

### 对比表格：关键词搜索 vs 语义搜索

| 维度 | Claude 内置（关键词） | 外部框架（语义） |
|------|---------------------|-----------------|
| 搜索方式 | 关键词匹配 | 向量相似度 |
| 上下文丢失 | 高（compact 后） | 低（存储在向量 DB） |
| 成本 | 零额外成本 | 需维护外部服务 |
| 准确性 | 受 context bloat 影响 | 按语义相关性召回 |

---

## 7. CLAUDE.md 与 Agentic Operating System

### 核心概念

CLAUDE.md 是 Claude 启动时读取的全局配置文件。进阶用法是通过文件夹结构实现上下文注入：

```
project/
├── CLAUDE.md          ← 主指令文件，引用子文件夹
├── context/
│   ├── brand/
│   │   ├── voice.md       ← 品牌语调
│   │   └── visual.md      ← 视觉标识
│   ├── clients/
│   │   ├── client-a.md    ← 客户 A 专属上下文
│   │   └── client-b.md    ← 客户 B 专属上下文
│   └── workflows/
│       └── review.md      ← 流程规范
└── skills/
    └── copywriting/
        ├── SKILL.md
        └── references/
```

### 最佳实践

- ✅ CLAUDE.md 引用文件夹，而非内联所有上下文
- ✅ 按品牌/客户/工作流分离上下文
- ✅ Skills 中引用具体文件路径（"需要品牌语音时读取 context/brand/voice.md"）
- ✅ 这就是 Agentic Operating System 的本质——在正确时机注入正确上下文

---

## 8. 规划策略：Plan Mode vs PRD

### 关键洞察

Plan Mode 默认将计划保存到全局临时文件夹，context compaction 后 Claude 会丢失原计划。

```
❌ 错误做法：
  Plan Mode → 全局临时文件夹 → context 压缩后丢失

✅ 正确做法：
  Plan Mode / PRD → 项目文件夹内保存 → 可随时重读
```

### 判断决策树

```
选择规划方式：
  复杂多 Agent 任务？
    ✅ → Ultra Code（自动规划 + 执行）
  中等任务（< 1 小时）？
    ✅ → Plan Mode（快速规划）
  长期项目（> 1 小时 session）？
    ✅ → PRD 文件写入项目文件夹
       → Plan 中引用该文件，context 丢失后可重读
```

---

## 9. Slot Machine Theory — 不要纠缠错误

### 核心概念

来自 Anthropic 团队的建议：**把 Claude 当作老虎机（Slot Machine）**。一次拉杆（执行）结果不好时，不要和机器争论——直接重置再拉一次。

### 问题模式

```
正常流程：
  Prompt → 输出 A（错误）→ 纠正 → 输出 B（更差）→ 再纠正 → 输出 C（更差）

Slot Machine Theory：
  Prompt → 输出 A（错误）→ /reset → 附加上下文 → 重新执行 → 输出 D（更好）
```

每次纠正都会把旧错误代码和错误上下文累积到对话中，导致输出越来越差。

### 解决方案：使用 /rewind

```bash
# /rewind 让你回滚到会话中任意检查点
# 相当于"巨型撤销按钮"
/re
```

### 最佳实践

- ✅ 结果不理想 → 不要说"No, fix this"
- ✅ 用 /rewind 回到出错前的检查点，附加新上下文后重新执行
- ❌ 不要在错误输出上反复修补（沉没成本陷阱）

---

## 10. Agent View — 多任务并行管理

### 使用方式

```bash
# 在终端输入
claude agents
```

Agent View 是一个仪表盘视图，按仓库和状态分组所有 Agent 对话，支持直接跳转或回复。

### 对比表格

| 维度 | 旧方式（多终端） | Agent View |
|------|-----------------|------------|
| 管理 | 手动切换终端 | 统一仪表盘 |
| 视角 | 单会话 | 目标驱动（按仓库/状态） |
| 回复 | 切到对应终端 | 直接在 Dashboard 回复 |

---

## 11. 可移植性 — 避免 Vendor Lock-in

### 三大开放标准

| 标准 | Claude Code 等价物 | 其他工具支持 |
|------|------------------|-------------|
| `AGENTS.md` | `CLAUDE.md` | Codex, Cursor, Copilot |
| `SKILL.md` | Skills | 开放标准 |
| MCP / CLI | 工具连接 | 所有主流工具 |

### 最佳实践

- ✅ 确保你的 Skills、CLAUDE.md、MCP 可移植到其他工具
- ✅ CLAUDE.md 内容可复制为 AGENTS.md 供其他工具使用
- ❌ 不要将所有逻辑绑定在 Claude Code 专有功能上

---

## 12. VPS 远程部署 — 手机控制 Claude Code

### 核心思路

Claude Code 内置的 Remote Control 和 Channels 无法在断连后保持后台运行。解决方案：

```
手机 (Telegram/Discord)
        │
        │ SSH / Tailscale
        ▼
+------------------+
| VPS（永不休眠）   │ ← Claude Code 持久运行
| + Claude Code    │
| + Tmux           │ ← 保持会话活跃
+------------------+
```

### Claude Code Channels 对比

| 方案 | 后台执行 | 手机派发 | 断连续跑 | 复杂度 |
|------|---------|---------|---------|--------|
| 内置 Channels | ❌ | ✅ | ❌ | 低 |
| Tmux + VPS | ✅ | ✅ | ✅ | 中 |
| Hermes/OpenClaw | ✅ | ✅ | ✅ | 不透明 |

**推荐方案**：Claude Code Channels + Tmux + VPS = 手机派发任务后离开，审批通过 Telegram 回传，使用现有 Pro/Max 订阅，无额外 API 成本。

---

## 13. Sub Agent vs Skill — 何时用哪个

### 核心框架

```
Skill = Claude 知道如何做的事（指令）
Sub Agent = 执行工作的人（角色 + 独立上下文）

本质区别：
  Skill  = Sub Agent - Role（分离角色和任务）
  Sub Agent = Role + Task（角色和任务绑定）
```

### 判断决策树

```
选择 Sub Agent 如果：
  ✅ 工作会产生大量不需要主会话的上下文
  ✅ 需要不同工具/权限/模型
  ✅ 需要并行执行多个相同任务

选择 Skill 如果：
  ✅ 需要中间上下文回到主会话
  ✅ 任务简单，不需要隔离
  ✅ 上下文需要共享

Skill 可以随时 handoff 给 Sub Agent：
  主会话(Skills 链) → 图片生成步骤 → handoff → Image Gen Sub Agent
```

### 实战案例

LinkedIn Carousel Skill System 中：
- 主流程：18 个 Skills 链式执行（无独立上下文）
- 图片生成步骤：handoff 给 Image Gen Sub Agent（上下文隔离——主流程不需要看到样式参考和生成脚本）
- 理由：Context Isolation + Separation of Concerns

---

## 14. 实战案例：LinkedIn Carousel Skill System

完整展示了 Skill System 的设计理念：

```
输入（URL/主题）
    │
    ▼
[Brand Voice Skill] ──→ 品牌语调指导
    │
    ▼
[Content Scraper Skill] ──→ 抓取 LinkedIn/URL 内容
    │
    ▼
[Copywriting Skill] ──→ 撰写内容
    │
    ▼
[Image Gen Sub Agent] ──→ 上下文隔离生成图片
    │
    ▼
[Template Builder Skill] ──→ 组装轮播图
    │
    ▼
[Publish Skill] ──→ 发布
    │
    ▼
输出：完整的社交帖子 + 平台适配图片/轮播
```

### 设计要点

1. **可复用**：Brand Voice 和 Visual Identity 可用于任何内容创建系统
2. **可维护**：改品牌语调只改一个 Skill，所有系统自动更新
3. **上下文隔离**：图片生成在 Sub Agent 中，不污染主流程上下文
4. **关注点分离**：主流程只接收最终图片，不知道生成细节

---

## 参考资料

- [14 GENIUS Ways to Give Claude Code SUPERPOWERS - YouTube](https://youtube.com/watch?v=mNawxNjrR_E)
- [Simon Scrapes YouTube Channel](https://www.youtube.com/@simonscrapes)
- [Anthropic Dynamic Workflows 文档](https://docs.anthropic.com/en/docs/claude-code/workflows)

## 相关笔记

- [[Claude Code 使用笔记]]
- [[AI Coding Agent 对比]]
