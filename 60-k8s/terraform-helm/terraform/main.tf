provider "helm" {
  kubernetes {
    config_path = "~/.kube/config"
  }
}

resource "helm_release" "my-kubernetes-dashboard" {

  name  = "my-kubernetes-dashboard"

  # local
  chart = "../kubernetes-dashboard"

  # github
  #chart = "https://yudady.github.io/helm-chart-repository/kubernetes-dashboard-2.0.1.tgz"


  #  repository = "https://kubernetes.github.io/dashboard/"
  #  chart      = "kubernetes-dashboard"
  namespace = "default"

  set {
    name  = "service.type"
    value = "LoadBalancer"
  }

  set {
    name  = "protocolHttp"
    value = "true"
  }

  set {
    name  = "service.externalPort"
    value = 80
  }

  set {
    name  = "replicaCount"
    value = 1
  }

  set {
    name  = "rbac.clusterReadOnlyRole"
    value = "true"
  }
}



#resource "helm_release" "example" {
#  name  = "redis"
#  chart = "https://charts.bitnami.com/bitnami/redis-10.7.16.tgz"
#}