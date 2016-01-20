package autolift.scalaz

import autolift.FoldComplete
import scalaz.{Foldable, Monoid}

trait ScalazFoldComplete[Obj] extends FoldComplete[Obj]

object ScalazFoldComplete extends LowPriorityScalazFoldComplete{
	def apply[Obj](implicit lift: ScalazFoldComplete[Obj]): Aux[Obj, lift.Out] = lift

	implicit def base[F[_], A](implicit fold: Foldable[F], ev: Monoid[A]): Aux[F[A], A] =
		new ScalazFoldComplete[F[A]]{
			type Out = A

			def apply(fa: F[A]) = fold.fold(fa)
		}
}

trait LowPriorityScalazFoldComplete{
	type Aux[Obj, Out0] = ScalazFoldComplete[Obj]{ type Out = Out0 }

	implicit def recur[F[_], G, Out0](implicit fold: Foldable[F], 
											   lift: Aux[G, Out0], 
											   ev: Monoid[Out0]): Aux[F[G], Out0] =
		new ScalazFoldComplete[F[G]]{
			type Out = Out0

			def apply(fg: F[G]) = fold.foldMap(fg){ g: G => lift(g) }
		}
}

