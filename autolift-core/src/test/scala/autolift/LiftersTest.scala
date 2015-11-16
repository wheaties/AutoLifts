package autolift

import org.scalatest._
import scalaz._
import Scalaz._
import Lifters._

//TODO: Test out the andThen, compose and map methods. Already finding errors!

class LiftersTest extends FlatSpec{
	def same[A](x: A, y: A) = assert(x == y)

	val intF = {x: Int => x+1}
	def intintF(x: Int, y: Int) = x + y
	def anyanyF(x: Any, y: Any) = 1
	val intL = {x: Int => List(x+1, x+2)}
	val intAp = List(intF)
	val anyF = {x: Any => 1}
	val anyL = {x: Any => List(1)}
	val anyO = { x: Any => Option(1) }
	val even = {x: Int => x % 2 == 0 }

	"liftMap on an Option[List]" should "work" in{
		val in = Option(List(1))
		val out = in liftMap intF

		same[Option[List[Int]]](out, Option(List(2)))
	}

	"liftMap on an Option[List]" should "work with functions" in{
		val in = Option(List(1, 2))
		val out = in liftMap anyF

		same[Option[Int]](out, Option(1))
	}

	"liftFlatMap on an Option[List]" should "work" in{
		val in = Option(List(1))
		val out = in liftFlatMap intL

		same[Option[List[Int]]](out, Option(List(2, 3)))
	}

	"liftFlatMap on an Option[List]" should "work with functions" in{
		val in = Option(List(1, 2))
		val out = in liftFlatMap anyO

		same[Option[Int]](out, Option(1))
	}

	"liftAp on an Option[List]" should "work" in{
		val in = Option(List(1))
		val out = in liftAp List(intF)

		same[Option[List[Int]]](out, Option(List(2)))
	}

	"liftFoldLeft on a List" should "work" in{
		val in = List(1, 2, 3)
		val out = in.liftFoldLeft(0)(intintF _)

		same[Int](out, 6)
	}

	"liftFoldLeft on a List[Option]" should "work" in{
		val in = List(Option(1), Option(2), None)
		val out = in.liftFoldLeft(3)(intintF _)

		same[List[Int]](out, List(4, 5, 3))
	}

	"liftFoldAt on a List" should "work" in{
		val in = List(1, 2, 3)
		val out = in.liftFoldAt[List]

		same[Int](out, 6)
	}

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

	"liftFoldMap on a List" should "work" in{
		val in = List("1", "2", "3")
		val out = in.liftFoldMap{ x: String => x.toInt }

		same[Int](out, 6)
	}

