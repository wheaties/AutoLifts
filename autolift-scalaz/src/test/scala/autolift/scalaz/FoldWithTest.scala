package autolift.test.scalaz

import scalaz._
import Scalaz._
import autolift.Scalaz._

class FoldWithTest extends BaseSpec{
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
}