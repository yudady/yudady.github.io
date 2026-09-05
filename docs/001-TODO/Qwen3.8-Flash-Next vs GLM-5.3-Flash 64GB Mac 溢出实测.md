---
title: Qwen3.8-Flash-Next vs GLM-5.3-Flash 64GB Mac 溢出实测
aliases: [溢出推理实测, N-gram 查表 SSD 流式, MoE 溢出 thrashing, 64GB 跑百G模型]
tags:
  - local-llm
  - mac
  - mlx
  - llama-cpp
  - moe
  - qwen
  - glm
  - benchmark
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=7E5DYu24bxw"
  - "https://recipes.vllm.ai/Qwen/Qwen3.8-Flash-Next"
  - "https://z.ai/blog/glm-5.3-flash"
  - "https://unsloth.ai/docs/models/qwen3.8-next"
author: HoaLab（頻道）
created: 2026-09-05
updated: 2026-09-05
description: 同樣「模型大於 RAM」，GLM-5.3-Flash 只跑 1.9 tok/s 而 Qwen3.8-Flash-Next 達 16.3 tok/s——拆解 N-gram 查表 vs MoE 專家層兩種溢出機制的根本差異與 64GB Mac 調優實錄
level: intermediate
stars: 5
note: Tier 0 英文字幕直出。核心架構聲稱（125B+51B n-gram+4B MTP / 320B-A18B）經 vLLM 官方 recipes、z.ai 官方博客、unsloth、NVIDIA 論壇多源交叉驗證。字幕為 YouTube 自動生成（Quen→Qwen、engram→N-gram、MAPAP→mmap 轉寫噪音已修正）。
---

# Qwen3.8-Flash-Next vs GLM-5.3-Flash 64GB Mac 溢出实测

> 同一台 64GB Mac Studio，同樣「模型檔案大於記憶體」的問題：GLM-5.3-Flash 爬行在 1.9 tok/s（只能跑隔夜批處理），Qwen3.8-Flash-Next 卻能到 16.3 tok/s（可互動對話）。差距不是「誰流式做得好」，而是**兩個模型溢出到 SSD 的東西根本不同**——一個溢出的是每個 token 都要算的專家矩陣，另一個溢出的是不做矩陣乘法的查表。
>
> 視頻：HoaLab《Qwen3.8-Flash-Next vs GLM-5.3-Flash on a 64GB Mac》（2026-09-03 實測，前作為 GLM 101GB 測試）

## 目錄

