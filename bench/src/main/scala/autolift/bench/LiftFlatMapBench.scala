package autolift.bench

import autolift._
import AutoLift._
import scalaz._
import Scalaz._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

@State(Scope.Benchmark)
class LiftFlatMapBench{

	val two = Option(List(1, 2, 3, 4, 5))
	val three = Option(two)
	val four = Option(three)

	@Benchmark
	def twoDeep() = two.liftFlatMap{ x: Int => List(x + 1) }

	@Benchmark
	def threeDeep() = three.liftFlatMap{ x: Int => List(x + 1) }

	@Benchmark
	def fourDeep() = four.liftFlatMap{ x: Int => List(x + 1) }

	@Benchmark
	def basicTwoDeep() = two.map{ 
		_.flatMap{ x: Int => List(x + 1) }
	}

	@Benchmark
	def basicThreeDeep() = three.map{ 
		_.map{
			_.flatMap{ x: Int => List(x + 1) }
		}
	}

	@Benchmark
	def basicFourDeep() = four.map{
		_.map{
			_.map{
				_.flatMap{ x: Int => List(x + 1) }
			}
		}
	}
}