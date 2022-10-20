# terraform-deploy-helm


> https://opensource.com/article/21/8/terraform-deploy-helm

## Installing the Chart
### Add kubernetes-dashboard repository
> helm repo add kubernetes-dashboard https://kubernetes.github.io/dashboard/
### Deploy a Helm Release named "kubernetes-dashboard" using the kubernetes-dashboard chart
> helm install kubernetes-dashboard kubernetes-dashboard/kubernetes-dashboard

## Uninstalling the Chart
> helm delete kubernetes-dashboard









> terraform init
```shell
Initializing the backend...

Initializing provider plugins...
- Finding latest version of hashicorp/helm...
- Installing hashicorp/helm v2.6.0...
- Installed hashicorp/helm v2.6.0 (signed by HashiCorp)

Terraform has created a lock file .terraform.lock.hcl to record the provider
selections it made above. Include this file in your version control repository
so that Terraform can guarantee to make the same selections by default when
you run "terraform init" in the future.

Terraform has been successfully initialized!

You may now begin working with Terraform. Try running "terraform plan" to see
any changes that are required for your infrastructure. All Terraform commands
should now work.

If you ever set or change modules or backend configuration for Terraform,
rerun this command to reinitialize your working directory. If you forget, other
commands will detect it and remind you to do so if necessary.

```

> terraform apply

```shell
Terraform used the selected providers to generate the following execution
plan. Resource actions are indicated with the following symbols:
  + create

Terraform will perform the following actions:

  # helm_release.my-kubernetes-dashboard will be created
  + resource "helm_release" "my-kubernetes-dashboard" {
      + atomic                     = false
      + chart                      = "kubernetes-dashboard"
      + cleanup_on_fail            = false
      + create_namespace           = false
      + dependency_update          = false
      + disable_crd_hooks          = false
      + disable_openapi_validation = false
      + disable_webhooks           = false
      + force_update               = false
      + id                         = (known after apply)
      + lint                       = false
      + manifest                   = (known after apply)
      + max_history                = 0
      + metadata                   = (known after apply)
      + name                       = "my-kubernetes-dashboard"
      + namespace                  = "default"
      + pass_credentials           = false
      + recreate_pods              = false
      + render_subchart_notes      = true
      + replace                    = false
      + repository                 = "https://kubernetes.github.io/dashboard/"
      + reset_values               = false
      + reuse_values               = false
      + skip_crds                  = false
      + status                     = "deployed"
      + timeout                    = 300
      + verify                     = false
      + version                    = (known after apply)
      + wait                       = true
      + wait_for_jobs              = false

      + set {
          + name  = "protocolHttp"
          + value = "true"
        }
      + set {
          + name  = "rbac.clusterReadOnlyRole"
          + value = "true"
        }
      + set {
          + name  = "replicaCount"
          + value = "2"
        }
      + set {
          + name  = "service.externalPort"
          + value = "80"
        }
      + set {
          + name  = "service.type"
          + value = "LoadBalancer"
        }
    }

Plan: 1 to add, 0 to change, 0 to destroy.

Do you want to perform these actions?
  Terraform will perform the actions described above.
  Only 'yes' will be accepted to approve.

```


$ helm install kubernetes-dashboard kubernetes-dashboard/kubernetes-dashboard
NAME: kubernetes-dashboard
LAST DEPLOYED: Thu Sep 22 14:50:12 2022
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
*********************************************************************************
*** PLEASE BE PATIENT: kubernetes-dashboard may take a few minutes to install ***
*********************************************************************************

Get the Kubernetes Dashboard URL by running:
export POD_NAME=$(kubectl get pods -n default -l "app.kubernetes.io/name=kubernetes-dashboard,app.kubernetes.io/instance=kubernetes-dashboard" -o jsonpath="{.items[0].metadata.name}")
echo https://127.0.0.1:8443/
kubectl -n default port-forward $POD_NAME 8443:8443
