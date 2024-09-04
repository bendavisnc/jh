val Http4sVersion = "0.23.27"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.2.6"
val Slf4jVersion = "1.7.32"
val MunitCatsEffectVersion = "1.0.6"

lazy val root = (project in file("."))
  .settings(
    organization := "watweather",
    name := "watweather",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.3.0",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server" % Http4sVersion,
      "org.http4s"      %% "http4s-ember-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "io.circe"        %% "circe-generic"       % "0.14.6",
      "io.circe"        %% "circe-parser"        % "0.14.6",
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "org.scalameta"   %% "munit"               % MunitVersion           % Test,
      "org.typelevel"   %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "org.slf4j"       % "slf4j-api"            % Slf4jVersion
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    Compile / run / fork := true,
    Global / onChangedBuildSource := ReloadOnSourceChanges
  )
