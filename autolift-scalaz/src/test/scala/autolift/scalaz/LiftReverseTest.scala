package autolift.test.scalaz

import scalaz._
import Scalaz._
import autolift.Scalaz._

class LiftReverseTest extends BaseSpec{
  "liftReverse on a List" should "work" in{
    val in = List(1, 2, 3)
    val out = in.liftReverse

    same[List[Int]](out, List(3, 2, 1))
  }

  "liftReverse on a NEList[List]" should "work" in{
    val in = NonEmptyList(List(1, 2, 3), List(4, 5, 6))
    val out = in.liftReverse[List]

    same[NonEmptyList[List[Int]]](out, NonEmptyList(List(3, 2, 1), List(6, 5, 4)))
  }
}