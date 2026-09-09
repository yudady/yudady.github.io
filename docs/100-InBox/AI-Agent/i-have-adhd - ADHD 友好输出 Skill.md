---
title: i-have-adhd — 让 AI 编程助手去废话、直接给行动指令的输出规范 Skill
tags:
  - ai-coding-agent
  - prompt-engineering
  - adhd
  - status/active
  - type/doc
source:
  - "https://github.com/ayghri/i-have-adhd"
author: ayghri
created: 2026-09-01
updated: 2026-09-01
description: 一个 26k Stars 的 Agent Skill：用 10 条规则重塑 AI 编程助手的输出格式——行动优先、步骤编号、无寒暄无总结无客套，基于成人 ADHD 工具书原则设计。
level: beginner
stars: 5
---

# i-have-adhd — 让 AI 编程助手去废话、直接给行动指令的输出规范 Skill

> [!info] 基本信息
> - **仓库**: https://github.com/ayghri/i-have-adhd
> - **Stars**: 26,066 / **Forks**: 1,638（2026-09-01 实测，3 个月达成）
> - **作者**: ayghri（个人开发者）
> - **协议**: MIT
> - **创建时间**: 2026-05-13
> - **学理基础**: *The Adult ADHD Tool Kit*（J. Russell Ramsay & Anthony L. Rostain）

一句话定位：这不是一个工具，是一份**输出格式规范**——单一 `SKILL.md` 文件定义 10 条规则，让任何 AI 编程助手停止"把答案埋在废话里"（burying the answer）。

## 目录

