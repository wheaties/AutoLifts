package autolift.test.scalaz

import scalaz._
import Scalaz._
import autolift.Scalaz._

class LiftFlattenTest extends BaseSpec{
  "liftFlatten" should "work on a List of Lists" in{
    val in = List(List(1, 2), List(3, 4))
    val out = in.liftFlatten[List]

    same[List[Int]](out, List(1, 2, 3, 4))
  }

  "liftFlatten" should "work on an Option of List of Lists" in{
    val in = Option(List(List(1, 2), List(3, 4)))
    val out = in.liftFlatten[List]

    same[Option[List[Int]]](out, Option(List(1, 2, 3, 4)))
  }

  "liftFlatten" should "work without specifying the type if it's obvious" in{
    val in = Option(List(List(1)))
    val out = in.liftFlatten

    same[Option[List[Int]]](out, Option(List(1)))
  }

  "liftFlatten" should "work on a Disjunction of List of Lists" in{
    val in: Int \/ List[List[Int]] = \/.right(List(List(1, 2), List(3, 4)))
    val out = in.liftFlatten

    same[Int \/ List[Int]](out, \/.right(List(1, 2, 3, 4)))
  }
}