package studentaccounting

import cats.effect.IO
import scalafx.application.Platform
import scalafx.scene.control.Label

class ErrorHandler(private val errorLabel: Label) {

  // Запуск действия в потоке JavaFX
  def runOnFxThread(action: => Unit): Unit = {
    Platform.runLater(action)
  }

  // Очистка сообщения об ошибке
  def clearError(): Unit = runOnFxThread {
    errorLabel.text = ""
    errorLabel.style = "-fx-text-fill: red;"
  }

  // Отображение сообщения об ошибке
  def showError(message: String): Unit = runOnFxThread {
    errorLabel.text = message
    errorLabel.style = "-fx-text-fill: red;"
  }

  // Отображение сообщения об успехе
  def showSuccess(message: String): Unit = runOnFxThread {
    errorLabel.text = message
    errorLabel.style = "-fx-text-fill: green;"
  }

  // IO ошибки
  def handleIOError[A](io: IO[A]): IO[Unit] = {
    io.attempt.flatMap {
      case Left(ex) => IO(showError(s"Ошибка: ${ex.getMessage}"))
      case Right(_) => IO.unit
    }
  }

  // Поле не пустое
  def validateFields(fields: (String, String)*): IO[Unit] = IO {
    fields.foreach { case (value, name) =>
      if (value.isEmpty) {
        throw new IllegalArgumentException(s"Поле '$name' не может быть пустым")
      }
    }
  }

  // Проверка, что поле является числом
  def validateIntField(value: String, fieldName: String): IO[Int] =
    IO(value.toInt).handleErrorWith(_ =>
      IO.raiseError(
        new IllegalArgumentException(s"$fieldName должен быть числом")
      )
    )

  // Оценка от 1 до 5
  def validateGradeValue(value: Int): IO[Unit] =
    if (value < 1 || value > 5) {
      IO.raiseError(new IllegalArgumentException("Оценка должна быть от 1 до 5"))
    } else IO.unit
}
