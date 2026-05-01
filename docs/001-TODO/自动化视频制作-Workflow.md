---
in: null
title: 自动化视频制作 Workflow - 实现计划
aliases: [自动化视频制作 Workflow - 实现计划, "自动化视频制作 Workflow - 实现计划"]
tags: ["自动化", "视频制作", Workflow]
source: null
author: null
created: 2026-03-01 15:53
updated: 2026-03-07 22:06
description: null
level: null
stars: null
category: "技术"
summary: "自动化视频制作流程及其实现计划"
tags:
  - status/active
  - area/distill
  - type/doc
---

# 自动化视频制作 Workflow - 实现计划

## 项目概述

建立一个自动化视频制作流程，包含 7 个步骤：题材抓取、脚本生成、画面生成、配音、字幕、输出、发布。

---

## 核心结论

**答案：部分可以，但不是全部。**

Skills 可以实现**很多** workflow，但存在明确的能力边界。

---

## Skills 能做什么（纯 Skill 可实现）

### 1. 完整的流程编排
Skills 可以定义完整的执行步骤：

```yaml
---
name: deploy-feature
description: 一键部署功能分支到测试环境
disable-model-invocation: true
---

## 部署流程

1. 运行测试：`npm test`
2. 构建镜像：`docker build -t app .`
3. 推送镜像：`docker push registry/app`
4. 更新部署：`kubectl set image deployment/app`
5. 验证状态：`kubectl rollout status deployment/app`
```

### 2. 动态上下文注入
通过 `!`command`` 语法，Skill 可以获取实时数据：

```yaml
## 当前状态
- 分支: !`git branch --show-current`
- 最近提交: !`git log -1 --oneline`
- 状态: !`git status --short`
```

### 3. 执行脚本
Skills 可以捆绑脚本执行复杂操作：

```
.claude/skills/validate/
├── SKILL.md
└── scripts/
    └── check.sh        # 执行验证逻辑
```

### 4. 模板渲染
Skills 可以包含模板文件：

```
.claude/skills/new-component/
├── SKILL.md
└── templates/
    ├── component.tsx.template
    └── test.tsx.template
```

### 5. 隔离执行
通过 `context: fork` 在独立 subagent 中运行：

```yaml
---
name: security-scan
context: fork
agent: Explore
---
```

---

## Skills 不能做什么（需要 MCP）

| 需求 | 为什么 Skills 不行 | 解决方案 |
|------|-------------------|----------|
| **数据库查询** | Skills 没有持久化连接能力 | MCP Database Server |
| **API 调用（需要认证）** | Skills 无法安全存储凭证 | MCP + OAuth |
| **浏览器自动化** | 需要 WebSocket 长连接 | Playwright MCP |
| **实时数据流** | Skills 是一次性执行 | MCP Streaming |
| **GitHub API 操作** | 需要认证的 REST API 调用 | GitHub MCP |

### 关键区别

```
MCP = 手脚（连接外部世界）
Skills = 大脑（知道怎么做）
```

---

## 纯 Skill 可实现的 Workflow 类型

### ✅ 完全可行

| Workflow 类型 | 实现方式 |
|---------------|----------|
| 代码生成模板 | Skills + 模板文件 |
| Git 操作 | `!`git command`` |
| 代码审查 | Skills + Checklist |
| 测试生成 | Skills + 示例文件 |
| 文档生成 | Skills + 模板 |
| 环境检查 | Skills + Bash 脚本 |
| PR 创建 | `gh` CLI 命令 |
| 发布流程 | Bash 命令组合 |

### ❌ 需要 MCP

| Workflow 类型 | 需要的 MCP |
|---------------|------------|
| 查询 Jira/Linear | Issue Tracker MCP |
| 读数据库数据 | Database MCP |
| 操作 AWS 资源 | AWS MCP |
| Slack 通知 | Slack MCP |
| 浏览器测试 | Playwright MCP |
| Figma 设计读取 | Figma MCP |

---

## 混合方案（最佳实践）

对于复杂 workflow，推荐 **Skills + MCP 组合**：

```yaml
---
name: full-deploy
description: 完整部署流程（含 GitHub + Slack 通知）
allowed-tools: Bash, mcp__github, mcp__slack
---

## 部署流程

1. **代码检查**（纯 Bash）
   - `npm run lint`
   - `npm test`

2. **创建 PR**（通过 GitHub MCP）
   - 使用 mcp__github__create_pr

3. **部署**（Bash）
   - `docker build && docker push`

4. **通知**（通过 Slack MCP）
   - 使用 mcp__slack__post_message
```

---

## 对比总结

| 维度 | 纯 Skills | Skills + MCP |
|------|-----------|--------------|
| 外部服务访问 | ❌ 有限（仅 CLI） | ✅ 完整 |
| 开发复杂度 | 低 | 中 |
| 上下文消耗 | 较高 | 较低（按需调用） |
| 可维护性 | 高（单一文件） | 中（多组件） |
| 适用场景 | 内部工具、本地操作 | 全功能自动化 |

---

