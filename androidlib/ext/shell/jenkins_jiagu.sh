#!/bin/bash

DIRPATH=/Users/Shared/Jenkins/Library/jiagu
BASE=${DIRPATH}/jiagu.jar
NAME=tony@puahome.com
PASSWORD=a5223490

APK=$1   #需要加固的apk路径
DEST=$2  #输出加固包路径

echo "------ jiagu running! ------"

java -jar ${BASE} -version
java -jar ${BASE} -login ${NAME} ${PASSWORD}
java -jar ${BASE} -importsign ${DIRPATH}/android.keystore 123456 android.keystore 123456
java -jar ${BASE} -importsign ${DIRPATH}/ward.jks hnh111 ward 123456
java -jar ${BASE} -importsign ${DIRPATH}/wood.jks hnh111 wood hnh111
java -jar ${BASE} -showsign
echo "执行的第一个参数 $1"
echo "执行的第二个参数 $2"
echo "执行的第三个参数 $3"
echo java -jar $1 -jiagu $2 $3 -autosign
java -jar $1 -jiagu $2 $3 -autosign

echo "------ jiagu finished! ------"
