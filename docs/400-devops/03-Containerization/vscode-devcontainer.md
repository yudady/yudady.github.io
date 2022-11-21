---
created_date: 2022-11-15 09:38
updated_date: 2022-11-17 12:24
title: vscode-devcontainer
tags: [vscode , devcontainer]
date: 2022-09-29 12:03
modified: 2022-10-04 17:04
aliases: [vscode-devcontainer]
---

# vscode-devcontainer

source-url :: [Developing inside a Container using Visual Studio Code Remote Development](https://code.visualstudio.com/docs/remote/containers)

[devcontainers/images](https://github.com/devcontainers/images) : github devcontainer image

[base alpine image](https://github.com/devcontainers/images/blob/main/src/base-alpine/README.md)

## develope in docker container

- container 中開發環境
- 利用 volumn 掛載程式碼

## rebuild image
Try using `F1` > `Remote-Containers: Rebuild and Reopen in Container` when you have the local folder open. If that doesn't work, try removing the call to `init.sh` (maybe that returns an error). And if that doesn't work please append the full log from a rebuild attempt (that includes the merged config).

---
