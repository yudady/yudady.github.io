---
title: Hermes Agent 架构解析 — 一个 AIAgent 服務 20 個聊天平台
aliases: [Hermes Agent, Nous Hermes Agent]
tags:
  - ai-agent
  - status/active
  - type/doc
source:
  - "https://youtube.com/watch?v=m91Jv8Q_DjE"
  - "https://heymaibao.com/hermes-agent-architecture-20-platforms/"
  - "https://hermes-agent.nousresearch.com/docs/developer-guide/architecture"
author: 思思主播（影片）；Nous Research（專案）
created: 2026-05-28
updated: 2026-05-28
description: |
  Hermes Agent 是 Nous Research 的開源 AI Agent 框架，採用 platform-agnostic core 設計，用一個 AIAgent 類別統一服務 CLI、IDE、聊天平台等五個入口，內建 20 個聊天平台 adapter。
level: beginner
stars: 3
---

# Hermes Agent 架構解析 — 一個 AIAgent 服務 20 個聊天平台

> Nous Research 推出的 Hermes Agent 是一個反主流設計的 AI Agent 框架。不同於市面上「每個平台一個 agent」的專精化路線，它選擇把 CLI、IDE 整合、批次執行、API Server 以及 20 個聊天平台全部塞進同一個 `AIAgent` 類別。本文整理自思思主播的解析影片及其配套深度文章，梳理其架構賭注、收益與代價。

---

## 目錄

