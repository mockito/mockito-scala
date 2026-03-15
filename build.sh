#!/usr/bin/env bash
# TODO: Scala 3 modules are not all in root aggregate, so +test does not cross-build every module for 3.3.7.
#  As each module gains Scala 3 support, add it to the explicit +<module>/test commands below.
#  Once all modules support Scala 3, add scala3Version to commonSettings crossScalaVersions
#  and remove the per-module commands.
java -Xms512M -Xmx2G -Xss2M -XX:ReservedCodeCacheSize=192m -Dfile.encoding=UTF-8 -jar sbt/sbt-launch.jar -Dsbt.parser.simple=true clean +test +common/test +macroCommon/test 'set every publishTo := Some(Resolver.file("local-repo", file("target/dist")))' '+ publish'
