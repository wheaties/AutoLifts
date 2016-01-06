package autolift.test.algebird

import autolift._
import Algebird._
import org.scalatest._

class LiftFilterTest extends BaseSpec{
	val intF = { x: Int => x % 2 == 0 }
	val anyF = { x: Any => false }

	"liftFilter on a List" should "work" in{
		val in = List(1)
		val out = in liftFilter intF

		same[List[Int]](out, List.empty[Int])
	}
	"liftFilter on a Bar[List]" should "work" in{
		val in = Bar(List(1))
		val out = in liftFilter intF

		same[Bar[List[Int]]](out, Bar(List.empty[Int]))
	}
	"liftFilter on a Bar[List]" should "work with functions" in{
		val in = Bar(List(1))
		val out = in liftFilter anyF

		same[Bar[List[Int]]](out, Bar(List.empty[Int]))
	}
}