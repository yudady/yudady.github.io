
yu_da@tommy-msi MINGW64 /d/tommy/github-repo/yudady/practise-workspace/50-docker/jenkins/dev/topic002/deploy/
$ kubectl apply -f ./
namespace/devops created
serviceaccount/jenkins-admin created
resource mapping not found for name: "jenkins" namespace: "devops" from "deployment.yml": no matches for kind "Deployment" in version "extensions/v1beta1"
ensure CRDs are installed first
resource mapping not found for name: "jenkins-rbac" namespace: "devops" from "rabc.yml": no matches for kind "ClusterRole" in version "rbac.authorization.k8s.io/v1beta1"
ensure CRDs are installed first
resource mapping not found for name: "jenkins-admin" namespace: "devops" from "rabc.yml": no matches for kind "ClusterRoleBinding" in version "rbac.authorization.k8s.io/v1beta1"
ensure CRDs are installed first

yu_da@tommy-msi MINGW64 /d/tommy/github-repo/yudady/practise-workspace/50-docker/jenkins/dev/topic002/deploy/incluster (main)
$
