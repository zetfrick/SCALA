file:///D:/ИРИТ/SCALA/lab2.1/src/main/scala/Writer(-).scala
### scala.MatchError: TypeDef(A,TypeBoundsTree(EmptyTree,EmptyTree,EmptyTree)) (of class dotty.tools.dotc.ast.Trees$TypeDef)

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
offset: 37
uri: file:///D:/ИРИТ/SCALA/lab2.1/src/main/scala/Writer(-).scala
text:
```scala
case class Writer[L, A](run: (L, A))б@@ {
  // Преобразует значение, сохраняя лог неизменным
  def map[B](f: A => B): Writer[L, B] =
    Writer((run._1, f(run._2)))

  // Комбинирует два Writer'а, соединяя их логи
  def flatMap[B](f: A => Writer[L, B]): Writer[L, B] = {
    val (log1, a) = run
    val (log2, b) = f(a).run
    Writer((log1 ++ log2, b))  // Простое соединение логов
  }
}

object Writer {
  def pure[L, A](a: A)(implicit empty: L): Writer[L, A] =
    Writer((empty, a))
}

```



#### Error stacktrace:

```
dotty.tools.pc.completions.KeywordsCompletions$.checkTemplateForNewParents$$anonfun$2(KeywordsCompletions.scala:218)
	scala.Option.map(Option.scala:242)
	dotty.tools.pc.completions.KeywordsCompletions$.checkTemplateForNewParents(KeywordsCompletions.scala:215)
	dotty.tools.pc.completions.KeywordsCompletions$.contribute(KeywordsCompletions.scala:44)
	dotty.tools.pc.completions.Completions.completions(Completions.scala:122)
	dotty.tools.pc.completions.CompletionProvider.completions(CompletionProvider.scala:90)
	dotty.tools.pc.ScalaPresentationCompiler.complete$$anonfun$1(ScalaPresentationCompiler.scala:146)
```
#### Short summary: 

scala.MatchError: TypeDef(A,TypeBoundsTree(EmptyTree,EmptyTree,EmptyTree)) (of class dotty.tools.dotc.ast.Trees$TypeDef)