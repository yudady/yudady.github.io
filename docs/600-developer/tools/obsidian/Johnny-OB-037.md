---
title: Johnny-OB-037
categories: tools
tags: ["obsidian"]
date: 2022-01-09
modified: 2022-04-09 15:58
aliases: Johnny学OB 第37集 Mermaid做流程图
---

```ad-note
 title: [Johnny学OB 第37集 教你几招 - 如何在Obsidian中更好的使用Mermaid做流程图 Obsidian教程_哔哩哔哩_bilibili]
 collapse: open
 
 ![](https://www.bilibili.com/video/BV1eU4y1F7jn?spm_id_from=333.999.0.0)
```


```mermaid 
sequenceDiagram 

Alice->>+John: Hello John, how are you? 
Alice->>+John: John, can you hear me? 
John-->>-Alice: Hi Alice, I can hear you! 
John-->>-Alice: I feel great! 


```

 - [Markdown 高级技巧 | 菜鸟教程](https://www.runoob.com/markdown/md-advance.html?tdsourcetag=s_pctim_aiomsg)
 - [github mermaid](https://github.com/mermaid-js/mermaid/blob/develop/README.zh-CN.md)

## **1️⃣横向流程图源码格式：**
```mermaid
graph LR

A[方形]
B(圆角)
C{条件a}
D[结果1]
E[结果2]
F[横向流程图]


A --> B
B --> C
C -- a=1 --> D
C -- a=2 --> E
F


		
```


## **2️⃣竖向流程图源码格式：**
```mermaid
graph TD

A[方形]
B(圆角)
C{条件a}
D[结果1]
E[结果2]
F[竖向流程图]


A --> B
B --> C
C --> |a=1| D
C --> |a=2| E
F

```


## **时序图**
```mermaid
sequenceDiagram
Alice->>John: Hello John, how are you?
loop Healthcheck
    John->>John: Fight against hypochondria
end
Note right of John: Rational thoughts!
John-->>Alice: Great!
John->>Bob: How about you?
Bob-->>John: Jolly good!
```


## **甘特图**
```mermaid
gantt
section Section
Completed :done,    des1, 2014-01-06,2014-01-08
Active        :active,  des2, 2014-01-07, 3d
Parallel 1   :         des3, after des1, 1d
Parallel 2   :         des4, after des1, 1d
Parallel 3   :         des5, after des3, 1d
Parallel 4   :         des6, after des4, 1d
```