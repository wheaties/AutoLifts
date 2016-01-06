package autolift.scalaz

import scalaz._
import Scalaz._
import autolift._
import autolift.Scalaz._

class FoldAllTest extends BaseSpec{
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