# OpenSSL支援許多不同的加密演算法：

> 其主要函式庫是以C語言所寫成，實作了基本的加密功能，實作了SSL與TLS協定。


- 加密 AES、Blowfish、Camellia、Chacha20、Poly1305、SEED、CAST-128、DES、IDEA、RC2、RC4、RC5、TDES、GOST 28147-89[16]、SM4
- 密碼雜湊函式 MD5、MD4、MD2、SHA-1、SHA-2、SHA-3、RIPEMD-160、MDC-2、GOST R 34.11-94[16]、BLAKE2、Whirlpool[17]、SM3
- 公開金鑰加密 RSA、DSA、ECDSA、ECDHE、迪菲-赫爾曼密鑰交換、橢圓曲線密碼學、X25519、Ed25519、X448、Ed448、GOST R 34.10-2001[16]、SM2



![使用 OpenSSL 與 cURL 檢查網站伺服器支援哪幾種 Cipher Suites]()


```shell
openssl s_client -connect www.google.com:443
openssl s_client -connect www.google.com:443 -tls1_2 -msg
openssl ciphers -s -psk -srp
```


```shell
openssl ciphers -stdname

curl -s -S -v -o /dev/null --no-progress-meter https://www.google.com

curl -s -S -v -o /dev/null --no-progress-meter --tls-max 1.2 --ciphers ECDHE-ECDSA-AES128-GCM-SHA256 https://www.google.com

curl -s -S -v --tls-max 1.2 --ciphers ECDHE-ECDSA-AES128-GCM-SHA256 https://www.google.com
```

# SSL、TLS 協定
