---
modified: 2022-04-09 20:07
title: 簡介
author: tommy
tags:
  - docker
categories:
  - devops
toc: true
date: 2019-02-08 02:35:53
---

# 簡介

2 * vmware 連接
TODO check

<!--more-->
# 內容

## 安裝工具
> yum install bridge-utils 





[網橋](images/20190208023935.png)

```
但是通过这种桥接，所有网卡都要在一个网段下，所以要对每个Docker守护进程对ip的分配做出限制
下面，我们就来实现这个结构：
操作：
我用的是两台centos7.1的服务器
host1:192.168.2.1 eth0
host2:192.168.2.2 eth0
具体操作如下:
以下，以Host1 为例，Host2 上操作相似，只是网卡名字不一样，我在这里，没有使用默认的docker0 网桥，而是新建了虚拟网桥.
brctl addbr br1
为网桥分配一个同网段ip
host1
ifconfig br1 192.168.2.1 netmask 255.255.255.0　　
host2
ifconfig br1 192.168.2.2 netmask 255.255.255.0　　
host1,host2都要操作：
 桥接本地网卡：
brctl addif br1 eth0
这里，我们就准备好了网桥设置
下面我们来修改Docker的配置,使用我们新建的网桥代替docker0:
修改 /etc/sysconfig/docker文件
host1
cat /etc/sysconfig/docker
DOCKER_OPTS="-b=br1 --fixed-cidr='192.168.2.64/26' "
host2
DOCKER_OPTS="-b=br1 --fixed-cidr='192.168.2.128/26' "
这里，-b 用来指定容器连接的网桥名字
　　　　　--fixed-cidr用来限定为容器分配的IP地址范围
保存文件并重启Docker服务
systemctl restart docker

下面，就可以来验证：
分别在两个Host上启动一个容器
docker run -it dockersdd/cct1
在容器中运行ping命令查看连接情况 
[root@5dc021f7ecf8 /]# ping 172.17.0.6
PING 172.17.0.6 (172.17.0.6) 56(84) bytes of data.
64 bytes from 172.17.0.6: icmp_seq=1 ttl=64 time=0.047 ms

```



## 网桥常用命令：
- 创建网桥设备：brctl addbr ，eg：`brctl addbr  br0`
- 向网桥设备添加物理网卡：brctl addif ，eg：`brctl  addif  br0 eth0`
- 从网桥中删除网卡：brctl delif ，eg：`brctl  delif  br0 eth0`
- 删除网桥：brctl delbr ，eg：`brctl delbr br0`
- 查看网桥配置情况：brctl show


## 配置Br0网桥
```
$ vim /etc/sysconfig/network-scripts/ifcfg-br0
DEVICE="br0"
ONBOOT="yes"
TYPE="Bridge"
BOOTPROTO=static
IPADDR=192.168.15.49
NETMASK=255.255.255.0
GATEWAY=192.168.15.1
DNS1=192.168.15.1
DEFROUTE=yes


物理网卡,最後新增
BRIDGE=br0  --这个很重要
```

# 參考資料
- [docker虛擬網橋實現固定IP，容器互通，外網可用](https://www.smwenku.com/a/5b96c5f62b717750bda5efa0/)

