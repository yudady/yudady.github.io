---
title: "LLM Wiki 集成方案：dtg-ob + Karpathy Pattern"
tags:
  - status/active
  - area/distill
  - type/doc
created: 2026-04-06
---

# LLM Wiki 集成方案：dtg-ob + Karpathy Pattern

> [!info] 背景
> 参考 [Karpathy LLM Wiki](https://gist.github.com/karpathy/442a6bf555914893e9891c11519de94f) 的三层架构，在现有 CODE Method 基础上叠加知识编译层，实现知识复利。

## 现状快照

| 区域 | 文件数 | 说明 |
|------|--------|------|
| 000-Capture | 4 | Inbox 1 + Clippings 3 + To-Archive 0 |
| 100-Organize | 68 | 项目/任务/日报告 |
| 200-Distill | 65 | 知识沉淀（AI工具24、编程10、DevOps6、Obsidian4、学习5） |
| 300-Express | 48 | MOC 6 + 模板 42 |
| 400-Archive | 190 | 已归档 |

**核心问题**：
- 200-Distill 的 65 个文件彼此独立，无交叉引用
- 400-Archive 的 190 个文件基本"死亡"，不会再次被利用
- MOC 页面的链接指向旧路径（`004-ai-tools/`），多数已失效
- 无矛盾检测、无知识合成能力

---

## 设计原则

1. **归档层不动** — CODE 结构 + vault-organizer 技能保持不变
2. **在 Distill 层叠加 Wiki** — 200-Distill 成为知识编译区
3. **Archive 复活** — 归档文件作为 Raw Source 可被重新利用
4. **渐进式** — 分三期实施，每期独立可用

---

## 目录结构变更

### 新增目录（不动现有目录）

```
200-Distill/
├── wiki/                    # [新] LLM Wiki 核心区
│   ├── index.md             # [新] 全 wiki 索引（实体/概念/源文件目录）
│   ├── log.md               # [新] 编译日志（append-only）
│   ├── entities/            # [新] 实体页（人、项目、产品、技术）
│   │   ├── Claude-Code.md
│   │   ├── dtg-pay.md
│   │   ├── Obsidian.md
│   │   └── ...
│   ├── concepts/            # [新] 概念页（模式、方法论、原理）
│   │   ├── RAG-vs-Compiled-Wiki.md
│   │   ├── CircuitBreaker模式.md
│   │   └── ...
│   └── comparisons/         # [新] 对比分析（LLM 输出的高质量合成）
│       ├── LLM-Wiki-vs-CODE-Method.md
│       └── ...
├── 200-AI-Tools/            # [存] 不动
├── 210-DevOps/              # [存] 不动
├── 220-Programming/         # [存] 不动
├── 230-Payment-Systems/     # [存] 不动
├── 240-Obsidian/            # [存] 不动
└── 250-Learning/            # [存] 不动
```

### Raw Source 映射

Karpathy 的 Raw Sources 对应 dtg-ob 的现有目录：

| Karpathy 层 | dtg-ob 对应 | 是否不可变 |
|-------------|-------------|-----------|
| Raw Sources | 000-Capture/001-Clippings/（原始剪藏） | 是，不修改 |
| Raw Sources | 200-Distill/ 各子目录（已归档知识文件） | 是，wiki 不修改原文 |
| Raw Sources | 400-Archive/（归档文件） | 是，但 wiki 可引用 |
| Wiki | 200-Distill/wiki/ | LLM 全权维护 |
| Schema | 900-System/wiki-schema.md | [新] 人 + LLM 共同维护 |

---

## 三期实施计划

### Phase 1: 基础设施（1-2 天）

**目标**：搭建骨架，跑通 Ingest 流程

1. 创建目录结构 `200-Distill/wiki/` 及子目录
2. 创建 `900-System/wiki-schema.md` — 定义 wiki 约定
   - 页面格式模板（frontmatter、正文结构）
   - entity 页标准字段（名称、类型、相关项目、关键链接）
   - concept 页标准字段（定义、核心思想、应用场景、相关实体）
   - comparison 页标准字段（对比对象、维度、结论）
   - Ingest 流程定义：读取源文件 → 提取关键信息 → 创建/更新 entity/concept 页 → 更新 index.md → 写 log.md
3. 创建 `200-Distill/wiki/index.md` — 初始索引（空壳）
4. 创建 `200-Distill/wiki/log.md` — 初始日志
5. **创建 Hermes skill**：`llm-wiki-ingest` — 摄入单个源文件到 wiki
   - 输入：源文件路径
   - 输出：创建/更新的 wiki 页面列表 + log 条目
6. 手动跑 2-3 个文件验证流程

**Schema 示例**（900-System/wiki-schema.md 核心）：

```yaml
# 页面格式
frontmatter:
  required: [title, type, created, updated, sources]
  type_options: [entity, concept, comparison, summary]

# entity 页模板
# ---
# title: Claude Code
# type: entity
# entity_type: tool  # tool/project/person/product/technology
# created: 2026-04-06
# updated: 2026-04-06
# sources:
#   - path: 200-Distill/200-AI-Tools/claude/xxx.md
#   - path: 200-Distill/220-Programming/Claude-Code-实践指南.md
# tags: [claude, ai-coding, cli]
# ---
# # Claude Code
# ## 概述
# ## 核心特性
# ## 相关概念
# ## 相关实体
# ## 关键源文件
```

### Phase 2: 批量编译 + 现有内容整合（2-3 天）

**目标**：把现有 200-Distill 的 65 个文件编译进 wiki

1. **批量 Ingest 200-Distill 子目录**
   - 200-AI-Tools/（24 文件）→ 提取 entity 页：Claude Code、Gemini、Ollama、Cline、MCP 等
   - 220-Programming/（10 文件）→ 提取 concept 页 + 更新相关 entity
   - 210-DevOps/（6 文件）→ 提取 concept 页
   - 250-Learning/（5 文件）→ 提取 concept 页
   - 230-Payment-Systems/、240-Obsidian/（5 文件）→ 补充 entity

2. **修复 MOC 页面**
   - 现有 MOC 页面的 wikilink 大量指向旧路径（`004-ai-tools/`）
   - 修复为 `200-Distill/` 路径 或 `200-Distill/wiki/entities/` 路径
   - 评估是否保留现有 MOC，还是用 wiki/index.md 替代

3. **建立交叉引用**
   - entity 页之间互链（Claude Code ↔ MCP ↔ Obsidian）
   - entity 页链接回源文件（`[[200-Distill/200-AI-Tools/xxx|源文件]]`）
   - concept 页链接相关 entity

4. **创建 Hermes skill**：`llm-wiki-lint` — wiki 健康检查
   - 检测孤儿页面（无 inbound links）
   - 检测重复 entity
   - 检测过时信息（源文件比 wiki 更新时标记）
   - 建议缺失的交叉引用

### Phase 3: 持续运营（持续）

**目标**：让 wiki 成为日常工作的知识层

1. **整合到现有归档流程**
   - vault-organizer 归档完成后，自动触发 wiki Ingest
   - 新增文件到 001-Clippings/ 后，手动或 cron 触发编译

2. **整合到 Daily Note**
   - 每次问答/分析后，有价值的内容写入 wiki（不只在 chat 中消失）
   - 例如：排查一个 bug 后，根因分析写入 wiki/concepts/ 并链接到相关 entity

3. **Archive 复活**
   - 400-Archive 中的文件作为 Raw Source，可被 wiki 引用
   - 例如：归档的旧 bug 报告中发现的新知识，编译进 wiki

4. **定期 Lint**
   - cron 每周运行一次 llm-wiki-lint
   - 输出健康报告到 Daily Note 或单独文件

5. **评估搜索需求**
   - 100+ wiki 页面后，index.md 可能不够用
   - 考虑引入 `qmd` 或简单的 grep 搜索作为 Hermes skill

---

## 不做的事

- **不重命名现有文件** — 只在 wiki 层建立链接
- **不修改现有 frontmatter 体系** — wiki 有自己的 schema，与 CODE 标签体系并行
- **不删除 400-Archive** — 归档文件保留，wiki 只引用不移动
- **不引入数据库/向量检索** — 纯 markdown + index.md，保持简单
- **不自动化所有写入** — Ingest 仍需人确认（至少初期），避免 wiki 被低质内容污染

---

## 风险与缓解

| 风险 | 缓解措施 |
|------|---------|
| wiki 页面与源文件内容重复 | wiki 只提取结构化知识，源文件保留完整内容 |
| wiki 维护成本高 | Phase 1 先手动验证，再自动化；lint 帮助发现堆积 |
| wiki 内容质量不稳定 | Ingest 时人审关键 entity 页；初期控制 Ingest 范围 |
| MOC 和 wiki/index.md 功能重叠 | Phase 2 评估后决定：合并或分工（MOC=人浏览，index=LLM 导航） |

---

## 成功指标

- [ ] Phase 1：3 个源文件成功编译为 wiki 页面，index.md 可用
- [ ] Phase 2：200-Distill 80%+ 文件有对应 wiki entity/concept
- [ ] Phase 2：MOC 链接修复，0 个 broken link
- [ ] Phase 3：能通过 wiki index 回答跨文件问题（如"CircuitBreaker 在 dtg-pay 的哪些场景用到"）
- [ ] Phase 3：每周 lint 无孤儿页面 > 5 个
