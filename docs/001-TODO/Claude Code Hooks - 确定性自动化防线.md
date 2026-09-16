---
title: Claude Code Hooks — 确定性自动化防线
aliases: ["Claude Code Hooks 自动化实战", "31 个 Hooks 高阶玩法"]
tags:
  - claude-code
  - hooks
  - automation
  - devtools
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=Y2yElMkwH_A"
author: 频道作者（需确认）
created: 2026-09-16
updated: 2026-09-16
description: 将不可靠的提示词约束转换为软件级强制自动化防线 — Hooks 三層架構、四大核心 Event、兩大實戰場景與避坑指南
level: intermediate
stars: 4
---

# Claude Code Hooks — 确定性自动化防线

> 本文笔记基于 YouTube 视频结构化内容整理。视频将 Claude Code Hooks 的核心机制系统化，说明如何用确定性机制替代不可靠的提示词约束。

## 一句话定位

Hooks 是 Claude Code 的「确定性自动门」——把 AI 容易遗忘的约束条件，变成软件层自动触发的强制规则，零遗漏、零自覺依賴。

## 核心痛點：提示詞為什麼會失效

`CLAUDE.md` 中的规则本质是「便利贴」——AI 是否遵守取决于模型自身的判断，在长对话或复杂任务中极易被遗忘。

```
┌──────────────────────────────────────────────────────┐
│           约束机制的确定性分级                         │
│                                                      │
│  CLAUDE.md 规则 ──→ 提示词约束（不确定）              │
│         ↑                                            │
│  人类提醒 ──────→ 靠自觉（更不确定）                  │
│         ↑                                            │
│  Hooks ────────→ 软件强制（确定）                    │
│                                                      │
│  确定性的本质：条件达成 → 自动触发，不依赖模型自覺     │
└──────────────────────────────────────────────────────┘
```

### 职责划分原则

| 机制 | 职责 | 确定性 |
|------|------|--------|
| **对话输入** | 单次、临时任务 | 临时性 |
| **CLAUDE.md** | 项目通用指引与方向，供模型参考 | 部分（可被遗忘） |
| **Hooks** | 时机成熟「绝对必须执行」，零遗漏容忍 | **确定（强制）** |

## Hooks 三層架構

Hooks 配置在 `settings.json` 中，由三个核心属性构成：

```json
{
  "hooks": {
    "PreToolUse": [
      {
        "matcher": "Bash",
        "hooks": [
          {
            "type": "command",
            "command": "脚本路径"
          }
        ]
      }
    ]
  }
}
```

### 三層模型

```
┌──────────────────────────────────────────────────────────┐
│                    Hooks 三層模型                         │
│                                                          │
│  Event（何時發生）                                        │
│    │                                                     │
│    ▼                                                     │
│  Matcher（攔截對象）← 篩選條件，避免盲目觸發               │
│    │                                                     │
│    ▼                                                     │
│  Handler（如何處置）← 真正執行的動作                       │
│    │                                                     │
│    ├─ command  → 本机 Shell 命令或脚本                    │
│    ├─ prompt   → 次级模型直接判定                         │
│    ├─ agent    → 次级 AI 自主调用工具、跑测试              │
│    ├─ http     → 远程 HTTP 调用                          │
│    └─ mcp_tool → MCP 工具调用                            │
└──────────────────────────────────────────────────────────┘
```

### Handler 模式对比

| 模式 | 行为 | 适用场景 | 优点 | 缺点 |
|------|------|----------|------|------|
| `command` | 执行 Shell 命令/脚本 | 文件检查、格式验证、安全拦截 | 灵活，可写任意逻辑 | 需要维护脚本 |
| `prompt` | 次级模型判定 | 简单规则判断 | 无需脚本 | 增加 Token 消耗 |
| `agent` | 次级 AI 自主调用工具 | 需要多步骤验证（跑测试、改代码） | 可处理复杂验证链 | 耗时最长 |
| `http` | 远程 HTTP 调用 | 团队审计、日志上报 | 集中化管理 | 依赖网络 |
| `mcp_tool` | MCP 协议工具 | 已有 MCP 工具生态 | 复用现有工具 | 需配置 MCP server |

