import scala.util.{Try, Success, Failure}
import scala.concurrent.Future
import scala.io.StdIn.readLine
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object SecurePasswordApp {
  def main(args: Array[String]): Unit = {
    // Option
    println("Проверка с использованием Option")
    println(testPasswordOption("1Redgdggdg!"))
    println(testPasswordOption("red!"))
    println(testPasswordOption("Red"))
    println()

    // Either
    println("Проверка с использованием Either")
    println(testPassword("1Redgdggdg!"))
    println(testPassword("red!"))
    println(testPassword("Red"))
    println()

    // Future
    println("Запрос с использованием Future")
    val firstPassword = readingPassword()
    val finalPassword = Await.result(firstPassword, Duration.Inf)
    println(s"Пароль верен: $finalPassword \n")
    println()
  }
}

// Функция для проверки допустимости пароля
def testPasswordOption(password: String): Boolean = {
  val specialSymbol = "!@#$%^&*()_+-=[]{}|;:',.<>/?`~"

  val criteria: Seq[Boolean] = Seq(
    password.length >= 8,
    password.exists(_.isLower),
    password.exists(_.isUpper),
    password.exists(_.isDigit),
    password.exists(specialSymbol.contains)
  )
  criteria.reduce(_ && _) // Вернёт true, если все хорошо, иначе false
}

// Функция для проверки пароля с использованием Either
def testPassword(password: String): Either[String, Boolean] = {
  val specialChars = "!@#$%^&*()_+-=[]{}|;:',.<>/?`~"

  def checkLength: Either[String, Boolean] =
    if (password.length >= 8) Right(true)
    else Left("Пароль должен содержать не менее 8 символов")

  def checkUpperCase: Either[String, Boolean] =
    if (password.exists(_.isUpper)) Right(true)
    else Left("Пароль должен содержать хотя бы одну заглавную букву")

  def checkLowerCase: Either[String, Boolean] =
    if (password.exists(_.isLower)) Right(true)
    else Left("Пароль должен содержать хотя бы одну строчную букву")

  def checkDigit: Either[String, Boolean] =
    if (password.exists(_.isDigit)) Right(true)
    else Left("Пароль должен содержать хотя бы одну цифру")

  def checkSpecialChar: Either[String, Boolean] =
    if (password.exists(specialChars.contains)) Right(true)
    else Left("Пароль должен содержать хотя бы один специальный символ")

  // Последовательное выполнение проверок
  checkLength
    .flatMap(_ =>
      checkUpperCase
    ) // Проверяем длинну, если ОК идём дальше и т.д.
    .flatMap(_ => checkLowerCase)
    .flatMap(_ => checkDigit)
    .flatMap(_ => checkSpecialChar)
}

// Функция для запроса пароля у пользователя и проверки его допустимости
def readingPassword(): Future[String] = {
  Future {
    print("Введите пароль: ")
    readLine()
  }.map { password =>
    testPassword(password) match {
      case Right(_) => password
      case Left(error) =>
        println(s"Ошибка: $error")
        throw new Exception() // Исключение
    }
  }.recoverWith { case _ => readingPassword() } // Перехватывает все исключения
}

// Я пытался ещё сделать пароль с символом ¬, но она просто его никак не пониамет, это такая особенность или почему?
