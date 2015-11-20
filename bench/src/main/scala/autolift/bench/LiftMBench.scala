package autolift.bench

import autolift._
import AutoLift._
import scalaz._
import Scalaz._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

/** 11/17/15 - timing
[info] Benchmark               Mode  Cnt        Score       Error  Units
[info] LiftMBench.doubleNest  thrpt  200  2258094.506 ± 37122.193  ops/s
[info] LiftMBench.singleNest  thrpt  200  2394289.346 ± 21467.774  ops/s
[info] LiftMBench.transNest   thrpt  200   808685.187 ±  6298.217  ops/s
*/

@State(Scope.Benchmark)
class LiftMBench{

	val two = Option(List(1, 2, 3, 4, 5))
	val three = Option(two)
	val four = Option(three)

	val trans = ListT.fromList(two)

	def add(x: Int, y: Int) = x + y

	val lifted = liftM2(add)

	@Benchmark
	def singleNest() = lifted(two, two)

	@Benchmark
	def doubleNest() = lifted(three, three)

	@Benchmark
	def transNest() = for{
		a <- trans
		b <- trans
	} yield add(a, b)
}