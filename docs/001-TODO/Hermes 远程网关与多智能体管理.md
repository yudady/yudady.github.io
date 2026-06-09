---
title: Hermes 远程网关与多智能体管理
aliases:
  - Hermes Desktop 远程网关
  - Hermes 多设备部署
  - AI SOP 经验复刻
tags:
  - hermes-agent
  - ai-agent
  - remote-gateway
  - multi-agent
  - sop
  - type/doc
  - status/active
source:
  - "https://www.youtube.com/watch?v=pRojx7LegDA"
  - "https://www.cmd8.com/post/2728.html"
  - "https://www.cnblogs.com/itech/p/19961757"
author: wow.insight
created: 2026-06-09
updated: 2026-06-09
description: |
  如何通过 Hermes Desktop 远程连接分布在不同设备上的多个 Hermes 智能体，以及利用 AI 踩坑写 SOP 实现跨系统经验复刻。
level: intermediate
stars: 4
---

# Hermes 远程网关与多智能体管理

> 本文整理自 wow.insight 频道的 YouTube 视频，涵盖 Hermes Desktop 远程网关配置、Basic Auth 与 OAuth 认证选择、以及"AI 为 AI 写 SOP"的跨设备经验复刻工作流。适合已经部署了 Hermes Agent、希望管理多台设备上多个智能体的用户。

## 目录

