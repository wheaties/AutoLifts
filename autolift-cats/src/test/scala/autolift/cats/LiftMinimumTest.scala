package autolift.cats

import cats.implicits._
import autolift.cats.fold._

class LiftMinimumTest extends BaseSpec{
  "liftMinimum on a List" should "work" in{
    val in = List(1, 2, 3)
    val out = in.liftMin

    same[Option[Int]](out, Option(1))
  }

  "liftMinimum on a Option[List]" should "work" in{
    val in = Option(List(1, 2, 3))
    val out = in.liftMin[Int]

    same[Option[Option[Int]]](out, Option(Option(1)))
  }

  "liftMinimum on a List[List]" should "work" in{
    val in = List(Nil, Nil, List(1))
    val out = in.liftMin[Int]

    same[List[Option[Int]]](out, List(None, None, Option(1)))
  }
}