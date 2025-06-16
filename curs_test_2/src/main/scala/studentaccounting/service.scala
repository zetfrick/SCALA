package studentaccounting

import cats.Monad
import cats.implicits._

// Сервис для работы со студентами
class StudentService[F[_]](repo: Repository[F])(implicit F: Monad[F]) {
  // Создание студента
  def createStudent(
      id: Int,
      name: String,
      course: Int,
      groupName: String
  ): F[Unit] =
    repo.addStudent(Student(id, name, course, groupName))

  // Получение данных
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

  // Удаление студента и его оценок
  def deleteStudentWithGrades(id: Int): F[Unit] =
    for {
      _ <- repo.deleteStudent(id)
      _ <- repo.deleteGradesForStudent(id)
    } yield ()
}
