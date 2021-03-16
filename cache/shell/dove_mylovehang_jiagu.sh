#!/bin/bash
jar=/Users/mylovehang/Documents/360jiagubao_mac/jiagu/jiagu.jar
java -jar $jar -login tony@puahome.com a5223490
java -jar $jar -importsign /Users/mylovehang/Desktop/dove/dove/app/key/dove.jks hnh111 dove hnh111
java -jar $jar -showsign
java -jar $jar -config -x86
java -jar $jar -showconfig
echo java -jar $jar -jiagu /Users/mylovehang/Desktop/dove/dove/history/1.0.0_beta4/1.0.0_beta4-1-dove.apk /Users/mylovehang/Desktop/dove/dove/history/1.0.0_beta4 -autosign
java -jar $jar -jiagu /Users/mylovehang/Desktop/dove/dove/history/1.0.0_beta4/1.0.0_beta4-1-dove.apk /Users/mylovehang/Desktop/dove/dove/history/1.0.0_beta4 -autosign