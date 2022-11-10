---
title: drone
tags: [2022-11, devops]
aliases: [drone]
created_date: 2022-11-10 11:48
updated_date: 2022-11-10 12:07
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

## 緣起

- 

## 是什麼

- 

## 去哪下載

- 

## 📝 怎麼玩

- 
