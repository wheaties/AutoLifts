package autolift.scalaz

object functor extends LiftMapPackage

object applicative extends LiftApPackage
  with LiftMergePackage
  with LiftMergeWithPackage
  with `LiftA*Package`

object monad extends LiftBindPackage
  with LiftFlattenPackage
  with LiftFilterPackage
  with `LiftM*Package`

object fold extends LiftFoldPackage
  with LiftFoldLeftPackage
  with LiftFoldRightPackage
  with LiftFoldMapPackage
  with LiftAnyPackage
  with LiftAllPackage
  with LiftMinimumPackage
  with LiftMaximumPackage

object traverse extends LiftSequencePackage
  with LiftTraversePackage
  with LiftReversePackage

object zip extends LiftZipPackage
  with LiftZipWithPackage