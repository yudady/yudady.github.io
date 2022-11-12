---
title: 自建CA搭建HTTPS及域名配置
categories:
tags: ["tools"]
date:
modified: 2022-05-07 16:20
---

# 自建CA搭建HTTPS及域名配置

## openssl-自建CA

### 使用密碼 `-des3` 密碼：123456
> openssl genrsa -des3 -out ca-des3.key 2048
```sh
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop/openssl/ca$ openssl genrsa -des3 -out ca-des3.key 2048
Generating RSA private key, 2048 bit long modulus (2 primes)
..........................................................................+++++
..................................................................................+++++
e is 65537 (0x010001)
Enter pass phrase for ca-des3.key:
Verifying - Enter pass phrase for ca-des3.key:

tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop/openssl/ca$ cat ca-des3.key
-----BEGIN RSA PRIVATE KEY-----
Proc-Type: 4,ENCRYPTED
DEK-Info: DES-EDE3-CBC,84E81AC956FF2CF0

tLa9EBWbRK8H6y0sB3HfjJk+n9N4jP+uaCVdzo2l8lqBEDSxZiB+tGIAki7IaMOe
...
bNyeoWAjikyqado6CEnjqMuD+Q9dsnCJjJfYtGHOEsItybKmY0DdgbrubYlGrR/Q
-----END RSA PRIVATE KEY-----
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop/openssl/ca$
```


### 沒密碼
>  openssl genrsa -out ca.key 2048
```sh
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop/openssl/ca$ openssl genrsa -out ca.key 2048
Generating RSA private key, 2048 bit long modulus (2 primes)
....................................................................................................................................................................+++++
................................+++++
e is 65537 (0x010001)


tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop/openssl/ca$ cat ca.key
-----BEGIN RSA PRIVATE KEY-----
MIIEowIBAAKCAQEAtchcx4y9nPgDc3I2Ik0doj3wCnr+1jiVAHCAfgtK6uLLGCxI
...
QLXxZPdvCZgbgZ05QEqbRnUlpSwkvseyosPf1XeKuNpS5fdmJ2bq
-----END RSA PRIVATE KEY-----
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop/openssl/ca$
```


### 產生CA的證書的CRT(CER)
> openssl req -x509 -key ca-des3.key -out ca.crt -days 3650
```sh
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop/openssl/ca$ openssl req -x509 -key ca-des3.key -out ca.crt -days 3650
Enter pass phrase for ca-des3.key:
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [AU]:TW
State or Province Name (full name) [Some-State]:Taiwan
Locality Name (eg, city) []:taipei
Organization Name (eg, company) [Internet Widgits Pty Ltd]:tommy-dev
Organizational Unit Name (eg, section) []:dev
Common Name (e.g. server FQDN or YOUR name) []:*.yudady.tk
Email Address []:yu_dady@yahoo.com.tw
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop/openssl/ca$

tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop/openssl/ca$ more ca.crt
-----BEGIN CERTIFICATE-----
MIID+zCCAuOgAwIBAgIUX1ag5/n21DQKmx4y2I335+D5r8swDQYJKoZIhvcNAQEL
...
4xx6KP+Djg0GYsGvJfoH
-----END CERTIFICATE-----


```


