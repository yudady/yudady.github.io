---
created_date: 2022-10-27 10:39
updated_date: 2022-10-28 11:20
title: wsl
Topic: wsl
tags: ["windows", "wsl", 2022-10, todo]
aliases: [wsl]
---

# wsl

- **🏷️Tags** :   #2022-10 #todo 
- Link: [適用於 Linux 的 Windows 子系統文件 | Microsoft Learn](https://learn.microsoft.com/zh-tw/windows/wsl/)

## install-WSL2

[WSL 的手動安裝步驟](https://learn.microsoft.com/zh-tw/windows/wsl/install-manual#step-4---download-the-linux-kernel-update-package)

```shell
## 步驟 1 - 啟用 Windows 子系統 Linux 版
dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
## 步驟 2 - 檢查執行 WSL 2 的需求

## 步驟 3 - 啟用虛擬機器功能
dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart
## 步驟 4 - 下載 Linux 核心更新套件
[WSL2 Linux 核心更新套件 (適用於 x64 電腦)](https://wslstorestorage.blob.core.windows.net/wslblob/wsl_update_x64.msi)
## 步驟 5 - 將 WSL 2 設定為預設版本
wsl --set-default-version 2


```

## wsl command

- sudo apt update && sudo apt upgrade
- 命令 **explorer.exe .**   : 從命令列開啟 Windows 檔案總管來查看儲存檔案的目錄

## wsl network

- Windows (localhost) 存取 Linux 網路應用程式
- 從 Linux (主機 IP) 存取 Windows 網路應用程式 : *cat /etc/resolv.conf*

> https://learn.microsoft.com/zh-tw/windows/wsl/networking

##   從 Windows 命令列執行 Linux 工具

不要跨作業系統使用檔案

```powershell
C:\temp> wsl ls -la
<- contents of C:\temp ->
```

## wsl git

[在 WSL 上使用 Git 開始 | Microsoft Learn](https://learn.microsoft.com/zh-tw/windows/wsl/tutorials/wsl-git)

## wsl docker

[開始使用 WSL 上的 Docker 容器 | Microsoft Learn](https://learn.microsoft.com/zh-tw/windows/wsl/tutorials/wsl-containers)

---

## wslg Install 圖形程式

[使用 WSL 執行 Linux GUI 應用程式 | Microsoft Learn](https://learn.microsoft.com/zh-tw/windows/wsl/tutorials/gui-apps)

## other

> 匯入 WSL 檔案

> [!quote] powershell
> 
> wsl --import U20.04-tommy D:\install\wsl-work\storage\Ubuntu-20.04-tommy D:\install\wsl-work\image\Ubuntu-20.04-20220121.tar

---

> [!quote] powershell
> 
> ### 停止 wsl linux
> wsl --terminate U20.04-tommy

---

> [!quote] powershell
>  
> ### 變更 root 密碼
> wsl -d U20.04-tommy -u root
> 
> 
> ### 會使用 root 進入 linux，需要變更 root 密碼，變更使用者
> passwd root
> 
> 
> 
> $myUsername=tommy
> 
> echo -e "[user]\ndefault=$myUsername" >> /etc/wsl.conf
> 
> passwd $myUsername
> 
> ### 更新
> yum update -y && yum install passwd sudo -y
> 

> [!quote] powershell
> 
> wsl -l -v
> 
> wsl --list --verbose
> 
> wsl --export Ubuntu-20.04 ./Ubuntu-20.04-20220121.tar
> 
> ### 移除
> wsl --unregister U20.04-tommy
> 
