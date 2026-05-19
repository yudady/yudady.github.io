---
title: Zero (ZeroLang) - Vercel Labs 推出的 AI Agent 专属系统编程语言
aliases:
  - ZeroLang
  - Zero 编程语言
  - AI 专属编程语言
tags:
  - programming-language
  - ai-agent
  - vercel
  - systems-programming
  - status/active
  - type/doc
source:
  - "https://youtu.be/WeaB3DmoHZI"
  - "https://github.com/vercel-labs/zero"
  - "https://needhelp.icu/blogs/zerolang-vercel-ai-agent-language"
author: Vercel Labs (Chris Tate 主导)
created: 2026-05-19
updated: 2026-05-19
description: |
  Vercel Labs 推出的实验性系统编程语言 Zero，文件扩展名 .0，从编译器输出到工具链全部为 AI Agent 的消费而设计——结构化 JSON 诊断、类型化修复 ID、统一 CLI、能力基 I/O，代表编程语言从"人读"到"人+机共读"的范式转变。
level: intermediate
stars: 4
---

# Zero (ZeroLang) — Vercel Labs 推出的 AI Agent 专属系统编程语言

> Vercel Labs 推出的实验性系统编程语言，编译出不到 10KB 的原生二进制，但最大卖点是编译器输出和工具链从第一天起就为 AI Agent 的消费而设计。适合关注 AI Agent 编程基础设施和编程语言设计的开发者。

## 目录

