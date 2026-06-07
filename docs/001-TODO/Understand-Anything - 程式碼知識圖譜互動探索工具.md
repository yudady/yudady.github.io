---
title: Understand-Anything - 程式碼知識圖譜互動探索工具
aliases: [Understand-Anything, Lum1104/Understand-Anything]
tags:
  - code-understanding
  - knowledge-graph
  - tree-sitter
  - llm-agent
  - claude-code
  - status/active
  - type/learning-note
source:
  - "https://youtu.be/r6gFohtJ2aA"
  - "https://github.com/Lum1104/Understand-Anything"
author: Lum1104 (Sujeeth)
created: 2026-06-07
updated: 2026-06-07
description: Understand-Anything 用多 Agent 管線將程式碼庫轉換為互動知識圖譜，結合 Tree-sitter 結構解析與 LLM 語意理解，支援 Claude Code/Codex/Cursor 等 14 個平台。
level: intermediate
stars: 4
note: 無字幕，基於 GitHub README + dev.to 文章 + 社群討論綜合整理
---

# Understand-Anything - 程式碼知識圖譜互動探索工具

> Understand-Anything 是一個開源 AI Agent 插件，用多 Agent 管線分析程式碼庫，自動生成互動式知識圖譜（Knowledge Graph）。每個檔案、函式、類別都成為可點擊的節點，帶有自然語言摘要。結合 Tree-sitter 精確結構解析與 LLM 語意理解，適合 legacy code 分析和團隊 onboarding。

## 目錄

