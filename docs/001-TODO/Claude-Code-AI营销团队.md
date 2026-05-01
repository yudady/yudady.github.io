---
in: null
title: 用 Claude Code 构建 AI 营销团队完整指南
aliases: [用 Claude Code 构建 AI 营销团队完整指南, "用 Claude Code 构建 AI 营销团队完整指南", "Claude Code 营销工具", Claude Code AI Marketing Team]
tags: ["团队构建", "AI营销", ai-marketing, automation, Claude Code, claude-code, skills, tutorial]
source: [https://docs.anthropic.com/en/docs/claude-code, https://github.com/zubair-trabzada/ai-marketing-claude, https://www.youtube.com/watch?v=eorc3jLBqIA]
author: "整理自 Zubair Trabzada 影片"
created: 2026-03-07 11:02
updated: 2026-03-07 22:06
description: "学习如何使用 Claude Code 在 16 分钟内构建完整的 AI 营销团队工具。包含 15 个营销命令，可进行网站审计、文案分析、竞争对手研究和 PDF 报告生成。"
level: intermediate
stars: 5
category: "技术"
summary: "使用Claude Code构建AI营销团队的完整指南"
tags:
  - status/active
  - area/distill
  - type/doc
---

# 用 Claude Code 构建 AI 营销团队完整指南

> 影片来源：[I Built An Entire AI Marketing Team With Claude Code In 16 Minutes](https://www.youtube.com/watch?v=eorc3jLBqIA)
> GitHub 专案：[ai-marketing-claude](https://github.com/zubair-trabzada/ai-marketing-claude)

---

## 目录

- [一、课程概述](#一课程概述)
- [二、Claude Code 基础入门](#二claude-code-基础入门)
- [三、安装 AI 营销工具](#三安装-ai-营销工具)
- [四、15 个营销命令详解](#四15-个营销命令详解)
- [五、PDF 报告生成](#五pdf-报告生成)
- [六、商业化应用](#六商业化应用)
- [七、AI 营销工具对比](#七ai-营销工具对比)
- [八、进阶开发指南](#八进阶开发指南)
- [九、常见问题与解决方案](#九常见问题与解决方案)

---

## 一、课程概述

### 1.1 这是什么？

这是一个**免费开源**的 AI 营销工具，使用 Claude Code 构建，可以：

| 功能 | 传统方式 | Claude Code 方式 |
|------|----------|-----------------|
| 网站审计 | 营销代理 $5,000-10,000/月 | 免费自动化 |
| 文案分析 | 人工分析师 | AI 即时分析 |
| 竞争对手研究 | 需要数周 | 几分钟完成 |
| PDF 报告生成 | 设计师制作 | 自动生成专业报告 |
| 邮件序列 | 文案撰写师 | AI 自动生成 |
| 广告策略 | 广告代理 | AI 策略建议 |

### 1.2 目标受众

- 想要启动 **AI 自动化代理业务**的人
- 需要进行**营销审计**的本地商家
- 想要**节省营销成本**的中小企业
- 想要学习 **Claude Code 实战应用**的开发者

### 1.3 影片时间轴

| 时间 | 内容 |
|------|------|
| 0:00 | 介绍与构建概述 |
| 0:57 | 完整营销审计演示与 PDF 报告 |
| 2:12 | 在 VS Code 中安装 Claude Code |
| 3:25 | 免费安装 AI 营销工具 |
| 4:43 | 为本地商家运行营销审计 |
| 6:17 | Claude Code AI 营销 Skills 演示 |
| 11:21 | 使用 Claude Code 生成 PDF 报告 |
| 12:20 | 完整营销审计 PDF 报告演示 |

---

## 二、Claude Code 基础入门

### 2.1 什么是 Claude Code

Claude Code 是 Anthropic 官方推出的 **AI 代码助手命令行工具**，它是一个 agentic coding tool，直接在你的终端中工作。

```
★ Insight ─────────────────────────────────────
• Claude Code 不同于 ChatGPT 的聊天界面，它可以
  直接编辑文件、运行命令、创建 Git 提交
• 支持 Skills 系统，可以将专业知识打包成可复用模块
• 通过 MCP 可以连接外部服务如 Jira、Google Drive 等
─────────────────────────────────────────────────
```

### 2.2 核心功能

| 功能类型 | 描述 |
|---------|------|
| **构建功能** | 用自然语言描述，Claude 制定计划、编写代码 |
| **调试修复** | 描述 bug 或粘贴错误信息，自动分析并修复 |
| **浏览代码库** | 理解整个项目结构，回答关于代码的问题 |
| **自动化任务** | 修复 lint 问题、解决合并冲突、编写发布说明 |

### 2.3 安装方式

#### 方式一：VS Code 扩展（推荐）

1. 打开 VS Code
2. 搜索 "Claude Code" 扩展
3. 点击安装
4. 使用 `/login` 命令登录

#### 方式二：命令行安装

```bash
# macOS / Linux / WSL
curl -fsSL https://claude.ai/install.sh | bash

# 或使用 NPM (需要 Node.js 18+)
npm install -g @anthropic-ai/claude-code
```

### 2.4 基本使用

```bash
# 启动 Claude Code
claude

# 带初始提示启动
claude "explain this project"

# 一次性查询
claude -p "what does this function do?"

# 继续最近的对话
claude -c
```

---

## 三、安装 AI 营销工具

### 3.1 一键安装

```bash
# 克隆专案
git clone https://github.com/zubair-trabzada/ai-marketing-claude.git

# 进入目录
cd ai-marketing-claude

# 将 Skills 复制到 Claude Code 配置目录
cp -r skills/* ~/.claude/skills/
```

### 3.2 验证安装

```bash
# 启动 Claude Code
claude

# 询问可用的 Skills
> What Skills are available?

# 测试营销审计
> Run a marketing audit for example.com
```

---

## 四、15 个营销命令详解

### 4.1 命令总览

| # | 命令 | 功能描述 |
|---|------|----------|
| 1 | `/marketing-audit` | 完整营销审计 |
| 2 | `/seo-analysis` | SEO 分析 |
| 3 | `/competitor-analysis` | 竞争对手研究 |
| 4 | `/copy-analysis` | 文案分析 |
| 5 | `/content-strategy` | 内容策略 |
| 6 | `/email-sequence` | 邮件序列生成 |
| 7 | `/ad-strategy` | 广告策略 |
| 8 | `/social-media` | 社交媒体内容 |
| 9 | `/landing-page-audit` | 落地页审计 |
| 10 | `/conversion-analysis` | 转化率分析 |
| 11 | `/brand-analysis` | 品牌分析 |
| 12 | `/customer-journey` | 客户旅程分析 |
| 13 | `/swot-analysis` | SWOT 分析 |
| 14 | `/market-research` | 市场研究 |
| 15 | `/generate-report` | 生成 PDF 报告 |

### 4.2 核心命令详解

#### `/marketing-audit` - 完整营销审计

```bash
> /marketing-audit https://example.com
```

**审计内容包括：**

| 类别 | 检查项目 |
|------|----------|
| **SEO** | Meta 标签、标题结构、图片 alt、URL 结构 |
| **性能** | 页面加载时间、资源优化、缓存设置 |
| **可访问性** | 颜色对比度、键盘导航、ARIA 标签 |
| **安全** | HTTPS、安全标头、Cookie 设置 |

#### `/competitor-analysis` - 竞争对手分析

```bash
> /competitor-analysis yoursite.com competitor1.com competitor2.com
```

**分析维度：**
- 关键词排名对比
- 流量估算对比
- 内容策略差异
- 社交媒体表现

#### `/copy-analysis` - 文案分析

```bash
> /copy-analysis https://example.com
```

**分析内容：**
- 标题效果评估
- 行动号召 (CTA) 优化建议
- 情感触发词使用
- 可读性评分

#### `/email-sequence` - 邮件序列生成

```bash
> /email-sequence --purpose "new-product-launch" --audience "small-business-owners"
```

**生成内容：**
- 5-7 封邮件序列
- 主题行建议
- 最佳发送时间
- A/B 测试建议

### 4.3 使用流程图

```
┌─────────────────────────────────────────────────────────────┐
│                    营销审计工作流程                           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
              ┌───────────────────────────────┐
              │  1. 运行 /marketing-audit     │
              │     获取网站基本信息           │
              └───────────────────────────────┘
                              │
                              ▼
              ┌───────────────────────────────┐
              │  2. 运行 /seo-analysis        │
              │     深入 SEO 问题             │
              └───────────────────────────────┘
                              │
                              ▼
              ┌───────────────────────────────┐
              │  3. 运行 /competitor-analysis │
              │     竞争对手对比              │
              └───────────────────────────────┘
                              │
                              ▼
              ┌───────────────────────────────┐
              │  4. 运行 /copy-analysis       │
              │     文案优化建议              │
              └───────────────────────────────┘
                              │
                              ▼
              ┌───────────────────────────────┐
              │  5. 运行 /generate-report     │
              │     生成 PDF 报告             │
              └───────────────────────────────┘
                              │
                              ▼
              ┌───────────────────────────────┐
              │  ✅ 专业营销审计报告完成       │
              └───────────────────────────────┘
```

---

## 五、PDF 报告生成

### 5.1 报告生成流程

```bash
# 方式一：直接生成
> Generate a PDF report for the marketing audit

# 方式二：使用命令
> /generate-report --format pdf --template professional
```

### 5.2 报告内容结构

| 章节 | 内容 |
|------|------|
| **执行摘要** | 关键发现和建议概述 |
| **SEO 分析** | 技术 SEO、内容 SEO、站外 SEO |
| **性能评估** | 页面速度、Core Web Vitals |
| **竞争对手分析** | 市场定位、差距分析 |
| **优化建议** | 优先级排序的行动清单 |
| **附录** | 详细数据和技术细节 |

### 5.3 技术实现原理

```
★ Insight ─────────────────────────────────────
• PDF 生成使用 Python 的 ReportLab 或 WeasyPrint
• Claude Code 可以直接调用 Python 脚本
• 报告模板使用 Markdown + CSS 样式
─────────────────────────────────────────────────
```

**Python PDF 生成脚本示例：**

```python
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.platypus import SimpleDocTemplate, Table, TableStyle, Paragraph

def generate_marketing_report(data, output_path):
    """生成营销审计 PDF 报告"""
    doc = SimpleDocTemplate(output_path, pagesize=A4)
    elements = []

    # 添加标题
    elements.append(Paragraph("营销审计报告", styles['Heading1']))

    # 添加 SEO 评分表格
    seo_data = [
        ['类别', '评分', '状态'],
        ['技术 SEO', data['seo']['technical'], '良好'],
        ['内容 SEO', data['seo']['content'], '需改进'],
        ['站外 SEO', data['seo']['offpage'], '待优化'],
    ]

    table = Table(seo_data)
    table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.grey),
        ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
        ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
        ('GRID', (0, 0), (-1, -1), 1, colors.black)
    ]))
    elements.append(table)

    doc.build(elements)
    return output_path
```

---

## 六、商业化应用

### 6.1 AI 自动化代理业务模式

| 服务项目 | 传统收费 | AI 自动化收费 | 利润空间 |
|----------|----------|--------------|----------|
| 网站审计报告 | $500-2,000 | $99-299 | 80%+ |
| 月度营销审计 | $1,000-3,000 | $199-499 | 90%+ |
| 竞争对手分析 | $300-1,000 | $49-149 | 85%+ |
| SEO 优化建议 | $500-2,000 | $99-199 | 80%+ |

### 6.2 目标客户

| 客户类型 | 痛点 | 解决方案 |
|----------|------|----------|
| **本地商家** | 没有营销团队 | 自动化审计 + 洞察 |
| **中小企业** | 预算有限 | 低成本高质量报告 |
| **营销代理** | 人力成本高 | AI 辅助规模化 |
| **电商卖家** | 转化率低 | 落地页优化建议 |

### 6.3 服务流程

```
┌─────────────────────────────────────────────────────────────┐
│                    AI 营销代理服务流程                        │
└─────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
   ┌─────────┐          ┌─────────┐          ┌─────────┐
   │ 发现客户 │          │ 交付价值 │          │ 增值服务 │
   └─────────┘          └─────────┘          └─────────┘
        │                     │                     │
        ▼                     ▼                     ▼
   • 社交媒体             • 运行审计            • 月度订阅
   • 冷邮件               • 生成报告            • 实施建议
   • 网络活动             • 发送给客户          • 持续优化
```

---

## 七、AI 营销工具对比

### 7.1 传统营销代理 vs AI 自动化

| 对比维度 | 传统营销代理 | AI 自动化工具 |
|---------|-------------|--------------|
| **成本** | $3,000-50,000/月 | $50-500/月 |
| **响应速度** | 天/周 | 秒级 |
| **个性化** | 高（但成本高） | 大规模自动化 |
| **可扩展性** | 需增加人力 | 几乎无限扩展 |
| **透明度** | 依赖代理报告 | 数据实时可见 |

### 7.2 Claude Code vs 其他 AI 工具

| 工具 | 定位 | 营销模板 | PDF 报告 | 自动化 | 价格 |
|------|------|----------|----------|--------|------|
| **Claude Code** | 开发者 CLI | 可自定义 | ✅ | ✅ | API 费用 |
| ChatGPT | 通用对话 | ❌ | ❌ | ❌ | $20/月 |
| Jasper AI | 营销文案 | 50+ | ❌ | ⚠️ | $49-125/月 |
| Copy.ai | 内容创作 | 90+ | ❌ | ⚠️ | $49-249/月 |
| HubSpot | 营销平台 | ✅ | ✅ | ✅ | $15-3600/月 |

### 7.3 决策矩阵

| 场景 | 推荐方案 | 原因 |
|------|---------|------|
| **初创企业** | AI 自动化 | 成本低，快速见效 |
| **大型企业** | 混合模式 | AI 标准化 + 代理战略 |
| **高度定制** | 传统代理 | 需要人类创意 |
| **数据驱动** | AI 自动化 | 实时优化，透明数据 |

---

## 八、进阶开发指南

### 8.1 创建自定义 Skill

**步骤 1：创建目录**

```bash
mkdir -p ~/.claude/skills/my-marketing-skill
```

**步骤 2：创建 SKILL.md**

```yaml
---
name: my-marketing-skill
description: This skill should be used when the user asks to "analyze marketing", "create marketing plan", or "review marketing strategy".
version: 1.0.0
---

# My Marketing Skill

Provide comprehensive marketing analysis and recommendations.

## Capabilities
- Marketing audit
- Competitor analysis
- ROI calculation
- Report generation

## Process
1. Gather marketing data
2. Analyze key metrics
3. Identify opportunities
4. Generate actionable recommendations

## Scripts
- `scripts/analyze.py` - Main analysis script
- `scripts/report.py` - Report generation script
```

**步骤 3：添加支持脚本**

```python
# scripts/analyze.py
import json
import sys

def analyze_marketing(data):
    """分析营销数据"""
    results = {
        'score': calculate_score(data),
        'strengths': identify_strengths(data),
        'weaknesses': identify_weaknesses(data),
        'recommendations': generate_recommendations(data)
    }
    return results

if __name__ == "__main__":
    data = json.loads(sys.argv[1])
    results = analyze_marketing(data)
    print(json.dumps(results, indent=2))
```

### 8.2 Skill 最佳实践

```
★ Insight ─────────────────────────────────────
• 描述要具体，包含触发词：如 "analyze marketing",
  "create marketing plan"
• 保持 Skill 专注：一个 Skill 解决一个能力
• 使用渐进式披露：主文件简洁，详细内容放引用文件
─────────────────────────────────────────────────
```

**好的描述示例：**

```yaml
description: Analyze marketing campaign performance, calculate ROI, and generate optimization recommendations. Use when analyzing marketing data, reviewing campaign metrics, or calculating marketing ROI.
```

**不好的描述示例：**

```yaml
description: Helps with marketing  # 太模糊
```

### 8.3 集成 MCP 服务器

```json
// .mcp.json
{
  "mcpServers": {
    "google-analytics": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-ganalytics"],
      "env": {
        "GA_PROPERTY_ID": "${GA_PROPERTY_ID}"
      }
    },
    "slack": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-slack"],
      "env": {
        "SLACK_BOT_TOKEN": "${SLACK_BOT_TOKEN}"
      }
    }
  }
}
```

---

## 九、常见问题与解决方案

### 9.1 Skill 不触发

**问题**：Claude 不使用我的 Skill

**解决方案**：

1. **检查描述是否具体**

```yaml
# 不好的做法
description: Helps with data

# 好的做法
description: Analyze Excel spreadsheets, generate pivot tables, create charts. Use when working with Excel files, spreadsheets, or .xlsx files.
```

2. **检查 YAML 格式**

```bash
# 查看文件前 15 行
cat ~/.claude/skills/my-skill/SKILL.md | head -n 15

# 常见问题：
# - 缺少开头或结尾的 ---
# - 使用制表符而不是空格
# - 特殊字符未加引号
```

3. **检查文件位置**

```bash
# 个人 Skills
ls ~/.claude/skills/*/SKILL.md

# 项目 Skills
ls .claude/skills/*/SKILL.md
```

### 9.2 PDF 生成失败

**问题**：无法生成 PDF 报告

**解决方案**：

```bash
# 检查依赖是否安装
pip install reportlab weasyprint pdfkit

# 检查 pandoc 是否安装（Markdown 转 PDF）
brew install pandoc  # macOS
# 或
apt-get install pandoc  # Linux
```

### 9.3 网站访问问题

**问题**：无法访问目标网站

**解决方案**：

```bash
# 检查网络连接
curl -I https://example.com

# 使用 Chrome DevTools MCP 进行更可靠的分析
# 在 .mcp.json 中配置：
{
  "mcpServers": {
    "chrome-devtools": {
      "command": "npx",
      "args": ["-y", "@anthropic-ai/mcp-server-chrome"]
    }
  }
}
```

---

## 参考资料

### 官方资源

- [Claude Code 官方文档](https://docs.anthropic.com/en/docs/claude-code)
- [Claude Code GitHub](https://github.com/anthropics/claude-code)
- [Agent Skills 文档](https://docs.anthropic.com/en/docs/claude-code/skills)

### 专案资源

- [AI Marketing Claude GitHub](https://github.com/zubair-trabzada/ai-marketing-claude)
- [AI Workshop 社群](https://skool.com/aiworkshop)
- [免费资源汇总](https://bit.ly/aiw-lite-about)

### 相关学习

- [Claude Code 完整指南](./Claude-Code-完整指南.md)
- [Claude Code 实践范例完整指南](./Claude-Code-实践范例完整指南.md)
- [MCP 协议文档](https://modelcontextprotocol.io)

---

*此笔记基于 YouTube 影片和相关资源整理，持续更新中。*

## 相关笔记
- [[004-ai-tools/claude/claude|Claude Code]]
- [[004-ai-tools/ai-prompt/vibe-coding|Vibe Coding]]
- [[study-notes/AI编程GSD工作流完全指南|GSD 工作流]]
- [[03-knowledge/工作/项目管理/AI-策略分工布局|AI 策略分工]]