---
in:
aliases: [Claude Code 使用指南]
source:
level:
stars:
title: Claude Code 使用指南
type: "doc"
project: "general"
status: "active"
created: 2026-01-25 17:02
updated: 2026-03-03 10:00
tags: ["代碼審查", "ai", "claude", "skill"]
author: "Tommy"
description: "Claude Code 技能管理、代碼審查和開發工作流指南"
---

# Claude Code 使用指南

## 思考模式

Claude Code 提供四種思考強度等級：

| 模式 | 說明 |
|------|------|
| `think` | 基礎擴展思考 |
| `think hard` | 增加思考預算 |
| `think harder` | 更多計算能力 |
| `ultrathink` | 最大思考分配 |

---

## 安裝與更新

```bash
brew update
brew upgrade --cask claude-code
```

---

## Ralph Loop 自動化

Ralph Loop 用於重複執行任務直到完成。

### 基本語法

```bash
/ralph-wiggum:ralph-loop PROMPT [--max-iterations N] [--completion-promise TEXT]
```

### 範例

```bash
# 一次性執行
/ralph-wiggum:ralph-loop "@plan.md 修復並測試覆蓋 100%" --completion-promise "DONE"

# 多任務執行
/ralph-loop:ralph-loop "@task2.md @task3.md @task4.md 執行任務，需要編譯測試通過" --completion-promise "DONE"

# 產生任務計劃
/ralph-wiggum:ralph-loop "根據代碼審查報告，ultrathink 產生一份 task.md" --max-iterations 20 --completion-promise "DONE"

# 開始修復
/ralph-wiggum:ralph-loop "開始修復，編譯成功測試100%" --max-iterations 20 --completion-promise "DONE"
```

### 工作流程

1. Claude Code 執行任務
2. 嘗試退出
3. Stop hook 阻止退出
4. Stop hook 將相同提示詞送回
5. 重複直到完成

---

## 代碼審查

### 標準審查命令

```bash
# 完整智能審查（推薦）
/review
```

### 審查範例

```bash
# 對分支進行代碼審查並生成報告
/code-review:code-review git branch origin/EZPAY-697-698 的變更進行代碼審查，並生成一份報告(report.md)
```

### 審查重點

- 專注於已修改的文件（尚未 commit）
- 如果找不到修改，則查找最後一個 commit
- 檢查編譯成功與測試覆蓋率

---

## 技能系統

### 查看可用技能

```bash
# 目前有哪些 SKILL
⏺ 我來查看一下當前可用的技能列表。
```

### 內建技能分類

**用戶技能 (user)**：
- `algorithmic-art` — 算法藝術創作（p5.js）
- `brand-guidelines` — Anthropic 品牌規範
- `canvas-design` — 視覺藝術設計
- `frontend-design` — 前端界面設計
- `skill-creator` — 技能創建指南
- `webapp-testing` — Web 應用測試

**插件技能 (plugin)**：
- `example-skills` — 系列技能（插件版本）
- `hookify:writing-rules` — Hookify 規則編寫

---

## 圖片理解

Claude 對圖片與圖表的理解能力很強。

### 提供圖片的方式

| 方式 | 說明 |
|------|------|
| 截圖貼上 | macOS 使用 `cmd+ctrl+shift+4` 擷取螢幕並 `ctrl+v` 貼上 |
| 拖拉 | 直接拖拉圖片至輸入框 |
| 檔案路徑 | 提供圖片檔案路徑 |

### 應用場景

- UI mockup 參考
- 資料圖表分析與除錯
- 建議明確說明「輸出需有視覺吸引力」

---

## 項目版本匹配

當前項目匹配結果：

```json
{
  "reason": "檢測到 Spring Boot (dependencyManagement) + Dubbo 配置",
  "versions": {
    "spring_boot": "2.7.18",
    "dubbo": "3.2.14",
    "java": "11"
  }
}
```

---

## 常用操作

### Git 操作

```bash
# 強制重置並推送
git reset --hard 8c6f28cf7
git force push stage-724
```

### MySQL 同步

針對資料表同步需求：

- 清除目標資料表資料
- 從來源資料庫完整複製資料
- 不開啟事務，失敗繼續處理

---

## 相關資源

- [Skill Seekers GitHub](https://github.com/yusufkaraaslan/Skill_Seekers) — 將文檔網站、GitHub 倉庫和 PDF 轉換成 Claude AI 技能
- [next-ai-draw-io](https://github.com/DayuanJiang/next-ai-draw-io) — AI 繪圖工具
- [antigravity-awesome-skills](https://github.com/sickn33/antigravity-awesome-skills) — Antigravity 技能集合

---

## 相关笔记

- [[004-ai-tools/MCP servers|MCP Servers]] - Claude Code 的 MCP 服务器配置
- [[004-ai-tools/CLI|CLI 工具]] - 命令行 AI 编程工具比较
- [[004-ai-tools/Cline|Cline]] - VS Code AI 编程助手
- [[004-ai-tools/claude/slash commands for Claude Code|Slash Commands]] - Claude Code 斜杠命令
- [[004-ai-tools/claude/planning-with-files|Planning with Files]] - Claude Code 文件规划法
- [[004-ai-tools/claude/Skill_Seekers|Skill Seekers]] - 文档转 Claude 技能工具
- [[study-notes/AI编程GSD工作流完全指南|GSD 工作流]] - AI 编程方法论
- [[study-notes/Superpowers-AI-Coding-Workflow|Superpowers 工作流]] - AI Coding 最佳实践
- [[03-knowledge/技术/编程/Claude-Code-完整指南-v2|Claude Code 完整指南]]
- [[03-knowledge/技术/编程/Claude-Code-MCP完整指南|Claude Code MCP 指南]]
- [[03-knowledge/技术/编程/Claude-Code-实践指南|Claude Code 实践指南]]
- [[03-knowledge/技术/编程/Claude-Code-新功能指南|Claude Code 新功能指南]]
- [[03-knowledge/技术/编程/Claude-Code-Skills-2.0|Claude Code Skills 2.0]]
- [[03-knowledge/技术/项目/Claude-Code-持久化记忆系统|持久化记忆系统]]
- [[004-ai-tools/claude/opencode-oh-my-opencode|Oh My OpenCode]] - OpenCode 增强配置
- [[MOC-AI编程工具|AI 编程工具 MOC]] - AI 工具导航
- [[MOC-Obsidian工作流|Obsidian 工作流 MOC]]
- [[03-knowledge/技术/工具/Claude-Obsidian-自动化工作流|Claude + Obsidian 自动化]]

---

*最後更新: 2026-03-27*
