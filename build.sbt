name := "logins-analyzer"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.rogach" %% "scallop" % "3.3.0",
  "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"
)

mainClass in assembly := Some("ru.johnspade.logins.Main")
