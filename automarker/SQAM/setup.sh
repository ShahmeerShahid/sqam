#!/bin/sh

if [ $# -ne 4 ]
  then
    echo "We need just need 4 arguements for this script."
    exit
fi

if [ $1 == "mysql" ]; then
    mysql -h mysqlam -u $2 --password=$3 -e "CREATE DATABASE $4"
    mysqldump -h mysqlam -u $2 --password=$3 $4 < start.sql
    echo "Done adding initialization table"
fi

if [ $1 == "psql" ]; then
    psql -h mysql -u $2 -p $3 $4 < start.sql
    echo "Done adding init∂alization table"
fi