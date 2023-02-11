---
title: Elasticsearch
tags: []
aliases: [Elasticsearch]
created_date: 2023-02-04 10:15
updated_date: 2023-02-11 17:12
---

# Elasticsearch

[Java面试题：Elasticsearch专场（持续更新中......）_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1xY4y1B7rf/?spm_id_from=333.337.search-card.all.click&vd_source=6bd04a20c72eb5cca642210346af7081)

## Elasticsearch 是什麼

index document fields

node shard

|  RDBMS   | Elasticsearch  |
|  ----  | ----  |
| Table  | Index |
| Row  | Document |
| Column  | Field |
| Schema  | Mapping |
| SQL  | DSL |

### 概念

java 開發，基於 lucene 搜索 聚合 分析 存儲，非關係文檔數據庫

### 特點

高性能 高可用 易擴展 易維護

結構化 非結構化 地理位置

### mapping 

文檔 json like mysql field

`keyword` 精確查詢，不分詞

`text` 分詞，到排索引

映射：手動，dynamic 

### 全文檢索

分詞檢索

相關度

### ES support search

DSL

query string => kibana dev tool

### term match

term : "iphone 手機 " 當成一個詞語

match : 分詞

-------

6.MySQL（B+Trees）为什么不适合做全文检索

7.Elasticsearch 前言

8.倒排索引深入骨髓

9.Elasticsearch 的写入原理

10.读写性能调优

11.ES 的节点类型

12.搜索引擎和 ES

13.面试技巧
