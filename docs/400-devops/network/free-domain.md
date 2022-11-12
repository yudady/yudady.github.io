---
created_date: 2022-10-04 19:55
updated_date: 2022-11-12 09:12
title: free-domain
tags: [devops, 2022-10, todo]
date: 2022-09-28 20:30
modified: 2022-10-04 16:56
aliases: [free-domain]
---

# free-domain

- **🏷️Tags** :   #2022-10 #todo 
- Link: [捋一捋这些年一起薅过的羊毛 – 包含永久免费vps、域名等 - 大鸟博客](https://www.daniao.org/15400.html)
source-url :: [freenom 免費域名 + DNS](https://www.freenom.com/zu/index.html?lang=zu)
	- login by google account
	- [luolongfei/freenom: Freenom 域名自动续期。Freenom domain name renews automatically.](https://github.com/luolongfei/freenom#-%E9%80%9A%E8%BF%87-docker-%E9%83%A8%E7%BD%B2)
- ssl domain : [Let's Encrypt Explained: Free SSL - YouTube](https://www.youtube.com/watch?v=jrR_WfgmWEw&list=RDCMUCFe9-V_rN9nLqVNiI8Yof3w&start_radio=1&rv=jrR_WfgmWEw&t=11)
- [免费域名介紹](https://wenjie.org/archives/free-domain)


---

## my free domain  

---

1. yudady.ml  
2. yudady.cf  
3. yudady.gq
4. yudady.tk  , TOMMY TRAEFIK WEB NGINX NGINX
5. yudady.ga  ,  web.yudady.ga ,  admin.yudady.ga 

## SSL

<iframe title="Let's Encrypt Explained: Free SSL" src="https://www.youtube.com/embed/jrR_WfgmWEw?start=17&amp;feature=oembed" height="113" width="200" allowfullscreen="" allow="fullscreen" style="aspect-ratio: 1.76991 / 1; width: 100%; height: 100%;"></iframe>

EXAMPLE.COM/.well-known/acme-challenge/

certbot

## TLS

```shell
tommy@tommy-msi:www.yudady.tk$ls -la
-rwxrwxrwx 1 tommy tommy 2431 Nov 11 18:50 ca_bundle.crt
-rwxrwxrwx 1 tommy tommy 2281 Nov 11 18:50 certificate.crt
-rwxrwxrwx 1 tommy tommy 1706 Nov 11 18:50 private.key
-rwxrwxrwx 1 tommy tommy 5045 Nov 12 03:12 tls.pfx

# 合併 crt
cat certificate.crt ca_bundle.crt > full_chain.crt

# 合成 tls.pfx
openssl pkcs12 -export -out tls.pfx -inkey private.key -in certificate.crt -certfile ca_bundle.crt

# letsencrypt validation path
http://www.yudady.tk/.well-known/pki-validation/10A01C0C743DA31E6706682CF9257B37.txt

# tomcat8/conf/server.xml

	<Connector port="443" protocol="HTTP/1.1" SSLEnabled="true"
		maxThreads="150" scheme="https" secure="true"
		clientAuth="false" sslProtocol="TLS"
		keystoreFile="C:\Users\yu_da\Desktop\apache-tomcat-8.5.83\conf\tls.pfx"
		keystoreType="PKCS12"
		keystorePass="" />

```

## DNSmasq

docker pull jpillora/dnsmasq

[基于docker搭建DNSmasq - 腾讯云开发者社区-腾讯云](https://cloud.tencent.com/developer/article/1692705)

docker pull 4km3/dnsmasq

[DrPsychick/docker-dnsmasq: dnsmasq docker image, fully configurable through ENV](https://github.com/DrPsychick/docker-dnsmasq)

[drpsychick/dnsmasq Tags | Docker Hub](https://hub.docker.com/r/drpsychick/dnsmasq/tags)
