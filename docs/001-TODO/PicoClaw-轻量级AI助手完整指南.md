---
in: null
title: PicoClaw 轻量级 AI 助手完整指南
aliases: [PicoClaw 轻量级 AI 助手完整指南, "皮皮虾AI", "PicoClaw 轻量级 AI 助手完整指南", "PicoClaw安装", "PicoClaw教程"]
tags: ["安装指南", "轻量级", "嵌入式", "AI助手", AI, Docker, OpenClaw, PicoClaw, Telegram]
source: [https://github.com/sipeed/picoclaw, https://picoclaw.io, https://www.youtube.com/watch?v=kE-V-XeyxCs]
author: "影片原作者 + AI 补充"
created: 2026-02-22 17:40
updated: 2026-03-07 21:59
description: "PicoClaw 是一个极致轻量级的 AI 助手，内存占用 <10MB，启动时间 <1 秒，可在 $10 硬件上运行。本指南涵盖安装、配置、Telegram 集成及与其他方案对比。"
level: beginner
stars: 5
category: "技术"
summary: "PicoClaw轻量级AI助手的安装与使用指南"
tags:
  - status/active
  - area/distill
  - type/doc
---

# PicoClaw 轻量级 AI 助手完整指南

## 目录

- [什么是 PicoClaw](#什么是-picoclaw)
- [快速安装](#快速安装)
- [配置详解](#配置详解)
- [Telegram 机器人集成](#telegram-机器人集成)
- [使用示例](#使用示例)
- [与其他方案对比](#与其他方案对比)
- [性能数据](#性能数据)
- [常见问题](#常见问题)
- [参考资料](#参考资料)

---

## 什么是 PicoClaw

PicoClaw（皮皮虾）是一个**超轻量级个人 AI 助手**，由**矽速科技（Sipeed）**开发。

### 核心特点

| 特性 | 数值 | 对比 OpenClaw |
|------|------|---------------|
| 内存占用 | <10 MB | 减少 99% |
| 启动时间 | <1 秒 | 快 400 倍 |
| 硬件成本 | ~$10 | 1/60 价格 |
| 二进制大小 | 15-25 MB | 单一文件 |

### 设计理念

```
OpenClaw (TypeScript, 43万行代码)
    ↓
Nanobot (Python, 4000行代码, 港大HKUDS实验室)
    ↓
PicoClaw (Go, 矽速科技Sipeed) ← 本指南主角
    ↓
ZeroClaw (Rust, 极致性能)
```

### 适用场景

- 树莓派等嵌入式设备
- 闲置安卓电视盒/旧手机
- 低配云服务器
- 边缘 AI 计算场景
- 成本敏感的个人部署

---

## 快速安装

### 方法一：预编译二进制（推荐）

```bash
# 下载对应平台的二进制文件
wget https://github.com/sipeed/picoclaw/releases/download/v0.1.1/picoclaw-linux-arm64
chmod +x picoclaw-linux-arm64

# 运行初始化
./picoclaw-linux-arm64 onboard
```

### 方法二：Docker Compose

```bash
# 克隆仓库
git clone https://github.com/sipeed/picoclaw.git
cd picoclaw

# 复制并编辑配置
cp config/config.example.json config/config.json
vim config/config.json  # 设置 API 密钥、令牌等

# 使用 Docker 启动
docker compose --profile gateway up -d

# 查看日志
docker compose logs -f picoclaw-gateway
```

### 方法三：从源码编译

```bash
git clone https://github.com/sipeed/picoclaw.git
cd picoclaw
make deps      # 安装依赖
make build     # 编译
make install   # 安装到系统
```

### 前置要求

- **Docker**：用于隔离环境运行
- **API 密钥**：OpenRouter / 智谱 AI / 其他 LLM 提供商
- **Telegram Bot Token**：如需 Telegram 集成

---

## 配置详解

### 目录结构

```
~/.picoclaw/
├── workspace/          # 工作区
│   ├── sessions/       # 对话会话和历史记录
│   ├── memory/         # 长期记忆 (MEMORY.md)
│   ├── cron/           # 定时任务数据库
│   ├── skills/         # 自定义技能
│   └── state/          # 持久化状态
└── config/
    └── config.json     # 主配置文件
```

### 最小化配置（使用 OpenRouter）

```json
{
  "agents": {
    "defaults": {
      "workspace": "~/.picoclaw/workspace",
      "model": "openai/gpt-4o-mini",
      "temperature": 0.7
    }
  },
  "providers": {
    "openrouter": {
      "api_key": "你的OpenRouter密钥",
      "api_base": "https://openrouter.ai/api/v1"
    }
  }
}
```

### 完整配置示例

```json
{
  "agents": {
    "defaults": {
      "workspace": "~/.picoclaw/workspace",
      "restrict_to_workspace": true,
      "model": "zhipu/glm-4.7",
      "max_tokens": 8192,
      "temperature": 0.7,
      "max_tool_iterations": 20
    }
  },
  "channels": {
    "telegram": {
      "enabled": true,
      "token": "YOUR_TELEGRAM_BOT_TOKEN",
      "allow_from": ["YOUR_USER_ID"]
    },
    "discord": {
      "enabled": false,
      "token": "YOUR_DISCORD_BOT_TOKEN"
    }
  },
  "providers": {
    "zhipu": {
      "api_key": "YOUR_ZHIPU_API_KEY",
      "api_base": "https://open.bigmodel.cn/api/paas/v4"
    },
    "openrouter": {
      "api_key": "YOUR_OPENROUTER_KEY",
      "api_base": "https://openrouter.ai/api/v1"
    }
  },
  "tools": {
    "web": {
      "brave": {
        "enabled": true,
        "api_key": "YOUR_BRAVE_API_KEY",
        "max_results": 5
      },
      "duckduckgo": {
        "enabled": true,
        "max_results": 5
      }
    }
  }
}
```

### 使用本地 LM Studio 模型

```json
{
  "providers": {
    "openrouter": {
      "api_key": "lm-studio",
      "api_base": "http://localhost:1234/v1"
    }
  },
  "agents": {
    "defaults": {
      "model": "local-model"
    }
  }
}
```

### 支持的 AI 模型

| Vendor | 前缀 | 免费额度 |
|--------|------|----------|
| 智谱 AI | `zhipu/` | 200K tokens/月 |
| OpenRouter | `openrouter/` | 200K tokens/月 |
| OpenAI | `openai/` | 付费 |
| DeepSeek | `deepseek/` | 付费 |
| Groq | `groq/` | 免费推理 |
| Ollama | `ollama/` | 本地免费 |

---

## Telegram 机器人集成

### 步骤 1：创建机器人

1. 打开 Telegram，搜索 `@BotFather`
2. 发送 `/newbot`，按提示操作
3. 复制获得的 token（格式：`123456:ABC-DEF...`）

### 步骤 2：获取用户 ID

1. 搜索 `@userinfobot`
2. 发送任意消息
3. 记下你的 User ID

### 步骤 3：配置 PicoClaw

```json
{
  "channels": {
    "telegram": {
      "enabled": true,
      "token": "123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11",
      "allow_from": ["你的用户ID"]
    }
  }
}
```

### 步骤 4：启动网关

```bash
# 启动 Telegram 网关
picoclaw gateway

# 或使用 Docker
docker compose --profile gateway up -d
```

### 安全说明

- `allow_from` 列表确保只有你能与机器人交互
- 可添加多个用户 ID 以允许多人使用

---

## 使用示例

### 基础对话

```bash
# 命令行对话
picoclaw agent -m "你好，介绍一下你自己"

# 交互模式
picoclaw agent
> What is 2+2?
> 4
```

### Telegram 使用

```
用户: hello
机器人: 你好！我是 PicoClaw，有什么可以帮你的？

用户: who are you?
机器人: 我是 PicoClaw，一个轻量级 AI 助手...

用户: can you search for the latest news about AI?
机器人: [使用 Brave/DuckDuckGo 搜索并返回结果]
```

### 定时任务

```bash
# 查看定时任务
picoclaw cron list

# 添加定时任务（通过对话）
"每天早上8点提醒我开会"
```

---

## 与其他方案对比

### 核心对比表

| 特性 | OpenClaw | Nanobot | **PicoClaw** | ZeroClaw |
|------|----------|---------|--------------|----------|
| **语言** | TypeScript | Python | **Go** | Rust |
| **内存** | >1GB | >100MB | **<10MB** | <5MB |
| **启动** | >500秒 | >30秒 | **<1秒** | <10ms |
| **成本** | $599 | $50 | **$10** | $10 |
| **Stars** | 50K+ | 19.7K | 12K+ | 较新 |

### 性能对比图

```
内存占用（从小到大）：
ZeroClaw ████████ 5MB
PicoClaw ████████████████ 10MB
Nanobot  ████████████████████████████████████████████████████████████████████████████████████████████████████ 100MB
OpenClaw ████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████ 1GB

启动时间（从快到慢）：
ZeroClaw █ 10ms
PicoClaw ██ 1s
Nanobot  █████████████████████████████████ 30s
OpenClaw  ████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████ 500s
```

### 选择建议

| 你的情况 | 推荐 |
|----------|------|
| 有树莓派/闲置开发板 | ✅ **PicoClaw** |
| 预算有限（$10级别） | ✅ **PicoClaw** |
| 需要浏览器控制 | ❌ OpenClaw |
| 需要完整桌面自动化 | ❌ OpenClaw |
| 熟悉 Python 开发 | ⚠️ Nanobot |
| 追求极致性能 | ⚠️ ZeroClaw |

---

## 性能数据

### 资源占用

| 资源 | 要求 |
|------|------|
| RAM | <10 MB 空闲 |
| 存储 | ~15-25 MB |
| 处理器 | 0.6 GHz 单核 |
| 操作系统 | Linux |

### 支持的硬件

| 设备 | 成本 | 用途 |
|------|------|------|
| LicheeRV-Nano | $9.90 | 极简家居助手 |
| 旧安卓手机 | $0 | Termux 变身 AI 助手 |
| NanoKVM | $30-50 | 服务器自动化 |
| MaixCAM | $50 | 智能监控 |
| Raspberry Pi | $35 | 通用部署 |

### 支持的架构

- x86_64 (Intel/AMD)
- ARM64 (树莓派等)
- RISC-V

---

## 常见问题

### Q1: Web 搜索报错 "API 配置问题"

**原因**：未配置搜索 API 密钥

**解决方案**：
1. 推荐：[Brave Search](https://brave.com/search/api) 获取免费 API（2000次/月）
2. 备选：DuckDuckGo（无需密钥，自动回退）

```json
{
  "tools": {
    "web": {
      "duckduckgo": { "enabled": true, "max_results": 5 }
    }
  }
}
```

### Q2: Telegram 提示 "Conflict: terminated by other getUpdates"

**原因**：另一个机器人实例正在运行

**解决方案**：确保同时只运行一个 `picoclaw gateway` 实例

### Q3: 内容过滤错误

**原因**：某些提供商（如智谱）有内容过滤

**解决方案**：尝试重新表述查询或使用不同的模型

### Q4: 安全沙箱限制

**解决方案**：如需访问工作区外路径：

```json
{
  "agents": {
    "defaults": {
      "restrict_to_workspace": false
    }
  }
}
```

或设置环境变量：
```bash
export PICOCLAW_AGENTS_DEFAULTS_RESTRICT_TO_WORKSPACE=false
```

---

## 参考资料

### 官方资源

- **GitHub**: https://github.com/sipeed/picoclaw
- **官网**: https://picoclaw.io
- **Discord**: https://discord.gg/V4sAZ9XWpN
- **开发商**: https://sipeed.com

### 相关项目

- OpenClaw: https://github.com/OpenClaw/OpenClaw
- Nanobot: https://github.com/xubinrencs/nanobot
- ZeroClaw: https://github.com/zeroclaw-labs/zeroclaw

### 视频来源

- 原始教程视频: https://www.youtube.com/watch?v=kE-V-XeyxCs

---

## 安全声明

> ⚠️ **官方警告**：
> - 无官方加密货币/代币，所有在 pump.fun 或其他交易平台的声称都是诈骗
> - 唯一官方域名：picoclaw.io 和 sipeed.com
> - 当前处于早期开发，v1.0 之前不建议部署到生产环境

---

*本指南基于 2026-02-22 的信息整理，如有更新请参考官方文档。*

## 相关笔记
- [[03-knowledge/技术/人工智能/AntiGravity-GravityClaw-本地优先AI助手|Antigravity/GravityClaw]]
- [[03-knowledge/技术/人工智能/小龙虾-OpenClaw版本选择指南|OpenClaw]]
- [[03-knowledge/技术/人工智能/Top20AIAgentProjects-本周精选|Top 20 AI Agent]]
- [[004-ai-tools/nanobot/NANOBOT_SOURCE_ANALYSIS|Nanobot]]