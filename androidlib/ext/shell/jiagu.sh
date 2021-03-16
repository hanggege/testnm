#!/bin/bash
path=$(dirname $0)
path=${path/\./$(pwd)}
echo "执行的第一个参数 $1"
echo "执行的第二个参数 $2"
echo "执行的第三个参数 $3"
#java -jar /Users/joker/workSpace/jiagu/360jiagubao_mac/jiagu/jiagu.jar -jiagu /Users/joker/workSpace/xiaolu/Wood/app/build/outputs/apk/Wood-1.7.0.1_beta6-11.apk /Users/joker/Downloads -autosign
echo java -jar $1 -jiagu $2 $3 -autosign
java -jar $1 -jiagu $2 $3 -autosign