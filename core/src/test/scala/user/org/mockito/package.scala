package user.org

package object mockito {
  case class Bread(name: String) extends AnyVal
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

  trait ValueClassWithVarArg {
    def bar(bread: Bread*)
  }
  trait ValueClassWithSecondParameterList {
    def bar(bread: Bread)(cheese: Cheese)
  }
  trait ValueClassWithVarArgAndSecondParameterList {
    def bar(breads: Bread*)(cheese: Cheese)
  }
}
