---
title: wsl
tags: ["windows", "wsl"]
aliases: ["wsl"]
date: 2022-01-21 15:17
Source URL: "https://docs.microsoft.com/zh-tw/windows/wsl/use-custom-distro"
---

###### 1. 資料來源
 ```button  
name wsl
type link  
action https://docs.microsoft.com/zh-tw/windows/wsl/use-custom-distro
templater true  
color blue
```
---


## install 圖形程式


> 匯入WSL檔案
```ad-quote
title: powershell

wsl --import U20.04-tommy D:\install\wsl-work\storage\Ubuntu-20.04-tommy D:\install\wsl-work\image\Ubuntu-20.04-20220121.tar
```


---
````ad-quote
title: powershell

### 停止 wsl linux
wsl --terminate U20.04-tommy
````


---
```ad-quote
title: powershell
 
### 變更root密碼
wsl -d U20.04-tommy -u root


### 會使用root 進入linux，需要變更root密碼，變更使用者
passwd root



$myUsername=tommy

echo -e "[user]\ndefault=$myUsername" >> /etc/wsl.conf

passwd $myUsername

### 更新
yum update -y && yum install passwd sudo -y

```





```ad-quote
title: powershell

wsl -l -v

wsl --list --verbose

wsl --export Ubuntu-20.04 ./Ubuntu-20.04-20220121.tar

### 移除
wsl --unregister U20.04-tommy

```

