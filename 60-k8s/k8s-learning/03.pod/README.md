# command

```shell
# create
tommy@tommy-msi:/03.pod$ kubectl run mynginx --image=nginx
# 察看default名稱空間
tommy@tommy-msi:/03.pod$ kubectl get pods

NAME      READY   STATUS    RESTARTS   AGE
mynginx   1/1     Running   0          40s
# describe
tommy@tommy-msi:/03.pod$ kubectl describe pod mynginx
Name:         mynginx
Namespace:    default
Priority:     0
Node:         docker-desktop/192.168.65.4
Start Time:   Sun, 05 Dec 2021 19:47:52 +0800
Labels:       run=mynginx
Annotations:  <none>
Status:       Running
IP:           10.1.0.43
IPs:
  IP:  10.1.0.43
Containers:
  mynginx:
    Container ID:   docker://2ed4f37ce145cfb95d8ca9ad54569d26f5efa7ddc0cce4c2a29b359b27295a05
    Image:          nginx
    Image ID:       docker-pullable://nginx@sha256:9522864dd661dcadfd9958f9e0de192a1fdda2c162a35668ab6ac42b465f0603
    Port:           <none>
    Host Port:      <none>
    State:          Running
      Started:      Sun, 05 Dec 2021 19:48:09 +0800
    Ready:          True
    Restart Count:  0
    Environment:    <none>
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-vn7tj (ro)
Conditions:
  Type              Status
  Initialized       True
  Ready             True
  ContainersReady   True
  PodScheduled      True
Volumes:
  kube-api-access-vn7tj:
    Type:                    Projected (a volume that contains injected data from multiple sources)
    TokenExpirationSeconds:  3607
    ConfigMapName:           kube-root-ca.crt
    ConfigMapOptional:       <nil>
    DownwardAPI:             true
QoS Class:                   BestEffort
Node-Selectors:              <none>
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                             node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
Events:
  Type    Reason     Age   From               Message
  ----    ------     ----  ----               -------
  Normal  Scheduled  102s  default-scheduler  Successfully assigned default/mynginx to docker-desktop
  Normal  Pulling    101s  kubelet            Pulling image "nginx"
  Normal  Pulled     85s   kubelet            Successfully pulled image "nginx" in 15.4876993s
  Normal  Created    85s   kubelet            Created container mynginx
  Normal  Started    85s   kubelet            Started container mynginx
  
  
  
# 刪除 pod  
tommy@tommy-msi:/03.pod$ kubectl delete pod mynginx
pod "mynginx" deleted
```