- [[#为什么需要 Agent 专属语言]]
- [[#核心设计哲学：Everything is Explicit]]
- [[#Agent-First 工具链]]
- [[#语言特性速览]]
- [[#范式转变：从人读到人+机共读]]

---

## 为什么需要 Agent 专属语言

### AI 编程的三个痛点

传统编译器和编程语言是为人类设计的，AI Agent 使用时会遇到三个系统性问题：

| 痛点 | 问题本质 | AI 的困境 |
|------|----------|-----------|
| 非结构化错误 | 编译器输出人类可读的自由文本 | AI 必须解析非结构化文本，格式随版本变化就崩溃 |
| 工具链割裂 | 检查、构建、分析依赖、路由各用不同工具 | AI 在多工具间切换，工作流脆弱低效 |
| 隐藏副作用 | 函数签名不暴露 I/O、全局状态 | AI 难以推理隐藏的全局状态，追踪成本极高 |

### 解决思路

```
传统模式：
  人类写代码 → 编译器报错(人读文本) → 人类修 → 循环

AI Agent 模式（传统语言）：
  AI 写代码 → 编译器报错(人读文本) → AI 解析文本(脆弱!) → 猜测修复 → 可能死循环

AI Agent 模式（Zero）：
  AI 写代码 → 编译器输出 JSON → AI 拿到结构化诊断+修复计划 → 精准修复
```

> 核心洞察：不是让机器理解人类的模糊，而是用机器的精确赋能人类的创造。

---

## 核心设计哲学：Everything is Explicit

Zero 的三个"拒绝"：

### 1. 拒绝隐藏分配器 (No Hidden Allocators)

需要内存分配的函数必须在签名中声明 `Alloc` capability，显式传入：

```zero
pub fun main(world: World) -> Void raises {
    let bytes: Span = std.mem.span("zero")
    let view = BufferView { bytes: bytes }
    if std.mem.len(view.bytes) == 4 && view.bytes[0] == 122 {
        check world.out.write("span ok\n")
    }
}
```

### 2. 拒绝隐式异步 (No Implicit Async)

完全拒绝 `async/await`。同步执行，执行流就是字面所写——没有复杂的异步状态机。

### 3. 拒绝魔法全局变量 (No Magic Globals)

所有 I/O 通过显式的 `World` capability 对象传入 `main`：

```zero
pub fun main(world: World) -> Void raises {
    check world.out.write("hello from zero\n")
}
```

### 传统语言 vs Zero 对比

| 维度 | C / Rust / Python | Zero |
|------|-------------------|------|
| 输出 | 隐式全局 (`printf` / `println!`) | 显式 capability (`world.out.write`) |
| 错误处理 | 异常 / 返回值 / 隐式传播 | 显式 `check` + `raises` |
| 内存分配 | 隐式 GC 或默认分配器 | 显式 `Alloc` capability |
| 异步 | 隐式 `async/await` | 无隐式异步 |
| 全局状态 | 允许魔法全局变量 | **拒绝** — 签名必须声明 |

---

## Agent-First 工具链

> "Humans read the message. Agents read the JSON." — 同一个 CLI 同时服务人类和 Agent。

### 统一 CLI

所有操作集中在一个 `zero` 命令下：

```bash
zero check --json examples/hello.0    # 结构化诊断
zero run examples/add.0               # 运行
zero build --emit exe --target linux-musl-x64 examples/add.0 --out add
zero graph --json examples/systems-package   # 依赖图
zero size --json examples/point.0     # 二进制大小报告
zero routes --json examples/web/hello # 路由分析
zero skills get zero --full           # 技能查询
zero doctor --json                    # 环境诊断
```

### Agent 修复循环专用命令

```
┌─────────────────────────────────────────────────────┐
│           AI Agent 修复循环 (Zero)                   │
│                                                     │
│  1. zero check --json                               │
│     → 结构化 JSON 诊断                               │
│     → 包含错误代码 (NAM003) + 行号 + 修复 ID         │
│                                                     │
│  2. zero explain <error-code>                        │
│     → 获取错误的结构化解释                            │
│     → 不需要爬网页/找文档                            │
│                                                     │
│  3. zero fix-plan --json                             │
│     → 机器可读的修复计划                              │
│     → 明确告诉 AI：第几行、改成什么                   │
│                                                     │
│  4. AI 执行修复 → 回到步骤 1                         │
└─────────────────────────────────────────────────────┘
```

### 结构化 JSON 诊断示例

```json
{
  "ok": false,
  "diagnostics": [{
    "code": "NAM003",
    "message": "unknown identifier",
    "line": 3,
    "repair": { ... }
  }]
}
```

关键设计：**稳定的错误代码**（如 NAM003），不随版本变化格式，AI 的解析逻辑不会崩溃。

---

## 语言特性速览

### 基本语法

```zero
fun function_name(params: Type) -> ReturnType { ... }
```

**类型系统**：`i8`/`i16`/`i32`/`i64`、`u8`/`u16`/`u32`/`u64`、`usize`/`isize`、`f32`/`f64`、`bool`、`char`、`Void`

无隐式转换，必须显式 `as`：
```zero
let count: u32 = 0x12c_u32
let byte: u8 = count as u8
```

### Effects System（能力基 I/O）

签名即能力合约。`pub fun main(world: World) -> Void raises` 声明了：
1. 接收 `World` capability → 可与外部世界交互
2. 返回 `Void` → 无有意义的返回值
3. 标记 `raises` → 可能失败

`check` 关键字处理可失败操作；使用 `check` 的函数必须声明 `raises` → 启用**静态错误路径追踪**。

### 数据建模

```zero
// shape — 命名记录（可设默认值）
shape Point { x: i32, y: i32, }
shape Counter { value: i32 = 0, }

// enum — 固定名称集
enum Status { ready, failed, }

// choice — 带负载的标签联合（ADT）
choice Result { ok: i32, err: String, }
```

匹配必须**穷尽**：
```zero
match result {
    .ok => value {
        check world.out.write("choice ok\n")
    }
    .err => message {
        check world.out.write("choice err\n")
    }
}
```

### 内存管理

无 GC、无 Rust 式借用检查器。Zig 风格的显式管理 + Agent 友好的简化：

| 类型 | 用途 |
|------|------|
| `Span` | 只读视图，零成本 |
| `MutSpan` | 显式可写视图 |
| `[N]T` | 固定长度数组 |
| `Maybe` | 可能为空的值 |
| `ref` / `mutref` | 显式引用可变性 |
| `owned` | 拥有值，作用域结束时自动清理 |
| `Alloc` | 分配器 capability |

```zero
pub fun main(world: World) -> Void raises {
    defer cleanup()          // 确定性清理
    check world.out.write("work\n")
}
```

> 用户**不能直接调用 `value.drop()`** — 确保清理确定性。

### C 互操作 & Web 支持

```zero
// C 互操作
extern c "config.h" as config
extern shape CConfig {
    enabled: bool,
    limit: i32,
}

// Web 路由
pub fun GET(req: Request) -> Response {
    return Response.text("hello from zero web\n")
}
```

---

## 范式转变：从人读到人+机共读

### 认知框架转变

```
传统思维：
  编程语言的受众 = 人类
  设计目标 = 人类可读性

新范式：
  编程语言的受众 = 人类 + AI Agent
  设计目标 = 人类可读性 + 机器可读性 + 机器可操作性
```

### 双轨制开发模式

```
┌─────────────────────────────────────────────┐
│              双轨制开发                       │
│                                             │
│  人类轨道：                                  │
│    高层逻辑设计、架构规划、业务决策           │
│                                             │
│  AI Agent 轨道：                             │
│    底层代码实现、错误诊断、自动修复          │
│                                             │
│  桥梁：                                      │
│    同时具备人类可读语法 + 机器可读工具链      │
│    的新一代编程语言（如 Zero）                │
└─────────────────────────────────────────────┘
```

### 最佳实践

- ✅ 用 Agent-First 的工具链（JSON 诊断、结构化修复计划）提升 AI 编程效率
- ✅ 函数签名暴露所有副作用 — 让 AI 能准确推理代码行为
- ✅ 稳定的错误代码格式 — AI 的解析逻辑不随版本崩溃
- ❌ 不要让 AI 解析非结构化的编译器输出 — 这是当前 AI 编程死循环的根源
- ❌ 不要追求"像自然语言一样编程" — 自然语言的模糊性对机器是灾难

### 项目现状

| 项目 | 信息 |
|------|------|
| 仓库 | github.com/vercel-labs/zero |
| 许可证 | Apache-2.0 |
| 最新版本 | v0.1.2 (2026-05-16) |
| Stars | ~981 |
| 语言构成 | C 65.5% / JavaScript 28% / Shell 5.8% |
| 编辑器 | VSCode 扩展（.0 文件语法高亮） |

> **实验性警告**：语言尚未稳定，编译器、标准库、文档仍在变化中。

### 对未来的预测

视频观点（有道理但需理性看待）：

| 预测 | 可能性 | 理由 |
|------|--------|------|
| Python/Rust 引入类似 JSON 诊断模式 | 高 | 这是工具链层面的改进，不涉及语法变更 |
| 整个 IDE 生态转向结构化诊断 | 中 | LSP 已经在做类似的事，但 Zero 走得更远 |
| Zero 成为系统编程主流 | 低 | 生态太新，需与 Rust/Zig/C 竞争 |
| "Agent 专属语言"成为新品类 | 中 | 概念会被借鉴，但不一定是 Zero 胜出 |

---

## 参考资料

- [GitHub - vercel-labs/zero: The programming language for agents](https://github.com/vercel-labs/zero)
- [Zero 官网](https://zerolang.ai)
- [When Vercel Built a Systems Programming Language for AI Agents](https://needhelp.icu/blogs/zerolang-vercel-ai-agent-language)
- [Vercel Labs Introduces Zero - MarkTechPost](https://www.marktechpost.com/2026/05/17/vercel-labs-introduces-zero-a-systems-programming-language-designed-so-ai-agents-can-read-repair-and-ship-native-programs/)
- [Zero: Vercel Labs' New Experimental Systems Language - Reddit r/WebAfterAI](https://www.reddit.com/r/WebAfterAI/comments/1tex8t8/zero_vercel_labs_new_experimental_systems/)

## 相关笔记

- [[AI Agent]]
- [[编程语言设计]]
- [[Vercel]]
- [[系统编程语言对比]]
