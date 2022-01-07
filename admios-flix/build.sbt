import Dependencies._
import org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version      := "1.0.0-SNAPSHOT"
ThisBuild / organization := "com.admios"

lazy val commonSettings = Seq(
  name := "admios-flix",
  libraryDependencies ++= common,
  dockerBaseImage := "openjdk:11-jre",
  addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.0" cross CrossVersion.full),
  addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
)

lazy val ItConf: Configuration = config("it") extend Test
lazy val testSettings = inConfig(ItConf)(Defaults.testSettings ++ scalafmtConfigSettings) ++ Seq(
  libraryDependencies ++= tests
)

lazy val core = project
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    name += "-core",
    coverageMinimumStmtTotal := 87,
    testSettings
  )

lazy val http = project
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    name += "-http",
    Docker / packageName     := "admios-flix",
    dockerExposedPorts       := Seq(8080),
    coverageMinimumStmtTotal := 0.0,
    libraryDependencies ++= Dependencies.http,
    testSettings,
    compile / mainClass := Option("com.admios.flix")
  )
  .dependsOn(core % "compile->compile;test->test", core % "compile->compile;test->it")
  .enablePlugins(DockerPlugin, AshScriptPlugin)

lazy val swagger = project
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    name += "-swagger",
    Docker / packageName := "admios-flix-swagger",
    dockerExposedPorts   := Seq(8081),
    libraryDependencies ++= swaggerDependencies,
    compile / mainClass      := Option("com.admios.flix"),
    coverageMinimumStmtTotal := 0.0
  )
  .dependsOn(http)
  .enablePlugins(DockerPlugin, AshScriptPlugin)
