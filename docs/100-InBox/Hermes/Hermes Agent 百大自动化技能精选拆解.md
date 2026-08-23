---
title: Hermes Agent 百大自动化技能精选拆解
aliases: [Hermes Skills Top 14, Dubibubi Hermes skills, Agent Reach]
tags:
  - hermes-agent
  - ai-agent
  - skills
  - automation
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=IbFaY3xFpZM"
author: Dubibubi (Dubi)
created: 2026-07-26
updated: 2026-07-26
description: Dubibubi 频道测试 100+ Hermes Agent 自动化技能后，精选 14 个 + 3 个荣誉提名的完整拆解笔记
level: intermediate
stars: 5
---

# Hermes Agent 百大自动化技能精选拆解

> 频道 Dubibubi（Dubi）实测 100+ Hermes 技能后的 Top 14 精选 + 3 荣誉提名。核心论点：好的 skill 保持精简（< 15KB），技能生态正从"单一提示词"走向"自我修复 + 多 Agent 委员会 + 基础设施层"。

## 目录

- [核心概念：什么是 Skill](#核心概念什么是-skill)
- [一、系統演化與資源優化（#14-#11）](#一系統演化與資源優化14-11)
- [二、開發流程與上下文控管（#13-#2）](#二開發流程與上下文控管13-2)
- [三、能力擴展與外部軟體整合（#10-#7）](#三能力擴展與外部軟體整合10-7)
- [四、多 Agent 協同與架構層（#6-#3）](#四多-agent-協同與架構層6-3)
- [五、頂級關鍵技能與榮譽提名（#1 + HM）](#五頂級關鍵技能與榮譽提名1--hm)
- [快速選型決策樹](#快速選型決策樹)
- [三步行動建議](#三步行動建議)
- [全技能一覽表](#全技能一覽表)
- [參考資料](#參考資料)

---

## 核心概念：什么是 Skill

Skill 实质是精简的 Markdown 规范文件，教 Agent 如何一步步完成任务。

```
Skill 生命周期
┌─────────────┐    ┌──────────────┐    ┌─────────────┐
│ GitHub repo │───>│ hermes agent │───>│ 常驻 agent   │
│ (SKILL.md)  │    │ 读取并安装    │    │ memory 中   │
└─────────────┘    └──────────────┘    └─────────────┘
                                              │
                                              v
              ┌───────────────────────────────────────┐
              │ 任务触发：自动匹配 或 用户显式调用      │
              └───────────────────────────────────────┘
```

关键约束：

| 维度 | 红线 | 原因 |
|------|------|------|
| 文件大小 | < 15KB | 超过会膨胀 agent memory，浪费 token |
| 触发方式 | 自动 + 手动 | 安装后常驻，匹配场景自动触发，也可显式调用 |

安装只需复制 GitHub repo 给 Hermes Agent。

---

## 一、系統演化與資源優化（#14-#11）

这一层让 agent "后台静默进化"，无需人工干预。

### #14 SkillClaw — 自我进化循环

- 仓库：https://github.com/AMAP-ML/SkillClaw
- 核心机制：每次 Session 结束后自动触发 Evolution Loop

```
Evolution Loop 流程
┌──────────┐   ┌──────────┐   ┌───────────┐   ┌───────────┐
│ Session  │──>│ 审查所有  │──>│ 去重重叠   │──>│ 重写弱指令 │
│ 结束     │   │ 使用过的  │   │ skills    │   │           │
│          │   │ skills   │   │           │   │           │
└──────────┘   └──────────┘   └───────────┘   └───────────┘
                                                      │
                                                      v
                                              ┌───────────────┐
                                              │ 自动更新      │
                                              │ skill library │
                                              └───────────────┘
```

价值在于"复合效应"：第 1 周略变锋利，第 4 周已与初始版本面目全非。适合不想维护的 vibe creator / business owner。

### #12 Defuddle — 网页去噪

- 仓库：https://github.com/kepano/defuddle
- 解决问题：Agent 抓网页时把 nav、footer、cookie banner、侧边栏广告全部读进来，烧 token 却无用

```
带噪音的 HTML                Defuddle                干净的 Markdown
┌───────────────┐         ┌──────────┐         ┌──────────────┐
│ <nav>...</nav>│         │ strip    │         │ # 标题       │
│ <header>...   │  ─────> │ noise    │ ─────>  │ 正文段落     │
│ 正文内容      │         │ keep     │         │ 列表         │
│ <footer>...   │         │ content  │         │ 表格         │
│ <aside ads>   │         │          │         │              │
└───────────────┘         └──────────┘         └──────────────┘
```

效果：Agent 网页阅读效率提升 3-4 倍。做竞品分析、市场调研、文档查阅几乎必装。

> **注意**：技能名实际是 Defuddle（作者 kepano），视频音译为 Defat/Deflo。

### #11 Humanizer — 去 AI 化文案

- 仓库：https://github.com/blader/humanizer
- 数据源：Wikipedia "Signs of AI" 页面，自动随原页更新

原理是抓取维基百科维护的 AI 写作特征清单（如 Em-dash 滥用），把 agent 输出重写为自然人类口吻。

| AI 罐头特征 | Humanizer 处理 |
|-------------|----------------|
| Em-dash (—) 滥用 | 改用其他标点 |
| 过度结构化（无意义小标题） | 自然段落化 |
| "delve into" / "tapestry" | 替换日常用词 |
| 列表泛滥 | 散文化表达 |

2026 年内容发布的"最后一道防线"。

---

## 二、開發流程與上下文控管（#13-#2）

### #13 Matt Pocock Skills Pack

- 仓库：https://github.com/mattpocock/skills
- 作者背景：60,000+ 订阅 newsletter 主理人
- 全包 15 个 skills，三个最突出：

| Skill | 作用 | 价值 |
|-------|------|------|
| **Grill Me** | Agent 写代码前先用 5 个针对性提问澄清需求 | 杜绝"上下文不足乱建功能" |
| **Caveat** | 动态清除长 session 中的 token bloat | 最多降 75% token 成本 |
| **Teach Me** | 把复杂知识转成结构化 HTML 讲义 | 提供可操作的实践指引 |

Grill Me 的价值对比：

```
没有 Grill Me                    有 Grill Me
┌──────────────────┐            ┌──────────────────┐
│ "帮我建个 X"      │            │ "帮我建个 X"      │
│      │            │            │      │            │
│      v            │            │      v            │
│ Agent 直接开写    │            │ 5 问澄清边界      │
│ （理解错了）      │            │      │            │
│      │            │            │      v            │
│ 返工 / 重新做     │            │ 精确实现          │
└──────────────────┘            └──────────────────┘
```

### #8 Adi Osmani Dev Pack

- 仓库：https://github.com/addyosmani/agent-skills
- 作者：Addy Osmani，Google Chrome 高级工程经理
- 规模：24 个生产级 skill，8 个 `/` 命令覆盖全生命周期

```
开发生命周期命令链
/spec ──────> /plan ─────> /build ─────> /test ─────> /review ─────> /ship
(规格)       (拆解)       (实现)       (验证)       (审查)        (部署)
```

灵魂特性是 **Doubt-Driven Development（质疑驱动开发）**：agent 每做重大决策时会停下来与自己争辩——提取假设 → 逐条挑战 → 弥合缺口 → 才继续执行。对生产代码、不可逆操作尤其关键。

### #2 Details That Make Interfaces Feel Better

- 仓库：https://github.com/jakubkrehel/make-interfaces-feel-better
- 灵感来源：被开发者收藏却从不应用的文章，转成 skill 后自动应用

涉及的 UI/UX 微交互原则：

| 原则 | 效果 |
|------|------|
| Text wrapping 防止孤行 | 标题不会在末行只剩一个单词 |
| 同心圆角（Concentric radius） | 卡片内按钮的圆角与卡片匹配，不再"略偏" |
| 上下文图标动画 | opacity / scale / blur 反应交互 |
| Tabular numbers | 数字更新时不会左右跳动 |
| 可中断动画 | 快速点击不会卡死 |

> 如果你是 vibe coder，曾发布"感觉哪里怪却说不上来"的 UI，通常就是缺这些。

---

## 三、能力擴展與外部軟體整合（#10-#7）

从"效率工具"跨入"能力扩展"——改变 agent 能做什么。

### #10 youtube-full

- 仓库：https://github.com/ZeroPointRepo/youtube-skills
- 解决问题：Hermes 默认 YouTube 工具在 VPS/云环境因 IP 封锁完全失效

| 默认 YouTube skill | youtube-full |
|---------------------|--------------|
| 本地机器可用，VPS 失效 | 云环境正常工作 |
| 需 Google API Key | 无需 API Key |
| 单一功能 | 字幕提取 + 频道浏览 + 播放列表解析 + 视频搜索 |

典型用途：把 YouTube 教程字幕喂给 agent 当训练素材（如教 AI 写更好的 YouTube 脚本）。

### #9 Composio

- 仓库：https://github.com/ComposioHQ/skills
- 规模：1,000+ SaaS 工具，20,000+ GitHub stars
- 价值：一次接入取代 N 次手写 OAuth

```
没有 Composio                        有 Composio
┌──────────────────────────┐        ┌──────────────────────────┐
│ Gmail    ── OAuth 配置   │        │                          │
│ Slack    ── OAuth 配置   │        │   Composio               │
│ Notion   ── OAuth 配置   │ ────>  │   (一个 skill 接全部)     │
│ HubSpot  ── OAuth 配置   │        │                          │
│ Salesforce ── OAuth 配置 │        │   1000+ SaaS tools       │
└──────────────────────────┘        └──────────────────────────┘
   每个工具一次头痛                    一键完成
```

### #7 Resemble AI Detect

- 仓库：https://github.com/resemble-ai/detect-skill
- 用途：给 agent 的输入管道加防御层，检测 deepfake / AI 生成内容

检测范围与追溯能力：

| 输入类型 | 检测内容 | 可追溯来源 |
|----------|----------|------------|
| 音频 | AI 生成语音 | ElevenLabs 等 |
| 图像 | AI 生成图片 | Midjourney / DALL-E |
| 视频 | Deepfake 篡改 | — |
| 文本 | AI 生成文案 | ChatGPT / Claude |

场景：agent 频繁爬网或处理用户提交内容时，防止恶意 prompt 注入与数据污染。

---

## 四、多 Agent 协同与架构层（#6-#3）

"基础设施层"——管理多个 agent 并行工作。

### #6 Mission Control / Minions

- 仓库：https://github.com/agent37-platform/minions
- 解决问题：多 agent 各自为政，无全局可见性

提供的核心能力：全舰队（fleet）仪表板、任务分发、agent 健康监控、实时成本追踪、跨栈状态可视化。

### #5 OpenMontage

- 仓库：https://github.com/calesthio/OpenMontage
- 定位：世界首个开源 agentic 视频生产系统
- 规模：12 pipelines / 52 tools / 500+ agent skills

```
OpenMontage 工作流
┌──────────────┐
│ 输入参考视频 │
│ (YouTube/短) │
└──────┬───────┘
       v
┌──────────────────────────┐
│ Agent 分析               │
│ - 节奏 (pacing)          │
│ - 场景结构               │
│ - 关键帧                 │
│ - 基调 (tone)            │
└──────┬───────────────────┘
       v
┌──────────────────────────────────┐
│ 输出方案（生成前确认）            │
│ - 2-3 个差异化概念               │
│ - 完整工具路径                   │
│ - 成本预估                       │
│ - 预览                           │
└──────┬───────────────────────────┘
       v (用户批准后)
┌──────────────────────────────────┐
│ 实际生成                         │
│ 例：60s Pixar 风格动画           │
│ 6 段 motion clip + 配音 + 音乐   │
│ + 字幕 ≈ $1.33                  │
└──────────────────────────────────┘
```

### #4 Anthropic Cybersecurity Skills

- 仓库：https://github.com/mukul975/Anthropic-Cybersecurity-Skills
- 作者：安全研究员 mukul975
- 核心：700+ 结构化安全技能，映射 MITRE ATT&CK 框架

| 能力域 | 说明 |
|--------|------|
| 威胁建模 | 系统化识别潜在攻击面 |
| 漏洞评估 | 自动化扫描 + 人工验证 |
| 安全代码审查 | PR 级别的安全把关 |
| 事件响应剧本 | 预定义 incident response playbooks |

价值主张：solo builder / 小团队雇不起专职安全工程师时，用这个 skill pack 覆盖基础资安。

### #3 Oh My Hermes (OMH)

- 仓库：https://github.com/witt3rd/oh-my-hermes
- 灵感：oh-my-claudecode（36,000 stars）
- 核心：把单 agent 拆成"委员会"，委员会自检工作

> [!info] 仓库实际组成（README 验证）
> OMH 提供多个 composable skills：`omh-deep-research`、`omh-ralplan`（共识规划）、`omh-deep-interview`、`omh-ralph`（验证执行）、`omh-triage`（多角色共识分诊）、`omh-autopilot`（端到端流水线）等。
>
> 推荐组合链：`omh-deep-research → omh-deep-interview → omh-ralplan → omh-ralph`

核心收益：更快的大型任务执行、更少的人工提示、专家角色的 QA 审查、session log 实时可见、智能路由最多省 50% token、验证通过前持续执行。

---

## 五、頂級關鍵技能與榮譽提名（#1 + HM）

### #1 Agent Reach — 最强推荐

- 仓库：https://github.com/Panniantong/Agent-Reach
- 核心痛点：agent 聪明，但"上网找东西"基本瞎

| 平台 | 默认 agent 遇到的墙 |
|------|---------------------|
| Twitter | API 付费，agent 碰不到 |
| Reddit | 云 IP 一访问就 403 |
| YouTube on VPS | 直接封锁 |
| 小红书 | 必须登录 |

Agent Reach 一键解决。视频说"Twitter / Reddit / YouTube / GitHub"，但仓库 README 实际支持 **16 个平台**（含 B 站、小红书、Facebook、Instagram、LinkedIn、V2EX、雪球、小宇宙播客等）。

> [!info] 设计理念（README 验证）
> Agent Reach 定位为"能力层（capability layer）"，不重写底层读取——负责**选型、安装、体检、路由**。每个平台 = "首选 + 备选"的有序后端列表。平台封了某条路，自动切换下一条，用户无感。
>
> 实例：yt-dlp 被 B 站风控封死（2026-06）→ 已切 bili-cli，用户零操作。

为什么排第一：其他所有 skill 让 agent 把"已经能做的事"做得更好；Agent Reach 给了它一个原本没有的能力——**看见整个互联网**。

### 荣誉提名

| Skill | 仓库 | 价值 |
|-------|------|------|
| **Browser Harness** | https://github.com/browser-use/browser-harness | 人类级 Chrome 操作；遇到不会的页面自动写补丁，自癒式浏览器自动化 |
| **Codebase Memory MCP** | https://github.com/DeusData/codebase-memory-mcp | 代码库持久化知识图谱；Linux Kernel（2800 万行）3 分钟索引，查询用 1/120 的 token |
| **Loop Library / Loopy** | https://github.com/Forward-Future/loopy | 把一次性 prompt 升级为闭环迭代（测量 → 改进 → 再测量 → 达标为止） |

Loop Library 的本质差异：

```
普通 prompt             Loop
"让网站变快"            "找最慢的页面
 │                       → 做一个聚焦改进
 v                       → 测量
 凭感觉改一通             → 有效才保留
                         → 重复直到达标"
```

---

## 快速選型決策樹

```
你的需求是什么？
│
├─ 想 agent 自动维护 / 升级自己
│  └─> SkillClaw (#14)
│
├─ agent 网页调研烧太多 token
│  ├─> Defuddle (#12) 去噪
│  └─> Caveat (Matt Pocock) 清 session bloat
│
├─ 写代码 / 建功能
│  ├─ 需求不清晰 ──────> Grill Me (Matt Pocock)
│  ├─ 全生命周期 ──────> Adi Osmani Dev Pack (#8)
│  ├─ 生产代码要稳 ────> Doubt-Driven Development
│  └─ 复杂任务要 QA ───> Oh My Hermes (#3)
│
├─ 要联网获取真实数据
│  ├─ Twitter/Reddit/YouTube 等 ─> Agent Reach (#1)
│  ├─ 只需 YouTube 字幕 ────────> youtube-full (#10)
│  └─ 要串接 SaaS 工具 ─────────> Composio (#9)
│
├─ UI / 前端开发
│  └─> make-interfaces-feel-better (#2)
│
├─ 多 agent 管理
│  └─> Mission Control (#6)
│
├─ 资安 / 安全
│  └─> Anthropic Cybersecurity Skills (#4)
│
├─ 防 AI 内容污染
│  └─> Resemble AI Detect (#7)
│
└─ 发布 AI 辅助文案
   └─> Humanizer (#11)
```

---

## 三步行動建議

视频给的行动路径，按优先级排序：

**第一步：基础防护与演化**
- 安装 SkillClaw（建立自动升级机制）
- 安装 Defuddle（减少无用的网页 token 消费）

**第二步：开发与品管**
- 若用于代码生成：载入 Adi Osmani Dev Pack + Oh My Hermes
- 建立完整的 QA 验证闸门

**第三步：外部联网与扩展**
- 部署 Agent Reach（解锁社群数据检索）
- 部署 Composio（自动化工作流串接）

---

## 全技能一覽表

| # | 技能 | 仓库 | 类别 | 一句话价值 |
|---|------|------|------|------------|
| 14 | SkillClaw | AMAP-ML/SkillClaw | 演化 | 后台自动去重 / 升级 skill library |
| 13 | Matt Pocock Skills | mattpocock/skills | 开发 | Grill Me + Caveat + Teach Me |
| 12 | Defuddle | kepano/defuddle | 效率 | 网页去噪为 Markdown，阅读效率 3-4x |
| 11 | Humanizer | blader/humanizer | 文案 | 抹除 AI 写作罐头特征 |
| 10 | youtube-full | ZeroPointRepo/youtube-skills | 能力 | 云端也能抓 YouTube 字幕 |
| 9 | Composio | ComposioHQ/skills | 整合 | 1 键串接 1000+ SaaS |
| 8 | Adi Osmani Dev Pack | addyosmani/agent-skills | 开发 | 24 skill 全生命周期 + 质疑驱动 |
| 7 | Resemble AI Detect | resemble-ai/detect-skill | 防御 | 输入管道 deepfake 检测 |
| 6 | Mission Control | agent37-platform/minions | 架构 | 多 agent 舰队仪表板 |
| 5 | OpenMontage | calesthio/OpenMontage | 视频 | 开源 agentic 视频生产 |
| 4 | Cybersecurity Skills | mukul975/Anthropic-Cybersecurity-Skills | 安全 | 700+ skill 映射 MITRE ATT&CK |
| 3 | Oh My Hermes | witt3rd/oh-my-hermes | 架构 | 委员会机制 + 自检 + 省 50% token |
| 2 | Interfaces Feel Better | jakubkrehel/make-interfaces-feel-better | UI/UX | 自动应用高级前端微交互 |
| 1 | Agent Reach | Panniantong/Agent-Reach | 联网 | 16 平台真实数据，零 API 费 |
| HM | Browser Harness | browser-use/browser-harness | 浏览器 | 人类级 Chrome 自癒自动化 |
| HM | Codebase Memory MCP | DeusData/codebase-memory-mcp | 代码 | 持久知识图谱，省 120x token |
| HM | Loop Library | Forward-Future/loopy | 迭代 | 一次性 prompt → 闭环迭代 |

---

## 参考资料

- 视频原文：[I Tested 100+ Hermes Agent Automations. These Are The Best](https://www.youtube.com/watch?v=IbFaY3xFpZM) — Dubibubi, 2026-07-23
- Agent Reach 仓库：https://github.com/Panniantong/Agent-Reach （README 验证：实际支持 16 平台，非视频所说的 4 个）
- Oh My Hermes 仓库：https://github.com/witt3rd/oh-my-hermes （README 验证：10 个 composable skills，非单一 skill）
- Hermes 官方 Skills Hub（社区 skills 注册中心）
- 作者推广：Ace（buildwithace.ai）— 让多个 AI 订阅协同工作的 app，优惠码 'Dubi'

## 相关笔记

- [[Hermes Agent 基础]]
- [[AI Agent 自动化工作流]]

---

*文档生成时间：2026-07-26*
*基于 Dubibubi 频道视频 + 仓库 README 交叉验证*
