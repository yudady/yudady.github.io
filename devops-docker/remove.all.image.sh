#!/bin/bash


docker rm -f $(docker ps -aq)

echo "docker rm done!"

docker rmi -f $(docker images -aq)

echo "docker rmi done!"