package autolift.test.scalaz

import scalaz._
import Scalaz._
import autolift.Scalaz._

class LiftFoldLeftTest extends BaseSpec{
  "liftFoldLeft on a List" should "work" in{
    val in = List(1, 2, 3)
    val out = in.liftFoldLeft(0)(intintF _)

    same[Int](out, 6)
  }

  "liftFoldLeft on a List[Option]" should "work" in{
    val in = List(Option(1), Option(2), None)
    val out = in.liftFoldLeft(3)(intintF _)

    same[List[Int]](out, List(4, 5, 3))
  }

  "liftFoldLeft on a Disjunction[List]" should "work" in{
    val in: Int \/ List[Int] = \/.right(List(1, 2, 3))
    val out = in.liftFoldLeft(4)(intintF)

    same[Int \/ Int](out, \/.right(10))
  }
}