package autolift.bench

import scalaz._
import Scalaz._
import autolift._
import autolift.Scalaz._
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

/** 11/21/15 timing
[info] LiftMapBench.basicFourDeep        thrpt  200  20613835.099 ± 143286.655  ops/s
[info] LiftMapBench.basicThreeDeep       thrpt  200  23312221.369 ±  97998.694  ops/s
[info] LiftMapBench.basicTwoDeep         thrpt  200  26558062.675 ± 151435.896  ops/s
[info] LiftMapBench.fourDeep             thrpt  200  15336410.941 ±  63983.535  ops/s
[info] LiftMapBench.threeDeep            thrpt  200  17932173.536 ±  99693.349  ops/s
[info] LiftMapBench.twoDeep              thrpt  200  22628507.065 ±  99009.730  ops/s
[info] LiftMapBench.transMap             thrpt  200  25178988.954 ± 108644.012  ops/s
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
	def oneDeep() = Option(1).liftMap{ x: Any => 2 }

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