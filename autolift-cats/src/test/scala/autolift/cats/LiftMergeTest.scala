package autolift.cats

import autolift.cats.applicative._
import cats.implicits._

class LiftMergeTest extends BaseSpec{
	"liftMerge on a Option[Int] from a Option[Int]" should "work" in{
		val in = Option(1)
		val out = in liftMerge in

		same[Option[(Int,Int)]](out, Option(1 -> 1))
	}

	"liftMerge on a Either[Option] from an Option" should "work" in{
		val in = Either.right(Option(1))
		val out = in liftMerge Option(1)

		same[Either[Nothing,Option[(Int,Int)]]](out, Either.right(Option(1 -> 1)))
	}

	"liftMerge on a Option[List[Int]] on a List[Int]" should "work" in{
		val in = Option(List(1, 2))
		val out = in liftMerge List(1, 2)

		same[Option[List[(Int,Int)]]](out, Option(List((1, 1), (2, 1), (1, 2), (2, 2))))
	}

	"liftMerge on an List[Either[?,Option]]" should "work" in{
		val in = List(Either.right(Option(1)))
		val out = in liftMerge Option(1)

		same[List[Either[Nothing,Option[(Int,Int)]]]](out, List(Either.right(Option(1 -> 1))))
	}
}