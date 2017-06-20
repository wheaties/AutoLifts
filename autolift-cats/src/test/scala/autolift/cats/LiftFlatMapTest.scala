package autolift.cats

import cats.implicits._
import autolift.cats.monad._

class LiftFlatMapTest extends BaseSpec{
  "liftFlatMap on an Option[List]" should "work" in{
    val in = Option(List(1))
    val out = in liftFlatMap intL

    same[Option[List[Int]]](out, Option(List(2, 3)))
  }

  "liftFlatMap on an Option[List]" should "work with functions" in{
    val in = Option(List(1, 2))
    val out = in liftFlatMap anyO

    same[Option[Int]](out, Option(1))
  }

  "liftFlatMap on a Either[List]" should "work" in{
    val in = Either.right(List(1))
    val out = in liftFlatMap { x: Int => List(x+1) }

    same[Either[Nothing,List[Int]]](out, Either.right(List(2)))
  }

  "liftFlatMap on an Option[Either[List]]" should "work" in{
    val in = Option(Either.right(List(1)))
    val out = in liftFlatMap { x: Int => List(x+1) }

    same[Option[Either[Nothing,List[Int]]]](out, Option(Either.right(List(2))))
  }

  "LiftedBind" should "work on a List" in{
    val lf = liftFlatMap(intL)
    val out = lf(List(1))

    same[List[Int]](out, List(2, 3))
  }

  "LiftedBind" should "work on an Option[List]" in{
    val lf = liftFlatMap(intL)
    val out = lf(Option(List(2, 3)))

    same[Option[List[Int]]](out, Option(List(3, 4, 4, 5)))
  }

  "LiftedBind on a List" should "work with functions" in{
    val lf = liftFlatMap(anyL)
    val out = lf(List(1, 2, 3))

    same[List[Int]](out, List(1, 1, 1))
  }

  "LiftedBind" should "andThen with other liftFlatMap" in{
    val lf = liftFlatMap(anyL)
    val lf2 = liftFlatMap(intL)
    val comp = lf andThen lf2
    val out = comp(List(1, 2, 3))

    same[List[Int]](out, List(2, 3, 2, 3, 2, 3))
  }

  "LiftedBind" should "compose with other liftFlatMap" in{
    val lf = liftFlatMap(anyL)
    val lf2 = liftFlatMap(intL)
    val comp = lf2 compose lf
    val out = comp(List(1, 2, 3))

    same[List[Int]](out, List(2, 3, 2, 3, 2, 3))
  }

  "LiftedBind" should "map" in{
    val lf = liftFlatMap(intL) map (_ + 1)
    val out = lf(List(0))

    same[List[Int]](out, List(2, 3))
  }
}