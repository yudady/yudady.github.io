GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' identified by 'root' with grant option;
CREATE USER 'tommy'@'%' IDENTIFIED BY 'tommy';
GRANT ALL PRIVILEGES ON *.* TO 'tommy'@'%' identified by 'tommy' with grant option;
FLUSH PRIVILEGES;
CREATE DATABASE IF NOT EXISTS `k8sDb`;
USE `k8sDb`;

CREATE TABLE IF NOT EXISTS tasks
(
    task_id     INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    start_date  DATE,
    due_date    DATE,
    status      TINYINT      NOT NULL,
    priority    TINYINT      NOT NULL,
    description TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE = INNODB;

SHOW tables;
SHOW databases;