## 四大核心生命周期（Events）

官方 31 种事件中，最核心的是以下 4 个触发时机：

### 1. SessionStart — 注入上下文

**触发时机**：开启新对话时

**用途**：注入技能说明、历史上下文

```json
{
  "hooks": {
    "SessionStart": [
      {
        "type": "command",
        "command": "echo '## Git Status' && git status --short"
      }
    ]
  }
}
```

**⚠️ 隐藏成本**：注入的信息持续占用每轮对话的 Token 配额。灌入 1300 tokens（约 2000 字）可能影响模型前几轮的表现——不要塞太多。

### 2. PreToolUse — 安全防呆

**触发时机**：工具执行前，可阻止执行

**最佳用途**：拦截高危命令

```bash
# 实战：拦截 .env 文件提交
# .claude/hooks/block-env.sh
if echo "$CLAUDE_TOOL_INPUT" | grep -q "\.env"; then
  echo "🛑 安全拦截：禁止提交敏感文件（.env、API Key）"
  exit 2  # 阻止执行
fi
```

| 可拦截操作 | 风险等级 | 示例 |
|-----------|---------|------|
| Git 操作 | 🔴 高 | `git reset --hard`、`git push` |
| 文件写入 | 🟡 中 | 覆盖 `.env`、修改密钥文件 |
| 命令执行 | 🟡 中 | `rm -rf`、格式化磁盘 |
| 网络请求 | 🟢 低 | 外发包含敏感数据的请求 |

### 3. PostToolUse — 快速验收

**触发时机**：工具执行成功后

**最佳用途**：验证可量化/可计算的错误

```bash
# 自动运行语法检查
npx eslint --fix "$CLAUDE_TOOL_INPUT_FILE_PATH"
```

| 验证类型 | 示例 | 工具 |
|---------|------|------|
| 语法检查 | TypeScript/ESLint 自动修复 | `eslint --fix` |
| HTML 质量 | 扫描破图图片 | 自定义脚本 |
| 对比度标准 | 文字与背景对比度检查 | 无障碍审计工具 |
| 代码格式 | Prettier 格式化 | `prettier --write` |

### 4. Stop — 深度审查

**触发时机**：任务完成、准备停止时

**核心原则**：避免「唠叨成本（Nagging Cost）」——不要每改一行代码就审查一次，会让模型变得畏首畏尾。将结构、排版、配色的深度检查统一到 Stop 阶段。

```json
{
  "hooks": {
    "Stop": [
      {
        "type": "agent",
        "prompt": "检查文章是否有 AI 味，如有则逐条修改",
        "timeout": 120
      }
    ]
  }
}
```

### 四大 Event 决策树

```
                    要做什么？
                       │
              ┌────────┴────────┐
              │                 │
         注入上下文         条件性执行
         SessionStart       其他 Event
              │                 │
              │           ┌─────┴─────┐
              │           │           │
              │        事前拦截      事后验证
              │        PreToolUse   PostToolUse
              │           │           │
              │        安全拦截    可量化检查
              │           │           │
              │           └─────┬─────┘
              │                 │
              │           任务完成？
              │              │
              │           是 → Stop
              │              │
              │           深度审查
              │           质量把关
```

## 兩大落地實戰

### 實戰 1：客觀安全攔截（擋下 .env 提交）

```
流程：
  PreToolUse 触发
       │
       ▼
  Matcher 锁定 "Bash"
       │
       ▼
  Handler: shell 脚本
       │
       ├── grep 检测 .env 或敏感文件路径
       │
       ├── 命中 → exit 2（阻止执行）
       │         │
       │         ▼
       │      返回拦截消息给 Claude
       │      Claude 收到提示后改用安全路径
       │
       └── 未命中 → exit 0（放行）
```

**关键点**：`exit 2` 会直接阻止工具执行，并将拦截消息反馈给 Claude 模型，让 AI 自行修正而非硬闯。

### 實戰 2：主觀品質把關（去除文章 AI 味）

