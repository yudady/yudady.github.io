---
title: 老筆電變身家庭 NAS 實戰：Ubuntu Server + CasaOS + Tailscale
aliases: [2009 Laptop NAS, KuleGuy NAS, CasaOS 老筆電改造]
tags:
  - nas
  - homelab
  - casaos
  - ubuntu-server
  - tailscale
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=rAc3kWZ_td0"
  - "https://github.com/IceWhaleTech/CasaOS"
  - "https://tailscale.com/"
author: KuleGuy (Luke)
created: 2026-09-06
updated: 2026-09-06
description: 2009 年 Toshiba 老筆電 + 12TB 外接硬碟，用 Ubuntu Server + CasaOS + Tailscale 打造低成本入門家庭 NAS 的完整實錄、效能瓶頸分析與風險評估
level: beginner
stars: 3
---

# 老筆電變身家庭 NAS 實戰：Ubuntu Server + CasaOS + Tailscale

> YouTuber KuleGuy（Luke）把一台 2009 年的 Toshiba Satellite L455-S5980 老筆電加上三顆外接硬碟（共 12TB），用 Ubuntu Server + CasaOS 建成家庭 NAS，再透過 Tailscale 實現異地存取。本筆記整理完整建置流程，並補充影片未明說的效能瓶頸技術分析（10 MB/s 的真正原因）與資料風險評估。

## 目錄

