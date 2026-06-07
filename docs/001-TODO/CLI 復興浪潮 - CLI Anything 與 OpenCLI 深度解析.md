---
title: "CLI 復興浪潮 - 為什麼巨頭都在做命令行？CLI Anything 與 OpenCLI 深度解析"
aliases:
  - CLI Anything
  - OpenCLI
  - CLI vs MCP
  - CLI 復興
tags:
  - type/learning-note
  - topic/ai-tools
  - topic/CLI
  - topic/MCP
  - topic/agent-framework
  - source/youtube
source: "https://youtu.be/5rAIU0GBjII"
author: "技术爬爬虾 TechShrimp"
created: "2026-06-07"
updated: "2026-06-07"
description: "CLI 這個計算機世界最古老的交互方式正在迎來新爆發。CLI Anything 能一行命令把任意軟體變成 Agent 可控的 CLI，OpenCLI 能把任何網站或 Electron 應用轉成命令行。從 CLI-Anything 的 7-phase pipeline 到 OpenCLI 的 Chrome session reuse，CLI 正成為 AI Agent 的標準互動介面。"
level: 4
stars: 5
note: "資料來源：YouTube 描述 + CLI Anything 官網/GitHub (HKUDS) + OpenCLI GitHub (jackwener) + AlphaMatch 深度文章 + Simon Willison/Tembo CLI 對比文章"
---

## 目錄

