package autolift.test.scalaz

import scalaz._
import Scalaz._
import autolift.Scalaz._

class LiftTraverseTest extends BaseSpec{
  def anyI(in: Int) = Option(in)

  "liftTraverse on a List" should "work" in{
    val in = List(1, 2, 3)
    val out = in.liftTraverse(anyI)

    same[Option[List[Int]]](out, Option(List(1, 2, 3)))
  }

  "liftTraverse on a List[List]" should "work" in{
    val in = List(List(1), List(2), List(3))
    val out = in.liftTraverse(anyI)

    same[List[Option[List[Int]]]](out, List(Option(List(1)), Option(List(2)), Option(List(3))))
  }

  "liftTraverse on a List" should "work with functions" in{
    val in = List(1, 2, 3)
    val out = in.liftTraverse(anyO)

    same[Option[List[Int]]](out, Option(List(1, 1, 1)))
  }

  "LiftedTraverse on a List" should "work" in{
    val fn = liftTraverse(anyI)
    val out = fn(List(1, 2, 3))

    same[Option[List[Int]]](out, Option(List(1, 2, 3)))
  }

  "LiftedTraverse on a List[List]" should "work" in{
    val fn = liftTraverse(anyI)
    val out = fn(List(List(1, 2), List(3)))

    same[List[Option[List[Int]]]](out, List(Option(List(1, 2)), Option(List(3))))
  }
}