package autolift.test.scalaz

import scalaz._
import Scalaz._
import autolift.Scalaz._

class LiftForAllTest extends BaseSpec{
  case class Bar[A](a: A)

  implicit val fn = new Functor[Bar]{
    def map[A, B](fa: Bar[A])(f: A => B) = Bar(f(fa.a))
  }

  "liftAll" should "work on a List" in{
    val out = List(1, 2, 3).liftAll(even)

    same[Boolean](out, false)
  }

  "liftAll" should "work on a List[Option]" in{
    val in = List(Option(1), None, Option(3))
    val out = in liftAll even

    same[List[Boolean]](out, List(false, true, false))
  }

  "liftAll" should "work with functions" in{
    val in = Bar(List(1, 2, 3))
    val out = in liftAll any

    same[Bar[Boolean]](out, Bar(false))
  }

  "liftAll" should "work on a Disjunction[Option]" in{
    val in: Int \/ Option[Int] = \/.right(Option(1))
    val out = in liftAll any

    same[Boolean](out, true)
  }
}