---
title: "Google Workspace CLI (GWS) 完整教學指南"
aliases: ["GWS CLI", "Google Workspace CLI", "gws 命令行工具"]
tags: [Google, CLI, 自動化, MCP, Claude, Drive, Gmail, Calendar]
source: ["https://www.youtube.com/watch?v=QZK9NcaQ31s", "https://github.com/googleworkspace/cli", "https://www.npmjs.com/package/@googleworkspace/cli"]
author: YAHA学堂
created: 2026-03-08 14:30
updated: 2026-03-08 14:30
description: |
  Google Workspace CLI (GWS) 是 Google 於 2026 年 3 月發布的開源命令行工具，
  可用單一命令操作 20+ Google 服務，支援 MCP Server 整合 Claude Desktop。
level: beginner
stars: 5
tags:
  - status/active
  - area/distill
  - type/doc
---

# Google Workspace CLI (GWS) 完整教學指南

## 目錄

- [簡介](#簡介)
- [安裝與設置](#安裝與設置)
- [OAuth 認證配置](#oauth-認證配置)
- [Drive 文件管理](#drive-文件管理)
- [Gmail 郵件操作](#gmail-郵件操作)
- [MCP Server 整合](#mcp-server-整合)
- [常用命令速查](#常用命令速查)

---

## 相关笔记

- [[MOC-AI编程工具|AI 编程工具 MOC]]
- [[MCP servers|MCP Servers]]
- [[Claude Code 使用指南]]
- [[Google-Workspace-Agent-集成完整指南]]
- [[GWS-CLI-Google-Workspace命令行工具|GWS CLI 学习笔记]]
- [[Pi-Mono-CLI工具指南|Pi-Mono CLI]]
- [工具對比分析](#工具對比分析)
- [常見問題與解決](#常見問題與解決)
- [參考資料](#參考資料)

---

## 簡介

### 什麼是 Google Workspace CLI (GWS)？

**Google Workspace CLI (gws)** 是 Google 於 2026 年 3 月新發布的開源命令行工具，讓開發者和系統管理員可以通過單一命令操作超過 20 種 Google 服務。

### 核心特色

| 特色 | 說明 |
|------|------|
| **一條命令操作** | 無需為每個 API 編寫 SDK 和認證代碼 |
| **動態命令構建** | 通過 Google Discovery Service 自動生成新命令 |
| **多種輸出格式** | JSON、Table、YAML、CSV |
| **40+ Agent Skills** | 內建跨服務工作流程 |
| **MCP Server** | 讓 Claude Desktop / Gemini CLI 直接控制 |

### 支援的服務

```
┌─────────────────────────────────────────────────────────────┐
│                    GWS CLI 支援的服務                        │
├─────────────────────────────────────────────────────────────┤
│  📁 Drive    │ 📧 Gmail    │ 📅 Calendar  │ 📊 Spreadsheet │
│  📝 Docs      │ 📋 Forms    │ 💬 Chat      │ 🎥 Meet        │
│  👥 Admin     │ 📁 Shared Drives │ 🔐 Cloud Identity     │
│  📱 Chrome    │ 📊 Looker Studio │ 🏷️ Tag Manager        │
│  📺 YouTube   │ 📝 Keep     │ ✅ Tasks     │ 👤 Contacts    │
└─────────────────────────────────────────────────────────────┘
```

### 相關資源

- **GitHub**: https://github.com/googleworkspace/cli (13.9k ⭐)
- **npm**: https://www.npmjs.com/package/@googleworkspace/cli
- **Google Cloud Console**: https://console.cloud.google.com

---

## 安裝與設置

### 安裝方式

```bash
# npm 全域安裝（推薦）
npm install -g @googleworkspace/cli

# 驗證安裝
gws --version
gws --help
```

### 命令結構

```bash
gws <service> <resource> [sub-resource] <method> [flags]
```

**範例**:
```bash
gws drive files list --params '{"pageSize": 10}'
gws gmail users messages list --params '{"userId": "me"}'
```

### 探索命令

```bash
# 查看幫助
gws --help
gws <service> --help
gws <service> <resource> --help

# 查看方法結構描述
gws schema drive.permissions.create
gws schema gmail.users.messages.list
```

---

## OAuth 認證配置

### 方式一：自動設置（推薦）

```bash
# 一鍵設置：自動創建 Cloud 專案、啟用 API、完成登入
gws auth setup
```

此命令會：
- 創建 Google Cloud 專案
- 啟用所需的 API
- 引導完成 OAuth 登入

### 方式二：後續登入

```bash
# 重新登入
gws auth login
```

### 方式三：無頭環境 / CI 環境

```bash
# 匯出憑證
gws auth export --unmasked > credentials.json

# 在無頭機器上使用
export GOOGLE_WORKSPACE_CLI_CREDENTIALS_FILE=/path/to/credentials.json
gws drive files list
```

### 方式四：服務帳號認證

```bash
# 使用服務帳號
export GOOGLE_WORKSPACE_CLI_CREDENTIALS_FILE=/path/to/service-account.json

# 網域範圍委派（模擬使用者）
export GOOGLE_WORKSPACE_CLI_IMPERSONATED_USER=admin@example.com
```

### 方式五：使用 gcloud Token

```bash
# 從 gcloud 取得 token
export GOOGLE_WORKSPACE_CLI_TOKEN=$(gcloud auth print-access-token)
gws drive files list
```

### 認證流程圖

```
┌─────────────────────────────────────────────────────────────┐
│                    OAuth 認證流程選擇                         │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
              ┌───────────────────────────────┐
              │  首次使用？                    │
              └───────────────────────────────┘
                    │                │
                   是               否
                    │                │
                    ▼                ▼
        ┌─────────────────┐  ┌─────────────────┐
        │  gws auth setup │  │  gws auth login │
        │  (自動設置)      │  │  (重新登入)      │
        └─────────────────┘  └─────────────────┘
                    │                │
                    └───────┬────────┘
                            │
                            ▼
              ┌───────────────────────────────┐
              │  CI/無頭環境？                 │
              └───────────────────────────────┘
                    │                │
                   是               否
                    │                │
                    ▼                ▼
        ┌─────────────────┐  ┌─────────────────┐
        │  匯出憑證檔案    │  │  開始使用 CLI   │
        │  設定環境變數    │  └─────────────────┘
        └─────────────────┘
```

---

## Drive 文件管理

### 輸出格式控制

```bash
# JSON 格式（預設）
gws drive files list --params '{"pageSize": 5}'

# 表格格式
gws calendar +agenda --format table

# YAML 格式
gws admin users get --params '{"userKey": "user@example.com"}' --format yaml

# CSV 格式
gws gmail +triage --format csv
```

### 列出檔案

```bash
# 基本列表
gws drive files list --params '{"pageSize": 10}'

# 指定欄位（提高效率）
gws drive files list --params '{"pageSize": 10}' --fields "files(id,name,mimeType)"

# 搜尋檔案
gws drive files list --params '{"q": "name contains \"Report\"", "pageSize": 10}'
```

### 分頁處理

```bash
# 自動分頁（輸出 NDJSON 格式）
gws drive files list --params '{"pageSize": 100}' --page-all

# 限制分頁數量
gws drive files list --page-all --page-limit 5

# 分頁間延遲（毫秒）
gws drive files list --page-all --page-delay 200

# 搭配 jq 處理
gws drive files list --params '{"pageSize": 100}' --page-all | jq -r '.files[].name'
```

### 上傳檔案

```bash
# 簡易上傳
gws drive +upload ./report.pdf

# 上傳到指定資料夾
gws drive +upload ./report.pdf --parent FOLDER_ID

# 上傳並指定名稱
gws drive +upload ./data.csv --name 'Sales Data.csv'

# 使用 multipart 上傳
gws drive files create --json '{"name": "report.pdf"}' --upload ./report.pdf
```

### 下載與匯出

```bash
# 下載檔案
gws drive files get \
  --params '{"fileId": "FILE_ID", "alt": "media"}' \
  --output ./downloaded.pdf

# 匯出 Google 文件為 PDF
gws drive files export \
  --params '{"fileId": "DOC_ID", "mimeType": "application/pdf"}' \
  --output ./document.pdf
```

### 建立資料夾與分享

```bash
# 建立資料夾
gws drive files create \
  --json '{"name": "Project Files", "mimeType": "application/vnd.google-apps.folder"}'

# 分享檔案
gws drive permissions create \
  --params '{"fileId": "FILE_ID"}' \
  --json '{"role": "reader", "type": "user", "emailAddress": "user@example.com"}'
```

### 共用雲端硬碟

```bash
# 列出共用雲端硬碟
gws drive drives list --params '{"pageSize": 10}'
```

---

## Gmail 郵件操作

### 發送郵件

```bash
# 簡易發送
gws gmail +send --to alice@example.com --subject 'Hello' --body 'Hi Alice!'

# 發送原始郵件（RFC 2822 base64 編碼）
gws gmail users messages send \
  --params '{"userId": "me"}' \
  --json '{"raw": "BASE64_ENCODED_MESSAGE"}'
```

### 收件匣管理

```bash
# 顯示未讀郵件摘要
gws gmail +triage

# 自訂查詢條件
gws gmail +triage --max 5 --query 'from:boss'

# 顯示標籤
gws gmail +triage --labels --format table
```

### 郵件列表與搜尋

```bash
# 取得使用者資料
gws gmail users getProfile --params '{"userId": "me"}'

# 列出郵件
gws gmail users messages list --params '{"userId": "me", "maxResults": 10}'

# 搜尋郵件
gws gmail users messages list \
  --params '{"userId": "me", "q": "is:unread from:important"}'

# 取得郵件詳情
gws gmail users messages get --params '{"userId": "me", "id": "MSG_ID"}'
```

### 標籤管理

```bash
# 列出標籤
gws gmail users labels list --params '{"userId": "me"}'
```

---

## MCP Server 整合

### 什麼是 MCP？

**Model Context Protocol (MCP)** 是由 Anthropic 開發的開源協議，讓 AI 工具（如 Claude Desktop）能夠安全地與外部服務互動。

```
┌─────────────────────────────────────────────────────────────┐
│                    MCP 架構示意圖                            │
└─────────────────────────────────────────────────────────────┘

┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│              │     │              │     │              │
│  Claude      │◄───►│  MCP         │◄───►│  Google      │
│  Desktop     │     │  Server      │     │  Workspace   │
│              │     │  (GWS)       │     │  API         │
└──────────────┘     └──────────────┘     └──────────────┘
                            │
                            ▼
                     ┌──────────────┐
                     │  本地配置     │
                     │  claude_desktop_config.json
                     └──────────────┘
```

### Claude Desktop 配置

配置文件位置：`~/Library/Application Support/Claude/claude_desktop_config.json`

```json
{
  "mcpServers": {
    "google-workspace": {
      "command": "gws",
      "args": ["mcp"],
      "env": {
        "GOOGLE_WORKSPACE_CLI_CREDENTIALS_FILE": "/path/to/credentials.json"
      }
    }
  }
}
```

### 啟用 MCP Server

```bash
# 啟動 MCP Server
gws mcp

# 或指定傳輸方式
gws mcp --transport stdio
```

### 使用場景

配置完成後，可以直接對 Claude Desktop 說：

- 「幫我列出 Google Drive 中所有 PDF 檔案」
- 「發送郵件給 team@example.com，標題是週報」
- 「查看我明天的行事曆」
- 「建立一個新的 Spreadsheet」

### MCP 安全性考量

| 安全措施 | 說明 |
|----------|------|
| **API 金鑰管理** | 使用環境變數，不要寫入程式碼 |
| **OAuth 2.0** | 使用最小權限原則 |
| **權限範圍控制** | 只授予必要的 API 權限 |
| **審計日誌** | 啟用 Google Cloud 審計記錄 |
| **憑證輪換** | 定期更新服務帳號金鑰 |

---

## 常用命令速查

### 關鍵旗標速查表

| 旗標 | 說明 | 範例 |
|------|------|------|
| `--params '<JSON>'` | URL/查詢參數 | `--params '{"pageSize": 10}'` |
| `--json '<JSON>'` | 請求主體（POST/PUT/PATCH） | `--json '{"name": "file.pdf"}'` |
| `--page-all` | 自動分頁，輸出 NDJSON | `--page-all \| jq '.files[]'` |
| `--fields '<MASK>'` | 限制回應欄位 | `--fields "files(id,name)"` |
| `--upload <PATH>` | 上傳檔案路徑 | `--upload ./report.pdf` |
| `--output <PATH>` | 下載檔案目標路徑 | `--output ./downloaded.pdf` |
| `--format <FORMAT>` | 輸出格式 | `--format table/yaml/csv/json` |

### jq 整合範例

```bash
# 列出所有檔案名稱
gws drive files list --params '{"pageSize": 100}' --page-all | jq -r '.files[].name'

# 篩選特定類型檔案
gws drive files list --page-all | jq '.files[] | select(.mimeType == "application/pdf")'

# 取得檔案 ID 和名稱對照
gws drive files list --page-all | jq -r '.files[] | "\(.id)\t\(.name)"'

# 統計檔案數量
gws drive files list --page-all | jq '[.files[]] | length'

# Gmail 搜尋並格式化輸出
gws gmail users messages list --params '{"userId": "me", "q": "is:unread"}' | jq '.messages[] | .id'
```

---

## 工具對比分析

### GWS CLI vs 其他工具

| 對比項 | GWS CLI | GAM | Google API SDK |
|--------|---------|-----|----------------|
| **維護者** | Google 官方 | 社區 | Google 官方 |
| **安裝方式** | npm | pip / MSI | 各語言 SDK |
| **學習曲線** | 低 | 中 | 高 |
| **動態命令** | ✅ | ❌ | ❌ |
| **MCP 支援** | ✅ | ❌ | 需自行實現 |
| **批量操作** | ✅ | ✅ | 需編寫 |
| **適用場景** | 快速操作 | 管理員批量 | 客製化開發 |

### 選擇建議

```
┌─────────────────────────────────────────────────────────────┐
│                    工具選擇決策樹                            │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
              ┌───────────────────────────────┐
              │  需要 AI 工具整合？            │
              └───────────────────────────────┘
                    │                │
                   是               否
                    │                │
                    ▼                ▼
        ┌─────────────────┐  ┌─────────────────────────────┐
        │  【GWS CLI】    │  │  需要批量管理使用者/群組？   │
        │  (MCP 內建)     │  └─────────────────────────────┘
        └─────────────────┘        │                │
                                  是               否
                                   │                │
                                   ▼                ▼
                       ┌─────────────────┐  ┌─────────────────┐
                       │  【GAM】        │  │  需要客製化？    │
                       │  (管理員專用)   │  └─────────────────┘
                       └─────────────────┘        │                │
                                                 是               否
                                                  │                │
                                                  ▼                ▼
                                      ┌─────────────────┐  ┌─────────────────┐
                                      │  【API SDK】    │  │  【GWS CLI】    │
                                      │  (完全控制)     │  │  (快速簡單)     │
                                      └─────────────────┘  └─────────────────┘
```

---

## 常見問題與解決

### 認證問題

| 錯誤訊息 | 原因 | 解決方案 |
|----------|------|----------|
| `credentials not found` | 未完成認證 | 執行 `gws auth setup` |
| `insufficient permissions` | 權限不足 | 在 Cloud Console 啟用 API |
| `token expired` | Token 過期 | 執行 `gws auth login` |

### MCP Server 問題

| 問題 | 解決方案 |
|------|----------|
| Server 未顯示 | 檢查 JSON 配置格式 |
| 工具調用失敗 | 確認憑證路徑正確 |
| API 錯誤 | 檢查 API 是否啟用 |

### 除錯技巧

```bash
# 使用 MCP Inspector 測試
npx @modelcontextprotocol/inspector gws mcp

# 查看詳細錯誤
gws drive files list --verbose
```

---

## 參考資料

### 官方資源

- [GWS GitHub Repository](https://github.com/googleworkspace/cli)
- [GWS npm Package](https://www.npmjs.com/package/@googleworkspace/cli)
- [Google Cloud Console](https://console.cloud.google.com)
- [MCP 官方文檔](https://modelcontextprotocol.io)

### 影片資源

- [YouTube 教學影片](https://www.youtube.com/watch?v=QZK9NcaQ31s) - YAHA学堂

### 相關工具

- [Claude Desktop](https://claude.ai/download)
- [GAM (Google Apps Manager)](https://github.com/GAM-team/GAM)

---

## 影片章節索引

| 時間 | 章節 |
|------|------|
| 0:00 | 開場：用單一命令控制 Google Workspace |
| 0:31 | 安裝 GWS CLI (npm install) |
| 1:02 | OAuth 認證設置（gcloud 自動方式） |
| 1:31 | OAuth 認證設置（手動方式） |
| 3:59 | 實作：Drive 文件管理（列表、搜尋、分頁、上傳） |
| 4:54 | 實作：Gmail 郵件發送與接收（+triage, +send 快捷方式） |
| 6:07 | MCP Server 設置：讓 Claude Desktop 操作你的 Google Workspace |
| 7:12 | 常見陷阱與注意事項 |

---

> **整理說明**：本筆記基於 YAHA学堂 的 YouTube 教學影片整理，並補充了官方文檔、實踐範例和工具對比分析。影片無字幕，內容根據影片描述和網路資料補充完成。
