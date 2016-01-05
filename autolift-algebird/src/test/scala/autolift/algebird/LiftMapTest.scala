package autolift.test.algebird

import autolift._
import Algebird._
import org.scalatest._

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