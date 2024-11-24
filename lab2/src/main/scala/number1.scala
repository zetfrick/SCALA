object CalculatorIntegral {
  def main(args: Array[String]): Unit = {
    val f: Double => Double = x => x * x * x
    val l = 0.0
    val r = 5.0
    val steps = 1000
    val result = integral(f, l, r, steps)
    println(s"Интеграл функции f(x) = x^3 от $l до $r с $steps шагами = $result")
  }
}
def integral(f: Double => Double, l: Double, r: Double, steps: Int): Double = {
  val width = (r - l) / steps
  val points = (0 until steps).map(i => l + i * width) // Создаёт диапазон чисел от 0 до steps и к каждому числу применяет формулу
  val values = points.map(f) // Функция f к кажой точке
  val sum = values.reduce(_ + _) // Суммируем все значения
  sum * width
}