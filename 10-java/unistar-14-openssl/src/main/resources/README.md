# openssl
[证书格式转换](https://www.jianshu.com/p/15d58b1ada5b)
[证书格式转换](http://jianiau.blogspot.com/2015/07/openssl-key-and-certificate-conversion.html)

## [rsa_converter](https://decoder.link/rsa_converter)

## `PKCS1`私钥生成
```shell script
openssl genrsa -out private.pem 1024
```
![](img/20200510190439.png)

### PKCS1
```text
-----BEGIN RSA PRIVATE KEY-----
MIICXAIBAAKBgQCXWWngsGk9+pYpdsyTeqK6tnBEaSArOZV/2nIi5QcGJfERFrFA
NBXd5n8Dxi4B/ZOj6xu8Wwiq2TozheMy1aHK/egEkyF9hCW5bTx7g3khhU8cEWz1
ZANlBo2m3jGey/OVz4PVQAHPrBa4F7kQztNuOuGK3qS4Muc0VQ+dOaDWjwIDAQAB
AoGAM1XDoM+VUs1tI3FFIHzHN1dsKvgRTjgA6lXYwtc2E7LI2of85Q8Fn7dvu4L7
1fcBnebACJpEDVcS86jlEDf/4UnF0+7VVKGPZklZWR7MckyD5dFsxK65jDVxDf4I
rTkUzw89YIllBbbQKkIXiJnEt72LfH8B5LeKy4SWMck9BcECQQDHIxgNg47Gzn+2
TZueXd/wpj09uLSGpgwWgN1j0HJxn9ZY/FVD/Mgw/H/3Y39QU+OjwDv7jr7uQuol
IAmFLM+hAkEAwpEItOl/mGJLaCQ5nkMUPaCWEteeiL0kbySTOQzkhAocH8VD854g
zrmbSrSvwYgyJvfq3FUq9+DjjERPnUu4LwJBAIZoL3Hfm9yfwB+B3klTvzSkX61r
H53KMVp2+/bE2JnMclZ5aeuLkGtP4edOJr5HANyjgVjXDhOu4m7QYtyaVEECQD5i
LE19VIjyc0UX4FWLInFEDPpLoiwroQP10TKInLhtWWkiYdA50kEWJ+GDH5tI5POz
p90vfZBuzebKfFBt18cCQEOjny/9BBJRpy5T6s11BVLLJ1fPSoY8LUt47MWScavH
ShKNu5DIPjk05dxmjyV9BvvmRGoGk94JoNSmVbJgVhw=
-----END RSA PRIVATE KEY-----

```
原始的 DER 格式结构
```text
RSAPrivateKey ::= SEQUENCE {
  version           Version,
  modulus           INTEGER,  -- n
  publicExponent    INTEGER,  -- e
  privateExponent   INTEGER,  -- d
  prime1            INTEGER,  -- p
  prime2            INTEGER,  -- q
  exponent1         INTEGER,  -- d mod (p-1)
  exponent2         INTEGER,  -- d mod (q-1)
  coefficient       INTEGER,  -- (inverse of q) mod p
  otherPrimeInfos   OtherPrimeInfos OPTIONAL
}
```

## `public.pem`
```shell script
openssl rsa -in private.pem -pubout -out public.pem
```
![](img/20200510204942.png)
```text
-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXWWngsGk9+pYpdsyTeqK6tnBE
aSArOZV/2nIi5QcGJfERFrFANBXd5n8Dxi4B/ZOj6xu8Wwiq2TozheMy1aHK/egE
kyF9hCW5bTx7g3khhU8cEWz1ZANlBo2m3jGey/OVz4PVQAHPrBa4F7kQztNuOuGK
3qS4Muc0VQ+dOaDWjwIDAQAB
-----END PUBLIC KEY-----

```




## PKCS1私钥转换为`PKCS8`(该格式一般Java调用)

```shell script
openssl pkcs8 -topk8 -inform PEM -in private.pem -outform pem -nocrypt -out pkcs8.pem
```
![](img/20200510190544.png)

### pkcs8.pem文件内容
```text
-----BEGIN PRIVATE KEY-----
MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJdZaeCwaT36lil2
zJN6orq2cERpICs5lX/aciLlBwYl8REWsUA0Fd3mfwPGLgH9k6PrG7xbCKrZOjOF
4zLVocr96ASTIX2EJbltPHuDeSGFTxwRbPVkA2UGjabeMZ7L85XPg9VAAc+sFrgX
uRDO02464YrepLgy5zRVD505oNaPAgMBAAECgYAzVcOgz5VSzW0jcUUgfMc3V2wq
+BFOOADqVdjC1zYTssjah/zlDwWft2+7gvvV9wGd5sAImkQNVxLzqOUQN//hScXT
7tVUoY9mSVlZHsxyTIPl0WzErrmMNXEN/gitORTPDz1giWUFttAqQheImcS3vYt8
fwHkt4rLhJYxyT0FwQJBAMcjGA2DjsbOf7ZNm55d3/CmPT24tIamDBaA3WPQcnGf
1lj8VUP8yDD8f/djf1BT46PAO/uOvu5C6iUgCYUsz6ECQQDCkQi06X+YYktoJDme
QxQ9oJYS156IvSRvJJM5DOSEChwfxUPzniDOuZtKtK/BiDIm9+rcVSr34OOMRE+d
S7gvAkEAhmgvcd+b3J/AH4HeSVO/NKRfrWsfncoxWnb79sTYmcxyVnlp64uQa0/h
504mvkcA3KOBWNcOE67ibtBi3JpUQQJAPmIsTX1UiPJzRRfgVYsicUQM+kuiLCuh
A/XRMoicuG1ZaSJh0DnSQRYn4YMfm0jk87On3S99kG7N5sp8UG3XxwJAQ6OfL/0E
ElGnLlPqzXUFUssnV89KhjwtS3jsxZJxq8dKEo27kMg+OTTl3GaPJX0G++ZEagaT
3gmg1KZVsmBWHA==
-----END PRIVATE KEY-----

```
注意，这里就没有 RSA 字样了，因为 PKCS#8 是一个通用型的秘钥格式方案；其中的 BASE64 ENCODED DATA 所标注的内容为 PEM 格式中对 DER 原始二进制进行的 BASE64 编码；
所对应的 DER 原始二进制所表述的内容为
```text
PublicKeyInfo ::= SEQUENCE {
  algorithm       AlgorithmIdentifier,
  PublicKey       BIT STRING
}

AlgorithmIdentifier ::= SEQUENCE {
  algorithm       OBJECT IDENTIFIER,
  parameters      ANY DEFINED BY algorithm OPTIONAL
}
```





## PKCS8格式私钥转换为PKCS1（传统私钥格式）
```shell script
openssl rsa -in pkcs8.pem -out pkcs1.pem 
```

![](img/20200510190820.png)

```text
-----BEGIN RSA PRIVATE KEY-----
MIICXAIBAAKBgQCXWWngsGk9+pYpdsyTeqK6tnBEaSArOZV/2nIi5QcGJfERFrFA
NBXd5n8Dxi4B/ZOj6xu8Wwiq2TozheMy1aHK/egEkyF9hCW5bTx7g3khhU8cEWz1
ZANlBo2m3jGey/OVz4PVQAHPrBa4F7kQztNuOuGK3qS4Muc0VQ+dOaDWjwIDAQAB
AoGAM1XDoM+VUs1tI3FFIHzHN1dsKvgRTjgA6lXYwtc2E7LI2of85Q8Fn7dvu4L7
1fcBnebACJpEDVcS86jlEDf/4UnF0+7VVKGPZklZWR7MckyD5dFsxK65jDVxDf4I
rTkUzw89YIllBbbQKkIXiJnEt72LfH8B5LeKy4SWMck9BcECQQDHIxgNg47Gzn+2
TZueXd/wpj09uLSGpgwWgN1j0HJxn9ZY/FVD/Mgw/H/3Y39QU+OjwDv7jr7uQuol
IAmFLM+hAkEAwpEItOl/mGJLaCQ5nkMUPaCWEteeiL0kbySTOQzkhAocH8VD854g
zrmbSrSvwYgyJvfq3FUq9+DjjERPnUu4LwJBAIZoL3Hfm9yfwB+B3klTvzSkX61r
H53KMVp2+/bE2JnMclZ5aeuLkGtP4edOJr5HANyjgVjXDhOu4m7QYtyaVEECQD5i
LE19VIjyc0UX4FWLInFEDPpLoiwroQP10TKInLhtWWkiYdA50kEWJ+GDH5tI5POz
p90vfZBuzebKfFBt18cCQEOjny/9BBJRpy5T6s11BVLLJ1fPSoY8LUt47MWScavH
ShKNu5DIPjk05dxmjyV9BvvmRGoGk94JoNSmVbJgVhw=
-----END RSA PRIVATE KEY-----


```


