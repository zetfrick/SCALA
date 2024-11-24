file:///D:/ИРИТ/SCALA/lab2/src/main/scala/number3.scala
### scala.MatchError: TypeDef(B,TypeBoundsTree(EmptyTree,EmptyTree,EmptyTree)) (of class dotty.tools.dotc.ast.Trees$TypeDef)

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
offset: 94
uri: file:///D:/ИРИТ/SCALA/lab2/src/main/scala/number3.scala
text:
```scala
// Определение типа Functor
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B] .@@
}

// Определение типа Monad
trait Monad[M[_]] extends Functor[M] {
  def flatMap[A, B](ma: M[A])(f: A => M[B]): M[B]
  def unit[A](a: A): M[A]

  override def map[A, B](ma: M[A])(f: A => B): M[B] =
    flatMap(ma)(a => unit(f(a)))
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

scala.MatchError: TypeDef(B,TypeBoundsTree(EmptyTree,EmptyTree,EmptyTree)) (of class dotty.tools.dotc.ast.Trees$TypeDef)