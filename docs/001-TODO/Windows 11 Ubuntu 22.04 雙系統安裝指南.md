---
title: Windows 11 Ubuntu 22.04 雙系統安裝指南
aliases: [雙系統安裝, Win11 Ubuntu Dual Boot, UEFI GPT 雙系統]
tags:
  - linux
  - ubuntu
  - dual-boot
  - windows
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=szlNPdAt3Kk"
author: Robotic Artisan（頻道）
created: 2026-09-05
updated: 2026-09-05
description: 現代 UEFI + GPT 架構下 Windows 11 無損安裝 Ubuntu 22.04 雙系統的完整實踐路徑：環境校驗、Ventoy 介質、分區部署到 GParted 動態擴容
level: intermediate
stars: 4
note: 無字幕（創作者已禁用），基於視頻描述章節 + Content Insights + 外部官方資料交叉驗證整理
---

# Windows 11 Ubuntu 22.04 雙系統安裝指南

> 核心思想：雙系統共存成敗的 90% 在開工之前就決定了——**引導架構統一（UEFI + GPT）**與**磁區隔離防護**。只要引導表與檔案系統規範一致，就能規避開機黑屏、引導遺失、磁碟破壞三大經典故障。
>
> 視頻：Robotic Artisan《Windows 11 安装 Ubuntu 22.04 双系统步骤和注意事项》（2024-07-03，67 分鐘，19 章）

## 目錄

