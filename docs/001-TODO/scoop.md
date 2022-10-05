---
title: 📚scoop
tags: [2022-10, devops]
aliases: [📚scoop]
created_date: 2022-10-06 00:43
updated_date: 2022-10-06 00:53
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

```powershell 7
PowerShell 7.2.6
Copyright (c) Microsoft Corporation.

https://aka.ms/powershell
Type 'help' to get help.

PS C:\Users\yu_da> Set-ExecutionPolicy RemoteSigned -scope CurrentUser
PS C:\Users\yu_da> Invoke-Expression (New-Object System.Net.WebClient).DownloadString('https://get.scoop.sh')
Initializing...
Running the installer as administrator is disabled by default, see https://github.com/ScoopInstaller/Install#for-admin for details.
Abort.
PS C:\Users\yu_da> irm get.scoop.sh -outfile 'install.ps1'
PS C:\Users\yu_da> .\install.ps1 -RunAsAdmin [-OtherParameters ...]
Initializing...
Downloading...
Test-Path: C:\Users\yu_da\install.ps1:494
Line |
 494 |      if (!(Test-Path $SCOOP_APP_DIR)) {
     |            ~~~~~~~~~~~~~~~~~~~~~~~~
     | Cannot retrieve the dynamic parameters for the cmdlet. The specified wildcard character pattern is not
     | valid: [-OtherParameters

PS C:\Users\yu_da> iex "& {$(irm get.scoop.sh)} -RunAsAdmin"
Initializing...
Downloading...
Extracting...
Creating shim...
Adding ~\scoop\shims to your path.
Scoop was installed successfully!
```

## 📝 怎麼玩

- TODO
- [Quick Start | Scoop](https://scoop-docs.vercel.app/docs/getting-started/Quick-Start.html#using-scoop)
- TODO
