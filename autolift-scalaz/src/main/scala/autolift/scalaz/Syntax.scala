package autolift.scalaz

import autolift._

trait Syntax extends LiftMapSyntax
  with LiftApSyntax
  with LiftBindSyntax
  with LiftFilterSyntax
  with LiftFoldLeftSyntax
  with LiftFoldRightSyntax
  with LiftFlattenSyntax
  with LiftFoldSyntax
  with LiftFoldMapSyntax
  with LiftAnySyntax
  with LiftAllSyntax
  with LiftZipSyntax
  with LiftZipWithSyntax
  with LiftMergeSyntax
  with LiftMergeWithSyntax
  with LiftSequenceSyntax
  with LiftReverseSyntax
  with LiftTraverseSyntax