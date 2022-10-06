---
title: 📚scoop
tags: [2022-10, devops]
aliases: [📚scoop]
created_date: 2022-10-06 00:43
updated_date: 2022-10-06 10:59
---

# 📚scoop

- **🏷️Tags** :  #2022-10 #devops 
- Link: [ScoopInstaller/Install: 📥 Next-generation Scoop (un)installer](https://github.com/ScoopInstaller/Install#for-admin)

## 緣起

sdk install jdk fail , git bash permission denied

## 是什麼

> [!info] Windows 安裝 Scoop 來進行包管理
> Scoop 可以簡單理解為一個在 Windows 中可以使用的包管理工具，這個包管理工具需要在 PowerShell 中執行。

## 去哪下載

- [Quick Start | Scoop](https://scoop-docs.vercel.app/docs/getting-started/Quick-Start.html#requirements)

```git-bash
choco install powershell-core
choco install pwsh
```

> [!info] use powershell 7 run
> Invoke-Expression (New-Object System.Net.WebClient).DownloadString('https://get.scoop.sh')
> 

```pwsh
PowerShell 7.2.6
Copyright (c) Microsoft Corporation.

https://aka.ms/powershell
Type 'help' to get help.

PS C:\Users\tommy.lin> Invoke-Expression (New-Object System.Net.WebClient).DownloadString('https://get.scoop.sh')
Initializing...
Downloading...
Extracting...
Creating shim...
Adding ~\scoop\shims to your path.
Scoop was installed successfully!
```

## 📝 怎麼玩

- [Scoop](https://scoop.sh/) : A command-line installer for Windows
	- 安裝程式不會加入 path 路徑
	- Scoop 默認將程序安裝到您的主目錄。因此，您不需要管理員權限即可安裝程序，並且每次需要添加或刪除程序時，您都不會看到 UAC 彈出窗口。
	- buckets
- [Quick Start | Scoop](https://scoop-docs.vercel.app/docs/getting-started/Quick-Start.html#using-scoop) : 如何開始
- [ScoopInstaller/Extras: 📦 The Extras bucket for Scoop.](https://github.com/ScoopInstaller/Extras) : github
- [Scoop doc](https://scoop-docs.vercel.app/) : 文檔
