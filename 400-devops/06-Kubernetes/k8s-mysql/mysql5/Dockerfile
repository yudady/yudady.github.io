FROM mysql:5.7

USER root

ADD sqls/init.sql /docker-entrypoint-initdb.d/init.sql

EXPOSE 3306


# docker build -t yudady/mysql5:v1 .
# docker push yudady/mysql5:v1

