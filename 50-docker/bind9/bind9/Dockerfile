FROM alpine:latest
MAINTAINER GordonWei wei@kmp.tw
RUN apk add bind openrc
RUN mkdir -p /run/openrc && touch /run/openrc/softlevel
RUN rc-update -u named

Volume ["/sys/fs/cgroup"]