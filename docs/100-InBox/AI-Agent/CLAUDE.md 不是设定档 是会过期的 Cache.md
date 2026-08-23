---
title: CLAUDE.md 不是设定档，是会过期的 Cache
aliases: [CLAUDE.md Cache, Dream Pass, AI 上下文维护]
tags:
  - claude
  - claude-md
  - prompt-engineering
  - context-management
  - dreaming
  - ai-workflow
  - status/active
  - type/doc
source:
  - "https://youtu.be/00y_w-HWb-E"
  - "https://platform.claude.com/docs/en/managed-agents/dreams"
  - "https://code.claude.com/docs/en/memory"
  - "https://x.com/Mnilax/status/2054955621829443903"
author: AI 雷达站
created: 2026-05-18
updated: 2026-05-18
description: |
  基于视频解析 + Anthropic 官方 Dreams 文档，阐述 CLAUDE.md 应被视作会过期的 Cache 而非静态配置文件，Dream Pass 机制会删除 73% 的过时规则，Rubric 评分标准比模型选择更重要（85% vs 15%）。
level: beginner
stars: 3
---

# CLAUDE.md 不是设定档，是会过期的 Cache

> **核心观点**：停止把 CLAUDE.md 当成写好就不管的静态配置。它应该被当作「会过期的 Cache」——需要定期清理和更新。实测 Dream Pass 机制会自动删除 73% 的过时规则，与其花时间写死规定，不如打磨评分标准（Rubric）。

## 目录

