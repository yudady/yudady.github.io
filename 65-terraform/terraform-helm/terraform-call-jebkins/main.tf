provider "helm" {
  kubernetes {
    config_path = "~/.kube/config"
  }
}

resource "helm_release" "jenkins" {

  name = "jenkins"

  repository = "./"
  chart      = "jenkins"
  namespace  = "default"

}

