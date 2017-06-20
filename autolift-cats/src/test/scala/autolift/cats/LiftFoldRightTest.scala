package autolift.cats

import cats.{Eval, Now}
import cats.implicits._
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

  "liftFoldRight on a Either[List]" should "work" in{
    val in = Either.right(List(1, 2, 3))
    val out = in.liftFoldRight(Now(0))(plus).map(_.value)

    same[Either[Nothing,Int]](out, Either.right(6))
  }

  "LiftedFoldRight on a List[Option]" should "work" in{
    val fn = liftFoldRight(Now(0))(plus)
    val out = fn(List(Option(1), Option(2), None)).map(_.value)

    same[List[Int]](out, List(1, 2, 0))
  }

  "LiftedFoldRight on an Either[List]" should "work" in{
    val fn = liftFoldRight(Now(0))(plus)
    val out = fn(Either.right(List(1,2))).map(_.value)

    same[Either[Nothing,Int]](out, Either.right(3))
  }
}