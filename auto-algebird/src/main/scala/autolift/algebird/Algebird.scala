package autolift

import export._
import autolift.algebird._

@reexports[AlgeLiftF, AlgeLiftAp, AlgeLiftB, AlgeLiftFlatten, AlgeLiftFilter, LiftJoinWith]
object Algebird extends Syntax with Contexts{
	implicit def mkF[Obj, Fn](implicit lift: AlgeLiftF[Obj, Fn]): AlgeLiftF.Aux[Obj, Fn, lift.Out] = lift
	implicit def mkAp[Obj, Fn](implicit lift: AlgeLiftAp[Obj, Fn]): AlgeLiftAp.Aux[Obj, Fn, lift.Out] = lift
	implicit def mkFM[Obj, Fn](implicit lift: AlgeLiftB[Obj, Fn]): AlgeLiftB.Aux[Obj, Fn, lift.Out] = lift
	implicit def mkFl[M[_], Obj](implicit lift: AlgeLiftFlatten[M, Obj]): AlgeLiftFlatten.Aux[M, Obj, lift.Out] = lift
	implicit def mkJw[Obj1, Obj2, Fn](implicit lift: LiftJoinWith[Obj1, Obj2, Fn]): LiftJoinWith.Aux[Obj1, Obj2, Fn, lift.Out] = lift
}