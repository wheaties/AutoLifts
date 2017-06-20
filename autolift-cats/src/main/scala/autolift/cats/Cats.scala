package autolift

import autolift.cats._

object Cats extends LiftMapPackage
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