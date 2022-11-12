

```shell
[root@hdss7-11 03-ingress]# kubectl apply -f 01-ingress.yaml 
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
validatingwebhookconfiguration.admissionregistration.k8s.io/ingress-nginx-admission created
serviceaccount/ingress-nginx-admission created
clusterrole.rbac.authorization.k8s.io/ingress-nginx-admission created
clusterrolebinding.rbac.authorization.k8s.io/ingress-nginx-admission created
role.rbac.authorization.k8s.io/ingress-nginx-admission created
rolebinding.rbac.authorization.k8s.io/ingress-nginx-admission created
job.batch/ingress-nginx-admission-create created
job.batch/ingress-nginx-admission-patch created

```

## 检查安装的结果
```shell
[root@hdss7-11 03-ingress]# kubectl get pod,svc -n ingress-nginx
NAME                                            READY   STATUS      RESTARTS   AGE
pod/ingress-nginx-admission-create-kpptw        0/1     Completed   0          58s
pod/ingress-nginx-admission-patch-ng6p2         0/1     Completed   0          58s
pod/ingress-nginx-controller-6cb6fdd64b-hhhxz   1/1     Running     0          58s

NAME                                         TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
service/ingress-nginx-controller             NodePort    172.16.42.147   <none>        80:30035/TCP,443:30603/TCP   58s
service/ingress-nginx-controller-admission   ClusterIP   172.16.110.85   <none>        443/TCP                      58s
[root@hdss7-11 03-ingress]# 

```


# 02-test-ingress.yaml


```shell
[root@hdss7-11 03-ingress]# kubectl apply -f 02-test-ingress.yaml 
deployment.apps/myapp-depl created
service/myapp-svc created
ingress.networking.k8s.io/myapp-ing created


[root@hdss7-11 03-ingress]# kubectl get pods,svc,ing -o wide
NAME                             READY   STATUS    RESTARTS   AGE   IP            NODE                NOMINATED NODE   READINESS GATES
pod/myapp-depl-777f7fdbb-kpr54   1/1     Running   0          87s   192.168.1.6   hdss7-21.host.com   <none>           <none>
pod/myapp-depl-777f7fdbb-snl2c   1/1     Running   0          87s   192.168.2.5   hdss7-22.host.com   <none>           <none>

NAME                 TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)   AGE   SELECTOR
service/kubernetes   ClusterIP   172.16.0.1      <none>        443/TCP   58m   <none>
service/myapp-svc    ClusterIP   172.16.54.194   <none>        80/TCP    87s   app=myapp

NAME                                  CLASS   HOSTS          ADDRESS     PORTS   AGE
ingress.networking.k8s.io/myapp-ing   nginx   myapp.od.com   10.4.7.21   80      87s
[root@hdss7-11 03-ingress]# 


```


```shell
[root@hdss7-11 03-ingress]# curl  192.168.1.6
Hello MyApp | Version: v1 | <a href="hostname.html">Pod Name</a>
[root@hdss7-11 03-ingress]# curl 192.168.2.5 
Hello MyApp | Version: v1 | <a href="hostname.html">Pod Name</a>




[root@hdss7-11 03-ingress]# curl 172.16.54.194/hostname.html
myapp-depl-777f7fdbb-kpr54
[root@hdss7-11 03-ingress]# curl 172.16.54.194/hostname.html
myapp-depl-777f7fdbb-snl2c



```


# 使用瀏覽器
> http://myapp.od.com:30035/hostname.html


# nginx proxy in hdss7-12
```shell
vi /etc/nginx/conf.d/harbor.od.com.conf

server {
listen       80;
server_name  harbor.od.com;

    client_max_body_size 1000m;

    location / {
        proxy_pass http://127.0.0.1:180;
    }
}


[root@hdss7-12 yum.repos.d]# sudo systemctl start nginx
[root@hdss7-12 yum.repos.d]# sudo systemctl enable nginx
Created symlink from /etc/systemd/system/multi-user.target.wants/nginx.service to /usr/lib/systemd/system/nginx.service.
[root@hdss7-12 yum.repos.d]# systemctl status nginx
● nginx.service - nginx - high performance web server
   Loaded: loaded (/usr/lib/systemd/system/nginx.service; enabled; vendor preset: disabled)
   Active: active (running) since Fri 2021-12-31 19:23:31 PST; 14s ago
     Docs: http://nginx.org/en/docs/
 Main PID: 2099 (nginx)
   CGroup: /system.slice/nginx.service
           ├─2099 nginx: master process /usr/sbin/nginx -c /etc/nginx/nginx.conf
           ├─2100 nginx: worker process
           └─2101 nginx: worker process

Dec 31 19:23:31 hdss7-12.host.com systemd[1]: Starting nginx - high performance web server...
Dec 31 19:23:31 hdss7-12.host.com systemd[1]: Started nginx - high performance web server.



[root@hdss7-12 yum.repos.d]# vi /etc/nginx/conf.d/harbor.od.com.conf
[root@hdss7-12 yum.repos.d]# nginx -t
nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
nginx: configuration file /etc/nginx/nginx.conf test is successful


[root@hdss7-12 yum.repos.d]# systemctl restart nginx.service


```


# nginx 反帶 ingress  
> 30035 是ingress 端口

```shell

[root@hdss7-12 yum.repos.d]# vi /etc/nginx/conf.d/od.com.conf
upstream default_backend_nginx {
    server 10.4.7.21:30035    max_fails=3 fail_timeout=10s;
    server 10.4.7.22:30035    max_fails=3 fail_timeout=10s;
}
server {
    server_name *.od.com;

    location / {
        proxy_pass http://default_backend_nginx;
        proxy_set_header Host  $http_host;
        proxy_set_header x-forwarded-for $proxy_add_x_forwarded_for;
    }
}



[root@hdss7-12 yum.repos.d]# nginx -t
nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
nginx: configuration file /etc/nginx/nginx.conf test is successful
[root@hdss7-12 yum.repos.d]# nginx -s reload
[root@hdss7-12 yum.repos.d]#


```

# 11机器,解析域名：
~]# vi /var/named/od.com.zone
前滚serial
traefik            A    10.4.7.10

~]# systemctl restart named


[myapp](http://myapp.od.com/hostname.html)