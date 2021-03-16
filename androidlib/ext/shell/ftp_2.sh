#!/bin/bash
# putfile=/Users/joker/Downloads/test/multi/wood-1.8.5.1-17_1851_jiagu_sign-360.apk
# putfile=/Users/joker/Downloads/test/multi/wood-1.8.5.1-17_1851_jiagu_sign-baidu.apk
dir=$1
file1=$1/$4
file2=$1/$5
echo 开始上传ftp
echo $file1
echo $file2
echo $2
ftp_server=ph.ixiaolu.com
ftp -n $ftp_server <<EOF
user apk  huainanhai111
bin
mkdir $3
cd $3
mkdir $2
put $file1 $2/$4
put $file2 $2/$5
by
EOF
echo 上传ftp结束




