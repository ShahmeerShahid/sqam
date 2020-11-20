#!/bin/sh

if [ $# -ne 6 ]
  then
    echo "We need just need 6 arguements for this script."
    exit 1
fi

if [ $1 == "mysql" ]; then
    mysql -h mysqlam -u $2 --password=$3 -e "CREATE DATABASE $4"
    mysql -h mysqlam -u $2 --password=$3 -e "GRANT ALL PRIVILEGES ON $4.* TO $5@'%' IDENTIFIED BY '$6'"
    echo "Done privilege grant to database $4 for user $5"
    mysql -h mysqlam -u $2 --password=$3 -e "FLUSH PRIVILEGES"
    mysql -h mysqlam -u $2 --password=$3 $4 < start.sql
    echo "Done adding initialization table"
    exit 0
fi

if [ $1 == "psql" ]; then
    psql -h mysql -u $2 -p $3 $4 < start.sql
    echo "Done adding init∂alization table"
    exit 0
fi