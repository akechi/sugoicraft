name := "sugoicraft"

version := "1.0"

scalaVersion := "2.10.3"

resolvers ++= Seq(
  "org.bukkit" at "http://repo.bukkit.org/service/local/repositories/snapshots/content/",
  "bee-client" at "http://repo.bigbeeconsultants.co.uk/repo/")

libraryDependencies ++= Seq(
  "org.bukkit" % "bukkit" % "1.7.2-R0.4-SNAPSHOT",
  "uk.co.bigbeeconsultants" %% "bee-client" % "0.21.+",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  "org.scalatest" %% "scalatest" % "2.0" % "test")
