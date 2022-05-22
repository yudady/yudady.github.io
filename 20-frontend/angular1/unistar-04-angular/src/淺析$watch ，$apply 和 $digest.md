# 淺析$watch ，$apply 和 $digest （Angular篇）

[$watch ，$apply 和 $digest](https://www.itdaan.com/tw/76549d406f14aa904f4ce5b5b02f2658)


$apply
$apply()函數可以使事件進入到Angular的執行環境中執行。那么問題來了，什么時候該用$apply()，什么時候不該用呢？

Angular提供的可用於視圖中的任意指令（比如ng-click、ng-keypress）、Angular內置的服務（比如$http、$timeout等），使用的時候都會自動調用$apply()。所以，我們不用再去手動調用$apply()了；

當我們手動處理事件，使用第三方框架（比如jQuery、Facebook API），或者調用setTimeout()，他們並沒有調用$apply()，所以事件不能在angular執行環境中執行，$digest循環無法檢測到事件中對視圖數據的更改，視圖無法更新。這個時候，我們就需要手動調用$apply()；


