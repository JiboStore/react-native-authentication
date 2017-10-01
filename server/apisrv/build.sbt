import com.typesafe.sbt.packager.MappingsHelper._

mappings in Universal ++= directory(baseDirectory.value / "resources")
    
name := "mangafun"

version := "0.0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

organization := "be.objectify"

libraryDependencies ++= Seq(
  cache,
  ws,
  "be.objectify" %% "deadbolt-scala" % "2.5.0",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.14",
  "org.mindrot" % "jbcrypt" % "0.4",
  "commons-io" % "commons-io" % "2.4",
  "com.google.code.gson" % "gson" % "2.2.4"
)

routesGenerator := InjectedRoutesGenerator

PlayKeys.devSettings := Seq("play.server.http.port" -> "3005")

fork in run := true