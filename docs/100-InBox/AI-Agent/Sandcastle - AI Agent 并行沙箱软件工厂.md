---
title: Sandcastle - AI Agent 并行沙箱软件工厂
aliases: [sandcastle, AI software factory, AFK coding]
tags:
  - ai-agent
  - sandbox
  - automation
  - status/active
  - type/doc
source:
  - "https://youtu.be/1v5Ek0qDnnw"
  - "https://github.com/mattpocock/sandcastle"
author: 思思主播（脈報）/ Matt Pocock
created: 2026-06-16
updated: 2026-06-16
description: Matt Pocock 开源 Sandcastle，将 Claude Code 和 Codex 关进隔离沙箱并行执行，用 GitHub Issues 当队列，实现自动化软件工厂流水线。核心洞察：流程 > 工具，沙箱是自动化的绝对前提。
level: intermediate
stars: 4
---

# Sandcastle - AI Agent 并行沙箱软件工厂

> Matt Pocock 开源 Sandcastle（TypeScript 库，4,900+ stars），把 Claude Code 和 Codex 放进隔离 Docker 沙箱并行写代码，GitHub Issues 当任务队列，实现规划 → 实现 → 互审 → 合并的全自动软件工厂。核心结论：**流程 > 工具，沙箱隔离是自动化 agent 的绝对前提**。

---

## 目录

