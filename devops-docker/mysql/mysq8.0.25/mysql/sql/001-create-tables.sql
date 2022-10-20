-- sample privileges ----
-- -- drop ----
DROP TABLE IF EXISTS `t1`;

-- -- create ----
create table IF not exists `t1`
(
    `id`   INT(8) AUTO_INCREMENT,
    `name` VARCHAR(20) NOT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;



show databases;


