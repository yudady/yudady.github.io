---
title: Hermes Agent + CUA Driver — 开源 AI Agent 的后台桌面控制
aliases: [Hermes Computer Use, CUA Driver, macOS Background Agent, Hermes Agent Hands]
tags:
  - ai/agent
  - hermes-agent
  - computer-use
  - macos
  - cua-driver
  - nous-research
  - status/active
  - type/learning-note
source:
  - "https://www.youtube.com/watch?v=8mvoG0U40gU"
  - "https://hermes-agent.nousresearch.com/docs/user-guide/features/computer-use"
  - "https://github.com/trycua/cua"
author: Julian Goldie (视频作者), Nous Research / TrickUA (项目方)
created: 2026-05-14
updated: 2026-05-14
description: 深度解析 Hermes Agent 集成 CUA Driver 实现开源 AI Agent 后台操控 macOS 原生应用的技术架构，包括 SkyLight SPI、Set of Mark 视觉定位、Token 优化四层策略
level: intermediate
stars: 4
---

# Hermes Agent + CUA Driver — 开源 AI Agent 的后台桌面控制

> 开源 AI Agent Hermes Agent 集成 CUA Driver，实现了在 macOS 上后台操控原生应用——光标不动、窗口不抢、Space 不切，你和 AI 真正并行工作。支持任意模型（Claude/GPT/Gemini/本地开源），零遥测，MIT 协议。

---

## 目录

