---
title: ServletContainerInitializer
author: tommy
tags:
  - spring
categories:
  - java
toc: true
date: 2018-10-30 17:29:01
---

# 簡介

- @since Servlet 3.0
- web容器啟動時，給第3方初始化機會
- 新增檔案，META-INF/services/javax.servlet.ServletContainerInitializer
- 裡面放，package.ClassName，ex: org.springframework.web.WebApplicationInitializer
<!--more-->
# 內容
- ServletContainerInitializers (SCIs) are registered via an entry in the file META-INF/services/javax.servlet.ServletContainerInitializer that must be included in the JAR file that contains the SCI implementation.

```java
public interface ServletContainerInitializer {
    void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException;
}
```

## org.springframework.web.SpringServletContainerInitializer implements ServletContainerInitializer

- javax.servlet.annotation.HandlesTypes


# 參考資料
[Spring之WebContext不使用web.xml啟動 初始化重要的類源碼分析（Servlet3.0以上的）](https://hk.saowen.com/a/262c3ff121a703e95e435d86ee7faa2d91f779c1a4f8d51e368eb2aa7defcb84)

[Servlet3.0 - ServletContainerInitializer注册JAVA组件](https://blog.csdn.net/j080624/article/details/80016905)