## 实践建议

### 用纯 Skills 当：
- Workflow 只涉及本地操作（git、npm、docker）
- 已有 CLI 工具可用（`gh`、`kubectl`、`aws`）
- 流程相对固定，不需要外部服务

### 加 MCP 当：
- 需要查询数据库
- 需要调用需要认证的 API
- 需要浏览器自动化
- 需要实时数据流

---

## 验证方法

测试纯 Skill 实现的 workflow：

1. 创建 Skill 文件
2. 运行 `/skill-name`
3. 验证输出是否符合预期
4. 检查是否有无法完成的步骤

---

## 实战案例：自动化视频制作 Workflow

### 流程分析

```
┌─────────────────────────────────────────────────────────────┐
│  1. 題材來源 → 自動抓取特定網站頭條新聞                       │
│  2. 生成腳本 → 餵給 AI，規定架構（開頭/分析/總結）            │
│  3. 生成畫面 → 模板 + AI 圖片/影片素材庫                     │
│  4. 配音     → 選擇固定 AI 聲音，設定語速和情緒              │
│  5. 上字幕   → 設定樣式、出現時間                           │
│  6. 輸出     → 自動輸出成影片檔案                           │
│  7. 發佈     → 排程發佈到各大平台，帶上相關標籤              │
└─────────────────────────────────────────────────────────────┘
```

### 各步骤实现方案

| 步骤 | 纯 Skill | 需要 MCP | 推荐方案 |
|------|:--------:|:--------:|----------|
| **1. 題材來源** | ⚠️ | ✅ | Playwright MCP / Web Scraper MCP |
| **2. 生成腳本** | ✅ | | 纯 Skill + 模板 |
| **3. 生成畫面** | ⚠️ | ✅ | DALL-E/Runway MCP 或 API CLI |
| **4. 配音** | ⚠️ | ✅ | ElevenLabs/OpenAI TTS MCP |
| **5. 上字幕** | ✅ | | Skill + ffmpeg (Bash) |
| **6. 輸出** | ✅ | | Skill + ffmpeg (Bash) |
| **7. 發佈** | ⚠️ | ✅ | YouTube/TikTok MCP |

### 详细实现方案

#### 步骤 1：題材來源（网页抓取）
```
方案 A：Playwright MCP（推荐）
  - 动态渲染 JS 页面
  - 处理登录态
  - 截图验证

方案 B：纯 Skill + curl/jq
  - 适用于静态页面
  - 需要 API endpoint

方案 C：Web Scraper MCP
  - 专门用于数据提取
  - 支持分页、去重
```

#### 步骤 2：生成腳本（纯 Skill 可行）
```yaml
# .claude/skills/video-script/SKILL.md
---
name: video-script
description: 根据新闻素材生成视频脚本
allowed-tools: Read, Write
---

## 輸入
$ARGUMENTS

## 腳本架構

### 開頭（10秒）
- 引人注目的钩子
- 概述本期主题

### 分析（60秒）
- 3-5 个关键点
- 每点配数据/案例

### 總結（10秒）
- 核心结论
- 行动号召

## 输出格式
保存到 scripts/{date}_{topic}.md
```

#### 步骤 3：生成畫面（需要 API）
```bash
# 方案 A：使用 CLI 工具
openai images generate "prompt" --size 1920x1080

# 方案 B：调用 Runway/ElevenLabs API
curl -X POST https://api.runwayml.com/v1/generate \
  -H "Authorization: Bearer $RUNWAY_API_KEY"
```

#### 步骤 4：配音（需要 TTS API）
```bash
# 方案 A：OpenAI TTS CLI
openai audio speech --input script.txt --voice alloy

# 方案 B：ElevenLabs API
curl -X POST https://api.elevenlabs.io/v1/text-to-speech/{voice_id} \
  -H "xi-api-key: $ELEVENLABS_API_KEY"
```

#### 步骤 5-6：字幕 + 輸出（纯 Skill + ffmpeg）
```yaml
# .claude/skills/video-render/SKILL.md
---
name: video-render
description: 合成视频、字幕、配音
allowed-tools: Bash
---

## 输入
- 视频: $VIDEO_PATH
- 音频: $AUDIO_PATH
- 字幕: $SUBTITLE_PATH

## 执行命令

# 合并音视频
ffmpeg -i video.mp4 -i audio.mp3 -c:v copy -c:a aac output.mp4

# 添加字幕
ffmpeg -i output.mp4 -vf "subtitles=subs.srt" final.mp4
```

#### 步骤 7：發佈（需要平台 API）
```
方案 A：YouTube MCP
  - OAuth 认证
  - 上传 + 元数据
  - 排程发布

方案 B：yt-dlp / youtube-upload CLI
  - 需要 API Key 配置
  - 支持批量上传
```

### 推荐架构

