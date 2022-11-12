# NGINX Ingress Controller
> k8s 基礎套件 域名解析 , docker-desktop install


* [Kubernetes Nginx Ingress with Docker Desktop](https://www.michaelrose.dev/posts/k8s-ingress-docker-desktop/)
* [kubernetes.github.io](https://kubernetes.github.io/ingress-nginx/deploy/#docker-desktop)




```shell
helm upgrade --install ingress-nginx ingress-nginx \
  --repo https://kubernetes.github.io/ingress-nginx \
  --namespace ingress-nginx --create-namespace


kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.3.1/deploy/static/provider/cloud/deploy.yaml


kubectl apply -f ./deploy.yaml


## test
kubectl apply -f ./test-nginx-ingress.yaml
```



