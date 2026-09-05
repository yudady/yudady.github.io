---
title: Windows 11 24H2 LTSC 安裝啟用指南
aliases: [Win11 LTSC 2024, IoT Enterprise LTSC, 24H2 长期服务版]
tags:
  - windows
  - windows-11
  - ltsc
  - system-install
  - low-spec
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=8i_r8ML2GlY"
  - "https://massgrave.dev/hwid"
  - "https://learn.microsoft.com/en-us/lifecycle/products/windows-11-iot-enterprise-ltsc-2024"
author: 零度解說（頻道）
created: 2026-09-05
updated: 2026-09-05
description: Windows 11 24H2 IoT Enterprise LTSC 2024（支持至 2034）的 Rufus 免門檻安裝、English (World) 最小化安裝、MAS 啟用與中文化全流程
level: intermediate
stars: 4
note: Tier 0 無字幕，web_extract 直抓 YouTube 意外返回全量簡體字幕。視頻稱「OEM 版」實為 IoT Enterprise LTSC 2024（10 年支持至 2034-10，MAS HWID 唯一可激活的 LTSC 通道）——歸屬經 massgrave 官方文檔 + MS Lifecycle 確認。激活為 MAS 開源腳本（非微軟授權渠道，合規邊界已標注）。
---

# Windows 11 24H2 LTSC 安裝啟用指南

> Win11 血統裡最乾淨的長期服務版：**IoT Enterprise LTSC 2024**（24H2，build 26100）——無預裝、無廣告、10 年支持到 2034-10。它也是唯一能被 MAS 以 HWID 永久數位授權激活的 Win11 LTSC 通道（普通 Enterprise LTSC 依賴企業批量授權，個人無法正規激活）。
>
> 視頻：零度解說《Windows 11 最強穩定版！低配電腦都能流暢運行！一鍵免費激活（24H2）LTSC》

## 目錄

