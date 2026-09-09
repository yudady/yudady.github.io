# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

以下内容用中文说明（用户要求说中文）。

## 仓库性质

- MkDocs 个人笔记站（material 主题），GitHub Pages 部署于 https://yudady.github.io/
- `docs/` 同时是 **Obsidian vault 根目录**——笔记用 Obsidian 编辑（frontmatter + wikilink），用 MkDocs 发布，两套语义并存
- 内容几乎全是中文笔记，无测试、无应用代码

## 常用命令

- 本地预览（live reload）：在仓库目录执行 `mkdocs serve`
- 手动部署（一般不用，push 即自动部署）：`mkdocs gh-deploy --force`
- 本地 Python 依赖（与 CI 一致）：`mkdocs-material`、`mkdocs-roamlinks-plugin`、`mkdocs-mermaid2-plugin`、`mkdocs-exclude`

## 部署链路

- GitHub Actions（`.github/workflows/mkdocs-gh-deploy.yml`）：push 到 main 且 `docs/**` 或 `mkdocs.yml` 有改动时，自动 `mkdocs gh-deploy --force` 推到 `gh-pages` 分支
- `mkdocs.yml` **没有 nav**，站点导航按 `docs/` 目录结构自动生成——目录即选单
- `.drone.yml` 是遗留配置（只 echo），无实际作用

## Obsidian / MkDocs 双身份的关键语义

- **wikilink 按档名解析**：roamlinks 插件把 `[[basename]]` 解析到任意路径的同名笔记，因此用 `git mv` 移动笔记**不会断链**；改名才会
- **embed 断链**：`![[档名]]` 引用附件（图片等），移动/改名附件会断
- 断链检查脚本：`.hermes/plans/check-links.py`（pre/post 各跑一次对比 JSON，断链净增必须为 0）
- **hidden-folder/** 被 exclude 插件排除、不进站点，存放支撑文件（MathJax 配置、Excalidraw、hypothes 高亮）
- frontmatter（created_date / updated_date / title / tags / aliases）由 Obsidian 管理；obsidian-linter 会重排 frontmatter，**aliases 含 `#` 时必须加引号**（踩过坑，见 commit `f3678559`）
- 档名/目录名含中文与空格是常态，shell 操作注意加引号

## 笔记组织与归档纪律（本仓库核心工作流）

目录编号分类：

- `001-TODO/` — 收件箱，**新学习笔记的默认落点**；归档后保持空（仅 `.gitkeep`）
- `100-InBox/` — 归档区，**只有一层 7 个主题资料夹**：AI-Agent / AI-Models / AI-Tools / Hermes / Local-LLM / NotebookLM / misc（非 AI 兜底）；不建二层目录、根不放散档
- `200-學習OB/`（Obsidian 本身）、`300-閱讀筆記/`、`400-devops/`（入口 `000-MOC-devops.md`）、`600-developer/`（java/spring/docker/k8s 等主题子目录）、`templates/`（Obsidian 模板）

归档流程（001-TODO → 100-InBox），既有惯例见 `.hermes/plans/` 下的计划文档：

1. 逐档人工判定分类写 manifest，然后 `git mv`（保持 R100 rename 历史，可 revert）
2. 分类判定优先级：Hermes → NotebookLM → Local-LLM → AI-Models（模型发布/评测/榜单）→ AI-Agent（架构/工作流/方法论）→ AI-Tools（具名工具/框架/环境搭建）→ misc
3. `001-TODO/` 的资料夹与 `.gitkeep` 永远保留
4. wikilink / embed 断链净增必须为 0
5. 工作区有 ob-move 技能（`claude-code-plugin/skills/`）可辅助笔记搬移

## Git 惯例

- commit 前缀：新笔记 `note: <标题>`、归档 `docs: <描述>`、修复 `fix: <描述>`
- 直接提交 main（无 PR 流程），归档类操作只 `git add` 涉及的目录、不 push 由用户决定
- origin remote URL 内嵌了 GitHub PAT——不要改动 remote，不要把该 URL 复制进文档或提交内容
