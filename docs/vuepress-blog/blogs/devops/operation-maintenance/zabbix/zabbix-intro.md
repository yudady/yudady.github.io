---
title: zabbix
author: tommy
tags:
  - zabbix
categories:
  - devops
toc: true
date: 2018-10-07 20:41:21
---

# 簡介

> zabbix數據庫資料表

<!--more-->
# 內容



actions表：

actions表记录了当触发器触发时，需要采用的动作。可以通过desc actions;来自行查看表结构

alerts表：

alerts 表保存了历史的报警事件。

config表：

config表保存了全局的参数

functions表：

function 表是非常重要的一个表了，记录了trigger中使用的表达式，例如max、last、nodata等函数。

graphs表：

graphs 表包含了用户定义的图表信息。

graphs_items表：

graphs_items 保存了属于某个图表的所有的监控项信息。

groups表：

groups 保存了组名和组的ID 。

history 、history_str、history_log 、history_uint_sync等：

这部分表都差不多，唯一不同的是保存的数据类型。存储着不同类型item的历史数据，最终1小时或者1天等短时间的绘图数据都从其中获取。

trends、trends_uint表：

保留历史数据用的，不过是趋势数据。储存着不同类型item的历史趋势数据，每隔一小时从历史数据中统计一次，并计算统计区间的平均值、最值。长时间区间的绘图数据的数据源。

hosts表：

hosts 非常重要，保存了每个agent、proxy等的IP 、hostid、状态、IPMI等信息， 几乎是记录了一台设备的所有的信息。其他的表一般都时关联hostid的。

hosts_groups表：

hosts_groups 保存了host（主机）与host groups（主机组）的关联关系。

items表：

items 表保存了采集项的信息。字段说明，itemid是每个绘图项目唯一标识，hostid每个主机的标识，name每个item的名字，delay数据采集间隔，history历史数据保存时间，status标识item的状态(0表示正常显示的item)，units保存item的单位。

media表：

media 保存了某个用户的media配置项，即对应的告警方式。

media_type表：

media_type 表与media 表不同的是media_type 记录了某个告警方式对应的执行脚本，注意路径只是相对路径。media 与media_type 通过mediatypeid 键关联。

profiles表：

profiles 表保存了用户的一些配置项。

rights表：

rights 表保存了用户组的权限信息，zabbix的权限一直也是我理不太清的地方， 其实这个表里面有详细的记录。

screeens表：

screeens 表保存了用户定义的图片。

sessions表：

保存了每个用户的sessions,在登陆、注销的时候均会操作该张表的。

triggers表：

保存了trigger的所有信息。

trigger_depends表：

trigger_depends 保存了trigger的依赖关系。



# 參考資料


