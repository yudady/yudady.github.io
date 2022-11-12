---
title: yum-aliyun
author: tommy
tags:
  - yum
categories:
  - devops
toc: true
date: 2018-11-29 08:46:14
---

# 簡介

mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup

<!--more-->
# 內容

## 變更yum源
1. 備份yum源
> mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup

2. download CentOS 7 yum
> wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo


3. yum makecache生成缓存
> 

# 參考資料
- [google](http://www.google.com)

