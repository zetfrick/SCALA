file:///D:/ИРИТ/SCALA/curs_test_2/src/main/scala/studentaccounting/GUI.scala
### java.lang.AssertionError: assertion failed

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
uri: file:///D:/ИРИТ/SCALA/curs_test_2/src/main/scala/studentaccounting/GUI.scala
text:
```scala
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

class GUI(studentService: StudentService[IO], repo: Repository[IO])
    extends JFXApp3 {
  private val errorLabel = new Label {
    style = "-fx-text-fill: red;"
    wrapText = true
    maxWidth = 300
  }

  private val errorHandler = new ErrorHandler(errorLabel)

  private def createFormField(
      labelText: String,
      field: TextField,
      grid: GridPane,
      row: Int
  ): Unit = {
    grid.add(new Label(labelText), 0, row)
    grid.add(field, 1, row)
  }

  private def createButton(buttonText: String, action: => Unit): Button = {
    new Button(buttonText) {
      onAction = _ => action
    }
  }

  private def createResultArea(): TextArea = {
    new TextArea {
      editable = false
      wrapText = true
    }
  }

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

```



#### Error stacktrace:

```
scala.runtime.Scala3RunTime$.assertFailed(Scala3RunTime.scala:11)
	dotty.tools.dotc.core.TypeOps$.dominators$1(TypeOps.scala:241)
	dotty.tools.dotc.core.TypeOps$.approximateOr$1(TypeOps.scala:377)
	dotty.tools.dotc.core.TypeOps$.orDominator(TypeOps.scala:395)
	dotty.tools.dotc.core.TypeOps$.approximateOr$1(TypeOps.scala:358)
	dotty.tools.dotc.core.TypeOps$.orDominator(TypeOps.scala:395)
	dotty.tools.dotc.core.Types$OrType.join(Types.scala:3663)
	dotty.tools.dotc.core.Types$Type.classSymbol(Types.scala:602)
	dotty.tools.dotc.typer.Applications.targetClass$1(Applications.scala:2496)
	dotty.tools.dotc.typer.Applications.harmonizeWith(Applications.scala:2503)
	dotty.tools.dotc.typer.Applications.harmonize(Applications.scala:2530)
	dotty.tools.dotc.typer.Applications.harmonize$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.harmonize(Typer.scala:145)
	dotty.tools.dotc.typer.Typer.$anonfun$38(Typer.scala:2087)
	dotty.tools.dotc.typer.Applications.harmonic(Applications.scala:2553)
	dotty.tools.dotc.typer.Applications.harmonic$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.harmonic(Typer.scala:145)
	dotty.tools.dotc.typer.Typer.typedMatchFinish(Typer.scala:2087)
	dotty.tools.dotc.typer.Typer.typedMatch(Typer.scala:2016)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3407)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3669)
	dotty.tools.dotc.typer.Namer.typedAheadExpr$$anonfun$1(Namer.scala:1747)
	dotty.tools.dotc.typer.Namer.typedAhead(Namer.scala:1737)
	dotty.tools.dotc.typer.Namer.typedAheadExpr(Namer.scala:1747)
	dotty.tools.dotc.typer.Namer.valOrDefDefSig(Namer.scala:1803)
	dotty.tools.dotc.typer.Namer.defDefSig(Namer.scala:1959)
	dotty.tools.dotc.typer.Namer$Completer.typeSig(Namer.scala:827)
	dotty.tools.dotc.typer.Namer$Completer.completeInCreationContext(Namer.scala:974)
	dotty.tools.dotc.typer.Namer$Completer.complete(Namer.scala:850)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.completeFrom(SymDenotations.scala:175)
	dotty.tools.dotc.core.Denotations$Denotation.completeInfo$1(Denotations.scala:190)
	dotty.tools.dotc.core.Denotations$Denotation.info(Denotations.scala:192)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.ensureCompleted(SymDenotations.scala:393)
	dotty.tools.dotc.typer.Typer.retrieveSym(Typer.scala:3339)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3364)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3476)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3580)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3626)
	dotty.tools.dotc.typer.Typer.typedBlockStats(Typer.scala:1377)
	dotty.tools.dotc.typer.Typer.typedBlock(Typer.scala:1381)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3400)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedFunctionValue(Typer.scala:1861)
	dotty.tools.dotc.typer.Typer.typedFunction(Typer.scala:1600)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3402)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedMatch(Typer.scala:1970)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3407)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.ProtoTypes$FunProto.$anonfun$7(ProtoTypes.scala:512)
	dotty.tools.dotc.typer.ProtoTypes$FunProto.cacheTypedArg(ProtoTypes.scala:435)
	dotty.tools.dotc.typer.ProtoTypes$FunProto.typedArg(ProtoTypes.scala:513)
	dotty.tools.dotc.typer.Applications$ApplyToUntyped.typedArg(Applications.scala:996)
	dotty.tools.dotc.typer.Applications$ApplyToUntyped.typedArg(Applications.scala:996)
	dotty.tools.dotc.typer.Applications$Application.addTyped$1(Applications.scala:688)
	dotty.tools.dotc.typer.Applications$Application.matchArgs(Applications.scala:752)
	dotty.tools.dotc.typer.Applications$Application.init(Applications.scala:574)
	dotty.tools.dotc.typer.Applications$TypedApply.<init>(Applications.scala:878)
	dotty.tools.dotc.typer.Applications$ApplyToUntyped.<init>(Applications.scala:995)
	dotty.tools.dotc.typer.Applications.ApplyTo(Applications.scala:1257)
	dotty.tools.dotc.typer.Applications.ApplyTo$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.ApplyTo(Typer.scala:145)
	dotty.tools.dotc.typer.Applications.simpleApply$1(Applications.scala:1068)
	dotty.tools.dotc.typer.Applications.realApply$1$$anonfun$2(Applications.scala:1178)
	dotty.tools.dotc.typer.Typer$.tryEither(Typer.scala:118)
	dotty.tools.dotc.typer.Applications.realApply$1(Applications.scala:1193)
	dotty.tools.dotc.typer.Applications.typedApply(Applications.scala:1231)
	dotty.tools.dotc.typer.Applications.typedApply$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.typedApply(Typer.scala:145)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3392)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3669)
	dotty.tools.dotc.typer.Typer.typedBlock(Typer.scala:1384)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3400)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3669)
	dotty.tools.dotc.typer.Typer.caseRest$1(Typer.scala:2153)
	dotty.tools.dotc.typer.Typer.typedCase(Typer.scala:2169)
	dotty.tools.dotc.typer.Typer.typedCases$$anonfun$1(Typer.scala:2097)
	dotty.tools.dotc.core.Decorators$.loop$1(Decorators.scala:99)
	dotty.tools.dotc.core.Decorators$.mapconserve(Decorators.scala:115)
	dotty.tools.dotc.typer.Typer.typedCases(Typer.scala:2096)
	dotty.tools.dotc.typer.Typer.$anonfun$39(Typer.scala:2087)
	dotty.tools.dotc.typer.Applications.harmonic(Applications.scala:2552)
	dotty.tools.dotc.typer.Applications.harmonic$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.harmonic(Typer.scala:145)
	dotty.tools.dotc.typer.Typer.typedMatchFinish(Typer.scala:2087)
	dotty.tools.dotc.typer.Typer.typedMatch(Typer.scala:2016)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3407)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3669)
	dotty.tools.dotc.typer.Namer.typedAheadExpr$$anonfun$1(Namer.scala:1747)
	dotty.tools.dotc.typer.Namer.typedAhead(Namer.scala:1737)
	dotty.tools.dotc.typer.Namer.typedAheadExpr(Namer.scala:1747)
	dotty.tools.dotc.typer.Namer.valOrDefDefSig(Namer.scala:1803)
	dotty.tools.dotc.typer.Namer.defDefSig(Namer.scala:1959)
	dotty.tools.dotc.typer.Namer$Completer.typeSig(Namer.scala:827)
	dotty.tools.dotc.typer.Namer$Completer.completeInCreationContext(Namer.scala:974)
	dotty.tools.dotc.typer.Namer$Completer.complete(Namer.scala:850)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.completeFrom(SymDenotations.scala:175)
	dotty.tools.dotc.core.Denotations$Denotation.completeInfo$1(Denotations.scala:190)
	dotty.tools.dotc.core.Denotations$Denotation.info(Denotations.scala:192)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.ensureCompleted(SymDenotations.scala:393)
	dotty.tools.dotc.typer.Typer.retrieveSym(Typer.scala:3339)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3364)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3476)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3580)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3626)
	dotty.tools.dotc.typer.Typer.typedBlockStats(Typer.scala:1377)
	dotty.tools.dotc.typer.Typer.typedBlock(Typer.scala:1381)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3400)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedFunctionValue(Typer.scala:1861)
	dotty.tools.dotc.typer.Typer.typedFunction(Typer.scala:1600)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3402)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedMatch(Typer.scala:1970)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3407)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.ProtoTypes$FunProto.$anonfun$7(ProtoTypes.scala:512)
	dotty.tools.dotc.typer.ProtoTypes$FunProto.cacheTypedArg(ProtoTypes.scala:435)
	dotty.tools.dotc.typer.ProtoTypes$FunProto.typedArg(ProtoTypes.scala:513)
	dotty.tools.dotc.typer.Applications$ApplyToUntyped.typedArg(Applications.scala:996)
	dotty.tools.dotc.typer.Applications$ApplyToUntyped.typedArg(Applications.scala:996)
	dotty.tools.dotc.typer.Applications$Application.addTyped$1(Applications.scala:688)
	dotty.tools.dotc.typer.Applications$Application.matchArgs(Applications.scala:752)
	dotty.tools.dotc.typer.Applications$Application.init(Applications.scala:574)
	dotty.tools.dotc.typer.Applications$TypedApply.<init>(Applications.scala:878)
	dotty.tools.dotc.typer.Applications$ApplyToUntyped.<init>(Applications.scala:995)
	dotty.tools.dotc.typer.Applications.ApplyTo(Applications.scala:1257)
	dotty.tools.dotc.typer.Applications.ApplyTo$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.ApplyTo(Typer.scala:145)
	dotty.tools.dotc.typer.Applications.simpleApply$1(Applications.scala:1068)
	dotty.tools.dotc.typer.Applications.realApply$1$$anonfun$2(Applications.scala:1178)
	dotty.tools.dotc.typer.Typer$.tryEither(Typer.scala:118)
	dotty.tools.dotc.typer.Applications.realApply$1(Applications.scala:1193)
	dotty.tools.dotc.typer.Applications.typedApply(Applications.scala:1231)
	dotty.tools.dotc.typer.Applications.typedApply$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.typedApply(Typer.scala:145)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3392)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3669)
	dotty.tools.dotc.typer.Typer.typedBlock(Typer.scala:1384)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3400)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3669)
	dotty.tools.dotc.typer.Typer.caseRest$1(Typer.scala:2153)
	dotty.tools.dotc.typer.Typer.typedCase(Typer.scala:2169)
	dotty.tools.dotc.typer.Typer.typedCases$$anonfun$1(Typer.scala:2097)
	dotty.tools.dotc.core.Decorators$.loop$1(Decorators.scala:99)
	dotty.tools.dotc.core.Decorators$.mapconserve(Decorators.scala:115)
	dotty.tools.dotc.typer.Typer.typedCases(Typer.scala:2096)
	dotty.tools.dotc.typer.Typer.$anonfun$39(Typer.scala:2087)
	dotty.tools.dotc.typer.Applications.harmonic(Applications.scala:2552)
	dotty.tools.dotc.typer.Applications.harmonic$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.harmonic(Typer.scala:145)
	dotty.tools.dotc.typer.Typer.typedMatchFinish(Typer.scala:2087)
	dotty.tools.dotc.typer.Typer.typedMatch(Typer.scala:2016)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3407)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3669)
	dotty.tools.dotc.typer.Namer.typedAheadExpr$$anonfun$1(Namer.scala:1747)
	dotty.tools.dotc.typer.Namer.typedAhead(Namer.scala:1737)
	dotty.tools.dotc.typer.Namer.typedAheadExpr(Namer.scala:1747)
	dotty.tools.dotc.typer.Namer.valOrDefDefSig(Namer.scala:1803)
	dotty.tools.dotc.typer.Namer.defDefSig(Namer.scala:1959)
	dotty.tools.dotc.typer.Namer$Completer.typeSig(Namer.scala:827)
	dotty.tools.dotc.typer.Namer$Completer.completeInCreationContext(Namer.scala:974)
	dotty.tools.dotc.typer.Namer$Completer.complete(Namer.scala:850)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.completeFrom(SymDenotations.scala:175)
	dotty.tools.dotc.core.Denotations$Denotation.completeInfo$1(Denotations.scala:190)
	dotty.tools.dotc.core.Denotations$Denotation.info(Denotations.scala:192)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.ensureCompleted(SymDenotations.scala:393)
	dotty.tools.dotc.typer.Typer.retrieveSym(Typer.scala:3339)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3364)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3476)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3580)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3626)
	dotty.tools.dotc.typer.Typer.typedBlockStats(Typer.scala:1377)
	dotty.tools.dotc.typer.Typer.typedBlock(Typer.scala:1381)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3400)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedFunctionValue(Typer.scala:1861)
	dotty.tools.dotc.typer.Typer.typedFunction(Typer.scala:1600)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3402)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedMatch(Typer.scala:1970)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3407)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.ProtoTypes$FunProto.$anonfun$7(ProtoTypes.scala:512)
	dotty.tools.dotc.typer.ProtoTypes$FunProto.cacheTypedArg(ProtoTypes.scala:435)
	dotty.tools.dotc.typer.ProtoTypes$FunProto.typedArg(ProtoTypes.scala:513)
	dotty.tools.dotc.typer.Applications$ApplyToUntyped.typedArg(Applications.scala:996)
	dotty.tools.dotc.typer.Applications$ApplyToUntyped.typedArg(Applications.scala:996)
	dotty.tools.dotc.typer.Applications$Application.addTyped$1(Applications.scala:688)
	dotty.tools.dotc.typer.Applications$Application.matchArgs(Applications.scala:752)
	dotty.tools.dotc.typer.Applications$Application.init(Applications.scala:574)
	dotty.tools.dotc.typer.Applications$TypedApply.<init>(Applications.scala:878)
	dotty.tools.dotc.typer.Applications$ApplyToUntyped.<init>(Applications.scala:995)
	dotty.tools.dotc.typer.Applications.ApplyTo(Applications.scala:1257)
	dotty.tools.dotc.typer.Applications.ApplyTo$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.ApplyTo(Typer.scala:145)
	dotty.tools.dotc.typer.Applications.simpleApply$1(Applications.scala:1068)
	dotty.tools.dotc.typer.Applications.realApply$1$$anonfun$2(Applications.scala:1178)
	dotty.tools.dotc.typer.Typer$.tryEither(Typer.scala:118)
	dotty.tools.dotc.typer.Applications.realApply$1(Applications.scala:1193)
	dotty.tools.dotc.typer.Applications.typedApply(Applications.scala:1231)
	dotty.tools.dotc.typer.Applications.typedApply$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.typedApply(Typer.scala:145)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3392)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3607)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3626)
	dotty.tools.dotc.typer.Typer.typedBlockStats(Typer.scala:1377)
	dotty.tools.dotc.typer.Typer.typedBlock(Typer.scala:1381)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3400)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.$anonfun$16(Typer.scala:1479)
	dotty.tools.dotc.typer.Applications.harmonic(Applications.scala:2552)
	dotty.tools.dotc.typer.Applications.harmonic$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.harmonic(Typer.scala:145)
	dotty.tools.dotc.typer.Typer.typedIf(Typer.scala:1481)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3401)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3669)
	dotty.tools.dotc.typer.Typer.typedBlock(Typer.scala:1384)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3400)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.ProtoTypes$FunProto.$anonfun$7(ProtoTypes.scala:512)
	dotty.tools.dotc.typer.ProtoTypes$FunProto.cacheTypedArg(ProtoTypes.scala:435)
	dotty.tools.dotc.typer.ProtoTypes$FunProto.typedArg(ProtoTypes.scala:513)
	dotty.tools.dotc.typer.Applications$ApplyToUntyped.typedArg(Applications.scala:996)
	dotty.tools.dotc.typer.Applications$ApplyToUntyped.typedArg(Applications.scala:996)
	dotty.tools.dotc.typer.Applications$Application.addTyped$1(Applications.scala:688)
	dotty.tools.dotc.typer.Applications$Application.matchArgs(Applications.scala:752)
	dotty.tools.dotc.typer.Applications$Application.init(Applications.scala:574)
	dotty.tools.dotc.typer.Applications$TypedApply.<init>(Applications.scala:878)
	dotty.tools.dotc.typer.Applications$ApplyToUntyped.<init>(Applications.scala:995)
	dotty.tools.dotc.typer.Applications.ApplyTo(Applications.scala:1257)
	dotty.tools.dotc.typer.Applications.ApplyTo$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.ApplyTo(Typer.scala:145)
	dotty.tools.dotc.typer.Applications.simpleApply$1(Applications.scala:1068)
	dotty.tools.dotc.typer.Applications.realApply$1$$anonfun$2(Applications.scala:1178)
	dotty.tools.dotc.typer.Typer$.tryEither(Typer.scala:118)
	dotty.tools.dotc.typer.Applications.realApply$1(Applications.scala:1193)
	dotty.tools.dotc.typer.Applications.typedApply(Applications.scala:1231)
	dotty.tools.dotc.typer.Applications.typedApply$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.typedApply(Typer.scala:145)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3392)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3669)
	dotty.tools.dotc.typer.Typer.typeSelectOnTerm$1(Typer.scala:936)
	dotty.tools.dotc.typer.Typer.typedSelect(Typer.scala:978)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3367)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3476)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3669)
	dotty.tools.dotc.typer.Applications.realApply$1(Applications.scala:1040)
	dotty.tools.dotc.typer.Applications.typedApply(Applications.scala:1231)
	dotty.tools.dotc.typer.Applications.typedApply$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.typedApply(Typer.scala:145)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3392)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3669)
	dotty.tools.dotc.typer.Namer.typedAheadExpr$$anonfun$1(Namer.scala:1747)
	dotty.tools.dotc.typer.Namer.typedAhead(Namer.scala:1737)
	dotty.tools.dotc.typer.Namer.typedAheadExpr(Namer.scala:1747)
	dotty.tools.dotc.typer.Namer.valOrDefDefSig(Namer.scala:1803)
	dotty.tools.dotc.typer.Namer.defDefSig(Namer.scala:1959)
	dotty.tools.dotc.typer.Namer$Completer.typeSig(Namer.scala:827)
	dotty.tools.dotc.typer.Namer$Completer.completeInCreationContext(Namer.scala:974)
	dotty.tools.dotc.typer.Namer$Completer.complete(Namer.scala:850)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.completeFrom(SymDenotations.scala:175)
	dotty.tools.dotc.core.Denotations$Denotation.completeInfo$1(Denotations.scala:190)
	dotty.tools.dotc.core.Denotations$Denotation.info(Denotations.scala:192)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.ensureCompleted(SymDenotations.scala:393)
	dotty.tools.dotc.typer.Typer.retrieveSym(Typer.scala:3339)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3364)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3476)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3580)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3626)
	dotty.tools.dotc.typer.Typer.typedBlockStats(Typer.scala:1377)
	dotty.tools.dotc.typer.Typer.typedBlock(Typer.scala:1381)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3400)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedFunctionValue(Typer.scala:1861)
	dotty.tools.dotc.typer.Typer.typedFunction(Typer.scala:1600)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3402)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.ProtoTypes$FunProto.$anonfun$7(ProtoTypes.scala:512)
	dotty.tools.dotc.typer.ProtoTypes$FunProto.cacheTypedArg(ProtoTypes.scala:435)
	dotty.tools.dotc.typer.ProtoTypes$FunProto.typedArg(ProtoTypes.scala:513)
	dotty.tools.dotc.typer.Applications$ApplyToUntyped.typedArg(Applications.scala:996)
	dotty.tools.dotc.typer.Applications$ApplyToUntyped.typedArg(Applications.scala:996)
	dotty.tools.dotc.typer.Applications$Application.addTyped$1(Applications.scala:688)
	dotty.tools.dotc.typer.Applications$Application.matchArgs(Applications.scala:752)
	dotty.tools.dotc.typer.Applications$Application.init(Applications.scala:574)
	dotty.tools.dotc.typer.Applications$TypedApply.<init>(Applications.scala:878)
	dotty.tools.dotc.typer.Applications$ApplyToUntyped.<init>(Applications.scala:995)
	dotty.tools.dotc.typer.Applications.ApplyTo(Applications.scala:1257)
	dotty.tools.dotc.typer.Applications.ApplyTo$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.ApplyTo(Typer.scala:145)
	dotty.tools.dotc.typer.Applications.simpleApply$1(Applications.scala:1068)
	dotty.tools.dotc.typer.Applications.realApply$1$$anonfun$2(Applications.scala:1178)
	dotty.tools.dotc.typer.Typer$.tryEither(Typer.scala:118)
	dotty.tools.dotc.typer.Applications.realApply$1(Applications.scala:1193)
	dotty.tools.dotc.typer.Applications.typedApply(Applications.scala:1231)
	dotty.tools.dotc.typer.Applications.typedApply$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.typedApply(Typer.scala:145)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3392)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3443)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3669)
	dotty.tools.dotc.typer.Namer.typedAheadExpr$$anonfun$1(Namer.scala:1747)
	dotty.tools.dotc.typer.Namer.typedAhead(Namer.scala:1737)
	dotty.tools.dotc.typer.Namer.typedAheadExpr(Namer.scala:1747)
	dotty.tools.dotc.typer.Namer.typedAheadRhs$1$$anonfun$1(Namer.scala:2073)
	dotty.tools.dotc.inlines.PrepareInlineable$.dropInlineIfError(PrepareInlineable.scala:256)
	dotty.tools.dotc.typer.Namer.typedAheadRhs$1(Namer.scala:2073)
	dotty.tools.dotc.typer.Namer.rhsType$1(Namer.scala:2081)
	dotty.tools.dotc.typer.Namer.cookedRhsType$1(Namer.scala:2100)
	dotty.tools.dotc.typer.Namer.lhsType$1(Namer.scala:2101)
	dotty.tools.dotc.typer.Namer.inferredResultType(Namer.scala:2112)
	dotty.tools.dotc.typer.Namer.inferredType$1(Namer.scala:1779)
	dotty.tools.dotc.typer.Namer.valOrDefDefSig(Namer.scala:1785)
	dotty.tools.dotc.typer.Namer$Completer.typeSig(Namer.scala:823)
	dotty.tools.dotc.typer.Namer$Completer.completeInCreationContext(Namer.scala:974)
	dotty.tools.dotc.typer.Namer$Completer.complete(Namer.scala:850)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.completeFrom(SymDenotations.scala:175)
	dotty.tools.dotc.core.Denotations$Denotation.completeInfo$1(Denotations.scala:190)
	dotty.tools.dotc.core.Denotations$Denotation.info(Denotations.scala:192)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.ensureCompleted(SymDenotations.scala:393)
	dotty.tools.dotc.typer.Typer.retrieveSym(Typer.scala:3339)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3364)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3476)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3580)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3626)
	dotty.tools.dotc.typer.Typer.typedBlockStats(Typer.scala:1377)
	dotty.tools.dotc.typer.Typer.typedBlock(Typer.scala:1381)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3400)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3669)
	dotty.tools.dotc.typer.Namer.typedAheadExpr$$anonfun$1(Namer.scala:1747)
	dotty.tools.dotc.typer.Namer.typedAhead(Namer.scala:1737)
	dotty.tools.dotc.typer.Namer.typedAheadExpr(Namer.scala:1747)
	dotty.tools.dotc.typer.Namer.typedAheadRhs$1$$anonfun$1(Namer.scala:2073)
	dotty.tools.dotc.inlines.PrepareInlineable$.dropInlineIfError(PrepareInlineable.scala:256)
	dotty.tools.dotc.typer.Namer.typedAheadRhs$1(Namer.scala:2073)
	dotty.tools.dotc.typer.Namer.rhsType$1(Namer.scala:2081)
	dotty.tools.dotc.typer.Namer.cookedRhsType$1(Namer.scala:2100)
	dotty.tools.dotc.typer.Namer.lhsType$1(Namer.scala:2101)
	dotty.tools.dotc.typer.Namer.inferredResultType(Namer.scala:2112)
	dotty.tools.dotc.typer.Namer.inferredType$1(Namer.scala:1779)
	dotty.tools.dotc.typer.Namer.valOrDefDefSig(Namer.scala:1785)
	dotty.tools.dotc.typer.Namer.defDefSig(Namer.scala:1959)
	dotty.tools.dotc.typer.Namer$Completer.typeSig(Namer.scala:827)
	dotty.tools.dotc.typer.Namer$Completer.completeInCreationContext(Namer.scala:974)
	dotty.tools.dotc.typer.Namer$Completer.complete(Namer.scala:850)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.completeFrom(SymDenotations.scala:175)
	dotty.tools.dotc.core.Denotations$Denotation.completeInfo$1(Denotations.scala:190)
	dotty.tools.dotc.core.Denotations$Denotation.info(Denotations.scala:192)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.ensureCompleted(SymDenotations.scala:393)
	dotty.tools.dotc.typer.Typer.retrieveSym(Typer.scala:3339)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3364)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3476)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3580)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3626)
	dotty.tools.dotc.typer.Typer.typedBlockStats(Typer.scala:1377)
	dotty.tools.dotc.typer.Typer.typedBlock(Typer.scala:1381)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3400)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedFunctionValue(Typer.scala:1861)
	dotty.tools.dotc.typer.Typer.typedFunction(Typer.scala:1600)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3402)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3669)
	dotty.tools.dotc.typer.Namer.typedAheadExpr$$anonfun$1(Namer.scala:1747)
	dotty.tools.dotc.typer.Namer.typedAhead(Namer.scala:1737)
	dotty.tools.dotc.typer.Namer.typedAheadExpr(Namer.scala:1747)
	dotty.tools.dotc.typer.Namer.typedAheadRhs$1$$anonfun$1(Namer.scala:2073)
	dotty.tools.dotc.inlines.PrepareInlineable$.dropInlineIfError(PrepareInlineable.scala:256)
	dotty.tools.dotc.typer.Namer.typedAheadRhs$1(Namer.scala:2073)
	dotty.tools.dotc.typer.Namer.rhsType$1(Namer.scala:2081)
	dotty.tools.dotc.typer.Namer.cookedRhsType$1(Namer.scala:2100)
	dotty.tools.dotc.typer.Namer.lhsType$1(Namer.scala:2101)
	dotty.tools.dotc.typer.Namer.inferredResultType(Namer.scala:2112)
	dotty.tools.dotc.typer.Namer.inferredType$1(Namer.scala:1779)
	dotty.tools.dotc.typer.Namer.valOrDefDefSig(Namer.scala:1785)
	dotty.tools.dotc.typer.Namer$Completer.typeSig(Namer.scala:823)
	dotty.tools.dotc.typer.Namer$Completer.completeInCreationContext(Namer.scala:974)
	dotty.tools.dotc.typer.Namer$Completer.complete(Namer.scala:850)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.completeFrom(SymDenotations.scala:175)
	dotty.tools.dotc.core.Denotations$Denotation.completeInfo$1(Denotations.scala:190)
	dotty.tools.dotc.core.Denotations$Denotation.info(Denotations.scala:192)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.ensureCompleted(SymDenotations.scala:393)
	dotty.tools.dotc.typer.Typer.retrieveSym(Typer.scala:3339)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3364)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3476)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3580)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3626)
	dotty.tools.dotc.typer.Typer.typedBlockStats(Typer.scala:1377)
	dotty.tools.dotc.typer.Typer.typedBlock(Typer.scala:1381)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3400)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3669)
	dotty.tools.dotc.typer.Typer.$anonfun$64(Typer.scala:2816)
	dotty.tools.dotc.inlines.PrepareInlineable$.dropInlineIfError(PrepareInlineable.scala:256)
	dotty.tools.dotc.typer.Typer.typedDefDef(Typer.scala:2816)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3374)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3476)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3580)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3626)
	dotty.tools.dotc.typer.Typer.typedClassDef(Typer.scala:3074)
	dotty.tools.dotc.typer.Typer.typedTypeOrClassDef$1(Typer.scala:3380)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3384)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3476)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3580)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3626)
	dotty.tools.dotc.typer.Typer.typedPackageDef(Typer.scala:3207)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3426)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3477)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3554)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3558)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3669)
	dotty.tools.dotc.typer.TyperPhase.typeCheck$$anonfun$1(TyperPhase.scala:47)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	dotty.tools.dotc.core.Phases$Phase.monitor(Phases.scala:503)
	dotty.tools.dotc.typer.TyperPhase.typeCheck(TyperPhase.scala:53)
	dotty.tools.dotc.typer.TyperPhase.$anonfun$4(TyperPhase.scala:99)
	scala.collection.Iterator$$anon$6.hasNext(Iterator.scala:479)
	scala.collection.Iterator$$anon$9.hasNext(Iterator.scala:583)
	scala.collection.immutable.List.prependedAll(List.scala:152)
	scala.collection.immutable.List$.from(List.scala:685)
	scala.collection.immutable.List$.from(List.scala:682)
	scala.collection.IterableOps$WithFilter.map(Iterable.scala:900)
	dotty.tools.dotc.typer.TyperPhase.runOn(TyperPhase.scala:98)
	dotty.tools.dotc.Run.runPhases$1$$anonfun$1(Run.scala:343)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	scala.collection.ArrayOps$.foreach$extension(ArrayOps.scala:1323)
	dotty.tools.dotc.Run.runPhases$1(Run.scala:336)
	dotty.tools.dotc.Run.compileUnits$$anonfun$1(Run.scala:384)
	dotty.tools.dotc.Run.compileUnits$$anonfun$adapted$1(Run.scala:396)
	dotty.tools.dotc.util.Stats$.maybeMonitored(Stats.scala:69)
	dotty.tools.dotc.Run.compileUnits(Run.scala:396)
	dotty.tools.dotc.Run.compileSources(Run.scala:282)
	dotty.tools.dotc.interactive.InteractiveDriver.run(InteractiveDriver.scala:161)
	dotty.tools.pc.MetalsDriver.run(MetalsDriver.scala:47)
	dotty.tools.pc.PcCollector.<init>(PcCollector.scala:42)
	dotty.tools.pc.PcSemanticTokensProvider$Collector$.<init>(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector$lzyINIT1(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.provide(PcSemanticTokensProvider.scala:88)
	dotty.tools.pc.ScalaPresentationCompiler.semanticTokens$$anonfun$1(ScalaPresentationCompiler.scala:109)
```
#### Short summary: 

java.lang.AssertionError: assertion failed