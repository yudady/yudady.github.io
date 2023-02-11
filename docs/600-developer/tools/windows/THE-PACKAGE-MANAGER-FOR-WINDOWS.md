---
title: THE PACKAGE MANAGER FOR WINDOWS
categories: ["tools"]
tags: ["windows", "tools"]
date: 2022-08-19 21:12:57
---

# 簡介
> THE PACKAGE MANAGER FOR WINDOWS
* chocolatey ( notepad++ , sdkman , nvm  ...)
* sdkman ( java , gradle , maven ...)
* nvm ( nodejs )


## [chocolatey install](https://chocolatey.org/install)
> open powershell (管理員)

```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```

### [chocolatey command](https://docs.chocolatey.org/en-us/choco/commands/)

```powershell
C:\> choco Search nodejs
```


## [sdkman](https://walterteng.com/using-sdkman-on-windows)
```powershell
C:\> choco install unzip
C:\> choco install zip
C:\> choco install zip
```

### sdkman install
```powershell
curl -s "https://get.sdkman.io" | bash
```
