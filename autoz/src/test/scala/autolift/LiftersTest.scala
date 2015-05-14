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
	val anyF = {x: Any => 1}
	val anyL = {x: Any => List(1)}

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

	"liftIntoM on a List" should "work on a List" in{
		val lf = liftIntoM[List](intL)
		val out = lf(List(1, 2))

		same[List[Int]](out, List(2, 3, 3, 4))
	}

	"liftIntoM on a List" should "work on an Option[List]" in{
		val lf = liftIntoM[List](intL)
		val out = lf(Option(List(1, 2)))

		same[Option[List[Int]]](out, Option(List(2, 3, 3, 4)))
	}

	"liftIntoM on a List" should "work with functions" in{
		val lf = liftIntoM[List](anyL)
		val out = lf(List("2"))

		same[List[Int]](out, List(1))
	}

	"liftIntoM on a List" should "work on first matching List" in{
		val lf = liftIntoM[List](anyL)
		val out = lf(List(List("2")))

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