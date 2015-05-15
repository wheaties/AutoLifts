package autolift


import org.scalatest._
import scalaz._
import Scalaz._
import All._

class AutoLiftTest extends FlatSpec{
	def same[A](x: A, y: A) = assert(x == y)

	val intF = { x: Int => x + 1 }
	val intL = { x: Int => List(x + 1) }
	val anyF = { x: Any => 1 }
	val anyO = { x: Any => Option(1) }

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

	"liftFlatMap on an Option[List]" should "work" in{
		val in = Option(List(1))
		val out = in liftFlatMap intL

		same[Option[List[Int]]](out, Option(List(2)))
	}

	"liftFlatMap on an Option[List]" should "work with functions" in{
		val in = Option(List(1, 2))
		val out = in liftFlatMap anyO

		same[Option[Int]](out, Option(1))
	}

	"liftAp on an Option[List]" should "work" in{
		val in = Option(List(1))
		val out = in liftAp List(intF)

		same[Option[List[Int]]](out, Option(List(2)))
	}

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