- [核心洞察：沙箱是前提](#核心洞察沙箱是前提)
- [Sandcastle 架构拆解](#sandcastle-架构拆解)
- [四步软件工厂产线](#四步软件工厂产线)
- [两个实战技巧](#两个实战技巧)
- [实际效果](#实际效果)
- [Sandcastle vs YOLO 模式对比](#sandcastle-vs-yolo-模式对比)
- [沙箱隔离方案对比](#沙箱隔离方案对比)
- [快速上手](#快速上手)
- [资料来源](#资料来源)

---

## 核心洞察：沙箱是前提

大多数人研究 AI agent 编程，关注的是"怎么写更好的 Prompt"。但真正卡住全自动化的不是 Prompt，而是**权限与安全**。

### YOLO 模式 vs 沙箱模式

| 维度 | YOLO 模式（放飞） | 沙箱模式（隔离） |
|------|-------------------|------------------|
| 权限控制 | 完全放开 | 限制在容器内 |
| 交互需求 | 经常跳出权限确认，等你按同意 | 零交互，AFK 运行 |
| 安全风险 | 可能删掉家目录、搞坏系统 | 弄砸了也烧不到宿主系统 |
| 并行能力 | 多个 agent 互相踩脚 | 各自沙箱，互不干扰 |
| 适用场景 | 人工盯着，随时介入 | 整晚挂机无人值守 |

> ⚠️ **重要提醒**：沙盒防得了本机端灾难，但**不能防止数据泄露**。容器内的 AI 仍可能把公司机密代码传给第三方模型（如 OpenAI/Anthropic API）。企业内部使用需自行评估数据安全风险。

---

## Sandcastle 架构拆解

### 核心：就一个函数

```typescript
Sandcastle.run(agent, sandbox, prompt)
```

| 参数 | 说明 | 示例 |
|------|------|------|
| `agent` | 用哪个 AI agent | `claude-code` / `codex` |
| `sandbox` | 隔离环境类型 | `docker` / `podman` / `vercel` |
| `prompt` | 执行指令 | 任务描述 + 规范 |

完全解耦了模型和环境 — 今天用 Claude，明天换 Codex，改一行代码就行。

### 隔离原语：Git Worktree

Sandcastle 的核心设计决策是用 **Git Worktree** 作为隔离基础：

```
主仓库 (main branch)
├── worktree-agent-1 (feature/task-1)  ← Agent A 在这里写代码
├── worktree-agent-2 (feature/task-2)  ← Agent B 在这里写代码
└── worktree-agent-3 (feature/task-3)  ← Agent C 在这里写代码
```

每个 agent 获得独立的 worktree + 独立分支，Docker 容器 bind-mount 这个 worktree，**无需文件同步** — agent 直接通过挂载写入。

项目 `docs/adr/` 下有 **14 份架构决策记录（ADR）**，记录了为什么选择 worktree 而非其他方案。

### 分支策略

| 策略 | 行为 | 适用场景 |
|------|------|----------|
| `head` | 直接写主分支 | 需要立即生效的修改 |
| `merge-to-head` | 临时分支完成后合并回主分支 | **默认策略**，AFK 最常用 |
| `branch` | 创建显式命名分支 | 需要人工审查的 PR 流程 |

---

## 四步软件工厂产线

这是 Sandcastle 最有价值的部分 — 把人类团队的协作流程一比一映射到 AI。

```
┌─────────────┐     ┌──────────────────┐     ┌──────────────────┐     ┌───────────┐
│  GitHub     │     │  Step 1          │     │  Step 2          │     │  Step 3   │
│  Issues     │────▶│  Planner 规划者   │────▶│  Implementer     │────▶│  Reviewer │
│  (带标签)   │     │  分析工单→JSON    │     │  并行写代码       │     │  交叉审查  │
└─────────────┘     └──────────────────┘     └──────────────────┘     └─────┬─────┘
                                                                       │
                              ┌──────────────────┐                     │
                              │  Step 4          │◀────────────────────┘
                              │  Merger 合并者    │
                              │  解决冲突→合并   │
                              └──────────────────┘
```

### Step 1: Planner（规划者）

```
GitHub Issue (sandcastle 标签)
    │
    ▼
读取 Issue 内容 + 评论
    │
    ▼
判断是否被阻塞 (blocked)?
    │
    ├── 是 → 跳过，等下次轮询
    │
    └── 否 → 输出 JSON 执行计划
             (分配给 Implementer)
```

### Step 2: Implementer（实现者）

- 每个工单**各自开启独立沙箱 + 独立分支**
- 全部**并行处理**，互不干扰
- 沙箱内的 AI 自由跑指令、安装依赖、先写测试再写实现

### Step 3: Reviewer（审查者）

| 审查方式 | 说明 | 效果 |
|----------|------|------|
| 同模型审查 | Claude 写 → Claude 审 | 基础审查，可能盲区重合 |
| **跨模型互审** | Claude 写 → Codex 审 | ✅ 利用模型差异抓盲点，类似"不同门派高手互相挑刺" |

### Step 4: Merger（合并者）

- 派一个强能力 agent 当"资深合并工程师"
- 专门处理多分支并行导致的合并冲突
- **弹性设计**：可替换为标准 PR 流程，最终由人类把关

---

## 两个实战技巧

### 技巧 1：内嵌 Prompt 指令

在 Prompt 中用 `` !`command` `` 语法嵌入 shell 指令，系统会在组装 Prompt 时实时执行：

```
请审查以下代码变更：
!`git diff`

重点关注：
!`cat .cursorrules`
```

效果：`git diff` 的最新输出直接喂给 AI，**无需手动复制粘贴**。

### 技巧 2：内建代码规范

在 Reviewer 的 Prompt 中直接写入团队 Coding Standards：

```
你是代码审查者。请严格按照以下规范审查：

1. 函数长度不超过 50 行
2. 所有公开 API 必须有 JSDoc
3. 禁止使用 any 类型
4. 测试覆盖率不低于 80%
5. [你团队的具体规范...]

审查代码：
!`git diff main...HEAD`
```

每个 agent 产出的代码都会被你的团队标准检验。

---

## 实际效果

Matt Pocock 的演示：开了一个"创建 TypeScript 模板"的工单。

| 自动完成项 | 说明 |
|------------|------|
| 项目骨架 | TypeScript 配置、目录结构 |
| 测试框架 | Vitest 配置 + 示例测试 |
| CI 流程 | GitHub Actions 配置 |
| 命令行工具 | 完整的 CLI 实现 |
| 代码审查 | 自动通过审查 |
| 自动合并 | 合并到主分支 |
| 关闭工单 | GitHub Issue 自动关闭 |

全程零人工介入。

---

## Sandcastle vs YOLO 模式对比

| 对比维度 | Sandcastle 模式 | 传统 YOLO 模式 |
|----------|-----------------|----------------|
| 隔离方式 | Docker/Podman 沙箱 + Git Worktree | 直接在主仓库操作 |
| 并行能力 | ✅ 多 agent 并行，各自沙箱 | ❌ 互相踩脚 |
| 安全性 | ✅ 弄砸了不影响宿主系统 | ❌ 可能删库 |
| 自动化程度 | ✅ AFK 全自动 | ❌ 需要人工确认权限 |
| 合并冲突 | ✅ Merger agent 自动处理 | ❌ 需要人工解决 |
| 代码审查 | ✅ 跨模型互审 | ❌ 依赖人工 |
| 适合场景 | 批量任务、夜间挂机 | 实时交互、单任务 |

---

## 沙箱隔离方案对比

| 方案 | 隔离级别 | 性能 | 适用场景 |
|------|----------|------|----------|
| **Docker** | 容器级 | ⚡ 快 | 本地开发、CI/CD |
| **Podman** | 容器级（无守护进程） | ⚡ 快 | 无 root 环境的容器隔离 |
| **Vercel** | microVM 级 | 中等 | 云端执行、无本地依赖 |
| **Daytona** | 云端开发环境 | 中等 | 远程开发团队 |
| **No-Sandbox** | ❌ 无隔离 | ⚡⚡ 最快 | 仅测试用，不建议生产 |

---

## 快速上手

```bash
# 1. 安装
npm install --save-dev @ai-hero/sandcastle

# 2. 初始化（选择 workflow 模板）
npx sandcastle init
# 可选模板: blank / simple-loop / sequential-reviewer
#           parallel-planner / parallel-planner-with-review

# 3. 配置 .sandcastle/.env
#    - 选择 Agent (claude-code / codex)
#    - 选择 Sandbox (docker / podman / vercel)
#    - 填入 API keys

# 4. 运行
npx tsx .sandcastle/main.ts
```

**Workflow 模板说明**：

| 模板 | 流程 | 适用场景 |
|------|------|----------|
| `blank` | 空白，自己写 | 高度定制 |
| `simple-loop` | 单 agent 循环 | 简单批量任务 |
| `sequential-reviewer` | 实现→审查（串行） | 质量优先 |
| `parallel-planner` | 规划→并行实现 | 速度优先 |
| `parallel-planner-with-review` | 规划→并行实现→审查→合并 | **推荐**，视频演示用 |

---

## 核心总结

> **流程 > 工具**

三个关键认知：

1. **沙箱是自动化的绝对前提** — 不解决隔离问题，写再好的 Prompt 也没用
2. **产线模式可复制** — 规划→实现→互审→合并，不绑定任何特定工具
3. **先想清楚起点和终点** — 任务从哪来（队列）、成果怎么合并回去（合并策略），中间用哪个 AI 工具反而是最不重要的

### 决策树：什么时候该用 Sandcastle 模式

```
你想让 AI agent 自动写代码？
│
├── 需要人工实时盯着？
│   ├── 是 → YOLO 模式够了
│   └── 否 → 继续
│
├── 任务是否可拆分成独立工单？
│   ├── 否 → 单 agent 串行即可
│   └── 是 → 继续
│
├── 需要并行处理多个任务？
│   ├── 否 → sequential-reviewer 模板
│   └── 是 → parallel-planner-with-review 模板
│
└── 合并策略？
    ├── 信任 AI → merge-to-head（自动合并）
    └── 需要人工把关 → branch + 标准 PR 流程
```

---

## 资料来源

- **原始视频**：[I Open-Sourced My Own AFK Software Factory](https://www.youtube.com/watch?v=E5-QK3CDVQM) — Matt Pocock [@mattpocockuk]
- **中文解说**：[他讓一群 Claude Code 進沙盒並行寫程式，組成軟體工廠](https://youtu.be/1v5Ek0qDnnw) — 思思主播（脈報）
- **GitHub**：[mattpocock/sandcastle](https://github.com/mattpocock/sandcastle) — 4,900+ stars, MIT, TypeScript
- **深度文章**：[Sandcastle AI Agent Sandbox Software Factory](https://heymaibao.com/sandcastle-ai-agent-sandbox-software-factory/)
- **代码分析**：[codeline.co repo review](https://www.codeline.co/thoughts/repo-review/2026/sandcastle-orchestrate-ai-coding-agents-in-isolated-sandboxes)

## 相关笔记

- [[AI Agent 编排模式]]
- [[Git Worktree 实践]]
