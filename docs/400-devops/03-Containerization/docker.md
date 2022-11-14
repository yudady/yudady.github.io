---
title: docker
tags: []
aliases: [docker]
created_date: 2022-10-20 15:42
updated_date: 2022-11-14 15:28
---

# docker

## ## repository

[github/github-packages](../04-CI-CD-Pipelines/github/github-packages.md)  -  

[cloud.canister.io](https://cloud.canister.io/n/yudady) : free docker registor : login by yahoo email

## develope in docker container

### 1. build image

```Dockerflle
FROM golang:1.18-alpine as dev  
  
WORKDIR /work  
  
FROM golang:1.18--alpine as build  
  
WORKDIR /app  
COPY ./app/* /app/  
RUN go build -o app  
  
FROM alpine as runtime COPY --from=build /app/app /  
CMD ./app
```

###  2. develope in container : mount current folder into 

```shell
docker build --target dev . -t yudady/golang:1.18  

docker run -it -v ${PWD}:/work yudady/golang:1.18 sh  

go version
```

## canister : Free 20 Private docker Repos

```bash
$ docker login --username=yudady cloud.canister.io:5000
Password:
Login Succeeded

Logging in with your password grants your terminal complete access to your account.
For better security, log in with a limited-privilege personal access token. Learn more at https://docs.docker.com/go/access-tokens/


############################################################
$ docker tag d4a5e0eaa84f cloud.canister.io:5000/yudady/myapp

$ docker push cloud.canister.io:5000/yudady/myapp
Using default tag: latest
The push refers to repository [cloud.canister.io:5000/yudady/myapp]
a0d2c4392b06: Pushed
05a9e65e2d53: Pushed
68695a6cfd7d: Pushed
c1dc81a64903: Pushed
8460a579ab63: Pushed
d39d92664027: Pushed
latest: digest: sha256:9eeca44ba2d410e54fccc54cbe9c021802aa8b9836a0bcf3d3229354e4c8870e size: 1569


```

## registry-1.docker.io login

```shell
$ docker login registry-1.docker.io
Username: yudady
Password:
Login Succeeded
```
