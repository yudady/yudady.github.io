-- auto-generated definition
create table CORE.BPS_BANK_PARA
(
    BANK_PARA_ID   NUMBER(19)    not null
        constraint BPS_BANK_PARA_PK
            primary key,
    BANK_ID        NUMBER(19)    not null,
    PARTITION_TYPE VARCHAR2(32)  not null,
    PARA_NAME      VARCHAR2(128) not null,
    PARA_VALUE     VARCHAR2(128) not null,
    REMARK         VARCHAR2(512),
    SORTING        NUMBER(8)     not null,
    VERSION        NUMBER(8),
    CREATE_TIME    TIMESTAMP(6),
    UPDATE_TIME    TIMESTAMP(6),
    DEVICE_TYPE    VARCHAR2(1) default '1',
    TERMINAL       VARCHAR2(32)
)
/

comment on table BPS_BANK_PARA is '銀行參數表'
/

comment on column BPS_BANK_PARA.BANK_PARA_ID is '主鍵'
/

comment on column BPS_BANK_PARA.BANK_ID is 'BPS_BANK_N.BANK_ID'
/

comment on column BPS_BANK_PARA.PARTITION_TYPE is '種類 OFFLINE or THIRD_PARTY'
/

comment on column BPS_BANK_PARA.PARA_NAME is '參數名稱'
/

comment on column BPS_BANK_PARA.PARA_VALUE is '參數值'
/

comment on column BPS_BANK_PARA.REMARK is '備註'
/

comment on column BPS_BANK_PARA.SORTING is '排序 -1 不顯示'
/

comment on column BPS_BANK_PARA.VERSION is '版本'
/

comment on column BPS_BANK_PARA.CREATE_TIME is '新增時間'
/

comment on column BPS_BANK_PARA.UPDATE_TIME is '修改時間'
/

comment on column BPS_BANK_PARA.TERMINAL is '終端類型'
/

create index IDX_BPSBANKPARA_BANKID
    on BPS_BANK_PARA (BANK_ID)
/


CREATE SEQUENCE "CORE"."SEQ_BANK_PARA"
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9999999999999999999
    CYCLE
    NOCACHE;
/
