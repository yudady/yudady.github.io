---
in:
updated: 2026-03-07 22:05
level:
stars:
link: null
aliases: ["從 Linux 機器使用堡壘主機連接至 Amazon RDS 資料庫執行個體", Linux-从堡垒机连接RDS]
created_date: 2025-02-24 17:30
updated_date: 2025-02-24 20:28
title: Linux-从堡垒机连接RDS
source: https://repost.aws/zh-Hant/knowledge-center/rds-connect-using-bastion-host-linux
author: ["[[AWS Official]]"]
published: 2020-09-24
created: 2025-03-22 13:21
description: "我有一個無法公開存取的 Amazon Relational Database Service (Amazon RDS) 資料庫執行個體。我想從我的 Linux/macOS 機器連接至它。如何使用堡壘主機連接至我的 RDS 資料庫執行個體?"
tags: ["堡垒机", Amazon RDS, clippings, Linux]
category: "技术"
summary: "使用堡垒机从Linux机器连接私有Amazon RDS实例"
tags:
  - status/active
  - area/distill
  - type/doc
---

我有一個無法公開存取的 Amazon Relational Database Service (Amazon RDS) 資料庫執行個體。我想從我的 Linux/macOS 機器連接至它。如何使用堡壘主機連接至我的 RDS 資料庫執行個體?

---

## 簡短描述

