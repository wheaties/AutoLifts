package autolift.scalaz

import scalaz._
import Scalaz._
import autolift._
import autolift.Scalaz._

class FoldOverTest extends BaseSpec{
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
}