---
title: "Hermes Agent Masterclass — Module 6: Tools & MCP Servers"
aliases:
  - Hermes Agent 工具系统
  - Hermes MCP 配置
tags:
  - hermes-agent
  - MCP
  - tools
  - type/learning-note
  - status/active
source:
  - "https://youtu.be/U140gP-1bEI"
author: "Tonbi's AI Garage"
created: 2026-06-13
updated: 2026-06-13
description: "Hermes Agent 工具系统全解——70+ 工具、Tool Sets 分层控制、MCP 服务器接入与暴露、自定义工具开发"
level: intermediate
stars: 5
---

# Hermes Agent Masterclass — Module 6: Tools & MCP Servers

> Module 6 of the Hermes Agent Masterclass by Tonbi's AI Garage。"A model without tools is just a chatbot." 深入解析 Hermes 的工具注册系统、MCP 协议集成、安全控制面，以及如何开发自定义工具。

## 目录

- [扩展能力三件套：Skills vs Tools vs MCP](#扩展能力三件套skills-vs-tools-vs-mcp)
- [工具的本质与调用循环](#工具的本质与调用循环)
- [工具注册表与 Tool Sets](#工具注册表与-tool-sets)
- [安全控制面：Scoping + Sandbox + Approval](#安全控制面scoping--sandbox--approval)
- [MCP 服务器：消费与暴露](#mcp-服务器消费与暴露)
- [MCP 安全机制](#mcp-安全机制)
- [实战：自定义 Stock Price 工具](#实战自定义-stock-price-工具)
- [关键决策树](#关键决策树)
- [参考资料](#参考资料)

---

## 扩展能力三件套：Skills vs Tools vs MCP

每次想让 Hermes 做新事情时，先问一个决策问题：

```
需要让 Hermes 做新事情
       │
       ├── 需要「指令」？ → Skill（说明文档，Agent 可加载）
       │
       ├── 需要「代码/函数」？ → Tool（Python 函数，Agent 可调用）
       │
       └── 需要「外部适配器」？ → MCP Server（外部服务的协议桥接）
```

| 维度 | Skill | Tool | MCP Server |
|------|-------|------|------------|
| 本质 | 可加载的指令集 | 可调用的 Python 函数 | 外部服务的协议适配器 |
| 存储位置 | `~/.hermes/skills/` | `tool_sets.py` | config.yaml |
| 模型可见性 | 读取 SKILL.md 内容 | 看到 name + description + schema | 看到同等的工具 schema |
| 执行方式 | Agent 按指令行动 | Hermes 运行 handler | Hermes 通过协议转发 |
| 代码侵入 | 无需改核心代码 | 需注册到 registry | 零核心代码 |

---

## 工具的本质与调用循环

### 工具的三个组成部分

```
┌─────────────────────────────────────────┐
│  1. Function Block（Python 函数）       │  ← 做实际工作
│  2. Description/Schema（JSON Schema）   │  ← 模型看到的
│  3. Registry（注册到 registry）         │  ← 让工具存在
└─────────────────────────────────────────┘
```

关键原则：**模型从不直接运行函数**。模型发出结构化请求（工具名 + JSON 参数），Hermes 拥有执行权，调用 handler 并将结果返回给模型。

### Tool Calling Loop

```
Model ──发出 tool call──► Registry ──分发──► Handler
  ▲                                            │
  │◄──结果回到 context────── JSON string ◄──────┘
  │
  Model 决定下一步行动
```

### 错误处理原则

> "A failing tool should not crash the turn entirely. It should give the model enough information to recover or choose a different tool or ask for missing information or stop cleanly."

好的工具设计不只覆盖 happy path，还要覆盖 failure shape——让 Agent 能从错误中恢复。

---

## 工具注册表与 Tool Sets

### 结构化工具 vs Shell 命令

Hermes 有意提供结构化工具来替代常见的 shell 命令：

| Shell 命令 | Hermes 结构化工具 | 优势 |
|-----------|------------------|------|
| `cat` | `read_file` | 分页、行号、安全 |
| `grep`/`rg` | `search_files` | 模式控制、输出模式 |
| `sed`/`awk` | `patch` | 模糊匹配、语法检查 |
| `echo >` heredoc | `write_file` | 自动创建目录、lint |

**原则**：如果 Agent 能用结构化、分页、专用工具，就不应 shell out 到原始命令。

### Tool Set 分层架构

```
Tool Sets（约 30 个，70+ 工具）
  │
  ├── Core Tool Sets（核心）
  │   ├── file（read_file, write_file, patch, search_files）
  │   ├── terminal
  │   ├── web（web_search, web_extract）
  │   └── vision（vision_analyze）
  │
  ├── Composite Tool Sets（组合）
  │   ├── debugging（需要 file + terminal + web）
  │   └── safe（只读研究 + 媒体，无 shell）
  │
  ├── Platform Tool Sets（平台适配）
  │   ├── CLI → 完整 surface
  │   └── IDE / ACP → 减少消息等平台相关能力
  │
  └── Dynamic Tool Sets（运行时创建）
      ├── 每个 MCP Server → 自动创建
      ├── Plugins → 各自添加
      └── Custom Bundles → 用户自定义
```

---

## 安全控制面：Scoping + Sandbox + Approval

三层防御体系，回答两个问题：
- **What can the agent reach?**（Scoping）
- **Where does execution happen?**（Sandbox）

### 1. Scoping（作用域控制）

三个控制点：

| 控制点 | 方式 | 作用范围 |
|--------|------|----------|
| Session start | `hermes chat --tool-sets web` | 仅本次会话 |
| Persistent config | config.yaml | 持久配置 |
| Mid-session | `/tools enable <name>` | 会话中动态调整 |

```bash
# 启动时限制工具集
hermes chat --tool-sets web

# 会话中检查/切换
/tools                    # 列出所有工具及启用状态
/tools enable video      # 启用 video 工具集

# Safe 模式（只读研究）
hermes chat --tool-sets safe
```

### 2. Sandbox（沙盒后端）

| 后端 | 特点 |
|------|------|
| **Local** | 默认，本地执行 |
| **Docker** | 容器隔离（Module 2 详解） |
| **SSH** | 远程 VPS 执行（Tailscale） |
| **Approval** | 辅助模型分类风险操作 |

### 3. Approval Gate

> "MCP tool calls flow through the same approval gates. MCP servers are just more tools, so they don't get a free pass just because they came from a protocol."

### Safe Tool Set

只读研究场景的专属工具集：

```
Safe Tool Set 包含：
  ✓ web_search          ✗ write_file
  ✓ web_extract         ✗ terminal
  ✓ vision_analyze      ✗ execute_code
  ✓ image_gen

适用场景：
  - 上下文不可信
  - 公开暴露的 surface
  - 纯研究任务
```

---

## MCP 服务器：消费与暴露

### Hermes 作为 MCP Client

MCP = Model Context Protocol，是 Claude 推出的开放协议，让 AI Agent 连接外部系统。

**心智模型**：

```
Hermes = Agent（大脑）
MCP Server = 外部能力提供者（手）

Server 在启动/重载时被发现 → 工具注册到同一 registry
→ 模型看到它们就像普通工具
```

### 两种传输方式

| 传输方式 | 说明 | 示例 |
|----------|------|------|
| **STDIO** | 本地子进程，command + arguments | Blender MCP：`uvx blender-mcp` |
| **HTTP** | 远程 endpoint + bearer token | Stripe MCP |

### 添加 MCP Server

```bash
# CLI 方式
hermes mcp              # 查看可用服务器
hermes mcp catalog      # 浏览已审批的 MCP
hermes mcp install      # 安装
hermes mcp remove       # 移除
hermes mcp list         # 列出已配置
hermes mcp reload       # 重新加载

# 或者直接让 Hermes 帮你设置
# "Can you add the MCP server for Blender?"
```

config.yaml 配置示例：

```yaml
mcp:
  blender:
    transport: stdio
    command: uvx
    args: [blender-mcp]
    # 可选：include/exclude 过滤工具
    include: [get_scene_info, execute_blender_code]
    exclude: [download_poly_haven_asset]
```

### 工具过滤（include/exclude）

```yaml
# 只暴露信任的工具
mcp:
  blender:
    transport: stdio
    command: uvx
    args: [blender-mcp]
    exclude:
      - download_poly_haven_asset  # 排除不信任的下载工具
```

> "Good MCP usage is not connect everything. That's how you bury the model in 100 vague tools and degrade selection. Good MCP usage is connect to the right server, expose the smallest useful surface, and keep the rest invisible."

### Blender MCP 实战 Demo

视频演示了连接 Blender MCP Server：
1. 安装 `blender-mcp` 子进程
2. Blender 安装 MCP add-on 脚本
3. 在 Blender 中启用 add-on → 连接本地 MCP server
4. Prompt："Can you make a tower, Blender?" → Hermes Tower 自动生成

Blender MCP 暴露 22 个工具，全部以 `mcp_blender_` 前缀注册。

### Hermes 作为 MCP Server

```bash
hermes mcp serve    # 启动 STDIO MCP server（基于 FastMCP）
```

暴露 10 个工具：

| 工具 | 类型 | 说明 |
|------|------|------|
| conversations_list | Read | 列出所有对话 |
| conversation_get | Read | 读取单个对话 |
| message_read | Read | 读取消息 |
| send_message | Write | 发送消息（需要 gateway） |

> "Read operations work without the gateway. Send operations need the gateway running because something has to deliver the message into the live Hermes runtime."

### VS Code Copilot 连接 Hermes

在 VS Code settings.json 中添加 MCP server 配置后，Copilot 可以看到 Hermes 的对话工具——实现 IDE 内直接与 Hermes Agent 交互。

---

## MCP 安全机制

### OAuth 2.1 PKCE

对 HTTP 类型的 MCP Server，使用浏览器认证流程：
- Token 存储在本地 JSON，自动刷新
- 无长期密钥留在 config 中
- 示例：Notion MCP Server

### 恶意包扫描

每次 `npx` 或 `uvx` 启动前，Hermes 查询 `api.osv.dev` 检查恶意软件警告：

```
npx/uvx 启动前
      │
      ▼
查询 api.osv.dev
      │
      ├── 有恶意标记 → 阻止启动
      └── 无标记     → 继续执行
```

> "This is a screen, not a sandbox. It catches known bad packages. It doesn't make arbitrary remote packages trustworthy."

### 密钥管理原则

- 用环境变量存 bearer token
- 优先使用 OAuth flow 而非长期复制的 token
- 避免在 config.yaml 中存放密钥

---

## 实战：自定义 Stock Price 工具

视频演示了创建一个查询股票价格的自定义工具。

### 工具架构

```
┌──────────────────┐     ┌──────────┐     ┌──────────┐
│   Model          │────►│ Registry │────►│ Handler  │
│  "What is AAPL  │     │ (name +  │     │ (Python  │
│   trading at?"  │◄────│  schema) │◄────│  函数)   │
└──────────────────┘     └──────────┘     └────┬─────┘
                                             │
                                        ┌────▼─────┐
                                        │  Finnhub  │
                                        │  API      │
                                        └──────────┘
```

### 开发步骤

1. **编写 handler**：Python 函数调用 Finnhub API
2. **定义 schema**：name、description、parameters（JSON Schema）
3. **注册到 registry**：添加到 `tool_sets.py`
4. **配置环境变量**：`FINNHUB_API_KEY`

### 关键代码结构

```python
from tools.registry import registry

# Schema 定义（模型看到的）
stock_price_schema = {
    "name": "stock_price",
    "description": "Get the latest stock quote for a public company ticker using Finnhub. "
                  "Use this for questions like 'What is Apple trading at?' or 'Get current Nvidia price.'",
    "parameters": {
        "type": "object",
        "properties": {
            "ticker": {"type": "string", "description": "Stock ticker symbol, e.g. AAPL, NVDA"}
        },
        "required": ["ticker"]
    }
}

# Handler 函数（实际执行的）
def stock_price(ticker: str) -> str:
    if not os.environ.get("FINNHUB_API_KEY"):
        return json.dumps({"error": "FINNHUB_API_KEY not set"})
    # ... 调用 Finnhub API，返回价格
    return json.dumps({"ticker": ticker, "price": data["c"]})

# 注册
registry.register(stock_price_schema, stock_price)
```

### 注册到 tool_sets.py

在 Hermes 的 `tool_sets.py` 中将自定义工具添加到相应 tool set 即可使用。

---

## 关键决策树

```
想让 Hermes 做新事情？
  │
  ├── 只需要「怎么做好」的指导
  │   └── Skill（.md 指令文件）
  │
  ├── 需要执行代码逻辑
  │   ├── 外部服务？ → MCP Server（零核心代码适配器）
  │   └── 内部逻辑？ → Native Tool（Python 函数 + registry）
  │
  └── 需要外部系统访问
      └── MCP Server
          ├── 本地工具？ → STDIO transport
          └── 云端 API？ → HTTP transport + OAuth
```

---

## 参考资料

- [YouTube: Hermes Agent Masterclass Module 6](https://youtu.be/U140gP-1bEI)
- [Tonbi's AI Garage 频道](https://www.youtube.com/@TonbisAIGarage)
- [Hermes Agent 文档](https://hermes-agent.nousresearch.com/docs)

## 相关笔记

- [[Hermes Agent 入门与配置]]
- [[MCP 模型上下文协议]]
