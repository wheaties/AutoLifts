package autolift.cats

import cats.implicits._
import autolift.cats.fold._

class LiftMaximumTest extends BaseSpec{
  "liftMaximum on a List" should "work" in{
    val in = List(1, 2, 3)
    val out = in.liftMax

    same[Option[Int]](out, Option(3))
  }

  "liftMaximum on a Option[List]" should "work" in{
    val in = Option(List(1, 2, 3))
    val out = in.liftMax[Int]

    same[Option[Option[Int]]](out, Option(Option(3)))
  }

  "liftMaximum on a List[List]" should "work" in{
    val in = List(Nil, Nil, List(1))
    val out = in.liftMax[Int]

    same[List[Option[Int]]](out, List(None, None, Option(1)))
  }

  "liftMaximum on a List[Option]" should "work" in{
    val in = List(None, None, Some(1))
    val out = in.liftMax

    same[Option[Option[Int]]](out, Option(Option(1)))
  }
}