
```shell
# k8s 官網安裝前設定

# 节点之中不可以有重复的主机名
# 设置系统主机名以及 bind9 文件的相互解析 
vim /var/named/host.com.zone
named-checkconf
systemctl restart named

hostnamectl set-hostname hdss7-11.host.com
# 安装依赖包 
yum update -y

yum install -y conntrack ntpdate ntp ipvsadm ipset jq iptables curl sysstat libseccomp wget vim net-tools git



# 设置防火墙为 Iptables 并设置空规则 
systemctl stop firewalld && systemctl disable firewalld 

yum -y install iptables-services && systemctl start iptables && systemctl enable iptables && iptables -F && service iptables save

# 关闭 SELINUX 
swapoff -a && sed -i '/ swap / s/^\(.*\)$/#\1/g' /etc/fstab 
setenforce 0 && sed -i 's/^SELINUX=.*/SELINUX=disabled/' /etc/selinux/config


# 允许 iptables 检查桥接流量
cat <<EOF | sudo tee /etc/modules-load.d/k8s.conf
br_netfilter
EOF




cat <<EOF | sudo tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF
sudo sysctl --system


```

> 開始安裝

官方 CentOS 7 存儲庫中不提供 Kubernetes 包。 此步驟需要在主節點以及您計劃用於容器設置的每個工作節點上執行。 輸入以下命令以檢索 Kubernetes 存儲庫。
```shell


cat <<EOF > /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://packages.cloud.google.com/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://packages.cloud.google.com/yum/doc/yum-key.gpg https://packages.cloud.google.com/yum/doc/rpm-package-key.gpg
EOF



###############################################
yum install -y kubelet-1.23.1-0 kubeadm-1.23.1-0 kubectl-1.23.1-0

systemctl enable kubelet
systemctl start kubelet
systemctl status kubelet


###############################################
# 修正 docker.ce 
# I faced similar issue recently. The problem was cgroup driver. Kubernetes cgroup driver was set to systems but docker was set to systemd. So I created /etc/docker/daemon.json and added below:
# [kubelet-check] It seems like the kubelet isn't running or healthy.
# [kubelet-check] The HTTP call equal to 'curl -sSL http://localhost:10248/healthz' failed with error: Get "http://localhost:10248/healthz": dial tcp [::1]:10248: connect: connection refused.


cat <<EOF > /etc/docker/daemon.json 
{
    "exec-opts": ["native.cgroupdriver=systemd"]
}
EOF

sudo systemctl restart docker


###############################################

# 主節點初始化
kubeadm init \
--apiserver-advertise-address=10.4.7.11 \
--control-plane-endpoint=cluster-endpoint \
--service-cidr=172.16.0.0/16 \
--pod-network-cidr=192.168.0.0/16 | tee kubeadm-init.log



# 安裝成功後：訊息

Your Kubernetes control-plane has initialized successfully!

To start using your cluster, you need to run the following as a regular user:

  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config

Alternatively, if you are the root user, you can run:

  export KUBECONFIG=/etc/kubernetes/admin.conf

You should now deploy a pod network to the cluster.
Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
  https://kubernetes.io/docs/concepts/cluster-administration/addons/

You can now join any number of control-plane nodes by copying certificate authorities
and service account keys on each node and then running the following as root:

  kubeadm join cluster-endpoint:6443 --token 7xk678.48sbnte867jog8wt \
        --discovery-token-ca-cert-hash sha256:084a126b16b7cb35209978fc363b47cf96db8992228b2af27560156b22433cc7 \
        --control-plane

Then you can join any number of worker nodes by running the following on each as root:

kubeadm join cluster-endpoint:6443 --token 7xk678.48sbnte867jog8wt \
        --discovery-token-ca-cert-hash sha256:084a126b16b7cb35209978fc363b47cf96db8992228b2af27560156b22433cc7 


###############################################
# kubeadm 裝壞了，重新處理
kubeadm reset

sudo rm -rf ~/.kube
###############################################
# 安裝網路 : flannel
> wget https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml

# 需要修改 kube-flannel.yml 裡面的10.xx.xx.xx 
# pod-network-cidr 與 kube-flannel.yml 相關
kubectl apply -f kube-flannel.yml

# 安裝node
kubeadm join cluster-endpoint:6443 --token 7xk678.48sbnte867jog8wt \
        --discovery-token-ca-cert-hash sha256:084a126b16b7cb35209978fc363b47cf96db8992228b2af27560156b22433cc7 
        
        
        
[root@master ~]# kubeadm token create --print-join-command
kubeadm join cluster-endpoint:6443 --token j0kh9n.wlp3l1t2erc5jo90 --discovery-token-ca-cert-hash sha256:084a126b16b7cb35209978fc363b47cf96db8992228b2af27560156b22433cc7

```




