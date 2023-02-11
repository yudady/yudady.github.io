---
title: netty-EventLoopGroup
author: tommy
tags:
  - netty
categories:
  - devops
toc: true
date: 2018-11-11 11:05:10
---

# 簡介

NioEventLoopGroup


<!--more-->
# 內容



- NioEventLoopGroup
  - MultithreadEventLoopGroup
  - DefaultEventLoopGroup
- EventExecutor
  - SingleThreadEventExecutor
  - SingleThreadEventLoop
  - EmbeddedEventLoop
- RejectedExecutionHandlers
- DefaultPromise


## 比較
- EventLoopGroup extends Event`Executor`Group
- MultithreadEventLoopGroup extends MultithreadEvent`Executor`Group


## java.util.concurrent.Executor
> function interface
```java

Executor executor = <em>anExecutor</em>;
// Executor executor = Executors.newFixedThreadPool 
// Executor executor = Executors.newCachedThreadPool
// Executor executor = Executors.XXXXXXXXXXXXX ;
executor.execute(new RunnableTask1());
executor.execute(new RunnableTask2());


// 自定義 Executor--------------------

// 用戶線程執行
class DirectExecutor implements Executor {
  public void execute(Runnable r) {
    r.run();
  }
}}


// new Thread
class ThreadPerTaskExecutor implements Executor {
  public void execute(Runnable r) {
    new Thread(r).start();
  }
}}

// 一系列任務
class SerialExecutor implements Executor {
  final Queue<Runnable> tasks = new ArrayDeque<Runnable>();
  final Executor executor;
  Runnable active;

  SerialExecutor(Executor executor) {
    this.executor = executor;
  }

  public synchronized void execute(final Runnable r) {
    tasks.offer(new Runnable() {
      public void run() {
        try {
          r.run();
        } finally {
          scheduleNext();
        }
      }
    });
    if (active == null) {
      scheduleNext();
    }
  }

  protected synchronized void scheduleNext() {
    if ((active = tasks.poll()) != null) {
      executor.execute(active);
    }
  }
}}


```


# 參考資料


