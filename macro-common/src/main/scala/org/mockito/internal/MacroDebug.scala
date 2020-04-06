package org.mockito.internal

import scala.reflect.macros.blackbox

object MacroDebug {
  def debugResult(c: blackbox.Context)(enablingFlag: String)(tree: c.Tree): Unit = {
    import c.universe._

    if (c.settings.contains(enablingFlag)) {
      val pos = s"${c.enclosingPosition.source.file.name}:${c.enclosingPosition.line}"
      println(pos + " " + show(tree))
    }
  }
}
