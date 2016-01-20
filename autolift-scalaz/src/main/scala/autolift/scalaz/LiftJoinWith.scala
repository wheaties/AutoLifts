package autolift.scalaz

import autolift.{LiftJoinWith, LiftedJoinWith}
import scalaz.{Functor, Apply}

trait ScalazLiftJoinWith[Obj1, Obj2, Fn] extends LiftJoinWith[Obj1, Obj2, Fn]

object ScalazLiftJoinWith extends LowPriorityScalazLiftJoinWith{
	def apply[Obj1, Obj2, Fn](implicit lift: ScalazLiftJoinWith[Obj1, Obj2, Fn]): Aux[Obj1, Obj2, Fn, lift.Out] = lift

	implicit def base[F[_], G, H, G1 >: G, H1 >: H, Out0](implicit ap: Apply[F]): Aux[F[G], F[H], (G1, H1) => Out0, F[Out0]] =
		new ScalazLiftJoinWith[F[G], F[H], (G1, H1) => Out0]{
			type Out = F[Out0]

			def apply(fg: F[G], fh: F[H], f: (G1, H1) => Out0) = ap.ap(fg){
				ap.map(fh){ h: H => f(_, h) }
			}
		}
}

trait LowPriorityScalazLiftJoinWith{
	type Aux[Obj1, Obj2, Fn, Out0] = ScalazLiftJoinWith[Obj1, Obj2, Fn]{ type Out = Out0 }

	implicit def recur[F[_], G, H, Fn](implicit functor: Functor[F], lift: LiftJoinWith[G, H, Fn]): Aux[F[G], H, Fn, F[lift.Out]] =
		new ScalazLiftJoinWith[F[G], H, Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], h: H, f: Fn) = functor.map(fg){ g: G => lift(g, h, f) }
		}
}

trait LiftedJoinWithImplicits{
	implicit def liftedJoinWithFunctor[A, B] = new Functor[LiftedJoinWith[A, B, ?]]{
		def map[C, D](lm: LiftedJoinWith[A, B, C])(f: C => D) = lm map f
	}
}