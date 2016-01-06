package autolift.bench

import scalaz._
import Scalaz._
import autolift._
import autolift.Scalaz._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

/** 11/17/15 - timing
[info] Benchmark               Mode  Cnt        Score       Error  Units
[info] LiftMBench.doubleNest  thrpt  200  2258094.506 ± 37122.193  ops/s
[info] LiftMBench.singleNest  thrpt  200  2394289.346 ± 21467.774  ops/s
[info] LiftMBench.transNest   thrpt  200   808685.187 ±  6298.217  ops/s
*/

/** 11/21/15 timing
[info] LiftMBench.basicNest              thrpt  200   2895730.940 ±  15318.568  ops/s
[info] LiftMBench.basicTriple            thrpt  200   2787368.105 ±  13220.673  ops/s
[info] LiftMBench.doubleNest             thrpt  200   2369862.662 ±  10135.498  ops/s
[info] LiftMBench.singleNest             thrpt  200   2472520.241 ±   9912.827  ops/s
[info] LiftMBench.transNest              thrpt  200    813141.155 ±   4493.687  ops/s
 */

@State(Scope.Benchmark)
class LiftMBench{

	val two = Option(List(1, 2, 3, 4, 5))
	val three = Option(two)
	val four = Option(three)

	type OL[A] = ListT[Option, A]
	def mkOL[A](x: List[A]): OL[A] = ListT(Option(x))
	def mkOOL[A](x: List[A]): OptionT[OL, A] = OptionT(mkOL(x.map(Option(_))))

	val trans = ListT.fromList(two)
	val trans2 = mkOOL(List(1, 2, 3, 4, 5))

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

	@Benchmark
	def trans2Next() = for{
		a <- trans2
		b <- trans2
	} yield add(a, b)

	@Benchmark
	def basicNest() = two.flatMap{ l1 =>
		two.map{ l2 =>
			l1.flatMap{ x =>
				l2.map(_ + x)
			}
		}
	}

	@Benchmark
	def basicTriple() = three.flatMap{ o1 =>
		three.map{ o2 =>
			o1.flatMap{ l1 =>
				o2.map{ l2 =>
					l1.flatMap{ x =>
						l2.map(_ + x)
					}
				}
			}
		}
	}
}