- [CLI 復興：為什麼是現在](#cli-復興為什麼是現在)
- [CLI Anything：一行命令讓所有軟體 Agent-Native](#cli-anything一行命令讓所有軟體-agent-native)
- [OpenCLI：把任何網站變成命令行](#opencli把任何網站變成命令行)
- [官方 CLI 趨勢：巨頭的布局](#官方-cli-趨勢巨頭的布局)
- [MCP vs CLI：終極對比](#mcp-vs-cli終極對比)
- [兩個項目的差異與定位](#兩個項目的差異與定位)
- [關鍵洞察](#關鍵洞察)

---

## CLI 復興：為什麼是現在

CLI（Command Line Interface）是計算機世界最古老的交互方式，卻正在迎來一次新的爆發。越來越多軟體開始 CLI 化，背後的核心驅動力是 **AI Agent**。

**根本原因**：
- AI 模型的訓練資料充滿命令列操作手冊、shell 腳本 — CLI 是 AI 的「母語」
- GUI 設計給人類（圖標、面板、按鈕），Agent 操作 GUI 需要截圖→視覺模型→解讀→點擊→重複，**慢、脆弱、易錯**
- CLI 提供結構化、可組合、可確定性的互動 — 這正是 Agent 需要的

> 「Asking agents to navigate GUIs designed for humans is ridiculous.」 — *The Register*
> 「讓 Agent 去操作給人類設計的 GUI，是很荒謬的。」

---

## CLI Anything：一行命令讓所有軟體 Agent-Native

**倉庫**：[HKUDS/CLI-Anything](https://github.com/HKUDS/CLI-Anything) | **Stars**：21k+ | **許可證**：Apache 2.0 | **來源**：香港大學數據科學學院

### 核心理念

> 「Today's Software Serves Humans. Tomorrow's Users will be Agents.」

CLI Anything 不是玩具包裝器，而是用**真實軟體後端**（Blender 的 bpy、LibreOffice 的 headless 模式、Audacity 的 sox）自動生成生產級 CLI。

### 7-Phase 自動化 Pipeline

| 階段 | 名稱 | 動作 |
|------|------|------|
| 1 | Codebase Analysis | 掃描源碼，映射 GUI 操作到 API |
| 2 | Architecture Design | 規劃 CLI 結構、狀態模型、輸出格式 |
| 3 | Implementation | 生成 Click-based Python CLI |
| 4 | Test Planning | 定義測試覆蓋範圍 |
| 5 | Test Writing | 生成單元 + E2E 測試 |
| 6 | Documentation | 創建 README、SKILL.md（Agent 可發現性） |
| 7 | Packaging | PyPI-ready 發布包 |

**技術亮點**：1,298 個通過測試，覆蓋 8 個真實應用程式。

### 使用方式

```bash
# Claude Code 中一行命令
/cli-anything ./gimp           # 完整 CLI harness
/cli-anything:refine ./gimp    # 擴展覆蓋範圍

# 或透過 CLI-Hub 安裝現成 harness
pip install cli-anything-hub
cli-hub install blender
cli-hub launch blender --render scene.json --json
```

### 支持的軟體（40+ harnesses）

涵蓋 3D 建模（Blender、FreeCAD）、影像編輯（GIMP、Inkscape、Krita）、視頻剪輯（Kdenlive、Shotcut）、音訊（Audacity、MuseScore）、辦公（LibreOffice）、串流（OBS Studio）、圖表（Draw.io、Mermaid）、遊戲開發（Godot）、GIS（QGIS）等。

### Agent 平台支持

Claude Code（官方 Plugin）、Pi Coding Agent（官方）、OpenCode、Goose、Qodercli、OpenClaw、Codex、Hermes、GitHub Copilot CLI。

---

## OpenCLI：把任何網站變成命令行

**倉庫**：[jackwener/opencli](https://github.com/jackwener/opencli) | **需求**：Node.js >= 20

### 核心理念

CLI Anything 解決的是「有代碼庫的軟體」，OpenCLI 解決的是**沒有代碼庫的場景** — 網站和 Electron 應用。

### 工作原理

1. 安裝 Chrome 擴展（Browser Bridge）
2. 複用你已登入的 Chrome session（零風險、免重新認證）
3. AI Agent 透過 `opencli browser` 原語操控瀏覽器

### 核心能力

```bash
# 人類使用
opencli list                    # 列出可用站點/命令
opencli twitter trending        # Twitter 熱門話題
opencli bilibili hot            # Bilibili 熱門
opencli zhihu hot               # 知乎熱門

# Agent 使用（安裝 skills）
opencli skills install          # 安裝所有 Agent skills
```

### 內建站點支持

- **中文平台**：小紅書、Bilibili、知乎、微信公眾號
- **國際平台**：Twitter/X、Reddit、LinkedIn、Hacker News
- **AI 工具**：Claude、Gemini、NotebookLM
- **桌面應用**：Cursor、Trae、Codex、ChatGPT（Electron）

### Agent Skills 體系

| Skill | 用途 |
|-------|------|
| `opencli-browser` | 即時驅動 Chrome（導航、填表、點擊、提取） |
| `opencli-adapter-author` | 為新站點寫可複用適配器 |
| `opencli-autofix` | 自動修復損壞的適配器 |
| `opencli-browser-sitemap` | 利用站點地圖上下文導航 |
| `opencli-usage` | 所有命令的快速參考 |

### CLI Hub：統一入口

```bash
opencli external register mycli   # 註冊任何本地 CLI 工具
opencli list                       # 統一列出內建 + 外部命令
```

讓 `gh`、`docker`、`longbridge` 等本地工具和 OpenCLI 站點命令共存於同一個 `opencli list` 中。

---

## 官方 CLI 趨勢：巨頭的布局

2025-2026 年，所有主流 AI 廠商都推出了自己的 CLI 編碼 Agent：

| 工具 | 廠商 | 特點 |
|------|------|------|
| **Claude Code** | Anthropic | 全自主 Agent，修改檔案、跑命令、整合 MCP |
| **Codex CLI** | OpenAI | OpenAI 的終端 Agent，支援圖片貼上 |
| **Gemini CLI** | Google | 開源終端 Agent |
| **Copilot CLI** | GitHub/Microsoft | 2025.9 公開預覽，整合 GitHub MCP |
| **Grok Build** | xAI | xAI 的終端編碼工具 |
| **Qwen Code** | 阿里 | 阿里開源 CLI Agent |

共同特徵：
- **終端為中心**：所有操作在命令列完成
- **Agent 自主性**：不只是補全，而是自主修改、測試、提交
- **MCP 整合**：大多支持 MCP（但如 Eric Holmes 所論，CLI 本身可能更優）

---

## MCP vs CLI：終極對比

延續上一則筆記「MCP 之死」的辯論，這支影片從實踐角度補充了 CLI 的具體優勢：

| 維度 | MCP | CLI |
|------|-----|-----|
| **可發現性** | 需要專門的 schema 註冊 | `--help` 自描述，`which` 原生發現 |
| **可組合性** | 各自獨立，難以串接 | 管道、`jq`、`grep` 天然組合 |
| **可調試性** | JSON 傳輸紀錄，需要協定解碼器 | 同一指令人跑一樣的結果 |
| **權限粒度** | 全有或全無 | 精確到操作級別 |
| **運維成本** | 需要背景進程、初始化 | 硬碟上的執行檔，零開銷 |
| **適用範圍** | 無 CLI 的 GUI 工具 | 所有有 API/CLI 的工具 |
| **Agent 友好度** | 結構化但封閉 | 結構化 + 開放 |

**CLI-Anything 和 OpenCLI 的出現，實質上是把 CLI 的優勢推到了原本只有 GUI 的軟體領域**，大幅縮小了 MCP 的適用場景。

---

## 兩個項目的差異與定位

| 維度 | CLI Anything | OpenCLI |
|------|-------------|---------|
| **目標** | 有代碼庫的桌面軟體 | 網站和 Electron 應用 |
| **方法** | 靜態分析源碼 → 生成 CLI wrapper | 複用 Chrome session → 瀏覽器自動化 |
| **輸出** | 結構化 JSON | 結構化 JSON |
| **穩定性** | 高（直連 API，不依賴 DOM） | 中（依賴網站 DOM 結構） |
| **適配成本** | 一次性 7-phase pipeline | 需要維護適配器（有 autofix 輔助） |
| **認證** | N/A（本地軟體） | 複用 Chrome 登入狀態 |
| **互補性** | 桌面軟體 CLI 化 | Web/Electron 應用 CLI 化 |

兩者互補而非競爭：CLI Anything 覆蓋「有代碼的軟體」，OpenCLI 覆蓋「無代碼的網站」。

---

## 關鍵洞察

1. **GUI 的末日尚未到來，但 Agent 的首選介面已經是 CLI**：不是說 GUI 會消失，而是 AI Agent 的主戰場在命令列
2. **CLI 是真正的「萬用插座」**：MCP 最初想做這件事，但 CLI 天生具備這些特性（可組合、可調試、自描述），不需要額外協議
3. **Agent-Native 的定義正在從「有 MCP 伺服器」變成「有 CLI」**：CLI-Anything 的一行命令 `/cli-anything ./gimp` 比 MCP 的整個設定流程簡單太多
4. **巨頭都在押注 CLI**：Claude Code、Codex CLI、Gemini CLI、Copilot CLI 的出現說明行業共識已經形成
5. **未來的軟體設計原則**：先做好 API → 再做好 CLI → Agent 自己搞定。不需要為 AI 造專屬介面
