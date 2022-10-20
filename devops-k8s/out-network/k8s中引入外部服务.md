# [k8s中引入外部服务 ](https://www.cnblogs.com/lvzhenjiang/p/14445315.html)



## 通过无头服务实现

### mysql_svc.yml
```yaml 
apiVersion: v1
kind: Service
metadata:
  name: mysql-svc
  namespace: default
spec:
  clusterIP: None
  ports:
    - name: default-ep
      port: 3306
      protocol: TCP
      targetPort: 3306
  type: ClusterIP

---
apiVersion: v1
kind: Endpoints
metadata:
  name: mysql-ep
  namespace: default
subsets:
  - addresses:
      - ip: 114.32.146.154
    ports:
      - name: mysql
        port: 3306
        protocol: TCP
        
```

$ kubectl run pod — rm -i — tty — image ubuntu — bash
root@pod:/# apt update && apt -y install curl

root@pod:/# curl -Ls external-svc | grep \<title\>
<title>RTFM: Linux, DevOps, and system administration</title>

root@pod:/# curl -Ls external-svc.default.svc.cluster.local | grep \<title\>
<title>RTFM: Linux, DevOps, and system administration</title>