---
title: docker-compouse-02
author: tommy
tags:
  - docker
categories:
  - devops
toc: true
date: 2018-12-09 19:36:35
---

# 簡介

docker-compose up
mysql

<!--more-->
# 內容

```yml
version: "3"
services:

  db:
    image: mysql:5.7
    ports:
      - "33060:3306"
    volumes:
      - ./data:/var/lib/mysql
    restart: always
    environment:
      - MYSQL_USER=tommy
      - MYSQL_PASSWORD=tommy
      - MYSQL_DATABASE=dockerDb
      - MYSQL_ROOT_PASSWORD=root
```

# 參考資料


