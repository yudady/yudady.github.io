---
in:
aliases: [Oh My OpenCode]
updated: 2026-03-01 10:42
description:
level:
stars:
title: Oh My OpenCode
created: 2026-01-26 08:18
tags: [agent, ai, automation, development, llm, opencode]

---

## 相关笔记
- [[004-ai-tools/claude/claude|Claude Code]] - Anthropic AI 编程助手
- [[004-ai-tools/claude/slash commands for Claude Code|Slash Commands]]
- [[004-ai-tools/claude/planning-with-files|Planning with Files]]
- [[004-ai-tools/Cline|Cline]] - VS Code AI 助手
- [[004-ai-tools/ollama/Ollama|Ollama]] - 本地 LLM 运行环境
- [[004-ai-tools/gemini/README|Gemini]] - Google AI 工具
- [[004-ai-tools/nanobot/NANOBOT_SOURCE_ANALYSIS|Nanobot 源码分析]]
- [[study-notes/AI编程GSD工作流完全指南|GSD 工作流]]
- [[MOC-AI编程工具|AI 编程工具 MOC]]
cssclass: apple-style
source: https://github.com/code-yeongyu/oh-my-opencode
author: code-yeongyu
---

# Oh My OpenCode

[Open in github.dev](https://github.dev/) | [Open in a new github.dev tab](https://github.dev/) | [Open in codespace](https://github.com/codespaces/new/code-yeongyu/oh-my-opencode/tree/dev?resume=1)


## 連結您的 帳戶

```
opencode auth login
```

## 全域設定檔位於以下位置：

```
~/.config/opencode/opencode.json
```


```
類型`是 ultrawork`（或 `ulw`）。完成。
你的 Agent 配置

Agent | 模型| 用途|

Sisyphus | zai-coding-plan/glm-4.7 | 主编排器，不达目的不罢休|
Oracle | zai-coding-plan/glm-5 | 架构设计、调试专家|
Librarian | zai-coding-plan/glm-4.7 | 文档/代码搜索|
Explore | zai-coding-plan/glm-4.7-flash | 快速代码库搜索|
Multimodal Looker | zai-coding-plan/glm-4.6v | 视觉/截图分析|

1II

使用教程

1. ASisyphus agent 推荐使用 Opus 4.6 模型。使用其他模型（如GLM）可能会降低体验。

2. 懒人模式：在提示词中加上 vltrawork （或 ，ULw），agent 会自动处理一切。
3. 精准模式：按 ： Tab 进入 Promethevs 规划模式，通过访谈创建工作计划，然后运行/start-work = 执行。

4. 自定义 Agent：可以阅读 docs/quide/agent-model-matching.md Chttps://raw_ githubusercontent.com/code-yeongyu/oh-my-opencode/refs/heads/dev/docs/guide/ agent-model-matching.md） 了解更多配置选项。
```

---

## 魔法词

### ultrawork

**不想读完这些？只需在你的提示中包含 `ultrawork`（或 `ulw`）。**

就这样。我们提供的所有功能都会像魔法一样运行——并行智能体、后台任务、深度探索，以及不懈执行直到完成。智能体会自动理解其余的。

---

## 认识 Sisyphus

[![认识 Sisyphus](https://github.com/code-yeongyu/oh-my-opencode/raw/dev/.github/assets/sisyphus.png)](https://github.com/code-yeongyu/oh-my-opencode/blob/dev/.github/assets/sisyphus.png)

> [!note] 希腊神话的启示
> 在希腊神话中，西西弗斯因欺骗众神而被惩罚永恒地将巨石推上山坡。LLM 智能体并没有做错什么，但它们也每天推动着它们的"石头"——它们的思考。

**是的！LLM 智能体和我们没有区别。** 如果你给它们优秀的工具和可靠的队友，它们可以写出和我们一样出色的代码，工作得同样优秀。

### 主智能体：Sisyphus (Opus 4.5 High)

以下所有内容都是可配置的。按需选取。所有功能默认启用。你不需要做任何事情。开箱即用，电池已包含。

#### 队友配置

**精选智能体：**

- **Oracle**：设计、调试 (GPT 5.2 Medium)
- **Frontend UI/UX Engineer**：前端开发 (Gemini 3 Pro)
- **Librarian**：官方文档、开源实现、代码库探索 (Claude Sonnet 4.5)
- **Explore**：极速代码库探索（上下文感知 Grep）(Grok Code)

#### 核心能力

**完整的 LSP / AstGrep 支持**：果断重构。

**Todo 继续执行器**：如果智能体中途退出，强制它继续。这就是让 Sisyphus 继续推动巨石的关键。

**注释检查器**：防止 AI 添加过多注释。Sisyphus 生成的代码应该与人类编写的代码无法区分。

**Claude Code 兼容性**：Command、Agent、Skill、MCP、Hook（PreToolUse、PostToolUse、UserPromptSubmit、Stop）

#### 精选 MCP

- **Exa**：网络搜索
- **Context7**：官方文档
- **Grep.app**：GitHub 代码搜索

#### 交互支持

- 支持 Tmux 集成的交互式终端
- 异步智能体

---

## 安装指南

### 直接安装

只需安装这个，你的智能体就会这样工作：

1. Sisyphus 不会浪费时间自己寻找文件；他保持主智能体的上下文精简。相反，他向更快、更便宜的模型并行发起后台任务，让它们为他绘制地图。

2. Sisyphus 利用 LSP 进行重构；这更确定性、更安全、更精准。

3. 当繁重的工作需要 UI 时，Sisyphus 直接将前端任务委派给 Gemini 3 Pro。

4. 如果 Sisyphus 陷入循环或碰壁，他不会继续撞墙——他会召唤 GPT 5.2 进行高智商战略支援。

5. 在处理复杂的开源框架时？Sisyphus 生成子智能体实时消化原始源代码和文档。他拥有完整的上下文感知。

6. 当 Sisyphus 处理注释时，他要么证明它们存在的必要性，要么删除它们。他保持你的代码库整洁。

7. Sisyphus 受他的 TODO 列表约束。如果他没有完成开始的工作，系统会强制他回到"推石头"模式。你的任务会被完成，句号。

8. 老实说，甚至不用费心读文档。只需写你的提示。包含 'ultrawork' 关键词。Sisyphus 会分析结构，收集上下文，挖掘外部源代码，然后持续推进直到工作 100% 完成。

9. 其实，打 'ultrawork' 太费劲了。只需打 'ulw'。就 ulw。喝杯咖啡。你的工作完成了。

> [!warning] 注意事项
> 如果你不想要这些全部功能，如前所述，你可以只选择特定功能。

---

## 验证安装

```bash
opencode --version  # 应该是 1.0.150 或更高版本
cat ~/.config/opencode/opencode.json  # 应该在 plugin 数组中包含 "oh-my-opencode"
```

2.1.1 配置文件位置（按优先级排序）
配置文件按以下顺序加载，后面的配置会覆盖前面的冲突项：

远程配置（.well-known/opencode）- 组织默认
全局配置（~/.config/opencode/opencode.json）- 用户偏好
自定义路径（OPENCODE_CONFIG 环境变量）- 自定义覆盖
项目配置（opencode.json 在项目根目录）- 项目特定设置
.opencode 目录 - agents、commands、plugins
内联配置（OPENCODE_CONFIG_CONTENT 环境变量）- 运行时覆盖
注意： 配置文件是合并的，不是替换的。非冲突的设置会从所有配置中保留。
————————————————
版权声明：本文为CSDN博主「今天睡不醒～」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/jdiejbxiej/article/details/157727403




  
2. Category 调整

Category 一 模型 ！ 优化理由|

ultrabrain | GLM-5| 逻辑密集型任务，最强模型|

deep | GLM-5 | 深度问题解决，最强模型|
unspecified-high | GLM-5 | 高复杂度未分类任务 |
visual-engineering | Gemini 3 Pro | UI/UX 设计（多模态优势）！multimodal| Gemini 3 Flash | 图片/PDF 分析（快速）！
video-analysis | GLM-4.6V| 视频理解（原生支持）！
artistry | GLM-4.7| 创意和非常规解决方案|
writing | GLM-4.7 | 文档和技术写作！
unspecified-low GLM-4.7-Flash I#| 优化 - 低复杂度任务提速|quick | GLM-4.7-Flash | ' 快速修复和简单任务|

核心优化策略

速度优先场景（使用 GLM-4.7-Flash）：
- librarian - 文档检索需要快速扫描大量內容
- explore - 代码库探索需要快速搜索
- unspecified-Low - 日常简单任务提速

深度推理场景（使用 GLM-5）：
- oracle - 架构设计和复杂决策
- metis - 任务规划和需求分析
- ultrabrain/deep - 逻辑密集型问题

多模态场景（使用 Gemini/GLM-4.6V）：
- visual-engineering - UI/UX 需要 Gemini 的视觉理解
- multimodal - 图片/PDF 使用 Gemini Flash 平衡速度和质量
- video-analysis - GLM-4.6V 原生支持视频


- **Prometheus-设计：**站在产品或者架构师的角度，通过多轮对话的形式把你的模糊需求转为详细的设计文档。
- **Sisyphus - 开发，或者在 Prometheus 设计之后输入 **`/start-work`** 启动 Atlas：**在指令中增加 `ultrawork` （或简写为 `ulw`），可以激活 Agent 的最大强度模式，它会自动启动并行代理、后台任务、深度探索等功能，直到任务完成。
- **Oracle - 审查：**这一步需要手动通过 `@oracle` 唤起 Oracle Agent，让它 review 刚才的改动，看看产物是否符合预期以及是否带来了新的问题。
- **测试**：如果你的项目中已经有测试基础设施，那在设计阶段，Prometheus 会询问你是否开启 TDD，建议开启，可以大大提高你的项目功能稳定性。如果没有，你可以在指令中加入需要测试任务的要求。

---

## 配置认证

### Anthropic (Claude)

首先，添加 opencode-antigravity-auth 插件：

```json
{
  "plugin": [
    "oh-my-opencode",
    "opencode-antigravity-auth@1.2.8"
  ]
}
```

## 模型配置


> 请根据当前已配置的模型，给 oh-my-opencode 重新分配一下各个 Agent 的模型？预期是根据不同模型所擅长的能力与 Agent能匹配。


你还需要在 `opencode.json` 中配置完整的模型设置。阅读 [opencode-antigravity-auth 文档](https://github.com/NoeFabris/opencode-antigravity-auth)，从 README 复制 provider/models 配置，并仔细合并以避免破坏用户现有的设置。

#### oh-my-opencode 智能体模型覆盖

多模型智能协同 Multi-Model Orchestration

• GPT 5.2 （Oracle） 架构设计、复杂 Debug
• Gemini 3 Pro 前端 UI/UX 视觉设计
• Claude Sonnet -文档研究、OSS 分析
• Grok/Flash 快速探索，成本几乎为零

`opencode-antigravity-auth` 插件使用特定的模型名称。在 `oh-my-opencode.json`（或 `.opencode/oh-my-opencode.json`）中覆盖智能体模型：

```json
{
  "agents": {
    "frontend-ui-ux-engineer": { "model": "google/antigravity-gemini-3-pro-high" },
    "document-writer": { "model": "google/antigravity-gemini-3-flash" },
    "multimodal-looker": { "model": "google/antigravity-gemini-3-flash" }
  }
}





```





**可用模型名称：** `google/antigravity-gemini-3-pro-high`、`google/antigravity-gemini-3-pro-low`、`google/antigravity-gemini-3-flash`、`google/antigravity-claude-sonnet-4-5`、`google/antigravity-claude-sonnet-4-5-thinking-low`、`google/antigravity-claude-sonnet-4-5-thinking-medium`、`google/antigravity-claude-sonnet-4-5-thinking-high`、`google/antigravity-claude-opus-4-5-thinking-low`、`google/antigravity-claude-opus-4-5-thinking-medium`、`google/antigravity-claude-opus-4-5-thinking-high`、`google/gemini-3-pro-preview`、`google/gemini-3-flash-preview`、`google/gemini-2.5-pro`、`google/gemini-2.5-flash`

**多账号负载均衡：** 该插件支持最多 10 个 Google 账号。当一个账号达到速率限制时，它会自动切换到下一个可用账号。

---

### GitHub Copilot（备用提供商）

GitHub Copilot作为 **备用提供商** 受支持，当原生提供商（Claude、ChatGPT、Gemini）不可用时使用。安装程序将 Copilot 配置为低于原生提供商的优先级。

**优先级：** 原生提供商 (Claude/ChatGPT/Gemini) > GitHub Copilot > 免费模型

#### 模型映射

启用 GitHub Copilot 后，oh-my-opencode 使用以下模型分配：

| 代理 | 模型 |
| --- | --- |
| **Sisyphus** | `github-copilot/claude-opus-4.5` |
| **Oracle** | `github-copilot/gpt-5.2` |
| **Explore** | `grok code`（默认） |
| **Librarian** | `glm 4.7 free`（默认） |

GitHub Copilot 作为代理提供商，根据你的订阅将请求路由到底层模型。

#### 设置

运行安装程序并为 GitHub Copilot 选择"是"：

```bash
bunx oh-my-opencode install
# 选择你的订阅（Claude、ChatGPT、Gemini）
# 出现提示时："Do you have a GitHub Copilot subscription?" → 选择"是"
```

或使用非交互模式：

```bash
bunx oh-my-opencode install --no-tui --claude=no --chatgpt=no --gemini=no --copilot=yes
```

然后使用 GitHub 进行身份验证。

---

## 卸载

要移除 oh-my-opencode：

### 从配置中移除插件

编辑 `~/.config/opencode/opencode.json`（或 `opencode.jsonc`）并从 `plugin` 数组中移除 `"oh-my-opencode"`：

```bash
# 使用 jq
jq '.plugin = [.plugin[] | select(. != "oh-my-opencode")]' \
    ~/.config/opencode/opencode.json > /tmp/oc.json && \
    mv /tmp/oc.json ~/.config/opencode/opencode.json
```

### 移除配置文件（可选）

```bash
# 移除用户配置
rm -f ~/.config/opencode/oh-my-opencode.json
# 移除项目配置（如果存在）
rm -f .opencode/oh-my-opencode.json
```

### 验证移除

```bash
opencode --version
# 插件应该不再被加载
```

---

## 功能特性

### 智能体系统

**你的队友：**

- **Sisyphus** (`anthropic/claude-opus-4-5`)：**默认智能体。** OpenCode 的强大 AI 编排器。使用专业子智能体进行规划、委派和执行复杂任务，采用积极的并行执行策略。强调后台任务委派和 todo 驱动的工作流程。使用 Claude Opus 4.5 配合扩展思考（32k 预算）以获得最大推理能力。

- **oracle** (`openai/gpt-5.2`)：架构、代码审查、策略。使用 GPT-5.2 进行出色的逻辑推理和深度分析。灵感来自 AmpCode。

- **librarian** (`opencode/glm-4.7-free`)：多仓库分析、文档查找、实现示例。使用 GLM-4.7 Free 进行深度代码库理解和 GitHub 研究，提供基于证据的答案。灵感来自 AmpCode。

- **explore** (`opencode/grok-code`、`google/gemini-3-flash` 或 `anthropic/claude-haiku-4-5`)：快速代码库探索和模式匹配。配置 Antigravity 认证时使用 Gemini 3 Flash，有 Claude max20 时使用 Haiku，否则使用 Grok。灵感来自 Claude Code。

- **frontend-ui-ux-engineer** (`google/gemini-3-pro-preview`)：设计师转开发者。构建华丽的 UI。Gemini 擅长创造性的、美观的 UI 代码。

- **document-writer** (`google/gemini-3-flash`)：技术写作专家。Gemini 是文字大师——写出流畅的散文。

- **multimodal-looker** (`google/gemini-3-flash`)：视觉内容专家。分析 PDF、图像、图表以提取信息。

主智能体会自动调用这些，但你也可以显式调用它们：

```
让 @oracle 审查这个设计并提出架构
让 @librarian 看看这是如何实现的——为什么行为一直在变化？
让 @explore 查找这个功能的策略
```

在 `oh-my-opencode.json` 中自定义智能体模型、提示和权限。

---

### 后台智能体

**像团队一样工作**

如果你能让这些智能体不知疲倦地运行，永不空闲呢？

- 让 GPT 调试的同时 Claude 尝试不同的方法来找到根本原因
- Gemini 编写前端的同时 Claude 处理后端
- 启动大规模并行搜索，继续实现其他部分，然后使用搜索结果完成

这些工作流程在 OhMyOpenCode 中都是可能的。

在后台运行子智能体。主智能体在完成时收到通知。需要时等待结果。

**让你的智能体像你的团队一样工作。**

---

### 工具系统

#### LSP 工具

**为什么只有你在用 IDE？**

语法高亮、自动完成、重构、导航、分析——现在还有智能体在写代码...

**为什么只有你拥有这些工具？** 把它们给你的智能体，看它们升级。

[OpenCode 提供 LSP](https://opencode.ai/docs/lsp/)，但仅用于分析。

你编辑器中的功能？其他智能体无法触及。把你最好的工具交给你最好的同事。现在它们可以正确地重构、导航和分析。

- **lsp_diagnostics**：在构建前获取错误/警告
- **lsp_prepare_rename**：验证重命名操作
- **lsp_rename**：在工作区中重命名符号
- **ast_grep_search**：AST 感知的代码模式搜索（25 种语言）
- **ast_grep_replace**：AST 感知的代码替换
- **call_omo_agent**：生成专业的 explore/librarian 智能体。支持 `run_in_background` 参数进行异步执行。
- **delegate_task**：基于类别的任务委派，使用专业智能体。支持预配置的类别（visual、business-logic）或直接指定智能体。使用 `background_output` 检索结果，使用 `background_cancel` 取消任务。

#### 会话管理

导航和搜索 OpenCode 会话历史的工具：

- **session_list**：列出所有 OpenCode 会话，支持按日期和数量过滤
- **session_read**：从特定会话读取消息和历史
- **session_search**：在会话消息中进行全文搜索
- **session_info**：获取会话的元数据和统计信息

这些工具使智能体能够引用之前的对话并在会话之间保持连续性。

---

### 上下文管理

#### 上下文注入器

**目录 AGENTS.md / README.md 注入器：** 读取文件时自动注入 `AGENTS.md` 和 `README.md`。从文件目录向上遍历到项目根目录，收集路径上的 **所有** `AGENTS.md` 文件。支持嵌套的目录特定说明：

```
project/
├── AGENTS.md              # 项目级上下文
├── src/
│   ├── AGENTS.md          # src 特定上下文
│   └── components/
│       ├── AGENTS.md      # 组件特定上下文
│       └── Button.tsx     # 读取此文件会注入所有 3 个 AGENTS.md 文件
```

读取 `Button.tsx` 会按顺序注入：`project/AGENTS.md` → `src/AGENTS.md` → `components/AGENTS.md`。每个目录的上下文在每个会话中只注入一次。

#### 条件规则注入器

不是所有规则都始终适用。当条件匹配时从 `.claude/rules/` 注入规则。

- 从文件目录向上遍历到项目根目录，加上 `~/.claude/rules/`（用户级）。
- 支持 `.md` 和 `.mdc` 文件。
- 通过 frontmatter 中的 `globs` 字段匹配。
- `alwaysApply: true` 表示应始终触发的规则。

---

### 内置 MCP

**在线能力：** 项目规则不是全部。这些是用于扩展能力的内置 MCP：

- **websearch**：由 [Exa AI](https://exa.ai/) 驱动的实时网络搜索
- **context7**：官方文档查询
- **grep_app**：跨公共 GitHub 仓库的超快代码搜索（非常适合查找实现示例）

---

### 多模态支持

**节省 Token。**

来自 AmpCode 的 look_at 工具，现在在 OhMyOpenCode 中。智能体不再需要读取大文件并膨胀上下文，它在内部利用另一个智能体只提取所需内容。

---

### MCP 技能集成

我移除了他们的障碍。

替换内置的 grep 和 glob 工具。默认实现没有超时——可能永远挂起。

技能现在可以携带自己的 MCP 服务器。直接在技能 frontmatter 中或通过 `mcp.json` 文件定义 MCP 配置：

```yaml
---
description: 浏览器自动化技能
mcp:
  playwright:
    command: npx
    args: ["-y", "@anthropic-ai/mcp-playwright"]
---
```

当你加载带有内嵌 MCP 的技能时，其工具会自动可用。`skill_mcp` 工具允许你使用完整的 schema 发现来调用这些 MCP 操作。

**内置技能：**

- **playwright**：开箱即用的浏览器自动化、网页抓取、测试和截图

通过配置中的 `disabled_skills: ["playwright"]` 禁用内置技能。

---

### Claude Code 兼容层

Oh My OpenCode 有一个 Claude Code 兼容层。如果你之前使用 Claude Code，你现有的配置直接可用。

#### 钩子集成

通过 Claude Code 的 `settings.json` 钩子系统运行自定义脚本。Oh My OpenCode 从以下位置读取和执行钩子：

- `~/.claude/settings.json`（用户级）
- `./.claude/settings.json`（项目级）
- `./.claude/settings.local.json`（本地，git 忽略）

**支持的钩子事件：**

- **PreToolUse**：工具执行前运行。可以阻止或修改工具输入。
- **PostToolUse**：工具执行后运行。可以添加警告或上下文。
- **UserPromptSubmit**：用户提交提示时运行。可以阻止或注入消息。
- **Stop**：会话空闲时运行。可以注入后续提示。

---

### 配置加载器

#### 命令加载器

从 4 个目录加载基于 markdown 的斜杠命令：

- `~/.claude/commands/`（用户级）
- `./.claude/commands/`（项目级）
- `~/.config/opencode/command/`（opencode 全局）
- `./.opencode/command/`（opencode 项目）

#### 技能加载器

从包含 `SKILL.md` 的目录加载技能：

- `~/.claude/skills/`（用户级）
- `./.claude/skills/`（项目级）

#### 智能体加载器

从 markdown 文件加载自定义智能体定义：

- `~/.claude/agents/*.md`（用户级）
- `./.claude/agents/*.md`（项目级）

#### MCP 加载器

从 `.mcp.json` 文件加载 MCP 服务器配置：

- `~/.claude/.mcp.json`（用户级）
- `./.mcp.json`（项目级）
- `./.claude/.mcp.json`（本地）
- 支持环境变量展开（`${VAR}` 语法）

---

### 数据存储

**Todo 管理：** 会话待办事项以 Claude Code 兼容格式存储在 `~/.claude/todos/` 中。

**转录：** 会话活动以 JSONL 格式记录到 `~/.claude/transcripts/` 中，用于回放和分析。

---

### 兼容性开关

使用 `claude_code` 配置对象禁用特定的 Claude Code 兼容功能：

```json
{
  "claude_code": {
    "mcp": false,
    "commands": false,
    "skills": false,
    "agents": false,
    "hooks": false,
    "plugins": false
  }
}
```

| 开关 | 当为 `false` 时，停止从以下位置加载... | 不受影响 |
| --- | --- | --- |
| `mcp` | `~/.claude/.mcp.json`、`./.mcp.json`、`./.claude/.mcp.json` | 内置 MCP（context7、grep_app） |
| `commands` | `~/.claude/commands/*.md`、`./.claude/commands/*.md` | `~/.config/opencode/command/`、`./.opencode/command/` |
| `skills` | `~/.claude/skills/*/SKILL.md`、`./.claude/skills/*/SKILL.md` | - |
| `agents` | `~/.claude/agents/*.md`、`./.claude/agents/*.md` | 内置智能体（oracle、librarian 等） |
| `hooks` | `~/.claude/settings.json`、`./.claude/settings.json`、`./.claude/settings.local.json` | - |
| `plugins` | `~/.claude/plugins/`（Claude Code 市场插件） | - |

所有开关默认为 `true`（启用）。省略 `claude_code` 对象以获得完整的 Claude Code 兼容性。

---

## 不仅仅是为了智能体

当智能体蓬勃发展时，你也会收益。但我同时也想直接帮助你。

### Ralph Loop

**自引用开发循环，持续运行直到任务完成。** 灵感来自 Anthropic 的 Ralph Wiggum 插件。**支持所有编程语言。**

- 使用 `/ralph-loop "构建一个 REST API"` 开始，让智能体持续工作
- 循环检测 `<promise>DONE</promise>` 来判断何时完成
- 如果智能体在没有完成承诺的情况下停止，会自动继续
- 结束条件：检测到完成、达到最大迭代次数（默认 100）或 `/cancel-ralph`
- 在 `oh-my-opencode.json` 中配置：`{ "ralph_loop": { "enabled": true, "default_max_iterations": 100 } }`

### 关键词检测器

自动检测提示中的关键词并激活专门模式：

- `ultrawork` / `ulw`：最大性能模式，带并行智能体编排
- `search` / `find` / `찾아` / `検索`：最大化搜索力度，带并行 explore 和 librarian 智能体
- `analyze` / `investigate` / `분석` / `調査`：深度分析模式，带多阶段专家咨询

### Todo 继续执行器

让智能体在停止前完成所有 TODO。终结 LLM 中途放弃的慢性习惯。

### 注释检查器

LLM 喜欢注释。太多注释。这提醒它们减少噪音。智能地忽略有效模式（BDD、指令、文档字符串）并要求为其余部分提供理由。整洁的代码获胜。

### 思考模式

自动检测何时需要扩展思考并切换模式。捕获"深入思考"或"ultrathink"等短语，并动态调整模型设置以获得最大推理能力。

### 上下文窗口监控

实现 [上下文窗口焦虑管理](https://agentic-patterns.com/patterns/context-window-anxiety-management/)。

- 在使用率达到 70%+ 时，提醒智能体还有空间——防止草率、马虎的工作。

### 智能体使用提醒

当你直接调用搜索工具时，提醒你通过后台任务利用专业智能体以获得更好的结果。

### Anthropic 自动压缩

当 Claude 模型达到 token 限制时，自动总结和压缩会话——无需手动干预。

### 会话恢复

自动从会话错误中恢复（缺失的工具结果、思考块问题、空消息）。会话不会在运行中崩溃。即使崩溃，也会恢复。

### 自动更新检查器

自动检查 oh-my-opencode 的新版本，并可以自动更新你的配置。在启动时显示 toast 通知，显示当前版本和 Sisyphus 状态（启用时显示"Sisyphus on steroids is steering OpenCode"，否则显示"OpenCode is now on Steroids. oMoMoMoMo..."）。通过在 `disabled_hooks` 中添加 `"auto-update-checker"` 禁用所有功能，或通过在 `disabled_hooks` 中添加 `"startup-toast"` 仅禁用 toast 通知。

---

## 配置

个性鲜明，但可以根据个人喜好调整。

### 配置文件位置

**优先级顺序：**

1. `.opencode/oh-my-opencode.json`（项目级）
2. 用户配置（平台特定）：

| 平台 | 用户配置路径 |
| --- | --- |
| **Windows** | `~/.config/opencode/oh-my-opencode.json`（首选）或 `%APPDATA%\opencode\oh-my-opencode.json`（备选） |
| **macOS/Linux** | `~/.config/opencode/oh-my-opencode.json` |

**支持 Schema 自动补全：**

```json
{
  "$schema": "https://raw.githubusercontent.com/code-yeongyu/oh-my-opencode/master/assets/oh-my-opencode.schema.json"
}
```

### JSONC 支持

`oh-my-opencode` 配置文件支持 JSONC（带注释的 JSON）：

- 行注释：`// 注释`
- 块注释：`/* 注释 */`
- 尾随逗号：`{ "key": "value", }`

当 `oh-my-opencode.jsonc` 和 `oh-my-opencode.json` 文件同时存在时，`.jsonc` 优先。

---

### Google 认证

使用外部 [`opencode-antigravity-auth`](https://github.com/NoeFabris/opencode-antigravity-auth) 插件进行 Google 认证。它提供多账号负载均衡、更多模型（包括通过 Antigravity 的 Claude）和积极的维护。

使用 `opencode-antigravity-auth` 时，在 `oh-my-opencode.json` 中覆盖智能体模型：

```json
{
  "agents": {
    "frontend-ui-ux-engineer": { "model": "google/antigravity-gemini-3-pro-high" },
    "document-writer": { "model": "google/antigravity-gemini-3-flash" },
    "multimodal-looker": { "model": "google/antigravity-gemini-3-flash" }
  }
}
```

---

### 智能体配置

覆盖内置智能体设置：

```json
{
  "agents": {
    "explore": {
      "model": "anthropic/claude-haiku-4-5",
      "temperature": 0.5
    },
    "frontend-ui-ux-engineer": {
      "disable": true
    }
  }
}
```

每个智能体支持：`model`、`temperature`、`top_p`、`prompt`、`prompt_append`、`tools`、`disable`、`description`、`mode`、`color`、`permission`。

使用 `prompt_append` 添加额外指令而不替换默认系统提示：

```json
{
  "agents": {
    "librarian": {
      "prompt_append": "始终使用 elisp-dev-mcp 进行 Emacs Lisp 文档查找。"
    }
  }
}
```

你也可以使用相同的选项覆盖 `Sisyphus`（主编排器）和 `build`（默认智能体）的设置。

#### 权限选项

对智能体能做什么进行细粒度控制：

```json
{
  "agents": {
    "explore": {
      "permission": {
        "edit": "deny",
        "bash": "ask",
        "webfetch": "allow"
      }
    }
  }
}
```

| 权限 | 描述 | 值 |
| --- | --- | --- |
| `edit` | 文件编辑权限 | `ask` / `allow` / `deny` |
| `bash` | Bash 命令执行 | `ask` / `allow` / `deny` 或按命令：`{ "git": "allow", "rm": "deny" }` |
| `webfetch` | Web 请求权限 | `ask` / `allow` / `deny` |
| `doom_loop` | 允许无限循环检测覆盖 | `ask` / `allow` / `deny` |
| `external_directory` | 访问项目根目录外的文件 | `ask` / `allow` / `deny` |

或通过 `~/.config/opencode/oh-my-opencode.json` 或 `.opencode/oh-my-opencode.json` 中的 `disabled_agents` 禁用：

```json
{
  "disabled_agents": ["oracle", "frontend-ui-ux-engineer"]
}
```

可用智能体：`oracle`、`librarian`、`explore`、`frontend-ui-ux-engineer`、`document-writer`、`multimodal-looker`

---

### 内置技能

Oh My OpenCode 包含提供额外功能的内置技能：

- **playwright**：使用 Playwright MCP 进行浏览器自动化。用于网页抓取、测试、截图和浏览器交互。
- **git-master**：Git 专家，用于原子提交、rebase/squash 和历史搜索（blame、bisect、log -S）。**强烈推荐**：与 `delegate_task(category='quick', skills=['git-master'], ...)` 一起使用以节省上下文。

通过 `~/.config/opencode/oh-my-opencode.json` 或 `.opencode/oh-my-opencode.json` 中的 `disabled_skills` 禁用内置技能：

```json
{
  "disabled_skills": ["playwright"]
}
```

可用内置技能：`playwright`、`git-master`

---

### Sisyphus 智能体

启用时（默认），Sisyphus 提供一个强大的编排器，带有可选的专业智能体：

- **Sisyphus**：主编排智能体（Claude Opus 4.5）
- **OpenCode-Builder**：OpenCode 的默认构建智能体，由于 SDK 限制而重命名（默认禁用）
- **Prometheus (Planner)**：OpenCode 的默认规划智能体，带有工作规划方法论（默认启用）
- **Metis (Plan Consultant)**：预规划分析智能体，识别隐藏需求和 AI 失败点

**配置选项：**

```json
{
  "sisyphus_agent": {
    "disabled": false,
    "default_builder_enabled": false,
    "planner_enabled": true,
    "replace_plan": true
  }
}
```

| 选项 | 默认 | 描述 |
| --- | --- | --- |
| `disabled` | `false` | 当为 `true` 时，禁用所有 Sisyphus 编排并恢复原始 build/plan 为主要智能体。 |
| `default_builder_enabled` | `false` | 当为 `true` 时，启用 OpenCode-Builder 智能体（与 OpenCode build 相同，由于 SDK 限制而重命名）。默认禁用。 |
| `planner_enabled` | `true` | 当为 `true` 时，启用带有工作规划方法论的 Prometheus (Planner) 智能体。默认启用。 |
| `replace_plan` | `true` | 当为 `true` 时，将默认规划智能体降级为子智能体模式。设置为 `false` 以同时保留 Prometheus (Planner) 和默认 plan 可用。 |

---

### 后台任务

配置后台智能体任务的并发限制。这控制可以同时运行多少个并行后台智能体。

```json
{
  "background_task": {
    "defaultConcurrency": 5,
    "providerConcurrency": {
      "anthropic": 3,
      "openai": 5,
      "google": 10
    },
    "modelConcurrency": {
      "anthropic/claude-opus-4-5": 2,
      "google/gemini-3-flash": 10
    }
  }
}
```

**优先级顺序：** `modelConcurrency` > `providerConcurrency` > `defaultConcurrency`

**使用场景：**

- 限制昂贵的模型（例如 Opus）以防止成本激增
- 为快速/便宜的模型（例如 Gemini Flash）允许更多并发任务
- 通过设置提供商级别上限来尊重提供商速率限制

---

### 类别

类别通过 `delegate_task` 工具实现领域特定的任务委派。每个类别预配置一个专业的 `Sisyphus-Junior-{category}` 智能体，带有优化的模型设置和提示。

**默认类别：**

| 类别 | 模型 | 描述 |
| --- | --- | --- |
| `visual` | `google/gemini-3-pro-preview` | 前端、UI/UX、设计相关任务。高创造性（温度 0.7）。 |
| `business-logic` | `openai/gpt-5.2` | 后端逻辑、架构、战略推理。低创造性（温度 0.1）。 |

**使用方法：**

```
// 通过 delegate_task 工具
delegate_task(category="visual", prompt="创建一个响应式仪表板组件")
delegate_task(category="business-logic", prompt="设计支付处理流程")

// 或直接指定特定智能体
delegate_task(agent="oracle", prompt="审查这个架构")
```

**自定义类别：**

在 `oh-my-opencode.json` 中添加自定义类别：

```json
{
  "categories": {
    "data-science": {
      "model": "anthropic/claude-sonnet-4-5",
      "temperature": 0.2,
      "prompt_append": "专注于数据分析、ML 管道和统计方法。"
    },
    "visual": {
      "model": "google/gemini-3-pro-preview",
      "prompt_append": "使用 shadcn/ui 组件和 Tailwind CSS。"
    }
  }
}
```

每个类别支持：`model`、`temperature`、`top_p`、`maxTokens`、`thinking`、`reasoningEffort`、`textVerbosity`、`tools`、`prompt_append`。

---

### 钩子

通过 `~/.config/opencode/oh-my-opencode.json` 或 `.opencode/oh-my-opencode.json` 中的 `disabled_hooks` 禁用特定的内置钩子：

```json
{
  "disabled_hooks": ["comment-checker", "agent-usage-reminder"]
}
```

可用钩子：`todo-continuation-enforcer`、`context-window-monitor`、`session-recovery`、`session-notification`、`comment-checker`、`grep-output-truncator`、`tool-output-truncator`、`directory-agents-injector`、`directory-readme-injector`、`empty-task-response-detector`、`think-mode`、`anthropic-context-window-limit-recovery`、`rules-injector`、`background-notification`、`auto-update-checker`、`startup-toast`、`keyword-detector`、`agent-usage-reminder`、`non-interactive-env`、`interactive-bash-session`、`compaction-context-injector`、`thinking-block-validator`、`claude-code-hooks`、`ralph-loop`、`preemptive-compaction`

---

### MCP

Exa、Context7 和 grep.app MCP 默认启用。

- **websearch**：由 [Exa AI](https://exa.ai/) 驱动的实时网络搜索——搜索网络并返回相关内容
- **context7**：获取库的最新官方文档
- **grep_app**：通过 [grep.app](https://grep.app/) 在数百万个公共 GitHub 仓库中进行超快代码搜索

不想要它们？通过 `~/.config/opencode/oh-my-opencode.json` 或 `.opencode/oh-my-opencode.json` 中的 `disabled_mcps` 禁用：

```json
{
  "disabled_mcps": ["websearch", "context7", "grep_app"]
}
```

---

### LSP

OpenCode 提供用于分析的 LSP 工具。Oh My OpenCode 添加了重构工具（重命名、代码操作）。所有 OpenCode LSP 配置和自定义设置（来自 opencode.json）都受支持，加上额外的 Oh My OpenCode 特定设置。

通过 `~/.config/opencode/oh-my-opencode.json` 或 `.opencode/oh-my-opencode.json` 中的 `lsp` 选项添加 LSP 服务器：

```json
{
  "lsp": {
    "typescript-language-server": {
      "command": ["typescript-language-server", "--stdio"],
      "extensions": [".ts", ".tsx"],
      "priority": 10
    },
    "pylsp": {
      "disabled": true
    }
  }
}
```

每个服务器支持：`command`、`extensions`、`priority`、`env`、`initialization`、`disabled`。

---

### 实验性功能

可选的实验性功能，可能在未来版本中更改或删除。谨慎使用。

```json
{
  "experimental": {
    "truncate_all_tool_outputs": true,
    "aggressive_truncation": true,
    "auto_resume": true
  }
}
```

| 选项 | 默认 | 描述 |
| --- | --- | --- |
| `truncate_all_tool_outputs` | `false` | 截断所有工具输出而不仅仅是白名单工具（Grep、Glob、LSP、AST-grep）。工具输出截断器默认启用——通过 `disabled_hooks` 禁用。 |
| `aggressive_truncation` | `false` | 当超过 token 限制时，积极截断工具输出以适应限制。比默认截断行为更激进。如果不足以满足，则回退到总结/恢复。 |
| `auto_resume` | `false` | 从思考块错误或禁用思考违规成功恢复后自动恢复会话。提取最后一条用户消息并继续。 |

> [!warning] 警告
> 这些功能是实验性的，可能导致意外行为。只有在理解其影响后才启用。

---

## 作者札记

安装 Oh My OpenCode。

我纯粹为个人开发使用了价值 24,000 美元 token 的 LLM。尝试了每一个工具，把它们配置到极致。但始终是 OpenCode 胜出。

我遇到的每个问题的答案都融入了这个插件。直接安装使用。如果 OpenCode 是 Debian/Arch，Oh My OpenCode 就是 Ubuntu/[Omarchy](https://omarchy.org/)。

深受 [AmpCode](https://ampcode.com/) 和 [Claude Code](https://code.claude.com/docs/overview) 的影响——我已经将它们的功能移植到这里，通常还有改进。我仍在构建。

毕竟这是 **Open** Code。

享受多模型编排、稳定性和其他工具承诺但无法交付的丰富功能。我会持续测试和更新。因为我是这个项目最执着的用户。

- 哪个模型逻辑最锐利？
- 谁是调试之神？
- 谁写出最好的文字？
- 谁主宰前端？
- 谁拥有后端？
- 哪个模型日常使用最快？
- 其他工具在推出什么新功能？

这个插件是只取其精华。有更好的想法？欢迎 PR。

**不要再为智能体工具的选择而烦恼了。**

我会进行研究，借鉴最好的，然后发布更新。

如果这听起来很傲慢，但如果你有更好的答案，请贡献。欢迎你。

我与这里提到的任何项目或模型没有任何关联。这纯粹是个人实验和偏好。

这个项目 99% 是使用 OpenCode 构建的。我测试了功能——我实际上不太会写正确的 TypeScript。**但我个人审查并大量重写了这份文档，所以放心阅读。**

---

## 警告

- 生产力可能飙升太快。别让你的同事发现。
  - 其实，我会传播这个消息。让我们看看谁会赢。
- 如果你使用或更早版本，一个 OpenCode bug 可能会破坏配置。
  - [修复](https://github.com/sst/opencode/pull/5040) 在 1.0.132 之后合并——使用更新的版本。
  - 有趣的事实：那个 PR 是借助 OhMyOpenCode 的 Librarian、Explore 和 Oracle 设置发现并修复的。

---

## 受到以下专业人士的喜爱

- [Indent](https://indentcorp.com/)
  - 制作 Spray - 网红营销解决方案、vovushop - 跨境电商平台、vreview - AI 电商评论营销解决方案
- [Google](https://google.com/)
- [Microsoft](https://microsoft.com/)

---

## 赞助商

- **Numman Ali** [GitHub](https://github.com/numman-ali) [X](https://x.com/nummanali)
  - 第一位赞助商
- **Aaron Iker** [GitHub](https://github.com/aaroniker) [X](https://x.com/aaroniker)
- **Suyeol Jeon (devxoul)** [GitHub](https://github.com/devxoul)
  - 开启我职业生涯的人，在如何构建出色的智能体工作流方面给了我很深的启发。我学到了很多关于设计伟大系统来构建伟大团队的知识，这些经验对创建这个工具至关重要。
- **Hyerin Won (devwon)** [GitHub](https://github.com/devwon)

*特别感谢 [@junhoyeo](https://github.com/junhoyeo) 制作这张精彩的主图。*




```json
{
  "$schema": "https://opencode.ai/config.json",
  "autoupdate": true,
  "plugin": [
    "oh-my-opencode"
  ],
  "provider": {
    "google-vertexai": {
      "models": {
        "gemini-2.5-flash": {
          "limit": {
            "context": 1048576,
            "output": 65536
          },
          "modalities": {
            "input": [
              "text",
              "image",
              "pdf"
            ],
            "output": [
              "text"
            ]
          },
          "name": "Gemini 2.5 Flash (Gemini CLI)"
        },
        "gemini-2.5-pro": {
          "limit": {
            "context": 1048576,
            "output": 65536
          },
          "modalities": {
            "input": [
              "text",
              "image",
              "pdf"
            ],
            "output": [
              "text"
            ]
          },
          "name": "Gemini 2.5 Pro (Gemini CLI)"
        },
        "gemini-3-flash-preview": {
          "limit": {
            "context": 1048576,
            "output": 65536
          },
          "modalities": {
            "input": [
              "text",
              "image",
              "pdf"
            ],
            "output": [
              "text"
            ]
          },
          "name": "Gemini 3 Flash Preview (Gemini CLI)"
        },
        "gemini-3-pro-preview": {
          "limit": {
            "context": 1048576,
            "output": 65535
          },
          "modalities": {
            "input": [
              "text",
              "image",
              "pdf"
            ],
            "output": [
              "text"
            ]
          },
          "name": "Gemini 3 Pro Preview (Gemini CLI)"
        }
      }
    },
    "ollama": {
      "models": {
        "glm-4.7-flash": {
          "_launch": true,
          "name": "glm-4.7-flash"
        }
      },
      "name": "Ollama (local)",
      "npm": "@ai-sdk/openai-compatible",
      "options": {
        "baseURL": "http://127.0.0.1:11434/v1"
      }
    },
    "zai-coding-plan": {
      "models": {
        "glm-4.5-air": {
          "limit": {
            "context": 131072,
            "output": 8192
          },
          "modalities": {
            "input": [
              "text"
            ],
            "output": [
              "text"
            ]
          },
          "name": "GLM-4.5-Air"
        },
        "glm-4.6v": {
          "limit": {
            "context": 131072,
            "output": 8192
          },
          "modalities": {
            "input": [
              "text",
              "image",
              "video"
            ],
            "output": [
              "text"
            ]
          },
          "name": "GLM-4.6V"
        },
        "glm-4.7": {
          "limit": {
            "context": 131072,
            "output": 8192
          },
          "modalities": {
            "input": [
              "text"
            ],
            "output": [
              "text"
            ]
          },
          "name": "GLM-4.7"
        },
        "glm-4.7-flash": {
          "limit": {
            "context": 131072,
            "output": 8192
          },
          "modalities": {
            "input": [
              "text"
            ],
            "output": [
              "text"
            ]
          },
          "name": "GLM-4.7-Flash"
        },
        "glm-5": {
          "limit": {
            "context": 131072,
            "output": 8192
          },
          "modalities": {
            "input": [
              "text"
            ],
            "output": [
              "text"
            ]
          },
          "name": "GLM-5"
        }
      }
    }
  }
}
```