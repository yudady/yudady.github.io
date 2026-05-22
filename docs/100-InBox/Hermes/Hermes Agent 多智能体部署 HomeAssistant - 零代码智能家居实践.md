---
title: Hermes Agent 多智能体部署 HomeAssistant - 零代码智能家居实践
aliases:
  - 古董笔记本跑 Hermes + HomeAssistant
  - 多智能体 Kanban 部署 HA
tags:
  - ai-agent
  - hermes-agent
  - homeassistant
  - smart-home
  - multi-agent
  - kanban
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=tCmG4PozkWE"
  - "https://hermes-agent.nousresearch.com/docs/user-guide/features/kanban"
  - "https://bbs.hassbian.com/thread-32162-1-1.html"
author: YouTube 频道（产品开发者不详）
created: 2026-05-19
updated: 2026-05-19
description: |
  用一台十年前的 ThinkPad X230i，通过 Hermes Agent 的多智能体 Kanban 系统，零代码全自动部署 HomeAssistant 智能家居，并打造定时巡检的数字管家。
level: intermediate
stars: 4
---

# Hermes Agent 多智能体部署 HomeAssistant — 零代码智能家居实践

> 一台十年前的 ThinkPad X230i，通过 Hermes Agent 的 Kanban 多智能体协作系统，零代码全自动部署 HomeAssistant 智能家居系统，并孵化出一位每天凌晨准时巡检数据的数字管家。适合对 AI Agent + 智能家居交叉领域感兴趣的开发者。

## 目录

