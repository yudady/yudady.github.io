---
title: Gemini 3.5 Flash & Pro — Google I/O 2026 泄漏前瞻
aliases:
  - Gemini 3.5
  - Gemini Cappuccino
tags:
  - gemini
  - google
  - frontier-ai
  - status/leak
  - type/video-note
source:
  - "https://www.youtube.com/watch?v=oekV5AQGRCY"
author: WorldofAI (频道)
created: 2026-05-16
updated: 2026-05-16
description: Google I/O 2026 前 Gemini 3.5 Flash/Pro 在 Arena AI 泄漏的实测报告
level: intermediate
stars: 3
note: 基于泄漏信息，Gemini 3.5 尚未正式发布，内容可能随正式发布变化
---

# Gemini 3.5 Flash & Pro — Google I/O 2026 泄漏前瞻

> Google I/O 2026（约 5 月 20 日）前，Gemini 3.5 系列通过 Arena AI 泄漏，Flash 和 Pro 两个变体均已实测。视频由 WorldofAI 频道基于 LM Arena 的盲测结果制作。

---

## 背景：Gemini 版本演进

Google 的 Gemini 命名在近期经历了一次跳跃：

```
Gemini 2.5 Pro/Flash (2025 I/O)
    │
    ├── Gemini 3 Pro (2025-11)
    ├── Gemini 3 Flash (2025-12)
    │
    ├── Gemini 3.2 系列 (内部测试，未发布)
    │   ├── Flash: Fanta / Sprite / Cola checkpoints
    │   └── Pro: 首个 3.2 checkpoint
    │
    └── Gemini 3.5 系列 (本次泄漏)  ← 即将在 I/O 2026 发布
        ├── Flash: Arena 标记为 "Gemini 3.0 0 flash"
        └── Pro: 内部代号 "Cappuccino"
```

**关键变化**：3.2 直接跳到 3.5，跳过了 3.3/3.4 编号。

---

## Gemini 3.5 Flash 实测表现

### 优势

**前端/UI 生成 — 核心亮点**

| 对比维度 | Gemini 3.5 Flash | Claude Opus 4.7 | Gemini 3.1 Pro |
|---------|-----------------|-----------------|----------------|
| 布局层级 | 干净清晰 | 优秀 | 优秀 |
| 创意多样性 | 高 | 中 | 中 |
| 空间一致性 | 显著提升 | 高 | 中高 |
| UI 重复性 | 比前代改善 | 低 | 中 |

- **Minecraft 克隆**：完整生成了包含服务器连接、移动控制、游戏模式切换、生物、血条等元素的沙盒游戏，这在之前任何模型中均未见过
- **ASCII Art**：成功生成了一只骑自行车的鹈鹕的完整 ASCII 艺术，包含色相、字符大小、扫描线等可调参数。多数模型在此类任务中途崩溃，但 3.5 Flash 完整输出了连贯结果
- 前端生成质量已可与 Claude Opus 4.7 和 Gemini 3.1 Pro 竞争，部分场景甚至超越

**推理与一致性**
- 推理能力明显增强
- 输出更干净，定性一致性显著
- 指令遵循（Instruction Following）大幅改善
- 速度感和定价为 Flash 级别，性价比极高

### 弱点

| 问题 | 严重程度 | 说明 |
|------|---------|------|
| 过度 UI 化 | **高** | 无论 prompt 如何要求极简，都会塞满 HUD、浮动面板、仪表盘 |
| 风格同质化 | 中 | 被称为"GPT-ification"——反复生成相同的 SaaS 展示风格 |
| Prompt 遵守不足 | 中 | 明确要求不使用 web 访问时仍会调用，影响知识截止日期验证 |
| macOS 克隆退步 | 低 | 3.2 Pro 版本的 macOS 克隆（含 SVG 图标、小游戏）反而更完整 |

**UI 过度化的本质**：模型似乎在现代 SaaS UI showcase 美学上过度训练，形成了固定的输出模式。

---

## Gemini 3.5 Pro（代号 Cappuccino）

信息有限，但视频暗示：
- 输出质量"真正卓越"
- 是 3.5 系列中更强大的变体
- 具体测试结果未在视频中充分展示

---

## 与竞品的定位判断

```
                    代码生成质量
                         ▲
            Claude Opus 4.7 ●
                         │
          Gemini 3.1 Pro  ●
                         │
       Gemini 3.5 Flash  ●───  ← 性价比最强
                         │
       Gemini 3.2 Pro    ●
                         │
    ─────────────────────┼────────────► 价格
                         │
                    (Flash = 最低价)
```

**判断决策树**：

```
需要最强代码质量？
├── 是 → Claude Opus 4.7 或 Gemini 3.5 Pro（待正式发布）
└── 否
    ├── 预算敏感？
    │   ├── 是 → Gemini 3.5 Flash（Flash 价格，接近 Pro 质量）
    │   └── 否 → Gemini 3.1 Pro
    └── 需要 UI 生成？
        ├── 极简风格 → Claude（Gemini 会过度设计）
        └── 功能丰富 → Gemini 3.5 Flash
```

---

## 实际使用方式

截至视频发布时，Gemini 3.5 Flash 可通过 Arena AI 的 Battle Mode 随机抽到：

1. 访问 [arena.ai](https://arena.ai/)
2. 进入 Battle Mode
3. 发送任意 prompt
4. 有概率匹配到 Gemini 3.5 Flash（显示为 "Gemini 3.0 0 flash"）

**注意**：也可能匹配到之前的 3.2 checkpoint，无法稳定复现。

---

## 关键信息验证

| 声称 | 验证状态 | 来源 |
|------|---------|------|
| Gemini 3.5 将在 I/O 2026 发布 | ⏳ 待确认 | 频道推测，非官方声明 |
| 3.5 Flash 内部标记为 "Gemini 3.0 0 flash" | ✅ Arena 实测 | Arena AI 泄漏 |
| 3.5 Pro 代号 "Cappuccino" | ⏳ 待确认 | 社区泄漏 |
| 3.2 跳到 3.5 命名 | ✅ 确认 | Arena 显示的模型名 |

> **官方状态**（2026-05-16）：Google 官方 Gemini API 文档最高显示 Gemini 3 系列（Gemini 3 Flash/Pro），尚无 Gemini 3.5 正式条目。

---

## 参考资料

- [视频来源：WorldofAI - Gemini 3.5 Flash + Pro](https://www.youtube.com/watch?v=oekV5AQGRCY)
- [Google Gemini 官方模型文档](https://ai.google.dev/gemini-api/docs/models)
- [Reddit 讨论：Google I/O 泄漏 Gemini Omni / 3.2 / 3.5](https://www.reddit.com/r/Bard/comments/1t2mw8a/google_io_leaks_geminis_omni_push_and_gemini_3235/)
- [Arena AI](https://arena.ai/)

## 相关笔记

- [[Gemini 3 系列]]
- [[Google I/O 2026]]
