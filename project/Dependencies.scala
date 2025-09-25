import sbt.*
import sbt.Keys.*

object Dependencies {

  val scalatestVersion = "3.2.19"

  val commonLibraries = Seq(
    "org.mockito"    % "mockito-core"      % "5.19.0",
    "org.scalactic" %% "scalactic"         % scalatestVersion,
    "ru.vyarus"      % "generics-resolver" % "3.0.3"
  )

  val scalacheck = "org.scalacheck" %% "scalacheck" % "1.19.0"

  val scalatest = "org.scalatest" %% "scalatest" % scalatestVersion

  val specs2 = Seq(
    "org.specs2"  %% "specs2-core"   % "4.22.0" % "provided",
    "org.hamcrest" % "hamcrest-core" % "3.0"    % "provided"
  )

  val scalaReflection = Def.setting(
    if (scalaBinaryVersion.value == "3") {
      Nil
    } else {
      Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)
    }
  )

  val cats   = "org.typelevel" %% "cats-core"   % "2.13.0" % "provided"
  val scalaz = "org.scalaz"    %% "scalaz-core" % "7.3.8"  % "provided"

  val catsLaws = "org.typelevel" %% "cats-laws" % "2.13.0"

  val disciplineScalatest = "org.typelevel" %% "discipline-scalatest" % "2.3.0"
}
