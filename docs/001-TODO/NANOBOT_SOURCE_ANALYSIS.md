---
in:
title: nanobot 源码深度分析报告
aliases: [nanobot 源码深度分析报告]
tags: [nanobot, source-analysis, python, agent, mcp]
source:
author:
created: 2026-02-19 18:21
updated: 2026-03-08 00:13
description:
level:
stars:
tags:
  - status/active
  - area/distill
  - type/doc
---

# nanobot 源码深度分析报告

> 分析时间: 2026-02-19
> 版本: 0.1.4
> Python 文件数: 59

---

## 1. 项目概览

nanobot 是一个轻量级的个人 AI 助手框架，采用 Python 编写，具有以下核心特性：
- 多渠道消息接入（Telegram、WhatsApp、Discord、Slack、飞书、钉钉、QQ、Email等）
- 灵活的工具系统（文件操作、Shell 命令、Web 搜索等）
- MCP (Model Context Protocol) 集成
- 会话管理和记忆系统
- 子代理（Subagent）后台任务
- 定时任务（Cron）服务

---

## 2. 核心架构

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────┐
│                           CLI Layer                                  │
│                    (nanobot/cli/commands.py)                         │
├─────────────────────────────────────────────────────────────────────┤
│                        Gateway Server                                │
│              (启动 AgentLoop + ChannelManager)                       │
├─────────────────────────────────────────────────────────────────────┤
│                         Message Bus                                  │
│              (nanobot/bus/queue.py + events.py)                      │
│     ┌───────────────┐                      ┌────────────────┐        │
│     │ Inbound Queue │                      │ Outbound Queue │        │
│     └───────┬───────┘                      └────────┬───────┘        │
├─────────────┼──────────────────────────────────────┼────────────────┤
│             │                                      │                 │
│    ┌────────▼────────┐                   ┌─────────▼─────────┐       │
│    │  AgentLoop      │                   │  ChannelManager   │       │
│    │ (agent/loop.py) │                   │(channels/manager) │       │
│    └────────┬────────┘                   └─────────┬─────────┘       │
│             │                                      │                 │
│    ┌────────▼────────┐           ┌─────────────────▼────────────┐    │
│    │   ToolRegistry  │           │  BaseChannel implementations │    │
│    │ (tools/*.py)    │           │  (telegram, slack, etc.)     │    │
│    └─────────────────┘           └──────────────────────────────┘    │
├─────────────────────────────────────────────────────────────────────┤
│                      Provider Layer                                  │
│         (litellm_provider, custom_provider, etc.)                   │
├─────────────────────────────────────────────────────────────────────┤
│                     Supporting Services                              │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌─────────────┐ │
│  │SessionManager│ │ MemoryStore  │ │ CronService  │ │ Heartbeat   │ │
│  │              │ │              │ │              │ │ Service     │ │
│  └──────────────┘ └──────────────┘ └──────────────┘ └─────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 模块结构

```
nanobot/
├── __init__.py          # 版本信息 (__version__ = "0.1.4")
├── __main__.py          # 入口点 (python -m nanobot)
│
├── agent/               # 核心代理逻辑
│   ├── loop.py          # AgentLoop - 主处理引擎
│   ├── context.py       # ContextBuilder - 提示词构建
│   ├── memory.py        # MemoryStore - 记忆存储
│   ├── skills.py        # SkillsLoader - 技能加载器
│   ├── subagent.py      # SubagentManager - 子代理管理
│   └── tools/           # 工具系统
│       ├── base.py      # Tool 抽象基类
│       ├── registry.py  # ToolRegistry 工具注册表
│       ├── filesystem.py # 文件操作工具
│       ├── shell.py     # Shell 命令工具
│       ├── web.py       # Web 搜索和获取工具
│       ├── mcp.py       # MCP 客户端集成
│       ├── spawn.py     # 子代理生成工具
│       ├── cron.py      # 定时任务工具
│       └── message.py   # 消息发送工具
│
├── bus/                 # 消息总线
│   ├── queue.py         # MessageBus - 异步队列
│   └── events.py        # InboundMessage/OutboundMessage
│
├── channels/            # 通信渠道
│   ├── base.py          # BaseChannel 抽象类
│   ├── manager.py       # ChannelManager
│   ├── telegram.py      # Telegram 渠道
│   ├── slack.py         # Slack 渠道
│   ├── discord.py       # Discord 渠道
│   ├── whatsapp.py      # WhatsApp 渠道
│   ├── feishu.py        # 飞书渠道
│   ├── dingtalk.py      # 钉钉渠道
│   ├── email.py         # 邮件渠道
│   ├── mochat.py        # Mochat 渠道
│   └── qq.py            # QQ 渠道
│
├── cli/                 # 命令行接口
│   └── commands.py      # Typer CLI 命令定义
│
├── config/              # 配置系统
│   ├── schema.py        # Pydantic 配置模型
│   └── loader.py        # 配置加载器
│
├── cron/                # 定时任务
│   ├── types.py         # CronJob 数据结构
│   └── service.py       # CronService 服务
│
├── heartbeat/           # 心跳服务
│   └── service.py       # HeartbeatService
│
├── providers/           # LLM 提供者
│   ├── base.py          # LLMProvider 抽象类
│   ├── litellm_provider.py # LiteLLM 实现
│   ├── custom_provider.py  # 自定义端点
│   ├── openai_codex_provider.py # OpenAI Codex OAuth
│   ├── registry.py      # 提供者注册表
│   └── transcription.py # 语音转录
│
├── session/             # 会话管理
│   └── manager.py       # SessionManager
│
├── skills/              # 内置技能
│   ├── clawhub/         # ClawHub 技能搜索
│   ├── memory/          # 记忆管理
│   ├── cron/            # 定时任务
│   ├── weather/         # 天气查询
│   └── ...              # 其他技能
│
└── utils/               # 工具函数
    └── helpers.py       # 通用辅助函数
```

---

## 3. 核心组件详解

### 3.1 AgentLoop (agent/loop.py)

**职责**: 核心处理引擎，协调所有组件

**关键方法**:
```python
class AgentLoop:
    async def run(self) -> None:
        """主循环：从 bus 消费消息，处理后发送响应"""
        
    async def process_direct(self, content, session_key, ...) -> str:
        """直接处理消息（CLI/定时任务用）"""
        
    async def _process_message(self, msg: InboundMessage) -> OutboundMessage:
        """处理单条消息的核心逻辑"""
        
    async def _run_agent_loop(self, initial_messages, on_progress) -> tuple[str, list[str]]:
        """LLM 迭代循环：调用 LLM -> 执行工具 -> 重复"""
        
    async def _consolidate_memory(self, session) -> None:
        """记忆整合：将旧消息摘要到 MEMORY.md"""
```

**设计特点**:
1. **迭代循环**: 最多 `max_iterations` 次迭代，支持工具链式调用
2. **记忆整合**: 后台异步任务，将旧对话摘要到文件
3. **MCP 懒加载**: 首次使用时连接 MCP 服务器
4. **工具上下文**: 动态设置消息路由信息

**值得关注的代码**:
```python
# 工具迭代循环
while iteration < self.max_iterations:
    response = await self.provider.chat(
        messages=messages,
        tools=self.tools.get_definitions(),
        ...
    )
    
    if response.has_tool_calls:
        # 执行工具调用
        for tool_call in response.tool_calls:
            result = await self.tools.execute(tool_call.name, tool_call.arguments)
            messages = self.context.add_tool_result(messages, tool_call.id, ...)
    else:
        final_content = self._strip_think(response.content)
        break
```

### 3.2 MessageBus (bus/queue.py)

**职责**: 解耦渠道层和代理核心的异步消息队列

```python
class MessageBus:
    def __init__(self):
        self.inbound: asyncio.Queue[InboundMessage] = asyncio.Queue()
        self.outbound: asyncio.Queue[OutboundMessage] = asyncio.Queue()
        self._outbound_subscribers: dict[str, list[Callable]] = {}
```

**数据流**:
1. 渠道 -> `publish_inbound()` -> inbound queue
2. AgentLoop -> `consume_inbound()` -> 处理
3. AgentLoop -> `publish_outbound()` -> outbound queue
4. ChannelManager -> `consume_outbound()` -> 渠道发送

### 3.3 ToolRegistry (agent/tools/registry.py)

**职责**: 动态工具注册和执行

```python
class ToolRegistry:
    def register(self, tool: Tool) -> None
    def get_definitions(self) -> list[dict]  # OpenAI 格式
    async def execute(self, name: str, params: dict) -> str
```

**内置工具**:
| 工具名 | 功能 |
|--------|------|
| read_file | 读取文件内容 |
| write_file | 写入文件 |
| edit_file | 编辑文件（查找替换）|
| list_dir | 列出目录 |
| exec | 执行 Shell 命令 |
| web_search | Brave 搜索 |
| web_fetch | 获取网页内容 |
| message | 发送消息到渠道 |
| spawn | 生成子代理 |
| cron | 管理定时任务 |
| mcp_* | MCP 工具（动态注册）|

### 3.4 ChannelManager (channels/manager.py)

**职责**: 管理和协调所有通信渠道

```python
class ChannelManager:
    def _init_channels(self) -> None:
        """根据配置初始化渠道"""
        
    async def start_all(self) -> None:
        """启动所有渠道和出站分发器"""
        
    async def _dispatch_outbound(self) -> None:
        """将出站消息路由到对应渠道"""
```

**支持的渠道**:
- Telegram (python-telegram-bot)
- Slack (Socket Mode)
- Discord
- WhatsApp (通过 Node.js Bridge)
- 飞书/Lark
- 钉钉
- Email (IMAP/SMTP)
- QQ
- Mochat

### 3.5 ContextBuilder (agent/context.py)

**职责**: 构建代理的系统提示和消息上下文

**提示词构成**:
```
1. 核心身份 (时间、运行时、工作区路径)
2. Bootstrap 文件 (AGENTS.md, SOUL.md, USER.md, TOOLS.md, IDENTITY.md)
3. 记忆上下文 (memory/MEMORY.md)
4. 始终加载的技能 (always=true)
5. 可用技能摘要 (渐进式加载)
```

**关键代码**:
```python
def build_system_prompt(self, skill_names=None) -> str:
    parts = []
    parts.append(self._get_identity())  # 时间、运行时
    parts.append(self._load_bootstrap_files())
    parts.append(f"# Memory\n\n{self.memory.get_memory_context()}")
    
    # 始终加载的技能
    always_skills = self.skills.get_always_skills()
    if always_skills:
        parts.append(f"# Active Skills\n\n{always_content}")
    
    # 技能摘要（代理用 read_file 工具加载完整内容）
    skills_summary = self.skills.build_skills_summary()
    if skills_summary:
        parts.append(f"# Skills\n\n{skills_summary}")
```

---

## 4. Gateway 服务器工作原理

### 4.1 启动流程

```python
# nanobot/cli/commands.py - gateway 命令

@app.command()
def gateway(port: int = 18790, ...):
    config = load_config()
    bus = MessageBus()
    provider = _make_provider(config)
    session_manager = SessionManager(config.workspace_path)
    
    # 创建定时任务服务
    cron = CronService(cron_store_path)
    
    # 创建代理循环
    agent = AgentLoop(
        bus=bus,
        provider=provider,
        workspace=config.workspace_path,
        cron_service=cron,
        session_manager=session_manager,
        mcp_servers=config.tools.mcp_servers,
        ...
    )
    
    # 设置定时任务回调
    async def on_cron_job(job: CronJob) -> str | None:
        return await agent.process_direct(
            job.payload.message,
            session_key=f"cron:{job.id}",
            ...
        )
    cron.on_job = on_cron_job
    
    # 创建心跳服务
    heartbeat = HeartbeatService(...)
    
    # 创建渠道管理器
    channels = ChannelManager(config, bus)
    
    async def run():
        await cron.start()
        await heartbeat.start()
        await asyncio.gather(
            agent.run(),          # 消息处理
            channels.start_all(), # 渠道监听
        )
```

### 4.2 并发模型

```
┌─────────────────────────────────────────────────────────┐
│                    asyncio Event Loop                    │
├─────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │ AgentLoop    │  │ Telegram     │  │ Slack        │   │
│  │ .run()       │  │ Channel      │  │ Channel      │   │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘   │
│         │                 │                  │           │
│         ▼                 ▼                  ▼           │
│  ┌──────────────────────────────────────────────────┐   │
│  │              MessageBus (asyncio.Queue)          │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

---

## 5. MCP 集成机制

### 5.1 MCP 工具包装器

```python
# nanobot/agent/tools/mcp.py

class MCPToolWrapper(Tool):
    """将 MCP 服务器的工具包装为 nanobot 工具"""
    
    def __init__(self, session, server_name: str, tool_def):
        self._session = session
        self._original_name = tool_def.name
        self._name = f"mcp_{server_name}_{tool_def.name}"
        self._parameters = tool_def.inputSchema
```

### 5.2 MCP 连接管理

```python
async def connect_mcp_servers(
    mcp_servers: dict,  # 配置中的服务器定义
    registry: ToolRegistry,
    stack: AsyncExitStack
) -> None:
    for name, cfg in mcp_servers.items():
        if cfg.command:
            # Stdio 模式
            params = StdioServerParameters(
                command=cfg.command,
                args=cfg.args,
                env=cfg.env
            )
            read, write = await stack.enter_async_context(stdio_client(params))
        elif cfg.url:
            # HTTP 模式
            read, write, _ = await stack.enter_async_context(
                streamable_http_client(cfg.url)
            )
        
        session = await stack.enter_async_context(ClientSession(read, write))
        await session.initialize()
        
        # 注册所有工具
        tools = await session.list_tools()
        for tool_def in tools.tools:
            registry.register(MCPToolWrapper(session, name, tool_def))
```

### 5.3 配置示例

```json
{
  "tools": {
    "mcpServers": {
      "filesystem": {
        "command": "npx",
        "args": ["-y", "@modelcontextprotocol/server-filesystem", "/path/to/dir"]
      },
      "custom": {
        "url": "https://mcp.example.com/stream"
      }
    }
  }
}
```

---

## 6. 配置系统设计

### 6.1 Pydantic 模型层次

```python
Config                              # 根配置
├── agents: AgentsConfig
│   └── defaults: AgentDefaults
│       ├── workspace: str
│       ├── model: str
│       ├── max_tokens: int
│       ├── temperature: float
│       ├── max_tool_iterations: int
│       └── memory_window: int
│
├── channels: ChannelsConfig
│   ├── telegram: TelegramConfig
│   ├── slack: SlackConfig
│   ├── whatsapp: WhatsAppConfig
│   └── ...
│
├── providers: ProvidersConfig
│   ├── anthropic: ProviderConfig
│   ├── openai: ProviderConfig
│   ├── openrouter: ProviderConfig
│   └── ...
│
├── gateway: GatewayConfig
│   ├── host: str
│   └── port: int
│
└── tools: ToolsConfig
    ├── web: WebToolsConfig
    ├── exec: ExecToolConfig
    ├── restrict_to_workspace: bool
    └── mcp_servers: dict[str, MCPServerConfig]
```

### 6.2 提供者匹配逻辑

```python
def _match_provider(self, model: str) -> tuple[ProviderConfig | None, str | None]:
    model_lower = model.lower()
    
    # 1. 按关键词匹配（优先）
    for spec in PROVIDERS:
        if any(kw in model_lower for kw in spec.keywords):
            p = getattr(self.providers, spec.name)
            if p and (spec.is_oauth or p.api_key):
                return p, spec.name
    
    # 2. 回退到第一个有 API key 的提供者
    for spec in PROVIDERS:
        if spec.is_oauth:
            continue
        p = getattr(self.providers, spec.name)
        if p and p.api_key:
            return p, spec.name
    
    return None, None
```

### 6.3 camelCase 支持

```python
class Base(BaseModel):
    model_config = ConfigDict(
        alias_generator=to_camel,  # 支持 camelCase JSON
        populate_by_name=True      # 同时支持 snake_case
    )
```

---

## 7. CLI 命令实现

### 7.1 命令结构

```python
app = typer.Typer(name="nanobot", help="🐈 nanobot - Personal AI Assistant")

# 主命令
@app.command()
def onboard(): ...        # 初始化配置

@app.command()
def gateway(...): ...     # 启动 Gateway 服务器

@app.command()
def agent(...): ...       # 交互式聊天

@app.command()
def status(): ...         # 显示状态

# 子命令组
channels_app = typer.Typer()  # nanobot channels status/login
cron_app = typer.Typer()      # nanobot cron list/add/remove/enable/run
provider_app = typer.Typer()  # nanobot provider login
```

### 7.2 交互式输入处理

```python
# 使用 prompt_toolkit 处理输入
from prompt_toolkit import PromptSession
from prompt_toolkit.history import FileHistory

_PROMPT_SESSION = PromptSession(
    history=FileHistory(str(history_file)),
    enable_open_in_editor=False,
    multiline=False,
)

async def _read_interactive_input_async() -> str:
    with patch_stdout():
        return await _PROMPT_SESSION.prompt_async(
            HTML("<b fg='ansiblue'>You:</b> "),
        )
```

### 7.3 终端状态恢复

```python
def _flush_pending_tty_input() -> None:
    """丢弃生成输出时用户输入的字符"""
    import termios
    termios.tcflush(fd, termios.TCIFLUSH)

def _restore_terminal() -> None:
    """恢复终端原始状态"""
    termios.tcsetattr(fd, termios.TCSADRAIN, _SAVED_TERM_ATTRS)
```

---

## 8. Telegram/消息桥接机制

### 8.1 BaseChannel 抽象类

```python
class BaseChannel(ABC):
    name: str = "base"
    
    @abstractmethod
    async def start(self) -> None:
        """启动渠道，监听消息"""
        
    @abstractmethod
    async def stop(self) -> None:
        """停止渠道"""
        
    @abstractmethod
    async def send(self, msg: OutboundMessage) -> None:
        """发送消息"""
    
    def is_allowed(self, sender_id: str) -> bool:
        """检查发送者权限"""
        
    async def _handle_message(self, sender_id, chat_id, content, ...):
        """处理入站消息，发布到 bus"""
```

### 8.2 Telegram Channel 实现

```python
class TelegramChannel(BaseChannel):
    async def start(self) -> None:
        # 使用 python-telegram-bot 的长轮询
        self._app = Application.builder().token(self.config.token).build()
        
        # 注册处理器
        self._app.add_handler(CommandHandler("start", self._on_start))
        self._app.add_handler(CommandHandler("new", self._forward_command))
        self._app.add_handler(MessageHandler(..., self._on_message))
        
        await self._app.updater.start_polling(drop_pending_updates=True)
    
    async def _on_message(self, update: Update, context):
        # 下载媒体文件
        # 转录语音消息
        # 转发到消息总线
        await self._handle_message(sender_id, chat_id, content, media, metadata)
    
    async def send(self, msg: OutboundMessage) -> None:
        # Markdown -> HTML 转换
        html = _markdown_to_telegram_html(msg.content)
        await self._app.bot.send_message(chat_id, text=html, parse_mode="HTML")
```

### 8.3 WhatsApp Bridge (Node.js)

```
Python Gateway <--WebSocket--> Node.js Bridge <--Baileys--> WhatsApp Web
```

**Bridge 职责**:
- QR 码认证
- 消息双向转发
- 断线重连

### 8.4 Slack Socket Mode

```python
class SlackChannel(BaseChannel):
    async def start(self) -> None:
        self._socket_client = SocketModeClient(
            app_token=self.config.app_token,
            web_client=self._web_client,
        )
        self._socket_client.socket_mode_request_listeners.append(
            self._on_socket_request
        )
        await self._socket_client.connect()
```

---

## 9. 技能系统实现

### 9.1 技能目录结构

```
skills/
├── memory/
│   └── SKILL.md      # 技能定义
├── weather/
│   └── SKILL.md
└── custom/           # 用户自定义技能
    └── SKILL.md
```

### 9.2 SKILL.md 格式

```markdown
---
name: memory
description: 基于 grep 检索的双层记忆系统
always: true  # 始终加载到上下文
metadata: {"nanobot": {"emoji": "🧠"}}
---

# 记忆

## 搜索过去事件
\`\`\`bash
grep -i "关键词" memory/HISTORY.md
\`\`\`
```

### 9.3 SkillsLoader 实现

```python
class SkillsLoader:
    def list_skills(self, filter_unavailable=True) -> list[dict]:
        """列出所有技能，过滤不满足依赖的"""
        
    def load_skill(self, name: str) -> str | None:
        """加载技能内容"""
        
    def build_skills_summary(self) -> str:
        """构建 XML 格式的技能摘要"""
        
    def get_always_skills(self) -> list[str]:
        """获取 always=true 的技能"""
        
    def _check_requirements(self, skill_meta) -> bool:
        """检查依赖（bins, env）"""
```

### 9.4 渐进式加载策略

```
┌─────────────────────────────────────────────────────────────┐
│                    System Prompt                            │
├─────────────────────────────────────────────────────────────┤
│ 1. 完整加载 always=true 的技能                              │
│                                                             │
│ 2. 技能摘要（XML）：                                        │
│    <skills>                                                 │
│      <skill available="true">                               │
│        <name>weather</name>                                 │
│        <description>查询天气</description>                  │
│        <location>~/.nanobot/workspace/skills/weather/...</> │
│      </skill>                                               │
│    </skills>                                                │
│                                                             │
│ 3. 代理需要时用 read_file 工具加载完整内容                  │
└─────────────────────────────────────────────────────────────┘
```

---

## 10. 设计模式使用

### 10.1 抽象工厂模式
- `BaseChannel` 定义接口，各渠道实现具体逻辑
- `LLMProvider` 定义接口，`LiteLLMProvider`/`CustomProvider` 实现

### 10.2 注册表模式
- `ToolRegistry`: 工具动态注册和查找
- `ProviderSpec` 注册表: LLM 提供者元数据

### 10.3 观察者模式
- `MessageBus._outbound_subscribers`: 订阅出站消息

### 10.4 策略模式
- 不同渠道的消息发送策略
- 不同提供者的 API 调用策略

### 10.5 责任链模式
- 工具验证链：`validate_params` -> `execute`
- 配置匹配链：关键词匹配 -> 回退

### 10.6 单例模式（隐式）
- `SessionManager` 会话缓存
- `MessageBus` 单一实例

---

## 11. 值得关注的代码片段

### 11.1 记忆整合（后台异步任务）

```python
async def _consolidate_memory(self, session, archive_all=False):
    """将旧消息摘要到 MEMORY.md + HISTORY.md"""
    
    # 构建对话摘要
    prompt = f"""你是记忆整合代理。处理这段对话并返回 JSON：
    1. "history_entry": 摘要段落
    2. "memory_update": 更新后的长期记忆
    ..."""
    
    response = await self.provider.chat(messages=[...], prompt)
    result = json_repair.loads(response.content)
    
    # 更新文件
    if entry := result.get("history_entry"):
        memory.append_history(entry)
    if update := result.get("memory_update"):
        memory.write_long_term(update)
```

### 11.2 子代理结果通知

```python
async def _announce_result(self, task_id, label, task, result, origin):
    """通过 system 消息通知主代理"""
    
    msg = InboundMessage(
        channel="system",
        sender_id="subagent",
        chat_id=f"{origin['channel']}:{origin['chat_id']}",
        content=f"[Subagent '{label}' completed]\n\nResult:\n{result}",
    )
    await self.bus.publish_inbound(msg)
```

### 11.3 模型参数覆盖

```python
# providers/registry.py
ProviderSpec(
    name="moonshot",
    model_overrides=(
        ("kimi-k2.5", {"temperature": 1.0}),  # Kimi K2.5 要求 temperature >= 1.0
    ),
)
```

---

## 12. 可能的改进建议

### 12.1 错误处理增强

**现状**: 工具执行错误返回字符串
```python
except Exception as e:
    return f"Error executing {name}: {str(e)}"
```

**建议**: 使用结构化错误类型
```python
@dataclass
class ToolError:
    code: str
    message: str
    retryable: bool = False
```

### 12.2 配置热重载

**现状**: 配置仅在启动时加载

**建议**: 添加配置热重载
```python
class ConfigWatcher:
    async def watch(self, path: Path, on_change: Callable):
        """监控配置文件变化并回调"""
```

### 12.3 工具权限系统

**现状**: 所有工具对所有用户可用

**建议**: 添加工具权限控制
```python
class Tool:
    @property
    def required_permissions(self) -> list[str]:
        return []  # 默认无限制
```

### 12.4 健康检查端点

**现状**: 无 HTTP 健康检查

**建议**: 添加简单的 HTTP 状态端点
```python
@app.get("/health")
async def health():
    return {"status": "ok", "channels": [...], "uptime": ...}
```

### 12.5 消息优先级

**现状**: 所有消息平等处理

**建议**: 支持优先级队列
```python
class InboundMessage:
    priority: int = 0  # 0=普通, 1=高优先级
```

### 12.6 指标收集

**现状**: 无内置指标

**建议**: 添加 Prometheus 指标
```python
from prometheus_client import Counter, Histogram

MESSAGES_PROCESSED = Counter("nanobot_messages_total", "Total messages")
RESPONSE_TIME = Histogram("nanobot_response_seconds", "Response time")
```

### 12.7 会话存储优化

**现状**: JSONL 文件存储

**建议**: 支持可插拔存储后端
```python
class SessionBackend(ABC):
    @abstractmethod
    async def save(self, session: Session): ...
    @abstractmethod
    async def load(self, key: str) -> Session | None: ...

class FileSessionBackend(SessionBackend): ...
class RedisSessionBackend(SessionBackend): ...
```

### 12.8 工具输出流式传输

**现状**: 工具执行完成后返回完整结果

**建议**: 支持流式工具输出
```python
class Tool(ABC):
    async def execute_stream(self, **kwargs) -> AsyncIterator[str]:
        yield "Starting..."
        # ...
        yield "Done"
```

---

## 13. 总结

nanobot 是一个设计良好的轻量级 AI 代理框架，具有以下优点：

**架构优点**:
1. 清晰的模块分离（渠道、代理、工具、配置）
2. 异步消息总线解耦组件
3. 灵活的工具注册系统
4. 渐进式技能加载优化上下文

**代码质量**:
1. 使用 Pydantic 保证类型安全
2. ABC 抽象类定义清晰接口
3. 良好的错误处理和日志记录
4. 支持 camelCase/snake_case 双格式配置

**可扩展性**:
1. 添加新渠道只需实现 `BaseChannel`
2. 添加新工具只需继承 `Tool` 类
3. MCP 集成支持外部工具扩展
4. 技能系统支持用户自定义

**待改进领域**:
1. 监控和指标收集
2. 配置热重载
3. 分布式部署支持
4. 更丰富的权限控制

整体而言，nanobot 的代码组织清晰、设计模式运用得当，是一个值得学习和二次开发的项目。

## 相关笔记
- [[004-ai-tools/claude/claude|Claude Code]] - Anthropic 的 AI 编程工具
- [[004-ai-tools/Cline|Cline]] - VS Code AI 编程助手
- [[004-ai-tools/gemini/README|Gemini]] - Google AI 工具
- [[03-knowledge/技术/人工智能/AntiGravity-GravityClaw-本地优先AI助手|Antigravity/GravityClaw]]
