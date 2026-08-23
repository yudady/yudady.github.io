---
title: DevOps 教父 Patrick Debois — AI 编程时代的致命盲区与 CDLC
aliases:
  - CDLC 上下文开发生命周期
  - Context is the New Code
tags:
  - AI-engineering
  - CDLC
  - DevOps
  - SDLC
  - MCP
  - AI-security
  - type/learning-note
  - status/active
source:
  - "https://youtu.be/U8ha-xwLerQ"
  - "https://jedi.be/blog/2026/context-development-lifecycle/"
  - "https://www.bodenfuller.com/writing/cdlc"
  - "https://www.youtube.com/watch?v=bSG9wUYaHWU"
author: "Patrick Debois (Tessl), 视频解读: wow.insight"
created: 2026-06-13
updated: 2026-06-13
description: DevOps 之父提出 CDLC 框架——AI 时代上下文即新代码，软件工程正在经历从 SDLC 到 CDLC 的范式转移
level: intermediate
stars: 5
---

# DevOps 教父 Patrick Debois — AI 编程时代的致命盲区与 CDLC

> DevOps 之父 Patrick Debois 在 AI Engineer 大会上提出 CDLC（Context Development Life Cycle）框架。核心理念：**AI 编码时代，上下文（Context）已成为新代码（Context is the New Code）**。CDLC 将软件工程纪律应用于 AI Agent 的上下文管理，类比 DevOps 对 SDLC 的重塑。

## 目录

