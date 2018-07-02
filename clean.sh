#!/usr/bin/env bash
rm -rf dist
java -Xms512M -Xmx2G -Xss2M -XX:ReservedCodeCacheSize=192m -XX:+CMSClassUnloadingEnabled -Dfile.encoding=UTF-8 -jar sbt/sbt-launch.jar -Dsbt.parser.simple=true clean