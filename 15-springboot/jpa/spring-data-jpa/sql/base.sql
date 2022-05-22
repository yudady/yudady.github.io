
show variables like '%time_zone%';



set  time_zone = '+8:00';

SELECT @@global.time_zone;


SELECT @@GLOBAL.time_zone, @@SESSION.time_zone;


show variables like '%time_zone%';

set time_zone = '+08:00';
set global time_zone = '+08:00';




