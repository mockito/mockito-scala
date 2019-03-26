package org.mockito.specs2

import java.util

import org.hamcrest.core.IsNull
import org.mockito.captor.ArgCaptor
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.DefaultAnswer
import org.specs2.control.Exceptions._
import org.specs2.execute._
import org.specs2.matcher.MatchersImplicits._
import org.specs2.matcher._
import ActionMatchers._
import org.specs2.fp._
import org.specs2.fp.syntax._
import org.specs2.specification._
import org.specs2.specification.core._
import org.specs2.specification.core.Env
import org.specs2.specification.process._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


class MockitoScalaSpec extends script.Spec with Mockito with Groups {
  def is =
    s2"""

 Mockito is a Java library for mocking.

 The following samples are taken from the main documentation which can be found here:
 https://static.javadoc.io/org.mockito/mockito-core/2.25.1/org/mockito/Mockito.html

 CREATION
 ========

 Mocks can be created
   + with a name
   + with a default return value
   + with a name and default return value
   + with a default answer
   + with settings

 VERIFICATION
 ============

 When a mock is created with the mock method
   + it is possible to call methods on the mock
   + it is possible to verify that a method has been called
   + if one method has not been called on a mock there will be a failure
   + it is possible to check that no calls have been made
   + null values can be checked with beNull
   + it is possible to pass byname parameters
     + with several byname parameters
     + with 2 parameter lists and byname parameters
   + it is possible to check byname parameters
     + with several byname parameters
     + with mixed byname parameter and byvalue parameter
     + with 2 parameter lists and byname parameters
   it is possible to check a function parameter
     + with one argument
     + with one argument and a matcher for the return value
     + with n arguments
     + with n arguments and a matcher for the return value
     + as being anything
     + with Nothing as the return type
     + with Any as the return type

   it is possible to check a partial function parameter
     + with n arguments
     + with n arguments and a matcher for the return value
     + as being anything
     + when the argument is not defined

   + it is possible to verify a function with repeated parameters
   + it is possible to specify a timeout for the call
   + it doesn't match maps and functions as equal
   + spies must not be checked for matchers when called for real

STUBS
=====

 It is also possible to return a specific value from a mocked method
   + then when the mocked method is called, the same values will be returned
   + different successive values can even be returned
   a value can be returned when a parameter of the method matches
     + a hamcrest matcher
     + a specs2 matcher
     + with a subtype matcher
     + a Set
     + a List

 It is also possible to throw an exception from a mocked method
   + then when the mocked method is called, the exception will be thrown
   + different successive exceptions can even be thrown

 + A mock can be created and stubbed at the same time

 NUMBER OF CALLS
 ===============

 The number of calls to a mocked method can be checked
   + if the mocked method has been called once
   + if the mocked method has been called twice
   + if the mocked method has been called exactly n times
   + if the mocked method has been called atLeast n times
   + if the mocked method has been called atMost n times
   + if the mocked method has never been called
   + if the verification throws an exception, it will be reported as an Error
   + if the mocked method has not been called after some calls
   + if the mocked method has not been called after some calls - ignoring stubs

 ORDER OF CALLS
 ==============

 The order of calls to a mocked method can be checked
   + with 2 calls that were in order
   + with 2 calls that were in order - ignoring stubbed methods
   + with 2 calls that were not in order - on the same mock
   + with 2 calls that were not in order - on the same mock, with thrown expectations
   + with 2 calls that were not in order - on different mocks
   + with 3 calls that were not in order

 ANSWERS & PARAMETERS CAPTURE
 ============================

 + Answers can be created to control the returned a value
 + Answers can use the method's parameters

 + A parameter can be captured in order to check its value
 + A parameter can be captured in order to check its successive values

 OTHER CONTEXTS
 ==============

// The Mockito trait is reusable in other contexts
//   + in mutable specs
//   + with an in order call

 MATCHERS
 ========

 + Various mockito matchers can be used
 + Matching with any

${step(env)}                                                                                                                        ${step(
      env)}
"""

  lazy val env = Env()

  "creation" - new group {
    eg := {
      val list = mock[java.util.List[String]]("list1")
      (list.add("one") was called).message must contain("list1.add(\"one\")")
    }
    eg := {
      val list = mock[java.util.List[String]](DefaultAnswer(10))
      list.size must_== 10
    }
    eg := {
      val list = mock[java.util.List[String] with Cloneable with Serializable](withSettings(DefaultAnswer(10)).name("list1"))
      (list.size must_== 10) and
        ((list.add("one") was called).message must contain("list1.add(\"one\")"))
    }
    eg := {
      val list = mock[java.util.List[String]](DefaultAnswer((_: InvocationOnMock) => "hello"))
      list.get(0) must_== "hello"
    }
    eg := {
      val list = mock[java.util.List[String]](withSettings.name("list1"))
      (list.add("one") was called).message must contain("list1.add(\"one\")")
    }
  }

