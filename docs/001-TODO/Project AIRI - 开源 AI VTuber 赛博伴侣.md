---
created: 2026-04-25
updated: 2026-04-25
title: Project AIRI - 开源 AI VTuber 赛博伴侣
tags:
  - ai
  - vtuber
  - open-source
  - llm
  - digital-companion
source: https://github.com/moeru-ai/airi
author: Moeru AI Project AIRI Team
description: 复刻 Neuro-sama 的开源 AI VTuber 项目，支持实时语音聊天、玩游戏（Minecraft/Factorio）、Live2D/VRM 模型，Web/桌面/移动多端运行。
level: researched
---

# Project AIRI - 开源 AI VTuber 赛博伴侣

> [!info] 基本信息
> - **仓库**: https://github.com/moeru-ai/airi
> - **协议**: MIT
> - **版本**: v0.9.0-beta.2
> - **语言**: TypeScript / Vue.js / Rust (monorepo, pnpm workspaces)
> - **官网/在线体验**: https://airi.moeru.ai
> - **文档站**: https://airi.moeru.ai/docs/
> - **DeepWiki**: https://deepwiki.com/moeru-ai/airi
> - **社区**: [Discord](https://discord.gg/TgQ3Cu2F7A) / [Telegram](https://t.me/+7M_ZKO3zUHFlOThh) / [QQ群](https://qun.qq.com) / [WeChat](https://airi.moeru.ai/docs/wechat.md)
> - **X/Twitter**: [@proj_airi](https://x.com/proj_airi)
> - **组织**: [@moeru-ai](https://github.com/moeru-ai) (主仓库) + [@proj-airi](https://github.com/proj-airi) (子项目)
> - **灵感来源**: [Neuro-sama](https://www.youtube.com/@Neurosama)

---

## 一句话定位

复刻 Neuro-sama 的开源 AI VTuber/赛博伴侣项目，让用户拥有能聊天、玩游戏、实时语音交互的 AI 虚拟角色。

## 核心解决的问题

- Neuro-sama 不开源，直播结束后无法互动
- 现有 AI 角色产品（Character.ai、SillyTavern）仅限文字聊天，无法玩游戏或做更复杂的交互
- 用户需要**自托管、完全掌控**自己的 AI 数字伴侣

## 主要功能/特性

| 能力 | 状态 | 说明 |
|------|------|------|
| 实时语音聊天 | 已完成 | 语音识别 + TTS (ElevenLabs) |
| LLM 对话 | 已完成 | 支持 30+ LLM API Provider |
| 玩 Minecraft | 已完成 | 通过 Mineflayer 连接 MC 服务器 |
| 玩 Factorio | WIP | 有 PoC 和 demo |
| 玩 Kerbal Space Program | 已完成 | 公告待发 |
| Telegram 聊天 | 已完成 | services/telegram-bot |
| Discord 聊天 | 已完成 | services/discord-bot + 语音频道 |
| Live2D 模型 | 已完成 | 控制、动画、自动眨眼、视线追踪 |
| VRM 模型 | 已完成 | 3D 模型、Three.js 渲染 |
| 记忆系统 | WIP | DuckDB WASM / pglite（浏览器端），Alaya 记忆层开发中 |
| 浏览器端推理 | WIP | WebGPU 本地推理 |
| 插件系统 | WIP | Bilibili、Claude Code、HomeAssistant、Web Extension |

## 技术栈

- **前端框架**: Vue.js + TypeScript
- **构建工具**: Vite + Turbo (monorepo)
- **3D 渲染**: Three.js (via @tresjs/core) + TresJS + WebGPU
- **模型格式**: Live2D + VRM
- **2D 桌面版**: Electron (Stage Tamagotchi)
- **移动端**: Capacitor (Stage Pocket, iOS/Android)
- **数据库**: DuckDB WASM / pglite（浏览器端）, PostgreSQL + Drizzle ORM（Telegram bot）
- **包管理**: pnpm 10.33.0 (corepack)
- **LLM 交互**: 自研 [xsai](https://github.com/moeru-ai/xsai) (类似 Vercel AI SDK)
- **语音**: WebAudio API + ElevenLabs
- **平台**: Web (PWA) / macOS / Windows / iOS / Android

## 仓库结构

```
airi/
├── apps/                      # 应用入口
│   ├── stage-web/             # 浏览器版 (airi.moeru.ai)
│   ├── stage-tamagotchi/      # 桌面版 (Electron, "拓麻歌子")
│   ├── stage-pocket/          # 移动版 (Capacitor)
│   ├── server/                # 服务端
│   ├── component-calling/     # 组件调用
│   └── ui-server-auth/        # 服务端认证 UI
├── packages/                  # 内部包 (30+)
│   ├── core-character/        # 角色核心
│   ├── stage-ui/              # 舞台 UI 组件
│   ├── stage-ui-live2d/       # Live2D 支持
│   ├── stage-ui-three/        # Three.js/VRM 支持
│   ├── audio/                 # 音频处理
│   ├── memory-pgvector/       # pgvector 记忆
│   ├── server-runtime/        # 服务端运行时
│   ├── server-sdk/            # 服务端 SDK
│   ├── plugin-sdk/            # 插件 SDK
│   └── ...fonts, i18n, 等
├── plugins/                   # 插件
│   ├── airi-plugin-bilibili-laplace/
│   ├── airi-plugin-claude-code/
│   ├── airi-plugin-homeassistant/
│   └── airi-plugin-web-extension/
├── services/                  # 外部服务集成
│   ├── telegram-bot/          # Telegram Bot (需 PostgreSQL)
│   ├── discord-bot/           # Discord Bot
│   ├── minecraft/             # Minecraft Agent (Mineflayer)
│   ├── satori-bot/            # Satori 协议 Bot
│   └── twitter-services/      # Twitter 集成
├── docs/                      # 文档站 (VitePress)
└── nix/                       # Nix flake 打包
```

---

## 安装方式

### 方式一：直接下载安装包（推荐普通用户）

从 GitHub Releases 下载预编译二进制：

| 平台 | 文件 | 链接 |
|------|------|------|
| macOS (Apple Silicon) | `.dmg` | [v0.9.0-beta.2-darwin-arm64.dmg](https://github.com/moeru-ai/airi/releases/download/v0.9.0-beta.2/AIRI-0.9.0-beta.2-darwin-arm64.dmg) |
| Windows (x64) | `.exe` 安装器 | [v0.9.0-beta.2-windows-x64-setup.exe](https://github.com/moeru-ai/airi/releases/download/v0.9.0-beta.2/AIRI-0.9.0-beta.2-windows-x64-setup.exe) |
| Linux | 见 Releases 页面 | [Latest Release](https://github.com/moeru-ai/airi/releases/latest) |

### 方式二：浏览器直接用（零安装）

打开 https://airi.moeru.ai 即可体验 Web 版（PWA，可安装到桌面）。

### 方式三：Scoop 安装（Windows）

```powershell
scoop bucket add airi https://github.com/moeru-ai/airi
scoop install airi/airi
```

### 方式四：Nix 安装

```shell
# 启用 flakes 后
nix run github:moeru-ai/airi
```

### 方式五：从源码构建（开发者）

#### 前置依赖

| 工具 | 版本要求 | 安装方式 |
|------|---------|---------|
| Node.js | 23+ | `brew install node` |
| pnpm | corepack 管理 | `corepack enable && corepack prepare pnpm@latest --activate` |
| Git | 最新 | `brew install git` |

**macOS 完整流程**:

```shell
# 1. 克隆仓库
git clone https://github.com/moeru-ai/airi.git
cd airi

# 2. 启用 corepack + 安装依赖
corepack enable
pnpm install

# 3a. 启动浏览器版开发服务器
pnpm dev

# 3b. 或启动桌面版（需 Rust 工具链）
pnpm dev:tamagotchi

# 4. 或启动文档站
pnpm dev:docs
```

**Windows 额外步骤**（桌面版需要）:

```powershell
# 安装 Visual Studio（含 Windows SDK + C++ build tools）
scoop install git nodejs rustup
rustup toolchain install stable-x86_64-pc-windows-msvc
rustup default stable-x86_64-pc-windows-msvc
```

**Linux 桌面版额外依赖**:

```shell
sudo apt install libssl-dev libglib2.0-dev libgtk-3-dev \
  libjavascriptcoregtk-4.1-dev libwebkit2gtk-4.1-dev
```

### 方式六：移动端（iOS）

```shell
# 需 Xcode + iOS Simulator/设备
pnpm dev:pocket:ios --target <DEVICE_ID_OR_SIMULATOR_NAME>
# 查看可用设备
pnpm exec cap run ios --list
```

---

## Telegram Bot 搭建（额外服务）

Telegram Bot 需要 PostgreSQL 数据库：

```shell
cd services/telegram-bot
docker compose up -d        # 启动 PostgreSQL
cp .env .env.local          # 配置凭据
pnpm -F @proj-airi/telegram-bot db:generate  # 生成迁移
pnpm -F @proj-airi/telegram-bot db:push      # 执行迁移
pnpm -F @proj-airi/telegram-bot start        # 启动 bot
```

---

## 支持的 LLM Provider（30+）

AIHubMix (推荐) / OpenRouter / vLLM / SGLang / Ollama / OpenAI / Anthropic Claude / DeepSeek / Qwen / Google Gemini / xAI / Groq / Mistral / Cloudflare Workers AI / Together.ai / 硅基流动 / 智谱 / 阶跃星辰 / 百川 / Minimax / 月之暗面 / 魔搭社区 / 腾讯混元 等。

---

## 同类开源项目

| 项目 | 亮点 |
|------|------|
| [kimjammer/Neuro](https://github.com/kimjammer/Neuro) | 最完善的 Neuro-Sama 复刻 |
| [SugarcaneDefender/z-waif](https://github.com/SugarcaneDefender/z-waif) | 游戏能力、自主代理、prompt 工程 |
| [semperai/amica](https://github.com/semperai/amica/) | VRM + WebXR |
| [elizaOS/eliza](https://github.com/elizaOS/eliza) | 多系统集成、软件工程实践 |
| [InsanityLabs/AIVTuber](https://github.com/InsanityLabs/AIVTuber) | 优秀的 UI/UX |
| [Open-LLM-VTuber](https://github.com/t41372/Open-LLM-VTuber) | 本地推理支持 |

---

## 子项目生态（@proj-airi 组织）

- [Awesome AI VTuber](https://github.com/proj-airi/awesome-ai-vtuber) - AI VTuber 精选列表
- [unspeech](https://github.com/moeru-ai/unspeech) - ASR/TTS 统一代理（类似 LiteLLM）
- [xsai](https://github.com/moeru-ai/xsai) - LLM 交互库（类似 Vercel AI SDK）
- [MCP Launcher](https://github.com/moeru-ai/mcp-launcher) - MCP 构建器/启动器
- [SAD](https://github.com/moeru-ai/sad) - 自托管 LLM 文档笔记
- 更多见笔记上方正文

---

## 安全与隐私

- 项目声明**无任何官方加密货币或代币**
- 数据存储在本地（浏览器端 DuckDB）或自托管服务器
- LLM API Key 由用户自行配置，不经过第三方
