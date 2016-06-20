package autolift.cats

object functor extends LiftMapPackage

object applicative extends LiftApPackage
  with LiftMergePackage
  with LiftMergeWithPackage
  with `LiftA*Package`

object monad extends LiftFlatMapPackage
  with LiftFlattenPackage
  with LiftFilterPackage
  with `LiftM*Package`

object fold extends LiftFoldPackage
  with LiftFoldLeftPackage
  with LiftFoldRightPackage
  with LiftFoldMapPackage
  with LiftExistsPackage
  with LiftForAllPackage

object traverse extends LiftSequencePackage
  with LiftTraversePackage