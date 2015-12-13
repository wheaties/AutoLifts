package autolift

import export._
import autolift.algebird._

@reexports[AlgeLiftMap, AlgeLiftAp, AlgeLiftFlatMap, AlgeLiftFlatten, AlgeLiftFilter, LiftJoin, LiftJoinWith, 
		   AlgeLiftM2, AlgeLiftM3, AlgeLiftM4, AlgeLiftM5]
object Algebird extends Syntax with Context{
	implicit def mkF[Obj, Fn](implicit lift: AlgeLiftMap[Obj, Fn]): AlgeLiftMap.Aux[Obj, Fn, lift.Out] = lift
	implicit def mkAp[Obj, Fn](implicit lift: AlgeLiftAp[Obj, Fn]): AlgeLiftAp.Aux[Obj, Fn, lift.Out] = lift
	implicit def mkFM[Obj, Fn](implicit lift: AlgeLiftFlatMap[Obj, Fn]): AlgeLiftFlatMap.Aux[Obj, Fn, lift.Out] = lift
	implicit def mkFl[M[_], Obj](implicit lift: AlgeLiftFlatten[M, Obj]): AlgeLiftFlatten.Aux[M, Obj, lift.Out] = lift
	implicit def mkJ[Obj1, Obj2](implicit lift: LiftJoin[Obj1, Obj2]): LiftJoin.Aux[Obj1, Obj2, lift.Out] = lift
	implicit def mkJw[Obj1, Obj2, Fn](implicit lift: LiftJoinWith[Obj1, Obj2, Fn]): LiftJoinWith.Aux[Obj1, Obj2, Fn, lift.Out] = lift
}