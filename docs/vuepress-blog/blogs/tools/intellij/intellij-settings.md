---
title: 設定 Intellij
author: tommy
categories: tools
tags: ["intellij"]
date: 2018-10-04 09:27:17
modified: 2022-05-08 15:21
---

# 設定 Intellij


## 消除intellij `import *`

![](../images/20190303113813.png)



## Intellij codeStyle


![codeStyle](../images/20190303114751.png)

![codeStyle](../images/20190303115810.png)

![codeStyle](../images/20190303115631.png)


## Intellij Java Properties File Transfer
> 編碼自動轉換
![](../images/intellijSettings-202204301028.png)




## 把Intellij設定檔同步到Github，可以在其他地方匯入

![](../images/20181004090549.png)

- 看到一個同步的聯結網址
- github創建一個目錄，貼上去
- 要求取得"Personal access tokens"

![](../images/20181004093455.png)
![](../images/20181004093549.png)
![](../images/20181004093632.png)
![](../images/20181004094350.png)

- https://github.com/yudady/intellijSettings.git
- 開始同步數據，你懂得！ 

![](../images/20181004094744.png)


# 變更設定目錄
> idea.properties

```java
#---------------------------------------------------------------------
# Uncomment this option if you want to customize path to IDE config folder. Make sure you're using forward slashes.
#---------------------------------------------------------------------
# idea.config.path=${user.home}/.IntelliJIdea/config
idea.config.path=D:/install/JetBrains/ideaIU-2018.2.2/.IntelliJIdea2018.2/config
#---------------------------------------------------------------------
# Uncomment this option if you want to customize path to IDE system folder. Make sure you're using forward slashes.
#---------------------------------------------------------------------
# idea.system.path=${user.home}/.IntelliJIdea/system
idea.system.path=D:/install/JetBrains/ideaIU-2018.2.2/.IntelliJIdea2018.2/system
#---------------------------------------------------------------------

```

# 參考資料


