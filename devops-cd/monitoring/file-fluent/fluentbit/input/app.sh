#!/bin/sh

while true
do
	echo "Writing log to a file"
  echo '{"app":"file-myapp"}' >> /app/file-fluentbit-input.log
	sleep 3
done