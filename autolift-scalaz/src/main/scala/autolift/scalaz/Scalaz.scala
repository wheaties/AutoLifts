package autolift

import export._
import autolift.scalaz._

@reexports[ScalazLiftF, ScalazLiftAp, ScalazLiftB, ScalazLiftFoldLeft, ScalazLiftFoldRight, ScalazLiftFold, ScalazLiftFoldMap, 
		   ScalazLiftFoldAt, ScalazLiftFlatten, ScalazLiftFilter]
object Scalaz{
	implicit def mkM[Obj, Fn](implicit lift: ScalazLiftF[Obj, Fn]): ScalazLiftF.Aux[Obj, Fn, lift.Out] = lift
	implicit def mkAp[Obj, Fn](implicit lift: ScalazLiftAp[Obj, Fn]): ScalazLiftAp.Aux[Obj, Fn, lift.Out] = lift
	implicit def mkFM[Obj, Fn](implicit lift: ScalazLiftB[Obj, Fn]): ScalazLiftB.Aux[Obj, Fn, lift.Out] = lift
	implicit def mkFldL[Obj, Fn, Z](implicit lift: ScalazLiftFoldLeft[Obj, Fn, Z]): ScalazLiftFoldLeft.Aux[Obj, Fn, Z, lift.Out] = lift
	implicit def mkFldR[Obj, Fn, Z](implicit lift: ScalazLiftFoldRight[Obj, Fn, Z]): ScalazLiftFoldRight.Aux[Obj, Fn, Z, lift.Out] = lift
	implicit def mkFld[Obj](implicit lift: ScalazLiftFold[Obj]): ScalazLiftFold.Aux[Obj, lift.Out] = lift
	implicit def mkFlM[Obj, Fn](implicit lift: ScalazLiftFoldMap[Obj, Fn]): ScalazLiftFoldMap.Aux[Obj, Fn, lift.Out] = lift
	implicit def mkFlA[M[_], Obj](implicit lift: ScalazLiftFoldAt[M, Obj]): ScalazLiftFoldAt.Aux[M, Obj, lift.Out] = lift
	implicit def mkFl[M[_], Obj](implicit lift: ScalazLiftFlatten[M, Obj]): ScalazLiftFlatten.Aux[M, Obj, lift.Out] = lift
}