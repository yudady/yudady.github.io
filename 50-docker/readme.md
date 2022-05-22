# Docker: Remove all images and containers
docker rm -f $(docker ps -a -q)
docker rmi -f $(docker images -q)


## 移除全部未使用的 volume
> docker volume prune

## 移除全部未使用的 network
> docker network prune