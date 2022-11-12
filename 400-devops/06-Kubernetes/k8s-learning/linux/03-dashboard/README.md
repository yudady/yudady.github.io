# dashboard 

```shell
# 访问： https://集群任意IP:端口
https://hdss7-11.host.com:31260

```


```shell
[root@hdss7-11 02-dashboard]# kubectl apply -f 02-dashboard-account.yaml 
serviceaccount/admin-user created
clusterrolebinding.rbac.authorization.k8s.io/admin-user created
```


#获取访问令牌
> kubectl -n kubernetes-dashboard get secret $(kubectl -n kubernetes-dashboard get sa/admin-user -o jsonpath="{.secrets[0].name}") -o go-template="{{.data.token | base64decode}}"


```shell

[root@hdss7-11 02-dashboard]# kubectl -n kubernetes-dashboard get secret $(kubectl -n kubernetes-dashboard get sa/admin-user -o jsonpath="{.secrets[0].name}") -o go-template="{{.data.token | base64decode}}"


[root@hdss7-11 03-dashboard]# kubectl -n kubernetes-dashboard get secret $(kubectl -n kubernetes-dashboard get sa/admin-user -o jsonpath="{.secrets[0].name}") -o go-template="{{.data.token | base64decode}}"



eyJhbGciOiJSUzI1NiIsImtpZCI6ImltTVJaSTkyWTIwNnFPMDFwM0IwSG5yZjd1bVQzYXl0NG12XzZSVWwxalEifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlcm5ldGVzLWRhc2hib2FyZCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJhZG1pbi11c2VyLXRva2VuLTJqc3c1Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImFkbWluLXVzZXIiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiIyOTVlNWVlNC01MjAyLTQ0MDAtOGQxYi0yYTdiMzA4N2NjNjkiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZXJuZXRlcy1kYXNoYm9hcmQ6YWRtaW4tdXNlciJ9.rQZNDDRyRPxPnCTLZiUYmbKYb01Iq8hb3nmC5T_aqOb3PkunZ6yN27HApNaWGow_iRiLoPJ0qKStyJ6WTaMuWT6usCuFlzkiZepPY1DPtMuPCsaeAeCm_HO64-61GKSovidr2FLuccnJqYJfHRSXvB9b7WhlxavVQCgE3-By6aBL5to0zL1nL92WEWDz6JRGxsgLB8w12QQq82x3aiW7mob9Xp_dhSzpmzQhspx2i0CrRyApUKAT38FoCaRyNN_NNmuy087T9uj7SRlmTueH-YWckQ-kEnQ2TBL3J_pWpnTWViFR0a9lbVGxx4dRlkHFgVMJHlf18Fu7VujydQw5tA


```

dashboard 使用ingress 失敗  ，  ssl 需要處理

[dashboard](https://hdss7-21.host.com:30041/)





