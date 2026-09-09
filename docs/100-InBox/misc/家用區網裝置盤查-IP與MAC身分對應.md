---
title: 家用區網裝置盤查 — DHCP 租約裝置 IP/MAC 身分對應
aliases: [區網裝置清單, LAN device inventory]
tags:
  - network
  - lan-inventory
  - status/active
  - type/doc
source:
  - "https://192.168.1.1/"
author: tommy
created: 2026-08-31
updated: 2026-08-31
description: 中華電信 Askey RTF8207W-E 的 DHCP 租約表 13 台裝置，用被動指紋（ARP/Bonjour/mDNS/埠掃描/OUI）逐一識別身分的結果與可重跑方法
level: intermediate
stars: 4
---

# 家用區網裝置盤查 — DHCP 租約裝置 IP/MAC 身分對應

> 起因：路由器（中華電信 Askey RTF8207W-E，閘道 192.168.1.1）的 DHCP 租約表列出 13 台裝置，只有 IP/MAC 沒有名字。本文用純被動/低強度探測（不登入路由器、不跑重型掃描）把每台對應到實體設備，並整理成可重跑的普查配方。

## 裝置對應總表

2026-08-31 實測，ARP MAC 與 DHCP 表 13 筆全數吻合（無 ARP 偽造跡象）：

| IP | MAC | 身分 | 識別依據 | 確信度 |
|----|-----|------|----------|--------|
| .102 | f6:bf:8f:ef:36:bd | 這台 MacBook Pro（林炫羽的MacBook Pro / 主機名 yudady） | en0 實體 MAC 完全吻合 + `scutil --get ComputerName` | 確定 |
| .115 | ca:d7:5a:75:8a:df | tommy的MacBook Pro（tommyymr9.local） | Bonjour 廣播 SSH/SMB/RFB/AirPlay 全中，SSH 橫幅 OpenSSH 10.3（新版 macOS） | 確定 |
| .117 | 80:32:53:be:3b:6f | MSI 遊戲電腦 | 單播 mDNS 反解回 `MSI.local` + 廣播 `_nvstream_dbd._tcp`（NVIDIA GameStream）+ Intel 網卡 OUI | 確定 |
| .118 | 9c:b6:d0:68:53:4d | Windows 電腦（雙系統主機） | SSH 橫幅 `OpenSSH_for_Windows_8.1` + 開 445 + Killer 網卡 OUI（Rivet Networks） | 高 |
| .112 | f4:5c:89:c3:28:c3 | 另一台 Mac（macOS 12 Monterey 等級的舊機） | Apple 原廠 OUI + OpenSSH 8.6（Monterey 內建版本）；不廣播 Bonjour | 中高 |
| .109 | 52:51:fa:2d:c4:24 | iPhone / iPad | 62078 埠 = iOS lockdownd 特徵埠 | 中高 |
| .110 | 2c:19:5c:4d:5b:9f | 小米手機或 IoT 裝置 | Xiaomi OUI（Beijing Xiaomi Mobile Software） | 中 |
| .101 | f8:89:d2:33:76:ab | Amazon 系設備（Echo/Fire/Kindle，休眠中） | Cloud Network Technology Singapore OUI（Amazon 代工），無開埠 | 中 |
| .104 | 42:d1:4a:32:e9:84 | 開私有 Wi-Fi 位址的手機/平板 | MAC 隨機化（本地管理位址）+ 無開埠 + 不回 mDNS/NBNS | 低（只知道類別） |
| .105 | 5a:38:27:14:6e:e5 | 同上 | 同上 | 低 |
| .106 | 66:0b:a3:b4:f1:09 | 同上 | 同上 | 低 |
| .108 | 9e:9a:dd:13:53:bc | 同上 | 同上 | 低 |
| .113 | 02:ac:90:5c:c9:92 | 同上 | 同上 | 低 |

額外發現：ARP 表有一台 **192.168.1.116（04:09:86:18:13:b8，Arcadyan OUI）** 不在 DHCP 租約表內 —— Arcadyan 是中華電信設備常用代工廠，推測是全屋通 mesh 節點或機上盒（固定 IP 或剛上線）。

## 識別方法 Pipeline

macOS 沒裝 nmap 也能做完整輪普查，強度遞進：

```
DHCP 租約表（路由器貼出）
      │
      ▼
[1] ping 全網 → arp -a 交叉驗證 IP↔MAC（13/13 吻合）
      │
      ▼
[2] dns-sd -B 瀏覽 Bonjour 服務（_airplay/_ssh/_rfb/_smb）
    → 撈到 2 台 MacBook 的「人類可讀名」
      │
      ▼
[3] dns-sd -L 解析服務實例 → tommymr9.local → 192.168.1.115
      │
      ▼
[4] 單播 mDNS 反解（UDP 5353 PTR 查詢）→ MSI.local 現形
      │
      ▼
[5] TCP 埠指紋（nc -z）+ SSH 橫幅讀取 → 作業系統版本
      │
      ▼
[6] OUI 查詢（api.macvendors.com）→ 硬體廠商
      │
      ▼
[7] 仍無名 → 路由器 DHCP option 12 hostname（需 captcha 登入，本次未做）
```

