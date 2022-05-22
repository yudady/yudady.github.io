FROM java:8-jdk
MAINTAINER tommy

# RUN apt-get update
RUN apt-get install -y wget tar

USER root
RUN mkdir /tomcat
WORKDIR /tomcat
RUN cd /tomcat

ADD https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.73/bin/apache-tomcat-8.5.73.tar.gz .

RUN cd /tomcat

RUN tar zxvf apache-tomcat-8.5.73.tar.gz

CMD ["/tomcat/apache-tomcat-8.5.73/bin/catalina.sh", "run"]

EXPOSE 8080