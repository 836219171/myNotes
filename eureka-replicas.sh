#!/bin/bash
JAR_FILE=usp-eureka-test-1.0-SNAPSHOT.jar

PORT1=9000
PORT2=9001
PORT3=9002

HOSTNAME1=eureka9000.com
HOSTNAME2=eureka9001.com
HOSTNAME3=eureka9002.com

APPLICATION_NAME1=usp_eureka_1
APPLICATION_NAME2=usp_eureka_2
APPLICATION_NAME3=usp_eureka_3


function start()
{
 java -Dserver.port=$1 -Dserver2.port=$2   -Dserver2.hostname=$3 -Dserver3.port=$4  -Dserver3.hostname=$5 -jar $JAR_FILE &
echo $1 starting...
}

start $PORT1  $PORT2 $HOSTNAME2 $PORT3  $HOSTNAME3;
start $PORT2  $PORT1 $HOSTNAME1 $PORT3  $HOSTNAME3;
start $PORT3  $PORT1 $HOSTNAME1 $PORT2  $HOSTNAME2;

 
