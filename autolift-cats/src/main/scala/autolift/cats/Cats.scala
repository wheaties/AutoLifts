package autolift

import autolift.cats._

object Cats extends Syntax with Context with Reexports with Implicits{
  implicit def mkFAny[Obj, Fn](implicit lift: CatsFoldExists[Obj, Fn]): CatsFoldExists[Obj, Fn] = lift
  implicit def mkFAll[Obj, Fn](implicit lift: CatsFoldAll[Obj, Fn]): CatsFoldAll[Obj, Fn] = lift
  implicit def mkFC[Obj](implicit lift: CatsFoldComplete[Obj]): CatsFoldComplete.Aux[Obj, lift.Out] = lift
  implicit def mkFW[Obj, Fn](implicit lift: CatsFoldWith[Obj, Fn]): CatsFoldWith.Aux[Obj, Fn, lift.Out] = lift
  implicit def mkFO[M[_], Obj](implicit lift: CatsFoldOver[M, Obj]): CatsFoldOver.Aux[M, Obj, lift.Out] = lift
  implicit def mkSq[M[_], Obj](implicit lift: CatsLiftSequence[M, Obj]): CatsLiftSequence.Aux[M, Obj, lift.Out] = lift
  implicit def mkTv[Obj, Fn](implicit lift: CatsLiftTraverse[Obj, Fn]): CatsLiftTraverse.Aux[Obj, Fn, lift.Out] = lift
}
