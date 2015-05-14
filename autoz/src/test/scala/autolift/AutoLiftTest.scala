package autolift


import org.scalatest._
import scalaz._
import Scalaz._
import AutoLift._

class AutoLiftTest extends FlatSpec{
	def same[A](x: A, y: A) = assert(x == y)

	val intF = { x: Int => x + 1 }
	val intL = { x: Int => List(x + 1) }
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
		val out = in.liftAp(List({x: Int => x + 1}))

		same(out, Option(List(2)))
	}
}