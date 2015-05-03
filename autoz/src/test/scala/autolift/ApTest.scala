package autolift

import org.scalatest._
import scalaz._
import Scalaz._

class ApTest extends FlatSpec{

	def same[A](x: A, y: A) = assert(x == y)

	def appli[F, Func](fa: F, f: Func)(implicit ap: Ap[F, Func]): ap.Out = ap(fa, f)

	val OptF = Option({x: Int => x.toString})
	val ListF = List({x: Int => x.toString})
	val OptL = Option({x: List[Int] => 1})

	"A List" should "ap" in{
		val in = List(1)
		val out = appli(in, ListF)

		same(out, List("1"))
	}

	"An Option[List]" should "ap the right level (inner)" in{
		val in = Option(List(1))
		val out = appli(in, ListF)

		same(out, Option(List("1")))
	}

	"An Option[List]" should "ap the right level (outer)" in{
		val in = Option(List(1))
		val out = appli(in, OptL)

		same(out, Some(1))
	}

	"A List[List[List]]" should "ap the last match" in{
		val in = List(List(List(1)))
		val out = appli(in, ListF)

		same(out, List(List(List("1"))))
	}

	"An None: Option[List]" should "ap like a None" in{
		val in: Option[List[Int]] = None
		val out = appli(in, ListF)

		same(out, None)
	}
}