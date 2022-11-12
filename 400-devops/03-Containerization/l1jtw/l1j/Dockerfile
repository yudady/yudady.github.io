FROM openjdk:8

ENV LANG C.UTF-8
WORKDIR /opt/l1jtw

ADD L1J-TW_3.80c.tar.gz /opt/l1jtw/
ADD config/* /opt/l1jtw/L1J-TW_3.80c/config/


# Set TimeZone
ENV TZ=Asia/Taipei
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone


RUN sed -i s/L1JDB_HOST/mysql5/ /opt/l1jtw/L1J-TW_3.80c/config/server.properties
#RUN sed -i s/L1JDB_ACCOUNT/${L1JDB_ACCOUNT}/ /opt/l1jtw/L1J-TW_3.80c/config/server.properties
#RUN sed -i s/L1JDB_PASSWORD/${L1JDB_PASSWORD}/ /opt/l1jtw/L1J-TW_3.80c/config/server.properties

WORKDIR /opt/l1jtw/L1J-TW_3.80c/
CMD sh ./ServerStart.sh
