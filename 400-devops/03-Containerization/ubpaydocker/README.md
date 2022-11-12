# docker-compose


mysql 需要使用 utf8mb4_bin

需要處理 :  https://hub.docker.com/_/mysql



docker compose character-set-server=utf8mb4
command: ['mysqld', '--character-set-server=utf8mb4', '--collation-server=utf8mb4_unicode_ci']
https://stackoverflow.com/questions/45729326/how-to-change-the-default-character-set-of-mysql-using-docker-compose
https://gist.github.com/itkrt2y/c6c3142ef0d44b70121e4cb6a469ff00
https://www.codenong.com/c05664c7c0c08a19cebe/



## mysql , 第一次需要用ROOT連接MYSQL，然後執行下面SQL


````sql
create database `user`;
create database `order`;

CREATE USER 'user-service'@'localhost' IDENTIFIED BY '123456';
CREATE USER 'user-service'@'%' IDENTIFIED BY '123456';
GRANT ALL ON *.* TO 'user-service'@'localhost';
GRANT ALL ON *.* TO 'user-service'@'%';



CREATE USER 'order-service'@'localhost' IDENTIFIED BY '123456';
CREATE USER 'order-service'@'%' IDENTIFIED BY '123456';
GRANT ALL ON *.* TO 'order-service'@'localhost';
GRANT ALL ON *.* TO 'order-service'@'%';

CREATE USER 'db-migration'@'localhost' IDENTIFIED BY '123456';
CREATE USER 'db-migration'@'%' IDENTIFIED BY '123456';
GRANT ALL ON *.* TO 'db-migration'@'localhost';
GRANT ALL ON *.* TO 'db-migration'@'%';

flush privileges;

INSERT INTO `user`.`operator` (`id`, `password`, `state`, `password_salt`, `password_iteration`, `last_password_modify_time`, `role_id`, `create_time`, `update_time`) VALUES
    ('tommyy', '4b9138441288cfd0a409453bbefbbe6f6b7e5a5808f556bbfb1b81f7e6e1c6cd', 'NORMAL', '9d6iisbpyt9b7kgvmkfxq5mvvt747a6al33cxs8lg1k4qmcxmt4k258t7n99', 15370, '2021-12-17 05:55:05', 1001, '2021-12-17 05:54:31', '2021-12-17 05:55:05');

INSERT INTO `user`.`operator_password_history` (`id`, `operator_id`, `password`, `password_salt`, `password_iteration`, `create_time`) VALUES
    (1, 'admin001', 'e9b6349160004a31054706b4bea245ffee8dd1b26421e0c61f7d02474c77ad39', 'uz7fkcprrcx4tejicgnq7gfcy0edmgo8jf6ibojyojipn027qzljjmqvuvms', 11867, '2021-12-17 05:52:30'),
(2, 'tommyy', '4b9138441288cfd0a409453bbefbbe6f6b7e5a5808f556bbfb1b81f7e6e1c6cd', '9d6iisbpyt9b7kgvmkfxq5mvvt747a6al33cxs8lg1k4qmcxmt4k258t7n99', 15370, '2021-12-17 05:55:05');




INSERT INTO `order`.`vendor` (`id`, `en_name`, `cn_name`, `priority`, `deposit_callback_url`, `withdrawal_callback_url`, `withdrawal_reconfirm_url`, `status`, `create_time`, `update_time`, `version`) VALUES
    ('TS00000001', 'PF2-EN', 'PF2-CN', 99, 'https://yudady-callback.herokuapp.com/success', 'https://yudady-callback.herokuapp.com/success', 'https://yudady-callback.herokuapp.com/success', 'NORMAL', '2021-12-17 06:00:56', '2021-12-17 06:02:12', 0),
('UB21PF2SIT', 'PF2-EN', 'PF2-CN', 99, 'https://yudady-callback.herokuapp.com/success', 'https://yudady-callback.herokuapp.com/success', 'https://yudady-callback.herokuapp.com/success', 'NORMAL', '2021-12-17 06:00:56', '2021-12-17 06:02:12', 0);


INSERT INTO `order`.`vendor_allowed_list` (`id`, `vendor_id`, `ip_address`, `deleted`, `create_time`, `update_time`) VALUES
    (1, 'UB21PF2SIT', '127.0.0.1', 0, '2021-12-17 06:05:46', NULL),
    (2, 'TS00000001', '127.0.0.1', 0, '2021-12-17 06:05:46', NULL);


INSERT INTO `order`.`vendor_secret_key` (`id`, `vendor_id`, `secret_key`, `expired_time`, `enabled`, `create_time`, `update_time`) VALUES
    ('0e1c065a-23fb-4f49-a3bc-fc555de74983', 'UB21PF2SIT', '0589b9ca-9a30-4044-a2cb-62053b7d6920', '2023-12-17 06:07:55', 1, '2021-12-17 06:07:55', NULL),
('2d44f6d1-cca6-47cf-a363-ff2ed160c2cf', 'TS00000001', '067c9c64-cda4-4ae7-b9f9-d1e0b319769d', '2023-12-17 06:07:55', 1, '2021-12-17 06:07:55', NULL);



````