  "verification" - new group with list {
    eg := { list.add("one"); success }
    eg := {
      list.add("one")
      list.add("one") was called
    }
    eg := (list.add("one") was called).message must startWith("The mock was not called as expected")

    eg := list wasNever called

    eg := {
      list.add(3, null: String)
      list.add(be_>(0), beNull[String]) was called
    }

    eg := {
      byname.call(10)
      byname.call(10) was called
    }
    eg := {
      byname.add(1, 2)
      byname.add(1, 2) was called
    }
    eg := {
      byname.mult(1)(2)
      byname.mult(1)(2) was called
    }
    eg := {
      byname.call(10)
      byname.call(be_>(5)) was called
    }
    eg := {
      byname.add(1, 2)
      byname.add(anyInt, anyInt) was called
    }
    eg := {
      byname.min(2, 1)
      byname.min(anyInt, anyInt) was called
    }
    eg := {
      byname.mult(1)(2)
      byname.mult(anyInt)(anyInt) was called
    }

    eg := {
      function1.call((_: Int).toString)
      function1.call(1 -> "1") was called
    }
    eg := {
      function1.call((_: Int).toString)
      (function1.call(1 -> startWith("1")) was called) and
        ((function1.call(1 -> startWith("2")) was called).message must contain("1 doesn't start with '2'"))
    }
    eg := {
      function2.call((i: Int, d: Double) => (i + d).toString)
      function2.call((1, 3.0) -> "4.0") was called
    }
    eg := {
      function2.call((i: Int, d: Double) => (i + d).toString)
      function2.call((1, 3.0) -> haveSize[String](3)) was called
    }
    eg := {
      function2.call((i: Int, d: Double) => (i + d).toString)
      function2.call(*) was called
    }
    eg := {
      functionNothing.call((_: Int) => throw new Exception)
      functionNothing.call(*) was called
    }
    eg := {
      functionAny.call(() => throw new Exception)
      functionAny.call(*) was called
    }

    eg := {
      partial.call { case (i: Int, d: Double) => (i + d).toString }
      partial.call((1, 3.0) -> "4.0") was called
    }
    eg := {
      partial.call { case (i: Int, d: Double) => (i + d).toString }
      partial.call((1, 3.0) -> haveSize[String](3)) was called
    }
    eg := {
      partial.call { case (i: Int, d: Double) => (i + d).toString }
      partial.call(*) was called
    }
    eg := {
      partial.call { case (i: Int, d: Double) if i > 10 => (i + d).toString }
      (partial.call((1, 3.0) -> "4.0") was called).message must contain("a PartialFunction defined for (1,3.0)")
    }

    eg := {
      repeated.call(1, 2, 3)
      (repeated.call(1, 2, 3) was called) and
        ((repeated.call(1, 2) was called).message must contain("withRepeatedParams.call(1, 2)"))
    }

    eg := {
      scala.concurrent.Future { Thread.sleep(200); takesSometime.call(10) }
      ((takesSometime.call(10) wasCalled (once within 10.millis)).message must contain("Wanted but not invoked")) and
        (takesSometime.call(10) wasCalled (once within 300.millis))
    }

    eg := {
      functionInt.call((i: Int) => i + 2)
      (functionInt.call(Map(1 -> 2)) was called).message must contain("Argument(s) are different")
    }

    eg := {
      val foo        = mock[FooComponent]
      val controller = spy(new TestController(foo))

      foo.getBar(1) returns 1
      // controller is a spy. Calling 'test' for real must not re-evaluate
      // the arguments, hence make a mock call, to register matchers
      controller.test(1)
      foo.getBar(1) wasCalled once
    }
  }

  "stubs" - new group with list {
    eg := {
      list.add("one") mustReturn true
      list.add("one") must_== true
    }
    eg := {
      list.add("one") returns true andThen false andThen true
      (list.add("one"), list.add("one"), list.add("one")) must_== ((true, false, true))
    }

    eg := {
      list.contains(new IsNull[String]) mustReturn true
      list.contains(null) must_== true
    }
    eg := {
      list.contains(beMatching(".*o")) returns true
      list.contains("o") must_== true
    }
    eg := {

      trait Vet { def treat(p: Pet) = true }
      trait Pet
      case class Dog() extends Pet
      case class Cat() extends Pet
      val vet = mock[Vet]
      vet.treat(Cat())
      def isDog: Matcher[Dog] = (_: Dog) => (true, "ok", "ko")

      vet.treat(isDog) must not(throwA[ClassCastException])
    }
    eg := {
      list.contains(Set(1)) returns true
      list.contains(Set(1)) must_== true
      list.contains(Set(2)) must_== false
    }
    eg := {
      list.contains(List(1)) returns true
      list.contains(List(1)) must_== true
      list.contains(List(2)) must_== false
    }
    eg := {
      list.clear() throws new RuntimeException
      list.clear()
    } must throwA[RuntimeException]

    eg := {
      list.clear() throws new RuntimeException andThenThrow new IllegalArgumentException
      tryo(list.clear())
      list.clear()
    } must throwAn[IllegalArgumentException]
    eg := {
      val mocked: java.util.List[String] = mock[java.util.List[String]]
      mocked.contains("o") returns true
      mocked.contains("o") must beTrue
    }
  }