- [核心论点：从 SDLC 到 CDLC](#核心论点从-sdlc-到-cdlc)
- [CDLC 的四个阶段（Patrick Debois 原版）](#cdlc-的四个阶段patrick-debois-原版)
- [CDLC 的七阶段完整模型（Boden Fuller 扩展）](#cdlc-的七阶段完整模型boden-fuller-扩展)
- [SDLC 与 CDLC 对照表](#sdlc-与-cdlc-对照表)
- [MCP：AI 的万能插头](#mcpai-的万能插头)
- [错误预算：非确定性测试的新范式](#错误预算非确定性测试的新范式)
- [供应链安全：AI 时代的依赖地狱](#供应链安全ai-时代的依赖地狱)
- [更大的上下文窗口救不了你](#更大的上下文窗口救不了你)
- [DevOps 的平行类比](#devops-的平行类比)
- [社区反响](#社区反响)
- [参考资料](#参考资料)

---

## 核心论点：从 SDLC 到 CDLC

AI 编码 Agent 能写功能、修 bug。**瓶颈已从「写代码的速度」转移到「传达意图的有效性」**。

当前现状：
- 大多数团队的非正式上下文存储在 `.cursorrules`、散落的 `.md` 文档、Slack 线程中
- **没有版本控制、没有测试、没有冲突检测**
- 上下文腐化（Context Rot）— 过时的指导在无人察觉的情况下误导 Agent

CDLC 的核心类比：

```
DevOps 的提问方式    →    CDLC 的提问方式
─────────────────────────────────────────
"运维能不能更像开发？"    "喂给 Agent 的上下文能不能像代码一样工程化？"
```

> "LLMs are engines. Context is fuel. You can't tune the engine; that's the model vendor's job. You can engineer the fuel." — Boden Fuller

---

## CDLC 的四个阶段（Patrick Debois 原版）

### 1. Generate（生成）

将隐性的组织知识转化为 Agent 可执行的结构化规范。

- **技术上下文**：编码标准、架构模式
- **项目上下文**：时间线、优先级
- **业务上下文**：客户期望、合规要求

> 目前锁在人们脑子里或没人看的 wiki 里的东西，就是 Generate 阶段要挖掘的内容。

### 2. Evaluate（评估）

通过场景测试上下文，类似 TDD（Test-Driven Development），但测试的是规范而非代码。

- 定义场景，评估 Agent 输出是否符合意图
- 如果你不愿发布未测试的代码，为什么要发布未测试的上下文？

### 3. Distribute（分发）

将上下文视为**有版本控制的、可发布的安全包**。

- 知识需要像依赖一样跨组织扩展
- 带完整性保证和更新机制
- Skills 是打包格式：指令 + 脚本 + 参考资料 + 示例

### 4. Observe（观测）

从 Agent 在生产环境的行为中学习。

- 识别上下文不足的缺口
- 发现现实与假设的偏移（Drift）
- 迭代改进，**闭合反馈循环**

```
  ┌──────────┐
  │ Generate │ ──→ 将隐性知识结构化
  └──────────┘
       │
       ▼
  ┌──────────┐
  │ Evaluate │ ──→ 场景测试上下文质量
  └──────────┘
       │
       ▼
  ┌──────────┐
  │Distribute│ ──→ 版本化、安全分发
  └──────────┘
       │
       ▼
  ┌──────────┐     ┌──────────────────┐
  │ Observe  │ ──→ │ 识别缺口和偏移    │
  └──────────┘     └────────┬─────────┘
       ▲                     │
       └─────────────────────┘
            反馈循环（迭代改进）
```

---

## CDLC 的七阶段完整模型（Boden Fuller 扩展）

Boden Fuller 在 Patrick Debois 四阶段基础上扩展为七个阶段，更精确地对应 SDLC：

| 阶段 | SDLC 类比 | 核心问题 |
|------|-----------|----------|
| **Generate** | Plan | 应该存在什么上下文？ |
| **Compile** | Code + Build | 如何将原始上下文组装为精确数据包？ |
| **Test** | Test | 这个上下文能否产生预期的 Agent 行为？ |
| **Distribute** | Release | 其他团队/项目如何获取？ |
| **Deliver** | Deploy | 正确的上下文是否在 session 开始时到达 Agent？ |
| **Observe** | Operate + Monitor | 上下文是否有效？返回了什么信号？ |
| **Adapt** | Feedback → Plan | 下次应该改什么？ |

### 关键阶段详解

**Compile（编译）**：
- 将原始上下文组装为「阶段适配、角色限定、时效加权」的数据包
- 上下文编译器选择正确的片段，按实用性/新鲜度排序，裁剪到 token 预算
- **"99% 的团队差距在于此阶段"**

**Test（测试）**：
- 与代码测试不同，evals 是**非确定性的** — 运行 5 次，测量通过率
- 错误预算（Error Budget）替代通过/失败
- **"这是最难做对的阶段，也是大多数团队完全跳过的阶段"**

**Observe（观测）** 的三个反馈通道：

| 反馈通道 | 信号含义 |
|----------|----------|
| Agent 日志 | "我不知道怎么做 X" → 上下文缺失 |
| PR Review 反馈 | PR 被拒 → 上下文失败；修上下文而非修 PR |
| 生产故障 | 追溯写入故障代码时加载了什么上下文 |

> "Every failure is a missing piece of context."

---

## SDLC 与 CDLC 对照表

```
SDLC (代码)              CDLC (上下文)
───────────────          ──────────────────
Plan           ──────►   Generate (生成)
Code + Build   ──────►   Compile  (编译)
Test           ──────►   Test     (测试)
Release        ──────►   Distribute (分发)
Deploy         ──────►   Deliver  (交付)
Operate        ──────►   Observe  (观测)
Feedback       ──────►   Adapt    (适应)

SDLC 产出可部署的 artifacts
CDLC 产出可注入的 context
两者都通过反馈循环复合，都因缺乏纪律而退化
```

---

## MCP：AI 的万能插头

Model Context Protocol（MCP）是 Claude 推出的开放协议，作为 AI Agent 与外部系统的「万能插头」。

### 从手动 Prompt 到动态事实

| 传统方式 | MCP 方式 |
|----------|----------|
| 手动写 prompt，一次性输入 | agent.md + MCP 自动连接外部数据源 |
| 静态上下文，手动更新 | 基于实时、真实的外部事实动态解决 |
| 每次重新描述上下文 | 插拔式工具注册，Agent 按需调用 |

### MCP 在 CDLC 中的位置

```
CDLC Generate/Distribute 阶段
        │
        ▼
  ┌───────────┐
  │   MCP      │ ← 标准化的工具/数据注册协议
  │ (协议层)   │
  └───────────┘
        │
   ┌────┴────┐
   ▼         ▼
工具集成    数据源连接
(API/CLI)  (数据库/文件/API)
```

---

## 错误预算：非确定性测试的新范式

AI 输出充满随机性（非确定性），传统「非黑即白」的测试方法已经失效。

### 传统测试 vs AI 测试

| 维度 | 传统测试 | AI 输出测试 |
|------|----------|-------------|
| 确定性 | 固定输入 → 固定输出 | 相同输入 → 不同输出 |
| 通过标准 | Pass/Fail | 通过率（5 次运行 80% 通过？） |
| 质量管理 | Code Review | Error Budget + 概率思维 |
| 沙盒机制 | 预发布环境 | 沙盒裁判机制 |

### SRE 思维迁移

从 Site Reliability Engineering 借鉴：

```
传统 SRE                        AI 上下文测试
────────────                    ─────────────
SLI (服务等级指标)    ────►      Agent 输出质量指标
SLO (服务等级目标)    ────►      上下文有效性的目标阈值
Error Budget         ────►      上下文的容错空间
SLA (服务等级协议)    ────►      团队对上下文质量的承诺
```

**核心转变**：从「这个测试是否通过」到「这个上下文在 N 次运行中产生可接受结果的概率是多少」。

---

## 供应链安全：AI 时代的依赖地狱

共享上下文背后隐藏致命风险，类比软件供应链中的依赖安全问题。

### 风险对照

| 软件供应链 | AI 上下文供应链 |
|-----------|-----------------|
| 依赖地狱（Dependency Hell） | 上下文冲突 |
| 恶意 npm/PyPI 包 | 提示词木马注入（Prompt Injection Trojans） |
| XZ Utils 后门事件 | 通过共享 Skills/上下文注入恶意指令 |
| SBOM（Software Bill of Materials） | AI SBOM（上下文来源清单） |

### 防御策略

```
威胁                          防御
───────────────────────────────────────────
提示词木马注入    ────►      上下文过滤器（Context Filter）
恶意上下文包      ────►      AI SBOM（来源追踪与完整性验证）
共享 Skill 污染    ────►      版本 Pin + Code Review + 签名
上下文漂移         ────►      Observe 阶段监控 + Adapt 自动修正
```

> 类比 XZ Utils 事件：如果一个被广泛使用的共享上下文 Skill 被植入恶意指令，所有使用该 Skill 的 Agent 都可能产生危险输出。

---

## 更大的上下文窗口救不了你

人们倾向于认为无限上下文窗口能解决问题。**不能。**

> "Throwing more tokens at an agent without governance is like giving a junior developer access to the entire codebase without onboarding."

问题不在数量，而在：
- **质量**（Quality）— 是否相关且准确
- **相关性**（Relevance）— 是否匹配当前任务
- **新鲜度**（Freshness）— 是否仍然有效

这呼应了 Boden Fuller 提出的「40% 认知负载规则」：加载正确的东西，而非全部东西。

---

## DevOps 的平行类比

Patrick Debois 用 DevOps 历史类比 CDLC 的必然性：

```
DevOps 之前：                        CDLC 之前：
开发想快速发布，运维想稳定    →    人想快速用 AI，但上下文质量不可控
                                     ↓
DevOps 的突破不是更好的工具，         CDLC 的突破不是更好的 Prompt，
而是目标对齐                          而是上下文纪律
                                     ↓
CDLC 创造类似的对齐：
更好的上下文 → 更好的 Agent 输出
→ 每个人都有动力投资知识共享
→ 最接近领域知识的人终于有直接路径影响构建结果
```

---

## 社区反响

### 鸡生蛋问题
Ruslan Vlasyuk 提出：先有工具还是先有方法论？没有工具就无法采用生命周期，但没有人遵循方法论就没人造工具。

### 文档的复仇
Richard Gross 指出讽刺之处：60 年来开发者都在逃避文档，AI Agent 终于迫使我们写下来。区别在于**激励变了** — 糟糕的文档 = 糟糕的输出，立即可见。

### 并非全新概念
Tom Klaasen 将 CDLC 联系到 Peter Naur 1985 年的论文 "Programming as Theory Building"：编程本质上是建立共享理解，而非写代码。AI 只是让忽视这个事实的代价变得显而易见。

---

## 参考资料

- [Patrick Debois 原始博客：The Context Development Lifecycle](https://jedi.be/blog/2026/context-development-lifecycle/)
- [Boden Fuller：CDLC 七阶段详解](https://www.bodenfuller.com/writing/cdlc)
- [Tessl 博客：CDLC 完整文章](https://tessl.io/blog/context-development-lifecycle-better-context-for-ai-coding-agents/)
- [Patrick Debois 原始演讲：Context Is the New Code (AI Engineer Conference)](https://www.youtube.com/watch?v=bSG9wUYaHWU)
- [视频解读：wow.insight](https://youtu.be/U8ha-xwLerQ)

## 相关笔记

- [[Agent Skills 模块化设计]]
- [[MCP 模型上下文协议]]
