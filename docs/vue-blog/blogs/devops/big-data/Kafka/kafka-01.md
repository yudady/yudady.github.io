---
title: kafka-01
author: tommy
tags:
  - kafka
categories:
  - devops
toc: true
date: 2018-11-18 16:05:19
---
# 簡介

> kafka_2.11-0.11.0.3.tgz

- procuctor
- consumer
- consumer group 
- topic
- borker
- partition
- offset



<!--more-->
# 內容

## clustered

```s
# The id of the broker. This must be set to a unique integer for each broker.
broker.id=1  ##不能重複

#delete.topic.enable=true
delete.topic.enable=true

# A comma seperated list of directories under which to store log files
#log.dirs=/tmp/kafka-logs
log.dirs=/opt/kafka/logs

#The default number of log partitions per topic. More partitions allow greater
#parallelism for consumption, but this will also result in more files across
#the brokers.
num.partitions=1


# Zookeeper connection string (see zookeeper docs for details).
# This is a comma separated host:port pairs, each corresponding to a zk
# server. e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002".
# You can also append an optional chroot string to the urls to specify the
# root directory for all kafka znodes.
zookeeper.connect=localhost:2181,localhost1:2181,localhost2:2181

```




## 啟動
```s
bin/kafka-server-start.sh config/server.properties

ctrl + z
bg
jobs -1
fg 1
```

## 關閉
```s
bin/kafka-server-stop.sh
```



## 在控制台创建发送者
```s
bin/kafka-console-producer.sh --broker-list hadoop101:9092 --topic first
> hello world

```

## 在控制台创建消费者
```s
bin/kafka-console-consumer.sh --zookeeper hadoop101:2181 --topic first
```


## interceptor



# 參考資料
- [kafka](http://kafka.apache.org/)

