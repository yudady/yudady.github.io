---
title: Agent Skills — 结构化 AI 编码工作流框架
aliases: [Addy-Skills, Agent Skills by Addy Osmani]
tags: [ai-agent, status/active, area/distill, type/doc, topic-ai-workflow]
source: ["https://www.youtube.com/watch?v=zrbGCYGQr18", "https://github.com/addyosmani/agent-skills"]
author: Addy Osmani (讲解: AICodeKing)
created: 2026-04-22 09:12
updated: 2026-04-22 09:12
description: |
  Addy Osmani 的 Agent Skills 仓库：将资深工程师的工作流打包为可复用的 AI 编码代理技能，覆盖从需求定义到上线的完整生命周期。
level: intermediate
stars: 4
---

# Agent Skills — 结构化 AI 编码工作流框架

> 将谨慎资深工程师的工作方式打包成可复用的 agent 技能。不是"写更好的代码"，而是"遵循正确的流程"。

---

## 核心问题

AI 编码代理的核心问题不是"不会写代码"，而是**跳过无聊但关键的部分**：

```
+-----------------------+     +-----------------------+
|   AI 代理常做的事       |     |   应该做但常跳过的     |
+-----------------------+     +-----------------------+
| 拿到需求就写代码        | --> | 澄清 spec（规格定义） |
| 写完就提交             | --> | 制定正式计划           |
| 跑通就算完成           | --> | 写真正的测试           |
| 自信地声称完成          | --> | 代码审查流程           |
+-----------------------+     +-----------------------+
```

Agent Skills 的目标：**把这些"跳过"的部分变成强制流程**。

---

## 生命周期（Lifecycle）

Agent Skills 把软件工作拆解为完整的生命周期：

```
  +--------+    +--------+    +--------+    +--------+    +--------+    +--------+
  | /spec  | -> | /plan  | -> | /build | -> | /test  | -> | /review| -> | /ship  |
  | 定义   |    | 规划   |    | 切片构建|    | 验证   |    | 审查   |    | 上线   |
  +--------+    +--------+    +--------+    +--------+    +--------+    +--------+
                   ^                                                  |
                   |           +-------------+                         |
                   +----------- | /simplify  | <-----------------------+
                               | 简化       |
                               +-------------+
```

不是把编码当作一个巨大的输出块，而是当作**带检查点的工程流程**。

---

## 7 个主要命令入口

| 命令 | 阶段 | 核心行为 |
|------|------|----------|
| `/spec` | 需求 | 细化想法，定义清晰的规格 |
| `/plan` | 规划 | 任务拆解，排定优先级和顺序 |
| `/build` | 实现 | 增量实现，每次一个小切片 |
| `/test` | 验证 | TDD 驱动，测试即证明 |
| `/review` | 审查 | 代码审查，质量门禁 |
| `/code-simplify` | 简化 | 去除不必要的复杂度 |
| `/ship` | 上线 | CI/CD、文档、发布检查 |

---

## 技能全览

7 个命令入口映射到更底层的技能集：

| 类别 | 技能 |
|------|------|
| 需求 | 想法细化（Idea Refinement）、Spec 驱动开发 |
| 规划 | 任务拆解（Task Breakdown）、上下文工程（Context Engineering） |
| 实现 | 增量实现（Incremental Implementation）、TDD |
| 专项 | API 设计、前端工程、调试与错误恢复 |
| 质量 | 代码审查、安全审计、性能优化 |
| 交付 | 文档、CI/CD、发布 |

---

## 专家人设（Specialist Personas）

除了构建工作流，还包含三个专家角色：

```
                    +-------------------+
                    |   主编码代理       |
                    +-------------------+
                           |
              +------------+------------+
              |            |            |
     +--------v--+  +------v-----+  +--v---------+
     | Code      |  | Test       |  | Security    |
     | Reviewer  |  | Engineer   |  | Auditor     |
     +-----------+  +------------+  +-------------+
     可维护性问题    测试覆盖不足      安全漏洞
```

| 人设 | 关注点 | 捕获的问题类型 |
|------|--------|----------------|
| Code Reviewer | 可维护性 | 代码规范、复杂度、可读性 |
| Test Engineer | 测试覆盖 | 缺失用例、弱验证、边界条件 |
| Security Auditor | 安全 | 注入、权限、敏感数据泄露 |

