package autolift.cats

import cats.implicits._
import autolift.Cats._

class LiftBindTest extends BaseSpec{
	"liftBind on an Option[List]" should "work" in{
		val in = Option(List(1))
		val out = in liftBind intL

		same[Option[List[Int]]](out, Option(List(2, 3)))
	}

	"liftBind on an Option[List]" should "work with functions" in{
		val in = Option(List(1, 2))
		val out = in liftBind anyO

		same[Option[Int]](out, Option(1))
	}

	"LiftedBind" should "work on a List" in{
		val lf = liftBind(intL)
		val out = lf(List(1))

		same[List[Int]](out, List(2, 3))
	}

	"LiftedBind" should "work on an Option[List]" in{
		val lf = liftBind(intL)
		val out = lf(Option(List(2, 3)))

		same[Option[List[Int]]](out, Option(List(3, 4, 4, 5)))
	}

	"LiftedBind on a List" should "work with functions" in{
		val lf = liftBind(anyL)
		val out = lf(List(1, 2, 3))

		same[List[Int]](out, List(1, 1, 1))
	}

	"LiftedBind" should "andThen with other liftBind" in{
		val lf = liftBind(anyL)
		val lf2 = liftBind(intL)
		val comp = lf andThen lf2
		val out = comp(List(1, 2, 3))

		same[List[Int]](out, List(2, 3, 2, 3, 2, 3))
	}

	"LiftedBind" should "compose with other liftBind" in{
		val lf = liftBind(anyL)
		val lf2 = liftBind(intL)
		val comp = lf2 compose lf
		val out = comp(List(1, 2, 3))

		same[List[Int]](out, List(2, 3, 2, 3, 2, 3))
	}

	"LiftedBind" should "map" in{
		val lf = liftBind(intL) map (_ + 1)
		val out = lf(List(0))

		same[List[Int]](out, List(2, 3))
	}
}