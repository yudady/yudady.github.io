provider "helm" {
  kubernetes {
    config_path = "~/.kube/config"
  }
}

resource "helm_release" "my-nginx-test" {

  name = "test-nginx"

  repository = "./"
  chart      = "nginx-test"
  namespace  = "default"

}