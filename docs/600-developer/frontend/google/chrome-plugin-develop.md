---
title: chrome.plugin開發
author: tommy
tags:
  - chrome
categories:
  - tools
toc: true
date: 2018-10-01 14:14:58
---

# 簡介
自制一个chrome的plugin来自动输入账号密码自动登入表单

plugin: html + css + js

```
plugin/
  manifest.json // plugin 主要设定档
  app.css
  icon.png
  jquery.1.8.3.js
  popup.html 
  popup.js
```

<!--more-->
# 內容

[源码](https://github.com/yudady/chrome-extensions-zonvan)

## manifest.json
```
{
  "manifest_version": 2, //版本号
  "name": "超级账号", 
  "description": "超级账号",
  "version": "1.0.0",
  "icons": {
    "16": "icon.png",
    "48": "icon.png",
    "128": "icon.png"
  },
  "browser_action": {
    "default_icon": "icon.png",
    "default_popup": "popup.html" // 页面
  },
  "permissions": [
    "activeTab"
  ],
  "content_scripts": [{
    "matches": [
      "http://*/*", "https://*/*"
    ],
    "js": [
      "jquery.1.8.3.min.js" //可以汇入额外的js，当前看到的页面，不是popup.html
    ]
  }]
}
```

##　popup.html
```
<!doctype html>
<html xmlns="http://www.w3.org/1999/xhtml" style="width:200px;">

<head>
  <title>zonvan</title>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
</head>

<body id="body">
  <button id="myPay">超级账号</button><br />
  <button id="rd">rd</button><br />
  <button id="myPayTest">測試機myPay</button><br />
  <button id="myPayCenterTest">測試機myPayCenter</button><br />
  <button id="myCenterTest">測試機myCenter</button><br />
  <script src="./jquery.1.8.3.min.js"></script>
  <script src="./popup.js"></script>
</body>

</html>
```

## popup.js
```
chrome.tabs.getSelected(null, (tab) => {
    var tabId = tab.id; // 当前tab的id
    var sendUserNamePassword = (fun) => chrome.tabs.executeScript(tabId, fun);

    // 抓取 popup.html的Dom 加入 click listener ，执行 `chrome.tabs.executeScript(tabId, fun)` fun是当前打开页面的js
    // 下面是设定 账号 密码 验证码 然后点击btn，有汇入jquery所以可以直接用jquery抓取元素
    document.getElementById('myPay').addEventListener('click', () => {
        "use strict";
        sendUserNamePassword({
            code: 'document.getElementById("name").value = "zonpay";document.getElementById("pwd").value = "0221mypay@zonvan";document.getElementById("code").value = "c";document.getElementById("AccountDoLoginBtn").click(); '
        })
    });
    document.getElementById('rd').addEventListener('click', () => {
        "use strict";
        sendUserNamePassword({
            code: 'document.getElementById("name").value = "mypay";document.getElementById("pwd").value = "mypay@zonvan";document.getElementById("AccountDoLoginBtn").click(); '
        })
    });
    document.getElementById('myPayTest').addEventListener('click', () => {
        "use strict";
        sendUserNamePassword({
            code: 'document.getElementById("name").value = "zonvan";document.getElementById("pwd").value = "Zonvan123";document.getElementById("code").value = "c";document.getElementById("AccountDoLoginBtn").click(); '
        })
    });
    document.getElementById('myPayCenterTest').addEventListener('click', () => {
        "use strict";
        sendUserNamePassword({
            code: 'document.getElementById("name").value = "zonvan";document.getElementById("pwd").value = "Zonvan123";document.getElementById("AccountDoLoginBtn").click(); '
        })
    });


    // 使用jquery
    $("#myCenterTest").on('click', function () {
        chrome.tabs.executeScript(tabId, {
            code: 'document.getElementById("name").value = "mycenter";document.getElementById("pwd").value = "Mycenter123";document.getElementById("code").value = "c";document.getElementById("AccountDoLoginBtn").click(); '
        });

    })

});
```


# 參考資料
[google](http://www.google.com)

