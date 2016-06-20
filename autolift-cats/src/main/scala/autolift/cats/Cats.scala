package autolift

import autolift.cats._

object Cats extends AutoFolds 
  with LiftMapPackage
  with LiftApPackage
  with LiftMergePackage
  with LiftMergeWithPackage
  with `LiftA*Package`
  with LiftFlatMapPackage
  with LiftFlattenPackage
  with LiftFilterPackage
  with `LiftM*Package`
  with LiftFoldPackage
  with LiftFoldLeftPackage
  with LiftFoldRightPackage
  with LiftFoldMapPackage
  with LiftExistsPackage
  with LiftForAllPackage
  with LiftSequencePackage
  with LiftTraversePackage

//TODO: should this be it's own subproject?
trait AutoFolds extends FoldWithContext
  with FoldForallContext
  with FoldExistsContext
  with FoldForallSyntax
  with FoldExistsSyntax
  with FoldCompleteSyntax
  with FoldOverSyntax
  with FoldWithSyntax {
  implicit def mkFAny[Obj, Fn](implicit lift: CatsFoldExists[Obj, Fn]): CatsFoldExists[Obj, Fn] = lift
  implicit def mkFAll[Obj, Fn](implicit lift: CatsFoldAll[Obj, Fn]): CatsFoldAll[Obj, Fn] = lift
  implicit def mkFC[Obj](implicit lift: CatsFoldComplete[Obj]): CatsFoldComplete.Aux[Obj, lift.Out] = lift
  implicit def mkFW[Obj, Fn](implicit lift: CatsFoldWith[Obj, Fn]): CatsFoldWith.Aux[Obj, Fn, lift.Out] = lift
  implicit def mkFO[M[_], Obj](implicit lift: CatsFoldOver[M, Obj]): CatsFoldOver.Aux[M, Obj, lift.Out] = lift
}