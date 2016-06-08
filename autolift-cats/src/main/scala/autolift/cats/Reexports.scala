package autolift.cats

//TODO: boilerplate this

trait Reexports extends LiftMapExport
	with LiftApExport
	with LiftMergeExport
	with LiftMergeWithExport
	with LiftFlatMapExport
	with LiftFlattenExport
	with LiftFilterExport
	with LiftFoldExport
	with `LiftM*Reexports` 
	with `LiftA*Reexports`

trait `LiftM*Reexports` extends LiftM2Reexport
	with LiftM3Reexport
	with LiftM4Reexport
	with LiftM5Reexport
	with LiftM6Reexport
	with LiftM7Reexport
	with LiftM8Reexport
	with LiftM9Reexport
	with LiftM10Reexport
	with LiftM11Reexport
	with LiftM12Reexport
	with LiftM13Reexport
	with LiftM14Reexport
	with LiftM15Reexport
	with LiftM16Reexport
	with LiftM17Reexport
	with LiftM18Reexport
	with LiftM19Reexport
	with LiftM20Reexport
	with LiftM21Reexport

trait `LiftA*Reexports` extends LiftA2Reexport
	with LiftA3Reexport
	with LiftA4Reexport
	with LiftA5Reexport
	with LiftA6Reexport
	with LiftA7Reexport
	with LiftA8Reexport
	with LiftA9Reexport
	with LiftA10Reexport
	with LiftA11Reexport
	with LiftA12Reexport
	with LiftA13Reexport
	with LiftA14Reexport
	with LiftA15Reexport
	with LiftA16Reexport
	with LiftA17Reexport
	with LiftA18Reexport
	with LiftA19Reexport
	with LiftA20Reexport
	with LiftA21Reexport