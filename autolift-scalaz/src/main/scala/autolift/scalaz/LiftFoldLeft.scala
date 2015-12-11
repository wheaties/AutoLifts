package autolift.scalaz

import scalaz.{Functor, Foldable}
import autolift.LiftFoldLeft
import export._


trait ScalazLiftFoldLeft[Obj, Fn, Z] extends LiftFoldLeft[Obj, Fn, Z]

@exports(Subclass)
object ScalazLiftFoldLeft extends LowPriorityScalazLiftFoldLeft{
	def apply[FA, Fn, Z](implicit lift: ScalazLiftFoldLeft[FA, Fn, Z]): Aux[FA, Fn, Z, lift.Out] = lift

	@export(Subclass)
	implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F]): Aux[F[A], (B, C) => B, B, B] =
		new ScalazLiftFoldLeft[F[A], (B, C) => B, B]{
			type Out = B

			def apply(fa: F[A], f: (B, C) => B, z: B) = fold.foldLeft(fa, z)(f)
		}
}

trait LowPriorityScalazLiftFoldLeft{
	type Aux[FA, Fn, Z, Out0] = ScalazLiftFoldLeft[FA, Fn, Z]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G, Fn, Z](implicit functor: Functor[F], lift: LiftFoldLeft[G, Fn, Z]): Aux[F[G], Fn, Z, F[lift.Out]] =
		new ScalazLiftFoldLeft[F[G], Fn, Z]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn, z: Z) = functor.map(fg){ g: G => lift(g, f, z) }
		}
}