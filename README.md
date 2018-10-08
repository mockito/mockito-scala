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

## Migration Notes for version 1.0.0
* `DefaultAnswer` was moved from `org.mockito.DefaultAnswer` to `org.mockito.stubbing.DefaultAnswer`
* The recommended way to use the pre-defined `DefaultAnswer`s is via the object `org.mockito.DefaultAnswers`
* `*` matcher is now defined in `org.mockito.ArgumentMatchersSugar`, mixin (or use the companion object) this trait whenever you wanna use it
* `argumentCaptor[String]` was removed, replace by either `ArgCaptor[T]` (`Captor[T]` was renamed to `ArgCaptor[T]` to add clarity) or `ValCaptor[T]` as needed, (see [Improved ArgumentCaptor](#improved-argumentcaptor)) 
* The usage of `org.mockito.Answer[T]` was removed from the API in favour of [Function Answers](#function-answers)
* If you were using something like `doAnswer(_ => <something>).when ...` to lazily compute a return value when the method is actually called you should now write it like `doAnswer(<something>).when ...`, no need of passing a function as that argument is by-name
* If you have chained return values like `when(myMock.foo) thenReturn "a" thenReturn "b" etc...` the syntax has changed a bit to `when(myMock.foo) thenReturn "a" andThen "b" etc...`
* Idiomatic syntax has some changes to allow full support of mixing values and argument matchers
```scala
aMock wasCalled on bar              => aMock.bar wasCalled()
aMock wasCalled onlyOn bar          => aMock.bar wasCalled onlyHere
aMock was never called on bar       => aMock.bar wasNotCalled()
aMock wasCalled twiceOn bar         => aMock.bar wasCalled twice
aMock wasCalled sixTimesOn bar      => aMock.bar wasCalled sixTimes

"mocked!" willBe returned by aMock bar                      => "mocked!" willBe returned by aMock.bar
"mocked!" willBe answered by aMock bar                      => "mocked!" willBe answered by aMock.bar
((i: Int) => i * 10) willBe answered by aMock bar *         => ((i: Int) => i * 10) willBe answered by aMock.bar(*)
theRealMethod willBe called by aMock bar                    => theRealMethod willBe called by aMock.bar
new IllegalArgumentException willBe thrown by aMock bar     => new IllegalArgumentException willBe thrown by aMock.bar
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
*   Supports by-name arguments in some scenarios
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
*   `any` works even when the type can't be inferred, removing the need of using the likes of `anyString`, `anyInt`, etc (see [Notes](#dead-code-warning))
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
val captor = ArgCaptor[String]

aMock.stringArgument("it worked!")

verify(aMock).stringArgument(captor)

captor hasCaptured "it worked!"
```

As you can see there is no need to call `capture()` nor `getValue` anymore (although they're still there if you need them)

There is another constructor `ValCaptor[T]` that should be used to capture value classes

Both `ArgCaptor[T]` and `ValCaptor[T]` return an instance of `Captor[T]` so the API is the same for both

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

IMPORTANT: A session is defined on a per-test basis, and only the mocks created within the scope of the session are 
handled by it, so if you have class level fields with mocks, i.e. mocks that are not created within the test, they will
be ignored by the session. If you use the same mocks in all or most of the tests and want to avoid the boilerplate while
still usfing the advantages of strict stubbing then declare those mocks in a setup trait.

```scala
class MySpec extends WordSpec with MockitoFixture {
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

## Notes

# Dead code warning
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
 
# Scala 2.11
Please note that in Scala 2.11 the following features are not supported

* Default arguments on methods defined in traits (they will behave as before, getting `null` or a default value if they 
are of a primitive type)
* Any kind of `ArgumentMatcher[T]` for methods with by-name parameters (they'll throw an exception if used with `ArgumentMatcher[T]`)

## Authors

* **Bruno Bonanno** - *Initial work* - [bbonanno](https://github.com/bbonanno)


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
