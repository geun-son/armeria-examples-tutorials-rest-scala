val scala3Version = "3.2.0"
lazy val root = project
  .in(file("."))
  .settings(
    name := "armeria-tutorial-blog-service",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "com.linecorp.armeria" %% "armeria-scala" % "1.14.0",
      "ch.qos.logback" % "logback-classic" % "1.2.11"
    )
  )
