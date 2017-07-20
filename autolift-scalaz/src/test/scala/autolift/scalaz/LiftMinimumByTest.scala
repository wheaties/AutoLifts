package autolift.test.scalaz

import scalaz._
import Scalaz._
import autolift.Scalaz._

class LiftMinimumByTest extends BaseSpec{
  "liftMinimumBy on a List with identity" should "work" in{
    val in = List(1, 2, 3)
    val out = in.liftMinBy(identity[Int])

    same[Option[Int]](out, Option(1))
  }

  "liftMinimumBy on a Option[List]" should "work" in{
    val in = Option(List("1", "2", "3"))
    val out = in.liftMinBy(s2i)

    same[Option[Option[String]]](out, Option(Option("1")))
  }

  "liftMinimumBy on a List[List]" should "work with functions" in{
    val in = List(List("1"), List("2"), List("3"))
    val out = in.liftMinBy(anyF)

    same[Option[List[String]]](out, Option(List("3")))
  }

  "liftMinimumBy on a List[Option]" should "work" in{
    val in = List(None, None, Some("1"))
    val out = in.liftMinBy(s2i)

    same[List[Option[String]]](out, List(None, None, Some("1")))
  }

  "LiftedMinimumBy" should "work" in{
    val fn = liftMinBy(s2i)
    val out = fn(List(List("1", "2")))

    same[List[Option[String]]](out, List(Option("1")))
  }

  "LiftedMinimumBy" should "map" in{
    val lf = liftMinBy(s2i)
    val fn = lf map {_ + 1}
    val out = fn(List(List("1", "2")))

    same[List[Option[String]]](out, List(Option("1")))
  }
}