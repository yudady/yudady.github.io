GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' identified by 'root' with grant option;
CREATE USER 'tommy'@'%' IDENTIFIED BY 'tommy'
GRANT ALL PRIVILEGES ON *.* TO 'tommy'@'%' identified by 'root' with grant option;
FLUSH PRIVILEGES;
CREATE DATABASE IF NOT EXISTS `k8sDb`;
USE `k8sDb`;
