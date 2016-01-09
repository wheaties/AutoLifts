package autolift.cats

import cats.Functor
import cats.implicits._
import autolift.Cats._

class LiftForAllTest extends BaseSpec{
	case class Bar[A](a: A)

	implicit val fn = new Functor[Bar]{
		def map[A, B](fa: Bar[A])(f: A => B) = Bar(f(fa.a))
	}

	"liftForAll" should "work on a List" in{
		val out = List(1, 2, 3).liftForAll(even)

		same[Boolean](out, false)
	}

	"liftForAll" should "work on a List[Option]" in{
		val in = List(Option(1), None, Option(3))
		val out = in liftForAll even

		same[List[Boolean]](out, List(false, true, false))
	}

	"liftForAll" should "work with functions" in{
		val in = Bar(List(1, 2, 3))
		val out = in liftForAll any

		same[Bar[Boolean]](out, Bar(false))
	}
}