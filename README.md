## Compile

    $ sbt compile

## Run

    $ sbt package

Copy the jar `target/scala-2.10/sugoicraft_2.10-1.0.jar` to your craftbukkit `plugins` dir.

Create `lib` dir in craftbukkit, and copy your `scala-library` there. `sbt 'full-classpath'` will tell you where your `scala-library` jar file.

Modify your `run` script for craftbukkit like below.


    #!/bin/sh
    BINDIR=$(dirname "$(readlink -fn "$0")")
    cd "$BINDIR"
    exec java -Xmx3072M -cp craftbukkit.jar:lib/scala-library.jar org.bukkit.craftbukkit.Main -o true

## For developers

* <http://jd.bukkit.org/dev/apidocs/>

Q and A

* Why not include `scala-library` in generated jar file but let `run` specify it?
    * I did previously and it takes really long time every time to compile, with using `sbt-assembly`. Shorter compiliation time matters.
