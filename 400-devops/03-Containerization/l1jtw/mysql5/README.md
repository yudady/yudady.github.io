# [mysql8-docker-container](https://github.com/lagoon-io/mysql8-docker-container)


## 前期工作 查看當前mysql字符集情況
> mysql> SHOW VARIABLES LIKE 'character_set_%' ;

```mysql
mysql> SHOW VARIABLES LIKE 'character_set_%';
+--------------------------+--------------------------------+
| Variable_name            | Value                          |
+--------------------------+--------------------------------+
| character_set_client     | latin1                         |
| character_set_connection | latin1                         |
| character_set_database   | utf8mb4                        |
| character_set_filesystem | binary                         |
| character_set_results    | latin1                         |
| character_set_server     | utf8mb4                        |
| character_set_system     | utf8mb3                        |
| character_sets_dir       | /usr/share/mysql-8.0/charsets/ |
+--------------------------+--------------------------------+
8 rows in set (0.01 sec)
```
 
## 使用Dockerfile複製`my.cnf`設定檔

```mysql
Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> SHOW VARIABLES LIKE 'character_set_%';
+--------------------------+--------------------------------+
| Variable_name            | Value                          |
+--------------------------+--------------------------------+
| character_set_client     | utf8mb4                        |
| character_set_connection | utf8mb4                        |
| character_set_database   | utf8mb4                        |
| character_set_filesystem | binary                         |
| character_set_results    | utf8mb4                        |
| character_set_server     | utf8mb4                        |
| character_set_system     | utf8mb3                        |
| character_sets_dir       | /usr/share/mysql-8.0/charsets/ |
+--------------------------+--------------------------------+
8 rows in set (0.01 sec)

mysql> 

```


## init-database.sh  不能使用windows換行符
```shell
2022-01-28T04:16:12.394967800Z 2022-01-28 04:16:12+00:00 [Note] [Entrypoint]: Entrypoint script for MySQL Server 8.0.25-1debian10 started.
2022-01-28T04:16:12.505575300Z 2022-01-28 04:16:12+00:00 [Note] [Entrypoint]: Initializing database files
2022-01-28T04:17:22.297784100Z 2022-01-28 04:17:22+00:00 [Note] [Entrypoint]: Database files initialized
2022-01-28T04:17:22.301261000Z 2022-01-28 04:17:22+00:00 [Note] [Entrypoint]: Starting temporary server
2022-01-28T04:17:25.171390300Z 2022-01-28 04:17:25+00:00 [Note] [Entrypoint]: Temporary server started.
2022-01-28T04:17:29.406973000Z Warning: Unable to load '/usr/share/zoneinfo/iso3166.tab' as time zone. Skipping it.
2022-01-28T04:17:29.407030100Z Warning: Unable to load '/usr/share/zoneinfo/leap-seconds.list' as time zone. Skipping it.
2022-01-28T04:17:35.426838900Z Warning: Unable to load '/usr/share/zoneinfo/zone.tab' as time zone. Skipping it.
2022-01-28T04:17:35.426907800Z Warning: Unable to load '/usr/share/zoneinfo/zone1970.tab' as time zone. Skipping it.
2022-01-28T04:17:36.260814400Z 2022-01-28 04:17:36+00:00 [Note] [Entrypoint]: Creating database sandbox
2022-01-28T04:17:36.576428700Z 2022-01-28 04:17:36+00:00 [Note] [Entrypoint]: Creating user tommy
2022-01-28T04:17:36.841791300Z 2022-01-28 04:17:36+00:00 [Note] [Entrypoint]: Giving user tommy access to schema sandbox
2022-01-28T04:17:37.126378100Z 
2022-01-28T04:17:37.128070600Z 2022-01-28 04:17:37+00:00 [Note] [Entrypoint]: /usr/local/bin/docker-entrypoint.sh: running /docker-entrypoint-initdb.d/000-create-databases.sql
2022-01-28T04:17:37.684906100Z 
2022-01-28T04:17:37.684973700Z 
2022-01-28T04:17:37.686357700Z 2022-01-28 04:17:37+00:00 [Note] [Entrypoint]: /usr/local/bin/docker-entrypoint.sh: running /docker-entrypoint-initdb.d/001-create-tables.sql
2022-01-28T04:17:38.402788400Z Database
2022-01-28T04:17:38.402855100Z information_schema
2022-01-28T04:17:38.402869200Z mysql
2022-01-28T04:17:38.402875500Z order
2022-01-28T04:17:38.402881200Z performance_schema
2022-01-28T04:17:38.402886900Z sandbox
2022-01-28T04:17:38.402893600Z sys
2022-01-28T04:17:38.402900400Z user
2022-01-28T04:17:38.404956200Z 
2022-01-28T04:17:38.405019600Z 
2022-01-28T04:17:38.408771500Z 2022-01-28 04:17:38+00:00 [Note] [Entrypoint]: /usr/local/bin/docker-entrypoint.sh: running /docker-entrypoint-initdb.d/init-database.sh
2022-01-28T04:17:38.428993600Z mysql: [Warning] Using a password on the command line interface can be insecure.
2022-01-28T04:17:38.513892600Z mysql: [Warning] Using a password on the command line interface can be insecure.
2022-01-28T04:17:39.950200000Z Database
2022-01-28T04:17:39.950458900Z information_schema
2022-01-28T04:17:39.950499500Z mysql
2022-01-28T04:17:39.950523300Z order
2022-01-28T04:17:39.950544900Z performance_schema
2022-01-28T04:17:39.950570000Z sandbox
2022-01-28T04:17:39.950840900Z sys
2022-01-28T04:17:39.950873300Z user
2022-01-28T04:17:39.959689100Z 
2022-01-28T04:17:39.965296200Z 2022-01-28 04:17:39+00:00 [Note] [Entrypoint]: Stopping temporary server
2022-01-28T04:17:53.002630100Z 2022-01-28 04:17:53+00:00 [Note] [Entrypoint]: Temporary server stopped
2022-01-28T04:17:53.002829000Z 
2022-01-28T04:17:53.008730800Z 2022-01-28 04:17:53+00:00 [Note] [Entrypoint]: MySQL init process done. Ready for start up.
2022-01-28T04:17:53.008908900Z 


```

