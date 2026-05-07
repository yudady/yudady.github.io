---
title: notebooklm-py - NotebookLM 非官方 Python API 与 Agent Skill
aliases: [NotebookLM Python API, NotebookLM CLI, notebooklm-py Hermes]
tags:
  - notebooklm
  - python
  - ai-agent
  - google-ai
  - cli
  - status/active
  - type/tool
source:
  - "https://github.com/win4r/notebooklm-py"
  - "https://github.com/teng-lin/notebooklm-py"
author: teng-lin (upstream) / win4r (Hermes fork)
created: 2026-05-06
updated: 2026-05-06
description: 非官方 NotebookLM Python API，支持 CLI、Python 异步 API、AI Agent 集成（Claude Code/Codex/Hermes/OpenClaw），提供网页端不开放的功能如批量下载、Quiz JSON 导出、思维导图数据提取
level: intermediate
stars: 4
---

# notebooklm-py - NotebookLM 非官方 Python API 与 Agent Skill

> 完整的 NotebookLM 程序化访问能力。通过 Python API、CLI、AI Agent 三种方式，操作 NotebookLM 的全部功能——包括网页 UI 不开放的批量下载、结构化导出等特性。win4r 的 fork 在上游基础上增加了 **Hermes Agent 兼容性**和**安全加固**。

---

## 目录

