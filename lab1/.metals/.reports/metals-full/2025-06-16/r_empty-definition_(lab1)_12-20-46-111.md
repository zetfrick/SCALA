error id: file:///D:/ИРИТ/SCALA/lab1/src/main/scala/Main.scala:`<none>`.
file:///D:/ИРИТ/SCALA/lab1/src/main/scala/Main.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 281
uri: file:///D:/ИРИТ/SCALA/lab1/src/main/scala/Main.scala
text:
```scala
import scala.language.higherKinds
import scala.util.Either

trait Calculator[F[_]] {
  def add(a: Int, b: Int): F[Int]
  def subtract(a: Int, b: Int): F[Int]
  def multiply(a: Int, b: Int): F[Int]
  def divide(
      a: Int,
      b: Int
  ): F[Either[String, Int]] // Ош@@ибки
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
  override def add(a: Int, b: Int): () => Int = () => a + b
  override def subtract(a: Int, b: Int): () => Int = () => a - b
  override def multiply(a: Int, b: Int): () => Int = () => a * b
  override def divide(a: Int, b: Int): () => Either[String, Int] =
    () => if (b == 0) Left("Division by zero") else Right(a / b)
}

type Id[A] = A // Немедленные вычисления
type Lazy[A] = () => A // Ленивые вычисления

object CalculatorDemo extends App {
  def program[F[_]](calc: Calculator[F]): F[Either[String, Int]] = {
    import calc._
    for {
      sum <- add(10, 5)
      product <- multiply(sum, 2)
      result <- divide(product, 3)
    } yield result
  }

  println("Немедленные вычисления")
  val immediateResult: Either[String, Int] = program(ImmediateInterpreter)
  println(immediateResult)

  println("\nЛенивые вычисления (Lazy)")
  val lazyResult: () => Either[String, Int] = program(LazyInterpreter)
  println(lazyResult())
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.