---
title: docker-tomcat
author: tommy
tags:
  - docker
categories:
  - devops
toc: true
date: 2018-12-02 00:25:33
---

# 簡介

[tomcat](https://hub.docker.com/_/tomcat/)

<!--more-->
# 內容


## Docker Pull Command
> docker pull tomcat

- docker run -it --rm tomcat:8.0
- docker run -it --rm -p 8888:8080 tomcat:8.0














## [Docker的save和export命令的区别](https://my.oschina.net/zjzhai/blog/225112)
- docker load [tar file]
- Save命令用于持久化镜像
  - docker images
  - docker save busybox-1 > /home/save.tar
- Export命令用于持久化容器
  - docker ps -a
  - docker export <CONTAINER ID> > /home/export.tar




# 參考資料


