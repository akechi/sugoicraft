#!/bin/bash -xe
git pull
java -jar ~/bin/sbt-launch.jar package
cp target/scala-2.10/sugoicraft_2.10-1.0.jar ~/sugoi-dev/plugins/
if [ ! -e ~/sugoi-dev/lib/scala-library.jar ]; then
  cp ~/.sbt/boot/scala-2.10.3/lib/scala-library.jar ~/sugoi-dev/lib/
fi
