package autolift.scalaz

import scalaz.Functor
import autolift.LiftF
import export._

trait ScalazLiftF[Obj, Fn] extends LiftF[Obj, Fn]

@exports(Subclass)
object ScalazLiftF extends LowPriorityScalazLiftF {
	def apply[Obj, Fn](implicit lift: ScalazLiftF[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

	@export(Subclass)
	implicit def base[F[_], A, C >: A, B](implicit functor: Functor[F]): Aux[F[A], C => B, F[B]] =
		new ScalazLiftF[F[A], C => B]{
			type Out = F[B]

			def apply(fa: F[A], f: C => B) = functor.map(fa)(f)
		}
}

trait LowPriorityScalazLiftF{
	type Aux[Obj, Fn, Out0] = ScalazLiftF[Obj, Fn]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftF[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new ScalazLiftF[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}