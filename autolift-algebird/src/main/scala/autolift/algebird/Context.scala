package autolift.algebird

import autolift._

//TODO: ScalaDocs

trait Context extends LiftApContext
	with LiftMapContext
	with LiftFlatMapContext
	with LiftFilterContext
	with LiftJoinWithContext 
	//with LiftA2Context
	//with LiftA3Context
	//with LiftA4Context
	//with LiftA5Context
	with LiftM2Context
	with LiftM3Context
	with LiftM4Context
	with LiftM5Context