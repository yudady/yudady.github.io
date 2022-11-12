---
title: java8-collect
author: tommy
tags:
  - java
categories:
  - java
toc: true
date: 2018-10-19 08:52:18
---

# 簡介

- stream().collect()
- public final class java.util.stream.`Collectors`
- public interface java.util.stream.`Collector`<T, A, R>
  - static class `CollectorImpl`<T, A, R> implements Collector<T, A, R>
  - 收集器(A)，可變
    -  creation of a new result container ({@link #`supplier`()})。提供container
    -  incorporating a new data element into a result container ({@link #`accumulator`()})。container對下一個element的操作
    -  combining two result containers into one ({@link #`combiner`()})。container合併
    -  performing an optional final transform on the container ({@link #`finisher`()})。container轉換型態
- reduce，不可變

<!--more-->
# 內容

## groupingBy
- groupingBy(Function, Collector)
  - collect(Collectors.groupingBy(Student::getName))
- groupingBy(Function, Supplier, Collector)
  - collect(Collectors.groupingBy(Student::getName, Collectors.counting()));
- groupingByConcurrent(Function)



## partitioningBy
- partitioningBy(Predicate)
  - collect(Collectors.partitioningBy(student -> student.getScore() >= 90));
- partitioningBy(Predicate, Collector)
  - collect(Collectors.partitioningBy(student -> student.getScore() >= 90, Collectors.counting()));






# 參考資料


