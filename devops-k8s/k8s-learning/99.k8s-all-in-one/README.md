# [All-in-One Installation of Kubernetes and KubeSphere on Linux](https://kubesphere.io/docs/quick-start/all-in-one-on-linux/)




```shell
# install package
[root@k8s ~]# yum install -y ebtables socat ipset conntrack
Loaded plugins: fastestmirror, langpacks
Loading mirror speeds from cached hostfile
 * base: mirror01.idc.hinet.net
 * extras: mirror01.idc.hinet.net




# Step 2: Download KubeKey
[root@k8s ~]# curl -sfL https://get-kk.kubesphere.io | VERSION=v1.2.0 sh -
Downloading kubekey v1.2.0 from https://github.com/kubesphere/kubekey/releases/download/v1.2.0/kubekey-v1.2.0-linux-amd64.tar.gz ...
Kubekey v1.2.0 Download Complete!
anaconda-ks.cfg  kk  kubekey-v1.2.0-linux-amd64.tar.gz  original-ks.cfg  README.md  README_zh-CN.md
[root@k8s ~]# chmod +x kk




# Step 3: Get Started with Installation
[root@k8s ~]# ./kk create cluster --with-kubernetes v1.21.5 --with-kubesphere v3.2.0
+------+------+------+---------+----------+-------+-------+-----------+--------+------------+-------------+------------------+--------------+
| name | sudo | curl | openssl | ebtables | socat | ipset | conntrack | docker | nfs client | ceph client | glusterfs client | time         |
+------+------+------+---------+----------+-------+-------+-----------+--------+------------+-------------+------------------+--------------+
| k8s  | y    | y    | y       | y        | y     | y     | y         |        | y          |             | y                | PST 06:16:52 |
+------+------+------+---------+----------+-------+-------+-----------+--------+------------+-------------+------------------+--------------+

This is a simple check of your environment.
Before installation, you should ensure that your machines meet all requirements specified at
https://github.com/kubesphere/kubekey#requirements-and-recommendations

Continue this installation? [yes/no]: yes
INFO[06:20:53 PST] Downloading Installation Files
INFO[06:20:53 PST] Downloading kubeadm ...
INFO[06:21:00 PST] Downloading kubelet ...


#####################################################
###              Welcome to KubeSphere!           ###
#####################################################

Console: http://192.168.100.128:30880
Account: admin
Password: P@88w0rd

NOTES：
  1. After you log into the console, please check the
     monitoring status of service components in
     "Cluster Management". If any service is not
     ready, please wait patiently until all components
     are up and running.
  2. Please change the default password after login.

#####################################################
https://kubesphere.io             2021-12-13 08:14:39
#####################################################
INFO[08:14:46 PST] Installation is complete.

Please check the result using the command:

       kubectl logs -n kubesphere-system $(kubectl get pod -n kubesphere-system -l app=ks-install -o jsonpath='{.items[0].metadata.name}') -f

[root@k8s ~]#







# Step 4: Verify the Installation
[root@k8s ~]# kubectl logs -n kubesphere-system $(kubectl get pod -n kubesphere-system -l app=ks-install -o jsonpath='{.items[0].metadata.name}') -f




```


```shell
[root@localhost ~]# cat /etc/sysconfig/network-scripts/ifcfg-ens33
TYPE=Ethernet
PROXY_METHOD=none
BROWSER_ONLY=no
BOOTPROTO=static
DEFROUTE=yes
IPV4_FAILURE_FATAL=no
NAME=ens33
UUID=0119bb64-88ba-44aa-ac96-a5d7fe4374b4
DEVICE=ens33
ONBOOT=yes
IPADDR=192.168.100.128
NETMASK=255.255.255.0
GATEWAY=192.168.100.2
DNS1=192.168.100.2




# bind9












```
