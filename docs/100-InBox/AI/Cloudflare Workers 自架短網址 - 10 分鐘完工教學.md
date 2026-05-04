---
title: Cloudflare Workers 自架短網址 - 10 分鐘完工教學
created: 2026-04-27
source: https://www.youtube.com/watch?v=abEg58rzltE
author: YAHA学堂
tags:
  - cloudflare
  - workers
  - url-shortener
  - serverless
type: video-note
---

# Cloudflare Workers 自架短網址 - 10 分鐘完工教學

> Bitly 每月 5 美金，用 40 行程式碼自己建一個，部署到全球 300 節點，完全免費。

## 章節摘要

### 建立 Cloudflare Workers 專案（兩行指令搞定）

兩行指令 30 秒建立專案，回答幾個問題即可。重點關注  的 （Worker 名稱）和 （程式進入點），其他設定保持預設。

### KV 資料庫是什麼？為什麼短網址用它最適合

Cloudflare KV（Key-Value Store）是簡單的鍵值資料庫，給一個 key 回傳一個 value，完美適合儲存短網址的對應關係。

### 建立 KV namespace、設定 wrangler.jsonc

用指令建立 KV namespace，會輸出包含 ID 的配置。將其貼到 wrangler 設定檔的  區段。 是程式碼中存取 KV 的變數名稱。

**免費額度**：寫入每日 1,000 次（注意是天不是月）、讀取每日 100,000 次，個人使用綽綽有餘。

### 生 TypeScript 型別定義（一個指令）

執行指令自動產生 Env 介面（含 ）。

### 寫核心程式碼：POST 建立短網址、GET 做 redirect

短網址邏輯只有兩件事：
1. **POST**：接收長 URL → 生成短碼 → 存入 KV
2. **GET**：用短碼查 KV → 取得原始 URL → 302 redirect

### 我踩過的雷：沒做衝突檢查，舊連結被覆蓋掉

**必須做衝突檢查**：儲存前先檢查短碼是否已存在，已存在則回傳 409。作者第一次漏掉這個檢查，導致舊連結被覆蓋，花了半小時才定位到問題。

### 用 curl 實測 API：建立、redirect、衝突保護、404

- 建立短網址成功回傳 
- 重複短碼觸發 409 衝突保護
- 未指定短碼自動生成 6 位隨機碼
- 不存在的短碼回傳 404

### 加一個漂亮的前端頁面（直接在 Worker 回傳 HTML）

Workers 可以直接回傳 HTML，不需要額外框架。在同一個 index 檔中加入 HTML，提供輸入框 + 按鈕，貼上 URL 一鍵生成短連結並支援一鍵複製。

### 一行指令部署到全球 300 個節點

 一行指令推上線。部署後頁面外觀與本地一致，但運行在 Cloudflare 全球網路上。

**免費額度**：每日 100,000 次請求，個人使用不會觸及上限。

### 綁自己的網域：Custom Domain vs Routes 怎麼選

兩種綁定方式：
- **Custom Domain**：整個網域交由 Worker 處理（短網址推薦用這個）
- **Routes**：只攔截特定路徑

在 wrangler 設定檔加入 routes 配置，設定 ，Cloudflare 自動處理 DNS 和 SSL。也可直接在 Cloudflare 網站操作：Worker → Settings → Domains and Routes → Add。

### 整支影片總結：完全免費、可控、可擴充

從零開始用 Cloudflare Workers + KV 建立完整的短網址服務，包含後端 API、前端頁面、一鍵部署、自訂網域，全程免費。

**注意**：目前沒有認證保護，任何人知道 URL 都能建立短連結。個人使用沒問題，不建議直接上生產環境。下一集會用 D1 資料庫加入點擊追蹤和統計儀表板。

## 關鍵技術點

- **Cloudflare Workers**：Serverless 邊緣運算平台
- **KV**：分散式 Key-Value 資料庫，適合低延遲讀取
- **TypeScript**：Worker 開發語言
- **wrangler**：Cloudflare Workers CLI 工具
- **免費額度**：寫入 1,000 次/天、讀取 100,000 次/天、請求 100,000 次/天

## 下一步預告

用 Cloudflare D1（SQLite 資料庫）加入點擊追蹤和分析儀表板，追蹤誰在什麼時候點擊了短連結。