```
流程：
  Stop 触发
       │
       ▼
  Matcher 匹配任务完成信号
       │
       ▼
  Handler: agent 模式
       │
       ├── 次级 AI 调用 humanizer skill
       │
       ├── 扫描文章，识别 AI 腔调段落
       │
       ├── 标记问题，要求逐条修改
       │
       └── 修改完成 → 允许停止
```

**关键点**：结合专属写作技能（如 humanizer skill），让次级 AI 主动抓出浓重 AI 腔调段落，要求模型逐条修改直到过关。

## 避坑指南

### 踩坑 1：触发范围过大

**症状**：Matcher 设定过广，导致频繁空转卡顿

**根因**：每次工具调用都触发脚本，增加延迟

**正确策略**：

```
Matcher 收敛 → 最小范围筛选
       │
       ▼
  脚本内部 → 精细判定
```

**示例**：
- ❌ `"matcher": ".*"` — 匹配所有工具调用
- ✅ `"matcher": "Bash"` — 只匹配 Bash
- ✅ `"matcher": "Write|Edit"` — 只匹配文件编辑

### 踩坑 2：Stop 無限迴圈

**症状**：Stop 拦截要求修改 → 重新触发 Stop → 再次拦截 → 死循环

**解决方案：两道保险**

```
保险 1：记录通过状态
  └─ 首次通过后写标记文件
  └─ 后续 Stop 检查标记 → 已验证则放行

保险 2：最大重试次数上限
  └─ 计数修改次数
  └─ 超过阈值 → 转交人工处理
```

```
Stop 循环防护流程：

  Stop 触发
       │
       ├── 第 N 次拦截？
       │    │
       │    ├── N < 阈值 → 要求修改 → 继续
       │    │
       │    └── N ≥ 阈值 → 放行 → 转人工
       │
       └── 记录已通过 → 下次直接放行
```

## 落地守则

### 三重条件筛选标准

只有同时符合以下三个条件的任务，才值得配置为 Hook：

| 条件 | 说明 | 示例 |
|------|------|------|
| ① **会重复执行** | 不是单次任务 | 每次 commit 前检查 |
| ② **发生在固定时机** | 可预测的触发点 | PreToolUse, Stop |
| ③ **漏掉会造成实质损失** | 非优化类，是防错类 | 提交密钥、漏跑测试 |

### 快速起手式

1. 打开 `CLAUDE.md`
2. 挑出 Claude 最常遗漏的一条规则（例如「修改完代码后自动执行测试」）
3. 向 Claude 提出「请帮我建立一个 Hook」的指令
4. 让模型自动生成 `settings.json` 与对应脚本
5. 立即验证

## 对比：CLAUDE.md vs Hooks

```
┌─────────────────────┬──────────────────────┬──────────────────┐
│     维度            │   CLAUDE.md          │   Hooks          │
├─────────────────────┼──────────────────────┼──────────────────┤
│ 确定性              │ 模型判断，可能遗忘     │ 软件强制，零遗漏    │
│ 触发方式            │ 每次对话重新读取       │ 条件达成自动触发    │
│ 可阻止执行          │ ❌ 只能建议           │ ✅ 可终止流程      │
│ Token 开销          │ 每次加载占用上下文     │ 仅触发时产生开销    │
│ 维护成本            │ 单文件，易于维护       │ 需要脚本/配置      │
│ 适用场景            │ 大方向指引             │ 硬性规则、安全红线  │
└─────────────────────┴──────────────────────┴──────────────────┘
```

## 参考资料

- [Claude Code 官方 Hooks 文档](https://docs.anthropic.com/en/docs/claude-code/hooks)
- [The Ultimate Claude Code Guide - DEV Community](https://dev.to/holasoymalva/the-ultimate-claude-code-guide-every-hidden-trick-hack-and-power-feature-you-need-to-know-2l45)
- [Claude Code Hooks 深度解析 - 腾讯云](https://cloud.tencent.com/developer/article/2649082)
- [Claude Code Hooks: Complete Guide](https://claudefa.st/blog/tools/hooks/hooks-guide)
- [视频: 官方隐藏的31个高阶玩法](https://www.youtube.com/watch?v=Y2yElMkwH_A)
