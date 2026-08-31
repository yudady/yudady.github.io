---
title: 旧平板变身低功耗 Linux 家庭服务器（Ubuntu 24.04）十件事全流程
aliases: [TechShrimp Linux服务器, 平板改服务器, 低功耗家用服务器]
tags:
  - linux-server
  - self-hosted
  - docker
  - home-assistant
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=6pH3Uma8LlY"
  - "https://github.com/tech-shrimp/me/blob/master/doc/240502.md"
author: 技术爬爬虾 TechShrimp
created: 2026-08-31
updated: 2026-08-31
description: 旧低配平板装 Ubuntu 24.04 后必做的十件事：静态 IP、RustDesk+SSH 双远程、Samba、Docker、Home Assistant、小雅 AList、钉钉 AI 机器人，实测 3W 功耗 7x24 运行。含 2026 年过时项勘误。
note: 无字幕（创作者禁用），基于官方文字版 + 视频大纲 + 外部源交叉验证整理
level: beginner
stars: 4
---

# 旧平板变身低功耗 Linux 家庭服务器（Ubuntu 24.04）十件事全流程

> 技术爬爬虾 TechShrimp 2024-05-02 实操视频的文字沉淀。核心场景：一台 1.6GHz 低配、跑 Windows 极度卡顿的旧平板，刷 Ubuntu 24.04 后按「十件事」清单配置成无头家用服务器，同时承担文件中心（Samba）、智能家居枢纽（Home Assistant）、影音聚合（小雅 AList）、AI 对话机器人（钉钉 + 通义千问）四个角色，熄屏功耗仅 3W。
>
> 本笔记已按 2026-08 现状对视频中的命令做交叉验证，**5 处已过时会报错的命令**在 [2026 复刻勘误](#2026-年复刻勘误视频命令-vs-现状) 一节汇总，照抄视频命令会翻车的先看那一节。

## 目录

- [[#总览：十件事的依赖顺序]]
- [[#一、基础系统与环境适配]]
- [[#二、双模远程管理：RustDesk + SSH]]
- [[#三、本地生态与文件共享：微信 + Samba]]
- [[#四、容器化基础设施：Docker]]
- [[#五、Home Assistant 智能家居枢纽]]
- [[#六、影音聚合与 AI 扩展：小雅 AList + 钉钉机器人]]
- [[#七、负载与功耗实测]]
- [[#2026 年复刻勘误（视频命令 vs 现状）]]
- [[#判断决策树：什么设备适合这么玩]]
- [[#参考资料]]

---

## 总览：十件事的依赖顺序

十件事不是并列清单，有严格的依赖顺序——先保证「连得进、找得到」，再谈「跑服务」：

```
┌─────────────────────────────────────────────────────┐
│ Phase 1  基础适配（不做好后面全白搭）                  │
│   vim 安装 → 路由器绑定静态 IP → Wayland 切 Xorg      │
└──────────────────────┬──────────────────────────────┘
                       ▼
┌─────────────────────────────────────────────────────┐
│ Phase 2  远程通道（从此拔掉键鼠塞进柜子）              │
│   RustDesk 图形通道（IP 直连）+ OpenSSH 命令通道      │
└──────────────────────┬──────────────────────────────┘
                       ▼
┌─────────────────────────────────────────────────────┐
│ Phase 3  本地服务                                     │
│   Linux 微信 · Samba 局域网文件共享                   │
└──────────────────────┬──────────────────────────────┘
                       ▼
┌─────────────────────────────────────────────────────┐
│ Phase 4  容器化全家桶（全部跑在 Docker 上）            │
│   Home Assistant · 小雅 AList · 钉钉 AI 机器人       │
└─────────────────────────────────────────────────────┘
```

> [!info] 视频信息
> - 频道：技术爬爬虾 TechShrimp（2024-05-02，12:20，5.3 万播放）
> - 系列上期：给平板刷 Ubuntu 24.04 系统（B 站 BV1Gb421a7BW）
> - 官方文字版：github.com/tech-shrimp/me/blob/master/doc/240502.md

---

## 一、基础系统与环境适配

对应视频 00:26 - 02:34：vim、静态 IP、Xorg 三件套。看似琐碎，但每一项都直接决定远程维护是否成立。

### 1.1 文字编辑器：vi → vim

Ubuntu Server 精简环境只带 vi，方向键会乱码、无语法高亮，改配置文件非常痛苦：

```bash
sudo apt update
sudo apt install vim
```

| 对比项 | vi（自带） | vim（apt 安装） |
|--------|-----------|----------------|
| 方向键插入模式 | 输出乱码 ABC | 正常移动 |
| 语法高亮 | 无 | 有 |
| 后续所有教程的 `vi /etc/...` | 能用但折磨 | 流畅 |
| 必要性 | — | 十件事的地基 |

### 1.2 路由器绑定静态 IP（关键第一步）

IP 漂移是家庭服务器断连的头号原因——DHCP 重新分配后，SSH/RustDesk/Samba 全部失联。做法是在**路由器端**做 DHCP 静态绑定（MAC 地址 → 固定 IP），而不是只在系统里改：

```
路由器管理页 → 局域网设置 → DHCP 静态 IP 设置
    └─ 平板 MAC 地址  →  绑定 192.168.31.207（示例）
    └─ 保存后重启路由器生效
```

| 方案 | 优点 | 缺点 |
|------|------|------|
| 路由器端 DHCP 绑定（视频做法） | 集中管理、重装系统不丢配置、全家设备可统一管 | 需要路由器管理权限 |
| 系统内 netplan 写死 IP | 不依赖路由器 | 换网络环境即失联，重装丢失 |

- ✅ 在路由器端绑定，所有固定设备的 IP 管理集中在一处
- ✅ 绑定后重启路由器验证一次 IP 不变
- ❌ 不要在系统里 netplan 写死——平板以后换环境就废了

### 1.3 Wayland 切 Xorg（远程唤醒的前提）

Ubuntu 24.04 桌面默认 Wayland，**熄屏状态下远程软件无法唤醒屏幕**——这是远程桌面「连上但黑屏」的根因。改回 Xorg：

```bash
sudo vim /etc/gdm3/custom.conf
# 找到  #WaylandEnable=false  这一行
# 去掉行首注释符号 #，变成：
#   WaylandEnable=false
# ESC 后输入 :wq! 保存

sudo systemctl restart gdm3   # 重启显示管理器，立即生效
```

| 显示协议 | 远程唤醒熄屏 | 触控适配 | 多分数缩放 | 本场景结论 |
|----------|--------------|----------|------------|------------|
| Wayland（默认） | ❌ 唤醒失败 | 好 | 好 | 必须关 |
| Xorg（改配置） | ✅ 正常 | 一般 | 一般 | 服务器场景首选 |

---

## 二、双模远程管理：RustDesk + SSH

对应视频 01:41 - 04:10。无头化（Headless）的前提是两条独立远程通道：图形桌面走 RustDesk，命令行走 SSH，互为备份。

### 2.1 双通道架构

```
                    家局域网内的控制端（手机/PC）
                       │                    │
        图形通道        │                    │      命令通道
   RustDesk IP 直连    │                    │    ssh user@192.168.31.207
   192.168.31.207      │                    │
   :21118              │                    │
                       ▼                    ▼
              ┌─────────────────────────────────┐
              │  平板服务器（柜子里，无键鼠无屏） │
              │  RustDesk 常驻 + sshd 常驻       │
              └─────────────────────────────────┘
   图形通道用途：看桌面、调试 GUI 程序、应急救砖
   命令通道用途：日常运维、scp 传文件、跑脚本，开销几乎为零
```

| 对比项 | RustDesk | OpenSSH |
|--------|----------|---------|
| 交互形式 | 完整图形桌面 | 纯终端 |
| 资源开销 | 较高（编码渲染） | 极低 |
| 依赖 Xorg 修复 | 是 | 否 |
| 内网延迟 | 直连后极低 | 极低 |
| 使用时机 | 装软件、看微信、救急 | 90% 日常运维 |

### 2.2 RustDesk 安装与两项关键设置

```bash
# 从 github.com/rustdesk/rustdesk/releases 下载 deb（视频时 1.2.3，2026-08 已 1.4.9）
sudo apt install ./rustdesk-1.2.3-2-x86_64.deb
```

装完必改两处（设置 → 安全）：

1. **允许 IP 直接访问**：勾选后内网连接不再绕中继服务器，延迟和带宽大幅改善。直连端口默认 21118（官方文档确认此默认值至今不变），主控方直接输入 `192.168.31.207` 连接
2. **固定密码**：默认随机密码每次都变，设固定密码主控方才能随时连入

```bash
# 验证直连端口在监听
ss -tlnp | grep 21118
```

### 2.3 OpenSSH 服务端

Ubuntu 桌面版只带 SSH 客户端，没有服务端，需补装：

```bash
sudo apt install openssh-server    # 视频文字版笔误写成了 "apt openssh-server"
sudo systemctl enable --now ssh    # 开机自启（视频未提，服务器必做）
```

- ✅ `enable --now` 一次性解决「重启后连不上」
- ✅ RustDesk 和 SSH 各留一条通道，任一软件翻车都有后路
- ❌ 只装一条通道就拔键鼠——图形会话卡死时没有命令行救援

两条通道都验证通过后，拔掉键盘鼠标、关屏、进柜子，设备正式无头化。

---

## 三、本地生态与文件共享：微信 + Samba

对应视频 04:27 - 05:02。这一层让服务器在局域网内「有存在感」：手机电脑电视都能直接读写它的硬盘。

### 3.1 Linux 原生微信

视频时点（2024-05）腾讯尚未发布官方 Linux 版，UP 主借用国产系统 openKylin 的 deb 包：

```bash
# 2024 年方案（已过时，仅存档）
wget https://software.openkylin.top/openkylin/yangtze/pool/all/wechat-beta_1.0.0.238_amd64.deb
sudo apt install ./wechat-beta_1.0.0.238_amd64.deb
```

> [!warning] 2026 现状：不用再绕 openKylin
> 腾讯已于 2024 年底官宣微信 Linux 官方版（4.x，基于 Qt 重构，功能对齐桌面端），x86/Arm/龙芯架构，提供 deb/rpm/AppImage 三种格式，直接从 **linux.weixin.qq.com** 下载安装即可。详见勘误表。

### 3.2 Samba 文件共享

把服务器目录暴露给局域网，手机/电脑/电视盒子直接像访问本地盘一样读写：

```bash
sudo apt install samba
sudo vim /etc/samba/smb.conf
```

视频给出的共享段（已过时写法，会报错，正确版见勘误表）：

```ini
[ubuntu_smb]
path = /home/tech-shrimp
available = yes
browseable = yes
public = yes
writable = yes
create mask = 0755
security = share          # ← Samba 4.15 起已移除该模式，Ubuntu 24.04 直接报错
force user = tech-shrimp
force group = tech-shrimp
```

```bash
sudo service smbd restart
```

| 访问端 | 填写地址 | 典型用途 |
|--------|----------|----------|
| Windows 资源管理器 | `\\192.168.31.207\ubuntu_smb` | 电脑直接拖文件 |
| macOS Finder | `smb://192.168.31.207/ubuntu_smb` | 同上 |
| Android 文件管理器 | SMB/局域网 → 添加 `192.168.31.207` | 手机看服务器电影 |
| 电视盒子 | 自带播放器扫 SMB | 客厅直接放片 |

- ✅ `force user` 保证了来宾写入的文件属主统一，避免权限混乱
- ✅ 共享整个 home 目录对家用省事；更严谨做法是单独建 `/srv/share` 只放影音
- ❌ `public = yes` 的来宾可写共享别暴露到公网——只能待在纯内网

---

## 四、容器化基础设施：Docker

对应视频 06:31。视频的核心方法论主张：**后续所有服务一律 Docker 部署**。

### 4.1 为什么容器化

```
传统安装                          Docker 部署
──────────                        ──────────
apt install app                   docker run app
  ├─ 依赖装进系统                    ├─ 依赖锁在镜像里
  ├─ 版本被发行版绑架                ├─ 版本随镜像走，随便升级回滚
  ├─ 卸载残留配置                    ├─ 删容器即卸干净
  └─ 换机器重头配                    └─ compose 文件复制即迁移
```

UP 主的原话逻辑：「只要别人那里能跑的程序，到你机器上一定也能跑」——对低配旧硬件尤其重要，发行版自带的库版本老，源码编译装不动是常态。

### 4.2 安装命令（视频版已失效，两个都给出）

视频 2024-05 使用阿里云镜像源 + `apt-key`：

```bash
# 视频原命令 —— apt-key 在 Ubuntu 24.04 已移除，第三行直接报错（仅存档）
sudo apt-get install ca-certificates curl
curl -fsSL http://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] http://mirrors.aliyun.com/docker-ce/linux/ubuntu $(lsb_release -cs) stable"
sudo apt-get install docker-ce docker-ce-cli containerd.io
```

```bash
# 2026 正确写法（官方 keyring 方式，国内可保留阿里云源）
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | \
  sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/docker.gpg] \
  https://mirrors.aliyun.com/docker-ce/linux/ubuntu $(lsb_release -cs) stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io
```

- ✅ 新增第三方源一律用 `signed-by=` + `/etc/apt/keyrings/` 指定密钥
- ❌ 任何教程出现 `apt-key add` 都已过时（24.04 起命令不存在）

---

## 五、Home Assistant 智能家居枢纽

对应视频 07:15。Home Assistant（HA）是开源智能家居聚合平台，把各品牌各协议的设备统一到一个本地中枢管理，不依赖厂商云。

### 5.1 在这台服务器上的角色

```
 米家设备      涂鸦设备      HomeKit 设备    其他品牌
    │            │             │              │
    └────────────┴──────┬──────┴──────────────┘
                        ▼
            ┌─────────────────────────┐
            │ Home Assistant 容器      │
            │ Docker · 端口 8123       │
            │ · 设备状态聚合           │
            │ · 自动化联动             │
            │ · 可接 AI 大模型         │
            └────────────┬────────────┘
                         ▼
              手机浏览器 / 钉钉机器人
             （第六节的 AI 通道从这里取数据）
```

### 5.2 部署命令（至今仍是官方标准写法）

```bash
# 创建配置目录
mkdir -p /home/tech-shrimp/homeassistant

sudo docker run -d \
  --name homeassistant \
  --privileged \
  --restart=unless-stopped \
  -e TZ=Asia/Shanghai \
  -v /home/tech-shrimp/homeassistant:/config \
  -v /run/dbus:/run/dbus:ro \
  --network=host \
  ghcr.io/home-assistant/home-assistant:stable
```

浏览器访问 `http://192.168.31.207:8123` 进入初始化向导。

| 参数 | 作用 | 为什么这么写 |
|------|------|--------------|
| `--privileged` | 容器获得特权 | HA 需访问底层硬件（蓝牙、USB）发现设备 |
| `--network=host` | 直接用宿主机网络栈 | mDNS 设备发现必须同网段，NAT 模式发现不了 |
| `-v /run/dbus` | 挂载系统总线 | DBus 是与系统服务通信的通道 |
| `--restart=unless-stopped` | 崩溃/重启自动拉起 | 无人值守服务器的生命线 |
| `-e TZ=Asia/Shanghai` | 时区 | 自动化按本地时间触发的前提 |

- ✅ `--network=host` 是 HA 官方容器推荐的网络模式，别改成 `-p 8123:8123`（会破坏发现功能）
- ✅ 配置目录独立挂载，日后换机器 `docker run` 一条命令 + 拷贝目录即完成迁移

---

## 六、影音聚合与 AI 扩展：小雅 AList + 钉钉机器人

对应视频 08:35 - 10:23。这两个服务把服务器从「工具机」升级为「家庭成员」：一个管影音，一个管对话。

### 6.1 小雅 AList：数百 TB 影音聚合库

小雅 AList 是基于 AList 的阿里云盘共享资源聚合站（聚合约 391 个共享盘、28 万+资源条目），部署后通过「转存」机制按需把资源落到自己网盘观看。

```
准备三样凭据                        一键部署
──────────────                     ──────────
① 短 Token                         sudo bash -c "$(curl \
   alist.nn.ci 阿里云盘驱动页           http://docker.xiaoya.pro/update_new.sh)" \
   扫码获取                        └─ -s host
② 长 Token
   alist.nn.cn/tool/aliyundrive    访问 http://服务器IP:5678
   request 页面获取
③ 转存目录 File ID
   转存共享目录到自己网盘
   → 浏览器 URL 里
   /drive/file/resource/xxx
   这串就是 File ID
```

| 对比项 | 小雅 AList | 自建 AList + 自有网盘 |
|--------|-----------|----------------------|
| 资源量 | 开箱数百 TB 聚合库 | 只有自己网盘的内容 |
| 维护成本 | 脚本自动更新镜像 | 手动配驱动、token |
| Token 依赖 | 需定期更新阿里云盘 token | 同样需要 |
| 适合 | 影音消费者 | 隐私敏感的资料管理 |

> [!warning] 2026 现状
> 阿里云盘主域名已由 `aliyundrive.com` 迁移到 **alipan.com**（视频及文字版中的旧链接会跳转或失效），获取 File ID 的操作要在 alipan.com 域下进行。小雅项目仍在活跃维护（GitHub: xiaoyaDev/xiaoya-alist），另注意阿里云盘对第三方 token 的接口政策有收紧趋势，部署前先看项目 README 最新说明。

### 6.2 钉钉机器人 + 通义千问：免公网 IP 的 AI 管家

视频方案：Docker 部署开源框架 **chatgpt-on-wechat**，后端接阿里通义千问（Qwen）API，前端用钉钉企业机器人收发消息。

```
家庭成员在钉钉群 @机器人
        │
        ▼
钉钉服务器 ──回调──▶ chatgpt-on-wechat 容器（服务器上）
        │              │        │
        │              │        └─ 调 Home Assistant API 查/控设备
        │              └─ 调通义千问 API 生成回复
        ▼
钉钉服务器 ◀──回复── 容器 ──▶ 「帮我打开客厅灯」→ 灯亮 + 语音回执
```

选钉钉而非微信/公网方案的理由：

| 通道 | 需要公网 IP？ | 合规风险 | 接入难度 |
|------|--------------|----------|----------|
| 钉钉企业机器人 | ❌ 不需要（Outgoing 回调可走内网穿透替代） | 低 | 低 |
| 微信个人号（itchat 类） | ❌ | 高（封号） | 中 |
| 自建 Web 前端 | ✅ 需公网或穿透 | 低 | 高 |

> [!info] 详细教程
> UP 主另有两篇文字版专门讲钉钉机器人配置：`gitee.com/tech-shrimp/me` 仓库 `doc/240217.md` 与 `doc/240218.md`，本视频不展开。

---

## 七、负载与功耗实测

对应视频 11:11 - 11:58。这是整个方案的立论基础：同硬件，Windows 卡死，Linux 游刃有余。

### 7.1 资源占用对比（同一台 1.6GHz 平板）

| 指标 | Windows（改造前） | Ubuntu 24.04（改造后） |
|------|-------------------|------------------------|
| 日常状态 | 极易满载卡死 | 浏览器+微信+RustDesk+HA+AList 同开 |
| CPU 空闲 | 逼近 0% | **>50% 空闲** |
| 内存占用 | 系统自身吃掉大半 | 全部服务合计约 **3GB 出头** |
| 体验结论 | 不可用 | 余量充足 |

原因不神秘：Windows 桌面栈（Defender、更新服务、遥测、图形特效）对 1.6GHz 老芯片是重负；Ubuntu 无桌面负载 + 服务全容器化后，每个进程只做分内事。

### 7.2 功耗实测（智能插座）

| 状态 | 功耗 | 对比参考 |
|------|------|----------|
| 熄屏服务器态 | **3W** | 传统台式机待机通常 30-60W |
| 亮屏使用态 | **8W** | 笔记本亮屏约 15-30W |
| 7x24 年电费（3W 计） | 约 0.7 度/月 | ≈ 电费几毛钱的水平 |
| 风扇 | 无（平板被动散热） | 台式机 2-4 个风扇 |

- ✅ 平板天生是服务器好苗子：被动散热（零噪音）、自带电池（意外断电 UPS）、低功耗 SoC
- ❌ 别指望它跑重负载（转码、编译）——定位是轻量 7x24 常驻服务

---

## 2026 年复刻勘误（视频命令 vs 现状）

视频发布于 2024-05，以下 5 处照抄会翻车，均已对照官方文档/GitHub release 验证：

| # | 视频内容（2024-05） | 2026-08 现状 | 影响 |
|---|---------------------|--------------|------|
| 1 | Docker 安装用 `apt-key add` | **apt-key 已从 Ubuntu 24.04 移除**，命令直接报错 | 改用 `/etc/apt/keyrings/` + `signed-by=` 方案（见 4.2 节正确命令） |
| 2 | Samba 配置 `security = share` | **Samba 4.15 起该模式已删除**，Ubuntu 24.04（Samba 4.19）解析报错 | 删掉这行；来宾访问改用 `map to guest = Bad User`（全局段）+ `guest ok = yes` |
| 3 | 微信用 openKylin 的 wechat-beta deb | 腾讯官方 Linux 微信 4.x 已发布（linux.weixin.qq.com），x86/Arm/龙芯、deb/rpm/AppImage 齐全 | 直接装官方版，无需第三方包 |
| 4 | 阿里云盘域名 `aliyundrive.com` | 主域名已迁移至 **alipan.com**，旧链接失效或跳转 | 获取 File ID / 转存操作在 alipan.com 下进行 |
| 5 | RustDesk 1.2.3 deb | 当前 1.4.9；「允许 IP 直接访问」默认端口 21118 语义不变 | 装新版即可，配置路径与端口语义与视频一致 |

不受影响、可放心照抄的部分：vim 安装、路由器静态 IP 思路、Xorg 切换（`/etc/gdm3/custom.conf`）、openssh-server、Home Assistant 的 docker run（官方标准写法至今如此）、小雅一键脚本入口（docker.xiaoya.pro）。

---

## 判断决策树：什么设备适合这么玩

```
手头有旧设备想利用？
│
├─ x86 平板/瘦客户机/迷你主机（能装 Ubuntu）
│    ├─ CPU ≥ 1GHz、内存 ≥ 2GB ────────▶ 本方案完美适用（3-8W 常驻）
│    └─ 内存 < 2GB ────────────────────▶ 换轻量方案：装 Debian + 无桌面
│                                         只跑 SSH + Samba，砍掉图形栈
│
├─ ARM 安卓平板
│    └─ 不能装标准 Ubuntu ─────────────▶ Termux / Linux Deploy 路线，
│                                         或刷 postmarketOS（机型支持有限）
│
└─ 老笔记本
     └─ 天然 UPS + 自带屏幕 ───────────▶ 同样适用，功耗 5-15W，
                                          记得 BIOS 里设「开盖不开机」
```

| 需求 | 建议 |
|------|------|
| 只要文件共享 | Samba 就够，Docker 都可以不上 |
| 要智能家居 | HA 必装，且需 `--network=host` |
| 要影音库 | 小雅 AList + 阿里云盘会员（转存空间） |
| 要远程对话机器人 | chatgpt-on-wechat + 任一大模型 API + 钉钉 |
| 全都要 | 本方案全家桶，峰值内存 3GB 出头，老机器扛得住 |

---

## 参考资料

- [视频：低配电脑变身低功耗,功能全的Linux服务器（YouTube）](https://www.youtube.com/watch?v=6pH3Uma8LlY)
- [官方文字版：装完系统必干的十件事（tech-shrimp/me 240502.md）](https://github.com/tech-shrimp/me/blob/master/doc/240502.md)
- [RustDesk 官方文档：高级设置（直接 IP 访问端口）](https://rustdesk.com/docs/zh-cn/self-host/client-configuration/advanced-settings/)
- [RustDesk Releases（版本验证）](https://github.com/rustdesk/rustdesk/releases)
- [微信 Linux 官方版下载](https://linux.weixin.qq.com/)
- [小雅 AList 周边（xiaoyaDev/xiaoya-alist）](https://github.com/xiaoyaDev/xiaoya-alist)
- [钉钉机器人教程（tech-shrimp/me 240217.md）](https://gitee.com/tech-shrimp/me/blob/master/doc/240217.md)
- [apt-key 弃用说明（Ubuntu Discourse）](https://discourse.ubuntu.com/t/apt-key-is-deprecated-and-apt-sources-changed-after-release-upgrade-to-24-04/53548)
- [Home Assistant 官方容器部署](https://www.home-assistant.io/installation/linux)

## 相关笔记

- [[Home Assistant]]
- [[Docker]]
- [[RustDesk]]
