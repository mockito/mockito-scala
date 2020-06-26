package org.mockito

package object matchers {
  private val AnyArgMatcher: ArgumentMatcher[Any] = AllOf[Any]()

  def AnyArg[A]: ArgumentMatcher[A] = AnyArgMatcher.asInstanceOf[ArgumentMatcher[A]]
}
