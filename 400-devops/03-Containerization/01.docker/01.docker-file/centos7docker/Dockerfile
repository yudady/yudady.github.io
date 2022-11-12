FROM centos:8
MAINTAINER tommy<yudady@gmail.com>

# 更新
RUN yum -y update && yum -y install wget vim net-tools yum-utils && curl -fsSL https://get.docker.com | bash -s docker

ENTRYPOINT ["/bin/bash","-c"]

# 構建命令
# docker build -t yudady/centos-8-docker .

# 測試命令
# docker run -it --name centos8docker yudady/centos-8-docker "while true ; do echo tommy; sleep 1 ; done"
# docker run -d --name centos8docker yudady/centos-8-docker "while true ; do echo tommy; sleep 1 ; done"
# 因為沒有前台命令，所以啟動後會直接關閉
