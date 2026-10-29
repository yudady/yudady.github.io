---
title: claude-certified-architect — CCA-F 认证备考资料库
aliases: ["Claude Certified Architect Foundations", "CCA-F 备考", "Claude 认证架构师"]
tags:
  - claude
  - anthropic
  - certification
  - exam-prep
  - mcp
  - agent-sdk
  - status/active
  - type/doc
source:
  - "https://github.com/paullarionov/claude-certified-architect"
  - "https://anthropic.skilljar.com/claude-certified-architect-foundations-access-request"
  - "https://loginov-rocks.medium.com/build-as-anthropic-claude-certified-architect-foundations-cca-f-04fcd371b8c3"
author: Pavel Larionov（paullarionov）
created: 2026-10-29
updated: 2026-10-29
description: 5.1k stars 开源备考库：16 语言 CCA-F 学习指南 + 60 题交互式模拟考 HTML + Anki 卡组，GitHub Actions 自动构建 PDF/EPUB
level: intermediate
stars: 4
---

# claude-certified-architect — CCA-F 认证备考资料库

> 社区维护的 **Claude Certified Architect — Foundations（CCA-F）** 认证备考资料库。核心资产是一份基于官方考试指南整理的多语言学习指南（含 13 章理论基础 + 60 道带解析模拟题）、交互式 HTML 模拟考场和 Anki 卡组。对中文学习者来说，`guide_zh.md` 是目前少见的完整中文版 CCA-F 知识体系梳理。

> [!info] 基本信息
> - **仓库**：https://github.com/paullarionov/claude-certified-architect
> - **Stars / Forks**：5,133 / 1,223（快照：2026-10-29）
> - **作者**：Pavel Larionov（GitHub: paullarionov），社区个人项目，非 Anthropic 官方
> - **主语言**：HTML（题库页面）；内容本体为 Markdown
> - **协议**：无 License（fork/复用时注意版权边界）
> - **创建**：2026-03-14；最近 push：2026-10-02
> - **仓库类型**：知识集合（无 src，纯文档 + 构建脚本）

---

## 目录

