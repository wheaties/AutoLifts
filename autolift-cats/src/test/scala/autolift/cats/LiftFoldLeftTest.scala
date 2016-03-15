package autolift.cats

import cats.implicits._
import cats.data.Xor
import autolift.Cats._

class LiftFoldLeftTest extends BaseSpec{
  "liftFoldLeft on a List" should "work" in{
    val in = List(1, 2, 3)
    val out = in.liftFoldLeft(0)(intintF)

    same[Int](out, 6)
  }

  "liftFoldLeft on a List[Option]" should "work" in{
    val in = List(Option(1), Option(2), None)
    val out = in.liftFoldLeft(3)(intintF)

    same[List[Int]](out, List(4, 5, 3))
  }

  "liftFoldLeft on an Option[Xor[List]]" should "work" in{
    val in = Option(Xor.right(List(1, 2)))
    val out = in.liftFoldLeft(0)(intintF)

    same[Option[Xor[Nothing,Int]]](out, Option(Xor.right(3)))
  }
}