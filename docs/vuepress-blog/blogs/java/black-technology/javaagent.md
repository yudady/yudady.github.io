---
title: javaagent
author: tommy
tags:
  - java
categories:
  - java
toc: true
date: 2018-10-31 09:57:51
---

# 簡介
1. mian方法之前，執行
2. Java Agent 則是一個可以讓你在 Java runtime 即時改 bytecode 的技術。
<!--more-->
# 內容


- 需要打包成jar
- META-INF/MANIFEST.MF
- java -javaagent:myagent.jar=thisIsAgentArgs -jar thisIsMain.jar


```java
// MANIFEST.MF
Manifest-Version: 1.0
Premain-Class: tk.tommy.MyAgent
Can-Redefine-Classes: true


package tk.tommy;
public class MyAgent {
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("=========premain方法执行========");
        System.out.println(agentArgs);
        System.out.println(instrumentation);
    }
}


```


[用 Java Agent 實作 AOP](https://medium.com/@genchilu/%E7%94%A8-java-agent-%E5%AF%A6%E4%BD%9C-aop-ccf7506e397b)
[Spring Loaded is a JVM agent for reloading class file changes whilst a JVM is running](https://github.com/spring-projects/spring-loaded)


# 參考資料


