---
title: Windows 10 LTSC 長期服務版安裝啟用指南
aliases: [LTSC 2021 安裝, Win10 續命方案, Enterprise LTSC 教程]
tags:
  - windows
  - ltsc
  - system-install
  - low-spec
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=tz3jM6UoDWo"
  - "https://www.freedidi.com/19474.html"
  - "https://www.freedidi.com/4805.html"
author: 零度解說（頻道）
created: 2026-09-05
updated: 2026-09-05
description: Windows 10 停止支援後的 LTSC 長期服務版方案：官方 ISO 下載、Ventoy 隨身碟、乾淨安裝、MAS(HWID) 啟用全流程，含 2026 視角的版本支持期時效警告
level: intermediate
stars: 4
note: Tier 0 語言重試成功（zh-Hant 自動字幕，LTSC 被轉寫為 LTLC 已修正）。激活工具 get.activated.win 經 GitHub 證實為 MAS 開源激活腳本（非微軟授權渠道，已標注合規邊界）。視頻 2025-05 發布，推薦的 Enterprise LTSC 2021 支持期至 2027-01（現僅剩約 4 個月），IoT 版至 2032——時效警告已列入勘誤。
---

# Windows 10 LTSC 長期服務版安裝啟用指南

> Windows 10 家用/專業版 2025-10-14 停止支援後，不想升 Win11 或硬體受限的機器還有一條官方出路：**企業級長期服務通道（LTSC, Long-Term Servicing Channel）**——無預裝冗餘、無廣告、只收安全更新。本篇是完整安裝啟用流程 + 2026 視角的版本選擇時效修正。
>
> 視頻：零度解說《Windows 10 LTSC 長期服務版》（2025-05-18 實測錄屏）；文字版 freedidi.com/19474.html

## 目錄