#### 讀取證書資料
> openssl x509 -in ca.crt -text -noout
```sh
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop/openssl/ca$ openssl x509 -in ca.crt -text -noout
Certificate:
    Data:
        Version: 3 (0x2)
        Serial Number:
            5f:56:a0:e7:f9:f6:d4:34:0a:9b:1e:32:d8:8d:f7:e7:e0:f9:af:cb
        Signature Algorithm: sha256WithRSAEncryption
        Issuer: C = TW, ST = Taiwan, L = taipei, O = tommy-dev, OU = dev, CN = *.yudady.tk, emailAddress = yu_dady@yahoo.com.tw
        Validity
            Not Before: May 15 09:45:07 2022 GMT
            Not After : May 12 09:45:07 2032 GMT
        Subject: C = TW, ST = Taiwan, L = taipei, O = tommy-dev, OU = dev, CN = *.yudady.tk, emailAddress = yu_dady@yahoo.com.tw
        Subject Public Key Info:
            Public Key Algorithm: rsaEncryption
                RSA Public-Key: (2048 bit)
                Modulus:
                    00:a7:3c:c5:03:3d:9d:94:f3:16:98:94:b9:6c:89:
...
                    a4:65
                Exponent: 65537 (0x10001)
        X509v3 extensions:
            X509v3 Subject Key Identifier:
                56:D5:37:27:64:8C:C7:E8:49:1C:BE:0A:C1:F9:00:4A:98:93:71:F5
            X509v3 Authority Key Identifier:
                keyid:56:D5:37:27:64:8C:C7:E8:49:1C:BE:0A:C1:F9:00:4A:98:93:71:F5

            X509v3 Basic Constraints: critical
                CA:TRUE
    Signature Algorithm: sha256WithRSAEncryption
         32:8b:bf:a4:43:40:71:5a:40:ba:a4:58:de:6a:e1:f9:0c:a3:
...
         af:25:fa:07
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop/openssl/ca$




tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop/openssl/ca$ openssl x509 -in windows-root.cer -text -noout
Certificate:
    Data:
        Version: 3 (0x2)
        Serial Number:
            54:98:d2:d1:d4:5b:19:95:48:13:79:c8:11:c0:87:99
        Signature Algorithm: sha384WithRSAEncryption
        Issuer: C = US, O = Microsoft Corporation, CN = Microsoft Identity Verification Root Certificate Authority 2020
        Validity
            Not Before: Apr 16 18:36:16 2020 GMT
            Not After : Apr 16 18:44:40 2045 GMT
        Subject: C = US, O = Microsoft Corporation, CN = Microsoft Identity Verification Root Certificate Authority 2020
        Subject Public Key Info:
            Public Key Algorithm: rsaEncryption
                RSA Public-Key: (4096 bit)
                Modulus:
                    00:b3:91:2a:07:83:06:67:fd:9e:9d:e0:c7:c0:b7:
...
                    26:83:5d:b4:63:73:ef:ae:23:95:4f:60:28:be:37:
                    d5:5b:a5
                Exponent: 65537 (0x10001)
        X509v3 extensions:
            X509v3 Key Usage: critical
                Digital Signature, Certificate Sign, CRL Sign
            X509v3 Basic Constraints: critical
                CA:TRUE
            X509v3 Subject Key Identifier:
                C8:7E:D2:6A:85:2A:1B:CA:19:98:04:07:27:CF:50:10:4F:68:A8:A2
            1.3.6.1.4.1.311.21.1:
                ...
    Signature Algorithm: sha384WithRSAEncryption
         af:6a:dd:e6:19:e7:2d:94:43:19:4e:cb:e9:50:95:64:a5:03:
         ...
         88:80:06:5c:a2:b2:ed:a2
tommy@tommy-msi:/mnt/c/Users/yu_da/Desktop/openssl/ca$
```


![](../images/CA-202205151806.png)

### **修改域名**
將 /etc 目錄下的 hosts 檔案里加入一行 ip 到域名的對映

```host
127.0.0.1 <你的域名>
127.0.0.1 localhost 
127.0.0.1 <你的域名> 
127.0.1.1 ubuntu
```

```txt
將caconfig.cnf 檔案 下面這一行DNS後面修改為你的域名
subjectAltName          = DNS:<你的域名>
將exampleserver.cnf 檔案 commonName修改為你的域名
ommonName              = <你的域名>
```


### [建立CA 證書頒發機構](https://www.796t.com/article.php?id=201258)

```sh
  
# 使用證書頒發機構（CA）金鑰對伺服器證書進行簽名：
openssl ca -in tempreq.pem -out server_crt.pem
```



### [自建 CA 中心並簽發 CA 證書 - 程式人生](https://www.796t.com/content/1544953449.html)



### [自建CA搭建HTTPS及域名配置_實用技巧_程式人生](https://www.796t.com/article.php?id=201258)




### [如何使用 OpenSSL 建立開發測試用途的自簽憑證 (Self-Signed Certificate)](https://blog.miniasp.com/post/2019/02/25/Creating-Self-signed-Certificate-using-OpenSSL)


### X.509體系


[「根憑證」是來自後端憑證伺服器且採用 Base-64 編碼及 X.509(.CER)](https://docs.microsoft.com/zh-tw/azure/application-gateway/self-signed-certificates)  
  
  
https://ithelp.ithome.com.tw/articles/10193959  
  
$ openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout tls.key -out tls.crt  
req：使用 X.509 Certificate Signing Request（CSR） Management 產生憑證  
-x509：建立自我簽署的憑證  
-nodes：不使用密碼保護  
-days 365：設定憑證期限為 365 天  
-newkey rsa:2048：產生新的 RSA 金鑰  
-keyout：金鑰名稱與儲存位置  
-out：憑證名稱與儲存位置  


