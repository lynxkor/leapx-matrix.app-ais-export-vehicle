#!/bin/sh
java $JAVA_OPTS -javaagent:/app/SecPoint.jar -Dtcsec.app.key=$SECAPPKEY -Dagent.lazyload.enabled=true -Dagent.lazyload.time=180 -jar /app/app.jar --spring.profiles.active=$RUNENV --spring.cloud.nacos.discovery.namespace=$NACOS_NSID --spring.cloud.nacos.discovery.serverAddr=$NACOS_ADDR --spring.cloud.nacos.discovery.username=$NACOS_USER --spring.cloud.nacos.discovery.password=$NACOS_PWD

prog_exit()
{
    ps -ef| grep java |grep -v grep |awk '{print $2}'|xargs kill -15
}
trap "prog_exit" 15

flag=1
while [ $flag -ne 0 ];do
    sleep 3;
    flag=`ps -ef| grep java |grep -v grep | wc -l`
done;

