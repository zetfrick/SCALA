error id: 918C687535952DC4153F2B0EAD22E1BA
file:///D:/ИРИТ/SCALA/lab2.1/build.sbt
### java.lang.NullPointerException: Cannot read the array length because "a" is null

occurred in the presentation compiler.



action parameters:
uri: file:///D:/ИРИТ/SCALA/lab2.1/build.sbt
text:
```scala
import _root_.scala.xml.{TopScope=>$scope}
import _root_.sbt._
import _root_.sbt.Keys._
import _root_.sbt.nio.Keys._
import _root_.sbt.ScriptedPlugin.autoImport._, _root_.sbt.plugins.JUnitXmlReportPlugin.autoImport._, _root_.sbt.plugins.MiniDependencyTreePlugin.autoImport._, _root_.bloop.integrations.sbt.BloopPlugin.autoImport._
import _root_.sbt.plugins.IvyPlugin, _root_.sbt.plugins.JvmPlugin, _root_.sbt.plugins.CorePlugin, _root_.sbt.ScriptedPlugin, _root_.sbt.plugins.SbtPlugin, _root_.sbt.plugins.SemanticdbPlugin, _root_.sbt.plugins.JUnitXmlReportPlugin, _root_.sbt.plugins.Giter8TemplatePlugin, _root_.sbt.plugins.MiniDependencyTreePlugin, _root_.bloop.integrations.sbt.BloopPlugin
// The simplest possible sbt build file is just one line:

scalaVersion := "3.5.0"
// That is, to create a valid sbt build, all you've got to do is define the
// version of Scala you'd like your project to use.

// ============================================================================

// Lines like the above defining `scalaVersion` are called "settings". Settings
// are key/value pairs. In the case of `scalaVersion`, the key is "scalaVersion"
// and the value is "2.13.12"

// It's possible to define many kinds of settings, such as:

name := "hello-world"
organization := "ch.epfl.scala"
version := "1.0"

// Note, it's not required for you to define these three settings. These are
// mostly only necessary if you intend to publish your library's binaries on a
// place like Sonatype.

// Want to use a published library in your project?
// You can define other libraries as dependencies in your build like this:

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.3.0"

// Here, `libraryDependencies` is a set of dependencies, and by using `+=`,
// we're adding the scala-parser-combinators dependency to the set of dependencies
// that sbt will go and fetch when it starts up.
// Now, in any Scala file, you can import classes, objects, etc., from
// scala-parser-combinators with a regular import.

// TIP: To find the "dependency" that you need to add to the
// `libraryDependencies` set, which in the above example looks like this:

// "org.scala-lang.modules" %% "scala-parser-combinators" % "2.3.0"

// You can use Scaladex, an index of all known published Scala libraries. There,
// after you find the library you want, you can just copy/paste the dependency
// information that you need into your build file. For example, on the
// scala/scala-parser-combinators Scaladex page,
// https://index.scala-lang.org/scala/scala-parser-combinators, you can copy/paste
// the sbt dependency from the sbt box on the right-hand side of the screen.

// IMPORTANT NOTE: while build files look _kind of_ like regular Scala, it's
// important to note that syntax in *.sbt files doesn't always behave like
// regular Scala. For example, notice in this build file that it's not required
// to put our settings into an enclosing object or class. Always remember that
// sbt is a bit different, semantically, than vanilla Scala.

// ============================================================================

// Most moderately interesting Scala projects don't make use of the very simple
// build file style (called "bare style") used in this build.sbt file. Most
// intermediate Scala projects make use of so-called "multi-project" builds. A
// multi-project build makes it possible to have different folders which sbt can
// be configured differently for. That is, you may wish to have different
// dependencies or different testing frameworks defined for different parts of
// your codebase. Multi-project builds make this possible.

// Here's a quick glimpse of what a multi-project build looks like for this
// build, with only one "subproject" defined, called `root`:

// lazy val root = (project in file(".")).
//   settings(
//     inThisBuild(List(
//       organization := "ch.epfl.scala",
//       scalaVersion := "2.13.12"
//     )),
//     name := "hello-world"
//   )

// To learn more about multi-project builds, head over to the official sbt
// documentation at http://www.scala-sbt.org/documentation.html

```


