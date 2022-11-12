CREATE SEQUENCE "CORE"."SEQ_LOTT_GROUP_SERIES"
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9999999999999999999
    CYCLE
    NOCACHE;
/

-- auto-generated definition
create table LOTT_GROUP_SERIES
(
    GID         NUMBER                                 not null
        constraint PK_LOTT_GROUP_SERIES
            primary key,
    GROUP_ID    NUMBER                                 not null,
    GROUP_VALUE VARCHAR2(128)                          not null,
    GROUP_TYPE  NUMBER                                 not null,
    REMARK      VARCHAR2(128),
    PARAM_ONE   VARCHAR2(128),
    PARAM_TWO   VARCHAR2(32),
    CREATE_TIME TIMESTAMP(6) default CURRENT_TIMESTAMP not null,
    UPDATE_TIME TIMESTAMP(6) default CURRENT_TIMESTAMP not null,
    DEVICE_TYPE VARCHAR2(10) default 'WEB'             not null
)
/

comment on column LOTT_GROUP_SERIES.GID is '主鍵gid'
/

comment on column LOTT_GROUP_SERIES.GROUP_ID is '綜合娛樂遊戲代碼'
/

comment on column LOTT_GROUP_SERIES.GROUP_VALUE is '綜合娛樂遊戲廠商代碼'
/

comment on column LOTT_GROUP_SERIES.GROUP_TYPE is '綜合娛樂類型 3:SLOT 4:CARD_GAME 5:FMC 6:LIVE_GAME'
/

comment on column LOTT_GROUP_SERIES.REMARK is '備註,MGS試玩使用'
/

comment on column LOTT_GROUP_SERIES.PARAM_ONE is '備用字段1'
/

comment on column LOTT_GROUP_SERIES.PARAM_TWO is '備用字段2'
/

comment on column LOTT_GROUP_SERIES.CREATE_TIME is '新增時間'
/

comment on column LOTT_GROUP_SERIES.UPDATE_TIME is '修改時間'
/

comment on column LOTT_GROUP_SERIES.DEVICE_TYPE is '裝置類型 ALL/WEB/MOBILE'
/


