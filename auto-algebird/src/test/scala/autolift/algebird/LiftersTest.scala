package autolift.algebird

import autolift._
import org.scalatest._
import com.twitter.algebird._
import Algebird._

case class Foo[A](a: A)
object Foo{
	implicit val bind = new Monad[Foo]{
		def apply[T](t: T) = Foo(t)
		def flatMap[T, U](foo: Foo[T])(fn: T => Foo[U]) = fn(foo.a)
	}
}

case class Bar[A](a: A)
object Bar{
	implicit val fun = new Functor[Bar]{
		def map[T, U](m: Bar[T])(fn: T => U): Bar[U] = Bar(fn(m.a))
	}
}

trait BaseSpec extends FlatSpec{
	def same[A](x: A, y: A) = assert(x == y)
}

class LiftMapTest extends BaseSpec{
	val intF = { x: Int => x+1 }
	val anyF = { x: Any => "1" }

	"liftMap on an Foo[Foo]" should "work" in{
		val in = Foo(Foo(1))
		val out = in liftMap intF

		same[Foo[Foo[Int]]](out, Foo(Foo(2)))
	}
	"liftMap on an Foo" should "work" in{
		val in = Foo(1)
		val out = in liftMap intF

		same[Foo[Int]](out, Foo(2))
	}
	"liftMap on a Foo" should "work with functions" in{
		val in = Foo(1)
		val out = in liftMap anyF

		same[Foo[String]](out, Foo("1"))
	}
}

class LiftApTest extends BaseSpec{
	val intF = Foo({ x: Int => x+1 })

	"liftAp on an Foo" should "work" in{
		val in = Foo(1)
		val out = in liftAp intF

		same[Foo[Int]](out, Foo(2))
	}
	"liftAp on an Foo[Foo]" should "work" in{
		val in = Foo(Foo(1))
		val out = in liftAp intF

		same[Foo[Foo[Int]]](out, Foo(Foo(2)))
	}
}

class LiftFlatMapTest extends BaseSpec{
	val intF = { x: Int => Foo(x+1) }
	val anyF = { x: Any => Foo("1") }

	"liftMap on an Foo[Foo]" should "work" in{
		val in = Foo(Foo(1))
		val out = in liftFlatMap intF

		same[Foo[Foo[Int]]](out, Foo(Foo(2)))
	}
	"liftMap on an Foo" should "work" in{
		val in = Foo(1)
		val out = in liftFlatMap intF

		same[Foo[Int]](out, Foo(2))
	}
	"liftMap on a Foo" should "work with functions" in{
		val in = Foo(1)
		val out = in liftFlatMap anyF

		same[Foo[String]](out, Foo("1"))
	}
}

class LiftFilterTest extends BaseSpec{
	val intF = { x: Int => x % 2 == 0 }
	val anyF = { x: Any => false }

	"liftFilter on a List" should "work" in{
		val in = List(1)
		val out = in liftFilter intF

		same[List[Int]](out, List.empty[Int])
	}

	"liftFilter on a Bar[List]" should "work" in{
		val in = Bar(List(1))
		val out = in liftFilter intF

		same[Bar[List[Int]]](out, Bar(List.empty[Int]))
	}
	"liftFilter on a Bar[List]" should "work with functions" in{
		val in = Bar(List(1))
		val out = in liftFilter anyF

		same[Bar[List[Int]]](out, Bar(List.empty[Int]))
	}
}

class LiftFlattenTest extends BaseSpec{
	"liftFlatten on a Foo[Foo]" should "work" in{
		val in = Foo(Foo(1))
		val out = in.liftFlatten

		same[Foo[Int]](out, Foo(1))
	}
	"liftFlatten on a Bar[Foo[Foo]]" should "work" in{
		val in = Bar(Foo(Foo(1)))
		val out = in.liftFlatten

		same[Bar[Foo[Int]]](out, Bar(Foo(1)))
	}
	"liftFlatten[Foo] on a Bar[Bar[Foo[Foo]]]" should "work" in{
		val in = Bar(Bar(Foo(Foo(1))))
		val out = in.liftFlatten[Foo]

		same[Bar[Bar[Foo[Int]]]](out, Bar(Bar(Foo(1))))
	}
}