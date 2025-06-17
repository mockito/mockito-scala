package org.mockito

import java.lang.reflect.{ Field, Method, Modifier }

import org.mockito.internal.ValueClassWrapper
import org.mockito.invocation.InvocationOnMock
import org.scalactic.TripleEquals._
import ru.vyarus.java.generics.resolver.GenericsResolver

import scala.reflect.ClassTag
import scala.reflect.internal.Symbols
import scala.util.{ Try => uTry }
import scala.util.control.NonFatal

object ReflectionUtils {
  import scala.reflect.runtime.{ universe => ru }
  import ru._

  implicit def symbolToMethodSymbol(sym: Symbol): Symbols#MethodSymbol = sym.asInstanceOf[Symbols#MethodSymbol]

  private val mirror       = runtimeMirror(getClass.getClassLoader)
  private val customMirror = mirror.asInstanceOf[{
    def methodToJava(sym: Symbols#MethodSymbol): Method
  }]

  def listToTuple(l: List[Object]): Any =
    l match {
      case Nil      => Nil
      case h :: Nil => h
      case _        => Class.forName(s"scala.Tuple${l.size}").getDeclaredConstructors.head.newInstance(l: _*)
    }

  implicit class InvocationOnMockOps(val invocation: InvocationOnMock) extends AnyVal {
    def mock[M]: M                               = invocation.getMock.asInstanceOf[M]
    def method: Method                           = invocation.getMethod
    def arg[A: ValueClassWrapper](index: Int): A = ValueClassWrapper[A].wrapAs[A](invocation.getArgument(index))
    def args: List[Any]                          = invocation.getArguments.toList
    def callRealMethod[R](): R                   = invocation.callRealMethod.asInstanceOf[R]
    def argsAsTuple: Any                         = listToTuple(args.map(_.asInstanceOf[Object]))

    def returnType: Class[_] = {
      val javaReturnType = method.getReturnType

      if (javaReturnType == classOf[Object])
        resolveWithScalaGenerics
          .orElse(resolveWithJavaGenerics)
          .getOrElse(javaReturnType)
      else javaReturnType
    }

    def returnsValueClass: Boolean = findTypeSymbol.exists(_.returnType.typeSymbol.isDerivedValueClass)

    private def resolveWithScalaGenerics: Option[Class[_]] =
      uTry {
        findTypeSymbol
          .filter(_.returnType.typeSymbol.isClass)
          .map(_.asMethod.returnType.typeSymbol.asClass)
          .map(mirror.runtimeClass)
      }.toOption.flatten

    private def findTypeSymbol =
      uTry {
        mirror
          .classSymbol(method.getDeclaringClass)
          .info
          .decls
          .collectFirst {
            case symbol if isNonConstructorMethod(symbol) && customMirror.methodToJava(symbol) === method => symbol
          }
      }.toOption.flatten

    private def resolveWithJavaGenerics: Option[Class[_]] =
      try Some(GenericsResolver.resolve(invocation.getMock.getClass).`type`(method.getDeclaringClass).method(method).resolveReturnClass())
      catch {
        case _: Throwable => None
      }
  }

  private def isNonConstructorMethod(d: ru.Symbol): Boolean = d.isMethod && !d.isConstructor

  def extraInterfaces[T](implicit $wtt: WeakTypeTag[T], $ct: ClassTag[T]): List[Class[_]] =
    uTry {
      val cls = clazz($ct)
      $wtt.tpe match {
        case RefinedType(types, _) =>
          types.map($wtt.mirror.runtimeClass).collect {
            case c: Class[_] if c.isInterface && c != cls => c
          }
        case _ => List.empty
      }
    }.toOption
      .getOrElse(List.empty)

  def methodsWithLazyOrVarArgs(classes: Seq[Class[_]]): Seq[(Method, Set[Int])] =
    classes.flatMap { clazz =>
      uTry {
        mirror
          .classSymbol(clazz)
          .info
          .members
          .collect {
            case symbol if isNonConstructorMethod(symbol) =>
              symbol -> symbol.typeSignature.paramLists.flatten.zipWithIndex.collect {
                case (p, idx) if p.typeSignature.toString.startsWith("=>") => idx
                case (p, idx) if p.typeSignature.toString.endsWith("*")    => idx
              }.toSet
          }
          .collect {
            case (symbol, indices) if indices.nonEmpty => customMirror.methodToJava(symbol) -> indices
          }
          .toSeq
      }.toOption
        .getOrElse(Seq.empty)
    }

  def setFinalStatic(field: Field, newValue: AnyRef): Unit =
    try {
      // Try to get Unsafe instance (works with both sun.misc.Unsafe and jdk.internal.misc.Unsafe)
      val unsafeClass: Class[_] =
        try
          Class.forName("sun.misc.Unsafe")
        catch {
          case _: ClassNotFoundException => Class.forName("jdk.internal.misc.Unsafe")
        }

      val unsafeField = unsafeClass.getDeclaredField("theUnsafe")
      unsafeField.setAccessible(true)
      val unsafe = unsafeField.get(null)

      // Get methods via reflection to handle both Unsafe implementations
      val staticFieldBaseMethod   = unsafeClass.getMethod("staticFieldBase", classOf[Field])
      val staticFieldOffsetMethod = unsafeClass.getMethod("staticFieldOffset", classOf[Field])
      val putObjectMethod         = unsafeClass.getMethod("putObject", classOf[Object], classOf[Long], classOf[Object])

      // Make the field accessible
      field.setAccessible(true)

      // Get base and offset for the field
      val base: Object = staticFieldBaseMethod.invoke(unsafe, field)
      val offset: Long = staticFieldOffsetMethod.invoke(unsafe, field).asInstanceOf[Long]

      // Set the field value directly
      putObjectMethod.invoke(unsafe, base, java.lang.Long.valueOf(offset), newValue)
    } catch {
      case NonFatal(e) =>
        throw new IllegalStateException(s"Cannot modify final field ${field.getName}", e)
    }

}
