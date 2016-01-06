package autolift.test.algebird

import autolift._
import Algebird._
import org.scalatest._

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