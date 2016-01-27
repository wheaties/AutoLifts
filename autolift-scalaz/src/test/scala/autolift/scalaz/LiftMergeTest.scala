package autolift.test.scalaz

import scalaz._
import Scalaz._
import autolift.Scalaz._

class LiftMergeTest extends BaseSpec{
	"liftMerge on a Option[Int] from a Option[Int]" should "work" in{
		val in = Option(1)
		val out = in liftMerge in

		same[Option[(Int,Int)]](out, Option((1, 1)))
	}

	"liftMerge on a Option[List[Int]] on a List[Int]" should "work" in{
		val in = Option(List(1, 2))
		val out = in liftMerge List(1, 2)

		same[Option[List[(Int,Int)]]](out, Option(List((1, 1), (2, 1), (1, 2), (2, 2))))
	}
}