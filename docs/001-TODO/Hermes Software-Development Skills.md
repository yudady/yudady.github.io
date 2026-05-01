---
title: Hermes Software-Development Skills 详解
tags:
  - status/active
  - area/distill
  - type/doc
created: 2026-04-06
updated: 2026-04-08
---

# Hermes Software-Development Skills 详解

> [!info] 来源
> Hermes Agent 的 software-development 类别共 6 个 skill，构成完整的软件工程方法论——从规划、计划、实现、审查到调试。
> created: 2026-04-06
> tags: [hermes, ai-agent, software-engineering, workflow, tdd, code-review]

---

## 协作关系总览

```
用户需求
   ↓
[plan] → 轻量规划 / 聊天时快速出方案
   ↓
[writing-plans] → 产出详细的分步实现计划
   ↓
[subagent-driven-development] → 逐任务派子代理执行
   ↓ (每个任务内部)
[test-driven-development] → 实现者遵循 TDD
   ↓ (任务完成后)
[requesting-code-review] → 自动化验证流水线
   ↓ (遇到 bug)
[systematic-debugging] → 系统化排查
```

---

## 1. plan (规划模式)

**用途：** 只做规划，不执行。

**核心理念：** 你说我来写计划，写到 .hermes/plans/ 目录下，不动项目文件、不跑命令、不 commit。

**触发方式：** 用户说 "/plan" 或要求先做计划时。

**输出格式：** 一个 markdown 文件，路径 .hermes/plans/YYYY-MM-DD_HHMMSS-\<slug\>.md，包含：
- 目标 (Goal)
- 当前上下文/假设
- 方案
- 分步计划
- 会改动的文件列表
- 测试/验证步骤
- 风险和未决问题

**特点：** 轻量级的"想一想再动手"模式。如果需求不够清晰，会先问再写。

**与其他 skill 的关系：** plan 产出 → writing-plans 可进一步细化 → subagent-driven-development 执行。

---

## 2. writing-plans (写实现计划)

**用途：** 把需求/spec 转化成可直接执行的分步实现计划。

**核心理念：** 假设实现者对代码库零了解。计划必须详细到"不用猜"的程度——精确文件路径、完整代码示例、精确命令和预期输出。

**任务粒度：** 每个任务 2-5 分钟可完成。

**计划文档结构：**

```markdown
# [功能名] Implementation Plan
> For Hermes: 使用 subagent-driven-development 逐任务执行

Goal: 一句话
Architecture: 2-3 句话
Tech Stack: 关键技术

### Task N: [任务名]
Objective: 一句话
Files: Create/Modify/Test 精确路径
Step 1: 写失败测试 (完整代码)
Step 2: 运行测试验证失败 (精确命令)
Step 3: 写最小实现 (完整代码)
Step 4: 运行测试验证通过
Step 5: git commit
```

**三大原则：**
- **DRY**：不重复
- **YAGNI**：只实现当前需要的，不加"未来可能用到的"
- **TDD**：每个代码任务都包含红-绿-重构循环

**与其他 skill 的关系：** 产出计划 → subagent-driven-development 消费执行 → requesting-code-review 质量把关。

---

## 3. subagent-driven-development (子代理驱动开发)

**用途：** 执行实现计划——每个任务派一个全新的子代理去干，完成后经过两轮审查。

**核心理念：** 新鲜上下文 + 两轮审查 = 高质量、快速迭代。实现者不能审查自己的代码。

### 完整流程

1. 读取计划，提取所有任务，创建 todo list
2. 对每个任务：
   - 派"实现者子代理" (delegate_task) → 给完整上下文和 TDD 指令
   - 派"规格合规审查员" → 检查是否完整实现了计划要求（不接受"差不多"）
   - 派"代码质量审查员" → 检查风格、错误处理、测试覆盖、安全性
   - 任一轮审查不通过 → 修复 → 重新审查
   - 两轮都通过 → 标记完成
3. 所有任务完成后 → 派"集成审查员"检查整体一致性
4. 运行全量测试 + 最终 commit

### 关键规则

