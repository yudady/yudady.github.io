#!/bin/sh

while true
do
	echo "Sending logs to FluentD"
  curl -X POST http://fluentd:9880 -H 'Content-Type: application/json' -d '{"login":"my_login","password":"my_password"}'
	sleep 3
done
