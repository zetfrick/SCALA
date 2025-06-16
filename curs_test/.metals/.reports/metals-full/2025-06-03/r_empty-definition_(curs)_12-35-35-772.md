error id: file:///D:/ИРИТ/SCALA/curs/src/main/scala/Main.scala:`<none>`.
file:///D:/ИРИТ/SCALA/curs/src/main/scala/Main.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 5611
uri: file:///D:/ИРИТ/SCALA/curs/src/main/scala/Main.scala
text:
```scala
import cats.effect.{IO, IOApp}
import cats.implicits._
import fs2.Stream
import java.io.{InputStreamReader, BufferedReader, PrintWriter}
import java.nio.charset.StandardCharsets

// Упрощенные алгебраические типы данных без лишних ID
sealed trait Entity
case class Student(id: Int, name: String, groupName: String, year: Int)
    extends Entity
case class Subject(name: String, course: Int) extends Entity
case class Grade(studentId: Int, subjectName: String, value: Int) extends Entity

// Тип для статистики
case class Stats(
    studentAvg: Map[Int, Double],
    groupAvg: Map[String, Double],
    courseAvg: Map[Int, Double]
)

// Репозиторий как tagless final алгебра
trait Repository[F[_]] {
  def getAllStudents: F[List[Student]]
  def getStudent(id: Int): F[Option[Student]]
  def addStudent(student: Student): F[Unit]
  def updateStudent(student: Student): F[Unit]
  def deleteStudent(id: Int): F[Unit]

  def getAllSubjects: F[List[Subject]]
  def getSubject(name: String): F[Option[Subject]]
  def addSubject(subject: Subject): F[Unit]
  def deleteSubject(name: String): F[Unit]

  def getAllGrades: F[List[Grade]]
  def getGradesForStudent(studentId: Int): F[List[Grade]]
  def addGrade(grade: Grade): F[Unit]
  def deleteGradesForStudent(studentId: Int): F[Unit]

  def calculateStats: F[Stats]
  def getAllData: F[List[(Student, List[Grade])]]
}

// In-memory реализация репозитория
class InMemoryRepository(
    students: List[Student],
    subjects: List[Subject],
    grades: List[Grade]
) extends Repository[IO] {
  private var studentState = students
  private var subjectState = subjects
  private var gradeState = grades

  override def getAllStudents: IO[List[Student]] = IO(studentState)
  override def getStudent(id: Int): IO[Option[Student]] = IO(
    studentState.find(_.id == id)
  )
  override def addStudent(student: Student): IO[Unit] = IO {
    studentState = student :: studentState
  }
  override def updateStudent(student: Student): IO[Unit] = IO {
    studentState = studentState.map(s => if (s.id == student.id) student else s)
  }
  override def deleteStudent(id: Int): IO[Unit] = IO {
    studentState = studentState.filterNot(_.id == id)
  }

  override def getAllSubjects: IO[List[Subject]] = IO(subjectState)
  override def getSubject(name: String): IO[Option[Subject]] = IO(
    subjectState.find(_.name.equalsIgnoreCase(name))
  )
  override def addSubject(subject: Subject): IO[Unit] = IO {
    subjectState = subject :: subjectState
  }
  override def deleteSubject(name: String): IO[Unit] = IO {
    subjectState = subjectState.filterNot(_.name.equalsIgnoreCase(name))
  }

  override def getAllGrades: IO[List[Grade]] = IO(gradeState)
  override def getGradesForStudent(studentId: Int): IO[List[Grade]] = IO(
    gradeState.filter(_.studentId == studentId)
  )
  override def addGrade(grade: Grade): IO[Unit] = IO {
    gradeState = grade :: gradeState
  }
  override def deleteGradesForStudent(studentId: Int): IO[Unit] = IO {
    gradeState = gradeState.filterNot(_.studentId == studentId)
  }

  override def calculateStats: IO[Stats] = IO {
    lazy val studentGrades = gradeState.groupBy(_.studentId)
    lazy val groupStudents = studentState.groupBy(_.groupName)
    lazy val courseSubjects = subjectState.groupBy(_.course)

    val studentAvg = studentGrades.view.mapValues { grades =>
      grades.map(_.value).sum.toDouble / grades.length
    }.toMap

    val groupAvg = groupStudents.view.mapValues { students =>
      val studentIds = students.map(_.id).toSet
      val relevantGrades =
        gradeState.filter(g => studentIds.contains(g.studentId))
      if (relevantGrades.isEmpty) 0.0
      else relevantGrades.map(_.value).sum.toDouble / relevantGrades.length
    }.toMap

    val courseAvg = courseSubjects.view.mapValues { subjects =>
      val subjectNames = subjects.map(_.name).toSet
      val relevantGrades =
        gradeState.filter(g => subjectNames.contains(g.subjectName))
      if (relevantGrades.isEmpty) 0.0
      else relevantGrades.map(_.value).sum.toDouble / relevantGrades.length
    }.toMap

    Stats(studentAvg, groupAvg, courseAvg)
  }

  override def getAllData: IO[List[(Student, List[Grade])]] = IO {
    studentState.map { student =>
      val studentGrades = gradeState.filter(_.studentId == student.id)
      (student, studentGrades)
    }
  }
}

// Сервисный слой
class StudentService[F[_]](repo: Repository[F])(implicit F: cats.Monad[F]) {
  def createStudent(
      id: Int,
      name: String,
      groupName: String,
      year: Int
  ): F[Unit] =
    repo.addStudent(Student(id, name, groupName, year))

  def getStudentDetails(id: Int): F[Option[(Student, List[(String, Int)])]] =
    for {
      studentOpt <- repo.getStudent(id)
      result <- studentOpt match {
        case Some(student) =>
          for {
            grades <- repo.getGradesForStudent(student.id)
            gradesWithSubjects = grades.map(g => (g.subjectName, g.value))
          } yield Some((student, gradesWithSubjects))
        case None => F.pure(None)
      }
    } yield result

  def deleteStudentWithGrades(id: Int): F[Unit] =
    for {
      _ <- repo.deleteStudent(id)
      _ <- repo.deleteGradesForStudent(id)
    } yield ()
}

// Консольный интерфейс с поддержкой UTF-8
class ConsoleApi(studentService: StudentService[IO], repo: Repository[IO]) {
  private val utf8Reader = new BufferedReader(
    new InputStreamReader(System.in, StandardCharsets.UTF_8)
  )
  @@private val utf8Writer = new PrintWriter(System.out, true, "UTF-8")

  private def printMenu(): IO[Unit] = IO {
    utf8Writer.println("\nУчётная система студентов")
    utf8Writer.println("1. Добавить студента")
    utf8Writer.println("2. Просмотреть студента")
    utf8Writer.println("3. Удалить студента")
    utf8Writer.println("4. Добавить предмет")
    utf8Writer.println("5. Добавить оценку")
    utf8Writer.println("6. Просмотреть статистику")
    utf8Writer.println("7. Просмотреть все данные")
    utf8Writer.println("8. Выход")
    utf8Writer.print("Выберите действие: ")
    utf8Writer.flush()
  }

  private def readInput(prompt: String): IO[String] = IO {
    utf8Writer.print(prompt)
    utf8Writer.flush()
    utf8Reader.readLine()
  }

  private def printlnUTF8(text: String): IO[Unit] = IO {
    utf8Writer.println(text)
    utf8Writer.flush()
  }

  private def addStudent(): IO[Unit] =
    for {
      id <- readInput("ID студента: ").map(_.toInt)
      name <- readInput("Имя студента: ")
      groupName <- readInput("Название группы: ")
      year <- readInput("Год поступления: ").map(_.toInt)
      _ <- studentService.createStudent(id, name, groupName, year)
      _ <- printlnUTF8("Студент добавлен")
    } yield ()

  private def viewStudent(): IO[Unit] =
    for {
      id <- readInput("ID студента: ").map(_.toInt)
      details <- studentService.getStudentDetails(id)
      _ <- details match {
        case Some((student, grades)) =>
          IO {
            utf8Writer.println(
              s"\nСтудент: ${student.name} (ID: ${student.id})"
            )
            utf8Writer.println(
              s"Группа: ${student.groupName} (Год: ${student.year})"
            )
            utf8Writer.println("\nОценки:")
            grades.foreach { case (subject, grade) =>
              utf8Writer.println(s"$subject: $grade")
            }
            utf8Writer.flush()
          }
        case None =>
          printlnUTF8("Студент не найден")
      }
    } yield ()

  private def deleteStudent(): IO[Unit] =
    for {
      id <- readInput("ID студента для удаления: ").map(_.toInt)
      _ <- studentService.deleteStudentWithGrades(id)
      _ <- printlnUTF8("Студент и его оценки удалены")
    } yield ()

  private def addSubject(): IO[Unit] =
    for {
      name <- readInput("Название предмета: ")
      course <- readInput("Курс: ").map(_.toInt)
      _ <- repo.addSubject(Subject(name, course))
      _ <- printlnUTF8("Предмет добавлен")
    } yield ()

  private def addGrade(): IO[Unit] =
    for {
      studentId <- readInput("ID студента: ").map(_.toInt)
      subjectName <- readInput("Название предмета: ")
      value <- readInput("Оценка (1-5): ").map(_.toInt)
      _ <- repo.addGrade(Grade(studentId, subjectName, value))
      _ <- printlnUTF8("Оценка добавлена")
    } yield ()

  private def viewStats(): IO[Unit] =
    for {
      stats <- repo.calculateStats
      _ <- IO {
        utf8Writer.println("\nСтатистика:")
        utf8Writer.println("\nСредний балл по студентам:")
        stats.studentAvg.foreach { case (id, avg) =>
          utf8Writer.println(s"Студент $id: ${"%.2f".format(avg)}")
        }

        utf8Writer.println("\nСредний балл по группам:")
        stats.groupAvg.foreach { case (group, avg) =>
          utf8Writer.println(s"Группа $group: ${"%.2f".format(avg)}")
        }

        utf8Writer.println("\nСредний балл по курсам:")
        stats.courseAvg.foreach { case (course, avg) =>
          utf8Writer.println(s"Курс $course: ${"%.2f".format(avg)}")
        }
        utf8Writer.flush()
      }
    } yield ()

  private def viewAllData(): IO[Unit] =
    for {
      allData <- repo.getAllData
      _ <- IO {
        utf8Writer.println("\nВсе данные:")
        val header = "%-15s %-25s %-35s %s".format(
          "Группа",
          "Студент (ID)",
          "Предмет",
          "Оценка"
        )
        utf8Writer.println(header)
        utf8Writer.println("-" * 85)

        allData.foreach { case (student, grades) =>
          if (grades.isEmpty) {
            val line = "%-15s %-25s %-35s %s".format(
              student.groupName,
              s"${student.name} (${student.id})",
              "-",
              "-"
            )
            utf8Writer.println(line)
          } else {
            grades.foreach { grade =>
              val line = if (grade == grades.head) {
                "%-15s %-25s %-35s %s".format(
                  student.groupName,
                  s"${student.name} (${student.id})",
                  grade.subjectName,
                  grade.value
                )
              } else {
                "%-15s %-25s %-35s %s".format(
                  "",
                  "",
                  grade.subjectName,
                  grade.value
                )
              }
              utf8Writer.println(line)
            }
          }
        }
        utf8Writer.flush()
      }
    } yield ()

  def run(): IO[Unit] = {
    val commands = Stream.iterate(0)(_ + 1).evalMap { _ =>
      for {
        _ <- printMenu()
        choice <- readInput("")
        _ <- choice match {
          case "1" => addStudent()
          case "2" => viewStudent()
          case "3" => deleteStudent()
          case "4" => addSubject()
          case "5" => addGrade()
          case "6" => viewStats()
          case "7" => viewAllData()
          case "8" => IO.raiseError(new RuntimeException("Exit"))
          case _   => printlnUTF8("Неверный выбор")
        }
      } yield ()
    }

    commands
      .handleErrorWith {
        case _: RuntimeException => Stream.empty
        case e => Stream.eval(printlnUTF8(s"Ошибка: ${e.getMessage}"))
      }
      .compile
      .drain
  }
}

// Главное приложение
object StudentAccountingSystem extends IOApp.Simple {
  // Устанавливаем правильную кодировку для консоли
  System.setProperty("file.encoding", "UTF-8")
  java.nio.charset.Charset.forName("UTF-8")

  // Инициализация тестовых данных
  val initialData: IO[(List[Student], List[Subject], List[Grade])] = IO {
    val students = List(
      Student(1, "Иван Иванов", "CS-101", 2023),
      Student(2, "Петр Петров", "CS-101", 2023),
      Student(3, "Сидор Сидоров", "CS-102", 2023)
    )

    val subjects = List(
      Subject("Функциональное программирование", 1),
      Subject("Алгоритмы и структуры данных", 1),
      Subject("Базы данных", 2)
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

  override def run: IO[Unit] =
    for {
      (students, subjects, grades) <- initialData
      repo = new InMemoryRepository(students, subjects, grades)
      studentService = new StudentService[IO](repo)
      consoleApi = new ConsoleApi(studentService, repo)
      _ <- consoleApi.run()
    } yield ()
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.