# Base image
FROM mysql:8.0.25

USER root

COPY ./mysql/my.cnf /etc/mysql/conf.d/my.cnf
RUN chmod 644 /etc/mysql/conf.d/*.cnf

USER mysql