- [[#核心观念转换：设定档 → Cache]]
- [[#Dream Pass：73% 的规则会被自动删除]]
- [[#Rubric 85% vs Model 15%]]
- [[#14-30 天维护循环]]
- [[#Re-dream 三步骤]]
- [[#Anthropic 官方 Dreams 机制]]
- [[#CLAUDE.md 最佳实践]]

---

## 核心观念转换：设定档 → Cache

### 两种思维模式对比

| 思维模式 | 行为 | 结果 |
|----------|------|------|
| ❌ 静态设定档 | 写完就放着不管 | 规则越积越多，互相矛盾，AI 产出失控 |
| ✅ 会过期的 Cache | 定期清理、更新、重新对齐 | AI 始终保持敏锐，产出稳定 |

### Cache 的类比

把 CLAUDE.md 想象成**浏览器缓存**：

```
浏览器缓存塞太满 → 上网变慢、出错误页面
        ↕ 一模一样的逻辑
CLAUDE.md 塞太满 → AI 处理新任务时困惑、产出退化
```

- 过去的指令 + 堆积的旧背景信息 → 悄悄拖慢 AI
- 项目推进后，旧规则开始与新需求**互相矛盾**
- AI 为了跑完当下任务，会**自然洗掉**它认为过时的设定

### 关键认知

- CLAUDE.md 是**暂时且会衰退的记忆空间**
- 需要**主动清理和更新**
- 跟写好就永久固定的代码完全不同
- 这决定了 AI 是「神队友」还是「猪队友」

---

## Dream Pass：73% 的规则会被自动删除

### 实测数据

来源：@Mnilax 用 80 行 Python 本地复刻 Anthropic Dreaming 功能，跑完 100 个 Claude session 后分析。

```
Dream Pass 删除比例：73%
剩余有效规则：27%
```

### 什么是 Dream Pass

Dream Pass 是 Anthropic Claude 内建的一种**自然过滤机制**：

```
你写入的规则 ──────────────────────────────────┐
  ├─ 过时的规则（项目已推进）                    │
  ├─ 互相矛盾的规则                             ├──→ Dream Pass ──→ 73% 被删除
  ├─ 过度复杂的规则                             │
  └─ AI 认为用不到的规则                        │
                                                  │
  └─ 仍然有效的规则 ──────────────────────────────→ 27% 保留
```

### 为什么静态规则行不通

1. **规则膨胀**：花几天写像法律条文一样的 CLAUDE.md
2. **项目演进**：项目推进后，旧规则不再适用
3. **互相矛盾**：新规则与旧规则冲突
4. **Dream Pass 清洗**：AI 为完成当前任务，自动忽略过时设定

### 结论

> 不管你写了多少密密麻麻的静态规则，系统内建的 Dream Pass 最后会大刀一挥，删掉高达 73%。

---

## Rubric 85% vs Model 15%

### 影响力分布

```
┌─────────────────────────────────────┐
│          Rubric（评分标准）           │
│              85%                    │
│  清晰的评判依据 + 具体案例           │
└─────────────────────────────────────┘

┌──────────┐
│  Model   │
│  模型    │  15%
│  选择    │
└──────────┘
```

### 核心逻辑

| 维度 | Rubric（评分标准） | Model（模型） |
|------|-------------------|---------------|
| 作用 | 主动引导行为 | 处理数据 |
| 本质 | 北极星（指引方向） | 引擎（执行任务） |
| 重要性 | 压倒性的 85% | 微乎其微的 15% |
| 维护 | 需要随项目更新 | 跟随版本自动升级 |

### 实际启示

- ❌ 条列 100 条「不准做这个不准做那个」的死规则 → 无效
- ✅ 明确告诉 AI「什么是好的产出」+ 清晰的评判依据 + 具体案例 → 有效
- ✅ AI 需要**指引方向的北极星**，不是**绑死它的锁链**

### 停止追逐模型焦虑

```
常见误区：「要出新模型了，我是不是落后了？」

真相：战场不在模型版本，而在评分标准。
      强大的 Rubric + 旧模型 > 糟糕的 Rubric + 最新模型
```

---

## 14-30 天维护循环

### 维护节奏

```
Day 1          Day 14           Day 30
  │               │                │
  ▼               ▼                ▼
设定初始提示   检查产出退化     彻底 Re-dream
(符合当前       (AI 开始走钟？)   (清空旧 Cache)
 项目状态)         │                │
                   │                ▼
                   │           重新建立上下文
                   │           (对齐最新目标)
                   │                │
                   └────────────────┘
                         循环
```

### 为什么是这个节奏

- **14 天**：足以观察到 AI 产出的退化趋势
- **30 天**：项目状态通常已有显著变化，旧规则必然过时
- 适用于任何规模的团队，包括一人公司

---

## Re-dream 三步骤

### 步骤拆解

```
Step 1                    Step 2                    Step 3
评分标准审视               大刀阔斧清理               重建上下文
┌──────────────┐         ┌──────────────┐          ┌──────────────┐
│ 拿着精心设计  │         │ 把 CLAUDE.md │          │ 根据最新      │
│ 的 Rubric     │ ──→     │ 里过时的规则  │  ──→     │ 项目状态和    │
│ 审视 AI 最近  │         │ Cache 全部清掉│          │ 目标重新建立  │
│ 的表现        │         │ (不要手软)    │          │ 当前上下文    │
└──────────────┘         └──────────────┘          └──────────────┘
```

### 类比

> Re-dream 就像定时帮 AI 助手**冲个冷水澡**——让它瞬间清醒，重新对齐当前目标。

### 实操要点

1. **用 Rubric 当尺**：不是凭感觉删，而是用评分标准衡量哪些规则还在服务当前目标
2. **不要手软**：宁可多删，不要保留模棱两可的旧规则
3. **重建而非修补**：不是在旧规则上打补丁，而是根据当前状态从零建立

---

## Anthropic 官方 Dreams 机制

### Dreams 是什么

Anthropic 的 **Dreams** 是 Claude Managed Agents 的 Research Preview 功能，让 Claude 回顾过往 session 来整理记忆。

```
输入                          处理                           输出
┌─────────────┐              ┌─────────────┐              ┌─────────────┐
│ 现有记忆库   │──┐           │             │              │ 全新记忆库   │
│ (memory     │  │    Dream  │  合并重复    │              │ (去重、更新、│
│  store)     │  ├──────────→│  替换过时条目 │─────────────→│  新洞察)     │
└─────────────┘  │   Job     │  提炼新洞察  │              └─────────────┘
┌─────────────┐  │           │             │
│ 1-100 个     │──┘           └─────────────┘
│ 过往 Session │
│ (transcript)│
└─────────────┘
```

### 核心特性

| 特性 | 说明 |
|------|------|
| 异步执行 | 创建后几分钟到几十分钟完成 |
| 输入不被修改 | 原始记忆库和 session 保持不变 |
| 可丢弃 | 对输出不满意可以直接删除 |
| 可自定义指令 | `instructions` 参数指定关注重点 |

### 代码示例

```python
# 创建 Dream
dream = client.beta.dreams.create(
    inputs=[
        {"type": "memory_store", "memory_store_id": store_id},
        {"type": "sessions", "session_ids": [session_a, session_b]},
    ],
    model="claude-opus-4-7",
    instructions="Focus on coding-style preferences; ignore one-off debugging notes.",
)

# 轮询等待完成
while dream.status in ("pending", "running"):
    time.sleep(10)
    dream = client.beta.dreams.retrieve(dream.id)

# 使用输出
output_store_id = next(
    output.memory_store_id
    for output in dream.outputs
    if output.type == "memory_store"
)
```

### Dreams vs 手动 Re-dream

| 维度 | Anthropic Dreams | 手动 Re-dream |
|------|-----------------|---------------|
| 自动化 | 全自动（API 调用） | 手动三步骤 |
| 洞察质量 | 基于 1-100 个 session 的模式识别 | 基于人类判断 |
| 适用场景 | Managed Agents API 用户 | 所有 Claude Code / Claude 用户 |
| 个性化 | 可通过 instructions 定制 | 完全由人控制 |

---

## CLAUDE.md 最佳实践

### 官方建议（Claude Code Docs）

| 原则 | 指导 |
|------|------|
| **大小** | 每个文件控制在 **200 行以内**，越长 = 消耗越多上下文 + 遵循度越低 |
| **结构** | 用 markdown 标题和列表提高可扫描性 |
| **具体性** | 写可验证的指令，而非模糊描述 |
| **一致性** | 删除跨文件的矛盾指令 |

### 好的 vs 坏的指令

```
❌ 模糊：  "Format code properly"
✅ 具体：  "Use 2-space indentation"

❌ 模糊：  "Test your changes"
✅ 具体：  "Run npm test before committing"

❌ 模糊：  "Keep files organized"
✅ 具体：  "API handlers live in src/api/handlers/"
```

### 何时添加规则

- Claude **犯了两次同样的错误**
- Code review 发现 Claude 本该知道的事
- 你**重复输入上次的修正**
- 新队友需要同样的上下文

### 文件层级（加载顺序：从宽到窄）

| 范围 | 路径 | 用途 |
|------|------|------|
| 组织策略 | `/Library/Application Support/ClaudeCode/CLAUDE.md` | 全组织（IT/DevOps 管理） |
| 用户级 | `~/.claude/CLAUDE.md` | 个人偏好 |
| 项目级 | `./CLAUDE.md` | 团队共享（提交到 git） |
| 本地级 | `./CLAUDE.local.md` | 个人项目偏好（gitignore） |

### 关键认知

> Claude 把 CLAUDE.md 当作**上下文**，不是**强制配置**。越具体、越简洁，遵循度越高。

---

## 自检清单

看完这篇笔记后，问自己：

- [ ] 我的 CLAUDE.md 上次更新是什么时候？
- [ ] 里面有多少规则已经因为项目推进而过时？
- [ ] 我有没有清晰的 Rubric 评分标准？
- [ ] 我是在追模型版本焦虑，还是在打磨评分标准？
- [ ] 我是否建立了定期维护循环？

---

## 参考资料

- [Anthropic Dreams 官方文档](https://platform.claude.com/docs/en/managed-agents/dreams)
- [Claude Code Memory 官方文档](https://code.claude.com/docs/en/memory)
- [@Mnilax 原始推文（73% 删除数据来源）](https://x.com/Mnilax/status/2054955621829443903)
- [Claude Dreaming 深度解析](https://levelup.gitconnected.com/claude-now-dreams-inside-anthropics-6x-memory-feature-3-hidden-risks-a038f17f7d13)
- [Context Folder 管理指南](https://aimaker.substack.com/p/ai-context-management-guide)
- [Claude Code 日常开发工作流配置](https://systemprompt.io/guides/claude-code-daily-workflows)

## 相关笔记

- [[../Hermes/Hermes Agent 隐藏功能 - Dashboard, Claude Code CLI, TUI Chat]]
- [[../Hermes/Hermes Agent Kanban 看板 - 多智能体协作实战]]
