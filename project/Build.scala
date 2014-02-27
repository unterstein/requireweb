import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "requireweb"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "com.github.tuxBurner" %% "play-neo4jplugin" % "1.3.2",
    "commons-lang" % "commons-lang" % "2.6",
    "commons-io" % "commons-io" % "2.4",
    "com.typesafe" %% "play-plugins-mailer" % "2.1-RC2",
    "joda-time" % "joda-time" % "2.3",
    "com.google.code.gson" % "gson" % "2.2.4"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "tuxburner.github.io" at "http://tuxburner.github.io/repo",
    resolvers += "Neo4j" at "http://m2.neo4j.org/content/repositories/releases/",
    resolvers += "Spring milestones" at "http://repo.spring.io/milestone"
  )

}
