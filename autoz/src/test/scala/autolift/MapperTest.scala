package autolift

import org.scalatest._
import scalaz._
import Scalaz._

class MapperTest extends FlatSpec{

	def same[A](x: A, y: A) = assert(x == y)

	def mapped[F, A, B](fa: F, f: A => B)(implicit mapper: Mapper[F, A => B]): mapper.Out = mapper(fa, f)

	"A List" should "map" in{
		val in = List(1)
		val out = mapped(in, {x: Int => x.toString})

		same(out, List("1"))
	}

	"An Option[List]" should "map the right level" in{
		val in = Option(List(1))
		val out = mapped(in, {x: Int => x.toString})

		same(out, Option(List("1")))
	}

	"An Option[List]" should "map the inner level" in{
		val in = Option(List(1))
		val out = mapped(in, {x: List[Any] => "foo"})

		same(out, Option("foo"))
	}

	"A List[List]" should "map the lowest wrung" in{
		val in = List(List(1))
		val out = mapped(in, {x: Int => 2.0})

		same(out, List(List(2.0)))
	}

	"A List[List[List]]" should "map the last match" in{
		val in = List(List(List(2)))
		val out = mapped(in, {x: List[Any] => x.size})

		same(out, List(List(1)))
	}

	"A None: Option[List]" should "map like a None" in{
		val in: Option[List[Int]] = None
		val out = mapped(in, {x: Int => 5.0})

		same(out, None)
	}

	/*"a List[Bar]" should "map with inheritance" in{
		trait Foo
		class Bar extends Foo

		val in = List(new Bar)
		val out = mapped(in, {x: List[Foo] => "foo"})

		same(out, List("foo"))
	}*/
}