- [1. 背景：為什麼是 LTSC](#1-背景為什麼是-ltsc)
- [2. 版本選擇（含 2026 時效修正）](#2-版本選擇含-2026-時效修正)
- [3. 介質製作：Ventoy](#3-介質製作ventoy)
- [4. 安裝流程](#4-安裝流程)
- [5. 系統啟用（MAS/HWID）](#5-系統啟用mashwid)
- [6. 驗證與勘誤記錄](#6-驗證與勘誤記錄)
- [7. 決策樹：Win10 停支援後的出路](#7-決策樹win10-停支援後的出路)
- [參考資料](#參考資料)

---

## 1. 背景：為什麼是 LTSC

| 維度 | Win10 家用/專業版 | Win10 Enterprise LTSC |
|------|-------------------|------------------------|
| 支援截止 | 2025-10-14（已過） | 依版本 2027-01 / IoT 至 2032-01 |
| 預裝軟體 | Candy Crush、Teams 推廣等一堆 | 幾乎零預裝 |
| Microsoft Store | 內建 | 預設無（可另行開啟） |
| Edge | 內建 | 內建（LTSC 2021 起含） |
| 功能更新 | 半年度大版本推送 | 無功能更新，僅安全修補 |
| 適合人群 | 消費者 | 工控/ATM/醫療/追求穩定的極客 |

核心賣點：**只收安全修補、不做功能變更**——不會被大版本更新搞出相容性崩潰與驅動衝突，安裝後系統槽乾淨（視頻實測裝完桌面零第三方圖標）。

---

## 2. 版本選擇（含 2026 時效修正）

### 2.1 視頻當時的建議（2025-05 視角）

| 版本 | 內部版本號 | 視頻評價 |
|------|-----------|----------|
| **Enterprise LTSC 2021** | 19044.1288（21H2） | 主推薦：功能最全、多語言、新硬體支援好 |
| Enterprise LTSC 2019 | 17763 | 更精簡、資源佔用更低，適合老舊低規格硬體 |
| IoT Enterprise LTSC 2021 | 同 19044 | 被略過（「只有英文版」） |
| LTSC 2016 / 2015 | — | 不推薦（過老） |

### 2.2 2026-09 視角的重大修正

| 版本 | 支持截止（官方 Lifecycle） | 現狀 |
|------|---------------------------|------|
| **Enterprise LTSC 2021** | **2027-01-12** | ⚠️ **只剩約 4 個月**——視頻主推薦已嚴重過時 |
| **IoT Enterprise LTSC 2021** | **2032-01-13** | ✅ Win10 血統裡最長命的版本 |
| Enterprise LTSC 2019 | 2029-01-09 | 中間檔 |

> ⚠️ **時效要點**：視頻以「IoT 版只有英文」為由跳過了它，但 IoT Enterprise LTSC 2021 支持期比 Enterprise 版長 5 年。零度解說自己也在 2025-11-15 發布了後續影片《Windows 10 免費續命到 2032 年！最新 IoT 企業 LTSC》（freedidi.com/21644.html）改推 IoT 版。**2026 年新裝機應直接選 IoT Enterprise LTSC 2021**，安裝流程與本篇完全相同。IoT 版與普通版核心差異是授權通道與預裝（無 Store/更多可裁剪），對日常桌面使用無感。

```
Win10 各 LTSC 支持期時間軸（2026-09 視角）

2025─2026─2027─2028─2029─2030─2031─2032─2033
 │    │NOW │    │    │    │    │    │    │
 │    ┼─ Ent LTSC 2021 ★★★ ─┤(2027-01 谥世)
 │    └── Ent LTSC 2019 ──────────┤(2029-01)
 └(2025-10 消費版已死)
      └─── IoT Ent LTSC 2021 ──────────────────┤(2032-01) ✅ 最長
```

---

## 3. 介質製作：Ventoy

視頻流程（與 [[Windows 11 Ubuntu 22.04 雙系統安裝指南]] 的 Ventoy 章節同源）：

```
下載 ISO ──→ Ventoy 灌隨身碟 ──→ ISO 拖入 ──→ 多系統開機碟
(4.7GB)      (一次安裝)        (免燒錄)      (可疊加多 ISO)
```

1. **ISO 獲取**：微軟官方評估中心（Evaluation Center）或 freedidi 整理頁（4805.html）下載 64 位元中文版，約 4.69GB（視頻實測下載速度 ~90MB/s，一分鐘完成）
2. **隨身碟**：≥8GB，Ventoy 會清空
3. **Ventoy**：官網下載 Windows 安裝包（2025-02 版僅 15MB）→ 解壓 → `Ventoy2Disk.exe` → 選設備 → Install → 格式化確認
4. **寫入**：ISO 直接拖進隨身碟根目錄即完成（拖入約 2 分鐘，8GB 碟裝完剩 2.65GB）
5. **多系統**：再拖其他 ISO 進去即可疊加（Win11/Ubuntu/WinPE 共存），開機時選單切換

> Ventoy 的核心價值：安裝一次，之後換系統只是拷檔案。救援碟 + 裝機碟 + 測試碟三合一。

---

## 4. 安裝流程

### 4.1 BIOS 引導

開機快速敲擊快捷鍵進 Boot Menu：**Del / F11 / F12 / Esc**（依品牌），選隨身碟為第一啟動項，F10 儲存重啟。

### 4.2 全新安裝（無法保留資料）

| 安裝情境 | 操作 |
|----------|------|
| 其他電腦裝 | 隨身碟開機 → 選 Windows 10 Enterprise LTSC → 下一步自動展開 |
| 本機原地升級 | 掛載 ISO → 執行 `setup.exe` → 接受條款 → **「要保留什麼」只能選「無」** |

> ⚠️ 版本架構與家用版不同（Home/Pro → Enterprise LTSC 跨版本），安裝程式明確提示「無法保留你的檔案和應用程式設定」——**系統槽重要資料必須先外置備份**，這是本流程唯一不可逆的步驟。

### 4.3 OOBE 初始化（隱私與帳戶優化）

1. 區域 / 鍵盤佈局（微軟拼音）/ 跳過第二佈局
2. **離線本地帳戶**：網路連線頁選「改為網域加入（Domain Join instead）」→ 設定本地用戶名與密碼——繞過微軟帳戶綁定
3. **隱私設定全部關閉**：位置 / 診斷數據 / 廣告 ID / 手寫識別 / 語音識別等全數 Off——LTSC 本身遙測就少，這一步把殘餘全切掉

安裝全程比消費版快（視頻實測數分鐘完成多次重啟），進桌面即乾淨狀態。

---

## 5. 系統啟用（MAS/HWID）

### 5.1 實際命令（視頻與文字版一致）

安裝完成後系統未啟用（右下角水印）。管理員 PowerShell 執行：

```powershell
irm https://get.activated.win | iex
```

彈出 MAS 菜單後輸入 `1`（HWID 激活），等綠色成功提示。驗證：設定 → 系統 → 啟用狀態顯示「Windows 已使用數位授權啟用」。

### 5.2 這條命令的真實身份

| 項目 | 說明 |
|------|------|
| 工具 | **MAS（Microsoft Activation Scripts）**，github.com/massgravel/microsoft-activation-scripts |
| 性質 | 開源激活腳本，含 HWID / Ohook / TSforge / Online KMS 多種方式 |
| `get.activated.win` | MAS 官方短網域（irm 遠端載入腳本） |
| 選項 1 = HWID | 對 Win10 生成**永久數位許可證**（寫入硬體，重裝自動恢復）——比 KMS 180 天續期更省心 |

> ⚠️ **合規邊界（筆記補充）**：MAS 是社區公認最「乾淨」的激活腳本（開源、無廣告、廣泛使用），但**它不是微軟授權的激活渠道**——HWID 數位許可證的取得方式微軟並未認可。評估/折騰用途普遍，長期生產環境應購買正版授權。與 Server 2025 篇的第三方 KMS 同理：命令語法公開透明 ≠ 授權合規。

### 5.3 與「標準 KMS 三連」的對比

| 方式 | 命令 | 效果 | 依賴 |
|------|------|------|------|
| MAS HWID（本視頻） | `irm get.activated.win \| iex` → 選 1 | 永久數位許可 | 僅需網路，無外部 KMS 伺服器 |
| 手動 KMS 三連 | `slmgr /ipk` + `/skms` + `/ato` | 180 天，需 KMS 伺服器存活 | 第三方公共 KMS 存續 |

---

## 6. 驗證與勘誤記錄

| 視頻/Insights 聲稱 | 驗證結果 | 來源 |
|--------------------|----------|------|
| Windows 10 消費版停止支援後不再有安全修補 | ✅ 屬實（2025-10-14 EOL，視頻「5 個月後」與發布時間吻合） | 微軟 Lifecycle |
| LTSC 剔除預裝冗餘、專注長期安全維護 | ✅ 屬實（LTSC 定位：無 Store、無消費級預裝、僅安全更新） | MS 文檔 |
| LTSC 2021 內部版本號 19044.1288 / 21H2 | ✅ 屬實（視頻錄屏「關於」頁顯示 21H2） | 視頻錄屏 |
| IoT 版只有英文所以選 Enterprise 版 | ⚠️ **當時的取捨，現已過時**：Enterprise LTSC 2021 支持至 2027-01-12（現剩約 4 個月）；IoT Enterprise LTSC 2021 至 2032-01-13。零度自己 2025-11 已改推 IoT 版（freedidi.com/21644.html） | MS Lifecycle / freedidi |
| 激活用「標準批次 KMS 指令或企業金鑰」 | ❌ **勘誤**：實際是 MAS 開源激活腳本（`irm get.activated.win \| iex`，HWID 模式），非微軟官方 KMS/金鑰流程；合規狀態同樣存疑 | GitHub massgravel |
| ISO 約 4.7GB、Ventoy 15MB、8GB 碟裝完剩 2.65GB | ✅ 視頻錄屏實測值，量級合理 | 視頻錄屏 |
| 安裝無法保留檔案與設定、需選「無」 | ✅ 屬實（跨版本通道安裝的標準行為） | 視頻錄屏 |
| Domain Join 建本地帳戶、隱私全關 | ✅ 屬實（LTSC/企業版 OOBE 標準選項） | 視頻錄屏 |
| 字幕轉寫噪音 | 已修正：LTLC→LTSC、30位→32位（x86）、零度口音轉寫 | 本筆記 |

---

## 7. 決策樹：Win10 停支援後的出路

```
Win10 已停止支援（2025-10），你的機器？
│
├── 硬體能跑 Win11（TPM 2.0 + 8 代 CPU+）
│    └── 直接升 Win11（或 LTSC 2025 IoT 等新版）
│
├── 硬體受限 / 不想升級
│    ├── 要留在 Win10 生態
│    │    ├── 長期方案 → ★ IoT Enterprise LTSC 2021（支持到 2032-01）
│    │    ├── 只過渡一年半 → Enterprise LTSC 2021（2027-01 截止，現不建議新裝）
│    │    └── 極老硬體 → LTSC 2019（2029-01）/ Server 2025（見相關筆記）
│    └── 願意換生態 → Linux 輕量桌面 / 雙系統
│
└── 只是捨不得那台舊機器
     └── 評估硬體維修價值後再投資系統遷移
```

| 方案 | 支持至 | 合規激活 | 折騰度 |
|------|--------|----------|--------|
| IoT Ent LTSC 2021 | 2032-01 | ⚠️ MAS 類工具 | 低 |
| Ent LTSC 2021 | 2027-01 | ⚠️ 同上 | 低 |
| Windows Server 2025 DE | 2034-11 | ⚠️ 第三方 KMS | 中（驅動） |
| Win11 正規 | 逐年 | ✅ | 視硬體 |
| Linux 桌面 | 長期 | ✅ 免費 | 視用戶 |

---

## 參考資料

- [Windows 10 LTSC 長期服務版！免費下載、安裝並激活教程 — 零度解說（視頻）](https://www.youtube.com/watch?v=tz3jM6UoDWo)
- [同題文字版（含全部鏈接與激活命令）— 零度博客](https://www.freedidi.com/19474.html)
- [LTSC 2021 下載頁 — 零度博客](https://www.freedidi.com/4805.html)
- [Windows 10 免費續命到 2032 年！IoT 企業 LTSC（後續影片）— 零度博客](https://www.freedidi.com/21644.html)
- [Windows 10 IoT Enterprise LTSC 2021 Lifecycle — Microsoft Learn](https://learn.microsoft.com/en-us/lifecycle/products/windows-10-iot-enterprise-ltsc-2021)
- [Microsoft Activation Scripts — GitHub（massgravel）](https://github.com/massgravel/microsoft-activation-scripts)
- [Ventoy 官網](https://www.ventoy.net)

## 相關筆記

- [[Windows Server 2025 低配電腦輕量系統安裝啟用指南]] — 同頻道姊妹篇：Server 2025 作為低配替代（支持到 2034）
- [[Windows 11 Ubuntu 22.04 雙系統安裝指南]] — Ventoy 製碟詳解與舊機改造 Linux 路線
