#!/bin/bash
jar=/Users/mylovehang/Documents/360jiagubao_mac/jiagu/jiagu.jar
java -jar $jar -login tony@puahome.com a5223490
java -jar $jar -importsign /Users/mylovehang/Desktop/dove/dove/app/key/goat.jks hnh123 goat hnh123
java -jar $jar -showsign
java -jar $jar -config -x86
java -jar $jar -showconfig
echo java -jar $jar -jiagu /Users/mylovehang/Desktop/dove/dove/history/2.0.6_beta19/2.0.6_beta19-18-dove.apk /Users/mylovehang/Desktop/dove/dove/history/2.0.6_beta19 -autosign
java -jar $jar -jiagu /Users/mylovehang/Desktop/dove/dove/history/2.0.6_beta19/2.0.6_beta19-18-dove.apk /Users/mylovehang/Desktop/dove/dove/history/2.0.6_beta19 -autosign