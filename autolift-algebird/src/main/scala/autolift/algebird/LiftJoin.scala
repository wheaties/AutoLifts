package autolift.algebird

import autolift.LiftJoin
import com.twitter.algebird.{Functor, Applicative}

trait AlgeLiftJoin[Obj1, Obj2] extends LiftJoin[Obj1, Obj2]

object AlgeLiftJoin extends LowPriorityAlgeLiftJoin{
	def apply[Obj1, Obj2](implicit lift: AlgeLiftJoin[Obj1, Obj2]): Aux[Obj1, Obj2, lift.Out] = lift

	implicit def base[F[_], G, H](implicit ap: Applicative[F]): Aux[F[G], F[H], F[(G, H)]] =
		new AlgeLiftJoin[F[G], F[H]]{
			type Out = F[(G, H)]

			def apply(fg: F[G], fh: F[H]) = ap.join(fg, fh)
		}
}

trait LowPriorityAlgeLiftJoin{
	type Aux[Obj1, Obj2, Out0] = AlgeLiftJoin[Obj1, Obj2]{ type Out = Out0 }

	implicit def recur[F[_], G, H](implicit lift: LiftJoin[G, H], functor: Functor[F]): Aux[F[G], H, F[lift.Out]] =
		new AlgeLiftJoin[F[G], H]{
			type Out = F[lift.Out]

			def apply(fg: F[G], h: H) = functor.map(fg){ g: G => lift(g, h) }
		}
}