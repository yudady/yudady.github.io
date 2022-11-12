---
title: 泛型方法
author: tommy
tags:
  - java
categories:
  - java
toc: true
date: 2019-01-15 00:43:35
---

# 簡介

> 泛型有3種
1. 類
2. 接口
3. 方法

泛型方法能使方法独立于类而产生变化

<!--more-->
# 內容

[泛型方法](https://blog.csdn.net/s10461/article/details/53941091)

```java
class Generic<T> {
    T key;

    public T getKey() {
        return key;
    }

    public <U extends Number> U showKeyName(Generic<U> container) {
        System.out.println("container key :" + container.getKey());
        U test = container.getKey();
        return test;
    }
}
```


# 參考資料


