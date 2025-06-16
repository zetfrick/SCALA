package studentaccounting

import cats.effect.IO
import fs2.Stream
import java.io.{InputStreamReader, BufferedReader}
import java.nio.charset.StandardCharsets

class ConsoleApi(studentService: StudentService[IO], repo: Repository[IO]) {
  private val utf8Reader = new BufferedReader(
    new InputStreamReader(System.in, StandardCharsets.UTF_8)
  )

  private def printMenu(): IO[Unit] = IO {
    println("\nУчётная система студентов")
    println("1. Добавить студента")
    println("2. Просмотреть студента")
    println("3. Удалить студента")
    println("4. Добавить предмет")
    println("5. Добавить оценку")
    println("6. Просмотреть статистику")
    println("7. Просмотреть все данные")
    println("8. Выход")
    print("Выберите действие: ")
  }

  private def readInput(prompt: String): IO[String] = IO {
    print(prompt)
    utf8Reader.readLine()
  }

  private def readIntInput(prompt: String): IO[Int] =
    readInput(prompt).flatMap { input =>
      IO(input.toInt).handleErrorWith { _ =>
        IO.raiseError(new IllegalArgumentException("Некорректный ввод числа"))
      }
    }

  private def validateGradeValue(value: Int): IO[Unit] =
    IO.raiseWhen(value < 1 || value > 5)(
      new IllegalArgumentException("Оценка должна быть от 1 до 5")
    )

  private def ensureStudentExists(id: Int): IO[Student] =
    repo.getStudent(id).flatMap {
      case Some(student) => IO.pure(student)
      case None =>
        IO.raiseError(new NoSuchElementException("Студент не найден"))
    }

  private def ensureSubjectExists(name: String): IO[Subject] =
    repo.getSubject(name).flatMap {
      case Some(subject) => IO.pure(subject)
      case None =>
        IO.raiseError(new NoSuchElementException("Предмет не найден"))
    }

  private def ensureStudentNotExists(id: Int): IO[Unit] =
    repo.getStudent(id).flatMap {
      case Some(_) =>
        IO.raiseError(new IllegalArgumentException("Студент уже существует"))
      case None => IO.unit
    }

  private def ensureSubjectNotExists(name: String): IO[Unit] =
    repo.getSubject(name).flatMap {
      case Some(_) =>
        IO.raiseError(new IllegalArgumentException("Предмет уже существует"))
      case None => IO.unit
    }

  private def addStudent(): IO[Unit] = {
    val program = for {
      id <- readIntInput("ID студента: ")
      _ <- ensureStudentNotExists(id)
      name <- readInput("Имя студента: ")
      course <- readIntInput("Курс: ")
      groupName <- readInput("Группа: ")
      _ <- studentService.createStudent(id, name, course, groupName)
      _ <- IO.println("Студент успешно добавлен")
    } yield ()

    program.handleErrorWith {
      case e: IllegalArgumentException
          if e.getMessage == "Некорректный ввод числа" =>
        IO.println("Ошибка: некорректный ввод числа")
      case e: IllegalArgumentException
          if e.getMessage == "Студент уже существует" =>
        IO.println("Ошибка: студент с таким ID уже существует")
      case e =>
        IO.println(s"Ошибка: ${e.getMessage}")
    }
  }

  private def viewStudent(): IO[Unit] = {
    val program = for {
      id <- readIntInput("ID студента: ")
      studentOpt <- studentService.getStudentDetails(id)
      _ <- studentOpt match {
        case Some((student, grades)) =>
          IO {
            println(s"\nСтудент: ${student.name} (ID: ${student.id})")
            println(s"Курс: ${student.course}, Группа: ${student.groupName}")
            println("\nОценки:")
            grades.groupBy(_._1).foreach { case (subject, grades) =>
              println(s"$subject: ${grades.map(_._2).mkString(" ")}")
            }
          }
        case None => IO.println("Студент не найден")
      }
    } yield ()

    program.handleErrorWith {
      case _: IllegalArgumentException =>
        IO.println("Ошибка: ID студента должен быть числом")
      case e =>
        IO.println(s"Ошибка: ${e.getMessage}")
    }
  }

  private def deleteStudent(): IO[Unit] = {
    val program = for {
      id <- readIntInput("ID студента для удаления: ")
      _ <- studentService.deleteStudentWithGrades(id)
      _ <- IO.println("Студент и его оценки удалены")
    } yield ()

    program.handleErrorWith { case e =>
      IO.println(s"Ошибка: ${e.getMessage}")
    }
  }

