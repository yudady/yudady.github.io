---
title: Pi Agent Harness - 多層次 AI Agent 工具套件
aliases: [Pi, pi-agent, earendil-works/pi, Pi Coding Agent]
tags:
  - ai-agent
  - coding-agent
  - llm-toolkit
  - typescript
  - multi-provider
  - status/active
  - type/learning-note
source:
  - "https://youtu.be/adG1gv_6_5A"
  - "https://github.com/earendil-works/pi"
author: Mario Zechner (badlogic)
created: 2026-06-07
updated: 2026-06-07
description: Pi 是一個 TypeScript AI Agent 工具套件，四層架構從統一 LLM API 到完整 Coding Agent，支援 2000+ 模型和所有主流 Provider，60k+ Stars，被 OpenClaw 等專案採用。
level: intermediate
stars: 5
note: 無字幕，基於 GitHub README + dabit3 Gist 教學 + 社群文章綜合整理
---

# Pi Agent Harness - 多層次 AI Agent 工具套件

> Pi 是由 Mario Zechner（@badlogic）打造的 TypeScript AI Agent 工具套件，GitHub 60k+ Stars，MIT 授權。核心設計是**四層堆疊架構**：`pi-ai`（統一 LLM 通訊）→ `pi-agent-core`（Agent 迴圈 + Tool Calling）→ `pi-coding-agent`（互動式 Coding Agent CLI）+ `pi-tui`（終端 UI）。支援 2000+ 模型和所有主流 Provider，被 OpenClaw 等多頻道 AI 助手採用。

## 目錄

