---
title: Tailscale 全面玩法 - 异地组网、自建 DERP 与 Headscale 实战手册
aliases: [Tailscale 异地组网, Tailscale DERP 搭建, Headscale 部署, 韩风Talk Tailscale]
tags:
  - networking
  - tailscale
  - self-hosted
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=mgDpJX3oNvI"
  - "https://tailscale.com/docs/reference/derp-servers"
  - "https://headscale.net/"
author: 韩风Talk
created: 2026-09-09
updated: 2026-09-09
description: 基于韩风Talk 教学视频（2023-07）+ 2026 官方文档交叉验证：三局域网双向互访拓扑（主路由/旁路由/NAS Docker）、纯 IP 双栈 DERP 自建、Headscale 私有化部署，含 2023→2026 技术演进勘误。
level: intermediate
stars: 4
---

# Tailscale 全面玩法 - 异地组网、自建 DERP 与 Headscale 实战手册

> 韩风Talk 频道教学视频（2023-07-05，20:38，8.8 万播放）。一期讲完 Tailscale 从入门到完全私有化的五层玩法：节点授权 → 三局域网双向互访 → Exit Node 全隧道 → 纯 IP 双栈 DERP 自建 → Headscale 私有协调服务器。**注意：视频发布于 2023-07，距今三年多**，部分命令与官方界面已有演进（derper 现有官方 `-certmode manual` 参数、ACL 已更名 tailnet policy file、Headscale 官方立场需澄清），本笔记已逐项对照 2026-09 官方文档勘误，见文末表。核心拓扑思想（子网路由发布 + 静态路由联动实现全设备无感互访）至今仍是标准做法。

## 目录

