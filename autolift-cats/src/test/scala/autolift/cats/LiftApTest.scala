package autolift.cats

import cats.implicits._
import cats.data.Xor
import autolift.cats.applicative._

class LiftApTest extends BaseSpec{
  "liftAp on an Option[List]" should "work" in{
    val in = Option(List(1))
    val out = in liftAp List(intF)

    same[Option[List[Int]]](out, Option(List(2)))
  }

  "liftAp on a Xor[List]" should "work" in{
    val in = Xor.right(List(1))
    val out = in liftAp List(intF)

    same[Xor[Nothing,List[Int]]](out, Xor.right(List(2)))
  }

  "LiftedAp" should "work on a List" in{
    val lf = liftAp(intAp)
    val out = lf(List(1))

    same[List[Int]](out, List(2))
  }

  "LiftedAp" should "work on an Option[List]" in{
    val lf = liftAp(intAp)
    val out = lf(Option(List(2, 3)))

    same[Option[List[Int]]](out, Option(List(3, 4)))
  }

  "LiftedAp" should "andThen with other liftAp" in{
    val lf = liftAp(intAp)
    val lf2 = liftAp(List(anyF))
    val comp = lf andThen lf2
    val out = comp(List(4))

    same[List[Int]](out, List(1))
  }

  "LiftedAp" should "compose with other liftAp" in{
    val lf = liftAp(intAp)
    val lf2 = liftAp(List(anyF))
    val comp = lf2 compose lf
    val out = comp(List(4))

    same[List[Int]](out, List(1))
  }

  "LiftedAp" should "map" in{
    val lf = liftAp(intAp) map(_ + 1)
    val out = lf(Option(List(1)))

    same[Option[List[Int]]](out, Option(List(3)))
  }
}