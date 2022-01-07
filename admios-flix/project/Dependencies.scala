import sbt._

object Dependencies {

  object Versions {
    val cats = "2.6.1"
    val catsEffects = "2.5.1"
    val doobie = "0.13.4"
    val circe = "0.14.1"
    val circeConfig = "0.8.0"
    val http4s = "0.21.24"
    val rhoSwagger = "0.21.0"
    val enumeratum = "1.6.1"
    val fuuid = "0.5.0"
    val javaxCrypto = "1.0.1"
    val log4Cats = "1.1.1"
    val http4sJwtAut = "0.0.5"
    val redis4cats = "0.10.3"
    val mouse = "1.0.3"

    // testing
    val scalaTest = "3.2.9"
    val scalaCheck = "1.15.4"
    val testContainers = "0.39.7"
    val scalatestPlus = "3.2.2.0"
  }

  val common: Seq[ModuleID] = Seq(
    "org.typelevel"     %% "cats-core"            % Versions.cats,
    "org.typelevel"     %% "mouse"                % Versions.mouse,
    "org.typelevel"     %% "cats-effect"          % Versions.catsEffects,
    "org.tpolecat"      %% "doobie-core"          % Versions.doobie withSources () withJavadoc (),
    "org.tpolecat"      %% "doobie-postgres"      % Versions.doobie,
    "org.tpolecat"      %% "doobie-hikari"        % Versions.doobie,
    "io.circe"          %% "circe-core"           % Versions.circe,
    "io.circe"          %% "circe-parser"         % Versions.circe,
    "io.circe"          %% "circe-generic"        % Versions.circe,
    "io.circe"          %% "circe-generic-extras" % Versions.circe,
    "io.circe"          %% "circe-config"         % Versions.circeConfig,
    "org.http4s"        %% "http4s-circe"         % Versions.http4s,
    "org.http4s"        %% "http4s-dsl"           % Versions.http4s,
    "org.http4s"        %% "http4s-blaze-server"  % Versions.http4s,
    "org.http4s"        %% "http4s-blaze-client"  % Versions.http4s,
    "com.beachape"      %% "enumeratum"           % Versions.enumeratum,
    "com.beachape"      %% "enumeratum-circe"     % Versions.enumeratum,
    "io.chrisdavenport" %% "fuuid"                % Versions.fuuid,
    "io.chrisdavenport" %% "fuuid-circe"          % Versions.fuuid,
    "io.chrisdavenport" %% "fuuid-http4s"         % Versions.fuuid,
    "io.chrisdavenport" %% "fuuid-doobie"         % Versions.fuuid,
    "io.chrisdavenport" %% "log4cats-slf4j"       % Versions.log4Cats,
    "javax.xml.crypto"   % "jsr105-api"           % Versions.javaxCrypto,
    "dev.profunktor"    %% "redis4cats-effects"   % Versions.redis4cats,
    "dev.profunktor"    %% "redis4cats-log4cats"  % Versions.redis4cats
  )

  val http: Seq[ModuleID] = Seq(
    "dev.profunktor" %% "http4s-jwt-auth" % Versions.http4sJwtAut
  )

  val tests: Seq[ModuleID] = Seq(
    "org.scalatest"     %% "scalatest"                       % Versions.scalaTest,
    "org.scalacheck"    %% "scalacheck"                      % Versions.scalaCheck,
    "com.dimafeng"      %% "testcontainers-scala"            % Versions.testContainers,
    "com.dimafeng"      %% "testcontainers-scala-postgresql" % Versions.testContainers,
    "org.scalatestplus" %% "scalacheck-1-14"                 % Versions.scalatestPlus
  ).map(_ % s"it,$Test")

  val swaggerDependencies: Seq[ModuleID] = Seq(
    "org.http4s" %% "rho-swagger" % Versions.rhoSwagger
  )

}
