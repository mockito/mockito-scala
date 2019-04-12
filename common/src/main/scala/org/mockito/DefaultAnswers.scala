package org.mockito

import org.mockito.stubbing.DefaultAnswer

/**
 * Simple object to act as an 'enum' of DefaultAnswers
 */
object DefaultAnswers {
  val ReturnsDefaults: DefaultAnswer    = org.mockito.stubbing.ReturnsDefaults
  val ReturnsDeepStubs: DefaultAnswer   = org.mockito.stubbing.ReturnsDeepStubs
  val CallsRealMethods: DefaultAnswer   = org.mockito.stubbing.CallsRealMethods
  val ReturnsSmartNulls: DefaultAnswer  = org.mockito.stubbing.ReturnsSmartNulls
  val ReturnsEmptyValues: DefaultAnswer = org.mockito.stubbing.ReturnsEmptyValues
}
