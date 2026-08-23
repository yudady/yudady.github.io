---
tags:
  - AI/本地模型
  - AI/編碼Agent
  - 筆記
source: https://www.youtube.com/watch?v=nFiLFCrsg1w
created: 2025-07-02
---

# Ornith 1.0 本地端 AI 編碼模型實測筆記

## 模型家族概覽

Ornith 1.0 由舊金山 Deep Reinforce 推出，開源編碼模型家族：

| 尺寸 | 架構 | 權重開放 |
|------|------|---------|
| 9B | Dense | ✅ |
| 31B | Dense | ❌ |
| 35B | MoE | ✅ |
| 397B | MoE | ✅ |

**底座整合**：Gemma 4（強 Agent 任務執行）+ Qwen 3.5（本地編碼佼佼者）深度訓練。

**Benchmark 盲點**：SWE-bench 等基準側重「既有 Harness 中執行工具調用」與「未知代碼庫找 Bug 修復」，無法反映「從零構建（Build from scratch）」能力。

---

## 硬體實測（M4 Mac Mini 16GB）

- 系統保留 4GB，LM Studio 分配 ~12GB
- Ornith 9B GGUF 完全量化佔 6.2GB 權重
- 上下文視窗表現驚人：預設可塞入 **186K tokens**
- 開 Flash Attention + KV Cache Q8 量化 → 總記憶體 10.29GB，上下文拉到 **260K tokens**
- 推理速度：~16-18 tok/s（滿載壓力下）

> **對比**：上下文能力遠超 Gemma 4 12B。

---

## 實戰編碼對比（9B vs 35B）

**測試任務**：Pi Agent 驅動，單一 HTML 檔從零構建塔防遊戲。

### Ornith 9B — 不推薦複雜建構

- Thinking tokens 很長，文字邏輯看似聰明，但產出 800-900 行代碼**完全無法運行**
- 常見問題：死循環、函數宣告/調用不一致、空函數、工具調用格式出錯導致 Harness 無法解析
- 容易陷入 Debug 死循環，反而浪費開發者時間

### Ornith 35B MoE — 降維打擊

- Mac Studio 遠端調用，推理速度 **~100 tok/s**（比 9B 快 5 倍+）
- 幾乎 **one-shot** 生成完全可運行的塔防遊戲
- 能將 9B 的爛代碼經 4-5 輪迭代搶救成勉強可玩的狀態

---

## 核心結論

> **長上下文 ≠ 高推理精度**

9B 能在 16GB 設備吃下近 20 萬 tokens，但參數量限制了邏輯縫合的精準度。

### 部署建議

| 場景 | 推薦 |
|------|------|
| 從零建構中大型應用 | ❌ 9B 不行 |
| 簡單 Code Review、找 Bug、小模組 | ✅ 9B 勉強可用（需縮減 scope） |
| 生產力級 Agent 編碼 | ✅ 35B+（Ornith 35B / Qwen 3.6 35B） |
