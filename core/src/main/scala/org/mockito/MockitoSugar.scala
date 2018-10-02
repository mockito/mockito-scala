package org.mockito

import org.mockito.stubbing.ScalaFirstStubbing

import scala.language.experimental.macros

trait MockitoSugar extends MockitoEnhancer with DoSomething with Verifications with Rest {
  /**
    * Delegates to <code>Mockito.when()</code>, it's only here to expose the full Mockito API
    */
  def when[T](expr: T): ScalaFirstStubbing[T] = macro WhenMacro.traditionalWhen[T]

}

/**
  * Simple object to allow the usage of the trait without mixing it in
  */
object MockitoSugar extends MockitoSugar
