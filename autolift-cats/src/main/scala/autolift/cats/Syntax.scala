package autolift.cats

import autolift._

trait Syntax extends CatsLiftMapSyntax
  with CatsLiftApSyntax
  with CatsLiftFlatMapSyntax
  with LiftFilterSyntax
  with CatsLiftFlattenSyntax
  with CatsLiftFoldLeftSyntax
  with CatsLiftFoldRightSyntax
  with CatsLiftFoldSyntax
  with CatsLiftFoldMapSyntax
  with CatsLiftExistsSyntax
  with CatsLiftMergeSyntax
  with CatsLiftMergeWithSyntax
  with CatLiftForAllSyntax
  with FoldForallSyntax
  with FoldExistsSyntax
  with FoldCompleteSyntax
  with FoldOverSyntax
  with FoldWithSyntax