  "number of calls" - new group with list {
    val list2 = mock[java.util.List[String]]

    list.add("one")
    1 to 2 foreach { _ =>
      list.add("two")
    }
    list2.add("one")

    eg := list.add("one") wasCalled once // equivalent to 'list.add("one") was called'
    eg := list.add("two") wasCalled twice
    eg := list.add("two") wasCalled atLeastOnce
    eg := list.add("two") wasCalled twice
    eg := list.add("two") wasCalled atMostTwice
    eg := list.add("four") wasNever called
    eg := {
      val cause = new Exception("cause")
      val e     = new Exception("error", cause)
      (list.add(be_=== { throw e; "four" }) wasNever called) must throwAn[Exception]
    }
    eg := {
      list.add("one") was called
      list.add("two") wasCalled twice
      list wasNever calledAgain
    }
    eg := {
      val list3 = mock[java.util.List[String]]
      val list4 = mock[java.util.List[String]]
      list3.contains("3") returns false
      list4.contains("4") returns false

      list3.add("one")
      list4.add("one"); list4.add("one")
      list3.contains("3")
      list4.contains("4")

      list3.add("one") was called
      list4.add("one") wasCalled twice

      list3 wasNever calledAgain(ignoringStubs)
      list4 wasNever calledAgain(ignoringStubs)
    }
  }

  "order of calls" - new group {
    val list1 = mock[java.util.List[String]]
    val list2 = mock[java.util.List[String]]

    eg := {
      list1.get(0)
      list2.get(0)

      InOrder(list1, list2) { implicit order =>
        (list1.get(0) was called) and (list2.get(0) was called)
      }.message must_== "The mock was called as expected"
    }

    eg := {
      list1.get(1) returns "1"

      // there is an out of order call but to a stubbed method
      list1.get(1)
      list1.get(0)
      list2.get(0)

      InOrder(ignoreStubs(list1, list2): _*) { implicit order =>
        (list1.get(0) was called) and (list2.get(0) was called)
      }.message must_== "The mock was called as expected"
    }

    eg := {
      list1.get(0)
      list1.get(1)

      InOrder(list1) { implicit order =>
        (list1.get(1) was called) and (list1.get(0) was called)
      }.message must startWith("The mock was not called as expected")
    }

    eg := {
      list1.get(0)
      list1.get(1)

      var result: Result = success

      new ThrownExpectations {
        result = InOrder(list1) { implicit order =>
          (list1.get(1) was called) and (list1.get(0) was called)
        }
      }

      result.message must startWith("The mock was not called as expected")
    }

    eg := {
      list1.get(0)
      list2.get(0)

      InOrder(list1, list2) { implicit order =>
        (list2.get(1) was called) and (list1.get(0) was called)
      }.message must startWith("The mock was not called as expected")
    }

    eg := {
      list1.get(0); list1.size; list1.get(0); list1.size

      InOrder(list1) { implicit order =>
        (list1.get(0) was called) and
          (list1.size() was called) and
          (list1.get(0) wasNever called) and
          (list1.size() was called)
      }.message must startWith("The mock was not called as expected")
    }

  }

  "callbacks" - new group {
    val list = mock[java.util.List[String]]("list")

    eg := {
      list.get(*) answers { i: Int =>
        "The parameter is " + i.toString
      }
      list.get(2) must_== "The parameter is 2"
    }

    eg := {
      list.get(*) answers { i: Int =>
        (i + 1).toString
      }
      list.get(1) must_== "2"
      list.get(5) must_== "6"
    }

    eg := {
      list.set(*, *) answers { (i: Int, s: String) =>
        s"The parameters are ($i,$s)"
      }
      list.set(1, "foo") must_== "The parameters are (1,foo)"
    }

    eg := {
      list.get(1)
      val c = ArgCaptor[Int]
      list.get(c) was called
      c.value must_== 1
    }

    eg := {
      list.get(1)
      list.get(2)
      val c = ArgCaptor[Int]
      list.get(c) wasCalled twice
      c.values.toString === "[1, 2]"
    }
  }