- [核心價值與設計哲學](#核心價值與設計哲學)
- [三大分析模式](#三大分析模式)
- [技術架構：Tree-sitter + LLM 混合](#技術架構tree-sitter--llm-混合)
- [Multi-Agent 管線](#multi-agent-管線)
- [日常使用指令](#日常使用指令)
- [平台安裝支援](#平台安裝支援)
- [團隊協作與分享](#團隊協作與分享)
- [完整功能清單](#完整功能清單)
- [優缺點評估](#優缺點評估)

---

## 核心價值與設計哲學

> **「目標不是一張讓你驚嘆程式碼有多複雜的圖，而是一張默默教你每個部分如何組合在一起的圖。」**

### 解決的問題

```
傳統方式理解大型程式碼庫：
  1. 打開專案，看到幾百個檔案
  2. 手動 trace code，從入口點開始跳轉
  3. 畫 UML 圖（很快就過時）
  4. 問同事（同事也記不清）
  5. 看到懷疑人生

Understand-Anything 的方式：
  1. 執行 /understand
  2. 自動生成互動知識圖譜
  3. 點擊任何節點看自然語言解釋
  4. 搜尋「哪些部分處理 auth？」
  5. 按依賴順序自動導覽
```

### 設計原則

```
AI-native 但非 AI-locked
  └── 圖譜輸出是純 JSON
  └── Dashboard 獨立運行，探索時不需要 LLM
  └── 不綁定特定 AI 供應商
```

---

## 三大分析模式

| 模式 | 指令 | 適用場景 | 輸出 |
|------|------|----------|------|
| **Structural Graph** | `/understand` | 程式碼庫結構分析 | 檔案/函式/類別節點 + 依賴邊 |
| **Domain View** | `/understand-domain` | 業務領域抽取 | 水平圖譜：程式碼 → 業務流程 → 步驟 |
| **Knowledge Base** | `/understand-knowledge` | Markdown Wiki 分析 | 力導向圖 + 社群聚類 |

### Structural Graph（結構圖譜）

每個節點包含：
- 檔案/函式/類別名稱
- 自然語言摘要（用途、職責）
- 依賴關係（import/call 邊）
- 所在架構層（API/Service/Data/UI/Utility）
- 顏色編碼分層

### Domain View（領域視圖）

```
程式碼結構              業務領域映射
──────────             ──────────────
src/auth/login.ts   →   [用戶認證] → [登入流程] → [驗證步驟]
src/payment/        →   [支付處理] → [結帳流程] → [金額計算]
src/notify/         →   [通知系統] → [推送流程] → [模板渲染]
```

非技術人員（PM、產品經理）也能理解系統運作。

---

## 技術架構：Tree-sitter + LLM 混合

```
原始碼
  │
  ├── Tree-sitter ──→ 結構化資料（精確）
  │   ├── 函式定義
  │   ├── 類別結構
  │   ├── import 關係
  │   └── AST 節點
  │
  └── LLM ──→ 語意理解（豐富）
      ├── 自然語言摘要
      ├── 業務領域識別
      ├── 程式碼概念解釋
      └── 關係推斷
  │
  ▼
知識圖譜 JSON
  ├── 可再現的結構側（Tree-sitter）
  └── 語意意圖側（LLM）
```

### 為什麼不純用 LLM？

| 方式 | 優點 | 缺點 |
|------|------|------|
| 純 LLM | 靈活、語意豐富 | 不穩定、成本高、不可再現 |
| 純 Tree-sitter | 精確、快速、可再現 | 無語意、無摘要 |
| **混合** | 精確 + 語意兼顧 | 成本中等，管線較複雜 |

Tree-sitter 提供可再現的結構基礎，LLM 在此之上疊加語意層。結構側是確定性的，語意側可以隨 LLM 更新而改進。

---

## Multi-Agent 管線

```
/understand 觸發後的執行流程：

project-scanner ──→ file-analyzer ──→ architecture-analyzer
      │                    │                    │
      ▼                    ▼                    ▼
  發現檔案            提取函式/類別/       識別架構層級
  偵測語言/框架        import 關係        (API/Service/Data...)
                       產生節點和邊
                            │
                            ▼
                    ┌───────────────┐
                    │ tour-builder   │  生成依賴導覽
                    │ graph-reviewer│  驗證完整性
                    │ domain-analyzer│ 業務領域抽取
                    └───────────────┘
                            │
                            ▼
               knowledge-graph.json
```

| Agent | 職責 | 依賴 |
|-------|------|------|
| `project-scanner` | 發現檔案、偵測語言和框架 | 無 |
| `file-analyzer` | 提取函式/類別/import，產生圖節點和邊 | project-scanner |
| `architecture-analyzer` | 識別架構層級 | file-analyzer |
| `tour-builder` | 生成依賴排序的導覽路線 | architecture-analyzer |
| `graph-reviewer` | 驗證完整性和引用完整性 | tour-builder |
| `domain-analyzer` | 抽取業務領域、流程、步驟 | architecture-analyzer |
| `article-analyzer` | 從 Wiki 文章抽取實體和關係 | 無（獨立） |

### 效能特性

- **平行處理**：File analyzers 最多 5 個並行，每批 20-30 個檔案
- **增量更新**：只重新分析變更的檔案
- **`--auto-update`**：可加入 post-commit hook 自動增量更新

---

## 日常使用指令

```bash
# 完整分析（首次）
/understand

# 開啟互動 Dashboard
/understand-dashboard

# 自然語言問答
/understand-chat How does payment work?
/understand-chat 哪些部分處理認證？

# 查看當前 Diff 的影響範圍
/understand-diff

# 深入特定檔案/函式
/understand-explain src/auth/login.ts

# 生成 onboarding 文檔
/understand-onboard

# 業務領域分析
/understand-domain

# 分析 Markdown Wiki
/understand-knowledge ~/path/to/wiki

# 多語言輸出
/understand --language zh    # 簡體中文
/understand --language ja    # 日文
/understand --language ko    # 韓文
```

### 決策樹：何時用哪個指令

```
你是新成員？ → /understand + /understand-onboard
  │
  ├── 理解整體架構 → /understand-dashboard
  ├── 追蹤特定功能 → /understand-explain <file>
  ├── 改動前看影響 → /understand-diff
  └── 向非技術人員解釋 → /understand-domain
```

---

## 平台安裝支援

### Claude Code（原生外掛市場）

```bash
/plugin marketplace add Lum1104/Understand-Anything
/plugin install understand-anything
```

### 一行安裝（macOS/Linux）

```bash
# 互動式選擇平台
curl -fsSL https://raw.githubusercontent.com/Lum1104/Understand-Anything/main/install.sh | bash

# 指定平台
curl -fsSL ... | bash -s -- <platform>
```

### 支援平台（14 個）

| 類別 | 平台 |
|------|------|
| IDE 外掛 | Claude Code, Cursor, VS Code + Copilot |
| CLI Agent | Codex, Gemini CLI, OpenCode, Pi, OpenClaw, Hermes |
| 其他 CLI | Cline, KIMI CLI, Trae, Antigravity, Vibe |

### Cursor / VS Code 自動發現

Clone 倉庫到專案目錄後自動偵測 `.cursor-plugin/plugin.json` 或 `.copilot-plugin/plugin.json`，無需手動安裝。

---

## 團隊協作與分享

### 圖譜是純 JSON

輸出路徑：`.understand-anything/knowledge-graph.json`

```bash
# 提交圖譜讓團隊共享
git add .understand-anything/
git commit -m "chore: add knowledge graph"

# 排除中間檔案
# .gitignore 加入：
.understand-anything/intermediate/
.understand-anything/diff-overlay.json

# 大型圖譜（>10MB）用 git-lfs
git lfs install
git lfs track ".understand-anything/*.json"
```

**效益**：一人生成圖譜，全團隊直接使用，省去每人重複跑管線的時間和 token 成本。

---

## 完整功能清單

| 功能 | 說明 |
|------|------|
| Structural Graph | 每個檔案/函式/類別的可點擊節點 + 自然語言摘要 |
| Domain View | 程式碼到業務流程的水平映射 |
| Knowledge Base | 分析 LLM Wiki（Karpathy 模式）為力導向圖 |
| Guided Tours | 自動生成依賴排序的架構導覽 |
| Fuzzy & Semantic Search | 按名稱或含義搜尋（如「哪些部分處理 auth？」） |
| Diff Impact Analysis | 提交前查看變更的連鎖影響 |
| Persona-Adaptive UI | 根據角色（初級開發者/PM/資深工程師）調整細節程度 |
| Layer Visualization | 自動按架構層分組 + 顏色編碼 |
| Language Concepts | 12 種程式設計模式（泛型、閉包、裝飾器等）的上下文解釋 |
| Multilingual | 支援中英日韓輸出 |
| Incremental Updates | 只重新分析變更的檔案 |
| Auto-commit Hook | 自動增量更新圖譜 |

---

## 優缺點評估

### ✅ 優點

- **解決真實痛點**：legacy code 理解和團隊 onboarding 是普遍問題
- **Tree-sitter + LLM 混合架構**：精確結構與豐富語意兼顧，且可再現
- **輸出是純 JSON**：AI-native 但不 AI-locked，探索不需要 LLM
- **14 個平台支援**：Claude Code/Codex/Cursor/VS Code Copilot/Hermes 等全覆盖
- **團隊友好的分享機制**：JSON commit 共享，一人生成全團隊受益
- **增量更新**：大專案不會每次全量重跑
- **MIT 授權**：完全開源自由

### ❌ 缺點與風險

- **首次分析成本高**：大專案需要大量 LLM token（200K 行程式碼測試案例中成本顯著）
- **依賴 LLM 品質**：摘要和領域抽取的準確性取決於所用的 LLM
- **外掛而非獨立工具**：需要 Claude Code/Codex 等 AI Agent 平台才能運行
- **Git 倉庫汙染**：`.understand-anything/` 目錄加到專案根目錄，部分團隊可能介意
- **互動 Dashboard 功能仍在迭代**：長期維護和功能完善度有待觀察
- **Tree-sitter 語言覆蓋**：主流語言支援良好，小眾語言可能有限

---

## 参考资料

- [Lum1104/Understand-Anything GitHub](https://github.com/Lum1104/Understand-Anything)
- [Understand-Anything 官網](https://understand-anything.com)
- [Turn Any Codebase Into an Interactive Knowledge Graph (dev.to)](https://dev.to/arshtechpro/understand-anything-turn-any-codebase-into-an-interactive-knowledge-graph-37ed)
- [Codebase-Memory: Tree-Sitter-Based Knowledge Graphs (arXiv)](https://arxiv.org/html/2603.27277v1)

## 相关笔记

- [[Scrapling - AI 自適應網頁爬蟲框架]]
- [[PaddleOCR - 百度開源 OCR 工具包與文件 AI 引擎]]
- [[Tolaria - 基於 Git 的 Markdown 知識庫桌面應用]]
