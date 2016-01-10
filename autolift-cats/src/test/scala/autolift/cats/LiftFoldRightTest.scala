package autolift.cats

import cats.{Eval, Now}
import cats.implicits._
import autolift.Cats._

class LiftFoldRightTest extends BaseSpec{
  def plus(x: Int, y: Eval[Int]) = y.map(_ + x)

  "liftFoldRight on a List" should "work" in{
    val in = List(1, 2, 3)
    val out = in.liftFoldRight(Now(0))(plus).value

    same[Int](out, 6)
  }

  "liftFoldRight on a List[Option]" should "work" in{
    val in = List(Option(1), Option(2), None)
    val out = in.liftFoldRight(Now(3))(plus).map(_.value)

    same[List[Int]](out, List(4, 5, 3))
  }
}