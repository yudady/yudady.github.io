---
title: PaddleOCR - 百度開源 OCR 工具包與文件 AI 引擎
aliases: [PaddleOCR, PP-OCRv5, PaddleOCR-VL]
tags:
  - ocr
  - document-ai
  - python
  - llm-integration
  - status/active
  - type/learning-note
source:
  - "https://youtu.be/OO-QuzthER8"
  - "https://github.com/PaddlePaddle/PaddleOCR"
author: Baidu PaddlePaddle Team
created: 2026-06-07
updated: 2026-06-07
description: PaddleOCR 是百度飛槳團隊出品的開源 OCR 工具包，70k+ GitHub Stars，將 PDF/圖片轉為 LLM 可消費的結構化資料（JSON/Markdown），在公開評測中擊敗多個商業方案。
level: intermediate
stars: 5
note: 無字幕，基於 GitHub README + 官方文檔 + 技術報告 + 社群評測綜合整理
---

# PaddleOCR - 百度開源 OCR 工具包與文件 AI 引擎

> PaddleOCR 是目前全球最具影響力的開源 OCR 專案（70k+ Stars），能將 PDF 和圖片轉換為結構化的 LLM-Ready 資料。最新 PaddleOCR-VL-1.6 在 OmniDocBench v1.6 達到 96.3% 精度，超越所有開源與閉源方案。被 Dify、RAGFlow、Microsoft OmniParser 等 6500+ 專案採用。

## 目錄

