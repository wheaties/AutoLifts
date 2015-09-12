package autolift

trait DFunction1[A]{
	type Out

	def apply(a: A): Out

	override def toString() = "<DFunction1>"
}

trait DFunction2[A,B]{
	type Out

	def apply(a: A, b: B): Out

	override def toString() = "<DFunction2>"
}

trait DFunction3[A,B,C]{
	type Out

	def apply(a: A, b: B, c: C): Out

	override def toString() = "<DFunction3>"
}

trait DFunction4[A,B,C, D]{
	type Out

	def apply(a: A, b: B, c: C, d: D): Out

	override def toString() = "<DFunction4>"
}