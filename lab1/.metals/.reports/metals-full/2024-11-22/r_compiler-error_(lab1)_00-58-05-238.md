file:///d:/ИРИТ/SCALA/lab1/Main.scala
### dotty.tools.dotc.MissingCoreLibraryException: Could not find package scala from compiler core libraries.
Make sure the compiler core libraries are on the classpath.
   

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
offset: 9
uri: file:///d:/ИРИТ/SCALA/lab1/Main.scala
text:
```scala
object Ma@@

```



#### Error stacktrace:

```
dotty.tools.dotc.core.Denotations$.select$1(Denotations.scala:1321)
	dotty.tools.dotc.core.Denotations$.recurSimple$1(Denotations.scala:1349)
	dotty.tools.dotc.core.Denotations$.recur$1(Denotations.scala:1351)
	dotty.tools.dotc.core.Denotations$.staticRef(Denotations.scala:1355)
	dotty.tools.dotc.core.Symbols$.requiredPackage(Symbols.scala:943)
	dotty.tools.dotc.core.Definitions.ScalaPackageVal(Definitions.scala:215)
	dotty.tools.dotc.core.Definitions.ScalaPackageClass(Definitions.scala:218)
	dotty.tools.dotc.core.Definitions.AnyClass(Definitions.scala:281)
	dotty.tools.dotc.core.Definitions.syntheticScalaClasses(Definitions.scala:2161)
	dotty.tools.dotc.core.Definitions.syntheticCoreClasses(Definitions.scala:2176)
	dotty.tools.dotc.core.Definitions.init(Definitions.scala:2192)
	dotty.tools.dotc.core.Contexts$ContextBase.initialize(Contexts.scala:921)
	dotty.tools.dotc.core.Contexts$Context.initialize(Contexts.scala:544)
	dotty.tools.dotc.interactive.InteractiveDriver.<init>(InteractiveDriver.scala:41)
	dotty.tools.pc.MetalsDriver.<init>(MetalsDriver.scala:32)
	dotty.tools.pc.ScalaPresentationCompiler.newDriver(ScalaPresentationCompiler.scala:99)
```
#### Short summary: 

dotty.tools.dotc.MissingCoreLibraryException: Could not find package scala from compiler core libraries.
Make sure the compiler core libraries are on the classpath.
   