- [[#Platform-Agnostic Core：五個入口一個大腦]]
- [[#20 個聊天平台才是真正的殺手鐧]]
- [[#工具系統與 Profile 隔離]]
- [[#Cron 排程：Scheduled Task 作為第一公民]]
- [[#SOUL.md / MEMORY.md 命名約定]]
- [[#六條設計原則與隱藏張力]]
- [[#客觀評估：值得採用嗎]]

---

## Platform-Agnostic Core：五個入口一個大腦

### 核心概念

Hermes Agent 的設計原則叫做 **Platform-Agnostic Core（平台不可知核心）**。核心類別 `AIAgent`（定義在 `run_agent.py`）不關心請求來自哪個入口，所有平台差異在最外層的 entry point 就被消化掉。

### 五個入口

| 入口 | 檔案 | 說明 |
|------|------|------|
| CLI | `cli.py` | 互動式終端機（HermesCLI） |
| Gateway | `gateway/run.py` | 聊天平台事件處理（長駐程序） |
| ACP | `acp_adapter/` | IDE 整合（VS Code、Zed、JetBrains） |
| Batch Runner | `batch_runner.py` | 批次軌跡生成 |
| API Server | 內建於 Gateway | 對外 HTTP 介面 |

### 資料流架構

```
                    ┌──────────────────────────────┐
                    │       AIAgent (核心大腦)        │
                    │    run_agent.py               │
                    │    run_conversation()         │
                    └──────────┬───────────────────┘
                               │
          ┌────────────┬───────┼────────┬──────────────┐
          ▼            ▼       ▼        ▼              ▼
     ┌─────────┐ ┌────────┐ ┌────┐ ┌────────┐ ┌──────────┐
     │  CLI    │ │Gateway │ │ACP │ │ Batch  │ │API Server│
     │(終端機) │ │(聊天)  │ │(IDE)│ │Runner  │ │(HTTP)    │
     └─────────┘ └────────┘ └────┘ └────────┘ └──────────┘
```

三條主要資料流都收斂到同一個 `AIAgent.run_conversation()`：

- **CLI Session**：使用者輸入 → `HermesCLI.process_input()` → `AIAgent.run_conversation()` → 模型 API → 工具迴圈 → 顯示 + 寫入 SessionDB
- **Gateway Message**：平台事件 → adapter → MessageEvent → 授權 → 解析 session key → 用歷史建 AIAgent → 回應走 adapter
- **Cron Job**：scheduler tick → 載入到期 job → **全新 AIAgent（無歷史）** → 跑 → 送到目標平台

### 對比：專精化 vs 統一核心

| 維度 | 專精化路線（主流） | Hermes 統一核心 |
|------|-------------------|-----------------|
| 代表 | Claude Code (CLI)、Cursor (IDE) | Hermes Agent |
| 優點 | 各司其職、模組清晰 | 一套邏輯、跨平台一致 |
| 缺點 | 需要維護多套系統 | 核心膨脹、debug 較難 |
| 適合 | 單一場景深度使用 | 多場景統一管理 |

---

## 20 個聊天平台才是真正的殺手鐧

這是 Hermes Agent 最有感的差異化功能，尤其對需要管理中文社群的人來說。

### 平台覆蓋清單

**國際平台（10 個）**：
Telegram、Discord、Slack、WhatsApp、Signal、Matrix、Mattermost、Email、SMS、BlueBubbles

**中文圈平台（6 個）**：
釘釘（DingTalk）、飛書（Feishu）、企業微信（WeCom，含 wecom_callback）、微信（Weixin）、QQ Bot、元寶（Yuanbao）

**通用（4 個）**：
Webhook、API Server、Home Assistant、（wecom_callback 另列）

### Gateway 不只是「裝平台」

Gateway 本身是一個小型作業系統，需要處理：

```
Gateway 長駐程序
├── 統一 Session 路由
├── 使用者授權（allowlist + DM pairing）
├── 斜線指令派發
├── Hook 系統
├── Cron 排程 tick
└── 背景維護工作
```

### 最佳實踐

- ✅ 如果你需要覆蓋微信/企業微信/釘釘/飛書，Hermes 是少數開箱即用的開源選項
- ✅ 多數英文框架做到 Slack + Discord + Telegram 就停了，Hermes 在中文覆蓋深度上明顯領先
- ❌ 如果你只用單一平台（如純 Telegram），這個優勢對你沒太大意義

---

## 工具系統與 Profile 隔離

### 工具註冊機制

Hermes 採用 **自動發現** 模式：每個 `tools/*.py` 在 import 時呼叫 `registry.register()` 把自己掛上去，不需要中央 import 清單。

```
tools/registry.py (無相依，中央註冊表)
        ↑
tools/*.py (各自 register)
        ↑
model_tools.py
        ↑
run_agent.py / cli.py / batch_runner.py / environments/
```

### 工具規模

| 類別 | 數量 | 說明 |
|------|------|------|
| 工具總數 | 70+ | 官方文件數據 |
| Toolsets | ~28 | 工具分組 |
| 終端後端 | 7 種 | local、Docker、SSH、Daytona、Modal、Singularity、Vercel Sandbox |
| 瀏覽器後端 | 5 種 | 覆蓋主流瀏覽器自動化方案 |
| MCP | 動態載入 | Model Context Protocol 支援 |

### Profile 隔離

每個 profile（`hermes -p <name>`）有完全獨立的環境：

```
Profile A（個人助理）     Profile B（工作專案）     Profile C（寫程式）
┌────────────────┐      ┌────────────────┐      ┌────────────────┐
│ HERMES_HOME    │      │ HERMES_HOME    │      │ HERMES_HOME    │
│ config.yaml    │      │ config.yaml    │      │ config.yaml    │
│ memory/        │      │ memory/        │      │ memory/        │
│ sessions/      │      │ sessions/      │      │ sessions/      │
│ gateway PID    │      │ gateway PID    │      │ gateway PID    │
└────────────────┘      └────────────────┘      └────────────────┘
      ↑ 獨立記憶與設定，互不干擾 ↑
```

### 判斷決策樹

```
你需要 Profile 隔離嗎？
├── YES — 你同時管理多個專案/身份
│   ├── 每個身份需要不同的 system prompt → 用不同 profile + SOUL.md
│   ├── 每個身份需要不同的 LLM provider → 用不同 config.yaml
│   └── 每個身份需要獨立的對話歷史 → Profile 自動隔離 session DB
└── NO — 單一用途
    └── 直接用 default profile 即可
```

---

## Cron 排程：Scheduled Task 作為第一公民

### 設計選擇

Hermes 的排程任務不走延續對話路線，而是每次觸發時建立**全新、無歷史的 AIAgent**。

```
Cron Job 執行路徑：

scheduler tick
    │
    ▼
載入到期 job（jobs.json）
    │
    ▼
建立全新 AIAgent ← 無歷史記錄（fresh start）
    │
    ▼
注入掛載的 skills
    │
    ▼
執行任務 prompt
    │
    ▼
回應送到目標平台（origin / local / all）
```

### 設計意義

- ✅ 確保排程任務不被之前的閒聊「汙染」
- ✅ 可重現性高——每次執行從相同的乾淨狀態開始
- ✅ 排程任務是「第一公民 agent task」，不是附屬於 shell 的腳本工具
- ❌ 無法在排程任務中引用之前的對話上下文（需要通過 `context_from` 參數顯式注入）

---

## SOUL.md / MEMORY.md 命名約定

### System Prompt 組裝來源

`prompt_builder.py` 從六個來源組裝 system prompt：

```
1. SOUL.md          ← 個性/角色定義
2. MEMORY.md        ← 持久記憶
3. USER.md          ← 用戶偏好
4. Skills           ← 已載入的技能
5. Context Files    ← AGENTS.md / .cursorrules
6. Tool Guidance    ← 工具使用指引 + model-specific instructions
```

### 事實標準正在形成

`SOUL.md`、`AGENTS.md`、`MEMORY.md` 這些檔名與 Claude Code、OpenClaw 等生態高度一致，正在成為 AI agent 界的**不成文標準**：

| 檔名 | 用途 | 使用者 |
|------|------|--------|
| `SOUL.md` | 定義 agent 個性 | Hermes、Claude Code、OpenClaw |
| `MEMORY.md` | 持久記憶 | Hermes、Claude Code |
| `AGENTS.md` | 專案指令 | Hermes、Cursor、Windsurf |
| `.cursorrules` | IDE 專案設定 | Cursor、多數 AI IDE |

學一次，跨工具通用。

---

## 六條設計原則與隱藏張力

### 設計原則

1. **Prompt Stability** — 對話中不改 system prompt，避免破壞快取
2. **Observable Execution** — 每個工具呼叫對使用者可見
3. **Interruptible** — API 呼叫和工具執行可被中斷
4. **Platform-Agnostic Core** — 一個 AIAgent 服務所有入口
5. **Loose Coupling** — optional 子系統用 registry pattern，不硬相依
6. **Profile Isolation** — 每個 profile 完全獨立

### 核心張力

```
     乾淨核心承諾                膨脹的 Surface Area
  ┌─────────────────┐      ┌────────────────────────────┐
  │ 鬆耦合          │      │ 20 個 IM 平台              │
  │ 可中斷          │ ←→   │ 70+ 工具                   │
  │ Prompt 穩定     │  張力 │ 3 種 API mode              │
  │ 核心類別乾淨    │      │ 7 個終端後端                │
  └─────────────────┘      └────────────────────────────┘
```

Hermes 在賭「乾淨核心可以撐住膨脹的 surface area」。官方文件已把 `run_agent.py`、`cli.py`、`gateway/run.py` 標記為 **large file**——張力已經存在，但這是**有意的 trade-off**，不是技術債。

---

## 客觀評估：值得採用嗎？

### 收益

- ✅ 邏輯超級乾淨的核心（平台差異留在 entry point）
- ✅ 中文圈 IM 覆蓋最整齊的開源框架（微信/企業微信/釘釘/飛書/QQ/元寶）
- ✅ 70+ 工具 + 自動發現機制，擴展成本低
- ✅ Profile 隔離讓多身份管理變得清晰
- ✅ Cron 作為第一公民，排程任務乾淨可靠

### 代價

- ❌ All-in-one 讓核心檔案極其膨脹（large file 已出現）
- ❌ 自動發現機制讓 debug 追蹤困難
- ❌ 對新接手的開發者不夠友善
- ❌ 成熟度標示不明確（文件未標 alpha/beta/GA）

### 判斷決策樹

```
你應該採用 Hermes Agent 如果：
  ✅ 需要同時覆蓋多個中文 IM 平台（尤其企業微信/飛書/釘釘）
  ✅ 想用一套 agent 系統統一管理終端機 + IDE + 聊天平台
  ✅ 需要 Profile 隔離的多身份工作流
  ✅ 重視 Cron 排程的乾淨執行

你可能不需要 Hermes Agent 如果：
  ❌ 只用單一英文平台（Telegram/Discord），很多輕量方案更合適
  ❌ 是初學者，需要大量入門教學和社群支援
  ❌ 對核心檔案大小敏感，偏好模組化架構
```

---

## 參考資料

- [Architecture | Hermes Agent 官方文件](https://hermes-agent.nousresearch.com/docs/developer-guide/architecture)
- [Hermes Agent 怎麼用一個 AIAgent 服務 20 個聊天平台 — 思思主播](https://heymaibao.com/hermes-agent-architecture-20-platforms/)
- [Hermes Agent GitHub](https://github.com/nousresearch/hermes-agent)

## 相關筆記

- [[AI Agent 框架比較]]
- [[Platform-Agnostic 設計模式]]