- [四層堆疊架構](#四層堆疊架構)
- [Layer 1：pi-ai — 統一 LLM API](#layer-1pi-ai--統一-llm-api)
- [Layer 2：pi-agent-core — Agent 迴圈](#layer-2pi-agent-core--agent-迴圈)
- [Layer 3：pi-coding-agent — 編碼 Agent](#layer-3pi-coding-agent--編碼-agent)
- [Layer 4：pi-tui — 終端 UI](#layer-4pi-tui--終端-ui)
- [多 Provider 切換](#多-provider-切換)
- [安全與容器化](#安全與容器化)
- [供應鏈安全強化](#供應鏈安全強化)
- [與其他 Coding Agent 對比](#與其他-coding-agent-對比)
- [技術規格](#技術規格)
- [優缺點評估](#優缺點評估)

---

## 四層堆疊架構

```
┌─────────────────────────────────────────┐
│        你的應用程式                        │
│  （OpenClaw、CLI 工具、Slack Bot）        │
├──────────────────┬──────────────────────┤
│ pi-coding-agent  │    pi-tui            │
│ Session、工具、   │    終端 UI、          │
│ 擴展系統          │    Markdown、編輯器   │
├──────────────────┴──────────────────────┤
│            pi-agent-core                  │
│    Agent 迴圈、工具執行、事件系統          │
├─────────────────────────────────────────┤
│              pi-ai                        │
│    串流、模型、多 Provider LLM 統一接口     │
└─────────────────────────────────────────┘
```

**核心設計原則**：按需使用，每層可獨立使用。

| 使用場景 | 需要用到的層 |
|----------|-------------|
| 只需要呼叫 LLM | `pi-ai` |
| 需要帶工具的 Agent 迴圈 | `pi-ai` + `pi-agent-core` |
| 需要完整 Coding Agent | `pi-ai` + `pi-agent-core` + `pi-coding-agent` |
| 需要自訂 UI | 四層全部 |
| 只需要 TUI 元件 | `pi-tui`（獨立使用） |

---

## Layer 1：pi-ai — 統一 LLM API

### 核心價值

用**同一套介面**呼叫 2000+ 模型，切換 Provider 只需改一行程式碼。

### 支援的 Provider

```
Anthropic (Claude)        OpenAI (GPT, o-series)
Google (Gemini)           AWS Bedrock
Mistral                   Groq
xAI (Grok)                OpenRouter
Ollama (本地)             vLLM
自訂 OpenAI 相容端點       ...
```

### 基本用法

```typescript
import { getModel, completeSimple } from "@mariozechner/pi-ai";

const model = getModel("anthropic", "claude-opus-4-5");

const response = await completeSimple(model, {
  systemPrompt: "You are a helpful assistant.",
  messages: [
    { role: "user", content: "What is the capital of France?", timestamp: Date.now() }
  ],
});

// response.content → typed blocks: text | thinking | toolCall
// response.usage.totalTokens
// response.stopReason → "stop" | "toolUse" | "length" | "error" | "aborted"
```

### 串流支援

```typescript
import { getModel, streamSimple } from "@mariozechner/pi-ai";

const stream = streamSimple(model, context);

for await (const event of stream) {
  switch (event.type) {
    case "text_delta": process.stdout.write(event.delta); break;
    case "thinking_delta": /* 擴展思維 */ break;
    case "toolcall_delta": /* 工具呼叫 */ break;
    case "done": /* 完成 */ break;
    case "error": /* 錯誤 */ break;
  }
}

// 或直接等待最終結果
const finalMessage = await stream.result();
```

### 標準化串流事件

```
start → text_start → text_delta(×N) → text_end → done
                     → thinking_start → thinking_delta(×N) → thinking_end
                     → toolcall_start → toolcall_delta(×N) → toolcall_end
```

### 自訂模型（自託管端點）

```typescript
const localModel: Model = {
  id: "llama-3.1-8b",
  api: "openai-completions",  // 使用 OpenAI SDK 路由
  provider: "ollama",
  baseUrl: "http://localhost:11434/v1",
  contextWindow: 128000,
  maxTokens: 8192,
};
```

### Thinking 等級控制

```typescript
const stream = streamSimple(model, context, {
  reasoning: "high",  // minimal | low | medium | high | xhigh
});
```

適用於支援擴展思維的模型（Claude、o3、Gemini 2.5）。

---

## Layer 2：pi-agent-core — Agent 迴圈

### 核心功能

將 `pi-ai` 封裝成完整的 Agent 迴圈：發送訊息 → 執行工具呼叫 → 將結果反饋給 LLM → 迴圈直到完成。

### 關鍵擴展事件

| 事件 | 用途 |
|------|------|
| `context` | 在 LLM 看到訊息前重寫內容 |
| `session_before_compact` | 自訂摘要壓縮邏輯 |
| `tool_call` | 攔截或閘控工具調用 |
| `before_agent_start` | 注入上下文或修改 prompt |
| `session_start` / `session_switch` | 響應 session 切換 |

### 決策樹：何時用 pi-agent-core

```
需要工具呼叫嗎？
  ├── 是 → pi-agent-core
  └── 否 → 只用 pi-ai 就夠了
```

---

## Layer 3：pi-coding-agent — 編碼 Agent

完整互動式 Coding Agent CLI，帶有：

- **內建工具集**：檔案讀寫、shell 執行、搜尋等
- **Session 持久化**：跨 session 保持對話上下文
- **擴展系統**：自訂工具和事件處理器
- **摘要壓縮**：長對話自動摘要節省 token

```typescript
import { PiCodingAgent } from "@mariozechner/pi-coding-agent";

// 使用擴展事件自訂行為
agent.on("tool_call", (call) => {
  // 攔截或修改工具調用
});
agent.on("context", (messages) => {
  // 在 LLM 看到前修改訊息
});
```

---

## Layer 4：pi-tui — 終端 UI

獨立的終端 UI 函式庫，支援：

- **差異化渲染**（differential rendering）— 只更新變化的部分
- **Markdown 渲染**
- **內建編輯器**
- **可組合元件**

可獨立於其他層使用，適合任何需要 CLI UI 的 TypeScript 專案。

---

## 多 Provider 切換

```typescript
// 切換 Provider 只需改這一行
const model = getModel("anthropic", "claude-opus-4-5");
// const model = getModel("openai", "gpt-4o");
// const model = getModel("google", "gemini-2.5-pro");
// const model = getModel("groq", "llama-3.3-70b-versatile");
// const model = getModel("ollama", "llama-3.1-8b");  // 本地
```

### 選擇決策樹

```
你的需求是什麼？
  ├── 低成本本地推理 → Ollama / vLLM + pi-ai
  ├── 最高品質推理 → Claude Opus / GPT-4o / Gemini 2.5 Pro
  ├── 需要擴展思維 → Claude (reasoning: "high") / o3
  ├── 成本與品質平衡 → Claude Sonnet / GPT-4o-mini
  └── 完全自訂端點 → 自訂 Model 物件 + OpenAI 相容 API
```

---

## 安全與容器化

> **「Pi 不包含內建的權限系統來限制檔案系統、進程、網路或憑證存取。預設情況下，它以啟動它的用戶和進程權限運行。」**

三種容器化方案：

| 方案 | 隔離程度 | 複雜度 |
|------|----------|--------|
| **Plain Docker** | 整個 `pi` 進程在容器中 | 低 |
| **OpenShell** | 在策略控制沙盒中運行 | 中 |
| **Gondolin 擴展** | 主機保留 pi + 認證，工具命令路由到 micro-VM | 高 |

### 安全最佳實踐

```bash
# ✅ 在受控環境中運行
docker run -v $(pwd):/workspace pi-coding-agent

# ❌ 不要在生產伺服器上直接運行
# ❌ 不要將 API Key 硬編碼
```

---

## 供應鏈安全強化

Pi 在依賴安全方面做了大量工作：

| 措施 | 說明 |
|------|------|
| **精確版本鎖定** | 外部依賴鎖定到精確版本 |
| **`.npmrc` 配置** | `save-exact=true` + `min-release-age=2`（避免同一天發布的依賴） |
| **Lockfile 保護** | `package-lock.json` 是唯一真實來源，pre-commit 阻止意外提交 |
| **Shrinkwrap** | 發布的 CLI 包含 `npm-shrinkwrap.json` |
| **CI 審計** | 定期 `npm audit` + 簽名驗證 |
| **腳本限制** | `--ignore-scripts` 用於本地發布和安裝 |

---

## 與其他 Coding Agent 對比

| 特性 | Pi | Claude Code | Cursor | Aider |
|------|-----|-------------|--------|-------|
| 語言 | TypeScript | Python | TypeScript | Python |
| 多 Provider | ✅ 2000+ 模型 | ❌ 僅 Anthropic | ❌ 僅自家 | ✅ 多家 |
| 統一 LLM API | ✅ pi-ai | ❌ | ❌ | ✅ |
| 自訂 Agent 框架 | ✅ 四層堆疊 | ❌ 封閉 | ❌ 封閉 | ⚠️ 有限 |
| TUI 函式庫 | ✅ pi-tui | ✅ Ink | ❌ | ✅ |
| 終端 UI | ✅ | ✅ | ❌ | ✅ |
| Session 持久化 | ✅ | ✅ | ✅ | ✅ |
| 容器化 | ✅ 三種方案 | ❌ | ❌ | ❌ |
| 擴展事件 | ✅ 豐富 | ❌ | ❌ | ⚠️ |
| 授權 | MIT | 專有 | 專有 | Apache 2.0 |
| 可嵌入 | ✅ | ❌ | ❌ | ⚠️ |
| 社群 | 211 貢獻者 | Anthropic | 公司 | 社群 |

### Pi 的獨特優勢

```
與 Claude Code / Cursor 的根本差異：
  它們是「封閉的 Coding Agent 產品」
  Pi 是「用來建構 Coding Agent 的工具套件」
  └── 你可以用 Pi 打造自己的 Claude Code
```

---

## 技術規格

| 項目 | 規格 |
|------|------|
| 倉庫 | `earendil-works/pi` |
| Stars | 60k+ |
| Forks | 7.2k |
| 貢獻者 | 211 |
| Commits | 4,429+ |
| Releases | 226 |
| 語言 | TypeScript (93.6%) |
| 授權 | MIT |
| 最新版本 | v0.78.1 (2026.06.04) |
| Node.js | 20+ |
| 網站 | [pi.dev](https://pi.dev/) |
| 文檔 | [pi.dev/docs/latest](https://pi.dev/docs/latest/) |
| Discord | [discord.com/invite/3cU7Bz4UPx](https://discord.com/invite/3cU7Bz4UPx) |

### 著名貢獻者

- **@badlogic**（Mario Zechner）— 主要維護者，libGDX 創始人
- **@mitsuhiko**（Armin Ronacher）— Flask、Rye、Sentry SDK 作者

---

## 優缺點評估

### ✅ 優點

- **四層堆疊設計優雅**：按需使用，從簡單 LLM 呼叫到完整 Agent 都有對應層
- **真正的 Provider 無關**：2000+ 模型，切換只需一行程式碼
- **MIT 開源**：完全自由，可嵌入任何產品
- **60k+ Stars、211 貢獻者**：社群活躍，可持續發展
- **供應鏈安全強化**：精確版本鎖定、lockfile 保護、CI 審計
- **OpenClaw 背書**：作為多頻道 AI 助手的底層，已經過生產驗證
- **容器化方案**：三種隔離方案，安全考量到位

### ❌ 缺點與風險

- **無內建權限系統**：預設以使用者權限運行，安全性依賴外部容器化
- **TypeScript 生態**：對 Python/Rust 社群吸引力較低
- **文件可能滯後**：快速迭代（226 releases）意味著文件可能跟不上
- **PR 自動關閉**：新貢獻者的 Issue 和 PR 默認自動關閉，社群門檻較高
- **非獨立產品**：更像是「建構 Agent 的工具套件」而非「直接使用的 Coding Agent」
- **學習曲線**：四層架構的理解和組合使用需要投入學習時間

---

## 参考资料

- [earendil-works/pi GitHub](https://github.com/earendil-works/pi)
- [Pi 官網](https://pi.dev/)
- [How to Build a Custom Agent Framework with Pi (Gist by dabit3)](https://gist.github.com/dabit3/e97dbfe71298b1df4d36542aceb5f158)
- [Pi Coding Agent - Grokipedia](https://grokipedia.com/page/Pi_Coding_Agent)
- [OpenClaw (Pi 的生產用例)](https://github.com/openclaw/openclaw)

## 相关笔记

- [[Scrapling - AI 自適應網頁爬蟲框架]]
- [[PaddleOCR - 百度開源 OCR 工具包與文件 AI 引擎]]
- [[Tolaria - 基於 Git 的 Markdown 知識庫桌面應用]]
- [[Understand-Anything - 程式碼知識圖譜互動探索工具]]
