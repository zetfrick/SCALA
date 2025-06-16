error id: file:///D:/ИРИТ/SCALA/curs_test/src/main/scala/Main.scala:`<none>`.
file:///D:/ИРИТ/SCALA/curs_test/src/main/scala/Main.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 8229
uri: file:///D:/ИРИТ/SCALA/curs_test/src/main/scala/Main.scala
text:
```scala
import cats.effect.{IO, IOApp}
import cats.implicits._
import scalafx.application.JFXApp3
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout._
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.Includes._
import scalafx.scene.text.Font
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import scala.util.Try

trait Entity
case class Student(id: Int, name: String, course: Int, groupName: String)
    extends Entity
case class Subject(name: String) extends Entity
case class Grade(studentId: Int, subjectName: String, value: Int) extends Entity

case class Stats(
    studentAvg: Map[Int, Double],
    groupAvg: Map[String, Double],
    courseAvg: Map[Int, Double]
)

trait Repository[F[_]] {
  def getAllStudents: F[List[Student]]
  def getStudent(id: Int): F[Option[Student]]
  def addStudent(student: Student): F[Unit]
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
    val studentGrades = gradeState.groupBy(_.studentId)
    val courseStudents = studentState.groupBy(_.course)
    val groupStudents = studentState.groupBy(_.groupName)

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

    val courseAvg = courseStudents.view.mapValues { students =>
      val studentIds = students.map(_.id).toSet
      val relevantGrades =
        gradeState.filter(g => studentIds.contains(g.studentId))
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

class StudentService[F[_]](repo: Repository[F])(implicit F: cats.Monad[F]) {
  def createStudent(
      id: Int,
      name: String,
      course: Int,
      groupName: String
  ): F[Unit] =
    repo.addStudent(Student(id, name, course, groupName))

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

object StudentAccountingSystem extends JFXApp3 {
  // Инициализация репозитория с тестовыми данными
  private val initialData: (List[Student], List[Subject], List[Grade]) = {
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

  private val repo = new InMemoryRepository(
    initialData._1,
    initialData._2,
    initialData._3
  )
  private val studentService = new StudentService[IO](repo)

  // ExecutionContext для асинхронных операций
  private val ioExecutionContext =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(4))

  // Основное окно приложения
  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Система учёта успеваемости студентов"
      width = 800
      height = 600
      scene = createMainScene()
    }
  }

  // Создание главной сцены
  private def createMainScene(): Scene = {
    val tabPane = new TabPane {
      tabs = Seq(
        createStudentsTab(),
        createSubjectsTab(),
        createGradesTab(),
        createStatsTab(),
        createAllDataTab()
      )
    }

    new Scene {
      root = tabPane
    }
  }

  // Вкладка для работы со студентами
  private def createStudentsTab(): Tab = {
    val idField = new TextField { promptText = "ID" }
    val nameField = new TextField { promptText = "Имя" }
    val courseField = new TextField { promptText = "Курс" }
    val groupField = new TextField { promptText = "Группа" }

    val addButton = new Button("Добавить студента") {
      onAction = _ =>
        addStudent(
          idField.text.value,
          nameField.text.value,
          courseField.text.value,
          groupField.text.value
        )
    }

    val deleteButton = new Button("Удалить студента") {
      onAction = _ => deleteStudent(idField.text.value)
    }

    val studentsTable = new TableView[Student]() {
      columns ++= List(
        new TableColumn[Student, Int]("ID") {
          cellValueFactory = _.value.id
        },
        new TableColumn[Student, String]("Имя") {
          cellValueFactory = _.value.name
        },
        new TableColumn[Student, Int]("Курс") {
          cellValueFactory = _.value.course
        },
        new TableColumn[Student, String]("Группа") {
          cellValueFactory = _.value.groupName
        }
      )
    }

    val refreshButton = ne@@w Button("Обновить список") {
      onAction = _ => refreshStudentsTable(studentsTable)
    }

    val form = new GridPane {
      hgap = 10
      vgap = 10
      padding = Insets(20)

      add(new Label("ID:"), 0, 0)
      add(idField, 1, 0)
      add(new Label("Имя:"), 0, 1)
      add(nameField, 1, 1)
      add(new Label("Курс:"), 0, 2)
      add(courseField, 1, 2)
      add(new Label("Группа:"), 0, 3)
      add(groupField, 1, 3)
      add(addButton, 0, 4)
      add(deleteButton, 1, 4)
      add(refreshButton, 0, 5)
    }

    val layout = new BorderPane {
      center = studentsTable
      bottom = form
    }

    // Загружаем данные при открытии вкладки
    refreshStudentsTable(studentsTable)

    new Tab {
      text = "Студенты"
      content = layout
    }
  }

  // Вкладка для работы с предметами
  private def createSubjectsTab(): Tab = {
    val nameField = new TextField { promptText = "Название предмета" }

    val addButton = new Button("Добавить предмет") {
      onAction = _ => addSubject(nameField.text.value)
    }

    val deleteButton = new Button("Удалить предмет") {
      onAction = _ => deleteSubject(nameField.text.value)
    }

    val subjectsTable = new TableView[Subject]() {
      columns ++= List(
        new TableColumn[Subject, String]("Название") {
          cellValueFactory = _.value.name
        }
      )
    }

    val refreshButton = new Button("Обновить список") {
      onAction = _ => refreshSubjectsTable(subjectsTable)
    }

    val form = new GridPane {
      hgap = 10
      vgap = 10
      padding = Insets(20)

      add(new Label("Название:"), 0, 0)
      add(nameField, 1, 0)
      add(addButton, 0, 1)
      add(deleteButton, 1, 1)
      add(refreshButton, 0, 2)
    }

    val layout = new BorderPane {
      center = subjectsTable
      bottom = form
    }

    // Загружаем данные при открытии вкладки
    refreshSubjectsTable(subjectsTable)

    new Tab {
      text = "Предметы"
      content = layout
    }
  }

  // Вкладка для работы с оценками
  private def createGradesTab(): Tab = {
    val studentIdField = new TextField { promptText = "ID студента" }
    val subjectField = new TextField { promptText = "Предмет" }
    val gradeField = new TextField { promptText = "Оценка (1-5)" }

    val addButton = new Button("Добавить оценку") {
      onAction = _ =>
        addGrade(
          studentIdField.text.value,
          subjectField.text.value,
          gradeField.text.value
        )
    }

    val gradesTable = new TableView[Grade]() {
      columns ++= List(
        new TableColumn[Grade, Int]("ID студента") {
          cellValueFactory = _.value.studentId
        },
        new TableColumn[Grade, String]("Предмет") {
          cellValueFactory = _.value.subjectName
        },
        new TableColumn[Grade, Int]("Оценка") {
          cellValueFactory = _.value.value
        }
      )
    }

    val refreshButton = new Button("Обновить список") {
      onAction = _ => refreshGradesTable(gradesTable)
    }

    val form = new GridPane {
      hgap = 10
      vgap = 10
      padding = Insets(20)

      add(new Label("ID студента:"), 0, 0)
      add(studentIdField, 1, 0)
      add(new Label("Предмет:"), 0, 1)
      add(subjectField, 1, 1)
      add(new Label("Оценка:"), 0, 2)
      add(gradeField, 1, 2)
      add(addButton, 0, 3)
      add(refreshButton, 0, 4)
    }

    val layout = new BorderPane {
      center = gradesTable
      bottom = form
    }

    // Загружаем данные при открытии вкладки
    refreshGradesTable(gradesTable)

    new Tab {
      text = "Оценки"
      content = layout
    }
  }

  // Вкладка для просмотра статистики
  private def createStatsTab(): Tab = {
    val statsArea = new TextArea {
      editable = false
      wrapText = true
      font = Font("Monospaced", 12)
    }

    val refreshButton = new Button("Обновить статистику") {
      onAction = _ => showStats(statsArea)
    }

    val layout = new BorderPane {
      center = statsArea
      bottom = new HBox {
        alignment = Pos.Center
        padding = Insets(10)
        children = refreshButton
      }
    }

    // Загружаем данные при открытии вкладки
    showStats(statsArea)

    new Tab {
      text = "Статистика"
      content = layout
    }
  }

  // Вкладка для просмотра всех данных
  private def createAllDataTab(): Tab = {
    val dataArea = new TextArea {
      editable = false
      wrapText = true
      font = Font("Monospaced", 12)
    }

    val refreshButton = new Button("Обновить данные") {
      onAction = _ => showAllData(dataArea)
    }

    val layout = new BorderPane {
      center = dataArea
      bottom = new HBox {
        alignment = Pos.Center
        padding = Insets(10)
        children = refreshButton
      }
    }

    // Загружаем данные при открытии вкладки
    showAllData(dataArea)

    new Tab {
      text = "Все данные"
      content = layout
    }
  }

  // Методы для работы с данными

  private def addStudent(
      idStr: String,
      name: String,
      courseStr: String,
      group: String
  ): Unit = {
    (for {
      id <- IO.fromEither(
        Try(idStr.toInt).toEither.left.map(_ => "ID должен быть числом")
      )
      course <- IO.fromEither(
        Try(courseStr.toInt).toEither.left.map(_ => "Курс должен быть числом")
      )
      _ <- studentService.createStudent(id, name, course, group)
    } yield ()).unsafeRunAsync {
      case Right(_) =>
        Platform.runLater {
          showAlert(
            "Успех",
            "Студент успешно добавлен",
            Alert.AlertType.Information
          )
        }
      case Left(e) =>
        Platform.runLater {
          showAlert("Ошибка", e.getMessage, Alert.AlertType.Error)
        }
    }(ioExecutionContext)
  }

  private def deleteStudent(idStr: String): Unit = {
    (for {
      id <- IO.fromEither(
        Try(idStr.toInt).toEither.left.map(_ => "ID должен быть числом")
      )
      _ <- studentService.deleteStudentWithGrades(id)
    } yield ()).unsafeRunAsync {
      case Right(_) =>
        Platform.runLater {
          showAlert(
            "Успех",
            "Студент и его оценки удалены",
            Alert.AlertType.Information
          )
        }
      case Left(e) =>
        Platform.runLater {
          showAlert("Ошибка", e.getMessage, Alert.AlertType.Error)
        }
    }(ioExecutionContext)
  }

  private def addSubject(name: String): Unit = {
    if (name.isEmpty) {
      showAlert(
        "Ошибка",
        "Название предмета не может быть пустым",
        Alert.AlertType.Error
      )
    } else {
      repo
        .addSubject(Subject(name))
        .unsafeRunAsync {
          case Right(_) =>
            Platform.runLater {
              showAlert(
                "Успех",
                "Предмет успешно добавлен",
                Alert.AlertType.Information
              )
            }
          case Left(e) =>
            Platform.runLater {
              showAlert("Ошибка", e.getMessage, Alert.AlertType.Error)
            }
        }(ioExecutionContext)
    }
  }

  private def deleteSubject(name: String): Unit = {
    if (name.isEmpty) {
      showAlert(
        "Ошибка",
        "Название предмета не может быть пустым",
        Alert.AlertType.Error
      )
    } else {
      repo
        .deleteSubject(name)
        .unsafeRunAsync {
          case Right(_) =>
            Platform.runLater {
              showAlert(
                "Успех",
                "Предмет успешно удален",
                Alert.AlertType.Information
              )
            }
          case Left(e) =>
            Platform.runLater {
              showAlert("Ошибка", e.getMessage, Alert.AlertType.Error)
            }
        }(ioExecutionContext)
    }
  }

  private def addGrade(
      studentIdStr: String,
      subject: String,
      gradeStr: String
  ): Unit = {
    (for {
      studentId <- IO.fromEither(
        Try(studentIdStr.toInt).toEither.left.map(_ =>
          "ID студента должен быть числом"
        )
      )
      grade <- IO.fromEither(
        Try(gradeStr.toInt).toEither.left.map(_ => "Оценка должна быть числом")
      )
      _ <- IO.raiseWhen(grade < 1 || grade > 5)(
        new IllegalArgumentException("Оценка должна быть от 1 до 5")
      )
      _ <- repo.addGrade(Grade(studentId, subject, grade))
    } yield ()).unsafeRunAsync {
      case Right(_) =>
        Platform.runLater {
          showAlert(
            "Успех",
            "Оценка успешно добавлена",
            Alert.AlertType.Information
          )
        }
      case Left(e) =>
        Platform.runLater {
          showAlert("Ошибка", e.getMessage, Alert.AlertType.Error)
        }
    }(ioExecutionContext)
  }

  private def refreshStudentsTable(table: TableView[Student]): Unit = {
    repo.getAllStudents.unsafeRunAsync {
      case Right(students) =>
        Platform.runLater {
          table.items = ObservableBuffer(students)
        }
      case Left(e) =>
        Platform.runLater {
          showAlert(
            "Ошибка",
            s"Не удалось загрузить студентов: ${e.getMessage}",
            Alert.AlertType.Error
          )
        }
    }(ioExecutionContext)
  }

  private def refreshSubjectsTable(table: TableView[Subject]): Unit = {
    repo.getAllSubjects.unsafeRunAsync {
      case Right(subjects) =>
        Platform.runLater {
          table.items = ObservableBuffer(subjects)
        }
      case Left(e) =>
        Platform.runLater {
          showAlert(
            "Ошибка",
            s"Не удалось загрузить предметы: ${e.getMessage}",
            Alert.AlertType.Error
          )
        }
    }(ioExecutionContext)
  }

  private def refreshGradesTable(table: TableView[Grade]): Unit = {
    repo.getAllGrades.unsafeRunAsync {
      case Right(grades) =>
        Platform.runLater {
          table.items = ObservableBuffer(grades)
        }
      case Left(e) =>
        Platform.runLater {
          showAlert(
            "Ошибка",
            s"Не удалось загрузить оценки: ${e.getMessage}",
            Alert.AlertType.Error
          )
        }
    }(ioExecutionContext)
  }

  private def showStats(textArea: TextArea): Unit = {
    repo.calculateStats.unsafeRunAsync {
      case Right(stats) =>
        Platform.runLater {
          val sb = new StringBuilder
          sb.append("=== Статистика ===\n\n")

          sb.append("Средний балл по студентам:\n")
          stats.studentAvg.foreach { case (id, avg) =>
            sb.append(s"Студент $id: ${"%.2f".format(avg)}\n")
          }

          sb.append("\nСредний балл по группам:\n")
          stats.groupAvg.foreach { case (group, avg) =>
            sb.append(s"Группа $group: ${"%.2f".format(avg)}\n")
          }

          sb.append("\nСредний балл по курсам:\n")
          stats.courseAvg.foreach { case (course, avg) =>
            sb.append(s"Курс $course: ${"%.2f".format(avg)}\n")
          }

          textArea.text = sb.toString()
        }
      case Left(e) =>
        Platform.runLater {
          showAlert(
            "Ошибка",
            s"Не удалось загрузить статистику: ${e.getMessage}",
            Alert.AlertType.Error
          )
        }
    }(ioExecutionContext)
  }

  private def showAllData(textArea: TextArea): Unit = {
    repo.getAllData.unsafeRunAsync {
      case Right(allData) =>
        Platform.runLater {
          val sb = new StringBuilder
          sb.append("=== Все данные ===\n\n")

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
                sb.append(s"    ${student.name} (ID: ${student.id}):\n")

                if (grades.isEmpty) {
                  sb.append("      Нет оценок\n")
                } else {
                  grades.groupBy(_.subjectName).foreach {
                    case (subject, subjectGrades) =>
                      sb.append(
                        s"      $subject: ${subjectGrades.map(_.value).mkString(", ")}\n"
                      )
                  }
                }
              }
            }
          }

          textArea.text = sb.toString()
        }
      case Left(e) =>
        Platform.runLater {
          showAlert(
            "Ошибка",
            s"Не удалось загрузить данные: ${e.getMessage}",
            Alert.AlertType.Error
          )
        }
    }(ioExecutionContext)
  }

  private def showAlert(
      title: String,
      message: String,
      alertType: Alert.AlertType
  ): Unit = {
    new Alert(alertType) {
      this.title = title
      headerText = title
      contentText = message
    }.showAndWait()
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.