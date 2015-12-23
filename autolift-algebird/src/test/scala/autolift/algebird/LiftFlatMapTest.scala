package autolift.algebird

import autolift._
import Algebird._
import org.scalatest._

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