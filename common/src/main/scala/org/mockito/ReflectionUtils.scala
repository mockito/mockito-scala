package org.mockito

import java.lang.reflect.{ Field, Method, Modifier }

import org.mockito.internal.ValueClassWrapper
import org.mockito.invocation.InvocationOnMock
import org.scalactic.TripleEquals._
import ru.vyarus.java.generics.resolver.GenericsResolver

import scala.reflect.ClassTag
import scala.reflect.internal.Symbols
import scala.util.{ Failure, Success, Try => uTry }
import scala.util.control.NonFatal

object ReflectionUtils {
  import scala.reflect.runtime.{ universe => ru }
  import ru._

  implicit def symbolToMethodSymbol(sym: Symbol): Symbols#MethodSymbol = sym.asInstanceOf[Symbols#MethodSymbol]

  private val mirror = runtimeMirror(getClass.getClassLoader)
  private val customMirror = mirror.asInstanceOf[{
      def methodToJava(sym: Symbols#MethodSymbol): Method
    }
  ]

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
      scala.util
        .Try {
          findTypeSymbol
            .filter(_.returnType.typeSymbol.isClass)
            .map(_.asMethod.returnType.typeSymbol.asClass)
            .map(mirror.runtimeClass)
        }
        .toOption
        .flatten

    private def findTypeSymbol =
      scala.util
        .Try {
          mirror
            .classSymbol(method.getDeclaringClass)
            .info
            .decls
            .collectFirst {
              case symbol if isNonConstructorMethod(symbol) && customMirror.methodToJava(symbol) === method => symbol
            }
        }
        .toOption
        .flatten

    private def resolveWithJavaGenerics: Option[Class[_]] =
      try Some(GenericsResolver.resolve(invocation.getMock.getClass).`type`(method.getDeclaringClass).method(method).resolveReturnClass())
      catch {
        case _: Throwable => None
      }
  }

  private def isNonConstructorMethod(d: ru.Symbol): Boolean = d.isMethod && !d.isConstructor

  def extraInterfaces[T](implicit $wtt: WeakTypeTag[T], $ct: ClassTag[T]): List[Class[_]] =
    scala.util
      .Try {
        val cls = clazz($ct)
        $wtt.tpe match {
          case RefinedType(types, _) =>
            types.map($wtt.mirror.runtimeClass).collect {
              case c: Class[_] if c.isInterface && c != cls => c
            }
          case _ => List.empty
        }
      }
      .toOption
      .getOrElse(List.empty)

  def methodsWithLazyOrVarArgs(classes: Seq[Class[_]]): Seq[(Method, Set[Int])] =
    classes.flatMap { clazz =>
      scala.util
        .Try {
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
        }
        .toOption
        .getOrElse(Seq.empty)
    }

  def setFinalStatic(field: Field, newValue: Any): Unit = {
    val clazz = classOf[java.lang.Class[_]]
    field.setAccessible(true)
    val modifiersField: Field = uTry(clazz.getDeclaredField("modifiers")) match {
      case Success(modifiers) => modifiers
      case Failure(e) =>
        uTry {
          val getDeclaredFields0           = clazz.getDeclaredMethod("getDeclaredFields0", classOf[Boolean])
          val accessibleBeforeSet: Boolean = getDeclaredFields0.isAccessible
          getDeclaredFields0.setAccessible(true)
          val declaredFields: Array[Field] = getDeclaredFields0
            .invoke(classOf[Field], java.lang.Boolean.FALSE)
            .asInstanceOf[Array[Field]]
          getDeclaredFields0.setAccessible(accessibleBeforeSet)
          declaredFields.find("modifiers" == _.getName).get
        } match {
          case Success(modifiers) => modifiers
          case Failure(ex) =>
            e.addSuppressed(ex)
            throw e
        }
    }
    modifiersField.setAccessible(true)
    modifiersField.setInt(field, field.getModifiers & ~Modifier.FINAL)
    field.set(null, newValue)
  }

}
