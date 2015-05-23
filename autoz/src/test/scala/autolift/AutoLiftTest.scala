package autolift

import org.scalatest._
import scalaz._
import Scalaz._
import All._

class AutoLiftTest extends FlatSpec{
	def same[A](x: A, y: A) = assert(x == y)

	val intF = { x: Int => x + 1 }
	val intL = { x: Int => List(x + 1) }
	def intintF(x: Int, y: Int) = x + y
	val anyF = { x: Any => 1 }
	val anyO = { x: Any => Option(1) }

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

		same[Option[List[Int]]](out, Option(List(2)))
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

	///////////////////////////////////////////////////////////////////////////

	//TODO: This below is folders

	///////////////////////////////////////////////////////////////////////////

	"foldWith on a List" should "work" in{
		val in = List("1")
		val out = in foldWith {x: String => x.toInt }

		same[Int](out, 1)
	}

	"foldWith on a List" should "work with functions" in{
		val in = List(1, 2)
		val out = in foldWith anyF

		same[Int](out, 2)
	}

	"foldWith on an Option[List]" should "work" in{
		val in = Option(List(1))
		val out = in foldWith intF

		same[Int](out, 2)
	}

	"foldAll on a List" should "work" in{
		val in = List(1, 2, 3)
		val out = in.foldAll

		same[Int](out, 6)
	}

	"foldAll on a List[Option]" should "work" in{
		val in = List(Option(1), Option(2), None)
		val out = in.foldAll

		same[Option[Int]](out, Option(3))
	}

	"foldAll on a List[List]" should "work on the List if the type A is not a Monoid" in{
		val in = List(List(1, "2"), List("3", 4))
		val out = in.foldAll

		same[Any](out, List(1, "2", "3", 4))
	}

	"foldOver on a List[Option] w/ List" should "work" in{
		val in = List(Option(1), None)
		val out = in.foldOver[List]

		same[Option[Int]](out, Option(1))
	}

	"foldOver on a List[Option] w/ Option" should "work" in{
		val in = List(Option(1), None)
		val out = in.foldOver[Option]

		same[Int](out, 1)
	}

	///////////////////////////////////////////////////////////////////////////

	//TODO: This below is transforms

	///////////////////////////////////////////////////////////////////////////

	"transformMap on a List" should "work" in{
		val in = List(1)
		val out = in transformMap intF

		same[List[Int]](out, List(2))
	}

	"transformMap on an Option[List]" should "work" in{
		val in = Option(List(1))
		val out = in transformMap intF

		same[Option[List[Int]]](out, Option(List(2)))
	}

	//TODO: ill defined test to show it only goes to inner most type

	"transformMap on an Option[List] w/ an Any => B" should "force application to the List" in{
		val in = Option(List(1))
		val out = in transformMap anyF

		same[Option[List[Int]]](out, Option(List(1)))
	}

	"transformAp on a List" should "work" in{
		val in = List(1)
		val out = in transformAp List(intF)

		same[List[Int]](out, List(2))
	}

	"transformAp on an Option[List]" should "work" in{
		val in = Option(List(1))
		val out = in transformAp Option(List(intF))

		same[Option[List[Int]]](out, Option(List(2)))
	}
}