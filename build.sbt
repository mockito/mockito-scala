import scala.io.Source
import scala.language.postfixOps
import sbt.io.Using

val currentScalaVersion = "2.13.10"

inThisBuild(
  Seq(
    scalaVersion := currentScalaVersion,
    // Load version from the file so that Gradle/Shipkit and SBT use the same version
    version := sys.env
      .get("PROJECT_VERSION")
      .filter(_.trim.nonEmpty)
      .orElse {
        lazy val VersionRE = """^version=(.+)$""".r
        Using.file(Source.fromFile)(baseDirectory.value / "version.properties") {
          _.getLines.collectFirst { case VersionRE(v) => v }
        }
      }
      .map(_.replace(".*", "-SNAPSHOT"))
      .get
  )
)

lazy val commonSettings =
  Seq(
    organization := "org.mockito",
    // Load version from the file so that Gradle/Shipkit and SBT use the same version
    crossScalaVersions := Seq(currentScalaVersion, "2.12.16", "2.11.12"),
    scalafmtOnCompile  := true,
    scalacOptions ++= Seq(
      "-unchecked",
      "-feature",
      "-deprecation:false",
      "-encoding",
      "UTF-8",
      "-Xfatal-warnings",
//      "-Xmacro-settings:mockito-print-when,mockito-print-do-something,mockito-print-verify,mockito-print-expect,mockito-print-captor,mockito-print-matcher,mockito-print-extractor,mockito-print-wrapper,mockito-print-lenient",
      "-language:reflectiveCalls,implicitConversions,experimental.macros,higherKinds"
    ),
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 11)) =>
          Seq("-Xsource:2.12", "-Ypartial-unification")
        case Some((2, 12)) =>
          Seq("-Ypartial-unification", "-Ywarn-unused:locals")
        case Some((2, 13)) =>
          Seq("-Ywarn-unused:locals")
        case _ =>
          Seq()
      }
    },
    Test / scalacOptions += "-Ywarn-value-discard",
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, major)) if major <= 12 =>
          Seq()
        case _ =>
          Seq("org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4")
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
  publish         := {},
  publishLocal    := {},
  publishArtifact := false
)

lazy val noCrossBuildSettings = Seq(
  crossScalaVersions := Nil,
  publish / skip     := true
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
      Dependencies.cats.value,
      Dependencies.catsLaws.value      % "test",
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
    libraryDependencies ++= Dependencies.commonLibraries ++
      Dependencies.scalaReflection.value ++ Seq(
        Dependencies.catsLaws.value   % "test",
        Dependencies.scalacheck.value % "test"
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
    libraryDependencies ++= Dependencies.scalaReflection.value,
    // TODO remove when we remove the deprecated classes in org.mockito.integrations.Dependencies.scalatest
    libraryDependencies += Dependencies.scalatest % "provided",
    // include the macro classes and resources in the main jar
    Compile / packageBin / mappings ++= (macroSub / Compile / packageBin / mappings).value,
    // include the macro sources in the main source jar
    Compile / packageSrc / mappings ++= (macroSub / Compile / packageSrc / mappings).value,
    // include the common classes and resources in the main jar
    Compile / packageBin / mappings ++= (common / Compile / packageBin / mappings).value,
    // include the common sources in the main source jar
    Compile / packageSrc / mappings ++= (common / Compile / packageSrc / mappings).value,
    // include the common classes and resources in the main jar
    Compile / packageBin / mappings ++= (macroCommon / Compile / packageBin / mappings).value,
    // include the common sources in the main source jar
    Compile / packageSrc / mappings ++= (macroCommon / Compile / packageSrc / mappings).value
  )

lazy val macroSub = (project in file("macro"))
  .dependsOn(common)
  .settings(
    commonSettings,
    noPublishingSettings,
    libraryDependencies ++= Dependencies.commonLibraries,
    libraryDependencies ++= Dependencies.scalaReflection.value,
    publish         := {},
    publishLocal    := {},
    publishArtifact := false
  )

lazy val macroCommon = (project in file("macro-common"))
  .settings(
    commonSettings,
    noPublishingSettings,
    libraryDependencies ++= Dependencies.scalaReflection.value,
    publish         := {},
    publishLocal    := {},
    publishArtifact := false
  )

lazy val root = (project in file("."))
  .settings(noPublishingSettings, noCrossBuildSettings)
  .aggregate(common, core, scalatest, specs2, cats, scalaz)
