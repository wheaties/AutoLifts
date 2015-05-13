package autolift

import org.scalatest._
import scalaz._
import Scalaz._
import Lifters._

class LiftersTest extends FlatSpec{
	def same[A](x: A, y: A) = assert(x == y)

	val intF = {x: Int => x+1}
	val intL = {x: Int => List(x+1, x+2)}
	val anyF = {x: Any => 1}

	"liftF on List" should "work on a List" in{
		val lf = liftF[List](intF)
		val out = lf(List(1, 2, 3))

		same[List[Int]](List(2, 3, 4), out)
	}

	"liftF on a List" should "work on an Option[List]" in{
		val lf = liftF[List](intF)
		val out = lf(Option(List(1)))

		same[Option[List[Int]]](Option(List(2)), out)
	}

	"liftF on a List" should "work with functions" in{
		val lf = liftF[List](anyF)
		val out = lf(List(1, 2, 3))

		same[List[Int]](out, List(1, 1, 1))
	}

	"liftF on a List" should "work on first matching List" in{
		val lf = liftF[List](anyF)
		val out = lf(List(List("1")))

		same[List[Int]](out, List(1))
	}

	"liftM on a List" should "work on a List" in{
		val lf = liftM[List](intL)
		val out = lf(List(1, 2))

		same[List[Int]](out, List(2, 3, 3, 4))
	}

	"liftM on a List" should "work on an Option[List]" in{
		val lf = liftM[List](intL)
		val out = lf(Option(List(1, 2)))

		same[Option[List[Int]]](out, Option(List(2, 3, 3, 4)))
	}
}