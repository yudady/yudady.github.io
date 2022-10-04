---
title: wsl-command
Topic: wsl
tags: ["windows", "wsl"]
aliases: ["wsl"]
create date: 2022-01-21 15:17
Source URL: "https://docs.microsoft.com/zh-tw/windows/wsl/use-custom-distro"
date: 2022-09-28 20:30
modified: 2022-09-30 00:02
---

# wsl-command

###### 1. 資料來源

 
 ```button  
name wsl
type link  
action https://docs.microsoft.com/zh-tw/windows/wsl/use-custom-distro
templater true  
color blue
```
 
---

## Install 圖形程式

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
> ### 變更root密碼
> wsl -d U20.04-tommy -u root
> 
> 
> ### 會使用root 進入linux，需要變更root密碼，變更使用者
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
