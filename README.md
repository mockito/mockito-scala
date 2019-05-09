# Mockito Scala

<a href="http://site.mockito.org">
<img src="https://raw.githubusercontent.com/mockito/mockito/master/src/javadoc/org/mockito/logo.png"
     srcset="https://raw.githubusercontent.com/mockito/mockito/master/src/javadoc/org/mockito/logo@2x.png 2x"
     alt="Mockito" />
</a>

The most popular mocking framework for Java, now in Scala!!!

[![Build Status](https://travis-ci.org/mockito/mockito-scala.svg?branch=release/1.x)](https://travis-ci.org/mockito/mockito-scala)

[![Download](https://api.bintray.com/packages/mockito/maven/mockito-scala/images/download.svg) ](https://bintray.com/mockito/maven/mockito-scala/_latestVersion)
[![Maven Central](https://img.shields.io/maven-central/v/org.mockito/mockito-scala_2.12.svg)](https://search.maven.org/search?q=mockito-scala)

[![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/mockito-scala/)
## Why separate project?

The library has independent developers, release cycle and versioning from core mockito library (<https://github.com/mockito/mockito>). This is intentional because core Mockito developers don't use Scala and cannot confidently review PRs, and set the vision for the Scala library.

## Dependency

*   Artifact identifier: "org.mockito:mockito-scala_[scala-version]:[version]"
*   Artifact identifier: "org.mockito:mockito-scala-scalatest_[scala-version]:[version]"
*   Artifact identifier: "org.mockito:mockito-scala-specs2_[scala-version]:[version]"
*   Artifact identifier: "org.mockito:mockito-scala-cats_[scala-version]:[version]"
*   Latest version - see [release notes](/docs/release-notes.md)
*   Repositories: [Maven Central](https://search.maven.org/search?q=mockito-scala) or [JFrog's Bintray](https://bintray.com/mockito/maven/mockito-scala)

### Please ensure `mockito-scala` is your only mockito dependency

### Note: For more examples and use cases than the ones shown below, please refer to the library's [tests](/core/src/test)

## Notes for v1.4.0
As Specs2 support was added, now the library has been split in 3 different artifacts
- **mockito-scala** being the core
- **mockito-scala-scalatest** having specific classes that provide extra support for Scalatest
- **mockito-scala-specs2** having specific classes that provide extra support for Specs2

From now on, when using the idiomatic syntax, you'll get any non-matcher parameter automatically wrapped in an `eqTo`, 
this means you shouldn't need to use it manually anymore. This is to provide a consistent behaviour when a custom `scalactic.Equality` has been defined for
a type.

The traits that provide the specifics for each test framework are 
- Scalatest: 
    - `org.mockito.scalatest.MockitoSugar` and `org.mockito.scalatest.IdiomaticMockito` for standard specs
    - `org.mockito.scalatest.AsyncMockitoSugar`, `org.mockito.scalatest.AsyncIdiomaticMockito` for async specs
- Specs2: `org.mockito.specs2.Mockito`

This version also includes a lot of under-the-hood fixes and improvements that provide an even better experience.

## Note for v1.2.0
As now the varargs support works consistently across the whole lib, no no special syntax is needed, so if you were using `eqTo` with varargs, i.e. 
```scala
verify(myObj).myMethod(eqTo("arg1", "arg2"))
```
You must change it now to
```scala
verify(myObj).myMethod(eqTo("arg1"), eqTo("arg2"))
```

## Migration Notes for version 1.0.0
* `DefaultAnswer` was moved from `org.mockito.DefaultAnswer` to `org.mockito.stubbing.DefaultAnswer`
* The recommended way to use the pre-defined `DefaultAnswer`s is via the object `org.mockito.DefaultAnswers`
* `*` matcher is now defined in `org.mockito.ArgumentMatchersSugar`, mixin (or use the companion object) this trait whenever you wanna use it
* `argumentCaptor[String]` was removed, replace by `ArgCaptor[T]` (`Captor[T]` was renamed to `ArgCaptor[T]` to add clarity), `ValCaptor[T]` was deprecated, (see [Improved ArgumentCaptor](#improved-argumentcaptor))
* The usage of `org.mockito.Answer[T]` was removed from the API in favour of [Function Answers](#function-answers)
* If you were using something like `doAnswer(_ => <something>).when ...` to lazily compute a return value when the method is actually called you should now write it like `doAnswer(<something>).when ...`, no need of passing a function as that argument is by-name
* If you have chained return values like `when(myMock.foo) thenReturn "a" thenReturn "b" etc...` the syntax has changed a bit to `when(myMock.foo) thenReturn "a" andThen "b" etc...`
* Idiomatic syntax has some changes to remove postFix operations and also allow support for mixing values and argument matchers [Mix-and-Match](#mix-and-match)
```scala
aMock.bar shouldCallRealMethod                              => aMock.bar shouldCall realMethod

aMock wasCalled on bar                                      => aMock.bar was called
aMock wasCalled onlyOn bar                                  => aMock.bar wasCalled onlyHere
aMock was never called on bar                               => aMock.bar wasNever called
aMock wasCalled twiceOn bar                                 => aMock.bar wasCalled twice
aMock wasCalled sixTimesOn bar                              => aMock.bar wasCalled sixTimes
aMock was never called                                      => aMock.bar wasNever called
aMock was never called again                                => aMock.bar wasNever calledAgain

"mocked!" willBe returned by aMock bar                      => "mocked!" willBe returned by aMock.bar
"mocked!" willBe answered by aMock bar                      => "mocked!" willBe answered by aMock.bar
((i: Int) => i * 10) willBe answered by aMock bar *         => ((i: Int) => i * 10) willBe answered by aMock.bar(*)
theRealMethod willBe called by aMock bar                    => theRealMethod willBe called by aMock.bar
new IllegalArgumentException willBe thrown by aMock bar     => new IllegalArgumentException willBe thrown by aMock.bar
```
* eqToVal matcher syntax was improved to look more natural [Value Class Matchers](#value-class-matchers) 
NOTE: `eqToVal` has been deprecated in v 1.0.2 as `eqTo` is now aware of value classes
```scala
verify(myObj).myMethod(eqToVal[MyValueClass](456))    => verify(myObj).myMethod(eqToVal(MyValueClass(456)))
myObj.myMethod(eqToVal[MyValueClass](456)) was called => myObj.myMethod(eqToVal(MyValueClass(456))) was called
``` 

## Getting started

## `org.mockito.MockitoSugar`

For a more detailed explanation read [this](https://medium.com/@bbonanno_83496/introduction-to-mockito-scala-ede30769cbda)

This trait wraps the API available on `org.mockito.Mockito` from the Java version, but it provides a more Scala-like syntax, mainly
*   Fixes the compiler errors that sometimes occurred when using overloaded methods that use varargs like doReturn
*   Eliminates the need to use `classOf[T]`
*   Eliminates parenthesis when possible to make the test code more readable
*   Adds `spyLambda[T]` to allow spying lambdas (they don't work with the standard spy as they are created as final classes by the compiler)
*   Supports mocking inline mixins like `mock[MyClass with MyTrait]`
*   Full support for by-name arguments (the full support was added in **1.4.0**, before it was partial).
*   Adds support for working with default arguments

The companion object also extends the trait to allow the usage of the API without mixing-in the trait in case that's desired

## `org.mockito.ArgumentMatchersSugar`

For a more detailed explanation read [this](https://medium.com/@bbonanno_83496/introduction-to-mockito-scala-part-2-ba1a79cc4c53)

This trait exposes all the existent `org.mockito.ArgumentMatchers` but again it gives them a more Scala-like syntax, mainly
*   `eq` was renamed to `eqTo` to avoid clashing with the Scala `eq` operator for identity equality, `eq` also supports value classes out of the box and relies on `org.scalactic.Equality[T]` (see [Scalactic integration](#scalactic-integration)) 
*   `any[T]` works even when the type can't be inferred, removing the need of using the likes of `anyString`, `anyInt`, etc (see [Notes](#dead-code-warning))
*   `any[T]` also supports value classes (in this case you MUST provide the type parameter)
*   `isNull` and `isNotNull` are deprecated as using nulls in Scala is clear code smell
*   Adds support for value classes via `anyVal[T]` and `eqToVal[T]()` **NOTE: both had been deprecated (use `any[T]` or `eqTo[T]` instead)**
*   Adds `function0` to easily match for a function that returns a given value
*   Adds `argMatching` that takes a partial function to match, i.e. `argMatching({ case Baz(_, "pepe") => })`

Again, the companion object also extends the trait to allow the usage of the API without mixing-in the trait in case that's desired

### Value Class Matchers
`eqTo` and `any[T]` support value classes since v1.0.2, so no special syntax is needed for them (but you MUST provide the type param for `any[T]` otherwise you'll get a NPE)

## Improved ArgumentCaptor

For a more detailed explanation read [this](https://medium.com/@bbonanno_83496/introduction-to-mockito-scala-part-3-383c3b2ed55f)

A new set of classes were added to make it easier, cleaner and more elegant to work with ArgumentCaptors, they also add 
support to capture value classes without any annoying syntax

There is a new `object org.mockito.captor.ArgCaptor[T]` that exposes a nicer API

Before:
```scala
val aMock  = mock[Foo]
val captor = argumentCaptor[String]

aMock.stringArgument("it worked!")

verify(aMock).stringArgument(captor.capture())

captor.getValue shouldBe "it worked!"
```
Now:
```scala
val aMock  = mock[Foo]
val captor = ArgCaptor[String]

aMock.stringArgument("it worked!")

verify(aMock).stringArgument(captor)

captor hasCaptured "it worked!"
```

As you can see there is no need to call `capture()` nor `getValue` anymore (although they're still there if you need them as `capture` and `value` respectively)

The only scenario where you still have to call `capture` by hand is where the argument you want to capture is `Any` on the method signature, in that case the `implicit` conversion that automatically does the capture
```scala
implicit def asCapture[T](c: Captor[T]): T = c.capture
```
is not called as the compiler finds no need to convert `Captor[Any]` into `Any`, as it is already an instance of `Any`, given that `Any` is the parent of every type in Scala. Because of that, the type does not need any transformation to be passed in.

There is another constructor `ValCaptor[T]` that should be used to capture value classes
**NOTE: Since version 1.0.2 `ValCaptor[T]` has been deprecated as `ArgCaptor[T]` now support both, standard and value classes**

Both `ArgCaptor[T]` and `ValCaptor[T]` return an instance of `Captor[T]` so the API is the same for both

## `org.mockito.MockitoScalaSession`

### Basic usage

This is a wrapper around `org.mockito.MockitoSession`, it's main purpose (on top of having a Scala API) 
is to improve the search of mis-used mocks and unexpected invocations to reduce debugging effort when something doesn't work

To use it just wrap your code with it, e.g.
```scala
MockitoScalaSession().run {
    val foo = mock[Foo]
    when(foo.bar("pepe")) thenReturn "mocked"
    foo.bar("pepe") shouldBe "mocked"
}
``` 
That's it! that block of code will execute within a session which will take care of checking the use of the framework and,
if the test fails, it will try to find out if the failure could be related to a mock being used incorrectly

### Leniency

If for some reason we want that a mock created within the scope of a session does not report failures for some or all methods we can specify leniency for it.

For the whole mock or spy to be ignored by the session, so basically a mock/spy that behaves as if the session didn't exist at all, we can make it lenient, e.g.
```scala
val aMock = mock[Foo](withSettings.lenient())
val aSpy = spy(new Bar, lenient = true)
```

Now, if we just want to make one or more methods to be ignored by the session checks, we can make the method call lenient, this works as any other stubbing, so what it matters what matchers you define
```scala
aMock.myMethod(*) isLenient()
//or
when(aMock.myMethod(*)).isLenient()
``` 

## Strict Mode

For a more detailed explanation read [this](https://medium.com/@bbonanno_83496/introduction-to-mockito-scala-part-3-383c3b2ed55f)

When using Scalatest and `org.mockito.scalatest.Mockito` this is the default mode, you can override the strictness to be lenient by doing `val strictness: Strictness = Strictness.Lenient`
The implication under the hood is that every test will run inside a `MockitoScalaSession`, so **all** of them will run in **Strict Stub** mode.

`org.mockito.scalatest.Mockito` also includes `org.mockito.IdiomaticMockito` and `org.mockito.ArgumentMatchersSugar` so you have pretty much all 
the mockito-scala API available in one go, i.e.

```scala
class MyTest extends WordSpec with Mockito
```

IMPORTANT: A session is defined on a per-test basis, and only the mocks created within the scope of the session are 
handled by it, so if you have class level fields with mocks, i.e. mocks that are not created within the test, they will
be ignored by the session. If you use the same mocks in all or most of the tests and want to avoid the boilerplate while
still usfing the advantages of strict stubbing then declare those mocks in a setup trait.

```scala
class MySpec extends WordSpec with Mockito {
   trait Setup {
      val myMock = mock[Sth] 
      myMock.someMethod shouldReturn "something" /*stub common to **all** tests -notice that if it's not used by all of them then the session will find it as an unused stubbing on those-*/
   }

   "some feature" should {
       "test whatever i want" in new Setup {
            myMock.someOtherMethod(*) shouldReturn None /*stub specific only to this test*/
             ...test
       }

      "test something else" in new Setup {
             myMock.someOtherMethod("expected value") shouldReturn Some("result")  /*stub specific only to this test*/
             ...test
       }
   }
}
```

This will give you a fresh new instance of `myMock` for each test but at the same time you only declare the creation/common stubbing once.


## `org.mockito.integrations.scalatest.ResetMocksAfterEachTest`

Inspired by [this](https://stackoverflow.com/questions/51387234/is-there-a-per-test-non-specific-mock-reset-pattern-using-scalaplayspecmockito) StackOverflow question,
mockito-scala provides this trait that helps to automatically reset any existent mock after each test is run
The trait has to be mixed **after** `org.mockito.MockitoSugar` in order to work, otherwise your test will not compile
The code shown in the StackOverflow question would look like this if using this mechanism

NOTE: MockitoFixture and ResetMocksAfterEachTest are mutually exclusive, so don't expect them to work together

```scala
class MyTest extends PlaySpec with MockitoSugar with ResetMocksAfterEachTest

private val foo = mock[Foo]

override def fakeApplication(): Application = new GuiceApplicationBuilder().overrides(bind[Foo].toInstance(foo)).build
```

The main advantage being we don't have to remember to reset each one of the mocks...

If for some reason we want to have a mock that is not reset automatically while using this trait, then it should be 
created via the companion object of `org.mockito.MockitoSugar` so is not tracked by this mechanism

## Idiomatic Mockito

By adding the trait `org.mockito.IdiomaticMockito` you get access to some improved methods in the API

This API is heavily inspired on Scalatest's Matchers, so if have used them, you'll find it very familiar

Here we can see the old syntax on the left and the new one on the right

```scala
trait Foo {
  def bar: String
  def bar(v: Int): Int
}
  
val aMock = mock[Foo]  
  
when(aMock.bar) thenReturn "mocked!"                            <=> aMock.bar shouldReturn "mocked!"
when(aMock.bar) thenReturn "mocked!" thenReturn "mocked again!" <=> aMock.bar shouldReturn "mocked!" andThen "mocked again!"
when(aMock.bar) thenCallRealMethod()                            <=> aMock.bar shouldCall realMethod
when(aMock.bar).thenThrow[IllegalArgumentException]             <=> aMock.bar.shouldThrow[IllegalArgumentException]
when(aMock.bar) thenThrow new IllegalArgumentException          <=> aMock.bar shouldThrow new IllegalArgumentException
when(aMock.bar) thenAnswer(_ => "mocked!")                      <=> aMock.bar shouldAnswer "mocked!"
when(aMock.bar(any)) thenAnswer(_.getArgument[Int](0) * 10)     <=> aMock.bar(*) shouldAnswer ((i: Int) => i * 10)

doReturn("mocked!").when(aMock).bar                             <=> "mocked!" willBe returned by aMock.bar
doAnswer(_ => "mocked!").when(aMock).bar                        <=> "mocked!" willBe answered by aMock.bar
doAnswer(_.getArgument[Int](0) * 10).when(aMock).bar(any)       <=> ((i: Int) => i * 10) willBe answered by aMock.bar(*)
doCallRealMethod.when(aMock).bar                                <=> theRealMethod willBe called by aMock.bar
doThrow(new IllegalArgumentException).when(aMock).bar           <=> new IllegalArgumentException willBe thrown by aMock.bar
  
verifyZeroInteractions(aMock)                                   <=> aMock wasNever called
verify(aMock).bar                                               <=> aMock.bar was called
verify(aMock).bar(any)                                          <=> aMock.bar(*) was called

verify(aMock, only).bar                                         <=> aMock.bar wasCalled onlyHere
verify(aMock, never).bar                                        <=> aMock.bar wasNever called

verify(aMock, times(2)).bar                                     <=> aMock.bar wasCalled twice
verify(aMock, times(2)).bar                                     <=> aMock.bar wasCalled 2.times

verify(aMock, times(6)).bar                                     <=> aMock.bar wasCalled sixTimes
verify(aMock, times(6)).bar                                     <=> aMock.bar wasCalled 6.times

verify(aMock, atLeast(6)).bar                                   <=> aMock.bar wasCalled atLeastSixTimes
verify(aMock, atLeast(6)).bar                                   <=> aMock.bar wasCalled atLeast(sixTimes)
verify(aMock, atLeast(6)).bar                                   <=> aMock.bar wasCalled atLeast(6.times)

verify(aMock, atMost(6)).bar                                    <=> aMock.bar wasCalled atMostSixTimes
verify(aMock, atMost(6)).bar                                    <=> aMock.bar wasCalled atMost(sixTimes)
verify(aMock, atMost(6)).bar                                    <=> aMock.bar wasCalled atMost(6.times)

verify(aMock, timeout(2000).atLeast(6)).bar                     <=> aMock.bar wasCalled (atLeastSixTimes within 2.seconds)

verifyNoMoreInteractions(aMock)                                 <=> aMock wasNever calledAgain

val order = inOrder(mock1, mock2)                               <=> InOrder(mock1, mock2) { implicit order =>
order.verify(mock2).someMethod()                                <=>   mock2.someMethod() was called
order.verify(mock1).anotherMethod()                             <=>   mock1.anotherMethod() was called
                                                                <=> }
```

As you can see the new syntax reads a bit more natural, also notice you can use `*` instead of `any[T]`

Check the [tests](/scalatest/src/test/scala/user/org/mockito/IdiomaticMockitoTest.scala) for more examples

NOTE: When using the willBe syntax for stubbing, you can only stub one value to be returned, this is due to a limitation of the 
type inference. If for some reason you have to do that (ideally all functions should be referentially transparent, so you wouldn't have to), you can 
use the traditional syntax via the MockitoSugar companion object `MockitoSugar.doReturn("meh").when(myMock).foo` or you
can use an answer that can decide what to return given whatever condition you need to simulate 
`{ (args) => if(<condition>) something else somethingElse } willBe answered by myMock.foo`

## Default Answers
We defined a new type `org.mockito.stubbing.DefaultAnswer` which is used to configure the default behaviour of a mock when a non-stubbed invocation
is made on it.

The object `org.mockito.DefaultAnswers` contains each one of the provided ones

All the mocks created will use `ReturnsSmartNulls` by default, this is different to the Java version, which returns null for any non-primitive or non-final class.

A "Smart Null", is nothing else than a mock of the type returned by the called method.
The main advantage of doing that is that if the code tries to call any method on this mock, instead of failing with a NPE the mock will
throw a different exception with a hint of the non-stubbed method that was called (including its params),
this should make much easier the task of finding and fixing non-stubbed calls

Most of the Answers defined in `org.mockito.Answers` have it's counterpart in `org.mockito.DefaultAnswers`, and on top of that
we also provide `ReturnsEmptyValues` which will try its best to return an empty object for well known types, 
i.e. `Nil` for `List`, `None` for `Option` etc.
This DefaultAnswer is not part of the default behaviour as we think a SmartNull is better, to explain why, let's imagine we
have the following code.

```scala
class UserRepository {
  def find(id: Int): Option[User]
}
class UserController(userRepository: UserRepository) {
  def get(userId: Int): Option[Json] = userRepository.find(userId).map(_.toJson)
}

class UserControllerTest extends WordSpec with IdiomaticMockito {

  "get" should {
     "return the expected json" in {
        val repo = mock[UserRepository]
        val testObj = new UserController(repo)

        testObj.get(123) shouldBe Some(Json(....)) //overly simplified for clarity
      }
    }
}
```

Now, in that example that test could fail in 3 different ways

1) With the standard implementation of Mockito, the mock would return null and we would get a NullPointerException, which we all agree it's far from ideal, as it's hard to know where did it happen in non trivial code
2) With the default/empty values, we would get a `None`, so the final result would be `None` and we will get an assertion error as `None` is not `Some(Json(....))`, but I'm not sure how much improvement over the NPE this would be, because in a non-trivial method we may have many dependencies returning `Option` and it could be hard to track down which one is returning `None` and why
3) With a smart-null, we would return a `mock[Option]` and as soon as our code calls to `.map()` that mock would fail with an exception telling you what non-stubbed method was called and on which mock (in the example would say something you called the `find` method on some `mock of type UserRepository`) 

And that's why we use option 3 as default

Of course you can override the default behaviour, for this you have 2 options

1) If you wanna do it just for a particular mock, you can, at creation time do `mock[MyType](MyDefaultAnswer)`
2) If you wanna do it for all the mocks in a test, you can define an `implicit`, i.e. `implicit val defaultAnswer: DefaultAnswer = MyDefaultAnswer`

DefaultAnswers are also composable, so for example if you wanted empty values first and then smart nulls you could do `implicit val defaultAnswer: DefaultAnswer = ReturnsEmptyValues orElse ReturnsSmartNulls`

## Function Answers
`org.mockito.Answer[T]` can be a bit boilerplate-ish, mostly if you're still in Scala 2.11 (in 2.12 with SAM is much nicer),
to simplify the usage for both versions is that we replaced it by standard scala functions, so instead of 
```scala
when(myMock.foo("bar", 42)) thenAnswer new Answer[String] {
  override def answer(invocation: InvocationOnMock): String = i.getArgument[String](0) + i.getArgument[Int](1)
}
```
We can now write: (this may be nothing new for users of 2.12, but at least now the API is consistent for both 2.11 and 2.12)
```scala
when(myMock.foo("bar", 42)) thenAnswer ((i: InvocationOnMock) => i.getArgument[String](0) + i.getArgument[Int](1))
```

I guess we all agree that's much better, but, it gets even better, we can now pass standard functions that work over the arguments, we only need to take care to pass the right types, so the previous example would become
```scala
when(myMock.foo("bar", 42)) thenAnswer ((v1: String, v2: Int) => v1 + v2)
```


## Mix and match

### Mixing normal values with argument matchers

Since mockito 1.0.0, when you use the idiomatic syntax, you are not forced anymore to use argument matchers for all your parameters as soon as you use one, 
so stuff like this is now valid (not a comprehensive list, just a bunch of examples)
```scala
trait Foo {
  def bar(v: Int, v2: Int, v3: Int = 42): Int
}
  
val aMock = mock[Foo]  
  
aMock.bar(1,2) shouldReturn "mocked!"
aMock.bar(1,*) shouldReturn "mocked!"
aMock.bar(*,*) shouldReturn "mocked!"
aMock.bar(*,*, 3) shouldReturn "mocked!"

"mocked!" willBe returned by aMock.bar(1,2)
"mocked!" willBe returned by aMock.bar(1,*)
"mocked!" willBe returned by aMock.bar(*,*)
"mocked!" willBe returned by aMock.bar(*,*, 3)

aMock.bar(1,2) was called
aMock.bar(1,*) was called
aMock.bar(*,*) was called
aMock.bar(*,*, 3) was called
```
So far there is one caveat, if you have a curried function that has default arguments on the second (or any following) argument list, the macro that achieves this will fail, 
this is related to how the default method is created by the compiler.
I'll write a more detailed explanation at some point, but there are more than one reason why this is probably never going to work
The workaround is quite easy though, just provide a value (or a matcher) for that argument and you are good to go.


## Numeric matchers

A new set of matchers to deal with number comparison were introduced (see [Scalactic tolerance](#tolerance) for aproximation),
the syntax is slightly different to make them more readable, so now we can write stuff like (notice the 'n')
```scala
aMock.method(5)

aMock.method(n > 4.99) was called
aMock.method(n >= 5) was called
aMock.method(n < 5.1) was called
aMock.method(n <= 5) was called
```  

## Varargs

Most matchers that makes sense to, work with varargs out of the box, the only thing to notice is that if you are passing more than one value 
and want to use `eqTo` then you should pass all of them to the same instance of `eqTo` e.g.

```scala
trait FooWithVarArgAndSecondParameterList {
  def bar(bells: String*)(cheese: String): String
}

foo.bar(eqTo("cow", "blue"))(*) was called //RIGHT
foo.bar(eqTo("cow"), eqTo("blue"))(*) was called //WRONG - it will complain it was expecting 2 matchers but got 3
```

## Scalactic integration

### Equality

Since version 1.0.0 the `eqTo` matcher uses the `org.scalactic.Equality[T]` typeclass, this doesn't change anything on the API
and existent code will not be affected, but it allows you to override the standard equality of any class by just providing an
implicit `Equality` in scope, e.g.
```scala
implicit val fooEquality: Equality[Foo] = new Equality[Foo] { 
  override def areEqual(a: Foo, b: Any): Boolean = /*Do the comparison as you like*/ 
}

aMock.method(eqTo(/*some foo instance/*))
```


### Tolerance

You can use Scalactic's `Spread[T]` to deal with the precision errors in floating points, so you can now  write stuff like
```scala
aMock.method(4.999)

aMock.method(n =~ 5.0 +- 0.001) was called
```

### Prettifier

An instance of `org.scalactic.Prettifier` is implicitly pulled by the `EqTo` matcher to provide a nicer (and customisable) printing of 
your types when an verification fails. `EqTo` is also used internally by `Mockito` to print the arguments of every invocation, so you'll 
get a consisten printing for both the expectation and the actual call.

If you want to customise the print of any type you just need to declare your `Prettifier` in the implicit scope like

```scala
  implicit val prettifier: Prettifier = new Prettifier {
    override def apply(o: Any): String = o match {
      case Baz2(_, s) => s"PrettifiedBaz($s)"
      case other      => Prettifier.default(other)
    }
  }
```

## Cats integration
By adding the module `mockito-scala-cats` 2 new traits are available, `IdiomaticMockitoCats` and `MockitoCats` which are meant to be mixed-in in
tests that use `IdiomaticMockito` and `MockitoSugar` respectively.
Please look at the [tests](/cats/src/test) for more detailed examples

### MockitoCats
This traits adds `whenF()` which allows stubbing methods that return an Applicative (or an ApplicativeError) to be stubbed by just providing 
the content of said applicative (or the error).
So for 
```scala
trait Foo {
    def returnsOption[T](v: T): Option[T]
    def returnsMT[M[_], T](v: T): M[T]
}
// We can now write 
val aMock = mock[Foo]
whenF(aMock.returnsOption(*)) thenReturn "mocked!"
whenF(aMock.returnsMT[Future, String](*)) thenReturn "mocked!"
// Rather than
when(aMock.returnsOption(*)) thenReturn Some("mocked!")
when(aMock.returnsMT[Future, String](*)) thenReturn Future.successful("mocked!")

//We could also do stubbings in a single line if that's all we need from the mock
val inlineMock: Foo = whenF(mock[Foo].returnsOption(*)) thenReturn "mocked!"

// For errors we can do
type ErrorOr[A] = Either[Error, A]
val failingMock: Foo = whenF(mock[Foo].returnsMT[ErrorOr, MyClass](*)) thenFailWith Error("error")
//Rather than
val failingMock: Foo = when(mock[Foo].returnsMT[ErrorOr, MyClass](*)) thenReturn Left(Error("error"))
```

The trait also provides and implicit conversion from `cats.Eq` to `scalactic.Equality` so if you have an implicit `cats.Eq` instance in scope,
it will be automatically used by the `eqTo` matcher.

### IdiomaticMockitoCats

Similar to `MockitoCats` but for the idiomatic syntax (including the conversion from `cats.Eq` to `scalactic.Equality`), so the code would look like

```scala
trait Foo {
    def returnsOption[T](v: T): Option[T]
    def returnsMT[M[_], T](v: T): M[T]
}
// We can now write 
val aMock = mock[Foo]
aMock.returnsOption(*) shouldReturnF "mocked!"
aMock.returnsMT[Future, String](*) shouldReturnF "mocked!"
// Rather than
aMock.returnsOption(*) shouldReturn Some("mocked!")
aMock.returnsMT[Future, String](*) shouldReturn Future.successful("mocked!")

//We could also do stubbings in a single line if that's all we need from the mock
val inlineMock: Foo = mock[Foo].returnsOption(*) shouldReturnF "mocked!"

// For errors we can do
type ErrorOr[A] = Either[Error, A]
val failingMock: Foo = mock[Foo].returnsMT[ErrorOr, MyClass](*) shouldFailWith Error("error")
//Rather than
val failingMock: Foo = mock[Foo].returnsMT[ErrorOr, MyClass](*) shouldReturn Left(Error("error"))
```

## Notes

### Dead code warning
if you have enabled the compiler flag `-Ywarn-dead-code`, you will see the warning _dead code following this construct_ 
when using the `any` or `*` matchers , this is because in some cases the compiler can not infer the return type of those 
matchers and it will default to `Nothing`, and this compiler warning is shown every time `Nothing` is found on our code.
This will **NOT** affect the behaviour of Mockito nor your test in any way, so it can be ignored, but in case you 
want to get rid of it then you have 2 options:

1) If you are not too fuss about dead code warnings in test code, you can add `scalacOptions in Test -= "-Ywarn-dead-code"` to 
your build.sbt and that warning will be ignored for your tests **only**
2) If you wanna keep the warning enabled for potentially real dead code statements, but get rid of the warnings related to the 
matchers usage then you have to explicitly provide the type for the matcher, thus `any` would become `any[MyType]` and
`*` would become `*[MyType]` (you can also use `anyShort`, `anyInt`, etc for the primitive types)
 
### Scala 2.11
Please note that in Scala 2.11 the following features are not supported

* Default arguments on methods defined in traits (they will behave as before, getting `null` or a default value if they 
are of a primitive type)
* Any kind of `ArgumentMatcher[T]` for methods with by-name parameters (they'll throw an exception if used with `ArgumentMatcher[T]`)

## Authors

* **Bruno Bonanno** - *Initial work* - [bbonanno](https://github.com/bbonanno)


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
