---
title: java8-FunctionalInterface（Lambda）
author: tommy
tags:
  - java
categories:
  - java
toc: true
date: 2018-10-16 08:21:30
---

# 簡介

- js callback function <=> lambda(FunctionalInterface)
- 回調函數
- 傳動作、傳方法

```javascript
function javascriptCallback (a , callback){
	if(callback){
		callback();
	}
}
```

```java
// Interface Function<T   ,R   >
// Interface Function<參數,返回值>
public Integer javaLamdba(Integer a ,java.util.Function<Integer,Integer> fun) {
	fun.apply(a);
}


Integer intReturnValue = javaLamdba(2 , param -> param * param );
Integer intReturnValue = javaLamdba(2 , param -> param + param );
Integer intReturnValue = javaLamdba(2 , param -> param / param );

```


<!--more-->
# 內容

- Consumer<T>，(unary function from T to void)
- Function<T,R>，(unary function from T to R)
- Predicate<T>，(unary function from T to boolean)
- Supplier<T>，(nilary function to R)


## Consumer

## Function

## Predicate

## Supplier



# 參考資料





















