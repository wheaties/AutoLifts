package autolift.test.scalaz

import scalaz._
import Scalaz._
import autolift.Scalaz._

class LiftMapTest extends BaseSpec{
  "liftMap on an Option[List]" should "work" in{
    val in = Option(List(1))
    val out = in liftMap intF

    same[Option[List[Int]]](out, Option(List(2)))
  }

  "liftMap on an Option[List]" should "work with functions" in{
    val in = Option(List(1, 2))
    val out = in liftMap anyF

    same[Option[Int]](out, Option(1))
  }

  "liftMap on a Disjunction[List]" should "work" in{
    val in: Int \/ List[Int] = \/.right(List(1))
    val out = in liftMap intF

    same[Int \/ List[Int]](out, \/.right(List(2)))
  }

  //TODO: investigate, \/ has a Functor...
  /*"liftMap on a List[Disjunction]" should "work" in{
    val in: List[Int \/ Int] = List(\/.right(1))
    val out = in liftMap intF

    same[List[Int \/ Int]](out, List(\/.right(2)))
  }*/

  "LiftedMap" should "work on a List" in{
    val lf = liftMap(intF)
    val out = lf(List(1))

    same[List[Int]](out, List(2))
  }

  "LiftedMap" should "work on an Option[List]" in{
    val lf = liftMap(intF)
    val out = lf(Option(List(1)))

    same[Option[List[Int]]](out, Option(List(2)))
  }

  "LiftedMap on a List" should "work with functions" in{
    val lf = liftMap(anyF)
    val out = lf(List(1, 2, 3))

    same[List[Int]](out, List(1, 1, 1))
  }

  "LiftedMap" should "map" in{
    val lf = liftMap(intF) map(_ + 1)
    val out = lf(List(0, 1, 2))

    same[List[Int]](out, List(2, 3, 4))
  }

  "LiftedMap" should "andThen with other LiftMap" in{
    val lf = liftMap(anyF)
    val lf2 = liftMap(intF)
    val comp = lf andThen lf2
    val out = comp(List(4))

    same[List[Int]](out, List(2))
  }

  "LiftedMap" should "compose with other LiftMap" in{
    val lf = liftMap(anyF)
    val lf2 = liftMap(intF)
    val comp = lf2 compose lf
    val out = comp(List(4))

    same[List[Int]](out, List(2))
  }
}