## 私有 Wi-Fi 位址（MAC 隨機化）

iOS/Android/Windows 都支援每 SSID 隨機化 MAC。判斷法看第一碼的**第二個十六進位字元**：

| 第二碼 | 位址類型 | 範例 |
|--------|----------|------|
| 2 / 6 / A / E | 本地管理（隨機化/虛擬） | 9e:、5a:、ca:、66:、42:、52:、02:、f6: |
| 0 / 4 / 8 / C | 全域唯一（原廠燒錄） | 80:32:、f4:5c:、2c:19:、9c:b6: |

本清單 13 台中有 8 台是隨機化位址 —— 這是常態不是異常。推論：

- 這 8 台裡查 OUI 能查到的（.110 Xiaomi、.115 剛好撞出 Apple？不，.115 的 ca:d7 也是本地位址）只是巧合碰撞，OUI 結果對本地位址**不可信**
- 真正可信的 OUI 判定只有全域位址那幾台：.112（Apple）、.117（Intel）、.118（Killer）、.110（Xiaomi）、.101（Amazon 代工）

> 教訓：本地管理位址查 macvendors 得到的「廠商」是別人燒錄的 OUI 撞庫結果，只能當雜訊。識別隨機化裝置要靠行為指紋（開埠、mDNS、NBNS），不能靠 OUI。

## 埠指紋速查表

| 埠 | 服務 | 設備指向 |
|----|------|----------|
| 22 + `SSH-2.0-OpenSSH_x.y` | OpenSSH | 版本即 OS 指紋：8.6=macOS 12、10.3=新版 macOS、`for_Windows_8.1`=Windows 內建 |
| 445 | SMB | macOS（檔案共享）或 Windows |
| 5000 | AirPlay / UPnP | Mac 常見組合 |
| 5900 | VNC / 螢幕共享 | Mac 開了遠端管理 |
| 62078 | iOS lockdownd | iPhone/iPad（usbmuxd over Wi-Fi） |
| 7000 | AirPlay 接收 | Mac（Sidecar/AirPlay 接收器） |
| 49152+ | 動態/隨機埠 | 需逐一探測內容 |

特徵組合比單埠可靠：`.115 = 22+445+5000+5900+7000` 五連，是 Mac 開滿共享服務的教科書指紋。

## 可重跑指令

```bash
# 1. 喚醒全網 + ARP 交叉驗證
for i in $(seq 100 120); do ping -c1 -W 800 192.168.1.$i >/dev/null 2>&1; done
arp -a | grep '192.168.1.'

# 2. Bonjour 瀏覽（撈裝置人類名）
dns-sd -B _airplay._tcp local.    # 也試 _ssh/_rfb/_smb/_companion-link
dns-sd -L "tommy的MacBook Pro" _ssh._tcp local.   # 解析實例 → 主機名

# 3. 名稱反解
dscacheutil -q host -a name tommymr9.local

# 4. 埠指紋 + SSH 橫幅
nc -z -G 1 192.168.1.115 22 445 5900 7000
nc -G 3 192.168.1.118 22 </dev/null | head -c 60

# 5. OUI（僅對全域 MAC 有意義）
curl -s https://api.macvendors.com/f4:5c:89:c3:28:c3
```

單播 mDNS 反解（抓 MSI.local 那步）是對 `192.168.1.x` 的 5353/UDP 發 PTR 查詢，Python 30 行內搞定；NBNS 節點狀態查詢（UDP 137）對這批裝置全被防火牆擋掉，別浪費時間。

## 未識別與待辦

- [ ] .104/.105/.106/.108/.113 五台隨機化裝置：要 100% 對上名字，需登入路由器 `https://192.168.1.1`（帳號 `cht`），到 DHCP 頁點每筆「More」看 DHCP option 12 hostname。驗證碼需人工輸入
- [ ] .116（Arcadyan）身分確認：全屋通節點 or 機上盒
- [ ] 若某台要長期固定 IP：在路由器端做 DHCP 靜態綁定（見下方相關筆記），別只在系統內寫死

## 相關筆記

- [[旧平板变身低功耗Linux家庭服务器]] — 同一路由器的 DHCP 靜態 IP 綁定做法（IP 漂移是斷連頭號原因）

---

*盤查時間：2026-08-31，方法：ARP + Bonjour + 單播 mDNS + 埠指紋 + OUI，未登入路由器*
