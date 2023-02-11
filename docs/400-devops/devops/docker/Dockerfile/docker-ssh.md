---
title: docker-ssh
author: tommy
tags:
  - docker
categories:
  - devops
toc: true
date: 2018-12-03 22:48:57
---

# 簡介



<!--more-->
# 內容

``` yml
#显示该镜像是基于java8镜像
FROM java:8
#维护人信息
MAINTAINER quding niudear@foxmail.com
#更新源
RUN apt-get update
#安装软件
RUN apt-get install -y openssh-server
RUN mkdir -p /var/run/sshd
RUN mkdir -p /root/.ssh
#取消pam限制
RUN sed -ri 's/session  required   pam_loginuid.so/#session    required  pam_loginuid.so/g' /etc/pam.d/sshd
#复制配置文件到相应位置
COPY authorized_keys /root/.ssh/authorized_keys
COPY run.sh /run.sh
#赋予脚本权限
RUN chmod 755 /run.sh
#开放端口
EXPOSE 22
#设置启动命令
CMD ["/run.sh"]
```


# 參考資料
- [google](http://www.google.com)

