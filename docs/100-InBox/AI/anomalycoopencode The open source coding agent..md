---
title: "anomalyco/opencode: The open source coding agent."
source: "https://github.com/anomalyco/opencode/blob/dev/README.zh.md"
author:
  - "[[GitHub]]"
published:
created: 2026-03-31
description: "The open source coding agent. Contribute to anomalyco/opencode development by creating an account on GitHub."
tags:
  - "clippings"
---
[![[images/d90884254ea8793c1905188e3a0aadba_MD5.svg]]](https://opencode.ai/)

開源的 AI Coding Agent。

[英文](https://github.com/anomalyco/opencode/blob/dev/README.md) | [简体中文](https://github.com/anomalyco/opencode/blob/dev/README.zh.md) | [繁體中文](https://github.com/anomalyco/opencode/blob/dev/README.zht.md) | [韓國어](https://github.com/anomalyco/opencode/blob/dev/README.ko.md) | [德語](https://github.com/anomalyco/opencode/blob/dev/README.de.md) | 西班牙 [語](https://github.com/anomalyco/opencode/blob/dev/README.es.md) | [法語](https://github.com/anomalyco/opencode/blob/dev/README.fr.md) | [義大利語](https://github.com/anomalyco/opencode/blob/dev/README.it.md) | [丹麥](https://github.com/anomalyco/opencode/blob/dev/README.da.md) | [日本語](https://github.com/anomalyco/opencode/blob/dev/README.ja.md) | [波蘭](https://github.com/anomalyco/opencode/blob/dev/README.pl.md) | [Русский](https://github.com/anomalyco/opencode/blob/dev/README.ru.md) | [波薩斯基](https://github.com/anomalyco/opencode/blob/dev/README.bs.md) | [العربية](https://github.com/anomalyco/opencode/blob/dev/README.ar.md) | [挪威](https://github.com/anomalyco/opencode/blob/dev/README.no.md) | [葡萄牙（巴西）](https://github.com/anomalyco/opencode/blob/dev/README.br.md) | [ไทย](https://github.com/anomalyco/opencode/blob/dev/README.th.md) | [土耳其人](https://github.com/anomalyco/opencode/blob/dev/README.tr.md) | [Українська](https://github.com/anomalyco/opencode/blob/dev/README.uk.md) | [বাংলা](https://github.com/anomalyco/opencode/blob/dev/README.bn.md) | [Ελληνικά](https://github.com/anomalyco/opencode/blob/dev/README.gr.md) | [清越](https://github.com/anomalyco/opencode/blob/dev/README.vi.md)

[![[images/d2b9412dccf110e78ad6b466aade6d6b_MD5.png]]](https://opencode.ai/)

---

### 安裝

```
# 直接安装 (YOLO)
curl -fsSL https://opencode.ai/install | bash

# 软件包管理器
npm i -g opencode-ai@latest        # 也可使用 bun/pnpm/yarn
scoop install opencode             # Windows
choco install opencode             # Windows
brew install anomalyco/tap/opencode # macOS 和 Linux（推荐，始终保持最新）
brew install opencode              # macOS 和 Linux（官方 brew formula，更新频率较低）
sudo pacman -S opencode            # Arch Linux (Stable)
paru -S opencode-bin               # Arch Linux (Latest from AUR)
mise use -g opencode               # 任意系统
nix run nixpkgs#opencode           # 或用 github:anomalyco/opencode 获取最新 dev 分支
```

> [!tip] Tip
> 安裝前請先移除 0.1.x 之前的舊版本。

### 桌面應用程式 （BETA）

OpenCode 也提供桌面版應用。 可直接從 [發佈頁 （releases page）](https://github.com/anomalyco/opencode/releases) 或 [opencode.ai/download](https://opencode.ai/download) 下載。

| 平台 | 下載檔 |
| --- | --- |
| macOS（Apple Silicon） | `opencode-desktop-darwin-aarch64.dmg` |
| macOS（英特爾） | `opencode-desktop-darwin-x64.dmg` |
| 窗戶 | `opencode-desktop-windows-x64.exe` |
| Linux | `.deb` 、`.rpm` 或 AppImage |

```
# macOS (Homebrew Cask)
brew install --cask opencode-desktop
# Windows (Scoop)
scoop bucket add extras; scoop install extras/opencode-desktop
```

#### 安裝目錄

安裝文稿按照以下優先順序決定安裝路徑：

1. `$OPENCODE_INSTALL_DIR` - 自定義安裝目錄
2. `$XDG_BIN_DIR` - 符合 XDG 基礎目錄規範的路徑
3. `$HOME/bin` - 如果存在或可創建的使用者二進位目錄
4. `$HOME/.opencode/bin` - 預設備用路徑
```
# 示例
OPENCODE_INSTALL_DIR=/usr/local/bin curl -fsSL https://opencode.ai/install | bash
XDG_BIN_DIR=$HOME/.local/bin curl -fsSL https://opencode.ai/install | bash
```

### 代理商

OpenCode 內置兩種 Agent，可用 `Tab` 鍵快速切換：

- **build** - 預設模式，具備完整許可權，適合開發工作
- **plan** - 只讀模式，適合代碼分析與探索
	- 默認拒絕修改檔
		- 運行 bash 命令前會詢問
		- 便於探索未知代碼庫或規劃改動

另外還包含一個 **general** 子 Agent，用於複雜搜索和多步任務，內部使用，也可在消息中輸入 `@general` 調用。

瞭解更多 [Agents](https://opencode.ai/docs/agents) 相關信息。

### 文件

更多配置說明請查看我們的 [**官方文檔**](https://opencode.ai/docs) 。

### 參與貢獻

如有興趣貢獻代碼，請在提交 PR 前閱讀 [貢獻指南 （Contributing Docs）](https://github.com/anomalyco/opencode/blob/dev/CONTRIBUTING.md) 。

### 基於 OpenCode 進行開發

如果你在專案名中使用了 “opencode”（如 “opencode-dashboard” 或 “opencode-mobile”），請在 README 里註明該專案不是 OpenCode 團隊官方開發，且不存在隸屬關係。

### 常見問題 （FAQ）

#### 這和 Claude Code 有什麼不同？

功能上很相似，關鍵差異：

- 100% 開源。
- 不綁定特定供應商。 推薦使用 [OpenCode Zen](https://opencode.ai/zen) 的模型，但也可搭配 Claude、OpenAI、Google 甚至本地模型。 模型反覆運算會縮小差異、降低成本，因此保持 provider-agnostic 很重要。
- 內置 LSP 支援。
- 聚焦終端介面 （TUI）。 OpenCode 由 Neovim 愛好者和 [terminal.shop](https://terminal.shop/) 的建立者打造，會持續探索終端的極限。
- 用戶端/伺服器架構。 可在本機運行，同時用行動裝置遠端驅動。 TUI 只是眾多潛在用戶端之一。

---

**加入我們的社區** [飛書](https://applink.feishu.cn/client/chat/chatter/add_by_link?link_token=738j8655-cd59-4633-a30a-1124e0096789&qr_code=true) | [X.com](https://x.com/opencode)