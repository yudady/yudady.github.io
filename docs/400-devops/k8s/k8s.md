---
title: k8s
tags: [2022-10, devops, k8s]
aliases: [k8s]
created_date: 2022-10-30 14:03
updated_date: 2022-10-31 14:03
---

# k8s

- **🏷️Tags** :   #2022-10 #devops  #k8s 
- Link: [1-pvc-demo.yml](https://raw.githubusercontent.com/yudady/yudady.github.io/main/devops-k8s/k8s-learning/07.pv-pvc/1-pvc-demo.yml)

## command

### port-forward

> [!INFO] ### port-forward 
> kubectl port-forward mysql-dp-8dfb795cf-2hkgm 3306:3306 --address 0.0.0.0

## service

- type: LoadBalancer  
- type: ClusterIP  
- type: NodePort

## ingress

[kubernetes/ingress-nginx](https://github.com/kubernetes/ingress-nginx) : from github

[Helm NGINX Ingress](https://docs.nginx.com/nginx-ingress-controller/installation/installation-with-helm/) : from helm

## dashboard

[kubernetes/dashboard](https://github.com/kubernetes/dashboard) : from github

[kubernetes-dashboard](https://artifacthub.io/packages/helm/k8s-dashboard/kubernetes-dashboard) : from helm
