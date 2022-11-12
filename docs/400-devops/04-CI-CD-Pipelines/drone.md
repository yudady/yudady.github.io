---
title: drone
tags: [2022-11, devops]
aliases: [drone]
created_date: 2022-11-10 11:48
updated_date: 2022-11-11 15:49
---

# drone

- **🏷️Tags** :   #2022-11 #devops 
- Link: [Drone CI / CD | Drone](https://docs.drone.io/)

5000 build 次限制, 需要手動使用 go build

```go
# 構建開源版
$ go build -tags "oss nolimit" github.com/drone/drone/cmd/drone-server
# 構建企業版
$ go build -tags "nolimit" github.com/drone/drone/cmd/drone-server


1. Clone the repository
2. Install go 1.11 or later with Go modules enabled
3. Install binaries to $GOPATH/bin

    go build -tags "nolimit" github.com/drone/drone/cmd/drone-server

[使用容器方式編譯無功能限制的 Drone CI_蘇洋部落格 - MdEditor](https://www.gushiciku.cn/pl/gqUJ/zh-tw)
```

### 使用容器方式編譯無功能限制的 Drone CI

```Dockerfile
FROM golang:1.18-alpine AS Builder

RUN sed -i 's/https:\/\/dl-cdn.alpinelinux.org/http:\/\/mirrors.tuna.tsinghua.edu.cn/' /etc/apk/repositories && \
    echo "Asia/Shanghai" > /etc/timezone

RUN apk add build-base && \
    go env -w GO111MODULE=on 

#                 2.15.0 版本
ENV DRONE_VERSION 2.15.0

WORKDIR /src

# Build with online code
RUN apk add curl && curl -L https://github.com/drone/drone/archive/refs/tags/v${DRONE_VERSION}.tar.gz -o v${DRONE_VERSION}.tar.gz && \
    tar zxvf v${DRONE_VERSION}.tar.gz && rm v${DRONE_VERSION}.tar.gz
# OR with offline tarball
# ADD drone-1.10.1.tar.gz /src/

WORKDIR /src/drone-${DRONE_VERSION}

RUN go mod download

ENV CGO_CFLAGS="-g -O2 -Wno-return-local-addr"

RUN go build -ldflags "-extldflags \"-static\"" -tags="nolimit" github.com/drone/drone/cmd/drone-server



FROM alpine:3.13 AS Certs
RUN sed -i 's/https:\/\/dl-cdn.alpinelinux.org/http:\/\/mirrors.tuna.tsinghua.edu.cn/' /etc/apk/repositories && \
    echo "Asia/Shanghai" > /etc/timezone
RUN apk add -U --no-cache ca-certificates



FROM alpine:3.16
EXPOSE 80 443
VOLUME /data

RUN [ ! -e /etc/nsswitch.conf ] && echo 'hosts: files dns' > /etc/nsswitch.conf

ENV GODEBUG netdns=go
ENV XDG_CACHE_HOME /data
ENV DRONE_DATABASE_DRIVER sqlite3
ENV DRONE_DATABASE_DATASOURCE /data/database.sqlite
ENV DRONE_RUNNER_OS=linux
ENV DRONE_RUNNER_ARCH=amd64
ENV DRONE_SERVER_PORT=:80
ENV DRONE_SERVER_HOST=localhost
ENV DRONE_DATADOG_ENABLED=true
ENV DRONE_DATADOG_ENDPOINT=https://stats.drone.ci/api/v1/series

COPY --from=Certs /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/

#                              2.15.0 版本
COPY --from=Builder /src/drone-2.15.0/drone-server /bin/drone-server
ENTRYPOINT ["/bin/drone-server"]
```

> docker build -t yudady/drone:2.15.0 .
[../10-Version-Control/gitea](../10-Version-Control/gitea.md) + drone
