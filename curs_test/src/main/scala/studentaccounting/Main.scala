package studentaccounting

import cats.effect.{IO, IOApp}
import java.nio.charset.Charset

object Main extends IOApp.Simple {
  System.setProperty("file.encoding", "UTF-8")
  java.nio.charset.Charset.forName("UTF-8")

  val initialData: IO[(List[Student], List[Subject], List[Grade])] = IO {
    val students = List(
      Student(1, "John Smith", 1, "CS-101"),
      Student(2, "Peter Johnson", 1, "CS-101"),
      Student(3, "Michael Brown", 2, "CS-102")
    )

    val subjects = List(
      Subject("Functional Programming"),
      Subject("Algorithms and Data Structures"),
      Subject("Database Systems")
    )

    val grades = List(
      Grade(1, "Functional Programming", 5),
      Grade(1, "Algorithms and Data Structures", 4),
      Grade(2, "Functional Programming", 3),
      Grade(2, "Algorithms and Data Structures", 5),
      Grade(3, "Database Systems", 4)
    )

    (students, subjects, grades)
  }

  override def run: IO[Unit] =
    for {
      (students, subjects, grades) <- initialData
      repo = new InMemoryRepository(students, subjects, grades)
      studentService = new StudentService[IO](repo)
      consoleApi = new ConsoleApi(studentService, repo)
      _ <- consoleApi.run()
    } yield ()
}