- 每个任务一个全新子代理（防止上下文污染）
- 规格审查先于质量审查（顺序不能反）
- 不要让子代理自己读计划文件（要把完整任务文本直接塞进 context）
- 碰到相同文件的多个任务不能并行
- 修复也不能让实现者自己修（要派新的修复子代理）

---

## 4. requesting-code-review (提交前代码审查)

**用途：** 代码写完、commit/push 之前的自动化验证流水线。

**核心理念：** 自己的代码不能自己审。新上下文能发现你忽略的问题。

### 6 步流水线

**Step 1 - 获取 diff：** git diff --cached，太大就按文件拆

**Step 2 - 静态安全扫描：** 只扫描新增行，检查硬编码密钥、shell 注入、eval、pickle 反序列化、SQL 注入等

**Step 3 - 基线测试 + lint：** 先 stash 跑一次拿 baseline，再对比改动有没有引入新失败（自动检测语言和框架）

**Step 4 - 自查清单：** 快速 8 项检查（无密钥、有输入验证、参数化查询、无 debug 残留等）

**Step 5 - 独立审查子代理：** 只给它 diff 和扫描结果，返回 JSON 格式判定（fail-closed：解析失败 = 不通过）

**Step 6 - 评估结果：** 全过 → commit；有问题 → 进入 Step 7

**Step 7 - 自动修复循环（最多 2 轮）：** 派第三个子代理去修，修完重新走 1-6

**Step 8 - commit：** 带 [verified] 前缀

> [!tip] 与 github-code-review 的区别
> requesting-code-review 是审**自己**的改动（提交前）；
> github-code-review 是审**别人的** PR。

---

## 5. test-driven-development (测试驱动开发)

**用途：** 任何功能或 bugfix 实现前，强制先写测试。

**铁律：** 没有失败测试，就不写生产代码。先写了代码再补测试？删掉代码，重来。

### RED-GREEN-REFACTOR 循环

**RED：**
- 写一个最小测试描述期望行为
- 必须运行测试，亲眼看到它失败
- 测试直接通过了？说明测的不是新功能，修测试
- 测试报错（语法错）？修错，重新跑到正确失败

**GREEN：**
- 写最简单能通过的代码
- 允许硬编码、复制粘贴（脏代码没关系）
- 不加额外功能、不重构

**REFACTOR：**
- 消除重复、改善命名、提取辅助函数
- 保持测试全绿
- 测试红了？立刻撤销，缩小重构步幅

### 核心论点（为什么测试必须在先）

| 常见借口 | 实际情况 |
|---------|---------|
| "先写代码后补测试" | 测试直接通过，证明不了什么 |
| "手动测过了" | 临时的、没记录，改了代码又要重来 |
| "删了可惜" | 沉没成本谬误，留着不可信的代码才是浪费 |
| "测试先写太慢" | TDD 比调试快。所谓"务实捷径" = 生产环境调试 |

---

## 6. systematic-debugging (系统化调试)

**用途：** 遇到任何 bug、测试失败、异常行为时使用。

**铁律：** 不找到根因，不准修。没有完成阶段 1 调查，不能提出修复方案。

### 四个阶段（必须按顺序完成）

**Phase 1 - 根因调查：**
- 仔细读错误信息（不跳过）
- 稳定复现
- 检查最近的改动（git log / diff）
- 多组件系统：在每个边界加诊断日志，确定断点在哪一层
- 追踪数据流：坏值从哪来？一路往上游追到源头

**Phase 2 - 模式分析：**
- 找代码库中类似的正常工作代码
- 对比参考实现（要完整读完，不能跳读）
- 列出工作/不工作的所有差异
- 梳理依赖关系

**Phase 3 - 假设与测试：**
- 形成单一假设："我认为 X 是根因，因为 Y"
- 最小变更测试，一次只改一个变量
- 确认或形成新假设

**Phase 4 - 实现修复：**
- 先写失败测试（复现 bug 的回归测试）
- 修根因（不是修症状）
- 如果修了 3 次都不行 → 停下来质疑架构（可能是设计问题，不是代码问题）

### 红旗信号

出现这些想法时，说明你偏离了流程：
- "快速修一下，之后再调查"
- "试一下改 X 看看行不行"
- "我已经手动测过了"
- "再加一次修复试试"（已经试了 2+ 次）
