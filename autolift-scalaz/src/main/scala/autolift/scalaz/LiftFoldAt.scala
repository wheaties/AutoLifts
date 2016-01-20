package autolift.scalaz

import scalaz.{Functor, Foldable, Monoid}
import autolift.LiftFoldAt

trait ScalazLiftFoldAt[F[_], Obj] extends LiftFoldAt[F, Obj]

object ScalazLiftFoldAt extends LowPriorityScalazLiftFoldAt{
	def apply[F[_], Obj](implicit fold: ScalazLiftFoldAt[F, Obj]): Aux[F, Obj, fold.Out] = fold

	implicit def base[F[_], A](implicit fold: Foldable[F], m: Monoid[A]): Aux[F, F[A], A] =
		new ScalazLiftFoldAt[F, F[A]]{
			type Out = A

			def apply(fa: F[A]) = fold.fold(fa)
		}
}

trait LowPriorityScalazLiftFoldAt{
	type Aux[F[_], Obj, Out0] = ScalazLiftFoldAt[F, Obj]{ type Out = Out0 }

	implicit def recur[F[_], G[_], H](implicit functor: Functor[G], fold: LiftFoldAt[F, H]): Aux[F, G[H], G[fold.Out]] =
		new ScalazLiftFoldAt[F, G[H]]{
			type Out = G[fold.Out]

			def apply(gh: G[H]) = functor.map(gh){ h: H => fold(h) }
		}
}

