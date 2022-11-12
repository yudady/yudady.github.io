# install Minikube in windows sub linux ubuntu-20.04LTS

## Step 1) Apply updates
```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$ sudo apt update -y
[sudo] password for tommy:
Hit:1 http://archive.ubuntu.com/ubuntu focal InRelease
Get:2 http://archive.ubuntu.com/ubuntu focal-updates InRelease [114 kB]
Building dependency tree
Reading state information... Done
33 packages can be upgraded. Run 'apt list --upgradable' to see them.
```
```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$ sudo apt upgrade -y
Reading package lists... Done
Building dependency tree
invoke-rc.d: could not determine current runlevel
Processing triggers for systemd (245.4-4ubuntu3.13) ...
Processing triggers for man-db (2.9.1-1) ...
Processing triggers for dbus (1.12.16-2ubuntu2.1) ...
Processing triggers for install-info (6.7.0.dfsg.2-5) ...
Processing triggers for mime-support (3.64ubuntu1) ...
```

## Step 2) Install Minikube dependencies
```shell

tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$ sudo apt install -y curl wget apt-transport-https
Reading package lists... Done
Unpacking apt-transport-https (2.0.6) ...
Setting up apt-transport-https (2.0.6) ...
```

## Step 3) Download Minikube Binary
```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$ wget https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
--2021-12-31 12:11:01--  https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
Resolving storage.googleapis.com (storage.googleapis.com)... 172.217.160.80, 172.217.163.48, 142.251.42.240, ...
Connecting to storage.googleapis.com (storage.googleapis.com)|172.217.160.80|:443... connected.
HTTP request sent, awaiting response... 200 OK
Length: 69568775 (66M) [application/octet-stream]
Saving to: ‘minikube-linux-amd64’

minikube-linux-amd64          100%[=================================================>]  66.35M  8.85MB/s    in 7.4s

2021-12-31 12:11:09 (8.99 MB/s) - ‘minikube-linux-amd64’ saved [69568775/69568775]
```
```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$  sudo cp minikube-linux-amd64 /usr/local/bin/minikube
```
```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$ sudo chmod +x /usr/local/bin/minikube
```
```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$  minikube version
minikube version: v1.24.0
commit: 76b94fb3c4e8ac5062daf70d60cf03ddcc0a741b
```
```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$ curl -LO https://storage.googleapis.com/kubernetes-release/release/`curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt`/bin/linux/amd64/kubectl
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100 44.4M  100 44.4M    0     0  8891k      0  0:00:05  0:00:05 --:--:-- 9306k
```
```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$ chmod +x kubectl
```
```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$ sudo mv kubectl /usr/local/bin/
```
```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$ kubectl version -o yaml
clientVersion:
  buildDate: "2021-12-16T11:41:01Z"
  compiler: gc
  gitCommit: 86ec240af8cbd1b60bcc4c03c20da9b98005b92e
  gitTreeState: clean
  gitVersion: v1.23.1
  goVersion: go1.17.5
  major: "1"
  minor: "23"
  platform: linux/amd64

The connection to the server kubernetes.docker.internal:6443 was refused - did you specify the right host or port?
```


## Step 4) Start the minikube
```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$  minikube start --driver=docker
😄  minikube v1.24.0 on Ubuntu 20.04 (amd64)
✨  Using the docker driver based on user configuration

💣  Exiting due to PROVIDER_DOCKER_VERSION_EXIT_1: "docker version --format -" exit status 1:
📘  Documentation: https://minikube.sigs.k8s.io/docs/drivers/docker/
```


