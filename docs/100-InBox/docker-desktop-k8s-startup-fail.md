---
title: docker-desktop-k8s-startup-fail
tags: [k8s , docker]
aliases: [docker-desktop-k8s-startup-fail]
linter-yaml-title-alias: docker-desktop-k8s-startup-fail
date: 2022-09-29 12:03
modified: 2022-09-30 10:22
---

# docker-desktop-k8s-startup-fail

---

## summary

docker 啟動容器端口被佔用：An attempt was made to access a socket in a way forbidden by its access permissions

---

## capturing

docker-desktop 啟動失敗

## tracking

$ tail -f /c/Users/yu_da/AppData/Local/Docker/log.txt

> [!failure] log 資料
> 
> [2022-09-29T14:20:32.975192000Z][com.docker.backend.exe][I] Cluster config received
> [2022-09-29T14:20:32.991282700Z][com.docker.backend.exe][I] (49d39260) 2aa0cf31-BackendAPI S<-C Go-http-client/1.1 GET /forwards/list
> [2022-09-29T14:20:32.992283000Z][com.docker.backend.exe][I] (49d39260) 2aa0cf31-BackendAPI S->C Go-http-client/1.1 GET /forwards/list (1.0003ms): []
> [2022-09-29T14:20:32.992283000Z][com.docker.backend.exe][I] (e746a525) 2aa0cf31-BackendAPI S<-C Go-http-client/1.1 PUT /forwards/expose/port
> [2022-09-29T14:20:32.993281000Z][com.docker.backend.exe][I] (e746a525) 2aa0cf31-BackendAPI S<-C Go-http-client/1.1 bind: {"in_ip":"127.0.0.1","in_port":6443,"out_ip":"127.0.0.1","out_port":6443,"proto":"tcp"}
> [2022-09-29T14:20:33.016500600Z][com.docker.backend.exe][I] adding tcp forward from 127.0.0.1:6443 to 127.0.0.1:6443
> [2022-09-29T14:20:33.016628700Z][com.docker.backend.exe][W] (e746a525) 2aa0cf31-BackendAPI S->C Go-http-client/1.1 PUT /forwards/expose/port (24.3457ms): listen tcp 127.0.0.1:6443: bind: An attempt was made to access a socket in a way forbidden by its access permissions.
> [2022-09-29T14:20:33.017160600Z][GoBackendProcess ][Info ] [common/cmd/com.docker.backend/internal/handlers/ports.go:32 github.com/docker/pinata/common/cmd/com.docker.backend/internal/handlers.(*PortsHandler).ExposePort()
> [2022-09-29T14:20:33.017160600Z][GoBackendProcess ][Info ] [common/pkg/ipc/server.go:180 github.com/docker/pinata/common/pkg/ipc.(*server).wrapHandlerFunc.func1()
> [2022-09-29T14:20:34.025055400Z][com.docker.backend.exe][W] Error while setting up kubernetes: cannot expose the external port: listen tcp 127.0.0.1:6443: bind: An attempt was made to access a socket in a way forbidden by its access permissions.. Retrying in 1s
> 

---

## solutions

> [!note] 解决需要先关闭 Windows NAT 服务
> 
> net stop winnat

---

## reference

[docker启动容器，端口被占用：An attempt was made to access a socket in a way forbidden by its access permissions_冰糖炖冰糖的博客-CSDN博客_docker启动端口被占用](https://blog.csdn.net/xiaoxiao_ziteng/article/details/123988145)
