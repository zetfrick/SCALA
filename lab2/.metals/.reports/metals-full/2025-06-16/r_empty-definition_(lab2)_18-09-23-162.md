error id: file:///D:/ИРИТ/SCALA/lab2/src/main/scala/number3.scala:`<none>`.
file:///D:/ИРИТ/SCALA/lab2/src/main/scala/number3.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 693
uri: file:///D:/ИРИТ/SCALA/lab2/src/main/scala/number3.scala
text:
```scala
import scala.language.higherKinds
import scala.util.Either

trait Calculator[F[_]] {
  def add(a: Int, b: Int): F[Int]
  def subtract(a: Int, b: Int): F[Int]
  def multiply(a: Int, b: Int): F[Int]
  def divide(a: Int, b: Int): F[Either[String, Int]] // Ошибки
}

// Немедленные вычисления
object ImmediateInterpreter extends Calculator[Id] {
  override def add(a: Int, b: Int): Int = a + b
  override def subtract(a: Int, b: Int): Int = a - b
  override def multiply(a: Int, b: Int): Int = a * b
  override def divide(a: Int, b: Int): Either[String, Int] =
    if (b == 0) Left("Division by zero") else Right(a / b)
}

// Ленивые вычисления
object LazyInterpreter extends Calculator[Lazy] {
  @@override def add(a: Int, b: Int): () => Int = () => a + b
  override def subtract(a: Int, b: Int): () => Int = () => a - b
  override def multiply(a: Int, b: Int): () => Int = () => a * b
  override def divide(a: Int, b: Int): () => Either[String, Int] =
    () => if (b == 0) Left("Division by zero") else Right(a / b)
}

type Id[A] = A // Немедленные вычисления
type Lazy[A] = () => A // Ленивые вычисления

object Calculator extends App {
  // Для немедленных вычислений
  def programId(calc: Calculator[Id]): Either[String, Int] = {
    val sum = calc.add(10, 5)
    val product = calc.multiply(sum, 2)
    calc.divide(product, 3)
  }

  // Для ленивых вычислений
  def programLazy(calc: Calculator[Lazy]): () => Either[String, Int] = { () =>
    {
      val sum = calc.add(10, 5)()
      val product = calc.multiply(sum, 2)()
      calc.divide(product, 3)()
    }
  }

  println("Немедленные вычисления")
  val immediateResult: Either[String, Int] = programId(ImmediateInterpreter)
  println(immediateResult)

  println("\nЛенивые вычисления")
  val lazyResult: () => Either[String, Int] = programLazy(LazyInterpreter)
  println(lazyResult())
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.