  "other contexts" - new group {
    eg := {
      val s = new org.specs2.mutable.Specification with Mockito {
        val list = mock[java.util.List[String]]
        "ex1" in {
          list.add("one")
          list.add("two") was called
          1 must_== 1 // to check if the previous expectation really fails
        }
      }
      DefaultExecutor.runSpec(s.is, env).filter(Fragment.isExample).traverse(_.executionResult.map(_.isSuccess)) must
        beOk((list: List[Boolean]) => list must contain(false))
    }

    eg := {
      val s = new org.specs2.mutable.Specification with Mockito {
        "ex1" in new org.specs2.specification.Scope {
          val (list1, list2) = (mock[java.util.List[String]], mock[java.util.List[String]])
          list1.add("two"); list2.add("one")
          InOrder(list1, list2) { implicit order =>
            (list2.add("two") was called) and (list1.add("one") was called)
          }
        }
      }
      DefaultExecutor.runSpec(s.is, env).filter(Fragment.isExample).traverse(_.executionResult.map(_.isSuccess)) must
        beOk((list: List[Boolean]) => list must contain(false))
    }
  }

  "mockito matchers" - new group with Mockito with ThrownExpectations {
    trait M {
      def javaList[T](a: java.util.List[T]): Unit
      def javaSet[T](a: java.util.Set[T]): Unit
      def javaCollection[T](a: java.util.Collection[T]): Unit
      def javaMap[K, V](a: java.util.Map[K, V]): Unit

      def List[T](a: List[T]): Unit
      def Set[T](a: Set[T]): Unit
      def Traversable[T](a: Traversable[T]): Unit
      def Map[K, V](a: Map[K, V]): Unit

      def varargs[T](ts: T*): Unit
      def array[T](ts: Array[T]): Unit

      def method(a1: A, b: Boolean): Int
    }

    trait A
    class B extends A { override def toString = "B" }
    class C extends A { override def toString = "C" }

    val m = mock[M]

    eg := {
      m.javaList(new util.ArrayList[Int])
      m.javaSet(new util.HashSet[Int])
      m.javaCollection(new util.ArrayList[Int])
      m.javaMap(new util.HashMap[Int, String])

      m.List(List[Int]())
      m.Set(Set[Int]())
      m.Traversable(List[Int]())
      m.Map(Map[Int, String]())

      m.varargs(1, 2)
      m.array(Array(1, 2))

      m.javaList(*) was called

      m.javaSet(*) was called

      m.javaCollection(*) was called

      m.javaMap(*) was called

      m.List(*) was called

      m.Set(*) was called

      m.Traversable(*) was called

      m.Map(*) was called

      m.varargs(*, *) was called
      m.array(*) was called
    }

    eg := {
      m.method(*, *) returns 1

      m.method(new B, b = true)
      m.method(*, *) was called
    }
  }

  /**
    * HELPERS
    */
  trait list {
    val list  = mock[java.util.List[String]]
    val queue = mock[scala.collection.immutable.Queue[String]]

    trait ByName {
      def call(i: => Int)            = i
      def add(i: => Int, j: => Int)  = i + j
      def min(i: Int, j: => Int)     = i - j
      def mult(i: => Int)(j: => Int) = i * j
    }
    val byname = mock[ByName]

    trait WithFunction1 { def call(f: Int => String) = f(0) }
    val function1 = mock[WithFunction1]

    trait WithFunction2 { def call(f: (Int, Double) => String) = f(1, 2.0) }
    val function2 = mock[WithFunction2]

    val functionNothing = mock[WithFunctionNothing]
    val functionAny     = mock[WithFunctionAny]
    val functionInt     = mock[WithFunctionInt]

    trait WithPartialFunction { def call(f: PartialFunction[(Int, Double), String]) = f.apply((1, 2.0)) }
    val partial = mock[WithPartialFunction]

    trait WithRepeatedParams { def call[T](params: T*) = params.toString }
    val repeated = mock[WithRepeatedParams]

    trait TakesSometime {
      def call(i: Int) = i
    }
    val takesSometime = mock[TakesSometime]
  }
}

trait WithFunctionNothing { def call(f: Int => Nothing) = 1 }
trait WithFunctionAny     { def call(f: () => Any)      = 1 }
trait WithFunctionInt     { def call(f: Int => Any)     = 1 }

// this example comes from https://github.com/etorreborre/specs2/issues/428
class FooComponent {
  def getBar(id: Int): Int = id
}

class TestController(foo: FooComponent) {
  def async(f: => Int): Int = {
    println("Evaluating async")
    f
  }
  def test(id: Int) = {
    println("Calling async")
    async { foo.getBar(id) }
  }
}
