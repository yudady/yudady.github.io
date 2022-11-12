---
title: redis-01-install
author: tommy
tags:
  - redis
categories:
  - devops
toc: true
date: 2018-11-16 21:27:50
---

# 簡介

redis install

- Introduction to Redis data types. http://redis.io/topics/data-types-intro
- Try Redis directly inside your browser. http://try.redis.io
- The full list of Redis commands. http://redis.io/commands
- There is much more inside the Redis official documentation. http://redis.io/documentation

<!--more-->
# 內容

## 準備
- gcc -v
- yum install gcc-c++



## 下載，安裝
```shell
$ wget http://download.redis.io/releases/redis-5.0.0.tar.gz
$ tar zxvf redis-5.0.0.tar.gz
$ ln -s redis-5.0.0 redis
$ cd redis
$ make
```


## Running Redis
> To run Redis with the default configuration just type:
```shell
% cd src
% ./redis-server
```


## 檢查是否啟動
- ps -ef | grep redis | grep -v grep
- netstat -tlanp 6379
- lsof -i:6379


### net.core.somaxconn
> WARNING: The TCP backlog setting of 511 cannot be enforced because /proc/sys/net/core/somaxconn is set to the lower value of 128
> net.core.somaxconn是Linux中的一个kernel参数，表示socket监听（listen）的backlog上限。什么是backlog呢？backlog就是socket的监听队列，当一个请求（request）尚未被处理或建立时，他会进入backlog。而socket server可以一次性处理backlog中的所有请求，处理后的请求不再位于监听队列中。当server处理请求较慢，以至于监听队列被填满后，新来的请求会被拒绝。


### 如何修改net.core.somaxconn
```shell
sysctl -a //会显示所有的kernel参数及值。 
vim /etc/sysctl.conf //編輯
net.core.somaxconn=32768 

sysctl -p //然后执行命令
```

### WARNING overcommit_memory
> WARNING overcommit_memory is set to 0! Background save may fail under low memory condition. To fix this issue add `vm.overcommit_memory = 1` to `/etc/sysctl.conf` and then reboot or run the command 'sysctl vm.overcommit_memory=1' for this to take effect.

### WARNING you have Transparent Huge Pages (THP)
> WARNING you have Transparent Huge Pages (THP) support enabled in your kernel. This will create latency and memory usage issues with Redis. To fix this issue run the command `echo never > /sys/kernel/mm/transparent_hugepage/enabled` as root, and add it to your /etc/rc.local in order to retain the setting after a reboot. Redis must be restarted after THP is disabled.

###  Playing with Redis

> You can use redis-cli to play with Redis. Start a redis-server instance, then in another terminal try the following:

```shell
% cd src
% ./redis-cli
redis> ping
PONG
redis> set foo bar
OK
redis> get foo
"bar"
redis> incr mycounter
(integer) 1
redis> incr mycounter
(integer) 2
redis>
```


## make install
> You can use make PREFIX=/some/other/directory install if you wish to use a different destination.


# RDB


# AOF




# 命令
- SELECT index（切換數據庫）
- Dbsize(查看數據庫的key數量)
- FlushDb(清空當前數據庫)
- Flushall（數據庫通殺）

# 參考資料
- [redis官網](https://redis.io/)
- [redis中文網](http://www.redis.cn/)
- [Redis 命令参考](http://redisdoc.com/)
- [RedisClient](https://github.com/caoxinyu/RedisClient)
- [RedisDesktopManager](https://github.com/uglide/RedisDesktopManager/releases/tag/0.8.8


- [Redis分布式锁实现高并发控制实践](http://usherblog.site/2018/08/05/Redis%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81%E5%AE%9E%E7%8E%B0%E9%AB%98%E5%B9%B6%E5%8F%91%E6%8E%A7%E5%88%B6%E5%AE%9E%E8%B7%B5/`)







