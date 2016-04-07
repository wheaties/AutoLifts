package autolift.test.scalaz

import scalaz._
import Scalaz._
import autolift.Scalaz._

class LiftExistsTest extends BaseSpec{
	case class Bar[A](a: A)

	implicit val fn = new Functor[Bar]{
		def map[A, B](fa: Bar[A])(f: A => B) = Bar(f(fa.a))
	}

	"liftExists" should "work on a List" in{
		val out = List(1, 2, 3).liftAny(even)

		same[Boolean](out, true)
	}

	"liftExists" should "work on a Disjunction[List]" in{
		val in: Int \/ List[Int] = \/.right(List(1, 2, 3))
		val out = in liftAny even

		same[Int \/ Boolean](out, \/.right(true))
	}

	"liftExists" should "work on a List[Option]" in{
		val in = List(Option(1), None, Option(3))
		val out = in liftAny even

		same[List[Boolean]](out, List(false, false, false))
	}

	"liftExists" should "work with functions" in{
		val in = Bar(List(1, 2, 3))
		val out = in liftAny any

		same[Bar[Boolean]](out, Bar(true))
	}
}