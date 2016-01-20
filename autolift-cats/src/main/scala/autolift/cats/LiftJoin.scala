package autolift.cats

import autolift.LiftJoin
import cats.{Functor, Apply}

trait CatsLiftJoin[Obj1, Obj2] extends LiftJoin[Obj1, Obj2]

object CatsLiftJoin extends LowPriorityCatsLiftJoin{
	def apply[Obj1, Obj2](implicit lift: CatsLiftJoin[Obj1, Obj2]): Aux[Obj1, Obj2, lift.Out] = lift

	implicit def base[F[_], G, H](implicit ap: Apply[F]): Aux[F[G], F[H], F[(G, H)]] =
		new CatsLiftJoin[F[G], F[H]]{
			type Out = F[(G, H)]

			def apply(fg: F[G], fh: F[H]) = ap.ap(fg){ 
				ap.map(fh){ h: H => (_, h) }
			}
		}
}

trait LowPriorityCatsLiftJoin{
	type Aux[Obj1, Obj2, Out0] = CatsLiftJoin[Obj1, Obj2]{ type Out = Out0 }

	implicit def recur[F[_], G, H](implicit lift: LiftJoin[G, H], functor: Functor[F]): Aux[F[G], H, F[lift.Out]] =
		new CatsLiftJoin[F[G], H]{
			type Out = F[lift.Out]

			def apply(fg: F[G], h: H) = functor.map(fg){ g: G => lift(g, h) }
		}
}