FROM mysql:5.7

USER root

ADD sqls/init.sql /docker-entrypoint-initdb.d/init.sql

EXPOSE 3306


# docker build -t yudady/mysql-init:v1 .
# docker push yudady/mysql-init:v1

