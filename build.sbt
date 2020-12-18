import scala.io.Source
import scala.language.postfixOps
import scala.util.Try

val currentScalaVersion = "2.13.4"

ThisBuild / scalaVersion := currentScalaVersion

lazy val commonSettings =
  Seq(
    organization := "org.mockito",
    //Load version from the file so that Gradle/Shipkit and SBT use the same version
    version := {
      val versionFromEnv = System.getenv("PROJECT_VERSION")
      if (versionFromEnv != null && !versionFromEnv.trim().isEmpty()) {
        versionFromEnv
      } else {
        val pattern = """^version=(.+)$""".r
        val source  = Source.fromFile("version.properties")
        val version = Try(source.getLines.collectFirst { case pattern(v) =>
          v
        }.get)
        source.close
        version.get.replace(".*", "-SNAPSHOT")
      }
    },
    crossScalaVersions := Seq(currentScalaVersion, "2.12.13", "2.11.12"),
    scalafmtOnCompile := true,
    scalacOptions ++= Seq(
      "-unchecked",
      "-feature",
      "-deprecation:false",
      "-encoding",
      "UTF-8",
      "-Xfatal-warnings",
      "-language:reflectiveCalls,implicitConversions,experimental.macros,higherKinds"
//      "-Xmacro-settings:mockito-print-when,mockito-print-do-something,mockito-print-verify,mockito-print-expect,mockito-print-captor,mockito-print-matcher,mockito-print-extractor,mockito-print-wrapper,mockito-print-lenient"
    ),
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 11)) =>
          Seq("-Xsource:2.12", "-Ypartial-unification")
        case Some((2, 12)) =>
          Seq("-Ypartial-unification")
        case _ =>
          Nil
      }
    },
    Test / scalacOptions += "-Ywarn-value-discard",
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, major)) if major <= 12 =>
          Seq()
        case _ =>
          Seq("org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.0")
      }
    }
  )

lazy val publishSettings = Seq(
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

lazy val noPublishingSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
)

lazy val noCrossBuildSettings = Seq(
  crossScalaVersions := Nil,
  publish / skip := true
)

lazy val scalatest = (project in file("scalatest"))
  .dependsOn(core)
  .dependsOn(common % "compile-internal, test-internal")
  .dependsOn(macroSub % "compile-internal, test-internal")
  .settings(
    name := "mockito-scala-scalatest",
    commonSettings,
    publishSettings,
    libraryDependencies += Dependencies.scalatest % "provided"
  )

lazy val specs2 = (project in file("specs2"))
  .dependsOn(core)
  .dependsOn(common % "compile-internal, test-internal")
  .dependsOn(macroSub % "compile-internal, test-internal")
  .settings(
    name := "mockito-scala-specs2",
    commonSettings,
    publishSettings,
    libraryDependencies ++= Dependencies.specs2
  )

lazy val cats = (project in file("cats"))
  .dependsOn(core)
  .dependsOn(common % "compile-internal, test-internal, test->test")
  .dependsOn(macroSub % "compile-internal, test-internal")
  .settings(
    name := "mockito-scala-cats",
    commonSettings,
    publishSettings,
    libraryDependencies ++= Seq(
      Dependencies.cats,
      Dependencies.catsLaws            % "test",
      Dependencies.disciplineScalatest % "test",
      Dependencies.scalatest           % "test"
    )
  )

lazy val scalaz = (project in file("scalaz"))
  .dependsOn(core)
  .dependsOn(common % "compile-internal, test-internal")
  .dependsOn(macroSub % "compile-internal, test-internal")
  .settings(
    name := "mockito-scala-scalaz",
    commonSettings,
    publishSettings,
    libraryDependencies ++= Seq(
      Dependencies.scalaz,
      Dependencies.scalatest % "test"
    )
  )

lazy val common = (project in file("common"))
  .dependsOn(macroCommon)
  .settings(
    commonSettings,
    noPublishingSettings,
    libraryDependencies ++= Dependencies.commonLibraries ++ Seq(
      Dependencies.scalaReflection(scalaVersion.value),
      Dependencies.catsLaws   % "test",
      Dependencies.scalacheck % "test"
    )
  )

lazy val core = (project in file("core"))
  .dependsOn(macroSub % "compile-internal, test-internal")
  .dependsOn(common % "compile-internal, test-internal")
  .settings(
    commonSettings,
    publishSettings,
    name := "mockito-scala",
    libraryDependencies ++= Dependencies.commonLibraries,
    libraryDependencies += Dependencies.scalaReflection(scalaVersion.value),
    //TODO remove when we remove the deprecated classes in org.mockito.integrations.Dependencies.scalatest
    libraryDependencies += Dependencies.scalatest % "provided",
    // include the macro classes and resources in the main jar
    mappings in (Compile, packageBin) ++= mappings
      .in(macroSub, Compile, packageBin)
      .value,
    // include the macro sources in the main source jar
    mappings in (Compile, packageSrc) ++= mappings
      .in(macroSub, Compile, packageSrc)
      .value,
    // include the common classes and resources in the main jar
    mappings in (Compile, packageBin) ++= mappings
      .in(common, Compile, packageBin)
      .value,
    // include the common sources in the main source jar
    mappings in (Compile, packageSrc) ++= mappings
      .in(common, Compile, packageSrc)
      .value,
    // include the common classes and resources in the main jar
    mappings in (Compile, packageBin) ++= mappings
      .in(macroCommon, Compile, packageBin)
      .value,
    // include the common sources in the main source jar
    mappings in (Compile, packageSrc) ++= mappings
      .in(macroCommon, Compile, packageSrc)
      .value
  )

lazy val macroSub = (project in file("macro"))
  .dependsOn(common)
  .settings(
    commonSettings,
    noPublishingSettings,
    libraryDependencies ++= Dependencies.commonLibraries,
    libraryDependencies += Dependencies.scalaReflection(scalaVersion.value),
    publish := {},
    publishLocal := {},
    publishArtifact := false
  )

lazy val macroCommon = (project in file("macro-common"))
  .settings(
    commonSettings,
    noPublishingSettings,
    libraryDependencies += Dependencies.scalaReflection(scalaVersion.value),
    publish := {},
    publishLocal := {},
    publishArtifact := false
  )

lazy val root = (project in file("."))
  .settings(noPublishingSettings, noCrossBuildSettings)
  .aggregate (common, core, scalatest, specs2, cats, scalaz)
