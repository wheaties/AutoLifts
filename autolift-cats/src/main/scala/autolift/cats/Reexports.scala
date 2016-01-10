package autolift.cats

//TODO: boilerplate this

trait Reexports extends `LiftM*Reexports` with `LiftA*Reexports`

trait `LiftM*Reexports` extends LiftM2Reexport
  with LiftM3Reexport
  with LiftM4Reexport
  with LiftM5Reexport

trait `LiftA*Reexports` extends LiftA2Reexport
  with LiftA3Reexport
  with LiftA4Reexport
  with LiftA5Reexport