1. [[#核心解决的问题]]
2. [[#仓库资产地图]]
3. [[#考试速查表（官方口径）]]
4. [[#五大领域与知识体系]]
5. [[#运作机制：一条 Markdown 主干的自动构建管道]]
6. [[#使用路径建议]]
7. [[#验证与勘误记录]]
8. [[#相关资料]]

---

## 核心解决的问题

CCA-F 认证有两个现实门槛，这个仓库逐一对冲：

| 门槛 | 仓库的对冲 |
|------|-----------|
| 考试**仅对 Anthropic Partner Network 成员开放**，需企业邮箱申请（前 5,000 名合作企业员工免费，之后收费） | 备考资料完全开源，未拿到考试资格也能先系统化学习同一套知识体系 |
| 官方只有一份约 40 页英文 exam guide handout，无官方题库 | 把考点拆成 13 章理论 + 5 领域注释 + 60 题模拟（带解析），并翻译成 16 种语言 |

**判断决策树**：什么人该用这个仓库？

```
你要什么？
├─ 只想考 CCA-F 拿证
│   ├─ 有 Partner Network 资格 → 官方 course + 本仓库刷题（中文走 guide_zh.md）
│   └─ 无资格 → 先用 skilljar 申请入口排队，同时用本仓库打基础
├─ 不考证，只想系统学 Claude 工程实践
│   └─ 本仓库 = 一份按「考试大纲」组织好的 Claude 工程规范，
│      比散读官方文档更成体系（Agent SDK / MCP / Claude Code / 提示工程全覆盖）
└─ 团队培训/拉齐认知
    └─ 直接发对应语言 PDF/EPUB + HTML 模拟考链接，零成本教案
```

---

## 仓库资产地图

```
claude-certified-architect/
├── guide_{en,zh,zh-tw,ja,ko,ru,...}.md   # 16 语言学习指南（源文件，170-360KB/份）
├── claude-certified-architect-guide-uk.md # 乌克兰语完整版（343KB，最大单文件）
├── pdf/    guide_*.pdf                    # Actions 自动构建
├── epub/   guide_*.epub                   # Actions 自动构建
├── practical_test_{en,es,fr,ja,ko,pt-br,ru,zh}.html  # 8 语言交互式模拟考场
├── anki/   practical_test_*.apkg          # 8 语言 Anki 卡组（140-295KB/份）
├── utils/
│   ├── build_practical_test_html.py       # 题库 JSON → 单文件 HTML 考场
│   └── build_anki_deck.py                 # 题库 → genanki 卡组
└── .github/workflows/                     # markdown-to-pdf / markdown-to-epub / html-to-anki
```

| 资产 | 语言数 | 内容 | 刷新方式 |
|------|-------|------|---------|
| 学习指南 md | 16 | 13 章理论 + 5 领域注释 + 12 示例题 + 60 模拟题 | PR 合入即更新 |
| PDF / EPUB | 14 | 同上，排版版 | `markdown-to-pdf.yml` / `markdown-to-epub.yml` 自动构建 |
| HTML 模拟考场 | 8 | 60 题，答题/判分/解析交互 | PR 后 `html-to-anki.yml` 等流水线 |
| Anki `.apkg` | 8 | 同一套 60 题（Scenario/Situation/Question/4 选项/解析 九字段模型） | `utils/build_anki_deck.py`（genanki） |

**质量信号**：中文版 `guide_zh.md` 3,425 行 / 170KB，含 60 道模拟题 + 12 道带解析示例题，与 HTML 考场题量（60）一致——不是README 撑门面的空壳翻译，各语言版本是同一题库的完整本地化。

---

## 考试速查表（官方口径）

以下为官方 Skilljar / Pearson VUE 页面口径（2026-10 查证），仓库指南与之一致的部分单独标注：

| 参数 | 值 | 与仓库指南一致？ |
|------|-----|----------------|
| 题量 / 时长 | 60 题 / 120 分钟（约 135 分钟坐席时间） | ✅（指南给出题量口径的补充） |
| 题型 | 单选（4 选 1），闭卷，线上监考（Pearson VUE） | ✅ |
| 评分 | 100–1000 分制，**及格线 720**，答错不倒扣 | ✅ |
| 场景制 | 8 个场景随机抽 4 个出题 | ✅ |
| 考试语言 | **英语、日语** | ⚠️ 16 种是学习材料语言，不是考试语言 |
| 价格 | **$125 USD**/次，补考重收全款 | ⚠️ README 写 GA 价 $99，官方页现为 $125 |
| 资格 | Anthropic Partner Network 成员，验证过的合作企业邮箱注册 | ✅ |
| 有效期 | 徽章迁移至 Credly，有效期 **12 个月** | 指南未提及（来自考经） |

**五大领域权重**（占比直接决定复习时间分配）：

```
领域1 代理架构与编排            27% ██████████████
领域3 Claude Code 配置与工作流   20% ██████████
领域4 提示工程与结构化输出       20% ██████████
领域2 工具设计与 MCP 集成       18% █████████
领域5 上下文管理与可靠性        15% ███████▌
```

> 领域 1（27%）独占四分之一强：代理循环、协调者-子代理编排、`Task` 工具、Agent SDK 钩子、任务分解、会话恢复——这部分是 CCA-F 与「会调 API」之间的真正分水岭。

**8 个考试场景**：客户支持代理 / Claude Code 生成代码 / 多代理研究系统 / 开发者生产力工具 / CI 中的 Claude Code / 结构化数据提取 / 对话式 AI 架构 / Agentic AI 工具。前 7 个场景指南均已覆盖，**场景 8 内容缺失**（作者明确征求考生贡献）——备考时这部分只能靠官方文档自行补齐。

---

## 五大领域与知识体系

指南正文分两篇：**第一部分·理论基础**（13 章，按技术栈铺开）→ **第二部分·考试领域注释**（把 13 章内容重新映射到 5 个考试领域）。这个「双索引」结构是它比普通教程好用的关键：学按栈学，考前按领域权重复习。

| 章 | 主题 | 对应领域 |
|----|------|---------|
| 1 | Claude API：请求结构、消息角色、`stop_reason`、系统提示、上下文窗口 | 领域 4/5 |
| 2 | 工具与 `tool_use`：工具定义、`tool_choice`、JSON Schema 结构化输出、语法 vs 语义错误 | 领域 2/4 |
| 3 | Agent SDK：代理循环、`AgentDefinition`、轮辐式协调、`Task` 工具、生命周期钩子 | **领域 1** |
| 4 | MCP：服务器、配置、`isError` 标志、资源 | 领域 2 |
| 5 | Claude Code：CLAUDE.md 层级、`@path` 导入、`.claude/rules/`、斜杠命令与技能、规划模式、`/compact`、CI/CD CLI、`fork_session` | 领域 3 |
| 6 | 提示工程：少样本、显式标准、提示链、访谈模式、验证重试、自我纠正 | 领域 4 |
| 7 | Message Batches API：`custom_id`、失败处理、SLA 规划 | 领域 4 |
| 8 | 任务分解：固定管道 vs 动态自适应、多轮代码审查 | 领域 1 |
| 9 | 升级与人工参与：升级时机、结构化交接协议、置信度校准 | 领域 5 |
| 10 | 多代理错误处理：错误类别、反模式、结构化子代理错误、覆盖注释 | 领域 5 |
| 11 | 生产级上下文管理：关键事实抽取、裁剪工具返回、草稿文件、子代理隔离、崩溃恢复 | **领域 5** |
| 12 | 溯源保存：归因丢失、冲突数据处理、日期标注 | 领域 5 |
| 13 | Claude Code 内置工具：Read/Write/Edit/Bash/Grep/Glob 选择参考、渐进式调查 | 领域 2/3 |

**题目判定哲学**（从解析反推的出题逻辑，应试时比死记知识点更有用）：

1. **程序化约束 > 提示词约束**——能用 JSON Schema / 钩子 / 代码校验强制的，绝不靠「请在提示里注意」
2. **职责分离 + 不阻塞**——子代理完成本职、保留证据、把裁决权上交拥有更多上下文的协调者
3. **归因明确**——冲突数据不悄悄择一，而是带来源标注上抛

样题（模拟测试第 1 题，场景：多代理研究系统）：

> 文档分析代理发现两个可信来源对同一指标直接矛盾（政府报告 40% vs 行业分析 12%）。该怎么做？
>
> - A) 启发式选一个「最可能对」的数继续，脚注提及差异
> - B) 两个数都放进输出，不标注冲突，让综合代理自己看着办
> - C) 立即停下升级给协调者，等裁决再继续
> - **D) ✅ 两个数都保留 + 明确注释冲突与来源归因，完成后交协调者调合**
>
> 解析：D 保持职责分离——分析代理不阻塞地完成本职，保留双值与清晰归因，把调和工作交给上下文更全的协调者。（C 的错误在于「能完成却不完成」，A/B 的错误在丢失或模糊了归因。）

---

## 运作机制：一条 Markdown 主干的自动构建管道

```
                    PR 合入（翻译/勘误）
                          │
                          ▼
             guide_<lang>.md （唯一人工维护的源）
                          │
        ┌─────────────────┼──────────────────┐
        ▼                 ▼                  ▼
 markdown-to-pdf.yml  markdown-to-epub.yml  html-to-anki.yml
 （pdf-style.css      （epub-style.css      （题库抽取 → JSON）
   + pre-to-lines.lua）  + 样式）              │
        ▼                 ▼                  ▼
    pdf/guide_*.pdf   epub/guide_*.epub   ┌────┴────┐
                                          ▼         ▼
                              build_practical_test  build_anki_deck.py
                              _html.py              （genanki → .apkg）
                              （→ 单文件交互 HTML）
```

设计上只有 `.md` 源文件需要人工维护，PDF/EPUB/HTML/Anki 全部是 CI 产物——贡献翻译不需要会任何构建工具链。Anki 卡组用固定 `MODEL_ID` + 按语言派生 `deck_id`（`_deck_id(lang)`），保证重复构建不产生重复卡组。

题库字段模型（`utils/build_anki_deck.py`）：

```python
fields = ['Scenario', 'Situation', 'Question',
          'OptionA', 'OptionB', 'OptionC', 'OptionD',
          'Correct', 'Explanation']
# 正面: 场景 + 情境 + 问题 + A-D 选项
# 背面: 正确答案 + 为什么选它（对应 guide 内「为什么选X」解析段）
```

**已知缺口**：`build_practical_test_html.py` 在仓库根目录调用 `extract_question.py` 抽题，但根目录当前没有这个文件——HTML/Anki 均为预构建产物，本地想重新生成需先补回该脚本（或从 git 历史找回）。

---

## 使用路径建议

```
第一步（1-2 天）
  读 guide_zh.md 的「简介→考试格式→5领域→8场景」+ 第二部分领域注释
  → 建立考点全景，标记薄弱领域
        │
第二步（按权重分配）
  第一部分 13 章精读：领域1(27%) 第3/8/9/10/11章优先，
  其次领域3(20%) 第5章、领域4(20%) 第6/7章
        │
第三步（1-2 天）
  practical_test_zh.html 限时模拟（60 题/120 分钟节奏）
  → 错题回指南对应章节
        │
第四步（碎片时间）
  anki/practical_test_zh.apkg 导入 Anki 刷卡
        │
有资格考证？──→ skilljar 申请入口（合作企业邮箱）→ Pearson VUE 预约
无资格？    ──→ 知识体系本身已完整闭环，当工程规范读
```

配套免费资源（README 整理的 **Anthropic Academy 13 门免费课程**）：与 5 领域直接对口的是 *Building with the Claude API*（领域 2/4）、*Intro to MCP* + *MCP: Advanced Topics*（领域 2）、*Claude Code in Action*（领域 3）、*Introduction to Agent Skills*（领域 3）。README 逐条带 skilljar 链接，可直接按领域选课。

---

## 验证与勘误记录

| 视频外声称（README/传闻） | 验证结果 | 来源 |
|--------------------------|---------|------|
| 考试 GA 价格 $99 | ⚠️ 过时/口径分歧：官方 Skilljar 现价 **$125**/次，补考同价 | skilljar 访问申请页、claudecertifiedarchitects.com（2026-10 查证） |
| 16 种语言学习材料 | ✅ 实际更多：根目录 16 份 guide md + 独立乌克兰语完整版；PDF/EPUB 14 语言；HTML/Anki 8 语言 | GitHub contents API（2026-10-29） |
| 考试支持多语言 | ❌ 误读风险：官方考试语言仅**英语、日语**；16 语言只是材料本地化 | Anthropic Skilljar 官方页 |
| 前 5,000 名合作企业员工免费 | ✅ 与官方一致（ Partner Network 限制 + 企业邮箱注册） | README + skilljar |
| 自动化生成 PDF/EPUB | ✅ `.github/workflows/` 含 markdown-to-pdf / markdown-to-epub / html-to-anki 三个流水线 | GitHub API 目录列举 |
| Anki 卡组可用 | ✅ `anki/` 下 8 语言 .apkg 实存（zread 3 级目录树未显示，属工具截断非仓库缺失） | GitHub contents API |
| 模拟题覆盖真题场景 | ✅ 中文指南 60 模拟题 + 12 示例题；HTML 嵌入 60 题（与官方题量一致）；但**场景 8 指南明示缺失** | guide_zh.md 全文 grep |
| HTML 构建脚本可复现 | ⚠️ 依赖的 `extract_question.py` 不在仓库根目录，本地重建 HTML/Anki 前需找回 | build_practical_test_html.py L24-32 + 根目录清单 |
| 无 License | ✅ 确认无（GitHub API license=null），复用翻译内容注意版权 | GitHub API |

---

## 相关资料

- [仓库：paullarionov/claude-certified-architect](https://github.com/paullarionov/claude-certified-architect)
- [官方考试入口申请（Skilljar）](https://anthropic.skilljar.com/claude-certified-architect-foundations-access-request) — 价格/题量/时长官方口径
- [Claude Certification Program — Pearson VUE](https://www.pearsonvue.com/us/en/anthropic.html) — 考试预约与监考
- [CCA-F 徽章（Credly）](https://www.credly.com/org/anthropic/badge/claude-certified-architect-foundations)
- [考经：Build as Anthropic — CCA-F（Danila Loginov, 2026-07）](https://loginov-rocks.medium.com/build-as-anthropic-claude-certified-architect-foundations-cca-f-04fcd371b8c3) — 六年架构师视角的完整备考记录；文中确认徽章已迁 Credly、有效期 12 个月。注：作者与仓库作者是两个人
- [考经：r/claudeskills 通过贴](https://www.reddit.com/r/claudeskills/comments/1x1o45h/i_passed_anthropics_claude_architect_foundations) — 印证 60 题/2 小时/闭卷/监考

## 相关笔记

- （vault 内暂无同主题笔记，本文为首篇 Claude 认证方向笔记）

---

*文档生成时间：2026-10-29*
*基于 paullarionov/claude-certified-architect（push 2026-10-02）+ Anthropic Skilljar 官方页（2026-10 查证）*
