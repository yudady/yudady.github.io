# Hermes Agent v0.15 Velocity Release

> 来源: [They Deleted 76% of Hermes Agent's Core. It's Faster Now!](https://youtu.be/mR6v4x9nefw)
> 官方: [v0.15.0 Release Notes](https://github.com/NousResearch/hermes-agent/releases/tag/v2026.5.28)

## 概览

2026.5.28 发布，项目史上最大版本。1,302 commits, 747 PRs, 560+ issues, 321 贡献者。
命名 "Velocity" — 既是运行速度提升，也是项目演进速度。

## 架构重构

`run_agent.py` 16,083 → 3,821 行（-76%），拆为 14 个 `agent/*` 模块。其中 `run_conversation` 单函数就占 3,877 行。外部接口零破坏：`AIAgent` 上保留薄 forwarder，所有测试路径和外部调用者兼容。插件架构全面对齐——图片生成、浏览器、搜索、消息均实现一文件扩展。

## 性能优化（三轮叠加）

- 延迟加载 `openai._base_client`：每次 CLI 调用省 240ms / 17MB
- 延迟压缩可行性检查：每次 agent 构造省 170-290ms
- 热路径优化：31 轮对话函数调用 399K → 213K（-47%）
- 自适应子进程轮询：每次工具调用省 ~195ms，累积每轮省 1s+
- `hermes --version` 冷启动：701ms → 258ms（-63%）
- Termux 冷启动：2.9s → 0.8s（-72%）
- 对 Codex CLI 基准 5/11 → 6/11 胜出

## Kanban 多代理（104 PR）

- **自动分解**: Triage 时 orchestrator 自动将顶层任务拆解为子任务树
- **Swarm 拓扑**: `hermes kanban swarm` 一条命令生成完整 Swarm v1 图（root + 并行 workers + gated verifier + gated synthesizer + shared blackboard）
- **Per-task 模型路由**: 便宜模型铺量，贵模型收口，真正控制成本
- **Worktree 隔离**: 每个 worker 独立 git branch，互不干扰
- **可靠性**: 计划启动、可配置 claim TTL、重试指纹防 fleet-wide retry storm、过期检测 + respawn guard、decomposition 循环检测
- **可观测性**: `/workers/active`、`/runs/{id}`、`/inspect` 端点

## Session Search 重建

- 速度：~90s → ~20ms（discovery），scroll ~1ms，整体 **4,500x 提升**
- 成本：~$0.30/次 → **零成本**（移除辅助 LLM，改用 FTS5）
- 单工具三模式（discovery/scroll/browse），参数自动推断，无需 mode 参数或配套 skill

## Promptware 防御

基于 Brainworm/Promptware Kill Chain 研究（arxiv 2601.09625）：
- 三个拦截点：tool output、recalled memory、stored skills
- 共享威胁模式文件 `tools/threat_patterns.py`（~15 个新 pattern）
- 记忆加载时扫描；工具结果加 delimiter 标记区分系统指令
- 配套 `security-guidance` plugin 拦截危险代码写入

## Bitwarden Secrets Manager

- 单 bootstrap token (`BWS_ACCESS_TOKEN`) 替代所有 per-provider API key
- `bws` 首次使用自动安装，Bitwarden 为 source-of-truth（默认覆盖匹配 env var）
- 支持 EU Cloud 和 self-hosted，凭证标注来源（Bitwarden vs local env）

## 新集成

- **ntfy**: 第 23 个消息平台，自托管推送，零注册零 API key
- **xAI 深度轮**: web search 插件、proxy upstream、模型退役检测+迁移命令、TTS pause tags、base URL pinning、Grock 执行引导防跳过工具
- **图片生成**: Krea（medium $0.03/张, large $0.06/张），Fal.ai 重构为独立插件
- **MCP catalog**: Nous 认可的 curated 列表，交互式 picker 一键安装
- **OpenHands**: 作为 delegate coding agent 加入（model-agnostic，支持本地模型）
- **Skill Bundles**: 命名技能组，一条 slash 命令激活多个 skill
- **新 skills**: `code-wiki`（Karpathy 风格持久化开发 wiki）、`web-pentest`（OWASP 配方）

## TUI Session Orchestrator

多会话叠加层：list/switch/refresh/close 不离开 TUI，新会话 session-scoped 模型选择。配合 Kanban swarm 可在单窗口管理多个并行 agent。附带鼠标追踪、scrollback 保留、CJK/IME 修复、TTS 指示器。
