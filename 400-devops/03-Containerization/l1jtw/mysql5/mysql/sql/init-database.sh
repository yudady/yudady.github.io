#!/usr/bin/env bash

mysql -uroot -proot sandbox < "/docker-entrypoint-initdb.d/000-create-databases.sql"
mysql -uroot -proot sandbox < "/docker-entrypoint-initdb.d/001-l1jtw.sql"
