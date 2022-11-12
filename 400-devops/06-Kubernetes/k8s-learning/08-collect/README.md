# test

```shell
kubectl get pods
```


```shell
kubectl get all -owide
```

```shell 
kubectl port-forward service/svc-myapp 8080:80 --address 0.0.0.0
```



```shell
for i in {1..10}
do
   curl localhost:8000
done

```

###

```shell
yu_da@tommy-msi MINGW64 /d/tommy/github-repo/yudady/yudady.github.io/devops-k8s/k8s-learning (main)
$ for i in {1..5}
> do
>    curl localhost:8000
> done
Hello MyApp | Version: v1 | <a href="hostname.html">Pod Name</a>
Hello MyApp | Version: v2 | <a href="hostname.html">Pod Name</a>
Hello MyApp | Version: v1 | <a href="hostname.html">Pod Name</a>
Hello MyApp | Version: v2 | <a href="hostname.html">Pod Name</a>
Hello MyApp | Version: v1 | <a href="hostname.html">Pod Name</a>

```
