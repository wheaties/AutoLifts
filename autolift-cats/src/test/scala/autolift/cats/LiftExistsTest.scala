package autolift.cats

import cats.{Functor, Foldable, Eval}
import cats.data.Xor
import cats.implicits._
import autolift.cats.fold._

class LiftExistsTest extends BaseSpec{
  case class Bar[A](a: A)

  implicit val fn = new Functor[Bar]{
    def map[A, B](fa: Bar[A])(f: A => B) = Bar(f(fa.a))
  }

  case class Baz[A, B](a: A, b: B)

  implicit def fld[C] = new Foldable[Baz[C,?]]{
    def foldLeft[A, B](fa: Baz[C,A], b: B)(f: (B, A) => B): B = f(b, fa.b)
    def foldRight[A, B](fa: Baz[C,A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = f(fa.b, lb)
  }

  "liftExists" should "work on a List" in{
    val out = List(1, 2, 3).liftExists(even)

    same[Boolean](out, true)
  }

  "liftExists" should "work on a List[Option]" in{
    val in = List(Option(1), None, Option(3))
    val out = in liftExists even

    same[List[Boolean]](out, List(false, false, false))
  }

  "liftExists" should "work with functions" in{
    val in = Bar(List(1, 2, 3))
    val out = in liftExists any

    same[Bar[Boolean]](out, Bar(true))
  }

  "liftExists" should "work on a List[Baz[A,?]]" in{
    val in = List(Baz("a", 1))
    val out = in liftExists even

    same[List[Boolean]](out, List(false))
  }

  "liftExists" should "work on a Xor[List]" in{
    val in = Xor.right(List(1, 2))
    val out = in liftExists even

    same[Xor[Nothing,Boolean]](out, Xor.right(true))
  }
}