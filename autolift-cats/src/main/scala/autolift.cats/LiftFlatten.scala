package autolift.cats

import autolift.LiftFlatten
import export._

trait CatsLiftFlatten[M[_], Obj] extends LiftFlatten[M, Obj]

@exports(Subclass)
object CatsLiftFlatten extends LowPriorityCatsLiftFlatten{
	def apply[M[_], Obj](implicit lift: CatsLiftFlatten[M, Obj]): Aux[M, Obj, lift.Out] = lift

	@export(Subclass)
	implicit def base[M[_], A](implicit bind: Bind[M]): Aux[M, M[M[A]], M[A]] =
		new CatsLiftFlatten[M, M[M[A]]]{
			type Out = M[A]

			def apply(mma: M[M[A]]) = bind.bind(mma){ ma: M[A] => ma }
		}
}

trait LowPriorityCatsLiftFlatten{
	type Aux[M[_], Obj, Out0] = CatsLiftFlatten[M, Obj]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[M[_], F[_], G](implicit functor: Functor[F], lift: LiftFlatten[M, G]): Aux[M, F[G], F[lift.Out]] =
		new CatsLiftFlatten[M, F[G]]{
			type Out = F[lift.Out]

			def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
		}
}