**核心理念**：让一个代理假装同时做三件事，不如三个专用代理各司其职。

---

## 正确使用方式

### 错误方式 vs 正确方式

| | 错误 | 正确 |
|---|------|------|
| 载入方式 | 把整个仓库塞进一个 prompt | 按生命周期分阶段加载 |
| 上下文管理 | 一次性加载所有技能 | 在正确的时间加载正确的行为 |
| 期望 | 更多指令 = 更好结果 | 对的行为 > 多的指令 |

### 推荐的渐进策略

**始终开启的核心三件套**：

```
1. Spec 驱动开发   -- 动手前先定义清楚
2. TDD            -- 测试即证明，不是事后补
3. Code Review    -- 合并前必须审查
```

**按需加载的专项技能**：

```
前端项目  -> + Front-end Engineering
API 项目  -> + API & Interface Design
生产系统  -> + Security & Hardening
性能敏感  -> + Performance Optimization
```

---

## 跨工具可移植性

Agent Skills 不绑定任何特定工具。官方支持：

| 工具 | 集成方式 |
|------|----------|
| Claude Code | 官方插件 |
| Cursor | 官方配置 |
| Gemini CLI | 官方设置 |
| Windsurf | 官方支持 |
| OpenCode | 官方支持 |
| GitHub Copilot | 官方支持 |

但底层是纯 Markdown 工作流，**任何能读指令的代理都能用**。

---

## 在 Verdent（或类似工具）中的适配思路

视频后半段重点讲了如何将 Agent Skills 映射到 Verdent 的原生能力：

| Agent Skills 概念 | Verdent 对应 | 配置位置 |
|-------------------|-------------|----------|
| 全局行为原则 | Global User Rules | `verdent.md` |
| 项目级规则 | Project Rules | `agents.md` |
| 规划阶段强化 | Plan Rules | Verdent Plan Mode |
| 专家人设 | Custom Sub-agents | Sub-agent 定义 |
| 并行执行 | Parallel Workspaces | Git Worktree 隔离 |

### Verdent 适配的五个层级

```
层级 1: verdent.md
  -> spec before code, verify changes, don't skip testing,
     prefer simpler solutions, avoid rationalizing sloppy work
  (全局生效，所有项目共享)

层级 2: agents.md (项目级)
  -> always create clear spec, always break into small tasks,
     always verify with real evidence, always review before merge
  (项目级覆盖全局默认值)

层级 3: Plan Rules (规划强化)
  -> scope clarification + acceptance criteria + sequencing
     + verification steps + rollback thoughts

层级 4: Custom Sub-agents (专家人设)
  -> reviewer sub-agent | testing sub-agent | security sub-agent
  (实现后并行启动，各司其职)

层级 5: Parallel Workspaces (并行工作区)
  -> Workspace A: feature implementation
  -> Workspace B: tests & verification
  -> Workspace C: review / alternate implementation
  (scope 确定后并行，各自隔离)
```

---

## 核心洞察

> Model quality matters, but **workflow quality matters too** -- maybe even more.

| 组合 | 结果 |
|------|------|
| 强模型 + 草率流程 | 草率的输出 |
| 一般模型 + 严格流程 | 超预期的可靠成果 |

Agent Skills 的价值不是替代工程判断，而是**把工程判断编码为可复用的代理操作系统**。

---

## 实践要点总结

| 要点 | 说明 |
|------|------|
| 流程 > 模型 | 好的工作流比更强的模型更能提升可靠性 |
| 分阶段加载 | 不要一次性灌入所有指令，按生命周期逐步启用 |
| 始终保持核心三件套 | Spec + TDD + Review 解决大部分问题 |
| 专项按需加载 | 根据项目类型选择性加载前端/API/安全/性能技能 |
| 人设分离 | 用不同的代理/角色做审查、测试、安全审计 |
| 简化为上 | 优先简单方案，不为炫技而复杂化 |

---

## 参考资料
- [Agent Skills - GitHub (Addy Osmani)](https://github.com/addyosmani/agent-skills)
- [视频来源: AICodeKing YouTube](https://www.youtube.com/watch?v=zrbGCYGQr18)

## 相关笔记
- [[Hermes Agent Skills System]]
- [[Claude Code]]
