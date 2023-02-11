---
title: mysql8-免安裝
author: tommy
tags:
  - mysql
categories:
  - database
toc: true
date: 2018-10-24 17:38:06
---

# 簡介

mysql-8.0.13-winx64，免安裝，使用bat啟動

<!--more-->
# 內容

## 無服務狀態

- [Windows (x86, 64-bit), ZIP Archive](https://downloads.mysql.com/archives/community/)
- 添加`my.ini`
- 添加`run.bat`
- 進入bin目錄下面
- 執行初始化DB `mysqld --defaults-file=d:/my.ini --initialize-insecure --console`
- 使用cmd進入mysql `mysql -uroot -p`，密碼是空白
- 執行命令 `use mysql;`
- 執行命令 `SELECT User, Host FROM mysql.user;`
- 執行命令 `update mysql.user set host='%' where User='root' and Host='localhost';`
- 執行命令 `FLUSH PRIVILEGES;`
- 執行命令 `ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root';`
- 重起mysql


## my.ini

```ini
[client]
port=3306
 
[mysql]
 
[mysqld]
port=3306
#默认密码插件，8.0默认使用caching_sha2_password,为兼容老版本，改用mysql_native_password
default_authentication_plugin=mysql_native_password
basedir=D:\JavaTool\database\mysql-8.0.13-winx64
datadir=D:\JavaTool\database\mysql-8.0.13-winx64\db

 
general-log=0
general_log_file=D:/JavaTool/database/mysql-8.0.13-winx64/mysql-general.log
slow-query-log=1
slow_query_log_file=D:/JavaTool/database/mysql-8.0.13-winx64/mysql-slow.log
log-error=D:/JavaTool/database/mysql-8.0.13-winx64/mysql-error.log
 
server-id=1
max_connections=151
table_open_cache=2000
tmp_table_size=16M
thread_cache_size=10
 
# *** INNODB Specific options ***
# 事务日志刷新（fsync）到磁盘的行为，1每次提交都写到磁盘，0约每秒写一次，2每次都写，但速度约每秒一次
innodb_flush_log_at_trx_commit=1
 
# 日志数据缓冲，大了没有意义
innodb_log_buffer_size=1M
 
# 使用缓冲池来缓存索引和行数据
innodb_buffer_pool_size=64M
 
# 日志组中每个日志文件的大小。应该将日志文件的大小设置为 innodb_buffer_pool_size 的25％-100％，以避免日志文件覆盖时出现不必要的缓冲池刷新活动。
innodb_log_file_size=48M
 
# 内核允许的线程数, cpu核数*2+1
innodb_thread_concurrency=9
 
# 系统表空间文件变满时自动扩展的大小(in MB) 
innodb_autoextend_increment=32
 
# 缓冲池划分的区域数量。 对于缓冲池在数GB范围内的系统，通过在不同线程读取和写入缓存页面时减少争用，将缓冲池划分为不同的实例可以提高并发性。
innodb_buffer_pool_instances=8
 
# 每个线程获得执行后的最大计数次数
innodb_concurrency_tickets=5000
 
# 指定插入到旧子列表中的块在移动到新子列表之前必须在其第一次访问后保留多长时间（以毫秒为单位）。
innodb_old_blocks_time=1000
 
# 一次可以保持打开的.ibd文件的最大数量，最小为10
innodb_open_files=300
 
# 启用时，InnoDB会在元数据语句中更新统计信息
innodb_stats_on_metadata=0
 
# 当启用时，InnoDB将每个新创建的表的数据和索引存储在单独的.ibd文件中，而不是存储在系统表空间中。
innodb_file_per_table=1
 
# 校验和算法，0表示crc32，1表示strict_crc32，1表示innodb，3表示strict_innodb，4表示无，5表示strict_none。
innodb_checksum_algorithm=0
 
 
 
# 表示在MySQL短暂停止回答新请求之前的这段短时间内可以堆叠多少个请求。
back_log=80
 
# 如果将其设置为非零值，则每flush_time关闭所有表以释放资源并将未刷新的数据同步到磁盘。
flush_time=0
 
# 用于普通索引扫描，范围索引扫描和不使用索引并因此执行全表扫描的连接的最小缓冲区大小。
join_buffer_size=512K
 
# 一个数据包或任何生成或中间字符串的最大大小
max_allowed_packet=4M
 
# 如果超过这个数量，来自主机的多个连续连接请求在没有成功连接的情况下被中断，则服务器阻止该主机执行进一步的连接。
max_connect_errors=100
 
# 打开文件数量，如遇到 "Too many open files" 错误，增大此值。
open_files_limit=4161
 
# 如果在SHOW GLOBAL STATUS输出中每秒看到多个sort_merge_passes，则可以考虑增加此值以加快ORDER BY或GROUP BY操作的速度，这些操作无法通过查询优化或索引编制得到改进。
sort_buffer_size=512K
 
# 可以存储在定义缓存中的表定义数（from .frm files）。与普通表缓存不同，表定义缓存占用更少的空间并且不使用文件描述符。 最小值和默认值都是400
table_definition_cache=1400
 
# 指定基于行的二进制日志事件的最大大小（以字节为单位）。 如果可能的话，行被分成小于这个尺寸的事件。 该值应该是256的倍数。
binlog_row_event_max_size=8K
 
# 如值大于0，将 master.info 文件同步到磁盘（在每个sync_master_info事件之后使用 fdatasync（））
sync_master_info=10000
 
# 如值大于0，将其中继日志同步到磁盘（在每个sync_relay_log写入中继日志后使用 fdatasync（））
sync_relay_log=10000
 
# 如值大于0，将 relay-log.info 文件同步到磁盘（在每个sync_relay_log_info事务之后使用 fdatasync（））
sync_relay_log_info=10000
 
[mysqld-8.0]
sql_mode=TRADITIONAL
```



## run.bat
```bat
@echo off 
cd "%cd%"
%~dp0bin/mysqld --defaults-file=%~dp0my.ini --console 



rem %~dp0bin/mysqld --defaults-file=%~dp0my.ini --initialize-insecure --console 


rem mysqld.exe -u root --skip-grant-tables
rem use mysql;
rem SELECT User, Host FROM mysql.user;
rem update mysql.user set host='%' where User='root' and Host='localhost';
rem FLUSH PRIVILEGES;
rem ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root';
```


# 參考資料


