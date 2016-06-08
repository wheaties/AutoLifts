package autolift.cats

import cats.implicits._
import cats.data.Xor
import autolift.cats.fold._

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

  "liftFoldLeft on an Xor[List]" should "work" in{
    val in = Xor.right(List(1, 2))
    val out = in.liftFoldLeft(0)(intintF)

    same[Xor[Nothing,Int]](out, Xor.right(3))
  }
}