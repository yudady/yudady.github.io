---
title: 闲置 MacBook Air 变身 NAS + 高清机顶盒 — 配置教程笔记
aliases: [MacBook Air NAS, M1 NAS 机顶盒, OrbStack 家庭服务器]
tags:
  - macos
  - self-hosted
  - nas
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=XN0AeV355BA"
  - "https://post.smzdm.com/p/a65xnr0e"
author: 梅有机（YouTube 频道 @myjstudio2023）
created: 2026-09-01
updated: 2026-09-01
description: 用闲置 M1 MacBook Air 搭建家庭 NAS + 智能家居中枢 + 4K 机顶盒的完整配置流程：储存拓扑、防休眠、OrbStack 容器化、Immich/Jellyfin/Home Assistant 部署与手机遥控
level: intermediate
stars: 3
note: 无字幕（创作者关闭字幕），Tier 2 metadata + 外部资料综合整理。smzdm 文字版为 AI 生成摘要，文中精确数字（百分比/速率/功耗）未经独立验证，已标注。
---

# 闲置 MacBook Air 变身 NAS + 高清机顶盒

> 一台丐版 M1 MacBook Air（闲置机）通过拓展坞 + 外接硬盘阵列 + OrbStack 容器化，同时承担「家庭 NAS」「照片备份中心」「智能家居中枢」「4K 影音机顶盒」多重角色。核心思路：macOS 原生能力（SMB 共享、屏幕共享、caffeinate）打底，容器化服务上层，手机 App 补交互短板。