- [1. 測試背景與核心懸念](#1-測試背景與核心懸念)
- [2. 模型結構拆解：兩種溢出機制](#2-模型結構拆解兩種溢出機制)
- [3. 實測數據全記錄](#3-實測數據全記錄)
- [4. 落地局限與評測警示](#4-落地局限與評測警示)
- [5. 硬體選型新準則](#5-硬體選型新準則)
- [6. 驗證與勘誤記錄](#6-驗證與勘誤記錄)
- [7. 決策樹：記憶體檔位選模型](#7-決策樹記憶體檔位選模型)
- [參考資料](#參考資料)

---

## 1. 測試背景與核心懸念

### 1.1 測試平台

| 項目 | 規格 |
|------|------|
| 機器 | Mac Studio 2023（M2 Max） |
| 統一記憶體 | 64GB |
| 記憶體帶寬 | 400 GB/s |
| 儲存 | 內建 SSD |
| 測試時間 | 2026-09-02 至 09-03 夜間（機器閒置） |
| 基準工具 | llama-bench，128 token prompt / 生成 64 token，每項重複 3 次取平均 |

### 1.2 懸念的由來

前作實測：GLM-5.3-Flash 101GB Q2 檔在 64GB Mac 上靠 SSD 串流溢出，暖機後 1.6~1.9 tok/s——只能當隔夜批處理工具。隨後 Reddit 一條帖子報稱：48GB Mac 上用 MLX 串流工具 **Slotstream** 跑 Qwen Q4 拿到 12 tok/s。評論區一針見血：「mmap 路徑到底能不能用，還是會退化成 page fault thrashing？」

本片就是這個問題的系統性答覆。結論先行：**Reddit 的結果在純 CPU 路徑就能復現，不需要 Metal**。

---

## 2. 模型结构拆解：两种溢出机制

### 2.1 架構對比（官方規格，已交叉驗證）

| 維度 | Qwen3.8-Flash-Next | GLM-5.3-Flash（ox-alpha） |
|------|--------------------|-----------------------------|
| 主幹總參數 | 125B | 320B |
| 每 token 激活參數 | ~6B（超稀疏 MoE） | ~18B |
| 特殊結構 | **51B N-gram embedding 查表** + 4B MTP 模組 | 無等價物（原生多模態） |
| 原始 checkpoint | ~360GB（BF16 級） | — |
| 上下文 | 長上下文 | 1M context，MIT 協議 |
| 溢出行為 | 查表可留 SSD，主幹爭取進 RAM | 溢出的是 MoE 專家層——每 token 必讀 |

### 2.2 N-gram 查表：本片的關鍵角色

Qwen3.8-Flash-Next 的 51B 參數 N-gram embedding 查表是架構中的特殊設計（vLLM 官方 recipes 原文確認）：

- **不做矩陣乘法（matrix math）**——它是尋址/查表操作
- 佔量化檔案約 **25% 體積**
- **設計上就適合留在 SSD / mmap 讀取**（AirLLM 等運行時正是 mmap 這張表）
- N-gram 的含義：N 是查表用的 token 序列長度（如 3-gram 表覆蓋每個可能的 3-token 組合）

```
兩種溢出機制對比（核心圖）

  GLM-5.3-Flash（320B-A18B）        Qwen3.8-Flash-Next（125B+51B+4B）

  ┌──────────────┐                  ┌──────────────┐
  │   64GB RAM   │                  │   64GB RAM   │
  │ ┌──────────┐ │                  │ ┌──────────┐ │
  │ │ 注意力+  │ │                  │ │ 主幹骨架  │ │
  │ │ 共享層    │ │                  │ │ (~75%檔案)│ │
  │ └──────────┘ │                  │ │ 矩陣運算  │ │
  └──────┬───────┘                  │ └──────────┘ │
         │ 溢出                     └──────┬───────┘
  ┌──────▼───────┐                         │ 溢出
  │ MoE 專家層    │ ←每個 token             ┌──────▼───────┐
  │ (矩陣乘法!)  │   都要從 SSD            │ N-gram 查表   │
  │  ⚠️ page     │   讀回來算              │ (25%, 無矩陣  │
  │  fault 抖動  │                        │  乘法, 尋址)  │
  └──────────────┘                        └──────────────┘
  → 1.9 tok/s 批處理                      → 16.3 tok/s 可互動
```

### 2.3 量化檔案的「骨架門檻」

主幹骨架（真正需要常駐 RAM 做高頻矩陣運算的部分）≈ 檔案的 75%：

| 量化檔 | 總大小 | 骨架（~75%） | 64GB 能否裝下骨架 |
|--------|--------|--------------|-------------------|
| Qwen Q2 | 79GB | ~59GB | ✅ 勉強塞進（64GB） |
| Qwen Q4 | 111GB | ~83GB | ❌ 骨架本身溢出 |
| GLM Q2 | 101GB | ~101GB（無查表可剝離） | ❌ 專家層溢出 |

> GLM 沒有等價的查表結構，101GB 全是需要參與計算的權重——溢出到 SSD 的就是每個 token 都要讀的專家矩陣。

---

## 3. 实测数据全记录

### 3.1 純 CPU 路徑（llama-bench，0 層卸載，預設 mmap）

| 模型檔 | 檔案大小 | 生成速度 (tok/s) | Prompt 處理 (tok/s) |
|--------|----------|------------------|---------------------|
| GLM Q2 | 101GB | 1.92 ± 0.19 | 1.87 |
| Qwen Q4 | 111GB | 6.01 ± 0.25 | 7.89 |
| **Qwen Q2** | 79GB | **11.62 ± 0.54** | **23.90** |

規律：兩個乘數決定差距——

1. **激活參數量**：GLM 每 token 18B vs Qwen 6B（3 倍）
2. **溢出內容**：GLM 高頻專家進溢出區 vs Qwen Q2 把查表放在 RAM 外、骨架完整進 RAM

Qwen Q4 落在中間：激活模型比 GLM 輕，但 83GB 骨架仍溢出。

> 原話點睛：「架構沒有推翻記憶體帶寬定律，它只是選擇了讓哪些位元組付過路費（Architecture did not repeal memory bandwidth. It merely chose which bytes had to pay the toll.）」

### 3.2 Metal GPU 分流調校（Qwen Q2）

N-gram 查表強制留在 CPU/SSD 端，預設 GPU 記憶體上限 55.6GB：

| 分流層數（共 48 層） | 結果 |
|----------------------|------|
| 全層卸載 | ❌ 失敗（GPU 分頁置換） |
| 36 層 | ❌ 失敗 |
| 30 層 | ❌ Prompt 階段失敗 |
| **24 層（半數）** | ✅ **生成 16.3 ± 0.24 tok/s，Prompt 44.5 tok/s** |
| 實際採樣生成（llama-cli，160 token） | 11.5 tok/s（連貫輸出） |

### 3.3 反例：骨架溢出時 Metal 是負優化

| 配置 | 生成速度 |
|------|----------|
| Qwen Q4 純 CPU | 6.01 tok/s |
| Qwen Q4 + Metal 24 層 | **4.55 ± 0.76 tok/s（更慢！）** |

**本機鐵律：骨架裝得下 → Metal 有益；骨架本身溢出 → Metal 有害。** 機制：GPU 端分頁置換比 CPU 端 mmap 更昂貴。

---

## 4. 落地局限与评测警示

視頻作者主動列出四項 caveat（優秀的評測衛生）：

1. **未測 sudo 提升 GPU 記憶體上限的路徑**——全 Metal 卸載需要 sysctl 提高 GPU 上限（參考前篇筆記 `iogpu.wired_mem_limit`），本片未跑，無數據
2. **Q2 質量未對比雲端原版**——採樣輸出「連貫」，但連貫 ≠ 質量基準；且模型先思考後作答，160 token 時還在 Reasoning 中
3. **非同類軟體對比**——GLM 需要 llama.cpp PR #27754 分支構建（2026-09-02 master 尚無 GLM 5 next 支持）；Qwen 用 9 月 2 日 master + Qwen4-exp。兩者都走 mmap 但是不同構建，只證明「各自當前最優路徑的表現」，不證明軟體路徑等價
4. **他人數據不可混入**——Reddit 用戶 Returnity：M5 Max 128GB 無 MTP 跑 Qwen Q4 達 36 tok/s；Carlos LFU：48GB Mac 走 Slotstream 12 tok/s。都是別人的機器，不是本機測量

### 思考 token 的隱性成本

實測採樣 160 token 時模型仍在思考中——**Reasoning 消耗的 token 預算與你等待的答案是同一個池子**。基準測試（純生成）與實際體驗（含思考）必須並列看，11.5 tok/s 的採樣速度才是真實體感。

---

## 5. 硬体选型新准则

視頻的昇華結論，從舊規則到新規則：

```
舊規則（GLM 視頻）：RAM 決定什麼能載入
                          ↓ 昇級
新規則（本片）：  溢出的是什麼，決定跑多快

  檔案大小 > RAM ？
  ├── 溢出的是查表（無矩陣乘法）→ SSD 串流仍可互動 ✅
  └── 溢出的是主幹專家網絡      → 退化為離線批處理 ❌

  檔案大小只是告訴你「有問題」
  模型解剖學（anatomy）才告訴你「問題是否可救」
```

下載前的三問（ actionable checklist）：

- [ ] 這個檔案裡**工作骨架（backbone）**有多大？
- [ ] 每個生成 token 會**反覆觸碰**哪些權重？
- [ ] 骨架能否完整鎖進 RAM？

### 記憶體檔位與模型選擇

| 記憶體 | 建議 | 依據 |
|--------|------|------|
| 48GB | Qwen Q2 級（Slotstream 串流，12 tok/s 級） | 骨架 59GB 也放不下，靠查表外置 + 串流工具 |
| 64GB | **Qwen Q2 + Metal 24/48 分流 = 16.3 tok/s**（本片最優解） | 骨架 59GB 勉強全進 RAM |
| 96GB / 128GB | Qwen Q4 骨架 83GB 可完整進 RAM，進入新 regime | 128GB M5 Max 實測可達 36 tok/s（他人數據） |

> 2026-09-22 預告：作者將在 M5 硬件上重跑同一測試階梯（含提高 GPU 上限的 sudo 路徑）——值得跟蹤。

---

## 6. 验证与勘误记录

| 視頻聲稱 | 驗證結果 | 來源 |
|----------|----------|------|
| Qwen3.8-Flash-Next = 125B 主幹 + 51B N-gram 查表 + 4B MTP，6B 激活 | ✅ vLLM 官方 recipes 原文一致；kaitchup（原始 checkpoint ~360GB）、NVIDIA 開發者論壇、unsloth、HF GGUF 卡多源確認 | vLLM / kaitchup / NVIDIA |
| N-gram 查表不做矩陣乘法、設計為可留 SSD | ✅ unsloth 文檔稱「like a lookup table」；AirLLM 實現正是 mmap 該表；Reddit 架構討論帖解釋 N-gram = N-token 序列索引 | unsloth / Medium(AirLLM) / Reddit |
| GLM-5.3-Flash = 320B 總參 / 18B 激活，無查表等價物 | ✅ z.ai 官方博客確認（代號 ox-alpha，原生多模態，1M context，MIT，性能超 GLM-5.2） | z.ai / unsloth / Reddit |
| Slotstream 工具存在，作者 carloslfu，48GB Mac 跑 Qwen | ✅ ai-tldr 發布頁：「v0.2.0 — a 104GB model on a 48GB Mac，streams experts off SSD，runs Qwen3.8-Flash-Next」 | ai-tldr.dev |
| 實測數值（1.92 / 6.01 / 11.62 / 16.3 tok/s 等） | ⚠️ 視頻自報但方法論嚴謹：3 次重複取平均±標準差、明確區分基準與採樣、標注他人數據不可混入 | 視頻 |
| llama.cpp PR #27754（GLM 5 next 支持） | ⚠️ 細節未獨立核實；視頻自己已將其列為 caveat（非同類構建） | 視頻 |
| 字幕轉寫噪音 | 已修正：Quen→Qwen、engram→N-gram、MAPAP→mmap、Quen 4 exp→Qwen4-exp | 本筆記 |

> 頻道鑑別說明：HoaLab 與同類 Mac 本地 LLM 實測頻道（如 Bart Slodyczka）風格相近——方法論透明、主動列 caveat、區分自測與轉述數據，可信度高。

---

## 7. 决策树：内存档位选模型

```
手上的 Mac 有多少統一記憶體？
│
├── 48GB
│    └── Qwen Q2/Q4 級 + Slotstream 串流（查表外置）
│        → 12 tok/s 級（他人實測，待自驗）
│
├── 64GB（M2/M3/M4 Max 級，400GB/s）
│    ├── 要互動體驗 → Qwen Q2（骨架 59GB 進 RAM）
│    │     └── Metal 24/48 層分流 + 查表留 CPU = 16.3 tok/s
│    ├── 只跑隔夜批處理 → GLM Q2 也可（1.9 tok/s）
│    └── ❌ 別碰 Qwen Q4（骨架 83GB 溢出，Metal 反而更慢）
│
├── 96GB / 128GB
│    └── Qwen Q4 骨架 83GB 完整進 RAM，速度 regime 切換
│        （128GB M5 Max 實測 36 tok/s，他人數據）
│
└── 選任何模型前先問：
     骨架多大？激活參數多少？溢出的是查表還是專家？
```

---

## 參考資料

- [Qwen3.8-Flash-Next vs GLM-5.3-Flash on a 64GB Mac — HoaLab（視頻）](https://www.youtube.com/watch?v=7E5DYu24bxw)
- [Qwen3.8-Flash-Next — vLLM 官方 recipes（架構規格）](https://recipes.vllm.ai/Qwen/Qwen3.8-Flash-Next)
- [GLM-5.3-Flash: Frontier Intelligence, Flash Cost — z.ai 官方博客](https://z.ai/blog/glm-5.3-flash)
- [Qwen3.8-Flash-Next: How to Run Locally — Unsloth](https://unsloth.ai/docs/models/qwen3.8-next)
- [Qwen3.8-Flash-Next Review — Kaitchup（checkpoint 體積）](https://kaitchup.substack.com/p/qwen38-flash-next-review-benchmarks)
- [Slotstream v0.2.0 發布解讀 — ai-tldr.dev](https://ai-tldr.dev/releases/carloslfu-slotstream/)

## 相關筆記

- [[Qwen3.8-27B 模型评测与 SGLang 高速推理部署]] — 同家族 dense 27B 版本的高速部署路線
- [[Mac Studio oMLX + DeepSeek Harness 本地部署 Qwen3.8-27B 实战]] — 64GB 內跑 27B 的正統路線（不溢出）
- [[本地 LLM 硬件选购指南 - M5 Ultra vs RTX 5090 vs DGX Spark]] — 帶寬/容量選購基礎（本篇新增「溢出內容類型」維度）
- [[Llama.cpp + TurboQuant 本地 LLM 部署指南]] — llama.cpp 生態與 KV 壓縮
