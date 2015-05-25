package autolift

import org.scalatest._
import scalaz._
import Scalaz._
import AutoLift._

class TransformersTest extends FlatSpec{
	def same[A](x: A, y: A) = assert(x == y)

	val intF = { x: Int => x + 1 }
	val anyF = { x: Any => 1 }
	
	"transformMap on a List" should "work" in{
		val in = List(1)
		val out = in transformMap intF

		same[List[Int]](out, List(2))
	}

	"transformMap on an Option[List]" should "work" in{
		val in = Option(List(1))
		val out = in transformMap intF

		same[Option[List[Int]]](out, Option(List(2)))
	}

	//TODO: ill defined test to show it only goes to inner most type

	"transformMap on an Option[List] w/ an Any => B" should "force application to the List" in{
		val in = Option(List(1))
		val out = in transformMap anyF

		same[Option[List[Int]]](out, Option(List(1)))
	}

	"transformAp on a List" should "work" in{
		val in = List(1)
		val out = in transformAp List(intF)

		same[List[Int]](out, List(2))
	}

	"transformAp on an Option[List]" should "work" in{
		val in = Option(List(1))
		val out = in transformAp Option(List(intF))

		same[Option[List[Int]]](out, Option(List(2)))
	}
}