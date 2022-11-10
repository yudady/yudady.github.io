---
title: k8s
tags: [2022-10, devops, k8s]
aliases: [k8s]
created_date: 2022-10-30 14:03
updated_date: 2022-11-09 14:28
---

# k8s

- **🏷️Tags** :   #2022-10 #devops  #k8s 
- Link: [1-pvc-demo.yml](https://raw.githubusercontent.com/yudady/yudady.github.io/main/devops-k8s/k8s-learning/07.pv-pvc/1-pvc-demo.yml)

## command

### Config

```
# help
kubectl config 

#${HOME}/.kube/config
#kubectl config --kubeconfig="C:\someotherfolder\config"
#$KUBECONFIG
```

### contexts

```shell
#get the current context
kubectl config current-context

#get and set contexts
kubectl config get-contexts
kubectl config use-context minikube
```

### get command

```
kubectl get <resource>

#examples
kubectl get pods
kubectl get deployments
kubectl get services
kubectl get configmaps
kubectl get secrets
kubectl get ingress
```

### other

```
## port-forward
kubectl port-forward mysql-dp-8dfb795cf-2hkgm 3306:3306 --address 0.0.0.0

## Namespaces
kubectl get namespaces
kubectl create namespace test
kubectl get pods -n test

## Describe command
kubectl describe <resource> <name>

## Version
kubectl version
```

## service

- type: LoadBalancer  
- type: ClusterIP  
- type: NodePort

## ingress

[kubernetes/ingress-nginx](https://github.com/kubernetes/ingress-nginx) : from [github](../github/000-MOC-GitHub.md)

[Helm NGINX Ingress](https://docs.nginx.com/nginx-ingress-controller/installation/installation-with-helm/) : from [helm](helm.md)

## dashboard

[kubernetes/dashboard](https://github.com/kubernetes/dashboard) : from [github](../github/000-MOC-GitHub.md)

[kubernetes-dashboard](https://artifacthub.io/packages/helm/k8s-dashboard/kubernetes-dashboard) : from [helm](helm.md)

## tools

- [minikube](minikube.md) : k8s
- [skaffold](../skaffold.md) : k8s 開發工具
- service mesh & **istio** : sidecar , 保護 k8s
- kiali :  k8s 視圖整合 , labels:  **app**: xxxxx
- argocd :  cd , auto pull k8s yaml file from github 
- prometheus : cd 監控  =>  labels:  **release: prometheus**
- EFK(**Fluent-Bit**) : log
- kustmize : yaml file 合併
- okteto : online k8s , 部屬 pod
- buildah :  OCI image builder : like docker build
- kaniko : k8s 中構建 image 工具
- skopeo :  OCI 鏡像命令 :  skopeo copy -dest-tls-verifymfalse docker-daemon:au.icr.io/rhay/helloworld:ve.1 oci:helloworld_oci
- dive : 命令用來查看 image 狀況
- pulumi : IaC

---

![](images/k8s-202211061009.png)

## ref

- [Kubernetes Tutorial for Beginners [FULL COURSE in 4 Hours] - YouTube](https://www.youtube.com/watch?v=X48VuDVv0do)
- [devops tools](https://www.youtube.com/watch?v=1id6ERvfozo&list=PLy7NrYWoggjxKDRWLqkd4Kbt84XEerHhB)
- [Just me and Opensource - YouTube](https://www.youtube.com/c/wenkatn-justmeandopensource/playlists)
- [Visual Studio Code and Kubernetes plugin for beginners - YouTube](https://www.youtube.com/watch?v=Si6og3Wa2Hg&t=59s)
