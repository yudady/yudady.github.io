---
title: 把eclipse的plugin抽取出來
author: tommy
tags:
  - eclipse
categories:
  - tools
toc: true
date: 2018-10-05 11:53:43
---


# 簡介
> 把eclipse的plugin抽取出來，丟進dropins


<!--more-->
# 內容

- 需要下載zip板的eclipse或是STS
- 先用eclipse marketplace安裝`console Grep`
- 進入eclipse的目錄中找出剛剛安裝的檔案，全部複製出來，暫時存放起來，注意目錄結構
- 移除用market安裝的`console Grep`
- 關閉eclipse
- 把桌面的 `console Grep` 貼到eclipse的dropins下面
- 啟動

```
console/   =>這個是你安裝plugin的名稱，自己取，沒有限制
    eclipse/
            features/   帖上features檔案
            plugins/    帖上plugins檔案
```


```
eclipse/
    features          =>找出 console Grep 檔案
    plugins           =>找出 console Grep 檔案
    dropins/
         console/eclipse/
            features/   帖上features檔案
            plugins/    帖上plugins檔案
    
```


{% youtube MoJTHUjcRno %}

# 參考資料
