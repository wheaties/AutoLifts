package autolift.cats

import cats.implicits._
import autolift.Cats._

class LiftSequenceTest extends BaseSpec{
  "liftSequence on a List[Option]" should "work" in{
  	val in = List(Option(1))
  	val out = in.liftSequence

  	same[Option[List[Int]]](out, Option(List(1)))
  }

  "liftSequence on a List[Option[List]]" should "work" in{
  	val in = List(Option(List(1)))
  	val out = in.liftSequence[Option]

  	same[List[List[Option[Int]]]](out, List(List(Option(1))))
  }
}