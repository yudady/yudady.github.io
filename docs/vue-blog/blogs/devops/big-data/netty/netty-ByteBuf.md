---
title: netty-ByteBuf
author: tommy
tags:
  - netty
categories:
  - devops
toc: true
date: 2018-11-14 13:05:09
---

# 簡介

public abstract class `ByteBuf` implements `ReferenceCounted`, Comparable<ByteBuf> 

<!--more-->
# 內容


## 讀寫指針

- CONTENT，還未讀取的資料

```java
ByteBuf buffer = ...;
for (int i = 0; i &lt; buffer.capacity(); i ++) { 
    //絕對取值，readerIndex不會動
    byte b = buffer.getByte(i);
    System.out.println((char) b);
}

// Iterates the readable bytes of a buffer.
ByteBuf buffer = ...;
while (buffer.isReadable()) {
    // 相對取值，readerIndex會移動
    System.out.println(buffer.readByte());
}
</pr

/*
 * <pre>
 *      +-------------------+------------------+------------------+
 *      | discardable bytes |  readable bytes  |  writable bytes  |
 *      |                   |     (CONTENT)    |                  |
 *      +-------------------+------------------+------------------+
 *      |                   |                  |                  |
 *      0      <=      readerIndex   <=   writerIndex    <=    capacity
 * </pre>
 */


```

## Discardable bytes(把已經讀過的資料清空)
- Arrsys.copy...

```java

/*
 * <pre>
 *  BEFORE discardReadBytes()
 *
 *      +-------------------+------------------+------------------+
 *      | discardable bytes |  readable bytes  |  writable bytes  |
 *      +-------------------+------------------+------------------+
 *      |                   |                  |                  |
 *      0      <=      readerIndex   <=   writerIndex    <=    capacity
 *
 *
 *  AFTER discardReadBytes()
 *
 *      +------------------+--------------------------------------+
 *      |  readable bytes  |    writable bytes (got more space)   |
 *      +------------------+--------------------------------------+
 *      |                  |                                      |
 * readerIndex (0) <= writerIndex (decreased)        <=        capacity
 * </pre>
 */
```


## Clearing the buffer indexes（讀寫清空）
- 移動readerIndex
- 移動writerIndex

```java
/*
 * <pre>
 *  BEFORE clear()
 *
 *      +-------------------+------------------+------------------+
 *      | discardable bytes |  readable bytes  |  writable bytes  |
 *      +-------------------+------------------+------------------+
 *      |                   |                  |                  |
 *      0      <=      readerIndex   <=   writerIndex    <=    capacity
 *
 *
 *  AFTER clear()
 *
 *      +---------------------------------------------------------+
 *      |             writable bytes (got more space)             |
 *      +---------------------------------------------------------+
 *      |                                                         |
 *      0 = readerIndex = writerIndex            <=            capacity
 * </pre>
 */
```




# 參考資料
- [編碼](https://mp.weixin.qq.com/s/adB869wM3XPndwuv9LVHGg)

