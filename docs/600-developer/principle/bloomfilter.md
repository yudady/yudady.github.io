---
title: bloomfilter
author: tommy
tags:
  - business
categories:
  - principle
toc: true
date: 2019-02-01 13:53:54
---

# 簡介

解決緩存穿透Guava的`BloomFilter`布隆過濾器

<!--more-->
# 內容

```java
import java.nio.charset.Charset;
 
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
 
public class Bloom {
     
    public static void main(String[] args) {
        BloomFilter<String> b = BloomFilter.create(Funnels.stringFunnel(Charset.forName("utf-8")), 1000, 0.000001);
        b.put("121");
        b.put("122");
        b.put("123");
        System.out.println(b.mightContain("12321"));
        BloomFilter<String> b1 = BloomFilter.create(Funnels.stringFunnel(Charset.forName("utf-8")), 1000, 0.000001);
        b1.put("aba");
        b1.put("abb");
        b1.put("abc");
        b1.putAll(b);
        System.out.println(b1.mightContain("123"));
    }
 
}
```

# 參考資料
- [google guava bloom filter包的坑](http://www.voidcn.com/article/p-qmryfmjc-sa.html)
- [Redis 的 Bitmaps 实现布隆过滤器](https://mp.weixin.qq.com/s/TBCEwLVAXdsTszRVpXhVug)
- [Redis架构之防雪崩设计](https://mp.weixin.qq.com/s/TBCEwLVAXdsTszRVpXhVug)

