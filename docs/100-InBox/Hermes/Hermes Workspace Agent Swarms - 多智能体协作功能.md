---
title: Hermes Workspace Agent Swarms - 多智能体协作功能
aliases: [Hermes Swarms, Agent Swarms, Hermes 多智能体]
tags:
  - hermes-agent
  - multi-agent
  - swarm
  - orchestration
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=pSzeCN4NoBU"
author: AI Profit Boardroom
created: 2026-05-02
updated: 2026-05-02
description: Hermes Workspace 的 Agent Swarms 功能，允许多个 AI Agent 同时协作完成复杂任务，自动路由、角色分工、可视化监控
level: intermediate
stars: 3
---

# Hermes Workspace Agent Swarms - 多智能体协作功能

> [!info] 基本信息
> - **视频**: [Hermes Agent Swarms](https://www.youtube.com/watch?v=pSzeCN4NoBU)
> - **频道**: AI Profit Boardroom
> - **时长**: ~9 分钟
> - **关键项目**: [Hermes Agent](https://github.com/nousresearch/hermes-agent) (Open Core)

## 一句话定位

Hermes Workspace 的 Swarms 功能让你同时运行多个 AI Agent，各自承担不同角色（规划、构建、审查等），由 Orchestrator 自动协调和路由任务。

## 核心解决的问题

1. **串行瓶颈** -- 单 Agent 必须按步骤执行，容易卡住或变慢。Swarms 模式让多 Agent 并行工作
2. **任务分工** -- 不再是一个 Agent 包揽一切，而是专业化的 Agent 团队各司其职
3. **可视化监控** -- Workspace UI 提供办公室视图、终端视图、看板，实时查看所有 Agent 状态

## 架构概览

```
                    用户 Mission
                        |
                   +---------+
                   | Aurora  |  (主 Agent / Orchestrator)
                   +---------+
                        |
              Auto Route / Decompose
                   /    |    \
                  /     |     \
           +------+ +------+ +------+
           |Build | |Plan  | |Review|
           |Agent | |Agent | |Agent |
           +------+ +------+ +------+
              |        |        |
              v        v        v
           Local Output (文件/代码/报告)
```

## Swarm 角色

| 角色 | 职责 | 说明 |
|------|------|------|
| **Builder** | 代码/内容构建 | 执行实际的开发或创作任务 |
| **Reviewer** | 代码审查/质量检查 | 审查 Builder 的输出 |
| **Triage** | 任务分类和优先级 | 分析任务并分配给合适的 Agent |
| **Lab Sage** | 实验和分析 | 研究、测试、数据分析 |
| **Scribex** | 文档和写作 | 生成文档、博客、报告 |
| **Custom** | 自定义角色 | 自定义 system prompt + skills |

每个角色预设了对应的 system prompt 和 skills，也可完全自定义。

## 自动路由（Auto Routing）

核心机制：给 Orchestrator 一个目标，它自动：

1. 解析 mission，分解为子任务
2. 根据子任务类型匹配合适的 Agent 角色
3. 并行分派任务给各 Agent
4. 汇总各 Agent 输出

```
输入: "创建 SEO 博客 + 关键词策略 + 内容"
  |
  v
Orchestrator 自动分解:
  - Agent 1: 关键词研究 + 竞品分析
  - Agent 2: 内容日历规划 (90 天)
  - Agent 3: 博客文章 HTML 构建
  - Agent 4: 内链策略 + Featured Snippet 目标
```

## Workspace UI 功能

### Swarm 管理

- **左侧菜单** → Swarms 进入管理界面
- **Add Swarm** → 选择角色预设或自定义
- **Settings** → 配置路由目标、自动规划参数
- **Mobile Access** → 手机端远程监控 Agent 团队

### 可视化视图

```
+------------------+  +------------------+  +------------------+
| Office View      |  | Terminal View    |  | Kanban Board     |
| (圆形/网格布局)  |  | (各 Agent 终端)  |  | (任务看板)       |
+------------------+  +------------------+  +------------------+
- Agent 状态      |  - 实时输出        |  - 待办/进行/完成 |
- blocked/ready   |  - 独立日志        |  - 拖拽排序       |
- 任务分配        |  - 错误追踪        |  - 进度跟踪       |
```

### Agent 状态

| 状态 | 含义 | 处理方式 |
|------|------|----------|
| Ready | 空闲等待 | 可分配新任务 |
| Working | 执行中 | 正常监控 |
| Blocked | 被阻塞 | 检查 API 配置 / 依赖任务 |

## 实际演示效果

视频演示：4 个 Agent 在 **5-10 分钟**内完成：
- 关键词研究 + 竞品分析
- 90 天内容日历（发布日期、标题、关键词、字数、内容类型）
- 5 篇博客文章 HTML 构建
- 内链策略（入链/出链规划）
- Featured Snippet 目标定位
- 实施检查清单

## 设置步骤

```
1. 安装 Hermes Workspace（终端执行安装命令）
2. 运行 Hermes Gateway
3. 启动 Workspace UI（localhost）
4. 更新到最新版本（如已有旧版）
5. 左侧菜单 → Swarms
6. Add Swarm → 选择角色 / 自定义
7. 在 Aurora (主 Agent) 中描述 mission
8. 选择单 Agent 或自动团队模式
9. Route Mission → 自动开始执行
```

## 已知问题

- Swarms 功能偶有 bug（早期阶段）
- Agent 显示 blocked 时，通常是 API 配置问题
- 建议用 Claude Code 辅助排查连接问题

## 与我们的使用场景

```
当前 Hermes Agent 配置:
- 提供商: zai (glm-5-turbo)
- 平台: Telegram + Local
- 已有 Skills: dev-pay, dev-workflow, dev-ship, dev-testing 等

Swarms 适用场景:
- 大规模代码审查（Reviewer Agent）
- 多模块并行开发（Builder Agents）
- 自动化测试 + 部署（Lab Sage + Builder）
- 文档生成（Scribex Agent）
```

## 总结

```
Hermes Agent    = 自我改进的 AI Agent（CLI 核心）
Hermes Workspace = 本地运行的控制面板（Web UI）
Swarm Mode      = 多 Agent 并行协作，角色分工
Orchestrator    = 任务分解 + 自动路由
所有输出         = 本地存储
```

---

## 参考资料
- [Hermes Agent GitHub](https://github.com/nousresearch/hermes-agent)
- [视频: Hermes Agent Swarms Tutorial](https://www.youtube.com/watch?v=pSzeCN4NoBU)
- [Hermes Agent 文档](https://hermes-agent.nousresearch.com/docs)

## 相关笔记
- [[../AI/Rapid-MLX - Apple Silicon 最快本地 AI 推理引擎]]
