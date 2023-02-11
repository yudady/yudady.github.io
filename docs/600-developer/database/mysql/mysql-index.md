---
title: mysql-index
author: tommy
tags:
  - mysql
categories:
  - database
toc: true
date: 2018-10-08 15:07:33
---

# 簡介

> mysql執行計劃
> explain select * from `table` 

<!--more-->
# 內容

## type

- ALL   掃描全表數據
- index 遍曆索引
- range 索引範圍查找
- index_subquery 在子查詢中使用 ref
- unique_subquery 在子查詢中使用 eq_ref
- ref_or_null 對Null進行索引的優化的 ref
- fulltext 使用全文索引
- ref   使用非唯一索引查找數據
- eq_ref 在join查詢中使用PRIMARY KEYorUNIQUE NOT NULL索引關聯。
- const 使用主鍵或者唯一索引，且匹配的結果只有一條記錄。
- system const 連接類型的特例，查詢的表為系統表。

> 除了ALL之外其他都有使用索引


## 慢查詢日誌
```sql
show variables  like '%slow_query_log%';

// 暫時生效
set global slow_query_log=1;// 開啟慢查詢日誌

// 修改my.cnf
slow_query_log =1
```
## Error Log
```sql
show variables like 'log_error';
```

# 參考資料


