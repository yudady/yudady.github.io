---
title: Ornith-1.5 35B MoE Mac 本地实测 — 99% 的架构能力，输在 1% 的字符
aliases: [Ornith 1.5 35B 评测, Ornith-1.5-35B-A3B]
tags:
  - local-llm
  - mlx
  - model-eval
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=8qRl2R75Odg"
  - "https://github.com/Barty-Bart/ornith-1.5-35b-tests"
author: Bart Slodyczka（频道）；模型作者 Ornith / DeepReinforce
created: 2026-08-21
updated: 2026-08-21
description: Bart Slodyczka 在 M3 Ultra Mac Studio 上用 MLX 4-bit 实测 Ornith-1.5 35B-A3B：视觉发票解析约 98%、推论最高 120 TPS、代码架构能力 99% 但单次产出败于微小字符错误。
level: intermediate
stars: 4
---

# Ornith-1.5 35B MoE Mac 本地实测

> Bart Slodyczka 在 M3 Ultra Mac Studio（MLX、4-bit OQ4E 量化）上对 Ornith-1.5 35B-A3B 做的一组单次提示（One-shot）实测：发票视觉解析、塔防游戏、试算表、Three.js FPS。结论：结构化提取和 2D 交互逻辑已达实用水平，长代码单次生成败于微小字符错误，极度依赖迭代修复。适合关注本地 LLM（Local LLM）实用性的开发者。

## 目录

