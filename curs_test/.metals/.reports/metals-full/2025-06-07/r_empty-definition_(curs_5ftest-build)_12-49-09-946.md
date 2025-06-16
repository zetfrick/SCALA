error id: file:///D:/ИРИТ/SCALA/curs_test/build.sbt:
file:///D:/ИРИТ/SCALA/curs_test/build.sbt
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 412
uri: file:///D:/ИРИТ/SCALA/curs_test/build.sbt
text:
```scala
// curs_test/build.sbt
scalaVersion := "3.5.0"

name := "curs_test"
organization := "ch.epfl.scala"
version := "1.0"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.3.12",
      "org.http4s" %% "http4s-blaze-server" % "0.23.12",
      "org.http4s" %% "http4s-circe" % "0.23.12",
      "or@@g.http4s" %% "http4s-dsl" % "0.23.12",
      "io.circe" %% "circe-generic" % "0.14.3",
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC2",
      "org.tpolecat" %% "doobie-h2" % "1.0.0-RC2",
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )

```


#### Short summary: 

empty definition using pc, found symbol in pc: 