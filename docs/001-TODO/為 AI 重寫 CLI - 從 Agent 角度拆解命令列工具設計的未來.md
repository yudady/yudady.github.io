---
title: "為 AI 重寫 CLI - 從 Agent 角度拆解命令列工具設計的未來"
aliases:
  - Rewrite CLI for AI
  - Agent-first CLI
  - agentyper
  - CLI DX for AI
tags:
  - type/learning-note
  - topic/ai-tools
  - topic/CLI
  - topic/tool-design
  - topic/agent-framework
  - source/youtube
source: "https://youtu.be/Zix6q-cyXDs"
author: "蝦說 AI（小金老師）（原作 Justin Poehnelt）"
created: "2026-06-07"
updated: "2026-06-07"
description: "Justin Poehnelt（Google Workspace CLI 作者）提出 CLI 工具需要為 AI Agent 重新設計：JSON 輸出、Schema Introspection、Context Window Discipline、Dry-Run、Input Hardening。附 HN 社區 67 條激烈辯論，John Carmack 和 Andrej Karpathy 的正面回應，以及反對派的實戰質疑。"
level: 4
stars: 5
note: "資料來源：YouTube 描述 + The Undercurrent 深度分析 + HN 討論串（67 comments）+ Justin Poehnelt 原文"
---

## 目錄

