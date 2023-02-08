---
title: redis
tags: []
aliases: [redis]
created_date: 2023-02-04 10:15
updated_date: 2023-02-05 07:05
---

# redis

nosql內存型數據庫，持久化（RDB AOF），Reactor epoll io事件通知機制
string，list，set，zset，hash
數據庫，緩存，消息中間件

## 基礎知識

### 清空資料
 `flashall`  `flashdb`

### redis 為何會很快 
redis 是單線程**內存**數據庫，CPU不是瓶頸，瓶頸是內存大小與網路帶寬
redis 把數據放在內存中，單線程，沒有線程切換，讀寫都在單一個CPI上面

