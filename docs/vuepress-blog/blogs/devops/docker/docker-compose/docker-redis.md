---
title: docker-redis
author: tommy
tags:
  - docker
categories:
  - devops
toc: true
date: 2018-12-03 14:17:00
---

# 簡介



<!--more-->
# 內容

## docker-compose.yml

```yml
redis:
 image: redis
 ports:
  - "6379:6379"

```

## 进入Docker容器的redis的客户端
> docker exec -it 容器ID redis-cli 




# 參考資料
- [Docker安装官方Redis镜像](https://blog.csdn.net/CHENYUFENG1991/article/details/78513463)


