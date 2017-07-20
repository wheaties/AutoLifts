package autolift.test.scalaz

import scalaz._
import Scalaz._
import autolift.Scalaz._

class LiftZipWithTest extends BaseSpec{

  "liftZipWith on a Option[Int] from a Option[Int]" should "work" in{
    val in = Option(1)
    val out = in.liftZipWith(Option(1))(intintF)

    same[Option[Int]](out, Option(2))
  }

  "liftZipWith on a Option[List] on a List" should "work" in{
    val in = Option(List(1, 2))
    val out = in.liftZipWith(List(1, 2))(intintF)

    same[Option[List[Int]]](out, Option(List(2, 4)))
  }

  "liftZipWith on a Disjunction[List]" should "work" in{
    val in: Int \/ List[Int] = \/.right(List(1, 2))
    val out = in.liftZipWith(List(1, 2))(intintF)

    same[Int \/ List[Int]](out, \/.right(List(2, 4)))
  }

  "LiftedZipWith on a Option[List]" should "work" in{
    val lf = liftZipWith(intintF)
    val out = lf(Option(List(1)), List(1))

    same[Option[List[Int]]](out, Option(List(2)))
  }

  "LiftedZipWith" should "map" in{
    val lf = liftZipWith(intintF)
    val lf2 = lf map (_ + 1)
    val out = lf2(Option(List(1, 2)), List(1, 2))

    same[Option[List[Int]]](out, Option(List(3, 5)))
  }
}