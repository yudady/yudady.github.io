---
title: "OpenHuman AI Agent - 对比 OpenClaw 与 Hermes 的深度分析"
aliases:
  - "OpenHuman vs OpenClaw vs Hermes"
  - "NeoCortex 记忆引擎"
  - "AI Agent 记忆层架构"
tags:
  - ai/agent
  - tool/openhuman
  - tool/openclaw
  - tool/hermes-agent
  - memory/ai
  - status/active
source:
  - https://www.youtube.com/watch?v=-bPTuNgeBVY
author:
  - YouTube AI 开发者工具频道
  - Tiny Humans AI (openhuman 团队)
created: 2026-05-18
updated: 2026-05-18
description: 深度分析 OpenHuman（tinyhumansai）如何通过 NeoCortex 记忆引擎和潜意识系统挑战 OpenClaw 和 Hermes Agent。核心理念：模型不是瓶颈，记忆层才是。OpenHuman 从冷启动零上下文问题切入，118+ OAuth 集成 + 每 20 分钟自动同步，构建持久记忆图谱。
level: intermediate
stars: 4
---

## 三大开源 AI Agent 对比

### OpenClaw
- **作者**: Peter Steinberger (PSPDFKit 创始人)，后加入 OpenAI
- **Stars**: 68,000
- **哲学**: 消息应用即命令中心 — WhatsApp/Telegram/Signal/Discord
- **架构**: 本地运行，BYO 模型/API key，MIT 协议
- **特点**: 终端安装，Task Brain (SQLite 任务账本)，NVIDIA Nemo Claw 安全栈
- **局限**: 配置复杂，零自动上下文，开发者向

### Hermes Agent
- **作者**: Nous Research
- **Stars**: 135,000（3 个月）
- **哲学**: Agent 用得越久越强 — 任务执行后自动写入 skill 库
- **架构**: 166 skills / 26 类别 / 7 执行后端，MIT 协议
- **特点**: 后执行评估循环，复利价值，RL 训练数据生成
- **局限**: BYO 一切，零冷启动上下文，CLI 优先

### OpenHuman
- **作者**: Tiny Humans AI（cinem 领导）
- **Stars**: 11,700（快速上升中）
- **哲学**: Agent 应该已经认识你 — 持久记忆 + 自动上下文
- **架构**: 基于 OpenClaw 架构改造，Rust 69% + TypeScript，GPL-3.0
- **版本**: v0.53.43（2026-05-13），31 个版本，4 天 21 个补丁

## 核心技术：NeoCortex 记忆引擎

