---
title: Windows Server 2025 低配電腦輕量系統安裝啟用指南
aliases: [Server 2025 桌面化, 低配電腦救星, Win Server 2025 Desktop Experience]
tags:
  - windows
  - windows-server
  - low-spec
  - system-install
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=9jtupKytvvI"
  - "https://www.freedidi.com/19562.html"
author: 零度解說（頻道）
created: 2026-09-05
updated: 2026-09-05
description: 用 Windows Server 2025 Desktop Experience 替代 Win11 救活低配舊電腦：官方映像下載、Rufus 介質、Desktop Experience 關鍵勾選、評估版轉正與啟用全流程
level: intermediate
stars: 4
note: 本視頻字幕為創作者禁用（Transcripts disabled），基於視頻描述 + freedidi 文字版 + 微軟官方文檔交叉驗證整理。KMS 啟用部分涉及第三方伺服器，已標注合規邊界。
---

# Windows Server 2025 低配電腦輕量系統安裝啟用指南

> 核心思路：Windows Server 2025（LTSC）Desktop Experience 版 = 無廣告、無預裝、無 TPM 強制門檻的「官方純淨版 Windows」。對於跑不動 Win11 又想留在 Windows 生態的舊機器，它比各種第三方精簡版（Tiny10/11）更合規、更穩定。
>
> 視頻：零度解說《低配電腦起飛！Windows Server 2025 終於發布》（2025-06-08，28.4 萬播放，112 萬訂閱頻道）；文字版：freedidi.com/19562.html

## 目錄

