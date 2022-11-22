---
title: docker-run-command
tags: [2022-11, devops]
aliases: [docker-run-command, ab]
created_date: 2022-11-22 14:44
updated_date: 2022-11-22 17:55
---

# docker-run-command

- **🏷️Tags** :   #2022-11 #devops 
- Link: 

[jessfraz/dotfiles: My dotfiles. Buyer beware ;)](https://github.com/jessfraz/dotfiles)

[Ramblings from Jessie: Docker Containers on the Desktop](https://blog.jessfraz.com/post/docker-containers-on-the-desktop/)

[marcel-dempers/my-desktop](https://github.com/marcel-dempers/my-desktop)

### create Dockerfile

```Dockerfile
FROM alpine:latest
RUN apk add --no-cache apache2-ssl apache2-utils ca-certificates
ENTRYPOINT [ "ab" ]
```

### build docker image

```shell
$ docker build . -t yudady/ab

PS C:\Users\user\Desktop> docker images
REPOSITORY   TAG       IMAGE ID       CREATED       SIZE
yudady/ab    latest    f93e9e618407   2 hours ago   10.9MB
alpine       latest    bfe296a52501   10 days ago   5.54MB
```

### set alias

```bash
# vim C:\Users\user\.bash_profile
alias ab='docker run -it yudady/ab'
```

### run ab

```shell
user@M0201-014 MINGW64 ~
$ ab -n 1 -c 1 "http://www.google.com/"
This is ApacheBench, Version 2.3 <$Revision: 1901567 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking www.google.com (be patient).....done


Server Software:        gws
Server Hostname:        www.google.com
Server Port:            80

Document Path:          /
Document Length:        14454 bytes

Concurrency Level:      1
Time taken for tests:   0.101 seconds
Complete requests:      1
Failed requests:        0
Total transferred:      15333 bytes
HTML transferred:       14454 bytes
Requests per second:    9.87 [#/sec] (mean)
Time per request:       101.319 [ms] (mean)
Time per request:       101.319 [ms] (mean, across all concurrent requests)
Transfer rate:          147.79 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        6    6   0.0      6       6
Processing:    95   95   0.0     95      95
Waiting:       89   89   0.0     89      89
Total:        101  101   0.0    101     101

user@M0201-014 MINGW64 ~
```

### install vcxsrv

```windows
vcxsrv-64.1.20.14.0.installer.exe
```

![[install-wslg-202211221729.png]]

![[install-wslg-202211221730.png]]

### 登入 WSL 需要設定好 : DISPLAY

```shell
root@M0201-014:~# cat .bash_login
echo 'hello'
export DISPLAY=$(cat /etc/resolv.conf | grep nameserver | awk '{print $2}'):0
root@M0201-014:~#
```

### Tor Browser

```wsl
# wsl run docker
docker run -it \
    -v /tmp/.X11-unix:/tmp/.X11-unix \
    -e DISPLAY=$DISPLAY \
    --name tor-browser \
    jess/tor-browser




root@M0201-014:~# docker run -it \
/tmp/.X>     -v /tmp/.X11-unix:/tmp/.X11-unix \
>     -e DISPLAY=$DISPLAY \
>     --name tor-browser \
>     jess/tor-browser
Unable to find image 'jess/tor-browser:latest' locally
latest: Pulling from jess/tor-browser
de6fe37eab5f: Pull complete
3e1d48fd2953: Pull complete
73d2b096ee7c: Pull complete
73231da4c57e: Pull complete
77366c2c82be: Pull complete
Digest: sha256:bf9f419fccdead414bf3c7ec1375692a4ed61882e6fd1826ed3367ae673a885d
Status: Downloaded newer image for jess/tor-browser:latest
Logging Tor Browser debug information to /dev/stdout
Fontconfig warning: "/usr/local/bin/Browser/TorBrowser/Data/fontconfig/fonts.conf", line 85: unknown element "blank"
Nov 22 09:46:08.487 [notice] Tor 0.4.2.7 (git-74cad14699087b7e) running on Linux with Libevent 2.1.8-stable, OpenSSL 1.1.1g, Zlib 1.2.11, Liblzma N/A, and Libzstd N/A.
Nov 22 09:46:08.487 [notice] Tor can't help you if you use it wrong! Learn how to be safe at https://www.torproject.org/download/download#warning
Nov 22 09:46:08.487 [notice] Read configuration file "/usr/local/bin/Browser/TorBrowser/Data/Tor/torrc-defaults".
Nov 22 09:46:08.487 [notice] Read configuration file "/usr/local/bin/Browser/TorBrowser/Data/Tor/torrc".
Nov 22 09:46:08.488 [notice] Opening Control listener on 127.0.0.1:9151
Nov 22 09:46:08.488 [notice] Opened Control listener on 127.0.0.1:9151
```
