---
title: codebase-memory-mcp — 讓 AI Agent 先有一張本地程式碼地圖
aliases: [Codebase Memory MCP, CBM]
tags:
  - mcp
  - code-intelligence
  - knowledge-graph
  - ai-coding
  - tree-sitter
  - status/active
  - type/doc
source:
  - "https://youtube.com/watch?v=7igdxYXAgA8"
  - "https://github.com/DeusData/codebase-memory-mcp"
  - "https://arxiv.org/abs/2603.27277"
author: DeusData（開源專案）；影片由「Github雷達」頻道介紹
created: 2026-07-28
updated: 2026-07-28
description: 索引整份 codebase 為本地知識圖譜的 MCP Server，讓 AI coding agent 不必每次逐檔翻找，直接用圖譜查詢理解架構。
level: intermediate
stars: 4
---

# codebase-memory-mcp — 讓 AI Agent 先有一張本地程式碼地圖

> 影片來源：[Github雷達 — codebase-memory-mcp](https://youtube.com/watch?v=7igdxYXAgA8)（3:48）
> 專案倉庫：[DeusData/codebase-memory-mcp](https://github.com/DeusData/codebase-memory-mcp)（36k★, 2.8k Fork, MIT）
> 學術背書：arXiv:2603.27277 _Codebase-Memory: Tree-Sitter-Based Knowledge Graphs for LLM Code Exploration via MCP_

## 目錄

- [核心問題：AI Agent 在大型專案的困境](#核心問題ai-agent-在大型專案的困境)
- [定位與運作機制](#定位與運作機制)
- [效能表現與支援範疇](#效能表現與支援範疇)
- [關鍵功能與團隊協作](#關鍵功能與團隊協作)
- [部署安全性與限制](#部署安全性與限制)
- [快速開始](#快速開始)

---

## 核心問題：AI Agent 在大型專案的困境

AI coding agent（Claude Code、Codex CLI、Gemini CLI 等）面對大型 codebase 時有兩個結構性痛點：

1. **逐檔翻找效率低下** [00:00] — agent 用 grep 等工具一次打開十幾個檔案 [00:04]，看似努力回答卻未真正理解架構 [00:08]。
2. **Context Window 浪費** [01:04] — 大量無關程式碼塞入上下文，消耗 Token 也降低檢索精準度。

影片引用的實測數字（來自 arXiv 論文，31 個真實 repo 評測）：

```
傳統 file-by-file 探索 vs codebase-memory-mcp
┌──────────────────────┬──────────────┬─────────────────┐
│ 指標                 │ 傳統逐檔探索 │ codebase-memory │
├──────────────────────┼──────────────┼─────────────────┤
│ 5 次結構查詢 Token   │ ~412,000     │ ~3,400          │
│ 回答品質             │ baseline     │ 83%             │
│ 工具呼叫次數         │ baseline     │ 2.1x 更少       │
│ Token 消耗           │ baseline     │ 10x 更少        │
└──────────────────────┴──────────────┴─────────────────┘
= 99.2% Token reduction
```

> 核心洞察：問題不在「模型不夠大」或「context 不夠寬」，而在 agent 缺乏一張可重複利用的架構地圖。

---

## 定位與運作機制

### 一句話定位

codebase-memory-mcp 是一個 **本機 MCP Server**（MCP, Model Context Protocol），把整份 repository 索引成**持久化知識圖譜（Knowledge Graph）**。它不是 LLM，也不是雲端程式碼分析服務 [00:23][00:34]。

### 雙層解析架構

```
你的 Codebase
     │
     ▼
┌─────────────────────────────────────────────┐
│  Layer 1: Tree-sitter (語法樹解析)           │
│  - 158 種語言全部覆蓋                        │
│  - 提取函式、類別、呼叫關係、HTTP route       │
│  - 速度快，但只懂「語法」不懂「語意」        │
└──────────────────┬──────────────────────────┘
                   │ 補強
                   ▼
┌─────────────────────────────────────────────┐
│  Layer 2: Hybrid LSP (語意型別解析)          │
│  - Python, TypeScript, Go, Rust, Java,      │
│    C#, PHP, C/C++, Kotlin, Perl 等 13 語言   │
│  - 解析型別定義、方法解析、import 真實指向   │
│  - 純 C 實作，內嵌於 binary，無外部 LSP      │
│  - 等效於 IDE 的「Go to Definition」         │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
        持久化知識圖譜 (SQLite)
     ┌─────────────────────────┐
     │ Node: Function, Class,  │
     │       Route, File...    │
     │ Edge: CALLS, IMPORTS,   │
     │       HTTP_CALLS...     │
     └─────────────────────────┘
```

**為什麼需要兩層？**

Tree-sitter 解析語法樹 [00:37]，能抓到函式名稱、類別結構。但光靠語法樹，無法知道 `user.profile.display_name()` 實際指向三個模組外的 `Profile.display_name` 方法 [01:10]。Hybrid LSP 補上型別解析、import 解析、泛型展開，讓查詢從「文字搜尋」升級為「架構理解」[01:26]。

### 圖譜資料模型

| Node 類型 | Edge 類型 | 說明 |
|-----------|-----------|------|
| Project, Package, Folder, File | CONTAINS_* | 層級結構 |
| Class, Function, Method, Interface | DEFINES, CALLS | 定義與呼叫 |
| Route, Resource | HTTP_CALLS, ASYNC_CALLS | 跨服務連結 |
| — | IMPLEMENTS, INHERITS | 型別關係 |
| — | EMITS, LISTENS_ON | 事件 channel |

---

## 效能表現與支援範疇

### 效能基準（Apple M3 Pro）

| 操作 | 耗時 | 備註 |
|------|------|------|
| **Linux Kernel 完整索引** | **3 分鐘** | 28M LOC, 75K 檔案 → 4.81M nodes, 7.72M edges |
| Linux Kernel 快速索引 | 1m 12s | 1.88M nodes |
| Django 完整索引 | ~6s | 49K nodes, 196K edges |
| Cypher 查詢 | <1ms | 關係遍歷 |
| 名稱搜尋 (regex) | <10ms | SQL LIKE 預過濾 |
| Dead code 偵測 | ~150ms | 全圖掃描 + degree 過濾 |
| Call path 追蹤 (depth=5) | <10ms | BFS 遍歷 |

影片引用的數字 [00:49]：支援 **158 種程式語言**，Linux Kernel 2800 萬行程式碼完整索引約 3 分鐘 [00:54]，結構化查詢延遲低於 1 毫秒 [01:00]。

> 這些數字不只是炫技——背後代表 agent 可以少讀很多無關檔案 [01:03]，直接從圖譜拿到結構化答案。

### 語言支援（以解析品質分級）

| 等級 | 分數 | 代表語言 |
|------|------|----------|
| Excellent (≥90%) | 90+ | Lua, Kotlin, C++, Perl, C, Bash, Zig, Swift, CSS, YAML |
| Good (75-89%) | 75-89 | Python, TypeScript, Go, Rust, Java, JavaScript, Ruby, PHP, C# |
| Functional (<75%) | <75 | OCaml, Haskell |

### 跨平台與 Agent 整合 [01:35]

- **單一靜態 Binary**，零依賴（純 C 實作，無 runtime）
- 支援 macOS (arm64/amd64)、Linux (arm64/amd64)、Windows (amd64)
- 安裝後自動偵測並配置 **43 個 coding agent surface** 的 MCP entry

| Agent | 整合深度 |
|-------|----------|
| Claude Code | MCP config + Skill + 三層 graph agent + hooks |
| Codex CLI | MCP config + AGENTS.md + 三個唯讀 agent |
| Gemini CLI | MCP config + GEMINI.md + 三個子 agent |
| Zed / OpenCode / Aider | MCP config + 規範檔 |
| Cursor / VS Code / Windsurf | MCP config + 唯讀 agent |
| ...其他 30+ surfaces | 詳見 README 完整對照表 |

```
安裝流程 (macOS / Linux)
  curl install.sh | bash
        │
        ▼
  下載 static binary (LZ4 壓縮, SHA-256 驗證)
        │
        ▼
  偵測已安裝的 agent (43 surfaces)
        │
        ▼
  寫入各 agent 的 MCP config + 規範檔
        │
        ▼
  重啟 agent → 說 "Index this project" → 完成
```

---

## 關鍵功能與團隊協作

### 15 個 MCP 工具

agent 可以透過這些工具向圖譜提問 [01:50]：

| 類別 | 工具 | 回答什麼 |
|------|------|----------|
| 架構 | `get_architecture` | 語言、套件、entry points、routes、hotspots、layers |
| 呼叫追蹤 | `trace_path` | 誰呼叫了此 function？它又呼叫了什麼？ |
| 影響分析 | `detect_changes` | 這次 git diff 影響哪些模組？（含風險分級） |
| Dead code | `search_graph` + degree filter | 哪些 function 零呼叫者？ |
| 圖譜查詢 | `query_graph` | Cypher-like 語法，跨節點關係遍歷 |
| 語意搜尋 | `semantic_query` | 向量搜尋（內建 Nomic embedding，無需 API key） |
| ADR 管理 | `manage_adr` | 架構決策記錄，跨 session 保存 |

Cypher-like 查詢範例 [02:07]：

```cypher
// 找出所有呼叫 main 的 function
MATCH (f:Function)-[:CALLS]->(g) WHERE f.name = 'main' RETURN g.name

// Dead code：沒有任何 CALLS 入邊的 function
MATCH (f:Function) WHERE NOT EXISTS { (f)<-[:CALLS]-() } RETURN f.name
```

### Team-Shared Graph Artifact [02:16]

```
傳統模式：每人/每 session 從零索引
┌────────┐  ┌────────┐  ┌────────┐
│ Dev A  │  │ Dev B  │  │ Agent  │
 │ index  │  │ index  │  │ index  │   ← 各自 3 分鐘
 └────────┘  └────────┘  └────────┘

CBM Team-Shared 模式
┌────────┐
│ CI/CD  │── index ── commit graph.db.zst 到 repo
└────────┘                │
     ┌────────────────────┘
     ▼
repo/.codebase-memory/graph.db.zst  (壓縮比 8-13:1)
     │
     ├─ Dev A clone → 解壓 + 增量索引 (秒級)
     ├─ Dev B clone → 解壓 + 增量索引 (秒級)
     └─ 新 Agent session → 直接讀圖譜，免重建
```

- **格式**：SQLite DB，壓縮前 strip index + VACUUM INTO，再 zstd 1.5.7 壓縮
- **兩層匯出**：Best（`zstd -9`，完整索引時寫）/ Fast（`zstd -3`，watcher 增量更新）
- **Bootstrap**：clone 後偵測到 artifact，先匯入再增量補差，避免全量重建
- **無 merge 衝突**：自動加 `.gitattributes` 的 `merge=ours` 規則
- **可選**：不想版控就在 `.gitignore` 加 `.codebase-memory/`

這對大型 monorepo 特別實用 [02:27]——新人 onboard 或 CI 開新 agent session 都不必等完整索引。

---

## 部署安全性與限制

### 安全審查要點 [02:32]

影片明確提醒：雖然專案標榜程式碼完全留於本地 [02:41]，但它會：
1. 讀取你的整個 codebase
2. 修改 agent 設定檔

導入前必須審查：

| 審查項 | 具體做法 |
|--------|----------|
| Install script | 下載後先 `notepad install.ps1` / `cat install.sh` 檢查 |
| Release checksum | 比對 `checksums.txt` 的 SHA-256 |
| 二進位簽章 | 專案提供 SLSA Level 3 + Sigstore cosign 簽章 |
| 病毒掃描 | 每個 release 經 VirusTotal 70+ 引擎掃描（0/72 需通過） |
| 權限範圍 | 確認你願意讓它管理哪些 agent config |

專案在這方面做得相對完整：CodeQL SAST 阻擋有 open alert 的 release、所有處理 100% 本機、零 telemetry。

### 系統限制 [02:51]

> 它建立的是**結構化記憶**，不保證回答永遠正確 [02:51]。

最終品質仍取決於三個因素：

```
回答正確性 = f(索引新鮮度, 查詢品質, Agent 解讀能力)
              ▲              ▲              ▲
              │              │              │
        watcher 是否        prompt 是否       agent 是否
        同步了最新          精準指向圖譜     正確理解圖譜
        git 變更            工具             回傳的結構
```

### 適用場景判斷

```
你的專案適合導入 codebase-memory-mcp 嗎？

[專案規模]
  ├─ 小型專案 (<10K LOC, 單語言)
  │   └─ 普通搜尋就夠了 [03:08]
  │
  ├─ 中型多語言 / 微服務
  │   └─ 值得評估，團隊共享 artifact 是主要賣點
  │
  └─ 大型 monorepo / 多服務 / 歷史包袱重 [03:12]
      └─ 強烈建議，Token 節省 + 架構理解提升顯著

[你的 Agent 工作流]
  ├─ 偶爾用 AI 補程式碼 → 收益有限
  ├─ 每天用 Claude Code / Codex 寫功能 → 明顯省 Token
  └─ 團隊多人 + CI/CD agent 自動化 → 團隊 artifact 價值最大
```

---

## 快速開始

### 安裝（一行指令）

```bash
# macOS / Linux
curl -fsSL https://raw.githubusercontent.com/DeusData/codebase-memory-mcp/main/install.sh | bash

# 含 3D 圖譜視覺化 UI
curl -fsSL https://raw.githubusercontent.com/DeusData/codebase-memory-mcp/main/install.sh | bash -s -- --ui

# Windows (PowerShell)
irm https://raw.githubusercontent.com/DeusData/codebase-memory-mcp/main/scripts/setup-windows.ps1 | iex
```

### 基本使用

```bash
# 重啟 agent 後，直接說：
# "Index this project"

# CLI 模式（不等於 MCP session，一次性指令）
codebase-memory-mcp cli list_projects
codebase-memory-mcp cli search_graph --project my-project --name-pattern '.*Handler.*' --label Function
codebase-memory-mcp cli trace_path --project my-project --function-name Search --direction both

# Cypher 查詢
codebase-memory-mcp cli query_graph --project my-project \
  --query 'MATCH (f:Function) RETURN f.name LIMIT 5'

# 開啟 3D 圖譜 UI（localhost:9749）
codebase-memory-mcp --ui=true --port=9749
```

### 套件管理器安裝

```bash
npm install -g codebase-memory-mcp
pip install codebase-memory-mcp
go install github.com/DeusData/codebase-memory-mcp@latest
# Homebrew / Scoop / Winget / Chocolatey / AUR 均可用
```

---

## 核心結論

影片的核心論點 [03:18][03:37]：

> AI coding 的下一步不一定只是更大的模型 [03:18]——很多時候是讓 agent 先有一張可靠的本地地圖。codebase-memory-mcp 做的就是這張地圖。

這呼應了 MCP 生態的設計哲學：與其無限擴大 context window 硬塞程式碼，不如把 codebase 預先整理成 agent 能高效查詢的結構化記憶。

**行動建議：**
1. 小型專案維持一般搜尋即可 [03:08]
2. 多語言、微服務或大型歷史包袱專案優先評估導入 [03:12]
3. 確認安裝來源 checksum 及設定權限後配置 Agent MCP [01:35]
4. 建置 CI/CD 流程自動產出 Graph Snapshot 並版控，加速團隊與 AI Agent 對齊 [02:21]

---

## 參考資料

- [影片：Github雷達 — codebase-memory-mcp](https://youtube.com/watch?v=7igdxYXAgA8)
- [GitHub: DeusData/codebase-memory-mcp](https://github.com/DeusData/codebase-memory-mcp)（36k★, MIT）
- [arXiv:2603.27277 — Codebase-Memory 論文](https://arxiv.org/abs/2603.27277)
- [官網：deusdata.github.io/codebase-memory-mcp](https://deusdata.github.io/codebase-memory-mcp/)
- [Reddit: r/ClaudeAI 作者介紹帖](https://www.reddit.com/r/ClaudeAI/comments/1rp6pkr/i_built_an_mcp_server_that_gives_claude_code_a/)

## 相關筆記

- [[MCP 生態]]
- [[AI Coding Agent 工具鏈]]
- [[Tree-sitter]]

---

*文件生成時間：2026-07-28*
*基於 codebase-memory-mcp v0.8.0+（Hybrid LSP 已支援 13 語言）*
