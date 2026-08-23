---
title: Anthropic 倫敦 Keynote：Claude 開始 prompt Claude
aliases:
  - Code with Claude London 2026
  - Claude prompt Claude
tags:
  - anthropic
  - claude-code
  - ai-agent
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=aPpa7n77xwY"
  - "https://heymaibao.com/anthropic-london-keynote-claude-prompts-claude/"
  - "https://www.youtube.com/watch?v=6amLO7I9xdg"
author: 思思主播（摘要） / Boris Cherny, Lisa, Cat（原講者）
created: 2026-05-27
updated: 2026-05-27
description: |
  Anthropic 倫敦 Code with Claude 2026 開場 Keynote 重點整理：三層敘事架構（模型→平台→Claude Code）、Task Horizon 指標、Managed Agents 新功能、以及「Claude prompt Claude」的範式轉移。
level: intermediate
stars: 4
---

# Anthropic 倫敦 Keynote：Claude 開始 prompt Claude

> Anthropic 在倫敦舉辦 Code with Claude 2026 開場 Keynote 的深度摘要。核心理念：模型能力走指數、企業採用走線性、差距由開發者填補。適合關注 AI 編程工具趨勢的開發者和技術管理者閱讀。

---

## 核心矛盾：指數 vs 線性

```
模型能力  ╱╱╱╱╱╱╱╱╱  ← 指數級成長（12 個月出 8 個 frontier 模型）
          ╱╱╱
        ╱╱
      ╱
企業採用  ───────────  ← 線性成長（prompt 優化、tool 設計、harness 工程都得處理）
```

差距由誰填補？**開發者**。Keynote 的三層敘事架構正是回答這個問題。

---

## 三層敘事架構

```
┌─────────────────────────────────────┐
│  第三層：Claude Code（開發者工作區）  │  ← 你每天用的東西
├─────────────────────────────────────┤
│  第二層：Platform（平台基礎設施）    │  ← 企業落地的水管
├─────────────────────────────────────┤
│  第一層：Foundation Models（基礎模型）│  ← 驅動一切的引擎
└─────────────────────────────────────┘
```

---

## 第一層：Foundation Models — 指數級引擎

### 模型發布時間線

| 模型 | 意義 |
|------|------|
| Opus 3 | 第一個能寫長型程式碼的 Claude |
| Sonnet 3.5 | 第一個能安全使用電腦的 Claude |
| Sonnet 3.7 | 第一個會在回答前先思考的 Claude |
| Opus 4 | 第一個能寫複雜 Excel 與 PowerPoint 的 Claude |
| Opus 4.7 | 當前旗艦 |
| Mythos (preview) | 讀完整份 OpenBSD source tree，找到存活 27 年的漏洞 |

### Task Horizon — 唯一需要關注的指標

定義：模型在失去脈絡、開始胡言亂語或卡住之前，能自主執行任務的時間長度。

```
2025 年      2026 年        未來目標
  │            │              │
分鐘級  ──→  小時級  ──→  always-on
  │            │              │
寫單一功能    端到端專案     持續運行、
草擬 email    徹夜執行       主動處理高層目標
```

### Lisa 給開發者的三個建議

1. **為下一版 Claude 設計，不是當下版本** — 模型外的 loop、指令、tool 稱為 scaffolding，模型變強後，舊 scaffold 會綁住它。長線贏家是架構留有「下一次跳躍」空間的人
2. **持續做更難的 eval 與 prototype** — 保留舊的失敗測試，哪天過了就是該出貨「以前出不了的東西」的訊號
3. **把模型升級當成商業機會** — 自動化 eval + 人工 hands-on 試新版本，升級的真正價值要動手才看得到

### Mythos OpenBSD 案例 — 值得追蹤但需保留懷疑

- Anthropic 自稱 Mythos 讀完 OpenBSD 全部原始碼，找到存活 27 年的漏洞
- 無 CVE 編號、無第三方驗證
- 訊號清楚（模型能做以前只能仰賴頂尖人類專家的事），但目前定位為「Anthropic 自家聲稱」

---

## 第二層：Platform — 讓企業跑得起來

### Advisor Strategy（ROI 海克秘籍）

```
                    ┌──────────────┐
  高階指令 ────────→│   Opus（顧問）│ ← 只在難題時呼叫
                    └──────┬───────┘
                           │ 指導 + 把關品質
                    ┌──────▼───────┐
  大量執行 ────────→│Sonnet/Haiku  │ ← 幹苦力活
  （低成本）         │ （執行者）    │
                    └──────────────┘
```

- Eve Legal 個案：frontier 等級品質 + 5 倍成本下降
- 判斷：視 ROI 與工作量分布決定是否複現

### 兩個新發佈

| 功能 | 做什麼 | 支援平台 |
|------|--------|----------|
| Self-hosted Sandboxes | Claude Managed Agents 的 sandbox 跑在你自己的伺服器上 | Daytona、Cloudflare、Vercel、Modal |
| MCP Tunnels | MCP server 留在防火牆後，透過 `tunnel.anthropic.com` 安全連接 | 通用 |

```
  公網
   │
   ▼
tunnel.anthropic.com ──→ MCP Server（私網內）
                            │
                            ▼
                    內部 Slack / 資料庫 / Feature Flags
```

