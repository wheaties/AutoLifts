package autolift.cats

import autolift.cats.applicative._
import cats.implicits._
import cats.{Functor, Apply}
import cats.data.Xor

class LiftMergeWithTest extends BaseSpec{

	"liftMergeWith on a Option[Int] from a Option[Int]" should "work" in{
		val in = Option(1)
		val out = in.liftMergeWith(Option(1))(intintF)

		same[Option[Int]](out, Option(2))
	}

	"liftMergeWith on a Option[List] on a List" should "work" in{
		val in = Option(List(1))
		val out = in.liftMergeWith(List(1))(intintF)

		same[Option[List[Int]]](out, Option(List(2)))
	}

	"liftMergeWith on a Xor[Option]" should "work" in{
		val in = Xor.right(Option(1))
		val out = in.liftMergeWith(Option(1))(intintF)

		same[Xor[Nothing,Option[Int]]](out, Xor.right(Option(2)))
	}

	"LiftedMergeWith on a Option[List]" should "work" in{
		val lf = liftMergeWith(intintF)
		val out = lf(Option(List(1)), List(1))

		same[Option[List[Int]]](out, Option(List(2)))
	}

	"LiftedMergeWith" should "map" in{
		val lf = liftMergeWith(intintF)
		val lf2 = lf map (_ + 1)
		val out = lf2(Option(List(1)), List(1))

		same[Option[List[Int]]](out, Option(List(3)))
	}
}