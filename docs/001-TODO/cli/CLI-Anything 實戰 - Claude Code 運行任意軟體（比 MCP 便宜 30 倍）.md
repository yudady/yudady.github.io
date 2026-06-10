---
title: "CLI-Anything 實戰 - Claude Code 運行任意軟體（比 MCP 便宜 30 倍）"
aliases:
  - CLI-Anything demo
  - CLI vs MCP cost
  - Agent-Native Computer Use
tags:
  - type/learning-note
  - topic/ai-tools
  - topic/CLI
  - topic/MCP
  - topic/agent-framework
  - source/youtube
source: "https://youtu.be/O5bjNhUMJV4"
author: "FuturMinds"
created: "2026-06-07"
updated: "2026-06-07"
description: "CLI-Anything 是香港大學 HKUDS 實驗室推出的開源工具，能讓 Claude Code 透過自動生成的 CLI 控制任意開源軟體。以 Draw.io 為例的實戰 Demo，展示從源碼到可用 CLI 的完整流程，以及 CLI 比 MCP 便宜 30 倍的 token 成本對比。配套 arXiv 論文提出 Agent-Native Contract H=(S,C,I,R,V,D) 理論框架。"
level: 4
stars: 4
note: "資料來源：YouTube 字幕（FuturMinds）+ CLI-Anything GitHub + arXiv:2606.03854 論文 + AlphaMatch 文章"
---

## 目錄

