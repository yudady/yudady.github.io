---
title: oracle-db-link
author: tommy
tags:
  - oracle
categories:
  - database
toc: true
date: 2018-10-02 16:14:09
---

# 簡介
oracle database link


<!--more-->
# 內容

恢复时间：10/01 12:00 ~ 10/02 10:00



## db link(在客户机添加)
CREATE DATABASE LINK mypaycenter CONNECT TO mypaycenter IDENTIFIED BY `password`
USING
'(DESCRIPTION=(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = `10.140.0.1`)(PORT = 1521)))(CONNECT_DATA = (SERVICE_NAME =myPay)) )'



## 在客户机执行，恢复数据
```
INSERT INTO PY_ORDER 
select 
ORDER_NO
,PAGE_ID
,PAGE_NAME
,MERCHANT_ID
,MERCHANT_NAME
,PAYMENT_ORDER_NO
,RECHARGE_AMOUNT
,PAYMENT_DATE
,PAYMENT_NOTIFY_DATE
,'3'
,LOCK_USER_ID
,LOCK_USER_NAME
,LOCK_TIME
,CLIENT_ACCOUNT
,CLIENT_TERMINAL
,CLIENT_IP
,REMARK
,CREATE_DATE
,UPDATE_DATE
,UPDATE_USER_ID
,UPDATE_USER_NAME
,SUBMIT_DATE
,RESPONSE_DATA
,RESPONSE_CODE
,RESPONSE_DESC
,RESPONSE_QRCODE
,PAYMENT_PLATFORM_ID
,PAYMENT_PLATFORM_NAME
,PAYMENT_METHOD_ID
,PAYMENT_METHOD_NAME
,ROBOT_PROCESS_STATUS
,DEPOSIT_OFFER
,RED_ENVELOPES_OFFER
,TOTAL_AMOUNT
,CALLBACK_AMOUNT
,FEE_OFFER
,RECHARGE_CENTER_TYPE_ID
,RECHARGE_CENTER_TYPE_NAME
from (
SELECT c.* FROM py_order@mypaycenter c where c.CUST_ID = '079' and c.order_no >= 201810010790000001 and c.order_no like '20181001079%'
and  c.order_no not in ( SELECT p.order_no FROM py_order p where 1=1 and p.order_no >= 201810010790000001 and p.order_no like '20181001079%' )
union all
SELECT c.* FROM py_order@mypaycenter c where c.CUST_ID = '079' and c.order_no >= 201810020790000001 and c.order_no like '20181002079%'
and  c.order_no not in ( SELECT p.order_no FROM py_order p where 1=1 and p.order_no >= 201810020790000001 and p.order_no like '20181002079%' )
)
;

```





# 參考資料