**视频**：[闲置 Macbook Air 变身 NAS+高清机顶盒，终结配置教程](https://www.youtube.com/watch?v=XN0AeV355BA)（梅有机，2026-02-03，约 10 分钟）

---

## 目录

- [一、整体架构](#一整体架构)
- [二、硬件选型与储存拓扑](#二硬件选型与储存拓扑)
- [三、macOS 服务器底层环境](#三macos-服务器底层环境)
- [四、容器化服务与智慧生态](#四容器化服务与智慧生态)
- [五、影音中心与客厅机顶盒交互](#五影音中心与客厅机顶盒交互)
- [六、决策树与避坑清单](#六决策树与避坑清单)
- [参考资料](#参考资料)

---

## 一、整体架构

整条链路的分层数据流：

```
                         ┌─────────────────────────────┐
                         │   闲置 M1 MacBook Air        │
                         │   (担任家庭数字中枢)          │
                         └──────────────┬──────────────┘
                                        │ USB-C / 雷雳
              ┌─────────────────────────┼─────────────────────────┐
              │                         │                         │
     ┌────────▼────────┐      ┌─────────▼────────┐      ┌────────▼────────┐
     │ 多功能拓展坞      │      │ 多盘位硬盘坞      │      │ HDMI → 电视      │
     │ · 千兆网口        │      │ · 2×3.5" SATA    │      │ · 4K 画面输出    │
     │ · HDMI 输出      │      │ · 1× NVMe M.2    │      │                 │
     │ · PD 供电        │      │ (最高 56TB)       │      │                 │
     └────────┬────────┘      └─────────┬────────┘      └─────────────────┘
              │ 固定静态 IP              │ 挂载
              │                         │
     ┌────────▼─────────────────────────▼────────┐
     │              macOS 系统层                   │
     │  · caffeinate 防休眠守护进程                │
     │  · SMB 文件共享 / 屏幕共享(远程桌面)         │
     │  · 磁盘工具 RAID + 格式化                   │
     └────────────────────┬───────────────────────┘
                          │
     ┌────────────────────▼───────────────────────┐
     │           OrbStack 容器引擎                 │
     │  ┌─────────┐ ┌────────┐ ┌────────┐         │
     │  │ DPanel  │ │ Home   │ │ Immich │         │
     │  │ :8807   │ │ Assist.│ │ :2283  │         │
     │  │ 管理面板 │ │ :8123  │ │ 相簿   │         │
     │  └─────────┘ └────────┘ └────────┘         │
     └────────────────────────────────────────────┘
                          │
     ┌────────────────────▼───────────────────────┐
     │        macOS 原生应用层                      │
     │  · Jellyfin :8096 (影视库)                  │
     │  · qBittorrent (Web UI 远程下载)             │
     │  · Infuse (电视端播放)                       │
     └────────────────────────────────────────────┘

  客户端: 手机 Remote for Mac / Infuse / Immich App / SMB 挂载
```

三层分工原则：**系统层管稳定（不休眠、固定 IP、共享协议），容器层管服务（可迁移、易维护），原生层管性能（硬解、串流）**。

---

## 二、硬件选型与储存拓扑

### 2.1 配件需求 [[01:00]](https://www.youtube.com/watch?v=XN0AeV355BA&t=60s)

| 配件 | 必备能力 | 原因 |
|------|---------|------|
| 多功能拓展坞 | HDMI 输出 + 千兆实体网口 + USB-PD 供电 | 4K 画面输出、局域网稳定带宽（Wi-Fi 不稳定）、持续供电不断电 |
| 多盘位硬盘坞 | 2×3.5" SATA + 1× NVMe M.2 | 机械盘放媒体/照片冷数据，NVMe 放数据库等热数据 |

视频实测配置（smzdm 文字版记载）：

| 部件 | 型号 | 角色 |
|------|------|------|
| 硬盘坞 | 奥睿科（ORICO）双盘位坞 | 2×3.5" SATA + 1×2280 NVMe，全铝合金散热 |
| 机械硬盘 | 希捷酷狼 4TB ×2 | NAS 场景 7×24 设计 |
| NVMe SSD | 三星 980 Pro 2TB | 热数据/缓存 |

### 2.2 储存格式选择 [[02:27]](https://www.youtube.com/watch?v=XN0AeV355BA&t=147s)

| 硬盘类型 | 推荐格式 | 理由 |
|---------|---------|------|
| 机械硬盘（HDD） | Mac OS 扩充格式（日誌式）/ HFS+ Journaled | 机械盘随机写慢，日志式保证断电一致性；APFS 的 CoW 写入放大对 HDD 不友好 |
| 固态硬盘（SSD/NVMe） | APFS | 原生为 SSD 设计，快照/克隆/空间共享效率高 |

macOS 磁盘工具里可对双机械盘建软件 RAID（镜像 RAID 1 保障数据安全）。

```
磁盘工具 → 显示所有设备 → 选中顶层设备 → 分区/抹掉
  ├─ HDD-1 + HDD-2 → RAID 组（RAID 1 镜像）→ 格式: Mac OS 扩充格式（日誌式）
  └─ NVMe → APFS 卷宗
```

### 2.3 与替代方案对比

| 维度 | 多盘位硬盘坞（本方案） | 单盘位 SSD 硬盘盒 | 成品 NAS（群晖/威联通） |
|------|---------------------|------------------|----------------------|
| 单位容量成本 | 低（机械盘为主，文字版称降约 63%） | 高 | 中 |
| 7×24 适配 | 好（NAS 盘 + 铝合金散热） | 一般（消费级 SSD） | 最佳 |
| 功耗 | 低（笔记本 + 坞） | 最低 | 中 |
| 系统维护 | 需自行配置 macOS | 同左 | 厂商套件省心 |
| 数据冗余 | 软件 RAID | 无 | 硬件/软件 RAID |

---

## 三、macOS 服务器底层环境

### 3.1 防休眠配置 [[02:46]](https://www.youtube.com/watch?v=XN0AeV355BA&t=166s)

MacBook 做服务器的头号大敌：合盖/闲置后进入深度睡眠，容器全部断线。双重防护：

**第一重：系统设置**

```
系统设置 → 锁定屏幕/能源 → 
  ✅ 开启「使用电源适配器时，防止显示器关闭时自动睡眠」
  （Prevent automatic sleeping on power adapter when the display is off）
```

**第二重：caffeinate 守护进程（开机自启）**

```bash
# 临时防睡眠（测试用）：caffeinate -s 保持磁盘不休眠
caffeinate -s &

# 长期方案：创建 LaunchAgent 开机自启
cat > ~/Library/LaunchAgents/com.user.caffeinate.plist << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN"
  "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
  <key>Label</key>
  <string>com.user.caffeinate</string>
  <key>ProgramArguments</key>
  <array>
    <string>/usr/bin/caffeinate</string>
    <string>-s</string>
  </array>
  <key>RunAtLoad</key>
  <true/>
  <key>KeepAlive</key>
  <true/>
</dict>
</plist>
EOF

launchctl load ~/Library/LaunchAgents/com.user.caffeinate.plist
```

`caffeinate -s` 参数语义：仅在接通电源时声明系统睡眠禁止（`-s` = prevent system sleep while on AC power），合盖仍可休眠显示但系统服务不断。

> [!warning] 合盖行为
> M1 MacBook Air 合盖默认会睡眠（Clamshell mode 需外接电源 + 外接显示器才保持运行）。本方案接了拓展坞 HDMI 输出到电视，满足外接显示器条件。若不接 HDMI，建议保持开盖使用或外接任意显示器。

文字版声称实测连续运行 72 小时未意外休眠（AI 摘要数据，未独立验证）。

### 3.2 固定内网 IP [[03:41]](https://www.youtube.com/watch?v=XN0AeV355BA&t=221s)

两种方式任选其一：

| 方式 | 操作 | 适用 |
|------|------|------|
| 路由器绑定（推荐） | 路由器后台 → DHCP 静态租约，把 MacBook 的 MAC 地址绑定到固定 IP（如 192.168.100.111） | 不动主机配置，换机器只需改绑定 |
| 手动静态 IP | 系统设置 → 网络 → 以太网（USB 网卡）→ 详细信息 → TCP/IP → 手动配置 | 无路由器权限时 |

固定 IP 是后续一切的基础：SMB 挂载地址、容器端口访问、Remote for Mac 连接目标都依赖它。

### 3.3 文件共享与远程桌面 [[04:20]](https://www.youtube.com/watch?v=XN0AeV355BA&t=260s)

```
系统设置 → 通用 → 共享
  ✅ 文件共享（File Sharing, SMB）
     → 共享文件夹：添加外接硬盘挂载点
     → 用户权限：仅授权专用账户
  ✅ 远程管理 / 屏幕共享（Screen Sharing / Remote Management）
     → 允许指定用户图形化远程控制
```

配置后局域网内其他设备：

- **macOS/Windows**：`smb://192.168.100.111` 直接挂载外接硬盘为网络磁盘
- **另一台 Mac**：访达侧栏 → 共享的 → 屏幕共享，免接显示器图形化维护

---

## 四、容器化服务与智慧生态

### 4.1 为什么选 OrbStack [[05:02]](https://www.youtube.com/watch?v=XN0AeV355BA&t=302s)

| 维度 | OrbStack | Docker Desktop | Colima |
|------|----------|---------------|--------|
| 资源占用 | 极低（轻量虚拟化） | 重（Electron + VM） | 低 |
| macOS 集成 | 深（文件共享原生快） | 一般 | 一般 |
| ARM 原生 | ✅ Apple Silicon 原生（非 Rosetta 模拟） | ✅ | ✅ |
| 容器/机器管理 | 容器 + Linux 虚拟机一体 | 仅容器 | 仅容器 |
| 免费可用 | ✅ 个人免费 | 需订阅（大企业） | ✅ 开源 |

配合 **DPanel**（国产中文容器管理面板，默认端口 `8807`）降低命令行门槛：

```bash
# DPanel 安装（Docker 方式）
docker run -d \
  -p 8807:8088 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v dpanel:/dpanel \
  dpanel/dpanel:latest
# 浏览器访问 http://192.168.100.111:8807
```

### 4.2 端口规划总表

| 服务 | 端口 | 用途 | 部署方式 |
|------|------|------|---------|
| DPanel | 8807 | 容器管理面板 | Docker |
| Home Assistant | 8123 | 智能家居中枢 | Docker |
| Immich | 2283 | 照片备份/相簿 | Docker Compose |
| Jellyfin | 8096 | 影视媒体库 | macOS 原生 |
| qBittorrent | 8080（默认 Web UI） | 远程下载 | macOS 原生 |

原则：**数据库类/更新频繁的服务放容器（易迁移重建），依赖硬件编解码的放原生（性能直通）**。

### 4.3 Home Assistant [[06:47]](https://www.youtube.com/watch?v=XN0AeV355BA&t=407s)

```bash
docker run -d \
  --name homeassistant \
  --privileged \
  --restart=unless-stopped \
  -e TZ=Asia/Shanghai \
  -v /Volumes/HDD-RAID/homeassistant:/config \
  --network=host \
  homeassistant/home-assistant:stable
# 浏览器访问 http://192.168.100.111:8123
```

要点：
- 时区必须 `TZ=Asia/Shanghai`，否则自动化时间全部错位
- 配置目录挂载到机械硬盘分区，容器重建配置不丢
- 通过 **HACS**（Home Assistant Community Store）集成 Apple TV、Nanoleaf、米家网关等插件，把非 HomeKit 设备统一桥接进 Apple 生态
- 远程访问可用 Cloudflare Tunnel，无需公网 IP

### 4.4 Immich 相簿 [[07:17]](https://www.youtube.com/watch?v=XN0AeV355BA&t=437s)

开源的照片/视频自托管方案（Google Photos 替代）。macOS + OrbStack 部署（综合东东博客实测流程）：

```bash
# 1. 创建目录并下载官方配置
mkdir -p ~/immich-app && cd ~/immich-app
wget -O docker-compose.yml https://github.com/immich-app/immich/releases/latest/download/docker-compose.yml
wget -O .env https://github.com/immich-app/immich/releases/latest/download/example.env

# 2. 编辑 .env，把照片存储指向外接大容量硬盘
#    UPLOAD_LOCATION=/Volumes/HDD-RAID/photo_backup
#    DB_DATA_LOCATION=./postgres        (数据库放本地 SSD，性能更好)

# 3. 启动
docker compose up -d
# 浏览器访问 http://192.168.100.111:2283，注册首个用户（即管理员）
```

手机端装 Immich App，开启自动备份即取代 iCloud/Google Photos 付费方案。

**日常运维**：

```bash
# 更新（Immich 平均一周一小版本，更新前先比对官方最新 docker-compose.yml 差异）
docker compose pull && docker compose up -d

# 数据库备份
docker exec -t immich_postgres pg_dumpall -c -U postgres | gzip > immich-dump.sql.gz

# 批量导入存量照片（CLI，需 Node.js ≥ v20）
npm i -g @immich/cli
immich login http://192.168.100.111:2283 <API_KEY>
immich upload --recursive --album --skip-hash /path/to/photos
```

**版本演进要点**（按需启用）：v1.106+ 支持中文界面；v1.113+ 支持文件夹视图浏览；v1.120+ 内置数据库备份功能（管理后台直接开启）。

> [!warning] OrbStack + SMB 挂载坑（重要）
> 不要把 **SMB 网络共享挂载点**直接作为容器 volume 路径——OrbStack 早期版本（issue #822，2023-11）会整个挂死需重启。正确做法：本方案中外接硬盘是**直连 USB** 挂载到 `/Volumes/...`，属于本地磁盘，无此问题；`UPLOAD_LOCATION` 指向本地挂载点即可。若真要从网络共享读照片进 Immich，先拷到本地盘再导入（External Library）。

---

## 五、影音中心与客厅机顶盒交互

### 5.1 媒体库服务端 [[08:34]](https://www.youtube.com/watch?v=XN0AeV355BA&t=514s)

Jellyfin/Plex 服务端**直接装 macOS 原生版**（非 Docker），M1 硬解码直通：

| 维度 | Jellyfin | Plex | Emby |
|------|----------|------|------|
| 开源 | ✅ 完全开源 | ❌ 闭源（核心免费） | ❌ 部分闭源 |
| 硬解转码 | ✅ 免费 | 需 Plex Pass（付费） | 需订阅 |
| 客户端 | Infuse 等 | 全平台官方 | 全平台 |
| 刮削器 | 可换豆瓣源（中文匹配好） | 默认 TMDB | 可换 |
| 默认端口 | 8096 | 32400 | 8096 |

视频选 Jellyfin + 豆瓣刮削源（中文元数据匹配更准），配合 **qBittorrent** 的 Web UI 远程建下载任务，下载目录直指媒体库 watched 文件夹，入库全自动。

```
手机 qBittorrent App / 浏览器
        │ 添加种子/磁力
        ▼
qBittorrent (Web UI :8080) ──下载──▶ /Volumes/HDD-RAID/Media/Movies
                                          │ 监控新文件
                                          ▼
Jellyfin (:8096) ──刮削──▶ 豆瓣元数据 + 封面
                                          │
        ┌─────────────────────────────────┤
        ▼                                 ▼
  电视端 Infuse/App                手机 Infuse 串流
  (M1 HDMI 4K 输出)               (局域网直接拉流)
```

### 5.2 手机遥控：Remote for Mac [[09:40]](https://www.youtube.com/watch?v=XN0AeV355BA&t=580s)

MacBook 接电视后缺交互手段，用手机 App「Remote for Mac」补齐（四页式交互）：

| 页面 | 功能 | 对应场景 |
|------|------|---------|
| 首屏 | 媒体控制（播放/暂停/音量） | 看片中途控制 |
| 次屏 | 触控板（模拟鼠标手势） | 通用导航 |
| 三屏 | 键盘输入 | 搜索/输密码 |
| 四屏 | App 快捷栏 | 一键切换爱优腾/B站/Infuse |

核心价值：**爱奇艺/Bilibili 等网页或客户端全屏后，手机左右滑动秒切 App**，MacBook 变成「免电视会员的高性能电视盒」——电视端 App 的会员墙被绕过（用 Web 端/桌面端账号体系），且 M1 解码能力远超普通电视盒子。

文字版声称 Infuse 播 4K H.265 时 M1 功耗约 11W、表面温度 42.3℃（AI 摘要数据，量级合理但未独立验证）。

---

## 六、决策树与避坑清单

### 6.1 什么情况适合这套方案

```
手里有闲置 MacBook / Mac mini？
  │
  ├─ 只是想要 NAS（文件共享）
  │    → 最简路径：SMB 共享 + 固定 IP + 防休眠，完事
  │
  ├─ 想要 NAS + 照片备份
  │    → 上面 + OrbStack + Immich（Docker Compose）
  │
  ├─ 想要 NAS + 照片 + 智能家居
  │    → 上面 + Home Assistant 容器（HACS 桥接 HomeKit）
  │
  └─ 全家桶（NAS + 照片 + 智能家居 + 影音机顶盒）← 本视频
       → 上面 + Jellyfin 原生 + qBittorrent + Remote for Mac + HDMI 接电视
```

### 6.2 避坑清单

- ✅ 先做防休眠 + 固定 IP，再部署任何服务（顺序反了会反复断连排查到崩溃）
- ✅ 机械盘 HFS+ 日志式、SSD 用 APFS（别对 HDD 用 APFS，写入放大伤盘）
- ✅ 数据库类容器数据放 SSD（postgres/pgdata），照片视频放 HDD
- ❌ 不要把 SMB 网络挂载点直接当容器 volume（OrbStack 挂死坑）
- ❌ 不要用 Wi-Fi 跑 NAS 流量（拓展坞实体网口是为稳定带宽）
- ✅ Immich 升级前 diff 官方 docker-compose.yml，配置漂移是升级翻车主因
- ✅ MacBook 合盖想运行：需外接电源 + 外接显示器（本方案 HDMI 接电视正好满足）
- ✅ 远程访问（出门在外）用 Cloudflare Tunnel / Tailscale，不要裸暴露端口到公网

---

## 参考资料

- [视频：闲置 Macbook Air 变身 NAS+高清机顶盒，终结配置教程（梅有机）](https://www.youtube.com/watch?v=XN0AeV355BA)
- [smzdm 文字版：闲置Macbook Air变身 NAS+高清机顶盒](https://post.smzdm.com/p/a65xnr0e)（AI 生成摘要，含硬件型号与端口细节）
- [macOS 部署体验 immich 相册 — 东东's Blog](https://blog.yasking.org/a/macos-deploy-immich)（OrbStack + Immich 完整部署/备份/CLI 流程）
- [OrbStack issue #822: Network shares as volumes 挂死问题](https://github.com/orbstack/orbstack/issues/822)
- [Immich 官方文档](https://immich.app/docs/)

## 相关笔记

- [[Home Assistant]]
- [[OrbStack]]
- [[Jellyfin]]
- [[Immich]]

---

*文档生成时间：2026-09-01*
*基于视频 XN0AeV355BA（2026-02-03）+ 三份外部资料交叉整理*
