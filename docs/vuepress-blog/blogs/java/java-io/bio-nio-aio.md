---
title: bio-nio-aio
author: tommy
tags:
  - java
categories:
  - java
toc: true
date: 2018-10-24 08:32:51
---

# 簡介

## 阻塞、非阻塞（程式在io時，數據未準備好的行為）
- 阻塞和非阻塞主要關注的是等待結果返回調用方的狀態
- 阻塞:原地等待
- 非阻塞:直接返回，等下再來或是等待通知
- 阻塞:是指結果返回之前，當前線程被掛起，不做任何事
- 非阻塞:是指結果在返回之前，線程可以做一些其他事，不會被掛起。


## 同步.異步（程式與OS，在IO所採用的方式）
- 同步和異步關注的是結果消息的通信機制
- 同步:參與IO，等待（阻塞）處理完成，資料處理完成後OS把IO流阻塞，或是一直輪詢問是否完成
- 異步:操作系統處理，輪詢、事件監聽
- 同步:同步的意思就是調用方需要主動等待結果的返回
- 異步:異步的意思就是不需要主動等待結果的返回，而是通過其他手段比如，狀態通知，回調函數等。

<!--more-->
# 內容

- 同步阻塞
- 同步非阻塞
- 異步阻塞（X）
- 異步非阻塞

## select epoll



-----

## io
- stream
- 裝飾設計模式（頂層接口，每個子類都持有一個相同接口的引用）
- inputStream
  - 節點流（FileInputStream）
  - 緩衝流（BufferInputStream）




## nio
- select
- channel
- buffer
  - limit
  - capacity
  - position




## ByteBuffer
- asReadOnlyBuffer
- compact
- allocate-HeapByteBuffer
- allocateDirect(zero copy)-DirectByteBuffer


# 參考資料
- [異步化，高並發大殺器](https://www.jianshu.com/p/1b3d5878c931)
- [Java NIO中，关于DirectBuffer，HeapBuffer的疑问？](https://www.zhihu.com/question/57374068)
