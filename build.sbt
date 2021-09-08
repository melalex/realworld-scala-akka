name := "realworld-scala-akka"
version := "0.1"
scalaVersion := "2.13.6"
idePackagePrefix := Some("com.melalex.realworld")

Test / fork := true

libraryDependencies ++= {
  val akkaHttpVersion       = "10.2.6"
  val akkaVersion           = "2.6.16"
  val akkaJsonVersion       = "1.37.0"
  val macWireVersion        = "2.4.1"
  val circeVersion          = "0.14.1"
  val logbackVersion        = "1.2.5"
  val pureConfigVersion     = "0.16.0"
  val scalaTestVersion      = "3.2.9"
  val mockitoVersion        = "1.16.39"
  val chimneyVersion        = "0.6.1"
  val slickVersion          = "3.3.3"
  val catsVersion           = "3.2.7"
  val jwtVersion            = "9.0.1"
  val mysqlVersion          = "8.0.26"
  val testContainersVersion = "0.39.7"
  val jbcryptVersion        = "0.4"

  val akkaDependencies = List(
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream"      % akkaVersion,
    "com.typesafe.akka" %% "akka-pki"         % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j"       % akkaVersion,
    "com.typesafe.akka" %% "akka-discovery"   % akkaVersion,
    "com.typesafe.akka" %% "akka-http"        % akkaHttpVersion
  )

  val catsDependencies = List(
    "org.typelevel" %% "cats-effect" % catsVersion
  )

  val jsonDependencies = List(
    "io.circe"             %% "circe-core"           % circeVersion,
    "io.circe"             %% "circe-generic"        % circeVersion,
    "io.circe"             %% "circe-parser"         % circeVersion,
    "io.circe"             %% "circe-generic-extras" % circeVersion,
    "de.heikoseeberger"    %% "akka-http-circe"      % akkaJsonVersion,
    "com.github.jwt-scala" %% "jwt-circe"            % jwtVersion
  )

  val utilDependencies = List(
    "ch.qos.logback"           % "logback-classic" % logbackVersion,
    "com.softwaremill.macwire" %% "macros"         % macWireVersion,
    "com.github.pureconfig"    %% "pureconfig"     % pureConfigVersion,
    "io.scalaland"             %% "chimney"        % chimneyVersion,
    "org.mindrot"              % "jbcrypt"         % jbcryptVersion
  )

  val databaseDependencies = List(
    "com.typesafe.slick" %% "slick"               % slickVersion,
    "com.typesafe.slick" %% "slick-hikaricp"      % slickVersion,
    "mysql"              % "mysql-connector-java" % mysqlVersion
  )

  val testDependencies = List(
    "org.scalatest"     %% "scalatest"                      % scalaTestVersion      % Test,
    "org.mockito"       %% "mockito-scala-scalatest"        % mockitoVersion        % Test,
    "com.typesafe.akka" %% "akka-http-testkit"              % akkaHttpVersion       % Test,
    "com.typesafe.akka" %% "akka-actor-testkit-typed"       % akkaVersion           % Test,
    "com.typesafe.akka" %% "akka-stream-testkit"            % akkaVersion           % Test,
    "com.dimafeng"      %% "testcontainers-scala-scalatest" % testContainersVersion % Test,
    "com.dimafeng"      %% "testcontainers-scala-mysql"     % testContainersVersion % Test
  )

  akkaDependencies ::: catsDependencies ::: jsonDependencies ::: utilDependencies ::: databaseDependencies ::: testDependencies
}
