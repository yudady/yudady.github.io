FROM centos:7

ENV MYPATH /usr/local
ENV JAVA_BASE /usr/local/java

WORKDIR $MYPATH

#安装vim编辑器
RUN yum -y install vim && \
    yum -y install wget && \
    #安装ifconfig命令查看网络IP
    yum -y install net-tools && \
    #安装java8及lib库
    yum -y install glibc.i686 && \
    mkdir -p $JAVA_BASE && \
    cd ${JAVA_BASE} && \
    wget https://download.java.net/openjdk/jdk8u41/ri/openjdk-8u41-b04-linux-x64-14_jan_2020.tar.gz && \
    tar -zxvf openjdk-8u41-b04-linux-x64-14_jan_2020.tar.gz -C /usr/local/java

#配置java环境变量
ENV JAVA_HOME ${JAVA_BASE}/java-se-8u41-ri
ENV JRE_HOME $JAVA_HOME/jre
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib:$CLASSPATH
ENV PATH $JAVA_HOME/bin:$PATH

EXPOSE 80

RUN echo $JAVA_HOME \
  echo $JRE_HOME \
  echo $MYPATH \
  echo $PATH \
  echo "success--------------ok"
CMD /bin/bash