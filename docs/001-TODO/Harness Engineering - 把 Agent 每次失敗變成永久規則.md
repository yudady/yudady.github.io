---
title: Harness Engineering - 把 Agent 每次失敗變成永久規則
aliases: [Harness Engineering, AI Agent 架構]
tags:
  - ai-agent
  - harness-engineering
  - prompt-engineering
  - status/active
  - type/learning-note
source:
  - "https://www.youtube.com/watch?v=ZtZCBLeCDHs"
  - "https://addyosmani.com/blog/agent-harness-engineering/"
  - "https://martinfowler.com/articles/harness-engineering.html"
author: Addy Osmani / Viv Trivedy / Martin Fowler
created: 2026-05-10
updated: 2026-05-10
description: AI Agent 的真正戰場不在模型本身，而在包覆模型的 Harness（繫具）——包括 prompt、tools、hooks、sandbox 和反饋迴圈。掌握 Ratchet 紀律和七個工程支柱，把每次失敗轉化為永久防禦。
level: intermediate
stars: 5
---

# Harness Engineering - 把 Agent 每次失敗變成永久規則

> Agent = Model + Harness。如果你不是那個模型，你就是 Harness。模型說穿了只是一個輸入引擎，真正的工程戰場在於你怎麼設計包覆它的繫具（Harness）——包括 System Prompt、工具、控制迴圈、Sandbox 和可觀測性。

## 目錄

