---
created_date: 2022-10-04 13:43
updated_date: 2022-10-20 22:55
title: Github-Action
tags: [github, github-action , workflow, 2022-10]
date: 2022-09-29 12:03
modified: 2022-10-04 16:45
aliases: [Github-Action]
---

# Github-Action

- **🏷️Tags** :   #2022-10 #github
- Link: 
簡介範例 :: [Guides for GitHub Actions - GitHub Docs](https://docs.github.com/en/actions/guides)

source-url :: [GitHub Actions Documentation - GitHub Docs](https://docs.github.com/en/actions)

source-url :: [Workflow syntax](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions)

- 自動化
- CI/CD pipeline 工具

## 名詞

- 路徑 : `.github/workflows`
- workflows : 工作流,並行 workflow
- jobs : 每一個 workflow 可以有多個 job(action)
- GitHub-hosted runners vs self-hosted runners : 使用的虛擬主機 ( runs-on: `ubuntu-latest`)

## 工作流

event -> workflows -> jobs

## 自動構建部署到倉庫 ([github-packages](github-packages.md))

- build maven : [maven > repository](maven#repository)
- build gradle : [gradle > repository](gradle#repository)
- build docker : [../container/docker > repository](../container/docker#repository)
