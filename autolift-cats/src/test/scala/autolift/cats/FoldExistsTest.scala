package autolift.cats

import cats.implicits._
import autolift.Cats._

class FoldExistsTest extends BaseSpec {
  "foldExists on a List" should "work" in{
    val in = List(1, 2, 3)

    assert(in.foldExists{x: Int => x < 2})
    assert(!in.foldExists{x: Int => x < 0})
  }

  "foldExists on a List" should "work with functions" in{
    val in = List(1, 2, 3)

    assert(in.foldExists{x: Any => true})
    assert(!in.foldExists{x: Any => false})
  }

  "foldExists on a List[Option]" should "work" in{
    val in = List(Option(1), None, Option(2))

    assert(in.foldExists{x: Int => x < 2})
    assert(!in.foldExists{x: Int => x < 0})
  }
}