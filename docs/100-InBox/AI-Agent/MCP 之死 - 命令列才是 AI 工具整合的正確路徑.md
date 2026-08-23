---
title: "MCP 之死 - 命令列才是 AI 工具整合的正確路徑"
aliases:
  - MCP is dead
  - CLI vs MCP
  - MCP vs 命令列
tags:
  - type/learning-note
  - topic/ai-tools
  - topic/MCP
  - topic/CLI
  - topic/philosophy
  - source/youtube
source: "https://youtu.be/0M3wEKvJBcs"
author: "思思主播（原作 Eric Holmes）"
created: "2026-06-07"
updated: "2026-06-07"
description: "基礎設施工程師 Eric Holmes 提出五個工程論據，論證 MCP（Model Context Protocol）是多餘的抽象層，命令列才是 AI 工具整合的正確路徑。從可調試性、可組合性到權限粒度，挑戰「AI 需要專屬協議」的假設。"
level: 4
stars: 5
note: "資料來源：YouTube 字幕（思思主播）+ 原文 MCP is dead. Long live the CLI（ejholmes.github.io）+ 脈報深度文章（heymaibao.com）"
---

## 目錄

- [核心論點](#核心論點)
- [MCP 是什麼，為什麼曾經風靡](#mcp-是什麼為什麼曾經風靡)
- [五個工程論據：命令列為何勝出](#五個工程論據命令列為何勝出)
- [MCP 的實際痛點](#mcp-的實際痛點)
- [MCP 仍然有用的場景](#mcp-仍然有用的場景)
- [論據的盲區與局限](#論據的盲區與局限)
- [根本問題：優先順序](#根本問題優先順序)
- [思考與啟示](#思考與啟示)

---

## 核心論點

**MCP 正在死掉，我們根本不需要它。**

Eric Holmes（基礎設施工程師）的核心主張：為 AI 發明新的工具協議（MCP）是走錯方向。命令列（CLI）已經是 AI 最自然、最高效的互動介面，無需額外抽象層。

> 「Give them a CLI and some docs and they're off to the races.」
> 「給它一個命令列工具和說明文件，它就能跑起來了。」

這不是「我不喜歡 MCP」的空泛抱怨，而是五個具體的工程論據。

---

## MCP 是什麼，為什麼曾經風靡

**MCP（Model Context Protocol）** 是 Anthropic 在 2024 年底推出的開放協定，目標是讓 AI 模型透過統一介面連接各種外部服務（GitHub、Jira、資料庫等）。

概念很漂亮 — 一個專門為 AI 設計的「萬國轉接頭」，不管什麼外部工具都能用同一套標準溝通。

**業界反應瘋狂**：每家公司都搶著做 MCP 伺服器，當作「AI first」的入場券，大量資源投入新端點、新傳輸格式、新認證機制。

**Holmes 的質疑**：AI 本來就能透過命令列跟這些服務溝通，為什麼還要為 AI 發明新協議？

---

## 五個工程論據：命令列為何勝出

### 1. AI 本來就很會用命令列

- AI 模型的訓練資料裡有**幾十年的命令列操作手冊、Stack Overflow 回答和 GitHub shell 腳本**
- 對 AI 來說，命令列就像它的**母語**
- 即使用了 MCP，還是需要寫一樣的說明文件（工具做什麼、接受什麼參數、什麼時候用）
- **協定沒有省掉任何工作**

### 2. 除錯透明度

- **命令列**：AI 做了什麼，人在電腦上跑同一個指令，看到一模一樣的輸出 — 沒有黑箱
- **MCP**：工具只活在 AI 的對話裡，出錯要翻 JSON 傳輸紀錄才能搞清楚

> 「Debugging shouldn't require a protocol decoder.」
> 「除錯不應該需要一個協定解碼器。」

### 3. 可組合性（Composability）

這是**差距最大**的一點：

- **命令列** = 樂高積木，天生設計來互相組合：管道（pipe）、`jq`（結構化資料處理）、`grep`（篩選）、輸出重導向
- **MCP** = 各自獨立的玩具車，每臺自己跑得好，但難以串成一列火車

**實際例子**：分析大型 Terraform 部署計劃，命令列一行解決：
```bash
terraform plan -json | jq '[.resource_changes[] | select(.change.actions[0] == "no-op" | not)] | length'
```
MCP 呢？要嘛把整個計劃塞進 AI 對話視窗（貴又塞不下），要嘛在 MCP 伺服器裡自己寫篩選邏輯。

### 4. 認證機制已經夠用

MCP 在認證上管太多，試圖在現有門鎖外面再蓋一層新的門禁系統：

| 工具 | 認證方式 | 修復方式 |
|------|----------|----------|
| `aws` | Profiles + SSO | `aws sso login` |
| `gh` | `gh auth login` | `gh auth refresh` |
| `kubectl` | kubeconfig | 同上 |

不管人還是 AI 在操作，認證流程都一樣。不需要另學一套 MCP 專屬除錯方式。

### 5. 零運維開銷

- **命令列工具**：就是硬碟上的執行檔，不需要背景進程、不需要管理狀態、不需要初始化流程。需要的時候在，不需要的時候不礙事
- **MCP 伺服器**：需要啟動、維持運行、不能掛掉的進程。以子進程方式在 Claude Code 中運行，脆弱

---

## MCP 的實際痛點

Holmes 列出了日常使用的具體問題：

1. **初始化不穩定**：MCP 伺服器經常啟動失敗，導致 Claude Code 重啟，有時需要完全清除狀態
2. **無盡的重新認證**：每個 MCP 工具需要單獨認證；而命令列工具用 SSO/長效憑證一次認證即可
3. **全有或全無的權限**：Claude Code 對 MCP 工具的 allowlist 只能按工具名稱，無法細化到操作級別

**權限粒度對比**：
- CLI：可以設定 `gh pr view` 自動通過，但 `gh pr merge` 需要人工審批
- MCP：只有「全開」和「全關」兩種選擇

---

## MCP 仍然有用的場景

Holmes 承認 MCP 並非毫無用處：

- 當一個工具**真的沒有命令列替代品**時，MCP 可能是正確選擇
- 某些場景下標準化介面可能有價值
- 他自己日常也還在用一些 MCP 工具

**但沒有深入討論「沒有命令列替代品」的場景佔多大比例。**

---

## 論據的盲區與局限

脈報文章客觀地指出了 Holmes 論述的薄弱環節：

1. **背景偏見**：Holmes 是基礎設施工程師，日常用的工具（AWS、GitHub、Terraform、Kubernetes）都有**非常成熟的命令列生態**。推廣到所有 AI 使用場景時需要打折扣
2. **GUI/無 API 服務**：對於圖形介面為主、或根本沒有開放 API 的工具，命令列路徑可能**根本不存在**。MCP 在這些場景不是替代品，而是**唯一選項**
3. **場景覆蓋不足**：「沒有命令列替代品」的比例有多大？Holmes 沒有給出數據

---

## 根本問題：優先順序

Holmes 最後的呼籲：

> 「If you're a company investing in an MCP server but you don't have an official CLI, stop and rethink what you're doing. Ship a good API, then ship a good CLI. The agents will figure it out.」

這把討論從「MCP 和 CLI 哪個好」拉到更根本的問題：

**與其花資源做一個只有 AI 能用的介面，不如做一個人和 AI 都能用的工具。**

> 「The best tools are the ones that work for both humans and machines.」

這才是真正的「AI first」。

---

## 思考與啟示

1. **不要為 AI 重新發明輪子**：AI 的學習能力足以適應人類已經用得很習慣的工具，不需要專門為它造新介面
2. **命令列的隱藏價值**：幾十年的設計迭代讓 CLI 具備了可組合性、可調試性、標準化認證等特性，這些是任何新協議短期內無法超越的
3. **AI first 的真正含義**：不是「只給 AI 用」，而是「人和 AI 都能用」
4. **反概念的陷阱**：在 AI 浪潮中，有時候太急著追逐新概念（MCP、ACP...），反而忘了打造好工具的根本原則 — 簡單、透明、可組合
5. **權限粒度是關鍵差異**：CLI 能做到操作級別的精細控制，而 MCP 的全有全無模式在生產環境中是硬傷
