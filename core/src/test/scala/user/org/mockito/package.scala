package user.org

package object mockito {
  case class Cheese(name: String)

  trait FooWithVarArg {
    def bar(bells: String*)
  }
  trait FooWithSecondParameterList {
    def bar(bell: String)(cheese: Cheese)
  }
  trait FooWithVarArgAndSecondParameterList {
    def bar(bells: String*)(cheese: Cheese)
  }
}
