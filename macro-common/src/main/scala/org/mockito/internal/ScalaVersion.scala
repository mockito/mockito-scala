package org.mockito.internal

import scala.util.Properties

sealed trait ScalaVersion
object ScalaVersion {
  case object V2_11 extends ScalaVersion
  case object V2_12 extends ScalaVersion
  case object V2_13 extends ScalaVersion

  val Current: ScalaVersion = {
    val version = Properties.scalaPropOrElse("version.number", "unknown")
    if (version.startsWith("2.11")) ScalaVersion.V2_11
    else if (version.startsWith("2.12")) ScalaVersion.V2_12
    else if (version.startsWith("2.13")) ScalaVersion.V2_13
    else throw new Exception(s"Unsupported scala version $version")
  }
}
