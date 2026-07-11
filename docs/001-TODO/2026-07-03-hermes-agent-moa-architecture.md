---
title: "超过Opus4.8上限！Hermes Agent 实装 MoA 架构"
source: https://www.youtube.com/watch?v=O7cR4QbrgtI
type: video-note
tags: [hermes-agent, MoA, LLM, multi-model, architecture]
created: 2026-07-03
---

# 超过Opus4.8上限！Hermes Agent 实装 MoA 架构

## 核心價值

> 下輪 AI 的能力躍遷可能不在參數裡，而在調度裡 [00:59](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=59)

MoA 成功用現成模型組裝「臨時交響樂團」，打破單模型效能上限，低成本高效率的通用方案 [00:51](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=51)。

## 一、MoA 核心概念與發展演進

**運作本質**：不在訓練更大模型或堆疊硬體，而是「架構與調度」 [00:46](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=46)。讓多個頂級大腦在獨立房間思考後，由一位聰明的主編將意見交織成完整答覆 [00:00](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=0)。

- Claude Opus 4.8（主編）+ GPT-5.5（顧問）的 MoA 組合，基準分數超越任何單一模型 [00:27](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=27)

**歷史與創新**：

- 2024 Together AI 首提 MoA，主打分層架構 [01:08](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=68)
- 2026 Hermes Agent 突破：做成 **Agent Loop 兼容的 First-class model** [01:40](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=100)，與工具調用、Context Memory 無縫銜接，從「跑分工具」走向「生產力工具」 [01:54](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=114)

## 二、MoA 機制深度拆解與運行代價

### 盲審工作流

**顧問模型（Reference Models）**：如 GPT-5.5、DeepSeek V4 Pro，彼此不知道對方存在，獨立分析 Prompt [02:37](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=157)

**聚合器模型（Aggregator / 主編）**：負責看完所有顧問意見並寫出最終回覆。**只有聚合器能調用工具與看到全局資訊** [02:49](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=169)

### 隱性代價

| 代價 | 說明 |
|---|---|
| Token 消耗暴增 | 聚合器輸入含所有顧問輸出，Token 約單模型的 3–4 倍 [03:55](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=235) |
| 延遲控制 | 顧問並發調用 → 總延遲 = 顧問最大延遲 + 聚合器延遲（非線性疊加）[04:05](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=245) |
| 金錢成本 | 預設用強大模型（如 Opus）作聚合器，API 費用不菲 [04:13](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=253) |

## 三、適用場景與核心局限性

### 黃金場景（前 10% 高難度任務）[04:41](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=281)

1. **綜合評審**：多模型同時 Review 方案/程式碼，避免手動複製貼上 [04:51](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=291)
2. **決策前最終把關**：數輪單模型溝通後，最後加入跨模型理智檢查 [05:08](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=308)
3. **高質量輸出生成**：投資人報告、可進入生產環境的樣本程式碼 [05:16](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=316)

### 不建議使用

- 日常閒聊、工具密集型任務或簡單查詢 [05:39](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=339)
- 顧問看到的是被過濾（玻璃化）的 Context，**沒有 Agent System Prompt 和工具調用歷史**，複雜項目背景下可能失真 [05:52](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=352)

## 四、MoA vs Fable 系統對比

影片將 MoA（V0）與 2026.6 发布的 Fable 系統（V2）橫向對比 [12:56](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=776)：

| 維度 | Hermes MoA [02:14](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=134) | Fable 系統 [12:56](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=776) |
|---|---|---|
| 調度核心 | 靜態 .yaml 配置 [14:18](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=858) | SFT + 進化算法 + RL 訓練的 Orchestrator [13:42](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=822) |
| 資訊隔離 | 顧問間隔離，聚合器看所有試錯 [03:12](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=192) | **Access List 隔離**（後續 Worker 只看前者的最終結果，避免思維污染）[13:14](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=794) |
| 聚合器機制 | 固定 [14:18](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=858) | **動態選擇**（程式用 Claude，數學用 GPT）[14:03](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=843) |
| 成本 | 較低，部署快 [14:32](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=872) | 延遲與訓練成本極高 [14:28](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=868) |

## 行動建議

1. **分工策略**：起步規劃階段用 Human-in-the-loop 手動同步跨模型思維最省錢精準 [06:22](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=382)；MoA 最適合扮演「**夜班值班員**」[16:03](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=963)，在非即時互動的最終綜合評審中自行整合產出
2. **架構進階**：追求極致性能可關注 Fable 體系的「Access List 隔離」與「動態 Orchestrator 訓練」開源復現（如 Qwen 30.6B 的開源嘗試），這是個人訓練專屬 AI 調度員的起點 [15:23](https://www.youtube.com/watch?v=O7cR4QbrgtI&t=923)
