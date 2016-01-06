package autolift.test.algebird

import autolift._
import Algebird._
import org.scalatest._

class LiftApTest extends BaseSpec{
	val intF = Foo({ x: Int => x+1 })
	val anyF = Foo({ x: Any => 1 })

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

	"LiftedAp" should "work on a Foo" in{
		val lf = liftAp(intF)
		val out = lf(Foo(1))

		same[Foo[Int]](out, Foo(2))
	}

	"LiftedAp" should "work on an Foo[Foo]" in{
		val lf = liftAp(intF)
		val out = lf(Foo(Foo(2)))

		same[Foo[Foo[Int]]](out, Foo(Foo(3)))
	}

	"LiftedAp" should "andThen with other liftAp" in{
		val lf = liftAp(intF)
		val lf2 = liftAp(anyF)
		val comp = lf andThen lf2
		val out = comp(Foo(4))

		same[Foo[Int]](out, Foo(1))
	}

	"LiftedAp" should "compose with other liftAp" in{
		val lf = liftAp(intF)
		val lf2 = liftAp(anyF)
		val comp = lf2 compose lf
		val out = comp(Foo(4))

		same[Foo[Int]](out, Foo(1))
	}

	"LiftedAp" should "map" in{
		val lf = liftAp(intF) map(_ + 1)
		val out = lf(Foo(Foo(1)))

		same[Foo[Foo[Int]]](out, Foo(Foo(3)))
	}
}