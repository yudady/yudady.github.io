---
title: Google DESIGN.md - 用一个文件打包整个 AI 设计团队
aliases: [DESIGN.md, design.md, AI 设计规范文件, Stitch design.md]
tags:
  - design-system
  - ai-workflow
  - google
  - frontend
  - status/active
  - type/video-note
source:
  - "https://www.youtube.com/watch?v=JuUjUrWPVhE"
author: 思思主播（解读） / Google Labs（原始规范）
created: 2026-05-08
updated: 2026-05-08
description: Google Stitch 团队开源的 DESIGN.md 规范，用一份 Markdown 文件将完整设计系统描述给 AI Agent，解决多页面设计漂移问题
level: intermediate
stars: 4
note: 基于 NotebookLM 转录 + GitHub Spec + 外部搜索综合整理
---

# Google DESIGN.md - 用一个文件打包整个 AI 设计团队

> DESIGN.md 是 Google Labs 开源的格式规范，用一份 Markdown 文件向 AI 编码 Agent 描述完整视觉识别系统（Visual Identity），确保跨页面、跨工具的设计一致性。GitHub 仓库 10 天斩获 4 万 star。

---

## 目录

- [背景：AI 设计漂移问题](#背景ai-设计漂移问题)
- [DESIGN.md 规范结构](#designmd-规范结构)
- [食谱与食材：三层架构](#食谱与食材三层架构)
- [拆分 Agent 的底层逻辑](#拆分-agent-的底层逻辑)
- [90/10 迭代法则](#9010-迭代法则)
- [品位是终极竞争力](#品位是终极竞争力)
- [实际工作流示例](#实际工作流示例)

---

## 背景：AI 设计漂移问题

### 什么是设计漂移

当使用 AI 工具（如 v0、Bolt、Cursor）生成页面时，第一页通常很完美，但一旦开始做第二页或转成手机版，整个设计感觉就变了——字体、排版、特效全部不一致。

**根本原因**：你脑中的设计判断（字体选择、排版节奏、特效拿捏）没有系统化记录下来，AI 每次都在"盲猜"。

```
设计漂移的本质：
  用户脑中的设计规则 ──✗──→ AI 上下文（每次都不同）
  
  结果：页面 A 和页面 B 看起来不像同一个产品
```

### DESIGN.md 的定位

```
AGENTS.md = AI 的人格 + 通用技能（系统级）
DESIGN.md = AI 的视觉规范（项目级）
```

DESIGN.md 是 **写给 AI 读的设计系统文件**，把它放进代码仓库根目录，AI 编码时就能自动读取并遵循设计约束。

---

## DESIGN.md 规范结构

### 文件格式

DESIGN.md 由两部分组成：**YAML frontmatter**（机器可读的 Design Token）+ **Markdown 正文**（人类可读的设计原理）。

```yaml
---
version: alpha
name: Daylight Prestige
description: 高对比中性色调配单一强调色的专业设计系统
colors:
  primary: "#1A1C1E"
  secondary: "#6C7278"
  tertiary: "#B8422E"
  neutral: "#F7F5F2"
typography:
  h1:
    fontFamily: Public Sans
    fontSize: 48px
    fontWeight: 600
    lineHeight: 1.1
    letterSpacing: -0.02em
  body-md:
    fontFamily: Public Sans
    fontSize: 16px
    fontWeight: 400
    lineHeight: 1.6
spacing:
  base: 16px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 32px
  xl: 64px
rounded:
  sm: 4px
  md: 8px
  lg: 12px
  full: 9999px
---
```

### 八大 Section（按顺序）

| # | Section | 说明 |
|---|---------|------|
| 1 | Overview / Brand & Style | 品牌调性、目标用户、情感基调 |
| 2 | Colors | 色彩调色板 + 语义角色 |
| 3 | Typography | 字体层级（9-15 级） |
| 4 | Layout & Spacing | 网格模型、间距策略 |
| 5 | Elevation & Depth | 视觉层次表达方式 |
| 6 | Shapes | 圆角、形状语言 |
| 7 | Components | 按钮等组件规范 |
| 8 | Do's and Don'ts | 实用准则和常见陷阱 |

### Design Token Schema

```
┌────────────────────────────────────────────────┐
│                DESIGN.md Token 结构              │
├────────────────────────────────────────────────┤
│                                                │
│  colors:     map<string, Color>                │
│  typography: map<string, Typography>           │
│  spacing:    map<string, Dimension|number>     │
│  rounded:    map<string, Dimension>            │
│  components: map<string, map<string, string>>  │
│                                                │
│  Token 引用语法: {colors.primary-60}           │
│  可互相引用，组件可引用色彩/字体 token          │
│                                                │
│  兼容转换: tokens.json / Figma 变量 /          │
│           Tailwind theme config                │
└────────────────────────────────────────────────┘
```

---

## 食谱与食材：三层架构

知名设计师 Mike 和 Gregenberg 提出的工作流框架，将给 AI 的指令分为三层：

```
┌─────────────────────────────────────────┐
│         第一层：食谱（DESIGN.md）         │
│   字体、颜色、间距等最基础的设计基底      │
│   ── 改这一层：整体风格歪了              │
├─────────────────────────────────────────┤
│         第二层：成品（HTML 样品）          │
│   提供具体 HTML 给 AI 对齐目标与排版节奏  │
│   ── 改这一层：排版不如预期              │
├─────────────────────────────────────────┤
│         第三层：食材（Skill 文件）         │
│   单一技法：镭射特效、3D 物件、文案风格   │
│   ── 改这一层：某个特效跑掉              │
└─────────────────────────────────────────┘
```

**关键价值**：AI 出错时能精确定位该修哪一层，调试过程系统化，大幅降低面对 AI 的挫折感。

---

## 拆分 Agent 的底层逻辑

### 核心原则：职责分离

```
❌ 错误做法：把所有指令塞进 AGENTS.md
   → 吃掉大量 Token，执行不稳定

✅ 正确做法：
   AGENTS.md → 系统级，设定通用人格
   DESIGN.md → 项目级，每个产品独立一份
   Skill 文件 → 工作流级，可复用的单一技法
```

```
┌─────────────────────────────────────────────┐
│              职责分离模型                     │
│                                             │
│  系统级 (AGENTS.md)  →  人格 + 通用行为      │
│  项目级 (DESIGN.md)  →  视觉规范             │
│  工作流级 (Skill)    →  可复用技法            │
│                                             │
│  ✅ 防呆：避免跨项目污染                      │
│  ✅ 稳定：AI 跑得更准、更稳                   │
│  ✅ 省钱：Token 消耗更少                     │
└─────────────────────────────────────────────┘
```

**一句话类比**：你不会把品牌设计规范塞进系统提示词里，就像你不会把公司 VI 手册塞进员工手册里。

---

## 90/10 迭代法则

设计师 MK 的时间分配法则：

```
┌────────────────────────────────┐
│  90% 精力 → 疊代（Iteration）   │
│    专注单一产品，收敛细节       │
│    微调字句、打磨特效           │
│                                │
│  10% 精力 → 擴散（Expansion）   │
│    扩展到新媒介                │
│    手機版、IG 圖文等            │
└────────────────────────────────┘
```

**常见误区**：把收敛细节和扩展媒介混在一起做。结果每个版本都不及格，看起来很忙但没有品质累积。

**正确做法**：
- ✅ 先完成单一版本的迭代收敛
- ✅ 停下来做每一个微小判断
- ✅ 收敛完成后才切到下一个媒介

---

## 品位是终极竞争力

### 品位不是天生的

AI 生成的通用内容（如"AI 紫色渐变"）正在快速贬值，**个人独特的品位反而越来越值钱**。

设计师 MK 的品位训练方法：
1. 每个产品执行 **上千次提示词迭代**
2. 刻意看 **海量好设计**
3. **亲自使用**利基市场里所有的 APP
4. **追踪**所有相关创作者

### 知识工作者的品位建设四步法

```
Step 1: 截图收藏
  看到好设计就截图，逼自己写一句话解释好在哪

Step 2: 整理进 DESIGN.md
  把日常观察的洞察整理进专属 DESIGN.md

Step 3: 喂给 AI
  DESIGN.md + HTML 样品 + Skill 一起作为 AI 上下文

Step 4: 跨工具迁移
  无论换 Cursor 还是 Claude Code，无缝接轨
```

> "AI 并没有让我变得更懒，反而让我写了更多的提示词，制作了更多的影片，做了更多的行销。" — 设计师 MK

---

## 实际工作流示例

### Stitch + DESIGN.md + Claude Code 六步流程

```bash
# Step 1: 获取或编写 DESIGN.md
# 可从 Google 官方获取 62 家企业的设计模板
# 仓库: google-labs-code/design.md

# Step 2: 放入项目根目录
cp DESIGN.md /your-project/DESIGN.md

# Step 3: 在 Claude Code 中启动
# AI 自动读取 DESIGN.md 作为设计约束上下文

# Step 4: 生成页面
# "基于 DESIGN.md 创建一个落地页"

# Step 5: 迭代收敛（90% 精力）
# 微调细节，确保符合规范

# Step 6: 扩散到新媒介（10% 精力）
# 基于同一 DESIGN.md 生成手机版/暗色版
```

### 与现有工具的兼容性

| 来源 | 转换目标 | 说明 |
|------|----------|------|
| DESIGN.md YAML tokens | Figma Variables | 双向同步 |
| DESIGN.md YAML tokens | Tailwind theme config | 直接映射 |
| DESIGN.md YAML tokens | tokens.json (W3C spec) | 格式兼容 |
| Figma / Storybook | DESIGN.md | 也可从设计工具反向生成 |

---

## 参考资料

- [Google DESIGN.md 官方 Spec（GitHub）](https://github.com/google-labs-code/design.md/blob/main/docs/spec.md) — 10.6k star
- [Google 官方博客：Stitch 开源 DESIGN.md](https://blog.google/innovation-and-ai/models-and-research/google-labs/stitch-design-md/)
- [Google DESIGN.md 实践（thefivekey.com）](https://www.thefivekey.com/design-system-as-markdown/)
- [DESIGN.md Explained（Department of Product）](https://departmentofproduct.substack.com/p/designmd-explained-the-format-reshaping)
- [腾讯云：Google Design.md 一键复刻 60+ 大厂设计规范](https://cloud.tencent.com/developer/article/2654912)

## 相关笔记

- [[AGENTS.md 规范]] — AI Agent 的系统级人格定义
- [[AI UI 生成工具对比]] — v0, Bolt, Cursor 等工具横向对比
- [[Design Token 体系]] — W3C Design Token 标准与工具链
