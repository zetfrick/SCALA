package com.example

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

// Актор-сервер, выполняющий сложение целых чисел
object AddingServer {
  // Сообщение, которое принимает актор-сервер
  final case class AddMessage(a: Int, b: Int, replyTo: ActorRef[Int])

  def apply(): Behavior[AddMessage] = Behaviors.receive { (context, message) =>
    // Сложение и результат
    message.replyTo ! (message.a + message.b)

    Behaviors.same
  }
}

// Актор-клиент, генерируют и отправляет сообщения серверу
object AddingClient {
  // Сообщение, которое принимает актор-клиент
  final case class Start(server: ActorRef[AddingServer.AddMessage])

  def apply(): Behavior[Start | Int] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case Start(server) =>
        // Числа и сообщение
        server ! AddingServer.AddMessage(3, 8, context.self)
        // Обработка ответа
        Behaviors.receiveMessage {
          case message: Int =>
            context.log.info("Result: {}", message)
            Behaviors.same
        }
    }
  }
}

object AddingSystem {

  def apply(): Behavior[AddingClient.Start] = Behaviors.setup { context =>
    // Создание актор-сервер
    val server = context.spawn(AddingServer(), "addingServer")

    // Сощдание актор-клиент и отправка ему сообщения
    val client = context.spawn(AddingClient(), "addingClient")
    client ! AddingClient.Start(server)

    Behaviors.empty
  }
}


@main def AddingMain(): Unit = {
  // Создание системы акторов, AddingSystem корневой
  val system = ActorSystem(AddingSystem(), "system")
}
