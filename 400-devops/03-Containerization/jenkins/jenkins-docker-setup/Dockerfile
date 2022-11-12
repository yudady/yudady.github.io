FROM jenkins/jenkins:lts-jdk11

USER root

RUN curl -sSL https://get.docker.com/ | sh

RUN usermod -aG docker root

USER jenkins




### sudo chmod 777 /var/run/docker.sock
### docker exec docker-in-docker-jenkins docker ps



# example
# docker build --tag docker-in-docker-jenkins .
# docker run --rm --group-add 0 -v "/var/run/docker.sock:/var/run/docker.sock" -p 8080:8080 --name jenkins docker-in-docker-jenkins
# docker exec jenkins docker ps


