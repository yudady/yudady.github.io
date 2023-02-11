---
title: intellij
tags: []
aliases: [intellij]
created_date: 2022-11-12 08:43
updated_date: 2023-02-11 18:00
---

# intellij

## intellij-plugin

- TranslationPlugin
- Grep Console  :  .*(\Q[LOG]\E).*
- Lombok plugin
- Maven Helper
- Save Actions
- VisualVM Launcher
- String manipulation
- key promoter X
- Close Editor Tabs Left Right
- Material Theme UI
- .​env files support
- Atom Material Icons
- CheckStyle-IDEA
- Cloud Code
- Diagrams.​net Integration
- GitToolBox
- Indent Rainbow
- Quokka
- Return Highlighter
- SequenceDiagram
- IntelliJ Lombok plugin

## intellij code setting

![](images/20190131141859.png)

![](images/codeAlign.gif)

## intellij-mouse-font-size

> ctrl + 滑鼠滾輪

![](images/20190504133534.gif)

![](images/20190504133534.png)

## intellij-note-formater

> 修改 2 個地方

![](images/20190601100637.png)

![](images/20190601100637.gif)

## intellij-save-action

## 修改後 tab 會有 星號

![](images/20190810154650.png)

## 修改自動保存時間

![](images/20190810154748.png)

## intellij-settings

## 消除 intellij `import *`

![](images/20190303113813.png)

## Intellij codeStyle

![codeStyle](images/20190303114751.png)

![codeStyle](images/20190303115810.png)

![codeStyle](images/20190303115631.png)

## Intellij Java Properties File Transfer

> 編碼自動轉換
![](images/intellijSettings-202204301028.png)

## 把 Intellij 設定檔同步到 Github，可以在其他地方匯入

![](images/20181004090549.png)

- 看到一個同步的聯結網址
- github 創建一個目錄，貼上去
- 要求取得 "Personal access tokens"

![](images/20181004093455.png)

![](images/20181004093549.png)

![](images/20181004093632.png)

![](images/20181004094350.png)

- https://github.com/yudady/intellijSettings.git
- 開始同步數據，你懂得！ 

![](images/20181004094744.png)

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

## intellij-key

紀錄熱鍵

### Switching Between Open Projects

> Ctrl+Alt+]
> Ctrl+Alt+[

### 自動補全返回值

> ctrl + alt + v

### 在行尾增加分號

> Ctrl + Shift + Enter - 本身的含義是自動完成，如果需要的話，會在行尾添加分號或是大括號；

### 打開繼承樹

> ctrl + alt + U

### 如何找目錄

> ctrl + shift + n 後，使用/

### 自動翻譯選中文字

> ctrl + shift + Y

### 尋找頁面方法

> ctrl + F12

### 代碼提示不區分大小寫

> 將 Case sensitive completion 設置為 None 就可以了

### 開啟自動 import 包的功能

### 去掉導航欄

> 使用 alt+v，然後去掉 Navigation bar 即可