- [行业背景：Computer Use 的竞赛与锁](#行业背景computer-use-的竞赛与锁)
- [两个开源项目：Hermes Agent + CUA](#两个开源项目hermes-agent--cua)
- [CUA 技术栈拆解](#cua-技术栈拆解)
- [CUA Driver：为什么不是普通自动化](#cua-driver为什么不是普通自动化)
- [Set of Mark：让 Computer Use 从脆弱变可靠](#set-of-mark让-computer-use-从脆弱变可靠)
- [五个实际用例](#五个实际用例)
- [Token 经济学：从 60 万降到 3 万](#token-经济学从-60-万降到-3-万)
- [模型无关性设计](#模型无关性设计)
- [已知限制与风险](#已知限制与风险)
- [参考资料](#参考资料)

---

## 行业背景：Computer Use 的竞赛与锁

所有主要 AI 实验室都在做 Computer Use（计算机操控）：

| 公司 | 产品 | 形态 |
|------|------|------|
| Anthropic | Claude Computer Use | API（给截图 → 返回点击坐标，需自建循环） |
| OpenAI | Operator / Codex | 闭源消费产品 + 后台浏览器操控 |
| Google | Gemini in Workspace | 嵌入 Docs/Sheets/Gmail/Calendar |
| Microsoft | Copilot Studio | 闭源平台 |
| Salesforce | Agent Force | 闭源平台 |

**共同问题**：全部带有 vendor lock-in。Anthropic 的 schema 是专有的，OpenAI 的 Operator 是闭源的，Google/Microsoft 绑定各自的生态。而且它们提供的是**模型能力**而非**基础设施**——你仍需自己搭建沙箱、审批层、上下文管理、Token 优化。

---

## 两个开源项目：Hermes Agent + CUA

### Hermes Agent（Nous Research）

```
定位：自改进的自主 AI Agent
核心能力：
  - 内置学习循环（从经验中创建 Skills）
  - 跨平台触达（Telegram/Discord/CLI）
  - 多模型支持（Claude/GPT/Gemini/本地模型）
  - $5 VPS 即可运行
  - 零遥测，MIT 协议
GitHub: github.com/NousResearch/hermes-agent
```

### CUA / TrickUA（Y Combinator S25）

```
定位：Computer Use Agent 的基础设施层
使命：构建训练和评估 AI Agent 操控完整操作系统的基础设施
组成：
  - Loom    → Apple Silicon 虚拟机管理（97% 原生性能）
  - CUA Driver → macOS 后台输入驱动（本视频核心）
  - Set of Mark → 视觉定位库
  - MCP Server → 协议边界，任何 MCP 客户端可接入
  - CUA Bench  → 基准测试工具
协议：MIT 开源
GitHub: github.com/trycua/cua
```

**关键洞察**：模型已不是瓶颈，**基准测试、沙箱、基础设施**才是。TrickUA 做的就是后者。

---

## CUA 技术栈拆解

```
+----------------------------------------------------------+
|  Hermes Agent（大脑）                                     |
|  LLM 推理 / 记忆 / Skills / 多平台消息                    |
+----------------------------------------------------------+
|                    MCP over stdio                         |
+----------------------------------------------------------+
|  computer_use toolset（封装层）                            |
|  capture / click / type / key / scroll / drag / focus    |
+----------------------------------------------------------+
|  CUA Driver（驱动层）                                      |
|  SkyLight SPI 输入注入 + AX Tree 监控                     |
+----------------------------------------------------------+
|  Set of Mark（视觉定位层）                                  |
|  YOLO 图标检测 + EasyOCR + Metal Shader                   |
+----------------------------------------------------------+
|  macOS（目标系统）                                         |
|  原生应用：Mail / Safari / VS Code / Notion / ...          |
+----------------------------------------------------------+
```

---

## CUA Driver：为什么不是普通自动化

### 朴素方案的问题：CGEventPost

```
CGEventPost（传统方案）：
  注入事件到全局 HID 事件流
  ↓
  光标移动到坐标 → 窗口跳到前台 → Space 切换
  → 键盘焦点改变 → 前台应用改变
  → 你无法同时使用电脑

  对一次性脚本：可以忍受
  对并行工作 Agent：完全不可用
```

### CUA Driver 的方案：SkyLight 私有 SPI

```
CUA Driver 方案：
  使用 SkyLight 私有 SPI 直接投递到目标进程
  ↓
  SLEventPostToPid()       → 按进程 ID 投递事件
  SLPSPostEventRecordTo()  → 投递结构化事件记录
  ↓
  目标应用收到点击/输入/滚动/拖拽（和用户操作一样）
  但 macOS 不视为全局事件
  ↓
  光标不动 ✓  窗口不变 ✓  Space 不切 ✓
  你和 Agent 真正并行工作
```

### 第三个 SPI：AX Tree 监控

```
_AXObserverAddNotificationAndCheckRemote():
  - 监控非前台应用的无障碍通知
  - 关键能力：即使窗口被遮挡/隐藏，AX Tree 仍然活跃
  - 解决 Electron 应用（Notion/Linear/Slack/VS Code）的
    隐藏窗口 AX Tree 坍塌问题
```

### 性能特征

```
事件路由延迟：5-20ms（vs HID 直投 <1ms）
Agent 每次动作间隔：数百毫秒（模型推理时间）
  → 5-20ms 延迟完全不可感知
  → 瓶颈在推理，不在事件路由
```

---

## Set of Mark：让 Computer Use 从脆弱变可靠

### 问题：坐标猜测

```
朴素 Computer Use 流程：
  截图 → 模型看像素 → 猜坐标 → 点击
  ↓
  有时点对，有时点错
  UI 偏移 12 像素（通知横幅？）→ 全错
  → 大量开发者因此认为 Computer Use 不成熟
```

### 解决方案：结构化元素地图

```
Set of Mark 流程：
  截图 → 视觉管线处理 → 生成编号元素地图
  ↓
  [1] 菜单栏: Apple
  [2] 菜单栏: Mail
  [3] 按钮: 新建邮件
  [4] 文本框: 搜索
  [5] 侧边栏: 收件箱 (14)
  [6] 侧边栏: 已发送
  ...
  ↓
  模型说："click element 5"（而不是猜坐标）
  Driver 查地图 → 解析精确坐标 → 执行点击
```

### SOM 的两个底层技术

```
1. YOLO 目标检测模型
   - You Only Look Once → 单次前向传播识别+定位
   - 扫描截图中的 UI 原语：按钮/复选框/图标/开关/滑块
   - 针对 UI 元素微调

2. EasyOCR 文本识别
   - 识别元素上的文字标签
   - 通过 Metal Performance Shaders 针对 Apple Silicon 优化
   - 结合 YOLO 输出 → 每个元素 = 编号 + 标签 + 类型 + 坐标
```

### Agent 循环

```
capture(mode="som")
  → 返回带编号叠加层的截图 + AX Tree 索引
  → 模型读取元素地图 → 选择操作目标
  → click(element=14)
  → Driver 解析 element 14 → 执行点击
  → capture() 验证结果 → 规划下一步

关键：SOM 索引仅在下次 capture 前有效
  → 每次状态变更后必须重新 capture
  → 这不是 bug，是设计（防止过期索引导致错误操作）
```

---

## 五个实际用例

### 1. 邮件分类（不开邮箱）

```
你说："整理收件箱，标记紧急邮件，总结内容，起草回复"
Agent：
  - 后台打开 Mail → SOM 导航侧边栏和邮件列表
  - 通过 AX Tree 读取邮件正文
  - 生成结构化摘要 → 推送到 Telegram
  - 草拟回复等你审批

关键：不需要 Gmail OAuth / API 权限
      适用于任何原生邮件客户端
```

### 2. YouTube 发布（不碰浏览器）

```
你给：视频文件 + 标题 + 描述 + 标签 + 定时发布时间
Agent：
  - 后台打开 Safari → YouTube Studio
  - 完整走上传流程：填标题/贴描述/加标签/设缩略图/配置可见性
  - 危险操作前弹出确认

关键：不需要 YouTube API Key / 第三方工具
```

### 3. 发票处理（自动录入会计软件）

```
你给：PDF 发票文件夹
Agent：
  - 读取每份 PDF（视觉能力提取字段）
  - 打开会计软件 → SOM 导航到录入界面
  - 逐字段填写：供应商/发票号/日期/行项目/金额
  - 保存记录

关键：适用于任何原生 Mac 会计软件，不需要 API/导出格式支持
```

### 4. 仪表盘监控（定时巡检）

```
你设：每 30 分钟检查某个内部工具
Agent（cron 触发）：
  - capture 目标应用 → 读取数值
  - 与基线对比 → 超阈值则发告警
  - 正常则继续休眠

关键：适用于无 API / 无 Webhook / 无监控集成的内部工具
      只要人能读屏幕，Agent 就能监控
```

### 5. 社交媒体调度（跨平台发布）

```
你给：内容日历 + 草稿 + 素材 + 目标平台
Agent：
  - 后台打开各平台（Safari / 原生 App）
  - 导航到发布界面 → 填写内容 → 附加素材
  - 定时或立即发布（每次需你确认）

关键：不需要各平台 API / 浏览器扩展
      危险操作走审批层，不是无监督发布
```

**共同点**：不需要 API、不需要浏览器扩展、不需要软件厂商支持自动化。只要人能操作的界面，Agent 就能操作。

---

## Token 经济学：从 60 万降到 3 万

### 问题

```
每次 capture → 截图进入上下文
Anthropic 定价：每张图 ≈ 1,500 tokens（固定费率）
20 步操作 × 3 张截图/步（前/后/验证）= ~60 张图
  → 60 × 1,500 = 90,000 tokens（仅截图）
朴素实现（无优化）：20 步会话 ≈ 600,000 tokens
  → 那不是工作流，那是账单
```

### Hermes 的四层优化

```
+---------------------------+------------------------------------------+
| 层 1：截图驱逐            | 仅保留最近 3 张截图在上下文中              |
|                           | 旧截图替换为 "[screenshot removed]"       |
+---------------------------+------------------------------------------+
| 层 2：客户端压缩修剪      | 多模态工具结果中，仅删除图片部分           |
|                           | 保留文本（元素索引/操作确认/提取数据）     |
+---------------------------+------------------------------------------+
| 层 3：图像感知 Token 估算 | 按 Anthropic 固定费率（~1500 tokens/图）   |
|                           | 而非 base64 字符长度估算                   |
|                           | 避免过早触发压缩                           |
+---------------------------+------------------------------------------+
| 层 4：服务端上下文清理    | 启用 Anthropic API 的                      |
|                           | clear_tool_uses 标志                      |
|                           | 服务端主动清除旧工具结果                   |
+---------------------------+------------------------------------------+

结果：20 步会话从 ~600K 降至 ~30K tokens（降低 ~20 倍）
      1568×900 分辨率，当前 Claude 定价 → 日常可持续使用
      本地模型 → Token 成本为零
```

---

## 模型无关性设计

### 为什么重要

```
Anthropic Computer Use → 专有 schema，绑定 Claude API
OpenAI Codex → 专有工具约定，绑定 OpenAI API
  → 换模型 = 重写代码

Hermes → 统一工具集，协议翻译层
  → 换模型 = 一条命令：hermes model <provider>/<model>
```

### 兼容矩阵

| 模型 | 视觉能力 | 支持情况 | 备注 |
|------|----------|----------|------|
| Claude Sonnet/Opus | 有 | 完整支持 | 最佳体验，SOM + 原始坐标 |
| OpenRouter（200+ 模型） | 有 | 完整支持 | 多部件工具消息 |
| GPT-4 / GPT-5 | 有 | 完整支持 | 同上 |
| 本地 vLLM / LM Studio | 有 | 完整支持 | 需支持多部件工具内容 |
| 纯文本模型 | 无 | 降级支持 | mode="ax" 仅用 AX Tree |

### 纯文本模型的降级方案

```
mode="ax"（Accessibility Tree Only）：
  - Agent 不接收截图
  - CUA Driver 读取完整 AX Tree → 返回结构化文本
  - Agent 通过读取元素标签/角色/值导航
  - 性能不如视觉模式，但对结构良好的原生应用可用
  - Token 成本极低（无图片）
```

---

## 已知限制与风险

### 1. 平台限制：仅 macOS

```
CUA Driver 依赖 Apple 私有 SPI → 仅 macOS
Linux VPS（Hermes 最常见部署方式）→ 不可用
跨平台 GUI 自动化 → 用 browser toolset 替代

对于需要操控原生桌面应用 → 必须有 Mac
```

### 2. 私有 SPI 依赖风险

```
SkyLight 是 Apple 私有框架，无公开 API 承诺
历史表现：跨多代 macOS 稳定，TrickUA 团队跟踪维护
风险：未来 macOS 更新可能破坏 SPI 符号

缓解：HERMES_CUA_DRIVER_VERSION 环境变量锁定版本
      生产环境建议必须设置
```

### 3. 当前 macOS Tahoe Bug

```
macOS 26.4.1：ScreenCaptureKit 截图失败（SCStream 错误）
Workaround：mode="ax" 切换到 AX Tree 模式
影响：失去 SOM 视觉定位层，回退到纯文本导航
```

### 4. 安全约束

```
硬性阻止：
  - 系统快捷键（注销/锁屏/强制清空回收站）
  - 危险 shell 模式（curl | bash, sudo rm -rf）
  - macOS 权限对话框
  - 密码输入

设计约束：
  - SOM 索引仅在下次 capture 前有效
  - 工作流必须围绕紧密的 观察→行动→验证 循环
  - 不能「先规划一串操作再顺序执行」
```

---

## 参考资料

- [Hermes Agent 官方文档 - Computer Use](https://hermes-agent.nousresearch.com/docs/user-guide/features/computer-use)
- [CUA / TrickUA GitHub](https://github.com/trycua/cua) — 开源 Computer Use 基础设施
- [CUA Driver README](https://github.com/trycua/cua/blob/main/libs/cua-driver/README.md)
- [Inside macOS Window Internals（CUA 博客）](https://github.com/trycua/cua/blob/main/blog/inside-macos-window-internals.md)
- [Hacker News 讨论 - CUA Driver](https://news.ycombinator.com/item?id=47891384)
- [Hermes Agent GitHub](https://github.com/NousResearch/hermes-agent)
- [视频来源：AI Developer Tools](https://www.youtube.com/watch?v=8mvoG0U40gU)

## 相关笔记

- [[HTML 的不合理有效性 — Claude Code 文档范式转移]]
