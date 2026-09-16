---
title: 新版 MCP Server 开发指南 — 5 分钟从零接入 Claude Desktop
aliases:
  - 官方新版MCP Server开发指南
  - MCP Server 从零开发
tags:
  - mcp
  - typescript
  - claude-desktop
  - status/active
  - type/doc
source:
  - "https://www.youtube.com/watch?v=-C6K3wtjjoI"
  - "https://modelcontextprotocol.io/specification/2026-07-28/server/tools"
  - "https://github.com/modelcontextprotocol/typescript-sdk"
  - "https://www.npmjs.com/package/@modelcontextprotocol/server"
author: YAHA学堂
created: 2026-09-16
updated: 2026-09-16
description: 基于 2026-07-28 MCP 规范，从零手写 TypeScript MCP Server 并接入 Claude Desktop 的完整实战笔记，涵盖新字段、ESM 配置、stdio 传输与日志污染避坑
level: beginner
stars: 4
---

# 新版 MCP Server 开发指南 — 5 分钟从零接入 Claude Desktop

> 基于 2026-07-28 版 MCP 规范和官方 v2 TypeScript SDK，从零手写一个 stdio 传输的 MCP Server 并接入 Claude Desktop。覆盖新规范核心字段、ESM 环境配置、stdio 传输机制、日志污染避坑与 Claude Desktop 整合验证。适合想动手写第一个 MCP Server 的开发者。

## 目录

