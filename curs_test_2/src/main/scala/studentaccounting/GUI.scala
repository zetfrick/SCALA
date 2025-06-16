package studentaccounting

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.scene.text.Font

// Основной класс графического интерфейса
class GUI(studentService: StudentService[IO], repo: Repository[IO])
    extends JFXApp3 {
  private val errorLabel = new Label {
    style = "-fx-text-fill: red;"
    wrapText = true
    maxWidth = 300
  }

  private val errorHandler = new ErrorHandler(errorLabel)

  // Создание поля формы
  private def createFormField(
      labelText: String,
      field: TextField,
      grid: GridPane,
      row: Int
  ): Unit = {
    grid.add(new Label(labelText), 0, row)
    grid.add(field, 1, row)
  }

  // Создание кнопки
  private def createButton(buttonText: String, action: => Unit): Button = {
    new Button(buttonText) {
      onAction = _ => action
    }
  }

  // Создание области результата
  private def createResultArea(): TextArea = {
    new TextArea {
      editable = false
      wrapText = true
    }
  }

  // Вкладка для добавления студента
  private def addStudentTab(): Tab = {
    val idField = new TextField()
    val nameField = new TextField()
    val courseField = new TextField()
    val groupField = new TextField()

    val addStudent = () => {
      errorHandler.clearError()
      val program = for {
        _ <- errorHandler.validateFields(
          (idField.text.value, "ID студента"),
          (nameField.text.value, "Имя студента"),
          (courseField.text.value, "Курс"),
          (groupField.text.value, "Группа")
        )
        id <- errorHandler.validateIntField(idField.text.value, "ID студента")
        course <- errorHandler.validateIntField(courseField.text.value, "Курс")
        name = nameField.text.value.trim
        group = groupField.text.value.trim
        _ <- repo.getStudent(id).flatMap {
          case Some(_) =>
            IO.raiseError(
              new IllegalArgumentException("Студент с таким ID уже существует")
            )
          case None => studentService.createStudent(id, name, course, group)
        }
        _ <- IO {
          idField.text = ""
          nameField.text = ""
          courseField.text = ""
          groupField.text = ""
          errorHandler.showSuccess("Студент успешно добавлен")
        }
      } yield ()

      errorHandler.handleIOError(program).unsafeRunAsync(_ => ())
    }

    val grid = new GridPane {
      hgap = 10
      vgap = 10
      padding = Insets(20)
    }

    createFormField("ID студента:", idField, grid, 0)
    createFormField("Имя студента:", nameField, grid, 1)
    createFormField("Курс:", courseField, grid, 2)
    createFormField("Группа:", groupField, grid, 3)
    grid.add(createButton("Добавить студента", addStudent()), 0, 4, 2, 1)

    new Tab {
      text = "Добавить студента"
      content = grid
    }
  }

  // Вкладка для просмотра студента
  private def viewStudentTab(): Tab = {
    val idField = new TextField()
    val resultArea = createResultArea()

    val viewStudent = () => {
      errorHandler.clearError()
      val program = for {
        _ <- errorHandler.validateFields((idField.text.value, "ID студента"))
        id <- errorHandler.validateIntField(idField.text.value, "ID студента")
        studentOpt <- studentService.getStudentDetails(id)
        _ <- studentOpt match {
          case Some((student, grades)) =>
            IO {
              val sb = new StringBuilder
              sb.append(s"Студент: ${student.name} (ID: ${student.id})\n")
              sb.append(
                s"Курс: ${student.course}, Группа: ${student.groupName}\n\n"
              )
              if (grades.nonEmpty) {
                sb.append("Оценки:\n")
                grades.groupBy(_._1).foreach { case (subject, subjectGrades) =>
                  sb.append(
                    s"$subject: ${subjectGrades.map(_._2).mkString(" ")}\n"
                  )
                }
              } else {
                sb.append("Оценки: нет\n")
              }
              resultArea.text = sb.toString()
            }
          case None =>
            IO {
              resultArea.text = ""
              errorHandler.showError("Студент не найден")
            }
        }
      } yield ()

      errorHandler.handleIOError(program).unsafeRunAsync(_ => ())
    }

    val grid = new GridPane {
      hgap = 10
      vgap = 10
      padding = Insets(20)
    }

    createFormField("ID студента:", idField, grid, 0)
    grid.add(createButton("Просмотреть студента", viewStudent()), 0, 1, 2, 1)
    grid.add(new Label("Результат:"), 0, 2)
    grid.add(resultArea, 0, 3, 2, 1)

    new Tab {
      text = "Просмотреть студента"
      content = grid
    }
  }

  // Вкладка для удаления студента
  private def deleteStudentTab(): Tab = {
    val idField = new TextField()

    val deleteStudent = () => {
      errorHandler.clearError()
      val program = for {
        _ <- errorHandler.validateFields((idField.text.value, "ID студента"))
        id <- errorHandler.validateIntField(idField.text.value, "ID студента")
        exists <- repo.getStudent(id).map(_.isDefined)
        _ <-
          if (!exists) {
            IO.raiseError(new NoSuchElementException("Студент не найден"))
          } else {
            studentService.deleteStudentWithGrades(id)
          }
        _ <- IO {
          idField.text = ""
          errorHandler.showSuccess("Студент и его оценки успешно удалены")
        }
      } yield ()

      errorHandler.handleIOError(program).unsafeRunAsync(_ => ())
    }

    val grid = new GridPane {
      hgap = 10
      vgap = 10
      padding = Insets(20)
    }

    createFormField("ID студента:", idField, grid, 0)
    grid.add(createButton("Удалить студента", deleteStudent()), 0, 1, 2, 1)

    new Tab {
      text = "Удалить студента"
      content = grid
    }
  }

  // Вкладка для добавления предмета
  private def addSubjectTab(): Tab = {
    val nameField = new TextField()

    val addSubject = () => {
      errorHandler.clearError()
      val program = for {
        _ <- errorHandler.validateFields(
          (nameField.text.value, "Название предмета")
        )
        name = nameField.text.value.trim
        exists <- repo.getSubject(name).map(_.isDefined)
        _ <-
          if (exists) {
            IO.raiseError(
              new IllegalArgumentException(
                "Предмет с таким названием уже существует"
              )
            )
          } else {
            repo.addSubject(Subject(name))
          }
        _ <- IO {
          nameField.text = ""
          errorHandler.showSuccess("Предмет успешно добавлен")
        }
      } yield ()

      errorHandler.handleIOError(program).unsafeRunAsync(_ => ())
    }

    val grid = new GridPane {
      hgap = 10
      vgap = 10
      padding = Insets(20)
    }

    createFormField("Название предмета:", nameField, grid, 0)
    grid.add(createButton("Добавить предмет", addSubject()), 0, 1, 2, 1)

    new Tab {
      text = "Добавить предмет"
      content = grid
    }
  }

  // Вкладка для добавления оценки
  private def addGradeTab(): Tab = {
    val studentIdField = new TextField()
    val subjectField = new TextField()
    val gradeField = new TextField()

    val addGrade = () => {
      errorHandler.clearError()
      val program = for {
        _ <- errorHandler.validateFields(
          (studentIdField.text.value, "ID студента"),
          (subjectField.text.value, "Название предмета"),
          (gradeField.text.value, "Оценка")
        )
        studentId <- errorHandler.validateIntField(
          studentIdField.text.value,
          "ID студента"
        )
        gradeValue <- errorHandler.validateIntField(
          gradeField.text.value,
          "Оценка"
        )
        _ <- errorHandler.validateGradeValue(gradeValue)
        subjectName = subjectField.text.value.trim
        studentExists <- repo.getStudent(studentId).map(_.isDefined)
        _ <-
          if (!studentExists) {
            IO.raiseError(new NoSuchElementException("Студент не найден"))
          } else IO.unit
        subjectExists <- repo.getSubject(subjectName).map(_.isDefined)
        _ <-
          if (!subjectExists) {
            IO.raiseError(new NoSuchElementException("Предмет не найден"))
          } else IO.unit
        _ <- repo.addGrade(Grade(studentId, subjectName, gradeValue))
        _ <- IO {
          studentIdField.text = ""
          subjectField.text = ""
          gradeField.text = ""
          errorHandler.showSuccess("Оценка успешно добавлена")
        }
      } yield ()

      errorHandler.handleIOError(program).unsafeRunAsync(_ => ())
    }

    val grid = new GridPane {
      hgap = 10
      vgap = 10
      padding = Insets(20)
    }

    createFormField("ID студента:", studentIdField, grid, 0)
    createFormField("Название предмета:", subjectField, grid, 1)
    createFormField("Оценка (1-5):", gradeField, grid, 2)
    grid.add(createButton("Добавить оценку", addGrade()), 0, 3, 2, 1)

    new Tab {
      text = "Добавить оценку"
      content = grid
    }
  }

  // Вкладка для просмотра статистики
  private def viewStatsTab(): Tab = {
    val resultArea = createResultArea()

    val viewStats = () => {
      errorHandler.clearError()
      val program = for {
        stats <- repo.calculateStats
        _ <- IO {
          val sb = new StringBuilder
          sb.append("Статистика:\n\n")

          if (stats.studentAvg.nonEmpty) {
            sb.append("Средний балл по студентам:\n")
            stats.studentAvg.foreach { case (id, avg) =>
              sb.append(s"Студент $id: ${"%.2f".format(avg)}\n")
            }
          } else {
            sb.append("Нет данных о студентах\n")
          }

          if (stats.groupAvg.nonEmpty) {
            sb.append("\nСредний балл по группам:\n")
            stats.groupAvg.foreach { case (group, avg) =>
              sb.append(s"Группа $group: ${"%.2f".format(avg)}\n")
            }
          } else {
            sb.append("\nНет данных по группам\n")
          }

          if (stats.courseAvg.nonEmpty) {
            sb.append("\nСредний балл по курсам:\n")
            stats.courseAvg.foreach { case (course, avg) =>
              sb.append(s"Курс $course: ${"%.2f".format(avg)}\n")
            }
          } else {
            sb.append("\nНет данных по курсам\n")
          }

          resultArea.text = sb.toString()
        }
      } yield ()

      errorHandler.handleIOError(program).unsafeRunAsync(_ => ())
    }

    val grid = new GridPane {
      hgap = 10
      vgap = 10
      padding = Insets(20)
    }

    grid.add(createButton("Просмотреть статистику", viewStats()), 0, 0)
    grid.add(new Label("Результат:"), 0, 1)
    grid.add(resultArea, 0, 2)

    new Tab {
      text = "Просмотреть статистику"
      content = grid
    }
  }

  // Вкладка для просмотра всех данных
  private def viewAllDataTab(): Tab = {
    val resultArea = createResultArea()

    val viewAllData = () => {
      errorHandler.clearError()
      val program = for {
        allData <- repo.getAllData
        _ <- IO {
          if (allData.isEmpty) {
            resultArea.text = "Нет данных о студентах"
          } else {
            val sb = new StringBuilder
            sb.append("Все данные:\n\n")

            val groupedByCourse = allData
              .groupBy { case (student, _) => student.course }
              .toList
              .sortBy(_._1)

            groupedByCourse.foreach { case (course, courseStudents) =>
              sb.append(s"Курс $course:\n")

              val groupedByGroup = courseStudents
                .groupBy { case (student, _) => student.groupName }
                .toList
                .sortBy(_._1)

              groupedByGroup.foreach { case (groupName, groupStudents) =>
                sb.append(s"  Группа $groupName:\n")

                groupStudents.foreach { case (student, grades) =>
                  sb.append(
                    s"    Студент: ${student.name} (ID: ${student.id})\n"
                  )

                  if (grades.nonEmpty) {
                    sb.append("    Оценки:\n")
                    grades.groupBy(_.subjectName).foreach {
                      case (subject, subjectGrades) =>
                        sb.append(
                          s"      $subject: ${subjectGrades.map(_.value).mkString(" ")}\n"
                        )
                    }
                  } else {
                    sb.append("    Оценки: нет\n")
                  }
                }
              }
            }
            resultArea.text = sb.toString()
          }
        }
      } yield ()

      errorHandler.handleIOError(program).unsafeRunAsync(_ => ())
    }

    val grid = new GridPane {
      hgap = 10
      vgap = 10
      padding = Insets(20)
    }

    grid.add(createButton("Просмотреть все данные", viewAllData()), 0, 0)
    grid.add(new Label("Результат:"), 0, 1)
    grid.add(resultArea, 0, 2)

    new Tab {
      text = "Просмотреть все данные"
      content = grid
    }
  }

  // Запуск приложения
  override def start(): Unit = {
    stage = new PrimaryStage {
      title = "Учётная система студентов"
      scene = new Scene(1300, 600) {
        val tabPane = new TabPane {
          tabs = Seq(
            addStudentTab(),
            viewStudentTab(),
            deleteStudentTab(),
            addSubjectTab(),
            addGradeTab(),
            viewStatsTab(),
            viewAllDataTab()
          )
        }

        val mainLayout = new HBox {
          spacing = 10
          padding = Insets(10)
          children = Seq(
            tabPane,
            new VBox {
              spacing = 10
              padding = Insets(10)
              alignment = Pos.TopCenter
              children = Seq(
                new Label("Сообщения:") {
                  font = Font.font(14)
                },
                errorLabel
              )
            }
          )
        }

        root = mainLayout
      }
    }
  }
}
