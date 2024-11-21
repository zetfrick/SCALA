object Main extends App {
  println("1\n")
  hello()

  println("\n2\n")
  printHello5(5)
  println()
  printHello(5)

  println("\n3\n")
  // Зададим числа
  val nums = List(5, 3, 76, 12, 32, 56, 76, 87)
  println("Числа: " + nums.mkString(" "))
  // Выведем список чисел с чётным и нечётным индексом
  val (even, odd) = splitIndex(nums) // filter и map
  println("Четные: " + even.mkString(" "))
  println("Нечетные: " + odd.mkString(" "))
  max(nums) // reduce

  println("\n4\n")
  // Помещаем функцию в переменную
  val function = printHello(5)
  // Выводим переменную
  println("Вывод: " + function)

  println("\n5\n")
  val value = List(56, 76, 87)
  // Используем pattern matching для обработки списка чисел
  process(value)

  println("\n6\n")
  println(compose(first, second)(10))
}

def hello(): Unit = println("hello world")

def printHello5(n: Int): Unit = {
  for (i <- 1 to n) {
    println(s"hello $i")
  }
}

def printHello(n: Int): Unit = {
  for (i <- 1 to n) {
    val x = if (i % 2 == 0) i else n - i
    println(s"hello $x")
  }
}

def splitIndex(nums: List[Int]): (List[Int], List[Int]) = {

  val Num = nums.zipWithIndex

  // Фильтруем индексы
  val even = Num.filter { case (_, index) => index % 2 == 0 }.map(_._1)
  val odd = Num.filter { case (_, index) => index % 2 != 0 }.map(_._1)

  (even, odd)
}

def max(nums: List[Int]) = {
  println(nums.reduce((a, b) => if (a > b) a else b))
}

def process(nums: List[Int]): Unit = {
  nums.foreach {
    case x if x < 10            => println(s"$x меньше 10")
    case x if x >= 10 && x < 50 => println(s"$x между 10 и 49")
    case x if x == 50           => println(s"$x равен 50")
    case x if x > 50            => println(s"$x больше 50")
  }
}

val first: Int => Int = x => 3 + x
val second: Int => Double = x => x + 0.5
def compose(a: Int => Int, b: Int => Double): Int => Double = x => b(a(x))