  private def addSubject(): IO[Unit] = {
    val program = for {
      name <- readInput("Название предмета: ")
      _ <- ensureSubjectNotExists(name)
      _ <- repo.addSubject(Subject(name))
      _ <- IO.println("Предмет успешно добавлен")
    } yield ()

    program.handleErrorWith {
      case _: IllegalArgumentException =>
        IO.println("Ошибка: предмет с таким названием уже существует")
      case e =>
        IO.println(s"Ошибка: ${e.getMessage}")
    }
  }

  private def addGrade(): IO[Unit] = {
    val program = for {
      studentId <- readIntInput("ID студента: ")
      _ <- ensureStudentExists(studentId)
      subjectName <- readInput("Название предмета: ")
      _ <- ensureSubjectExists(subjectName)
      value <- readIntInput("Оценка (1-5): ")
      _ <- validateGradeValue(value)
      _ <- repo.addGrade(Grade(studentId, subjectName, value))
      _ <- IO.println("Оценка успешно добавлена")
    } yield ()

    program.handleErrorWith {
      case _: NoSuchElementException =>
        IO.println("Ошибка: студент или предмет не найден")
      case _: IllegalArgumentException =>
        IO.println("Ошибка: оценка должна быть от 1 до 5")
      case e =>
        IO.println(s"Ошибка: ${e.getMessage}")
    }
  }

  private def viewStats(): IO[Unit] = {
    val program = for {
      stats <- repo.calculateStats
      _ <- IO {
        println("\nСтатистика:")
        println("\nСредний балл по студентам:")
        stats.studentAvg.foreach { case (id, avg) =>
          println(s"Студент $id: ${"%.2f".format(avg)}")
        }

        println("\nСредний балл по группам:")
        stats.groupAvg.foreach { case (group, avg) =>
          println(s"Группа $group: ${"%.2f".format(avg)}")
        }

        println("\nСредний балл по курсам:")
        stats.courseAvg.foreach { case (course, avg) =>
          println(s"Курс $course: ${"%.2f".format(avg)}")
        }
      }
    } yield ()

    program.handleErrorWith(e =>
      IO.println(s"Ошибка при получении статистики: ${e.getMessage}")
    )
  }

  private def viewAllData(): IO[Unit] = {
    val program = for {
      allData <- repo.getAllData
      _ <- IO {
        println("\nВсе данные:")
        val header = "%-10s %-15s %-25s %-35s %s".format(
          "Курс",
          "Группа",
          "Студент (ID)",
          "Предмет",
          "Оценка"
        )
        println(header)
        println("-" * 100)

        val groupedByCourse = allData
          .groupBy { case (student, _) => student.course }
          .toList
          .sortBy(_._1)

        groupedByCourse.foreach { case (course, courseStudents) =>
          val groupedByGroup = courseStudents
            .groupBy { case (student, _) => student.groupName }
            .toList
            .sortBy(_._1)

          groupedByGroup.foreach { case (groupName, groupStudents) =>
            var isFirstStudentInGroup = true

            groupStudents.foreach { case (student, grades) =>
              val gradesBySubject = grades
                .groupBy(_.subjectName)
                .map { case (subject, gs) =>
                  (subject, gs.map(_.value).mkString(" "))
                }
                .toList
                .sortBy(_._1)

              if (gradesBySubject.isEmpty) {
                val line = "%-10s %-15s %-25s %-35s %s".format(
                  if (isFirstStudentInGroup) course.toString else "",
                  if (isFirstStudentInGroup) groupName else "",
                  s"${student.name} (${student.id})",
                  "-",
                  "-"
                )
                println(line)
                isFirstStudentInGroup = false
              } else {
                var isFirstSubject = true
                gradesBySubject.foreach { case (subjectName, gradesStr) =>
                  val line = if (isFirstStudentInGroup && isFirstSubject) {
                    "%-10s %-15s %-25s %-35s %s".format(
                      course.toString,
                      groupName,
                      s"${student.name} (${student.id})",
                      subjectName,
                      gradesStr
                    )
                  } else if (isFirstSubject) {
                    "%-10s %-15s %-25s %-35s %s".format(
                      "",
                      "",
                      s"${student.name} (${student.id})",
                      subjectName,
                      gradesStr
                    )
                  } else {
                    "%-10s %-15s %-25s %-35s %s".format(
                      "",
                      "",
                      "",
                      subjectName,
                      gradesStr
                    )
                  }
                  println(line)
                  isFirstStudentInGroup = false
                  isFirstSubject = false
                }
              }
            }
          }
        }
      }
    } yield ()

    program.handleErrorWith(e =>
      IO.println(s"Ошибка при получении данных: ${e.getMessage}")
    )
  }

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
          case _   => IO.println("Неверный выбор")
        }
      } yield ()
    }

    commands
      .handleErrorWith {
        case _: RuntimeException => Stream.empty
        case e => Stream.eval(IO.println(s"Ошибка: ${e.getMessage}"))
      }
      .compile
      .drain
  }
}
