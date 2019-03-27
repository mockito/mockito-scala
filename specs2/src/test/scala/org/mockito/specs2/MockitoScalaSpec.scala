package org.mockito.specs2

import java.util

import org.hamcrest.core.IsNull
import org.mockito.captor.ArgCaptor
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.DefaultAnswer
import org.specs2._
import org.specs2.control.Exceptions._
import org.specs2.execute._
import org.specs2.fp.syntax._
import org.specs2.matcher.ActionMatchers._
import org.specs2.matcher.MatchersImplicits._
import org.specs2.matcher._
import org.specs2.specification.core.{Env, _}
import org.specs2.specification.process._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class MockitoScalaSpec extends Spec with Mockito {
  def is       = s2"""

 Mockito is a Java library for mocking.

 The following samples are taken from the main documentation which can be found here:
 https://static.javadoc.io/org.mockito/mockito-core/2.25.1/org/mockito/Mockito.html

CREATION
========

 Mocks can be created
   with a name                           $creation1
   with a default return value           $creation2
   with a name and default return value  $creation3
   with a default answer                 $creation4
   with settings                         $creation5

VERIFICATION
============

 When a mock is created with the mock method
   it is possible to call methods on the mock                          $verification1
   it is possible to verify that a method has been called              $verification2
   if one method has not been called on a mock there will be a failure $verification3
   it is possible to check that no calls have been made                $verification4
   null values can be checked with beNull                              $verification5

   it is possible to pass byname parameters                            $verification6
     with several byname parameters                                    $verification7
     with 2 parameter lists and byname parameters                      $verification8

   it is possible to check byname parameters                           $verification9
     with several byname parameters                                    $verification10
     with mixed byname parameter and byvalue parameter                 $verification11
     with 2 parameter lists and byname parameters                      $verification12

   it is possible to check a function parameter
     with one argument                                                 $verification13
     with one argument and a matcher for the return value              $verification14
     with n arguments                                                  $verification15
     with n arguments and a matcher for the return value               $verification16
     as being anything                                                 $verification17
     with Nothing as the return type                                   $verification18
     with Any as the return type                                       $verification19

   it is possible to check a partial function parameter
     with n arguments                                                  $verification20
     with n arguments and a matcher for the return value               $verification21
     as being anything                                                 $verification22
     when the argument is not defined                                  $verification23

   it is possible to verify a function with repeated parameters        $verification24
   it is possible to specify a timeout for the call                    $verification25
   it doesn't match maps and functions as equal                        $verification26
   spies must not be checked for matchers when called for real         $verification27

STUBS
=====

 It is also possible to return a specific value from a mocked method
   then when the mocked method is called, the same values will be returned $stubs1
   different successive values can even be returned                        $stubs2
   a value can be returned when a parameter of the method matches
     a hamcrest matcher      $stubs3
     a specs2 matcher        $stubs4
     with a subtype matcher  $stubs5
     a Set                   $stubs6
     a List                  $stubs7

 It is also possible to throw an exception from a mocked method
   then when the mocked method is called, the exception will be thrown $stubs8
   different successive exceptions can even be thrown                  $stubs9

 A mock can be created and stubbed at the same time $stubs10

NUMBER OF CALLS
===============

 The number of calls to a mocked method can be checked
   if the mocked method has been called once                                   $calls1
   if the mocked method has been called twice                                  $calls2
   if the mocked method has been called exactly n times                        $calls3
   if the mocked method has been called atLeast n times                        $calls4
   if the mocked method has been called atMost n times                         $calls5
   if the mocked method has never been called                                  $calls6
   if the verification throws an exception, it will be reported as an Error    $calls7
   if the mocked method has not been called after some calls                   $calls8
   if the mocked method has not been called after some calls - ignoring stubs  $calls9

ORDER OF CALLS
==============

 The order of calls to a mocked method can be checked
   with 2 calls that were in order                                                  $order1
   with 2 calls that were in order - ignoring stubbed methods                       $order2
   with 2 calls that were not in order - on the same mock                           $order3
   with 2 calls that were not in order - on the same mock, with thrown expectations $order4
   with 2 calls that were not in order - on different mocks                         $order5
   with 3 calls that were not in order                                              $order6

ANSWERS & PARAMETERS CAPTURE
============================

 Answers can be created to control the returned a value $callbacks1
 Answers can use the method's parameters                $callbacks2

 A parameter can be captured in order to check its value             $callbacks1
 A parameter can be captured in order to check its successive values $callbacks2

 OTHER CONTEXTS
 ==============

The Mockito trait is reusable in other contexts
  in mutable specs      $contexts1
  with an in order call $contexts2

 MATCHERS
 ========

 Various mockito matchers can be used $mockitoMatchers1
 Matching with any                    $mockitoMatchers2

"""
  lazy val env = Env()

  /* CREATION */
  def creation1 = {
    val list = mock[java.util.List[String]]("list1")
    (there was one(list).add("one")).message must contain("list1.add(\"one\")")
  }

  def creation2 = {
    val list = mock[java.util.List[String]](DefaultAnswer(10))
    list.size must_== 10
  }

  def creation3 = {
    val list = mock[java.util.List[String] with Cloneable with Serializable](withSettings(DefaultAnswer(10)).name("list1"))
    (list.size must_== 10) and
    ((there was one(list).add("one")).message must contain("list1.add(\"one\")"))
  }

  def creation4 = {
    val list = mock[java.util.List[String]](DefaultAnswer((_: InvocationOnMock) => "hello"))
    list.get(0) must_== "hello"
  }

  def creation5 = {
    val list = mock[java.util.List[String]](withSettings.name("list1"))
    (there was one(list).add("one")).message must contain("list1.add(\"one\")")
  }

  /* VERIFICATION */

  def verification1 = {
    val list = mock[java.util.List[String]]
    list.add("one"); success
  }

  def verification2 = {
    val list = mock[java.util.List[String]]
    list.add("one")
    there was one(list).add("one")
  }

  def verification3 = {
    val list = mock[java.util.List[String]]
    (there was one(list).add("one")).message must startWith("The mock was not called as expected")
  }

  def verification4 = {
    val list = mock[java.util.List[String]]
    there were noCallsTo(list)
  }

  def verification5 = {
    val list = mock[java.util.List[String]]
    list.add(3, null: String)
    there was one(list).add(be_>(0), beNull[String])
  }

  def verification6 = {
    object list extends list; import list._

    byname.call(10)
    there was one(byname).call(10)
  }

  def verification7 = {
    object list extends list; import list._

    byname.add(1, 2)
    there was one(byname).add(1, 2)
  }

  def verification8 = {
    object list extends list; import list._

    byname.mult(1)(2)
    there was one(byname).mult(1)(2)
  }

  def verification9 = {
    object list extends list; import list._

    byname.call(10)
    there was one(byname).call(be_>(5))
  }

  def verification10 = {
    object list extends list; import list._

    byname.add(1, 2)
    there was one(byname).add(anyInt, anyInt)
  }

  def verification11 = {
    object list extends list; import list._

    byname.min(2, 1)
    there was one(byname).min(anyInt, anyInt)
  }

  def verification12 = {
    object list extends list; import list._

    byname.mult(1)(2)
    there was one(byname).mult(anyInt)(anyInt)
  }

  def verification13 = {
    object list extends list; import list._

    function1.call((_: Int).toString)
    there was one(function1).call(1 -> "1")
  }

  def verification14 = {
    object list extends list; import list._

    function1.call((_: Int).toString)
    (there was one(function1).call(1 -> startWith("1"))) and
    ((there was one(function1).call(1 -> startWith("2"))).message must contain("1 doesn't start with '2'"))
  }

  def verification15 = {
    object list extends list; import list._

    function2.call((i: Int, d: Double) => (i + d).toString)
    there was one(function2).call((1, 3.0) -> "4.0")
  }

  def verification16 = {
    object list extends list; import list._

    function2.call((i: Int, d: Double) => (i + d).toString)
    there was one(function2).call((1, 3.0) -> haveSize[String](3))
  }

  def verification17 = {
    object list extends list; import list._

    function2.call((i: Int, d: Double) => (i + d).toString)
    there was one(function2).call(*)
  }

  def verification18 = {
    object list extends list; import list._

    functionNothing.call((_: Int) => throw new Exception)
    there was one(functionNothing).call(*)
  }

  def verification19 = {
    object list extends list; import list._

    functionAny.call(() => throw new Exception)
    there was one(functionAny).call(any[() => Any])
  }

  def verification20 = {
    object list extends list; import list._

    partial.call { case (i: Int, d: Double) => (i + d).toString }
    there was one(partial).call((1, 3.0) -> "4.0")
  }

  def verification21 = {
    object list extends list; import list._

    partial.call { case (i: Int, d: Double) => (i + d).toString }
    there was one(partial).call((1, 3.0) -> haveSize[String](3))
  }

  def verification22 = {
    object list extends list; import list._

    partial.call { case (i: Int, d: Double) => (i + d).toString }
    there was one(partial).call(*)
  }

  def verification23 = {
    object list extends list; import list._

    partial.call { case (i: Int, d: Double) if i > 10 => (i + d).toString }
    (there was one(partial).call((1, 3.0) -> "4.0")).message must contain("a PartialFunction defined for (1,3.0)")
  }

  def verification24 = {
    object list extends list; import list._

    repeated.call(1, 2, 3)
    (there was one(repeated).call(1, 2, 3)) and
    ((there was one(repeated).call(1, 2)).message must contain("withRepeatedParams.call(1, 2)"))
  }

  def verification25 = {
    object list extends list; import list._

    scala.concurrent.Future { Thread.sleep(200); takesSometime.call(10) }
    ((takesSometime.call(10) wasCalled (once within 10.millis)).message must contain("Wanted but not invoked")) and
      (takesSometime.call(10) wasCalled (once within 300.millis))
  }

  def verification26 = {
    object list extends list; import list._

    functionInt.call((i: Int) => i + 2)
    (there was one(functionInt).call(Map(1 -> 2))).message must contain("Argument(s) are different")
  }

  def verification27 = {
    object list extends list

    val foo        = mock[FooComponent]
    val controller = spy(new TestController(foo))

    foo.getBar(1) returns 1
    // controller is a spy. Calling 'test' for real must not re-evaluate
    // the arguments, hence make a mock call, to register matchers
    controller.test(1)
    there were 1.times(foo).getBar(1)
  }

  /* STUBS */
  def stubs1 = {
    val list = mock[java.util.List[String]]
    list.add("one") mustReturn true
    list.add("one") must_== true
  }

  def stubs2 = {
    val list = mock[java.util.List[String]]
    list.add("one") returns true andThen false andThen true
    (list.add("one"), list.add("one"), list.add("one")) must_== ((true, false, true))
  }

  def stubs3 = {
    val list = mock[java.util.List[String]]
    list.contains(new IsNull[String]) mustReturn true
    list.contains(null) must_== true
  }

  def stubs4 = {
    val list = mock[java.util.List[String]]
    list.contains(beMatching(".*o")) returns true
    list.contains("o") must_== true
  }

  def stubs5 = {
    trait Vet { def treat(p: Pet) = true }
    trait Pet
    case class Dog() extends Pet
    case class Cat() extends Pet
    val vet = mock[Vet]
    vet.treat(Cat())
    def isDog: Matcher[Dog] = (_: Dog) => (true, "ok", "ko")

    (there was one(vet).treat(isDog)) must not(throwA[ClassCastException])
  }

  def stubs6 = {
    val list = mock[java.util.List[String]]
    list.contains(Set(1)) returns true
    list.contains(Set(1)) must_== true
    list.contains(Set(2)) must_== false
  }

  def stubs7 = {
    val list = mock[java.util.List[String]]
    list.contains(List(1)) returns true
    list.contains(List(1)) must_== true
    list.contains(List(2)) must_== false
  }

  def stubs8 = {
    val list = mock[java.util.List[String]]
    list.clear() throws new RuntimeException
    list.clear()
  } must throwA[RuntimeException]

  def stubs9 = {
    val list = mock[java.util.List[String]]
    list.clear() throws new RuntimeException andThenThrow new IllegalArgumentException
    tryo(list.clear())
    list.clear()
  } must throwAn[IllegalArgumentException]

  def stubs10 = {
    val list = mock[java.util.List[String]]
    list.contains("o") returns true
    list.contains("o") must beTrue
  }

  /* NUMBER OF CALLS */

  def makeCalls(list: java.util.List[String], list2: java.util.List[String]) = {
    list.add("one")
    1 to 2 foreach { _ =>
      list.add("two")
    }
    list2.add("one")
  }

  def calls1 = {
    val (list, list2) = (mock[java.util.List[String]], mock[java.util.List[String]])
    makeCalls(list, list2)
    there was one(list).add("one")
  }

  def calls2 = {
    val (list, list2) = (mock[java.util.List[String]], mock[java.util.List[String]])
    makeCalls(list, list2)
    there were two(list).add("two")
  }

  def calls3 = {
    val (list, list2) = (mock[java.util.List[String]], mock[java.util.List[String]])
    makeCalls(list, list2)
    there was atLeast(1)(list).add("two")
  }

  def calls4 = {
    val (list, list2) = (mock[java.util.List[String]], mock[java.util.List[String]])
    makeCalls(list, list2)
    there was exactly(2)(list).add("two")
  }

  def calls5 = {
    val (list, list2) = (mock[java.util.List[String]], mock[java.util.List[String]])
    makeCalls(list, list2)
    there were atMost(2)(list).add("two")
  }

  def calls6 = {
    val (list, list2) = (mock[java.util.List[String]], mock[java.util.List[String]])
    makeCalls(list, list2)
    there was no(list).add("four")
  }

  def calls7 = {
    val (list, list2) = (mock[java.util.List[String]], mock[java.util.List[String]])
    makeCalls(list, list2)

    val cause = new Exception("cause")
    val e     = new Exception("error", cause)
    (there was no(list).add(be_=== { throw e; "four" })) must throwAn[Exception]
  }

  def calls8 = {
    val (list, list2) = (mock[java.util.List[String]], mock[java.util.List[String]])
    makeCalls(list, list2)
    there was one(list).add("one")
    there were two(list).add("two")
    there were noMoreCallsTo(list)
  }

  def calls9 = {
    val list3 = mock[java.util.List[String]]
    val list4 = mock[java.util.List[String]]
    list3.contains("3") returns false
    list4.contains("4") returns false

    list3.add("one")
    list4.add("one"); list4.add("one")
    list3.contains("3")
    list4.contains("4")

    there was one(list3).add("one")
    there were two(list4).add("one")

    there were noMoreCallsTo(ignoreStubs(list3, list4))
  }

  /* ORDER */
  def order1 = {
    val list1 = mock[java.util.List[String]]
    val list2 = mock[java.util.List[String]]

    list1.get(0)
    list2.get(0)

    InOrder(list1, list2) { implicit order =>
      there was one(list1).get(0) andThen one(list2).get(0)
    }.message must_== "The mock was called as expected"
  }

  def order2 = {
    val list1 = mock[java.util.List[String]]
    val list2 = mock[java.util.List[String]]

    list1.get(1) returns "1"

    // there is an out of order call but to a stubbed method
    list1.get(1)
    list1.get(0)
    list2.get(0)

    InOrder(ignoreStubs(list1, list2): _*) { implicit order =>
      there was one(list1).get(0) andThen one(list2).get(0)
    }.message must_== "The mock was called as expected"
  }

  def order3 = {
    val list1 = mock[java.util.List[String]]

    list1.get(0)
    list1.get(1)

    InOrder(list1) { implicit order =>
      there was one(list1).get(1) andThen one(list1).get(0)
    }.message must startWith("The mock was not called as expected")
  }

  def order4 = {
    val list1 = mock[java.util.List[String]]

    list1.get(0)
    list1.get(1)

    var result: Result = success

    new ThrownExpectations {
      result = InOrder(list1) { implicit order =>
        there was one(list1).get(1) andThen one(list1).get(0)
      }
    }

    result.message must startWith("The mock was not called as expected")
  }

  def order5 = {
    val list1 = mock[java.util.List[String]]
    val list2 = mock[java.util.List[String]]

    list1.get(0)
    list2.get(0)

    InOrder(list1, list2) { implicit order =>
      there was one(list2).get(0) andThen one(list1).get(0)
    }.message must startWith("The mock was not called as expected")
  }

  def order6 = {
    val list1 = mock[java.util.List[String]]

    list1.get(0); list1.size; list1.get(0); list1.size

    InOrder(list1) { implicit order =>
      there was one(list1).get(0) andThen
        one(list1).size() andThen
        no(list1) .get(0) andThen
        one(list1).size()
    }.message must startWith("The mock was not called as expected")
  }

  /* CALLBACKS */

  def callbacks1 = {
    val list = mock[java.util.List[String]]("list")
    list.get(*) answers { i: Int =>
      "The parameter is " + i.toString
    }
    list.get(2) must_== "The parameter is 2"
  }

  def callbacks2 = {
    val list = mock[java.util.List[String]]("list")
    list.get(*) answers { i: Int =>
      (i + 1).toString
    }
    list.get(1) must_== "2"
    list.get(5) must_== "6"
  }

  def callbacks3 = {
    val list = mock[java.util.List[String]]("list")
    list.set(*, *) answers { (i: Int, s: String) =>
      s"The parameters are ($i,$s)"
    }
    list.set(1, "foo") must_== "The parameters are (1,foo)"
  }

  def callbacks4 = {
    val list = mock[java.util.List[String]]("list")
    list.get(1)
    val c = ArgCaptor[Int]
    there was one(list).get(c)
    c.value must_== 1
  }

  def callbacks5 = {
    val list = mock[java.util.List[String]]("list")
    list.get(1)
    list.get(2)
    val c = ArgCaptor[Int]
    there was two(list).get(c)
    c.values.toString === "[1, 2]"
  }

  /* OTHER CONTEXTS */
  def contexts1 = {
    val s = new org.specs2.mutable.Specification with Mockito {
      val list = mock[java.util.List[String]]
      "ex1" in {
        list.add("one")
        there was one(list).add("two")
        1 must_== 1 // to check if the previous expectation really fails
      }
    }
    DefaultExecutor.runSpec(s.is, env).filter(Fragment.isExample).traverse(_.executionResult.map(_.isSuccess)) must
    beOk((list: List[Boolean]) => list must contain(false))
  }

  def contexts2 = {
    val s = new org.specs2.mutable.Specification with Mockito {
      "ex1" in new org.specs2.specification.Scope {
        val (list1, list2) = (mock[java.util.List[String]], mock[java.util.List[String]])
        list1.add("two"); list2.add("one")
        InOrder(list1, list2) { implicit order =>
          there was one(list2).add("two") andThen one(list1).add("one")
        }
      }
    }
    DefaultExecutor.runSpec(s.is, env).filter(Fragment.isExample).traverse(_.executionResult.map(_.isSuccess)) must
    beOk((list: List[Boolean]) => list must contain(false))
  }

  /* MOCKITO MATCHERS */
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

  def mockitoMatchers1 = {
    val m = mock[M]
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

    (there was one(m).javaList(*)) andThen
    one(m).javaSet(*) andThen
    one(m).javaCollection(*) andThen
    one(m).javaMap(*) andThen
    one(m).List(*) andThen
    one(m).Set(*) andThen
    one(m).Traversable(*) andThen
    one(m).Map(*) andThen
    one(m).varargs(*, *) andThen
    one(m).array(*)
  }

  def mockitoMatchers2 = {
    val m         = mock[M]
    val returnsOk = m.method(*, *) returns 1

    m.method(new B, b = true)
    there was one(m).method(any[B], any[Boolean])
    there was one(m).method(*, *)
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
