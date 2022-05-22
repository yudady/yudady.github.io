show variables like '%time_zone%';


set time_zone = '+08:00';
set global time_zone = '+08:00';


SELECT @@GLOBAL.time_zone, @@SESSION.time_zone;







