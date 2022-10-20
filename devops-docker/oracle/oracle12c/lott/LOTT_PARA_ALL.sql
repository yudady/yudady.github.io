CREATE SEQUENCE "CORE"."SEQ_LOTT_PARA_ALL"
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9999999999999999999
    CYCLE
    NOCACHE;
/


create table LOTT_PARA_ALL
(
    GID                NUMBER                                 not null
        constraint PK_LOTT_PARA_ALL
            primary key,
    GROUPNAMES         VARCHAR2(128)                          not null,
    SUBGROUPNAMES      VARCHAR2(128),
    PARAMNAMES         VARCHAR2(128)                          not null,
    PARAMVALUES        VARCHAR2(1024)                         not null,
    COLUMNTYPES        VARCHAR2(128)                          not null,
    MAKERS             VARCHAR2(128),
    MAKETIMES          DATE,
    CHECKERS           VARCHAR2(128),
    CHECKTIMES         DATE,
    READROLES          VARCHAR2(512),
    CHECKROLES         VARCHAR2(512),
    WRITEROLES         VARCHAR2(512),
    PARAMVALUES_MODIFY VARCHAR2(1024),
    APPLYERS           VARCHAR2(128),
    APPLYTIMES         DATE,
    COMMENTS           VARCHAR2(512),
    GROUPNAMESEN       VARCHAR2(128),
    PARAM_STATE        NUMBER,
    ORDER_ID           NUMBER       default 1,
    CREATE_TIME        TIMESTAMP(6) default CURRENT_TIMESTAMP not null,
    UPDATE_TIME        TIMESTAMP(6) default CURRENT_TIMESTAMP not null,
    TGI_ID             NUMBER(10),
    CASUAL_GAME_ID     NUMBER(10)
)
/

comment on column LOTT_PARA_ALL.GID is '主鍵'
/

comment on column LOTT_PARA_ALL.GROUPNAMES is '所屬菜單'
/

comment on column LOTT_PARA_ALL.SUBGROUPNAMES is '所屬子菜單'
/

comment on column LOTT_PARA_ALL.PARAMNAMES is '參數名'
/

comment on column LOTT_PARA_ALL.PARAMVALUES is '參數值'
/

comment on column LOTT_PARA_ALL.COLUMNTYPES is '字段類型'
/

comment on column LOTT_PARA_ALL.MAKERS is '申請人最終'
/

comment on column LOTT_PARA_ALL.MAKETIMES is '申請時間最終'
/

comment on column LOTT_PARA_ALL.CHECKERS is '審核人'
/

comment on column LOTT_PARA_ALL.CHECKTIMES is '審核時間'
/

comment on column LOTT_PARA_ALL.READROLES is '擁有可讀權限角色,記錄角色GID'
/

comment on column LOTT_PARA_ALL.CHECKROLES is '擁有可審核權限角色,記錄角色GID'
/

comment on column LOTT_PARA_ALL.WRITEROLES is '擁有可寫權限角色色，記錄角色GID'
/

comment on column LOTT_PARA_ALL.PARAMVALUES_MODIFY is '修改之后值'
/

comment on column LOTT_PARA_ALL.APPLYERS is '申請人'
/

comment on column LOTT_PARA_ALL.APPLYTIMES is '申請時間'
/

comment on column LOTT_PARA_ALL.COMMENTS is '備注'
/

comment on column LOTT_PARA_ALL.GROUPNAMESEN is '所屬菜單英文名'
/

comment on column LOTT_PARA_ALL.PARAM_STATE is '新增 0：新增生效 1：申請修改 2：審核通過 3：審核不通過'
/

comment on column LOTT_PARA_ALL.ORDER_ID is '排序 -1 表示不顯示'
/

comment on column LOTT_PARA_ALL.CREATE_TIME is '新增時間'
/

comment on column LOTT_PARA_ALL.UPDATE_TIME is '修改時間'
/

comment on column LOTT_PARA_ALL.TGI_ID is '第三方廠商代碼'
/

comment on column LOTT_PARA_ALL.CASUAL_GAME_ID is 'LOTT_GROUP_SERIES.GID 遊戲ID'
/

