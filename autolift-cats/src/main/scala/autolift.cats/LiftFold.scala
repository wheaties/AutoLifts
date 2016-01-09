package autolift.cats

import autolift.LiftFold
import export._


trait ScalazLiftFold[Obj] extends LiftFold[Obj]

@exports(Subclass)
object ScalazLiftFold extends LowPriorityScalazLiftFold{
	def apply[FA](implicit lift: ScalazLiftFold[FA]): Aux[FA, lift.Out] = lift

	@export(Subclass)
	implicit def base[F[_], A](implicit fold: Foldable[F], ev: Monoid[A]): Aux[F[A], A] =
		new ScalazLiftFold[F[A]]{
			type Out = A

			def apply(fa: F[A]) = fold.fold(fa)
		}
}

trait LowPriorityScalazLiftFold{
	type Aux[FA, Out0] = ScalazLiftFold[FA]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G](implicit functor: Functor[F], lift: LiftFold[G]): Aux[F[G], F[lift.Out]] =
		new ScalazLiftFold[F[G]]{
			type Out = F[lift.Out]

			def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
		}
}