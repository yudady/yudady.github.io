---
title: docker-compose-01
author: tommy
tags:
  - docker
categories:
  - devops
toc: true
date: 2018-12-02 16:21:04
---

# 簡介

> docker-compose.yml

用來一次啟動多個images

<!--more-->
# 內容

## 使用yum安装Docker-Compose
1. yum install -y epel-release
2. yum install -y python-pip
3. pip install docker-compose
4. docker-compose --version

## 使用shell安装Docker-Compose
1. sudo curl -L "https://github.com/docker/compose/releases/download/1.23.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
2. sudo chmod +x /usr/local/bin/docker-compose
3. docker-compose --version
4. docker-compose migrate-to-labels


## 一個mysql and adminer
```yml
# Use root/example as user/password credentials
version: '3.1'

services:

  db:
    image: mysql:5.7
    restart: always
    environment:
       MYSQL_ROOT_PASSWORD: 'root'
       MYSQL_DATABASE: 'db'
       MYSQL_USER: 'tommy'
       MYSQL_PASSWORD: 'tommy'

  adminer:
    image: adminer
    restart: always
    ports:
      - 8888:8080

```



# 啟動命令( -d 可以啟動在後台 )
> docker-compose up


## adminer(UI)
> http://localhost:8888

# 參考資料
- [adminer](https://hub.docker.com/_/adminer/)
- [docker-compose教程（安装，使用, 快速入门）](https://blog.csdn.net/pushiqiang/article/details/78682323)
- [Install Docker Compose](https://docs.docker.com/compose/install/)

