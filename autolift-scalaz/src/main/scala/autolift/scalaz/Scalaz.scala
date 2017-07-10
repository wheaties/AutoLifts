package autolift

import autolift.scalaz._

object Scalaz extends LiftMapPackage
  with LiftApPackage
  with LiftMergePackage
  with LiftMergeWithPackage
  with `LiftA*Package`
  with LiftBindPackage
  with LiftFlattenPackage
  with LiftFilterPackage
  with `LiftM*Package`
  with LiftFoldPackage
  with LiftFoldLeftPackage
  with LiftFoldRightPackage
  with LiftFoldMapPackage
  with LiftAnyPackage
  with LiftAllPackage
  with LiftSequencePackage
  with LiftTraversePackage
  with LiftReversePackage
  with LiftZipPackage
  with LiftZipWithPackage
  with LiftMinimumPackage
  with LiftMaximumPackage
  with LiftMinimumByPackage
  with LiftMaximumByPackage