- [問題：AI Agent 為什麼控制不了大多數軟體](#問題ai-agent-為什麼控制不了大多數軟體)
- [三種失敗的方法](#三種失敗的方法)
- [CLI-Anything 如何填補空白](#cli-anything-如何填補空白)
- [Draw.io 實戰 Demo](#drawio-實戰-demo)
- [CLI vs MCP：30 倍成本差距](#cli-vs-mcp30-倍成本差距)
- [Agent-Native Contract 理論框架](#agent-native-contract-理論框架)
- [CLI-Anything vs MCP：定位差異](#cli-anything-vs-mcp定位差異)
- [一問測試](#一問測試)
- [誠實的局限性](#誠實的局限性)

---

## 問題：AI Agent 為什麼控制不了大多數軟體

現在的 AI Agent（Claude、Codex 等）能瀏覽網頁、寫程式、管理檔案，但**無法控制電腦上大多數的軟體**。

根本原因：絕大多數軟體只有 GUI 介面，而 GUI 是為人類設計的視覺隱喻（面板、圖標、按鈕）。Agent 操作 GUI 需要：截圖 → 視覺模型解讀 → 定位元素 → 模擬點擊 → 重複。這過程**慢、脆弱、易碎**。

---

## 三種失敗的方法

### 1. 螢幕自動化（Screen Automation）

工具直接看螢幕並幫你點按鈕。聽起來酷，實際上：
- **慢**：每個操作都要截圖→分析→點擊
- **脆弱**：菜單移動一個像素就會壞掉
- **不可靠**：依賴像素級別的座標定位

### 2. MCP Servers

當前的標準方案，在 MCP server 存在時很好用。但問題是：
- MCP server 必須有人**手動建置**
- 只有大型熱門工具（Slack、GitHub、資料庫）有 MCP server
- **絕大多數軟體沒有 MCP server**

### 3. 直接讓 Claude 編輯檔案

把檔案貼給 Claude 說「編輯這個」。問題是：
- Claude 不理解專有檔案格式
- 無法驗證它創建的內容是否正確
- **一半的時間生成損壞的檔案**

---

## CLI-Anything 如何填補空白

**CLI-Anything 讀取開源軟體的源碼，自動生成 Claude 可以使用的命令列介面。**

由香港大學 HKUDS 實驗室（LightRAG、RAG-Anything 的同一團隊）開發，首週即獲 10,000+ GitHub Stars。

不是玩具包裝器，而是連接**真實軟體後端**：
- Blender → `bpy` Python API
- LibreOffice → `--headless` 模式
- Audacity → `sox` 音訊處理
- Draw.io → 原生 XML 格式操作

---

## Draw.io 實戰 Demo

### 環境設置

1. **Clone Draw.io 源碼**：
   ```bash
   git clone https://github.com/jgraph/drawio-desktop
   ```

2. **安裝 CLI-Anything 到 Claude Code**：
   ```
   /plugin marketplace add HKUDS/CLI-Anything
   /plugin install cli-anything
   ```

3. **指向 Draw.io 倉庫，啟動 7-phase pipeline**：
   ```
   /cli-anything ./drawio-desktop
   ```

### Pipeline 執行結果

- **耗時**：15+ 分鐘（200MB 代碼庫）
- **生成測試**：90 個測試全部通過
- **產出**：完整的 CLI harness

### 使用 CLI 控制 Draw.io

```bash
# 一行 prompt 讓 Claude 創建架構圖
"Create an architecture diagram for draw.io itself"
```

Claude 透過生成的 CLI 直接操作 Draw.io 的原生 XML 格式，創建完整的架構圖。不需要截圖、不需要 GUI、不需要 MCP server。

```bash
# 再創建 Netflix 架構圖
"Create a detailed architecture diagram for Netflix with microservices"
```

同樣一行 prompt，生成包含完整微服務架構的圖表。

### 預載工具

CLI-Anything 插件安裝後，**40+ 軟體的 harness 已預載**，不需要每次都跑 7-phase pipeline。

---

## CLI vs MCP：30 倍成本差距

影片提出了一個具體的成本論據：

**MCP 的工作方式**：每次使用時，將完整的工具 schema 載入 Claude 的 context window。這只是**連接開銷**，就要消耗數千 tokens。

**CLI 的工作方式**：透過 `--help` 自描述，Agent 按需查詢，不需要預載完整 schema。

**結果**：CLI 完成同樣的工作，token 消耗是 MCP 的 **1/30**。

| 維度 | MCP | CLI-Anything |
|------|-----|-------------|
| Token 開銷 | 高（預載完整 schema） | 低（按需 `--help`） |
| 成本倍率 | 1x | **~1/30x** |
| 需要手動建置 | 是（每個工具都要） | 否（自動生成） |
| 覆蓋範圍 | 僅有 MCP server 的工具 | 任何有源碼的開源軟體 |

---

## Agent-Native Contract 理論框架

CLI-Anything 配套的 arXiv 論文（2606.03854）提出了正式的 **Agent-Native Contract** 理論：

### H=(S,C,I,R,V,D) 六元組

| 組件 | 定義 |
|------|------|
| **S**（State） | 持久化狀態空間：項目檔案、session 檔案、undo 歷史 |
| **C**（Commands） | 領域特定的變更/探測操作（Click subcommands + REPL） |
| **I**（Inspection） | JSON 狀態、列表、schema、歷史、preview summary |
| **R**（Rendering） | 委託給真實軟體或原生後端工具渲染 |
| **V**（Verification） | 單元測試、E2E 測試、檔案格式檢查、媒體檢查 |
| **D**（Discovery） | SKILL.md、registry metadata、CLI-Hub 記錄 |

### 六個不變量（Invariants）

1. **Explicit State** — 工作區必須有命名、可保存、可檢查的狀態
2. **Typed Actions** — 命令映射到領域操作，失敗時返回可操作的錯誤
3. **Cheap Inspection** — Agent 可以在提交前查看變更
4. **Backend Truth** — 最終渲染/導出使用真實軟體
5. **Programmatic Verification** — 檢查用戶實際會打開的產物，不只是 exit code
6. **Discoverability** — Agent 能從結構化 metadata 發現、安裝、調用工具

### 核心洞察

> 「If an artifact can be checked by code, it can often be built through code.」

如果一個產物可以透過代碼檢查，那它通常可以透過代碼構建。GUI 應保留給人類；Agent 的預設邊界應該是應用層之上的**有狀態契約**。

### Preview Protocol

CLI-Anything 設計了 **Preview Bundle** 機制：
- `manifest.json` — metadata
- `summary.json` — 精簡狀態摘要
- `artifacts/` — 圖像、影片、模型
- `trajectory.json` — 命令到預覽的追蹤歷史

**真實性是核心設計約束** — 明確拒絕玩具渲染器和 GUI 螢幕截圖作為預覽來源。

---

## CLI-Anything vs MCP：定位差異

影片明確表示：**CLI-Anything 不是取代 MCP，它們解決不同的問題。**

| 場景 | 推薦方案 |
|------|----------|
| 工具已有成熟的 MCP server（Slack、GitHub、資料庫） | **用 MCP** |
| 開源工具，團隊在用，但沒有 MCP server | **用 CLI-Anything** |
| 公司內部代碼庫 | **用 CLI-Anything** |

---

## 一問測試

在嘗試 CLI-Anything 之前，問自己一個問題：

> **這個軟體是否有獨立於 GUI 的後端、API 或檔案格式？**

- **Yes** → CLI-Anything 可以構建真正可用的東西
- **No**（唯一操作方式是視覺介面）→ 你可能得到一個看起來好看但實際不做事的 wrapper

---

## 誠實的局限性

1. **只適用於開源軟體**：需要源碼才能分析並生成 CLI
2. **純 GUI 應用會失敗**：如果軟體完全依賴視覺介面，沒有後端 API，CLI-Anything 無法創建有意義的 wrapper
3. **pipeline 耗時**：大型代碼庫（200MB）需要 15+ 分鐘
4. **不取代 MCP**：對於已有 MCP server 的熱門工具，MCP 仍然是更好的選擇
5. **覆蓋範圍有限**：雖然有 40+ 預載 harness，但相對全球數百萬軟體仍是少數

---

## 補充：HKUDS 實驗室背景

CLI-Anything 並非隨意開發者的項目。HKUDS（香港大學數據科學學院）此前已推出多個有影響力的開源項目：
- **LightRAG** — 輕量級 RAG 框架
- **RAG-Anything** — 多模態 RAG 系統

這些項目都展現了將學術研究轉化為高 Star 開源工具的能力。CLI-Anything 首週 10k Stars、兩週 21k Stars 的增速印證了 Agent-Native CLI 這個方向的市場需求。
