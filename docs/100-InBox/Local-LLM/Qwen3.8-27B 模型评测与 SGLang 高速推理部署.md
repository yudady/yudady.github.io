---
title: Qwen3.8-27B 模型评测与 SGLang 高速推理部署
aliases: [Qwen3.8-27B 实测, SGLang 部署 Qwen, 27B 本地模型标竿]
tags:
  - llm
  - qwen
  - sglang
  - inference
  - quantization
  - local-llm
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=PTuGGdDuyPI"
  - "https://huggingface.co/collections/Qwen/qwen38"
  - "https://lmsysorg.mintlify.app/cookbook/autoregressive/Qwen/Qwen3.8-27B"
author: Sam Witteveen（頻道）
created: 2026-09-05
updated: 2026-09-05
description: Qwen3.8-27B 基準表現、版本生態（BF16/FP8/NVFP4/MLX/去審查分支）與 SGLang 173 t/s 極限吞吐部署實錄，含思考模式 token 消耗實測與配置決策
level: intermediate
stars: 5
note: Tier 0 英文字幕直接成功；Artificial Analysis 分數、Muse Glimmer 對比、SGLang 吞吐均經外部多源交叉驗證。視頻由 Dell 贊助算力（RTX Pro 6000），部署結論基於單一 Blackwell 級硬件。
---

# Qwen3.8-27B 模型评测与 SGLang 高速推理部署

> Qwen3.8-27B 把旗艦 Qwen3.8 Max（2.4T 參數）的推理與 Agentic 能力下放到 27B 級——Artificial Analysis 智能指數 52 分（GLM 5.2 為 53），Agentic Index 超越 GPT-5.6 Terra，成為當前本地開源權重的標竿模型。本篇拆解：選哪個版本、怎麼配思考模式、以及 SGLang 在 96GB Blackwell 卡上榨出 173 t/s 的完整路徑。
>
> 視頻：Sam Witteveen《Qwen3.8-27B & How to Serve it Fast》（2026-08-18，15.6 萬播放，128K 訂閱）

## 目錄

