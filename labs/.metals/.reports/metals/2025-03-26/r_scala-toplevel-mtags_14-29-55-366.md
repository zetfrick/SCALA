error id: file:///D:/ИРИТ/SCALA/lab2.1/src/main/scala/Main.scala:[155..158) in Input.VirtualFile("file:///D:/ИРИТ/SCALA/lab2.1/src/main/scala/Main.scala", "trait MMMonad[M[_]]:
 def pure[X](x:X):M[X]
 def fish[A,B,C](f:A=>M[B],g:B=>M[C])(a:A):M[C]

 def flatmap[X,Y](f:x=>M[Y])(mx:M[X]):M[Y] = ???
 def 
 def flatten[X](mmx:M[M[X]]):M[X] = {
  fish()
 }

")
file:///D:/ИРИТ/SCALA/lab2.1/src/main/scala/Main.scala:7: error: expected identifier; obtained def
 def flatten[X](mmx:M[M[X]]):M[X] = {
 ^
#### Short summary: 

expected identifier; obtained def