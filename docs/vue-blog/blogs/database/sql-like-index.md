---
title: sql-like-index
author: tommy
tags:
  - database
categories:
  - database
toc: true
date: 2018-10-20 11:39:10
---

# 簡介

> sql where 條件有like，需要走索引

<!--more-->
# 內容

```sql
select * from table where columnName like 'keyword%'  //只有這一條會走索引
select * from table where columnName like '%keyword%'  //無法走索引
select * from table where columnName like '%keyword'  // 如果index是reverse，這條會生效

```
> 第3條改寫locate，可走索引
```sql
select * from table where columnName locate('keyword' , table.field) > 0  // 返回keyword第一次出现的index位置

select * from table where columnName and reverse(table.field) like reverse('%keyword')  
```


# 參考資料


