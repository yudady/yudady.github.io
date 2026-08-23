---
title: NotebookLM + Gemini CLI 批量生成简报提示词工作流
aliases: [NotebookLM 简报模板, NotebookLM 提示词工厂]
tags:
  - notebooklm
  - gemini-cli
  - prompting
  - slide-deck
  - status/active
  - type/note
source:
  - "https://www.youtube.com/watch?v=5smuPeIw10A"
author: 欸那個AJ
created: 2026-05-06
updated: 2026-05-06
description: 用 Gemini CLI 读取 Google 官方 Prompting Guide 101，自动批量生成 NotebookLM 简报风格提示词
level: beginner
stars: 3
---

# NotebookLM + Gemini CLI 批量生成简报提示词工作流

> 用 Gemini CLI 读 PDF → 基于官方分类框架 → 自动生成 27 套（x2 格式 = 54 个）NotebookLM 简报提示词，5 分钟搞定。

---

## 核心思路

NotebookLM 简报功能（Slide Deck）支持通过 Custom Instructions（自定义指令）控制输出风格，但多数用户卡在「不知道写什么提示词」。

本视频的解法：**让 AI 自己写提示词**。

```
Google Prompting Guide 101 (PDF)
         │
         ▼
    Gemini CLI (本地读取)
         │
         ▼
  9 大分类 × 3 种风格 × 2 种格式
  = 54 个简报提示词
         │
         ▼
  复制到 NotebookLM → 生成简报
```

---

## 前置准备

### 工具清单

