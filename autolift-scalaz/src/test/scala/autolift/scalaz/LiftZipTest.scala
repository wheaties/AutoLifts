package autolift.test.scalaz

import scalaz._
import Scalaz._
import autolift.Scalaz._

class LiftZipTest extends BaseSpec{
  "liftZip on a Option[Int] from a Option[Int]" should "work" in{
    val in = Option(1)
    val out = in liftZip in

    same[Option[(Int,Int)]](out, Option((1, 1)))
  }

  "liftZip on a Option[List[Int]] on a List[Int]" should "work" in{
    val arg = List(1, 2)
    val in = Option(arg)
    val out = in liftZip arg

    same[Option[List[(Int,Int)]]](out, in map (_ zip arg))
  }

  "liftZip on a Disjunction[List] with a List" should "work" in{
    val arg = List(1, 2)
    val in: Int \/ List[Int] = \/.right(arg)
    val out = in liftZip arg

    same[Int \/ List[(Int,Int)]](out, in map (_ zip arg))
  }
}