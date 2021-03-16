#!/bin/bash
echo "正在上传的文件 $1"
echo "正在上传,请等一小会儿"
#curl -F "file=@$1" -F "uKey=c61e4354d608df80e71608ad79a82c87" -F "_api_key=1bd4e19be1499a3faf7114659b7d3f7f" https://qiniu-storage.pgyer.com/apiv1/app/upload
curl -F "file=@$1" -F "_api_key=1bd4e19be1499a3faf7114659b7d3f7f" -F "buildInstallType=2" -F "buildPassword=666" https://www.pgyer.com/apiv2/app/upload