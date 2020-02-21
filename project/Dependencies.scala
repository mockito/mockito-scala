import sbt._

object Dependencies {

  val scalatestVersion = "3.1.1"

  val commonLibraries = Seq(
    "org.mockito"   % "mockito-core"      % "3.3.0",
    "org.scalactic" %% "scalactic"        % scalatestVersion,
    "ru.vyarus"     % "generics-resolver" % "3.0.2",
  )

  val scalatest = "org.scalatest" %% "scalatest" % scalatestVersion

  val specs2 = Seq(
    "org.specs2"   %% "specs2-core"  % "4.8.3" % "provided",
    "org.hamcrest" % "hamcrest-core" % "2.2"   % "provided"
  )

  def scalaReflection(scalaVersion: String) = "org.scala-lang" % "scala-reflect" % scalaVersion

  val cats   = "org.typelevel" %% "cats-core"   % "2.0.0" % "provided"
  val scalaz = "org.scalaz"    %% "scalaz-core" % "7.2.30"   % "provided"
}
