package org.mockito
package stubbing

import org.mockito.internal.{ ValueClassExtractor, ValueClassWrapper }

import scala.reflect.ClassTag

object ScalaOngoingStubbing {
  implicit def toScalaOngoingStubbing[T: ValueClassExtractor](v: OngoingStubbing[T]): ScalaOngoingStubbing[T] = ScalaOngoingStubbing(v)
  implicit def toMock[T](s: ScalaOngoingStubbing[_]): T                                                       = s.getMock[T]
}

case class ScalaOngoingStubbing[T: ValueClassExtractor](delegate: OngoingStubbing[T]) extends ScalaBaseStubbing[T] {
  /**
   * Sets consecutive return values to be returned when the method is called. E.g:
   * <pre class="code"><code class="java">
   * when(mock.someMethod()).thenReturn(1, 2, 3);
   * </code></pre>
   *
   * Last return value in the sequence (in example: 3) determines the behavior of further consecutive calls.
   * <p>
   * See examples in javadoc for {@link Mockito#when}
   *
   * @param value first return value
   * @param values next return values
   * @return object that allows stubbing consecutive calls
   */
  def andThen(value: T, values: T*): ScalaOngoingStubbing[T] = _thenReturn(value, values)

  /**
   * Sets Throwable objects to be thrown when the method is called. E.g:
   * <pre class="code"><code class="java">
   * when(mock.someMethod()).thenThrow(new RuntimeException());
   * </code></pre>
   *
   * If throwables contain a checked exception then it has to
   * match one of the checked exceptions of method signature.
   * <p>
   * You can specify throwables to be thrown for consecutive calls.
   * In that case the last throwable determines the behavior of further consecutive calls.
   * <p>
   * If throwable is null then exception will be thrown.
   * <p>
   * See examples in javadoc for {@link Mockito#when}
   *
   * @param throwables to be thrown on method invocation
   * @return object that allows stubbing consecutive calls
   */
  def andThenThrow(throwables: Throwable*): ScalaOngoingStubbing[T] = _thenThrow(throwables)

  /**
   * Sets a Throwable type to be thrown when the method is called. E.g:
   * <pre class="code"><code class="java">
   * when(mock.someMethod()).thenThrow(RuntimeException.class);
   * </code></pre>
   *
   * <p>
   * If the throwable class is a checked exception then it has to
   * match one of the checked exceptions of the stubbed method signature.
   * <p>
   * If throwable is null then exception will be thrown.
   * <p>
   * See examples in javadoc for {@link Mockito#when}
   *
   * <p>Note depending on the JVM, stack trace information may not be available in
   * the generated throwable instance.  If you require stack trace information,
   * use {@link OngoingStubbing#thenThrow(Throwable...)} instead.
   *
   * @param throwableType to be thrown on method invocation
   * @return object that allows stubbing consecutive calls
   */
  def andThenThrow[E <: Throwable: ClassTag]: ScalaOngoingStubbing[T] = _thenThrow

  /**
   * Sets the real implementation to be called when the method is called on a mock object.
   * <p>
   * As usual you are going to read <b>the partial mock warning</b>:
   * Object oriented programming is more less tackling complexity by dividing the complexity into separate, specific, SRPy objects.
   * How does partial mock fit into this paradigm? Well, it just doesn't...
   * Partial mock usually means that the complexity has been moved to a different method on the same object.
   * In most cases, this is not the way you want to design your application.
   * <p>
   * However, there are rare cases when partial mocks come handy:
   * dealing with code you cannot change easily (3rd party interfaces, interim refactoring of legacy code etc.)
   * However, I wouldn't use partial mocks for new, test-driven & well-designed code.
   * <pre class="code"><code class="java">
   *   // someMethod() must be safe (e.g. doesn't throw, doesn't have dependencies to the object state, etc.)
   *   // if it isn't safe then you will have trouble stubbing it using this api. Use Mockito.doCallRealMethod() instead.
   *   when(mock.someMethod()).thenCallRealMethod();
   *
   *   // calls real method:
   *   mock.someMethod();
   *
   * </code></pre>
   * See also javadoc {@link Mockito#spy(Object)} to find out more about partial mocks.
   * <b>Mockito.spy() is a recommended way of creating partial mocks.</b>
   * The reason is it guarantees real methods are called against correctly constructed object because you're responsible for constructing the object passed to spy() method.
   * <p>
   * See examples in javadoc for {@link Mockito#when}
   *
   * @return object that allows stubbing consecutive calls
   */
  def andThenCallRealMethod(): ScalaOngoingStubbing[T] = _thenCallRealMethod()

  def andThenAnswer(f: => T): ScalaOngoingStubbing[T] = _thenAnswer(f)
  def andThenAnswer[P0: ValueClassWrapper](f: P0 => T)(implicit classTag: ClassTag[P0] = defaultClassTag[P0]): ScalaOngoingStubbing[T] =
    _thenAnswer(f)
  def andThenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper](f: (P0, P1) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper](f: (P0, P1, P2) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper](f: (P0, P1, P2, P3) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper, P4: ValueClassWrapper](
      f: (P0, P1, P2, P3, P4) => T
  ): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper, P4: ValueClassWrapper, P5: ValueClassWrapper](
      f: (P0, P1, P2, P3, P4, P5) => T
  ): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper,
      P17: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper,
      P17: ValueClassWrapper,
      P18: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper,
      P17: ValueClassWrapper,
      P18: ValueClassWrapper,
      P19: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper,
      P17: ValueClassWrapper,
      P18: ValueClassWrapper,
      P19: ValueClassWrapper,
      P20: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

  def andThenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper,
      P17: ValueClassWrapper,
      P18: ValueClassWrapper,
      P19: ValueClassWrapper,
      P20: ValueClassWrapper,
      P21: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) => T): ScalaOngoingStubbing[T] =
    _thenAnswer(f)

//  (2 to 22).foreach { fn =>
//    val args = 0 until fn
//    print(s"""
//             |def andThenAnswer[${args.map(a => s"P$a: ValueClassWrapper").mkString(",")}](f: (${args.map(a => s"P$a").mkString(",")}) => T): ScalaOngoingStubbing[T] =
//             |    _thenAnswer(f)
//             |""".stripMargin)
//  }
}
