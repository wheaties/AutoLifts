package autolift.cats

import cats.{Eval, Now}
import cats.implicits._
import cats.data.Xor
import autolift.cats.fold._

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

  "liftFoldRight on a Xor[List]" should "work" in{
    val in = Xor.right(List(1, 2, 3))
    val out = in.liftFoldRight(Now(0))(plus).map(_.value)

    same[Xor[Nothing,Int]](out, Xor.right(6))
  }

  "LiftedFoldRight on a List[Option]" should "work" in{
    val fn = liftFoldRight(Now(0))(plus)
    val out = fn(List(Option(1), Option(2), None)).map(_.value)

    same[List[Int]](out, List(1, 2, 0))
  }

  "LiftedFoldRight on an Xor[List]" should "work" in{
    val fn = liftFoldRight(Now(0))(plus)
    val out = fn(Xor.right(List(1,2))).map(_.value)

    same[Xor[Nothing,Int]](out, Xor.right(3))
  }
}