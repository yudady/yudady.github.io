# jenkins


# 200机器，做共享存储的客户端：

````shell
[root@hdss7-200 ~]# yum install nfs-utils -y
Loaded plugins: fastestmirror, langpacks
Determining fastest mirrors

[root@hdss7-200 ~]# vi /etc/exports
[root@hdss7-200 ~]# mkdir /data/nfs-volume

[root@hdss7-200 ~]# mkdir -p /data/nfs-volume
[root@hdss7-200 ~]# systemctl start nfs
[root@hdss7-200 ~]# systemctl enable nfs
Created symlink from /etc/systemd/system/multi-user.target.wants/nfs-server.service to /usr/lib/systemd/system/nfs-server.service.
````

# build jenkins docker image
```shell
yu_da@tommy-msi MINGW64 /d/tommy/unistar/work/unistar4me/60.k8s/k8s-learning/linux/04-jenkins (master)
$ docker build . -t yudady/jenkins:lts-jdk11
[+] Building 105.0s (8/8) FINISHED
 

Use 'docker scan' to run Snyk tests against images to find vulnerabilities and learn how to fix them

yu_da@tommy-msi MINGW64 /d/tommy/unistar/work/unistar4me/60.k8s/k8s-learning/linux/04-jenkins (master)
$ docker images
REPOSITORY                    TAG         IMAGE ID       CREATED          SIZE
yudady/jenkins                lts-jdk11   6d7ed8109824   15 seconds ago   951MB
gcr.io/k8s-minikube/kicbase   v0.0.28     e2a6c047bedd   3 months ago     1.08GB

```



```shell
[root@hdss7-200 data]# vi /var/named/od.com.zone
jenkins            A   10.4.7.11

[root@hdss7-200 data]# systemctl restart named

[root@hdss7-200 data]# dig -t A jenkins.od.com @10.4.7.200 +short
10.4.7.11

```


```shell
[root@hdss7-11 04-jenkins]# kubectl apply -f 01-jenkins.yaml 
namespace/infra created
deployment.apps/jenkins created
service/jenkins created
ingress.networking.k8s.io/jenkins created

[root@hdss7-11 04-jenkins]# kubectl get pods,svc,ing -n infra -o wide
NAME                          READY   STATUS    RESTARTS   AGE     IP            NODE                NOMINATED NODE   READINESS GATES
pod/jenkins-7ff6f49d5-c7fm2   1/1     Running   0          3m22s   192.168.2.6   hdss7-22.host.com   <none>           <none>

NAME              TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)   AGE     SELECTOR
service/jenkins   ClusterIP   172.16.1.180   <none>        80/TCP    3m22s   app=jenkins

NAME                                CLASS    HOSTS            ADDRESS     PORTS   AGE
ingress.networking.k8s.io/jenkins   <none>   jenkins.od.com   10.4.7.21   80      2m7s
[root@hdss7-11 04-jenkins]# 


```



>  jenkins.od.com:30035


[jenkins](http://jenkins.od.com/)