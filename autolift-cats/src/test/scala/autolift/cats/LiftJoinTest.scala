package autolift.cats

import autolift.Cats._
import cats.implicits._

class LiftJoinTest extends BaseSpec{
	"liftJoin on a Option[Int] from a Option[Int]" should "work" in{
		val in = Option(1)
		val out = in liftJoin in

		same[Option[(Int,Int)]](out, Option((1, 1)))
	}

	"liftJoin on a Option[List[Int]] on a List[Int]" should "work" in{
		val in = Option(List(1, 2))
		val out = in liftJoin List(1, 2)

		same[Option[List[(Int,Int)]]](out, Option(List((1, 1), (2, 1), (1, 2), (2, 2))))
	}
}