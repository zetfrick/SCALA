file:///D:/ИРИТ/SCALA/lab2.1/src/main/scala/Main.scala
### java.nio.file.InvalidPathException: Illegal char <:> at index 3: jar:file:///C:/Users/fsome/AppData/Local/Coursier/cache/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.14/scala-library-2.13.14-sources.jar!/scala/Int.scala

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
offset: 455
uri: file:///D:/ИРИТ/SCALA/lab2.1/src/main/scala/Main.scala
text:
```scala
object Org{
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
  def f(tag:Int,name:String,parts:Seq[Org]):Org =
    tag m@@
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

java.nio.file.InvalidPathException: Illegal char <:> at index 3: jar:file:///C:/Users/fsome/AppData/Local/Coursier/cache/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.14/scala-library-2.13.14-sources.jar!/scala/Int.scala