---
title: OpenAI Codex 更新总览 (v0.122 - v0.128)
aliases: [Codex CLI 更新, OpenAI Codex 3.0, Codex Desktop]
tags:
  - ai/coding-agent
  - tool/openai-codex
  - changelog/update
  - status/reviewed
source:
  - "https://www.youtube.com/watch?v=erj1tgHtpIM"
  - "https://developers.openai.com/codex/changelog"
author: AICodeKing (video), OpenAI (official)
created: 2026-05-02
updated: 2026-05-02
description: OpenAI Codex 从 v0.122 到 v0.128 的完整更新总览，涵盖 Desktop App 重构、CLI 增强、GPT-5.5 模型、Amazon Bedrock 支持、插件生态和权限系统
level: intermediate
stars: 3
---

# OpenAI Codex 更新总览 (v0.122 - v0.128)

> OpenAI Codex 正从 CLI 编码助手快速进化为全功能 AI 工作台。本笔记覆盖 v0.122 ~ v0.128 的核心更新，包括 Desktop App 重构、GPT-5.5 模型集成、Amazon Bedrock 支持、插件生态系统、权限沙箱和浏览器验证闭环。

---

## 目录

1. [Codex 产品定位演变](#codex-产品定位演变)
2. [Desktop App 重大重构](#desktop-app-重大重构)
3. [CLI v0.122.0：基础能力强化](#cli-v01220基础能力强化)
4. [CLI v0.123.0：Bedrock + MCP 调试](#cli-v01230bedrock--mcp-调试)
5. [CLI v0.124.0：推理控制 + 多环境](#cli-v01240推理控制--多环境)
6. [GPT-5.5 + Codex App 更新](#gpt-55--codex-app-更新)
7. [CLI v0.125.0：App Server 管道](#cli-v01250app-server-管道)
8. [v0.126 - v0.128：最新进展](#v0126---v0128最新进展)
9. [更新全景图](#更新全景图)

---

## Codex 产品定位演变

```
Phase 1 (2025 初)          Phase 2 (2025 中)           Phase 3 (2026 至今)
+------------------+       +------------------+       +------------------+
| CLI 编码助手      |  -->  | Desktop App       |  -->  | AI 工作台          |
| 终端内运行        |       | GUI 包装 CLI      |       | 编码/测试/浏览/PR  |
| 单线程对话        |       | 基础文件操作       |       | 浏览器验证          |
| OpenAI 模型专用   |       | 多窗口支持        |       | 插件生态           |
+------------------+       +------------------+       | 多模型提供商        |
                                                         | 远程环境           |
                                                         | 自动审批           |
                                                         +------------------+

对比 Claude Code:
  Claude Code 定位: 纯 CLI 编码 Agent，极致终端体验
  Codex 定位: 全平台工作台，CLI + Desktop + App Server 三端联动
```

---

## Desktop App 重大重构

### 核心新增功能

| 功能 | 说明 | 价值 |
|------|------|------|
| 内置浏览器 | 打开本地/远程页面，留下反馈让 Codex 修复 | 闭环 UI 验证 |
| Computer Use (macOS) | 在原生应用中查看/点击/输入 | GUI-only Bug 修复 |
| Chats（无项目线程） | 不需要项目文件夹即可开始对话 | 研究/规划/写作 |
| Thread Automations | 线程按计划自动唤醒并继续 | 定时任务/长时间工作流 |
| Artifact Viewer | 查看 PDF/文档/表格/演示文稿 | 非代码产物处理 |
| Task Sidebar | 侧边栏任务管理 | 多任务并行 |
| Memory Support | 上下文记忆 | 跨会话信息保留 |
| Multi-Terminal | 多终端 | 多环境并行操作 |
| Plugins | 插件系统 | 可扩展性 |

### Codex Pets

表面看是个搞笑功能（动画宠物伴侣），但实际有实用价值：

```
功能特性:
  - 设置中启用，选择内置/自定义宠物
  - /pet 命令在 composer 中触发
  - 浮动叠加层（Floating Overlay）

实用价值:
  +-----------------------------+
  |  浮动叠加层（其他 App 上方）   |
  |  - 显示当前线程状态            |
  |  - 运行中 / 等待输入 / 待审核   |
  |  - 短进度提示                  |
  |  - 无需打开完整线程即可监控      |
  +-----------------------------+

  自定义: hatch pet skill -> 根据项目创建专属宠物
```

---

## CLI v0.122.0：基础能力强化

这是本轮更新的基础版本，重点是可靠性和可控性。

### 关键更新

| 类别 | 更新 | 说明 |
|------|------|------|
| 安装 | standalone installs 自包含 | 新用户体验改善 |
| 对话 | `/side` 侧边对话 | 不打断主线问快速问题 |
| 输入 | 队列化输入 | Codex 运行中可排队 `/` 命令和 `!` shell |
| 规划 | Plan Mode 刷新上下文 | 实现阶段用干净上下文 |
| 插件 | Tabbed browsing + Marketplace | 插件生态正式化 |
| 权限 | deny-read glob + sandbox | 企业级安全控制 |
| 工具 | tool discovery + image gen 默认开启 | 更丰富的 Agent 能力 |

### 侧边对话（Side Conversations）

```
场景: Codex 正在重构代码，你想快速确认一个文件用途

传统方式（无 /side）:
  1. 等当前任务完成
  2. 新开线程提问
  3. 切回主线继续

使用 /side:
  /side 这个文件是做什么用的？
  --> 侧边对话回答，主线不受影响

适用场景:
  - 确认文件用途
  - 检查依赖是否仍需要
  - 快速 API 查询
  - 临时思路验证
```

### Plan Mode 上下文刷新

```
规划阶段（Plan Phase）          实现阶段（Implementation Phase）
+------------------------+     +------------------------+
| 讨论方案 A vs B         |     |  干净上下文               |
| 列出优缺点              | --> |  仅保留最终计划            |
| 废弃的想法              |     |  无历史噪音                |
| 替代方案讨论             |     |  更高效的 token 利用        |
| context 快速填满        |     |  context 充裕             |
+------------------------+     +------------------------+

  在启动实现前显示 context 使用量，用户决定是否携带规划上下文
```

---

## CLI v0.123.0：Bedrock + MCP 调试

### Amazon Bedrock 模型提供商

```
之前: Codex 只能用 OpenAI 模型或通用 OpenAI 兼容端点
之后: 内置 Bedrock 支持，带 AWS Profile 认证

意义:
  - AWS 重度企业可以直接用 Bedrock 上的模型
  - 不被锁定在 OpenAI 生态
  - 支持 AWS SigV4 签名 + AWS 凭证认证
```

### MCP 调试增强

```
/mcp verbose 命令:
  - 完整 MCP Server 诊断信息
  - 已加载资源列表
  - 资源模板
  - 配置形状验证

解决痛点:
  - 工具不出现 -> 诊断连接状态
  - 资源未加载 -> 查看资源列表
  - 配置形状错误 -> 检查 .mcp.json 格式
```

---

## CLI v0.124.0：推理控制 + 多环境

### 快速推理控制（Power User Feature）

```
终端快捷键:
  Alt + ,  (逗号)  --> 降低推理强度
  Alt + .  (句号)  --> 提高推理强度

使用场景:
  - 简单文件编辑 --> 降低推理（更快）
  - 复杂架构决策 --> 提高推理（更准确）
  - 不需要打开菜单或改配置
```

### 多环境支持

```
单 Session 管理多环境:
  App Server Session
    |
    +-- Environment A (project-frontend)
    +-- Environment B (project-backend)
    +-- Environment C (deploy-staging)
    |
    每个 turn 可选择工作目录

适用场景:
  - 多仓库 monorepo
  - 远程开发环境
  - 前后端同时工作
```

### Hooks 稳定化

| Hook 能力 | 说明 |
|-----------|------|
| 观察 MCP 工具调用 | 审计/日志 |
| apply patch 监控 | 代码变更追踪 |
| 长时间 bash 会话 | CI/CD 流程 |

### 成本优化

> ChatGPT 付费计划默认使用 fast service tier（除非手动退出）。无需额外付费，但价值随更新持续提升。

---

## GPT-5.5 + Codex App 更新

### GPT-5.5 作为推荐模型

| 模型 | 适用场景 |
|------|----------|
| **GPT-5.5**（推荐） | 实现、重构、调试、测试、验证、知识工作产物 |
| GPT-5.4（fallback） | 滚动期间备用 |

> 如果模型选择器中看不到 GPT-5.5，需更新 CLI、IDE 扩展或 Desktop App。

### Codex App 浏览器控制（Browser Use）

```
之前（仅预览）:                    之后（Browser Use）:
+---------------------------+     +---------------------------+
| 打开页面预览                |     | Codex 主动操作浏览器        |
| 用户留下文字反馈            | --> |   点击 UI 元素             |
| Codex 根据反馈修改代码      |     |   复现视觉 Bug             |
|                          |     |   验证本地修复效果          |
| 限制: Codex 看不到渲染结果  |     |   填写表单测试              |
+---------------------------+     +---------------------------+

  闭环: 编辑代码 -> 渲染页面 -> 操作浏览器 -> 发现问题 -> 修复
```

### 自动审批审核（Automatic Approval Reviews）

```
Agent 请求操作
    |
    v
+-------------------+
| Automatic Reviewer |  <-- AI 审核 Agent
| (风险等级评估)      |
+-------------------+
    |
    +-- Approved --> 执行操作
    +-- Denied   --> 拒绝，告知原因
    +-- Stopped  --> 暂停等待人工
    +-- Timeout  --> 超时处理

适用: 危险命令、敏感文件访问、高风险操作
本质: AI 审核另一个 AI，增加安全层
```

---

## CLI v0.125.0：App Server 管道

### 核心改进

| 类别 | 更新 |
|------|------|
| App Server | Unix socket transport, resume/fork 分页 |
| 环境 | Sticky environments（会话持久化） |
| 插件 | 远程插件安装 + marketplace 升级 |
| 权限 | Profile 跨 TUI/MCP/Shell/App Server 同步 |

### 权限状态一致性

```
权限Profile 需要在以下所有端点保持一致:
  +----------+  +----------+  +----------+  +----------+  +----------+
  |   CLI    |  |  Desktop |  |   MCP    |  |  Shell   |  |   App    |
  |  (TUI)   |  |   App    |  |  Sandbox |  | Approval |  |  Server  |
  +----------+  +----------+  +----------+  +----------+  +----------+
       \            |            |            |            /
        +-----------+------------+------------+-----------+
                    |   Permission Profile 同步   |
                    +-----------------------------+

  覆盖: Two-way sessions, user turns, MCP sandbox state,
        shell escalation, app server APIs
```

---

## v0.126 - v0.128：最新进展

截至 2026-04-30，Codex CLI 已更新至 **v0.128.0**（视频仅覆盖到 v0.125.0）。

### v0.128.0 关键更新

| 类别 | 更新 |
|------|------|
| Goal Workflows | 持久化 `/goal` 工作流，支持暂停/恢复 |
| CLI 工具 | `codex update` 命令，可配置 TUI 快捷键 |
| 权限 | 内置默认 Profile，sandbox CLI profile 选择 |
| 插件 | Marketplace 安装、远程 bundle 缓存、插件内置 hooks |
| Multi-Agent | MultiAgentV2 配置：线程上限、等待控制、子 Agent 提示 |
| External Agent | 外部 Agent Session 导入 |

---

## 更新全景图

### 版本演进时间线

```
v0.122.0 (4月中)     v0.123.0            v0.124.0            v0.125.0
  基础能力强化           Bedrock + MCP        推理控制            App Server
  |                    |                    |                    |
  +-- /side 对话        +-- AWS Bedrock     +-- Alt+,/. 推理    +-- Unix socket
  +-- 队列输入           +-- /mcp verbose    +-- 多环境支持       +-- 远程插件
  +-- Plan 刷新上下文    +-- GPT-5.4 元数据   +-- Hooks 稳定      +-- 权限同步
  +-- 插件生态           +-- 插件 MCP 加载    +-- 快速服务层       +-- Provider 发现
  +-- 权限沙箱                                                    |
                                                                   v
                              GPT-5.5 + App 更新 (4月下旬)           v
                              +-- 推荐模型切换                     v0.126-128
                              +-- Browser Use                     +-- /goal 持久化
                              +-- 自动审批审核                     +-- MultiAgentV2
                                                               +-- 外部 Agent 导入
                                                               +-- codex update
```

### 与竞品对比

| 能力 | Codex | Claude Code | Cursor |
|------|-------|-------------|--------|
| 多模型提供商 | OpenAI + Bedrock + 兼容 | Anthropic 仅 | 多模型 |
| 插件生态 | Marketplace + 远程 | Skills + MCP | Extensions |
| 浏览器验证 | 内置 Browser Use | 需手动配置 | 内置 |
| Computer Use | macOS 原生应用 | 无 | 无 |
| Desktop App | 全功能工作台 | 无（纯 CLI） | IDE 集成 |
| 权限系统 | Profile + Sandbox | 简单权限 | 项目级 |
| 定价 | ChatGPT 订阅含 | API 按量付费 | 订阅制 |
| 开源 | CLI 开源 | CLI 开源 | 闭源 |

---

## 参考资料

- [Codex 官方 Changelog](https://developers.openai.com/codex/changelog)
- [Codex GitHub Releases](https://github.com/openai/codex/releases)
- [视频来源: AICodeKing - YouTube](https://www.youtube.com/watch?v=erj1tgHtpIM)

## 相关笔记

- [[AI Coding Agent]]
- [[GPT-5.5]]
- [[Claude Code]]
