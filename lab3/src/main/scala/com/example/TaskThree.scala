package com.example

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

// Актор, вычисляющий частичный интеграл
object IntegralWorker {
  // Сообщение, которое принимает актор
  final case class CalculatePartialIntegral(
      f: Double => Double, // Функция, которую нужно интегрировать
      l: Double, // Левая граница интервала
      r: Double, // Правая граница интервала
      steps: Int, // Количество шагов
      replyTo: ActorRef[Double] // Актор, которому нужно отправить результат
  )

  // Поведение актора
  def apply(): Behavior[CalculatePartialIntegral] = Behaviors.receive {
    (context, message) =>
      // Вычисление частичного интеграла
      val width =
        (message.r - message.l) / message.steps // Ширина подпромежутка
      val points = (0 until message.steps).map(i =>
        message.l + (i + 0.5) * width
      ) // Точки в подпромежутках
      val values = points.map(message.f) // Значения функции в этих точках
      val sum = values.reduce(_ + _) // Сумма значений
      val partialIntegral = sum * width // Частичный интеграл

      // Отправка результата обратно
      message.replyTo ! partialIntegral

      Behaviors.same
  }
}

// Промежуточный актор для обработки сообщений типа Double
object IntegralHandler {
  def apply(
      master: ActorRef[IntegralMaster.PartialIntegralResult]
  ): Behavior[Double] = Behaviors.receive { (context, message) =>
    // Перенаправление результата в основной актор
    master ! IntegralMaster.PartialIntegralResult(message)
    Behaviors.same
  }
}

// Актор, суммирующий частичные интегралы
object IntegralMaster {
  // Сообщение, которое принимает актор
  final case class StartIntegral(
      f: Double => Double, // Функция, которую нужно интегрировать
      l: Double, // Левая граница интервала
      r: Double, // Правая граница интервала
      steps: Int, // Общее количество шагов
      numWorkers: Int // Количество рабочих акторов
  )
  final case class PartialIntegralResult(
      result: Double
  ) // Результат частичного интеграла

  // Поведение актора
  def apply(): Behavior[StartIntegral | PartialIntegralResult] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case StartIntegral(f, l, r, steps, numWorkers) =>
          val stepSize =
            (r - l) / numWorkers // Размер подпромежутка для каждого рабочего актора
          val workers = (0 until numWorkers).map { i =>
            val limitL =
              l + i * stepSize // Левая граница подпромежутка для рабочего актора
            val limitR =
              if (i == numWorkers - 1) r
              else
                limitL + stepSize // Правая граница подпромежутка для рабочего актора
            context.spawn(
              IntegralWorker(),
              s"worker-$i"
            ) // Создание рабочего актора
          }

          val resultHandler = context.spawn(
            IntegralHandler(context.self),
            "resultHandler"
          ) // Создание промежуточного актора для обработки результатов

          workers.zipWithIndex.foreach { case (worker, i) =>
            val limitL =
              l + i * stepSize // Левая граница подпромежутка для рабочего актора
            val limitR =
              if (i == numWorkers - 1) r
              else
                limitL + stepSize // Правая граница подпромежутка для рабочего актора
            worker ! IntegralWorker.CalculatePartialIntegral(
              f,
              limitL,
              limitR,
              steps / numWorkers,
              resultHandler
            ) // Отправка задачи рабочему актору
          }

          collResults(numWorkers, 0.0) // Переход к состоянию сбора результатов
      }
    }

  // Поведение для сбора результатов
  def collResults(
      numWorkers: Int,
      totalIntegral: Double
  ): Behavior[StartIntegral | PartialIntegralResult] = Behaviors.receive {
    (context, message) =>
      message match {
        case PartialIntegralResult(result) =>
          val newTotalIntegral =
            totalIntegral + result // Обновление общего интеграла
          val remainingWorkers =
            numWorkers - 1 // Уменьшение количества оставшихся рабочих акторов
          if (remainingWorkers == 0) {
            context.log.info(
              "\nTotal Integral: {}\n",
              newTotalIntegral
            ) // Логирование окончательного результата
            Behaviors.stopped // Остановка актора
          } else {
            collResults(
              remainingWorkers,
              newTotalIntegral
            ) // Продолжение сбора результатов
          }
        case _ =>
          Behaviors.same // Игнорирование других сообщений
      }
  }
}

object CalculatorIntegral {
  def main(args: Array[String]): Unit = {
    val f: Double => Double = x => x * x * x // Функция для интегрирования
    val l = 0.0 // Левая граница интервала
    val r = 5.0 // Правая граница интервала
    val steps = 1000 // Общее количество шагов
    val numWorkers = 4 // Количество рабочих акторов

    val system = ActorSystem(
      IntegralMaster(),
      "integralSystem"
    ) // Создание системы акторов
    system ! IntegralMaster.StartIntegral(
      f,
      l,
      r,
      steps,
      numWorkers
    ) // Запуск вычисления интеграла
  }
}
