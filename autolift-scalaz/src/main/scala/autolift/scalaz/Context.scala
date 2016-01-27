package autolift.scalaz

import autolift._

trait Context extends LiftMapContext
	with LiftApContext
	with LiftBindContext
	with LiftFilterContext
	with LiftFoldLeftContext
	with LiftFoldRightContext
	with LiftFoldMapContext
	with LiftAnyContext
	with LiftAllContext
	with LiftZipWithContext
	with LiftMergeWithContext
	with LiftA2Context
	with LiftA3Context
	with LiftA4Context
	with LiftA5Context
	with LiftM2Context
	with LiftM3Context
	with LiftM4Context
	with LiftM5Context
	with FoldWithContext
	with FoldAllContext
	with FoldAnyContext