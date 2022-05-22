# service



```shell

# echo 111 > /usr/share/nginx/html/index.html
# echo 222 > /usr/share/nginx/html/index.html
# echo 333 > /usr/share/nginx/html/index.html


[root@master ~]# kubectl get pods -owide
NAME                                READY   STATUS    RESTARTS   AGE     IP           NODE    NOMINATED NODE   READINESS GATES
nginx-deployment-8475f9f6cc-755tf   1/1     Running   0          13m     172.96.1.6   node2   <none>           <none>
nginx-deployment-8475f9f6cc-br9zs   1/1     Running   0          9m19s   172.96.2.5   node1   <none>           <none>
nginx-deployment-8475f9f6cc-d468t   1/1     Running   0          13m     172.96.2.4   node1   <none>           <none>
[root@master ~]# curl 172.96.2.5
111
[root@master ~]# curl 172.96.2.4
333
[root@master ~]# curl 172.96.1.6
222
[root@master ~]#


# 暴露端口 service 8000
[root@master ~]# kubectl expose deploy nginx-deployment --port=8000 --target-port=80
service/nginx-deployment exposed
[root@master ~]# kubectl get service
NAME               TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)    AGE
kubernetes         ClusterIP   10.96.0.1      <none>        443/TCP    46h
nginx-deployment   ClusterIP   10.96.87.175   <none>        8000/TCP   72s
[root@master ~]# kubectl get svc
NAME               TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)    AGE
kubernetes         ClusterIP   10.96.0.1      <none>        443/TCP    46h
nginx-deployment   ClusterIP   10.96.87.175   <none>        8000/TCP   76s


# 測試：負載均衡
[root@master ~]# kubectl get svc
NAME               TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)    AGE
kubernetes         ClusterIP   10.96.0.1      <none>        443/TCP    46h
nginx-deployment   ClusterIP   10.96.87.175   <none>        8000/TCP   2m53s
[root@master ~]# curl 10.96.87.175:8000
111
[root@master ~]# curl 10.96.87.175:8000
111
[root@master ~]# curl 10.96.87.175:8000
333
[root@master ~]# curl 10.96.87.175:8000
222

# 查標籤
[root@master k8s]# kubectl get pod --show-labels
NAME                                READY   STATUS    RESTARTS   AGE   LABELS
nginx-deployment-8475f9f6cc-755tf   1/1     Running   0          32m   app=my-nginx,pod-template-hash=8475f9f6cc
nginx-deployment-8475f9f6cc-br9zs   1/1     Running   0          28m   app=my-nginx,pod-template-hash=8475f9f6cc
nginx-deployment-8475f9f6cc-d468t   1/1     Running   0          32m   app=my-nginx,pod-template-hash=8475f9f6cc


[root@master k8s]# kubectl apply -f service-ClusterIP.yaml
service/nginx-deployment-service created
[root@master k8s]# kubectl get svc
NAME                       TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
kubernetes                 ClusterIP   10.96.0.1       <none>        443/TCP    46h
nginx-deployment           ClusterIP   10.96.87.175    <none>        8000/TCP   12m
nginx-deployment-service   ClusterIP   10.96.169.148   <none>        8080/TCP   16s

# 集群內使用
[root@master k8s]# kubectl create deploy my-tomcat --image=tomcat
deployment.apps/my-tomcat created

# service-name:port  需要在pods內部使用
root@my-tomcat-b4c9b6565-kmzkw:/usr/local/tomcat# curl nginx-deployment-service:8080
222
root@my-tomcat-b4c9b6565-kmzkw:/usr/local/tomcat# curl nginx-deployment-service:8080
222
root@my-tomcat-b4c9b6565-kmzkw:/usr/local/tomcat# curl nginx-deployment-service:8080
333
root@my-tomcat-b4c9b6565-kmzkw:/usr/local/tomcat# curl nginx-deployment-service:8080
222
root@my-tomcat-b4c9b6565-kmzkw:/usr/local/tomcat# curl nginx-deployment-service:8080
111


# curl serviceName.namespace.svc:8080
root@my-tomcat-b4c9b6565-kmzkw:/usr/local/tomcat# curl nginx-deployment-service.default.svc:8080
111
root@my-tomcat-b4c9b6565-kmzkw:/usr/local/tomcat# curl nginx-deployment-service.default.svc:8080
333
root@my-tomcat-b4c9b6565-kmzkw:/usr/local/tomcat# curl nginx-deployment-service.default.svc:8080
222


# 刪除service
[root@master k8s]# kubectl delete service nginx-deployment
service "nginx-deployment" deleted
[root@master k8s]# kubectl delete service nginx-deployment-service
service "nginx-deployment-service" deleted


# create npde-port
[root@master k8s]# kubectl expose deploy nginx-deployment --port=8000 --target-port=80 --type=NodePort
service/nginx-deployment exposed
[root@master k8s]# kubectl get service
NAME               TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
kubernetes         ClusterIP   10.96.0.1       <none>        443/TCP          2d
nginx-deployment   NodePort    10.96.130.250   <none>        8000:30427/TCP   26s

# 集群外部訪問： NodePort
[root@master k8s]# curl 10.96.130.250:8000
333
[root@master k8s]# curl 10.96.130.250:8000
111
[root@master k8s]# curl 10.96.130.250:8000
333
[root@master k8s]# curl 10.96.130.250:8000
222




```




