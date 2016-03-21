package autolift.cats

import autolift._

trait Syntax extends CatsLiftMapSyntax
  with LiftApSyntax
  with LiftFlatMapSyntax
  with LiftFilterSyntax
  with LiftFlattenSyntax
  with CatsLiftFoldLeftSyntax
  with CatsLiftFoldRightSyntax
  with CatsLiftFoldSyntax
  with CatsLiftFoldAtSyntax
  with CatsLiftFoldMapSyntax
  with LiftExistsSyntax
  with LiftMergeSyntax
  with LiftMergeWithSyntax
  with CatLiftForAllSyntax
  with FoldForallSyntax
  with FoldExistsSyntax
  with FoldCompleteSyntax
  with FoldOverSyntax
  with FoldWithSyntax
