package autolift

import org.scalatest._
import scalaz._
import Scalaz._

class FlatMapperTest extends FlatSpec{

	def same[A](x: A, y: A) = assert(x == y)

	def mapped[F, Func](fa: F, f: Func)(implicit mapper: FlatMapper[F, Func]): mapper.Out = mapper(fa, f)

	"A List" should "flatMap" in{
		val in = List(1)
		val out = mapped(in, {x: Int => List(x)})

		same(out, List(1))
	}

	"An Option[List]" should "FlatMap the right level" in{
		val in = Option(List(1))
		val out = mapped(in, {x: Int => List(x)})

		same(out, Option(List(1)))
	}

	"An Option[List]" should "flatMap based on the return type" in{
		val in = Option(List(1))
		val out = mapped(in, {x: Any => Some(3)})

		same(out, Some(3))
	}

	"An Option[List]" should "FlatMap the inner level" in{
		val in = Option(List(1))
		val out = mapped(in, {x: List[Any] => None})

		same(out, None)
	}

	"A List[List[List]]" should "flatMap the last match" in{
		val in = List(List(List(1)))
		val out = mapped(in, {x: Int => List(x+2)})

		same(out, List(List(List(3))))
	}

	"An Option[List]" should "flatMap like a MonadTransformer" in{
		val in: Option[List[Int]] = None
		val out = mapped(in, {x: Int => List(x)})

		same(out, None)
	}
}