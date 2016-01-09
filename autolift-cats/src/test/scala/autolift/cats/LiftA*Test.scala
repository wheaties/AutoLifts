package autolift.cats

import cats.implicits._
import autolift.Cats._

class `LiftA*Test` extends BaseSpec{
	"liftA2" should "work on a pair of List" in{
		val lf = liftA2(intintF)
		val out = lf(List(0, 1), List(1, 2))

		same[List[Int]](out, List(1, 2, 2, 3))
	}

	"liftA2" should "work on a pair of Option List" in{
		val lf = liftA2(intintF)
		val out = lf(Option(List(0, 1)), Option(List(1, 2)))

		same[Option[List[Int]]](out, Option(List(1, 2, 2, 3)))

		val out2 = lf(Option(List(0, 1)), None: Option[List[Int]])

		same[Option[List[Int]]](out2, None)
	}

	"liftA2" should "work with functions" in{
		val lf = liftA2(anyanyF)
		val out = lf(Option(2), Option('c'))

		same[Option[Int]](out, Option(1))
	}

	"liftA2" should "map" in{
		val lf = liftA2(intintF) map (_.toString)
		val out = lf(List(1), List(2))

		same[List[String]](out, List("3"))
	}

	"liftA3" should "work on Lists" in{
		val lf = liftA3{ (x: Int, y: Int, z: Int) => x + y + z}
		val out = lf(List(0, 1), List(0, 1), List(0, 1))

		same[List[Int]](out, List(0, 1, 1, 2, 1, 2, 2, 3))
	}
}