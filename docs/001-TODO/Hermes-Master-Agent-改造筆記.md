---
title: "把 Hermes 爆改成超強主 Agent：無縫調度 SubAgent、Claude、Gemini 與 Codex"
source: https://www.youtube.com/watch?v=QAhJRYua62k
date: 2026-06-28
tags: [hermes, multi-agent, architecture, CLI, automation]
---

# 把 Hermes 爆改成超強主 Agent

> 核心目標：將 Hermes 改造為單一入口的主 Agent（Master Agent），直接調度 Sub-Agent、Claude、Gemini 與 Codex。

## 一、前言與核心痛點

深度使用 Hermes Agent 時遇到的操作與協同痛點：
- 頻繁切換窗口
- 模型間各自獨立
- 高強度使用第三方 API 成本過高

## 二、TUI（終端用戶介面）與工作流優化

### 單一入口與標識 [00:19](https://www.youtube.com/watch?v=QAhJRYua62k&t=19)
- 修改內置 TUI，用戶透過 `@` 符號直接切換或指定不同的 Agent 與大模型
- 回覆中加入名稱標識

### 看板監控 [01:09](https://www.youtube.com/watch?v=QAhJRYua62k&t=69)
- 狀態欄加入任務計數器統計所有看板任務
- 計數器在任務「卡住」時無法呈現異常 → 額外開發 **事件面板（Events Panel）**
- 事件面板透過週期性讀取本地數據庫，監控 Cron Job 與看板任務的最終狀態（特別是失敗狀態）

### 多窗格協同 [01:47](https://www.youtube.com/watch?v=QAhJRYua62k&t=107)
- 推薦基於 Tmux 的工具 `herholder`，支援滑鼠切換面板與直觀顯示 Agent 狀態
- 大型開發場景建議使用 IDE（如 VS Code）接入 Hermes 的插件

## 三、多模型與 Sub-Agent 的後端通信實現

### 消除冷啟動 [02:20](https://www.youtube.com/watch?v=QAhJRYua62k&t=140)
- 大模型套餐沒有 API Key 只能走 CLI
- 避免每次對話重新啟動 CLI 進程 → 參考 IDE 插件的 ACP 模式
- Hermes 運行期間僅啟動一個實例，透過 **TCP 協議 + JSON RPC** 進行持久通信
- Claude 使用 Stam Jon，Codex 複用官方 Server

### 降級防錯機制 [03:40](https://www.youtube.com/watch?v=QAhJRYua62k&t=220)
- 通信封裝成 Python 腳本，Skill 內部集成 **Fallback 機制**
- 若 TCP 通信未啟動，自動降級走傳統命令列冷啟動

### Agent 間的 HTTP 橋接 [04:17](https://www.youtube.com/watch?v=QAhJRYua62k&t=257)
- Hermes Agent 默認隔離，但各 Agent 都有自己的 Gateway API
- 主 Agent 透過發送 HTTP 請求與子 Agent 通信

### 自主路由學習 [04:42](https://www.youtube.com/watch?v=QAhJRYua62k&t=282)
- 撰寫插件在初始化時將所有 Sub-Agent 與 CLI 的連接方式與擅長領域寫入配置
- 經過一段時間對話後，主 Agent 會記住各自優勢
- 面對複合任務時自動將任務精準分派給合適的 Sub-Agent

## 四、Cron Job 執行結果的 Web 端渲染方案

[06:37](https://www.youtube.com/watch?v=QAhJRYua62k&t=397)

- 終端 TUI 沒有後端服務器，Cron Job 結果丟到手機聊天軟體常面臨排版崩壞
- 搭建**輕量級 Web 站點**，自動掃描 Job 的持久化輸出並序列化
- 前端根據數據類型選擇 Markdown 或 Chart 圖表組件進行渲染
- 配合內網穿透或雲端部署，跨終端隨時查看

## 五、聯網搜索與多層級結構化數據抓取策略

為了解決官方推薦的 Firecow 等第三方抓取 API 在高頻使用下成本過高的痛點，設計了**搜索抓取專用 Agent**，固定為三檔優先級的自動兜底策略：

### 第一檔（高效低成本）
優先走雲端 API 或大模型（如 Gemini/Codex）自帶的 Web Search。

### 第二檔（輕量自動化）
使用輕量、內置反爬蟲與搜索組件的 Camfox 進行無頭（Headless）瀏覽器數據抓取。

### 第三檔（強大反爬防禦）
遇到強反爬或需登入的網站時，使用 Safe Web Access 等開源工具去**託管日常使用的瀏覽器（CDP 模式）**。
- 直接複用現有的 Session Cookie 和瀏覽器指紋特徵
- 免去維護登入狀態的麻煩
- 利用後台 Tab 抓取，不影響日常網頁瀏覽

---

## 核心價值

將單一 AI 工具透過**架構層面的爆改**，升級為企業級/高生產力的**多智能體中控台（Master Agent）**：

1. **體驗一體化**：透過 TUI 優化與 JSON RPC/HTTP 通信，打破模型與 Agent 之間的壁籬
2. **成本與效率平衡**：融合大模型內置聯網、無頭瀏覽器與 CDP 託管，規避高昂第三方 API 費用

## 行動建議

1. 建立自己的主 Agent 意識，利用 API/HTTP Gateway 將多模型串聯至同一工作流
2. 頻繁調用 CLI 工具改採 TCP/JSON RPC 常駐實例（Daemon）模式
3. 數據抓取採分級制：API 優先 → 無頭瀏覽器 → CDP 託管真實瀏覽器兜底
