file:///D:/ИРИТ/SCALA/lab1/src/main/scala/Main.scala
### java.nio.file.InvalidPathException: Illegal char <:> at index 3: jar:file:///C:/Users/fsome/AppData/Local/Coursier/cache/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.14/scala-library-2.13.14-sources.jar!/scala/collection/immutable/List.scala

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
offset: 414
uri: file:///D:/ИРИТ/SCALA/lab1/src/main/scala/Main.scala
text:
```scala
object Main extends App {
  println("1")
  hello()
  println("2")
  printHello5(5)
  println(" ")
  printHello(5)
  println("// 1.3")
  // Зададим числа
  val nums = List(5, 3, 76, 12, 32, 56, 76, 87)
  println("Числа: " + nums.mkString(" "))
  // Выведем список чётных и список нечётных чисел
  val (evenIndex, oddIndex) = splitIndex(nums) // use filter, map
  println("Четные индексы " + evenIndex.m@@)
  println("Нечетные индексы " + oddIndex)
  printfindMax(List(3, 5, 6, 13, 5)) // use reduce

}

@main def hello() = println("hello world")

def printHello5(n: Int): Unit = {
    for (i <- 1 to n) {
      println(s"hello $i")
    }
}   
def printHello(n: Int): Unit = {
    for (i <- 1 to n) {
      val x = if (i % 2 == 0) i else n - i
      println(s"hello $x")
    }
}
def splitIndex(nums: List[Int]): (List[Int], List[Int]) = {
  // Создаем индексированный список
  val indexNum = nums.zipWithIndex

  // Фильтруем четные и нечетные индексы
  val evenIndex = indexNum.filter { case (_, index) => index % 2 == 0 }.map(_._1)
  val oddIndex = indexNum.filter { case (_, index) => index % 2 != 0 }.map(_._1)

  (evenIndex, oddIndex)
}

def printfindMax(nums: List[Int]) = {
  println("Максимальное число " + nums.reduce((a, b) => if (a > b) a else b) + ". В наборе " + nums)
}
```



#### Error stacktrace:

```
java.base/sun.nio.fs.WindowsPathParser.normalize(WindowsPathParser.java:182)
	java.base/sun.nio.fs.WindowsPathParser.parse(WindowsPathParser.java:153)
	java.base/sun.nio.fs.WindowsPathParser.parse(WindowsPathParser.java:77)
	java.base/sun.nio.fs.WindowsPath.parse(WindowsPath.java:92)
	java.base/sun.nio.fs.WindowsFileSystem.getPath(WindowsFileSystem.java:232)
	java.base/java.nio.file.Path.of(Path.java:147)
	java.base/java.nio.file.Paths.get(Paths.java:69)
	scala.meta.io.AbsolutePath$.apply(AbsolutePath.scala:58)
	scala.meta.internal.metals.MetalsSymbolSearch.$anonfun$definitionSourceToplevels$2(MetalsSymbolSearch.scala:70)
	scala.Option.map(Option.scala:242)
	scala.meta.internal.metals.MetalsSymbolSearch.definitionSourceToplevels(MetalsSymbolSearch.scala:69)
	dotty.tools.pc.completions.CaseKeywordCompletion$.dotty$tools$pc$completions$CaseKeywordCompletion$$$sortSubclasses(MatchCaseCompletions.scala:325)
	dotty.tools.pc.completions.CaseKeywordCompletion$.matchContribute(MatchCaseCompletions.scala:275)
	dotty.tools.pc.completions.Completions.advancedCompletions(Completions.scala:346)
	dotty.tools.pc.completions.Completions.completions(Completions.scala:118)
	dotty.tools.pc.completions.CompletionProvider.completions(CompletionProvider.scala:90)
	dotty.tools.pc.ScalaPresentationCompiler.complete$$anonfun$1(ScalaPresentationCompiler.scala:146)
```
#### Short summary: 

java.nio.file.InvalidPathException: Illegal char <:> at index 3: jar:file:///C:/Users/fsome/AppData/Local/Coursier/cache/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.14/scala-library-2.13.14-sources.jar!/scala/collection/immutable/List.scala