presentation compiler configuration:
Scala version: 2.12.20
Classpath:
D:\ИРИТ\SCALA\lab1\project\.bloop\lab1-build\bloop-bsp-clients-classes\classes-Metals-8WbdbzPnTTaB7CvazS48EQ== [missing ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.10.3\semanticdb-javac-0.10.3.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\sbt\1.10.5\sbt-1.10.5.jar [exists ], <HOME>\.sbt\boot\scala-2.12.20\lib\scala-library.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\ch\epfl\scala\sbt-bloop_2.12_1.0\2.0.5\sbt-bloop_2.12_1.0-2.0.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\main_2.12\1.10.5\main_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\io_2.12\1.10.1\io_2.12-1.10.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\ch\epfl\scala\bloop-config_2.12\2.1.0\bloop-config_2.12-2.1.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\logic_2.12\1.10.5\logic_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\actions_2.12\1.10.5\actions_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\main-settings_2.12\1.10.5\main-settings_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\run_2.12\1.10.5\run_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\command_2.12\1.10.5\command_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\collections_2.12\1.10.5\collections_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\scripted-plugin_2.12\1.10.5\scripted-plugin_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-lm-integration_2.12\1.10.5\zinc-lm-integration_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\util-logging_2.12\1.10.5\util-logging_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-xml_2.12\2.3.0\scala-xml_2.12-2.3.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\launcher-interface\1.4.4\launcher-interface-1.4.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\github\ben-manes\caffeine\caffeine\2.8.5\caffeine-2.8.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\io\get-coursier\lm-coursier-shaded_2.12\2.1.5\lm-coursier-shaded_2.12-2.1.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\logging\log4j\log4j-api\2.17.1\log4j-api-2.17.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\logging\log4j\log4j-core\2.17.1\log4j-core-2.17.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\apache\logging\log4j\log4j-slf4j-impl\2.17.1\log4j-slf4j-impl-2.17.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\librarymanagement-core_2.12\1.10.2\librarymanagement-core_2.12-1.10.2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\librarymanagement-ivy_2.12\1.10.2\librarymanagement-ivy_2.12-1.10.2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\compiler-interface\1.10.4\compiler-interface-1.10.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-compile_2.12\1.10.4\zinc-compile_2.12-1.10.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\swoval\file-tree-views\2.1.12\file-tree-views-2.1.12.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\github\plokhotnyuk\jsoniter-scala\jsoniter-scala-core_2.12\2.30.14\jsoniter-scala-core_2.12-2.30.14.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\unroll-annotation_2.12\0.1.12\unroll-annotation_2.12-0.1.12.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\util-relation_2.12\1.10.5\util-relation_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\completion_2.12\1.10.5\completion_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\task-system_2.12\1.10.5\task-system_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\tasks_2.12\1.10.5\tasks_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\testing_2.12\1.10.5\testing_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\util-tracking_2.12\1.10.5\util-tracking_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\eed3si9n\sjson-new-scalajson_2.12\0.10.1\sjson-new-scalajson_2.12-0.10.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\jline\jline-terminal\3.27.1\jline-terminal-3.27.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-classpath_2.12\1.10.4\zinc-classpath_2.12-1.10.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-apiinfo_2.12\1.10.4\zinc-apiinfo_2.12-1.10.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc_2.12\1.10.4\zinc_2.12-1.10.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\core-macros_2.12\1.10.5\core-macros_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\util-cache_2.12\1.10.5\util-cache_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\util-control_2.12\1.10.5\util-control_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\protocol_2.12\1.10.5\protocol_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\eed3si9n\sjson-new-core_2.12\0.10.1\sjson-new-core_2.12-0.10.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\template-resolver\0.1\template-resolver-0.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\util-position_2.12\1.10.5\util-position_2.12-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-compile-core_2.12\1.10.4\zinc-compile-core_2.12-1.10.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\util-interface\1.10.5\util-interface-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\jline\jline\2.14.7-sbt-9c3b6aca11c57e339441442bbf58e550cdfecb79\jline-2.14.7-sbt-9c3b6aca11c57e339441442bbf58e550cdfecb79.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\jline\jline-terminal-jni\3.27.1\jline-terminal-jni-3.27.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\jline\jline-native\3.27.1\jline-native-3.27.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lmax\disruptor\3.4.2\disruptor-3.4.2.jar [exists ], <HOME>\.sbt\boot\scala-2.12.20\lib\scala-reflect.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\checkerframework\checker-qual\3.4.1\checker-qual-3.4.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\google\errorprone\error_prone_annotations\2.4.0\error_prone_annotations-2.4.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-collection-compat_2.12\2.12.0\scala-collection-compat_2.12-2.12.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar [exists ], <HOME>\.sbt\boot\scala-2.12.20\lib\scala-compiler.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\github\mwiede\jsch\0.2.17\jsch-0.2.17.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\eed3si9n\gigahorse-apache-http_2.12\0.7.0\gigahorse-apache-http_2.12-0.7.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\ivy\ivy\2.3.0-sbt-396a783bba347016e7fe30dacc60d355be607fe2\ivy-2.3.0-sbt-396a783bba347016e7fe30dacc60d355be607fe2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\jline\jline-reader\3.27.1\jline-reader-3.27.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\jline\jline-builtins\3.27.1\jline-builtins-3.27.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\test-agent\1.10.5\test-agent-1.10.5.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\test-interface\1.0\test-interface-1.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\eed3si9n\shaded-scalajson_2.12\1.0.0-M4\shaded-scalajson_2.12-1.0.0-M4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\eed3si9n\shaded-jawn-parser_2.12\1.3.2\shaded-jawn-parser_2.12-1.3.2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\compiler-bridge_2.12\1.10.4\compiler-bridge_2.12-1.10.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-classfile_2.12\1.10.4\zinc-classfile_2.12-1.10.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-core_2.12\1.10.4\zinc-core_2.12-1.10.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-persist_2.12\1.10.4\zinc-persist_2.12-1.10.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\eed3si9n\sjson-new-murmurhash_2.12\0.10.1\sjson-new-murmurhash_2.12-0.10.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\ipcsocket\ipcsocket\1.6.3\ipcsocket-1.6.3.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-parser-combinators_2.12\1.1.2\scala-parser-combinators_2.12-1.1.2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\net\openhft\zero-allocation-hashing\0.16\zero-allocation-hashing-0.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\fusesource\jansi\jansi\2.4.0\jansi-2.4.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\eed3si9n\gigahorse-core_2.12\0.7.0\gigahorse-core_2.12-0.7.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\eed3si9n\shaded-apache-httpasyncclient\0.7.0\shaded-apache-httpasyncclient-0.7.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\jline\jline-style\3.27.1\jline-style-3.27.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\zinc-persist-core-assembly\1.10.4\zinc-persist-core-assembly-1.10.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-sbt\sbinary_2.12\0.5.1\sbinary_2.12-0.5.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\net\java\dev\jna\jna\5.12.0\jna-5.12.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\net\java\dev\jna\jna-platform\5.12.0\jna-platform-5.12.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\typesafe\ssl-config-core_2.12\0.6.1\ssl-config-core_2.12-0.6.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\reactivestreams\reactive-streams\1.0.3\reactive-streams-1.0.3.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\typesafe\config\1.4.2\config-1.4.2.jar [exists ]
Options:
-deprecation -Wconf:cat=unused-nowarn:s -Wconf:cat=unused-nowarn:s -Xsource:3 -Yrangepos -Xplugin-require:semanticdb




#### Error stacktrace:

```
java.base/java.util.Arrays.sort(Arrays.java:1233)
	scala.tools.nsc.classpath.JFileDirectoryLookup.listChildren(DirectoryClassPath.scala:119)
	scala.tools.nsc.classpath.JFileDirectoryLookup.listChildren$(DirectoryClassPath.scala:103)
	scala.tools.nsc.classpath.DirectoryClassPath.listChildren(DirectoryClassPath.scala:314)
	scala.tools.nsc.classpath.DirectoryClassPath.listChildren(DirectoryClassPath.scala:314)
	scala.tools.nsc.classpath.DirectoryLookup.list(DirectoryClassPath.scala:84)
	scala.tools.nsc.classpath.DirectoryLookup.list$(DirectoryClassPath.scala:79)
	scala.tools.nsc.classpath.DirectoryClassPath.list(DirectoryClassPath.scala:314)
	scala.tools.nsc.classpath.AggregateClassPath.$anonfun$list$3(AggregateClassPath.scala:105)
	scala.collection.Iterator.foreach(Iterator.scala:943)
	scala.collection.Iterator.foreach$(Iterator.scala:943)
	scala.collection.AbstractIterator.foreach(Iterator.scala:1431)
	scala.collection.IterableLike.foreach(IterableLike.scala:74)
	scala.collection.IterableLike.foreach$(IterableLike.scala:73)
	scala.collection.AbstractIterable.foreach(Iterable.scala:56)
	scala.tools.nsc.classpath.AggregateClassPath.list(AggregateClassPath.scala:101)
	scala.tools.nsc.util.ClassPath.list(ClassPath.scala:36)
	scala.tools.nsc.util.ClassPath.list$(ClassPath.scala:36)
	scala.tools.nsc.classpath.AggregateClassPath.list(AggregateClassPath.scala:30)
	scala.tools.nsc.symtab.SymbolLoaders$PackageLoader.doComplete(SymbolLoaders.scala:298)
	scala.tools.nsc.symtab.SymbolLoaders$SymbolLoader.complete(SymbolLoaders.scala:250)
	scala.reflect.internal.Symbols$Symbol.completeInfo(Symbols.scala:1542)
	scala.reflect.internal.Symbols$Symbol.info(Symbols.scala:1514)
	scala.reflect.internal.Types$TypeRef.decls(Types.scala:2283)
	scala.tools.nsc.typechecker.Namers$Namer.enterPackage(Namers.scala:766)
	scala.tools.nsc.typechecker.Namers$Namer.dispatch$1(Namers.scala:289)
	scala.tools.nsc.typechecker.Namers$Namer.standardEnterSym(Namers.scala:302)
	scala.tools.nsc.typechecker.AnalyzerPlugins.pluginsEnterSym(AnalyzerPlugins.scala:479)
	scala.tools.nsc.typechecker.AnalyzerPlugins.pluginsEnterSym$(AnalyzerPlugins.scala:478)
	scala.meta.internal.pc.MetalsGlobal$MetalsInteractiveAnalyzer.pluginsEnterSym(MetalsGlobal.scala:78)
	scala.tools.nsc.typechecker.Namers$Namer.enterSym(Namers.scala:280)
	scala.tools.nsc.typechecker.Analyzer$namerFactory$$anon$1.apply(Analyzer.scala:48)
	scala.tools.nsc.Global$GlobalPhase.applyPhase(Global.scala:465)
	scala.tools.nsc.Global$Run.$anonfun$compileLate$3(Global.scala:1657)
	scala.tools.nsc.Global$Run.$anonfun$compileLate$2(Global.scala:1657)
	scala.tools.nsc.Global$Run.$anonfun$compileLate$2$adapted(Global.scala:1656)
	scala.collection.Iterator.foreach(Iterator.scala:943)
	scala.collection.Iterator.foreach$(Iterator.scala:943)
	scala.collection.AbstractIterator.foreach(Iterator.scala:1431)
	scala.tools.nsc.Global$Run.compileLate(Global.scala:1656)
	scala.tools.nsc.interactive.Global.parseAndEnter(Global.scala:654)
	scala.tools.nsc.interactive.Global.typeCheck(Global.scala:664)
	scala.meta.internal.pc.SemanticdbTextDocumentProvider.textDocument(SemanticdbTextDocumentProvider.scala:35)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$semanticdbTextDocument$1(ScalaPresentationCompiler.scala:551)
```
#### Short summary: 

java.lang.NullPointerException: Cannot read the array length because "a" is null