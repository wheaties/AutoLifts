package autolift.cats

import autolift._

trait Syntax extends LiftMapSyntax
  with LiftApSyntax
  with LiftFlatMapSyntax
  with LiftFilterSyntax
  with LiftFoldLeftSyntax
  with LiftFoldRightSyntax
  with LiftFoldAtSyntax
  with LiftFlattenSyntax
  with LiftFoldSyntax
  with LiftFoldMapSyntax
  with LiftExistsSyntax
  with LiftJoinSyntax
  with LiftJoinWithSyntax
  with LiftForAllSyntax
  with FoldForallSyntax
  with FoldExistsSyntax
  with FoldCompleteSyntax
  with FoldOverSyntax
  with FoldWithSyntax
