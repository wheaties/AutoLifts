package autolift.scalaz

import autolift._

trait Syntax extends ScalazLiftMapSyntax
	with ScalazLiftApSyntax
	with LiftBindSyntax
	with LiftFilterSyntax
	with ScalazLiftFoldLeftSyntax
	with LiftFoldRightSyntax
	with ScalazLiftFlattenSyntax
	with ScalazLiftFoldSyntax
	with ScalazLiftFoldMapSyntax
	with LiftAnySyntax
	with LiftAllSyntax
	with ScalazLiftZipSyntax
	with ScalazLiftZipWithSyntax
	with ScalazLiftMergeSyntax
	with ScalazLiftMergeWithSyntax
	with LiftSequenceSyntax
	with FoldAllSyntax
	with FoldAnySyntax
	with FoldCompleteSyntax
	with FoldOverSyntax
	with FoldWithSyntax