- [1. 模型定位：旗艦能力下放](#1-模型定位旗艦能力下放)
- [2. 基準表現與權威評測](#2-基準表現與權威評測)
- [3. 版本生態與量化選擇](#3-版本生態與量化選擇)
- [4. 推理框架對比與 SGLang 部署](#4-推理框架對比與-sglang-部署)
- [5. 思考模式配置權衡（核心實測）](#5-思考模式配置權衡核心實測)
- [6. 驗證與勘誤記錄](#6-驗證與勘誤記錄)
- [7. 部署決策樹](#7-部署決策樹)
- [參考資料](#參考資料)

---

## 1. 模型定位：旗艦能力下放

時間線：Qwen 先發 2.4T 參數的 Qwen3.8 Max（極少人能本地跑）→ 社群真正等待的 27B 隨後落地。同週 Meta 搶先發布 Muse Glimmer 30B 對標——視頻作者判斷 Meta 是「知道 3.8-27B 要來了搶先卡位」。

```
2026-08 模型發布競速

  Qwen3.8 Max (2.4T)  ──→  Qwen3.8-27B   ← 社群真正的主角
  Meta Muse Glimmer 30B ──┘   （對標前代 Qwen3.6-27B，被全面反超）
                                    │
                                    ▼
                     本地開源權重新標竿（AA 52 分）
```

| 對比對象 | 結果 |
|----------|------|
| vs Qwen3.6-27B（前代社區寵兒） | 幾乎所有指標大幅提升，drop-in 替換 |
| vs Meta Muse Glimmer 30B | Qwen 官方基準全面超越；llm-stats 第三方對比 8 項基準 27B 全勝（CharXiv-R、GPQA、HLE、IFBench 等） |
| vs Opus 4.6 Max | 電腦操作（Computer Use）等視覺 Agent 任務反超 |
| vs GLM 5.2 / GPT-5.6 | 智能指數 1 分之差；Agentic Index 反超 |

> 前作銜接：Qwen3.6-27B 時代已有 ThinkingCap 微調（壓縮思考 token 保持智能），視頻預期 3.8 也會出 ThinkingCap 版本。

---

## 2. 基準表現與權威評測

| 評測體系 | 成績 | 參照系 |
|----------|------|--------|
| Artificial Analysis Intelligence Index | **52** | GLM 5.2 = 53；DeepSeek V4 Pro 同檔；遠超 Qwen3.6-27B 與其他可本地跑的開源模型 |
| Artificial Analysis Agentic Index | 超越 GLM 5.2 與部分 GPT-5.6 型號 | 含 GPT-5.6 Terra |
| Qwen 官方基準（視覺/Agent） | Computer Use 超越 Opus 4.6 Max | 前代 Opus 對 computer use 側重不足 |

第三方佐證（本筆記交叉驗證）：

- HN 討論：「Qwen3.8-27B 擊敗所有中型模型（40B–150B），高推理檔位下 Agentic 表現強勁」
- kingy.ai：「Agentic Index 排在 GPT-5.6 Terra 之上，單張 24GB GPU 可跑（注意基準與量化的 caveat）」
- Linas Newsletter：「AA 智能指數 52，綜合推理/知識/數學/編碼」
- orcarouter：Terminal-Bench 上 Qwen3.8 86.6 vs GLM-5.2 82.7（3.9 分名義領先）

> ⚠️ 分數解讀注意：AA 52 是**最高推理檔位（max reasoning effort）**下的成績——低思考檔位實際表現會明顯縮水（見第 5 節）。

---

## 3. 版本生態與量化選擇

HuggingFace Qwen3.8 collection 下的版本圖譜：

```
Qwen3.8-27B 版本生態

官方
├── BF16（全精度 ~54GB）        ← 旗艦參照，需大顯存
└── FP8（~27GB）                ← 官方效率版

社群量化
├── Unsloth NVFP4（4-bit）      ← 注意：僅部分 GPU 支援 NVFP4
├── MLX 社區版（Mac 專屬）       ← BF16 / 8-bit / 4-bit 全譜系
│    （含 -mtp 推測解碼變體，見 [[Mac Studio oMLX + DeepSeek Harness 本地部署 Qwen3.8-27B 实战]]）
└── 去審查分支
     ├── 資料集微調（Uncensored/Heretic）
     └── Abliteration（權重切除）— Black Frost AI 等首發
```

| 版本 | 適用場景 | 代價 |
|------|----------|------|
| BF16 | 96GB+ VRAM，質量基準 | ~30 t/s 純生成（96GB 卡） |
| FP8 | 32GB+ VRAM，官方效率解 | 幾乎無質感損失 |
| NVFP4 | Blackwell 架構（RTX Pro 6000 / DGX Spark） | 需確認 GPU 支援 |
| MLX 4/8-bit | Apple Silicon | 帶寬受限，速度看芯片檔位 |
| Abliterated | 去審查需求 | **實測易陷入思考重複循環**，視頻作者建議暫不採用 |

---

## 4. 推理框架对比与 SGLang 部署

### 4.1 實測速度階梯（RTX Pro 6000 96GB，Dell T2 Pro Max）

| 配置 | 框架 | 吞吐（純生成） |
|------|------|---------------|
| BF16，無投機解碼 | vLLM | ~30 t/s |
| BF16 + MTP=3 投機解碼 | vLLM | 明顯提速（未給具體值） |
| 官方 FP8 + MTP | vLLM | 80~120 t/s |
| Unsloth 量化 + MTP | vLLM | ~120 t/s |
| **SGLang 專屬 NVFP4 + DSpark 投機解碼** | **SGLang Docker** | **平均 173 t/s**（短文本峰值 200~220；長文本穩定 150~170） |

### 4.2 SGLang 最優配置要點

- 官方 cookbook 提供專屬 Docker 映像（Blackwell 系優先：RTX Pro 6000 / NVFP4 / DGX Spark 配置檔）
- **必須用 SGLang 自己的 NVFP4 權重**——換 Unsloth 量化權重套同一配置效果變差
- 視頻實測：36,000 token 生成全程平均 173 t/s，262K 完整上下文窗口開啟
- SGLang 官方 X 宣稱最高 206 t/s（與視頻峰值 200~220 吻合）

```
262K 上下文下的吞吐分層

  220 ┤ ●●●  短文本峰值
  200 ┤●●●●
  173 ┤━━━━━━━━━━ 全程平均（36K token 生成）
  160 ┤      ●●●●●●  長上下文穩態
  150 ┤
      └────────────────────→ 生成長度
```

### 4.3 框架選擇

| 框架 | 定位 |
|------|------|
| SGLang | Blackwell 新卡性能王，Docker 部署 |
| vLLM | 通用性最好，FP8 + MTP 已很快 |
| llama.cpp | 低 VRAM / 消費級卡 / GGUF 生態 |
| oMLX | Mac 專屬（前篇筆記路線，MTP 權重自動激活） |

> 視頻方法論：把框架文檔餵給本機 Coding Agent，讓它自動化測不同配置組合的吞吐——比人工試快得多。

---

## 5. 思考模式配置權衡（核心實測）

**視頻最有價值的發現：模型好不好用，思考 token 配置比量化等級影響更大。**

### 5.1 HTML 網頁測試（Dario Wellness Retreat 梗）

| 檔位 | 思考 token 消耗 | 結果質量 |
|------|----------------|----------|
| 關閉（No thinking） | 0 | 質量穩定、最快，很多人反而最喜歡這版 |
| Low | 512（多次運行較一致） | 風格不同（更多特效），同樣優秀 |
| Medium | **比 Low 還少**（反直覺，多次復現） | 與 Low 相似 |
| XHigh（無 High 檔） | **17,500~22,000**，32K 輸出上限被思考吃光，任務未完成 | 細節更多但性價比崩塌 |

### 5.2 SVG 鵜鶘測試（Simon Willison 經典測試）

| 檔位 | 思考 token | 畫面 |
|------|-----------|------|
| 關閉 | 0 | 明顯變醜 |
| Low | 略高於 Medium | 細節開始缺失 |
| Medium | ~1,000 | **甜點檔**：與 11K 思考的版本差距很小 |
| XHigh | 11,000 | 略好，不值得 |

### 5.3 過擬合驗證（紅龍 SVG）

換成紅龍後：無思考→幼稚畫風；Low（420 tokens）→改善；Medium（2,000 tokens）→龍反而退化；XHigh（21,000/35,000 tokens）→略好但成本瘋狂。

```
思考檔位決策樹

任務類型？
├── 純代碼生成 / HTML / 樣板代碼
│    └── 關閉思考 —— 質量穩定 + 零等待
├── 有細節要求的創作（SVG / 精細 UI）
│    └── Medium —— 甜點檔（~1K tokens 換 90% 質量）
├── 複雜邏輯 / 規劃 / 數學
│    └── High 級別才開始有意義，且必須開大上下文窗口
└── 跑基準測試刷分
     └── XHigh + max reasoning（AA 52 分的前提條件）
```

關鍵規律總結：

1. **思考 token 消耗非單調**：Medium 有時少於 Low（多次復現，非確定性），不能假設檔位越高消耗越多
2. **XHigh 是怪獸檔**：單任務 1.7萬~2.2萬思考 token，必須同時開大 max tokens 與上下文，否則任務直接失敗
3. 該規律跨權重一致（FP8 / BF16 / Unsloth 版本都一樣）
4. 展望：ThinkingCap 式微調（更少思考 token 換同等智能）預計會出現，值得跟蹤

---

## 6. 驗證與勘誤記錄

| 視頻聲稱 | 驗證結果 | 來源 |
|----------|----------|------|
| Qwen3.8 Max 2.4T 參數，27B 隨後發布 | ✅ 屬實（HF collection 確認，27B 2026-08-14 上架） | HuggingFace |
| Meta「Muse Glimmer 30B」存在 | ✅ 屬實（初判可疑——不符合 Llama 命名慣例，實為 Meta 新 Muse 系開源模型；HN/llm-stats/Reddit 多源確認） | llm-stats、HN、Meta 官方基準 |
| Qwen3.8-27B 大幅超越 Muse Glimmer | ✅ llm-stats 對比 8 項基準全勝；但 orcarouter 補充：Meta 自家表上 Glimmer 在 SWE-Bench Pro（51.2 vs 50.2）仍小勝 | llm-stats、orcarouter |
| AA Intelligence Index 52（GLM 5.2 = 53） | ✅ 多源一致（HN、X、LinkedIn、Linas、qubrid）；注意是 max reasoning 檔位成績 | Artificial Analysis 生態 |
| Agentic Index 超越 GPT-5.6 / GLM 5.2 | ✅ HN + kingy.ai 確認（超 GPT-5.6 Terra） | HN、kingy.ai |
| SGLang 平均 173 t/s、峰值 200~220、262K 上下文 | ⚠️ 視頻實測自報，與 SGLang 官方宣稱 206 t/s 吻合；硬件為贊助商提供的 RTX Pro 6000 單卡 | 視頻 + SGLang X 帳號 |
| Abliterated 版本易陷入思考循環 | ⚠️ 作者個人實測結論（合理但樣本有限） | 視頻 |
| ThinkingCap 是 3.6 時代微調 | ✅ 視頻前作引用；3.8 版本屬預期而非現實 | 視頻 |
| Medium 思考有時比 Low 消耗更少 | ⚠️ 反直覺但視頻多次復現；屬模型非確定性行為，值得自行驗證 | 視頻實測 |

> 命名驗證插曲：「Muse Glimmer 30B」乍看像口播誤讀（Meta 慣用 Llama 命名），實測為 Meta 新產品線真名——**疑點要驗證，但別急著當勘誤**，命名慣例會被廠商自己打破。

---

## 7. 部署决策树

```
要跑 Qwen3.8-27B？
├── 什麼硬件？
│    ├── Blackwell 工作站卡（RTX Pro 6000 / DGX Spark）
│    │    └── SGLang Docker + 官方 NVFP4 + DSpark 投機解碼（173+ t/s）
│    ├── 32GB+ 消費級 / 專業卡
│    │    └── vLLM + 官方 FP8 + MTP（80~120 t/s）
│    ├── 24GB 消費卡（3090/4090）
│    │    └── llama.cpp + GGUF 4-bit（kingy.ai 實測可跑）
│    └── Mac（Apple Silicon）
│         └── oMLX + MLX 量化（見前篇筆記，50+ t/s on Max/Ultra）
├── 要去審查？
│    └── 微調版優先；abliteration 版謹慎（思考循環風險）
└── 配思考檔位
     ├── 刷分 → XHigh + 大上下文
     ├── 日常 Agent → Medium 甜點檔
     └── 樣板代碼 → 關閉
```

---

## 參考資料

- [Qwen3.8-27B & How to Serve it Fast — Sam Witteveen（視頻）](https://www.youtube.com/watch?v=PTuGGdDuyPI)
- [Qwen3.8 系列 — HuggingFace Collection](https://huggingface.co/collections/Qwen/qwen38)
- [SGLang Cookbook: Qwen3.8-27B 部署指南](https://lmsysorg.mintlify.app/cookbook/autoregressive/Qwen/Qwen3.8-27B)
- [Muse Glimmer-30B vs Qwen3.8-27B — llm-stats 對比](https://llm-stats.com/models/compare/muse-glimmer-30b-vs-qwen3.8-27b)
- [Qwen3.8-27B Beats GPT-5.6 Terra on Agentic AI — kingy.ai](https://kingy.ai/blog/qwen3-8-27b-agentic-index-rtx-3090-4090/)
- [The Ultimate Guide to Qwen3.8-27B — Linas's Newsletter](https://linas.substack.com/p/qwen3-8-27b-local-guide)
- [Qwen3.8-27B scores 52 on Artificial Analysis — HN 討論](https://news.ycombinator.com/item?id=49334544)

## 相關筆記

- [[Mac Studio oMLX + DeepSeek Harness 本地部署 Qwen3.8-27B 实战]] — 同一模型的 Mac/oMLX 路線（MLX -mtp 量化，50+ t/s）
- [[本地 LLM 硬件选购指南 - M5 Ultra vs RTX 5090 vs DGX Spark]] — 硬件帶寬與選購邏輯
- [[Llama.cpp + TurboQuant 本地 LLM 部署指南]] — 低 VRAM 消費卡路線
