import sbt._
import sbt.Keys._

object Dependencies {

  val scalatestVersion = "3.2.16"

  val commonLibraries = Seq(
    "org.mockito"    % "mockito-core"      % "4.8.1",
    "org.scalactic" %% "scalactic"         % scalatestVersion,
    "ru.vyarus"      % "generics-resolver" % "3.0.3"
  )

  val scalacheck = Def.setting(
    if (scalaBinaryVersion.value == "3") {
      "org.scalacheck" %% "scalacheck" % "1.15.4"
    } else {
      "org.scalacheck" %% "scalacheck" % "1.15.2"
    }
  )

  val scalatest = "org.scalatest" %% "scalatest" % scalatestVersion

  val specs2 = Seq(
    "org.specs2"  %% "specs2-core"   % "4.10.6" % "provided",
    "org.hamcrest" % "hamcrest-core" % "2.2"    % "provided"
  )

  val scalaReflection = Def.setting(
    if (scalaBinaryVersion.value == "3") {
      Nil
    } else {
      Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)
    }
  )

  val cats = Def.setting(
    if (scalaBinaryVersion.value == "3") {
      "org.typelevel" %% "cats-core" % "2.7.0" % "provided"
    } else {
      "org.typelevel" %% "cats-core" % "2.0.0" % "provided"
    }
  )
  val scalaz = "org.scalaz" %% "scalaz-core" % "7.3.7" % "provided"

  val catsLaws = Def.setting(
    if (scalaBinaryVersion.value == "3") {
      "org.typelevel" %% "cats-laws" % "2.7.0"
    } else {
      "org.typelevel" %% "cats-laws" % "2.0.0"
    }
  )
  val disciplineScalatest = "org.typelevel" %% "discipline-scalatest" % "2.1.1"
}
