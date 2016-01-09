package autolift.cats

import cats.{Functor, Monoid, Foldable}
import autolift.LiftFold
import export._


trait CatsLiftFold[Obj] extends LiftFold[Obj]

@exports(Subclass)
object CatsLiftFold extends LowPriorityCatsLiftFold{
	def apply[FA](implicit lift: CatsLiftFold[FA]): Aux[FA, lift.Out] = lift

	@export(Subclass)
	implicit def base[F[_], A](implicit fold: Foldable[F], ev: Monoid[A]): Aux[F[A], A] =
		new CatsLiftFold[F[A]]{
			type Out = A

			def apply(fa: F[A]) = fold.fold(fa)
		}
}

trait LowPriorityCatsLiftFold{
	type Aux[FA, Out0] = CatsLiftFold[FA]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G](implicit functor: Functor[F], lift: LiftFold[G]): Aux[F[G], F[lift.Out]] =
		new CatsLiftFold[F[G]]{
			type Out = F[lift.Out]

			def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
		}
}
