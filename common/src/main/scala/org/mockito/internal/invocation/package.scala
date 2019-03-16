package org.mockito.internal

import org.mockito.ArgumentMatcher
import org.mockito.matchers.EqTo
import org.scalactic.Prettifier

import scala.collection.JavaConverters._

package object invocation {

  def argumentsToMatchers(arguments: Iterable[Any])(implicit $pt: Prettifier): java.util.List[ArgumentMatcher[_]] =
    arguments.map(EqTo(_).asInstanceOf[ArgumentMatcher[_]]).toList.asJava

}
