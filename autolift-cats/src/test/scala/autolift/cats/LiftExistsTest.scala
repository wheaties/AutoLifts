package autolift.cats

import cats._
import cats.implicits._
import autolift.Cats._

class LiftExistsTest extends BaseSpec{
  case class Bar[A](a: A)

  implicit val fn = new Functor[Bar]{
    def map[A, B](fa: Bar[A])(f: A => B) = Bar(f(fa.a))
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
}