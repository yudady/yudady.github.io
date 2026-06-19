# Claude Code Dynamic Workflows — 裁掉 AI 主管的多代理新範式

> 📺 [AI 幫手](https://youtu.be/ICdBtl8_3hQ) · 2026-06-07 · Video ID: `ICdBtl8_3hQ`

## TL;DR

Anthropic 於 2026-05-28 發布 **Claude Code Dynamic Workflows**，核心思路：用 JavaScript 編排腳本取代傳統的「AI 主管」模式，中間結果留在腳本變數而非塞入 Claude 上下文窗口，大幅降低 token 消耗與幻覺風險。

---

## 背景：傳統多代理的痛點

傳統 Multi-Agent 架構（如 CrewAI、AutoGen）：
- 每個子 Agent 完成任務後，把大量文本交給**主管 Agent** 閱讀
- 主管 Agent 上下文被塞爆 → 記憶超載 → 幻覺
- 幾萬字的「閱讀費」層層疊加，成本暴增
- 結果品質依賴主管 Agent 的判斷力，但主管本身也會犯錯

---

## Dynamic Workflows 是什麼

**一個 Claude 自動生成的 JavaScript 編排腳本**，在 runtime 拆解任務、分派子 Agent、合併結果。

### 核心差異

| 維度 | 傳統主管模式 | Dynamic Workflows |
|------|------------|-------------------|
| 編排者 | AI Agent（主管） | JavaScript 腳本 |
| 中間結果存放 | 主管的上下文窗口 | 腳本變數（不佔 Claude 上下文） |
| 每輪 token 消耗 | 全量中間文本 × 輪次 | 僅最終驗證結果進入上下文 |
| 可重複性 | 低（依賴即時推理） | 高（腳本可存檔重跑） |
| 中斷恢復 | 重頭來過 | 快取已完成 Agent，斷點續跑 |

### 三層協作原語

| 層級 | 用途 | 編排邏輯在哪 |
|------|------|------------|
| **Subagents** | 少量（幾個）委派任務 | Claude 逐輪決定 |
| **Dynamic Workflows** | 數十到數百個 Agent 協作 | JS 編排腳本 |
| **Agent Teams** | 多 Claude Code 實例獨立 session | 團隊 lead 協調 |

---

## 關鍵機制

### 1. 對抗性驗證（Adversarial Verification）
- 獨立 Agent 嘗試**推翻**其他 Agent 的結論
- 只有經過反駁考驗的 claim 才會浮現
- 類似同行評審機制

### 2. 收斂驅動
- 迭代直到結果穩定，不預設輪次
- 通過的標準是工作品質，不是固定圈數

### 3. 限制
- 最多 **16 個並行 Agent**
- 每次 run 最多 **1,000 個 Agent**
- 腳本本身不能直接操作檔案系統/shell（由 Agent 執行 I/O）

---

## 如何觸發

### 方法一：關鍵字觸發
Prompt 中包含 `workflow` 一詞即可：
```
Run a workflow to audit every API endpoint under src/routes/ for missing auth checks
```

### 方法二：Ultracode 模式
```
/effort ultracode
```
Claude 自動判斷是否需要 workflow，啟動 understand → change → verify 三階段。

### 內建指令
- `/deep-research` — 多角度搜索、交叉驗證、投票、生成引用報告
- `/workflows` — 監控、暫停、恢復、儲存 run

---

## 成本與注意事項

- Anthropic 明確警告：Dynamic Workflows **token 消耗可能顯著高於**普通 session
- 建議先在小範圍任務上測試成本
- 子 Agent 始終以 `acceptEdits` 模式運行
- 結果恢復僅限同一 session（退出 Claude Code 即失效）

---

## 適用場景

- 大規模程式碼遷移（500+ 檔案）
- 程式碼庫安全審計
- 未知 codebase 的探索性 bug 狩獵
- 需要多角度壓力測試的計劃
- 需要獨立對抗審視的任務

## 不適用場景

- 可預測、定義明確的重複任務 → 用 Subagent
- 小範圍單次通過即可的任務 → 直接用 Claude

---

## 與 Hermes 的關聯

Hermes 的 `delegate_task` 機制概念上接近 Claude Subagents（少量委派），但 Claude Dynamic Workflows 的 scale（上百 Agent + JS 編排 + 對抗驗證）是另一個量級。值得關注是否能將 workflow 腳本模式引入 Hermes。

---

## Tags

#ai #claude-code #multi-agent #orchestration #dynamic-workflows #anthropic #devops