| 工具 | 用途 | 安装 |
|------|------|------|
| Gemini CLI | 本地 AI 助手，可读文件 | `npm install -g @anthropic-ai/gemini-cli` 或参考 [前置教学](https://www.youtube.com/watch?v=cXEZ50vgDPw) |
| Google Prompting Guide 101 | 官方提示词分类框架 PDF | [直接下载](https://services.google.com/fh/files/misc/workspace_with_gemini_prompting_guide.pdf) |
| NotebookLM | 生成简报 | [notebooklm.google.com](https://notebooklm.google.com) |

### 关键资源

- **Google 官方 PDF**：`Gemini for Google Workspace: Prompting Guide 101`
  - 71 页，100+ 提示词案例
  - 按 9 大分类组织（见下文）
  - 下载：`https://services.google.com/fh/files/misc/workspace_with_gemini_prompting_guide.pdf`
- **中文翻译版**：宝玉的翻译 [baoyu.io](https://baoyu.io/translations/gemini-google-workspace-prompt-guide)

---

## 操作步骤

### Step 1：准备文件

```bash
mkdir -p ~/gemini-cli
# 将下载的 PDF 放入该目录
cp ~/Downloads/workspace_with_gemini_prompting_guide.pdf ~/gemini-cli/
cd ~/gemini-cli
```

### Step 2：用 Gemini CLI 生成提示词

在 Gemini CLI 中输入以下提示：

```
請閱讀 PDF 的提示分類架構，協助我創作 NotebookLM 的簡報生成風格提示。
參考提示的範例如下：[附上你想要的风格示例]

針對每個分類架構，生成不同的風格創意提示時：
1. 生成 YAML 格式
2. 生成自然語言（Natural Language）格式
```

**关键点**：
- Gemini CLI 会自动读取当前目录下的 PDF
- 先给一个**风格示例**（用 NotebookLM 网页版随便生成一个简报的截图/描述），作为参考基准
- 可以追加个性化需求：「我是一个老师 / 公务员 / 产品经理，配色偏好 xxx」

### Step 3：拿到结果

Gemini CLI 会按分类输出 `.md` 文件，每个文件包含 YAML + 自然语言两种格式的提示词。

---

## Google Prompting Guide 101 — 九大分类

视频中引用的 9 大分类（来自 Google 官方 PDF）：

| # | 分类 | 说明 |
|---|------|------|
| 1 | Human Resources（人力资源） | 招聘、培训、员工发展 |
| 2 | Sales（销售） | 客户提案、方案展示 |
| 3 | Marketing（营销） | 社群媒体、品牌传播 |
| 4 | Strategy（策略） | 战略规划、商业蓝图 |
| 5 | Customer Service（客服） | 服务流程、沟通规范 |
| 6 | Finance（财务） | 数据报告、财务分析 |
| 7 | Operations（运营） | 流程优化、项目管理 |
| 8 | Legal（法务） | 合规审查、政策解读 |
| 9 | Creative（创意） | 广告设计、内容创作 |

每个分类生成 3 套风格 × 2 种格式 = 6 个提示词，总计 54 个。

---

## 视频展示的简报风格示例

| 风格名称 | 特色 | 适用场景 |
|----------|------|----------|
| 敏捷开发冲刺看板 | 工业聚合体视觉 | 科技团队、项目管理 |
| 高能量销售提案 | 大脑/工业复合体 | 销售、商业展示 |
| 社群媒体视觉动态感 | IG 风格雾面背景 | 社交媒体、年轻化 |
| 策略规划蓝图 | 模块化信息架构 | 战略规划、咨询 |
| 政府行政风格 | 官方正式版式 | 政府、公部门 |
| 高端杂志色论 | 排版精美 | 出版、品牌故事 |
| 深度专题资料故事化 | 类 NYTimes 长文 | 新闻、研究 |
| 大胆活力广告创意 | 视觉冲击 | 广告、营销 |
| 战情指挥中心风格 | 仪表板/数据驱动 | 运营、监控 |
| 现代化办公场风格 | 简洁商务 | 通用商务 |

---

## NotebookLM 简报提示词写法要点

基于 Google 官方博客的最佳实践：

```
[内容主题] + [视觉风格描述] + [配色偏好] + [排版要求]
```

**有效 vs 无效对比**：

```
# 无效
帮我做一份关于 AI 的简报

# 有效
AI 技术趋势简报，采用深绿色黑板设计，
白色/黄色/粉色手写粉笔字体风格，
每页一张核心图表 + 简短总结
```

**YAML 格式提示词示例**（视频中展示的结构化写法）：

```yaml
style: magazine_editorial
colors:
  primary: "#1a1a2e"
  accent: "#e94560"
  background: "#f5f5f0"
layout: full_bleed_images
typography:
  heading: serif_bold
  body: sans_serif_light
mood: premium_editorial
```

---

## 进阶用法

### 个性化定制

在 Gemini CLI 提示中追加角色设定：

```
我是一名产品经理，需要向高层汇报季度成果。
偏好配色：深蓝 + 金色。
目标受众：CEO 和董事会。
```

### 结合 NotebookLM Brandbook

Google 官方支持上传品牌手册（Brandbook）作为来源，NotebookLM 会自动从中提取配色、字体、Logo 等品牌元素：

1. 上传品牌 PDF 到 NotebookLM
2. 在提示词中引用：`Use the brandbook for branding and styling references`
3. 生成的简报自动应用品牌规范

### NotebookLM 简报能力边界

| 支持长度 | 用户类型 |
|----------|----------|
| 标准版（基础长度限制） | 免费用户 |
| 2x 长度 + 更高限制 | AI Ultra 订阅用户 |

---

## 判断决策树：选择哪种简报提示词

```
你的场景是什么？
├── 内部汇报 → 政府行政 / 现代化办公场风格
├── 商业提案 → 高能量销售 / 策略规划蓝图
├── 营销推广 → 社群媒体 / 大胆活力广告
├── 教育/研究 → 深度专题故事化
└── 数据展示 → 战情指挥中心风格
```

---

## 参考资料

- [Gemini for Google Workspace Prompting Guide 101 (PDF)](https://services.google.com/fh/files/misc/workspace_with_gemini_prompting_guide.pdf)
- [Gemini for Google Workspace Prompting Guide 101 (October 2024)](https://services.google.com/fh/files/misc/gemini_for_workspace_prompt_guide_october_2024_digital_final.pdf)
- [Google 官方博客：8 ways to make the most out of Slide Decks in NotebookLM](https://blog.google/innovation-and-ai/models-and-research/google-labs/8-ways-to-make-the-most-out-of-slide-decks-in-notebooklm/)
- [Gemini CLI 安装教学（视频前置）](https://www.youtube.com/watch?v=cXEZ50vgDPw)
- [宝玉翻译：Gemini for Google Workspace 提示词指南](https://baoyu.io/translations/gemini-google-workspace-prompt-guide)

## 相关笔记
- [[NotebookLM]]
