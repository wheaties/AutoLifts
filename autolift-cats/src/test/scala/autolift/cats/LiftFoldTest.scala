package autolift.cats

import cats.implicits._
import cats.data.Xor
import autolift.cats.fold._

class LiftFoldTest extends BaseSpec{
  "liftFold[List] on a List" should "work" in{
    val in = List(1, 2, 3)
    val out = in.liftFold[List]

    same[Int](out, 6)
  }

  "liftFold on a List" should "work" in{
    val in = List(1, 2, 3)
    val out = in.liftFold

    same[Int](out, 6)
  }

  "liftFold on a List[List]" should "work" in{
    val in = List(Nil, Nil, List(1))
    val out = in.liftFold

    same[List[Int]](out, List(1))
  }

  "liftFold[List] on a List[Option] w/ List" should "work" in{
    val in = List(Option(1), None, Option(2))
    val out = in.liftFold[List]

    same[Option[Int]](out, Option(3))
  }

  "liftFold[Option] on a List[Option] w/ Option" should "work" in{
    val in = List(Option(1), None, Option(2))
    val out = in.liftFold[Option]

    same[List[Int]](out, List(1, 0, 2))
  }

  "liftFold[List] on a Xor[List]" should "work" in{
    val in = Xor.right(List(1, 2))
    val out = in.liftFold[List]

    same[Xor[Nothing,Int]](out, Xor.right(3))
  }
}