- [核心公式：Agent = Model + Harness](#核心公式agent--model--harness)
- [Harness 到底包含什麼？](#harness-到底包含什麼)
- [心態轉換：拒絕「等下個版本」](#心態轉換拒絕等下個版本)
- [The Ratchet：把每次失敗變成永久規則](#the-ratchet把每次失敗變成永久規則)
- [Harness 的七個工程支柱](#harness-的七個工程支柱)
- [HaaS 趨勢：從 LLM API 到 Harness API](#haas-趨勢從-llm-api-到-harness-api)
- [Martin Fowler 的前饋與反饋模型](#martin-fowler-的前饋與反饋模型)
- [三個可帶走的行動方針](#三個可帶走的行動方針)

---

## 核心公式：Agent = Model + Harness

這是 Viv Trivedy 提出的核心等式，也是整個 Harness Engineering 的基礎：

```
╔══════════════════════════════════════════╗
║           Agent = Model + Harness        ║
║                                          ║
║  ┌──────────────────────────────────┐    ║
║  │         Harness（你控制的）       │    ║
║  │  ┌────────────────────────────┐  │    ║
║  │  │      Model（供應商控制的）   │  │    ║
║  │  └────────────────────────────┘  │    ║
║  │  Prompts · Tools · Hooks · Loop  │    ║
║  │  Sandbox · Memory · Observability│    ║
║  └──────────────────────────────────┘    ║
╚══════════════════════════════════════════╝
```

**關鍵認知**：你平時感受到的「模型能力上限」，很多時候其實只是你的 **Harness 能力上限**。Addy Osmani 在 Terminal Bench 2.0 上觀察到，同一個 Claude Opus 4.6 在 Claude Code 裡的分數遠低於在定制 Harness 中的分數；Viv Trivedy 的團隊僅通過改變 Harness，就將編碼 Agent 從 Top 30 推到 Top 5。

---

## Harness 到底包含什麼？

Harness 是模型之外所有讓 Agent 能真正工作的東西：

| 組件 | 說明 | 具體例子 |
|------|------|----------|
| System Prompts | 指令層 | `CLAUDE.md`、`AGENTS.md`、Skill files |
| Tools & MCP | 工具層 | Bash、文件操作、Web 搜索、Context7 |
| Infrastructure | 基礎設施 | File System、Git、Sandbox |
| Control Flow | 控制流程 | Subagent 生成、Model 路由、Loop |
| Hooks | 強制執行層 | Pre-commit lint、Type check、破壞性命令攔截 |
| Observability | 可觀測性 | 日誌、追蹤、成本計量 |
| Memory | 記憶層 | 上下文壓縮、跨會話持久化 |

> **一句話**：Claude Code、Cursor、Codex、Aider、Cline 這些產品的本質都是 Harness。底層模型可能相同，但行為差異完全由 Harness 決定。

---

## 心態轉換：拒絕「等下個版本」

HumanLayer 說了一句狠話：**「不是模型問題，是配置問題。」**

常見的工程逃避模式 vs 正確的 Harness 思維：

```
失敗場景                    ❌ 逃避心態              ✅ Harness 思維
─────────────────────────────────────────────────────────────────
Agent 忽略編碼約定    →  等下個版本 Claude 更聰明  →  寫進 AGENTS.md
Agent 跑破壞性指令    →  這模型太笨了              →  加 Hook 攔截
Agent 在長任務中迷路  →  Context Window 不夠用     →  拆分 Planner + Executor
Agent 提交殘缺代碼    →  再跑一次就好了            →  接 TypeCheck 反壓信號
```

**Benchmark 鐵證**：把同一個強大模型丟進普通框架 vs 為特定任務高度調校的 Custom Harness，分數差距極大。

---

## The Ratchet：把每次失敗變成永久規則

Ratchet（棘輪）是一種只能往前轉、不能後退的機械結構。在 Harness Engineering 中，它的定義是：

> **Agent 觀察到的每一次失敗，都必須變成永久的規則或限制。**

### 實戰 Scenario

```
Step 1: 觀察失敗
  └─ Agent 提交了 PR，裡面有被註釋掉的測試代碼，不小心被 merge

Step 2: 更新 AGENTS.md
  └─ 加上：「永遠不要註釋掉測試，要麼刪掉，要麼修好」

Step 3: 加強防禦
  └─ Pre-commit hook 自動掃描 .skip() 和 xit()

Step 4: 驗證層面
  └─ Reviewer subagent 將註釋掉的測試標記為 blocker
```

### Ratchet 的三個黃金守則

1. **只在觀察到真實失敗時才加規則** — 不要憑空想像防護網
2. **模型進化後勇敢刪除過時規則** — Opus 4.6 殺掉了一整類 context-anxiety 鷹架
3. **每條規則必須能追溯到具體的失敗歷史** — 追溯不到的只是裝飾品

> **你的 AGENTS.md 應該像飛行員的起飛檢查清單**：短短的，很致命，每一條都是用血淋淋的失敗換來的。它絕不該像一份冗長的風格指南。

---

## Harness 的七個工程支柱

這是 Addy Osmani 根據 Viv Trivedy 的行為推導框架整理的架構檢查清單。

### 支柱 1-4：基礎設施層

```
┌─────────────────────────────────────────────────┐
│              Harness 基礎設施層                  │
│                                                 │
│  [1] File System + Git                          │
│      → 耐久狀態 + 版本控制 + 多 Agent 協作       │
│                                                 │
│  [2] Bash + 代碼執行環境                         │
│      → 通用問題解決手段（比預建工具更靈活）        │
│                                                 │
│  [3] Sandbox                                    │
│      → 隔離執行環境，防止誤操作生產環境            │
│      → 預裝語言運行時、Git、CLI、Headless Browser  │
│                                                 │
│  [4] Memory + Search                            │
│      → AGENTS.md 跨會話持久化                    │
│      → Web Search / MCP 彌補知識截止日            │
└─────────────────────────────────────────────────┘
```

### 支柱 5：對抗 Context Rot（上下文腐敗）

當 context 塞滿垃圾時，模型真的會變笨。三招對策：

| 技術 | 操作方式 | 效果 |
|------|----------|------|
| **壓縮（Compaction）** | 舊上下文總結後移出 | 釋放空間 |
| **工具調用卸載（Offloading）** | 大輸出存磁碟，只留頭尾 | 減少噪音 |
| **漸進式揭露（Progressive Disclosure）** | 按需加載工具和 Skill | 降低初始認知負擔 |

**Anthropic 的補充**：對於超長任務，壓縮仍然不夠，需要 **完整 Context Reset** — 銷毀會話後從結構化的交接文件重建。這更接近人類新人入職，而非傳統「記憶」概念。

### 支柱 6：長週期任務的三層結構

```
長週期任務
  │
  ├── [Ralph Loop] ── Hook 攔截模型「偷懶提前結束」的企圖
  │                    → 重新注入原始 prompt 到新 context window
  │
  ├── [Planning] ──── 強迫模型先產出 step-by-step 計劃文件
  │                    → 每步完成後自檢
  │
  └── [Split] ────── 生成和評估拆成獨立 Agent
                       → Agent 評估自己的產出會盲目自信（GAN 效應）
                       → Sprint Contract：開工前協商「完成」的定義
```

**最佳實踐**：

- ✅ 分離 Generator 和 Evaluator
- ✅ 開工前寫下 done-condition（比任何 prompt 改動更能防範範圍蔓延）
- ❌ 讓同一個 Agent 寫代碼又自己 review

### 支柱 7：Hooks（強制執行層）

Hooks 是「我告訴 Agent 做什麼」和「系統強制執行什麼」之間的差異。

**核心法則：Success Silent, Failure Verbose**

```
Agent 提交代碼
  │
  ├─ TypeCheck ✅ 通過 → Agent 什麼都不知道（零噪音）
  │
  └─ TypeCheck ❌ 失敗 → 錯誤信息直接噴回 Agent Loop
                          → Agent 當場自我修正
```

常見 Hook 點位：

- 工具調用前 / 文件編輯後
- Commit 前 / PR 創建前
- 會話啟動時
- 自動格式化（省 token）

> **安全警告**：工具描述會被注入 system prompt，任何 MCP Server 都是 Agent 會讀取的可信文本。惡意或粗心的 MCP 可以在你輸入任何東西之前就對 Agent 進行 Prompt Injection。

---

## HaaS 趨勢：從 LLM API 到 Harness API

### 演進路徑

```
第一代：LLM API
  → 只給你一段文本（completion）
  → 自己建 loop、工具調用、對話狀態、審批流程

第二代（現在）：Harness API（HaaS）
  → 給你完整 Runtime：Loop + Tools + Context + Hooks + Sandbox
  → 你專注於領域專屬的 Prompt 和工具設計
  → 代表：Claude Agent SDK、Codex SDK、OpenAI Agents SDK

第三代（未來）：動態 Harness
  → 根據任務即時組裝正確的工具和上下文
  → Agent 分析自己的 trace 來修復 harness 層面的失敗
  → 多 Agent 並行協作共享代碼庫
```

### Harness 不會縮小，只會遷移

模型變強後，舊的鷹架確實會過時（Opus 4.6 消滅了 context-anxiety 類問題）。但天花板也隨之上移，新的任務帶來新的失敗模式，需要新的鷹架。

> **每個 harness 組件都編碼了一個關於「模型不能獨立完成什麼」的假設。** 模型進步 → 舊假設過時 → 移除舊組件 → 新天花板 → 新假設 → 新組件。

### 模型-Harness 訓練迴圈

現代模型在 Post Training 階段是針對特定 Harness 設計進行過度擬合的。這意味著：

- Opus 4.6 在 Claude Code 裡的體驗跟在別的 Harness 裡不同
- 改變工具邏輯可能導致奇怪的性能退化（co-training overfitting）
- 最佳 Harness 不一定是模型訓練時用的那個，而是為你的任務定制的

---

## Martin Fowler 的前饋與反饋模型

Martin Fowler 將 Harness 進一步分為三層同心圓和兩個控制方向：

```
                    ┌─────────────────────────┐
                    │    用戶 Harness（你的）   │  ← AGENTS.md, Hooks,
                    │  ┌───────────────────┐  │     自定義 Linter
                    │  │ 內建 Harness（產品）│  │
                    │  │ ┌─────────────┐  │  │
                    │  │ │   Model     │  │  │
                    │  │ └─────────────┘  │  │
                    │  └───────────────────┘  │
                    └─────────────────────────┘
```

### 前饋（Feedforward）vs 反饋（Feedback）

| | 前饋（Guides） | 反饋（Sensors） |
|---|---|---|
| **時機** | Agent 行動之前 | Agent 行動之後 |
| **目的** | 預防問題 | 自我修正 |
| **推斷型** | AGENTS.md, Skills | Review Agent, LLM-as-Judge |
| **計算型** | LSP, Codemods, Scripts | Linter, Type Check, ArchUnit Tests |

### 三種調節維度

```
┌─────────────────────────────────────────┐
│ 可維護性 Harness                         │  ← 最成熟，工具最多
│   代碼質量、架構漂移、測試覆蓋率          │
├─────────────────────────────────────────┤
│ 架構適配性 Harness                       │  ← Fitness Functions
│   性能要求、日誌標準、可觀測性約定        │
├─────────────────────────────────────────┤
│ 行為 Harness                             │  ← 最難，仍在探索中
│   功能規格、人工測試、Mutation Testing    │
└─────────────────────────────────────────┘
```

### Harnessability（可繫具化程度）

不是所有代碼庫都同樣容易建 Harness：

- ✅ 強類型語言 → 天然有 Type Check 作為 Sensor
- ✅ 清晰的模組邊界 → 可以加架構約束規則
- ✅ 成熟的框架（如 Spring） → 隱含提升 Agent 成功率
- ❌ 技術債堆積的遺留系統 → 最需要 Harness，但也最難建

---

## 三個可帶走的行動方針

1. **把 Harness 當作活的工藝品** — 立刻落實 Ratchet 紀律。今天出錯，今天就變成防禦規則
2. **盤點 Codebase** — 每條 prompt 和 hook 都必須能追溯到真實失敗。沒歷史根據的果斷砍掉
3. **評估工程位置** — 你的系統有沒有 Loop、Tool、Hook、Sandbox？如果沒有，你可能還停留在 LLM API 包裝階段

> **最後的尖銳問題**：你現在到底只是在做 LLM API 的精美包裝，還是真的在打造一個具備 Loop、Tool、Hook 和 Sandbox 的 Harness API Runtime？

---

## 參考資料

- [Agent Harness Engineering - Addy Osmani](https://addyosmani.com/blog/agent-harness-engineering/)
- [Harness Engineering for Coding Agent Users - Martin Fowler](https://martinfowler.com/articles/harness-engineering.html)
- [Building Claude Code with Harness Engineering - Fareed Khan](https://levelup.gitconnected.com/building-claude-code-with-harness-engineering-d2e8c0da85f0)
- [Long-running Agents - Addy Osmani](https://addyosmani.com/blog/long-running-agents/)
- [Are Agent Harnesses the new "secret sauce"? - Department of Product](https://departmentofproduct.substack.com/p/are-agent-harnesses-the-new-secret)
- [Harness Engineering: How to Build Reliable AI Agents - deepset AI](https://www.deepset.ai/blog/harness-engineering)
- [Improving Deep Agents with Harness Engineering - LangChain](https://www.langchain.com/blog/improving-deep-agents-with-harness-engineering)

## 相關筆記

- [[AI Agent]]
- [[Prompt Engineering]]
- [[Context Engineering]]
