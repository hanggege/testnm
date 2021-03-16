#!/bin/bash
echo "发消息给测试"
echo "token = $1"
DING_URL="https://oapi.dingtalk.com/robot/send?access_token=$1"
curl $DING_URL \
-H 'Content-Type: application/json' \
-d '{"msgtype": "text",
"text": {"content": "Android当前打包版本: 《 1.0.0_beta4 》\n--------------------------------------------\n \nAndroid\n各位leader，快来下载新版【知心里】.\n --------------------------------------------\n下载：  https://www.pgyer.com/mei_dove\n\n"},
"at":{"isAtAll":true}
}'