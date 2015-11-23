package autolift

import org.scalatest._
import scalaz._
import Scalaz._
import AutoLift._

class FoldersTest extends FlatSpec{
	def same[A](x: A, y: A) = assert(x == y)

	val s2i = {x: String => x.toInt }
	val intF = { x: Int => x + 1 }
	val intL = { x: Int => List(x + 1) }
	val anyF = { x: Any => 1 }
	val anyO = { x: Any => Option(1) }

	"foldWith on a List" should "work" in{
		val in = List("1")
		val out = in foldWith s2i

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

	"foldComplete on a List" should "work" in{
		val in = List(1, 2, 3)
		val out = in.foldComplete

		same[Int](out, 6)
	}

	"foldComplete on a List[Option]" should "work" in{
		val in = List(Option(1), Option(2), None)
		val out = in.foldComplete

		same[Option[Int]](out, Option(3))
	}

	"foldComplete on a List[List]" should "work on the List if the type A is not a Monoid" in{
		val in = List(List(1, "2"), List("3", 4))
		val out = in.foldComplete

		same[List[Any]](out, List(1, "2", "3", 4))
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

	"foldAny on a List" should "work" in{
		val in = List(1, 2, 3)
		
		assert(in.foldAny{x: Int => x < 2})
		assert(!in.foldAny{x: Int => x < 0})
	}

	"foldAny on a List" should "work with functions" in{
		val in = List(1, 2, 3)
		
		assert(in.foldAny{x: Any => true})
		assert(!in.foldAny{x: Any => false})
	}

	"foldAny on a List[Option]" should "work" in{
		val in = List(Option(1), None, Option(2))
		
		assert(in.foldAny{x: Int => x < 2})
		assert(!in.foldAny{x: Int => x < 0})
	}

	"foldAll on a List" should "work" in{
		val in = List(1, 2, 3)
		
		assert(in.foldAll{x: Int => x < 4})
		assert(!in.foldAll{x: Int => x < 0})
	}

	"foldAll on a List" should "work with functions" in{
		val in = List(1, 2, 3)
		
		assert(in.foldAll{x: Any => true})
		assert(!in.foldAll{x: Any => false})
	}

	"foldAll on a List[Option]" should "work" in{
		val in = List(Option(1), None, Option(2))
		
		assert(in.foldAll{x: Int => x < 3})
		assert(!in.foldAll{x: Int => x < 2})
	}
}