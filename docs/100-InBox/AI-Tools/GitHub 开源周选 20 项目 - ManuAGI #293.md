---
title: "GitHub 开源周选 20 项目：Agent 生态与本机回归"
aliases: ["ManuAGI #293", "GitHub 周报 293", "BrowserSkill Coder pgrust 那期"]
tags:
  - github-trending
  - ai-agents
  - self-hosted
  - status/active
  - type/doc
source:
  - https://www.youtube.com/watch?v=GBcIPr_LJTE
author: ManuAGI - AutoGPT Tutorials
created: 2026-09-20
updated: 2026-09-20
description: 20 个本周热门开源项目盘点：AI Agent 工具链、本机优先自托管、CV 与生活 IoT 三大主线，附星数实测与 ASR 勘误
level: beginner
stars: 3
---

# GitHub 开源周选 20 项目：Agent 生态与本机回归

> ManuAGI 频道第 293 期周报（2026-09-19 发布，14:03）。20 个项目围绕两条主线：**AI Agent 的操作落地与治理**，以及**基础设施与私有数据向本机回归**。视频为口播盘点，每项目约 40 秒，本笔记结合各仓库 README 做了交叉验证与勘误。

## 目录

- [视频信息](#视频信息)
- [20 项目全景表](#20-项目全景表)
- [一、AI Agent 与工作流](#一ai-agent-与工作流)
- [二、本机优先与自托管](#二本机优先与自托管)
- [三、CV 与生活 IoT](#三cv-与生活-iot)
- [验证与勘误记录](#验证与勘误记录)
- [深挖亮点](#深挖亮点)
- [选型建议](#选型建议)
- [总结](#总结)
- [参考资料](#参考资料)

---

## 视频信息

| 项目 | 内容 |
|------|------|
| 频道 | ManuAGI - AutoGPT Tutorials |
| 发布 | 2026-09-19 |
| 时长 | 14:03 |
| 性质 | 每周 GitHub 开源项目盘点（#293） |
| 本期数量 | 20 个项目 + 频道推广（新频道 AI Agents Studio） |

---

## 20 项目全景表

星数为 2026-09-20 实抓 GitHub 页面值；「—」表示未单独验证。

| # | 时间戳 | 项目 | 仓库 | Stars | 一句话定位 | 领域 |
|---|--------|------|------|-------|-----------|------|
| 1 | 00:56 | BrowserSkill | Tencent/BrowserSkill | 5.4k | Agent 复用你已登录的浏览器，独立视窗不抢屏 | Agent |
| 2 | 01:33 | Ghidra | NationalSecurityAgency/ghidra | — | NSA 开源逆向工程框架 | 逆向 |
| 3 | 02:13 | knowledge-work-plugins | anthropics/knowledge-work-plugins | 23.6k | Anthropic 官方角色插件库（Claude Cowork/Code） | Agent |
| 4 | 02:54 | supervision | roboflow/supervision | — | 模型无关的计算机视觉统一工具库 | CV |
| 5 | 03:37 | Ever Gauzy | ever-co/ever-gauzy | — | ERP/CRM/HRM/PM 一体化开源商业管理平台 | 商业 |
| 6 | 04:17 | Coder | coder/coder | — | 自托管云开发环境（CDE）+ AI Agent 治理 | 基础设施 |
| 7 | 04:57 | Superpowers | obra/superpowers | — | Agent 编程方法论：先规格后代码 + TDD | 方法论 |
| 8 | 05:36 | Home Assistant | home-assistant/core | — | 本机优先智能家居中枢 | IoT |
| 9 | 06:12 | LibreChat | danny-avila/LibreChat | — | 自托管多模型 ChatGPT 替代 | AI 网关 |
| 10 | 06:56 | Agent-Reach | Panniantong/Agent-Reach | 82.5k | 免 API 费的 Agent 网络读取/搜索 CLI | Agent |
| 11 | 07:32 | awesome-gpt-image-2 | freestylefly/awesome-gpt-image-2 | 31.3k | GPT Image 2/2.5 提示词库（Prompt-as-Code） | 图像 |
| 12 | 08:17 | diagram-design | cathrynlavery/diagram-design | 38.4k | Claude Code 品牌匹配图表生成 skill | 图表 |
| 13 | 08:57 | Munder Difflin | chaitanyagiri/munder-difflin | — | 把 CLI Agent 组成 2D 虚拟办公室协同团队 | 多 Agent |
| 14 | 09:34 | minimind | jingyaogong/minimind | 55.0k | 2 小时从零训练 64M 参数迷你 LLM | 学习 |
| 15 | 10:12 | Securo | securo-finance/securo | — | 隐私优先自托管个人记账 | 财务 |
| 16 | 10:51 | hister | asciimoo/hister | 3.7k | 私有全文搜索引擎（网页+文件） | 搜索 |
| 17 | 11:26 | pgrust | malisper/pgrust | — | Rust 重写 PostgreSQL，回归测试 100% | 数据库 |
| 18 | 12:06 | OpenBot | CopilotKit/OpenBot | 4.8k | 受治理的 AI 同事，每动作先审核后落审计 | Agent |
| 19 | 12:43 | open-compute | elliothux/open-compute | — | 单 Rust 二进制自托管 Cloudflare Workers | 边缘 |
| 20 | 13:22 | fugleramme | arnegiacomo/fugleramme | — | 树莓派电子纸鸟框：BirdNET 听声辨鸟 | IoT |

---

## 一、AI Agent 与工作流

本类 8 个项目，覆盖 Agent 的「手」（浏览器/网络访问）、「脑」（方法论/治理）、「同事」（角色化/团队化）三个层次。

### 1.1 操作层：让 Agent 碰真实环境

| 项目 | 解决什么 | 关键机制 |
|------|---------|---------|
| BrowserSkill | Agent 需要已登录会话但抢用户浏览器 | 独立可见 Agent 视窗 + tab 借还机制 |
| Agent-Reach | 各平台 API 贵/难申请 | CLI 免 API 费直读 Twitter/Reddit/Bilibili 等 |

**BrowserSkill（Tencent，5.4k★，MIT）**核心是 `bsk` CLI + 浏览器扩展，Rust + TS workspace。任何 shell 型 Agent（Claude Code、Cursor、Codex）都能驱动，无模型/框架锁定。最有意思的设计是 **tab 借还**：

```
用户浏览器 ──┬── 主工作区（Agent 不碰）
             └── 借出 tab ──> Agent 独立视窗操作
                                │
                    遇 CAPTCHA ──> 交还控制权给用户
                                │
                    完成 ──> 归还 tab（复用登录态）
```

仓库结构（实测）：`crates/bsk-cli`（CLI + 守护进程）、`crates/bsk-protocol`（线协议）、`apps/extension`（扩展）、`evals/browser`（Agent 中立浏览器能力评测）。521 commits，活跃开发中。

**Agent-Reach（Panniantong，82.5k★，MIT）**本期星数之王。每平台有首选后端 + fallback 链，单点失效自动切换；cookie 全程本地；自动注册 skill 让 Agent 知道何时调用。支持 Claude Code、Cursor、OpenClaw。

### 1.2 治理层：先审后做，全程留痕

| 项目 | 方法 | 检查点位置 |
|------|------|-----------|
| Superpowers | 规格先行 + TDD + 四阶段调试 + 子 Agent 审查 | 写代码之前 |
| OpenBot | 政策网关：执行前评估 + 执行后审计行 | 浏览器动作之前/之后 |

**Superpowers（obra）**：反直觉的核心是「先别写代码」——从对话提炼 spec，切成人类可读的小块逐一批准，再进入 TDD 循环。以 Claude Code / Codex 插件形式安装。

**OpenBot（CopilotKit，4.8k★，MIT）**：每个 AI 同事配一台「电脑」（浏览器/文件/工具），治理不绑框架——骑在 **AG-UI 协议**上，LangGraph、CrewAI、Google ADK 或手写的 Agent 都能接入。Server 网关在浏览器动作前评估政策、动作后写审计行。

### 1.3 角色层与内容生成

- **knowledge-work-plugins（Anthropic 官方，23.6k★，Apache-2.0）**：把 Claude 变成特定领域专家的角色插件库，用于 Claude Cowork 与 Claude Code。实测目录含 14+ 插件：bio-research、customer-support、data、design、engineering、enterprise-search、finance、human-resources、legal、marketing、operations 等，每个打包该领域的 commands + skills。通过 MCP 接 Slack/Notion/Jira/Snowflake 后变强。
- **awesome-gpt-image-2（freestylefly，31.3k★，MIT，中文项目）**：Prompt-as-Code——530+ 真实案例逆向成 20+ 结构化模板，蒸馏为 Agent Skill；含 GPT Image 2.5 同提示词对比专区（`gpt-image-2-5/` 目录实测存在）。npm 安装 + 在线 gallery。
- **diagram-design（cathrynlavery，38.4k★）**：Claude Code/Codex/Pi 用的图表 skill，27 种类型 × 3 变体，输出自包含 HTML/SVG。亮点是品牌匹配：指向你的网站，自动读色板/字体映射为语义 token 并检查对比度。
- **Munder Difflin（chaitanyagiri，MIT）**：本地优先桌面应用，把已付费的 CLI 订阅（Claude Code、Codex、Gemini、Copilot）组成自协同团队——每个 Agent 有长期记忆、信箱和 2D 办公室工位，各自独立终端进程。像素画来自 LimeZu（资产单独授权），名字致敬《The Office》的 Dunder Mifflin。早期原型。

---

## 二、本机优先与自托管

### 2.1 开发环境与边缘计算

**Coder（coder/coder）**：自托管 CDE 平台。workspace 用 Terraform 定义（EC2 VM / K8s Pod / Docker 容器），WireGuard 隧道连接，闲置自动关机省钱。AI Agent 跑在自己基础设施上：凭证不进 workspace、动作绑定用户，AI 网关集中做治理/成本/审计。

**open-compute（elliothux，Apache-2.0）**：把 Cloudflare Workers 编程模型塞进**单个 Rust 二进制**——KV、D1、R2、Durable Objects、Queues、Workflows 全在内，用熟悉的 Wrangler 部署。无 Kubernetes/Redis/数据库 sidecar。包内 workerd fork 遵循 Cloudflare 上游许可。pre-1.0，Lynx AI 赞助。

### 2.2 数据自主

| 项目 | 数据类型 | 认证/安全 |
|------|---------|----------|
| LibreChat | 对话 + 文档 | 多用户、SSO/OIDC |
| Securo | 财务 | Passkeys/TOTP/OIDC |
| hister | 浏览历史 + 本地文件 | 本地索引 |

- **LibreChat（danny-avila）**：一个界面接所有 provider（OpenAI/Anthropic/Google/AWS Bedrock/DeepSeek/OpenRouter/任意 OpenAI 兼容端点），会话中可切模型。Agents、MCP、代码解释器、artifacts、图像生成、文件搜索、网页搜索全有。Docker 部署。
- **Securo（securo-finance，AGPL-3.0）**：自托管记账，公司与你的财务数据之间不隔任何厂商。多账户 + 余额、OFX/QIF/CAMT/CSV 导入、规则引擎自动分类、周期交易与预算跟踪。一条命令 Docker 安装。技术栈：前后端分离 + mise 工具链。
- **hister（asciimoo，3.7k★，AGPLv3，Go）**：私有搜索引擎，索引**全文内容**而非仅标题/URL。浏览器扩展自动索引访问过的页面，支持爬站/导入历史；查询支持过滤/短语/通配符/取反 + 可选语义搜索；接口三件套——Web UI、终端、MCP（AI 助手直接查）。
- **Ever Gauzy（ever-co）**：ERP + CRM + HRM + 招聘 ATS + 项目管理一体化，含工时追踪（截图/活动监控）与开票。Web + 三平台桌面客户端，Angular + NestJS，可 K8s 自托管。

### 2.3 底层重构

- **Ghidra（NSA）**：老牌开源逆向工程框架，反汇编/反编译/绘图，覆盖多处理器指令集与可执行格式，Java/Python 可扩展，面向大型团队协作分析。
- **pgrust（malisper，AGPL-3.0，v0.3）**：Rust 从零重写 PostgreSQL。README 徽章实测：**兼容 Postgres 18.6，回归测试套件 46,066/46,066 = 100% 通过**，磁盘目录兼容（可直接从现有 PG 数据目录启动）。架构改进：向量化 JIT 执行器、线程替代进程、内置 OOM killer。自称比 Postgres 和 ClickHouse都快（尚未 production ready，官方明确「下一步是让人敢把数据交给它」）。v0.3 新增 `cbstore` 与 `pgrcolumnar` 存储引擎。不收 PR，走 Discord 协作。

---

## 三、CV 与生活 IoT

- **supervision（Roboflow）**：模型无关 CV 工具库，核心是统一 Detections API + 各家输出转换器（Ultralytics YOLO、Roboflow inference、HF Transformers、SAM、Detectron2、VLM），换模型不改代码。标注、追踪、区域计数、数据集加载/切分/评估。
- **Home Assistant（Open Home Foundation）**：本机优先智能家居中枢，Python 应用跑在自己的硬件（树莓派/家庭服务器），整合 Zigbee/Z-Wave/Matter/MQTT 数千设备，断云可用。
- **minimind（jingyaogong，55.0k★，Apache-2.0，中文项目）**：教育向迷你 LLM——64M 参数约 2 小时可在平价硬件训完，CPU 都能跑。覆盖 Pre-train/SFT/LoRA/DPO/GRPO/工具调用/蒸馏全流程，模型代码对齐 Qwen3 生态（dense 与 MoE 变体），兼容 transformers/LLaMA-Factory/Ollama。
- **fugleramme（arnegiacomo，挪威语「鸟框」）**：树莓派电子纸相框，听声辨鸟：

```
麦克风 ──> BirdNET-Go（本地分类器）
              │ 检测事件
              v
        fugleramme 渲染引擎
              │ 匹配公有领域 19 世纪手绘插画
              v
        Inky-Impression 电子纸面板
        （体型大的居中排布，鸟群变化时重绘）
```

800 幅插画覆盖 400+ 物种；可跑电子纸面板或 Web kiosk。`install.sh` 一键装成 systemd 服务，作者还提供成品代做。

---

## 验证与勘误记录

视频口播与 Content Insights 同源于 YouTube 自动转写（ASR），英文专有名词错名同源。以下为逐一核对仓库后的勘误：

| 视频/Insights 名称 | 实际项目 | 证据 | 结果 |
|-------------------|---------|------|------|
| Zupervision | **supervision** | 描述链接 `roboflow/supervision`；视频标题也拼对 | ⚠️ 音译错名 |
| Evergazi | **Ever Gauzy** | 描述链接 `ever-co/ever-gauzy` | ⚠️ 音译错名 |
| Mundifflin | **Munder Difflin** | 描述链接 `chaitanyagiri/munder-difflin` | ⚠️ 音译错名 |
| Fugler / Fuglerom | **fugleramme** | 描述链接 `arnegiacomo/fugleramme` | ⚠️ 截短 |
| pig-rust | **pgrust** | 描述链接 `malisper/pgrust`（标题同拼错） | ⚠️ 音译错名 |
| BAN's Agent Reach | **Panniantong 的 Agent-Reach** | 仓库 owner 实为 Panniantong | ⚠️ ASR 噪声 |
| 10 cent | Tencent | 仓库 owner 实为 Tencent | ⚠️ ASR 噪声 |
| Goodra | Ghidra | 上下文 + 描述链接 | ⚠️ ASR 噪声 |
| Libra chat | LibreChat | 描述链接 | ⚠️ ASR 噪声 |
| Quinn 3 生态（minimind） | Qwen3 | minimind README 对齐 Qwen3 | ⚠️ ASR 错名 |
| 「騰訊開源的 Browser Skill」 | 属实 | `Tencent/BrowserSkill` 官方仓库 | ✅ |
| 「Anthropic 開源的 11 套角色外掛」 | 属实（14+ 目录） | `anthropics/knowledge-work-plugins` | ✅ |
| 「NSA 開源 Ghidra」 | 属实 | 公认事实 | ✅ |
| 「530+ 案例、20+ 模板」 | 属实 | README About 原文 | ✅ |
| 「46,000+ 回归测试」 | 实为 46,066 全过 | README 徽章 46066/46066 | ✅ |
| 「64M 参数 2 小时」 | 属实 | minimind About 原文 | ✅ |
| 「27 种图表类型」 | 属实（diagram-design） | 描述 + README | ✅ |

归属提醒：本期无「大厂挂名第三方项目」的营销话术——所有公司归属声称（Tencent、Anthropic、NSA、Roboflow、CopilotKit）经核验属实。

---

## 深挖亮点

星数快照（2026-09-20）与视频口播的差异及背景：

1. **Agent-Reach 82.5k★** —— 本期星数第一，视频未强调。中国开发者 Panniantong 的项目，MIT 开源，「零 API 费」定位踩中了 Agent 数据访问痛点。
2. **minimind 55.0k★** —— 中文教育向项目，星数超大多数 infra 项目；Qwen3 生态对齐让它不只是玩具。
3. **diagram-design 38.4k★ / awesome-gpt-image-2 31.3k★** —— 两个「内容生产」类 skill 项目星数远超平台类（Coder、LibreChat 量级），印证 skill 生态是当前增长最快的品类。awesome-gpt-image-2 也是中文项目。
4. **hister = Searx 作者 asciimoo 的新作** —— 视频完全没提。Searx（隐私元搜索）创建者的第二个隐私搜索项目，从「代理搜索」转向「本地全文索引」，Go 实现，AGPLv3。对老用户这是强信号。
5. **pgrust 100% 回归通过** —— 46,066 项测试全绿 + 磁盘目录兼容，说明它不是概念验证而是行为级复刻。「比 Postgres 和 ClickHouse 都快」的声称激进，但官方同时明确 not production ready，措辞诚实。
6. **open-compute 与 pgrust 同走「单二进制吞平台」路线** —— 前者吞 Cloudflare Workers，后者吞 Postgres。2026 年开源趋势：用 Rust 把托管平台的复杂度压回一个可复制文件。

---

## 选型建议

```text
想给 Agent 接浏览器？
├─ 要复用登录态、不抢屏 ──────────> BrowserSkill
└─ 只要读网页/搜平台内容 ──────────> Agent-Reach

担心 Agent 乱来？
├─ 编码场景，要流程纪律 ──────────> Superpowers（规格先行）
└─ 通用场景，要动作级审计 ─────────> OpenBot（政策网关）

数据不想出门？
├─ 对话记录 ─────────────────────> LibreChat
├─ 记账 ─────────────────────────> Securo
└─ 浏览历史/文件检索 ─────────────> hister

想自托管平台级设施？
├─ 开发环境 ─────────────────────> Coder
├─ Workers/边缘函数 ──────────────> open-compute（pre-1.0）
└─ 数据库 ───────────────────────> pgrust（not production ready）

学习/玩具？
├─ 从零训 LLM ───────────────────> minimind
├─ 听声辨鸟 ─────────────────────> fugleramme
└─ 认识 Agent 工具链 ─────────────> knowledge-work-plugins
```

注意两处成熟度红线：open-compute pre-1.0、pgrust not production ready——都值得围观，暂不值得上生产。

---

## 总结

本期 20 个项目验证了视频自己的判断：开发者正从「串大模型 API」转向**给 Agent 配手、配脑、配同事**（8/20 是 Agent 工具链），同时把运算、对话、财务、搜索、甚至数据库往本机搬（8/20 自托管）。剩下 4 个 CV/IoT 项目里，supervision 和 Home Assistant 是常青树，minimind 和 fugleramme 代表「轻量 AI + 边缘硬件」的教育与生活侧。

值得直接动手的三个：**BrowserSkill**（装完即提升所有 shell Agent 的浏览器能力）、**hister**（Searx 作者出品，MCP 接口对 AI 工作流友好）、**minimind**（周末就能跑完一次完整 LLM 训练）。

---

## 参考资料

- [视频：Top Open-Source GitHub Projects #293](https://www.youtube.com/watch?v=GBcIPr_LJTE)
- [Tencent/BrowserSkill](https://github.com/Tencent/BrowserSkill) · [anthropics/knowledge-work-plugins](https://github.com/anthropics/knowledge-work-plugins) · [Panniantong/Agent-Reach](https://github.com/Panniantong/Agent-Reach)
- [freestylefly/awesome-gpt-image-2](https://github.com/freestylefly/awesome-gpt-image-2) · [cathrynlavery/diagram-design](https://github.com/cathrynlavery/diagram-design) · [chaitanyagiri/munder-difflin](https://github.com/chaitanyagiri/munder-difflin)
- [elliothux/open-compute](https://github.com/elliothux/open-compute) · [securo-finance/securo](https://github.com/securo-finance/securo) · [asciimoo/hister](https://github.com/asciimoo/hister)
- [malisper/pgrust](https://github.com/malisper/pgrust) · [CopilotKit/OpenBot](https://github.com/CopilotKit/OpenBot) · [jingyaogong/minimind](https://github.com/jingyaogong/minimind) · [arnegiacomo/fugleramme](https://github.com/arnegiacomo/fugleramme)

## 相关笔记

- [[Superpowers]]（Agent 方法论，obra 的 skills 框架）
- [[Claude Code 插件生态]]
- [[自托管基础设施]]
