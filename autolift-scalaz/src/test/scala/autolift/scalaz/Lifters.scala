package autolift.scalaz

import org.scalatest._
import scalaz._
import Scalaz._
import autolift._
import autolift.Scalaz._
//TODO: Test out the andThen, compose and map methods. Already finding errors!

trait BaseSpec extends FlatSpec{
	def same[A](x: A, y: A) = assert(x == y)

	val intF = {x: Int => x+1}
	def intintF(x: Int, y: Int) = x + y
	def anyanyF(x: Any, y: Any) = 1
	val intL = {x: Int => List(x+1, x+2)}
	val intAp = List(intF)
	val anyF = {x: Any => 1}
	val anyL = {x: Any => List(1)}
	val anyO = { x: Any => Option(1) }
	val even = {x: Int => x % 2 == 0 }
}

class LiftersTest extends FlatSpec{
	def same[A](x: A, y: A) = assert(x == y)

	val intF = {x: Int => x+1}
	def intintF(x: Int, y: Int) = x + y
	def anyanyF(x: Any, y: Any) = 1
	val intL = {x: Int => List(x+1, x+2)}
	val intAp = List(intF)
	val anyF = {x: Any => 1}
	val anyL = {x: Any => List(1)}
	val anyO = { x: Any => Option(1) }
	val even = {x: Int => x % 2 == 0 }

	"liftFilter on a List" should "work" in{
		val in = List(1, 2, 3)
		val out = in.liftFilter(even)

		same[List[Int]](out, List(2))
	}

	"liftFilter on an Option[List]" should "work" in{
		val in = Option(List(1, 2, 3))
		val out = in.liftFilter(even)

		same[Option[List[Int]]](out, Option(List(2)))
	}

	"liftFilter on an NonEmptyList[List]" should "work work with functions" in{
		val in = NonEmptyList(List(1, 2, 3))
		val out = in.liftFilter{x: Any => false}

		same[NonEmptyList[List[Int]]](out, NonEmptyList(Nil))
	}

	

	/*"liftM2" should "work on a pair of List" in{
		val lf = liftM2(intintF)
		val out = lf(List(0, 1), List(1, 2))

		same[List[Int]](out, List(1, 2, 2, 3))
	}

	"liftM2" should "work on a pair of Option List" in{
		val lf = liftM2(intintF)
		val out = lf(Option(List(0, 1)), Option(List(1, 2)))

		same[Option[List[Int]]](out, Option(List(1, 2, 2, 3)))

		val out2 = lf(Option(List(0, 1)), None: Option[List[Int]])

		same[Option[List[Int]]](out2, None)
	}

	"liftM2" should "work with functions" in{
		val lf = liftM2(anyanyF)
		val out = lf(Option(2), Option('c'))

		same[Option[Int]](out, Option(1))
	}

	"liftA2" should "work on a pair of List" in{
		val lf = liftA2(intintF)
		val out = lf(List(0, 1), List(1, 2))

		same[List[Int]](out, List(1, 2, 2, 3))
	}

	"liftA2" should "work on a pair of Option List" in{
		val lf = liftA2(intintF)
		val out = lf(Option(List(0, 1)), Option(List(1, 2)))

		same[Option[List[Int]]](out, Option(List(1, 2, 2, 3)))

		val out2 = lf(Option(List(0, 1)), None: Option[List[Int]])

		same[Option[List[Int]]](out2, None)
	}

	"liftA2" should "work with functions" in{
		val lf = liftA2(anyanyF)
		val out = lf(Option(2), Option(1))

		same[Option[Int]](out, Option(1))
	}*/
}