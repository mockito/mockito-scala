import scala.io.Source
import scala.language.postfixOps
import scala.util.Try

name := "mockito-scala"
organization := "org.mockito"

//Load version from the file so that Gradle/Shipkit and SBT use the same version
version := {
  val pattern = """^version=(.+)$""".r
  val source = Source.fromFile("version.properties")
  val version = Try(source.getLines.collectFirst {
    case pattern(v) => v
  }.get)
  source.close
  version.get
}

scalaVersion := "2.12.6"

libraryDependencies += "org.mockito" % "mockito-core" % "2.19.0"
libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.12.6"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
