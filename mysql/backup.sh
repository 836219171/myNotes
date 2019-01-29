#!/bin/bash

date=`date +%s`
echo file date is $date
echo 'step1. backup mysql data'
echo 'enter target mysql password'
mysqldump -h127.0.0.1 -uroot -P13307 -pMySql@hangzhou+2017 --all-databases > robot-db-$date-backup.sql

echo 'step2. export source mysql data'
echo 'enter source mysql password'
mysqldump -h127.0.0.1 -uroot -pMySql@hangzhou+2017 --all-databases > robot-db-$date.sql

echo 'step3. target mysql import data'
echo 'enter target mysql password'
mysql -h127.0.0.1 -uroot -P13307 -pMySql@hangzhou+2017 < robot-db-$date.sql

echo 'step4. delete source mysql data'
rm -rf robot-db-$date.sql
