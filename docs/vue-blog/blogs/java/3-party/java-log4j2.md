---
title: java-log4j2
author: tommy
tags:
  - java
categories:
  - java
toc: true
date: 2018-11-19 23:35:58
---

# 簡介
> log4j2

<!--more-->
# 內容

## Log4j2 Multiple appenders example

```java
public class LogUtils {
    public static final Logger main = LoggerFactory.getLogger("main");
    public static final Logger bolt = LoggerFactory.getLogger("bolt");
    public static final Logger spout = LoggerFactory.getLogger("spout");
}

```
## xml設定
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration monitorInterval="60">

    <Properties>
        <Property name="LOG_PATTERN">[%-5level] %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %c{1} - %msg%n</Property>
    </Properties>

    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="${LOG_PATTERN}"/>
        </Console>


        <RollingFile name="main" fileName="C:/logs/main.log"
                     filePattern="C:/logs/main-%d{yyyy-MM-dd}-%i.log">
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="19500KB"/>
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile>
        <RollingFile name="bolt" fileName="C:/logs/bolt.log"
                     filePattern="C:/logs/bolt-%d{yyyy-MM-dd}-%i.log">
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="19500KB"/>
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile>
        <RollingFile name="spout" fileName="C:/logs/spout.log"
                     filePattern="C:/logs/spout-%d{yyyy-MM-dd}-%i.log">
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="19500KB"/>
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile>
    </Appenders>


    <Loggers>
        <Logger name="main" additivity="false">
            <AppenderRef ref="main"/>
        </Logger>
        <Logger name="bolt" additivity="false">
            <AppenderRef ref="bolt"/>
        </Logger>
        <Logger name="spout" additivity="false">
            <AppenderRef ref="spout"/>
        </Logger>


        <Root level="all">
            <AppenderRef ref="Console"/>
        </Root>


    </Loggers>
</configuration>


```



# 參考資料
- [HowToDoInJava](https://howtodoinjava.com/log4j2/)

