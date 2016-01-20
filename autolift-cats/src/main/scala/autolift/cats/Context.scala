package autolift.cats

import autolift._

trait Context extends LiftMapContext
  with LiftApContext
  with LiftFlatMapContext
  with LiftFilterContext
  with LiftFoldLeftContext
  with LiftFoldRightContext
  with LiftFoldMapContext
  with LiftExistsContext
  with LiftForAllContext
  with LiftJoinWithContext
  with LiftA2Context
  with LiftA3Context
  with LiftA4Context
  with LiftA5Context
  with LiftM2Context
  with LiftM3Context
  with LiftM4Context
  with LiftM5Context
  with FoldWithContext
  with FoldForallContext
  with FoldExistsContext
