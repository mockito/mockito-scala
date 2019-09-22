package user.org.mockito
import user.org.mockito.matchers.{ ValueCaseClass, ValueClass }

import scala.annotation.varargs

trait FooTrait {
  def bar = "not mocked"

  def iHaveByNameArgs(normal: String, byName: => String, byName2: => String): String = "not mocked"

  def iHaveSomeDefaultArguments(noDefault: String, default: String = "default value"): String = "not mocked"

  def iStartWithByNameArgs(byName: => String, normal: String): String = "not mocked"

  def iHavePrimitiveByNameArgs(byName: => Int, normal: String): String = "not mocked"

  def iHaveByNameAndVarArgs(byName: => String, normal: String, byName2: => String, normal2: String, vararg: String*)(
      byName3: => String,
      normal3: String,
      vararg3: String*
  ): String = "not mocked"

  def iHaveFunction0Args(normal: String, f0: () => String): String = "not mocked"

  def iHaveByNameAndFunction0Args(normal: String, f0: () => String, byName: => String): String = "not mocked"

  def returnBar: Bar = new Bar

  def doSomethingWithThisIntAndString(v: Int, v2: String): ValueCaseClass = ValueCaseClass(v)

  def returnsValueCaseClass: ValueCaseClass = ValueCaseClass(-1)

  def returnsValueCaseClass(i: Int): ValueCaseClass = ValueCaseClass(i)

  def baz(i: Int, b: Baz2): String = "not mocked"
}

class Foo extends FooTrait

class Bar {
  def iAlsoHaveSomeDefaultArguments(noDefault: String, default: String = "default value"): String = "not mocked"
  def iHaveDefaultArgs(v: String = "default"): String                                             = "not mocked"
}

trait Baz {
  def varargMethod(s: String, arg: Int*): Int                                                = -1
  def varargMethod(arg: Int*): Int                                                           = -1
  @varargs def javaVarargMethod(arg: Int*): Int                                              = -1
  def byNameMethod(arg: => Int): Int                                                         = -1
  def traitMethod(arg: Int): ValueCaseClass                                                  = ValueCaseClass(arg)
  def traitMethodWithDefaultArgs(defaultArg: Int = 30, anotherDefault: String = "hola"): Int = -1
}

class ConcreteBaz extends Baz

class HigherKinded[F[_]] {
  def method: F[Either[String, String]]  = null.asInstanceOf[F[Either[String, String]]]
  def method2: F[Either[String, String]] = null.asInstanceOf[F[Either[String, String]]]
}

class FooWithBaz extends Foo with Baz

class ConcreteHigherKinded extends HigherKinded[Option]

class Implicit[T]

class Org {
  def bar = "not mocked"
  def baz = "not mocked"

  def defaultParams(a: String, defaultParam1: Boolean = false, defaultParam2: Int = 1): String               = "not mocked"
  def curriedDefaultParams(a: String, defaultParam1: Boolean = false)(defaultParam2: Int = a.length): String = "not mocked"

  def doSomethingWithThisInt(v: Int): Int = -1

  def doSomethingWithThisIntAndString(v: Int, v2: String): String = "not mocked"

  def doSomethingWithThisIntAndStringAndBoolean(v: Int, v2: String, v3: Boolean): String = "not mocked"

  def returnBar: Bar = new Bar

  def highOrderFunction(f: Int => String): String = "not mocked"

  def iReturnAFunction(v: Int): Int => String = i => (i * v).toString

  def iBlowUp(v: Int, v2: String): String = throw new IllegalArgumentException("I was called!")

  def iHaveTypeParamsAndImplicits[A, B](a: A, b: B)(implicit v3: Implicit[A]): String = "not mocked"

  def valueClass(n: Int, v: ValueClass): String = "not mocked"

  def valueCaseClass(n: Int, v: ValueCaseClass): String = "not mocked"

  def returnsValueCaseClass: ValueCaseClass = ValueCaseClass(-1)

  def baz(i: Int, b: Baz2): String = "not mocked"

  def fooWithVarArg(bells: String*): Unit                                                  = ()
  def fooWithActualArray(bells: Array[String]): Unit                                       = ()
  def fooWithSecondParameterList(bell: String)(cheese: Cheese): Unit                       = ()
  def fooWithVarArgAndSecondParameterList(bells: String*)(cheese: Cheese): Unit            = ()
  def fooWithActualArrayAndSecondParameterList(bells: Array[String])(cheese: Cheese): Unit = ()
  def valueClassWithVarArg(bread: Bread*): Unit                                            = ()
  def valueClassWithSecondParameterList(bread: Bread)(cheese: Cheese): Unit                = ()
  def valueClassWithVarArgAndSecondParameterList(breads: Bread*)(cheese: Cheese): Unit     = ()

  def option: Option[String]                    = None
  def option(a: String, b: Int): Option[String] = None
}

case class Baz2(param1: Int, param2: String)

trait ParametrisedTrait[+E] { def m(): E }
class ParametrisedTraitInt extends ParametrisedTrait[Int] { def m() = -1 }

class TestController(org: Org) {
  def async(f: => Int): Int = f
  def test(id: Int)         = async { org.doSomethingWithThisInt(id) }
}