- [核心能力全景](#核心能力全景)
- [四大核心元件](#四大核心元件)
- [PaddleOCR-VL 視覺語言模型](#paddleocr-vl-視覺語言模型)
- [PP-OCRv5 全場景文字識別](#pp-ocrv5-全場景文字識別)
- [PP-StructureV3 文件結構解析](#pp-structurev3-文件結構解析)
- [技術規格與生態](#技術規格與生態)
- [快速開始](#快速開始)
- [RAG 整合方案](#rag-整合方案)
- [版本演進與注意事項](#版本演進與注意事項)
- [優缺點評估](#優缺點評估)

---

## 核心能力全景

```
┌─────────────────────────────────────────────┐
│           PaddleOCR 3.x 生態系統             │
├────────────┬────────────┬───────────────────┤
│  場景 OCR   │  文件 AI   │   AI Agent 整合    │
│            │            │                   │
│ PP-OCRv5   │ PP-Struct  │ Agent Skills      │
│ 場景文字    │ V3         │ MCP Server         │
│ 識別       │ 文件結構    │ LangChain          │
│            │ 解析       │ PaddleOCR.js       │
│            │            │                   │
│ 100+ 語言  │ PDF→Markdown│ Dify/RAGFlow 整合 │
├────────────┴────────────┴───────────────────┤
│         PaddleOCR-VL (0.9B VLM)              │
│   統一文件理解：文本+表格+公式+圖表+印章       │
└─────────────────────────────────────────────┘
```

---

## 四大核心元件

| 元件 | 定位 | 核心能力 | 適用場景 |
|------|------|----------|----------|
| **PaddleOCR-VL** | 視覺語言模型 | 0.9B 參數，109 語言，處理文本/表格/公式/圖表 | 複雜文件端到端理解 |
| **PP-OCRv5** | 文字識別模型 | 5 種文字類型（簡中/繁中/英/日/拼音），100+ 語言 | 場景 OCR、多語言混合 |
| **PP-StructureV3** | 文件結構解析 | PDF/圖片 → Markdown/JSON，保留版式層次 | RAG 文件預處理 |
| **PP-ChatOCRv4** | 智能資訊抽取 | 整合 ERNIE 4.5，問答式抽取關鍵資訊 | 發票/表單/合約抽取 |

### 選擇決策樹

```
你要處理什麼？
  ├── 單張圖片中的文字 → PP-OCRv5（場景 OCR）
  ├── PDF/掃描件轉結構化 → PP-StructureV3 或 PaddleOCR-VL
  ├── 從文件中抽取特定資訊 → PP-ChatOCRv4
  └── 最複雜的文件理解 → PaddleOCR-VL（統一處理）
```

---

## PaddleOCR-VL 視覺語言模型

### 版本演進

| 版本 | 日期 | OmniDocBench 精度 | 關鍵升級 |
|------|------|-------------------|----------|
| VL-1.0 | 2025 | 基礎版 | 首次發佈 |
| VL-1.5 | 2026.01 | **94.5%** | 異形框定位、印章識別、spotting |
| VL-1.6 | 2026.05 | **96.3%** | 文本/公式/表格全面 SOTA，古籍/生僻字大幅提升 |

### 核心特性

- **0.9B 超緊湊參數量** — 對比通用 VLM（7B+），資源消耗極低
- **109 種語言** — 多語種文件解析
- **異形框定位** — 掃描件、傾斜、彎折、螢幕拍攝、複雜光照場景
- **零成本遷移** — VL-1.5 → VL-1.6 結構一致，無縫升級

### 與通用 VLM 對比

| 指標 | PaddleOCR-VL-1.6 | 通用 VLM（GPT-4o 等） |
|------|-----------------|---------------------|
| 文件解析精度 | 96.3%（SOTA） | 較低 |
| 模型大小 | 0.9B | 7B-70B+ |
| 推理成本 | 極低 | 極高 |
| 表格/公式識別 | 專門優化 | 依賴通用能力 |
| 可本地部署 | ✅ | ❌（閉源） |

---

## PP-OCRv5 全場景文字識別

### 解決的問題

多語言混合文件是 OCR 的經典難題。PP-OCRv5 用**單一模型**支援 5 種文字類型：

```
簡體中文 ←→ 繁體中文 ←→ 英文 ←→ 日文 ←→ 拼音
                    全部用同一模型處理
```

### 性能提升

| 指標 | PP-OCRv4 | PP-OCRv5 | 提升 |
|------|----------|----------|------|
| 多語言精度 | 基準 | +13 百分點 | 📈 |
| 語言覆蓋 | 中英日韓 | 37+ 語言（含西里爾/阿拉伯/天城文） | 📈 |
| 手寫體支援 | 有限 | 顯著改善 | 📈 |

### Reddit 社群評價

> 「PP-OCRv5 作為 70M 參數的專門 OCR 模型，在多數 OCR 任務上持續超越 Gemini 2.5 Pro 等通用 VLM 模型。」 — r/LocalLLaMA

---

## PP-StructureV3 文件結構解析

### 核心價值

將複雜 PDF 和文件圖片智能轉換為 **Markdown 和 JSON**，完美保持原始版式和層次結構。

```
PDF 文件（含表格、圖片、公式）
    │
    ▼ PP-StructureV3
    │
    ├── Markdown（保留標題層次、表格結構、公式）
    ├── JSON（結構化資料）
    └── AI-Ready → 直接餵給 LLM/RAG
```

### 在公開評測中的表現

在文件結構解析的公開評測中**領先眾多商業方案**，這也是影片標題的來由。

---

## 技術規格與生態

| 項目 | 規格 |
|------|------|
| Python | 3.8–3.12 |
| 作業系統 | Linux, Windows, macOS |
| 硬體 | CPU, GPU, XPU, NPU（崑崙芯、昇騰等國產硬體） |
| 授權 | Apache 2.0 |
| GitHub Stars | 70k+ |
| 被依賴 | 6,500+ 個倉庫 |
| 版本 | v3.6.0（2026.05.28） |

### 採用 PaddleOCR 的知名專案

| 專案 | 領域 |
|------|------|
| **Dify** | AI Agent 工作流平台 |
| **RAGFlow** | 基於深度文件理解的 RAG 引擎 |
| **MinerU** | 多型態文件轉 Markdown 工具 |
| **Microsoft OmniParser** | 螢幕解析 GUI Agent |
| **Cherry Studio** | 多 LLM 桌面客戶端 |
| **Umi-OCR** | 免費離線批次 OCR 軟體 |
| **Haystack** | AI 編排框架 |
| **QAnything** | 網易有道問答系統 |

---

## 快速開始

### 選項 1：線上使用（零安裝）

訪問 [paddleocr.com](https://www.paddleocr.com) 體驗中心，免費 API 每日 20,000 頁。

### 選項 2：本地部署

```bash
pip install paddlepaddle paddleocr
```

基本用法：

```python
from paddleocr import PaddleOCR

ocr = PaddleOCR(use_angle_cls=True, lang='ch')
result = ocr.ocr('example.png', cls=True)

for line in result:
    for word_info in line:
        print(word_info[1][0])  # 識別文字
```

### 選項 3：Docker

```bash
# 官方鏡像
docker pull paddlepaddle/paddleocr
```

---

## RAG 整合方案

PaddleOCR 在 RAG 應用中的典型角色：

```
PDF/掃描件/圖片
    │
    ▼ PaddleOCR（PP-StructureV3 或 PaddleOCR-VL）
    │
    ├── Markdown（保留結構）
    │
    ▼ Text Splitter（按標題/段落切分）
    │
    ▼ Embedding Model
    │
    ▼ Vector DB
    │
    └── LLM Retrieval + Generation（RAG）
```

### 整合方式

| 方式 | 說明 |
|------|------|
| **LangChain** | `langchain-paddleocr` 整合包 |
| **MCP Server** | 官方 `mcp_server/` 實現，可與 Claude/Cursor 整合 |
| **Agent Skills** | 官方 Skills，可在支持 Skills 的 AI 應用中直接調用 |
| **API** | paddleocr.com 免費 API |

---

## 版本演進與注意事項

### ⚠️ 2.x → 3.x 破壞性變更

PaddleOCR 3.x 引入了**多項重要接口變動**，基於 2.x 的舊代碼**很可能無法在 3.x 上運行**。

```python
# ⚠️ 舊代碼（2.x）
from paddleocr import PaddleOCR
ocr = PaddleOCR()

# ✅ 新代碼（3.x）— 需參考最新文檔確認接口
```

**建議**：閱讀 [升級指南](https://paddlepaddle.github.io/PaddleOCR/latest/update/upgrade_notes.html) 了解完整變更。

### 版本里程碑

| 時間 | 事件 |
|------|------|
| 2025.05 | PaddleOCR 3.0 發佈，適配飛槳框架 3.0 |
| 2025 下半年 | PP-OCRv5 發佈（CVPR 接收） |
| 2026.01 | PaddleOCR-VL-1.5（94.5% OmniDocBench） |
| 2026.05 | PaddleOCR 3.6.0 + PaddleOCR-VL-1.6（96.3%） |

---

## 優缺點評估

### ✅ 優點

- **開源免費**，Apache 2.0 授權，可商用
- **精度 SOTA**，公開評測擊敗商業方案
- **生態成熟**，70k Stars，6500+ 專案採用，Dify/RAGFlow 等主流專案整合
- **部署靈活**：CPU/GPU/XPU/NPU，Docker，線上 API
- **LLM-Ready**：原生輸出 Markdown/JSON，與 RAG 流水線無縫銜接
- **多語言**：109 語言支援
- **PaddleOCR-VL 0.9B 極輕量**，對比通用 VLM 成本極低
- **AI Agent 整合**：MCP Server、Agent Skills、LangChain

### ❌ 缺點與風險

- **依賴飛槳框架**：核心運行依賴 PaddlePaddle，不兼容 PyTorch 生態（需注意環境衝突）
- **2.x → 3.x 破壞性變更**：升級成本高
- **文檔質量**：中文文檔較完善，英文/其他語言文檔可能有滯後
- **國產硬體支援**：雖然支援崑崙芯/昇騰，但主要測試環境仍是 NVIDIA GPU
- **社群主要在國內**：GitHub Issues 回應速度、英文社群活躍度不及國際化專案

---

## 参考资料

- [PaddlePaddle/PaddleOCR GitHub](https://github.com/PaddlePaddle/PaddleOCR)
- [PaddleOCR 官方文檔](https://paddlepaddle.github.io/PaddleOCR/latest/)
- [PaddleOCR 3.0 技術報告 (arXiv)](https://arxiv.org/html/2507.05595v1)
- [PaddleOCR 官網（線上體驗 + API）](https://www.paddleocr.com)
- [PP-OCRv5 Reddit 討論](https://www.reddit.com/r/LocalLLaMA/comments/1nerv64/ppocrv5_70m_modular_ocr_model/)
- [PaddleOCR-VL + RAG 實戰](https://gaodalie.substack.com/p/paddleocr-vl-rag-revolutionize-complex)

## 相关笔记

- [[Scrapling - AI 自適應網頁爬蟲框架]]
- [[RAG 文件預處理工具對比]]
