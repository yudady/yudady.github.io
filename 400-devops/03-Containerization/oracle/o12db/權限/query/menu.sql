select lpad(' ', 2 * level) || menu_id || '-' || level || '-' || menu_name AS partName,
       SYS_CONNECT_BY_PATH(menu_name, '/')                                 AS PartPath,
       Level,
       IS_LEAF,
       IS_DISPLAY,
       IS_BUTTON,
       MENU_KEY
from US_MENU_ITEM
--以 PARENT_DEPT_ID = 0 這筆做為起始點開始長樹
start with PARENT_ID = 0
--欄位名前方的一元運算符PRIOR用於指定父資料欄位
--故此處為"將 DEPT_ID 等於本筆 PARENT_DEPT_ID 的資料列視為父資料列"
connect by prior menu_id = PARENT_ID;



select *
from US_MENU_ITEM
where MENU_KEY = 'PER_USER_ACCOUNT_MODIFY_BTN';

select *
from US_MENU_ITEM
where PARENT_ID in ('21696', '107')
order by CREATE_TIME desc;

select menu_id, url, menu_name, description, parent_id, menu_key
from US_MENU_ITEM
where PARENT_ID in ('2', '3', '4', '5', '27', '21696')
order by PARENT_ID, display_order;


select menu_id,
       url,
       menu_name,
       description,
       parent_id,
       is_leaf,
       is_display,
       display_order,
       tree_level,
       is_button,
       menu_key
from US_MENU_ITEM
where PARENT_ID in ('2', '3', '4', '5', '27', '21696')
order by PARENT_ID, display_order;


