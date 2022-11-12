---
title: weblogic.intellij
author: tommy
tags:
  - weblogic
categories:
  - java
toc: true
date: 2019-02-18 19:54:18
---

# 簡介

Weblogic，intellij，JIRA

[WebLogic Diagnostics Framework](https://docs.oracle.com/cd/E13222_01/wls/docs103/wldf_configuring/index.html)

<!--more-->
# 內容
- [Weblogic(12C)-安装教程](https://startshineye.github.io/2017/02/15/Weblogic-12C-%E5%AE%89%E8%A3%85%E6%95%99%E7%A8%8B/)
- [weblogic12.1.3的安装及更新](https://blog.csdn.net/yiyiwudian/article/details/40382709)
- [idea工具部署webLogic项目](https://blog.csdn.net/qq_37104736/article/details/79666731)
- [jira](https://www.astralweb.com.tw/introduction-of-atlassian-jira/)
- [Jira 教學](https://www.slideshare.net/rtesldoremi/jira-34821914?from_action=save)
- [JIRA再多任務也不怕，讓您一目了然的好工具Filter](https://www.astralweb.com.tw/project-tool-jira/)



## 準備使用docker來練習
docker-compose up
```yml
version: "3"
services:

  db:
    image: ismaleiva90/weblogic12
    ports:
      - "7001:7001"
      - "7002:7002"
      - "5556:5556"
    restart: always

# http://localhost:7001/console
# http://192.168.79.100:7001/console
# User: weblogic
# Pass: welcome1

```


# 參考資料


