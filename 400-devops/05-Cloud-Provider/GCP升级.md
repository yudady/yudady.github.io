# GKE 升級
- gcloud auth login
- terraform init
- terraform refresh 確認當前terraform與gcp相同
- [GKE-version](https://cloud.googles.ltd/kubernetes-engine/docs/release-notes) 找`No channel` 版本
- rename `11-app.tf.back` to `11-app.tf`
- 修改 win-env-project\dev\gcloud\01-variables.tf  ＋  win-env-project\dev\gcloud\11-app.tf => variable "gke_version" { default = `"1.21.13-gke.900"` }
- win-env-project\dev\11-app.tf (name: app->temp, 創建升級後的google_container_node_pool) terraform plan terraform apply // 創建一組轉移期間暫時的node環境
- 會創建一個節點node `node_pool_1` temp ,用來存放轉移的POD
- win-env-project\dev\kube -> nodeSelector: pool: app 改成 nodeSelector: pool: temp 將k8s 指向 temp  (kafka1,2,3 輪流指向 間隔約3分鐘) 03-apply-kube.sh
```yaml
      nodeSelector:
        pool: app
--
      nodeSelector:
        pool: temp
```
- win-env-project\dev\gcloud\modules\site\03-node-pool.tf  "google_container_node_pool" "node_pool" 升級 gke version // 升級app gke
- win-env-project\dev\kube -> nodeSelector: pool: temp 改成 nodeSelector: pool: app 將k8s 的node 指向 app (kafka1,2,3 輪流指向 間隔約3分鐘) 03-apply-kube.sh
- rename `11-app.tf` to `11-app.tf.back`
- 更新完 去gcp 手動調整 VCP network ops ip for jenkins  (http://kube.16888dev.com:30100/ | win-dev-ops-ip)[https://console.cloud.google.com/networking/addresses/list?project=unistar-win-dev-v1]