```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$ docker version --format {{.Server.Os}}-{{.Server.Version}}
linux-20.10.11

```
```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$ minikube start --addons=ingress --cpus=2 --cni=flannel --install-addons=true --kubernetes-version=stable --memory=6g
😄  minikube v1.24.0 on Ubuntu 20.04 (amd64)
✨  Automatically selected the docker driver. Other choices: none, ssh
❗  Your cgroup does not allow setting memory.
    ▪ More information: https://docs.docker.com/engine/install/linux-postinstall/#your-kernel-does-not-support-cgroup-swap-limit-capabilities
👍  Starting control plane node minikube in cluster minikube
🚜  Pulling base image ...
💾  Downloading Kubernetes v1.22.3 preload ...
    > preloaded-images-k8s-v13-v1...: 501.73 MiB / 501.73 MiB  100.00% 5.23 MiB
    > gcr.io/k8s-minikube/kicbase: 355.78 MiB / 355.78 MiB  100.00% 3.47 MiB p/
🔥  Creating docker container (CPUs=2, Memory=6144MB) ...
🐳  Preparing Kubernetes v1.22.3 on Docker 20.10.8 ...
    ▪ Generating certificates and keys ...
    ▪ Booting up control plane ...
    ▪ Configuring RBAC rules ...
🔗  Configuring Flannel (Container Networking Interface) ...
🔎  Verifying Kubernetes components...
    ▪ Using image gcr.io/k8s-minikube/storage-provisioner:v5
    ▪ Using image k8s.gcr.io/ingress-nginx/controller:v1.0.4
    ▪ Using image k8s.gcr.io/ingress-nginx/kube-webhook-certgen:v1.1.1
    ▪ Using image k8s.gcr.io/ingress-nginx/kube-webhook-certgen:v1.1.1
🔎  Verifying ingress addon...
🌟  Enabled addons: storage-provisioner, default-storageclass, ingress
🏄  Done! kubectl is now configured to use "minikube" cluster and "default" namespace by default
```

```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$ minikube status
minikube
type: Control Plane
host: Running
kubelet: Running
apiserver: Running
kubeconfig: Configured
```

```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$  kubectl cluster-info
Kubernetes control plane is running at https://127.0.0.1:53486
CoreDNS is running at https://127.0.0.1:53486/api/v1/namespaces/kube-system/services/kube-dns:dns/proxy

To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.
```
```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$ kubectl get nodes
NAME       STATUS   ROLES                  AGE     VERSION
minikube   Ready    control-plane,master   5m40s   v1.22.3


```

## Step 5) Managing Addons on minikube

```shell
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$ minikube addons list
|-----------------------------|----------|--------------|-----------------------|
|         ADDON NAME          | PROFILE  |    STATUS    |      MAINTAINER       |
|-----------------------------|----------|--------------|-----------------------|
| ambassador                  | minikube | disabled     | unknown (third-party) |
| auto-pause                  | minikube | disabled     | google                |
| csi-hostpath-driver         | minikube | disabled     | kubernetes            |
| dashboard                   | minikube | disabled     | kubernetes            |
| default-storageclass        | minikube | enabled ✅   | kubernetes            |
| efk                         | minikube | disabled     | unknown (third-party) |
| freshpod                    | minikube | disabled     | google                |
| gcp-auth                    | minikube | disabled     | google                |
| gvisor                      | minikube | disabled     | google                |
| helm-tiller                 | minikube | disabled     | unknown (third-party) |
| ingress                     | minikube | enabled ✅   | unknown (third-party) |
| ingress-dns                 | minikube | disabled     | unknown (third-party) |
| istio                       | minikube | disabled     | unknown (third-party) |
| istio-provisioner           | minikube | disabled     | unknown (third-party) |
| kubevirt                    | minikube | disabled     | unknown (third-party) |
| logviewer                   | minikube | disabled     | google                |
| metallb                     | minikube | disabled     | unknown (third-party) |
| metrics-server              | minikube | disabled     | kubernetes            |
| nvidia-driver-installer     | minikube | disabled     | google                |
| nvidia-gpu-device-plugin    | minikube | disabled     | unknown (third-party) |
| olm                         | minikube | disabled     | unknown (third-party) |
| pod-security-policy         | minikube | disabled     | unknown (third-party) |
| portainer                   | minikube | disabled     | portainer.io          |
| registry                    | minikube | disabled     | google                |
| registry-aliases            | minikube | disabled     | unknown (third-party) |
| registry-creds              | minikube | disabled     | unknown (third-party) |
| storage-provisioner         | minikube | enabled ✅   | kubernetes            |
| storage-provisioner-gluster | minikube | disabled     | unknown (third-party) |
| volumesnapshots             | minikube | disabled     | kubernetes            |
|-----------------------------|----------|--------------|-----------------------|
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop$
```


# 參考資料
- [How to Install Minikube on Ubuntu 20.04 LTS / 21.04](https://www.linuxtechi.com/how-to-install-minikube-on-ubuntu/)


