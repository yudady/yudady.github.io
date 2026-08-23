---
title: Claude Code vs Codex 的 Goal 目标功能深度对比
aliases: [Goal 功能, AI 自主编程循环, vibe coding 自动化验收]
tags:
  - claude-code
  - codex
  - openai
  - ai-coding
  - agent
  - goal-mode
  - status/active
source:
  - "https://www.youtube.com/watch?v=iSm0WD5oXSQ"
author: "BNY（视频）; 实际产品: Anthropic (Claude Code) / OpenAI (Codex)"
created: 2026-05-17
updated: 2026-05-17
description: 深度拆解 Claude Code 和 Codex 的 Goal（目标）功能，通过实战 Pokemon 倖存者游戏对比两者的底层思维差异
level: intermediate
stars: 4
---

# Claude Code vs Codex 的 Goal 目标功能深度对比

> Vibe Coding 的下一步不是写更多代码，而是让 AI 自己验收——Goal 功能让 AI 自主完成「写代码 → 测试 → 检查效果 → 自我修正」的完整循环，无需人工介入。

---

## 目录

1. [痛点：人工验收瓶颈](#痛点人工验收瓶颈)
2. [Goal 功能是什么](#goal-功能是什么)
3. [核心机制：自主验收循环](#核心机制自主验收循环)
4. [Claude Code 的 Goal 实现](#claude-code-的-goal-实现)
5. [Codex 的 Goal 实现](#codex-的-goal-实现)
6. [实战对比：Pokemon 倖存者游戏](#实战对比pokemon-倖存者游戏)
7. [代码质量对比：底层思维差异](#代码质量对比底层思维差异)
8. [选择建议](#选择建议)
9. [Goal 指令编写技巧](#goal-指令编写技巧)

---

## 痛点：人工验收瓶颈

当前 Vibe Coding 的工作流：

```
传统 Vibe Coding 循环:

  你写 Prompt → AI 写代码 → 你运行看效果
       ▲                        │
       │                        ▼
    你给反馈 ← 你截图描述问题 ← 不满意？
       │
       ▼
  AI 修改代码 → 你再运行看效果 ...
       │
       └── 无限循环，你就是瓶颈
```

**核心问题**：每一步都需要你亲自运行、截图、描述问题、给出反馈。你就是整个循环里唯一的验收人，根本离不开。

---

## Goal 功能是什么

Goal（目标）功能让 AI **自己当验收人**：

```
开启 Goal 后的循环:

  你写 Goal Prompt → AI 写代码 → AI 自己运行
                          │           │
                          ▼           ▼
                    AI 自己检查效果 ← AI 自主验收
                          │
                    满足目标？──Yes──→ 交付
                          │
                         No
                          │
                          ▼
                    AI 自我修正 → 继续循环
```

**一句话**：你设定「完成是什么样子」，AI 自己执行、自己对照标准、自己修正，直到满足条件。

**现实案例**：
- 开发者 Jasper 给 Codex 设了一个 Goal，AI 后台连续跑了 30+ 小时，用 Brainfuck 语言写了 600 多万行代码，搞出一个完全可玩的毁灭战士游戏
- 有人为了不打断 AI 长达 45 小时的 Goal 运行循环，连每天重启电脑的习惯都戒了

---

## 核心机制：自主验收循环

Goal 的核心是一个**三阶段自动化循环**：

```
┌─────────────────────────────────────────┐
│            Goal 自主循环                  │
│                                         │
│  Phase 1: EXECUTE（执行）               │
│  ├── AI 根据目标编写/修改代码            │
│  └── 产出可运行的代码文件                │
│                                         │
│  Phase 2: VERIFY（验证）                │
│  ├── AI 运行程序                         │
│  ├── AI 打开浏览器查看效果               │
│  ├── AI 运行测试套件                     │
│  └── AI 对比目标标准                     │
│                                         │
│  Phase 3: CORRECT（修正）               │
│  ├── 不满足 → 回到 Phase 1              │
│  └── 满足 → 交付给用户                  │
└─────────────────────────────────────────┘
```

**关键变化**：
- ❌ 没有 Goal：每步等你判断
- ✅ 有 Goal：AI 自己判断是否达标

---

## Claude Code 的 Goal 实现

Claude Code 使用 `--goal` 参数启动 Goal 模式：

```bash
# 基本用法
claude --goal "你的目标描述"

# 示例
claude --goal "创建一个 Pokemon 倖存者游戏，使用 Canvas，包含升级系统、进化机制、死亡/获胜结算"
```

### Claude Code Goal 特点

- **透明 Token 报告**：完成后告知消耗了多少 tokens
- **详细执行日志**：每个步骤（写代码、测试、优化）都有输出
- **浏览器自动测试**：使用 Chrome 打开文件，自动游玩测试
- **问题自发现**：主动发现升级文字偏差、进化特效、死亡重置逻辑等问题并修复
- **闭环验证**：所有系统测试通过后才交付

---

## Codex 的 Goal 实现

Codex 使用 `/goal` 指令：

```bash
# 在 Codex 会话中
/goal "创建一个 Pokemon 倖存者游戏，使用 Canvas，包含升级系统、进化机制、死亡/获胜结算"
```

### Codex Goal 特点

- **简洁输出**：完成只说 "OK 整个耗时"，不报告 token 数
- **完整审计**：写代码 → 检查文件 → 测试 → 调整 → 验收
- **底层优化导向**：生成的代码更关注性能和算法效率

---

## 实战对比：Pokemon 倖存者游戏

同一套 Goal 指令，分别让 Claude Code 和 Codex 自动生成一个完整的 Pokemon 倖存者网页游戏。

### 速度对比

| 维度 | Claude Code | Codex |
|------|------------|-------|
| 耗时 | 5 分 18 秒 | 5 分 01 秒 |
| Token 报告 | ✅ 24,000 tokens | ❌ 未报告 |
| 速度差异 | - | 快 ~17 秒 |

### 执行过程对比

**Claude Code**：
1. 设置好 Git 仓库
2. 构建 HTML 文件 + 游戏代码
3. 用 Chrome 打开文件，自动游玩测试
4. 发现问题（升级文字偏差等）→ 自行优化
5. 测试进化特效、死亡重置逻辑等
6. 所有系统验证通过 → 交付

**Codex**：
1. 写出稿代码
2. 检查文件
3. 测试、调整、验收
4. 完成交付

### 产品体验差异

| 维度 | Claude Code | Codex |
|------|------------|-------|
| 初始怪物数量 | 合理（用户体验好） | 过多（一级就满屏怪） |
| 游戏平衡 | 更注重玩家体验 | 更注重功能完整性 |
| 死亡/获胜结算 | ✅ 完整 | ✅ 完整 |
| 皮卡丘进化 | ✅ 进化为雷丘 | ✅ 进化为雷丘 |

---

## 代码质量对比：底层思维差异

视频通过对比两者的源码，揭示了**产品思维 vs 极客思维**的根本差异。

### 差异一：怪物数量控制（防卡顿）

**Claude Code（产品思维）**：
```javascript
// 在主循环里写死限制
if (当前怪物数 > 200) {
    删除最新刷出的怪物;
}
```
- ✅ 简单直接
- ❌ 怪物凭空消失，玩家体验奇怪

**Codex（极客思维）**：
```javascript
// 全局配置字典
const CONFIG = {
    maxMonsters: 100,
    maxParticles: 50,
    maxExperience: 1000,
    // ...所有上限参数集中管理
};

// 在生成函数里源头拦截
function spawnMonster() {
    if (当前怪物数 >= CONFIG.maxMonsters) return;
    // 生成怪物...
}
```
- ✅ 全局配置，修改方便
- ✅ 源头拦截，不会凭空消失
- ✅ 成熟稳定的架构

### 差异二：距离计算（CPU 性能优化）

**Claude Code（标准前端）**：
```javascript
// 直接调用原生函数计算绝对距离
const distance = Math.sqrt(
    (x2 - x1) ** 2 + (y2 - y1) ** 2
);
```
- ✅ 数学正确
- ❌ 涉及开平方根，每秒几十次的高频循环中消耗 CPU

**Codex（底层优化）**：
```javascript
// 手写距离平方函数，避免开方运算
function distanceSquared(a, b) {
    return (a.x - b.x) ** 2 + (a.y - b.y) ** 2;
}

// 碰撞体积也做平方处理
const hitRadiusSquared = hitRadius * hitRadius;

// 比较：距离平方 < 碰撞半径平方
if (distanceSquared(monster, player) < hitRadiusSquared) {
    // 碰撞！
}
```
- ✅ 完全绕过开平方根运算
- ✅ 大量节约 CPU 算力开销
- ✅ 游戏开发经典优化技巧

### 思维差异总结

```
Claude Code                        Codex
┌──────────────────┐              ┌──────────────────┐
│  产品经理思维      │              │  资深极客思维      │
│                   │              │                   │
│  "用户会用这个"    │              │  "CPU 会跑这个"    │
│  "体验要流畅"      │              │  "算法要极致"      │
│  "代码要可读"      │              │  "性能要压榨"      │
│                   │              │                   │
│  适合：业务开发    │              │  适合：底层/性能   │
│  商业化产品        │              │  游戏/算法密集型   │
└──────────────────┘              └──────────────────┘
```

---

## 选择建议

| 场景 | 推荐 | 原因 |
|------|------|------|
| 商业 Web 应用 | Claude Code | 产品思维，用户体验优先 |
| SaaS 产品开发 | Claude Code | 业务逻辑处理更完善 |
| 游戏开发 | Codex | 底层性能优化更强 |
| 算法密集型 | Codex | 极致性能压榨 |
| 快速原型 | 两者均可 | 速度差异不大 |
| 需要透明成本 | Claude Code | Token 消耗可见 |

---

## Goal 指令编写技巧

好的 Goal 指令 = 清晰的完成标准。编写原则：

```
❌ 差的 Goal:
"帮我写个游戏"

✅ 好的 Goal:
"创建一个 Pokemon 倖存者网页游戏：
1. 使用 HTML Canvas 渲染
2. 玩家控制皮卡丘，WASD 移动
3. 怪物自动追踪玩家
4. 击杀怪物获得经验值，升级提升属性
5. 10 级时皮卡丘进化为雷丘
6. 生存 5 分钟 → 获胜结算
7. 死亡 → 显示等级、击杀数、生存时间
8. 使用浏览器打开测试，确保所有功能正常"
```

**关键要素**：
- ✅ 明确的技术栈
- ✅ 可量化的完成条件
- ✅ 具体的功能清单
- ✅ 验收标准

---

## 总结

Goal 功能的本质是把「人工验收」这个最后的瓶颈也自动化了。Claude Code 和 Codex 在实现上各有侧重：

- **Claude Code**：产品经理视角，注重用户体验和代码可读性，适合商业应用开发
- **Codex**：资深工程师视角，注重底层优化和极致性能，适合游戏/算法开发

两者的 Goal 模式都值得在实际项目中使用——设定好目标，让 AI 自主跑完整个开发循环，把精力留给更核心的产品决策。

---

## 参考资料

- [YouTube: BNY Goal 功能深度对比](https://www.youtube.com/watch?v=iSm0WD5oXSQ)
- [Claude Code 官方文档](https://docs.anthropic.com/en/docs/claude-code)
- [OpenAI Codex CLI](https://github.com/openai/codex)

## 相关笔记

- [[Claude Code]]
- [[../AI/Google Antigravity - Agent-First IDE 开发平台]]
- [[AI 编码 Agent 生态]]
- [[PrintingPress - AI Agent 原生 CLI 生成器]]
