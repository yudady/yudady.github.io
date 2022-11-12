---
title: 前端性能监控
author: tommy
tags:
  - chrome
categories:
  - tools
toc: true
date: 2018-10-09 08:28:32
---

# 簡介

`window.performance`



- window.performance.memory
- window.performance.navigation
- window.performance.timing

---
- 用json格式直接進Elasticsearch，或加入一個中間件，使用kabana統計
- HTML5 Geolocation API



<!--more-->
# 內容

## 性能測試
- [google PageSpeed Insights](https://developers.google.com/speed/pagespeed/insights/?hl=zh-TW&url=https%3A%2F%2Fyudady.github.io%2F&tab=mobile)
- [gtmetrix](https://gtmetrix.com/)
- chrome dev tools


## 性能指标（Navigation.timing）
- DNS查询耗时 = domainLookupEnd - domainLookupStart
- TCP链接耗时 = connectEnd - connectStart
- request请求耗时 = responseEnd - responseStart
- 解析dom树耗时 = domComplete - domInteractive
- 白屏时间 = domloadng - fetchStart
- domready时间 = domContentLoadedEventEnd - fetchStart
- onload时间 = loadEventEnd - fetchStart



## [W3C Resource Timing](https://w3c.github.io/perf-timing-primer/)
![Resource Timing](https://w3c.github.io/perf-timing-primer/images/resource-timing-overview-1.png)

![Load Times for Resources](https://w3c.github.io/perf-timing-primer/images/resource-timing-detail-w3-org.png)

![PerformanceNavigationTiming attributes](https://w3c.github.io/perf-timing-primer/images/navigation-timing-attributes.png)






```
所以根据上面的时间点，我们可以计算常规的性能值，如下：
（使用该api时需要在页面完全加载完成之后才能使用，最简单的办法是在window.onload事件中读取各种数据，因为很多值必须在页面完全加载之后才能得出。）
 
var timing = window.performance && window.performance.timing;
var navigation = window.performance && window.performance.navigation;
 
 
重定向次数：
var redirectCount = navigation && navigation.redirectCount;
 
跳转耗时：
var redirect = timing.redirectEnd - timing.redirectStart;
 
APP CACHE 耗时：
var appcache = Math.max(timing.domainLookupStart - timing.fetchStart, 0);
 
DNS 解析耗时：
var dns = timing.domainLookupEnd - timing.domainLookupStart;
 
TCP 链接耗时：
var conn = timing.connectEnd - timing.connectStart;
 
等待服务器响应耗时（注意是否存在cache）：
var request = timing.responseStart - timing.requestStart;
 
内容加载耗时（注意是否存在cache）:
var response = timing.responseEnd - timing.responseStart;
 
总体网络交互耗时，即开始跳转到服务器资源下载完成：
var network = timing.responseEnd - timing.navigationStart;
 
渲染处理：
var processing = (timing.domComplete || timing.domLoading) - timing.domLoading;
 
抛出 load 事件：
var load = timing.loadEventEnd - timing.loadEventStart;
 
总耗时：
var total = (timing.loadEventEnd || timing.loadEventStart || timing.domComplete || timing.domLoading) - timing.navigationStart;
 
可交互：
var active = timing.domInteractive - timing.navigationStart;
 
请求响应耗时，即 T0，注意cache：
var t0 = timing.responseStart - timing.navigationStart;
 
首次出现内容，即 T1：
var t1 = timing.domLoading - timing.navigationStart;
 
内容加载完毕，即 T3：
var t3 = timing.loadEventEnd - timing.navigationStart;
```





# 參考資料
- [30 天學會 Web 前端效能優化 系列](https://ithelp.ithome.com.tw/users/20091377/ironman/781)
- [5 分钟撸一个前端性能监控工具](https://mp.weixin.qq.com/s/BKtt2ns5CQ752lAF3Kgt7g)
- [前端性能监控：window.performance](https://juejin.im/entry/58ba9cb5128fe100643da2cc)
