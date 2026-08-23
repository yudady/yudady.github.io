---
title: Anthropic Managed Agents - 解耦大脑与双手的 Agent 操作系统
aliases:
  - Agent 解耦架构
  - 宠物与牛马模式
tags:
  - ai-agents
  - anthropic
  - architecture
  - status/active
  - type/study-note
source:
  - "https://www.youtube.com/watch?v=4kgkYAGPuD0"
  - "https://www.anthropic.com/engineering/managed-agents"
author: 视频作者; Anthropic Engineering (Lance Martin, Gabe Cemaj, Michael Cohen)
created: 2025-05-12
updated: 2025-05-12
description: Anthropic 提出 Agent 四层解耦架构（大脑/双手/Session/Session Store），将 Agent 从宠物变为牛马，实现云端蜂群编排
level: intermediate
stars: 4
---

# Anthropic Managed Agents - 解耦大脑与双手的 Agent 操作系统

> Anthropic 在 2025 年 4 月发布 Managed Agents 云端服务及配套工程博客，提出将 Agent 的"大脑"（LLM + Harness）与"双手"（沙盒执行环境）彻底解耦，配合 Session 外部化和云端 Session Store，构建了一套可扩展、可恢复、安全的 Agent 运行架构。

---

## 目录

1. [核心问题：为什么 Agent 养着养着就死了？](#核心问题)
2. [解耦方案：四层架构](#四层解耦架构)
3. [Anthropic 的工程实践细节](#anthropic-的工程实践)
4. [开源复刻思路](#开源复刻思路)
5. [行业趋势与启示](#行业趋势与启示)
6. [参考资料](#参考资料)

---

## 核心问题：为什么 Agent 养着养着就死了？

**经典陷阱：宠物与牛马（Pets vs Cattle）**

传统做法把 Agent 的所有组件（模型、Harness、沙盒、Session、凭证）塞进一个容器：

```
+------------------------------------------+
|          单一容器（宠物模式）                |
|  LLM + Harness + Sandbox + Session + Keys |
|                                          |
|  任一组件挂掉 → 整个 Agent 丢失状态          |
|  配置冲突 → 必须手动调试恢复                  |
|  Prompt 注入 → 直接拿到容器内所有凭证          |
+------------------------------------------+
```

**Anthropic 的血泪史**：

- 为弥补模型缺陷写了数千行复杂 Harness
- 新模型一发布，旧代码补丁瞬间沦为垃圾
- 核心洞察：**你为模型写的修补代码注定会过时**，因为模型进化速度永远快于你重构代码的速度

**根因**：Agent 的记忆、执行环境、上下文全部耦合在一起，任何一点故障都可能导致全盘崩溃。

---

## 四层解耦架构

视频作者从 Anthropic 的 SDK 源码和博客中提炼出四层递进的解耦结构：

```
+============================================================+
|                    第4层: Session Store                      |
|              云端记忆，Agent 彻底无状态                         |
|  +------------------------------------------------------+  |
|  |                第3层: Session                         |  |
|  |          记忆树，可 fork / 回滚 / 克隆                  |  |
|  |  +------------------------------------------------+  |  |
|  |  |           第2层: Agent Harness                  |  |  |
|  |  |   工具编排、模型选择、系统提示词（静态模板）        |  |  |
|  |  |  +------------------------------------------+  |  |  |
|  |  |  |         第1层: 沙盒 (Sandbox)             |  |  |  |
|  |  |  |   无状态执行环境，随时销毁/重建              |  |  |  |
|  |  |  +------------------------------------------+  |  |  |
|  |  +------------------------------------------------+  |  |
|  +------------------------------------------------------+  |
+============================================================+
```

### 第1层：沙盒解耦 - 大脑离开容器

**核心接口**：
```
execute(name, input) -> string    # 统一工具调用接口
provision({resources})            # 标准化沙盒初始化
```

**关键变化**：
- Harness 不再住在容器里，容器变成"牛马"而非"宠物"
- 沙盒挂了？直接销毁，`provision()` 拉一个全新的
- 安全边界：凭证通过 Vault 或资源绑定注入，沙盒内零凭证可达

**安全设计对比**：

| 方面 | 耦合架构 | 解耦架构 |
|------|----------|----------|
| 凭证存储 | 容器内环境变量 | Vault / 资源级 Token |
| Prompt 注入风险 | 直接拿到所有 Key | 沙盒内零凭证可达 |
| Git 访问 | Agent 持有 Token | 初始化时注入，Agent 永不接触 |
| MCP 调用 | 直连外部服务 | 经 Proxy 中转，Proxy 从 Vault 取凭证 |

### 第2层：Agent vs Session 分离

```
Agent（静态模板）          Session（动态实例）
├── 模型选择              ├── 对话线程
├── 系统提示词            ├── 挂载的 Memory Store
├── 工具集                ├── 工作文件
├── MCP 服务器            └── 任务上下文
└── 技能定义

同一个 Agent → 10 个独立 Session → 各自互不干扰
```

**核心洞察**：
- Agent 定义"我是谁"（身份、能力、工具）
- Session 定义"我在干什么"（状态、记忆、上下文）
- Memory Store 挂载到 Session 而非 Agent → 完全解耦

### 第3层：Session 作为记忆树

视频作者发现 Anthropic SDK 中 Session 支持以下操作：

```
Session
├── fork()      # 分叉出新分支
├── rollback()  # 回滚到历史状态
├── clone()     # 克隆到新实例
└── getEvents() # 查询任意位置的事件流
```

**实用场景**：Agent 刚搜完代码库时处于"最聪明"状态，完成任务后因上下文压缩逐渐变笨。编排者可以随时 fork/回滚到那个聪明状态来处理针对性任务。

### 第4层：Session Store - 云端记忆

2025 年 4 月 19 日，`session_store` 被合并进 Claude Agent SDK：

- 记忆从本地搬到云端（Redis 缓存）
- Claude Code 可从云端恢复完整上下文
- Agent 彻底无状态 → 随时销毁、随时重建

---

## Anthropic 的工程实践

### 性能提升数据

| 指标 | 耦合架构 | 解耦架构 |
|------|----------|----------|
| p50 TTFT | 基线 | 降低约 60% |
| p95 TTFT | 基线 | 降低超过 90% |

**原因**：解耦后，不需要沙盒的 Session 不必等待容器启动，推理可以立即开始。

### Session ≠ Context Window

Anthropic 强调 Session 是独立于 Context Window 的外部记忆对象：

```
Context Window（有限、易丢失）
  ├── 上下文压缩（compaction）→ 不可逆丢失
  ├── 上下文裁剪（trimming）→ 选择性删除
  └── 风险：未来回合可能需要被丢弃的 token

Session（持久、可恢复）
  ├── append-only 事件日志
  ├── getEvents(start, end) 灵活查询
  ├── 支持回滚到任意历史位置
  └── Harness 可对事件做任意变换后再送入 Context Window
```

**关键接口**：`getEvents()` 允许大脑按位置切片查询事件流，支持：
- 从上次停止的位置继续
- 回退到某个关键决策前查看上下文
- 重新读取特定操作前的完整背景

### 多大脑 + 多双手

**多大脑**：解耦后，扩展到 N 个 Agent 只需启动 N 个无状态 Harness，仅在需要时才创建沙盒。

**多双手**：每个"手"都是 `execute(name, input) -> string` 接口，Harness 不知道沙盒是容器、手机还是模拟器。大脑之间还可以互相传递"手"。

---

## 开源复刻思路

视频作者在本地复刻了 Anthropic 的四层架构：

```
+---------------------------------------------------+
|              主控服务器（协调者）                      |
|   通过预定义任务图调度各 Worker 按规则交互             |
+---------------------------------------------------+
         |                    |                    |
    +---------+          +---------+          +---------+
    | Worker  |          | Worker  |          | Worker  |
    | Docker  |          | Docker  |          | Docker  |
    | Claude  |          | Codex   |          |  Pi     |
    | Code    |          |         |          |         |
    +---------+          +---------+          +---------+
```

**关键设计决策**：

| 决策点 | 选择 | 理由 |
|--------|------|------|
| Agent Harness 位置 | 塞进 Worker 节点 | 未完全解耦，但简化了部署 |
| 隔离单位 | Docker 容器 | 每个可随时销毁/创建 |
| 模型选择 | 每个 Worker 可独立配置 | 不同任务用不同模型，优化 Token 效率 |
| 恢复机制 | 从 Session Store 指定位置恢复 | 支持状态回滚 |
| 任务编排 | 预定义任务图（DAG） | 编码→Review→测试可并行后汇总 |

**典型任务图**：

```
编码任务：                    Deep Research 任务：
  编码1 ──┐                    搜索1 ──┐
  编码2 ──┼──→ Review ──→ 测试    搜索2 ──┼──→ 写作1 ──┐
  编码3 ──┘                    搜索3 ──┘    写作2 ──┼──→ 报告/PPT
                                           写作3 ──┘
```

**待完善**：结果评估模块尚未实现。

---

## 行业趋势与启示

### 海外已发生的事

| 公司/项目 | 方向 |
|-----------|------|
| Palantir AIP | Agent 作为企业基础设施 |
| Anthropic Managed Agents | 云端 Agent 操作系统 |
| OpenAI Frontier | 企业级 Agent 平台 |
| FDE（新兴职业） | 全栈 Agent 工程师 |

### 核心趋势判断

1. **编排 + 蜂群是今年主线** - 单 Agent 向多 Agent 协作演进
2. **Agent 从个人工具 → 企业基础设施** - 不是"养宠物"而是"运营牛马"
3. **计算模式变革** - 从固定长期容器 → 动态创建/销毁的 Agent 沙盒群
4. **沙盒技术演进** - Docker 启动耗时仍是大瓶颈，毫秒级沙盒（如腾讯 Cube Sandbox）是方向
5. **Harness 职责上移** - 从"控制模型"转向"提供原语 + 安全边界 + 性能优化"

### 最佳实践清单

- **推荐做法**：
  - Agent 模板与运行时 Session 分离
  - 沙盒无状态化，凭证外置到 Vault
  - Session 作为可查询的外部记忆，不依赖 Context Window
  - 不同任务阶段用不同大小模型，优化 Token 效率

- **避免做法**：
  - 把 Agent 所有组件塞一个容器（宠物模式）
  - 在 Harness 里硬编码模型缺陷的补丁（注定过时）
  - 用复杂的 RAG 管道做本可由模型自行处理的上下文管理

---

## 参考资料

- [Scaling Managed Agents: Decoupling the brain from the hands](https://www.anthropic.com/engineering/managed-agents) - Anthropic 官方工程博客（2025-04-08）
- [Building Effective AI Agents](https://www.anthropic.com/research/building-effective-agents) - Anthropic Agent 设计指南
- [The Architectural Genius of Anthropic's Managed Agents](https://www.epsilla.com/blogs/anthropic-managed-agents-decoupling-brain-hands-enterprise-orchestration) - Epsilla 深度分析
- [Claude Managed Agents API Docs - Sessions](https://platform.claude.com/docs/en/managed-agents/sessions) - 官方 API 文档

## 相关笔记

- [[Anthropic Building Effective Agents]]
