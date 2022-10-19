---
title: gitpod
tags: [2022-10, tools]
aliases: [gitpod]
created_date: 2022-10-19 10:23
updated_date: 2022-10-19 10:59
---

# gitpod

- **🏷️Tags** :   #2022-10 #tools  
- Link: [Gitpod: Always ready to code.](https://www.gitpod.io/)

## 緣起

- 研究 zipkin 點到這個功能

## 是什麼

- brower 開發工具 (vscode ...)
- Your project’s `.gitpod.yml` and optional `.gitpod.Dockerfile`
- [workspace-images](https://github.com/gitpod-io/workspace-images)  ：選擇開發的 docker image 

### 1. 收費狀況

- 50 hours/month
- Public & private repos
- 4 parallel workspaces
- 30min inactivity timeout

## 去哪下載

- 

## 📝 怎麼玩

```bash
touch .gitpod.yml

	image:
	  file: .gitpod.Dockerfile

touch .gitpod.Dockerfile

	FROM gitpod/workspace-full
	USER gitpod
	RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && \
	    sdk install java 17.0.3-ms && \
	    sdk default java 17.0.3-ms"




```
