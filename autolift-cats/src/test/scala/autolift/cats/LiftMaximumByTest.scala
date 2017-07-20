package autolift.cats

import cats.implicits._
import autolift.cats.fold._

class LiftMaximumByTest extends BaseSpec{
  "liftMaximumBy on a List with identity" should "work" in{
    val in = List(1, 2, 3)
    val out = in.liftMaxBy(identity[Int])

    same[Option[Int]](out, Option(3))
  }

  "liftMaximumBy on a Option[List]" should "work" in{
    val in = Option(List("1", "2", "3"))
    val out = in.liftMaxBy(s2i)

    same[Option[Option[String]]](out, Option(Option("3")))
  }

  "liftMaximumBy on a List[List]" should "work with functions" in{
    val in = List(List("1"), List("2"), List("3"))
    val out = in.liftMaxBy(anyF)

    same[Option[List[String]]](out, Option(List("3")))
  }

  "liftMaximumBy on a List[Option]" should "work" in{
    val in = List(None, None, Some("1"))
    val out = in.liftMaxBy(s2i)

    same[List[Option[String]]](out, List(None, None, Some("1")))
  }

  "LiftedMaximumBy" should "work" in{
    val fn = liftMaxBy(s2i)
    val out = fn(List(List("1", "2")))

    same[List[Option[String]]](out, List(Option("2")))
  }

  "LiftedMaximumBy" should "map" in{
    val lf = liftMaxBy(s2i)
    val fn = lf map {_ + 1}
    val out = fn(List(List("1", "2")))

    same[List[Option[String]]](out, List(Option("2")))
  }
}