- [[#古董硬件改造：打造静音 AI 沙盒主机]]
- [[#Kanban 多智能体协作：零代码部署 HomeAssistant]]
- [[#AI 接管物理世界：赛博修理工]]
- [[#数字管家 Home Caretaker：凌晨两点半的灵魂]]
- [[#Hermes Agent 接入 HomeAssistant 技术细节]]

---

## 古董硬件改造：打造静音 AI 沙盒主机

### 核心思路

视频作者将一台十年前的 ThinkPad X230i 改造为 24/7 运行的 AI 沙盒宿主机，目标是「纯净、静音、永不休眠」。

### 硬件升级清单

| 组件 | 原始配置 | 升级后 | 花费 |
|------|----------|--------|------|
| 硬盘 | 500GB 机械硬盘 | 500GB SSD | ~$70 |
| 内存 | 4GB | 16GB | ~$30 |
| CPU | Intel i3 (X230i) | 不变 | $0 |

### 系统调优策略

```
┌─────────────────────────────────────────────────┐
│              ThinkPad X230i 改造               │
├─────────────────────────────────────────────────┤
│                                                 │
│  1. 禁用合盖休眠                                │
│     修改 logind.conf → HandleLidSwitch=ignore   │
│                                                 │
│  2. GRUB 底层参数                                │
│     60秒无操作 → 自动切断视频输出                 │
│     → 彻底屏保，省电降热                        │
│                                                 │
│  3. 压制内存交换 (swap)                          │
│     16GB 内存对 HA + Hermes 绰绰有余            │
│     → 减少 SSD 无谓磨损                          │
│                                                 │
│  4. 网络流量全局走软路由                          │
│     → 高速拉取 Docker 镜像和模型数据             │
│                                                 │
│  结果：无光污染、无风扇噪音、永不休眠            │
│       像一艘潜伏在角落的静音潜艇                 │
└─────────────────────────────────────────────────┘
```

### 最佳实践

- ✅ 机械硬盘必须换 SSD — HA 和 Hermes 日常产生大量高频 I/O，机械硬盘影响性能且寿命堪忧
- ✅ 16GB 内存是甜蜜点 — HA 容器 + Hermes Agent + Python 运行时，8GB 勉强、16GB 宽裕
- ✅ 禁用 swap 保护 SSD — 服务器场景宁可 OOM 也不愿 SSD 被交换写磨损
- ❌ 不要用笔记本内置屏幕做长期输出 — 通过 GRUB 参数切断视频输出，彻底省电

---

## Kanban 多智能体协作：零代码部署 HomeAssistant

### 核心概念

Hermes Agent 的 Kanban（看板）系统是一个 **持久化的 SQLite 任务队列**，允许多个命名智能体（Agent Profile）在同一块看板上协作，每个任务是一个 SQLite 行，每次交接都可被任何 Agent 读写。

### 部署流程

```
用户："我想装 HomeAssistant"
          │
          ▼
   ┌──────────────┐
   │ Hermes Agent  │ ← 像老练的 HR 总监，查阅技能库
   │ 自动规划岗位  │
   └──────┬───────┘
          │ 一口气列出 6 个专职岗位
          ▼
   ┌─────────────────────────────────────┐
   │           Kanban 看板                │
   │                                     │
   │  [DevOps]    → 服务器运维            │
   │  [Admin]     → 系统管理员            │
   │  [Hacker]    → 配置代码程序员        │
   │  [Architect] → 架构师               │
   │  [QA]        → 测试员               │
   │  [Reviewer]  → 代码审查             │
   └──────────────┬──────────────────────┘
                  │
                  ▼
   ┌─────────────────────────────────────┐
   │         任务自动流转                 │
   │                                     │
   │  DevOps: 创建 Docker 部署命令        │
   │     → 被卡住：无 Docker 权限         │
   │     → 看板上举手求助                 │
   │                                     │
   │  人类: 精准下放 docker 用户组权限     │
   │     + 文件目录所有权                  │
   │                                     │
   │  DevOps → Admin → Hacker → QA      │
   │     依任务依赖自动传递                │
   │                                     │
   │  HAAutomator: patch 工具优雅修复    │
   │     配置文件 + 建好 packages 目录    │
   └─────────────────────────────────────┘
          │
          ▼
   HomeAssistant 完美运行 ✅
```

### 关键交互：权限管理的边界感

视频作者刻意没有给 AI 系统最高权限（root），而是**精准下放**：

| 权限操作 | 授予内容 | 不授予 |
|----------|----------|--------|
| Docker 操作 | docker 用户组成员 | root / sudo |
| 文件系统 | HA 数据目录的所有权 | / 等系统目录 |
| 网络 | 通过软路由管控 | 防火墙规则修改 |

### Kanban vs delegate_task 决策树

```
选 delegate_task 如果你需要：
  ✅ 父 Agent 需要子 Agent 的返回值才能继续
  ✅ 短期任务，不需要人类介入
  ✅ 一次性 RPC 调用

选 Kanban 如果你需要：
  ✅ 任务跨多个 Agent 边界协作
  ✅ 需要人类审批（如权限授予）
  ✅ 任务需要存活于重启之间
  ✅ 任何 Profile 都能读写任务状态
  ✅ 需要审计追踪（SQLite 持久化）
```

---

## AI 接管物理世界：赛博修理工

### 案例 1：小米网关 MQTT 魔改

HomeAssistant 社区插件对小米多功能网关的彩色 LED 灯不支持调色和调光。Hermes Agent 通过 **MQTT Bridge** 直接魔改网关：

| 改造前 | 改造后 |
|--------|--------|
| 仅支持开关 | 亮度随心调节 |
| 固定颜色 | 颜色自由控制 |
| 社区插件限制 | MQTT 直连绕过 |

### 案例 2：发现并修复脑裂风险

Agent 在复盘时主动发现网关暴露了**双重实体**（本地 + 云端），存在脑裂（Split-brain）风险：

```
正常状态：网关 → 单一实体 → HA
脑裂风险：网关 → 本地实体 ←→ 云端实体 → HA
                     ↑
               状态不一致！
                     
Agent 处理：主动禁用多余的云端实体
结果：消除脑裂风险 ✅
```

### 最佳实践

- ✅ 让 Agent 自主复盘已完成的部署 — 可能发现人眼忽略的安全隐患
- ✅ AI 修改硬件配置时，保留原始配置的备份
- ❌ 不要让 AI 直接操作 shell_command 等高危 HA 服务域

---

## 数字管家 Home Caretaker：凌晨两点半的灵魂

### 架构设计

```
┌──────────────────────────────────────────────────┐
│                Home Caretaker                    │
│          (Hermes Agent 克隆体)                   │
│                                                  │
│  注入 Soul.md (灵魂指令):                        │
│  ├─ 家庭环境信息                                 │
│  ├─ 宠物信息（3只猫，合计约50斤）                 │
│  ├─ 植物信息                                     │
│  ├─ 智能设备列表                                 │
│  └─ 电脑和服务器的角色定位                       │
│                                                  │
│  定时唤醒: Cron → 每天凌晨 2:45 ~ 3:45           │
│                                                  │
│  运行流程:                                       │
│  1. 连接 HomeAssistant API                       │
│  2. 读取传感器/设备状态                           │
│  3. 分析数据，识别异常                            │
│  4. 生成每日简报 → Telegram 推送                 │
└──────────────────────────────────────────────────┘
```

### 首次运行发现的问题

| 发现 | 详情 | 严重程度 |
|------|------|----------|
| 温室效应 | 一楼密封阳台比室内高近 3°C，提醒关注植物水分蒸发 | 🟡 提醒 |
| 滤芯寿命 | 反渗透饮水器滤芯寿命剩余 64% | 🟡 提醒 |
| 门锁异常 | 某时间点门锁状态为「卡住」，标记红色异常 | 🔴 异常 |
| 扫地机器人 | 记录 12:29 完成清扫，备注「在对抗三只猫制造的猫砂」 | 🟢 记录 |

### Agent 的认知层次

```
Level 1: 数据记录
  "扫地机器人 12:29 完成清扫"

Level 2: 因果推理  
  "扫地机辛苦工作 → 对抗三只猫制造的猫砂"

Level 3: 主动关怀
  "饮水器滤芯 64% → 主动提醒更换"
  "阳台温差 3°C → 提醒植物浇水"

Level 4: 上下文理解
  "门锁卡住 → 检查是否人为操作 → 标记待确认"
  (实际是作者在门口换地毯，门没关)
```

### Soul.md 注入模式

Soul.md 是注入给克隆智能体的系统级指令文件，定义了它的职责范围和关注重点。这是 Hermes Agent 的一个核心模式：

```bash
# 克隆智能体并注入灵魂指令
# 在 Hermes 中通过对话完成：
# "克隆你自己，命名为 HomeCaretaker，注入以下灵魂指令..."
```

### 对比：传统自动化 vs AI 管家

| 维度 | 传统 HomeAssistant 自动化 | AI 数字管家 (Caretaker) |
|------|--------------------------|----------------------|
| 触发方式 | 固定规则 (if-then) | 自主分析 + 主动发现 |
| 认知能力 | 阈值判断 (温度 > 30°C) | 因果推理 + 上下文理解 |
| 异常处理 | 发通知 | 分析原因 + 标记严重程度 |
| 日常报告 | 无 | 每日结构化简报 |
| 学习能力 | 无 | 随运行积累环境认知 |

---

## Hermes Agent 接入 HomeAssistant 技术细节

### 集成架构

```
┌─────────────┐     WebSocket      ┌──────────────────┐
│   Home      │ ◄═════════════════► │   Hermes Agent    │
│ Assistant   │   实时状态变更      │   Gateway         │
│             │                    │                   │
│  REST API   │ ◄═════════════════► │  HA Tools:        │
│             │   设备查询/控制     │  ├ ha_list_entities│
│             │                    │  ├ ha_get_state    │
│             │                    │  ├ ha_call_service │
│             │                    │  └ ha_list_services│
└─────────────┘                    └──────────────────┘
                                          │
                                   ┌──────┴──────┐
                                   │  Telegram    │
                                   │  通知推送    │
                                   └─────────────┘
```

### 配置步骤

**1. 创建 HA 长期访问令牌**

进入 HA → 个人资料 → 长期访问令牌 → 创建

**2. 配置环境变量**

```bash
# ~/.hermes/.env 或对应环境
HASS_TOKEN=your-long-lived-access-token
HASS_URL=http://192.168.1.100:8123  # 可选，默认 homeassistant.local:8123
```

**3. 启动网关**

```bash
hermes gateway
```

**4. 事件过滤配置（重要）**

```yaml
# ~/.hermes/config.yaml
platforms:
  homeassistant:
    enabled: true
    extra:
      watch_domains:
        - climate
        - binary_sensor
        - alarm_control_panel
        - light
      ignore_entities:
        - sensor.uptime
        - sensor.cpu_usage
      cooldown_seconds: 30
```

> 默认不转发任何事件，必须显式配置 `watch_domains` 或 `watch_entities`。

### 四个 HA 工具速查

| 工具 | 功能 | 关键参数 |
|------|------|----------|
| `ha_list_entities` | 列出实体 | domain, area |
| `ha_get_state` | 获取状态 | entity_id (必填) |
| `ha_call_service` | 控制设备 | domain, service, entity_id, data |
| `ha_list_services` | 列出服务 | domain |

### 安全边界

以下 HA 服务域被 Hermes **阻止调用**，防止任意代码执行：

| 被阻止的域 | 风险 |
|------------|------|
| `shell_command` | 任意 shell 命令 |
| `command_line` | 执行命令的传感器/开关 |
| `python_script` | 脚本化 Python |
| `pyscript` | 广泛脚本集成 |
| `hassio` | 主机关机/重启 |
| `rest_command` | SSRF 风险 |

---

## 参考资料

- [Hermes Agent Kanban 官方文档](https://hermes-agent.nousresearch.com/docs/user-guide/features/kanban)
- [Hermes Agent GitHub 仓库](https://github.com/NousResearch/hermes-agent)
- [Hermes Agent 接入 HomeAssistant 完整指南 - 瀚思彼岸](https://bbs.hassbian.com/thread-32162-1-1.html)
- [Hermes Agent v0.13 Reference](https://blakecrosley.com/guides/hermes)
- [Multi-Agent Kanban board in Hermes Agent - unwind ai](https://www.theunwindai.com/p/multi-agent-kanban-board-in-hermes-agent)
- [视频部署日志网页](https://test-github-repo.vercel.app/hermes_kanban_install_ha.html#section7)

## 相关笔记

- [[Hermes Agent]]
- [[HomeAssistant]]
- [[多智能体系统]]
