---
title: screenshot-to-code - 截图转前端代码的 Agentic 引擎
aliases: [screenshot-to-code, 截图转代码, abi screenshot-to-code]
tags:
  - ai-coding-agent
  - frontend
  - multimodal
  - code-generation
  - status/active
  - type/doc
source:
  - "https://github.com/abi/screenshot-to-code"
  - "https://github.com/abi/screenshot-to-code/blob/main/design-docs/agent-tool-calling-flow.md"
  - "https://screenshottocode.com/"
author: abi（@_abi_）
created: 2026-09-08
updated: 2026-09-08
description: 78k-star 项目：截图/Figma/录屏 → HTML/React/Vue 代码，2026 版已演进为多模型 agentic tool-calling 循环（9 工具 + Playwright 视觉自省）
level: intermediate
stars: 4
note: 基于仓库 main 分支实测调研（zread + GitHub API），并交叉验证视频/页面转述的架构描述
---

# screenshot-to-code - 截图转前端代码的 Agentic 引擎

> 把网页截图、设计稿（Figma/Mockup）、甚至屏幕录制视频丢进去，直接产出可运行的前端代码。2023 年底病毒式走红的「截图变代码」鼻祖项目，2026 年已从「单次 Prompt 生成」重构为完整的 agentic tool-calling 引擎。

> [!info] 基本信息
> - **仓库**：https://github.com/abi/screenshot-to-code
> - **Stars** / **Forks**：78,260 / 9,514（GitHub API，2026-09-08；官网落地页仍写 72.9k，已滞后）
> - **作者**：abi（Twitter @_abi_）｜ **协议**：MIT ｜ 主语言 Python
> - **创建**：2023-11-14 ｜ 最近 push：2026-08-14（活跃维护）
> - **商业化**：托管版 screenshottocode.com（`hosted` 分支对接独立 SaaS 代码库）

---

## 目录