- [核心論點：CLI 的主要消費者不再是人類](#核心論點cli-的主要消費者不再是人類)
- [傳統 CLI 對 AI 的六大不友善之處](#傳統-cli-對-ai-的六大不友善之處)
- [JSON 是 AI 的母語](#json-是-ai-的母語)
- [Schema Introspection：CLI 自身就是文檔](#schema-introspectioncli-自身就是文檔)
- [Shell Quoting：讓 AI 懷疑人生的引用地獄](#shell-quoting讓-ai-懷疑人生的引用地獄)
- [Context Window Discipline：Token 紀律](#context-window-disciplinetoken-紀律)
- [Dry-Run：預覽再執行](#dry-run預覽再執行)
- [Input Hardening：嚴格驗證輸入](#input-hardening嚴格驗證輸入)
- [35 倍 Token 成本差距的實測數據](#35-倍-token-成本差距的實測數據)
- [HN 社區辯論：支持與反對](#hn-社區辯論支持與反對)
- [Agent-First CLI 最低可行檢查清單](#agent-first-cli-最低可行檢查清單)
- [市場信號與新興工具](#市場信號與新興工具)
- [關鍵洞察](#關鍵洞察)

---

## 核心論點：CLI 的主要消費者不再是人類

> 「Human DX optimizes for discoverability and forgiveness. Agent DX optimizes for predictability and defense-in-depth.」
> 「人類體驗優化可發現性和容錯性。Agent 體驗優化可預測性和深度防禦。」

Justin Poehnelt（Google Workspace CLI `gws` 作者）從實際開發 Agent-first CLI 的經驗出發，提出了一個大膽的主張：**CLI 工具需要為 AI Agent 重新設計**。不是因為人類不重要，而是因為 Agent 和人類的使用模式有根本差異，硬湊在一起是輸的策略。

---

## 傳統 CLI 對 AI 的六大不友善之處

| 問題 | 人類體驗 | Agent 體驗 |
|------|----------|-----------|
| **輸出格式** | 彩色表格、進度條、emoji | 無法解析的非結構化文字 |
| **錯誤訊息** | 友善的「哎呀，出錯了」 | 無法程序化判斷的模糊描述 |
| **參數組合** | 猜測 flag 組合 | 需要精確的 schema 定義 |
| **文檔** | README、Stack Overflow | 需要機器可讀的即時查詢 |
| **Shell Quoting** | 直覺處理 | 嵌套引號、特殊字元讓 Agent 崩潰 |
| **Token 消耗** | 不計較 | 每個 byte 都要花錢 |

---

## JSON 是 AI 的母語

### Human-First vs Agent-First 命令對比

**Human-First（需要猜測 flag 組合）：**
```bash
my-cli spreadsheet create \
  --title "Q1 Budget" \
  --locale "en_US" \
  --sheet-title "January" \
  --frozen-rows 1 \
  --frozen-cols 2
```

**Agent-First（一個 flag 承載完整 API payload）：**
```bash
gws sheets spreadsheets create --json '{
  "properties": {"title": "Q1 Budget", "locale": "en_US"},
  "sheets": [{"properties": {
    "title": "January",
    "gridProperties": {"frozenRowCount": 1, "frozenColumnCount": 2}
  }}]
}'
```

核心差異：Agent-First 不需要記住 50 個 flat flag，直接傳入結構化 JSON payload。

**最低要求**：`--output json`、`OUTPUT_FORMAT=json` 環境變數、或 stdout 非 TTY 時預設 NDJSON。

---

## Schema Introspection：CLI 自身就是文檔

Agent 無法在不消耗大量 token 的情況下 Google 文檔，system prompt 中的靜態文檔很快就過時。

**解法：讓 CLI 自身可查詢。**

```bash
gws schema drive.files.list      # 列出參數、請求體、回應類型、OAuth scopes
gws schema sheets.spreadsheets.create
```

> 「Make the CLI itself the documentation, queryable at runtime. Your tool should be able to answer "what do you accept?" and "what will you return?" without the agent ever leaving the terminal.」

已有工具：`gh` CLI、`docker`。不做的工具會被 shim 層包裝（成為維護負債）。

---

## Shell Quoting：讓 AI 懷疑人生的引用地獄

AI 生成 shell 命令時最常犯的錯誤之一就是引號處理：

```bash
# AI 想要執行的
my-cli search --query "user:admin AND status:active"

# AI 實際生成的
my-cli search --query "user:admin AND status:active"
# 或更糟的嵌套引號地獄
my-cli run --cmd "echo \"hello 'world'\" "
```

嵌套引號、轉義、特殊字元（`$`、`\`、`!`）在 bash 中的行為極其複雜。Agent 處理這些時錯誤率很高。

**設計建議**：
- 接受 stdin 或 `--file` 參數來繞過 shell quoting
- 支持 `@filename` 語法從檔案讀取輸入
- 提供管道友好的 NDJSON 模式

---

## Context Window Discipline：Token 紀律

Agent 按字元付費，每個 byte 的噪音都在消耗推理能力。

### Field Masks（欄位遮罩）

限制 API 返回的內容：
```bash
gws drive files list --params '{"fields": "files(id,name,mimeType)"}'
```

### NDJSON Pagination（流式分頁）

每行一個 JSON 物件，流式處理，不需要緩衝整個回應：
```bash
gws drive files list | jq '.name'   # 逐行處理
```

> 「If your CLI dumps everything and expects the consumer to filter, you're burning tokens that could be spent on reasoning.」

---

## Dry-Run：預覽再執行

Agent 的操作可能具有破壞性。提供 `--dry-run` 讓 Agent 先看到會發生什麼：

```bash
my-cli deploy --dry-run --target production
# 輸出：會刪除 3 個舊容器，啟動 2 個新容器，零停機時間
```

Agent 看到預覽後可以決定是否真正執行，避免災難性操作。

---

## Input Hardening：嚴格驗證輸入

傳統 CLI 常接受模糊輸入（如 `--count "three"` 然後猜測用戶要 3）。Agent-first CLI 需要：

- **嚴格類型驗證**：「count should be a valid integer」
- **可操作的結構化錯誤**：返回 JSON 格式的錯誤，含 error code + 可修復建議
- **語義化退出碼**：0=成功，1=輸入錯誤，2=關鍵錯誤（Agent 可根據 exit code 分支處理）

---

## 35 倍 Token 成本差距的實測數據

Jannik Reinhard 對 Microsoft Graph 的合規性檢查實測：

| 方式 | Token 消耗 |
|------|-----------|
| **MCP** | ~145,000（其中 28K 僅 schema 注入） |
| **CLI** | ~4,150 |

**35 倍差距。** MCP server 將數十甚至數百個工具定義傾倒入 context，無論是否需要。疊加幾個企業 MCP server，光是 plumbing 就消耗 150K+ tokens。

**結論**：MCP 不是錯的，但對許多工作流，設計良好的 CLI + `--json` + Schema Introspection 更快、更便宜、更可靠。**CLI 本身就是 tool call。**

---

## HN 社區辯論：支持與反對

### 支持派

**John Carmack**：
> 「LLM assistants are going to be a good forcing function to make sure all app features are accessible from a textual interface as well as a GUI. A strong enough AI can drive a GUI, but it makes so much more sense to just make the GUI a wrapper around a command line interface.」

**Andrej Karpathy**：
> 「CLIs are super exciting precisely because they are a 'legacy' technology, which means AI agents can natively and easily use them, combine them, interact with them via the entire terminal toolkit.」

### 反對派

**缺乏實證**（sheept）：
> 「This feels completely speculative: there's no measure of whether this approach is actually effective.」

**Context 污染**（sheept）：
> 「gws ships 100+ SKILL.md files... hundreds of lines of YAML frontmatter polluting your context」

**人類設計的 CLI 本來就適合 Agent**（gck1）：
> 「People seem to forget one of the L's in LLM stands for Language, and human language is likely the largest chunk in training data. A CLI well designed for humans is well designed for agents too.」

**實戰失敗模式不是輸出格式**（mahendra0203）：
> 「I run AI agents against CLIs every day. The failures are almost never 'the output wasn't structured enough.' They're context window overflows, permission issues, and the agent confidently running the wrong command over and over.」

**最小修改建議**：`--output json` + 有意義的 exit code + 不要主動對抗 Agent。

---

## Agent-First CLI 最低可行檢查清單

- [ ] **`--json` flag everywhere** — 結構化輸出到 stdout，人類訊息到 stderr
- [ ] **有意義的 exit codes** — 不只是 0/1，Agent 需要根據失敗模式分支
- [ ] **冪等操作** — Agent 會重試，必須優雅處理
- [ ] **Schema introspection** — `mytool schema` 回傳命令接受的/返回的格式
- [ ] **NDJSON 分頁** — 流式輸出大型結果集，不緩衝
- [ ] **名詞-動詞結構** — `mytool resource action` 讓發現變成樹搜索
- [ ] **TTY 偵測** — 人類用漂亮輸出，管道用 JSON（自動切換）

---

## 市場信號與新興工具

| 項目 | 描述 |
|------|------|
| **Google Workspace CLI (`gws`)** | 從 Day 1 就是 Agent-first 設計的 CLI |
| **RTK** | HN Show 項目，包裝現有 CLI 剝離人類格式化，節省 60-90% tokens |
| **agentyper**（Roman Medvedev） | Agent-first Python CLI 庫，Typer 兼容，argparse + pydantic |
| **CLIWatch** | 建構 Agent-ready 基準測試（通過率、token 效率、回合數）+ README badges |
| **coasts** | 內嵌文檔模式 + 語義搜索（~28k 英文詞，~16MB/語系） |

---

## 關鍵洞察

1. **CLI 的下一個 power user 不讀 README** — 它讀 `--help`、introspect schema、解析 JSON。為那個用戶設計
2. **不是 Human vs Agent，是 Human AND Agent** — TTY 偵測讓同一個 CLI 同時服務兩種消費者
3. **35 倍成本差距是真實的** — MCP 的 schema 注入成本在企業環境中會疊加到不可接受的程度
4. **Schema Introspection 是殺手級特性** — 讓 CLI 成為自文檔化工具，Agent 不需要離開終端
5. **辯論雙方都有道理** — 反對派指出實際失敗模式是 context overflow 和權限問題，不是輸出格式；但支持派的 35 倍成本數據無法忽視
6. **最小可行改動是現實路徑** — 加 `--json`、修 exit code、做 TTY 偵測，不需要重寫整個 CLI
