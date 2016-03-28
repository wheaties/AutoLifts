package autolift.scalaz

import autolift._

trait Syntax extends LiftMapSyntax
	with ScalazLiftApSyntax
	with LiftBindSyntax
	with LiftFilterSyntax
	with LiftFoldLeftSyntax
	with LiftFoldRightSyntax
	with ScalazLiftFoldAtSyntax
	with ScalazLiftFlattenSyntax
	with ScalazLiftFoldSyntax
	with LiftFoldMapSyntax
	with LiftAnySyntax
	with LiftAllSyntax
	with LiftZipSyntax
	with LiftZipWithSyntax
	with LiftMergeSyntax
	with LiftMergeWithSyntax
	with FoldAllSyntax
	with FoldAnySyntax
	with FoldCompleteSyntax
	with FoldOverSyntax
	with FoldWithSyntax