- [1. 版本定位：為什麼是 IoT LTSC 2024](#1-版本定位為什麼是-iot-ltsc-2024)
- [2. 事前準備與 Rufus 啟動盤](#2-事前準備與-rufus-啟動盤)
- [3. 安裝流程與 English (World) 技巧](#3-安裝流程與-english-world-技巧)
- [4. 一鍵啟用與中文化](#4-一鍵啟用與中文化)
- [5. 系統微調](#5-系統微調)
- [6. 驗證與勘誤記錄](#6-驗證與勘誤記錄)
- [7. 決策樹：零度三部曲怎麼選](#7-決策樹零度三部曲怎麼選)
- [參考資料](#參考資料)

---

## 1. 版本定位：為什麼是 IoT LTSC 2024

### 1.1 Win11 LTSC 2024 版本生態（官方 Lifecycle）

| 版本 | 支持截止 | 激活通道（個人可用性） |
|------|----------|------------------------|
| **IoT Enterprise LTSC 2024** | **2034-10-10（10 年）** | ✅ MAS HWID 永久數位授權（IotEnterpriseS 金鑰） |
| Enterprise LTSC 2024 | 2029-10-09 | ❌ 依賴企業批量授權（KMS/MAK），個人無正規途徑 |

> 視頻口播的「OEM 版是目前唯一一個安裝好以後可以免費激活的版本」即指 IoT 版——massgrave 官方文檔確認 HWID 激活對 LTSC 2024 使用 `IotEnterpriseS (LTSC) 2024` 金鑰；Reddit/MAS 社區確認非 IoT 的 Win11 LTSC 不支援 HWID。

### 1.2 與其他長期支持版對比

| 方案 | 內核 | 支持至 | 適合 |
|------|------|--------|------|
| **Win11 IoT LTSC 2024（本篇）** | 24H2 (26100) | 2034-10 | 要新系統相容性 + 10 年安穩 |
| Win10 IoT Ent LTSC 2021 | 21H2 (19044) | 2032-01 | 老機器留在 Win10 生態 |
| Win Server 2025 DE | 26100 | 2034-11 | 極限輕量 + 無TPM門檻（驅動風險） |

硬體門檻：視頻稱 2GB RAM 也可流暢運行（IoT 版官方最低即低於消費版；Rufus 還可解除安裝檢查，見下）。

---

## 2. 事前準備與 Rufus 啟動盤

### 2.1 映像獲取

- 官方 24H2 LTSC ISO 約 **4.2GB**（視頻從其博客整理頁下載；微軟官方 Evaluation Center 也有，但 IoT 版走 OEM 通道）
- 隨身碟 ≥8GB（製作會清空）

### 2.2 Rufus 燒錄配置（核心步驟）

Rufus 4.6：選設備 → 引導類型選 ISO → 選檔 → 開始 → 彈出「自訂 Windows 安裝」對話框：

| 選項 | 作用 | 建議 |
|------|------|------|
| 移除 4GB RAM / Secure Boot / TPM 2.0 要求 | 解除 Win11 硬體門檻 | ✅ 老機必勾 |
| 移除微軟聯網帳號要求 | OOBE 不強制線上帳號 | ✅ 勾 |
| 建立本地帳號 | 預置離線用戶 | 可選 |
| 禁止數據收集 | 減少遙測 | ✅ 勾 |
| 禁用 BitLocker 自動加密 | 保住磁碟性能（老 SSD 尤其） | ✅ 勾 |

寫入數分鐘完成（視隨身碟速度），之後即可給任何一台電腦安裝。

```
Rufus 五選項與後果對照

移除硬體檢查 ──→ 2GB RAM / 無 TPM 老機也能裝 24H2
移除聯網帳號 ──→ OOBE 直接進本地帳號流程
禁數據收集   ──→ 遙測預設關
禁 BitLocker ──→ 系統槽不被自動加密拖慢
      ↓
  「任何一台電腦」的可攜安裝碟
```

---

## 3. 安裝流程與 English (World) 技巧

### 3.1 引導開機

快捷鍵進 Boot Menu（Del / F10 / F12 / Esc），選隨身碟啟動。安裝界面是英文沒關係，裝完再中文化。

### 3.2 English (World) 最小化安裝（關鍵技巧）

安裝初期的語言與格式設定頁：

- 安裝語言：English (United States)
- **時間與貨幣格式：English (World)** ⚠️ 核心技巧

選 World 而非具體國家，系統會以**最小安裝模式**部署——進一步剔除預裝組件（這是社區流傳的 Geo 技巧，對 OEM/IoT 映像效果疊加）。

### 3.3 安裝類型與帳號

| 環節 | 選擇 | 說明 |
|------|------|------|
| 安裝類型 | Custom（第一項） | 最乾淨：格式化系統盤全新安裝（**資料務必先備份**） |
| 保留資料 | 若不想清盤選 Repair | 但 LTSC 精神就是乾淨安裝 |
| 帳號 | 登錄選項 → 「改為網域加入（Domain Join instead）」 | 建立純本地離線帳號，跳過微軟帳號 |
| 隱私設定 | 全部關閉 | 診斷/位置/廣告 ID 全 Off |

安裝中重啟數次，切勿斷電；中途報錯直接 Skip 跳過。

---

## 4. 一鍵啟用與中文化

### 4.1 MAS 啟用（管理員 PowerShell）

```powershell
irm https://get.activated.win | iex
```

視頻演示在 MAS 菜單**依序輸入 `3` 與 `1`**（本視頻錄製時的菜單路徑），等綠色成功標記。驗證：Settings → System → 顯示已激活。

> ⚠️ **菜單序號會隨 MAS 版本變動**（當前版本 [1]=HWID、[2]=Ohook、[3]=TSforge……）。核心結論不變：**IoT LTSC 2024 支援 HWID 永久數位授權通道**（massgrave.dev/hwid 官方文檔：LTSC 2024 使用 IotEnterpriseS 金鑰）。操作時按當前 MAS 菜單的說明文字選對應激活方式即可，不必死記視頻裡的數字。
>
> ⚠️ 合規邊界：MAS 是開源激活腳本，非微軟授權渠道。與前兩篇（Server 2025 KMS、Win10 LTSC）同理——命令透明 ≠ 授權合規。

### 4.2 介面中文化

裝完預設英文。Settings → Time & Language → Language & region：

1. Add a language → 搜尋 Chinese (Traditional/Simplified)
2. 勾選語言包全部組件（含顯示語言）→ Install/Download
3. 把中文 **Move up** 到語言列表頂部
4. 重啟 → 介面全中文

---

## 5. 系統微調

### 5.1 移除「Learn about this picture」頑固圖示

桌面右下角的 Windows 聚焦圖示**無法右鍵刪除、無法拖入回收站**。解法：

```
右鍵桌面 → Personalize（個性化）
  → Background（背景）
  → 把「Windows 聚焦（Spotlight）」改為 Picture（圖片）或 Solid color（純色）
  → 圖示自動消失
```

原理：該圖示是 Spotlight 圖片聚焦功能的附屬物，關閉聚焦即除根。

### 5.2 雙軌安裝策略

| 硬體狀況 | 安裝方式 |
|----------|----------|
| 符合 Win11 規範（TPM 2.0 + 8 代 CPU+） | 直接掛載 ISO 雙擊 `setup.exe` 原地升級 |
| 不達標（無 TPM / 老 CPU / 低 RAM） | 必須走 Rufus 隨身碟重灌繞過檢測（第 2 節五選項） |

---

## 6. 驗證與勘誤記錄

| 視頻/Insights 聲稱 | 驗證結果 | 來源 |
|--------------------|----------|------|
| 「24H2 LTSC OEM 版」 | ✅ 存在，實為 **IoT Enterprise LTSC 2024**（OEM 通道發行，24H2/build 26100） | MS Lifecycle / MS Learn |
| 該版本「是目前唯一可免費激活的版本」 | ✅ 屬實（準確說：唯一支援 MAS HWID 永久激活的 Win11 LTSC——massgrave 文檔確認 HWID 用 IotEnterpriseS (LTSC) 2024 金鑰；非 IoT 版依賴企業批量授權） | massgrave.dev/hwid / Reddit MAS 社區 |
| LTSC 10 年長期支持 | ✅ IoT 版至 2034-10-10；注意普通 Enterprise LTSC 2024 只到 2029-10-09 | MS Lifecycle |
| 2GB RAM 也能流暢 | ⚠️ IoT 版門檻確實低於消費版，且 Rufus 可解除安裝檢查；「流暢」屬口播體感，無實測數據 | 視頻 |
| ISO 約 4.2GB、Rufus 4.6 | ✅ 視頻錄屏實測值 | 視頻錄屏 |
| Rufus 五選項（記憶體/TPM/SecureBoot/帳號/BitLocker/遙測） | ✅ Rufus 標準 Windows 11 定制清單 | 視頻錄屏 + Rufus 通識 |
| English (World) 觸發最小化安裝 | ✅ 社區公認 Geo 技巧（微軟非官方文檔行為，對預裝組件做減法） | 社區共識 |
| Domain Join 建本地帳號 | ✅ 標準繞過（與 Win10 LTSC 篇同） | 視頻錄屏 |
| MAS 按「3 再按 1」 | ⚠️ 菜單序號隨 MAS 版本迭代變化，不可硬記；IoT LTSC 2024 走 HWID 通道的結論不變 | massgrave.dev |
| Learn about this picture 圖示解法 | ✅ 屬實（Spotlight 聚焦附屬圖示，切背景類型即除根） | 視頻錄屏 |
| Insights 稱「線上數位授權」 | ⚠️ 部分不準：HWID 激活寫入的是硬體數位許可證（聯網激活一次後永久，重裝自動恢復），非「線上授權」持續依賴 | massgrave.dev/hwid |

---

## 7. 決策樹：零度三部曲怎麼選

同一頻道三期影片構成「長期支持版」方案矩陣（2026-09 視角）：

```
要長期穩定、無廣告的 Windows？
│
├── 機器較新 / 要 Win11 相容性
│    └── ★ Win11 IoT LTSC 2024（本篇）— 支持到 2034-10，MAS HWID 可激活
│
├── 機器較老 / 習慣 Win10
│    └── Win10 IoT Ent LTSC 2021 — 支持到 2032-01（見前篇）
│         ⚠️ 別再新裝 Ent LTSC 2021（2027-01 就截止了）
│
├── 極限輕量 / 無 TPM 且折騰得起驅動
│    └── Windows Server 2025 DE — 支持到 2034-11（見姊妹篇）
│
└── 硬體達標、不怕更新
     └── 正規 Win11 消費版
```

| 方案 | 支持至 | 激活合規 | 折騰度 |
|------|--------|----------|--------|
| Win11 IoT LTSC 2024 | 2034-10 | ⚠️ MAS HWID | 低 |
| Win10 IoT LTSC 2021 | 2032-01 | ⚠️ MAS HWID | 低 |
| Server 2025 DE | 2034-11 | ⚠️ 第三方 KMS | 中（驅動） |
| Win11 正規 | 逐年 | ✅ | 視硬體 |

---

## 參考資料

- [Windows 11 最強穩定版（24H2）LTSC — 零度解說（視頻）](https://www.youtube.com/watch?v=8i_r8ML2GlY)
- [HWID Activation — MAS 官方文檔（massgrave.dev）](https://massgrave.dev/hwid)
- [Windows 11 IoT Enterprise LTSC 2024 Lifecycle — Microsoft Learn](https://learn.microsoft.com/en-us/lifecycle/products/windows-11-iot-enterprise-ltsc-2024)
- [Windows 11 Enterprise LTSC 2024 Lifecycle — Microsoft Learn](https://learn.microsoft.com/en-us/lifecycle/products/windows-11-enterprise-ltsc-2024)
- [MAS：LTSC 2024 激活支援討論 — GitHub Discussions](https://github.com/massgravel/Microsoft-Activation-Scripts/discussions/458)
- [Rufus 官網](https://rufus.ie)

## 相關筆記

- [[Windows 10 LTSC 長期服務版安裝啟用指南]] — 前篇：Win10 IoT LTSC 2021（支持到 2032）
- [[Windows Server 2025 低配電腦輕量系統安裝啟用指南]] — 姊妹篇：Server 2025 DE 路線
- [[Windows 11 Ubuntu 22.04 雙系統安裝指南]] — Rufus/Ventoy 介質製作詳解與 Linux 路線
