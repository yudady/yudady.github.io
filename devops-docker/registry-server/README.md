




$ docker run -d -p 5000:5000 --restart always --name registry registry:2
Now, use it from within Docker:

docker pull ubuntu

docker image tag ubuntu localhost:5000/myfirstimage


docker push localhost:5000/myfirstimage


docker pull localhost:5000/myfirstimage









