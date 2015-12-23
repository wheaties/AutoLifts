package autolift.algebird

import autolift._
import Algebird._
import org.scalatest._

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