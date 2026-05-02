---
in: null
title: AntiGravity just became UNSTOPPABLE (GravityClaw)
aliases: [AntiGravity just became UNSTOPPABLE (GravityClaw)]
tags: ["语音交互", "AI助手", AI, AntiGravity, ElevenLabs, GravityClaw, MCP, PersonalAssistant, Pinecone, Telegram, Tutorial, VoiceAI]
source: null
author: null
created: 2026-02-21 10:43
updated: 2026-03-07 21:50
description: null
level: null
stars: null
category: "技术"
summary: "介绍基于AntiGravity构建的GravityClaw，一个本地优先的AI私人助手。"

---

# AntiGravity just became UNSTOPPABLE (GravityClaw)

> 📺 **影片來源**: [YouTube](https://www.youtube.com/watch?v=-hYE5U6FGk8)
> 📅 **觀看日期**: 2026-02-21
> ⏱️ **影片長度**: 34分45秒

---

## 📹 影片嵌入

<iframe width="560" height="315" src="https://www.youtube.com/embed/-hYE5U6FGk8" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>

---

## 📝 筆記摘要

### 核心概念

GravityClaw 是一個基於 AntiGravity 構建的本地優先 AI 私人助手，使用 CLAWS 五步框架：

| 步驟 | 名稱 | 功能 |
|------|------|------|
| **C** | Connect | 連接 Telegram Bot |
| **L** | Listen | 語音交互能力 |
| **A** | Archive | 三層記憶系統 |
| **W** | Wire | MCP 工具連接 |
| **S** | Sense | 定時主動提醒 |

### 重點內容

#### 1. Connect - 連接 Telegram Bot

- 透過 BotFather 創建新的 Telegram Bot
- 獲取 Access Token
- 設定白名單（只允許特定 Telegram ID 訪問）
- 使用 OpenRouter API（支援 300+ 模型，包含免費模型）

#### 2. Listen - 語音交互能力

**語音輸入（Speech-to-Text）**
- 使用 Groq API（免費）或 OpenAI Whisper
- 自動將語音訊息轉錄為文字

**語音輸出（Text-to-Speech）**
- 使用 ElevenLabs API
- 可自定義聲音（男聲/女聲、口音）
- 新版本具有更低延遲和更自然的情感表達

#### 3. Archive - 三層記憶系統

```
┌─────────────────────────────────────┐
│  Core Memory (Always On)            │  ← 核心資訊，每個 prompt 都帶
├─────────────────────────────────────┤
│  Conversation Buffer (SQLite)      │  ← 完整對話記錄
├─────────────────────────────────────┤
│  Semantic Memory (Pinecone)        │  ← 向量化記憶，語意檢索
└─────────────────────────────────────┘
```

**特點**：
- 每條訊息自動保存到 Conversation Buffer 和 Pinecone
- LLM 自動掃描對話提取重要事實（姓名、偏好、截止日期）
- 提供 `remember_facts` 工具讓 Agent 主動記憶

**soul.md** - 定義 Agent 個性：
- 友善但具挑戰性
- 不迎合（non-sycophantic）
- 鏡像用戶語言風格
- 思考問題背後的問題

#### 4. Wire - MCP 工具連接

**核心概念**：
- MCP（Model Context Protocol）是 AntiGravity 與外部工具溝通的通用協議
- GravityClaw 可直接使用已在 AntiGravity 中配置的 MCP 連接

**實測範例**：
- Zappier MCP → 查詢 Gmail 最後一封信的主旨
- Context7 MCP → 獲取最新文檔

**對比 Pinecone vs Supabase**：
| 項目 | Pinecone + SQLite | Supabase + pgvector |
|------|-------------------|---------------------|
| 讀取速度 | 即時（本地） | 網絡跳躍 |
| 成本 | 免費 | 需付費 |
| 數據位置 | 本地 | 雲端 |
| 設置難度 | 已內建 | 需額外設置 |

#### 5. Sense - 心跳系統

**功能**：
- 使用 node-cron 設置定時任務
- 每天 8:00 AM 主動發送問候與問責訊息
- 載入記憶上下文、詢問目標進度
- 輕量級 LLM 調用（500 tokens max）

**範例訊息**：
> "Good morning, Jack. Hope you're crushing Tuesday already. Quick time to check in. Have you stepped in Skull today yet? What's one thing you want to demolish today?"

### 部署到 Railway

**為什麼要部署**：
- 筆電關機時仍可使用
- Railway 無開放端口，比 VPS 更安全
- 加上白名單機制，雙重保護

**部署步驟**：
1. 安裝 Railway CLI
2. 執行 `railway login`（瀏覽器配對）
3. 執行 `railway up` 部署
4. 設置環境變數

**注意事項**：
- SQLite 在重新部署時會重置
- 重要記憶存在 Pinecone 不會丟失
- 不要同時在本地和 Railway 運行（會發送重複訊息）

**推薦工作流**：
1. 凍結 Railway (`railway pause`)
2. 本地開發測試
3. 完成後部署到 Railway
4. 停止本地運行

---

## 🔧 技術棧

| 類別 | 工具 |
|------|------|
| **AI 編排** | AntiGravity |
| **平台** | Telegram Bot API |
| **LLM** | OpenRouter (Claude, GPT, 免費模型) |
| **語音辨識** | Groq Whisper / OpenAI Whisper |
| **語音合成** | ElevenLabs |
| **向量資料庫** | Pinecone |
| **本地資料庫** | SQLite |
| **定時任務** | node-cron |
| **雲端部署** | Railway |

---

## 💡 實用技巧

### 功能配置 Dashboard

作者提供了一個功能選擇 Dashboard，可以像樂高積木一樣選擇想要的功能：

- WhatsApp / Telegram 整合
- 語音轉錄 / 語音回覆
- Knowledge Graph
- Context Pruning
- Markdown Memory
- Self Memory
- 各種工具整合

選擇後會生成 prompt，貼到 AntiGravity 即可整合。

### 安全性考量

1. **本地優先**：所有數據存在自己的筆電
2. **白名單**：只允許特定 Telegram ID 訪問
3. **無開放端口**：Railway 部署不暴露端口
4. **API Key 命名**：建議用用途命名，方便管理

---

## 📚 資源連結

- **AntiGravity**: https://antigravity.dev
- **OpenRouter**: https://openrouter.ai
- **Pinecone**: https://pinecone.io
- **ElevenLabs**: https://elevenlabs.io
- **Groq**: https://console.groq.com
- **Railway**: https://railway.app
- **MCP 列表**: https://github.com/modelcontextprotocol/servers

---

## 🏷️ 標籤

#AI #AntiGravity #Telegram #PersonalAssistant #MCP #Pinecone #VoiceAI #ElevenLabs #Tutorial

## 相关笔记
- [[03-knowledge/技术/人工智能/PicoClaw-轻量级AI助手完整指南|PicoClaw]] - 另一个轻量 AI 助手
- [[03-knowledge/技术/人工智能/小龙虾-OpenClaw版本选择指南|OpenClaw/小龙虾]]
- [[03-knowledge/技术/人工智能/Top20AIAgentProjects-本周精选|Top 20 AI Agent 项目]]
- [[004-ai-tools/claude/Mac M3 安装 Antigravity Agent "已损坏" 问题解决方案|Antigravity 安装问题]]
- [[004-ai-tools/nanobot/NANOBOT_SOURCE_ANALYSIS|Nanobot 源码分析]]
- [[MOC-AI编程工具|AI 编程工具 MOC]]
- [[MCP servers|MCP Servers]]