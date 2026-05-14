---
title: Hermes Agent Computer Use 深度解析 — CUA Driver 与 Set-of-Mark 架构
aliases: [Hermes CUA Driver, AI Computer Use 开源方案, Hermes Agent 原生桌面操控]
tags:
  - ai/agent
  - ai/computer-use
  - hermes-agent
  - macos/automation
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=8mvoG0U40gU"
author: Panda Making Money
created: 2025-05-14
updated: 2025-05-14
description: Hermes Agent 通过 CUA Driver 实现 macOS 原生应用后台操控，SOM 视觉定位让 computer use 从脆弱 demo 变为可用的生产工具
level: intermediate
stars: 4
---

# Hermes Agent Computer Use 深度解析 — CUA Driver 与 Set-of-Mark 架构

> Hermes Agent 通过集成 CUA Driver（TrickU，YC S25），实现了在真实 Mac 上后台操控原生应用的能力。用户和 Agent 可以同时使用同一台机器，光标不移动、窗口不置顶、Space 不切换。配合 SOM（Set-of-Mark）视觉定位层，Agent 不是猜测坐标，而是通过编号元素地图精确交互。

---

## 目录

- [行业背景：Computer Use 的闭源困境](#行业背景computer-use-的闭源困境)
- [两大开源项目：Hermes Agent + TrickU CUA Driver](#两大开源项目hermes-agent--tricku-cua-driver)
- [CUA Driver 架构：Skylight SPI 后台事件投递](#cua-driver-架构skylight-spi-后台事件投递)
- [SOM 视觉定位：从坐标猜测到元素地图](#som-视觉定位从坐标猜测到元素地图)
- [五个实际用例](#五个实际用例)
- [Token 经济学：四层优化从 60 万降到 3 万](#token-经济学四层优化从-60-万降到-3-万)
- [模型无关性：任何 Vision 模型都能驱动](#模型无关性任何-vision-模型都能驱动)
- [已知限制与风险](#已知限制与风险)
- [总结](#总结)

---

## 行业背景：Computer Use 的闭源困境

所有主流 AI 实验室都在构建 Computer Use，但都带有 Vendor Lock-in：

| 平台 | 形式 | 锁定成本 |
|------|------|----------|
| Anthropic | Claude Computer Use API | 自己的 Schema、模型、定价 |
| OpenAI | Operator（闭源消费产品）+ Codex | 不控制基础设施，不能选模型 |
| Google | Gemini Agents in Workspace | 绑定 Google 生态 |
| Microsoft | Copilot Studio | 绑定 Microsoft 生态 |

**核心问题：** 这些闭源实现给的是「模型」而非「基础设施」。你拿到的是能解释截图的模型，但 sandbox、审批层、上下文管理、token 效率层都要自己搭。

> 闭源平台解决的是问题，但解决的方式让你永久依赖它们。

---

## 两大开源项目：Hermes Agent + TrickU CUA Driver

### Hermes Agent（Nous Research）

- 自我改进的开源自主 Agent
- 有大脑（推理）、记忆、学习循环、多平台触达（Telegram/Discord）
- 可在 $5 VPS 上运行

### TrickU（YC Spring 2025）

开源 Computer Use 基础设施公司，核心洞察：**模型不是瓶颈，基础设施才是。**

**TrickU 仓库的五大组件：**

| 组件 | 功能 | 关键特性 |
|------|------|----------|
| **Loom** | Apple Silicon 虚拟机管理 | 基于 Apple Virtualization Framework，97% 原生 CPU 速度 |
| **CUA Driver** | 原生 Mac 后台输入驱动 | 不需要 VM，操作物理机上的真实应用 |
| **SOM** | Set-of-Mark 视觉定位 | YOLO + EasyOCR，编号元素地图 |
| **MCP Server** | Model Context Protocol 接口 | 任何 MCP 客户端（如 Claude Code）都能用 |
| **CUA Bench** | 基准测试框架 | 衡量 Agent 在真实 Computer Use 任务上的表现 |

```
┌─────────────────────────────────────────────┐
│                Hermes Agent                  │
│  (大脑 / 记忆 / 学习 / 多平台触达)            │
│                    │                         │
│              MCP (stdio)                     │
│                    │                         │
│            ┌───────▼────────┐                │
│            │  CUA Driver    │                │
│            │ (TrickU)       │                │
│            │                │                │
│            │  Skylight SPIs │                │
│            │  SOM Pipeline  │                │
│            │  AX Tree       │                │
│            └───────┬────────┘                │
│                    │                         │
│            ┌───────▼────────┐                │
│            │  macOS 原生应用  │                │
│            │ Mail, Safari   │                │
│            │ VS Code, etc.  │                │
│            └────────────────┘                │
└─────────────────────────────────────────────┘
```

---

## CUA Driver 架构：Skylight SPI 后台事件投递

### 传统方案的问题

标准 Mac 自动化使用 `CGEventPost` → 投递到全局 HID 事件流：

```
CGEventPost → 全局 HID Stream
                │
                ├── 光标移动到坐标
                ├── 目标窗口置顶
                ├── 切换 Space
                ├── 键盘焦点改变
                └── 前台应用改变
```

**对「边用电脑边让 Agent 工作」的场景完全不可用。**

### CUA Driver 的方案：Skylight Private SPIs

```
CUA Driver → SLPostEventToPID / SLPSEventPostRecord2
                │
                ├── 事件直接投递到目标进程
                ├── 光标不动
                ├── 窗口不置顶
                ├── Space 不切换
                └── 你和 Agent 真正并行工作
```

**第三个关键 SPI：** `_AXObserverAddNotification` — 让 Agent 监控不在前台、甚至被遮挡/隐藏的应用的 Accessibility Tree。这对 Electron 应用（Notion、Linear、Slack、VS Code）至关重要，否则窗口隐藏时 AX Tree 会崩溃。

### 性能特征

| 指标 | 数值 |
|------|------|
| 每次事件延迟 | 5-20ms |
| 对比 HID 直投 | 略慢，但 Agent 每次动作间隔数百毫秒（模型推理时间） |
| 结论 | **瓶颈是推理时间，不是事件路由时间** |

---

## SOM 视觉定位：从坐标猜测到元素地图

### 问题：坐标式 Computer Use 为什么不可靠

```
Agent 看截图 → 猜按钮位置 → 输出坐标 → 点击
                │
                ├── 通知弹窗导致 UI 偏移 12px → 点错
                ├── 元素位置变化 → 点错
                └── 模型「记住」的坐标已过时 → 点错
```

### SOM 的解决方案

```
截图 → 视觉 Pipeline → 编号元素地图
                          │
                          ├── Button [1]
                          ├── Text Field [2]
                          ├── Dropdown [3]
                          ├── Row [4], [5], [6]
                          └── ...

Agent: "点击元素 14" → Driver 查表 → 精确坐标 → 执行
```

### 视觉 Pipeline：两条路线

**路线 A：YOLO + EasyOCR（默认，Apple Silicon 优化）**

| 技术 | 功能 | 优化 |
|------|------|------|
| YOLO 目标检测 | 识别按钮、图标、复选框、滑块等 UI 元素 | Metal Performance Shaders (GPU) |
| EasyOCR | 读取屏幕上的文字标签 | 关联文字与视觉元素 |

**路线 B：Omni Loop（Microsoft OmniParser）**

- 更通用的 UI 解析模型
- 适用场景：使用没有 Computer Use 训练的 VLM
- 外部完成 grounding，语言模型只需读编号地图

### SOM 工作流程

```
Agent 调用 capture(mode='som', app='Mail')
    │
    ▼
Driver 截图 → YOLO 检测 → EasyOCR 识别 → 叠加编号标签
    │
    ▼
返回：标注截图 + 结构化元素索引（编号/标签/坐标）
    │
    ▼
语言模型读地图 → 识别目标元素 → 调用 action(element=14)
    │
    ▼
Driver 解析索引 → 精确坐标 → 执行操作
    │
    ▼
Agent 再次 capture → 验证结果 → 规划下一步
```

> Agent 不是在幻觉坐标，而是在读取当前界面状态的全新结构化地图并精确操作。

---

## 五个实际用例

### 用例 1：后台邮件分拣

- Agent 在后台打开 Mail → SOM 导航侧边栏和邮件列表 → 通过 AX Tree 读取正文 → 生成摘要和回复建议 → 推送到 Telegram/Discord
- **不需要 OAuth 或 API**，适用于任何原生邮件客户端

### 用例 2：通过 Safari 发布 YouTube 视频

- 提供视频文件、标题、描述、标签、定时发布时间
- Agent 打开 Safari → YouTube Studio → 上传流程 → 填充所有字段 → 提交
- **不需要 YouTube API Key**，破坏性操作需审批

### 用例 3：自动发票处理

- Agent 读取 PDF 发票（视觉能力） → 提取字段 → 打开会计软件 → SOM 导航 → 填入数据 → 保存
- 适用于任何原生 Mac 会计软件

### 用例 4：应用监控与告警

- Cron 定时任务 → 每 30 分钟 capture 目标应用 → 读取指标 → 与基线对比 → 超阈值则发送告警
- **适用于没有 API / Webhook 的内部工具**

### 用例 5：社交媒体定时发布（含审批）

- 提供内容日历、帖子草稿、媒体附件、目标平台
- Agent 后台打开各平台 → 填写内容 → 附加媒体 → 发布或定时
- **每条帖子通过审批按钮确认后才发布**

> 这五个用例的共同点：**不需要 API、不需要浏览器扩展、不需要软件厂商支持自动化。** 能力来自「像人一样操作软件界面」。

---

## Token 经济学：四层优化从 60 万降到 3 万

### 问题

每次 capture 的截图作为 tool result 发送给语言模型。1568×900 像素截图在 Anthropic API 计为 ~1500 tokens。

```
20 步操作 session（无优化）:
  每步 3 张截图 × 20 步 × 1500 tokens = ~600,000 tokens
```

**这不是工作流，这是账单。**

### 四层优化

| 层级 | 策略 | 效果 |
|------|------|------|
| **1. 截图驱逐** | 只保留最近 3 张截图，旧截图替换为文字占位符 | 大幅减少视觉 payload |
| **2. 客户端压缩修剪** | Context compressor 剥离旧 tool result 的图片部分，保留文字（元素索引、操作确认、结构化数据） | 保留语义，丢弃视觉 |
| **3. 图像感知 Token 估算** | 按 Anthropic 固定费率 1500 tokens/图估算，而非 base64 字符长度 | 避免过早触发压缩 |
| **4. 服务端上下文编辑** | Anthropic API 的 `clear tool uses` 标志，服务端清理旧 tool result | 双重防线 |

```
优化前: ~600,000 tokens / 20 步 session
优化后:  ~30,000 tokens / 20 步 session
         ↓
      约 20 倍缩减
```

> 本地模型（vLLM / LM Studio）token 成本为零。

---

## 模型无关性：任何 Vision 模型都能驱动

### 架构设计

```
CUA Driver MCP Interface（模型中性 Schema）
    │
    ├── OpenAI 风格 image URL parts（通用格式）
    │
    ├── Anthropic Adapter（自动转换为原生 image blocks）
    │
    └── Hermes 内部协议翻译（透明，无需配置）
```

### 支持的模型

| 模型 | 支持程度 | 备注 |
|------|----------|------|
| Claude Sonnet / Opus | 最佳体验 | 强视觉推理 + 指令遵循 |
| OpenRouter 200+ 模型 | 完整支持 | 单 API Key |
| GPT-4 / GPT-5 | 完整支持 | — |
| 本地 VLM (vLLM / LM Studio) | 完整支持 | 需支持 multimodal tool content |
| **纯文本模型** | 降级可用 | 切换到 AX Tree 模式，无截图 |

### 纯文本降级模式

```
capture_mode = 'ax'
    │
    ├── Agent 不接收截图
    ├── Quad Driver 读取完整 Accessibility Tree
    ├── 返回结构化文本（元素标签/角色/值）
    └── Token 成本接近零
```

> 一条命令 `hermes model` 切换模型，Computer Use 工具集无需任何配置变更。

---

## 已知限制与风险

### 1. 平台限制：仅 macOS

- Skylight SPI 是 Apple 专有的，Linux/Windows 不可用
- Hermes 在 Linux VPS 上运行时，Computer Use 不可用（可用 Browser 工具集替代）

### 2. Private API 依赖风险

- Apple 不保证 Skylight SPI 在未来 macOS 版本中存在
- **缓解：** 通过环境变量 `CUA_DRIVER_VERSION` 锁定驱动版本

### 3. macOS Tahoe 26.4.1 Bug

- ScreenCaptureKit 存在 bug，截图失败（SCStream error）
- **Workaround：** 设置 `capture_mode='ax'`，降级到 AX Tree 模式

### 4. 安全约束

| 约束类型 | 具体限制 |
|----------|----------|
| 硬阻断快捷键 | 清空废纸篓、强制删除、锁屏、注销 |
| 硬阻断输入模式 | `curl \| bash`、`sudo rm` 等 shell payload |
| 禁止操作 | macOS 权限对话框、密码输入 |
| 防注入 | 不遵循截图中嵌入的指令 |

### 5. SOM 索引生命周期

- 元素索引仅在下次 capture 前有效
- 不能「缓存索引，几步后再用」
- 工作流必须是紧密的 **observe-act-verify 循环**

### 6. 后台模式延迟

- 每次 Skylight 事件路由 5-20ms
- 对 Agent 操作完全无感知（推理时间远大于此）
- 不适用于高频输入场景（不是设计目标）

---

## 总结

### 核心架构对比

```
传统 Mac 自动化:
  CGEventPost → 全局 HID → 光标移动/窗口置顶/Space 切换
  ✗ 不能与人并行工作

CUA Driver:
  Skylight SPI → 直接投递目标进程 → 光标不动/窗口不变
  ✓ 真正的后台并行操作

坐标式 Computer Use:
  截图 → 猜坐标 → 经常点错 → 不可靠

SOM Computer Use:
  截图 → 编号元素地图 → "点击元素 14" → 精确可靠
```

### 关键数字

| 指标 | 数值 |
|------|------|
| Token 优化倍率 | 20x（60 万 → 3 万） |
| 后台事件延迟 | 5-20ms |
| Loom VM 性能 | 97% 原生 CPU |
| SOM 元素索引保留 | 仅到下次 capture |
| 截图驱逐策略 | 保留最近 3 张 |

### 最佳实践

✅ **推荐：**
- 生产环境锁定 CUA Driver 版本
- 复杂界面用 Claude Sonnet/Opus（最佳视觉推理）
- 简单结构化工作流用便宜的 OpenRouter 模型降本
- 敏感数据用本地模型，数据不出机器
- 工作流设计为紧密的 observe-act-verify 循环

❌ **避免：**
- 期望跨平台原生桌面操控（目前仅 macOS）
- 在高频输入场景使用 CUA Driver
- 缓存 SOM 元素索引跨多步使用
- 在未锁定版本的生产环境升级 macOS

---

## 参考资料

- [YouTube: Hermes Agent Just Got Hands — This Changes Everything](https://www.youtube.com/watch?v=8mvoG0U40gU) — Panda Making Money, 2026-05-13
- [Hermes Agent GitHub](https://github.com/nousresearch/hermes-agent) — Nous Research
- [CUA Driver GitHub](https://github.com/nousresearch/cua-driver) — TrickU (YC S25)

## 相关笔记

- [[AI 多工具工作流 — 五类任务分工地图]]
- [[Coding Agent 对比评测]]