- [[#核心架构：三个组件的职责]]
- [[#方案选择：局域网 vs VPS + Tailscale]]
- [[#认证机制：Basic Auth vs OAuth]]
- [[#实操步骤：配置远程网关]]
- [[#AI 经验复刻：让智能体为智能体写 SOP]]
- [[#故障排查]]

---

## 核心架构：三个组件的职责

理解 Hermes 远程管理前，必须先搞清三个组件各自做什么：

```
┌─────────────────┐        ┌──────────────────┐        ┌─────────────────┐
│  Hermes Desktop  │───────>│ hermes dashboard  │───────>│ hermes gateway   │
│   (本地客户端)    │ HTTP   │  (远程后端进程)     │       │  (消息网关进程)    │
│   前端壳          │        │  状态/配置/记忆管理 │        │  Telegram/Discord│
└─────────────────┘        └──────────────────┘        │  Slack 等渠道     │
                                                       └─────────────────┘
```

| 组件 | 职责 | 说明 |
|------|------|------|
| **hermes dashboard** | 远程运行的后端进程 | 管理 Agent 状态、配置、记忆。Desktop 本质上是它的前端 |
| **hermes gateway** | 消息网关进程 | 处理 Telegram/Discord/Slack 等渠道，**与 dashboard 分开，需单独启动** |
| **Hermes Desktop** | 本地客户端 | 默认管理本地后端，可切换为连接远程 dashboard |

> ⚠️ **常见误区**：`hermes gateway` 和 `hermes dashboard` 是两个独立进程。视频演示的是通过 Desktop 连接远程 dashboard，不涉及 gateway 的远程暴露。

---

## 方案选择：局域网 vs VPS + Tailscale

```
选择部署方案：

  设备在同一局域网？
    │
    ├─ 是 → 方案一：局域网直连
    │       ✅ 配置简单，无需额外工具
    │       ❌ 出门连不上
    │
    └─ 否 → 方案二：VPS + Tailscale
            ✅ 随时可连，公网零暴露
            ❌ 需装 Tailscale 客户端
```

| 维度 | 方案一：局域网 | 方案二：VPS + Tailscale |
|------|---------------|------------------------|
| 适用设备 | Mac mini、NAS、树莓派（同网络） | 云端 VPS、异地服务器 |
| 访问地址 | `192.168.x.x`（局域网 IP） | `100.x.x.x`（Tailscale 虚拟 IP） |
| 安全性 | 局域网内可信 | 仅 Tailscale 私有网络可访问 |
| 便携性 | ❌ 出门连不上 | ✅ 随时随地连 |
| 认证方式 | Basic Auth（用户名+密码） | Basic Auth（用户名+密码） |

> 两个方案都走 Basic Auth，因为流量在可信网络内（局域网或 Tailscale VPN），不需要 OAuth。

---

## 认证机制：Basic Auth vs OAuth

视频的核心论点之一：认证机制的选择取决于网络环境。

```
网络环境判断：

  局域网 / Tailscale（可信网络）？
    │
    ├─ 是 → Basic Auth（轻量，用户名+密码）
    │       ✅ 配置简单，两行环境变量搞定
    │       ✅ 重启后登录状态保持（设固定 SECRET）
    │
    └─ 否（公网直接暴露）→ OAuth
            ✅ 免密码，Token 自动刷新
            ✅ 通过 Nous Portal 统一管理
            ❌ 配置较复杂
```

| 特性 | Basic Auth | OAuth |
|------|-----------|-------|
| 认证方式 | 用户名 + 密码 | Token 自动刷新 |
| 适用场景 | 局域网、Tailscale VPN | 公网 VPS |
| 配置复杂度 | 低（2 个环境变量） | 中（需 Portal 认证） |
| 安全性 | 可信网络内足够 | 公网级别 |
| 重启后保持登录 | ✅ 设 `BASIC_AUTH_SECRET` 即可 | ✅ Token 自动续期 |

---

## 实操步骤：配置远程网关

### Step 1：写入认证凭证（远程服务器）

```bash
cat >> ~/.hermes/.env <<'EOF'
HERMES_DASHBOARD_BASIC_AUTH_USERNAME=admin
HERMES_DASHBOARD_BASIC_AUTH_PASSWORD=你的强密码
HERMES_DASHBOARD_BASIC_AUTH_SECRET=$(openssl rand -base64 32)
EOF
chmod 600 ~/.hermes/.env
```

> **关键细节**：`HERMES_DASHBOARD_BASIC_AUTH_SECRET` 必须设固定值。不设的话 dashboard 每次重启生成新密钥，桌面端登录状态随之失效。

### Step 2：创建 systemd 服务

```bash
mkdir -p ~/.config/systemd/user/
nano ~/.config/systemd/user/hermes-dashboard.service
```

**局域网方案配置：**

```ini
[Unit]
Description=Hermes Agent Dashboard
After=network.target

[Service]
Type=simple
EnvironmentFile=%h/.hermes/.env
ExecStart=/home/%u/.hermes/hermes-agent/venv/bin/hermes dashboard --no-open --host 0.0.0.0 --port 9119
Restart=on-failure
RestartSec=5s
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=default.target
```

**VPS + Tailscale 方案的差异（仅 2 处）：**

```ini
# 1. After 加上 tailscaled
After=network.target tailscaled.service
Requires=tailscaled.service

# 2. --host 改为 Tailscale IP（不要用 0.0.0.0）
ExecStart=/home/%u/.hermes/hermes-agent/venv/bin/hermes dashboard --no-open --host 100.x.x.x --port 9119
```

### Step 3：启动服务

```bash
systemctl --user daemon-reload
systemctl --user enable hermes-dashboard
systemctl --user start hermes-dashboard
systemctl --user status hermes-dashboard
loginctl enable-linger $USER  # 不登录时也保持运行
```

### Step 4：桌面端连接

打开 Hermes Desktop → **Settings → Gateway → Remote gateway**：

1. **Remote URL**：`http://<服务器IP>:9119`
2. **Sign in**：填入 Step 1 设置的用户名和密码
3. **Save and reconnect**

```
局域网方案：http://192.168.x.x:9119
Tailscale 方案：http://100.x.x.x:9119（用 Tailscale IP，不要用公网 IP）
```

### 最佳实践

- ✅ 设固定 `BASIC_AUTH_SECRET`，避免每次重启掉登录
- ✅ Tailscale 方案加防火墙：`ufw allow in on tailscale0 to any port 9119 && ufw deny 9119`
- ✅ 用 `systemctl --user` 而非 `sudo`，不需要 root 权限
- ✅ `ExecStart` 路径用 `which hermes` 的实际输出替换
- ❌ Tailscale 方案不要用 `--host 0.0.0.0`（公网暴露）
- ❌ 不要混淆 dashboard 和 gateway（两个独立进程）

---

## AI 经验复刻：让智能体为智能体写 SOP

这是视频最核心的观点——"杀手锏"工作流。

### 核心理念

```
传统方式：
  设备 A 上 AI 踩坑 → 人工记录 → 设备 B 上人工配置 → AI 再踩一遍同样的坑

SOP 复刻方式：
  设备 A 上 AI 踩坑 → AI 自己写 SOP 笔记 → 设备 B 的 AI 读取 SOP → 直接复制经验
```

### 实际案例

视频中，一台旧笔记本上的 Hermes 智能体花了 5 分钟从零配置好远程网关，并自动生成了 SOP 笔记。另一台 Mac mini 上的智能体读取这个 SOP 后，能跳过踩坑直接完成配置。

### SOP 复刻工作流

```
┌──────────────┐     ┌───────────────┐     ┌──────────────┐
│  AI Agent A   │────>│  SOP 笔记存储  │────>│  AI Agent B   │
│  (踩坑 + 记录) │     │  (Obsidian/    │     │  (读取 + 执行) │
│               │     │   共享文件系统)  │     │               │
└──────────────┘     └───────────────┘     └──────────────┘
     Ubuntu               SOP .md              macOS
     旧笔记本                                    Mac mini
```

### 决策树：什么时候用 SOP 复刻

```
多设备部署同一服务？
  │
  ├─ 是 → 让一个 AI 先做，写 SOP
  │       ✅ 避免重复踩坑
  │       ✅ SOP 可版本管理（git）
  │       ✅ 跨 OS 复用（Ubuntu SOP → macOS 适配）
  │
  └─ 否 → 单独配置即可
```

### 最佳实践

- ✅ SOP 存在共享位置（git 仓库或 Obsidian vault）
- ✅ 包含踩坑记录（`Pitfall` 部分）而非只有成功步骤
- ✅ 跨 OS 部署时标注差异（systemd vs launchd、路径差异等）
- ❌ 不要只记录"成功步骤"，失败经验才是最有价值的

---

## 故障排查

| 问题 | 原因与解决 |
|------|-----------|
| 401 Invalid credentials | 用户名密码对不上。验证：`curl -s http://<host>:9119/api/status` |
| 无 "Sign in" 按钮 | `.env` 中认证变量未设置，或 dashboard 未重载 |
| 每次重启掉登录 | `BASIC_AUTH_SECRET` 未设固定值 |
| 局域网连接超时 | dashboard 绑定 `127.0.0.1`，检查 `--host` 参数 |
| Tailscale 连接超时 | 检查本地/服务器 Tailscale 是否在线 |
| dashboard 启动失败 | Tailscale 方案中 `Requires=tailscaled.service` 未加 |
| gateway 没消息 | gateway 是独立进程，需单独启动：`hermes gateway install` |

---

## 参考资料

- [视频：AI 竟然会写 SOP 教同类？5分钟搞定 Hermes 智能体跨设备远程网关！](https://www.youtube.com/watch?v=pRojx7LegDA)
- [如何通过 Hermes 桌面端连接远程网关 - cmd8.com](https://www.cmd8.com/post/2728.html)
- [Hermes Agent 终于有了像样的 Web 界面 - 博客园](https://www.cnblogs.com/itech/p/19961757)
- [Hermes Agent 官方文档](https://hermes-agent.nousresearch.com/docs)

## 相关笔记

- [[Hermes Agent]]
- [[AI Agent 远程管理]]
