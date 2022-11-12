---
title: centos7-command
author: tommy
tags:
  - centos7
categories:
  - devops
toc: true
date: 2018-11-18 01:17:04
---

# 簡介

紀錄命令

<!--more-->
# 內容

## 先檢查目前的hostname
> hostnamectl

## 修改hostname
> hostnamectl set-hostname tk.yudady.com

## hosts
> vim /etc/hosts

## tar to another folder
> tar -zxvf source -C dest

## scp
來源 -> 目的地
> scp -r username@ip:/opt/tomcat username2@ip2:/opt/tomcat

## ssh免密碼
- 生成密鑰對，公鑰複製到目標主機的authorized_keys
- ssh-keygen -t rsa
- ssh-copy-id 目標主機

## rsync(內容相同不複製，內容不同才會替換)，只增加不會減少
> rsync -rvl $source $user@ip:$dest

## jobs bg fg
- & 用在一個命令的最後，可以把這個命令轉換為後台運行的任務進程
- jobs 查看當前終端有多少在後台運行的進程。
- ctrl + z：可以將一個正在前台執行的任務放到後台運行，並且掛起
- bg %num 將選中的任務進程啟動運行
- fg %num 將選中的任務進程調至前台
- jobs 
- jobs -l


## 讓程式登出後可繼續執行，重新導向輸出
> nohup /path/my_program > my.out 2> my.err &








---


# SHELL

## 複製資料到其他主機(xsync.sh)
- 循環所有節點到相同目錄下，shell放在/usr/local/bin/xsync.sh
- xsync.sh and xcall.sh
```s
#!/bin/bash
#1 獲取輸入參數個數，如果沒有參數，直接退出
pcount=$#
if((pcount==0)); then
  echo no args;
  exit;
fi
#2 獲取文件名稱
p1=$1
fname=`basename $p1`
echo fname=$fname
#3 獲取上級目錄到絕對路徑
pdir=`cd -P $(dirname $p1); pwd`
echo pdir=$pdir
#4 獲取當前用戶名稱
user=`whoami`
#5 循環主機名稱（hadoop101,hadoop102,hadoop103      hosts設定）
for((host=101; host<104; host++)); do
  #echo $pdir/$fname $user@hadoop$host:$pdir
  echo --------------- hadoop$host ----------------
  rsync -rvl $pdir/$fname $user@hadoop$host:$pdir
done
```

## 一起執行命令(xcall.sh)
- 添加環境變量到 ~/.bashrc(SSH免密碼需要用到)
- xcall.sh ls
- xcall.sh /opt/jdk1.8.0_181/bin/jps
```s
#!/bin/bash
pcount=$#
if((pcount==0));then
  echo no args;
exit;
fi
echo -------------localhost----------
$@
for((host=101; host<104; host++)); do
  echo ----------hadoop$host---------
  ssh hadoop$host $@
done

```


# 參考資料


