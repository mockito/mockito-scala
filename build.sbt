import sbt.Keys._

import scala.io.Source
import scala.language.postfixOps
import scala.util.Try

val _scalaVersion = "2.12.6"

lazy val commonSettings =
  Seq(
    scalaVersion := _scalaVersion,
    organization := "org.mockito",
    //Load version from the file so that Gradle/Shipkit and SBT use the same version
    version := {
      val pattern = """^version=(.+)$""".r
      val source  = Source.fromFile("version.properties")
      val version = Try(source.getLines.collectFirst {
        case pattern(v) => v
      }.get)
      source.close
      version.get
    }
  )

lazy val commonLibraries = Seq(
  "org.mockito" % "mockito-core" % "2.19.0",
  "org.scala-lang" % "scala-reflect" % _scalaVersion
)

lazy val core = (project in file("core"))
  .dependsOn(macroSub % "compile-internal, test-internal")
  .settings(
    commonSettings,
    name := "mockito-scala",
    libraryDependencies ++= commonLibraries,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    ),
    // include the macro classes and resources in the main jar
    mappings in (Compile, packageBin) ++= mappings
      .in(macroSub, Compile, packageBin)
      .value,
    // include the macro sources in the main source jar
    mappings in (Compile, packageSrc) ++= mappings
      .in(macroSub, Compile, packageSrc)
      .value,
    licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
    homepage := Some(url("https://github.com/mockito/mockito-scala")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/mockito/mockito-scala"),
        "git@github.com:mockito/mockito-scala.git"
      )
    ),
    developers := List(
      Developer(
        "bbonanno",
        "Bruno Bonanno",
        "bbonanno@gmail.com",
        url("https://github.com/bbonanno")
      )
    )
  )

lazy val macroSub = (project in file("macro"))
  .settings(
    commonSettings,
    libraryDependencies ++= commonLibraries,
    publish := {},
    publishLocal := {}
  )
