---
title: Hermes Agent v0.13.0 - The Tenacity Release
aliases: [Hermes v0.13, Hermes Tenacity Release, hermes-agent 0.13]
tags:
  - hermes-agent
  - ai-agent
  - release-notes
  - status/active
  - type/release-note
source:
  - "https://www.youtube.com/watch?v=p15Uxm7il8c"
  - "https://github.com/NousResearch/hermes-agent/releases"
author: Nous Research（原始发布）/ Julian Goldie SEO（视频解读）
created: 2026-05-08
updated: 2026-05-08
description: Hermes Agent v0.13 "Tenacity Release" — 多 Agent Kanban、/goal 锁定目标、视频理解、安全加固 8 个 P0、Google Chat 第 20 个平台、7 种语言国际化
level: advanced
stars: 5
note: 视频为营销号内容（字幕禁用，描述仅推广链接），笔记基于 GitHub 官方 release notes 撰写。视频本身参考价值低，但 v0.13 版本内容重要。
---

# Hermes Agent v0.13.0 - The Tenacity Release

> v0.13.0 发布于 2026.5.7，代号 "Tenacity"。核心主题：让 Agent 完成它开始的事。864 commits, 588 PRs, 128K+ 行新增, 295 位贡献者。

---

## 目录

- [版本对比：v0.12 → v0.13](#版本对比v012--v013)
- [Top Highlights（重点功能）](#top-highlights重点功能)
- [多 Agent Kanban 系统](#多-agent-kanban-系统)
- [/goal 目标锁定](#goal-目标锁定)
- [安全加固（8 个 P0）](#安全加固8-个-p0)
- [Checkpoints v2](#checkpoints-v2)
- [新平台与国际化](#新平台与国际化)
- [Provider 插件化](#provider-插件化)
- [基础设施改进](#基础设施改进)
- [升级指南](#升级指南)

---

## 版本对比：v0.12 → v0.13

| 维度 | v0.12 Curator（4.30） | v0.13 Tenacity（5.7） |
|------|----------------------|----------------------|
| 主题 | Agent 自我维护 | Agent 完成它开始的事 |
| Commits | 1,096 | 864 |
| 核心新增 | 自治 Curator | Kanban 多 Agent 板 |
| 安全 | Secret redaction 默认关 | 8 个 P0 修复，redaction 默认开 |
| 平台数 | 19 | 20（+Google Chat） |
| 国际化 | 英文/中文 | +5 语言（日/德/西/法/乌/土） |

---

## Top Highlights（重点功能）

```
┌─────────────────────────────────────────────────────┐
│            v0.13 Tenacity 核心矩阵                   │
├──────────────┬──────────────────────────────────────┤
│ 多 Agent 协作 │ Kanban — heartbeat/reclaim/zombie    │
│ 目标锁定     │ /goal — Ralph loop 一等原语           │
│ 视频理解     │ video_analyze — Gemini 多模态          │
│ 语音克隆     │ xAI Custom Voices TTS provider        │
│ 安全加固     │ 8 P0 修复（CVSS 8.1 级）               │
│ 会话持久化   │ Gateway 重启后自动恢复会话              │
│ 无 Agent 定时 │ no_agent cron — 纯脚本看门狗           │
│ 国际化       │ 7 语言 CLI + 中文文档站                 │
│ Provider     │ 可插拔插件系统                         │
│ MCP          │ SSE transport + OAuth + keepalive      │
└──────────────┴──────────────────────────────────────┘
```

---

## 多 Agent Kanban 系统

v0.13 最重要的新功能。将任务分配给多个 Hermes Worker 协同完成。

```
┌──────────────────────────────────────────────┐
│              Kanban Board                      │
├──────────┬──────────┬──────────┬─────────────┤
│  TODO    │ IN PROG  │  REVIEW  │    DONE     │
├──────────┼──────────┼──────────┼─────────────┤
│ Task A   │ Task C   │ Task E   │ Task G ✓    │
│ Task B   │ Task D   │          │ Task H ✓    │
│ Task F   │          │          │             │
└──────────┴──────────┴──────────┴─────────────┘
         ↑         ↑          ↑
    多个 Worker 并行拾取、执行、交接、关闭
```

**保障机制**：
- **Heartbeat**：Worker 心跳检测，卡死自动标记
- **Reclaim**：超时任务自动回收重新分配
- **Zombie Detection**：识别僵尸 Worker 并清理
- **Auto-block**：未正常退出时自动阻止后续分配
- **Per-task Retries**：每个任务独立重试预算
- **Hallucination Gate**：幻觉检测门控，防止伪造完成

---

## /goal 目标锁定

解决 Agent 在多轮对话中"忘记"用户要求的经典问题。

```
传统模式：
  用户："帮我重构这个模块"
  Agent：开始工作... 几轮后偏离到其他任务

/goal 模式（Ralph Loop）：
  /goal 重构 auth 模块，拆分为 service + middleware
  → Agent 在后续每一轮都锚定这个目标
  → 不会因为工具调用或上下文切换而偏离
```

**实现原理**：Ralph loop 作为一等原语（first-class primitive），每次 turn 都注入目标锚点，确保 Agent 始终知道自己在做什么。

---

## 安全加固（8 个 P0）

v0.13 修复了 **13 个 P0 + 36 个 P1**，其中安全相关 P0 有 8 个：

| P0 | 修复内容 | 严重度 |
|----|----------|--------|
| Redaction 默认开启 | 之前 v0.12 改为默认关，现在重新默认开 | 防信息泄露 |
| Discord role-allowlist guild-scoped | 修复跨 guild DM bypass（CVSS 8.1） | 远程利用 |
| WhatsApp 默认拒绝陌生人 | 新对话需先授权 | 未授权访问 |
| TOCTOU 窗口修复 | auth.json + MCP OAuth 的竞态条件 | 提权风险 |
| Browser SSRF 防护 | cloud-metadata SSRF floor | 云环境攻击 |
| Cron prompt-injection 扫描 | 组装后的 skill 内容检查注入 | 供应链攻击 |
| debug share redaction | 上传调试报告时自动脱敏 | 信息泄露 |
| MCP OAuth 安全 | SSE transport OAuth forwarding 安全 | 凭证泄露 |

---

## Checkpoints v2

状态持久化完全重写。

| v1 问题 | v2 修复 |
|---------|---------|
| 无真正清理，shadow repos 累积 | Real pruning，真正删除过时数据 |
| 磁盘无限制 | Disk guardrails，磁盘保护 |
| 孤儿仓库 | 不再有 orphan shadow repos |

---

## 新平台与国际化

**Google Chat — 第 20 个消息平台**

同时引入通用 platform-plugin hooks，第三方适配器（如 IRC、Teams）可以不修改核心代码直接接入。

**国际化（i18n）**

静态 gateway + CLI 消息支持 7 种语言：

```
Chinese (zh) / Japanese (ja) / German (de) /
Spanish (es) / French (fr) / Ukrainian (uk) / Turkish (tr)
```

文档站新增中文（zh-Hans）locale。

---

## Provider 插件化

Provider 从硬编码变为可插拔系统：

```python
# 新增 Provider 只需实现 ABC
class ProviderProfile(ABC):
    @abstractmethod
    def list_models(self) -> list[ModelInfo]: ...
    @abstractmethod
    async def complete(self, messages, **kwargs) -> Completion: ...

# 放入 plugins/model-providers/ 即可加载
```

**影响**：第三方 Provider 无需修改 Hermes 核心代码，drop-in 即用。

---

## 基础设施改进

### Agent 自我 Lint
`write_file` + `patch` 后自动运行 delta lint（Python/JSON/YAML/TOML），语法错误立即暴露而非传递到下游。

### no_agent Cron 模式
```bash
# 纯脚本看门狗，跳过 LLM，零 token 消耗
cronjob action=create name=watchdog no_agent=True \
  script=check_health.sh
```

### MCP 增强升级
- SSE transport with OAuth forwarding
- Stale-pipe 自动重试
- 图片结果以 MEDIA tag 呈现（不再丢失）
- 长生命周期 wait 期间的 keepalive

### ACP 增强
- `/steer` 和 `/queue` 从 Zed/VS Code/JetBrains 直接操控运行中的 Agent
- 原子化会话持久化
- 重启后推理元数据保留

### 其他亮点
- SearXNG 原生搜索后端
- OpenRouter response caching
- `[[as_document]]` skill 媒体路由指令
- `transform_llm_output` 插件 hook（LLM 输出后处理）
- Curator 子命令（archive/prune/list-archived）
- TUI 优化（`/model` picker、context-compression counter）
- Dashboard 增长（Plugins 页面、Profiles 管理、可排序分析表）
- 6 个新可选 skills（Shopify、Anthropic 金融、kanban-video-orchestrator 等）

---

## 升级指南

```bash
# 升级
hermes update

# 验证版本
hermes --version  # v0.13.0 (v2026.5.7)

# 安全建议：确认 redaction 已开启
# config.yaml 中 redaction.enabled 默认为 true（v0.13）
# 如果你在 v0.12 手动关闭过，升级后会自动重新开启

# 检查 security patches
hermes doctor
```

---

## 参考资料

- [Hermes Agent v0.13.0 官方 Release Notes（GitHub）](https://github.com/NousResearch/hermes-agent/releases)
- [NousResearch/hermes-agent 仓库](https://github.com/NousResearch/hermes-agent)
- [Hermes Agent 官方文档](https://hermes-agent.nousresearch.com/docs)

## 相关笔记

- [[Hermes Agent v0.12 Curator Release]] — 上一版本：自治 Curator + 自我改进循环
- [[Hermes Agent 配置与使用]] — 日常使用指南
- [[AI Agent 平台对比 2026]] — Hermes vs Claude Code vs Codex
