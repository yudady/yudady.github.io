#!/bin/sh

while true
do
	echo "Sending logs to FluentD"
  curl -X POST http://fluentd:9880 -H 'Content-Type: application/json' -d '{"login":"my_login","password":"my_password"}'
	sleep 3
done


#while true
#do
#	echo "Sending logs to FluentD"
#  curl -X POST -d 'json={"foo":"bar"}' http://fluentd:9880/http-myapp.log
#	sleep 3
#done

