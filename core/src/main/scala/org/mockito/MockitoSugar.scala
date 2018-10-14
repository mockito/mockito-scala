package org.mockito

import scala.language.experimental.macros

trait MockitoSugar extends MockitoEnhancer with DoSomething with Verifications with Rest

/**
  * Simple object to allow the usage of the trait without mixing it in
  */
object MockitoSugar extends MockitoSugar
