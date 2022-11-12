---
title: learn-powershell01
author: tommy
tags:
  - powershell
categories:
  - tools
toc: true
date: 2018-10-11 10:13:36
---

# 簡介



<!--more-->
# 內容

```
PS C:\Users\mypay> Get-Process -Name java

Handles  NPM(K)    PM(K)      WS(K) VM(M)   CPU(s)     Id ProcessName
-------  ------    -----      ----- -----   ------     -- -----------
    312      24   407908     265896  1348            2796 java
    522     142   331920     134820  1338            4344 java
    443      68   381964     228504  1345            4724 java


Get-Process -ID 4344 | Select-Object *


__NounName                 : Process
Name                       : java
Handles                    : 521
VM                         : 1402654720
WS                         : 138055680
PM                         : 339886080
NPM                        : 144208

---
Get-Process -ID 4344 | Format-List  Threads

Threads : {4276, 2932, 4340, 4868...}


---



$A = Get-Process -ID 4344
$A.StartInfo.Environment

$A.StartInfo.EnvironmentVariables | Format-List * 


---
get-process | Sort-Object -Property PM -Descending | Format-Table * -AutoSize

__NounName Name                 Handles         VM         WS         PM    NPM Path                                        
---------- ----                 -------         --         --         --    --- ----                                        
Process    Tomcat8                  957 1836093440  912228352 1055809536  82336                                             
Process    oracle                  1597 1910321152 -114892800  446918656 128376                                             
Process    java                     313 1413644288  272297984  417697792  24752                                             
Process    java                     533 1408450560  236490752  384319488 126816                                             
Process    java                     529 1400557568  138280960  339582976 147408                                             
Process    powershell_ise           574  906424320  142921728  118153216  57104 C:\windows\System32\WindowsPowerShell\v1....
Process    chrome                  1085  544501760  167329792   96591872  63312

---



```



# 參考資料