- [[#一、專案動機與背景]]
- [[#二、硬體選型與配置]]
- [[#三、系統建置與軟體架構]]
- [[#四、異地遠端存取：Tailscale]]
- [[#五、實測效能、痛點與客觀限制]]
- [[#六、驗證與勘誤記錄]]
- [[#七、實務行動建議]]

---

## 一、專案動機與背景

### 需求緣起

創作者身為影音內容創作者，以往上傳 YouTube 後便刪除本機原始檔案以節省空間（0:06）。這種「雲端即備份」的模式遇到了現實限制，於是開始尋找儲存方案。

### 三種儲存路線比較

| 方案 | 月費 | 容量 | 隨身性 | 資料主控權 |
|------|------|------|--------|-----------|
| 雲端服務（Google Drive / Dropbox / Mega） | 免費額度不夠用，付費月租 | 有限 | 最好 | ❌ 公司可掃描檔案 |
| 外接硬碟隨身攜帶 | 無 | 1TB 起 | ❌ 攜帶麻煩 | ✅ 完全自控 |
| 自建 NAS（Network Attached Storage） | 電費而已 | 可擴充 | ✅ 網路隨處存取 | ✅ 完全自控 |

### 什麼是 NAS

NAS（Network Attached Storage，網路附加儲存）本質上就是一台裝了硬碟的專用電腦，接上路由器後，區域網路內任何設備都能存取——也就是「個人私有雲」：可擴容、可換碟、資料完全自己掌控。

### 選擇判斷

```
選自建 NAS 如果你：
  ✅ 手邊有閒置電腦（零硬體成本）
  ✅ 不想付雲端月租費
  ✅ 願意花時間學基礎 Linux

買成品 NAS（Synology/QNAP）如果你：
  ✅ 預算充足（數萬元起）
  ✅ 要求 RAID 容錯與保固
  ✅ 不想折騰
```

創作者因為沒預算（1:46），選擇用手邊既有設備自建。

---

## 二、硬體選型與配置

### 運算主機：2009 年 Toshiba Satellite L455-S5980

| 項目 | 規格 | 狀態 |
|------|------|------|
| CPU | Intel Celeron T3000 雙核心 1.8GHz | 原廠 |
| RAM | 4GB | 自行升級（原廠 2GB，官方上限 8GB） |
| 系統碟 | 512GB 2.5 吋 SSD | 自行升級（原廠 250GB HDD） |
| 網路 | 10/100 百兆乙太網路 | 原廠（後述瓶頸來源） |
| USB | USB 2.0 | 原廠 |

### 儲存陣容：三顆 Seagate 外接硬碟

| 容量 | 型號類型 | 價格 |
|------|----------|------|
| 2TB | 外接硬碟 | $60 |
| 4TB | 外接硬碟 | $89 |
| 6TB | Expansion 系列 | $120 |
| **合計 12TB** | | **$269 USD** |

### 網路實體走線

```
父母房路由器 → 乙太網路交換器（Switch）
                    │
                    └─→ 75 英尺 Cat8 網線
                          → 穿牆孔 → 閣樓
                          → 衣櫃舊電視纜線孔下垂
                          → 書桌下 → NAS 筆電
```

創作者父親早年買的 75ft Cat8 網線（3:07），穿越牆壁與閣樓直連主路由器的實體交換器，確保有線連線穩定。**注意**：Cat8 對這台百兆網口的筆電是嚴重過剩規格（Cat5e 即可跑滿千兆），瓶頸永遠在設備端不在線材。

---

## 三、系統建置與軟體架構

### 作業系統選型：為什麼棄 TrueNAS / OMV

| 系統 | 上手難度 | 硬體需求 | 適合誰 |
|------|----------|----------|--------|
| TrueNAS | 高（ZFS 概念重） | 高（建議 8GB+ ECC RAM） | 進階玩家、資料安全至上 |
| OpenMediaVault（OMV） | 中 | 低 | 有 Linux 基礎者 |
| **Ubuntu Server + CasaOS** | **低（一鍵腳本）** | **極低** | 新手、老硬體 |

創作者明確表示跟著 Tech Hut 的教學影片走（3:39），因為 TrueNAS 和 OMV 都試過但門檻較高，這個組合「看起來更簡單」。

### 安裝流程

```
下載 Ubuntu Server ISO
   → 燒錄至 USB 隨身碟
   → 插入老筆電安裝（⚠️ 取消勾選 LVM）
   → 主電腦 SSH 連入
   → apt update
   → curl 一鍵安裝 CasaOS
   → 瀏覽器開啟 http://<IP> 進 Web GUI
```

關鍵命令：

```bash
# SSH 連入（4:16）
ssh username@<伺服器IP>

# 更新套件（4:29）
sudo apt update

# 一鍵安裝 CasaOS（官方腳本，IceWhaleTech 開源專案）
curl -fsSL https://get.casaos.io | sudo bash
```

**LVM 陷阱（4:10）**：Ubuntu Server 安裝程式預設勾選 LVM（Logical Volume Manager，邏輯卷管理），會把硬碟切成多個分割區、只給根目錄一部分空間。新手務必取消勾選，讓系統碟一次用滿。

### CasaOS 設定要點

1. 首次進入先格式化所有外接磁碟（5:02，格式化後需手動重新整理頁面才會顯示）
2. 檔案管理器中對外接碟右鍵 → 設定為區域網路共享（5:15）
3. 無法在 CasaOS 內重新命名硬碟 → 退而求其次在客戶端檔案管理器掛載後改名
4. 有合併多碟為單一儲存池的功能，但創作者過去踩過坑，此次不使用

### 多設備掛載

- Linux 桌機：Dolphin 檔案管理器輸入 `smb://<NAS IP>` 即見共享碟（5:37）
- Android 手機：需另裝 SMB App（內建檔案管理器不好用，6:07）

---

## 四、異地遠端存取：Tailscale

### 為什麼需要

NAS 在家裡的區域網路，出門（創作者場景：學校）後就摸不到。傳統解法要在路由器上開 Port Forwarding 或設定 DDNS，對新手既複雜又有安全風險。

### Tailscale 原理

Tailscale 是基於 WireGuard 的 VPN Mesh（網狀虛擬專用網路）服務：所有設備登入同一帳號（創作者用 Google 帳號）後，各自獲得一個 100.x.x.x 的虛擬 IP，彼此直連組網，無需動路由器任何設定。

```
        ┌───────────── Tailscale 虛擬網路（Tailnet）─────────────┐
        │                                                       │
   家中 NAS ────┐                                       ┌──── 學校筆電
   (Ubuntu)     │         100.x.x.x 虛擬 IP 互通          │      (HP)
                ├───────────── 直連隧道 ──────────────────┤
   Android ─────┘         （NAT 穿透，免開埠）            └──── 任何網路
   手機
```

### 安裝方式

```bash
# NAS 端（Ubuntu Server，6:37）
curl -fsSL https://tailscale.com/install.sh | sh
sudo tailscale up   # 跳出 Google 帳號綁定連結

# HP 筆電（Linux）同樣 curl 安裝
# Android 手機：Play Store 裝 Tailscale App 登入同帳號
```

### 遠端存取方案對比

| 方案 | 需動路由器 | 安全性 | 設定難度 | 公網 IP 需求 |
|------|-----------|--------|----------|-------------|
| Port Forwarding | ✅ 要 | ❌ 服務直接暴露公網 | 中 | 要 |
| DDNS + PF | ✅ 要 | ❌ 同上 | 高 | 要（動態） |
| **Tailscale** | ❌ 免 | ✅ WireGuard 加密、設備白名單 | **低（登入即用）** | 免 |

創作者自述這是全程最複雜的一步（6:21），跟著 BigBearTechWorld 的教學完成——但相比傳統方案已是最低門檻。

---

## 五、實測效能、痛點與客觀限制

### 實測速度：約 10 MB/s

從備份硬碟複製資料至 NAS 僅約 10 MB/s（5:41），搬完所有資料花了數小時。創作者歸因於「低階硬體」，但沒有拆解具體瓶頸。依硬體規格推算：

| 環節 | 理論頻寬 | 對應速度上限 | 是否瓶頸 |
|------|----------|--------------|----------|
| 百兆乙太網路（10/100） | 100 Mbps | **~12.5 MB/s** | ✅ **真正瓶頸** |
| USB 2.0 | 480 Mbps | ~35 MB/s | 潛在第二瓶頸 |
| Celeron T3000 跑 Samba | — | 單使用者綽綽有餘 | ❌ 非瓶頸 |
| Cat8 網線 | 40 Gbps | 遠超所需 | ❌ 純浪費 |

實測 10 MB/s 幾乎貼滿百兆網路上限——這是 2009 年入門筆電的時代限制，與硬碟或線材無關。若換任何有 Gigabit 網孔的設備，速度可立即躍升 10 倍。

### 資料容錯風險：零冗餘

三顆外接碟**獨立掛載、未組 RAID 或儲存池**（7:08）：

- 任一碟故障 → 該碟資料直接遺失，無任何備援
- 創作者自己也在片尾承認「硬碟遲早都會壞，有些東西就是不會永遠存在」（7:27）
- 老筆電長期供電運轉，電源模組與 USB 供電穩定性都是額外風險點

### 定位：過渡期方案

創作者明確將此定位為臨時方案（7:19）：不滿意速度，也預期硬碟終將損壞。核心價值在於**極低試錯成本**——零採購的閒置設備 + 開源軟體，先跑起來、先學會，之後再升級硬體時所有軟體知識（CasaOS、Tailscale、SMB）全部可平移。

---

## 六、驗證與勘誤記錄

依 ob-clip 流程對影片聲稱交叉驗證（2026-09-06 驗證）：

| 影片聲稱 | 驗證結果 | 來源 |
|----------|----------|------|
| Toshiba L455-S5980 配 Celeron T3000 1.8GHz 雙核 | ✅ Office Depot / Newegg / eBay 規格頁一致；原配 2GB RAM + 250GB HDD，「升級至 4GB/512GB SSD」與官方上限（8GB）相容 | officedepot.com、newegg.com |
| 字幕口誤「Celeron T300」 | ⚠️ 正確型號為 T3000（雙核心 Penryn） | 規格頁 |
| CasaOS 官方一鍵 curl 安裝 | ✅ IceWhaleTech 開源專案，官方指令 `curl -fsSL https://get.casaos.io \| sudo bash` 屬實 | github.com/IceWhaleTech/CasaOS |
| Tailscale 免 Port Forwarding 異地組網 | ✅ 官方 homelab 使用案例文件確認；WireGuard 加密、零配置 | tailscale.com/use-cases/homelab |
| Content Insights 寫「75 英尺 CAT-A 網線」 | ⚠️ 字幕原文為「cat8」，CAT-A 為聽打筆誤；且 Cat8 對百兆網口嚴重過剩（Cat5e 即可） | 字幕 3:07 |
| 10 MB/s 歸因「低階硬體」 | ⚠️ 更精確：百兆網卡瓶頸（上限 ~12.5 MB/s），非 USB 2.0 亦非 CPU | 規格推算 |
| 三碟無 RAID 無備援 | ✅ 影片未展示任何陣列/快照設定，風險自述屬實 | 影片 7:08 |

影片資訊：KuleGuy 頻道（Luke），2024-05-27 發布，長度 7:35，觀看 53,999 次、讚 1,584。

---

## 七、實務行動建議

### 適用場景判斷

```
這套方案適合你如果：
  ✅ 個人冷資料備份（照片、影片歸檔）
  ✅ 小型文件同步
  ✅ homelab 新手入門練手
  ✅ 手邊正好有閒置筆電

不適合如果：
  ❌ 高頻讀寫、多路 4K 串流剪輯
  ❌ 嚴苛的即時備份 / 商用情境
  ❌ 資料不容許任何遺失（無 RAID 是硬傷）
```

### 防護強化（若照做）

- ✅ 規劃 3-2-1 備份：3 份資料、2 種媒介、1 份異地（本方案僅 1 份，最低標都不達）
- ✅ 重要資料另備一份至雲端或另一地點硬碟
- ❌ 避免把三顆外接碟當「安全備份」——它們只是容量，不是冗餘

### 未來升級路線

| 升級目標 | 建議 | 預期效果 |
|----------|------|----------|
| 100 MB/s+ 滿速 | 二手 Mini PC 或準系統 NAS 殼（原生 SATA/NVMe + Gigabit 網孔） | 千兆區網跑滿 |
| 資料安全 | TrueNAS（ZFS mirror）或 OMV + SnapRAID | 單碟故障不丟資料 |
| 不想換硬體 | 至少 USB 3.0 轉 Gigabit 網卡（若筆電有 ExpressCard/PCIe 通道） | 緩解但不治本（USB 2.0 仍卡 35 MB/s） |

---

## 參考資料

- [影片：Turning my 2009 Laptop into an easy home NAS! (Ubuntu Server + CasaOS)](https://www.youtube.com/watch?v=rAc3kWZ_td0) — KuleGuy
- [Tech Hut NAS 教學（影片引用）](https://www.youtube.com/watch?v=_qNWpdFqLIU)
- [BigBearTechWorld Tailscale 教學（影片引用）](https://www.youtube.com/watch?v=BvVkM-EkmXM)
- [CasaOS 官方倉庫（IceWhaleTech）](https://github.com/IceWhaleTech/CasaOS)
- [Tailscale Homelab 使用案例](https://tailscale.com/use-cases/homelab)
- [Ubuntu Server 下載](https://ubuntu.com/download/server)
- [Toshiba Satellite L455-S5980 規格（Office Depot）](https://www.officedepot.com/a/products/932967/Toshiba-Satellite-L455-S5980-156-Widescreen/)

## 相關筆記

- [[headless-laptop]]（閒置筆電伺服器化的系統面設定）
- [[ssh-key-setup]]（SSH 免密連線設定）