若要連接至私有 Amazon RDS 或 Amazon Aurora 資料庫執行個體,最佳實務是使用 [VPN](https://docs.aws.amazon.com/vpc/latest/userguide/vpn-connections.html) 或 [AWS Direct Connect](https://docs.aws.amazon.com/whitepapers/latest/aws-vpc-connectivity-options/aws-direct-connect-network-to-amazon.html)。如果您無法使用 VPN 或 AWS Direct Connect,則偏好選項是使用堡壘主機。您也可以使用此方法從 VPC 外部連接至 Aurora Serverless 和 RDS Proxy。此範例說明如何設定堡壘主機,以從 Linux/macOS 機器連接至您的 RDS 資料庫執行個體,即使 RDS 資料庫執行個體為私有亦同。

---

## 解決方法

1. [修改資料庫執行個體](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Overview.DBInstance.Modifying.html),將 Amazon RDS 資料庫執行個體設定為私有。將**可公開存取**參數設為**否**,而使用私有子網絡(即,路由表中沒有**網際網路閘道–igw**)。設定安全群組,以允許資料庫從所有 IP 連接(**5432**、**3306**)。

2. 在與資料庫執行個體相同的 VPC 中 [啟動最小可用的 EC2 執行個體](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/LaunchingAndUsingInstances.html)。將 Amazon Elastic Compute Cloud (Amazon EC2) 執行個體設定為可透過網際網路存取,使用公有子網路(即路由表中有**網際網路閘道–igw**)。設定安全群組以允許您嘗試連線之 Linux/macOS 機器的 IP。

3. 從您的 Linux/macOS 機器執行下列命令,以建立從您的機器連線的通道:

```bash
Syntax 1:
ssh -i <identity_file> -f -l <bastion-host-username> -L
<local-port-you-connect-to>:<rds-endpoint>:<rds:listening-port>
<bastion-host-public-ip> -v

Example Command:
ssh -i "private_key.pem" -f -l ec2-user -L 5432:172.31.39.62:5432  3.133.141.189 -v
```

執行上述命令 (SSH 通道) 時,您可以設定下列設定:

- 偵錯 1:到 **LOCALHOST:5432** 的本機連線轉送至遠端位址 **172.31.39.62:5432**
- 偵錯 1:在 **127.0.0.1** 連接埠 **5432** 上的本機轉送接聽。
- 偵錯 1:通道 **0**:新的 \[連接埠接聽程式\]
- 偵錯 1:在 **::1** 連接埠 **5432** 上的本機轉送接聽。

```bash
Syntax 2:
ssh -i "Private_key.pem" -f -N -L 5433:RDS_Instance_Endpoint:5432 ec2-user@EC2-Instance_Endpoint -v

Example Command:
ssh -i "private.pem" -f -N -L
5433:pg115.xxxx.us-east-2.rds.amazonaws.com:5432
ec2-user@ec2-xxxx-xxx9.us-east-2.compute.amazonaws.com -v
```

4. 現在 SSH 通道已經到位,您可以從本機 Linux/macOS 機器連接至資料庫執行個體。下列範例會連接至 PostgreSQL,但您也可以使用此方法連接至 MySQL 或您想要連線的任何其他引擎。

```bash
Syntax -

psql -hlocalhost -Upostgres -p<local-port-you-connect-to> -d postgres

-h = localhost

-U = the username present in the DB for connectivity

-p = 'local-port-you-connect-to' from the SSH Tunneling command

-d = Any DB, user wish to connect.

Example command -
a483e73d651f:.ssh rahul_saha$ psql -hlocalhost -Upostgres -p5432 -d postgres
Password for user postgres:
psql (12.1, server 11.5)
SSL connection (protocol: TLSv1.2, cipher: ECDHE-RSA-AES256-GCM-SHA384, bits: 256, compression: off)
Type "help" for help.

postgres=> \l
                                  List of databases
   Name    |  Owner   | Encoding |   Collate   |    Ctype    |   Access privileges
-----------+----------+----------+-------------+-------------+-----------------------
 postgres  | postgres | UTF8     | en_US.UTF-8 | en_US.UTF-8 |
 rdsadmin  | rdsadmin | UTF8     | en_US.UTF-8 | en_US.UTF-8 | rdsadmin=CTc/rdsadmin
 template0 | rdsadmin | UTF8     | en_US.UTF-8 | en_US.UTF-8 | =c/rdsadmin          +
           |          |          |             |             | rdsadmin=CTc/rdsadmin
 template1 | postgres | UTF8     | en_US.UTF-8 | en_US.UTF-8 | =c/postgres          +
           |          |          |             |             | postgres=CTc/postgres
(4 rows)

$ psql -hlocalhost -Upostgres -p -d postgres
$ mysql -h127.0.0.1 -uroot -p
```

**注意**:如果您在連接至資料庫執行個體時使用關鍵字 **localhost**,MySQL 會嘗試使用通訊端連線。在存取 MySQL 資料庫執行個體時,請務必使用主機名稱 127.0.0.1。如需詳細資訊,請參閱 [無法連接至 \[本機\] MySQL 伺服器](https://dev.mysql.com/doc/refman/8.0/en/can-not-connect-to-server.html) 的 MySQL 文件。

---

## aws-mysql

```bash
mysql -hdtg-mysql-test-2.cn8o8wwuc4lb.ap-southeast-1.rds.amazonaws.com -udtgMysqlTest -pnhXzDmmxvSdBB37VKuFU8NJdx7bjrw

ssh -i "/Users/tommy/Documents/work.nosync/yudady/g-work/dayooint.com/dtg-stage-key/DTG-TEST-IT.pem" -f -N -L \
3307:dtg-mysql-test-2.cn8o8wwuc4lb.ap-southeast-1.rds.amazonaws.com:3306 \
ubuntu@13.228.187.227 -v
```

連線:`jdbc:mysql://127.0.0.1:3307/xxpay`

---

## aws-mongosh

```bash
mongosh --tls --host dtg-dcdb-test.cluster-cn8o8wwuc4lb.ap-southeast-1.docdb.amazonaws.com:27017 --sslCAFile global-bundle.pem --username adminDTG --password HALE7e7eNDCMYYNV7Tfu3n4wxLYgRX

ssh -i "/Users/tommy/Documents/work.nosync/yudady/g-work/dayooint.com/dtg-stage-key/DTG-TEST-IT.pem" -f -N -L \
27018:dtg-dcdb-test.cluster-cn8o8wwuc4lb.ap-southeast-1.docdb.amazonaws.com:27017 \
ubuntu@13.228.187.227 -v
```

連線:

```bash
mongodb://adminDTG:HALE7e7eNDCMYYNV7Tfu3n4wxLYgRX@127.0.0.1:27018/?tls=true&tlsCAFile=%2FUsers%2Ftommy%2FDocuments%2Fshell%2Fglobal-bundle.pem&tlsAllowInvalidCertificates=true&tlsAllowInvalidHostnames=true&directConnection=true
```

---

## aws-redis

```bash
ssh -i "/Users/tommy/Documents/work.nosync/yudady/g-work/dayooint.com/dtg-stage-key/DTG-TEST-IT.pem" -f -N -L \
6380:dtg-redis-test-notls.qebi7j.clustercfg.memorydb.ap-southeast-1.amazonaws.com:6379 \
ubuntu@13.228.187.227 -v
```

---

## aws-activeMQ

```bash
ssh -i "/Users/tommy/Documents/work.nosync/yudady/g-work/dayooint.com/dtg-stage-key/DTG-TEST-IT.pem" -f -N -L \
8163:b-27971fd9-1dfc-4a09-aef3-96c00509b5e8-1.mq.ap-southeast-1.amazonaws.com:8162 \
ubuntu@13.228.187.227 -v
```

訪問地址:https://localhost:8163/admin/

- 用戶名:dtg-mq-test
- 密碼:BuscC7htGBTW4TK5GUDEvHDyQ3kRjR

## 相关笔记
- [[03-knowledge/技术/网络/网络配置-CloudDNS与Cloudflare|网络配置]]
- [[001-TODO/02-dtg/03-info/mysql|MySQL]]
- [[001-TODO/02-dtg/03-info/ActiveMQ|ActiveMQ]]
- [[03-knowledge/技术/数据库/MySQL-mac备份|MySQL 备份]]
- [[03-knowledge/技术/数据库/MySQL-字符集错误|MySQL 字符集问题]]
- [[03-knowledge/技术/工具/ngrok-应用程序部署|ngrok 部署]]
- [[MOC-DevOps与基础设施|MOC-DevOps 与基础设施]]