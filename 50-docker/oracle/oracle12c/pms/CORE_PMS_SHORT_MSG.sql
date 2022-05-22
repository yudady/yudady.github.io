create table PMS_SHORT_MSG
(
    MSG_ID            NUMBER(12)     not null
        constraint PMS_SHORT_MGS_PK
            primary key,
    SENDRD_NAME       VARCHAR2(16)   not null,
    SM_TITLE          VARCHAR2(400)  not null,
    SM_CONTENT        VARCHAR2(4000) not null,
    PUBLISH_TIME      TIMESTAMP(6),
    SEND_ALL          NUMBER(1)      not null,
    SM_STATUS         VARCHAR2(60)   not null,
    OPERATOR_ID       NUMBER(10),
    PARENT_MSG_ID     NUMBER(10),
    PLAN_PUBLISH_TIME TIMESTAMP(6),
    NICKNAME          VARCHAR2(64),
    SENDER_TYPE       VARCHAR2(64),
    VERIFY_STATUS     VARCHAR2(64),
    SEND_DEL_FLAG     NUMBER(1)    default 0,
    CREATE_TIME       TIMESTAMP(6) default CURRENT_TIMESTAMP,
    UPDATE_TIME       TIMESTAMP(6) default CURRENT_TIMESTAMP,
    VERSION           NUMBER(8)    default 0
)
/

comment on column PMS_SHORT_MSG.MSG_ID is 'MSG ID PK'
/

comment on column PMS_SHORT_MSG.SENDRD_NAME is '寄件人名稱 -1:系統'
/

comment on column PMS_SHORT_MSG.SM_TITLE is '站內信標題'
/

comment on column PMS_SHORT_MSG.SM_CONTENT is '信息內容'
/

comment on column PMS_SHORT_MSG.PUBLISH_TIME is '實際發佈時間'
/

comment on column PMS_SHORT_MSG.SEND_ALL is '是否寄送所有玩家 0:否, 1:是'
/

comment on column PMS_SHORT_MSG.SM_STATUS is '信息審核狀態. INITIAL:初始 , NORMAL:正常 , DELETED:已刪除'
/

comment on column PMS_SHORT_MSG.OPERATOR_ID is '系統寄出,後台操作人員ID'
/

comment on column PMS_SHORT_MSG.PARENT_MSG_ID is '回覆的父信件ID'
/

comment on column PMS_SHORT_MSG.PLAN_PUBLISH_TIME is '預計發送時間'
/

comment on column PMS_SHORT_MSG.NICKNAME is '暱稱'
/

comment on column PMS_SHORT_MSG.SENDER_TYPE is '寄給上級 寄給下級 後臺寄信'
/

comment on column PMS_SHORT_MSG.VERIFY_STATUS is '審核狀態. Deleted: 刪除 ,AuditDisagree: 審核不通過, Audit: 新增審核中, AuditApprove: 審核通過, AuditDelete: 刪除審核中, DeleteDisagree:刪除通過'
/

comment on column PMS_SHORT_MSG.SEND_DEL_FLAG is '已發信件刪除狀態 0: 未刪除, 1: 已刪除'
/

comment on column PMS_SHORT_MSG.CREATE_TIME is '建立時間'
/

comment on column PMS_SHORT_MSG.UPDATE_TIME is '更新時間'
/

comment on column PMS_SHORT_MSG.VERSION is '更新版本'
/

create index PMS_MSG_IDX_SENDER
    on PMS_SHORT_MSG (SENDRD_NAME)
/

INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6327, '-1', 'qweqwe', 'qweqweqweqwe', TO_TIMESTAMP('2021-02-01 12:19:08.711000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', 3383, null, TO_TIMESTAMP('2021-02-01 12:18:44.264000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-02-01 12:18:44.300000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-02-01 12:19:08.760000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 2);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6433, '-1', '提现审核不通过', '尊敬的用户：
  您好！
  非常抱歉地通知您，您的提现审核不通过。
由于您当前的提款人姓名与提现银行卡姓名不同，您的提现申请审核未通过。请确认您在平台中的提款人姓名与银行卡姓名是否相同，如有任何疑问可咨询在线客服。祝您游戏愉快。
优游娱乐', TO_TIMESTAMP('2021-04-16 13:16:12.534000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2021-04-16 13:16:12.474000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-04-16 13:16:12.498000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-04-16 13:16:12.596000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6428, '-1', '欢迎成为优游娱乐新用户', '亲爱的用户您好：
  感谢您成为优游娱乐平台新会员，享受高端专业的购彩体验。
  在开始体验游戏前，请确保有可用虚拟币，游戏过程中欲申请提现，请先完善个人安全信息(设定后不可修改)及银行卡等重要信息，安全信息包含取款人姓名、资金密码、安全邮箱等。
  温馨提示：当您设置完成后，请务必妥善保管个人安全信息及充提信息，不要将任何汇款信息及截图提供给他人，谨防上当受骗，若因自行保管不善造成损失，平台不承担任何责任。个人资金交易（充值，提现，投注等）出现延误未及时到账情况，请直接联系在线客服。
  优游娱乐是业内游戏彩种最全，活动最丰富，回馈礼金档次最高的老信誉平台，大额无忧，品质保证。
  最优质的游戏体验，最贴心的服务保障，为您的财务管理保驾护航。史无前例，机不可失，给梦想一个机会，优游娱乐助您日进斗金，实现精彩人生梦，详情立即点击<brandUrl>https://www.ugamefun.com/?utm_source=new user letter&utm_medium=mail&utm_campaign=new user<brandUrl><urlName>品牌官网<urlName>。
  还等什么？赶快开始游戏吧！

优游娱乐
2021年04月13日', TO_TIMESTAMP('2021-04-13 12:31:50.121000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2021-04-13 12:31:50.085000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-04-13 12:31:50.102000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-04-13 12:31:50.180000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6310, '-1', '2123123', '124123123123', TO_TIMESTAMP('2021-01-29 18:02:18.916000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', 4619, null, TO_TIMESTAMP('2021-01-29 18:01:59.711000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-01-29 18:01:59.713000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-01-29 18:02:18.925000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 2);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6309, '-1', 'reeee', 'tetetet', TO_TIMESTAMP('2021-01-29 18:00:42.836000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Deleted', 3383, null, TO_TIMESTAMP('2021-01-29 17:59:56.598000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'Deleted', 0, TO_TIMESTAMP('2021-01-29 17:59:56.673000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-04-28 15:50:36.270000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 2);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6430, '-1', '欢迎成为优游娱乐新用户', '亲爱的用户您好：
  感谢您成为优游娱乐平台新会员，享受高端专业的购彩体验。
  在开始体验游戏前，请确保有可用虚拟币，游戏过程中欲申请提现，请先完善个人安全信息(设定后不可修改)及银行卡等重要信息，安全信息包含取款人姓名、资金密码、安全邮箱等。
  温馨提示：当您设置完成后，请务必妥善保管个人安全信息及充提信息，不要将任何汇款信息及截图提供给他人，谨防上当受骗，若因自行保管不善造成损失，平台不承担任何责任。个人资金交易（充值，提现，投注等）出现延误未及时到账情况，请直接联系在线客服。
  优游娱乐是业内游戏彩种最全，活动最丰富，回馈礼金档次最高的老信誉平台，大额无忧，品质保证。
  最优质的游戏体验，最贴心的服务保障，为您的财务管理保驾护航。史无前例，机不可失，给梦想一个机会，优游娱乐助您日进斗金，实现精彩人生梦，详情立即点击<brandUrl>https://www.ugamefun.com/?utm_source=new user letter&utm_medium=mail&utm_campaign=new user<brandUrl><urlName>品牌官网<urlName>。
  还等什么？赶快开始游戏吧！

优游娱乐
2021年04月15日', TO_TIMESTAMP('2021-04-15 14:48:41.866000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2021-04-15 14:48:41.830000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-04-15 14:48:41.847000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-04-15 14:48:41.921000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6234, '-1', '提现失败', '尊敬的用户：您好！非常抱歉地通知您提现申请失败，请您稍后再尝试。如有疑问请咨询在线客服。

优游娱乐
2020年12月18日', TO_TIMESTAMP('2020-12-18 16:08:03.788000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2020-12-18 16:08:03.768000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-12-18 16:08:03.775000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-12-18 16:08:03.799000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6235, '-1', '提现失败', '尊敬的用户：您好！非常抱歉地通知您提现申请失败，请您稍后再尝试。如有疑问请咨询在线客服。

优游娱乐
2020年12月18日', TO_TIMESTAMP('2020-12-18 16:14:32.025000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2020-12-18 16:14:32.021000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-12-18 16:14:32.021000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-12-18 16:14:32.028000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6207, '-1', '提现审核不通过', '尊敬的用户：
  您好！
  非常抱歉地通知您，您的提现审核不通过。
由于您的投注额未达标，您的提现申请审核未通过。如有任何疑问可咨询在线客服。祝您游戏愉快。
优游娱乐', TO_TIMESTAMP('2020-12-11 15:22:55.698000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2020-12-11 15:22:55.654000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-12-11 15:22:55.670000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-12-11 15:22:55.729000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6347, '-1', '提现审核不通过', '尊敬的用户：
  您好！
  非常抱歉地通知您，您的提现审核不通过。
由于您的投注额未达标，您的提现申请审核未通过。如有任何疑问可咨询在线客服。祝您游戏愉快。
优游娱乐', TO_TIMESTAMP('2021-02-20 17:06:11.023000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2021-02-20 17:06:10.944000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-02-20 17:06:10.976000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-02-20 17:06:11.080000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6388, '-1', '提现失败', '尊敬的用户：您好！非常抱歉地通知您提现申请失败，请您稍后再尝试。如有疑问请咨询在线客服。

优游娱乐
2021年03月03日', TO_TIMESTAMP('2021-03-03 15:58:29.128000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2021-03-03 15:58:29.105000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-03-03 15:58:29.114000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-03-03 15:58:29.150000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6389, '-1', '提现失败', '尊敬的用户：您好！非常抱歉地通知您提现申请失败，请您稍后再尝试。如有疑问请咨询在线客服。

优游娱乐
2021年03月03日', TO_TIMESTAMP('2021-03-03 16:42:37.233000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2021-03-03 16:42:37.213000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-03-03 16:42:37.220000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-03-03 16:42:37.254000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6429, '-1', '欢迎成为优游娱乐新用户', '亲爱的用户您好：
  感谢您成为优游娱乐平台新会员，享受高端专业的购彩体验。
  在开始体验游戏前，请确保有可用虚拟币，游戏过程中欲申请提现，请先完善个人安全信息(设定后不可修改)及银行卡等重要信息，安全信息包含取款人姓名、资金密码、安全邮箱等。
  温馨提示：当您设置完成后，请务必妥善保管个人安全信息及充提信息，不要将任何汇款信息及截图提供给他人，谨防上当受骗，若因自行保管不善造成损失，平台不承担任何责任。个人资金交易（充值，提现，投注等）出现延误未及时到账情况，请直接联系在线客服。
  优游娱乐是业内游戏彩种最全，活动最丰富，回馈礼金档次最高的老信誉平台，大额无忧，品质保证。
  最优质的游戏体验，最贴心的服务保障，为您的财务管理保驾护航。史无前例，机不可失，给梦想一个机会，优游娱乐助您日进斗金，实现精彩人生梦，详情立即点击<brandUrl>https://www.ugamefun.com/?utm_source=new user letter&utm_medium=mail&utm_campaign=new user<brandUrl><urlName>品牌官网<urlName>。
  还等什么？赶快开始游戏吧！

优游娱乐
2021年04月14日', TO_TIMESTAMP('2021-04-14 10:38:41.736000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2021-04-14 10:38:41.672000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-04-14 10:38:41.713000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-04-14 10:38:41.806000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6367, '-1', '站內信', '全', TO_TIMESTAMP('2021-03-02 18:06:33.702000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1, 'Audit', 1959, null, TO_TIMESTAMP('2021-03-02 18:06:33.702000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'Audit', 0, TO_TIMESTAMP('2021-03-02 18:06:33.750000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-03-02 18:06:34.469000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6287, '-1', '提现审核不通过', '尊敬的用户：
  您好！
  非常抱歉地通知您，您的提现审核不通过。
由于您的投注额未达标，您的提现申请审核未通过。如有任何疑问可咨询在线客服。祝您游戏愉快。
优游娱乐', TO_TIMESTAMP('2021-01-27 17:21:34.677000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2021-01-27 17:21:34.604000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-01-27 17:21:34.631000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-01-27 17:21:34.732000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6230, '-1', '提现审核不通过', '尊敬的用户：
  您好！
  非常抱歉地通知您，您的提现审核不通过。
由于您的投注额未达标，您的提现申请审核未通过。如有任何疑问可咨询在线客服。祝您游戏愉快。
优游娱乐', TO_TIMESTAMP('2020-12-15 17:33:32.315000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2020-12-15 17:33:32.294000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-12-15 17:33:32.299000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-12-15 17:33:32.344000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6247, '-1', '欢迎成为优游娱乐新用户', '亲爱的用户您好：
  感谢您成为优游娱乐平台新会员，享受高端专业的购彩体验。
  在开始体验游戏前，请确保有可用虚拟币，游戏过程中欲申请提现，请先完善个人安全信息(设定后不可修改)及银行卡等重要信息，安全信息包含取款人姓名、资金密码、安全邮箱等。
  温馨提示：当您设置完成后，请务必妥善保管个人安全信息及充提信息，不要将任何汇款信息及截图提供给他人，谨防上当受骗，若因自行保管不善造成损失，平台不承担任何责任。个人资金交易（充值，提现，投注等）出现延误未及时到账情况，请直接联系在线客服。
  优游娱乐是业内游戏彩种最全，活动最丰富，回馈礼金档次最高的老信誉平台，大额无忧，品质保证。
  最优质的游戏体验，最贴心的服务保障，为您的财务管理保驾护航。史无前例，机不可失，给梦想一个机会，优游娱乐助您日进斗金，实现精彩人生梦，详情立即点击<brandUrl>https://www.ugamefun.com/?utm_source=new user letter&utm_medium=mail&utm_campaign=new user<brandUrl><urlName>品牌官网<urlName>。
  还等什么？赶快开始游戏吧！

优游娱乐
2020年12月22日', TO_TIMESTAMP('2020-12-22 12:13:38.855000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2020-12-22 12:13:38.813000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-12-22 12:13:38.831000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-12-22 12:13:38.898000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6147, '-1', '提现失败', '尊敬的用户：您好！非常抱歉地通知您提现申请失败，请您稍后再尝试。如有疑问请咨询在线客服。

优游娱乐
2020年11月18日', TO_TIMESTAMP('2020-11-18 17:36:32.625000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2020-11-18 17:36:32.581000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-11-18 17:36:32.587000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-11-18 17:36:32.651000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6387, '-1', '欢迎成为优游娱乐新用户', '亲爱的用户您好：
  感谢您成为优游娱乐平台新会员，享受高端专业的购彩体验。
  在开始体验游戏前，请确保有可用虚拟币，游戏过程中欲申请提现，请先完善个人安全信息(设定后不可修改)及银行卡等重要信息，安全信息包含取款人姓名、资金密码、安全邮箱等。
  温馨提示：当您设置完成后，请务必妥善保管个人安全信息及充提信息，不要将任何汇款信息及截图提供给他人，谨防上当受骗，若因自行保管不善造成损失，平台不承担任何责任。个人资金交易（充值，提现，投注等）出现延误未及时到账情况，请直接联系在线客服。
  优游娱乐是业内游戏彩种最全，活动最丰富，回馈礼金档次最高的老信誉平台，大额无忧，品质保证。
  最优质的游戏体验，最贴心的服务保障，为您的财务管理保驾护航。史无前例，机不可失，给梦想一个机会，优游娱乐助您日进斗金，实现精彩人生梦，详情立即点击<brandUrl>https://www.ugamefun.com/?utm_source=new user letter&utm_medium=mail&utm_campaign=new user<brandUrl><urlName>品牌官网<urlName>。
  还等什么？赶快开始游戏吧！

优游娱乐
2021年03月03日', TO_TIMESTAMP('2021-03-03 14:05:25.410000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2021-03-03 14:05:25.351000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-03-03 14:05:25.377000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-03-03 14:05:25.497000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6427, '-1', '欢迎成为优游娱乐新用户', '亲爱的用户您好：
  感谢您成为优游娱乐平台新会员，享受高端专业的购彩体验。
  在开始体验游戏前，请确保有可用虚拟币，游戏过程中欲申请提现，请先完善个人安全信息(设定后不可修改)及银行卡等重要信息，安全信息包含取款人姓名、资金密码、安全邮箱等。
  温馨提示：当您设置完成后，请务必妥善保管个人安全信息及充提信息，不要将任何汇款信息及截图提供给他人，谨防上当受骗，若因自行保管不善造成损失，平台不承担任何责任。个人资金交易（充值，提现，投注等）出现延误未及时到账情况，请直接联系在线客服。
  优游娱乐是业内游戏彩种最全，活动最丰富，回馈礼金档次最高的老信誉平台，大额无忧，品质保证。
  最优质的游戏体验，最贴心的服务保障，为您的财务管理保驾护航。史无前例，机不可失，给梦想一个机会，优游娱乐助您日进斗金，实现精彩人生梦，详情立即点击<brandUrl>https://www.ugamefun.com/?utm_source=new user letter&utm_medium=mail&utm_campaign=new user<brandUrl><urlName>品牌官网<urlName>。
  还等什么？赶快开始游戏吧！

优游娱乐
2021年04月13日', TO_TIMESTAMP('2021-04-13 10:42:21.390000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2021-04-13 10:42:21.343000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-04-13 10:42:21.360000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-04-13 10:42:21.438000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6307, '-1', '欢迎成为优游娱乐新用户', '亲爱的用户您好：
  感谢您成为优游娱乐平台新会员，享受高端专业的购彩体验。
  在开始体验游戏前，请确保有可用虚拟币，游戏过程中欲申请提现，请先完善个人安全信息(设定后不可修改)及银行卡等重要信息，安全信息包含取款人姓名、资金密码、安全邮箱等。
  温馨提示：当您设置完成后，请务必妥善保管个人安全信息及充提信息，不要将任何汇款信息及截图提供给他人，谨防上当受骗，若因自行保管不善造成损失，平台不承担任何责任。个人资金交易（充值，提现，投注等）出现延误未及时到账情况，请直接联系在线客服。
  优游娱乐是业内游戏彩种最全，活动最丰富，回馈礼金档次最高的老信誉平台，大额无忧，品质保证。
  最优质的游戏体验，最贴心的服务保障，为您的财务管理保驾护航。史无前例，机不可失，给梦想一个机会，优游娱乐助您日进斗金，实现精彩人生梦，详情立即点击<brandUrl>https://www.ugamefun.com/?utm_source=new user letter&utm_medium=mail&utm_campaign=new user<brandUrl><urlName>品牌官网<urlName>。
  还等什么？赶快开始游戏吧！

优游娱乐
2021年01月29日', TO_TIMESTAMP('2021-01-29 15:32:10.501000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2021-01-29 15:32:10.458000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-01-29 15:32:10.472000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-01-29 15:32:10.547000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6071, 'penny00102', '給你', '給你', TO_TIMESTAMP('2020-10-27 10:20:03.302000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'DELETED', null, null, TO_TIMESTAMP('2020-10-27 10:20:03.302000', 'YYYY-MM-DD HH24:MI:SS.FF6'), '胚倪苓苓一零二', 'CUSTOMER', 'Deleted', 0, TO_TIMESTAMP('2020-10-27 10:20:03.302000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-04-15 15:28:47.036000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6228, '-1', '提现审核不通过', '尊敬的用户：
  您好！
  非常抱歉地通知您，您的提现审核不通过。
由于您的投注额未达标，您的提现申请审核未通过。如有任何疑问可咨询在线客服。祝您游戏愉快。
优游娱乐', TO_TIMESTAMP('2020-12-14 17:46:21.445000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2020-12-14 17:46:21.441000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-12-14 17:46:21.441000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-12-14 17:46:21.456000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6229, '-1', '提现审核不通过', '尊敬的用户：
  您好！
  非常抱歉地通知您，您的提现审核不通过。
由于您的投注额未达标，您的提现申请审核未通过。如有任何疑问可咨询在线客服。祝您游戏愉快。
优游娱乐', TO_TIMESTAMP('2020-12-15 17:04:16.821000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2020-12-15 17:04:16.799000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-12-15 17:04:16.805000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-12-15 17:04:16.851000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6227, '-1', '提现审核不通过', '尊敬的用户：
  您好！
  非常抱歉地通知您，您的提现审核不通过。
由于您的投注额未达标，您的提现申请审核未通过。如有任何疑问可咨询在线客服。祝您游戏愉快。
优游娱乐', TO_TIMESTAMP('2020-12-14 17:43:37.623000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2020-12-14 17:43:37.542000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-12-14 17:43:37.574000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-12-14 17:43:37.677000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6308, '-1', '123', '321', TO_TIMESTAMP('2021-01-29 17:47:20.063000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', 4619, null, TO_TIMESTAMP('2021-01-29 17:47:02.241000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-01-29 17:47:02.302000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-01-29 17:47:20.195000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 2);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6107, '-1', '提现审核通过', 'ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt', TO_TIMESTAMP('2020-11-05 17:06:50.643000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', 1959, null, TO_TIMESTAMP('2020-11-05 17:06:50.563000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-11-05 17:06:50.595000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-11-05 17:06:50.708000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6167, '-1', '欢迎成为优游娱乐新用户', '亲爱的用户您好：
  感谢您成为优游娱乐平台新会员，享受高端专业的购彩体验。
  在开始体验游戏前，请确保有可用虚拟币，游戏过程中欲申请提现，请先完善个人安全信息(设定后不可修改)及银行卡等重要信息，安全信息包含取款人姓名、资金密码、安全邮箱等。
  温馨提示：当您设置完成后，请务必妥善保管个人安全信息及充提信息，不要将任何汇款信息及截图提供给他人，谨防上当受骗，若因自行保管不善造成损失，平台不承担任何责任。个人资金交易（充值，提现，投注等）出现延误未及时到账情况，请直接联系在线客服。
  优游娱乐是业内游戏彩种最全，活动最丰富，回馈礼金档次最高的老信誉平台，大额无忧，品质保证。
  最优质的游戏体验，最贴心的服务保障，为您的财务管理保驾护航。史无前例，机不可失，给梦想一个机会，优游娱乐助您日进斗金，实现精彩人生梦，详情立即点击<brandUrl>https://www.ugamefun.com/?utm_source=new user letter&utm_medium=mail&utm_campaign=new user<brandUrl><urlName>品牌官网<urlName>。
  还等什么？赶快开始游戏吧！

优游娱乐
2020年11月26日', TO_TIMESTAMP('2020-11-26 09:34:40.192000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2020-11-26 09:34:40.144000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-11-26 09:34:40.161000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-11-26 09:34:40.257000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6328, '-1', 'qweqwe', 'qweqweqwe', TO_TIMESTAMP('2021-02-01 14:07:11.113000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', 3383, null, TO_TIMESTAMP('2021-02-01 14:07:00.636000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-02-01 14:07:00.693000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-02-01 14:07:11.169000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 2);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6232, '-1', '提现审核不通过', '尊敬的用户：
  您好！
  非常抱歉地通知您，您的提现审核不通过。
由于您的投注额未达标，您的提现申请审核未通过。如有任何疑问可咨询在线客服。祝您游戏愉快。
优游娱乐', TO_TIMESTAMP('2020-12-15 17:48:43.781000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2020-12-15 17:48:43.760000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-12-15 17:48:43.766000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-12-15 17:48:43.813000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6233, '-1', '提现审核不通过', '尊敬的用户：
  您好！
  非常抱歉地通知您，您的提现审核不通过。
由于您的投注额未达标，您的提现申请审核未通过。如有任何疑问可咨询在线客服。祝您游戏愉快。
优游娱乐', TO_TIMESTAMP('2020-12-15 17:51:25.564000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2020-12-15 17:51:25.559000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-12-15 17:51:25.559000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-12-15 17:51:25.587000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6407, '-1', '欢迎成为优游娱乐新用户', '亲爱的用户您好：
  感谢您成为优游娱乐平台新会员，享受高端专业的购彩体验。
  在开始体验游戏前，请确保有可用虚拟币，游戏过程中欲申请提现，请先完善个人安全信息(设定后不可修改)及银行卡等重要信息，安全信息包含取款人姓名、资金密码、安全邮箱等。
  温馨提示：当您设置完成后，请务必妥善保管个人安全信息及充提信息，不要将任何汇款信息及截图提供给他人，谨防上当受骗，若因自行保管不善造成损失，平台不承担任何责任。个人资金交易（充值，提现，投注等）出现延误未及时到账情况，请直接联系在线客服。
  优游娱乐是业内游戏彩种最全，活动最丰富，回馈礼金档次最高的老信誉平台，大额无忧，品质保证。
  最优质的游戏体验，最贴心的服务保障，为您的财务管理保驾护航。史无前例，机不可失，给梦想一个机会，优游娱乐助您日进斗金，实现精彩人生梦，详情立即点击<brandUrl>https://www.ugamefun.com/?utm_source=new user letter&utm_medium=mail&utm_campaign=new user<brandUrl><urlName>品牌官网<urlName>。
  还等什么？赶快开始游戏吧！

优游娱乐
2021年03月26日', TO_TIMESTAMP('2021-03-26 14:52:43.406000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2021-03-26 14:52:43.319000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-03-26 14:52:43.365000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-03-26 14:52:43.483000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6127, 'ricoyang', 'ＧＭＡＩＬ', 'ＧＭＡＩＬ', TO_TIMESTAMP('2020-11-09 12:31:24.731000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', null, null, TO_TIMESTAMP('2020-11-09 12:31:24.731000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 'ricoyang222', 'AGENT', 'AuditApprove', 0, TO_TIMESTAMP('2020-11-09 12:31:24.735000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-11-09 12:31:24.735000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6447, '-1', '欢迎成为优游娱乐新用户', '亲爱的用户您好：
  感谢您成为优游娱乐平台新会员，享受高端专业的购彩体验。
  在开始体验游戏前，请确保有可用虚拟币，游戏过程中欲申请提现，请先完善个人安全信息(设定后不可修改)及银行卡等重要信息，安全信息包含取款人姓名、资金密码、安全邮箱等。
  温馨提示：当您设置完成后，请务必妥善保管个人安全信息及充提信息，不要将任何汇款信息及截图提供给他人，谨防上当受骗，若因自行保管不善造成损失，平台不承担任何责任。个人资金交易（充值，提现，投注等）出现延误未及时到账情况，请直接联系在线客服。
  优游娱乐是业内游戏彩种最全，活动最丰富，回馈礼金档次最高的老信誉平台，大额无忧，品质保证。
  最优质的游戏体验，最贴心的服务保障，为您的财务管理保驾护航。史无前例，机不可失，给梦想一个机会，优游娱乐助您日进斗金，实现精彩人生梦，详情立即点击<brandUrl>https://www.ugamefun.com/?utm_source=new user letter&utm_medium=mail&utm_campaign=new user<brandUrl><urlName>品牌官网<urlName>。
  还等什么？赶快开始游戏吧！

优游娱乐
2021年04月21日', TO_TIMESTAMP('2021-04-21 17:02:17.464000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2021-04-21 17:02:17.427000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-04-21 17:02:17.440000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-04-21 17:02:17.525000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6467, '-1', '欢迎成为优游娱乐新用户', '亲爱的用户您好：
  感谢您成为优游娱乐平台新会员，享受高端专业的购彩体验。
  在开始体验游戏前，请确保有可用虚拟币，游戏过程中欲申请提现，请先完善个人安全信息(设定后不可修改)及银行卡等重要信息，安全信息包含取款人姓名、资金密码、安全邮箱等。
  温馨提示：当您设置完成后，请务必妥善保管个人安全信息及充提信息，不要将任何汇款信息及截图提供给他人，谨防上当受骗，若因自行保管不善造成损失，平台不承担任何责任。个人资金交易（充值，提现，投注等）出现延误未及时到账情况，请直接联系在线客服。
  优游娱乐是业内游戏彩种最全，活动最丰富，回馈礼金档次最高的老信誉平台，大额无忧，品质保证。
  最优质的游戏体验，最贴心的服务保障，为您的财务管理保驾护航。史无前例，机不可失，给梦想一个机会，优游娱乐助您日进斗金，实现精彩人生梦，详情立即点击<brandUrl>https://www.ugamefun.com/?utm_source=new user letter&utm_medium=mail&utm_campaign=new user<brandUrl><urlName>品牌官网<urlName>。
  还等什么？赶快开始游戏吧！

优游娱乐
2021年04月23日', TO_TIMESTAMP('2021-04-23 16:45:43.833000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2021-04-23 16:45:43.783000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-04-23 16:45:43.804000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-04-30 11:33:05.914000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6187, '-1', '欢迎成为优游娱乐新用户', '亲爱的用户您好：
  感谢您成为优游娱乐平台新会员，享受高端专业的购彩体验。
  在开始体验游戏前，请确保有可用虚拟币，游戏过程中欲申请提现，请先完善个人安全信息(设定后不可修改)及银行卡等重要信息，安全信息包含取款人姓名、资金密码、安全邮箱等。
  温馨提示：当您设置完成后，请务必妥善保管个人安全信息及充提信息，不要将任何汇款信息及截图提供给他人，谨防上当受骗，若因自行保管不善造成损失，平台不承担任何责任。个人资金交易（充值，提现，投注等）出现延误未及时到账情况，请直接联系在线客服。
  优游娱乐是业内游戏彩种最全，活动最丰富，回馈礼金档次最高的老信誉平台，大额无忧，品质保证。
  最优质的游戏体验，最贴心的服务保障，为您的财务管理保驾护航。史无前例，机不可失，给梦想一个机会，优游娱乐助您日进斗金，实现精彩人生梦，详情立即点击<brandUrl>https://www.ugamefun.com/?utm_source=new user letter&utm_medium=mail&utm_campaign=new user<brandUrl><urlName>品牌官网<urlName>。
  还等什么？赶快开始游戏吧！

优游娱乐
2020年12月02日', TO_TIMESTAMP('2020-12-02 15:29:32.772000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2020-12-02 15:29:32.725000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-12-02 15:29:32.744000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-12-02 15:29:32.820000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6231, '-1', '提现审核不通过', '尊敬的用户：
  您好！
  非常抱歉地通知您，您的提现审核不通过。
由于您的投注额未达标，您的提现申请审核未通过。如有任何疑问可咨询在线客服。祝您游戏愉快。
优游娱乐', TO_TIMESTAMP('2020-12-15 17:40:30.211000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2020-12-15 17:40:30.194000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-12-15 17:40:30.199000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-12-15 17:40:30.247000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6431, '-1', '欢迎成为优游娱乐新用户', '亲爱的用户您好：
  感谢您成为优游娱乐平台新会员，享受高端专业的购彩体验。
  在开始体验游戏前，请确保有可用虚拟币，游戏过程中欲申请提现，请先完善个人安全信息(设定后不可修改)及银行卡等重要信息，安全信息包含取款人姓名、资金密码、安全邮箱等。
  温馨提示：当您设置完成后，请务必妥善保管个人安全信息及充提信息，不要将任何汇款信息及截图提供给他人，谨防上当受骗，若因自行保管不善造成损失，平台不承担任何责任。个人资金交易（充值，提现，投注等）出现延误未及时到账情况，请直接联系在线客服。
  优游娱乐是业内游戏彩种最全，活动最丰富，回馈礼金档次最高的老信誉平台，大额无忧，品质保证。
  最优质的游戏体验，最贴心的服务保障，为您的财务管理保驾护航。史无前例，机不可失，给梦想一个机会，优游娱乐助您日进斗金，实现精彩人生梦，详情立即点击<brandUrl>https://www.ugamefun.com/?utm_source=new user letter&utm_medium=mail&utm_campaign=new user<brandUrl><urlName>品牌官网<urlName>。
  还等什么？赶快开始游戏吧！

优游娱乐
2021年04月15日', TO_TIMESTAMP('2021-04-15 15:14:56.131000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2021-04-15 15:14:56.128000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-04-15 15:14:56.128000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-04-15 15:14:56.139000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6208, '-1', '提现审核不通过', '尊敬的用户：
  您好！
  非常抱歉地通知您，您的提现审核不通过。
null祝您游戏愉快。
优游娱乐', TO_TIMESTAMP('2020-12-11 16:49:41.576000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2020-12-11 16:49:41.557000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-12-11 16:49:41.562000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-12-11 16:49:41.605000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6329, '-1', 'test', 'test', TO_TIMESTAMP('2021-02-01 14:09:12.564000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1, 'Normal', 3383, null, TO_TIMESTAMP('2021-02-01 14:09:06.166000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-02-01 14:09:06.169000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-02-01 14:09:12.578000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 2);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6267, '-1', '欢迎成为优游娱乐新用户', '亲爱的用户您好：
  感谢您成为优游娱乐平台新会员，享受高端专业的购彩体验。
  在开始体验游戏前，请确保有可用虚拟币，游戏过程中欲申请提现，请先完善个人安全信息(设定后不可修改)及银行卡等重要信息，安全信息包含取款人姓名、资金密码、安全邮箱等。
  温馨提示：当您设置完成后，请务必妥善保管个人安全信息及充提信息，不要将任何汇款信息及截图提供给他人，谨防上当受骗，若因自行保管不善造成损失，平台不承担任何责任。个人资金交易（充值，提现，投注等）出现延误未及时到账情况，请直接联系在线客服。
  优游娱乐是业内游戏彩种最全，活动最丰富，回馈礼金档次最高的老信誉平台，大额无忧，品质保证。
  最优质的游戏体验，最贴心的服务保障，为您的财务管理保驾护航。史无前例，机不可失，给梦想一个机会，优游娱乐助您日进斗金，实现精彩人生梦，详情立即点击<brandUrl>https://www.ugamefun.com/?utm_source=new user letter&utm_medium=mail&utm_campaign=new user<brandUrl><urlName>品牌官网<urlName>。
  还等什么？赶快开始游戏吧！

优游娱乐
2020年12月29日', TO_TIMESTAMP('2020-12-29 17:44:54.046000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2020-12-29 17:44:53.982000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2020-12-29 17:44:54.010000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2020-12-29 17:44:54.104000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);
INSERT INTO CORE.PMS_SHORT_MSG (MSG_ID, SENDRD_NAME, SM_TITLE, SM_CONTENT, PUBLISH_TIME, SEND_ALL, SM_STATUS, OPERATOR_ID, PARENT_MSG_ID, PLAN_PUBLISH_TIME, NICKNAME, SENDER_TYPE, VERIFY_STATUS, SEND_DEL_FLAG, CREATE_TIME, UPDATE_TIME, VERSION) VALUES (6432, '-1', '欢迎成为优游娱乐新用户', '亲爱的用户您好：
  感谢您成为优游娱乐平台新会员，享受高端专业的购彩体验。
  在开始体验游戏前，请确保有可用虚拟币，游戏过程中欲申请提现，请先完善个人安全信息(设定后不可修改)及银行卡等重要信息，安全信息包含取款人姓名、资金密码、安全邮箱等。
  温馨提示：当您设置完成后，请务必妥善保管个人安全信息及充提信息，不要将任何汇款信息及截图提供给他人，谨防上当受骗，若因自行保管不善造成损失，平台不承担任何责任。个人资金交易（充值，提现，投注等）出现延误未及时到账情况，请直接联系在线客服。
  优游娱乐是业内游戏彩种最全，活动最丰富，回馈礼金档次最高的老信誉平台，大额无忧，品质保证。
  最优质的游戏体验，最贴心的服务保障，为您的财务管理保驾护航。史无前例，机不可失，给梦想一个机会，优游娱乐助您日进斗金，实现精彩人生梦，详情立即点击<brandUrl>https://www.ugamefun.com/?utm_source=new user letter&utm_medium=mail&utm_campaign=new user<brandUrl><urlName>品牌官网<urlName>。
  还等什么？赶快开始游戏吧！

优游娱乐
2021年04月15日', TO_TIMESTAMP('2021-04-15 18:05:56.416000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 0, 'Normal', -1, null, TO_TIMESTAMP('2021-04-15 18:05:56.378000', 'YYYY-MM-DD HH24:MI:SS.FF6'), null, 'OPERATOR', 'AuditApprove', 0, TO_TIMESTAMP('2021-04-15 18:05:56.395000', 'YYYY-MM-DD HH24:MI:SS.FF6'), TO_TIMESTAMP('2021-04-15 18:05:56.449000', 'YYYY-MM-DD HH24:MI:SS.FF6'), 1);