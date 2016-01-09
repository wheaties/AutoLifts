package autolift.cats

import cats.implicits._
import autolift.Cats._

class LiftFoldMapTest extends BaseSpec{
  "liftFoldMap on a List" should "work" in{
    val in = List("1", "2", "3")
    val out = in.liftFoldMap{ x: String => x.toInt }

    same[Int](out, 6)
  }

  "liftFoldMap on a List[Option]" should "work" in{
    val in = List(Option("1"), Option("2"), Option("3"))
    val out = in.liftFoldMap{ x: String => x.toInt }

    same[List[Int]](out, List(1, 2, 3))
  }

  "LiftedFoldMap" should "work on a List" in{
    val lf = liftFoldMap(intF)
    val out = lf(List(1, 2, 3))

    same[Int](out, 9)
  }

  "LiftedFoldMap" should "work on an Option[List]" in{
    val lf = liftFoldMap(intF)
    val out = lf(Option(List(1, 2, 3)))

    same[Option[Int]](out, Option(9))
  }

  "LiftedFoldMap" should "andThen with other liftFoldMap" in{
    val lf = liftFoldMap(intF)
    val lf2 = liftFoldMap(anyF)
    val comp = lf andThen lf2
    val out = comp(List(1, 2, 3))

    same[Int](out, 3)
  }

  "LiftedFoldMap" should "compose with other liftFoldMap" in{
    val lf = liftFoldMap(intF)
    val lf2 = liftFoldMap(anyF)
    val comp = lf2 compose lf
    val out = comp(List(1, 2, 3))

    same[Int](out, 3)
  }

  "LiftedFoldMap" should "map" in{
    val lf = liftFoldMap(intF) map(_ + 1)
    val out = lf(List(1, 2, 3))

    same[Int](out, 12)
  }
}