- [模块一：基础入门与节点授权技巧](#模块一基础入门与节点授权技巧)
- [模块二：三局域网双向互访拓扑](#模块二三局域网双向互访拓扑)
- [模块三：Exit Node 全隧道模式](#模块三exit-node-全隧道模式)
- [模块四：自建纯 IP 双栈 DERP 中转](#模块四自建纯-ip-双栈-derp-中转)
- [模块五：Headscale 私有化部署](#模块五headscale-私有化部署)
- [2023→2026 技术演进勘误](#20232026-技术演进勘误)
- [实施路线决策树](#实施路线决策树)
- [验证与勘误记录](#验证与勘误记录)
- [参考资料](#参考资料)
- [相关笔记](#相关笔记)

---

## 模块一：基础入门与节点授权技巧

**设备入网两种姿势**：

| 系统类型 | 授权方式 | 细节 |
|---------|---------|------|
| GUI 系统（Win/macOS/iOS/Android） | 点击 Log in → 调用系统浏览器 → 第三方账号认证 | 逻辑一致 |
| 无 GUI Linux | 终端 `tailscale up` → 输出授权 URL → 任意浏览器打开登录 | URL 可复制 |

**远程授权技巧**（视频 01:26）：受限环境或移动设备无法跳转认证时，把授权 URL 复制到任意外部电脑浏览器完成授权，设备即绑定上线。本质：授权 URL 与设备绑定，与在哪台浏览器打开无关。

**Disable Key Expiry**（视频 01:40）：节点密钥默认定期过期（需重新登录授权）。作为网关/服务器/NAS 的关键节点，必须在 Admin Console 里对该节点勾选「Disable key expiry」，保证无人值守长期在线。这是所有后续拓扑的前置动作。

✅ 检查清单：每个承担网关职责的节点 → 改名（方便识别）→ 禁用密钥过期 → 开启子网路由审批，三件事一次做完。

---

## 模块二：三局域网双向互访拓扑

视频构建的完整模型：**1 个外部节点 + 3 个局域网**，目标是网段内所有设备（不装客户端）双向互访：

```
                    外部节点（笔记本/手机）
                          │
              ┌───────────┼───────────────────────┐
              │           │                       │
        ┌─────┴────┐ ┌────┴─────┐           ┌─────┴─────┐
        │ 局域网 1  │ │ 局域网 2  │           │  局域网 3  │
        │ 主路由跑  │ │ 旁路由跑  │           │ 群晖 NAS  │
        │ Tailscale│ │ Tailscale│           │ Docker 跑 │
        └──────────┘ └────┬─────┘           └─────┬─────┘
                     主路由加静态路由             主路由加静态路由
                     （下一跳=旁路由）           （下一跳=NAS IP）
```

三种方案的差异本质是「Tailscale 装在哪、谁来当网关」：

| 维度 | 主路由方案 | 旁路由方案 | NAS Docker 方案 |
|------|-----------|-----------|----------------|
| Tailscale 位置 | OpenWrt 主路由本体 | OpenWrt 旁路由 | 群晖 Docker 容器 |
| 子网路由发布 | `--advertise-routes=网段1` | 同（改网段2） | 同（容器内） |
| 主路由是否要改 | 否（自己就是网关） | **要：加静态路由** | **要：加静态路由** |
| 额外关键设置 | 防火墙区域绑定 tailscale0 | 开启 IP 伪装（Masquerading） | 挂载 /dev/net/tun + iptables 转发 + 开机任务 |
| 适用 | 路由器可装插件且愿意动主路由 | 主路由动不了/不想动 | 无路由器权限，只有 NAS |

### 局域网 1：OpenWrt 主路由（视频 02:30）

1. OpenWrt 官方库装 `iptables-nft` + Tailscale 插件（2023 时 Tailscale 守护进程依赖 iptables 规则；OpenWrt 21.02+ 默认 fw4/nftables。**2026 现状见勘误表**）
2. SSH 执行 `tailscale up --advertise-routes=192.168.x.0/24`（换成局域网 1 网段），复制授权 URL 到浏览器登录
3. Admin Console：节点改名 → 禁用密钥过期 → 审批子网路由规则
4. OpenWrt 防火墙配置：
   - 网络 → 接口 → 添加新接口：协议「不配置协议」，设备选 `tailscale0`
   - 给该接口自定义防火墙区域：入站/出站/转发全 Accept
   - 允许转发到目标区域：lan + wan；允许源区域：lan

### 局域网 2：OpenWrt 旁路由（视频 04:04）

1. 旁路由同法安装并 `--advertise-routes=网段2`
2. **确认旁路由「IP 伪装（Masquerading）」开启**——回程流量的关键
3. 局域网 2 的主路由（以 OpenWrt 为例）：网络 → 路由 → 静态 IPv4 路由，逐条添加：
   - 目标 = Tailscale 虚拟网段（100.64.0.0/10 档），网关 = 旁路由 IP
   - 目标 = 局域网 1 网段，网关 = 旁路由 IP
   - 目标 = 局域网 3 网段，网关 = 旁路由 IP

逻辑：局域网 2 内设备要访问任何异地网段，都交给旁路由处理（旁路由既是 Tailscale 节点又做 NAT 回程）。

### 局域网 3：群晖 NAS Docker（视频 05:16）

群晖 7.0 套件版权限受限（视频评价其功能不完善），改用 Docker：

1. SSH 进群晖 → `sudo -i` 提权 → 开启 TUN/Tap（`ls /dev/net/tun` 确认，`insmod` 加载）
2. Docker 跑 Tailscale 容器，关键参数：
   - 挂载 `/dev/net/tun`
   - 环境变量传入 **Auth Key**（Admin Console → Settings → Keys 生成，勾选可复用 Reusable）
   - `--advertise-routes=网段3`，可选 `--advertise-exit-node`（不想做出口就删）
3. Admin Console 审批子网路由 + 禁用密钥过期（同前）
4. NAS 内执行 iptables 转发指令（接口名换成 NAS 本地物理网卡），让局域网 3 其他设备可达
5. 局域网 3 主路由加静态路由（同局域网 2 逻辑，目标含 Tailscale 网段 + 局域网 1/2 网段，下一跳 = NAS IP）
6. **防火墙规则持久化**：群晖控制面板 → 计划任务 → 新增开机触发的任务（用户 root），脚本开头 `sleep 60`（按开机速度微调）再执行 iptables 规则——否则重启即失效

---

## 模块三：Exit Node 全隧道模式

```
节点 A（外地设备）                    节点 B（出口，如家里软路由/境外 VPS）
   全部流量 ════ WireGuard 隧道 ════►  以 B 的网络环境出公网
                                       B 能访问什么，A 就能访问什么
```

配置三步（视频 07:33）：

1. 节点 B：`tailscale up --advertise-exit-node`
2. Admin Console：审批 B 为出口节点
3. 节点 A：客户端里选择 B 作为 Exit Node

典型用途：公共 Wi-Fi 全流量加密防护；借异地（或跨境）网络环境访问资源。设置极简，是 Tailscale 性价比最高的玩法之一。

---

## 模块四：自建纯 IP 双栈 DERP 中转

**为什么自建**：Tailscale 官方 DERP 全在海外，国内打洞失败 fallback 到官方中转时延迟高、带宽受限；自建国内 VPS 上的 DERP 显著降延迟。

**纯 IP 的难点**：官方 derper 默认要求有效 SSL 域名；国内域名要备案。视频的解法（2023）：改源码绕过证书强制 + 自签假域名证书。**2026 已有更优路径，见勘误表。**

视频完整流程（20:38 中的 08:13-14:16 段）：

1. 云服务器装 Go → 拉取 Tailscale 源码
2. 修改 derper 证书校验的 go 文件（注释三行）→ 重新编译 derper
3. 自签一个「假域名」证书（域名仅本地使用不暴露网络）启动 derper
4. systemd 服务化，关键参数：`-a :33445`（HTTPS 端口）、`-http-port`、`--certmode manual`（手动指定证书目录）、STUN 用 3478/udp
5. 防火墙放行：33445/tcp + 3478/udp
6. Admin Console 的 ACL（现名 tailnet policy file）里加 `derpMap` 配置：`OmitDefaultRegions: true` 禁用官方中转 + 填自建服务器 IPv4/端口
7. 验证：`tailscale netcheck`、`tailscale status`、`tailscale ping`（显示 via DERP 即中转生效）

**IPv6 双栈**（视频 11:53-14:16）：纯 IPv4 DERP 会让有公网 IPv6 的节点反而无法直连。两种解法：

| 方案 | 做法 | 评价 |
|------|------|------|
| 借官方香港 DERP | ACL 里禁用全部官方 region 后单独放开香港（region id 查询当时值，视频时为 20） | 快速，但依赖官方节点 |
| 云服务器加 IPv6（视频推荐） | 云商无 IPv6 就用 he.net 免费隧道：注册 → 创建隧道（填云服务器公网 IP，需放行 ICMP 可 ping）→ 配置示例里**必须用云服务器私网 IP** 而非公网 IP → 写入网络配置文件 → 重启 → ACL 的 derpMap 加 IPv6 地址 | 彻底，双栈直连 |

**防白嫖 verify-clients**（视频 14:16-14:57）：不加限制时，任何知道你 DERP 信息的人都能蹭带宽。解法：云服务器自己装 Tailscale 加入你的网络，derper 服务加 `--verify-clients` 参数重启——只为自己 tailnet 内的节点服务。

---

## 模块五：Headscale 私有化部署

**定位**：Headscale 是 Tailscale 官方协调服务器（控制面）的开源复刻，把设备注册、密钥交换、节点管理全部收到自己手里。**官方立场注意**：Tailscale 开源页表述为「独立于 Tailscale 开发的开源实现」——兼容官方客户端，但不是官方产品（视频说「官方认可和支持」，措辞过强，见勘误表）。

视频流程（16:54-19:57）：

1. 云服务器按官方手册装 Headscale，`systemctl enable` 开机自启
2. 配置文件修改：
   - `server_url` 换成 `http://<公网IP>:3355`（防火墙放行 3355/tcp）
   - 注释 IPv6 前缀（当时的 bug：有 IPv6 虚拟地址时部分客户端只显示 IPv6 不显示 IPv4；2026 该 bug 状态需自行验证）
3. Nginx 反向代理 3355 → Headscale 本地端口
4. 部署 Headscale-Web-UI（GitHub 项目，Nginx 托管 `/web` 路径）
5. `headscale apikey create` 生成 API Key → Web UI 的 Settings 里填入并检测连通
6. Web UI 里创建用户
7. 各平台客户端改登录服务器：

| 平台 | 方法 |
|------|------|
| macOS | 退出官方账号 → 按住 Option 点图标 → Debug → 登录服务器地址改为 `http://<IP>:3355` |
| Linux | `tailscale up --login-server=http://<IP>:3355` |
| Windows/iOS/Android | 官方手册各有覆写注册服务器地址的入口 |

8. 登录时复制 URL 里的 key → Web UI → 设备 → 添加设备 → 选择用户

**挂载自建 DERP**：Headscale 自带 DERP 但同样要域名；无域名就在 Nginx 上托管一份自定义 derp JSON 配置文件（按 Headscale 的 derp 格式写自建服务器信息），Headscale 配置文件里把 `urls` 指向该文件、注释官方地址，重启 nginx + headscale。**坑**：之前开过 `--verify-clients` 的话，必须把云服务器上的 Tailscale 客户端也加入 Headscale 网络，否则自建 DERP 拒绝服务。

---

## 2023→2026 技术演进勘误

视频方法三年后的现状速查（详细来源见验证表）：

| 视频做法（2023-07） | 2026-09 现状 | 建议 |
|--------------------|-------------|------|
| OpenWrt 装 iptables-nft 兼容包（Tailscale 不支持 nftables） | tailscaled 已有防火墙模式选择（firewall-mode 文档：iptables/nftables/auto）；但 OpenWrt fw4 环境下 iptables-nft 兼容层仍是社区主流做法，双层规则冲突案例仍常见（issue #18696） | 保留 iptables-nft 做法，升级后遇路由不通先查双层防火墙冲突 |
| 改 derper 源码注释证书校验行 + 重编译 | derper 有官方 `-certmode manual --certdir` 参数，手动指定自签证书无需改源码（社区广泛使用） | 优先用官方参数；改源码法仅作历史参考 |
| ACL 编辑器填 derpMap | 功能仍在，入口已演进为 tailnet policy file（ACL 的更名），derpMap 对象语法兼容 | 按官方 derp-servers 文档当前语法写 |
| 自签假域名证书纯 IP DERP | 已知坑：自签 IP 证书会触发客户端 `x509: certificate signed by unknown authority` 拒连（issue #15579）；自签**假域名**证书（视频法）因域名不上公网反而绕开了 IP 证书校验问题 | 视频的假域名法依然有效；直接给 IP 签证书才是坑 |
| Headscale「官方认可和支持」 | 官方开源页明确：独立于 Tailscale 开发维护的开源实现，兼容客户端但非官方产品 | 心智模型：社区项目 + 官方知悉兼容，非官方支持 |
| 免费版 100 客户端 | 2026 Personal 计划仍为 100 设备 / 3 用户 | 家庭场景依然够用 |
| 香港 DERP region id=20 | region ID 会随官方扩容变化 | 用 `tailscale netcheck` 现场查 |

---

## 实施路线决策树

```
你的需求是？
├─ 只是几台设备互访（手机/笔记本/台式机）
│    └─→ 装客户端 + 同账号登录，到此为止（模块一）
├─ 要让整个局域网的设备（打印机/NAS/IoT）都能被异地访问
│    ├─ 路由器是 OpenWrt 且愿意动 ──→ 主路由子网路由（模块二·局域网1）
│    ├─ 主路由动不了 ────────────→ 旁路由 + 静态路由（模块二·局域网2）
│    └─ 只有 NAS 有权限 ──────────→ Docker + 计划任务（模块二·局域网3）
├─ 全流量走家里/境外出口
│    └─→ Exit Node（模块三，三步搞定）
├─ 打洞总失败、官方中转慢
│    └─→ 自建 DERP（模块四；先 verify-clients 再上线）
└─ 完全私有化，控制面也不要官方的
     └─→ Headscale + 自建 DERP + Web UI（模块五，维护成本最高）
```

视频作者自己的分级建议（字幕 14:57-15:16）：一般朋友玩到自建 DERP 就够了（免费版 100 客户端对家庭绰绰有余）；Headscale 只留给「官方协调服务器连不上或有特殊需求」的极端场景——私有化的代价是防白嫖、证书、升级维护全自理。

✅ 依序推进勿一步到位：先主路由子网路由验证双向互访 → 再进阶旁路由静态路由 → 最后才考虑 DERP/Headscale
❌ NAS iptables 规则不做开机计划任务 = 重启即失效；DERP 不开 verify-clients = 公开带宽白送

---

## 验证与勘误记录

字幕获取：Tier 0 首次 No transcript found → `--language zh-Hant,zh,en` 重试成功（694 行，zh-Hans 自动字幕，质量好）。核对基准：2023 视频声称 vs 2026-09 官方文档/社区实证：

| 声称（视频/Insights） | 验证结果 | 实际情况 | 来源 |
|------|---------|---------|------|
| Tailscale 守护进程不支持 nftables，OpenWrt 20.3+ 需 iptables-nft | ⚠️ 时效 | 2023 成立；2026 tailscaled 已有 nftables 防火墙模式（文档 features/firewall-mode、reference/netfilter-modes），但 OpenWrt fw4 下 iptables-nft 兼容层仍是主流，双层规则冲突仍见 issue #18696 | tailscale.com/docs、GitHub issues |
| 旁路由需开 IP 伪装 + 主路由静态路由指向旁路由 | ✅ | 子网路由 + 静态路由联动是官方 subnet router 模式标准实践 | tailscale.com/docs/features/subnet-routers |
| 群晖 Docker 需挂 /dev/net/tun + Auth Key + iptables 转发 + 开机计划任务 | ✅ | Docker 官方部署法一致；开机 sleep 60 自启规则是群晖持久化惯例 | Tailscale Docker 文档 |
| DERP 需改 derper 源码绕过域名证书限制 | ⚠️ 已过时 | 2026 derper 官方 `-certmode manual --certdir` 支持手动证书，社区普遍用法；无需改源码 | Reddit r/Tailscale、vsheg.com、noise.amono.me |
| 自签证书纯 IP 搭 DERP 可行 | ⚠️ 有坑 | 自签 **IP** 证书触发客户端 x509 校验失败拒连（issue #15579）；视频用自签**假域名**（不进公网 DNS）恰好绕开此坑 | GitHub issue #15579 |
| ACL 填 derpMap + OmitDefaultRegions 禁官方中转 | ✅ | 语法仍支持；入口更名为 tailnet policy file | tailscale.com/docs/reference/derp-servers |
| DERP 端口：HTTPS 33445/tcp + STUN 3478/udp | ✅ | STUN 3478/udp 为标准；HTTPS 端口可自定义（-a 参数） | derper 参数、社区教程 |
| he.net 隧道给云服务器加 IPv6，配置须用私网 IP | ✅ | HE tunnelbroker 实践惯例（公网 NAT 环境用内网地址建隧道） | he.net 配置示例 |
| verify-clients 防白嫖，需本机装 Tailscale 入网 | ✅ | `--verify-clients` 参数确认存在，与 tailnet 成员校验联动 | derper 参数、Reddit |
| Headscale「受到官方认可和支持」 | ⚠️ 措辞过强 | 官方开源页：独立于 Tailscale 开发维护的开源实现（developed independently and separately）；兼容官方客户端 | tailscale.com/opensource、headscale.net |
| Headscale IPv6 前缀 bug 需注释 | ⚠️ 时效 | 2023-07 时点的 bug；2026 状态未复验，升级版本后自行测试 | 视频口播 |
| 免费版支持 100 客户端 | ✅ | 2026 Personal 计划 100 设备/3 用户 | tailscale.com/pricing |
| Headscale 自带 DERP 必须域名 | ✅ | Headscale 内置 derper 仍依赖有效证书域名；无域名走自建 derp JSON 挂载 | headscale.net/stable/ref/derp |
| Linux `--login-server` / macOS Option+Debug 改服务器 | ✅ | 各平台覆写登录服务器方法沿用 | Headspace 官方客户端接入文档 |

---

## 参考资料

- [视频：Tailscale 玩法之内网穿透、异地组网、全隧道模式、纯 IP 双栈 DERP、Headscale](https://www.youtube.com/watch?v=mgDpJX3oNvI)（韩风Talk，2023-07-05）
- [Tailscale 官方：DERP servers 参考（derpMap 语法）](https://tailscale.com/docs/reference/derp-servers)
- [Tailscale 官方：Firewall mode](https://tailscale.com/docs/features/firewall-mode) · [Netfilter modes](https://tailscale.com/docs/reference/netfilter-modes)
- [Tailscale 官方：Subnet routers](https://tailscale.com/docs/features/subnet-routers) · [Exit nodes](https://tailscale.com/docs/features/exit-nodes)
- [Headscale 官网与文档](https://headscale.net/) · [GitHub: juanfont/headscale](https://github.com/juanfont/headscale)
- [Tailscale 开源页（Headscale 官方立场）](https://tailscale.com/opensource)
- [GitHub issue #15579：自签 IP 证书连接问题](https://github.com/tailscale/tailscale/issues/15579) · [issue #18696：iptables/nftables 双层冲突](https://github.com/tailscale/tailscale/issues/18696)
- [记一次 Bug 定位：Tailscale Derper 自签 IP 证书问题](https://noise.amono.me/posts/tailscale-mysterious-derper/)（中文实证）

## 相关笔记

- [[Tailscale + RustDesk 零成本远程桌面 - 架构解析与实操 SOP]]（同生态：Tailnet 内 IP 直连远程桌面，本文 DERP 知识是其「打洞失败兜底」章节的展开）
- [[老筆電變身家庭 NAS（Ubuntu Server + CasaOS + Tailscale）]]（Tailscale 组网另一实践：单节点 NAS 异地存取）
- [[闲置 MacBook Air 变身 NAS 与机顶盒]]（远程访问安全原则）

---

*文档生成时间：2026-09-09。视频为 2023-07 内容，命令与界面演进较快，动手前以 tailscale.com/docs 与 headscale.net 当前文档为准；勘误表已标注各条目验证状态。*
