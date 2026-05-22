---
title: "GPT-5 Pro 工作流 - 手與大腦的分工哲學"
aliases:
  - "ChatGPT Pro 正確使用方式"
  - "Oracle CLI 工作流"
tags:
  - ai/workflow
  - tool/chatgpt-pro
  - tool/oracle-cli
  - status/active
source:
  - https://www.youtube.com/watch?v=qyVzitO18dQ
author:
  - 思思主播 (@heymaibao)
  - Aniket Panjwani (經濟學家、AI 顧問)
created: 2026-05-18
updated: 2026-05-18
description: 經濟學家 Aniket Panjwani 提出 ChatGPT Pro 的重新分類：Pro 不是「更快的手」，而是「大腦」。配合 Oracle CLI 打包 context，透過 Codex Chrome 擴充功能自動送進 ChatGPT 網頁，實現 Codex（手）+ Pro（大腦）的聯動工作流。
level: intermediate
stars: 3
---

## 核心觀點

### Pro 的重新分類

大多數人把 ChatGPT Pro 當成「升級版跑得更快的同一台機器」，但 Aniket Panjwani 指出：

> **Pro 不是更貴的 ChatGPT，它是一個完全不同的工具，本來就該負責完全不同的任務。**

用器官比喻：
- **手** = Thinking Model、ChatGPT Codex、Claude Code — 快、靈活、擅長執行任務，可以高頻互動
- **大腦** = GPT-5 Pro — 深思熟慮、審查計畫、揪出架構深處的邏輯漏洞

Ethan Mollick（華頓商學院）也背書：「如果你正在進行複雜的學術研究，目前市面上沒有任何模型可以取代 Pro。」

### 兩個黃金觸發條件

只在以下情況才把 Pro 請出來：

1. **架構疑慮** — 對現有計畫或技術架構覺得「哪裡怪怪的」，需要客觀評估各種取捨
2. **高複雜度 + 高風險** — 主題太複雜，漏掉一個細節後續代價慘痛

### 實際案例

- **經濟學**：一般資料分析用普通模型；結構性估計（structural estimation）這類連頂尖期刊作者都會踩雷的問題，Pro 能精準抓出盲點
- **顧問業**：比對客戶口頭需求 vs 最終提案，找出人類容易漏掉的邏輯斷層
- **軟體開發**：預見一個小改動在龐大系統裡引發的連鎖反應

簡單 CRUD → 交給 Codex 秒殺，連計畫都不用寫。PaySlice（6 微服務 × 3 環境 × 9 元件）→ 必須請 Pro 做全面架構建議。

## 工具鏈：Oracle CLI

**GitHub**: [steipete/oracle](https://github.com/steipete/oracle) ⭐ 2.1k | MIT | TypeScript | v0.9.0

### 作用

自動讀取 `.gitignore`，避開垃圾檔案，把專案精華 + prompt 打包成單一 markdown，字數快爆時提前警告。然後自動送進 ChatGPT 網頁選好 GPT-5 Pro 模型。

### 安裝

```shell
npm install -g @steipete/oracle
# 或
brew install steipete/tap/oracle
# 或直接跑
npx -y @steipete/oracle ...
```

需要 Node 22+。

### 雙引擎

- **API 模式**（設了 `OPENAI_API_KEY` 自動選用）：GPT-5 Pro API 預設 detach 背景，用 `--wait` 阻塞
- **Browser 模式**（無 API key 時自動選用）：自動操作 Chrome，不需 API key

### 關鍵用法

```shell
# 複製 bundle 手動貼到 ChatGPT
npx -y @steipete/oracle --render --copy \
  -p "Review the TS data layer" \
  --file "src/**/*.ts,*/*.test.ts"

# API 模式直接跑
npx -y @steipete/oracle -p "Write architecture note" \
  --file src/storage/README.md

# 多模型對比
npx -y @steipete/oracle -p "Cross-check assumptions" \
  --models gpt-5.1-pro,gemini-3-pro \
  --file "src/**/*.ts"

# Browser 模式（免 API key）
npx -y @steipete/oracle --engine browser \
  -p "Walk through UI smoke test" \
  --file "src/**/*.ts"
```

### 支援模型

- OpenAI：GPT-5.4 Pro（預設）、GPT-5.4、GPT-5.1 Pro、GPT-5.2 Pro 等
- Google：Gemini 3 Pro
- Anthropic：Claude 4.5 Sonnet、Claude 4.1 Opus
- OpenRouter：任意模型

## 自動化工作流

Aniket 的完整流程：

1. **Oracle CLI** 一鍵打包專案 context → 單一 markdown
2. **Codex Chrome 擴充功能** 自動接管瀏覽器 → 開啟 ChatGPT → 選 GPT-5 Pro → 貼上 context → Enter
3. 泡咖啡等 5-30 分鐘
4. Codex 自動把 Pro 的答案帶回開發環境

= 大腦 + 手的完美聯動。

## 當前限制

- Codex Chrome 擴充功能在歐洲地區不可用
- 外掛偶爾抓不到網頁
- Computer Use 路徑會霸佔整個螢幕
- Browser Use 路徑目前不穩定

## 行動建議

同題雙開測試：挑一個最困難的專案，同一 prompt 分別給 Thinking 模式和 Pro，比較 Pro 揪出的底層細節和結構盲點是否值得多等十分鐘。

值得 → 解鎖 Pro 真正威力；不值得 → 省下訂閱費買咖啡。

## 關鍵人物

- **Aniket Panjwani, PhD** — 經濟學家兼 AI 顧問，專門教經濟學家使用 Claude Code / Codex 加速研究（[LinkedIn](https://www.linkedin.com/in/aniket-a-panjwani)、[X @aniketapanjwani](https://x.com/aniketapanjwani)）
- **Ethan Mollick** — 華頓商學院 AI 研究教授
- **steipete** — Oracle CLI 作者（[GitHub](https://github.com/steipete/oracle)）
