package studentaccounting

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
