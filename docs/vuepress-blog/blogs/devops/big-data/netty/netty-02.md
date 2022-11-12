---
title: netty-02
author: tommy
tags:
  - netty
categories:
  - devops
toc: true
date: 2018-11-15 20:49:01
---

# 簡介
- NioServerSocketChannel


<!--more-->
# 內容

## NioServerSocketChannel create and start
- io.netty.bootstrap.AbstractBootstrap#doBind()
  - final ChannelFuture regFuture = initAndRegister();
    - channel = channelFactory.newChannel();
      - pipeline
    - init(channel)
      - options0
      - attrs0()
      - currentChildOptions
      - currentChildAttrs
      - pipeline.addLast(new ServerBootstrapAcceptor(ch, currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs));
    - registor
      - eventloop
      - registor0
        - doRegister
        - invokeHandlerAddedIfNeeded
        - fireChannelRegistered
    - AbstractUnsafe.doBind
      - doBind
        - javaChannel.bind
      - pipeline.fireChannelActived







## ServerBootstrap.bind
![netty](../images/20181116001847.png)
![netty](../images/20181116001714.png)
![netty](../images/20181116002031.png)
![netty](../images/20181116002658.png)
![netty](../images/20181116002805.png)
![netty](../images/20181116002858.png)

---

## NioEventLoop
![netty](../images/20181116001300.png)
- ThreadPerTaskExecutor（每次執行都會建立新的線程）
  - DefaultThreadFactory
- FastThreadLocalThread（NioEventLoop的線程，內含有一個InternalThreadLocalMap）
  - InternalThreadLocalMap
    - InternalThreadLocalMap


![netty](../images/20181116004633.png)
![netty](../images/20181116004917.png)
![netty](../images/20181116005038.png)

## NioEventLoop啟動
![netty](../images/20181116010158.png)
### 服務器啟動觸發
![netty](../images/20181116011522.png)
### accept(0 -> OP_ACCEPT)觸發
- 
![netty](../images/20181116012535.png)
![netty](../images/20181116014454.png)
![netty](../images/20181116020551.png)
### 註冊新連接(與accept(OP_ACCEPT->OP_CONNECT)共用邏輯)
![netty](../images/20181116073235.png)
![netty](../images/20181116073828.png)
- NioEventLoop#processSelectedKey
![netty](../images/20181116081233.png)
![netty](../images/20181116094054.png)

### 新连接NioEventLoop
- 創建NioSocketChannel(pipline unsafe)
- 綁定selector(attch=NioSocketChannel)	
- 註冊OP_READ
![netty](../images/20181116095514.png)
![netty](../images/20181116095648.png)

## pipeline
- 有多個ChannelHanderContext
- head
- tail
### ChannelHanderContext
- 有一個channel
- 有一個hander
- 有一個EventExecutor(NioEventLoop)
- 有多個EvemtFire
![netty](../images/20181116114723.png)
![netty](../images/20181116114820.png)
![netty](../images/20181116120759.png)







# 參考資料