- [一、一句话定位](#一一句话定位)
- [二、核心解决的问题](#二核心解决的问题)
- [三、主要功能](#三主要功能)
- [四、架构深潜：从单次生成到 Agentic 循环](#四架构深潜从单次生成到-agentic-循环)
- [五、使用方式](#五使用方式)
- [六、技术栈](#六技术栈)
- [七、仓库结构](#七仓库结构)
- [八、质量工程与评估](#八质量工程与评估)
- [九、安全与隐私](#九安全与隐私)
- [十、应用场景与选型](#十应用场景与选型)
- [十一、验证与勘误记录](#十一验证与勘误记录)

---

## 一、一句话定位

**多模态视觉转代码引擎**：输入截图/设计稿/录屏，输出 HTML+Tailwind / React / Vue / Bootstrap / Ionic 代码，由 Gemini、GPT、Claude 三家模型驱动，并以 Playwright 无头浏览器做视觉自省（visual self-check）闭环。

## 二、核心解决的问题

前端开发中「从视觉意图到代码骨架」的冷启动成本：

| 痛点 | 项目的解法 |
|------|-----------|
| 设计稿 → 手写页面结构/样式耗时 | 多模态模型直接生成整页代码 |
| 截图里的 Logo/图标素材缺失 | Gemini 资产提取（复用真实图）+ Replicate 图像生成补缺 + 自动抠图 |
| 生成结果与视觉目标偏差无反馈 | screenshot_preview 工具：渲染自己生成的页面再截图对比，自我纠错 |
| 只能看静态图，交互行为丢失 | 录屏视频模式：从操作录像推断交互并生成可交互原型 |
| 模型一次生成质量不稳定 | agentic 循环多轮工具调用 + 多 variant 并行对比 |

## 三、主要功能

### 3.1 输入 → 输出矩阵

| 输入 | 输出（6 种技术栈） | 备注 |
|------|------------------|------|
| 网页截图 | HTML + Tailwind（默认） | 资产提取复用真实 Logo |
| Figma / Mockup 设计稿 | HTML + CSS | |
| 屏幕录制视频 | React + Tailwind | **视频模式必须 GEMINI_API_KEY** |
| 竞品界面参考 | Vue + Tailwind | |
| 老系统 UI 逆向 | Bootstrap / Ionic + Tailwind | |

### 3.2 图像资产智能处理（三层流水线）

```
 截图输入
    │
    v
 [extract_assets]──Gemini──> 提取截图中的真实 Logo/图标
    │                          （保真：直接复用原图）
    v
 [generate_images]──Replicate─> 生成截图里没有源文件的素材
    │                          （默认模型 z-image-turbo）
    v
 [remove_backgrounds / edit_images]──Replicate──> 抠图/修图
    │
    v
 [save_assets] ──> 存入产物，代码直接引用
```

没有 REPLICATE_API_KEY 时 `edit_images`/`remove_backgrounds` 直接不可用（Settings 对话框可见工具可用性）——功能是渐进增强设计。

### 3.3 默认模型阵容（README main 分支）

| 提供方 | 代码生成模型 | README 定位 |
|--------|-------------|------------|
| Google | Gemini 3 Flash Preview / Gemini 3.1 Pro Preview | **best models**（且是资产提取+视频模式刚需） |
| OpenAI | GPT-5.5 / GPT-5.4 Mini | |
| Anthropic | Claude Opus 4.6 / Opus 4.8（key 表还列 Opus 5、Fable 5、Sonnet 4.6） | |
| Replicate | z-image-turbo | 图像生成 |

多 key 时自动按 variant 混配更强模型组合；单 key 则只用该 provider。Ollama 本地模型可跑但 README 明言不推荐（质量差）。

## 四、架构深潜：从单次生成到 Agentic 循环

### 4.1 与常见认知的关键差异

多数介绍（包括视频/文章转述）把本项目描述为「组装 Prompt → 分发给大模型 → 流式吐代码」的单次生成。**2026 年的 main 分支不是这样**——它是一个完整的 tool-calling agent：

```
 前端 (React+Vite)                后端 FastAPI (backend/agent/)
 ─────────────────               ──────────────────────────────
 输入截图/录屏  ── WebSocket ──>  AgentEngine._run_with_session()
                                   │
                                   v
                     ┌──── loop（上限 20 轮 tool turns）────┐
                     │  provider stream_turn()               │
                     │    │  thinking_delta / assistant_delta │
                     │    v                                   │
                     │  有 tool_calls? ──无──> finalize        │
                     │    │是                                 │
                     │    v                                   │
                     │  AgentToolRuntime.execute()            │
                     │  （9 个工具，见 4.2）                   │
                     │    │                                   │
                     │    v                                   │
                     │  session.append_tool_results()          │
                     │  （provider 各自的续写格式）            │
                     └────继续下一轮──────────────────────────┘
                                   │
   <── setCode/toolStart/ ────────┘
       toolResult 流式回传         v
                            最终代码 = 内存文件状态
                            （空则从末条 assistant 文本抽 HTML）
```

### 4.2 九个 Agent 工具

| 工具 | 作用 | 依赖 |
|------|------|------|
| `create_file` | 写出代码文件（**边流式边预览**，见 4.3） | LLM key |
| `edit_file` | 修改已生成代码 | LLM key |
| `generate_images` | 生成缺失素材 | Replicate |
| `remove_backgrounds` | 抠图 | Replicate |
| `edit_images` | 修图 | Replicate |
| `extract_assets` | 从截图提取真实 Logo/图标 | Gemini |
| `screenshot_preview` | 渲染生成页 → 截图 → 回喂模型对比纠错 | Playwright Chromium（缺失则静默跳过） |
| `save_assets` | 保存资产进产物 | — |
| `retrieve_option` | 取回用户交互选项 | — |

`screenshot_preview` 就是「视觉自省」的实现：agent 把自己刚写的代码在 headless Chromium 渲染、截图、作为工具结果回喂模型，驱动下一轮自我修正。Chromium 未安装时该工具自动降级不可用，不阻塞主流程。

### 4.3 流式交付的实现细节

WebSocket 消息类型：`assistant` / `thinking` / `toolStart` / `toolResult` / `setCode`。

最巧妙的一处是 **live streamed create_file 预览**：`backend/agent/tools/parsing.py` 在工具参数还在流式到达时就开始解析部分 `content`/`path`，提前发 `toolStart` 并随 `content` 增长逐步推 `setCode`——前端在工具真正执行完之前就能渲染代码预览。

### 4.4 三家 Provider 的续写契约

工具结果回喂时各家消息格式不同，这是多模型 agent 的实际工程成本：

| Provider | 续写机制 |
|----------|---------|
| OpenAI | 历史 items 追加 `function_call_output`（按 `call_id`） |
| Anthropic | assistant `tool_use` blocks + user `tool_result` blocks |
| Gemini | 追加原样模型内容 + `Part.from_function_response`（保 thought-signature 结构） |

## 五、使用方式

### 5.1 三条路径对比

| 路径 | 适合 | 成本 |
|------|------|------|
| 托管版 screenshottocode.com | 零_setup 试一下 | 按量付费 |
| Docker 一键起 | 快速自托管 | 4 个 API key |
| 本地开发跑 | 二次开发/贡献 | Poetry + pnpm 全家桶 |

### 5.2 Docker（最快自托管）

```bash
echo "OPENAI_API_KEY=sk-your-key" > .env
echo "ANTHROPIC_API_KEY=your-key" >> .env
echo "GEMINI_API_KEY=your-key" >> .env
echo "REPLICATE_API_KEY=r8_your-key" >> .env
docker-compose up -d --build
# 打开 http://localhost:5173
```

### 5.3 本地开发

```bash
# 后端（Poetry，Python ^3.10，实际解析到 3.12）
cd backend
poetry install
poetry run playwright install chromium   # 可选：启用视觉自省工具
poetry run uvicorn main:app --reload --port 7001

# 前端（pnpm + Vite）
cd frontend
pnpm install
pnpm dev   # http://localhost:5173
```

实测陷阱（来自仓库 AGENTS.md，非 README）：

- Vite dev server 只绑 `localhost`——用 `http://localhost:5173`，**用 `127.0.0.1:5173` 会拒连**
- `pnpm install` 的 esbuild/puppeteer build-scripts 警告无害，可忽略
- `pnpm lint --max-warnings 0` 有存量告警（如 `generateCode.ts` 的 no-explicit-any），是基线问题
- REPLICATE_API_KEY 只认 `backend/.env`，UI 设置对话框配不了
- OpenAI 区域限制可设 `OPENAI_BASE_URL` 代理（路径须含 `/v1`）

## 六、技术栈

| 层 | 技术 |
|----|------|
| 前端 | React + Vite + Tailwind + shadcn/ui（components.json）+ Zustand（store/）+ pnpm |
| 后端 | Python 3.10+（Poetry）+ FastAPI + WebSocket |
| 视觉自省 | Playwright headless Chromium |
| 图像处理 | Replicate API（z-image-turbo 默认） |
| LLM | OpenAI Responses API / Anthropic Messages / Gemini（各成 provider session） |
| 质检 | pytest + pyright（AGENTS.md 强制每次改动跑） |

## 七、仓库结构

```
├── backend/
│   ├── agent/            # ★ agentic 引擎核心
│   │   ├── engine.py     #   tool-calling 主循环（20 轮上限）
│   │   ├── runner.py     #   Agent 入口（thin wrapper）
│   │   ├── providers/    #   openai / anthropic / gemini 续写适配
│   │   └── tools/        #   9 工具定义 + 运行时 + 流式参数解析
│   ├── prompts/          # create/ update 两套 + design_system + policies
│   ├── preview_screenshot/  # Playwright 截图后端（registry 模式）
│   ├── image_generation/    # Replicate 封装
│   ├── uploaded_assets/     # 用户上传资产存取
│   ├── costs/            # token 计价与用量追踪
│   ├── evals/            # 评估框架（含 asset_extraction_benchmark）
│   └── routes/           # generate_code / screenshot / export 等 FastAPI 路由
├── frontend/             # React+Vite 应用（5173）
├── design-docs/          # ★ 7 篇架构决策文档（agent 流程/variant 系统等）
├── blog/                 # evaluating-claude.md（模型评估笔记）
├── AGENTS.md / CLAU.md   # agent 协作指令（测试/type-check 政策）
└── docker-compose.yml
```

值得注意的工程信号：`design-docs/`（agent-tool-calling-flow、agentic-runner-refactor、variant-system 等 7 篇）+ `evals/` 完整评估框架 + `costs/` 计价模块——这不是 demo 级仓库，是按产品标准演进的主干。

## 八、质量工程与评估

- **测试政策**（AGENTS.md）：每次代码改动必跑 `pytest` + `pyright`，改动文件零新增告警；40+ 测试文件覆盖 provider 配置、工具运行时、websocket 通信、token 计量等
- **评估框架**：`backend/evals/` 独立成体系（runner/sessions/sets），含资产提取专项 benchmark；根目录另有 `Evaluation.md`、`QA.md`、`TESTING.md`
- **模型横向评估**：`blog/evaluating-claude.md` 记录对 Claude 系的实测评估
- **variant 系统**：同一次生成并行产出多个变体（design-docs/variant-system.md），用户对比挑选——这是「多模型混配」的产品化落点

## 九、安全与隐私

- 截图/录屏会发送到所配 LLM provider（OpenAI/Anthropic/Google）与 Replicate——**自托管不等于数据不出境**，出境地由你的 key 决定
- API key 可全部放 `backend/.env`（服务端），前端不持有密钥
- 支持 `OPENAI_BASE_URL` 指向自建网关/代理，可做审计中转
- MIT 协议，无 CLA；hosted 商业版为独立代码库，与 OSS 版边界清晰

## 十、应用场景与选型

### 10.1 场景判断树

```
你的需求是什么？
│
├─ 只想快速试一次效果
│    └─> 托管版 screenshottocode.com（免 key 免装）
│
├─ 敏感 UI / 内网设计稿，不能出第三方云
│    └─> ⚠️ 本项目仍需调外部 LLM API——考虑内网代理网关
│         + OPENAI_BASE_URL，或评估本地多模态模型（质量打折）
│
├─ 高频使用 + 想控成本/定制技术栈规则
│    └─> Docker 自托管 + 四 key 全配（Gemini+Replicate 强烈建议）
│
└─ 想改 prompt / 加技术栈 / 做研究
     └─> 源码跑（Poetry+pnpm），从 backend/prompts/ 入手
```

### 10.2 最佳实践清单

- ✅ GEMINI_API_KEY 必配（资产提取 + 视频模式 + README 认定 Gemini 系最强）
- ✅ 装上 Playwright Chromium（screenshot_preview 自省闭环是质量放大器）
- ✅ 多 key 混配，用 variant 对比选优
- ❌ 别指望 Ollama 本地模型（README 自评质量差）
- ❌ 别用 127.0.0.1 访问前端 dev server（绑的是 localhost）

## 十一、验证与勘误记录

| 转述声称（视频/页面 Insights） | 验证结果 | 来源 |
|------|---------|------|
| 前后端分离：React+Vite / FastAPI(Python) | ✅ 完全一致 | README + AGENTS.md + repo 结构 |
| WebSocket 实时通信管道 | ✅ `VITE_WS_BACKEND_URL`（默认 ws://127.0.0.1:7001），消息类型 assistant/thinking/toolStart/toolResult/setCode | AGENTS.md + design-docs |
| 「组装结构化 Prompt 分发至 Gemini/GPT/Claude 生成代码」 | ⚠️ 方向对但**严重低估**：main 分支是 agentic tool-calling 循环（9 工具、20 轮上限、provider 续写适配），非单次 prompt→code | design-docs/agent-tool-calling-flow.md |
| Playwright Headless Chromium 渲染自检并驱动自我纠错 | ✅ `screenshot_preview` 工具；Chromium 缺失时静默跳过（渐进增强） | README + agent 工具清单 |
| 流式交付、即时渲染 | ✅ 且有 live create_file 预览：工具参数流式解析、边到边渲染 | design-docs + tools/parsing.py |
| 提取真实 Logo 图标 + 图像生成补缺 + 自动抠图 | ✅ extract_assets（Gemini）/ generate_images（z-image-turbo）/ remove_backgrounds+edit_images（Replicate） | README + 工具清单 |
| 六技术栈（HTML+Tailwind/CSS、React、Vue、Bootstrap、Ionic） | ✅ 与 README 完全一致 | README |
| （官网）72.9k stars | ⚠️ 落地页滞后；GitHub API 实测 78,260（2026-09-08） | api.github.com |

> [!note] 调研方法
> zread 读取 main 分支 README/AGENTS.md/design-docs + GitHub API 实时统计交叉验证。仓库 2023-11 创建即爆红，2026 年主干已 agentic 化并配套 evals/costs/design-docs 体系——引用旧架构描述（单次 prompt 生成）时注意时效。

---

## 参考资料

- [abi/screenshot-to-code 仓库](https://github.com/abi/screenshot-to-code)
- [Agent Tool-Calling Flow 设计文档](https://github.com/abi/screenshot-to-code/blob/main/design-docs/agent-tool-calling-flow.md)
- [托管版产品页](https://screenshottocode.com/)
- [GitHub API 仓库元数据](https://api.github.com/repos/abi/screenshot-to-code)

## 相关笔记

- [[Qwen3.8-27B 回归 Dense 实战 - vLLM 调参、四模型对比与 Hermes 蜂群架构]]（多模型 agent 架构对比视角）
- [[Ego-Lite-AI-Agent-浏览器自动化]]（同属 agent + 浏览器工具调用范式）
- [[show-me-让AI把我懂了画出来]]（agent 沟通结构化：图 vs 代码的另一面）
