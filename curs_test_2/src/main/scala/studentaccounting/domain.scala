package studentaccounting

trait Entity

// Студент
case class Student(id: Int, name: String, course: Int, groupName: String)
    extends Entity

// Предмет
case class Subject(name: String) extends Entity

// Оценка
case class Grade(studentId: Int, subjectName: String, value: Int) extends Entity

// Статистика
case class Stats(
    studentAvg: Map[Int, Double], // Средний балл по студентам
    groupAvg: Map[String, Double], // Средний балл по группам
    courseAvg: Map[Int, Double] // Средний балл по курсам
)
