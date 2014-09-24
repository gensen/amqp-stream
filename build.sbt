name := "amqp-stream"

organization := "com.gs"

scalaVersion := "2.11.2"

releaseSettings

resolvers ++= Seq(
  "GS Artifactory" at "http://artifactory.generalsensing.com/artifactory/libs-release"
)

publishTo := {
  val artifactory = "http://artifactory.generalsensing.com/artifactory/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at artifactory + "libs-snapshot-local")
  else
    Some("releases" at artifactory + "libs-release-local")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

scalacOptions ++= Seq(
  "-feature",
  "-unchecked",
  "-deprecation",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps",
  "-Yno-adapted-args",
  "-encoding",
  "utf8"
)

libraryDependencies ++= {
  Seq(
    "com.rabbitmq"               %  "amqp-client"          % "3.3.4",
    "org.scalaz"                 %% "scalaz-core"          % "7.1.0",
    "org.scalaz.stream"          %% "scalaz-stream"        % "0.5a",
    "org.scalacheck"             %% "scalacheck"           % "1.11.3" % "test"
  )
}
