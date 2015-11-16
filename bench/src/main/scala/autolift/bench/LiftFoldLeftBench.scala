package autolift.bench

import scalaz._
import Scalaz._
import autolift._
import AutoLift._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

/** 11/19/15 timing
[info] LiftFoldLeftBench.basicFourDeep   thrpt  200  14717383.070 ±  72202.999  ops/s
[info] LiftFoldLeftBench.basicThreeDeep  thrpt  200  31195068.705 ± 435549.557  ops/s
[info] LiftFoldLeftBench.basicTwoDeep    thrpt  200  35910475.104 ± 231291.513  ops/s
[info] LiftFoldLeftBench.fourDeep        thrpt  200  18604099.935 ±  77640.094  ops/s
[info] LiftFoldLeftBench.threeDeep       thrpt  200  21924743.225 ±  90974.435  ops/s
[info] LiftFoldLeftBench.twoDeep         thrpt  200  15416127.427 ±  82848.916  ops/s
 */

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