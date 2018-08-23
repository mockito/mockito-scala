# Mockito Scala

<a href="http://site.mockito.org">
<img src="https://raw.githubusercontent.com/mockito/mockito/master/src/javadoc/org/mockito/logo.png"
     srcset="https://raw.githubusercontent.com/mockito/mockito/master/src/javadoc/org/mockito/logo@2x.png 2x"
     alt="Mockito" />
</a>

The most popular mocking framework for Java, now in Scala!!!

[![Build Status](https://travis-ci.org/mockito/mockito-scala.svg?branch=master)](https://travis-ci.org/mockito/mockito-scala)

[![Download](https://api.bintray.com/packages/mockito/maven/mockito-scala/images/download.svg) ](https://bintray.com/mockito/maven/mockito-scala/_latestVersion)
[![Maven Central](https://img.shields.io/maven-central/v/org.mockito/mockito-scala_2.12.svg)](https://search.maven.org/search?q=mockito-scala)
## Why separate project?

The library has independent developers, release cycle and versioning from core mockito library (https://github.com/mockito/mockito). This is intentional because core Mockito developers don't use Scala and cannot confidently review PRs, and set the vision for the Scala library.

## Dependency

*   Artifact identifier: "org.mockito:mockito-scala_2.12:VERSION"
*   Latest version - see [release notes](/docs/release-notes.md)
*   Repositories: [Maven Central](https://search.maven.org/search?q=mockito-scala) or [JFrog's Bintray](https://bintray.com/mockito/maven/mockito-scala)


## Note: For more examples and use cases than the ones shown below, please refer to the library's [tests](https://github.com/mockito/mockito-scala/blob/master/core/src/test)

## Getting started

## `org.mockito.MockitoSugar`

For a more detailed explanation read [this](https://medium.com/@bbonanno_83496/introduction-to-mockito-scala-ede30769cbda) 

This trait wraps the API available on `org.mockito.Mockito` from the Java version, but it provides a more Scala-like syntax, mainly
*   Fixes the compiler errors that sometimes occurred when using overloaded methods that use varargs like doReturn
*   Eliminates the need to use `classOf[T]`
*   Eliminates parenthesis when possible to make the test code more readable
*   Adds `spyLambda[T]` to allow spying lambdas (they don't work with the standard spy as they are created as final classes by the compiler)
*   Supports mocking inline mixins like `mock[MyClass with MyTrait]`
*   Supports by-name arguments in some scenarios [EXPERIMENTAL](#experimental-features)
    *   Full support when all arguments in a method are by-name
    *   Full support when only some arguments in a method are by-name, but we use the `any[T]` matcher for every argument
    *   Full support when only some arguments in a method are by-name, but we use NO matchers at all
    *   Partial support when only some arguments in a method are by-name and we use specific matchers, 
    in this scenario the stubbing will only work if the by-name arguments are the last ones in the method signature
*   Adds support for working with default arguments

The companion object also extends the trait to allow the usage of the API without mixing-in the trait in case that's desired

## `org.mockito.ArgumentMatchersSugar`

For a more detailed explanation read [this](https://medium.com/@bbonanno_83496/introduction-to-mockito-scala-part-2-ba1a79cc4c53) 

This trait exposes all the existent `org.mockito.ArgumentMatchers` but again it gives them a more Scala-like syntax, mainly
*   `eq` was renamed to `eqTo` to avoid clashing with the Scala `eq` operator for identity equality
*   `any` resolves to the correct type most of the times, removing the need of using the likes of `anyString`, `anyInt`, etc
*   `isNull` and `isNotNull` are deprecated as using nulls in Scala is clear code smell
*   Adds support for value classes via `anyVal[T]` and `eqToVal[T]()`
*   Adds `function0` to easily match for a function that returns a given value

Again, the companion object also extends the trait to allow the usage of the API without mixing-in the trait in case that's desired

### Value Class Matchers

The matchers for the value classes always require the type to be explicit, apart from that, they should be used as any other matcher, e.g.
   ```scala
when(myObj.myMethod(anyVal[MyValueClass]) thenReturn "something"

myObj.myMethod(MyValueClass(456)) shouldBe "something"

verify(myObj).myMethod(eqToVal[MyValueClass](456))
   ```

## Improved ArgumentCaptor

For a more detailed explanation read [this](https://medium.com/@bbonanno_83496/introduction-to-mockito-scala-part-3-383c3b2ed55f) 

A new set of classes were added to make it easier, cleaner and more elegant to work with ArgumentCaptors, they also add 
support to capture value classes without any annoying syntax

There is a new `trait org.mockito.captor.ArgCaptor[T]` that exposes a nicer API

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
val captor = Captor[String]

aMock.stringArgument("it worked!")

verify(aMock).stringArgument(captor)

captor <-> "it worked!" 
//or 
captor shouldHave "it worked!"
```

As you can see there is no need to call `capture()` nor `getValue` anymore (although they're still there if you need them)

There is another constructor `ValCaptor[T]` that should be used to capture value classes

Both `Captor[T]` and `ValCaptor[T]` return an instance of `ArgCaptor[T]` so the API is the same for both

## `org.mockito.MockitoScalaSession`

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

## MockitoFixture

For a more detailed explanation read [this](https://medium.com/@bbonanno_83496/introduction-to-mockito-scala-part-3-383c3b2ed55f) 

If you mix-in this trait on your test class **after** your favourite Spec trait, you will get an automatic 
`MockitoScalaSession` around each one of your tests, so **all** of them will run in **Strict Stub** mode.

This trait also includes `org.mockito.MockitoSugar` and `org.mockito.ArgumentMatchersSugar` so you have pretty much all 
the mockito-scala API available in one go, i.e.

```scala
class MyTest extends WordSpec with MockitoFixture
```

In case you want to use the Idiomatic Syntax just do

```scala
class MyTest extends WordSpec with IdiomaticMockitoFixture
```

## `org.mockito.integrations.scalatest.ResetMocksAfterEachTest`

Inspired by [this](https://stackoverflow.com/questions/51387234/is-there-a-per-test-non-specific-mock-reset-pattern-using-scalaplayspecmockito) StackOverflow question,
mockito-scala provides this trait that helps to automatically reset any existent mock after each test is run
The trait has to be mixed **after** `org.mockito.MockitoSugar` in order to work, otherwise your test will not compile
The code shown in the StackOverflow question would look like this if using this mechanism

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
when(aMock.bar) thenCallRealMethod()                            <=> aMock.bar shouldCallRealMethod
when(aMock.bar).thenThrow[IllegalArgumentException]             <=> aMock.bar.shouldThrow[IllegalArgumentException]
when(aMock.bar) thenThrow new IllegalArgumentException          <=> aMock.bar shouldThrow new IllegalArgumentException
when(aMock.bar) thenAnswer(_ => "mocked!")                      <=> aMock.bar shouldAnswer "mocked!"
when(aMock.bar(any)) thenAnswer(_.getArgument[Int](0) * 10)     <=> aMock.bar(*) shouldAnswer ((i: Int) => i * 10)

doReturn("mocked!").when(aMock).bar                             <=> "mocked!" willBe returned by aMock bar
doAnswer(_ => "mocked!").when(aMock).bar                        <=> "mocked!" willBe answered by aMock bar
doAnswer(_.getArgument[Int](0) * 10).when(aMock).bar(any)       <=> ((i: Int) => i * 10) willBe answered by aMock bar *
doCallRealMethod.when(aMock).bar                                <=> theRealMethod willBe called by aMock bar
doThrow(new IllegalArgumentException).when(aMock).bar           <=> new IllegalArgumentException willBe thrown by aMock bar
  
verifyZeroInteractions(aMock)                                   <=> aMock was never called
verify(aMock).bar                                               <=> aMock wasCalled on bar
verify(aMock, only).bar                                         <=> aMock wasCalled onlyOn bar
verify(aMock, never).bar                                        <=> aMock was never called on bar
verify(aMock, times(2)).bar                                     <=> aMock wasCalled twiceOn bar
verify(aMock, times(6)).bar                                     <=> aMock wasCalled sixTimesOn bar
verifyNoMoreInteractions(aMock)                                 <=> aMock was never called again

val order = inOrder(mock1, mock2)                               <=> InOrder(mock1, mock2) { implicit order =>
order.verify(mock2).someMethod()                                <=>   mock2 wasCalled on someMethod ()
order.verify(mock1).anotherMethod()                             <=>   mock1 wasCalled on anotherMethod () 
                                                                <=> }

```

As you can see the new syntax reads a bit more natural, also notice you can use `*` instead of `any[T]`

Check the [tests](https://github.com/mockito/mockito-scala/blob/master/core/src/test/scala/org/mockito/IdiomaticMockitoTest.scala) for more examples

## Experimental features

* **by-name** arguments is currently an experimental feature as the implementation is a bit hacky and it gave some people problems

If you want to use it, you have to mix-in an extra trait (`org.mockito.ByNameExperimental`) 
in your test class, after `org.mockito.MockitoSugar`, so your test file would look like

```scala
class MyTest extends WordSpec with MockitoSugar with ByNameExperimental
```

It is important to notice that this feature relies on the class loader order to work, as we currently have to override a class
from `mockito-core`, you should not have to do anything special to make it work, `mockito-scala` already pulls the right 
version of `mockito-core` as a transitive dependency and it should be the only dependency you need, **BUT**, if you start getting 
weird exceptions while trying to use `org.mockito.ByNameExperimental` you can try to be explicit with the `mockito-core` dependency.
I can't tell you if you should put it before or after the `mockito-scala` dependency in your build file as it depends a lot
on which build tool and IDE you use, so try it out.

We are working with the mockito-core developers to add the necessary features in it so we can get rid of this hack as soon as we can, stay tuned!

## Notes for Scala 2.11

Please note that in Scala 2.11 the following features are not supported

* Default arguments on methods defined in traits (they will behave as before, getting `null` or a default value if they are of a primitive type)
* Any kind of ArgumentMatchers for methods with by-name parameters (they'll throw an exception if used with ArgumentMatchers)

## Authors

* **Bruno Bonanno** - *Initial work* - [bbonanno](https://github.com/bbonanno)


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
