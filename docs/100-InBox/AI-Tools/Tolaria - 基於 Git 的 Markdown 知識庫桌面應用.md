---
title: Tolaria - 基於 Git 的 Markdown 知識庫桌面應用
aliases: [Tolaria, refactoringhq/tolaria]
tags:
  - knowledge-management
  - markdown
  - git
  - desktop-app
  - ai-assisted
  - status/active
  - type/learning-note
source:
  - "https://youtu.be/vMBJ6C8VpjY"
  - "https://github.com/refactoringhq/tolaria"
author: Luca Rossi (Refactoring.fm)
created: 2026-06-07
updated: 2026-06-07
description: Tolaria 是一個免費開源的桌面 Markdown 知識庫管理器，原生 Git 整合，支援 AI Agent 與本地模型，用純 Markdown + YAML frontmatter 管理筆記，真正擁有你的資料。
level: intermediate
stars: 4
note: 無字幕，基於 GitHub README + 官網 + 作者 Substack 介紹文章 + HN 討論綜合整理
---

# Tolaria - 基於 Git 的 Markdown 知識庫桌面應用

> Tolaria 是 Refactoring.fm 作者 Luca Rossi 打造的免費開源桌面知識庫管理器，用純 Markdown 檔案 + Git 管理筆記，內建 AI Agent 整合和本地模型聊天。作者自己管理 10,000+ 筆記運行整個生活和工作。技術棧：Tauri 2 + React + Rust，100K 行程式碼，AGPL-3.0 授權。

## 目錄

