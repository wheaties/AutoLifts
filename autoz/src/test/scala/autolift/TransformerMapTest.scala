package autolift

import org.scalatest._
import scalaz._
import Scalaz._

class TransformerFlatMap extends FlatSpec{
	def same[A](x: A, y: A) = assert(x == y)

	def mapped[F, Func](fa: F, f: Func)(implicit fm: TransformerMap[F, Func]): fm.Out = fm(fa, f)
}

class TransformerFlatMapTest extends FlatSpec{

	def same[A](x: A, y: A) = assert(x == y)

	def mapped[F, Func](fa: F, f: Func)(implicit fm: TransformerFlatMap[F, Func]): fm.Out = fm(fa, f)

	val listF = {x: Int => List(x)}
	val optListF = {x: List[Int] => Option(1)}

	//TODO: Should I block this? I mean, it's not of the form F[G[A]]
	"A List" should "flatMap" in{
		val in = List(1)
		val out = mapped(in, listF)

		same(out, List(1))
	}

	"A List[List]" should "flatMap" in{
		val in = List(List(1))
		val out = mapped(in, listF)

		same(out, List(List(1)))
	}

	//TODO: Need illdefined from Shapeless to test this out as a unit test.
	//"An Option[List]" should "flatMap" in{
	//	val in = Option(List(1))
	//	val out = mapped(in, optListF)
	//}
}