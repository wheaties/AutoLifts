package autolift.bench

import scalaz._
import Scalaz._
import autolift._
import AutoLift._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

/** 11/19/15 timing (this looks wrong)
[info] LiftFilterBench.basicFourDeep     thrpt  200  12811583.045 ± 175514.128  ops/s
[info] LiftFilterBench.basicThreeDeep    thrpt  200  13854868.616 ± 359493.857  ops/s
[info] LiftFilterBench.basicTwoDeep      thrpt  200  20786040.405 ±  85541.221  ops/s
[info] LiftFilterBench.fourDeep          thrpt  200  13329752.990 ±  59816.671  ops/s
[info] LiftFilterBench.threeDeep         thrpt  200  14803483.229 ±  65009.156  ops/s
[info] LiftFilterBench.twoDeep           thrpt  200  11328360.244 ±  91769.887  ops/s
 */

@State(Scope.Benchmark)
class LiftFilterBench{

	val two = Option(List(1, 2, 3, 4, 5))
	val three = Option(two)
	val four = Option(three)

	@Benchmark
	def twoDeep() = two.liftFilter{ x: Int => x == 1 }

	@Benchmark
	def threeDeep() = three.liftFilter{ x: Int => x == 1 }

	@Benchmark
	def fourDeep() = four.liftFilter{ x: Int => x == 1 }

	@Benchmark
	def basicTwoDeep() = two.map{ 
		_.filter{ x: Int => x == 1 }
	}

	@Benchmark
	def basicThreeDeep() = three.map{ 
		_.map{
			_.filter{ x: Int => x == 1 }
		}
	}

	@Benchmark
	def basicFourDeep() = four.map{
		_.map{
			_.map{
				_.filter{ x: Int => x == 1 }
			}
		}
	}
}