- [核心理念](#核心理念)
- [10 条规则详解](#10-条规则详解)
- [平台支持矩阵](#平台支持矩阵)
- [激活机制：三层设计](#激活机制三层设计)
- [何时打破规则](#何时打破规则)
- [工程质量：不止一个 prompt](#工程质量不止一个-prompt)
- [客制化路径](#客制化路径)
- [潜在限制与适用判断](#潜在限制与适用判断)
- [参考资料](#参考资料)

---

## 核心理念

项目建立在 5 个关于 ADHD 大脑的事实上，每条规则都能追溯到其中一个事实：

| # | ADHD 大脑的事实 | 对应的设计决策 |
|---|----------------|---------------|
| 1 | 工作记忆小，屏幕外的东西等于忘记 | 规则 5：每轮重申状态（"step 3 of 5 done"） |
| 2 | "知道答案" ≠ "做到答案"，摩擦在 got it 与 done it 之间 | 规则 1/3：首行给可执行动作，结尾给一个 <2 分钟的下一步 |
| 3 | 启动是最难的一步 | 规则 1：第一个动作必须明显、小、现在就能做 |
| 4 | 时间感觉失真，"a bit" 和 "几小时" 读起来一样 | 规则 6：分钟级具体时间预估 |
| 5 | 多巴胺稀缺，看不见的进步等于没发生 | 规则 7：把完成的工作用具体可验证的方式展示出来 |

关键洞察：**输出"简短"不是目的，输出"可被 ADHD 大脑执行"才是**。这也是它和普通 "be concise" prompt 的本质区别——普通压缩只删字数，这套规则重塑信息结构（首行=动作、结尾=下一步、中间=编号步骤）。

README 的 Before/After 对照最能说明变化：

```text
Before（典型 AI 回复）:
  Great question! Let me think about this. Your auth flow has a few
  moving pieces... Looking at src/auth.ts, the verifyToken function
  seems to be using an older API. One approach would be to update...
  Hope this helps! Let me know if you want to dig deeper.

After（i-have-adhd 规则下）:
  Run `npm install jsonwebtoken@latest`, then edit `src/auth.ts:42`.
  1. Open `src/auth.ts`
  2. Replace `verifyToken` (lines 42–58) with the snippet below
  3. Run `npm test -- auth.spec.ts`
  Next: paste the first failing line if any test fails.
```

---

## 10 条规则详解

`skills/i-have-adhd/SKILL.md` 是唯一的 canonical 行为定义。规则按功能分四组：

### 结构组（信息放在哪）

| 规则 | 要求 | 反例 → 正例 |
|------|------|------------|
| 1. Lead with the next action | 首行必须是可做的事，不是上下文不是计划 | "Let's think about this..." → "Run `npm install jsonwebtoken`, then edit `src/auth.ts:42`." |
| 2. Number multi-step tasks | 多步任务必须编号，每步一个有界动作，不含双重 "and then" | 连续散文叙述 → 1/2/3 编号清单 |
| 3. End with one concrete next action | 结尾给一个 2 分钟内能完成的动作，"open the file" 也算 | "Hope that helps." → "Next: run `npm test` and paste the first failing line." |
| 9. Cap lists at 5 items | 清单超过 5 项就拆成 do now / later 或 must / nice to have | 10 项无排序清单 → 5 项排序清单 |

### 专注组（砍掉什么）

| 规则 | 要求 |
|------|------|
| 4. Suppress tangents | 第二个问题存在时，先解决第一个，再把第二个作为独立问题单独提出 |
| 10. No preamble / recap / closers | 禁止 "Great question," "Let me..." "Hope this helps" "Feel free to ask" 全家桶 |

### 状态组（跨轮次记忆补偿）

| 规则 | 要求 |
|------|------|
| 5. Restate state every turn | 读者记不住"我们在第 3/5 步"，每轮重申；有 task/plan 工具时优先用工具做重申 |
| 7. Make wins visible | 用具体可验证的方式展示现在什么能跑了："Try: `npm run dev`, open `/login`" |

### 语气组（怎么说话）

| 规则 | 要求 |
|------|------|
| 6. Specific time estimates | "About 15 minutes if tests already cover this. An afternoon if not."——具体单位，拒绝 "a bit" |
| 8. Matter-of-fact errors | 禁止 "Uh oh" "Oh no"；直接说 位置 + 原因 + 修复 |

SKILL.md 还内置一个 **Pre-send check**（发送前自检清单）：删掉预告动作的首句、删掉 recap 的尾句、删掉 "by the way" 旁支、删掉不携带真实不确定性的对冲副词（保留有信息量的 hedge）、把习语换成字面动作。最后验证：**只读首行和尾行的人，能否知道 (a) 接下来做什么 (b) 刚发生了什么**。

---

## 平台支持矩阵

单一 SKILL.md 通过各平台的适配层分发，实测覆盖 15+ 运行时：

| 平台 | 安装方式 | 激活方式 | Always-on 机制 |
|------|---------|---------|---------------|
| Claude Code | `claude plugin marketplace add ayghri/i-have-adhd` | `/i-have-adhd` | `touch ~/.claude/.i-have-adhd-always`（SessionStart hook） |
| Codex | `codex plugin marketplace add` | `$i-have-adhd`（显式） | 写入 `~/.codex/AGENTS.md` |
| Gemini CLI | `gemini extensions install` 或 curl 命令 | `/i-have-adhd` | extension 路线天然 always-on |
| GitHub Copilot | `npx skills add ayghri/i-have-adhd -a github-copilot` | `/i-have-adhd`（尊重 disable-model-invocation） | 写入 `copilot-instructions.md` |
| Cursor / Amp | `npx skills add ayghri/i-have-adhd -a cursor` | `/i-have-adhd` | Settings → Rules → User Rules |
| **Hermes** | `hermes skills install ayghri/i-have-adhd/skills/i-have-adhd` | `/i-have-adhd` | 写入 workdir 的 `AGENTS.md` 或 persona `SOUL.md` |
| OpenCode | clone + `opencode.json` 挂 plugin | `/i-have-adhd` | `touch ~/.config/opencode/.i-have-adhd-always` |
| Qwen Code | `qwen extensions install ayghri/i-have-adhd` | `/i-have-adhd` | — |
| Kimi Code CLI | `/plugins` → Custom → 贴 URL | `/skill:i-have-adhd` | — |
| Zed | Agent Panel → Create skill from URL | `/i-have-adhd` | `~/.config/zed/AGENTS.md` |
| Pi / OMP / Antigravity | `pi install` / `omp plugin install` / `agy plugin install` | `/i-have-adhd` | flag 文件 + JSON config |

要点：
- **Claude Code / Qwen Code / Codex 是严格 opt-in**——`disable-model-invocation: true` frontmatter 保证不调用就不生效，没有中间态
- Hermes 原生支持一行安装，这在本机（Hermes Agent）可直接验证
- 各平台的 always-on 本质相同：把 10 条规则注入持久上下文（hook / AGENTS.md / rules 文件）

---

## 激活机制：三层设计

```text
                     ┌─────────────────────────────┐
                     │  Installed, not invoked     │
                     │  （默认：装了但什么都不改） │
                     └──────────────┬──────────────┘
                                    │
              ┌─────────────────────┼─────────────────────┐
              ▼                     ▼                     ▼
    ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
    │ 显式调用          │  │ flag 文件         │  │ 手动注入          │
    │ /i-have-adhd     │  │ ~/.claude/       │  │ AGENTS.md /      │
    │ （当前 session）  │  │ .i-have-adhd-    │  │ SOUL.md 粘贴     │
    │                  │  │ always           │  │ 10 条规则        │
    └────────┬─────────┘  └────────┬─────────┘  └────────┬─────────┘
             │                     │                     │
             └─────────────────────┼─────────────────────┘
                                   ▼
                    "stop adhd mode" / "normal mode"
                    （任何一层都能随时退出当前 session）
```

细节：Claude Code 的 SessionStart hook（`hooks/hooks.json` + `always-on.mjs`）只在 flag 文件存在时触发——**安装插件本身不改变任何默认行为**，这是对用户信任的尊重。matcher 覆盖 `startup|resume|clear|compact`，即 compaction 之后规则也会重新注入，不会因上下文压缩而失效。

---

## 何时打破规则

SKILL.md 明确定义了 6 个 override 场景，避免"死板的简洁"伤害答案质量：

```text
用户请求类型？
│
├─ "explain" / "walk me through"
│   → 规则让位于解释：正文跑满需要的长度
│     但仍然：无开场白、无结尾客套、加 header 便于回扫
│
├─ 危险操作在前（rm -rf / force push / schema migration）
│   → 安全 > 简洁：先确认再动手
│
├─ Debug 螺旋（连续 3 轮 "still broken"）
│   → 停止迭代代码，说出可能错误的假设，问一个诊断问题
│
├─ 请求存在真实歧义
│   → 一个简短的澄清问题 > 猜错后重写
│
├─ 规则会删掉答案本身（"what are my options"）
│   → 选项就是答案：给 2-4 个排序选项 + 单行 trade-off，推荐在前
│
└─ 规则与 agent harness 冲突
    → harness 的 system prompt 优先级更高
      （需要宣告工具调用就宣告；时间预估指向实际执行者）
```

核心原则一句话：**task wins, the shape stays**——任务内容优先，但输出形态（结构）尽量保持。

---

## 工程质量：不止一个 prompt

这个仓库的价值密度超出"一个 SKILL.md"，工程配套完整：

1. **Eval 体系**（`evals/` + `scripts/run_evals.py`）
   - `cases.jsonl` 测试用例 + `rubric.md` 评分契约（correctness / autonomy / actionability / safety / concision 五维）
   - 对照实验设计：baseline vs candidate 双条件注入，`--condition-skill` 参数注入待测 SKILL.md
   - 防污染意识强：runner 用 `--setting-sources ""` 隔离操作者自己的插件/hooks/记忆——README 明确警告 always-on flag 会把规则泄进 baseline 条件，变成"自己和自己比"

   ```bash
   python3 scripts/run_evals.py run \
     --runner claude --condition baseline \
     --condition-skill skills/i-have-adhd/SKILL.md \
     --trials 3 --budget-usd 12.50 \
     --output evals/results/responses.jsonl
   ```

2. **跨平台 hooks**（`hooks/always-on.{mjs,sh,ps1}`）——Node/shell/PowerShell 三实现，Windows 也覆盖
3. **AI Agora 治理实验**——issue #127 作为带 `AI Agora` 标签的 agent 公共论坛：agent 可在自己的 PR 评论、可带标签参与 issue 讨论，但标签不等于授权改仓库。用规则限定 agent 行为边界，是对"agent 协作治理"的先行实验
4. **AGENTS.md 规范**——完整的 agent 贡献指南：canonical 文件 + mirror 同步规则（`.cursor/` 镜像必须同步）、manifest 版本对齐、验证命令清单

### 与同类方案对比

| 方案 | 本质 | 优点 | 缺点 |
|------|------|------|------|
| i-have-adhd | 输出格式规范（10 条规则） | 有学理依据、规则有 override 机制、平台覆盖最广 | 只管输出格式，不管 agent 行为质量 |
| 普通 "be concise" system prompt | 单条指令 | 零成本 | 只删字数不重塑结构，模型执行不稳定 |
| CLAUDE.md / AGENTS.md 手写规范 | 项目级定制 | 完全可控 | 无 override 设计时容易过度压制解释性输出 |
| Tree-of-thought / 执行力 agent skill | 推理过程增强 | 提升答案质量 | 与输出格式正交，可叠加使用 |

---

## 客制化路径

官方支持的 fork 改法（改完自己的规则集换入）：

```bash
# 1. Fork 仓库后编辑 skills/i-have-adhd/SKILL.md（规则唯一真源）

# 2. 换入自己的版本（fork 和 upstream 同名，先卸上游）
claude plugin uninstall i-have-adhd
claude plugin marketplace remove i-have-adhd
claude plugin marketplace add <your-username>/i-have-adhd
claude plugin install i-have-adhd@i-have-adhd

# 3. 重启 Claude Code，重新 /i-have-adhd
```

注意 `.cursor/skills/i-have-adhd/SKILL.md` 是 mirror，改规则要同步两处（或只改 canonical 再同步）。

在 Hermes 中的最小验证路径：

```bash
hermes skills install ayghri/i-have-adhd/skills/i-have-adhd
hermes skills list          # 确认出现
# 新 session 中 /i-have-adhd 启用；"stop adhd mode" 关闭
```

---

## 潜在限制与适用判断

```text
你的使用场景？
│
├─ 日常修 bug / 小改动 / 明确任务的执行
│   → 高收益：直接拿指令，省读废话时间
│
├─ 探索性架构讨论 / 需要权衡分析
│   → 有损：设计核心是压制延伸讨论
│     应对：说 "explain" 触发 override，或临时 "stop adhd mode"
│
├─ 学习新技术 / 需要 deep dive
│   → 不适合：规则天然反对长解释
│     应对：换普通模式，读完再切回
│
└─ 多任务并行、跨 session 长流程
    → 规则 5（重申状态）最有价值，但依赖模型自觉执行
```

其他注意点：
- **规则 10 与 harness 要求可能冲突**：部分 agent harness 要求宣告工具调用（"I'll now run the tests"），SKILL.md 的 override 6 已处理（harness 优先，形态保留），但非 Claude 平台的模型遵守度未经验证
- **效果依赖模型指令遵循能力**：10 条规则本质是软约束，弱模型会漂移（INSTALL 的 troubleshooting 专门有一条 "Installed but replies still preamble"）
- **26k stars ≠ 26k 深度用户**：star 动机包含情绪共鸣（"烦透了 AI 废话"），实际留存未公开；但 evals 体系和 15+ 平台适配的工程质量说明这不是纯情绪项目

---

## 参考资料

- [ayghri/i-have-adhd — GitHub](https://github.com/ayghri/i-have-adhd)
- [SKILL.md 全文（canonical 规则）](https://github.com/ayghri/i-have-adhd/blob/master/skills/i-have-adhd/SKILL.md)
- [INSTALL.md（15+ 平台安装指南）](https://github.com/ayghri/i-have-adhd/blob/master/INSTALL.md)
- [evals/README.md（评估体系）](https://github.com/ayghri/i-have-adhd/blob/master/evals/README.md)
- 学理来源：*The Adult ADHD Tool Kit*, J. Russell Ramsay & Anthony L. Rostain

## 相关笔记

- 待补：Agent Skills 生态观察（npx skills / skills CLI 跨平台分发机制）

---

*文档生成时间：2026-09-01*
*基于 ayghri/i-have-adhd（MIT，commit 61d9584，2026-08-26 最后推送）*
*Stars/Forks 数据为 2026-09-01 GitHub API 实测*
