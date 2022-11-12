-- ORACLE筆記-使用 CONNECT BY 呈現階層化資料
--
--
--
-- START WITH 條件被用來指定樹狀結構的起始點
-- CONNECT BY 條件用來定義從屬關係，由於關聯欄位分別來自父資料列與子資料列，
-- 父資料列欄位名稱需加上 PRIOR 運算子以為區別
-- LEVEL 可傳回該筆資料所在階層
-- 產生縮排可用 LPAD(' ', 4 * (LEVEL – 1))
-- 神奇的 SYS_CONNECT_BY_PATH(ColName, '/') 能自動組裝出 "Parent/Child/GrandChild"，超方便

-- level
select lpad(' ', 2 * level) || DEPT_ID || '-' || level || '-' || DEPT_NAME AS partName,
       SYS_CONNECT_BY_PATH(DEPT_NAME, '/')                                 AS PartPath,
       Level
from US_DEPARTMENT
--以 PARENT_DEPT_ID = 0 這筆做為起始點開始長樹
start with PARENT_DEPT_ID = 0
--欄位名前方的一元運算符PRIOR用於指定父資料欄位
--故此處為"將 DEPT_ID 等於本筆 PARENT_DEPT_ID 的資料列視為父資料列"
connect by prior DEPT_ID = PARENT_DEPT_ID;

--




