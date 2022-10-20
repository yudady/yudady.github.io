

spring:
datasource:
url: jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useLegacyDatetimeCode=false
username: root
password:


useTimezone = true这个参数才可以转换时区
useLegacyDatetimeCode=true





````shell

解决方法

步骤一：修改java中的时区为东八区

// serverTimezone可以设置为北京时间GMT%2B8、上海时间Asia/Shanghai或者香港时间Hongkong

url: jdbc:mysql://localhost:3306/test?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=true

步骤二：修改MySQL数据库的时区为东八区

// 方法一：使用命令(优点：不需要重启MySQL服务，缺点：一旦MySQL服务被重启，设置就会消失)

mysql> set time_zone = '+8:00';

mysql> set global time_zone = '+8:00';

// 方法二：修改my.ini配置文件(优点：永久保存设置，缺点：需重启MySQL服务)

[mysqld]

// 设置默认时区

default-time_zone='+8:00'







3.1 MySQL 內部時間不是北京時間

遇到這類問題，首先檢查下系統時間及時區是否正確，然後看下 MySQL 的 time_zone，建議將 time_zone 改為'+8:00'。

3.2 Java 程式存取的時間與資料庫中的時間相差 8 小時

出現此問題的原因大概率是程式時區與資料庫時區不一致導致的。我們可以檢查下兩邊的時區，如果想統一採用北京時間，則可以在 jdbc 連線串中增加 serverTimezone=Asia/Shanghai，並且 MySQL 方面也可以將 time_zone 改為'+8:00'。

3.3 程式時間與資料庫時間相差 13 小時或 14 小時

如果說相差 8 小時不夠讓人驚訝，那相差 13 小時可能會讓很多人摸不著頭腦。出現這個問題的原因是 JDBC 與 MySQL 對 “CST” 時區協商不一致。因為 CST 時區是一個很混亂的時區，有四種含義：

美國中部時間 Central Standard Time (USA) UTC-05:00 或 UTC-06:00
澳大利亞中部時間 Central Standard Time (Australia) UTC+09:30
中國標準時 China Standard Time UTC+08:00
古巴標準時 Cuba Standard Time UTC-04:00
MySQL 中，如果 time_zone 為預設的 SYSTEM 值，則時區會繼承為系統時區 CST，MySQL 內部將其認為是 UTC+08:00。而 jdbc 會將 CST 認為是美國中部時間，這就導致會相差 13 小時，如果處在冬令時還會相差 14 個小時。

解決此問題的方法也很簡單，我們可以明確指定 MySQL 資料庫的時區，不使用引發誤解的 CST，可以將 time_zone 改為'+8:00'，同時 jdbc 連線串中也可以增加 serverTimezone=Asia/Shanghai。

3.4 如何避免出現時區問題

如何避免上述時區問題，可能你心裡也有了些方法，簡要總結幾點如下：

首先保證系統時區準確。
jdbc 連線串中指定時區，並與資料庫時區一致。
time_zone 引數建議設定為'+8:00'，不使用容易誤解的 CST。
各環境資料庫例項時區引數保持相同。
````