這意味著 Anthropic 開始往大企業的安全合規邊界推。**下一階段競爭不只是模型誰強，是誰能讓「強到要小心」的模型在嚴格客戶那邊跑得起來。**

---

## 第三層：Claude Code — 預設值改變了

### 產品 Surface

| 入口 | 定位 | 使用場景 |
|------|------|----------|
| CLI | 純文字、最小 | Power user |
| VS Code 插件 | 同一 agent + 看程式碼變化 | 日常開發 |
| Desktop App | 全螢幕 GUI + 預覽 | 圖形介面使用者 |
| Agents View (CLI) | 一眼所有 session 狀態 | 多任務管理 |
| iOS / Android | 遠端丟任務 | 行動場景 |

### 新功能

| 功能 | 做什麼 | 實際效果 |
|------|--------|----------|
| Code Review | 派 agent 團隊巡 PR 找 bug | Anthropic 內部所有團隊在用 |
| Routines | 排程 / Webhook / API 觸發 | Boris 比作 higher-order function：「你不是寫 prompt，你是寫會寫 prompt 的東西」 |
| CI Autofix | 盯 PR，自動修 review 留言、CI 失敗、merge conflict | Demo 中 CI 紅燈 → routine 自動診斷 retry → 轉綠，工程師沒看到紅 X |
| Claude Security | 過夜掃漏洞 + 依嚴重度排序 + 派 Claude Code 修 | 全 codebase 自動化安全巡檢 |

### Claude Code 自動化工作流

```
                    Webhook / 排程觸發
                           │
                           ▼
  ┌──────────┐      ┌─────────────┐
  │ Routines │─────→│ Claude Code │ ← 自動執行任務
  └──────────┘      └──────┬──────┘
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
        ┌──────────┐ ┌──────────┐ ┌──────────┐
        │CI Autofix│ │Security  │ │Code      │
        │          │ │Scan      │ │Review    │
        └──────────┘ └──────────┘ └──────────┘
              │            │            │
              └────────────┼────────────┘
                           ▼
              早上端咖啡審查成品
```

### 客戶規模對照

| 公司 | 規模 | Claude Code 成果 |
|------|------|------------------|
| Spotify | 大型 | 背景吃白話 migration 規格，每月合併 1,000+ PR，migration 時間砍 90%+ |
| Mercado Libre | 1 億買家、2.3 萬工程師 | 審 50 萬 PR、現代化 9,000 app，目標 2026 Q3 達 90% autonomous coding |
| Anthropic（自用） | 內部 dogfood | PR per engineer +200%（注意：wall-to-wall 採用條件，不一定外推） |

---

## 核心轉折：「Claude prompt Claude」

> 「The default isn't 'I'm going to prompt Claude' — the default is now 'I'm going to have Claude prompt itself.」
> — Boris Cherny

這是整場 Keynote 最核心的訊號。

```
舊範式                    新範式
┌──────────┐              ┌──────────┐
│  人類    │──prompt──→   │  Claude  │──prompt──→  Claude Code
│（寫每一行│              │（人類只   │              （自主執行）
│  prompt）│              │ 給高階指令│
└──────────┘              └──────────┘
```

### 對 Prompt 工程的影響

| 維度 | 舊思維 | 新思維 |
|------|--------|--------|
| 技能焦點 | 寫好單句 prompt | 設計 routine 與 orchestration |
| 角色定位 | AI 的操作者 | AI 管理員的管理員 |
| 產出物 | prompt 文字 | 可觸發的自動化流程 |
| 價值所在 | 精確的指令 | 高層目標的拆解與委派 |

---

## 該保留的懷疑

- ✅ Anthropic 內部 dogfood 數字（PR +200%）不能外推到小團隊
- ✅ Mythos OpenBSD 案例值得追蹤但目前無第三方驗證
- ✅ always-on agent 是未來預測，不是已交付能力
- ✅ Lisa 說 scaffold 要輕，但同一場在推越來越厚的 scaffold（Routines、CI autofix）— 這不是矛盾，是給不同層的人：模型內越來越輕，模型外的「協調這群 agent」越來越厚

---

## 個人可立即行動的事

1. **把 eval 自動化** — 保留舊失敗測試，模型升級時自動跑，過了就是出貨訊號
2. **架構為下一版 Claude 留空間** — scaffold 別包太死，預留「下一次跳躍」空間
3. **從寫 prompt 升級到寫 routine** — 如果你還在手動一行一行打 prompt，那個時代已經結束

---

## 參考資料

- [Code with Claude London 2026: Opening Keynote（原始 Keynote）](https://www.youtube.com/watch?v=6amLO7I9xdg)
- [Anthropic 倫敦 keynote：Claude 開始 prompt Claude（思思主播深度文章）](https://heymaibao.com/anthropic-london-keynote-claude-prompts-claude/)
- [Notes from Code with Claude 2026 - Chris Ebert](https://chrisebert.net/notes-from-code-with-claude-2026/)
- [Live blog: Code w/ Claude 2026 - Simon Willison](https://simonwillison.net/2026/May/6/code-w-claude-2026/)
- [MIT Technology Review: Anthropic's Code with Claude showed off coding's future](https://www.technologyreview.com/2026/05/21/1137735/anthropics-code-with-claude-showed-off-codings-future-whether-you-like-it-or-not/)
- [Fortune: Anthropic lands in London](https://fortune.com/2026/05/21/claude-code-london-anthropic-ai-software-engineering/)