- [核心設計理念](#核心設計理念)
- [四大支柱架構](#四大支柱架構)
- [功能詳解](#功能詳解)
- [Git 整合深度](#git-整合深度)
- [AI 整合能力](#ai-整合能力)
- [與其他工具對比](#與其他工具對比)
- [技術架構](#技術架構)
- [安裝與快速開始](#安裝與快速開始)
- [優缺點評估](#優缺點評估)

---

## 核心設計理念

Tolaria 的設計圍繞一個核心信念：

> **「AI 對那些能夠捕獲和整理上下文的人帶來最大效益」**

基於此，Tolaria 的四個不妥協原則：

```
1. Files  — 可攜性、離線可用、AI 相容性
2. Markdown — AI 時代文件的事實標準，YAML frontmatter 可擴展
3. Git    — 歷史追蹤、雲端同步、人類/AI 協作作者身份追蹤
4. Open Source — 長期可攜性與用戶掌控
```

額外原則：Rich UX（視覺提示、導航、顏色、圖示）、Keyboard-first（所有操作可鍵盤完成）、Opinions（有強烈觀點的最佳實踐）。

---

## 四大支柱架構

```
┌────────────────────────────────────────────┐
│              Tolaria 應用                   │
├──────────┬──────────┬──────────┬──────────┤
│  Editor  │ Sidebar  │  Git     │   AI     │
│          │          │          │          │
│ WYSIWYG  │ Inbox    │ Commit   │ Claude   │
│ Raw Mode │ Types    │ Push     │ Codex    │
│ Wikilink │ Views    │ History  │ OpenCode │
│ Slash cmd│ Favorites│ AutoGit  │ Models   │
└──────────┴──────────┴──────────┴──────────┘
         │         │          │
         ▼         ▼          ▼
    ┌─────────────────────────────┐
    │  純 Markdown 檔案 + Git    │
    │  （無資料庫、無專有格式）     │
    └─────────────────────────────┘
```

---

## 功能詳解

### 側邊欄（Sidebar）— 組織中心

| 區段 | 用途 | 操作 |
|------|------|------|
| **📥 Inbox** | 未整理筆記 | `cmd+e` 標記為已整理 |
| **🗂️ All Notes** | 完整 Vault 檢視 | — |
| **📦 Archive** | 非活躍筆記永久存放 | 仍會出現在搜尋中 |
| **🧩 Types** | 主要組織單位（Note/Topic/Project） | 自訂圖示與顏色 |
| **⭐ Favorites** | 置頂筆記 | `cmd+d` 切換，可拖曳排序 |
| **🔍 Views** | 自訂過濾檢視 | YAML 配置 |
| **📂 Folders** | 次要導航 | 列在最後 |
| **🗑️ Trash** | 無回收桶 | Git 歷史就是安全網 |

### 自訂 Views（YAML 配置範例）

```yaml
name: Active Projects
icon: 🚀
filters:
  all:
    - field: type
      op: equals
      value: Project
    - field: Status
      op: equals
      value: Active
    - any:
      - field: status
        op: equals
        value: active
      - field: date
        op: after
        value: in 1 week
```

### 編輯器

- **WYSIWYG 模式** — 類 Notion 體驗，支援 slash commands
- **Raw 模式** — 直接看 Markdown 原始碼（`Cmd+/` 切換）
- **Wikilinks** — 輸入 `[[` 觸發全域自動完成，檔名改變時自動更新
- 檔名永遠顯示在頂部

### 命令面板

- `cmd+K` 開啟語義命令面板
- 語義匹配 — 不需要精確關鍵字
- 幾乎所有操作都可鍵盤完成

---

## Git 整合深度

這是 Tolaria 與其他 Markdown 編輯器的**核心差異化**。

```
一般 Markdown 編輯器         Tolaria
─────────────────────      ───────────────
Git 是外掛/外掛              Git 是一等公民
手動開終端 commit            應用內 commit/push
無改版追蹤                   瀏覽單一筆記的 Git 歷史
無同步方案                   AutoGit 自動 commit/push
刪除即消失                   刪除進 Trash，Git 歷史可恢復
```

### AutoGit 模式（可選）

自動根據以下觸發條件 commit 和 push：
- 閒置時間
- 應用焦點切換
- 啟發式規則

### 無回收桶設計

> 刪除的筆記進 Trash，但**真正的安全網是 Git 歷史**。這是對 Git 作為基礎設施的深度信任。

---

## AI 整合能力

### 兩種 AI 使用方式

| 方式 | 用途 | 工具 |
|------|------|------|
| **CLI Coding Agents** | 有工具能力的編輯（可直接寫入 Vault） | Claude Code, Codex, OpenCode, Pi, Gemini |
| **Direct Model Providers** | 基於筆記上下文的聊天（無寫入權限） | 本地模型或 API 模型 |

### MCP Server

Tolaria 內建 MCP Server，讓 AI Agent 能透過 MCP 協議與知識庫互動。

### Repository 作為「活的 artifact」

作者的觀點：整個 Git 倉庫本身就是 AI 輔助開發的證據：
- 70 個 ADR（Architecture Decision Records）
- AI 會讀取過去的決策來保持一致性
- 作者審查 ADR，AI 超過 90% 時間能做出正確決策

---

## 與其他工具對比

| 特性 | Tolaria | Obsidian | Notion | Logseq |
|------|---------|----------|--------|--------|
| 格式 | Markdown | Markdown | 專有 JSON | Markdown/org |
| Git 整合 | 一等公民，內建 | 外掛社群插件 | ❌ | 外掛 |
| 桌面應用 | ✅ Tauri 原生 | ✅ Electron | ✅ Web | ✅ Electron |
| 離線優先 | ✅ | ✅ | ❌ | ✅ |
| AI Agent 整合 | ✅ 內建 MCP | 外掛 | 需 API | 外掛 |
| 自訂 Views | ✅ YAML | 外掛 | ✅ DB Filter | 查詢語言 |
| 知識關係 | 一等公民 | 外掛/插件 | 關聯 DB | 圖譜 |
| 授權 | AGPL-3.0 | 免費/商業 | 專有 | AGPL-3.0 |
| 無需帳號 | ✅ | ✅ | ❌ | ✅ |
| 定價 | **免費永遠** | 免費/商業 | 免費/付費 | 免費 |
| 資料可攜 | ✅ 純檔案 | ✅ 純檔案 | ❌ 匯出有限 | ✅ 純檔案 |

### 與 Obsidian 的關鍵差異

```
相同點：
  - 純 Markdown 檔案
  - 本地優先
  - 外掛生態

差異點：
  Tolaria                    Obsidian
  ─────────                  ────────
  Git 內建一等公民            Git 是外掛
  有強烈觀點的 UX             高度可自訂
  Tauri (輕量，Rust)          Electron (較重)
  內建 MCP/AI 整合            AI 需外掛
  Types + Views 組織系統       資料夾 + 標籤
  免費                        免費 + 付費功能
```

---

## 技術架構

| 項目 | 規格 |
|------|------|
| 技術棧 | Tauri 2 + React + TypeScript + Rust |
| 行程式碼 | ~100K（Rust + React） |
| Commits | 2,970+ |
| Releases | 1,171 |
| 測試 | 3,000+ |
| ADRs | 70 |
| 授權 | AGPL-3.0-or-later |
| 平台 | macOS, Windows, Linux |
| CI | GitHub Actions |
| 程式碼品質 | Codecov + CodeScene |

### 倉庫結構重點

```
tolaria/
├── src-tauri/          # Tauri (Rust) 後端
├── src/                # React/TypeScript 前端
├── mcp-server/         # 內建 MCP Server（AI 工具）
├── site/               # 使用者文檔（GitHub Pages）
├── docs/               # 開發者文檔 + ADR
├── demo-vault-v2/      # Demo Vault
├── e2e/, tests/        # 測試套件
├── CLAUDE.md           # Claude Code Agent 配置
├── GEMINI.md           # Gemini Agent 配置
└── AGENTS.md           # 通用 Agent 配置
```

---

## 安裝與快速開始

### macOS (Homebrew)

```bash
brew install --cask tolaria
```

### 直接下載

從 [tolaria.md/download](https://tolaria.md/download/) 下載最新版本。

### 首次啟動

1. 安裝後首次啟動，可 clone **[getting started vault](https://github.com/refactoringhq/tolaria-getting-started)** 了解完整功能
2. 用現有 Git 倉庫或新建 Vault
3. 開始用 Markdown + YAML frontmatter 建立筆記

### 本地開發

```bash
# 需要：Node.js, Rust, Tauri CLI
git clone https://github.com/refactoringhq/tolaria.git
cd tolaria
pnpm install
# 瀏覽器 Mock 模式：localhost:5173
# 或運行原生桌面應用
```

---

## 優缺點評估

### ✅ 優點

- **真正擁有你的資料**：純 Markdown 檔案，無資料庫，無專有格式
- **Git 一等公民**：內建 commit/push/history/AutoGit，不是外掛
- **AI 整合深度**：MCP Server、CLI Agent 支援、本地模型聊天
- **免費永遠開源**：AGPL-3.0，無帳號要求
- **跨平台原生**：Tauri 2 比 Electron 輕量得多
- **有強烈觀點的設計**：Types + Views + Relationships 組織系統，不用從零設計
- **活躍開發**：作者每天投入 2+ 小時，100K 行程式碼、3000+ 測試、70 個 ADR
- **AI 輔助開發標竿**：CLAUDE.md/GEMINI.md/AGENTS.md 配置齊全，倉庫本身就是 AI 工作流的展示

### ❌ 缺點與風險

- **Bus Factor 極高**：作者 Luca 一人主導開發（雖然投入很大）
- **生態早期**：Plugins/Themes 不如 Obsidian 豐富
- **AGPL-3.0 授權限制**：商業整合需注意授權義務
- **Windows 企業部署**：可能需要 IT 批准 Tolaria 發行者
- **知識庫遷移成本**：從 Notion 遷移需要手動整理，雖然格式簡單但量大
- **功能仍在快速迭代**：API 可能變動，文檔可能滯後

---

## 参考资料

- [refactoringhq/tolaria GitHub](https://github.com/refactoringhq/tolaria)
- [Tolaria 官網](https://tolaria.md/)
- [Introducing Tolaria (Refactoring.fm)](https://refactoring.fm/p/introducing-tolaria)
- [Show HN: Tolaria (Hacker News)](https://news.ycombinator.com/item?id=47882697)
- [Tolaria Reddit 討論](https://www.reddit.com/r/macapps/comments/1sykgst/os_tolaria_a_filesfirst_markdown_app_for_mac/)

## 相关笔记

- [[Scrapling - AI 自適應網頁爬蟲框架]]
- [[cli/PaddleOCR - 百度開源 OCR 工具包與文件 AI 引擎]]
