---
title: java8-Spliterator
author: tommy
tags:
  - java
categories:
  - java
toc: true
date: 2018-10-22 11:33:25
---

# 簡介

- public interface Spliterator<T>
- public final class Spliterators
- abstract class ReferencePipeline<P_IN, P_OUT> extends AbstractPipeline<P_IN, P_OUT, Stream<P_OUT>> implements Stream<P_OUT>
  - Abstract base class for an intermediate pipeline stage or pipeline source stage implementing whose elements are of type {@code U}(收集操作) 
- abstract static class StatelessOp<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT>
  - Base class for a stateless intermediate stage of a Stream.(無狀態)



# java.sql.ResultSet => Spliterator (TODO)

<!--more-->
# 內容

## characteristics
- public static final int ORDERED    = 0x00000010;
- public static final int DISTINCT   = 0x00000001;
- public static final int SORTED     = 0x00000004;
- public static final int SIZED      = 0x00000040;
- public static final int NONNULL    = 0x00000100;
- public static final int IMMUTABLE  = 0x00000400;
- public static final int CONCURRENT = 0x00001000;
- public static final int SUBSIZED = 0x00004000;



# 參考資料


