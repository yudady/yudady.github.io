https://medium.com/k8s%E7%AD%86%E8%A8%98/kubernetes-k8s-pv-pvc-%E5%84%B2%E5%AD%98%E5%A4%A7%E5%B0%8F%E4%BA%8B%E4%BA%A4%E7%B5%A6pv-pvc%E7%AE%A1%E7%90%86-4d412b8bafb5




# fila mysql 權限



grant all privileges on *.* to bill@localhost identified by 'pass' with grant option;

```shell
# create
kubectl apply -f ./templates/
```

```shell
kubectl port-forward mysql-dp-8dfb795cf-k5knz 3306:3306
```



```shell
# delete
kubectl delete -f ./templates/
```

```shell
kubectl get pods -A
```