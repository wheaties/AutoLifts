package autolift.cats

import cats.implicits._
import autolift.Cats._

class FoldAllTest extends BaseSpec{
  "foldAll on a List" should "work" in{
    val in = List(1, 2, 3)

    assert(in.foldForall{x: Int => x < 4})
    assert(!in.foldForall{x: Int => x < 0})
  }

  "foldAll on a List" should "work with functions" in{
    val in = List(1, 2, 3)

    assert(in.foldForall{x: Any => true})
    assert(!in.foldForall{x: Any => false})
  }

  "foldAll on a List[Option]" should "work" in{
    val in = List(Option(1), None, Option(2))

    assert(in.foldForall{x: Int => x < 3})
    assert(!in.foldForall{x: Int => x < 2})
  }
}