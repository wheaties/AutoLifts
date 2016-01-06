package autolift.scalaz

import scalaz._
import Scalaz._
import autolift._
import autolift.Scalaz._

class LiftFoldRightTest extends BaseSpec{
	def plus(x: Int, y: => Int) = x + y

	"liftFoldRight on a List" should "work" in{
		val in = List(1, 2, 3)
		val out = in.liftFoldRight(0)(plus)

		same[Int](out, 6)
	}

	"liftFoldRight on a List[Option]" should "work" in{
		val in = List(Option(1), Option(2), None)
		val out = in.liftFoldRight(3)(plus)

		same[List[Int]](out, List(4, 5, 3))
	}
}