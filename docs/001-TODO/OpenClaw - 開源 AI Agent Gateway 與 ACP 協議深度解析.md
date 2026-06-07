---
title: "OpenClaw - 開源 AI Agent Gateway 與 ACP 協議深度解析"
aliases:
  - OpenClaw
  - OpenClaw ACP
  - openclaw
  - Molty
tags:
  - type/learning-note
  - topic/ai-agent
  - topic/chat-integration
  - topic/ACP
  - source/youtube
source: "https://youtu.be/MouBsTufhhU"
author: "思思主播"
created: "2026-06-07"
updated: "2026-06-07"
description: "OpenClaw 是一個開源的自託管 AI Agent Gateway，通過 ACP（Agent Client Protocol）將 Discord、Telegram 等聊天軟體變成 AI 編碼指揮中心，支持 Claude Code、Codex、Gemini CLI 等多種編碼 Agent 的統一調度。"
level: 4
stars: 5
note: "資料來源：YouTube 字幕（思思主播）+ OpenClaw 官方文檔 + GitHub README（377k Stars）+ Nuno Coração 深度報導"
---

## 目錄

- [OpenClaw 是什麼](#openclaw-是什麼)
- [核心概念：爪子與龍蝦](#核心概念爪子與龍蝦)
- [ACP 協議：通用語言](#acp-協議通用語言)
- [ACP vs Subagents 兩種模式](#acp-vs-subagents-兩種模式)
- [執行緒綁定：聊天室變 AI 工作室](#執行緒綁定聊天室變-ai-工作室)
- [Session 控制指令](#session-控制指令)
- [預設權限陷阱與修復](#預設權限陷阱與修復)
- [架構概覽](#架構概覽)
- [支持的通道與平台](#支持的通道與平台)
- [安全模型](#安全模型)
- [項目背景：從 Burnout 到 377k Stars](#項目背景從-burnout-到-377k-stars)
- [技術規格](#技術規格)
- [關鍵洞察與未來展望](#關鍵洞察與未來展望)

---

## OpenClaw 是什麼

OpenClaw（龍蝦）是由 **Peter Steinberger**（@steipete）創建的开源 AI Agent Gateway，以 TypeScript 為主要語言，MIT 許可證。它是一個 **自託管（self-hosted）Gateway**，在你的機器或伺服器上運行單一進程，作為聊天應用和 AI 助手之間的橋樑。

**核心定位**：讓你在日常使用的聊天軟體裡直接調度所有 AI 編碼工具，不需要切換視窗或終端機。

**項目數據**（截至 2026 年 6 月）：
- ⭐ 377k Stars | 🍴 78.9k Forks | 👥 2,325 Contributors
- 最新版本：`openclaw 2026.6.1`
- 257 Tags，57,808 Commits
- 3 個月突破 200k Stars，超越 React

---

## 核心概念：爪子與龍蝦

OpenClaw 用了一個生動的比喻來解釋架構：

- **OpenClaw = 爪子（Claw）**：負責「抓住」外面的各種 AI 編碼工具
- **AI 工具 = 龍蝦（Lobster）**：被抓住的各種 AI 編碼 Agent（Claude Code、Codex、Gemini CLI 等）
- **聊天軟體 = 盤子**：所有龍蝦被抓到盤子上，統一在同一個地方操作

這個設計讓開發者不再需要在終端機、IDE、瀏覽器之間來回切換。

---

## ACP 協議：通用語言

**ACP = Agent Client Protocol（代理客戶端協議）**，是 OpenClaw 背後的核心驅動力。

可以把 ACP 想象成：
- **萬用說明書** — 所有 AI 都聽得懂的通用語言
- **JSON-RPC 2.0 over NDJSON 協議** — 結構化的 Agent 間通信

ACP 的核心作用：
1. 讓 OpenClaw（爪子）與所有不同的 AI 工具（龍蝦）**順暢溝通、下指令**
2. 提供 **Agent-to-Agent** 通信，不再需要 PTY scraping
3. 統一 Claude Code、Codex、Gemini CLI、OpenClaw ACP 等的指令介面

**acpx** 是 ACP 的 headless CLI 客戶端：
- 一個指令介面統一調度 Pi、OpenClaw ACP、Codex、Claude 等 Agent
- 支援 `acpx openclaw exec` 一次性請求
- 支援持久化 session：`acpx openclaw sessions ensure --name codex-bridge`

---

## ACP vs Subagents 兩種模式

| 模式 | 用途 | 指令 |
|------|------|------|
| **ACP** | 啟動外部 AI（Claude Code、Codex、Gemini CLI） | `/acp` |
| **Subagents** | 使用 OpenClaw 內建的小工具 | `/subagents` |

這兩者分開管理，不會搞混。ACP 用於與外部編碼 Agent 通信，Subagents 用於 OpenClaw 內部的輕量任務。

---

## 執行緒綁定：聊天室變 AI 工作室

這是 OpenClaw 最殺手級的功能：

```
/acp start    # 啟動 AI Session，綁定到 Discord 對話串
```

綁定後的效果：
- 該對話串裡的**每一句話**都自動送到**同一個 AI Session**
- 對話脈絡是連續的 — 不再需要複製貼上、重複解釋前文
- 等於給 AI 一個**專屬辦公室**，它記得你們之前聊了什麼
- 從聊天室指揮 AI，就像傳訊息一樣自然

---

## Session 控制指令

| 指令 | 功能 |
|------|------|
| `/acp model` | 即時切換 AI 模型 |
| `/acp timeout` | 調整超時時間 |
| `/acp steer` | AI 偏離時拉回正軌 |
| `/acp cancel` | 取消當前操作 |
| `/acp close` | 完全結束 Session |

這些指令讓開發者對 AI 工作流程有**完整且靈活的控制權**。

---

## 預設權限陷阱與修復

**問題**：OpenClaw 的預設 `permissionMode` 是 `approve-reads`（唯讀權限），在非互動環境下，AI 無法寫檔或執行程式碼，Session 會直接 crash。

**修復**：在配置中將 `permissionMode` 改為 `approve-all`：

```json
{
  "permissionMode": "approve-all"
}
```

這是新手最容易踩的坑，安裝後第一件事就應該改這個設定。

---

## 架構概覽

```
Chat Apps + Plugins → Gateway → Pi Agent / CLI
                                  → Web Control UI
                                  → macOS App
                                  → iOS / Android Nodes
```

**Agent Loop 流程**：
1. **Receive** — 接收來自聊天通道的訊息
2. **Route** — 路由到對應的 Agent（按通道/帳號/對話方隔離）
3. **Think** — 載入上下文和 Skills，發送到 LLM
4. **Act** — 執行工具（browser、canvas、cron、terminal 等）
5. **Persist** — 寫入對話記錄和記憶到 Workspace
6. **Stream** — 串流回覆到聊天通道

**檔案化狀態**：所有配置都是純文本檔案（`SOUL.md`、`IDENTITY.md`、`AGENTS.md`、`TOOLS.md`），可編輯、可搜尋、可版本控制。

---

## 支持的通道與平台

**23+ 個通道**：WhatsApp、Telegram、Slack、Discord、Google Chat、Signal、iMessage、IRC、Microsoft Teams、Matrix、Feishu（飛書）、LINE、Mattermost、Nextcloud Talk、Nostr、Synology Chat、Tlon、Twitch、Zalo、WeChat、QQ、WebChat + 原生 macOS/iOS/Android 應用。

**運行環境**：macOS、Linux、Windows（原生 Windows Hub 應用），通常在 Mac Mini、VPS 或 Raspberry Pi 上 24/7 運行。

**安裝**：
```shell
npm install -g openclaw@latest
openclaw onboard --install-daemon
```

需要 Node 24（推薦）或 Node 22.19+。

---

## 安全模型

**DM 訪問控制**：
- 預設 `dmPolicy="pairing"`：未知發送者需配對驗證
- 公開 DM 需明確設置 `dmPolicy="open"` + 在 allowlist 加入 `"*"`

**沙盒隔離**：
- 非主 Session 可在 Docker 沙盒中運行
- 沙盒默認允許：`bash`、`process`、`read`、`write`、`edit`、`sessions_*`
- 沙盒默認拒絕：`browser`、`canvas`、`nodes`、`cron`、`discord`、`gateway`

---

## 項目背景：從 Burnout 到 377k Stars

OpenClaw 的故事本身就很有啟發性：

1. **PSPDFKit 時代（2011-2021）**：Peter Steinberger 創建了 PSPDFKit，從個人項目成長到 60-70 人的全球遠端團隊，客戶包括 Dropbox、IBM、Volkswagen，影響近 10 億用戶
2. **意義危機（2021-2024）**：退出後經歷三年的存在性漂移，幾乎不碰電腦
3. **AI 輔助編程回歸（2025.4）**：發現 AI 輔助編程，開發「Inference-Speed Shipping」工作流
4. **Clawdbot 原型（2025.11）**：一小時內構建出能跨 WhatsApp、Telegram、Slack 等平台通信的 AI Agent，首日獲 9,000 Stars
5. **商標戰 → OpenClaw（2025.12）**：因商標問題更名
6. **加入 OpenAI（2026）**：Steinberger 加入 OpenAI，OpenClaw 保持開源獨立

**「語音訊息」定義性時刻**：Steinberger 發送了一條語音訊息給原型（當時不支持語音），Agent 自主推理：發現 Opus 格式 → FFmpeg 轉 WAV → 找不到 Whisper → 找到 OpenAI API Key → curl 發送 → 回覆文字。這證明了 Agent 的創造性工具鏈接能力。

---

## 技術規格

| 項目 | 規格 |
|------|------|
| 語言 | TypeScript 91.7%、Swift 3.3%、JavaScript 2.7%、Kotlin 1.0% |
| 許可證 | MIT |
| 配置路徑 | `~/.openclaw/openclaw.json` |
| 工作區 | `~/.openclaw/workspace/`（Skills、SOUL.md 等） |
| Skills 生態 | 1,700+ Skills on ClawHub |
| 模型支持 | Claude、GPT-5、Gemini、Llama 4、Mixtral、Grok、Opus 4.6 等 |
| 多 Agent | 支持按工作區隔離的專門化 Agent（coding、blog、research） |
| 記憶系統 | 持久化記憶，記住偏好、歷史對話、進行中的項目 |

---

## 關鍵洞察與未來展望

1. **從終端機到聊天室的范式轉移**：OpenClaw 的願景是將 AI 互動從冷冰冰的命令列搬到人們每天花最多時間的聊天室，大幅降低使用門檻
2. **本地優先 + 數據自主**：自託管架構讓用戶不依賴雲端服務，完全掌控自己的數據
3. **ACP 作為標準化協議的價值**：統一了不同 AI 編碼工具的通信方式，避免了每個工具各自的 PTY 解析
4. **Agent 不是對話夥伴，是數位員工**：與 ChatGPT/Claude 的關鍵區別 — OpenClaw 有系統級訪問權限，能執行真實操作（發訊息、管理檔案、運行程式碼、控制應用）
5. **未來思考**：當終端機不再是操作 AI 的主要入口時，軟體開發甚至工作的方式將會發生天翻地覆的改變
