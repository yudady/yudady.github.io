---
title: minikube
tags: [2022-10, devops, docker, choco, k8s]
aliases: [minikube]
created_date: 2022-10-28 12:08
updated_date: 2022-11-05 11:09
---

# minikube

- **🏷️Tags** :   #2022-10 #devops  #docker  #k8s
- Link: [minikube start | minikube](https://minikube.sigs.k8s.io/docs/start/)

## install

```shell
choco install minikube
```

### start minikube

#### 1. Start a cluster using the docker driver:

```shell
$ minikube start


😄  minikube v1.27.1 on Microsoft Windows 10 Pro 10.0.19045 Build 19045
✨  Using the docker driver based on user configuration
📌  Using Docker Desktop driver with root privileges
👍  Starting control plane node minikube in cluster minikube
🚜  Pulling base image ...
    > gcr.io/k8s-minikube/kicbase:  0 B [________________________] ?% ? p/s 40s
🔥  Creating docker container (CPUs=2, Memory=3875MB) ...
🐳  Preparing Kubernetes v1.25.2 on Docker 20.10.18 ...
    ▪ Generating certificates and keys ...
    ▪ Booting up control plane ...
    ▪ Configuring RBAC rules ...
🔎  Verifying Kubernetes components...
    ▪ Using image gcr.io/k8s-minikube/storage-provisioner:v5
🌟  Enabled addons: default-storageclass, storage-provisioner
🏄  Done! kubectl is now configured to use "minikube" cluster and "default" namespace by default

```

#### 2. To make docker the default driver:

```bash
$ minikube config set driver docker
❗  These changes will take effect upon a minikube delete and then a minikube start



$ minikube stop
$ minikube config set memory 8192
$ minikube config set cpus 4

$ minikube config view
- memory: 8192
- cpus: 4
- driver: docker

$ minikube start - cpus 6 - memory 12288

$ minikube start --nodes 2 -p multinode-demo

```

##### powershell

```powershell
minikube docker-env
```

#### 3. To make delete

```shell
$ minikube delete
🙄  "minikube" profile does not exist, trying anyways.
💀  Removed all traces of the "minikube" cluster.
```

## Deploy applications

Create a sample deployment and expose it on port 80:

```shell
kubectl create deployment k8s02-minikube --image=docker.io/nginx:1.23
kubectl expose deployment k8s02-minikube --type=NodePort --port=80
```

It may take a moment, but your deployment will soon show up when you run:

```shell
kubectl get services k8s02-minikube
```

The easiest way to access this service is to let minikube launch a web browser for you:

```shell
minikube service k8s02-minikube
```

Alternatively, use kubectl to forward the port:

```shell
kubectl port-forward service/k8s02-minikube 7080:80
```

Tada! Your application is now available at [http://localhost:7080/](http://localhost:7080/).

## Manage your cluster

Pause Kubernetes without impacting deployed applications:

```shell
minikube pause
```

Unpause a paused instance:

```shell
minikube unpause
```

Halt the cluster:

```shell
minikube stop
```

Change the default memory limit (requires a restart):

```shell
minikube config set memory 9001
```

Browse the catalog of easily installed Kubernetes services:

```shell
minikube addons list
```

Create a second cluster running an older Kubernetes release:

```shell
minikube start -p aged --kubernetes-version=v1.16.1
```

Delete all of the minikube clusters:

```shell
minikube delete --all
```

## tools

-   [Draft](https://draft.sh/)
-   [Okteto](https://github.com/okteto/okteto)
-   [Skaffold](https://github.com/GoogleContainerTools/skaffold)

## reference

- [1.Kubernetes 简介_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1Tg411P7EB/?p=1&vd_source=6bd04a20c72eb5cca642210346af7081)