- [1. 為什麼是 UEFI + GPT](#1-為什麼是-uefi--gpt)
- [2. 前期環境檢查與相容性校驗](#2-前期環境檢查與相容性校驗)
- [3. 安裝前必做的五項防護](#3-安裝前必做的五項防護)
- [4. 安裝介質製作（Ventoy GPT）](#4-安裝介質製作ventoy-gpt)
- [5. 磁碟空間壓縮](#5-磁碟空間壓縮)
- [6. Live USB 開機與顯卡引導調校](#6-live-usb-開機與顯卡引導調校)
- [7. Ubuntu 手動分區與引導器部署](#7-ubuntu-手動分區與引導器部署)
- [8. 裝機後首日配置](#8-裝機後首日配置)
- [9. 後期磁區動態調整與無損擴容](#9-後期磁區動態調整與無損擴容)
- [10. 驗證與勘誤記錄](#10-驗證與勘誤記錄)
- [11. 時效性提醒（2026 視角）](#11-時效性提醒2026-視角)
- [12. 決策樹：要不要上雙系統](#12-決策樹要不要上雙系統)
- [參考資料](#參考資料)

---

## 1. 為什麼是 UEFI + GPT

雙系統共存的前提是兩者採用**相同的引導模式**。Windows 11 強制 UEFI + GPT 安裝，因此 Ubuntu 也必須走 UEFI 引導，共用或獨立管理 EFI 系統分區（ESP, EFI System Partition）。

傳統 MBR + Legacy BIOS 架構在雙系統安裝的最後一步常發生引導器寫入失敗與互相覆蓋衝突（兩個系統搶同一個 512 位元組的 MBR 引導碼區域）；GPT 架構下每個系統在 ESP 內擁有獨立目錄（`EFI/Microsoft`、`EFI/ubuntu`），由 NVRAM 開機項選擇，互不干擾。

| 維度 | Legacy BIOS + MBR | UEFI + GPT（本篇） |
|------|-------------------|---------------------|
| 引導碼存放 | MBR 前 512 位元組，兩系統互相覆蓋 | ESP 分區內獨立目錄，互不干擾 |
| Windows 11 支援 | 不支援（Win11 強制 UEFI） | 原生支援 |
| 磁碟容量上限 | 2TB | 9.4ZB（128 分區） |
| 雙系統引導切換 | 第三方工具鏈，易損壞 | NVRAM 開機項 + GRUB/os-prober |
| 安裝失敗高發點 | 引導器寫入衝突 | 幾乎無（只要模式統一） |

```
UEFI + GPT 雙系統磁碟佈局（示意）

┌─────────────────────────────────────────────────────────┐
│  NVMe SSD（GPT 分區表）                                  │
├──────────┬──────────────┬──────────┬────────────────────┤
│ ESP 分區  │ Windows 分區  │ 其他資料碟 │ Ubuntu 根分區 /     │
│ 100-500MB│ NTFS (C:)    │ NTFS (D:)│ Ext4               │
│ FAT32    │              │          │                    │
│          │              │          │                    │
│ /boot/efi│  ←───── 兩系統共用同一 ESP ─────→               │
│EFI/Microsoft  EFI/ubuntu（各自獨立子目錄）                 │
└──────────┴──────────────┴──────────┴────────────────────┘
      ↑ NVRAM 開機項指向 ESP 內各引導器，切換系統零衝突
```

---

## 2. 前期環境檢查與相容性校驗

### 2.1 引導模式驗證（msinfo32）

Windows 執行 `msinfo32`，確認「BIOS 模式」為 **UEFI**。這一步決定後續所有操作路徑。

```
Win + R → msinfo32 → 系統資訊 → BIOS 模式
```

| BIOS 模式顯示 | 含義 | 對策 |
|---------------|------|------|
| UEFI | 現代架構，Win11 標配 | 繼續本流程 |
| 舊版 / Legacy | MBR 架構 | 停止；需先轉換 MBR→GPT（mbr2gpt）或重灌，不在本篇範圍 |

### 2.2 分區格式規範（磁碟管理）

`diskmgmt.msc` → 對目標硬碟右鍵→「內容」→「磁碟區」標籤頁，確認「磁碟分割區樣式」為 **GPT**。

```
Win + R → diskmgmt.msc → 磁碟 0（或目標碟）右鍵 → 內容
  → 磁碟分割區樣式：GUID 磁碟分割表格 (GPT) ✅
```

命令列替代方案：

```powershell
# PowerShell（管理員）：看 PartitionStyle 是否為 GPT
Get-Disk | Select-Object Number, FriendlyName, PartitionStyle, Size
```

### 2.3 為什麼兩項都要查

```
環境自檢決策流程

msinfo32 BIOS 模式 = UEFI ？
 ├── 否 → Legacy BIOS 機器：Ubuntu 也須以 Legacy 模式安裝，
 │        否則裝完無法引導（模式不匹配 = 直接失敗）
 └── 是 → 磁碟分割樣式 = GPT ？
          ├── 是 → ✅ 進入第 3 節防護清單
          └── 否 → 該碟為 MBR：轉換（mbr2gpt / 重灌）後再繼續
```

---

## 3. 安裝前必做的五項防護

視頻用四個獨立章節（BitLocker、獨顯直連、Secure Boot、Intel RST）講這一段，是全片避坑密度最高的部分。漏掉任何一項都會在某個後續步驟炸出來。

| # | 防護項 | 不關的後果 | 操作位置 |
|---|--------|-----------|----------|
| 1 | **BitLocker 加密** | 壓縮/改寫分區時磁碟鎖死，Linux 無法讀寫；金鑰遺失 = 資料全滅 | 控制台 → BitLocker → 關閉 |
| 2 | **快速啟動（Fast Startup）** | Windows 休眠態掛載 NTFS，Ubuntu 只讀掛載甚至 fsck 損壞 | 控制台 → 電源選項 → 關閉快速啟動 |
| 3 | **獨顯直連（MUX 切換）** | 部分筆記本 GPU 直連模式下 Live 環境黑屏 | 廠商控制中心切回混合模式 |
| 4 | **Secure Boot** | 部分驅動/nomodeset 參數被攔截（Ubuntu 官方簽名支援，但排障時建議先關） | BIOS → Security → Secure Boot = Disabled |
| 5 | **Intel RST / VMD 模式** | 安裝器完全看不到 NVMe 硬碟（Ubuntu 官方文檔明確要求關閉） | BIOS → SATA/VMD 模式改 AHCI |

> ⚠️ **Intel RST 是隱形殺手**：許多品牌筆電（Dell/Lenovo/MSI）出廠預設 Intel RST（RAID/VMD）模式，Ubuntu 安裝器一個硬碟都列不出來。Ubuntu 官方文檔原文：「If the Ubuntu installer can't detect the disks that you need, you must turn off RST in the computer's firmware」。BIOS 內改為 AHCI 即可。
>
> ⚠️ **改 AHCI 前注意**：Windows 若以 RST 模式安裝，直接切 AHCI 可能導致 Windows 藍屏。安全順序：Windows 內先 `bcdedit /set {current} safeboot minimal` → 重啟進 BIOS 改 AHCI → 進安全模式 → `bcdedit /deletevalue {current} safeboot` 恢復。

```
五項防護檢查清單（安裝前逐項打勾）

□  BitLocker 已關閉（且等金鑰解密完成，不是暫停）
□  快速啟動已關閉
□  獨顯直連已切回混合模式（筆電適用）
□  BIOS 內 Secure Boot = Disabled（排障期）
□  BIOS 內儲存模式 = AHCI（非 RST/RAID/VMD）
□  重要資料已備份（壓縮分區仍有極小機率出錯）
□  隨身碟 ≥8GB 且已騰空（製作過程會清空）
```

---

## 4. 安裝介質製作（Ventoy GPT）

### 4.1 鏡像選擇

下載 **Ubuntu 22.04 LTS Desktop** 版。注意別下成 Server 版——Server 無圖形介面，安裝流程完全不同。

| 版本 | 介面 | 適用 |
|------|------|------|
| Desktop（本篇） | GNOME 圖形介面 + Try Ubuntu 試用 | 日常雙系統 |
| Server | 純文字安裝器 | 雲端/無頭機 |
| Live Server | 文字 + web 安裝頁 | 同上 |

校驗下載完整性（官網 SHA256）：

```bash
# macOS/Linux
shasum -a 256 ubuntu-22.04.x-desktop-amd64.iso
# Windows
certutil -hashfile ubuntu-22.04.x-desktop-amd64.iso SHA256
```

### 4.2 Ventoy 多合一引導碟

Ventoy 的核心價值：隨身碟灌一次 Ventoy，之後**直接拷貝 ISO 檔案**即可開機，免反覆燒錄。一隻碟可同時放 Ubuntu + WinPE + GParted 等多個 ISO。

關鍵配置：執行 Ventoy2Disk 時，在「配置選項」將**分區類型切換為 GPT**。

> **Ventoy 官方文檔驗證**（doc_mbr_vs_gpt）：對 Ventoy 自身功能而言 MBR/GPT 無差異；選 GPT 的意義在於（1）部分機器 UEFI 韌體只認 GPT 碟，（2）與 Windows 11 引導規範一致，「GPT is part of the UEFI spec, so GPT supports UEFI without any compatibility issues」。

```
Ventoy 隨身碟製作流程

┌─────────────┐   ┌──────────────────┐   ┌───────────────────┐
│ 下載 Ventoy  │ → │ Ventoy2Disk.exe  │ → │ 配置選項：        │
│ 官方 zip 解壓│   │ 選擇隨身碟       │   │ 分區類型 = GPT ✅ │
└─────────────┘   └──────────────────┘   └─────────┬─────────┘
                                                     ↓
                                          ┌───────────────────┐
                                          │ Install（清空隨身碟）│
                                          └─────────┬─────────┘
                                                    ↓
                                          ┌───────────────────┐
                                          │ 拷貝 ubuntu ISO    │
                                          │ 進隨身碟（拖放即可）│
                                          └───────────────────┘
```

---

## 5. 磁碟空間壓縮

在 Windows「磁碟管理」對**資料碟（如 D 槽）**執行「壓縮卷」（Shrink Volume），釋出未分配空間（Unallocated Space）作為 Ubuntu 安裝池。

實務要點：

- **從 D 槽壓縮，不動 C 槽**：C 槽有不可移動系統檔案，壓縮量常遠小於空閒量；D 槽資料可先挪走，壓縮乾淨
- 壓縮出的空間**保持未分配狀態**即可，Ubuntu 安裝器會識別
- 建議給 Ubuntu ≥ 50GB（桌面日常）或 ≥ 100GB（開發用途）

```powershell
# 命令列替代：PowerShell（管理員）
# 查詢可壓縮空間
defrag D: /A /U
# 或用 diskpart 的 shrink desired=大小(MB)
diskpart
list volume
select volume <D槽編號>
shrink desired=102400   # 釋出 100GB
```

> ⚠️ 若壓縮可用量遠小於預期：關閉系統保護（還原點）、頁面檔案、休眠檔後重試，或先對 D 槽做磁碟重組。

---

## 6. Live USB 開機與顯卡引導調校

### 6.1 BIOS 開機優先級

開機進 BIOS（通常 F2/Del），將 USB 隨身碟設為第一開機順序。UEFI 機器上隨身碟會顯示為 `UEFI: <隨身碟名>`——**選帶 UEFI 前綴的那個**，保證安裝器以 UEFI 模式啟動（與 Windows 模式一致，這是第 1 節原則的落地）。

### 6.2 GRUB 參數注入（防黑屏核心技巧）

進入 GRUB 選單後，在高亮項目 **Try or Install Ubuntu** 上按 `e` 鍵編輯開機參數，找到 `linux` 開頭的核心行，行尾追加參數後 `Ctrl+X` 啟動。

| 參數 | 作用 | 適用症狀 |
|------|------|----------|
| `nomodeset` | 禁用開機階段載入 GPU 驅動 | 黑屏、花屏、卡啟動 logo |
| `acpi=off` | 關閉 ACPI 電源管理 | 特定老機型當機（最後手段） |
| `noapic` | 禁用 APIC 中斷 | USB/鍵盤失靈（老機器） |

```
GRUB 編輯畫面結構

  Try or Install Ubuntu        ← 高亮此項按 e
  ┌────────────────────────────────────┐
  │ set gfx_payload=keep               │
  │ linux /casper/vmlinuz ... quiet --- │ ← 游標移到此行行尾
  │ initrd /casper/initrd              │
  └────────────────────────────────────┘
  追加： nomodeset  →  Ctrl+X 啟動
```

> 💡 `nomodeset` 只是**安裝階段的拐杖**。進系統後應安裝對應閉源驅動（`ubuntu-drivers autoinstall`）再移除該參數，否則解析度鎖死在低解析度。

### 6.3 進入 Try Ubuntu

選 Try Ubuntu 進試用環境（完整桌面跑在隨身碟上，不碰硬碟）。這個環境同時是後續第 9 節無損擴容的操作台。

---

## 7. Ubuntu 手動分區與引導器部署

### 7.1 為什麼選「其他選項」（Something else）

安裝類型選單中的「安裝 Ubuntu，與 Windows Boot Manager 共存」看似省事，但它自動決定分區大小與位置，不可控。**手動分區（Something else / 其他選項）**才能精準指定根分區與 ESP 掛載點。

### 7.2 分區配置

| 掛載點 | 檔案系統 | 大小 | 說明 |
|--------|----------|------|------|
| `/` | Ext4 | 壓縮出的全部空間（如 100GB） | 系統主體，單分區方案最簡 |
| `/boot/efi` | FAT32（ESP，已有） | 不新建，掛載現有 ESP | **必須**掛載，否則引導器無處安裝 |
| swap | swap（可選） | 視需求，或用 swapfile | 記憶體充足可不建（見 8.4） |

操作順序：

1. 選中「未分配空間（free space）」→ `+` 新增 → Ext4、掛載點 `/`
2. 選中**已有的 ESP 分區**（FAT32，通常 100-500MB，微軟/ESP 標記）→ Change → 掛載點 `/boot/efi`，**不要格式化**
3. 下方「安裝引導啟動器的設備（Device for bootloader installation）」選**整塊系統硬碟**（如 `/dev/nvme0n1`，不帶數字編號）

> ⚠️ **「引導器設備」的真實語義（易誤解點）**：UEFI 模式下這個下拉框的選擇**很大程度上被安裝器忽略**——grub-efi 實際安裝到掛載為 `/boot/efi` 的 ESP 分區內（`EFI/ubuntu` 目錄）。選整盤即可，選錯盤在 UEFI 模式下通常不致命，但漏掛 `/boot/efi` 才是致命錯誤。

```
手動分區操作流程

安裝類型選單
  ├── 清除整個磁碟 ❌（毀掉 Windows）
  ├── 與 Windows 共存（自動）⚠️ 不可控
  └── 其他選項（Something else）✅
        │
        ├─ free space → + → Ext4 / → 100GB
        ├─ 既有 ESP（FAT32）→ Change → /boot/efi（勿格式化！）
        └─ 引導器設備 → /dev/nvme0n1（整盤）
```

### 7.3 雙引導選單的真相（重要勘誤）

視頻稱裝完自動生成含 Windows Boot Manager 的雙引導選單。**實測語義（2024-2026 的 Ubuntu 22.04）**：GRUB 2.06 起 `os-prober` 出於安全原因**預設禁用**，Windows 不會自動出現在 GRUB 選單。修復：

```bash
# Ubuntu 內終端機
sudo nano /etc/default/grub
# 加入一行：
#   GRUB_DISABLE_OS_PROBER=false
sudo update-grub
# 輸出中應出現 "Found Windows Boot Manager on ..."
```

即使 GRUB 選單沒有 Windows，也可以在開機時按 F8/F12（依機型）叫出韌體開機選單選 Windows——ESP 內兩套引導器始終並存，資料無虞。

---

## 8. 裝機後首日配置

視頻第 14-18 章覆蓋的裝機後配置，按依賴順序整理：

### 8.1 GRUB 引導參數持久化

安裝期臨時加的 `nomodeset` 若需保留，寫入 `/etc/default/grub` 的 `GRUB_CMDLINE_LINUX_DEFAULT` 後 `sudo update-grub`。裝好閉源驅動後即可移除。

### 8.2 連接 Wi-Fi 與更換安裝源

```bash
# 檢查網卡識別情況
lspci | grep -i network
nmcli device wifi list

# 換國內鏡像源（22.04 代號 jammy）
sudo sed -i 's|archive.ubuntu.com|mirrors.aliyun.com|g' /etc/apt/sources.list
sudo apt update && sudo apt upgrade -y
```

### 8.3 系統時間同步（雙系統經典 8 小時漂移）

Windows 預設硬體時鐘存本地時間，Linux 預設存 UTC——雙系統切換後時間差 8 小時（東八區）。標準修法（Ubuntu 端改用本地時間）：

```bash
timedatectl set-local-rtc 1 --adjust-system-clock
# 查詢確認
timedatectl
# RTC in local TZ : yes
```

### 8.4 Swap 交換空間

22.04 預設使用 `/swapfile`（而非獨立分區）。記憶體 ≥16GB 且不休眠的桌面機可縮小或關閉：

```bash
swapon --show                          # 當前狀態
sudo swapoff /swapfile                 # 停用
sudo fallocate -l 8G /swapfile         # 調整為 8GB（示例）
sudo chmod 600 /swapfile
sudo mkswap /swapfile && sudo swapon /swapfile
```

| 記憶體 | 建議 swap | 場景 |
|--------|-----------|------|
| ≤ 8GB | = 記憶體大小 | 輕量機 |
| 16GB | 8GB 或減半 | 日常開發 |
| ≥ 32GB | 2-4GB 或關閉 | 需休眠則必須保留 |

---

## 9. 後期磁區動態調整與無損擴容

### 9.1 運行時限制

Ubuntu 運行中無法對**自身掛載中的根分區**做縮放/位移（裝置 busy）。Windows 端同理。跨系統調整必須借助第三方中性環境——Live USB。

### 9.2 無損擴容完整流程

```
無損擴容流程（Windows 釋放空間 → Ubuntu 吸收）

┌──────────────┐   ┌───────────────────┐   ┌────────────────────┐
│ Windows 磁碟  │ → │ 產生相鄰未分配空間  │ → │ 重啟進入 Try Ubuntu │
│ 管理：壓縮 D 槽│   │ （緊鄰 Ubuntu 根分區）│  │ （Live USB 環境）   │
└──────────────┘   └───────────────────┘   └─────────┬──────────┘
                                                      ↓
                                           ┌────────────────────┐
                                           │ GParted → 選 ext4  │
                                           │ → 調整大小/移動     │
                                           │ → 拖滿未分配空間     │
                                           │ → Apply ✅         │
                                           └────────────────────┘
```

GParted 在 Try Ubuntu 環境內建（22.04 桌面版自帶；沒有就 `sudo apt install gparted`）。

關鍵操作：

1. GParted 右上角選**目標硬碟**（認清 Windows 分區與 ext4 分區的排布）
2. 選中 ext4 根分區 → 右鍵「調整大小/移動（Resize/Move）」
3. 拖動邊界**吞併相鄰未分配空間** → 確認無 ⚠️ 警告後 Apply
4. 全程對 ext4 資料零損壞（線上檔案系統擴展技術）

> ⚠️ **相鄰性是硬約束**：GParted 只能吸收**緊鄰**的未分配空間。若壓縮出的空間與 ext4 之間隔著 NTFS 分區，需要先移動中間分區（耗時且有風險）或換從磁碟尾部壓縮。規劃分區時就該把 Ubuntu 放在碟尾。
>
> 💡 這也是 Ventoy 碟的長期價值：ISO 之外再放一個 GParted Live / WinPE，隨身碟就是永久救援載具（引導修復、救援刪除、擴容三合一）。

### 9.3 擴容 vs 重建對比

| 方案 | 資料風險 | 耗時 | 適用 |
|------|----------|------|------|
| GParted 無損擴容 | 極低（仍建議備份） | 分鐘級 | 空間緊張，日常擴容 |
| 重灌 Ubuntu | 根分區資料全滅 | 小時級 | 系統本身要換版本/架構 |
| 動態掛載新碟 | 無 | 分鐘級 | 有空閒硬碟可直接掛 `/home` |

---

## 10. 驗證與勘誤記錄

視頻聲稱 vs 外部官方資料交叉驗證結果：

| 視頻聲稱 | 驗證結果 | 來源 |
|----------|----------|------|
| 前提：兩系統引導模式必須一致（UEFI） | ✅ 正確，雙系統基本原則 | Ubuntu 官方文檔、社區共識 |
| GPT 允許多系統共用/獨立管理 ESP，避免 MBR 引導器覆蓋衝突 | ✅ 正確 | GPT/UEFI 規範，Ventoy 官方文檔 |
| Ventoy 製作時分區類型切 GPT 以符合 Win11 引導規範 | ✅ 正確（補充：對 Ventoy 自身功能無差異，意義在 UEFI 相容性） | ventoy.net/doc_mbr_vs_gpt.html |
| Intel RST 需關閉（第 8 章） | ✅ 正確，Ubuntu 官方文檔原文確認「installer can't detect disks → turn off RST」 | ubuntu.com/desktop/docs Intel RST 頁 |
| 裝完後自動生成含 Windows 的雙引導選單 | ⚠️ **部分過時**：GRUB 2.06（22.04 起）os-prober 預設禁用，需手動 `GRUB_DISABLE_OS_PROBER=false` + `update-grub` | omgubuntu.co.uk、askubuntu.com |
| `nomodeset` 防黑屏 | ✅ 正確，標準排障手段 | Ubuntu 社區文檔 |
| GParted Live 環境無損擴容 ext4 | ✅ 正確，Live 環境根分區未掛載即可操作 | GParted 官方文檔 |
| Ubuntu 22.04 作為安裝標的（視頻 2024-07） | ⚠️ 時效提醒：22.04 標準支持至 2027-05；2026 年新裝建議評估 24.04 LTS / 26.04 LTS | ubuntu.com/info/release-end-of-life |

---

## 11. 時效性提醒（2026 視角）

視頻發布於 2024-07，2026 年實裝前的版本考量：

| 版本 | 標準支持至 | 評估 |
|------|-----------|------|
| 22.04 LTS（視頻標的） | 2027-05（ESM 至 2032-05） | 仍可用，剩餘窗口縮短 |
| 24.04 LTS | 2029-05（ESM 至 2034-05） | 2026 新裝推薦 |
| 26.04 LTS | 2031-05（ESM 至 2036-05） | 2026-04 發布，最新 LTS |

本篇流程（UEFI 校驗、Ventoy GPT、手動分區、GParted 擴容）**與版本無關**，對 24.04/26.04 完全適用；僅 8.2 換源（24.04 起改 `/etc/apt/sources.list.d/ubuntu.sources` 格式）與具體桌面細節有差異。

---

## 12. 決策樹：要不要上雙系統

```
需要 Ubuntu 環境？
├── 只跑 CLI 工具/容器/編譯？
│    └── → WSL2（零風險、秒切換、不動磁碟）
│         見 [[install-multiple-Ubuntu-WSL2]]、[[wsl]]
├── 需要 GPU 直控/原生內核/性能測試？
│    └── → 雙系統（本篇）
├── 偶爾用、怕折騰？
│    └── → 虛擬機（VMware/VirtualBox）
└── 主要用 Linux、偶爾 Windows？
     └── → 反過來：Linux 為主 + Windows 虛擬機
```

| 維度 | WSL2 | 虛擬機 | 雙系統 |
|------|------|--------|--------|
| 性能 | 接近原生（I/O 偏弱） | 有損耗 | 100% 原生 |
| GPU | 可直通（CUDA） | 一般不可用 | 完整獲得 |
| 切換成本 | 秒級（視窗切換） | 分鐘級 | 重啟 |
| 磁碟風險 | 無 | 無 | 存在（本篇全部防護即為此） |
| 內核控制 | 受限 | 完整 | 完整 |

---

## 參考資料

- [Windows 11 安装 Ubuntu 22.04 双系统步骤和注意事项 — Robotic Artisan（視頻）](https://www.youtube.com/watch?v=szlNPdAt3Kk)
- [Ventoy MBR vs GPT — 官方文檔](https://www.ventoy.net/en/doc_mbr_vs_gpt.html)
- [Intel RST during Ubuntu installation — Ubuntu 官方文檔](https://ubuntu.com/desktop/docs/en/latest/reference/intel-rst-during-ubuntu-installation/)
- [Ubuntu release end-of-life — 官方支持週期表](https://ubuntu.com/info/release-end-of-life)
- [OS Prober is Disabled in Ubuntu 22.04 — OMG! Ubuntu!](https://www.omgubuntu.co.uk/2021/12/grub-doesnt-detect-windows-linux-distros-fix)
- [Missing Windows from GRUB After Dual Boot — It's FOSS](https://itsfoss.com/grub-os-prober/)

## 相關筆記

- [[install-multiple-Ubuntu-WSL2]] — WSL2 多實例方案（決策樹中的輕量分支）
- [[wsl]] — WSL 基礎安裝
