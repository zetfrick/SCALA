error id: file:///D:/ИРИТ/SCALA/curs_test_2/src/main/scala/studentaccounting/Main.scala:`<none>`.
file:///D:/ИРИТ/SCALA/curs_test_2/src/main/scala/studentaccounting/Main.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 359
uri: file:///D:/ИРИТ/SCALA/curs_test_2/src/main/scala/studentaccounting/Main.scala
text:
```scala
package studentaccounting

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  // Установка UTF-8
  System.setProperty("file.encoding", "UTF-8")
  java.nio.charset.Charset.forName("UTF-8")

  // Примеры
  val initialData: IO[(List[Student], List[Subject], List[Grade])] = IO {
    val students = List(
      Student(1, "Иван Ивано@@", 1, "CS-101"),
      Student(2, "Петр Петров", 1, "CS-101"),
      Student(3, "Сергей Сергеев", 2, "CS-102")
    )

    val subjects = List(
      Subject("Функциональное программирование"),
      Subject("Алгоритмы и структуры данных"),
      Subject("Базы данных")
    )

    val grades = List(
      Grade(1, "Функциональное программирование", 5),
      Grade(1, "Алгоритмы и структуры данных", 4),
      Grade(2, "Функциональное программирование", 3),
      Grade(2, "Алгоритмы и структуры данных", 5),
      Grade(3, "Базы данных", 4)
    )

    (students, subjects, grades)
  }

  // Запуск приложения
  override def run: IO[Unit] =
    for {
      (students, subjects, grades) <- initialData
      repo = new InMemoryRepository(students, subjects, grades)
      studentService = new StudentService[IO](repo)
      _ <- IO {
        scalafx.application.Platform.startup(() => {})
        new GUI(studentService, repo).main(Array.empty)
      }
    } yield ()
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.