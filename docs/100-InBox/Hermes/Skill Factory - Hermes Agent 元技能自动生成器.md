---
created: 2026-04-10
tags:
  - AI/Agent
  - AI/Skills
  - 开源项目
  - Hermes
  - status/active
  - area/distill
  - type/doc
source: https://github.com/Romanescu11/hermes-skill-factory
---

# Skill Factory - Hermes Agent 的元技能自动生成器

> [!info] 基本信息
> - **仓库**: https://github.com/Romanescu11/hermes-skill-factory
> - **Stars** 35 / **Forks** 1 / **Open Issues** 0
> - **作者**: [@superman32432432](https://github.com/superman32432432) (Romanescu11)
> - **协议**: MIT（README 声明，仓库未设 license 文件）
> - **版本**: 1.0.0
> - **创建时间**: 2026-03-18（约 23 天）
> - **主要语言**: Python (88%), Shell (12%)
> - **依赖**: Hermes Agent v2026.3+
> - **关联项目**: [NousResearch/hermes-agent](https://github.com/NousResearch/hermes-agent)

---

## 一句话定位

Hermes Agent 的 meta-skill -- 静默观察你的工作流，自动检测重复模式，把工作流转化为可复用的 Hermes skill。

---

## 核心解决的问题

用 Hermes 工作时，每次解决一个问题的工作流（环境搭建、调试、创建 PR）在会话结束后就消失了，下次又要重新描述。Skill Factory 自动捕捉这些可复用的工作流，生成标准化的 skill 文件和 plugin 文件。

---

## 工作原理

**本质是两个文件的配合**：

1. **SKILL.md**（AI 指令）-- 教 Hermes AI 如何观察、检测、提出 skill 候选
2. **skill_factory.py**（Plugin）-- 提供 `/skill-factory` 命令和文件生成逻辑

### 五阶段流程

| 阶段 | 行为 |
|---|---|
| 1. 静默观察 | 跟踪重复操作、多步骤工作流、工具组合模式 |
| 2. 触发检测 | 用户显式请求 / 斜杠命令 / 重复 2 次以上 / 会话结束 / 用户表达挫败感 |
| 3. 提出建议 | 输出标准化的 skill 提案卡片，含名称、分类、步骤摘要 |
| 4. 生成文件 | 用户确认后生成 SKILL.md 和/或 plugin.py |
| 5. 后续跟进 | 告知安装方式，询问是否继续处理队列中的下一个模式 |

### 触发条件

| 触发方式 | 示例 |
|---|---|
| 用户显式请求 | "save this as a skill" |
| 斜杠命令 | `/skill-factory propose` |
| 重复模式 (2x+) | 同一工作流出现两次 |
| 会话结束 | 用户说 "done", "thanks" |
| 用户挫败 | "I always have to do this manually..." |

---

## 生成的产物

### SKILL.md
标准 Hermes skill 格式，包含：frontmatter、触发条件、分阶段工作流步骤、质量检查清单、实际示例、反模式提醒。

路径：`~/.hermes/skills/<category>/<name>/SKILL.md`

### plugin.py
Hermes plugin 格式，包含：注册 slash command + 可选 tool 注册。

路径：`~/.hermes/plugins/<name>.py`

---

## 技术实现细节

**SessionTracker 类**（纯内存，per-session）：
- `events` -- 记录 tool_call / command 事件
- `generated_skills` -- 已生成的 skill 列表
- `proposal_queue` -- 待提出模式队列
- `last_proposal` -- 最近一次提案

**事件钩子**：
```python
@hermes.on("tool_call")  # 被动记录工具调用
@hermes.on("command")    # 被动记录命令执行
```

**文件生成**：
- `generate_skill_md()` -- 用 textwrap.dedent 拼接 SKILL.md
- `generate_plugin_py()` -- 用 textwrap.dedent 拼接 plugin.py
- `_sanitize_name()` -- 字符串转 kebab-case

---

## 斜杠命令

| 命令 | 功能 |
|---|---|
| `/skill-factory propose` | 分析当前会话，提出最佳 skill 候选 |
| `/skill-factory list` | 列出本次会话已生成的 skill |
| `/skill-factory status` | 显示当前跟踪状态 |
| `/skill-factory queue` | 显示待处理模式队列 |
| `/skill-factory save <name>` | 用自定义名称保存最近提案 |
| `/skill-factory clear` | 清空会话跟踪日志 |

---

## 仓库结构

```
hermes-skill-factory/
├── skills/
│   └── skill-factory/
│       └── SKILL.md          # 核心元技能（AI 指令）
├── plugins/
│   └── skill_factory.py      # Plugin：命令 + 文件生成
├── templates/
│   ├── SKILL_TEMPLATE.md     # 生成 skill 的模板
│   └── PLUGIN_TEMPLATE.py    # 生成 plugin 的模板
├── examples/
│   └── generated/
│       └── git-pr-workflow/  # 生成示例
├── docs/
│   └── how-it-works.md       # 架构详解
└── install.sh                # 一键安装
```

---

## 评价

**优点**：
- 概念很好 -- 把"程序记忆"自动化的思路清晰
- 触发条件设计合理（不只是机械检测，还包括情绪/语义触发）
- 生成的 skill 格式完整（含 anti-patterns、quality checklist）

**局限**：
- 实际的模式检测逻辑依赖 AI 推理（SKILL.md 里的指令），没有真正的算法模式匹配
- SessionTracker 只记录原始事件，没有相似度计算或聚类
- plugin.py 生成的是脚手架代码（全是注释和 pass），不是真正可执行的实现
- 仓库很轻量（~20KB Python），更像是一个精心设计的 prompt + 简单框架
- 单一贡献者，项目基本停滞（最后更新 3/18）

**对 Hermes skill 生态的参考价值**：SKILL.md 的 skill 模板格式和质量标准值得借鉴。
