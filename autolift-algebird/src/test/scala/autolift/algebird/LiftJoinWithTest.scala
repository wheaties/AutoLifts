package autolift.test.algebird

import autolift._
import Algebird._
import com.twitter.algebird.Monad._

class LiftJoinWithTest extends BaseSpec{
	def intintF(x: Int, y: Int) = x + y

	"liftJoinWith on a Foo[Int] from a Foo[Int]" should "work" in{
		val in = Foo(1)
		val out = in.liftJoinWith(Foo(1))(intintF)

		same[Foo[Int]](out, Foo(2))
	}

	"liftJoinWith on a Foo[List] on a List" should "work" in{
		val in = Foo(List(1))
		val out = in.liftJoinWith(List(1))(intintF)

		same[Foo[List[Int]]](out, Foo(List(2)))
	}

	"LiftedJoinWith on a Foo[List]" should "work" in{
		val lf = liftJoinWith(intintF)
		val out = lf(Foo(List(1)), List(1))

		same[Foo[List[Int]]](out, Foo(List(2)))
	}

	"LiftedJoinWith" should "map" in{
		val lf = liftJoinWith(intintF)
		val lf2 = lf map (_ + 1)
		val out = lf2(Foo(List(1)), List(1))

		same[Foo[List[Int]]](out, Foo(List(3)))
	}
}