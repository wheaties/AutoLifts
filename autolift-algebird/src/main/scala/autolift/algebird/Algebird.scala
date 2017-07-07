package autolift

import autolift.algebird._

object Algebird extends Syntax with Context with Reexports with Implicits{
  implicit def mkF[Obj, Fn](implicit lift: AlgeLiftMap[Obj, Fn]): AlgeLiftMap.Aux[Obj, Fn, lift.Out] = lift
  implicit def mkAp[Obj, Fn](implicit lift: AlgeLiftAp[Obj, Fn]): AlgeLiftAp.Aux[Obj, Fn, lift.Out] = lift
  implicit def mkFM[Obj, Fn](implicit lift: AlgeLiftFlatMap[Obj, Fn]): AlgeLiftFlatMap.Aux[Obj, Fn, lift.Out] = lift
  implicit def mkFl[M[_], Obj](implicit lift: AlgeLiftFlatten[M, Obj]): AlgeLiftFlatten.Aux[M, Obj, lift.Out] = lift
  implicit def mkJ[Obj1, Obj2](implicit lift: AlgeLiftMerge[Obj1, Obj2]): AlgeLiftMerge.Aux[Obj1, Obj2, lift.Out] = lift
  implicit def mkJw[Obj1, Obj2, Fn](implicit lift: AlgeLiftMergeWith[Obj1, Obj2, Fn]): AlgeLiftMergeWith.Aux[Obj1, Obj2, Fn, lift.Out] = lift
  implicit def mkFil[Obj, Fn](implicit lift: AlgeLiftFilter[Obj, Fn]): AlgeLiftFilter[Obj, Fn] = lift
}