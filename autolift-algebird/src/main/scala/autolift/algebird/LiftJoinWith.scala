package autolift.algebird

import autolift.LiftJoinWith
import com.twitter.algebird.{Functor, Applicative}

trait AlgeLiftJoinWith[Obj1, Obj2, Fn] extends LiftJoinWith[Obj1, Obj2, Fn]

object AlgeLiftJoinWith extends LowPriorityAlgeLiftJoinWith{
	def apply[Obj1, Obj2, Fn](implicit lift: AlgeLiftJoinWith[Obj1, Obj2, Fn]): Aux[Obj1, Obj2, Fn, lift.Out] = lift

	implicit def base[F[_], G, H, G1 >: G, H1 >: H, Out0](implicit ap: Applicative[F]): Aux[F[G], F[H], (G1, H1) => Out0, F[Out0]] =
		new AlgeLiftJoinWith[F[G], F[H], (G1, H1) => Out0]{
			type Out = F[Out0]

			def apply(fg: F[G], fh: F[H], f: (G1, H1) => Out0) = ap.joinWith(fg, fh)(f)
		}
}

trait LowPriorityAlgeLiftJoinWith{
	type Aux[Obj1, Obj2, Fn, Out0] = AlgeLiftJoinWith[Obj1, Obj2, Fn]{ type Out = Out0 }

	implicit def recur[F[_], G, H, Fn](implicit functor: Functor[F], lift: LiftJoinWith[G, H, Fn]): Aux[F[G], H, Fn, F[lift.Out]] =
		new AlgeLiftJoinWith[F[G], H, Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], h: H, f: Fn) = functor.map(fg){ g: G => lift(g, h, f) }
		}
}