# ingress-nginx 安裝


## 01-ingress-nginx-base.yaml 如果您沒有 Helm或者您更喜歡使用 YAML 清單，則可以運行以下命令：
> kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.1.0/deploy/static/provider/cloud/deploy.yaml


## 02-ingress-nginx-vmware.yaml 暴露模式 （Bare metal clusters），vm 裝 k8s 只能用NodePort
> kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.1.0/deploy/static/provider/baremetal/deploy.yaml


NGINX Ingress Controller 是使用 Kubernetes Ingress 資源物件構建的，用 ConfigMap 來儲存 Nginx 配置的一種 Ingress Controller 實現。

要使用 Ingress 對外暴露服務，就需要提前安裝一個 Ingress Controller，我們這裡就先來安裝 NGINX Ingress Controller，由於 nginx-ingress 所在的節點需要能夠訪問外網，這樣域名可以解析到這些節點上直接使用，所以需要讓 nginx-ingress 繫結節點的 80 和 443 埠，所以可以使用 hostPort 來進行訪問，當然對於線上環境來說為了保證高可用，一般是需要執行多個 nginx-ingress 例項的，然後可以用一個 nginx/haproxy 作為入口，通過 keepalived 來訪問邊緣節點的 vip 地址。



[ingress-nginx](https://kubernetes.github.io/ingress-nginx/deploy/baremetal/)

# 通過 NodePort 服務



https://kubernetes.github.io/ingress-nginx/kubectl-plugin/


```shell
[root@hdss7-11 04-Ingress]# kubectl get endpoints
NAME         ENDPOINTS        AGE
kubernetes   10.4.7.11:6443   3d19h
[root@hdss7-11 04-Ingress]# 

```


# traffic 


```shell

[root@hdss7-11 ~]# kubectl get nodes
NAME                STATUS   ROLES                  AGE    VERSION
hdss7-11.host.com   Ready    control-plane,master   5d9h   v1.23.1
hdss7-21.host.com   Ready    <none>                 5d2h   v1.23.1
hdss7-22.host.com   Ready    <none>                 5d2h   v1.23.1

# 將 Ingress Node 加上 lable
$ kubectl label node hdss7-11.host.com k8s-app=traefik-ingress
```

http://traefik-web-ui.od.com:32716
http://traefik-web-ui.od.com:32606


# 80
http://hdss7-11.host.com:32716

# 8080
http://hdss7-11.host.com:32606



watch -n 1 kubectl get pod,svc -n ingress-nginx


kubectl describe pod/ingress-nginx-controller-78b7f85775-cb989 -n ingress-nginx



kubectl delete namespace nginx-ingress
kubectl delete clusterrole nginx-ingress
kubectl delete clusterrolebinding nginx-ingress
kubectl delete -f common/crds/
kubectl delete -A ValidatingWebhookConfiguration ingress-nginx-admission







