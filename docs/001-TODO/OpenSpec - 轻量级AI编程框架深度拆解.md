---
title: OpenSpec - 轻量级 AI 编程框架的逆袭
aliases: [OpenSpec, SDD, 规范驱动开发]
tags:
  - ai-agent
  - spec-driven-development
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=vP3zqckJhT0"
  - "https://github.com/Fission-AI/openspec"
author: 影片作者；OpenSpec by Fission-AI
created: 2026-07-09
updated: 2026-07-09
description: |
  深度拆解 OpenSpec 的核心理念——规范驱动开发（SDD），对比 Superpowers 框架，
  涵盖工作流四阶段、CLI 分工、Stores 跨仓库管理，以及 AI 时代工程师角色的范式转移。
level: intermediate
stars: 4
---

# OpenSpec — 轻量级 AI 编程框架的逆袭

> OpenSpec 是 Fission-AI 开源的一款规范驱动开发（Spec-Driven Development, SDD）框架，
> 专注用轻量级文档把需求与约束交代清楚，再让 AI 编码助手严格按规范执行。
> 适合独立开发者和追求快速迭代的中小型团队。GitHub 59.1k stars, TypeScript 实现。

## 目录

- [[#一、AI 编程时代的核心痛点]]
- [[#二、OpenSpec 核心理念与工作流]]
- [[#三、OpenSpec 进阶工程与分工设计]]
- [[#四、未来软件工程范式转移]]
- [[#参考资料]]

---

## 一、AI 编程时代的核心痛点

### 表面修复与缺乏系统设计

直接将代码丢给 AI（如 Cursor、Claude Code）处理复杂 Bug 时，AI 往往「一顿操作猛如虎」：
改了多个文件、加入一堆新的状态管理，但**治标不治本**。缺乏整体系统设计上下文，
AI 只在表面上堵住漏洞，修复旧 Bug 的同时引入新 Bug。

### 上下文碎片化与无状态对话

开发新特性（如多币种支付）时，AI 不知道系统的既有架构约束、数据库分片策略、或团队代码风格。
更糟的是，在不同对话窗口切换时，AI 会**迅速遗忘**先前讨论，提出互相矛盾的建议。

```
传统 AI 编程的困境：

  开发者 → 丢代码给 AI → AI 盲目修改 → 引入新 Bug
                ↑
          缺少系统设计上下文

  对话 1：AI 建议 X 方案
  对话 2：AI 建议 Y 方案（与 X 矛盾）
                ↑
          上下文碎片化
```

---

## 二、OpenSpec 核心理念与工作流

### 与 Superpowers 框架的定位对比

| 维度 | Superpowers | OpenSpec |
|------|------------|----------|
| Stars | ~240k | ~59k |
| 定位 | 强约束方法论全家桶 | 轻量、灵活的规范驱动 |
| 包含模块 | 头脑风暴、Git 工作流、TDD、代码评审 | 仅规范文档 + 闭环验证 |
| 适用场景 | 大型团队、接受繁复流程 | 独立开发者 / 中小型团队 |
| 代理绑定 | 绑定特定代理 | 不强制特定代理或测试 |
| 核心理念 | 完整软件工程流程 | 用文档交代清楚，让 AI 执行 |

### 规范驱动（SDD）的四个核心文件

OpenSpec 的核心在于一组轻量但完整的规范文档，存放在 `openspec/changes/<change-name>/` 下：

```
openspec/changes/add-dark-mode/
├── proposal.md        ← 为什么做、根本原因
├── specs/             ← 需求定义 + 边界场景
│   ├── core.md
│   └── edge-cases.md
├── design.md          ← 技术方案 + 架构决策
└── tasks.md           ← 实施清单（文件路径 + 预期改动 + 验证步骤）
```

| 文件 | 职责 | 关键问题 |
|------|------|---------|
| `proposal.md` | 说明改动动机 | 为什么做这个？根本原因是什么？ |
| `specs/` | 定义需求与边界 | 预期行为是什么？余额不足、网络超时怎么处理？ |
| `design.md` | 技术方案决策 | 用什么架构？关键依赖是什么？ |
| `tasks.md` | 具体实施清单 | 改哪些文件？怎么验证？ |

### 工作流四阶段

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│ Explore  │───→│ Propose  │───→│  Apply   │───→│ Verify & │
│ /opsx:   │    │ /opsx:   │    │ /opsx:   │    │ Archive  │
│ explore  │    │ propose  │    │  apply   │    │ /opsx:   │
└──────────┘    └──────────┘    └──────────┘    │ verify   │
                                                 │ /opsx:   │
                                                 │ archive  │
                                                 └──────────┘
```

| 阶段 | 命令 | 产出 |
|------|------|------|
| **探索** | `/opsx:explore` | 无成本的头脑风暴，梳理思路 |
| **计划** | `/opsx:propose` | 自动创建四核心文件（proposal + specs + design + tasks） |
| **实施** | `/opsx:apply` | AI 严格按照 tasks.md 清单编码 |
| **验证与归档** | `/opsx:verify` → `/opsx:archive` | 检查实现 vs 规范 → 归档并更新全局规范 |

---

## 三、OpenSpec 进阶工程与分工设计

### 规范约束与反馈校验的闭环

```
         proposal.md
              │
    ┌─────────┼─────────┐
    ▼         ▼         ▼
  specs/    design.md  tasks.md
    │         │         │
    └─────────┼─────────┘
              │
         /opsx:apply
              │
              ▼
         代码实现
              │
         /opsx:verify ──→ 不匹配 → 修改文档 → 重新 apply
              │
              ▼ (匹配)
         /opsx:archive
              │
              ▼
         更新全局规范
```

靠文件实施规范约束，靠 `apply` → `verify` → `validate` → `archive` 命令形成闭环。
开发过程中需求有变时，随时修改四核心文档，AI 根据最新版本继续迭代。

### 聊天命令与终端 CLI 的高效分工

| 层级 | 工具 | 用途 |
|------|------|------|
| **高层规划** | 编辑器对话（Cursor / Claude Code） | `/opsx:explore`、`/opsx:propose` 理解意图、规划方案 |
| **低层验证** | 终端 CLI | `openspec validate --all --strict` 严格校验 |

```bash
# CLI 常用命令

# 严格校验所有规范
openspec validate --all --strict

# 查看变更差异（JSON 格式，仅增量）
openspec show <change> --json --deltas-only

# 生成完整上下文（供 AI 读取）
openspec instructions

# 初始化项目
openspec init

# 更新配置
openspec config profile    # 选择工作流 profile
openspec update             # 应用到项目
```

### Stores：跨仓库全局规范管理（v1.5.0 Beta）

解决微服务架构下，一个功能需要修改前后端等多个仓库、规范分散的问题。

```
传统多仓库开发：

  Repo A（前端）── specs-a/
  Repo B（后端）── specs-b/      ← 规范分散，AI 看不到全局
  Repo C（共享库）── specs-c/

Stores 方案：

  Store Repo ── openspec/
       │
       ├── 全局规范（单一数据源）
       └── openspec.config.yaml（引用配置）
              │
       ┌──────┼──────┐
       ▼      ▼      ▼
  Repo A  Repo B  Repo C
  （引用） （引用） （引用）
```

| 特性 | 说明 |
|------|------|
| 独立规范仓库 | 一个 Store 作为单一数据源 |
| 配置引用 | 各代码仓库通过 `openspec.config.yaml` 引用 Store |
| 摘要 + 精确拉取 | AI 获取全局设计，同时避免上下文爆炸 |
| 跨仓库特性 | 一个 change 对应多个代码仓库的改动 |

---

## 四、未来软件工程范式转移

### 文档不再是官僚主义，而是工程纪律

```
传统时代：人肉维护文档 → 极其痛苦 → 容易过时 → 文档 = 废纸
                                        ↓
AI 时代：AI 生成 / 检查 / 更新 → 维护成本大幅降低
                                        ↓
          文档 = 可被 Agent 读取的工程上下文
                = 控制 AI 的「缰绳」
```

OpenSpec 的规范不是给人看的说明书，而是**可被 Agent 读取、检查和更新的工程上下文**——
控制 AI 这匹野马的缰绳。

### 工程师角色的重新定义

| 维度 | 过去 | 未来 |
|------|------|------|
| 精力分配 | 大部分花在「如何实现」 | 转移到「如何定义」 |
| 核心技能 | 写代码 | 写规范、定义边界、架构设计 |
| 角色定位 | 代码编写者 | 需求定义者 + 系统架构师 |
| 与 AI 的关系 | AI 辅助写代码 | AI 按规范写代码 |

### 核心洞察

> AI 能力的上限，取决于工程师提供的上下文质量。

OpenSpec 通过规范约束与反馈校验的闭环，将结构化、可被 Agent 读取的文档喂给 AI，
把无状态、碎片化的 AI 聊天转化为具备工程纪律的协作过程。

### 行动建议

- ✅ 停止直接丢代码让 AI 盲目修复，改为先头脑风暴、落实 proposal 与 tasks
- ✅ 大型团队选 Superpowers，独立开发者/中小团队选 OpenSpec
- ✅ 将心力从手敲代码转移到：精准描述业务边界、架构约束、撰写高质量规范

---

## 参考资料

- [OpenSpec GitHub 仓库](https://github.com/Fission-AI/openspec) — 59.1k stars, MIT License
- [OpenSpec 官方文档](https://github.com/Fission-AI/OpenSpec/blob/main/docs/README.md)
- [Stores Beta 用户指南](https://github.com/Fission-AI/OpenSpec/blob/main/docs/stores-beta/user-guide.md)
- [影片来源](https://www.youtube.com/watch?v=vP3zqckJhT0)
