package autolift.cats

import cats.implicits._
import cats.data.NonEmptyList
import autolift.cats.monad._

class LiftFilterTest extends BaseSpec{
  "liftFilter on a List" should "work" in{
    val in = List(1, 2, 3)
    val out = in.liftFilter(even)

    same[List[Int]](out, List(2))
  }

  "liftFilter on an Option[List]" should "work" in{
    val in = Option(List(1, 2, 3))
    val out = in.liftFilter(even)

    same[Option[List[Int]]](out, Option(List(2)))
  }

  "liftFilter on an NonEmptyList[List]" should "work work with functions" in{
    val in: NonEmptyList[List[Int]] = NonEmptyList(List(1, 2, 3))
    val out = in.liftFilter{x: Any => false}

    same[NonEmptyList[List[Int]]](out, NonEmptyList(Nil))

  }
}