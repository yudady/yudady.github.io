---
in: null
title: "Claude Code Skills：AI 代理的未來"
aliases:
- AI Agent Skills
- Anthropic Skills
- "Claude Code Skills：AI 代理的未來"
- Claude Skills
tags:
- Claude
- MCP
- "AI代理"
- "智能"
- AI
- Claude-Code
- AI-Agent
- "专业知识"
- Skills
source:
- https://docs.anthropic.com/en/docs/claude-code/skills
- https://modelcontextprotocol.io/docs/concepts/architecture
- https://www.youtube.com/watch?v=wqH1hTkA6qg
author: AI Workshop
created: 2026-03-07 15:30
updated: 2026-03-07 11:44
description: "這份筆記整理了 Claude Code Skills 的核心概念、架構設計、實踐方法以及與 AI Agents 的對比分析。Skills 是 Anthropic 提出的 AI 能力擴展新範式，代表了從「構建獨立 Agent」到「按需注入專業知識」的重大轉變。"
level: intermediate
stars: 5
category: "技术"
summary: "探讨AI代理的未来，介绍Skills的概念及其优势"

---
# Claude Code Skills：AI 代理的未來

## 目錄

- [核心理念](#核心理念)
- [什麼是 Skills？](#什麼是-skills)
- [Skills 架構設計](#skills-架構設計)
- [漸進式披露機制](#漸進式披露機制)
- [Skills vs AI Agents 對比](#skills-vs-ai-agents-對比)
- [MCP 與 Skills 的協作](#mcp-與-skills-的協作)
- [創建 Skills 實踐指南](#創建-skills-實踐指南)
- [實際案例：GEO 審計系統](#實際案例geo-審計系統)
- [社群反饋與應用數據](#社群反饋與應用數據)
- [最佳實踐建議](#最佳實踐建議)
- [參考資源](#參考資源)

---

## 核心理念

### 為什麼 Anthropic 說「停止構建 AI Agents」？

現在每個人都在構建 AI agents——每家初創公司、每家科技公司、每個開發者都在談論。但 Anthropic 提出了一個不同的觀點：**未來不是更多的 agents，而是 Skills**。

### 關鍵洞見：智能 ≠ 專業知識

> **智能不等於專業知識。**

想像你需要有人幫你報稅：

| 選項 | 描述 | 結果 |
|------|------|------|
| **天才** | 智商超高，可從第一性原理弄清楚任何事，但從未做過報稅 | ❌ 從頭研究稅法 |
| **稅務專家** | 做過成千上萬份報稅表，知道每條規則、每個邊緣情況 | ✅ 高效完成 |

**這正是今天 AI agents 的問題**：它們是優秀的通才，但沒有預裝特定的專業知識。

### 傳統做法的困境

```
每個用例構建一個單獨的 agent
├── 稅務 agent（自定義工具 + 架構）
├── 法律 agent（自定義工具 + 架構）
├── 營銷 agent（自定義工具 + 架構）
└── ...無法擴展 ❌
```

### Skills 的解決方案

```
一個通用的 agent + 按需注入專業知識
├── Skills（稅務領域）
├── Skills（法律領域）
├── Skills（營銷領域）
└── 可無限擴展 ✅
```

---

## 什麼是 Skills？

### 定義

**Skills 是可重用的模塊化能力擴展包**，包含：
- 指令文檔（SKILL.md）
- 腳本
- 參考文檔
- 模板
- 示例

### 核心特點

| 特點 | 說明 |
|------|------|
| **模型自主調用** | Claude 根據 description 自動決定何時使用 |
| **漸進式加載** | 不一次性加載所有內容，按需拉取 |
| **可移植性** | 遵循 Agent Skills 開放標準 |
| **Git 共享** | 可通過 git 在團隊間共享專業知識 |
| **兩種調用方式** | 自動觸發 或 手動 `/skill-name` |

### Skills 的三種類型

| 類型 | 存儲位置 | 用途 |
|------|----------|------|
| **Personal** | `~/.claude/skills/` | 個人工作流程、實驗性 Skills |
| **Project** | `.claude/skills/` | 團隊工作流程、項目特定知識 |
| **Plugin** | 插件包中 | 隨插件安裝自動可用 |

---

## Skills 架構設計

### 目錄結構

```
my-skill/
├── SKILL.md           # 必需：主要指令文件（入口點）
├── reference.md       # 可選：詳細參考文檔
├── examples/
│   └── sample.md      # 可選：示例輸出
├── scripts/           # 可選：可執行代碼
│   └── helper.py
├── templates/         # 可選：模板文件
│   └── report.md
└── assets/            # 可選：資源（字體、圖標等）
```

### SKILL.md 格式規範

```markdown
---
name: my-skill-name
description: 完整描述此 skill 的功能和何時使用
argument-hint: [參數提示]
disable-model-invocation: false
user-invocable: true
allowed-tools: Read, Grep, Glob, Bash
model: claude-sonnet-4-20250514
context: fork
agent: Explore
---

# Skill 標題

這裡是 Claude 在調用 skill 時遵循的指令...

## 參數使用
- 所有參數: $ARGUMENTS
- 第一個參數: $0
- 第二個參數: $1

## 動態上下文注入
當前 PR 差異: !`gh pr diff`
```

### YAML Frontmatter 字段參考

| 字段 | 必需 | 描述 |
|------|------|------|
| `name` | 否 | Skill 名稱，小寫字母、數字、連字符（最多 64 字符） |
| `description` | **推薦** | 功能描述及觸發條件（Claude 用此判斷何時應用） |
| `argument-hint` | 否 | 自動完成時的參數提示 |
| `disable-model-invocation` | 否 | 設為 `true` 防止 Claude 自動加載 |
| `user-invocable` | 否 | 設為 `false` 從 `/` 菜單隱藏 |
| `allowed-tools` | 否 | 限制可用的工具 |
| `model` | 否 | 指定使用的模型 |
| `context` | 否 | 設為 `fork` 在分叉上下文中運行 |
| `agent` | 否 | 指定 subagent 類型 |

---

## 漸進式披露機制

### 什麼是漸進式披露？

**Claude 不會一次性將每個 skill 加載到內存中。**

```
┌─────────────────────────────────────────────────────────────┐
│                    漸進式加載流程                            │
└─────────────────────────────────────────────────────────────┘

1. 啟動時
   ┌─────────┐  ┌─────────┐  ┌─────────┐
   │ Skill A │  │ Skill B │  │ Skill C │
   │ (標題)  │  │ (標題)  │  │ (標題)  │
   └─────────┘  └─────────┘  └─────────┘
        ↓
   只讀取 description（~100 tokens）

2. 匹配時
   ┌─────────────────────────────────┐
   │        Skill A                  │
   │  完整 SKILL.md 內容             │
   │  - 詳細指令                     │
   │  - 工作流程                     │
   │  - 規則                         │
   └─────────────────────────────────┘
        ↓
   加載完整指令

3. 需要時
   ┌─────────────────────────────────┐
   │     參考文檔 / 腳本             │
   └─────────────────────────────────┘
        ↓
   按需加載資源
```

### Token 消耗對比

| 層級 | 發生什麼 | Token 消耗 |
|------|----------|-----------|
| **元數據層** | 只加載 description | ~100 tokens |
| **指令層** | 匹配時加載完整 SKILL.md | 中等 |
| **資源層** | 明確需要時才加載參考文檔 | 按需 |
| **執行層** | 腳本直接運行不加載到上下文 | **零成本** |

### 與傳統 Prompt 的對比

| 方式 | 行為 | 問題 |
|------|------|------|
| **Prompt 塞入** | 所有指令一次性塞入上下文 | 快速變得混亂 |
| **Skills** | 有組織、模塊化，按需拉取 | 保持清晰 ✅ |

---

## Skills vs AI Agents 對比

### 對比表格

| 特性 | Skills | Agents (Subagents) |
|------|--------|-------------------|
| **定義** | 可重用的指令模板 | 獨立運行的專用 AI 助手 |
| **執行上下文** | 主對話上下文 | 獨立上下文窗口 |
| **自主性** | 被動 - 等待調用 | 主動 - 獨立執行多步驟任務 |
| **狀態管理** | 無狀態 | 可保持狀態和持久化記憶 |
| **上下文隔離** | 不隔離 | 完全隔離 |
| **並發執行** | 不支持 | 支持後台運行和並行執行 |
| **配置複雜度** | 簡單 | 較複雜 |

### 決策流程圖

```
任務需要執行嗎？
├── 需要 → 任務會產生大量輸出嗎？
│   ├── 是 → 使用 Agent（上下文隔離）✅
│   └── 否 → 需要工具限制嗎？
│       ├── 是 → 使用 Agent（獨立權限）
│       └── 否 → 使用 Skill（簡單快速）✅
└── 不需要（僅參考） → 使用 Skill（知識庫）✅
```

### 使用 Skills 當

- ✅ 需要參考內容（代碼規範、風格指南）
- ✅ 需要快速簡單的提示詞模板
- ✅ 需要在主對話中持續可用
- ✅ 任務較輕，不會產生大量輸出

### 使用 Agents 當

- ✅ 任務會產生大量輸出（運行測試、分析日誌）
- ✅ 需要工具限制（只讀操作）
- ✅ 需要並行執行多個獨立研究
- ✅ 需要「防火牆」隔離操作
- ✅ 需要持久化記憶

---

## MCP 與 Skills 的協作

### MCP (Model Context Protocol) 架構

```
┌─────────────────────────────────────────────────────┐
│                    Host (主機)                       │
│   LLM 應用程序（Claude Desktop / IDE）              │
│                                                      │
│   ┌─────────────┐     ┌─────────────┐               │
│   │   Client    │ ←→  │   Server    │               │
│   │  (客戶端)    │     │  (服務器)    │               │
│   └─────────────┘     └─────────────┘               │
│                            ↓                         │
│                    Resources/Tools/Prompts           │
└─────────────────────────────────────────────────────┘
```

### MCP 組件說明

| 組件 | 說明 |
|------|------|
| **Hosts** | LLM 應用程序，發起連接 |
| **Clients** | 維護與服務器的 1:1 連接 |
| **Servers** | 提供上下文、工具和提示 |

### Skills + MCP 的關係

```
┌─────────────────────────────────────────────────────────────┐
│                      擴展能力層次                           │
└─────────────────────────────────────────────────────────────┘

Claude Code
    │
    ├── 內置工具（Read, Write, Bash...）
    │
    ├── Skills (SKILL.md)
    │   └── 本地專業知識、工作流程
    │
    └── MCP Servers
        ├── 數據庫訪問（PostgreSQL, MySQL）
        ├── API 集成（GitHub, Slack, Notion）
        └── 自定義工具（400+ 企業級服務器）
```

### 形象比喻

| 組件 | 比喻 |
|------|------|
| **MCP** | AI 的「手」— 讓 AI 能夠伸出手與世界互動 |
| **Skills** | AI 的「經驗」— 告訴 AI 實際上用這些做什麼 |

### 實際應用流程

```
用戶請求：「從 Google Drive 讀取設計文檔並生成代碼」

Claude Code 執行：
1. 自動激活相關 Skill（代碼生成 Skill）
2. 通過 MCP 調用 Google Drive 服務器獲取文檔
3. 使用內置工具編寫代碼
```

---

## 創建 Skills 實踐指南

### 六步框架

#### 步驟 1：名稱和觸發器

```yaml
名稱：morning-coffee
觸發器：「幫我規劃今天」、「morning coffee」、「今天的計劃」
```

#### 步驟 2：目標

```markdown
目標：每天早上自動規劃一天的日程，整合日曆、任務和優先事項
```

#### 步驟 3：逐步流程

```markdown
1. 讀取今日日曆事件
2. 查看待辦任務列表
3. 檢查本週截止事項
4. 生成時間塊建議
5. 等待用戶確認後執行
```

#### 步驟 4：參考文件

```markdown
需要什麼上下文：
- 圖片？
- 當前項目信息？
- 風格指南？
- 品牌資產？
```

#### 步驟 5：規則

```markdown
規則：
- 不要在沒有確認的情況下修改日曆
- 如果有衝突，優先顯示並詢問用戶
- 保持每個時間塊不超過 2 小時
```

#### 步驟 6：自我改進循環

```
運行 skill → 觀察結果 → 給出反饋 → 更新 skill → 重複
```

### 範例：簡單的代碼解釋 Skill

```markdown
---
name: explain-code
description: 用可視化圖表和類比解釋代碼。當解釋代碼如何工作、
            教學代碼庫或用戶問「這個怎麼工作？」時使用
---

解釋代碼時，始終包括：

1. **類比開頭**: 將代碼與日常生活比較
2. **畫圖**: 使用 ASCII 藝術展示流程、結構或關係
3. **逐步講解**: 解釋發生了什麼
4. **注意事項**: 常見錯誤或誤解是什麼

保持解釋對話式。對於複雜概念，使用多個類比。
```

### 範例：帶動態上下文的 PR 摘要 Skill

```markdown
---
name: pr-summary
description: 總結 pull request 中的變更
context: fork
agent: Explore
allowed-tools: Bash(gh *)
---

## Pull request context
- PR diff: !`gh pr diff`
- PR comments: !`gh pr view --comments`
- Changed files: !`gh pr diff --name-only`

## Your task
總結這個 pull request...
```

### Description 撰寫最佳實踐

#### 好的 vs 不好的 Description

| 類型 | 范例 | 問題 |
|------|------|------|
| ❌ 不好 | "helps with documents" | 過於籠統 |
| ❌ 不好 | "Does stuff with files" | 沒有資訊價值 |
| ✅ 好 | "Generate React components with TypeScript... Use when user asks to create new UI components." | 明確觸發條件 |

#### 鐵律

1. **以 "Use when..." 開頭**
2. **僅描述何時使用**，不描述做什麼
3. **包含具體症狀、情況和上下文**
4. **保持在 500 字符以內**

---

## 實際案例：GEO 審計系統

### 項目概述

一個由 12 個不同 Skills 組成的 GEO（生成引擎優化）工具，可以對任何網站進行完整的 AI 搜索可見性審計。

### GEO 定義

**GEO = Generative Engine Optimization**
為 ChatGPT、Gemini、Claude 等 AI 搜索優化企業。

### 工作流程

```
┌─────────────────────────────────────────────────────────────┐
│                    GEO 審計流程                             │
└─────────────────────────────────────────────────────────────┘

階段一：發現
    │
    └── 發現網站首頁內容

階段二：並行子代理委派
    │
    ├── Agent 1: 內容分析
    ├── Agent 2: 結構化數據檢查
    ├── Agent 3: 引用來源分析
    ├── Agent 4: 品牌提及檢查
    └── Agent 5: 競爭對手對比

階段三：報告生成
    │
    └── 輸出完整 GEO 報告
```

### 輸出內容

| 部分 | 內容 |
|------|------|
| **執行摘要** | 整體評分和關鍵發現 |
| **分數細分** | ChatGPT、Perplexity、Gemini、Bing 等 |
| **關鍵發現** | 如「CEO 沒有維基百科文章」 |
| **優先行動計劃** | 每週可執行的改進建議 |

### 蓋房子比喻

就像蓋房子一樣：
- **總承包商** = Claude Code
- **分包商** = 子代理（電工、暖通空調、打地基）
- 每個分包商負責特定任務，最高效地建造房子

---

## 社群反饋與應用數據

### Reddit 社群 (r/ClaudeAI - 152萬成員)

#### 生產力革命

| 指標 | 數據 |
|------|------|
| 開發者經驗 | 30年經驗開發者 6 個月沒寫一行代碼 |
| 效率比 | 1週完成原本 3 個月的工作 |
| 角色轉變 | 從「代碼編寫者」變成「管理 6-10 個博士生」 |

#### 成本效益案例

| 項目 | 傳統成本 | Skills 成本 | 節省 |
|------|----------|-------------|------|
| 網站翻譯（350頁×7語言） | $2,000-$5,000/語言 | $18 總計 | **99%+** |
| 時間 | 2週 | 1天 | **93%** |

### MCP 生態系統

| 類別 | 數量 |
|------|------|
| 官方參考實現 | 7 個核心服務器 |
| 官方集成 | **400+** 企業級 MCP 服務器 |
| 社區服務器 | 數百個 |

### 已知問題和限制

| 問題 | 解決方案 |
|------|----------|
| Token 消耗 | 構建本地依賴圖，減少 65% 使用量 |
| 穩定性 | 需要人工監督和引導 |
| 學習曲線 | 適應「管理 agents」的新角色 |

### 整體評價

> **「Claude Code Skills 代表了軟件開發範式的重大轉變。雖然仍處於早期階段，但社區反饋普遍積極，收益遠大於挑戰。」**

---

## 最佳實踐建議

### 設計原則

1. **保持 SKILL.md 在 500 行以下**
2. **將詳細參考資料移到單獨文件**
3. **清晰引用支持文件**
4. **設計優先考慮 description**

### 節省 Tokens 的方法

#### 1. 硬編碼常量

```yaml
# 之前：每次都要查詢
- 調用 MCP 獲取列表 ID

# 之後：直接寫入
list_ids:
  - tasks: "abc123"
  - projects: "def456"
```

#### 2. 使用子 Agent

```markdown
將耗時的搜索任務委託給專門的子 agent，
避免炸毀主 agent 的上下文窗口。
```

#### 3. 本地化參考文檔

```markdown
# 之前：每次網絡搜索
- 搜索 Claude Code 文檔

# 之後：本地文件
- 參考 @references/claude-code-docs.md
```

### 測試清單

#### 測試一：觸發準確性

| 輸入 | 預期結果 |
|------|----------|
| 「幫我創建一個 React 組件」 | ✅ 觸發 |
| 「修改這個函數的邏輯」 | ❌ 不觸發 |

#### 測試二：輸出品質

- [ ] 輸出格式是否正確
- [ ] 關鍵內容是否完整
- [ ] 是否符合專案規範

#### 測試三：邊界條件

- 空輸入處理
- 超長輸入處理
- 特殊字元處理

---

## 參考資源

### 官方資源

| 資源 | 連結 |
|------|------|
| Claude Code 總覽 | https://docs.anthropic.com/en/docs/claude-code |
| Agent Skills 文檔 | https://docs.anthropic.com/en/docs/claude-code/skills |
| MCP 架構文檔 | https://modelcontextprotocol.io/docs/concepts/architecture |
| Agent Skills 開放標準 | https://agentskills.io |
| Anthropic Skills GitHub | https://github.com/anthropics/skills |

### 社群資源

| 資源 | 說明 |
|------|------|
| [Awesome Claude Skills](https://github.com/anthropics/awesome-claude-skills) | 22,620+ stars |
| [SkillsMP 平台](https://skillsmp.com/zh) | 128,427+ Agent Skills |
| r/ClaudeAI Reddit | 152萬成員 |
| MCP Servers GitHub | 400+ 企業級服務器 |

### 影片來源

- **標題**: STOP Building AI Agents. Do THIS Instead.
- **YouTube**: https://www.youtube.com/watch?v=wqH1hTkA6qg
- **GitHub 項目**: https://github.com/zubair-trabzada/geo-seo-claude

---

## 總結

### 一句話定位

| 工具 | 定位 |
|------|------|
| **Claude Skills** | AI 的「專業技能包」，教一次用終身 |
| **MCP** | AI 的「連接器」，讓 AI 訪問外部世界 |
| **Agents** | AI 的「獨立助手」，處理複雜多步驟任務 |

### 核心觀點

> **「如果你發現自己重複做某事或重複提示，那就是構建 skill 的好時機。」**

Skills 不必複雜 - 可以只是一個 50 行的 markdown 文件。關鍵在於：
1. **清晰的觸發條件**（Description）
2. **簡潔的工作流程**
3. **持續的迭代優化**

---

*最後更新：2026-03-07*

## 相关笔记
- [[03-knowledge/技术/编程/Claude-Code-Skills-2.0|Claude Code Skills 2.0]]
- [[03-knowledge/技术/编程/Claude-Code-完整指南-v2|Claude Code 完整指南]]
- [[03-knowledge/技术/编程/Claude-Skills-实战挑战|Skills 实战挑战]]
- [[004-ai-tools/claude/claude|Claude Code 使用指南]]
- [[study-notes/Superpowers-AI-Coding-Workflow|Superpowers 工作流]]
- [[MOC-AI编程工具|AI 编程工具 MOC]]