---
title: SOA.ESB.MicroService
author: tommy
tags:
  - principle
categories:
  - principle
toc: true
date: 2019-02-28 08:20:25
---

# 簡介

- SOA(Service-Oriented Architecture)
  > SOA是一种理念，它的主要特性--面向服务的分布式计算，服务间松散耦合，支持服务的封装，服务注册和自动发现，以服务契约方式定义服务交互方式。但是，SOA并没有定义出具体的实现方式，目前有两套SOA理念的实现方式：中心化和去中心化，这两套架构并没有优劣之分，还是要针对企业的根本诉求。
- ESB(Enterprise service bus)
  > SOA`中心化`的实现方式就是ESB，ESB的根本诉求是为了解决异构系统之间的连通性，通过协议转换、消息解析、消息路由把服务提供者的数据传送到服务消费者。所以，ESB是中心化的，很重，有一定的逻辑，但它的确可以解决一些公用逻辑的问题。
- Micro Service
  > SOA`去中心化`的实现方式的根本诉求是扩展性，阿里巴巴的分布式服务框架HSF是一种去中心化的实现方式，也是微服务的最佳实践。分布式服务框架，主要有dubbox、spring cloud，实现后端服务治理的功能。

<!--more-->
# 內容

## SOA
- SOA在相对较粗的粒度上对应用服务或业务模块进行封装与重用；
- 服务间保持松散耦合，基于开放的标准， 服务的接口描述与具体实现无关；
- 灵活的架构 -服务的实现细节，服务的位置乃至服务请求的底层协议都应该透明；

# 參考資料
- [google](http://www.google.com)

