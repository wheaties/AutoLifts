package autolift.test.scalaz

import scalaz._
import Scalaz._
import autolift.Scalaz._

class LiftFoldTest extends BaseSpec{
	"liftFold on a List" should "work" in{
		val in = List(1, 2, 3)
		val out = in.liftFold

		same[Int](out, 6)
	}

	"liftFold on a List[List]" should "work" in{
		val in = List(Nil, Nil, List(1))
		val out = in.liftFold

		same[List[Int]](out, List(1))
	}

	"liftFold[List] on an Option[Option[List[List]]]" should "work" in{
		val in = Option(Option(List(Nil, Nil, List(1))))
		val out = in.liftFold[List]

		same[Option[Option[List[Int]]]](out, Option(Option(List(1))))
	}

	"liftFold on a State[List]" should "work" in{
		val in: State[Int,List[Int]] = State.state[Int,List[Int]](List(1, 2, 3))
		val out = in.liftFold

		same[Int](out.run(1)._2, 6)
	}

	"liftFold[List] on a List" should "work" in{
		val in = List(1, 2, 3)
		val out = in.liftFold[List]

		same[Int](out, 6)
	}

	"liftFold[List] on a List[Option] w/ List" should "work" in{
		val in = List(Option(1), None, Option(2))
		val out = in.liftFold[List]

		same[Option[Int]](out, Option(3))
	}

	"liftFold[Option] on a List[Option] w/ Option" should "work" in{
		val in = List(Option(1), None, Option(2))
		val out = in.liftFold[Option]

		same[List[Int]](out, List(1, 0, 2))
	}

	"liftFold[List] on a Disjunction[List]" should "work" in{
		val in: Int \/ List[Int] = \/.right(List(1, 2, 3))
		val out = in.liftFold[List]

		same[Int \/ Int](out, \/.right(6))
	}
}