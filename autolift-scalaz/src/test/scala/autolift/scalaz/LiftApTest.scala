package autolift.test.scalaz

import scalaz._
import Scalaz._
import autolift.Scalaz._

class LiftApTest extends BaseSpec{
	"liftAp on an Option[List]" should "work" in{
		val in = Option(List(1))
		val out = in liftAp List(intF)

		same[Option[List[Int]]](out, Option(List(2)))
	}

	"liftAp on a Disjunction[Option]" should "work" in{
		val in: Int \/ List[Int] = \/.right(List(1))
		val out = in liftAp List(intF)

		same[Int \/ List[Int]](out, \/.right(List(2)))
	}

	//TODO: revisit
	/*"liftAp on an Option[Disjunction]" should "work" in{
		val in: Option[Int \/ Int] = Option(\/.right(1))
		val f: Int \/ (Int => Int) = \/.right(intF)
		val out = in liftAp f

		same[Option[Int \/ Int]](out, Option(\/.right(2)))
	}*/

	"LiftedAp" should "work on a List" in{
		val lf = liftAp(intAp)
		val out = lf(List(1))

		same[List[Int]](out, List(2))
	}

	"LiftedAp" should "work on an Option[List]" in{
		val lf = liftAp(intAp)
		val out = lf(Option(List(2, 3)))

		same[Option[List[Int]]](out, Option(List(3, 4)))
	}

	"LiftedAp" should "andThen with other liftAp" in{
		val lf = liftAp(intAp)
		val lf2 = liftAp(List(anyF))
		val comp = lf andThen lf2
		val out = comp(List(4))

		same[List[Int]](out, List(1))
	}

	"LiftedAp" should "compose with other liftAp" in{
		val lf = liftAp(intAp)
		val lf2 = liftAp(List(anyF))
		val comp = lf2 compose lf
		val out = comp(List(4))

		same[List[Int]](out, List(1))
	}

	"LiftedAp" should "map" in{
		val lf = liftAp(intAp) map(_ + 1)
		val out = lf(Option(List(1)))

		same[Option[List[Int]]](out, Option(List(3)))
	}
}