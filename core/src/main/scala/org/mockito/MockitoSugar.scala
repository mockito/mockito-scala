package org.mockito

import scala.language.experimental.macros

trait MockitoSugar extends InternalMockitoSugar

/**
  * Simple object to allow the usage of the trait without mixing it in
  */
object MockitoSugar extends MockitoSugar
