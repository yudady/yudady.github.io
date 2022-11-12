---
title: browerHotReload
author: tommy
tags:
  - node
categories:
  - tools
toc: true
date: 2018-10-04 11:03:30
---

# 簡介

- 開發jsp自動重新reload頁面
- browser-sync（代理）
<!--more-->
# 內容

```cmd
cd C:\project\oneShop\code\web

browser-sync start --proxy "localhost:8080/oneShop/" --files "**/*.css, **/*.js, **/*.jsp"
```

> 打開 "http://localhost:3000/oneShop" ， 修改前端會自動reload

# 參考資料


