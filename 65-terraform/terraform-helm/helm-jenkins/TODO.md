* ingress


```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: myapp-ing
spec:
  ingressClassName: nginx
  rules:
    - host: "yudady.ml" # check
      http:
        paths:
          - pathType: Prefix
            path: "/"
            backend:
              service:
                name: myapp-svc # check
                port:
                  number: 80
```