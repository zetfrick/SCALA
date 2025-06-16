package studentaccounting

import cats.Monad
import cats.implicits._

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
