---
title: java-maven01
author: tommy
tags:
  - maven
categories:
  - java
toc: true
date: 2018-10-18 12:46:49
---

# 簡介
> 紀錄maven特殊地方


<!--more-->
# 內容

## 依賴本地jar包

```java
// 命令
mvn install:install-file -Dfile={Path/to/your/ojdbc6.jar} -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0 -Dpackaging=jar

<!-- ojdbc6.jar example -->
<dependency>
    <groupId>com.oracle</groupId>
    <artifactId>ojdbc6</artifactId>
    <version>11.2.0</version>
</dependency>
```


```java
// 直接引用
<dependency>
    <groupId>com.sample</groupId>
    <artifactId>sample</artifactId>
    <version>1.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/src/main/resources/yourJar.jar</systemPath>
</dependency>
```

## resources

```xml
// excludes比較強
<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
            <includes>
                <include>app1.xml</include>
            </includes>
            <excludes>
                <exclude>app2.xml</exclude>
            </excludes>
        </resource>
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.properties</include>
            </includes>
        </resource>
    </resources>
</build>
```

## properties

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.compiler.vertion>1.8</java.compiler.vertion>
    <failOnMissingWebXml>false</failOnMissingWebXml>
</properties>
```


## [maven根据不同环境 不同配置打包](https://my.oschina.net/u/2341924/blog/667730)






# 參考資料



