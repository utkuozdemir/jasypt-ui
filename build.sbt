addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

name := "jasypt-ui"
version := "1.0"

val javaVersion = "1.8"
val scVersion = "2.11.8"

javacOptions ++= Seq("-source", javaVersion, "-target", javaVersion)

scalaVersion := scVersion

resolvers += Resolver.sonatypeRepo("releases")

unmanagedResourceDirectories in Compile += (scalaSource in Compile).value

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scVersion,
  "org.scalafx" %% "scalafx" % "8.0.60-R9",
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.2.2",
  "com.jsuereth" %% "scala-arm" % "1.4",
  "org.jasypt" % "jasypt" % "1.9.2"
)

fork := true