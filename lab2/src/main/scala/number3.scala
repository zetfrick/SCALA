import scala.language.higherKinds
import scala.util.Either
import scala.collection.immutable.LazyList

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
  override def add(a: Int, b: Int): () => Int = () => a + b
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

  println("\nДемонстрация ленивых списков:")

  val infiniteNumbers: LazyList[Int] = LazyList.from(1)

  val processedNumbers = infiniteNumbers
    .map { x =>
      println(s"Умножение к $x")
      x * 2
    }
    .filter { x =>
      println(s"Фильтрую $x")
      x % 3 == 0
    }

  println("\nПервые 5 обработанных элементов:")
  val firstFive = processedNumbers.take(5)
  println(firstFive.toList)

  println("\nСледующие 3:")
  val nextThree = processedNumbers.drop(5).take(3)
  println(nextThree.toList)

  println("\nС калькулятором в списке:")
  val calculations = LazyList.from(1).take(5).map { n =>
    val calc = LazyInterpreter
    val sum = calc.add(n, n)()
    val product = calc.multiply(sum, n)()
    product
  }
  println("Результаты вычислений: " + calculations.toList)

}
