package autolift


import org.scalatest._
import scalaz._
import Scalaz._
import AutoLift._

class AutoLiftTest extends FlatSpec{
	def same[A](x: A, y: A) = assert(x == y)

	"liftMap on an Option[List]" should "work" in{
		val in = Option(List(1))
		val out = in.liftMap{ x: Int => x + 1}

		same(out, Option(List(2)))
	}

	"liftFlatMap on an Option[List]" should "work" in{
		val in = Option(List(1))
		val out = in.liftFlatMap{ x: Int => List(x + 1)}

		same(out, Option(List(2)))
	}

	"liftAp on an Option[List]" should "work" in{
		val in = Option(List(1))
		val out = in.liftAp(List({x: Int => x + 1}))

		same(out, Option(List(2)))
	}
}