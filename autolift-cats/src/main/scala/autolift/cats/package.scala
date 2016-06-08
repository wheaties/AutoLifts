package autolift.cats

object functor extends LiftMapPackage

object applicative extends LiftApPackage
  with LiftMergePackage
  with LiftMergeWithPackage

object monad extends LiftFlatMapPackage
  with LiftFlattenPackage
  with LiftFilterPackage

object fold extends LiftFoldPackage
  with LiftFoldLeftPackage
  with LiftFoldRightPackage
  with LiftFoldMapPackage
  with LiftExistsPackage
  with LiftForAllPackage

//object traverse extends LiftSequencePackage
//  with LiftTraversePackage