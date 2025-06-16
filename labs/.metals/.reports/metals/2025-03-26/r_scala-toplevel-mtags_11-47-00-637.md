error id: file:///D:/ИРИТ/SCALA/lab2.1/src/main/scala/Main.scala:[263..264) in Input.VirtualFile("file:///D:/ИРИТ/SCALA/lab2.1/src/main/scala/Main.scala", "object Definition1{
  // Опишем некоторый примитивный язык. В данном случае - целочисленные выражения
  abstract sealed trait Expr
  case class Lit(n:Int) extends Expr
  case class Add(e1:Expr, e2:Expr) extends Expr
  case class Neg(e:Expr) extends Expr
  ..
  //И в TF
  trait ExprTF[X]:
    def lit(n:Int):X
    def add(e1:X, e2:X):X
    def neg(e:X):X

}

object Agregator1{
  import Definition1.*
  def show(expr:Expr):String = 
    expr match
      case Lit(n) => s"$n"
      case Add(e1, e2) => s"(${show(e1)} + ${show(e2)})"
      case Neg(e) => s"(-${show(e)})"
  
  def eval(expr:Expr):Int = 
    expr match
      case Lit(n) => n
      case Add(e1, e2) => eval(e1) + eval(e2)
      case Neg(e) => -eval(e)
  
  object Show extends ExprTF[String]:
    def lit(n: Int): String = s"$n"
    def add(e1: String, e2: String): String = s"($e1 + $e2)"
    def neg(e: String): String = s"(-$e)"

  object Eval extends ExprTF[Int]:
    def lit(n: Int): Int = n
    def add(e1: Int, e2: Int): Int = e1 + e2
    def neg(e:Int):Int = -e
}

object Program1{
  import Definition1.*
  import Agregator1.*

  @main def testInitialEncoding() = 
    // +(16, -(+(1, 2))) = 13 
    val thierty = Add(
      Lit(16), 
      Neg(
        Add(Lit(1),
        Lit(2))
        )
      )
    
    def thiertyTF[X](expr:ExprTF[X]):X = 
      expr.add(
        expr.lit(16),
        expr.neg(
          expr.add(
            expr.lit(1),
            expr.lit(2)
          )
        )
      )
    println(thierty)
    println("-"*100)
    println(show(thierty))
    println(eval(thierty))
    println("-"*100)
    println(thiertyTF(Show))
    println(thiertyTF(Eval))
}
")
file:///D:/ИРИТ/SCALA/lab2.1/src/main/scala/Main.scala:7: error: expected identifier; obtained dot
  ..
   ^
#### Short summary: 

expected identifier; obtained dot