- [[#模型背景：Ornith-1.5 家族]]
- [[#硬体配置与推论速度]]
- [[#实测方法论：四变体矩阵]]
- [[#视觉文档解析（发票提取）]]
- [[#塔防游戏：创意预算被规划吃掉]]
- [[#试算表：思考模式的最佳场景]]
- [[#Three.js FPS：全部阵亡的字符级错误]]
- [[#关键洞察]]
- [[#实践建议]]
- [[#参考资料]]

---

## 模型背景：Ornith-1.5 家族

Ornith-1.5 于 2026-08-19 发布，来自 Ornith / DeepReinforce（开源，MIT 系），主打「自我改进（Self-Improvement）」训练策略——模型自己写训练课程。本次实测对象是 35B MoE 版本。

> [!info] 归属验证
> 视频标题只说 "Ornith's New 35b MoE"。经查证：发布方为 **Ornith / DeepReinforce**（HuggingFace: `ornith-ai/Ornith-1.5-35B-A3B`），不是 Google/OpenAI 等大厂产品。35B 版本激活参数约 3B（A3B），bf16 约 70 GB。

| 版本 | 架构 | 定位 |
|------|------|------|
| 9B | Dense（稠密） | 可跑在手机上 |
| **35B-A3B** | MoE，激活约 3B | **本次实测**，4-bit 仅 21.6 GB，32 GB Mac 可跑 |
| 397B | MoE | 旗舰，据报道在 Terminal-Bench 2.1、SWE-bench 上超越 DeepSeek V4 Flash 0731 与 Claude Opus 4.8（社区说法） |

发布信息拆解：

```
Ornith-1.5（2026-08-19）
├── 9B Dense      → 手机端
├── 35B MoE A3B   → 消费级桌面（本视频主角）
└── 397B MoE      → 旗舰 / benchmark 竞争
```

---

## 硬体配置与推论速度

测试机为 M3 Ultra Mac Studio（512 GB），运行 MLX 引擎，4-bit OQ4E 量化（为 MLX 优化的量化方案）。作者自嘲 Ultra 跑 35B 是杀鸡用牛刀，正在筹措 24/32 GB 设备做更贴近现实的测试。

### 记忆体占用构成

```
21.6 GB   模型权重（4-bit OQ4E）
+21.5 GB  KV Cache（262k 完整上下文）
─────────────────────────────
≈43 GB    满上下文总需求
```

- ✅ 32 GB Mac：可跑 4-bit 权重 + 中等上下文
- ❌ 262k 满上下文：需要 43 GB+，32 GB 机器撑不满

### 解码速度（Decode Speed）

| 配置 | 上下文 | 速度 |
|------|--------|------|
| 4-bit + MTP | 0 | **120 TPS** |
| 8-bit + MTP | 0 | 107 TPS |
| 4-bit 无 MTP | 0 | 83 TPS |
| 4-bit + MTP | 60k–120k | 76.1 TPS |
| DeepSeek V4 Flash（作者日常机，参照） | — | 40 TPS（MTP 开） |
| Qwen 3.8 27B（前作视频，参照） | 70k | ~25 TPS（无 MTP） |

MTP = 多 Token 预测（Multi-Token Prediction），一次前向预测多个 token 以加速解码。

要点：4-bit + MTP 的 120 TPS 在本地模型里属于极快；上下文拉到一半窗口仍有 76 TPS，衰减平缓。

---

## 实测方法论：四变体矩阵

四个代码任务各跑 4 个变体，全部 `--no-session`（每次从零上下文开始，唯一变量是标签），共 12 个 build，**7 个可用**。基础提示（Basic Prompt）只描述需求；升级提示（Upgraded Prompt）额外强制三步：先写计划 → 按计划实现 → 跑测试用例自检。

| 变体 | 量化 | Thinking | 提示 |
|------|------|----------|------|
| `4bit-nothink.html` | 4-bit | 关 | basic |
| `4bit-thinking.html` | 4-bit | 开 | basic |
| `4bit-thinking-upgraded.html` | 4-bit | 开 | upgraded |
| `8bit-thinking.html` | 8-bit | 开 | basic |

升级提示的增量部分（示意）：

```text
1. First, create a plan.
2. Build the app exactly as designed.
3. Run test cases and verify everything works.
```

> [!warning] 视频口误 vs 仓库终局
> 视频里作者口头认为塔防 4-bit+thinking「应该整个能跑，只是背景里有个小问题」；GitHub 仓库的最终判定是它 **fail**（见下文）。以仓库 README 为准：7/12 可用。

---

## 视觉文档解析（发票提取）

三张 Claude 生成的发票（PDF 转 PNG），按信息密度递增，逐行结构化提取。注意是**信息密集而非像素密集**——像素密度过高（手机相机高分辨率拍摄）本身就可能压垮模型，这里特意排除该变量。

| 发票 | 结果 | 错误细节 |
|------|------|----------|
| 基础（1 行项目） | 15/16 | 漏掉唯一行项目的描述字段——作者多次跑此测试，基础发票首次失手 |
| 中等 | **32/32** | 全对 |
| 高密度 | 82/84 | SWIFT 代码一个字母认错（PPTP→PPTL）；数量 2232→2332（形近数字） |
| **合计** | **129/132 ≈ 98%** | |

错误模式高度一致：**形近字符**（相似字母、7/5 类数字）。

生产环境最佳实践：

- ✅ 模型输出 + 确定性校验（正则、校验位、规则引擎）
- ✅ 关键字段双通道提取（LLM + 传统解析器），不一致时人工/仲裁
- ✅ 控制输入像素密度，别让摄像头分辨率先压垮模型
- ❌ 98% 准确率直接无监督上线——2% 的错误恰好集中在 SWIFT 码、金额这类最痛的字段

---

## 塔防游戏：创意预算被规划吃掉

三个 4-bit 变体的对比揭示了最有趣的现象：

| 变体 | 代码行数 | 视觉效果 | 实际判定 |
|------|----------|----------|----------|
| no-think | **881** | 最好，完整可玩 | ✅ works |
| thinking | 710 | 更精致一点 | ❌ **fails**：`between: true` 在初始化时反转了 `startWave()` 的守卫条件，开始按钮永久失效，且控制台无任何报错 |
| thinking + upgraded | **509** | 明显阳春，但逻辑对 | ✅ works |

「创意预算（Creativity Budget）」假说——作者的现场推测：

```
总生成预算（固定）
├── 生成精致 UI/CSS 的预算   ← no-think：几乎全额
└── 生成计划 + 测试用例的预算 ← upgraded：先扣掉一大块
                              └→ 剩余预算只够"保证能跑"
                                 → 模型主动剥离视觉复杂度
```

模型意识到「代码必须通过自检」后，优化方向从「好看」滑向「能跑」——把复杂度砍到最低以保证测试通过。约 200 行的差距（881→509）大致就是被吃掉的 CSS/UI 预算。

---

## 试算表：思考模式的最佳场景

要求：可点击单元格、输入数字、公式实时计算（如 `=A1+B1`）、高亮反馈。

| 变体 | 结果 |
|------|------|
| 4-bit no-think | 视频演示中明显残缺（部分单元格无法输入），但 README 判定为 works——有可用逻辑 |
| 4-bit thinking | ✅ 公式、交互、高亮全部可用 |
| 4-bit upgraded + thinking | ✅ 且输入时整格高亮，体验最好 |
| 8-bit thinking | ✅ |

README 补充硬数据：四个变体全部通过 8 项公式测试（运算符优先级、括号、左结合减法、单元格引用、`SUM`、`AVERAGE`），8/8。

构建时间（README）：

| 任务 | no-think | thinking | upgraded | 8-bit |
|------|----------|----------|----------|-------|
| 塔防 | 1m36s | 2m27s | 16m43s | 1m46s |
| 试算表 | 16m31s | 18m50s | 21m23s | **7m29s** |
| FPS | 2m36s | 1m46s | 25m29s | 2m03s |

> [!tip] 反直觉数据
> 8-bit 试算表 7m29s，不到 4-bit（16m31s）的一半。「4-bit 一定更快」值得重新验证。

对比参照：前作视频中 Qwen 3.8 27B 与 DeepSeek 在同场景（no-think + 基础提示）连单元格点击、数字输入都做不到——Ornith no-think 残缺版已胜出。

任务难度判断决策树：

```
开 Thinking 吗？
├── 数学/公式/逻辑运算（试算表类）→ ✅ 必开，收益明确
├── 视觉丰富的 UI/游戏 → ⚠️ 权衡：可能吃掉视觉预算
└── 长代码（3D/大项目）→ 单次生成本来就会挂，开不开都救不了
```

---

## Three.js FPS：全部阵亡的字符级错误

四个变体**全部无法启动**，且没有一个是架构问题——12 个 build 全部包含结构合理、设计可信的完整实现，败因全是「转录级（Transcription-level）」小错：

| 变体 | 死因 |
|------|------|
| 4-bit no-think | `function loop()` 声明了两次 |
| 4-bit thinking | `new THREE.CapsuleGeometry` 少写一个点 `.` |
| 4-bit upgraded | JS module 内出现 HTML 注释 `<!-- -->` |
| 8-bit thinking | `obstacles` 在声明前 9 行就被使用 |

修复流程实证：

```
4bit-thinking.html  →  Claude 审查定位：缺一个 "."
                     →  补上这一个字符，其余零改动
                     →  4bit-thinking-PATCHED.html
                     →  ✅ 完整可玩（含音效、键鼠操作、视角转动）
```

最 instructive 的是 8-bit 的失败：语法完美解析、HUD 完整渲染、canvas 尺寸正确、控制台干净——**唯独 3D 场景没出现**。这种错误不执行页面根本发现不了。

对「自检提示」的致命打脸：升级提示要求「证明它能跑」，但模型没有浏览器，于是它把「证明」写成了一份**断言程序能跑的 QA 文档**——而这份 QA 文档自身还带着语法错误。

> **A self-check is worth exactly what the checker can observe.**
> （自检的价值，等于检查者实际能观测到的东西。）

---

## 关键洞察

1. **失败全部是转录级错误，不是设计失败**。4-bit MoE 有 99% 的代码架构能力，单次产出的最大痛点是漏个点、用错括号、变量先用后声明。
2. **8-bit 救不了 FPS**。量化精度不是瓶颈——8-bit 的失败模式（解析全过、场景缺失）反而最难发现。
3. **Thinking 不是万能开关**。塔防开 thinking 反而挂了（且无报错静默失败）；试算表开 thinking 则从残缺变可用。
4. **「证明它能跑」在无执行环境时是伪命题**。模型会退化为「写一份声称能跑的文档」。

生产化决策树：

```
你的任务是什么？
├── 结构化提取（发票/文档）→ Ornith 35B + 确定性校验层
├── 2D 交互应用（试算表/工具页）→ Thinking 开 + 基础提示
├── 3D / 长代码项目
│    ├─ 单次生成？ → ❌ 必挂（字符级错误率 100%）
│    └─ Agent 分段生成 + 编译/运行反馈闭环 → ✅ 正确姿势
└── 视觉精致的 UI → 提示里少加约束，把预算留给 CSS
```

✅/❌ 清单：

- ✅ 把 Ornith 35B 4-bit 当「极快的 99% 完成度生成器」用
- ✅ 串接 linter / 编译器 / 运行时报错作为反馈回路
- ❌ 指望升级提示（自检话术）替代真实测试环境
- ❌ 因为 4-bit 出小错就指望 8-bit 解决——错误在转录层，不在精度层

---

## 实践建议

1. **生产环境必须搭配验证机制**：视觉解析与代码生成均高准确率，但不宜无监督上线。串接正则校验、语法 linter、或第二个专用解析器做 fallback。
2. **拒绝 One-shot 长代码**：3D/复杂前端项目采用 Agent 架构——分段生成、每段编译验证、报错回填修复。单字符错误的修复成本趋近于零，但必须先「看见」它。
3. **按场景选择 Thinking**：数学/公式逻辑（试算表类）必开；纯视觉 UI 场景少约束、关 thinking 反而更好。
4. **32 GB Mac 用户**：4-bit（21.6 GB）是甜点位，满 262k 上下文才需要 43 GB。

---

## 参考资料

- [I Tested Ornith's New 35b MoE on Mac — Here's How It Goes（Bart Slodyczka）](https://www.youtube.com/watch?v=8qRl2R75Odg)
- [Barty-Bart/ornith-1.5-35b-tests — 全部提示词与原始输出（含失败 build）](https://github.com/Barty-Bart/ornith-1.5-35b-tests)
- [ornith-ai/Ornith-1.5-35B-A3B — HuggingFace 模型页](https://huggingface.co/ornith-ai/Ornith-1.5-35B-A3B)
- [Ornith-1.5: From Self-Scaffolding to Self-Improvement — HN 讨论](https://news.ycombinator.com/item?id=49362401)
- [r/LocalLLaMA: 3x new Ornith 1.5 released](https://www.reddit.com/r/LocalLLaMA/comments/1vsn2xw/we_have_q38_35b_at_home_3x_new_ornith_15_released/)

## 相关笔记

- [[Qwen 3.8 27B]]
- [[DeepSeek V4 Flash]]
- [[MLX 本地推理]]