- [1. 為什麼拿 Server 當桌面系統](#1-為什麼拿-server-當桌面系統)
- [2. 輕量聲稱 vs 官方硬體需求](#2-輕量聲稱-vs-官方硬體需求)
- [3. 下載官方映像與安裝介質製作](#3-下載官方映像與安裝介質製作)
- [4. 安裝過程的三個關鍵設定](#4-安裝過程的三個關鍵設定)
- [5. 評估版轉正式版與啟用](#5-評估版轉正式版與啟用)
- [6. 潛在風險與實務限制](#6-潛在風險與實務限制)
- [7. 驗證與勘誤記錄](#7-驗證與勘誤記錄)
- [8. 時效性與生命週期](#8-時效性與生命週期)
- [9. 決策樹：低配機選什麼系統](#9-決策樹低配機選什麼系統)
- [參考資料](#參考資料)

---

## 1. 為什麼拿 Server 當桌面系統

Server 版與消費版 Windows 同核心（NT，build 26100 系），但砍掉了消費級的商業化包袱：

| 維度 | Windows 11 桌面版 | Server 2025 Desktop Experience |
|------|-------------------|-------------------------------|
| 預裝廣告/推薦內容 | 開始選單推薦、鎖屏推廣、通知彈窗 | 無 |
| 預裝 App（Candy Crush 等） | 一批 | 無 |
| TPM 2.0 / Secure Boot | 安裝強制 | 不強制（僅 BitLocker/Secured-core 功能需要） |
| CPU 支援清單 | 8 代 Intel 以上白名單 | 無白名單，只看指令集 |
| 背景服務數量 | 消費級全家桶 | 機房取向，預設更精簡 |
| 更新通道 | 半年度 + 功能推送 | LTSC，僅安全更新，10 年支援 |
| 授權成本 | 零售密鑰 | 評估版 180 天免費；正式授權按 CPU 核數計費（昂貴） |

極客群體長期用 Server 當桌面的原因：純淨、穩定、不打擾。Server 2025 的 Desktop Experience 已是完整桌面環境（含 Microsoft Edge、新式設定），不再是老版本那種「殘缺桌面」。

```
同一 NT 核心，兩種包裝

  build 26100 (2024-11 GA)
      │
      ├── Windows 11 24H2 ── 消費級外殼（廣告/推薦/商店）+ TPM 門檻
      │
      └── Windows Server 2025 LTSC
             ├── Server Core（純命令列，本篇不用）
             └── Desktop Experience（完整桌面 ✅ 本篇主角）
```

---

## 2. 輕量聲稱 vs 官方硬體需求

視頻聲稱「4GB 記憶體舊筆電流暢運行」。對照微軟官方硬體需求頁（2025-07 修訂版）：

| 項目 | Server 2025 官方最低 | Windows 11 官方最低 | 備註 |
|------|---------------------|---------------------|------|
| CPU | 1.4 GHz 64-bit（支援 x64/NX/DEP/SSE4.2/POPCNT/SLAT） | 相容清單內 CPU（Intel 8 代+） | Server 無型號白名單 ✅ |
| RAM（Desktop Experience） | **2 GB 最低，4 GB 建議** | 4 GB | 視頻「4GB 流暢」與官方建議吻合 |
| RAM（Server Core） | 2 GB | — | Core 比 Desktop 約小 4GB 磁碟佔用 |
| 磁碟 | 32 GB 系統分割區 | 64 GB | |
| TPM | **安裝不需要**（BitLocker/Secured-core 才要 TPM 2.0） | 強制 TPM 2.0 | 舊機免繞門檻的關鍵 ✅ |
| 網卡 | PCIe 介面、建議 1 Gbps | — | 消費級 Realtek 一般有內建驅動 |

結論：視頻的「低配可跑」聲稱與官方門檻一致，且 Server 不檢查 CPU 型號清單、不安裝時強制 TPM——這是它對老機器友善的官方依據，不是玄學。

> ⚠️ 注意一個反直覺點：官方文檔明確警告，在 1 核心 + 1GB RAM 的虛擬機上安裝會直接失敗（安裝器需要 ≥1.5GB 可用記憶體做快取）。物理機 2GB 是最低，實用建議 4GB 起步。

---

## 3. 下載官方映像與安裝介質製作

### 3.1 映像獲取（官方評估中心）

- 簡體中文：`microsoft.com/zh-cn/evalcenter/download-windows-server-2025`
- 繁體中文：`microsoft.com/zh-tw/evalcenter/download-windows-server-2025`
- ISO 容量約 5.79 GB（視頻實測值），含 VHD 與 ISO 兩種格式可選

> 評估中心下載的是 **Evaluation（評估）版**：功能完整，180 天試用期，桌面右下角有評估版水印。這是唯一的官方免費路徑（第 5 節見轉換與啟用的合規邊界）。

### 3.2 Rufus 製作啟動隨身碟

Rufus 4.x 寫盤時會彈出定制對話框，兩個選項按需勾選：

| Rufus 選項 | 建議 | 說明 |
|------------|------|------|
| 移除 4GB RAM / 2GB RAM 與 Secure Boot 要求 | 視情況 | Server 本身門檻已低，一般無需 |
| 停用遙測數據收集 | ✅ 推薦 | 安裝映像內即關閉遙測，比裝完再關乾淨 |
| 停用 BitLocker 自動裝置加密 | ✅ 推薦 | 老機器無 TPM 或不想被加密鎖盤時必選 |
| 建立本地帳戶 | 可選 | Server 用 Administrator 本地帳戶，本來就不強制微軟帳戶 |

```
介質製作 → 安裝 → 轉換 → 啟用 全流程

下載 ISO (5.79GB)          官方 evalcenter（180 天評估版）
      │
      ▼
Rufus 4.x 寫入 ≥8GB 隨身碟  ── 勾選：停用遙測 + 停用 BitLocker
      │
      ▼
BIOS (F11/F12/Esc) USB 開機 ── 選帶 UEFI 前綴的隨身碟
      │
      ▼
安裝：勾選 Desktop Experience ⚠️ 最關鍵的一步
      │
      ▼
CMD(管理員) DISM 轉正式版    ── 水印消失
      │
      ▼
CMD(管理員) slmgr 三連       ── KMS 啟用（注意合規邊界，見 5.3）
```

---

## 4. 安裝過程的三個關鍵設定

### 4.1 版本選擇 + Desktop Experience 勾選（最關鍵）

安裝中途選擇作業系統版本的畫面，每個版本有兩個條目：

| 選項條目 | 結果 |
|----------|------|
| Standard（Desktop Experience） | 標準版 + 完整圖形桌面 ✅ 一般 PC 用這個 |
| Standard | 純命令列 Server Core（黑窗，無桌面）❌ 誤選重灌 |
| Datacenter（Desktop Experience） | 資料中心版 + 桌面（多熱修補/軟體定義基礎設施等功能） |
| Datacenter | Server Core ❌ |

> ⚠️ **沒有後悔藥**：Core 與 Desktop Experience 是安裝時一次性選擇，裝完互換只能重灌（或 DISM 轉版本，但轉換同樣繼承當時的安裝類型）。影片 03:57 處特別強調此點。
>
> Standard vs Datacenter 對桌面使用者差異不大（Datacenter 多出的 Shielded VM、SDN/SDS 桌面機用不到）。實務上按你手上的金鑰選——影片示範用 Datacenter GVLK，故選 Datacenter (Desktop Experience)。

### 4.2 Administrator 密碼複雜度

首次進系統要求設定 Administrator 密碼，Server 預設密碼原則強制：大小寫字母 + 數字 + 特殊符號。忘記複雜度要求會反覆報「密碼不符合複雜性需求」。

```
密碼格式範例（滿足 Server 預設原則）：
  ✅ P@ssw0rd-2025!   （大小寫+數字+符號）
  ❌ 123456           （純數字）
  ❌ password         （無大寫/數字/符號）
```

裝完若嫌麻煩，可在本地群組原則放寬：`gpedit.msc` → 電腦設定 → Windows 設定 → 安全性設定 → 帳戶原則 → 密碼原則 → 「密碼必須符合複雜性需求」= 已停用。（桌面機自用可接受；對外暴露的機器別這麼做。）

### 4.3 BIOS 開機順序

開機快捷鍵進 Boot Menu：F11（多數主板/微星）、F12（Lenovo/Dell）、Esc/F9（華碩/HP）。選帶 `UEFI:` 前綴的隨身碟條目；若機器是老 Legacy BIOS 則選不帶前綴的（Server 2025 仍支援 Legacy 開機安裝，這點比 Win11 寬鬆——Win11 官方僅支援 UEFI）。

---

## 5. 評估版轉正式版與啟用

### 5.1 為什麼要轉換

評估版功能完整但有兩個限制：桌面水印 + 180 天後每小時重啟（評估期結束的官方行為，非破解）。影片 08:42 示範用 DISM 把評估版轉為正式版渠道。

### 5.2 DISM 轉換命令（管理員 CMD）

```
DISM /online /Set-Edition:ServerDatacenter /ProductKey:D764K-2NDRG-47T6Q-P8T8W-YP6DF /AcceptEula
```

- `ServerDatacenter` 對應 Datacenter 版；轉 Standard 用 `ServerStandard` + Standard 金鑰
- `D764K-2NDRG-47T6Q-P8T8W-YP6DF` 是**微軟官方公開的 GVLK**（Generic Volume License Key，官方 KMS 金鑰表 Server 2025 Datacenter 條目原樣可查），不是盜版密鑰
- 執行完重啟，評估水印消失（影片 09:40）

對照官方 GVLK 表（來源見參考資料）：

| 版本 | 官方 GVLK（KMS 客戶端金鑰） |
|------|------------------------------|
| Server 2025 Standard | `TVRH6-WHNXV-R9WG3-9XRFY-MY832` |
| Server 2025 Datacenter | `D764K-2NDRG-47T6Q-P8T8W-YP6DF` |
| Server 2025 Datacenter: Azure Edition | `XGN3F-F394H-FD2MY-PP6FD-8MCRC` |

### 5.3 KMS 啟用三連（管理員 CMD）

```
slmgr -ipk D764K-2NDRG-47T6Q-P8T8W-YP6DF   # 安裝 GVLK
slmgr -skms kms.0t.net.cn                   # 指向 KMS 伺服器
slmgr -ato                                  # 執行啟用
```

> ⚠️ **合規邊界（筆記補充，影片未明說）**：
> - GVLK 金鑰本身是微軟官方公開的，安裝它完全合法
> - 但 KMS 啟用架構要求**你所在組織的網路內有合法 KMS 主機**（需向微軟購買批量授權取得 KMS 主機金鑰）
> - `kms.0t.net.cn` 是**第三方公共 KMS 伺服器**，不屬於微軟授權部署。對它啟用 = 使用非授權啟用渠道，版權狀態存疑，適合評估/折騰用途
> - 乾淨的官方路徑：評估版 180 天內決定去留；長期使用應購買零售/批量授權，或改用免費的 Linux 桌面
>
> KMS 啟用有效期最長 180 天，到期前系統會自動向已設定的 KMS 伺服器續期——第三方伺服器一旦關停，系統會進入通知模式（水印回歸）。

### 5.4 啟用狀態檢查

```cmd
slmgr /xpr        # 查詢啟用到期時間（KMS 顯示 ~180 天後）
slmgr /dli        # 詳細授權資訊（渠道、部分產品金鑰）
DISM /online /Get-CurrentEdition   # 確認當前版本已無 Eval 字樣
```

---

## 6. 潛在風險與實務限制

影片結尾坦白了 Server 當桌面的代價，補充整理：

### 6.1 驅動程式缺失（最大風險）

Server 只對企業級硬體保證驅動。消費級硬體常見問題：

| 硬體 | 風險 | 對策 |
|------|------|------|
| 消費級主板晶片組 | 通常可複用 Win10/11 驅動 | 手動安裝 Intel INF/AMD Chipset |
| Wi-Fi 網卡（Intel AX 系列） | 常無內建驅動，裝完斷網 | 提前下載驅動到隨身碟；或有線 + 手機 USB 共享網路應急 |
| 舊獨顯（AMD/NVIDIA 老卡） | 無 Server 認證驅動，可能藍屏（影片提到 Clock Watchdog Timeout） | 安裝前先在原系統驗證；必要時用工作站驅動（Quadro/Tesla 系列有 Server 簽名） |
| Realtek 有線網卡 | 內建驅動通常覆蓋 | 基本無憂 |

> **影片行動建議（正確且重要）**：格式化主硬碟之前，先確認網卡與主板有可用的 Server/Win10/Win11 驅動。否則裝完斷網，驅動都下載不了。

### 6.2 桌面體驗的隱性差異

```
Server 桌面化後需要手動處理的項目

□ 音訊：Windows Audio 服務預設停用 → services.msc 啟用並設自動
□ 關閉 IE 增強的安全設定（IE ESC，彈窗攔截狂魔）→ 伺服器管理器本地伺服器頁
□ 登入時自動開啟 Server Manager → 伺服器管理器 → 管理伺服器器屬性 → 不顯示
□ 遊戲反作弊（EAC/BattlEye）：部分遊戲拒絕在 Server 系統運行
□ 消費級安全軟體：部分明確不支援 Server 版
□ Microsoft Store：Server 預設無商店，需另想辦法（wsreset -i 或 LTSC 部署包）
```

### 6.3 適用人群重定位

- ✅ 舊筆電/小主機當輕量辦公 + 下載機 + 家庭伺服器（原本就是 Server 的本職）
- ✅ 開發者要一台乾淨的 Windows 測試機
- ❌ 遊戲主力機（驅動 + 反作弊雙重不確定性）
- ❌ 小白日用機（每次系統元件行為差異都是排障成本）

---

## 7. 驗證與勘誤記錄

| 視頻/文章聲稱 | 驗證結果 | 來源 |
|---------------|----------|------|
| 對 CPU/記憶體/磁碟要求比 Win11 更低 | ✅ 官方：DE 版 2GB 最低（4GB 建議）/ 32GB 磁碟，低於 Win11 的 4GB/64GB | MS Learn 硬體需求頁 |
| 避開 Win11 對舊 CPU 與 TPM 的嚴格限制 | ✅ 官方：安裝不強制 TPM/Secure Boot，無 CPU 型號白名單（僅指令集要求）；TPM 2.0 僅 BitLocker/Secured-core 功能需要 | MS Learn 硬體需求頁 |
| 無預裝廣告、彈窗、推送 | ✅ 與 LTSC 定位一致（官方無「無廣告」承諾，但消費級商業化元件確實不隨 Server 發佈） | MS 發行通道文檔 + 社區共識 |
| 4GB 記憶體舊筆電流暢運行 | ✅ 可信（官方 DE 建議值就是 4GB），「流暢」屬體感描述 | MS Learn 硬體需求頁 |
| ISO 約 5.79GB | ⚠️ 未獨立驗證（量級合理，官方評估中心 ISO 歷來 5-6GB 檔） | 視頻 01:16 |
| 務必勾選 Desktop Experience，否則只有命令列 | ✅ 正確，安裝時一次性選擇 | MS Learn 安裝選項文檔 |
| DISM 轉換 + D764K 金鑰 | ✅ D764K-2NDRG-47T6Q-P8T8W-YP6DF 與微軟官方 GVLK 表 Server 2025 Datacenter 條目完全一致 | MS Learn KMS 金鑰頁 |
| KMS 三連啟用 | ⚠️ 命令語法正確，但 `kms.0t.net.cn` 是第三方公共 KMS，非微軟授權渠道（見 5.3 合規邊界） | MS Learn KMS 架構文檔 |
| 評估版 180 天 | ✅ 官方評估中心標準試用期 | MS evalcenter |
| 標題「終於發布」 | ⚠️ 時間滯後：GA 是 2024-11-01，視頻 2025-06-08 發佈（晚 7 個月），非新鮮發布 | MS Learn 發行資訊頁 |

---

## 8. 時效性與生命週期

| 項目 | 值 |
|------|-----|
| GA（正式發布） | 2024-11-01 |
| 通道 | LTSC（長期服務通道） |
| 主流支援截止 | 2029-11-13 |
| 延伸支援截止 | 2034-11-14 |
| OS build | 26100（與 Win11 24H2 同源） |
| 評估版試用 | 180 天 |

LTSC 十年支援是它對「穩定純淨桌面」人群的核心賣點——裝一次，2034 年前只有安全更新，無功能推送、無行銷彈窗。

---

## 9. 決策樹：低配機選什麼系統

```
舊/低配電腦（≤8GB RAM，無 TPM 2.0 或老 CPU）
│
├── 必須留在 Windows 生態（特定軟體/遊戲/周邊）？
│    ├── 是 → 硬碟夠且能接受折騰？
│    │         ├── 是 → Windows Server 2025 DE（本篇：官方純淨 + 無門檻）
│    │         ├── 要更省資源 → Windows 10 IoT/LTSC 授權路徑（2025 年仍在支援期）
│    │         └── 不折騰 → 繼續 Win10/Win11 舊版撐到支援截止
│    └── 否 → 對 Linux 接受度？
│              ├── 高 → Linux 輕量桌面（Xubuntu/LXQt/Mint）——真正零成本
│              └── 低 → ChromeOS Flex（僅瀏覽器場景）
│
└── 機器其實還行（16GB+/SSD）？
     └── 直接 Win11 正規版；「卡」多半是機械碟/滿碟，換 SSD 解決
```

| 方案 | 合規性 | 資源佔用 | 折騰度 | 支援到 |
|------|--------|----------|--------|--------|
| Server 2025 DE（評估版） | ✅ 官方免費 180 天 | 低 | 中（驅動/服務） | 評估期 |
| Server 2025 DE（第三方 KMS） | ⚠️ 存疑 | 低 | 中 | KMS 存續期 |
| Win11 正規 | ✅ | 中 | 低 | 逐年版本 |
| Tiny10/11 精簡版 | ❌ 非官方改裝 | 極低 | 中（更新斷裂） | 無保證 |
| Linux 桌面 | ✅ 免費開源 | 極低 | 視用戶 | 長期 |

---

## 參考資料

- [低配电脑起飞！Windows Server 2025 终于发布 — 零度解说（視頻）](https://www.youtube.com/watch?v=9jtupKytvvI)
- [Windows Server 2025 正式发布！免费下载+激活 — 零度博客（文字版）](https://www.freedidi.com/19562.html)
- [Hardware requirements for Windows Server — Microsoft Learn](https://learn.microsoft.com/en-us/windows-server/get-started/hardware-requirements)
- [KMS client activation keys（官方 GVLK 表）— Microsoft Learn](https://learn.microsoft.com/en-us/windows-server/get-started/kms-client-activation-keys)
- [Windows Server release information — Microsoft Learn](https://learn.microsoft.com/en-us/windows-server/get-started/windows-server-release-info)
- [Windows Server 2025 下載 — 微軟評估中心](https://www.microsoft.com/zh-cn/evalcenter/download-windows-server-2025)

## 相關筆記

- [[Windows 11 Ubuntu 22.04 雙系統安裝指南]] — 同為低配/舊機改造路線（Linux 方向）
- [[install-multiple-Ubuntu-WSL2]] — 不動磁碟的輕量替代方案
- [[wsl]] — WSL 基礎