```
┌──────────────────────────────────────────────────────┐
│                   主控 Skill                          │
│            /video-pipeline                           │
├──────────────────────────────────────────────────────┤
│                                                      │
│  ┌─────────┐   ┌─────────┐   ┌─────────┐           │
│  │ Skill   │   │ Skill   │   │ Skill   │           │
│  │ fetch   │ → │ script  │ → │ render  │           │
│  └────┬────┘   └─────────┘   └────┬────┘           │
│       │                           │                 │
│       ▼                           ▼                 │
│  ┌─────────┐                 ┌─────────┐           │
│  │ MCP     │                 │ Bash    │           │
│  │Playwright│                │ ffmpeg  │           │
│  └─────────┘                 └─────────┘           │
│                                                      │
│  ┌─────────┐   ┌─────────┐   ┌─────────┐           │
│  │ MCP     │   │ API     │   │ MCP     │           │
│  │ TTS API │   │ Image   │   │YouTube  │           │
│  └─────────┘   └─────────┘   └─────────┘           │
│                                                      │
└──────────────────────────────────────────────────────┘
```

### 实现优先级

| 优先级 | 步骤 | 原因 |
|--------|------|------|
| 🔴 高 | 1. 題材來源 | 自动化的起点 |
| 🔴 高 | 2. 生成腳本 | 核心价值，纯 Skill 可行 |
| 🟡 中 | 5-6. 字幕+輸出 | ffmpeg 成熟稳定 |
| 🟡 中 | 4. 配音 | TTS API 成本低 |
| 🟢 低 | 3. 生成畫面 | 可先用素材库替代 |
| 🟢 低 | 7. 發佈 | 可先手动发布 |

### 最小可行产品（MVP）

用纯 Skill + CLI 实现的简化版：

```yaml
# .claude/skills/video-mvp/SKILL.md
---
name: video-mvp
description: MVP 版视频制作流程
disable-model-invocation: true
allowed-tools: Bash, Read, Write
---

## 步骤 1：抓取新闻（curl + jq）
NEWS=$(curl -s "https://api.example.com/news" | jq -r '.headlines[0]')

## 步骤 2：生成脚本（Claude 处理）
# 脚本内容由 Claude 生成

## 步骤 3：生成配音（OpenAI CLI）
openai audio speech --input script.txt --voice alloy --output audio.mp3

## 步骤 4：合成视频（ffmpeg）
ffmpeg -loop 1 -i image.jpg -i audio.mp3 -c:v libx264 -tune stillimage -c:a aac -shortest output.mp4

## 步骤 5：添加字幕
ffmpeg -i output.mp4 -vf "subtitles=subs.srt" final.mp4
```

---

## 待创建的文件结构

### 目标目录
`video-automation/`

### 文件清单

| 文件 | 内容 |
|------|------|
| `video-automation-guide.md` | 完整实现指南（主文档） |
| `skills/video-pipeline/SKILL.md` | 主控 Skill |
| `skills/video-script/SKILL.md` | 脚本生成 Skill |
| `skills/video-render/SKILL.md` | 视频渲染 Skill |
| `templates/video-script.md` | 脚本模板 |
| `scripts/fetch-news.sh` | 新闻抓取脚本 |
| `scripts/render-video.sh` | 视频合成脚本 |

---

## 实现阶段

### Phase 1: 创建目录结构
```bash
mkdir -p video-automation/skills/video-{pipeline,script,render}
mkdir -p video-automation/templates
mkdir -p video-automation/scripts
```

### Phase 2: 创建主文档
- 完整的架构说明
- 各步骤实现细节
- MCP vs Skill 选择建议
- CLI 工具配置指南

### Phase 3: 创建 Skills
- video-pipeline: 主控流程
- video-script: 脚本生成
- video-render: ffmpeg 合成

### Phase 4: 创建辅助脚本
- fetch-news.sh: curl + jq 抓取
- render-video.sh: ffmpeg 命令封装

### Phase 5: 创建模板
- 脚本模板（开头/分析/总结）
- 字幕样式模板

---

## 所需工具和依赖

### CLI 工具
- `ffmpeg` - 视频处理
- `curl` + `jq` - API 调用
- `openai` CLI - OpenAI API 调用

### MCP 服务器
- `playwright` - 网页抓取
- `elevenlabs` - 配音（可选）
- `youtube` - 发布（可选）

### API Keys
- OpenAI API Key（脚本生成 + 配音）
- Runway API Key（视频生成，可选）
- ElevenLabs API Key（配音，可选）
- YouTube API Key（发布，可选）

---

## 参考资料

- [Claude Skills vs MCP：区别与实战指南](https://juejin.cn/post/7593630465380106278)
- [Claude 官方 Skills 构建完全指南](https://m.toutiao.com/article/7610090773840691746/)
- [Claude Skills 与 MCP 的关系详解](https://m.toutiao.com/article/7596997791998591488/)

## 相关笔记
- [[03-knowledge/技术/项目/Claude-Code-AI营销团队|AI 营销团队]]
- [[004-ai-tools/flowise/EasyVideoTrans - 开源的AI视频翻译工具，快速将英文视频转中文视频  AI工具集|EasyVideoTrans]]
- [[004-ai-tools/n8n/n8n|n8n 工作流]]