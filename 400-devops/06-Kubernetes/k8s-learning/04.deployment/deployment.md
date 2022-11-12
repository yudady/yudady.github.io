# deployment


```shell
# kubectl exec nginx-deployment-8475f9f6cc-7qhgb --container=my-nginx --namespace=default --stdin=true --tty=true -- /bin/bash
```




```shell
[root@master k8s]# kubectl create deployment mytomcat --image=tomcat
deployment.apps/mytomcat created
[root@master k8s]# kubectl get pods
NAME                        READY   STATUS              RESTARTS   AGE
mytomcat-58b8488d44-bmqqc   0/1     ContainerCreating   0          24s
[root@master k8s]# kubectl delete deployment mytomcat
deployment.apps "mytomcat" deleted
[root@master k8s]# kubectl get pods
No resources found in default namespace.


```


```shell
# delete pod
[root@master ~]# kubectl delete -n default pod nginx-deployment-8475f9f6cc-47txb
pod "nginx-deployment-8475f9f6cc-47txb" deleted
[root@master ~]#

```