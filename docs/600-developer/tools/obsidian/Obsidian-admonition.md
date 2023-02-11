---
title: obsidian-美化提示方塊
categories: tools
tags: ["obsidian"]
date: 2022-04-09 16:32
modified: 2022-04-09 16:59
---

# obsidian-美化提示方塊

## 美化提示方塊的外掛
[github admonition](https://github.com/valentine195/obsidian-admonition)



## Ad
````
```ad-<type> # Admonition type. See below for a list of available types.
title:                  # Admonition title.
collapse:               # Create a collapsible admonition.
icon:                   # Override the icon.
color:                  # Override the color.
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla et euismod nulla.
```
````



## Ad-note
````
```ad-note
title: Title
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla et euismod nulla.
```
````


## Title

```ad-question
title: Title
collapse: close
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla et euismod nulla.
```


## Bug
```ad-bug
title: Title
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla et euismod nulla.
```

## Note
`````ad-note
title: Nested Admonitions
collapse: open

Hello!

````ad-note
title: This admonition is nested.
This is a nested admonition!

```ad-warning
title: This admonition is closed.
collapse: close

Hello!
Hello!
Hello!
Hello!
```

````

This is in the original admonition.
`````

## Media-Extended：嵌入多媒體檔案的簡單方法
![button](https://www.youtube.com/watch?v=pMDTksChihE)


```button  
name Toggle spellcheck  
type comand  
action Toggle spellcheck  
color red  
```  
^button-spellcheck







