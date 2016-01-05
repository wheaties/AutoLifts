package autolift.algebird

import autolift._
import Algebird._
import org.scalatest._
import com.twitter.algebird._

class LiftJoinWithTest extends BaseSpec{
	def intintF(x: Int, y: Int) = x + y

	"liftJoinWith on a Foo[Int] from a Foo[Int]" should "work" in{
		val in = Foo(1)
		val out = in.liftJoinWith(Foo(1))(intintF)

		same[Foo[Int]](out, Foo(2))
	}
	"liftJoinWith on a Foo[List[Int]] on a List[Int]" should "work" in{
		val in = Foo(List(1))
		val out = in.liftJoinWith(List(1))(intintF)

		same[Foo[List[Int]]](out, Foo(List(2)))
	}
}