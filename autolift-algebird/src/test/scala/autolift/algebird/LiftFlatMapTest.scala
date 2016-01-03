package autolift.algebird

import autolift._
import Algebird._
import org.scalatest._

class LiftFlatMapTest extends BaseSpec{
	val intF = { x: Int => Foo(x+1) }
	val anyF = { x: Any => Foo("1") }
	val anyFoo = { x: Any => Foo(1) }

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

	"LiftedFlatMap" should "work on a Foo" in{
		val lf = liftFlatMap(intF)
		val out = lf(Foo(1))

		same[Foo[Int]](out, Foo(2))
	}

	"LiftedFlatMap" should "work on an Foo[Foo]" in{
		val lf = liftFlatMap(intF)
		val out = lf(Foo(Foo(2)))

		same[Foo[Foo[Int]]](out, Foo(Foo(3)))
	}

	"LiftedFlatMap on a Foo" should "work with functions" in{
		val lf = liftFlatMap(anyFoo)
		val out = lf(Foo(2))

		same[Foo[Int]](out, Foo(1))
	}

	"LiftedFlatMap" should "andThen with other liftFlatMap" in{
		val lf = liftFlatMap(anyFoo)
		val lf2 = liftFlatMap(intF)
		val comp = lf andThen lf2
		val out = comp(Foo(1))

		same[Foo[Int]](out, Foo(2))
	}

	"LiftedFlatMap" should "compose with other liftFlatMap" in{
		val lf = liftFlatMap(anyFoo)
		val lf2 = liftFlatMap(intF)
		val comp = lf2 compose lf
		val out = comp(Foo(1))

		same[Foo[Int]](out, Foo(2))
	}

	"LiftedFlatMap" should "map" in{
		val lf = liftFlatMap(intF) map (_ + 1)
		val out = lf(Foo(0))

		same[Foo[Int]](out, Foo(2))
	}
}