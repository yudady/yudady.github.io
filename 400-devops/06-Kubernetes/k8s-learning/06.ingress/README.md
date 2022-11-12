# [ingress : service的統一往關入口](https://kubernetes.io/zh/docs/concepts/services-networking/ingress/)

```textmate
Ingress 是对集群中服务的外部访问进行管理的 API 对象，典型的访问方式是 HTTP。

Ingress 可以提供负载均衡、SSL 终结和基于名称的虚拟托管。
```

[install](https://kubernetes.github.io/ingress-nginx/deploy/)

```shell
# NodePort

# Loadbalance
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.1.0/deploy/static/provider/cloud/deploy.yaml

[root@master k8s]# kubectl apply -f ./ingress.deploy.yaml
namespace/ingress-nginx created
serviceaccount/ingress-nginx created
configmap/ingress-nginx-controller created
clusterrole.rbac.authorization.k8s.io/ingress-nginx created
clusterrolebinding.rbac.authorization.k8s.io/ingress-nginx created
role.rbac.authorization.k8s.io/ingress-nginx created
rolebinding.rbac.authorization.k8s.io/ingress-nginx created
service/ingress-nginx-controller-admission created
service/ingress-nginx-controller created
deployment.apps/ingress-nginx-controller created
ingressclass.networking.k8s.io/nginx created
validatingwebhookconfiguration.admissionregistration.k8s.io/ingress-nginx-admission created
serviceaccount/ingress-nginx-admission created
clusterrole.rbac.authorization.k8s.io/ingress-nginx-admission created
clusterrolebinding.rbac.authorization.k8s.io/ingress-nginx-admission created
role.rbac.authorization.k8s.io/ingress-nginx-admission created
rolebinding.rbac.authorization.k8s.io/ingress-nginx-admission created
job.batch/ingress-nginx-admission-create created
job.batch/ingress-nginx-admission-patch created
[root@master k8s]#

[root@master k8s]# kubectl get pods --namespace=ingress-nginx
NAME                                      READY   STATUS      RESTARTS   AGE
ingress-nginx-admission-create--1-b8r7s   0/1     Completed   0          50s
ingress-nginx-admission-patch--1-6c7cg    0/1     Completed   1          50s
ingress-nginx-controller-54bfb9bb-bjbts   1/1     Running     0          50s
[root@master k8s]#


[root@master k8s]# kubectl get svc -A
NAMESPACE              NAME                                 TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
default                kubernetes                           ClusterIP      10.96.0.1       <none>        443/TCP                      2d
default                service-nodeport                     NodePort       10.96.44.53     <none>        8000:30486/TCP               32m
ingress-nginx          ingress-nginx-controller             LoadBalancer   10.96.15.61     <pending>     80:32411/TCP,443:30176/TCP   3m39s
ingress-nginx          ingress-nginx-controller-admission   ClusterIP      10.96.27.115    <none>        443/TCP                      3m39s
kube-system            kube-dns                             ClusterIP      10.96.0.10      <none>        53/UDP,53/TCP,9153/TCP       2d
kubernetes-dashboard   dashboard-metrics-scraper            ClusterIP      10.96.144.115   <none>        8000/TCP                     27h
kubernetes-dashboard   kubernetes-dashboard                 NodePort       10.96.243.112   <none>        443:30712/TCP                27h
[root@master k8s]#




kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=120s

# 讓我們創建一個簡單的 Web 服務器和相關的服務：
kubectl create deployment demo --image=httpd --port=80
kubectl expose deployment demo

# 然後創建一個入口資源。以下示例使用映射到 localhost 的主機：
kubectl create ingress demo-localhost --class=nginx \
  --rule=demo.localdev.me/*=demo:80
  
  
# 現在，將本地端口轉發到入口控制器：
kubectl port-forward --namespace=ingress-nginx service/ingress-nginx-controller 8080:80
  
# 此時，如果您訪問 http://demo.localdev.me:8080/，您應該會看到一個 HTML 頁面，告訴您“它有效！”。
  
  
# 在線測試¶
# 如果您的 Kubernetes 集群是支持 type 服務的“真實”集群LoadBalancer，它將為入口控制器分配一個外部 IP 地址或 FQDN。
# 您可以使用以下命令查看該 IP 地址或 FQDN：
[root@master k8s]# kubectl get service ingress-nginx-controller --namespace=ingress-nginx
NAME                       TYPE           CLUSTER-IP    EXTERNAL-IP   PORT(S)                      AGE
ingress-nginx-controller   LoadBalancer   10.96.15.61   <pending>     80:32411/TCP,443:30176/TCP   17m
[root@master k8s]#

  
  
# 這將是EXTERNAL-IP領域。如果該字段顯示<pending>，則表示您的 Kubernetes 集群無法配置負載均衡器（通常，這是因為它不支持類型為 的服務LoadBalancer）。
  
  
  
  
#
#
# START
# START
# START
# START
#
#
  
  
  
  
# type: LoadBalancer to NodePort
[root@master k8s]# kubectl apply -f ./ingress.deploy.yaml
namespace/ingress-nginx created
serviceaccount/ingress-nginx created
configmap/ingress-nginx-controller created
clusterrole.rbac.authorization.k8s.io/ingress-nginx created
clusterrolebinding.rbac.authorization.k8s.io/ingress-nginx created
role.rbac.authorization.k8s.io/ingress-nginx created
rolebinding.rbac.authorization.k8s.io/ingress-nginx created
service/ingress-nginx-controller-admission created
service/ingress-nginx-controller created
deployment.apps/ingress-nginx-controller created
ingressclass.networking.k8s.io/nginx created
validatingwebhookconfiguration.admissionregistration.k8s.io/ingress-nginx-admission created
serviceaccount/ingress-nginx-admission created
clusterrole.rbac.authorization.k8s.io/ingress-nginx-admission created
clusterrolebinding.rbac.authorization.k8s.io/ingress-nginx-admission created
role.rbac.authorization.k8s.io/ingress-nginx-admission created
rolebinding.rbac.authorization.k8s.io/ingress-nginx-admission created
job.batch/ingress-nginx-admission-create created
job.batch/ingress-nginx-admission-patch created
[root@master k8s]# kubectl get pods --namespace=ingress-nginx
NAME                                      READY   STATUS      RESTARTS   AGE
ingress-nginx-admission-create--1-8d72g   0/1     Completed   0          16s
ingress-nginx-admission-patch--1-tc4qz    0/1     Completed   0          16s
ingress-nginx-controller-54bfb9bb-hxt2f   0/1     Running     0          16s
[root@master k8s]#

```





