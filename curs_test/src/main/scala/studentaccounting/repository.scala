package studentaccounting

import cats.effect.IO
import cats.implicits._

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