- [仓库信息](#仓库信息)
- [核心价值](#核心价值)
- [功能覆盖](#功能覆盖)
- [三种使用方式](#三种使用方式)
- [网页端不具备的独有功能](#网页端不具备的独有功能)
- [Hermes Agent 安装指南](#hermes-agent-安装指南)
- [认证方案](#认证方案)
- [Python API 速查](#python-api-速查)
- [CLI 速查](#cli-速查)
- [架构与风险](#架构与风险)
- [参考资料](#参考资料)

---

## 仓库信息

| 项目 | 详情 |
|------|------|
| **上游仓库** | [teng-lin/notebooklm-py](https://github.com/teng-lin/notebooklm-py) |
| **Hermes Fork** | [win4r/notebooklm-py](https://github.com/win4r/notebooklm-py) |
| **协议** | MIT |
| **语言** | Python 3.10+ |
| **上游 Stars** | ~19k |
| **PyPI 版本** | 0.3.4（已落后上游 main ~20 commits） |
| **Fork 审计标签** | `v0.3.4-hermes.4`（含上游 main 修复 + PR #298/#279） |
| **Fork 额外内容** | Hermes skill layout、SECURITY_AUDIT.md、import_browser_cookies.py |

```
上游 teng-lin/notebooklm-py (main, ~19k stars)
    │
    ├── PyPI v0.3.4 (落后 main ~20 commits)
    │
    └── fork: win4r/notebooklm-py
            │
            ├── v0.3.4-hermes.4 (审计标签)
            │   ├── 上游 main 全部代码
            │   ├── cherry-pick #298 (cookie 自动刷新)
            │   ├── cherry-pick #279 (Playwright venv 修复)
            │   ├── skills/notebooklm/ (Hermes skill layout)
            │   ├── SECURITY_AUDIT.md
            │   └── import_browser_cookies.py
            │
            └── main (跟踪上游 + Hermes additions)
```

---

## 核心价值

```
┌──────────────────────────────────────────────────────────┐
│                  没有 notebooklm-py                       │
│                                                          │
│  手动打开网页 → 上传文件 → 等待处理 → 逐个下载            │
│  ⚠ 无法批量操作  ⚠ 无法结构化导出  ⚠ 无法自动化          │
├──────────────────────────────────────────────────────────┤
│                  有了 notebooklm-py                       │
│                                                          │
│  CLI 一行命令 / Python 几行代码                           │
│  ✅ 批量创建笔记本  ✅ 批量导入来源  ✅ 自动生成内容       │
│  ✅ 批量下载产物  ✅ Agent 直接调用  ✅ CI/CD 集成        │
└──────────────────────────────────────────────────────────┘
```

---

## 功能覆盖

### 笔记本与来源管理

| 能力 | 支持的格式 |
|------|-----------|
| 笔记本 | 创建、列表、重命名、删除 |
| 来源类型 | URL、YouTube、PDF、文本、Markdown、Word、音视频、图片、Google Drive、粘贴文本 |
| 来源操作 | 添加、刷新、获取指南、获取全文 |
| 研究代理 | Web 快速/深度研究 + Drive 搜索，结果自动导入为来源 |
| 分享 | 公开/私有链接、用户权限（查看者/编辑者） |

### 内容生成（全部 Studio 类型）

| 类型 | 选项 | 下载格式 |
|------|------|----------|
| Audio Overview | 4 格式（深度/简报/评论/辩论）× 3 长度 × 50+ 语言 | MP3/MP4 |
| Video Overview | 3 格式（讲解/简报/电影感）× 9 视觉风格 | MP4 |
| Slide Deck | 详细/演讲者格式，可调长度，单页修改 | PDF/PPTX |
| Infographic | 3 方向 × 3 精细度 | PNG |
| Quiz | 可配置数量和难度 | JSON/Markdown/HTML |
| Flashcards | 可配置数量和难度 | JSON/Markdown/HTML |
| Report | 简报/学习指南/博客/自定义 prompt | Markdown |
| Data Table | 自然语言自定义结构 | CSV |
| Mind Map | 交互式层级可视化 | JSON |

---

## 三种使用方式

| 方式 | 最适合 | 复杂度 |
|------|--------|--------|
| **Python API** | 应用集成、异步工作流、自定义管道 | 中等 |
| **CLI** | Shell 脚本、快速任务、CI/CD 自动化 | 低 |
| **Agent 集成** | Claude Code、Codex、Hermes Agent、OpenClaw | 需要配置 |

### Agent 集成选项

```
┌─────────────────────────────────────────────────┐
│  Option 1: CLI install                          │
│  notebooklm skill install                       │
│  → ~/.claude/skills/notebooklm                  │
│  → ~/.agents/skills/notebooklm                  │
├─────────────────────────────────────────────────┤
│  Option 2: npx install                          │
│  npx skills add win4r/notebooklm-py            │
│  (open skills ecosystem)                        │
├─────────────────────────────────────────────────┤
│  Option 3: Hermes Agent ← 推荐                   │
│  hermes skills tap add win4r/notebooklm-py      │
│  hermes skills install ... --force              │
│  → skills/notebooklm/ layout                   │
└─────────────────────────────────────────────────┘
```

---

## 网页端不具备的独有功能

这些功能只能通过 API/CLI 使用，NotebookLM 网页 UI 不提供：

| 功能 | 说明 |
|------|------|
| **批量下载** | 一次下载某类型的全部产物 |
| **Quiz/Flashcard 结构化导出** | 导出 JSON/Markdown/HTML（网页端只有交互视图） |
| **思维导图数据提取** | 导出层级 JSON 供可视化工具使用 |
| **Data Table CSV 导出** | 下载结构化表格为电子表格 |
| **Slide Deck PPTX** | 下载可编辑的 PowerPoint（网页端仅 PDF） |
| **单页 Slide 修改** | 用自然语言修改单独的幻灯片 |
| **Report 模板自定义** | 在内置格式模板后追加额外指令 |
| **保存对话为笔记** | 将 Q&A 答案或对话历史保存为笔记本笔记 |
| **来源全文访问** | 检索任何来源的索引文本内容 |
| **程序化权限管理** | 不需要 UI 即可管理分享权限 |

---

## Hermes Agent 安装指南

完整安装步骤（基于 win4r fork 的 `v0.3.4-hermes.4`）：

```bash
# 1. 注册 skill 来源并安装
hermes skills tap add win4r/notebooklm-py
hermes skills install win4r/notebooklm-py/skills/notebooklm --force

# 2. 安装 Python 包到 Hermes venv（审计标签）
VIRTUAL_ENV=~/.hermes/hermes-agent/venv uv pip install \
  "notebooklm-py[browser,cookies] @ git+https://github.com/win4r/notebooklm-py@v0.3.4-hermes.4"
~/.hermes/hermes-agent/venv/bin/playwright install chromium

# 3. 暴露 CLI 到 PATH
mkdir -p ~/.local/bin
ln -sf ~/.hermes/hermes-agent/venv/bin/notebooklm ~/.local/bin/notebooklm

# 4. 通过 Chrome cookies 认证（跳过 Google 新设备流程）
notebooklm login --browser-cookies chrome
# macOS: Keychain 提示选 "Always Allow"

# 5. 配置自动刷新（Google 每 15-30 分钟轮换 PSIDTS）
echo 'NOTEBOOKLM_REFRESH_CMD=notebooklm login --browser-cookies chrome' >> ~/.hermes/.env

# 6. 验证
hermes skills list                    # 应包含 notebooklm
notebooklm auth check --test          # 所有行 ✓
notebooklm list                       # 列出你的笔记本
```

**一次配置，永久运行。** 步骤 5 之后，任何 Hermes session 中的 notebooklm RPC 调用如果遇到过期 cookie，会自动从 Chrome 刷新并重试。

---

## 认证方案

| 方案 | 适用场景 | 复杂度 | 稳定性 |
|------|----------|--------|--------|
| `--browser-cookies` + 自动刷新 | Hermes Agent（推荐） | 低 | 高（自动续期） |
| `notebooklm login`（Playwright） | 交互式使用 | 低 | 中（Google 可能限制新设备） |
| 手动导出 Cookie JSON | 兜底方案 | 高 | 低（15-30 分钟过期） |

### 自动刷新机制

```
RPC 调用 → 过期？ ──否──→ 正常返回
                │
                是
                ▼
    执行 $NOTEBOOKLM_REFRESH_CMD
    (从 Chrome 读取新 cookies)
                │
                ▼
    重载 storage_state.json
                │
                ▼
    重试原始 RPC 调用（一次性，不会循环）
                │
                ▼
    返回结果
```

**安全注意**：`~/.notebooklm/storage_state.json` 包含 Google SID cookie，等同于密码。建议用专用 Google 账号，避免绑定敏感服务。

---

## Python API 速查

```python
import asyncio
from notebooklm import NotebookLMClient

async def main():
    async with await NotebookLMClient.from_storage() as client:
        # 创建笔记本 + 添加来源
        nb = await client.notebooks.create("Research")
        await client.sources.add_url(nb.id, "https://example.com", wait=True)

        # 对话
        result = await client.chat.ask(nb.id, "Summarize this")
        print(result.answer)

        # 生成 Audio Overview
        status = await client.artifacts.generate_audio(
            nb.id, instructions="make it fun"
        )
        await client.artifacts.wait_for_completion(nb.id, status.task_id)
        await client.artifacts.download_audio(nb.id, "podcast.mp3")

        # 生成 Quiz 并导出 JSON
        status = await client.artifacts.generate_quiz(nb.id)
        await client.artifacts.wait_for_completion(nb.id, status.task_id)
        await client.artifacts.download_quiz(nb.id, "quiz.json", output_format="json")

asyncio.run(main())
```

---

## CLI 速查

```bash
# 认证
notebooklm login [--browser-cookies chrome|firefox|brave|edge]

# 笔记本管理
notebooklm create "名称"
notebooklm list
notebooklm use <notebook_id>

# 来源管理
notebooklm source add "https://..."          # URL
notebooklm source add "./paper.pdf"          # 本地文件
notebooklm source add-research "AI"          # Web 研究 + 自动导入

# 对话
notebooklm ask "关键主题是什么？"

# 内容生成
notebooklm generate audio "make it engaging" --wait
notebooklm generate video --style whiteboard --wait
notebooklm generate cinematic-video "documentary summary" --wait
notebooklm generate quiz --difficulty hard
notebooklm generate flashcards --quantity more
notebooklm generate slide-deck
notebooklm generate infographic --orientation portrait
notebooklm generate mind-map
notebooklm generate data-table "compare concepts"

# 下载
notebooklm download audio ./podcast.mp3
notebooklm download video ./overview.mp4
notebooklm download quiz --format json ./quiz.json
notebooklm download mind-map ./mindmap.json
notebooklm download data-table ./data.csv

# 诊断
notebooklm auth check --test
notebooklm metadata --json
```

---

## 架构与风险

### 工作原理

```
notebooklm-py
    │
    ├── 逆向工程 NotebookLM 的内部 API
    │   (undocumented Google endpoints)
    │
    ├── 模拟浏览器请求 (Playwright / cookies)
    │
    └── 提供 Python async API + CLI 封装
```

### 风险评估

| 风险 | 等级 | 说明 |
|------|------|------|
| API 随时可能变更 | 高 | 使用未文档化的 Google 内部 API |
| Google 可能封禁 | 中 | 高频调用可能被限流 |
| Cookie 安全 | 中 | storage_state.json 等同密码 |
| 非 Google 官方 | 信息 | 社区项目，不受 Google 支持 |

**适合**：原型开发、个人研究、自动化管道
**不适合**：生产环境关键业务（无 SLA 保证）

### 使用建议

- 用**专用 Google 账号**，不绑定 Gmail/Drive 等敏感服务
- 定期检查上游更新（`teng-lin/notebooklm-py` main 分支）
- fork 的 `SECURITY_AUDIT.md` 提供了完整的审计记录
- 优先使用 `--browser-cookies` + 自动刷新，避免频繁 Playwright 登录

---

## 参考资料

- [上游仓库 (teng-lin/notebooklm-py)](https://github.com/teng-lin/notebooklm-py)
- [Hermes Fork (win4r/notebooklm-py)](https://github.com/win4r/notebooklm-py)
- [Reddit 讨论](https://www.reddit.com/r/notebooklm/comments/1qbopkm/i_built_a_full_python_client_for_notebooklm/)
- [NotebookLM REST API wrapper (基于此项目)](https://github.com/gnh1201/notebooklm-rest-api)
- [PyPI notebooklm-py](https://pypi.org/project/notebooklm-py/)

## 相关笔记

- [[Gemini 整合 NotebookLM + 多模态文件输出]]
- [[Andrew Wilkinson 六层 AI 架构]]
