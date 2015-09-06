package autolift

import org.scalatest._
import scalaz._
import Scalaz._
import Lifters._

class LiftersTest extends FlatSpec{
	def same[A](x: A, y: A) = assert(x == y)

	val intF = {x: Int => x+1}
	val intL = {x: Int => List(x+1, x+2)}
	val intAp = List(intF)
	def intintF(x: Int, y: Int) = x + y
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

	"liftFilter on a List" should "work" in{
		val in = List(1, 2, 3)
		val out = in.liftFilter(even)

		assert(out == List(2))
	}

	"liftFilter on an Option[List]" should "work" in{
		val in = Option(List(1, 2, 3))
		val out = in.liftFilter(even)

		assert(out == Option(List(2)))
	}

	"liftFilter on an Option[List]" should "work work with functions" in{
		val in = NonEmptyList(List(1, 2, 3))
		val out = in.liftFilter{x: Any => false}

		assert(out == NonEmptyList(Nil))
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

	"liftF" should "work on a List" in{
		val lf = liftF(intF)
		val out = lf(List(1))

		same[List[Int]](out, List(2))
	}

	"liftF" should "work on an Option[List]" in{
		val lf = liftF(intF)
		val out = lf(Option(List(1)))

		same[Option[List[Int]]](out, Option(List(2)))
	}

	"liftF on a List" should "work with functions" in{
		val lf = liftF(anyF)
		val out = lf(List(1, 2, 3))

		same[List[Int]](out, List(1, 1, 1))
	}

	"liftF" should "compose with other liftF" in{
		val lf = liftF(anyF)
		val lf2 = liftF(intF)
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

	"liftM" should "work on a List" in{
		val lf = liftM(intL)
		val out = lf(List(1))

		same[List[Int]](out, List(2, 3))
	}

	"liftM" should "work on an Option[List]" in{
		val lf = liftM(intL)
		val out = lf(Option(List(2, 3)))

		same[Option[List[Int]]](out, Option(List(3, 4, 4, 5)))
	}

	"liftM on a List" should "work with functions" in{
		val lf = liftM(anyL)
		val out = lf(List(1, 2, 3))

		same[List[Int]](out, List(1, 1, 1))
	}
}