	"liftFoldMap on a List[Option]" should "work" in{
		val in = List(Option("1"), Option("2"), Option("3"))
		val out = in.liftFoldMap{ x: String => x.toInt }

		same[List[Int]](out, List(1, 2, 3))
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

	"liftFlatten" should "work on a List of Lists" in{
		val in = List(List(1, 2), List(3, 4))
		val out = in.liftFlatten[List]

		same[List[Int]](out, List(1, 2, 3, 4))
	}

	"liftFlatten" should "work on an Option of List of Lists" in{
		val in = Option(List(List(1, 2), List(3, 4)))
		val out = in.liftFlatten[List]

		same[Option[List[Int]]](out, Option(List(1, 2, 3, 4)))
	}

	"liftFilter on a List" should "work" in{
		val in = List(1, 2, 3)
		val out = in.liftFilter(even)

		same[List[Int]](out, List(2))
	}

	"liftFilter on an Option[List]" should "work" in{
		val in = Option(List(1, 2, 3))
		val out = in.liftFilter(even)

		same[Option[List[Int]]](out, Option(List(2)))
	}

	"liftFilter on an NonEmptyList[List]" should "work work with functions" in{
		val in = NonEmptyList(List(1, 2, 3))
		val out = in.liftFilter{x: Any => false}

		same[NonEmptyList[List[Int]]](out, NonEmptyList(Nil))
	}

	"liftIntoF on List" should "work on a List" in{
		val lf = liftIntoF[List](intF)
		val out = lf(List(1, 2, 3))

		same[List[Int]](List(2, 3, 4), out)
	}

	"liftIntoF on a List" should "work on an Option[List]" in{
		val lf = liftIntoF[List](intF)
		val out = lf(Option(List(1)))

		same[Option[List[Int]]](Option(List(2)), out)
	}

	"liftIntoF on a List" should "work with functions" in{
		val lf = liftIntoF[List](anyF)
		val out = lf(List(1, 2, 3))

		same[List[Int]](out, List(1, 1, 1))
	}

	"liftIntoF on a List" should "work on first matching List" in{
		val lf = liftIntoF[List](anyF)
		val out = lf(List(List("1")))

		same[List[Int]](out, List(1))
	}

	"liftMap" should "work on a List" in{
		val lf = liftMap(intF)
		val out = lf(List(1))

		same[List[Int]](out, List(2))
	}

	"liftMap" should "work on an Option[List]" in{
		val lf = liftMap(intF)
		val out = lf(Option(List(1)))

		same[Option[List[Int]]](out, Option(List(2)))
	}

	"liftMap on a List" should "work with functions" in{
		val lf = liftMap(anyF)
		val out = lf(List(1, 2, 3))

		same[List[Int]](out, List(1, 1, 1))
	}

	"liftMap" should "compose with other liftMap" in{
		val lf = liftMap(anyF)
		val lf2 = liftMap(intF)
		val comp = lf andThen lf2
		val out = comp(List(4))

		same[List[Int]](out, List(2))
	}

	"liftAp" should "work on a List" in{
		val lf = liftAp(intAp)
		val out = lf(List(1))

		same[List[Int]](out, List(2))
	}

	"liftAp" should "work on an Option[List]" in{
		val lf = liftAp(intAp)
		val out = lf(Option(List(2, 3)))

		same[Option[List[Int]]](out, Option(List(3, 4)))
	}

	"liftAp" should "compose with other liftAp" in{
		val lf = liftAp(intAp)
		val lf2 = liftAp(List(anyF))
		val comp = lf andThen lf2
		val out = comp(List(4))

		same[List[Int]](out, List(1))
	}

	"liftFlatMap" should "work on a List" in{
		val lf = liftFlatMap(intL)
		val out = lf(List(1))

		same[List[Int]](out, List(2, 3))
	}

	"liftFlatMap" should "work on an Option[List]" in{
		val lf = liftFlatMap(intL)
		val out = lf(Option(List(2, 3)))

		same[Option[List[Int]]](out, Option(List(3, 4, 4, 5)))
	}

	"liftFlatMap on a List" should "work with functions" in{
		val lf = liftFlatMap(anyL)
		val out = lf(List(1, 2, 3))

		same[List[Int]](out, List(1, 1, 1))
	}

	"liftFlatMap" should "compose with other liftFlatMap" in{
		val lf = liftFlatMap(anyL)
		val lf2 = liftFlatMap(intL)
		val comp = lf andThen lf2
		val out = comp(List(1, 2, 3))

		same[List[Int]](out, List(2, 3, 2, 3, 2, 3))
	}

	"liftM2" should "work on a pair of List" in{
		val lf = liftM2(intintF)
		val out = lf(List(0, 1), List(1, 2))

		same[List[Int]](out, List(1, 2, 2, 3))
	}

	"liftM2" should "work on a pair of Option List" in{
		val lf = liftM2(intintF)
		val out = lf(Option(List(0, 1)), Option(List(1, 2)))

		same[Option[List[Int]]](out, Option(List(1, 2, 2, 3)))

		val out2 = lf(Option(List(0, 1)), None: Option[List[Int]])

		same[Option[List[Int]]](out2, None)
	}

	"liftM2" should "work with functions" in{
		val lf = liftM2(anyanyF)
		val out = lf(Option(2), Option('c'))

		same[Option[Int]](out, Option(1))
	}

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
		val out = lf(Option(2), Option(1))

		same[Option[Int]](out, Option(1))
	}
}