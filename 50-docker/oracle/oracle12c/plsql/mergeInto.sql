BEGIN
    EXECUTE IMMEDIATE 'drop table TOMSOUR';
    --EXECUTE IMMEDIATE 'drop table table_name purge';  --- purge prevents saving it in the recyclebin
EXCEPTION
    WHEN OTHERS
        THEN
            DBMS_OUTPUT.put_line(SQLERRM);
END;
BEGIN
    EXECUTE IMMEDIATE 'drop table TOMDEST';
    --EXECUTE IMMEDIATE 'drop table table_name purge';  --- purge prevents saving it in the recyclebin
EXCEPTION
    WHEN OTHERS
        THEN
            DBMS_OUTPUT.put_line(SQLERRM);
END;


-- 建立來源 Temp Table
create table TOMSOUR
(
    aa number,
    bb varchar2(100)
);

-- 建立要比對的目的 Temp Table
create table TOMDEST
(
    aa number,
    bb varchar2(100)
);

-- 新增來源資料
insert into tomSour
values (1, '100');
insert into tomSour
values (2, '200');
insert into tomSour
values (3, '300');
insert into tomSour
values (4, '400');
insert into tomSour
values (5, '500');

-- 新增目的資料
insert into tomDest
values (1, 'abc');
insert into tomDest
values (3, 'xyz');

commit;

-- 開始比對
-- 若存在於來源, 則更改目的 Table 的 bb 欄位
-- 若不存在於來源, 則新增到目地 Table
begin
    merge into tomDest d
    using tomSour s
    on (d.aa = s.aa)
    when MATCHED then
        update
        set d.bb = d.bb || ' to ' || s.bb
    when NOT MATCHED then
        insert (aa, bb) values (s.aa, 'New: ' || s.bb);
end;
commit;


-- 查看目的資料
select *
from tomDest;