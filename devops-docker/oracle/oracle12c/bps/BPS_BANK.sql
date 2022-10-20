-- auto-generated definition
create table BPS_BANK
(
    BANK_ID           NUMBER(19)   not null
        constraint BPS_BANK_ID_PK
            primary key,
    BANK_PARENT_ID    NUMBER(19)   not null,
    BANK_NAME         VARCHAR2(64) not null,
    BANK_CODE         VARCHAR2(32) not null,
    BANK_ABBREVIATION VARCHAR2(32) not null,
    DISPLAY_NAME      VARCHAR2(32) not null,
    SORTING           NUMBER(8)    not null,
    VERSION           NUMBER(8),
    CREATE_TIME       TIMESTAMP(6),
    UPDATE_TIME       TIMESTAMP(6),
    CATEGORY          NUMBER(1),
    PAYMENT_TERM      VARCHAR2(32),
    PROVIDER          VARCHAR2(32)
)
/

comment on column BPS_BANK.BANK_ID is 'ID'
/

comment on column BPS_BANK.BANK_PARENT_ID is '渠道供應商ID'
/

comment on column BPS_BANK.BANK_NAME is '銀行名稱'
/

comment on column BPS_BANK.BANK_CODE is '銀行編號，第三方參數用。'
/

comment on column BPS_BANK.BANK_ABBREVIATION is '銀行縮寫，平台查詢用。'
/

comment on column BPS_BANK.DISPLAY_NAME is 'I18N 顯示名稱'
/

comment on column BPS_BANK.SORTING is '排序'
/

comment on column BPS_BANK.CATEGORY is '銀行在前後台顯示類型 0:网银转账 1:快捷充值 2:扫码支付'
/

comment on column BPS_BANK.PAYMENT_TERM is '銀行在前後台顯示類型'
/

comment on column BPS_BANK.PROVIDER is '渠道商'
/

