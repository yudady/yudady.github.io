---
title: windows-命令
categories: tools
tags: ["windows"]
date: 2022-04-10 09:15
modified: 2022-04-10 09:15
---

# windows-命令

## windows 11 拖放檔案無法執行
- 按Windows键+R,打开“运行”对话框：输入**regedit**，回车或确定。
    
- 依次找到以下键值**HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\policies\system**
    
- 右键“EnableLUA” 选“修改”。把值改成**0**，确定。然后重启一下机器就好了。


