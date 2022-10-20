# [Install Docker Engine on CentOS](https://docs.docker.com/engine/install/centos/)


```shell
yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
                  
                  
yum install -y yum-utils

yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo
    
    
yum install docker-ce docker-ce-cli containerd.io

systemctl enable docker --now



```