**仓库**: [tinyhumansai/neocortex](https://github.com/tinyhumansai/neocortex) | 闭源 Alpha

### 设计理念

> "Every AI model shares the same fundamental limitation: they are stateless."

所有现有记忆系统（mem0、GPT memory 等）本质相同：存 embedding → 语义相似度检索 → 传入模型。问题：
- 相似度不等于重要性（6 个月前关键词匹配 vs 今早关键信息）
- Google Titans 研究：模型吸收更多数据后**准确率反而下降**
- 更大上下文窗口使问题恶化（更慢、更贵、更易出错）

### 架构特点

1. **零 LLM 依赖** — 记忆层本身不调用 LLM，所有性能优势由此而来
2. **知识图谱** — 非扁平文本，构建实体/关系/时序链结构
3. **跨源实体解析** — Slack 提到的 Q4 deck + Notion 引用 + 邮件附件 = 图中一个节点
4. **语义去重** — 群聊/共享工作区 80%+ 消息重复，NeoCortex 入图前剥离冗余

### 性能指标

| 指标 | NeoCortex | 最接近竞品 (Mem0) |
|------|-----------|-------------------|
| 索引速度 | 1000 万 token < 10s | ~1000x 慢 |
| 索引成本 | ~$0.0004/次 | ~$0.0112/次 (28x) |
| 查询延迟 | ~1.1s | ~3.6s |
| 数据规模 | 10 亿+ token | — |
| 硬件要求 | MacBook Air CPU，无 GPU | — |

### 四层记忆架构（神经科学启发）

- **Hot**: 最新/最活跃上下文，即时查询
- **Warm**: 近几天重要信息，稍降优先级
- **Cool**: 较旧但可被正确线索唤回
- **Cold**: 最旧/最少访问，重降优先但永不删除

基于 Ryan & Franklin (2022, Nature Reviews Neuroscience) — 遗忘是主动生物过程。Carnegie Mellon 研究：有意遗忘释放工作记忆资源，反而提升其他内容回忆。

## 潜意识系统 (Subconscious)

### 生物学灵感

Perkingi 细胞（位于小脑）— 随机思维生成神经元，在人类意识中扮演重要角色。大脑不仅在处理显式指令时运行，而是持续在后台建立连接。

### 技术实现

通过 NeoCortex 周期性触发**全局记忆回溯** (Global Memory Recalls)：
- 宽泛的半随机扫描整个知识图谱
- 基于时间性 + 相关性 + **故意随机因子**
- 非目标检索，而是跨全量数据找模式/矛盾/连接

### 输出类型

- 主动洞察（你没问的）
- 行动建议（基于识别的模式）
- 已进行事项确认
- 你自己不会关联的两件事之间的新连接

### 运行指标

- 每天 10,000+ 次循环，成本 < $1
- 仅因 NeoCortex 无 LLM 依赖才可能

### 限制（团队明确声明）

不是 AGI，无自主目标，无自我意识，不可在未批准下执行不可逆操作，不覆盖显式指令。

## 产品特性

### 118+ OAuth 集成 + Auto-Fetch

- Gmail / Notion / GitHub / Slack / Stripe / Calendar / Drive / Linear / Jira 等
- 每 20 分钟自动同步所有活跃连接到 Memory Tree
- 无需手动轮询/插件配置/BYO 集成管道

### Memory Tree

- 本地 SQLite 知识库，原始数据压缩为 ≤3k token Markdown chunks
- 同时写入 Obsidian 兼容 vault（灵感来自 Karpathy 的 obsidian-wiki 工作流）
- 支持 `agentmemory` 后端与 Claude Code / Cursor / Codex / OpenCode 共享

### Token Juice

- 所有工具调用/爬取/邮件/搜索先压缩再送 LLM
- HTML→Markdown，长 URL 缩短，非 ASCII 剥离
- 声称 80% token 压缩率，v0.52.25 从 JS 移植到 Rust

### 模型路由

基础设施层自动选择：推理任务→深度模型，快速对话→速度模型，视觉任务→图像模型。可选 Ollama 本地运行。

### 桌面 Mascot

- 动画 orb，ElevenLabs 语音输出 + 实时唇同步
- 可加入 Google Meet 作为可见参与者
- v0.53.43 新增颜色自定义 + "bum dance" 动画
- 产品决策而非噱头：面向从未打开过终端的用户

## 商业模式差异

| | OpenClaw | Hermes | OpenHuman |
|---|---|---|---|
| 协议 | MIT | MIT | GPL-3.0 |
| 定价 | BYO 一切 | BYO 一切 | SaaS 订阅 + credit |
| 目标用户 | 开发者 | 开发者/研究者 | 所有人 |

GPL-3.0 将限制商业生态发展。SaaS 定价在 BYO 社区中有争议。

## 评估

### 看多理由

- 解决的真实问题：每次会话都要重新解释自己（"A smarter amnesiac is still an amnesiac"）
- 记忆层是正确切入点 — 模型不是瓶颈
- NeoCortex 架构论证自洽
- Auto-Fetch + Obsidian vault 降低冷启动门槛
- "portfolio readiness cleanup" commit 暗示即将有重大动作

### 看空理由

- 9k-11k stars ≠ 成熟产品
- NeoCortex 性能指标需独立验证
- 潜意识输出质量需大规模验证
- GPL-3.0 限制生态
- 社区/研究深度远不及 Hermes（135k stars + Atropus RL 集成）

### 结论

OpenHuman 不是在跟 OpenClaw/Hermes 比执行能力，而是在做**不同产品类别** — 记忆与智能层。关键问题：市场是否真的有冷启动零上下文问题，以及 NeoCortex 是否是正确解。