- [[#一、新版 MCP 规范的核心差异]]
- [[#二、环境与依赖初始化避坑]]
- [[#三、Server 实作与传输架构]]
- [[#四、TS 编译配置与 stdio 协议除错]]
- [[#五、Claude Desktop 整合验证]]
- [[#验证与勘误记录]]
- [[#参考资料]]

---

## 一、新版 MCP 规范的核心差异

### 核心概念

2026-07-28 版 MCP 规范（[specification](https://modelcontextprotocol.io/specification/2026-07-28)）是 MCP 协议的一次重大升级：去除了会话状态（stateless），引入了 `_meta` 元数据字段和 `resultType` 完成状态标记。

### 新旧规范对比

| 特性 | 旧版规范（2025-03-26 等） | 新版规范（2026-07-28） |
|------|--------------------------|----------------------|
| 会话管理 | 有 stateful session（`initialize` 握手） | Stateless，无握手，每请求独立 |
| 元数据 | 无强制要求 | `_meta` 字段 MUST 包含在每请求中 |
| 结果状态 | 无显式完成标记 | `resultType: "complete"` 或 `"input_required"` |
| 工具列表 | 直接返回 tools 数组 | `resultType` + `tools` + `ttlMs` + `cacheScope` |
| SDK 包名 | `@modelcontextprotocol/sdk`（v1） | `@modelcontextprotocol/server` + `@modelcontextprotocol/client`（v2） |

### `_meta` 字段结构

每个 MCP 请求 MUST 包含以下 `_meta` 字段：

```json
{
  "_meta": {
    "io.modelcontextprotocol/protocolVersion": "2026-07-28",
    "io.modelcontextprotocol/clientInfo": {
      "name": "my-client",
      "version": "1.0.0"
    },
    "io.modelcontextprotocol/clientCapabilities": {}
  }
}
```

### `resultType` 字段

响应中的 `resultType` 标记输出的完成状态：

| 值 | 含义 | 后续动作 |
|----|------|---------|
| `"complete"` | 工具执行完成，结果已返回 | 直接处理结果 |
| `"input_required"` | 需要额外输入才能完成 | 客户端收集输入后重试（带 `inputResponses`） |

### 兼容性注意

沿用旧版规范代码不会立即报错，但缺少 `_meta` 和 `resultType` 会导致：
- 工具状态追踪失效
- 多轮交互（`input_required`）无法触发
- 高级客户端功能（缓存、认证）不兼容

---

## 二、环境与依赖初始化避坑

### Node.js 版本要求

`@modelcontextprotocol/server`（v2.0.0）的 `engines` 声明为 `{"node": ">=20"}`。

```bash
# 检查 Node 版本
node --version   # 需要 v20+，推荐 v24（视频实测用 Node 24）
```

### ESM 模块类型

`package.json` 必须声明 `"type": "module"`：

```json
{
  "name": "my-mcp-server",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "build": "tsc",
    "start": "node dist/index.js"
  }
}
```

**❌ 常见错误**：不加 `"type": "module"` 直接用 `import` 语法 → Node 原生环境报 `SyntaxError: Cannot use import statement outside a module`

**⚠️ 无效解法**：改 Webpack/Babel 配置——MCP Server 是 Node 原生运行，不经过打包器

### 依赖安装

```bash
npm init -y
npm install @modelcontextprotocol/server
```

**⚠️ 包名注意**：

| SDK 版本 | 包名 | 状态 | 周下载量 |
|----------|------|------|---------|
| v1（legacy） | `@modelcontextprotocol/sdk` | 仍维护（1.30.0），6 个月后弃用 | ~52M |
| v2（stable） | `@modelcontextprotocol/server` + `@modelcontextprotocol/client` | 当前 stable，对应 2026-07-28 spec | ~4.9M |

视频说"旧包即将淘汰"方向正确但时间线模糊——v1 当前仍在活跃维护，但 v2 是新的 stable release line。新项目建议直接用 v2。

### TypeScript ≥6.0 的额外配置

npm 页面特别提示：TypeScript ≥6.0 不再自动包含 `@types/*`，需要在 `tsconfig.json` 中显式声明：

```json
{
  "compilerOptions": {
    "types": ["node"]
  }
}
```

---

## 三、Server 实作与传输架构

### 传输机制：stdio vs HTTP

```
┌─────────────┐    stdin/stdout     ┌──────────────┐
│  Claude      │ ◄────────────────► │  MCP Server   │
│  Desktop     │    (JSON-RPC)      │  (Node.js)    │
└─────────────┘                     └──────────────┘

stdio 模式：
- 不开启网络端口
- 通过标准输入/输出双向通信
- 适合本地开发工具

HTTP 模式（Streamable HTTP）：
- 开启网络端口
- 适合远程部署
- 需处理认证（OAuth）
```

**选择决策树**：

```
选 stdio 如果你：
  ✅ 本地开发工具
  ✅ 与桌面应用（Claude Desktop）集成
  ✅ 不需要网络暴露

选 HTTP 如果你：
  ✅ 远程部署
  ✅ 多客户端共享
  ✅ 需要认证/授权
```

### Server 最小实现（v2 SDK）

```typescript
// index.ts
import { McpServer } from "@modelcontextprotocol/server";
import { StdioServerTransport } from "@modelcontextprotocol/server/stdio";

const server = new McpServer({
  name: "my-tool-server",
  version: "1.0.0",
  capabilities: {
    tools: {},
  },
});

// 注册工具
server.registerTool("get_weather", {
  description: "获取指定城市的当前天气信息",
  inputSchema: {
    type: "object",
    properties: {
      location: {
        type: "string",
        description: "城市名称或邮编",
      },
    },
    required: ["location"],
  },
  handler: async ({ location }) => {
    // 实际业务逻辑
    return {
      content: [{ type: "text", text: `当前天气：${location} 晴，25°C` }],
    };
  },
});

// 启动 stdio 传输
const transport = new StdioServerTransport();
await server.connect(transport);
```

### 工具注册的三层结构

```
registerTool(工具名, {
  description:    ← 给 LLM 看的工具描述（决定模型是否调用）
  inputSchema:    ← JSON Schema 定义参数（LLM 生成参数值的依据）
  handler:        ← 实际执行逻辑（收到调用后运行）
})
```

### description 的核心作用

工具的 `description` 不是开发者注释，而是 **LLM 判断上下文的依据**：

```
┌──────────────────────────────────────────────────┐
│  用户: "帮我查一下纽约的天气"                        │
│                                                   │
│  LLM 扫描所有已注册工具的 description:               │
│    "获取指定城市的当前天气信息"  ← 匹配！            │
│    "发送电子邮件"                         ← 不匹配  │
│    "查询数据库"                         ← 不匹配    │
│                                                   │
│  结论: 调用 get_weather(location="纽约")            │
└──────────────────────────────────────────────────┘
```

**验证方法**（视频实测）：清空 `description` 后，相同用户输入 → Claude 只生成文本回复，完全不触发工具调用。

---

## 四、TS 编译配置与 stdio 协议除错

### 日志污染（Protocol Poisoning）

stdio 模式下，stdout 是 JSON-RPC 通信通道：

```
正常流程:
  Server → stdout → JSON-RPC 封包 → Client 解析 ✅

日志污染:
  Server → stdout → "DEBUG: 开始执行" + JSON-RPC 封包 → Client 解析 ❌ 崩溃
```

**规则**：

| 场景 | 正确做法 | 错误做法 |
|------|---------|---------|
| 调试日志 | `console.error(...)` → stderr | `console.log(...)` → stdout |
| 错误信息 | `console.error(...)` → stderr | `console.log(...)` → stdout |
| 协议消息 | SDK 内部处理 → stdout | 手动拼接 → stdout |

**✅ 正确**：
```typescript
console.error("DEBUG: 工具被调用", { location });  // 走 stderr，不影响协议
```

**❌ 错误**：
```typescript
console.log("DEBUG: 工具被调用", { location });    // 走 stdout，污染 JSON-RPC
```

### tsconfig 配置

```json
{
  "compilerOptions": {
    "target": "ES2022",
    "module": "Node16",
    "moduleResolution": "Node16",
    "outDir": "./dist",
    "rootDir": "./src",
    "strict": true,
    "esModuleInterop": true,
    "types": ["node"]
  },
  "include": ["src"]
}
```

**❌ 错误配置**：`module: "CommonJS"` → 编译后执行报 `exports is not defined`（ESM 项目不能用 CommonJS 输出）

**`module` 选择**：

| 值 | 适用场景 |
|----|---------|
| `Node16` | Node.js 16+，ESM 项目（推荐） |
| `NodeNext` | Node.js 16+，自动检测 ESM/CJS（推荐） |
| `ES2020` | 仅配合 bundler（Webpack/Vite） |
| `CommonJS` | 旧式 CJS 项目（MCP Server 不适用） |

---

## 五、Claude Desktop 整合验证

### 配置文件

编辑 `claude_desktop_config.json`（macOS: `~/Library/Application Support/Claude/claude_desktop_config.json`）：

```json
{
  "mcpServers": {
    "my-tool-server": {
      "command": "node",
      "args": ["/绝对/路径/到/dist/index.js"]
    }
  }
}
```

**⚠️ 绝对路径**：相对路径会导致 Claude Desktop 找不到文件。

### 验证流程

```
1. 重启 Claude Desktop
       │
       ▼
2. 打开「Connectors」界面
       │
       ├── 看到 "my-tool-server"（type: desktop, local dev）✅
       │
       └── 没看到 → 检查配置路径 / Node 版本 / 编译产物
       │
       ▼
3. 在对话中提及工具功能
       │
       ▼
4. Claude 触发安全授权弹窗
       │
       ├── 用户核准 → 工具执行 → 返回结果 ✅
       │
       └── 用户拒绝 → Claude 文本回复（不调用工具）
```

### 反向验证：description 的影响

视频实测：清空 `description` 后，相同用户输入 → Claude 不触发工具调用，只生成文本回复。这证明 **提示词语义是 Agent 工具调用的决定因素**。

---

## 验证与勘误记录

| 视频声称 | 验证结果 | 来源 |
|---------|---------|------|
| `resultType` 新字段 | ✅ 真实存在 | [MCP 2026-07-28 spec - Tools](https://modelcontextprotocol.io/specification/2026-07-28/server/tools) |
| `_meta` 新字段 | ✅ 真实存在 | 同上，规范要求每请求 MUST 包含 |
| Node.js >= 20 | ✅ 正确 | npm registry: `engines: {"node": ">=20"}` |
| ESM `"type": "module"` | ✅ 正确 | npm 包声明 `"type": "module"` |
| `@modelcontextprotocol/server` 官方维护 | ✅ 正确 | Anthropic, PBC，v2.0.0 stable（2026-07-27 发布） |
| 旧包 `@modelcontextprotocol/sdk` 即将淘汰 | ⚠️ 部分正确 | v1 仍维护（1.30.0，2026-07-27 更新），但 v2 是新 stable line；v1 周下载 52M vs v2 4.9M |
| stdio 传输机制 | ✅ 正确 | 官方 SDK 支持 stdio，有 first-server tutorial |
| `capabilities: { tools: {} }` | ✅ 正确 | 规范要求声明 tools capability |
| `console.log` 污染 stdio | ✅ 正确 | stdio 模式 stdout 用于 JSON-RPC，debug 须走 stderr |
| tsconfig Node16/NodeNext | ✅ 正确 | ESM 项目标准配置 |
| `claude_desktop_config.json` 绝对路径 | ✅ 正确 | Claude Desktop 配置标准做法 |

**勘误说明**：
1. 视频说"旧包即将淘汰"表述过于绝对。v1 `@modelcontextprotocol/sdk` 当前仍在活跃维护（1.30.0），官方承诺 v1.x 至少 6 个月 bug fix + security update。但新项目建议直接用 v2。
2. 视频提到 `registerTool` 的方法名，v2 SDK 的实际 API 可能有差异（SDK 处于 v2 稳定期，API 可能调整），建议以 [官方文档](https://ts.sdk.modelcontextprotocol.io/v2/) 为准。

---

## 参考资料

- [MCP 2026-07-28 Specification - Tools](https://modelcontextprotocol.io/specification/2026-07-28/server/tools)
- [MCP TypeScript SDK (GitHub)](https://github.com/modelcontextprotocol/typescript-sdk)
- [@modelcontextprotocol/server (npm)](https://www.npmjs.com/package/@modelcontextprotocol/server)
- [@modelcontextprotocol/sdk v1 (npm)](https://www.npmjs.com/package/@modelcontextprotocol/sdk)
- [MCP v2 Documentation](https://ts.sdk.modelcontextprotocol.io/v2/)
- [Build your first server (v2 tutorial)](https://github.com/modelcontextprotocol/typescript-sdk/blob/main/docs/get-started/first-server.md)

## 相关笔记

- [[MCP 协议架构总览]]
- [[Claude Desktop MCP 配置指南]]

---

*文档生成时间：2026-09-16*
*基于 YAHA学堂 YouTube 视频 + MCP 2026-07-28 官方规范交叉验证*
