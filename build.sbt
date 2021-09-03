name := "realworld-scala-akka"
version := "0.1"
scalaVersion := "2.13.6"
idePackagePrefix := Some("com.melalex.realworld")

libraryDependencies ++= {
  val akkaHttpVersion   = "10.2.0"
  val akkaVersion       = "2.6.8"
  val akkaJsonVersion   = "1.34.0"
  val macWireVersion    = "2.3.7"
  val circeVersion      = "0.13.0"
  val logbackVersion    = "1.2.3"
  val pureConfigVersion = "0.13.0"
  val scalaTestVersion  = "3.2.1"
  val scalaMockVersion  = "5.0.0"
  val chimneyVersion    = "0.6.1"
  val slickVersion      = "3.3.2"
  val catsVersion       = "3.2.5"

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
    "io.circe"          %% "circe-core"           % circeVersion,
    "io.circe"          %% "circe-generic"        % circeVersion,
    "io.circe"          %% "circe-parser"         % circeVersion,
    "io.circe"          %% "circe-generic-extras" % circeVersion,
    "de.heikoseeberger" %% "akka-http-circe"      % akkaJsonVersion
  )

  val utilDependencies = List(
    "ch.qos.logback"           % "logback-classic" % logbackVersion,
    "com.softwaremill.macwire" %% "macros"         % macWireVersion,
    "com.github.pureconfig"    %% "pureconfig"     % pureConfigVersion,
    "io.scalaland"             %% "chimney"        % chimneyVersion
  )

  val databaseDependencies = List(
    "com.typesafe.slick" %% "slick"          % slickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
  )

  val testDependencies = List(
    "org.scalatest"     %% "scalatest"                % scalaTestVersion % Test,
    "org.scalamock"     %% "scalamock"                % scalaMockVersion % Test,
    "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion  % Test,
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion      % Test,
    "com.typesafe.akka" %% "akka-stream-testkit"      % akkaVersion      % Test
  )

  akkaDependencies ::: catsDependencies ::: jsonDependencies ::: utilDependencies ::: databaseDependencies ::: testDependencies
}
