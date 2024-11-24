// Определение типа Functor
trait Functor[F[_]] {
  def map[A, B](fu: F[A])(f: A => B): F[B] // Принимает F[A] и функцию f: A => B, 
                                            //а возвращает F[B]
}

// Определение типа Monad
trait Monad[M[_]] extends Functor[M] {
  def flatMap[A, B](ma: M[A])(f: A => M[B]): M[B] // Принимает M[A] и функцию f: A => M[B], 
                                                // а возвращает M[B]
  def unit[A](a: A): M[A] // Этот метод оборачивает значение А в М[A]

}
