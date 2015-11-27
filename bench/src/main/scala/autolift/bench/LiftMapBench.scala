package autolift.bench

import autolift._
import AutoLift._
import scalaz._
import Scalaz._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

/** 11/19/15 timing
[info] Benchmark                     Mode  Cnt         Score        Error  Units
[info] LiftMapBench.basicFourDeep   thrpt  200  20441115.712 ± 266943.826  ops/s
[info] LiftMapBench.basicThreeDeep  thrpt  200  23340590.121 ± 231281.395  ops/s
[info] LiftMapBench.basicTwoDeep    thrpt  200  25954719.436 ± 279325.241  ops/s
[info] LiftMapBench.fourDeep        thrpt  200  15012980.192 ± 197590.881  ops/s
[info] LiftMapBench.threeDeep       thrpt  200  17754355.890 ± 178856.124  ops/s
[info] LiftMapBench.twoDeep         thrpt  200  22368644.231 ± 319274.797  ops/s
*/

@State(Scope.Benchmark)
class LiftMapBench{

	val two = Option(List(1, 2, 3, 4, 5))
	val three = Option(two)
	val four = Option(three)

	type OL[A] = ListT[Option, A]
	def mkOL[A](x: List[A]): OL[A] = ListT(Option(x))
	def mkOOL[A](x: List[A]): OptionT[OL, A] = OptionT(mkOL(x.map(Option(_))))

	val trans = ListT.fromList(two)
	val trans2 = mkOOL(List(1, 2, 3, 4, 5))

	@Benchmark
	def twoDeep() = two.liftMap{ x: Int => x + 1 }

	@Benchmark
	def threeDeep() = three.liftMap{ x: Int => x + 1 }

	@Benchmark
	def fourDeep() = four.liftMap{ x: Int => x + 1 }

	@Benchmark
	def basicTwoDeep() = two.map{ _.map{ x: Int => x + 1 }}

	@Benchmark
	def basicThreeDeep() = three.map{ 
		_.map{
			_.map{ x: Int => x + 1 }
		}
	}

	@Benchmark
	def basicFourDeep() = four.map{
		_.map{
			_.map{
				_.map{ x: Int => x + 1 }
			}
		}
	}

	@Benchmark
	def transMap() = trans.map(_ + 1)

	@Benchmark
	def trans2Map() = trans2.map(_ + 1)
}