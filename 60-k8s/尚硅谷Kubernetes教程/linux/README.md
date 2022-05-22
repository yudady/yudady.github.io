# [k8s workloads](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/)










# BIND 9 






Last login: Mon Dec 27 06:53:39 2021 from 10.4.7.1
[root@hdss7-200 ~]# cd /var/named/
[root@hdss7-200 named]# ls
data  dynamic  host.com.zone  named.ca  named.empty  named.localhost  named.loopback  od.com.zone  slaves
[root@hdss7-200 named]# ls -la
total 28
drwxrwx--T.  5 root  named  167 Dec 27 06:53 .
drwxr-xr-x. 22 root  root  4096 Dec 20 20:52 ..
drwxrwx---.  2 named named   49 Dec 26 03:45 data
drwxrwx---.  2 named named   60 Dec 28 04:02 dynamic
-rw-r--r--   1 root  root   538 Dec 24 09:46 host.com.zone
-rw-r-----.  1 root  named 2253 Apr  5  2018 named.ca
-rw-r-----.  1 root  named  152 Dec 15  2009 named.empty
-rw-r-----.  1 root  named  152 Jun 21  2007 named.localhost
-rw-r-----.  1 root  named  168 Dec 15  2009 named.loopback
-rw-r--r--.  1 root  root   324 Dec 20 21:11 od.com.zone
drwxrwx---.  2 named named    6 Nov 24 08:38 slaves
[root@hdss7-200 named]# cat od.com.zone
$ORIGIN od.com.
$TTL 600        ; 10 minutes
@               IN SOA  dns.od.com. dnsadmin.od.com. (
2020122101 ; serial
10800      ; refresh (3 hours)
900        ; retry (15 minutes)
604800     ; expire (1 week)
86400      ; minimum (1 day)
)
NS   dns.od.com.
$TTL 60 ; 1 minute
dns                A    10.4.7.200
traefik            A    10.4.7.11

[root@hdss7-200 named]# vim od.com.zone
[root@hdss7-200 named]# systemctl restart named
named.service             named-setup-rndc.service
[root@hdss7-200 named]# systemctl restart named
[root@hdss7-200 named]#




