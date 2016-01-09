package autolift.cats

import autolift.FoldComplete
import export._

trait CatsFoldComplete[Obj] extends FoldComplete[Obj]

@exports(Subclass)
object CatsFoldComplete extends LowPriorityCatsFoldComplete{
	def apply[Obj](implicit lift: CatsFoldComplete[Obj]): Aux[Obj, lift.Out] = lift

	@export(Subclass)
	implicit def base[F[_], A](implicit fold: Foldable[F], ev: Monoid[A]): Aux[F[A], A] =
		new CatsFoldComplete[F[A]]{
			type Out = A

			def apply(fa: F[A]) = fold.fold(fa)
		}
}

trait LowPriorityCatsFoldComplete{
	type Aux[Obj, Out0] = CatsFoldComplete[Obj]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G, Out0](implicit fold: Foldable[F], 
											   lift: Aux[G, Out0], 
											   ev: Monoid[Out0]): Aux[F[G], Out0] =
		new CatsFoldComplete[F[G]]{
			type Out = Out0

			def apply(fg: F[G]) = fold.foldMap(fg){ g: G => lift(g) }
		}
}
