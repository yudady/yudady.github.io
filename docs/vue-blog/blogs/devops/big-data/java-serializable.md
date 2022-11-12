---
title: 數據傳輸序列化
author: tommy
tags:
  - big-data
categories:
  - devops
toc: true
date: 2018-11-05 09:52:08
---

# 簡介

## rmi 
- java serializable


## RPC(跨語言)
- xml-webService 
- json-restful
- thrift-facebook
- protobuf-google
- avro
- gRPC


<!--more-->
# 內容

## Thrift

### 傳輸格式
- TBinaryProtocol
- TCompactProtocol
- TJsonProtocol
- TSimpleJsonProtocol
- TDebugProtocol 
### 傳輸方式
- TSocket
- TFranedTrandport
- TFileTransPort
- TMemoryTransPort
### 服務模型
- TSimpleServer
- TThreadPoolServer
- TNonblockingServer
- THsHaServer


## Protobuf



# 參考資料
[Thrift序列化与反序列化](https://blog.csdn.net/xuemengrui12/article/details/60963538)
[Apache Avro](http://avro.apache.org/docs/1.8.2/)
[Protocol Buffers](https://developers.google.com/protocol-buffers/?hl=zh-TW)





