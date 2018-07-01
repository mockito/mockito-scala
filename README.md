# Mockito Scala

<a href="http://site.mockito.org">
<img src="https://raw.githubusercontent.com/mockito/mockito/master/src/javadoc/org/mockito/logo.png"
     srcset="https://raw.githubusercontent.com/mockito/mockito/master/src/javadoc/org/mockito/logo@2x.png 2x"
     alt="Mockito" />
</a>

The most popular mocking framework for Java, now in Scala!!!.

## Why separate project?

The library has independent developers, release cycle and versioning from core mockito library (https://github.com/mockito/mockito). This is intentional because core Mockito developers don't use Scala and cannot confidently review PRs, and set the vision for the Scala library.

## Getting started

Add the latest version to your dependencies

### SBT
```libraryDependencies += "org.mockito" %% "mockito-scala" % "0.0.1" % Test```

Then mixin one (or both) of the following traits as required

## ```org.mockito.MockitoSugar```

This trait wraps the API available on ```org.mockito.Mockito``` from the Java version, but it provides a more Scala-like syntax, mainly
*   Fixes the compiler errors that sometimes occurred when using overloaded methods that use varargs like doReturn
*   Eliminates the need to use ```classOf[T]```
*   Eliminates parenthesis when possible to make the test code more readable
*   Adds ```spyLambda[T]``` to allow spying lambdas (they don't work with the standard spy as they are created as final classes by the compiler)
*   Supports mocking inline mixins like ```mock[MyClass with MyTrait]```

The companion object also extends the trait to allow the usage of the API without mixing-in the trait in case that's desired

## ```org.mockito.ArgumentMatchersSugar```

This trait exposes all the existent ```org.mockito.ArgumentMatchers``` but again it gives them a more Scala-like syntax, mainly
*   ```eq``` was renamed to ```eqTo``` to avoid clashing with the Scala ```eq``` operator for identity equality
*   ```any``` resolves to the correct type most of the times, removing the need of using the likes of ```anyString```, ```anyInt```, etc
*   ```isNull``` and ```isNotNull``` are deprecated as using nulls in Scala is clear code smell

Again, the companion object also extends the trait to allow the usage of the API without mixing-in the trait in case that's desired

## Authors

* **Bruno Bonanno** - *Initial work* - [bbonanno](https://github.com/bbonanno)


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
