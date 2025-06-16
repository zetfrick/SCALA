error id: file:///D:/ИРИТ/SCALA/lab2.1/src/main/scala/Main.scala:[403..404) in Input.VirtualFile("file:///D:/ИРИТ/SCALA/lab2.1/src/main/scala/Main.scala", "object Org{
  // Опишем некоторый примитивный язык. В данном случае - целочисленные выражения
  abstract sealed trait Org
  case class Person(id:Int,house:String) extends Org
  case class SubOrg(name:String,parts:Seq[Org]) extends Org

  trait OrgTF[X]:
    def person(id:Int,house:String):X
    def subOrg(name:String,point:Seq[X]):X
}


object Program1{
  import Org.*
  @main 
  def 
}
")
file:///D:/ИРИТ/SCALA/lab2.1/src/main/scala/Main.scala:17: error: expected identifier; obtained rbrace
}
^
#### Short summary: 

expected identifier; obtained rbrace