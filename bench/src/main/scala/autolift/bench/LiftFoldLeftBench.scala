package autolift.bench

import autolift._
import AutoLift._
import scalaz._
import Scalaz._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

@State(Scope.Benchmark)
class LiftFoldLeftBench{

	val two = Option(List(1, 2, 3, 4, 5))
	val three = Option(two)
	val four = Option(three)
	val sum = { (x: Int, y: Int) => x + y }

	@Benchmark
	def twoDeep() = two.liftFoldLeft(0)(sum)

	@Benchmark
	def threeDeep() = three.liftFoldLeft(0)(sum)

	@Benchmark
	def fourDeep() = four.liftFoldLeft(0)(sum)

	@Benchmark
	def basicTwoDeep() = two.map{ 
		_.foldLeft(0)(sum)
	}

	@Benchmark
	def basicThreeDeep() = three.map{ 
		_.map{
			_.foldLeft(0)(sum)
		}
	}

	@Benchmark
	def basicFourDeep() = four.map{
		_.map{
			_.map{
				_.foldLeft(0)(sum)
			}
		}
	}
}