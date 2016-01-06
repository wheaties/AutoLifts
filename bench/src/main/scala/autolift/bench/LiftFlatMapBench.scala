package autolift.bench

import scalaz._
import Scalaz._
import autolift._
import autolift.Scalaz._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

/** 11/21/15 timing (this looks wrong)
[info] LiftFlatMapBench.basicFourDeep    thrpt  200   2040065.769 ±  12447.073  ops/s
[info] LiftFlatMapBench.basicThreeDeep   thrpt  200   2025508.780 ±  20879.652  ops/s
[info] LiftFlatMapBench.basicTwoDeep     thrpt  200   2052203.206 ±  26929.461  ops/s
[info] LiftFlatMapBench.fourDeep         thrpt  200   2046034.302 ±  14711.355  ops/s
[info] LiftFlatMapBench.threeDeep        thrpt  200   2043121.787 ±  18518.036  ops/s
[info] LiftFlatMapBench.twoDeep          thrpt  200   2088752.420 ±  21884.494  ops/s
 */

@State(Scope.Benchmark)
class LiftFlatMapBench{

	val two = Option(List(1, 2, 3, 4, 5))
	val three = Option(two)
	val four = Option(three)

	@Benchmark
	def twoDeep() = two.liftBind{ x: Int => List(x + 1) }

	@Benchmark
	def threeDeep() = three.liftBind{ x: Int => List(x + 1) }

	@Benchmark
	def fourDeep() = four.liftBind{ x: Int => List(x + 1) }

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