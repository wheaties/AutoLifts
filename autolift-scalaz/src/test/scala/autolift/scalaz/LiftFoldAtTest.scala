package autolift.test.scalaz

import scalaz._
import Scalaz._
import autolift.Scalaz._

class LiftFoldAtTest extends BaseSpec{
	"liftFoldAt on a List" should "work" in{
		val in = List(1, 2, 3)
		val out = in.liftFoldAt[List]

		same[Int](out, 6)
	}

	"liftFoldAt on a List[Option] w/ List" should "work" in{
		val in = List(Option(1), None, Option(2))
		val out = in.liftFoldAt[List]

		same[Option[Int]](out, Option(3))
	}

	"liftFoldAt on a List[Option] w/ Option" should "work" in{
		val in = List(Option(1), None, Option(2))
		val out = in.liftFoldAt[Option]

		same[List[Int]](out, List(1, 0, 2))
	}

	"liftFold on a Disjunction[List]" should "work" in{
		val in: Int \/ List[Int] = \/.right(List(1, 2, 3))
		val out = in.liftFoldAt[List]

		same[Int \/ Int](out, \/.right(6))
	}
}