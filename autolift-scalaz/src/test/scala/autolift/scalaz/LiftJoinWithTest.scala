package autolift.test.scalaz

import scalaz._
import Scalaz._
import autolift.Scalaz._

class LiftJoinWithTest extends BaseSpec{

	"liftJoinWith on a Option[Int] from a Option[Int]" should "work" in{
		val in = Option(1)
		val out = in.liftJoinWith(Option(1))(intintF)

		same[Option[Int]](out, Option(2))
	}

	"liftJoinWith on a Option[List] on a List" should "work" in{
		val in = Option(List(1))
		val out = in.liftJoinWith(List(1))(intintF)

		same[Option[List[Int]]](out, Option(List(2)))
	}

	"LiftedJoinWith on a Option[List]" should "work" in{
		val lf = liftJoinWith(intintF)
		val out = lf(Option(List(1)), List(1))

		same[Option[List[Int]]](out, Option(List(2)))
	}

	"LiftedJoinWith" should "map" in{
		val lf = liftJoinWith(intintF)
		val lf2 = lf map (_ + 1)
		val out = lf2(Option(List(1)), List(1))

		same[Option[List[Int]]](out, Option(List(3)))
	}
}